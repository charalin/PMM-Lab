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
package de.bund.bfr.knime.foodprocess.ui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerGenerator;
import org.eclipse.ui.ide.IDE;
import org.hsh.bfr.db.DBKernel;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.Node;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.NodeAnnotationData;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.util.VMFileLocker;
import org.knime.workbench.ui.layout.commands.AutoLayoutCommand;
import org.knime.workbench.ui.nature.KNIMEProjectNature;

import de.bund.bfr.knime.foodprocess.FoodProcessNodeFactory;
import de.bund.bfr.knime.foodprocess.FoodProcessNodeModel;
import de.bund.bfr.knime.foodprocess.FoodProcessNodeSettings;
import de.bund.bfr.knime.foodprocess.addons.AddonNodeModel;
import de.bund.bfr.knime.foodprocess.addons.IngredientsNodeFactory;
import de.bund.bfr.knime.foodprocess.lib.FoodProcessSetting;

public class CreateWorkflow extends AbstractHandler {
	/**
	 * The constructor.
	 */
	
	private int processchainID = 12; // 12: ESL-Mikrofiltration
	
	public CreateWorkflow(final int pcID) {
		this.processchainID = pcID;
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
			loadWorkflowManager2(DBKernel.getLocalConn(true), processchainID);
		}
		catch (Exception e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}
	
	private void loadWorkflowManager2(final Connection conn, final int pcId)
			throws Exception {

		String projectName = "PC_" + System.currentTimeMillis();
	    Object pn = DBKernel.getValue("ProzessWorkflow", "ID", pcId+"", "Name");
	    projectName = "PC_" + pn.toString(); 
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	    IResource resource = root.findMember(new Path(projectName));
	    if (resource != null) {
	    	projectName += "_" + System.currentTimeMillis();
	    }

		// create wfm for the project
		final WorkflowManager wfm = WorkflowManager.ROOT.createAndAddProject(projectName);
		// add nodes and connect them
		addNodes(conn, wfm);
	    		
	    if (wfm.getNodeContainers().size() > 1) {
	    	// Autolayout workflow
		    AutoLayoutCommand layoutCommand = new AutoLayoutCommand(wfm, null);
		    layoutCommand.execute();
	    }	

	    // create project
		IProject proj = createProject(new Path(projectName));
		// save workflow to the project
	    wfm.save(proj.getLocation().toFile(), new ExecutionMonitor(), false);
	    // refresh so that project shows up in "Workflow projects"
	    proj.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	    
	    // remove lock from the project
	    VMFileLocker.unlockForVM(proj.getLocation().toFile());
	    
	    // get the "workflow file"
        final IFile workflowFile = proj.getFile(
                    new Path(WorkflowPersistor.WORKFLOW_FILE));
        Display display = Display.getDefault();
        display.asyncExec(new Runnable() {
            @Override
			public void run() {
                IWorkbenchPage page =
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                .getActivePage();
                try {
                	// open the workflow
                    IDE.openEditor(page, workflowFile, true);
                } catch (PartInitException e) {
                    // ignore it
                }
                
            }
        });	    
	}
	
