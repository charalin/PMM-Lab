/*******************************************************************************
 * PMM-Lab � 2012, Federal Institute for Risk Assessment (BfR), Germany
 * 
 * PMM-Lab is a set of KNIME-Nodes and KNIME workflows running within the KNIME software plattform (http://www.knime.org.).
 * 
 * PMM-Lab � 2012, Federal Institute for Risk Assessment (BfR), Germany
 * Contact: armin.weiser@bfr.bund.de or matthias.filter@bfr.bund.de 
 * 
 * Developers and contributors to the PMM-Lab project are 
 * J�rgen Brandt (BfR)
 * Armin A. Weiser (BfR)
 * Matthias Filter (BfR)
 * Alexander Falenski (BfR)
 * Christian Th�ns (BfR)
 * Annemarie K�sbohrer (BfR)
 * Bernd Appel (BfR)
 * 
 * PMM-Lab is a project under development. Contributions are welcome.
 * 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/**
 * 
 */
package org.hsh.bfr.db.gui.dbtree;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.hsh.bfr.db.DBKernel;
import org.hsh.bfr.db.MyLogger;
import org.hsh.bfr.db.MyTable;

/**
 * @author Armin
 *
 */
public class MyDBTreeModel implements TreeModel {

	private LinkedHashMap<Integer, DefaultMutableTreeNode>[] myIDs = null;	
	private LinkedHashMap<Integer, DefaultMutableTreeNode>[] myFilterIDs = null;	
	private LinkedHashMap<String, int[]> knownCodeSysteme = null;	
	private DefaultMutableTreeNode root = null;
	private DefaultMutableTreeNode filteredRoot = null;
	private String filter = "";
	private String[] showOnly = null;
	private int codeSystemNumTOP = -1;

	  public MyDBTreeModel(final MyTable myT) {
		  this(myT, null);
	  }
	public MyDBTreeModel(final MyTable myT, final String[] showOnly) {
		this.showOnly = showOnly;
	  	knownCodeSysteme = new LinkedHashMap<String, int[]>();
	  	knownCodeSysteme.put("Matrices_ADV_01", new int[]{2,3,5,7}); // 01-011123
	  	knownCodeSysteme.put("Matrices_ADV_14", new int[]{2,3,5,7}); // 14-011123
	  	knownCodeSysteme.put("Matrices_ADV_15", new int[]{2,3,6}); // 15-011123
	  	knownCodeSysteme.put("Matrices_ADV_20", new int[]{2,3,5,7}); // 20-011123
	  	knownCodeSysteme.put("Matrices_BLS", new int[]{1,3,4,5,6}); // A011123
	  	knownCodeSysteme.put("Matrices_GS1", new int[]{2,3}); // 0001
	  	knownCodeSysteme.put("Matrices_FA", new int[]{2,4,6,8,10,12,14,16,18}); // 
	  	knownCodeSysteme.put("Agenzien_ADV", new int[]{2,4}); // 0102123
	  	knownCodeSysteme.put("Matrices_SiLeBAT", new int[]{2,4,6,8,10});
	  	knownCodeSysteme.put("Agenzien_SiLeBAT", new int[]{2,4,6,8,10});
	  	knownCodeSysteme.put("Methodiken_SiLeBAT", new int[]{2,4,6,8,10});
	  	knownCodeSysteme.put("Matrices_Extra", new int[]{2,4,6,8,10});
	  	knownCodeSysteme.put("Agenzien_Extra", new int[]{2,4,6,8,10});
	  	knownCodeSysteme.put("Methodiken_Extra", new int[]{2,4,6,8,10});
	  	// Agenzien_VET
	  	knownCodeSysteme.put("Methoden_BVL", new int[]{2,3,5,6,8,9}); // 
	  	// TOP
	  	knownCodeSysteme.put("Agenzien_TOP", new int[]{2,4}); // 
	  	knownCodeSysteme.put("Matrices_TOP", new int[]{2,4}); // 
	  	knownCodeSysteme.put("Methoden_TOP", new int[]{2,4}); // 

	  	knownCodeSysteme.put("Methodiken_BfR", new int[]{2,4,6}); // 
	  	setTable(myT);
  }

  
  public void setShowOnly(final String[] newShowOnly) {
	  showOnly = newShowOnly;
  }
	@Override
	public Object getChild(final Object parent, final int index) {
		Object result = null;
		if (parent instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode mdtn = (DefaultMutableTreeNode) parent;		
			result = mdtn.getChildAt(index);
		}
		return result;
	}

