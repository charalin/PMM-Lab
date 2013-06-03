/*
 * Created by JFormDesigner on Sat Sep 29 12:27:04 CEST 2012
 */

package de.bund.bfr.knime.pmm.manualmodelconf.ui;

import java.awt.*;
import java.awt.event.*;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import de.bund.bfr.knime.pmm.common.ui.*;
import de.bund.bfr.knime.pmm.common.units.Categories;
import de.bund.bfr.knime.pmm.common.units.Category;

import org.hsh.bfr.db.DBKernel;
import org.hsh.bfr.db.MyDBTables;
import org.hsh.bfr.db.MyTable;
import org.lsmp.djep.djep.DJep;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.SymbolTable;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

import de.bund.bfr.knime.pmm.bfrdbiface.lib.Bfrdb;
import de.bund.bfr.knime.pmm.common.DepXml;
import de.bund.bfr.knime.pmm.common.LiteratureItem;
import de.bund.bfr.knime.pmm.common.ParametricModel;
import de.bund.bfr.knime.pmm.common.PmmException;
import de.bund.bfr.knime.pmm.common.PmmTimeSeries;
import de.bund.bfr.knime.pmm.common.PmmXmlDoc;
import de.bund.bfr.knime.pmm.common.PmmXmlElementConvertable;
import de.bund.bfr.knime.pmm.common.math.MathUtilities;
import de.bund.bfr.knime.pmm.common.resources.Resources;

/**
 * @author Armin Weiser
 */