	/** Add nodes the the workflow manager and connect them. */
	private void addNodes(final Connection conn, final WorkflowManager wfm) throws CoreException {	
		LinkedHashMap<Integer, NodeID> nodesMap = new LinkedHashMap<Integer, NodeID>();
		LinkedHashMap<Integer, Integer> fromMap = new LinkedHashMap<Integer, Integer>();
		LinkedHashMap<Integer, Integer> toMap = new LinkedHashMap<Integer, Integer>();

		ResultSet rs = DBKernel.getResultSet("SELECT * FROM " + DBKernel.delimitL("Prozessdaten") +
				" WHERE " + DBKernel.delimitL("Workflow") + "=" + processchainID, false);
		try {
			if (rs != null && rs.first()) {
				FoodProcessNodeFactory pf = new FoodProcessNodeFactory();
				do {
				    Integer processID = rs.getInt("ID");
				    // create a custom node annotation (diplayed at the bottom of the node)
				    Object element = DBKernel.getValue("ProzessElemente", "ID", rs.getInt("Prozess_CARVER")+"", "ProzessElement");
				    Object nodeAnno = rs.getObject("ProzessDetail");
				    if (nodeAnno == null || nodeAnno.toString().length() == 0) {
				    	nodeAnno = (element == null ? "" : element.toString());
				    }

				    SingleNodeContainer p2nc = createNodeContainer(wfm, processID, nodeAnno, rs.getString("ProzessDetail"), nodesMap, pf);;
				    Node p2Node = p2nc.getNode();
				    
				    // create custom settings for the food process node
				    FoodProcessSetting foodProcessSetting = new FoodProcessSetting(4, 4);
				    foodProcessSetting.setProcessName(element == null ? "" : element.toString()); // "Milchturm"
				    Double dbl = getDouble(DBKernel.getValue("DoubleKennzahlen", "ID", rs.getInt("Kapazitaet")+"", "Wert"));
				    if (dbl != null) {
						foodProcessSetting.setCapacity(dbl);
					}
				    Integer cuv = rs.getInt("KapazitaetEinheit");
				    // 1, "Kilogramm"
				    // 2, "Gramm"
				    // 7, "Liter"
				    foodProcessSetting.setCapacityUnitVolume(cuv == 1 ? "kg" : cuv == 2 ? "g" : cuv == 7 ? "l" : "");
				    String cut = rs.getString("KapazitaetEinheitBezug");
				    if (cut != null) {
					    foodProcessSetting.setCapacityUnitTime(cut.equals("Stunde") ? "h" : cut.equals("Sekunde") ? "s" :
					    	cut.equals("Tag") ? "d" : cut.equals("Minute") ? "min" : "");				    	
				    }
				    dbl = getDouble(DBKernel.getValue("DoubleKennzahlen", "ID", rs.getInt("Dauer")+"", "Wert"));
				    if (dbl != null) {
						foodProcessSetting.setDuration(dbl);
					}
				    String du = rs.getString("DauerEinheit");
				    if (du != null) {
					    foodProcessSetting.setDurationUnit(du.equals("Stunde") ? "h" : du.equals("Sekunde") ? "s" :
					    	du.equals("Tag") ? "d" : du.equals("Minute") ? "min" : "");				    	
				    }
				    dbl = getDouble(DBKernel.getValue("DoubleKennzahlen", "ID", rs.getInt("Temperatur")+"", "Wert"));
				    if (dbl != null) {
						foodProcessSetting.getParametersSetting().setTemperature(dbl+"");
					}
				    foodProcessSetting.getParametersSetting().setTemperatureUnit("�C");
				    dbl = getDouble(DBKernel.getValue("DoubleKennzahlen", "ID", rs.getInt("pH")+"", "Wert"));
				    if (dbl != null) {
						foodProcessSetting.getParametersSetting().setPh(dbl+"");
					}
				    dbl = getDouble(DBKernel.getValue("DoubleKennzahlen", "ID", rs.getInt("aw")+"", "Wert"));
				    if (dbl != null) {
						foodProcessSetting.getParametersSetting().setAw(dbl+"");
					}
				    dbl = getDouble(DBKernel.getValue("DoubleKennzahlen", "ID", rs.getInt("Druck")+"", "Wert"));
				    if (dbl != null) {
						foodProcessSetting.getParametersSetting().setPressure(dbl+"");
					}
				    foodProcessSetting.getParametersSetting().setPressureUnit("bar");
				    
				    System.err.println(rs.getString("KapazitaetEinheit") + "\t" + rs.getString("KapazitaetEinheitBezug")
				    		 + "\t" + rs.getString("DauerEinheit") + "\t" +
				    		 DBKernel.getValue("ProzessElemente", "ID", rs.getInt("Prozess_CARVER")+"", "ProzessElement"));
				    
				    foodProcessSetting.setStepWidth(1.0);
				    foodProcessSetting.setStepWidthUnit("s");
				    
				    FoodProcessNodeSettings p2Settings = new FoodProcessNodeSettings();
				    p2Settings.setFoodProcessSetting(foodProcessSetting);
				    
				    // get the node model
				    FoodProcessNodeModel p2Model = ((FoodProcessNodeModel)p2Node.getNodeModel());
				    // set the custom settings
				    p2Model.setSetting(p2Settings);
			    	
				    try {
				    	LinkedList<Double> volumeDef = new LinkedList<Double>();
				    	LinkedList<String> volumeUnitDef = new LinkedList<String>();
				    	LinkedList<Integer> iarr = new LinkedList<Integer>();
				    	LinkedList<String> narr = new LinkedList<String>();
						ResultSet rsZ = DBKernel.getResultSet("SELECT * FROM " + DBKernel.delimitL("Zutatendaten") +
								" WHERE " + DBKernel.delimitL("Prozessdaten") + "=" + processID, false);
						if (rsZ != null && rsZ.first()) {
							do {
								String zp = rsZ.getString("Zutat_Produkt");
								Object vp = rsZ.getObject("Vorprozess");
								Object mat = rsZ.getObject("Matrix");
								if (zp.equals("Zutat")) {
									if (mat != null) {
										if (vp == null) {
											// new Node: IngredientNode
										    dbl = getDouble(DBKernel.getValue("DoubleKennzahlen", "ID", rsZ.getInt("Unitmenge")+"", "Wert"));
											int unit = rsZ.getInt("UnitEinheit");
										    if (dbl != null && (unit <= 7)) { // ADV-Codes: 1=Kilogramm 2=Gramm 7=Liter 24=Prozent 25=Promille 35=St�ck
										    	if (unit == 1) dbl *= 1000;
										    	volumeDef.add(dbl); volumeUnitDef.add(unit <= 2 ? "g" : "l");
										    	iarr.add((Integer) mat);
										    	narr.add(""+DBKernel.getValue("Matrices", "ID", mat+"", "Matrixname"));
											}
										    else {
										    	System.err.println("ERROR: Unitmenge als Zutat null... or Unit nicht ok...");
										    }
										}
										else {
											// Inport redefinition
										}
									}
									else if (vp == null) {
										System.err.println("ERROR: Matrix als Zutat null and Vorprozess auch ...");
									}
								}
								else if (zp.equals("Produkt")) {
									// Outport redefinition
								}
							} while (rsZ.next());	
						}
						if (volumeDef != null) {
							IngredientsNodeFactory inf = new IngredientsNodeFactory();
							SingleNodeContainer i2nc = createNodeContainer(wfm, null, "Zutats...", "Zutatenliste...", null, inf);
							AddonNodeModel i2Model = ((AddonNodeModel)i2nc.getNodeModel());
							i2Model.setSetting(volumeDef, volumeUnitDef, iarr, narr, true);
						    wfm.addConnection(i2nc.getID(), 1, p2nc.getID(), 1);
						    toMap.put(processID, 1);
						}
				    }
				    catch (Exception e) {e.printStackTrace();}
				} while (rs.next());
			}
			
		    // Connect the nodes
		    // Port 0 is the "Flow Variable Port"
		    // Port 1 is the first normal port
			for (Entry<Integer, NodeID> entry : nodesMap.entrySet()) {
				int fromKey = entry.getKey();
				rs = DBKernel.getResultSet("SELECT * FROM " + DBKernel.delimitL("Prozess_Verbindungen") +
						" WHERE " + DBKernel.delimitL("Ausgangsprozess") + "=" + fromKey, false);
				if (rs != null && rs.first()) {
					do {
						int toKey = rs.getInt("Zielprozess");
						//System.err.println(fromKey + "\t" + toKey);
						if (toKey > 0 && nodesMap.get(toKey) != null) {
							int fromPort = 1;
							if (fromMap.containsKey(fromKey)) {
								fromPort = fromMap.get(fromKey) + 1;
							}
							int toPort = 1;
							if (toMap.containsKey(toKey)) {
								toPort = toMap.get(toKey) + 1;
							}
							
						    wfm.addConnection(entry.getValue(), fromPort, nodesMap.get(toKey), toPort);
						    
						    fromMap.put(fromKey, fromPort);
						    toMap.put(toKey, toPort);
						}
					} while(rs.next());
				}
			}
		}
		catch (Exception e) {throwCoreException("Error in fetching process chain data from the internal database", e);}
	}
	