	@Override
	public int getChildCount(final Object parent) {
		int result = 0;
		if (parent instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode mdtn = (DefaultMutableTreeNode) parent;			
			result = mdtn.getChildCount();
		}
		return result;
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child) {
		int result = 0;
		if (parent instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode mdtn = (DefaultMutableTreeNode) parent;		
			if (child instanceof TreeNode) {
				result = mdtn.getIndex((TreeNode) child);
			}
		}
		return result;
	}

	@Override
	public Object getRoot() {
		if (filter.length() == 0 || filteredRoot == null) {
			return root;
		} else {
			return filteredRoot;
		}
	}

	@Override
	public boolean isLeaf(final Object node) {
		boolean result = false;
		if (node instanceof DefaultMutableTreeNode) {
			result = ((DefaultMutableTreeNode) node).isLeaf();
		}
		return result;
	}

  // Since this is not an editable tree model, we never fire any events,
  // so we don't actually have to keep track of interested listeners
	@Override
	public void addTreeModelListener(final TreeModelListener l) {}
	@Override
	public void removeTreeModelListener(final TreeModelListener l) {}

  // This method is invoked by the JTree only for editable trees.  
  // This TreeModel does not allow editing, so we do not implement 
  // this method.  The JTree editable property is false by default.
	@Override
	public void valueForPathChanged(final TreePath path, final Object newvalue) {}

	
	  @SuppressWarnings("unchecked")
	private void setTable(final MyTable myT) {
		if (myT != null) {
			root = new DefaultMutableTreeNode(new MyDBTreeNode(0, "", "Codes", false, -1));			
	    // Erst die Codetypen
			Vector<String> codeVec = new Vector<String>();
			String sql = "SELECT DISTINCT(" + DBKernel.delimitL("CodeSystem") + ") FROM " + DBKernel.delimitL(DBKernel.getCodesName(myT.getTablename()));
			ResultSet rs = DBKernel.getResultSet(sql, true);
			try {
				if (rs != null && rs.first()) {
					do {
						String cs = rs.getString("CodeSystem");
						if (cs != null) {
							codeVec.add(cs);
						}
					} while (rs.next());
				}
			}
			catch (Exception e) {MyLogger.handleException(e);}

			LinkedHashMap<String, DefaultMutableTreeNode>[] myCodes = new LinkedHashMap[codeVec.size()];
			myIDs = new LinkedHashMap[codeVec.size()];
			myFilterIDs = new LinkedHashMap[codeVec.size()];
			
			// Dann die einzelnen CodeSysteme
			for (int i=0;i<codeVec.size();i++) {
				boolean doIt = (showOnly == null);
				if (!doIt) {
					for (int j=0;j<showOnly.length;j++) {
						if (showOnly[j] != null && showOnly[j].equals(codeVec.get(i))) {
							doIt = true;
							break;
						}
					}
				}
				if (doIt) {
					sql = "SELECT " + DBKernel.delimitL("Code") + "," + DBKernel.delimitL("Basis") + "," + DBKernel.delimitL(myT.getFieldNames()[0]) +
					" FROM " + DBKernel.delimitL(DBKernel.getCodesName(myT.getTablename())) +
					" LEFT JOIN " + DBKernel.delimitL(myT.getTablename()) +
					" ON " + DBKernel.delimitL(DBKernel.getCodesName(myT.getTablename())) + "." + DBKernel.delimitL("Basis") + 
					" = " + DBKernel.delimitL(myT.getTablename()) + "." + DBKernel.delimitL("ID") + 
					" WHERE " + DBKernel.delimitL("CodeSystem") + "='" + codeVec.get(i) + "'" +
					" ORDER BY " + DBKernel.delimitL("Code") + " ASC, LENGTH(" + DBKernel.delimitL("Code") + ") ASC";
					DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(new MyDBTreeNode(0, "", codeVec.get(i), false, i));
					myCodes[i] = new LinkedHashMap<String, DefaultMutableTreeNode>();
					myIDs[i] = new LinkedHashMap<Integer, DefaultMutableTreeNode>();
					myFilterIDs[i] = new LinkedHashMap<Integer, DefaultMutableTreeNode>();
					int[] cutSystem = null;
	  				if (knownCodeSysteme.containsKey(myT.getTablename() + "_" + codeVec.get(i))) {
	  					cutSystem = knownCodeSysteme.get(myT.getTablename() + "_" + codeVec.get(i));
	  				}
					readDB(myCodes[i], i, dmtn, sql, myT.getTablename() + "_" + codeVec.get(i), cutSystem);	
					if (codeVec.get(i).equals("TOP") || codeVec.get(i).equals("GS1")) {
						root.insert(dmtn, 0);
					} else {
						root.add(dmtn);
					}
					if (codeVec.get(i).equals("TOP")) {
						codeSystemNumTOP = i;
					}
				}
			}
		}		
	}
	private void readDB(final LinkedHashMap<String, DefaultMutableTreeNode> myCodes, final int codeSystemNum, final DefaultMutableTreeNode root, final String sql, final String tablename_codeSystem, int[] cutSystem) {
		myIDs[codeSystemNum].clear(); myCodes.clear();
	    try {
	  		ResultSet rs = DBKernel.getResultSet(sql, false);
	  		if (rs.first()) {
	  			do {
	  				Integer id = rs.getInt("Basis");
	  				String code = rs.getString("Code");
	  				if (cutSystem == null && code.length() > 1 && knownCodeSysteme.containsKey(tablename_codeSystem + "_" + code.substring(0, 2))) {
	  					cutSystem = knownCodeSysteme.get(tablename_codeSystem + "_" + code.substring(0, 2));	  					
	  				}
	  				String cutCode = (cutSystem == null) ? cutEndZeros(code) : code; // codeSystemIsGS1
	  				String description = rs.getString(3);
	  				DefaultMutableTreeNode dmtn;
	  				if (code == null || code.trim().length() == 0) {
	  					System.err.println("Br�mde?");
	  				}
	  				else {
	  					MyDBTreeNode mydbtn = new MyDBTreeNode(id, code, description, false, codeSystemNum);
	  					dmtn = new DefaultMutableTreeNode(mydbtn);
	  					DefaultMutableTreeNode n = look4ParentNode(myCodes, cutCode, cutSystem);
						if (n != null) {
							n.add(dmtn);
						} else {
							root.add(dmtn);
						}
						myIDs[codeSystemNum].put(id, dmtn); //  && !myLeafs.containsKey(id)
						myCodes.put(cutCode, dmtn);
	  				}
	  			} while (rs.next());
	  		}  	
	    }
	    catch (SQLException e) {
	    	MyLogger.handleException(e);
	    }			
	}
	private String cutEndZeros(final String code) {
		String result = code;
		if (code != null) {
			while (result.endsWith("0")) {
				result = result.substring(0, result.length() - 1);
			}			
		}
		return result;
	}
	private DefaultMutableTreeNode look4ParentNode(final LinkedHashMap<String, DefaultMutableTreeNode> myCodes, final String code, final int[] cutSystem) {
		DefaultMutableTreeNode result = null;
		String key;
		if (cutSystem == null) {
			for (int i=code.length()-1;i>=0;i--) {
				key = code.substring(0, i);
				if (myCodes.containsKey(key))	{
					result = myCodes.get(key);
					break;
				}
			}
		}
		else {
			for (int i=cutSystem.length-1;i>=0;i--) {
				if (cutSystem[i] <= code.length()) {
					key = code.substring(0, cutSystem[i]);
					if (myCodes.containsKey(key))	{
						result = myCodes.get(key);
						break;
					}
					else {
						if (i > 0) { // sonst Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: -1
							String zeroKey = key;
							for (int j=i;j<cutSystem.length;j++) { // Nullen anf�gen, gibts dann nen Parent? Das ist zumindest wichtig bei ADV-01 Matrices. Sieht bei den anderen System auch gut aus
								for (int k=0;k<cutSystem[j]-cutSystem[j-1];k++) {
									zeroKey += "0";
								}
								if (myCodes.containsKey(zeroKey))	{
									result = myCodes.get(zeroKey);
									break;
								}													
							}
							if (result != null) {
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}
	public DefaultMutableTreeNode getTreeNode(final int id, final int codeSystemNum) {
		DefaultMutableTreeNode result = null;
		LinkedHashMap<Integer, DefaultMutableTreeNode>[] theIDs = (filter.length() > 0) ? myFilterIDs : myIDs;
		if (codeSystemNum < 0 && codeSystemNumTOP >= 0) {
			result = theIDs[codeSystemNumTOP].get(id);
		} else if (codeSystemNum >= 0 && codeSystemNum < theIDs.length) {
			result = theIDs[codeSystemNum].get(id);
		}
		if (result == null) {
			for (int i=0;i<theIDs.length;i++) {
				if (theIDs[i] != null && theIDs[i].get(id) != null) {
					return theIDs[i].get(id);
				}
			}			
		}
		return result;
	}
	
	public void checkFilter(final String filter) {
		this.filter = filter;
		if (filter.length() > 0) {
			setInvisible(root);
		    StringTokenizer tok = new StringTokenizer(filter);
		    String[] findStrings = new String[tok.countTokens()];
		    for (int i=0;tok.hasMoreTokens();i++) {
		    	findStrings[i] = tok.nextToken().toLowerCase();
		    }
			checkVisibility(root, findStrings);
			filteredRoot = new DefaultMutableTreeNode(new MyDBTreeNode(0, "", "Codes", false, -1));		
			if (myFilterIDs != null) {
				for (int i=0;i<myFilterIDs.length;i++) {
					if (myFilterIDs[i] != null) {
						myFilterIDs[i].clear();
					}
				}
			}
			populateFilteredNode(root, filteredRoot);			
		}		
	}
	private void setInvisible(final DefaultMutableTreeNode node) {
	    for (int i = 0; i < node.getChildCount(); i++) {
	    	DefaultMutableTreeNode unfilteredChildNode = (DefaultMutableTreeNode) node.getChildAt(i);
	    	((MyDBTreeNode) unfilteredChildNode.getUserObject()).setVisible(false);
	    	setInvisible(unfilteredChildNode);
	    }
	}
	private void checkVisibility(final DefaultMutableTreeNode node, final String[] filter) {
	    for (int i = 0; i < node.getChildCount(); i++) {
	    	DefaultMutableTreeNode unfilteredChildNode = (DefaultMutableTreeNode) node.getChildAt(i);
	    	int j;
	    	for (j=0;j<filter.length;j++) {
	    		if (((MyDBTreeNode) unfilteredChildNode.getUserObject()).toString().toLowerCase().indexOf(filter[j]) < 0) {
					break;
				}
	    	}
	    	if (j == filter.length) {
	    		setVisible(unfilteredChildNode);
	    	}
	    	checkVisibility(unfilteredChildNode, filter);
	    }
	}
	private void setVisible(final DefaultMutableTreeNode node) {
		((MyDBTreeNode) node.getUserObject()).setVisible(true);
		if ((node.getParent() != null)) {
			setVisible((DefaultMutableTreeNode) node.getParent());
		}
	}

	private void populateFilteredNode(final DefaultMutableTreeNode unfilteredNode, final DefaultMutableTreeNode filteredNode) {
    for (int i = 0; i < unfilteredNode.getChildCount(); i++) {
    	DefaultMutableTreeNode unfilteredChildNode = (DefaultMutableTreeNode) unfilteredNode.getChildAt(i);

      if (((MyDBTreeNode) unfilteredChildNode.getUserObject()).isVisible()) {
      	DefaultMutableTreeNode filteredChildNode = (DefaultMutableTreeNode) unfilteredChildNode.clone();
        filteredNode.add(filteredChildNode);
    		Integer id = ((MyDBTreeNode) filteredChildNode.getUserObject()).getID();
    		int codeSystemNum = ((MyDBTreeNode) filteredChildNode.getUserObject()).getCodeSystemNum();
    		if (!myFilterIDs[codeSystemNum].containsKey(id)) {
				myFilterIDs[codeSystemNum].put(id, filteredChildNode);
			}
        populateFilteredNode(unfilteredChildNode, filteredChildNode);
      }
      else {
        populateFilteredNode(unfilteredChildNode, filteredNode);      	
      }
    }
  }
}