public class MMC_M extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String LABEL_OWNMODEL = "Manually defined formula";

	private Frame m_parentFrame = null;
	@SuppressWarnings("unchecked")
	private JComboBox<ParametricModel>[] threeBoxes = new JComboBox[3];
	private HashMap<ParametricModel, HashMap<String, ParametricModel>> m_secondaryModels = null;
	private DefaultListModel<ParametricModel> listModel;  
	private MMC_TS m_mmcts;
	private HashMap<Integer, PmmTimeSeries> tss;
	private boolean dontFireList = false;

	private Connection m_conn = null;
	private String dbuuid = null;
	private boolean dontTouch = false;
	
	private boolean modelNameChangedManually = false;
	private boolean formulaCreator;

	public MMC_M() {
		this(null, 1, "", false, null);
	}
	public MMC_M(final Frame parentFrame, final int level, final String paramName, boolean formulaCreator, MMC_TS m_mmcts) {
		this.m_parentFrame = parentFrame;
		this.formulaCreator = formulaCreator;
		this.m_mmcts = m_mmcts;
		initComponents();
		m_secondaryModels = new HashMap<ParametricModel, HashMap<String, ParametricModel>>();
		depVarLabel.setText(paramName);
		if (level == 1) {
			radioButton1.setSelected(true);
			//depVarLabel.setVisible(false);
		}
		else {
			depVarLabel.setVisible(true);
			radioButton2.setSelected(true);
			radioButton1.setEnabled(false);
			radioButton2.setEnabled(false);
			radioButton3.setEnabled(false);
		}
		
		scrollPane3.setVisible(false); // List1
		if (formulaCreator) {
			label8.setVisible(false);
			label3.setVisible(false);
			label4.setVisible(false);
			label5.setVisible(false);
			label6.setVisible(false);
			r2Field.setVisible(false);
			rmsField.setVisible(false);
			aicField.setVisible(false);
			bicField.setVisible(false);
			checkBox1.setVisible(false);
			qScoreBox.setVisible(false);
			table.getColumnModel().getColumn(2).setMinWidth(0);
			table.getColumnModel().getColumn(2).setMaxWidth(0);
			table.getColumnModel().getColumn(2).setWidth(0);
			table.getColumnModel().getColumn(3).setMinWidth(0);
			table.getColumnModel().getColumn(3).setMaxWidth(0);
			table.getColumnModel().getColumn(3).setWidth(0);
		}
		
		referencesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

		    public void valueChanged(ListSelectionEvent lse) {
		        if (!lse.getValueIsAdjusting()) {
		            if (referencesTable.getSelectedRowCount() > 0) {
		            	button2.setEnabled(true);
		            	button3.setEnabled(true);
		            }
		            else {
		            	button2.setEnabled(false);
		            	button3.setEnabled(false);
		            }
		        }
		    }
		});
		depVarLabel4Units.setText("[]");
	}
	
	public ParametricModel getPM() {
		return table.getPM();
	}
	public void setPM(ParametricModel pm) {
		if (pm != null) {
			String categoryStr = pm.getDepCategory();
			String unit = pm.getDepUnit();
			if (unit != null && !unit.isEmpty()) depVarLabel4Units.setText("[" + categoryStr + " -> " + unit + "]");
			else depVarLabel4Units.setText("[]");
			if (pm.getLevel() == 2) {
				if (!radioButton2.isSelected()) {
					radioButton2.setSelected(true);
					setComboBox();
				}
			}
			modelnameField.setText(pm.getModelName());
			String depVar = depVarLabel.getText();
			if (!depVar.isEmpty()) {
				String formula = pm.getFormula();
				int index = formula.lastIndexOf("=");
				if (index >= 0) {
					//String oldDepVar = formula.substring(0, index);
					//System.err.println(oldDepVar + "=" + depVar);
					//pm.getDepXml().setOrigName(oldDepVar);//.addVarParMap(depVar, oldDepVar);
					
					//formula = MathUtilities.replaceVariable(formula.substring(index), depVar, depVar+depVar);			
					formula = depVar + formula.substring(index);
					pm.setDepVar(depVar);
					pm.setFormula(formula);
				}
			}			
			formulaArea.setText(pm.getFormula());
			table.setPM(pm, m_secondaryModels.get(pm), radioButton3);
			setDblTextVal(rmsField, pm.getRms());
			setDblTextVal(r2Field, pm.getRsquared());
			setDblTextVal(aicField, pm.getAic());
			setDblTextVal(bicField, pm.getBic());
			while (referencesTable.getRowCount() > 0) ((DefaultTableModel) referencesTable.getModel()).removeRow(0);		
			insertRefs(pm.getEstModelLit());
			insertRefs(pm.getModelLit());
			if (pm.isChecked() != null && pm.isChecked()) checkBox1.setSelected(true);
			else checkBox1.setSelected(false);
			if (pm.getQualityScore() != null) qScoreBox.setSelectedIndex(pm.getQualityScore());
			else qScoreBox.setSelectedIndex(0);
			insertNselectPMintoBox(pm);
		}
	}
	private void insertRefs(PmmXmlDoc modelLit) {
		for (PmmXmlElementConvertable el : modelLit.getElementSet()) {
			if (el instanceof LiteratureItem) {
				Vector<LiteratureItem> vli = new Vector<LiteratureItem>();
				LiteratureItem li = (LiteratureItem) el;
				vli.add(li);
				((DefaultTableModel) referencesTable.getModel()).addRow(vli);
			}
		}
	}
	private void setDblTextVal(DoubleTextField tf, Double value) {
		if (value == null || Double.isNaN(value)) tf.setText("");
		else tf.setValue(value);
	}
	public void setConnection(final Connection conn) {	
		this.m_conn = conn;
		try {
			Bfrdb db = new Bfrdb(m_conn);
			dbuuid = db.getDBUUID();
		}
		catch (SQLException e1) {e1.printStackTrace();}
		setComboBox();
	}
	private void setComboBox() {		
		remove(modelNameBox);
		modelNameBox = threeBoxes[getSelRadio() - 1];
		if (modelNameBox == null) {
			modelNameBox = new JComboBox<ParametricModel>();
			modelNameBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					modelNameBoxActionPerformed(e);
				}
			});
			threeBoxes[getSelRadio() - 1] = modelNameBox;
		}
		add(modelNameBox, CC.xywh(5, 5, 17, 1));
		if (modelNameBox.getItemCount() == 0) {
			modelNameBox.removeAllItems();
			//if (m_secondaryModels != null) m_secondaryModels.clear();
			int level = radioButton2.isSelected() ? 2 : 1;
			ParametricModel pm = new ParametricModel(LABEL_OWNMODEL, "", null, level);
			modelNameBox.addItem(pm);
			//System.err.println("added1:" + pm + "\t" + pm.hashCode());
			try {			
				Bfrdb db = new Bfrdb(m_conn);
				ResultSet result = db.selectModel(level);			
				while(result.next()) {				
					String modelName = result.getString(Bfrdb.ATT_NAME);
					String formula = result.getString(Bfrdb.ATT_FORMULA);
					int modelID = result.getInt(Bfrdb.ATT_MODELID);

					pm = new ParametricModel(modelName, formula, new DepXml(result.getString(Bfrdb.ATT_DEP)), level, modelID);
					String s = result.getString("LitMID");
		    		if (s != null) pm.setMLit(db.getLiteratureXml(s));
					manageDBMinMax(result, pm);
					manageIndep(pm, result.getArray(Bfrdb.ATT_INDEP));
					
					modelNameBox.addItem(pm);
					//System.err.println("added2:" + pm + "\t" + pm.hashCode());
				}			
				result.getStatement().close();
				result.close();				
				db.close();
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	private void manageDBMinMax(ResultSet result, ParametricModel pm) throws SQLException {
		Array array = result.getArray(Bfrdb.ATT_PARAMNAME);
		Array arrayMin = result.getArray(Bfrdb.ATT_MINVALUE);
		Array arrayMax = result.getArray(Bfrdb.ATT_MAXVALUE);
	    if (array != null && arrayMin != null && arrayMax != null) {
		    try {
				Object[] o = (Object[])array.getArray();
				Object[] oMin = (Object[])arrayMin.getArray();
				Object[] oMax = (Object[])arrayMax.getArray();
				if (o != null && o.length > 0) {
					for (int ii=0;ii<o.length;ii++) {
						pm.addParam(o[ii].toString(), Double.NaN, Double.NaN);
						if (oMin != null && oMin.length > ii && oMin[ii] != null) {
							pm.setParamMin(o[ii].toString(), Double.parseDouble(oMin[ii].toString()));
						}
						if (oMax != null && oMax.length > ii && oMax[ii] != null) {
							pm.setParamMax(o[ii].toString(), Double.parseDouble(oMax[ii].toString()));
						}
					}					
				}
			}
		    catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	}
    private String manageIndep(ParametricModel pm, Array array) {
    	String result = null;
	    if (array != null) {
		    try {
				Object[] o = (Object[])array.getArray();
				if (o != null && o.length > 0) {
					for (int i=0;i<o.length;i++) {
						pm.addIndepVar(o[i].toString());
					}					
				}
			}
		    catch (SQLException e) {
				e.printStackTrace();
			}
	    }
    	return result;
    }

	public void stopCellEditing() {
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}
	}

	private void parseFormula(ParametricModel oldPM, ParametricModel newPM) {
		String formula = formulaArea.getText();
		formula = formula.replaceAll( "\n", "" );
		formula = formula.replaceAll( "\\s", "" );
		formula = formula.replace("~", "=").trim();
		
		int index = formula.indexOf("=");
		if (index < 0) {
			return;
		}
		
		formulaArea.setText(formula);
		String depVar = formula.substring(0, index).trim();
		newPM.setDepVar(depVar);

		DJep parser = MathUtilities.createParser();
		try {
			parser.parse(formula);
			SymbolTable st = parser.getSymbolTable();
		    for (Object o : st.keySet()) {
		    	String os = o.toString();
		        if (!os.equals(depVar)) {
		        	if (oldPM.containsIndep(os)) {
		        		newPM.addIndepVar(os, oldPM.getIndepMin(os), oldPM.getIndepMax(os));
		        	}
		        	else if (oldPM.containsParam(os)) {
		        		newPM.addParam(os, oldPM.getParamValue(os), oldPM.getParamError(os), oldPM.getParamMin(os), oldPM.getParamMax(os));
		        	}
		        	else {
		        		newPM.addParam(os);
		        	}
		        }		        
		    }
		}
		catch (ParseException e) {
			if (!e.getErrorInfo().startsWith("Unexpected \"<EOF>\"") &&
					!e.getErrorInfo().startsWith("Encountered \"-\" at")) {
				e.printStackTrace();
			}
		}
	}

	private void modelNameBoxActionPerformed(ActionEvent e) {
		if (dontTouch) return;
		table.clearTable();
		//if (!dontRemoveSec && m_secondaryModels != null) m_secondaryModels.clear();
		formulaArea.setText("");
		modelnameField.setText("");
		
		ParametricModel pm = (ParametricModel) modelNameBox.getSelectedItem();
		if (pm != null) {
			setPM(pm);
		}
		else if (modelNameBox.getItemCount() > 1) {
			System.err.println("pm = null???\t" + modelNameBox.getSelectedItem() + "\t" + modelNameBox.getItemCount());
		}
		else {
			//System.err.println("pm = null???\t" + modelNameBox.getItemCount());
		}
		if (pm != null && !m_secondaryModels.containsKey(pm)) {
			m_secondaryModels.put(pm, new HashMap<String, ParametricModel>());
		}
	}
	
	private void insertNselectPMintoBox(ParametricModel pm) {
		int i=0;
		for (i=0;i<modelNameBox.getItemCount();i++) {
			if (pm.getModelId() == modelNameBox.getItemAt(i).getModelId()) {
			//if (pm.hashCode() == ((ParametricModel) modelNameBox.getItemAt(i)).hashCode()) {
				break;
			}
		}
		dontTouch = true;
		if (i == modelNameBox.getItemCount()) {
			modelNameBox.addItem(pm);
			//System.err.println("added3:" + pm + "\t" + pm.hashCode());
		}
		else if (!pm.equals(modelNameBox.getItemAt(i))) {
			modelNameBox.removeItemAt(i);
			modelNameBox.insertItemAt(pm, i);
		}
		modelNameBox.setSelectedItem(pm);
		dontTouch = false;		
	}

	private void formulaAreaFocusLost(FocusEvent e) {
		ParametricModel pm = table.getPM();
		if (pm != null && !pm.getFormula().equals(formulaArea.getText())) {
			String newMN = getNewModelname(pm);
			ParametricModel newPM = new ParametricModel(newMN, formulaArea.getText(), pm.getDepXml(), pm.getLevel(), MathUtilities.getRandomNegativeInt());
			insertNselectPMintoBox(newPM);
			parseFormula(pm, newPM);
			cloneSecondary(pm, newPM);
			modelNameBox.setSelectedItem(newPM);
		}
	}

	private void formulaAreaKeyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			formulaAreaFocusLost(null);
		}
	}

	private String getNewModelname(ParametricModel pm) {
		if (pm == null) return null;
		String mn = pm.getModelName();
		if (modelNameChangedManually) return mn;
		int lio = mn.lastIndexOf(" (v");
		String result = mn;
		try {
			if (lio >= 0) {
				String number = mn.substring(lio + 3, mn.length() - 1);
				Long.parseLong(number);
				result = mn.substring(0, lio);
			}
		}
		catch (Exception e) {}
		result += " (v" + System.currentTimeMillis() + ")";
		return result;
	}

	private void modelnameFieldFocusLost(FocusEvent e) {
		ParametricModel pm = table.getPM();
		if (pm != null && !pm.getModelName().equals(modelnameField.getText())) {
			ParametricModel newPM = pm.clone();
			newPM.setModelName(modelnameField.getText());
			newPM.setModelId(MathUtilities.getRandomNegativeInt());
			cloneSecondary(pm, newPM);
			/*
			System.err.println(pm.getParamValue("a0") + "\t" + pm + "\t" + pm.hashCode() + "\n" +
					newPM.getParamValue("a0") + "\t" + newPM + "\t" + newPM.hashCode() + "\n" +
					table.getValueAt(0, 2) + "\t" + table.getPM() + "\t" + table.getPM().hashCode() + "\t" + table.getPM().getParamValue("a0"));
					*/
			insertNselectPMintoBox(newPM);
			modelNameBox.setSelectedItem(newPM);
			modelNameChangedManually = true;
		}
	}
	private void cloneSecondary(ParametricModel pm, ParametricModel newPM) {
		HashMap<String, ParametricModel> smOld = m_secondaryModels.get(pm);
		if (smOld != null && !m_secondaryModels.containsKey(newPM)) {
			HashMap<String, ParametricModel> smNew = new HashMap<String, ParametricModel>();
			for (String key : smOld.keySet()) {
				if (smOld.get(key) != null) smNew.put(key, smOld.get(key).clone());
			}
			m_secondaryModels.put(newPM, smNew);
		}		
	}

	private void modelnameFieldKeyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			modelnameFieldFocusLost(null);
		}
	}

	private int getLastClickedCol(final MouseEvent e, JTable table) {
	      int lastClickedCol;
	      int val = 0;
	      for (lastClickedCol = 0; lastClickedCol < table.getColumnCount(); lastClickedCol++) {
	      	val += table.getColumnModel().getColumn(lastClickedCol).getWidth();
	      	if (val >= e.getX()) {
				break;
			}
	      }		
	      return lastClickedCol;
	}
	private int getLastClickedRow(final MouseEvent e, JTable table) {
	    int lastClickedRow;// = e.getY()/this.getTable().getRowHeight();
	    int val = 0;
	    for (lastClickedRow = 0; lastClickedRow < table.getRowCount(); lastClickedRow++) {
	    	val += table.getRowHeight(lastClickedRow);
	    	if (val >= e.getY()) {
				break;
			}
	    }		
	    return lastClickedRow;
	}
	private void tableMouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			int row = getLastClickedRow(e, table);
			int col = getLastClickedCol(e, table);
			if (col == 0) {
				ParametricModel pm = getPM();
				if (pm != null) {
					String param = table.getValueAt(row, col).toString();
					String defCategory, defUnit;
					Object isIndep = table.getValueAt(row, 1);
			    	if (isIndep != null && isIndep instanceof Boolean && ((Boolean) isIndep)) {
			    		defCategory = pm.getIndepCategory(param);
			    		defUnit = pm.getIndepUnit(param);
			    	}
			    	else {
			    		defCategory = pm.getParamCategory(param);
			    		defUnit = pm.getParamUnit(param);		
			    	}

			    	String categoryStr = (String)JOptionPane.showInputDialog(
							null,
							"Bitte eine Einheitenkategorie angeben f�r " + param + ":",
							"Einheit des Parameters " + param,
							JOptionPane.QUESTION_MESSAGE,
							null,
							Categories.getAllCategories(),
							defCategory);
					
			    	if (categoryStr != null) {
			    		Category category = Categories.getCategory(categoryStr);
						String unit = (String)JOptionPane.showInputDialog(
								null,
								"Bitte eine Einheit angeben f�r " + param + " (Kategorie: " + categoryStr + "):",
								"Einheit des Parameters " + param,
								JOptionPane.QUESTION_MESSAGE,
								null,
								category.getAllUnits(),
								defUnit != null && !defUnit.isEmpty() ? defUnit : category.getStandardUnit());
						
				    	if (unit != null) {
					    	if (isIndep != null && isIndep instanceof Boolean && ((Boolean) isIndep)) {
					    		pm.setIndepCategory(param, categoryStr);
					    		pm.setIndepUnit(param, unit);
					    	}
					    	else {
					    		pm.setParamCategory(param, categoryStr);
					    		pm.setParamUnit(param, unit);		
					    	}
				    	}
			    	}
				}
			}
		}
		else if (radioButton3.isSelected()) {
			int row = table.getSelectedRow();
			int col = table.getSelectedColumn();
			Object isIndep = table.getValueAt(row, 1);
			if (col == 0 && (isIndep == null || isIndep instanceof Boolean && !((Boolean) isIndep))) {
				SecDialog secondaryDialog = new SecDialog(m_parentFrame);
				secondaryDialog.setModal(true);
				secondaryDialog.setIconImage(Resources.getInstance().getDefaultIcon());
				String param = table.getValueAt(row, 0).toString();
				MMC_M m2 = new MMC_M(null, 2, param, formulaCreator, null);
				m2.setConnection(m_conn);
				HashMap<String, ParametricModel> sm = m_secondaryModels.get(table.getPM());
				m2.setPM(sm.get(param));
				secondaryDialog.setPanel(m2, param, sm);
				secondaryDialog.pack();
				
				secondaryDialog.setLocationRelativeTo(this);
				//secondaryDialog.setAlwaysOnTop(true);
				secondaryDialog.setVisible(true);
				
			}
		}
	}

	private void radioButtonActionPerformed(ActionEvent e) {
		if (m_conn != null) {
			/*
			int level = radioButton2.isSelected() ? 2 : 1;
			ParametricModel pm = table.getPM();

			//if (!radioButton3.isSelected()) m_secondaryModels.clear();
			if (level != pm.getLevel())
			*/
			setComboBox();
			modelNameBox.repaint();
			this.revalidate();
			modelNameBoxActionPerformed(null);
		}
	}
	private int getSelRadio() {
		return radioButton1.isSelected() ? 1 : radioButton2.isSelected() ? 2 : 3;
	}

	private ParametricModel finalizePM(int lastSelIndex) {
		ParametricModel pm = table.getPM();
		if (table.hasChanged()) {
			if (pm != null && pm.getModelName().equals(modelnameField.getText())) {
				modelnameField.setText(getNewModelname(pm));
				modelnameFieldFocusLost(null);
				pm = (ParametricModel) modelNameBox.getSelectedItem();
			}
		}
		pm.removeEstModelLits();
		pm.removeModelLits();
		for (int i=0;i<referencesTable.getRowCount();i++) {
			LiteratureItem li = (LiteratureItem) referencesTable.getValueAt(i, 0);
			if (formulaCreator || !table.isEstimated()) pm.addModelLit(li);
			else pm.addEstModelLit(li);
		}
		
		if (listModel != null && lastSelIndex >= 0 && lastSelIndex < listModel.size()) {
			dontFireList = true;
			listModel.remove(lastSelIndex);
			listModel.add(lastSelIndex, pm);
			//list1.setSelectedIndex(selIndex);
			list1.revalidate();
			dontFireList = false;
		}
		
		// change other identical secondaryModels 
		if (m_secondaryModels.containsKey(pm)) {
			HashMap<String, ParametricModel> hm = m_secondaryModels.get(pm);
			for (ParametricModel pm_ : hm.values()) {
				for (HashMap<String, ParametricModel> hmAll : m_secondaryModels.values()) {
					for (ParametricModel pmAll : hmAll.values()) {
						if (!pm_.equals(pmAll) && pmAll.getEstModelId() == pm_.getEstModelId()) {
							System.err.println("WEW");
						}
					}
				}
			}
		}
		
		// fetch TS
		if (m_mmcts != null && tss != null) {
			PmmTimeSeries ts = m_mmcts.getTS();
			tss.put(ts.getCondId(), ts);			
		}
		return pm;
	}
	public String listToXmlString() {
		if (listModel == null || listModel.size() == 0) {
			if (m_mmcts != null && m_mmcts.getCondId() != null) getPM().setCondId(m_mmcts.getCondId());
			return toXmlString();
		}
		PmmXmlDoc doc = new PmmXmlDoc();
		for (int i=0;i<listModel.size();i++) {
			if (list1.isSelectedIndex(i)) finalizePM(i);
			ParametricModel pm1 = listModel.get(i);
			doc.add(pm1);
			if (m_secondaryModels.containsKey(pm1)) {
				for (Map.Entry<String, ParametricModel> entry : m_secondaryModels.get(pm1).entrySet()) {
					String key = entry.getKey();
					if (pm1.containsParam(key)) {
						ParametricModel value = entry.getValue();
						doc.add(value);
					}
				}
			}
		}
		return doc.toXmlString();
	}
	public String tssToXmlString() {
		if (formulaCreator) return "";
		PmmXmlDoc doc = new PmmXmlDoc();
		if (tss == null) {
			PmmTimeSeries ts = m_mmcts.getTS();
			doc.add(ts);
		}
		else {
			for (PmmTimeSeries ts : tss.values()) {
				doc.add(ts);
			}
		}
		return doc.toXmlString();
	}
	private String toXmlString() {
		PmmXmlDoc doc = new PmmXmlDoc();

		ParametricModel pm = finalizePM(-1);
		doc.add(pm);
		
		if (radioButton3.isSelected()) {
			for (Map.Entry<String, ParametricModel> entry : m_secondaryModels.get(pm).entrySet()) {
				String key = entry.getKey();
				if (pm.containsParam(key)) {
					ParametricModel value = entry.getValue();
					doc.add(value);
				}
			}
		}
		return doc.toXmlString();
	}
	
	public void setInputData(Collection<ParametricModel> m1s, HashMap<ParametricModel, HashMap<String, ParametricModel>> m_secondaryModels,
			HashMap<Integer, PmmTimeSeries> tss) {
		this.tss = tss;
		this.m_secondaryModels = m_secondaryModels;
		dontFireList = true;
		listModel = new DefaultListModel<>();
		list1.setModel(listModel);
		for (ParametricModel pm : m1s) {
			listModel.addElement(pm);
		}
		scrollPane3.setVisible(true);
		dontFireList = false;
		if (listModel.getSize() > 0) list1.setSelectedIndex(0);
	}

	public void setFromXmlString(final String xmlString) {		
		try {			
			PmmXmlDoc doc = new PmmXmlDoc(xmlString);			
			// fetch model set
			ParametricModel theModel = null;
			HashMap<String, ParametricModel> sm = new HashMap<String, ParametricModel>();
			for (int i = 0; i < doc.size(); i++) {				
				PmmXmlElementConvertable el = doc.get(i);
				if (el instanceof ParametricModel) {
					ParametricModel pm = (ParametricModel) el;
					
					if (pm.getLevel() == 1) {
						theModel = pm;
					}
					else {
						if (theModel == null) theModel = pm;
						sm.put(pm.getDepVar(), pm);
					}
				}				
			}

			if (theModel != null) {
				if (theModel.getLevel() == 1) {
					m_secondaryModels.put(theModel, sm);
				}
				if (sm.size() > 0) {
					radioButton3.setSelected(true);
				}
				setComboBox();
				setPM(theModel);
			}
			/*
			List<Integer> li = new ArrayList<Integer>();
			for( LiteratureItem item : primModel.getModelLit()) {
				li.add(item.getId());
			}
			for( LiteratureItem item : primModel.getEstModelLit()) {
				li.add(item.getId());
			}
			ParametricModel pmc = modelCatalog.get( modelNameBox.getSelectedItem() );
			modLitMat.put(pmc.getModelId(), li);
			
			updatePossibleLiterature();
			updateLiterature();
			*/
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void button1ActionPerformed(ActionEvent e) {
		// New
		doLit(null);
	}

	private void button2ActionPerformed(ActionEvent e) {
		// Delete
		deleteSelLitRow();
	}

	private void button3ActionPerformed(ActionEvent e) {
		// Edit
		LiteratureItem li = (LiteratureItem) referencesTable.getValueAt(referencesTable.getSelectedRow(), 0);
		//if (li != null) doLit(li.getId());
		doLit(li);
	}
	private void deleteSelLitRow() {
		((DefaultTableModel) referencesTable.getModel()).removeRow(referencesTable.getSelectedRow());		
	}
	private void doLit(LiteratureItem oldLi) {
		MyTable lit = MyDBTables.getTable("Literatur");
		Integer litID = (oldLi != null && (dbuuid != null && dbuuid.equals(oldLi.getDbuuid()))) ? oldLi.getID() : null;
		Object newVal = DBKernel.myList.openNewWindow(
				lit,
				litID,
				(Object) "Literatur",
				null,
				1,
				1,
				null,
				true, null, this);
		if (newVal != null && newVal instanceof Integer) {
			Object author = DBKernel.getValue("Literatur", "ID", newVal.toString(), "Erstautor");
			Object year = DBKernel.getValue("Literatur", "ID", newVal.toString(), "Jahr");
			Object title = DBKernel.getValue("Literatur", "ID", newVal.toString(), "Titel");
			Object mAbstract = DBKernel.getValue("Literatur", "ID", newVal.toString(), "Abstract");
			LiteratureItem li = new LiteratureItem(author == null ? "?" : author.toString(),
					year == null ? null : (Integer) year,
							title == null ? "?" : title.toString(),
							mAbstract == null ? "?" : mAbstract.toString(),
							(Integer) newVal, dbuuid);
			Vector<LiteratureItem> vli = new Vector<LiteratureItem>();
			vli.add(li);
			if (oldLi != null) {
				int selRow = referencesTable.getSelectedRow();
				deleteSelLitRow();
				((DefaultTableModel) referencesTable.getModel()).insertRow(selRow, vli);
			}
			else {
				((DefaultTableModel) referencesTable.getModel()).addRow(vli);
			}
		}		
	}

	private void r2FieldFocusLost(FocusEvent e) {
		ParametricModel pm = table.getPM();
		if (pm != null) {
			try {
				pm.setRsquared(r2Field.getValue());
			} catch (PmmException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void r2FieldKeyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			r2FieldFocusLost(null);
		}
	}

	private void rmsFieldFocusLost(FocusEvent e) {
		ParametricModel pm = table.getPM();
		if (pm != null) {
			try {
				pm.setRms(rmsField.getValue());
			} catch (PmmException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void rmsFieldKeyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			rmsFieldFocusLost(null);
		}
	}

	private void aicFieldFocusLost(FocusEvent e) {
		ParametricModel pm = table.getPM();
		if (pm != null) {
			pm.setAic(aicField.getValue());
		}
	}

	private void aicFieldKeyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			aicFieldFocusLost(null);
		}
	}

	private void bicFieldFocusLost(FocusEvent e) {
		ParametricModel pm = table.getPM();
		if (pm != null) {
			pm.setBic(bicField.getValue());
		}
	}

	private void bicFieldKeyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			bicFieldFocusLost(null);
		}
	}

	private void qScoreBoxActionPerformed(ActionEvent e) {
		ParametricModel pm = table.getPM();
		if (pm != null) {
			try {
				pm.setQualityScore(qScoreBox.getSelectedIndex());
			} catch (PmmException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void checkBox1ActionPerformed(ActionEvent e) {
		ParametricModel pm = table.getPM();
		if (pm != null) {
			try {
				pm.setChecked(checkBox1.isSelected());
			} catch (PmmException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void list1ValueChanged(ListSelectionEvent e) {
		if (!dontFireList && !e.getValueIsAdjusting() && list1.getSelectedIndex() >= 0) {
			if (list1.getSelectedIndex() != e.getFirstIndex()) finalizePM(e.getFirstIndex());
			ParametricModel pm1 = list1.getSelectedValue();
			if (m_secondaryModels.get(pm1) != null && m_secondaryModels.get(pm1).size() > 0) {
				radioButton3.setSelected(true);
			}
			setComboBox();
			setPM(pm1);

    		m_mmcts.setTS(tss.get(pm1.getCondId()));
		}
	}

	private void depVarLabel4UnitsMouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			ParametricModel pm = getPM();
			if (pm != null) {
				String param = pm.getDepVar();
				String defCategory = pm.getDepCategory();
				String defUnit = pm.getDepUnit();

		    	String categoryStr = (String)JOptionPane.showInputDialog(
						null,
						"Bitte eine Einheitenkategorie angeben f�r " + param + ":",
						"Einheit des Parameters " + param,
						JOptionPane.QUESTION_MESSAGE,
						null,
						Categories.getAllCategories(),
						defCategory);
				
		    	if (categoryStr != null) {
		    		Category category = Categories.getCategory(categoryStr);
					String unit = (String)JOptionPane.showInputDialog(
							null,
							"Bitte eine Einheit angeben f�r " + param + " (Kategorie: " + categoryStr + "):",
							"Einheit des Parameters " + param,
							JOptionPane.QUESTION_MESSAGE,
							null,
							category.getAllUnits(),
							defUnit != null && !defUnit.isEmpty() ? defUnit : category.getStandardUnit());
					
			    	if (unit != null) {
			    		pm.setDepCategory(categoryStr);
			    		pm.setDepUnit(unit);
			    		depVarLabel4Units.setText("[" + categoryStr + " -> " + unit + "]");
			    	}
		    	}
			}
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		depVarLabel = new JLabel();
		scrollPane3 = new JScrollPane();
		list1 = new JList<>();
		label7 = new JLabel();
		radioButton1 = new JRadioButton();
		radioButton2 = new JRadioButton();
		radioButton3 = new JRadioButton();
		modelNameLabel = new JLabel();
		modelNameBox = new JComboBox<>();
		label1 = new JLabel();
		modelnameField = new JTextField();
		label2 = new JLabel();
		formulaArea = new JTextField();
		formulaApply = new JButton();
		label12 = new JLabel();
		depVarLabel4Units = new JLabel();
		tableLabel = new JLabel();
		scrollPane1 = new JScrollPane();
		table = new ModelTableModel();
		label8 = new JLabel();
		label3 = new JLabel();
		r2Field = new DoubleTextField(true);
		label4 = new JLabel();
		rmsField = new DoubleTextField(true);
		label5 = new JLabel();
		aicField = new DoubleTextField(true);
		label6 = new JLabel();
		bicField = new DoubleTextField(true);
		scrollPane2 = new JScrollPane();
		referencesTable = new JTable();
		label9 = new JLabel();
		button1 = new JButton();
		button3 = new JButton();
		button2 = new JButton();
		label10 = new JLabel();
		label11 = new JLabel();
		qScoreBox = new JComboBox<>(new Color[] {Color.WHITE, Color.GREEN, Color.YELLOW, Color.RED});
		qScoreBox.setRenderer(new DefaultListCellRenderer() {
					private static final long serialVersionUID = 1L;

					private Color color = Color.WHITE;
					private boolean isSelected = false;

					@Override
					public Component getListCellRendererComponent(JList<?> list,
							Object value, int index, boolean isSelected,
							boolean cellHasFocus) {
						color = (Color) value;
						this.isSelected = isSelected;

						return super.getListCellRendererComponent(list, value, index,
								isSelected, cellHasFocus);
					}

					@Override
					protected void paintComponent(Graphics g) {
						Rectangle rect = g.getClipBounds();

						if (rect != null) {
							g.setColor(color);
							g.fillRect(rect.x, rect.y, rect.width, rect.height);

							if (isSelected) {
								g.setColor(UIManager.getDefaults().getColor(
										"List.selectionBackground"));
							} else {
								g.setColor(UIManager.getDefaults().getColor(
										"List.background"));
							}

							((Graphics2D) g).setStroke(new BasicStroke(5));
							g.drawRect(rect.x, rect.y, rect.width, rect.height);
						}
					}
		});
		checkBox1 = new JCheckBox();

		//======== this ========
		setBorder(new CompoundBorder(
			new TitledBorder("Model Properties"),
			Borders.DLU2));
		setLayout(new FormLayout(
			"4*(default, $lcgap), default:grow, 2*($lcgap, default), $lcgap, default:grow, 2*($lcgap, default), $lcgap, default:grow",
			"default, $rgap, default, $ugap, 2*(default, $pgap), default, $ugap, default, $lgap, 2*(default, $ugap), default, $lgap, fill:default:grow, 1dlu, default, $pgap, default"));
		((FormLayout)getLayout()).setColumnGroups(new int[][] {{5, 11, 17}, {7, 13, 19}, {9, 15, 21}});

		//---- depVarLabel ----
		depVarLabel.setText("Parameter");
		depVarLabel.setHorizontalAlignment(SwingConstants.CENTER);
		depVarLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		add(depVarLabel, CC.xywh(3, 1, 19, 1));

		//======== scrollPane3 ========
		{

			//---- list1 ----
			list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list1.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					list1ValueChanged(e);
				}
			});
			scrollPane3.setViewportView(list1);
		}
		add(scrollPane3, CC.xywh(1, 1, 1, 23));

		//---- label7 ----
		label7.setText("Model type:");
		add(label7, CC.xy(3, 3));

		//---- radioButton1 ----
		radioButton1.setText("primary");
		radioButton1.setSelected(true);
		radioButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				radioButtonActionPerformed(e);
			}
		});
		add(radioButton1, CC.xywh(5, 3, 5, 1));

		//---- radioButton2 ----
		radioButton2.setText("secondary");
		radioButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				radioButtonActionPerformed(e);
			}
		});
		add(radioButton2, CC.xywh(11, 3, 5, 1));

		//---- radioButton3 ----
		radioButton3.setText("primary (secondary)");
		radioButton3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				radioButtonActionPerformed(e);
			}
		});
		add(radioButton3, CC.xywh(17, 3, 5, 1));

		//---- modelNameLabel ----
		modelNameLabel.setText("Formula from DB:");
		add(modelNameLabel, CC.xy(3, 5));

		//---- modelNameBox ----
		modelNameBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modelNameBoxActionPerformed(e);
			}
		});
		add(modelNameBox, CC.xywh(5, 5, 17, 1));

		//---- label1 ----
		label1.setText("Formula Name:");
		add(label1, CC.xy(3, 7));

		//---- modelnameField ----
		modelnameField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				modelnameFieldFocusLost(e);
			}
		});
		modelnameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				modelnameFieldKeyReleased(e);
			}
		});
		add(modelnameField, CC.xywh(5, 7, 17, 1));

		//---- label2 ----
		label2.setText("Formula:");
		add(label2, CC.xy(3, 9));

		//---- formulaArea ----
		formulaArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				formulaAreaFocusLost(e);
			}
		});
		formulaArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				formulaAreaKeyReleased(e);
			}
		});
		add(formulaArea, CC.xywh(5, 9, 15, 1));

		//---- formulaApply ----
		formulaApply.setText("Apply");
		add(formulaApply, CC.xy(21, 9));

		//---- label12 ----
		label12.setText("Dependent Unit:");
		add(label12, CC.xy(3, 11));

		//---- depVarLabel4Units ----
		depVarLabel4Units.setText("[]");
		depVarLabel4Units.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				depVarLabel4UnitsMouseClicked(e);
			}
		});
		add(depVarLabel4Units, CC.xywh(5, 11, 17, 1));

		//---- tableLabel ----
		tableLabel.setText("Parameter Definition:");
		add(tableLabel, CC.xy(3, 13));

		//======== scrollPane1 ========
		{

			//---- table ----
			table.setPreferredScrollableViewportSize(new Dimension(450, 175));
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					tableMouseClicked(e);
				}
			});
			scrollPane1.setViewportView(table);
		}
		add(scrollPane1, CC.xywh(5, 13, 17, 1));

		//---- label8 ----
		label8.setText("Goodness of fit:");
		add(label8, CC.xywh(3, 15, 1, 3));

		//---- label3 ----
		label3.setText("R\u00b2:");
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		add(label3, CC.xy(5, 15));

		//---- r2Field ----
		r2Field.setColumns(7);
		r2Field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				r2FieldFocusLost(e);
			}
		});
		r2Field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				r2FieldKeyReleased(e);
			}
		});
		add(r2Field, CC.xy(7, 15));

		//---- label4 ----
		label4.setText("RMS:");
		label4.setHorizontalAlignment(SwingConstants.CENTER);
		add(label4, CC.xy(11, 15));

		//---- rmsField ----
		rmsField.setColumns(7);
		rmsField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				rmsFieldFocusLost(e);
			}
		});
		rmsField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				rmsFieldKeyReleased(e);
			}
		});
		add(rmsField, CC.xy(13, 15));

		//---- label5 ----
		label5.setText("AIC:");
		add(label5, CC.xy(5, 17));

		//---- aicField ----
		aicField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				aicFieldFocusLost(e);
			}
		});
		aicField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				aicFieldKeyReleased(e);
			}
		});
		add(aicField, CC.xy(7, 17));

		//---- label6 ----
		label6.setText("BIC:");
		add(label6, CC.xy(11, 17));

		//---- bicField ----
		bicField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				bicFieldFocusLost(e);
			}
		});
		bicField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				bicFieldKeyReleased(e);
			}
		});
		add(bicField, CC.xy(13, 17));

		//======== scrollPane2 ========
		{
			scrollPane2.setPreferredSize(new Dimension(452, 120));

			//---- referencesTable ----
			referencesTable.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Reference"
				}
			) {
				/**
				 * 
				 */
				private static final long serialVersionUID = -8486729813187070298L;
				boolean[] columnEditable = new boolean[] {
					false
				};
				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return columnEditable[columnIndex];
				}
			});
			scrollPane2.setViewportView(referencesTable);
		}
		add(scrollPane2, CC.xywh(5, 19, 17, 1));

		//---- label9 ----
		label9.setText("References:");
		add(label9, CC.xywh(3, 19, 1, 3));

		//---- button1 ----
		button1.setText("New Reference");
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				button1ActionPerformed(e);
			}
		});
		add(button1, CC.xywh(5, 21, 5, 1));

		//---- button3 ----
		button3.setText("Edit Reference");
		button3.setEnabled(false);
		button3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				button3ActionPerformed(e);
			}
		});
		add(button3, CC.xywh(11, 21, 5, 1));

		//---- button2 ----
		button2.setText("Delete Reference");
		button2.setEnabled(false);
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				button2ActionPerformed(e);
			}
		});
		add(button2, CC.xywh(17, 21, 5, 1));

		//---- label10 ----
		label10.setText("Subjective quality:");
		add(label10, CC.xy(3, 23));

		//---- label11 ----
		label11.setText("QualityScore:");
		add(label11, CC.xywh(5, 23, 3, 1));

		//---- qScoreBox ----
		qScoreBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				qScoreBoxActionPerformed(e);
			}
		});
		add(qScoreBox, CC.xy(9, 23));

		//---- checkBox1 ----
		checkBox1.setText("Checked");
		checkBox1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBox1ActionPerformed(e);
			}
		});
		add(checkBox1, CC.xy(15, 23));

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radioButton1);
		buttonGroup1.add(radioButton2);
		buttonGroup1.add(radioButton3);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel depVarLabel;
	private JScrollPane scrollPane3;
	private JList<ParametricModel> list1;
	private JLabel label7;
	private JRadioButton radioButton1;
	private JRadioButton radioButton2;
	private JRadioButton radioButton3;
	private JLabel modelNameLabel;
	private JComboBox<ParametricModel> modelNameBox;
	private JLabel label1;
	private JTextField modelnameField;
	private JLabel label2;
	private JTextField formulaArea;
	private JButton formulaApply;
	private JLabel label12;
	private JLabel depVarLabel4Units;
	private JLabel tableLabel;
	private JScrollPane scrollPane1;
	private ModelTableModel table;
	private JLabel label8;
	private JLabel label3;
	private DoubleTextField r2Field;
	private JLabel label4;
	private DoubleTextField rmsField;
	private JLabel label5;
	private DoubleTextField aicField;
	private JLabel label6;
	private DoubleTextField bicField;
	private JScrollPane scrollPane2;
	private JTable referencesTable;
	private JLabel label9;
	private JButton button1;
	private JButton button3;
	private JButton button2;
	private JLabel label10;
	private JLabel label11;
	private JComboBox<Color> qScoreBox;
	private JCheckBox checkBox1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

}