	@SuppressWarnings("rawtypes")
	private SingleNodeContainer createNodeContainer(WorkflowManager wfm, Integer processID, Object nodeAnno, String description, LinkedHashMap<Integer, NodeID> nodesMap, NodeFactory pf) {
	    // create a node with default settings
	    NodeID p2 = wfm.createAndAddNode(pf);
	    // set dummy location, the location will later be change by the
	    // layout manger, but a location must be set here, so that the node
	    // is correctly initialized.
	    setDummyLocation(wfm, p2);
	    
	    if (nodesMap != null) nodesMap.put(processID, p2);
	    
	    // get the container of the node
	    SingleNodeContainer p2Container = (SingleNodeContainer)wfm.getNodeContainer(p2);
	    // set the node description, normally set by clicking "set description" 
	    // in the context menu of the node.
	    p2Container.setCustomDescription(description); // "Ein sch�ner Milchturm"
	    
	    AnnotationData p2AnnoData = NodeAnnotationData.createFromObsoleteCustomName(nodeAnno.toString());
	    // set custom node annotation
	    p2Container.getNodeAnnotation().getData().copyFrom(p2AnnoData, true);
	    // set dirty so that changer are saved
	    p2Container.setDirty();
	    
	    
	    // get the node from the container
	    return p2Container;		
	}
	/** Place the node a (100px, 100px); */
	private void setDummyLocation(final WorkflowManager wfm, final NodeID nodeId) {
		wfm.getNodeContainer(nodeId).setUIInformation(
				new NodeUIInformation(100, 100, -1, -1, false));
	}

	private IProject createProject(final IPath workflowPath) throws CoreException {
		IProgressMonitor monitor = new NullProgressMonitor();
		
		 IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	        IResource resource = root.findMember(workflowPath);
	        if (resource != null) {
	            throwCoreException("Resource \"" + workflowPath.toString()
	                    + "\" does already exist.", null);
	        }
	        // check if there is a folder with the same name on the file system
	        // see bug (http://bimbug.inf.uni-konstanz.de/show_bug.cgi?id=1912)
	        IPath rootLocation = root.getLocation();
	        if (rootLocation != null) {
	            IPath absolutePath = rootLocation.append(workflowPath);
	            if (absolutePath.toFile().exists()) {
	                throwCoreException(
	                        "Resource " + workflowPath + " already exists!", null);
	            }
	        }
	        ContainerGenerator generator = new ContainerGenerator(workflowPath);
	        IContainer containerResult = generator.generateContainer(monitor);
	        if (containerResult instanceof IProject) {
	            IProject project = (IProject)containerResult;
	            // open the project
	            project.open(monitor);
	            // Create project description, set the nature IDs and build-commands
	            try {
	                // set the nature id of the project is enough
	                // the name is already set by IProject#create()
	                IProjectDescription description = project.getDescription();
	                description.setNatureIds(new String[]{KNIMEProjectNature.ID});
	                project.setDescription(description, monitor);
	                
	            } catch (CoreException ce) {
	                throwCoreException(
	                        "Error while creating project description for " 
	                        + project.getName(), ce);
	            }
	            return project;
	        }
	        return null;
	}
	
    private static void throwCoreException(final String message, 
            final Throwable t) 
        throws CoreException {
        IStatus status =
                new Status(IStatus.ERROR, "org.knime.workbench.ui", IStatus.OK,
                        message, t);
        throw new CoreException(status);
    }	

    private Double getDouble(final Object dbl) {
    	Double result = null;
    	if (dbl != null) {
    		try {
        		if (dbl instanceof Double) {
					result = (Double) dbl;
				} else {
					result = Double.parseDouble(dbl.toString());
				}    			
    		}
    		catch (Exception e) {}
    	}
    	return result;
    }
}
