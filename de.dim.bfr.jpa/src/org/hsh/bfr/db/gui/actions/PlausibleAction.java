package org.hsh.bfr.db.gui.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Array;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JProgressBar;

import org.hsh.bfr.db.DBKernel;
import org.hsh.bfr.db.MyLogger;
import org.hsh.bfr.db.MyTable;
import org.hsh.bfr.db.PlausibilityChecker;
import org.hsh.bfr.db.gui.PlausibleDialog;
import org.hsh.bfr.db.gui.dbtable.MyDBTable;
import org.hsh.bfr.db.imports.InfoBox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Armin
 *
 */
public class PlausibleAction extends AbstractAction {

	private JProgressBar progress;
	private MyDBTable myDB;

	public PlausibleAction(final String name, final Icon icon, final String toolTip, final JProgressBar progressBar1, final MyDBTable myDB) {
	  	this.progress = progressBar1;
	  	this.myDB = myDB;
	    putValue(Action.NAME, name);
	    putValue(Action.SHORT_DESCRIPTION, toolTip);
	    putValue(Action.SMALL_ICON, icon);
	    this.setEnabled(false);
	}    

	@Override
	public void actionPerformed(final ActionEvent e) {
	  	final PlausibleDialog pd = new PlausibleDialog(DBKernel.mainFrame); 
	  	pd.setVisible(true);
	  	final Vector<int[]> ids = parseIDs(pd.textField1.getText());
	  	if (pd.okPressed && (!pd.radioButton3.isSelected() || ids != null)) {
		  	Runnable runnable = new Runnable() {
		        @Override
				public void run() {
		  		    try {
		        		LinkedHashMap<String, MyTable> myTables = DBKernel.myList.getAllTables();
		        		if (progress != null) {
		        			progress.setVisible(true);
		        			progress.setStringPainted(true);
		        			progress.setString("Plausibilitätstests laufen...");
		        			progress.setMinimum(0);
		        			progress.setMaximum(myTables.size());
		        			progress.setValue(0);
		        		}
		  
		        		int lfd = 0;
		        	  	boolean showOnlyDataFromCurrentUser = pd.checkBox1.isSelected();
		        		//String result = "";
	        			String selectedTN = myDB.getActualTable().getTablename();
		        		Vector<String> result = new Vector<String>();
		        		for(String key : myTables.keySet()) {
		        			MyTable myT = myTables.get(key);
		        			String tn = myT.getTablename();
		        	  		if (pd.radioButton1.isSelected()) { // Alle Datensätze
		        	  			go4Table(tn, result, -1, -1, myT, showOnlyDataFromCurrentUser);
		        	  		}
		        	  		else if (pd.radioButton2.isSelected()) { // Nur sichtbare Tabelle
		        	  			if (tn.equals(selectedTN)) {
		        	  				go4Table(tn, result, -1, -1, myT, showOnlyDataFromCurrentUser);
		        	  			}
		        	  		}
		        	  		else if (pd.radioButton3.isSelected()) { // nur IDs der sichtbaren Tabelle
		        	  			if (tn.equals(selectedTN)) {
		        	  				go4Table(tn, result, ids.get(0)[0], ids.get(0)[1], myT, showOnlyDataFromCurrentUser);
		        	  			}
		        	  		}
		        	  		else if (pd.radioButton4.isSelected()) { // nur selektierte Zeile der sichtbaren Tabelle
			        			int selID = myDB.getSelectedID();
		        	  			if (tn.equals(selectedTN)) {
		        	  				go4Table(tn, result, selID, selID, myT, showOnlyDataFromCurrentUser);
		        	  			}
		        	  		}
		        	  		/*
		        	  		if (analysedIDs != null && tn.equals("Versuchsbedingungen")) {// erstmal nur für Messwerte bzw. Versuchsbedingungen
    	  						go4Table("Messwerte", result, -1, -1, DBKernel.myList.getTable("Messwerte"),
    	  								null, showOnlyDataFromCurrentUser); // analysedIDs oder WHERE Versuchsbedingungen=5 oder so ähnlich vielleicht???
		        	  		}
		        	  		*/
		        	  		progress.setValue(++lfd);		
		        		}	        		
		        		
		    			if (progress != null) {
		    				progress.setVisible(false);
		    			}
		    			String log = "Alles tutti!";
		        		if (result.size() > 0) {
		        			Collections.sort(result);
		        			log = vectorToString(result, "\n");
		        		}
		        		InfoBox ib = new InfoBox(log, true, new Dimension(800, 500), null, false);
		        		ib.setVisible(true);    				  										        			
				    }
				    catch (Exception e) {MyLogger.handleException(e);}
		      }
		    };
		    
		    Thread thread = new Thread(runnable);
		    thread.start();
	  	}
	  	else {
	  		doSpecialThings();
	  	}
	}
	private void go4Table(final String tn, final Vector<String> result, final int id1, final int id2, final MyTable myT, final boolean showOnlyDataFromCurrentUser) {
		if (!tn.equals("Users")) {
			if (id1 < 0 && id2 < 0 &&
					!tn.equals("ChangeLog") && !tn.equals("DateiSpeicher") && !tn.equals("Users") &&
					!tn.equals("Matrices") && !tn.equals("Methoden") && !tn.equals("Tierkrankheiten") &&
					!tn.equals("Aufbereitungsverfahren_Kits") && !tn.equals("Nachweisverfahren_Kits") &&
					!tn.equals("Methoden_Normen") && !tn.equals("DoubleKennzahlen") &&
					!tn.equals("Messwerte_Sonstiges") && !tn.equals("Versuchsbedingungen_Sonstiges")) {
				String sql = PlausibilityChecker.getTableRepeats(myT);
				if (sql != null) {
					ResultSet rs = DBKernel.getResultSet(sql, false);
					try {
						if (rs != null && rs.first()) {
							do {
								String vals = "";
								for (int i=2;i<=rs.getMetaData().getColumnCount();i++) {
									vals += ", " + rs.getString(i);
								}
								if (vals.length() > 0) {
									vals = vals.substring(1);
									result.add(tn + " hat " + rs.getString(1) + " Datensätze mit denselben Werten: (" + vals + ")"); // Duplikate
								}
							} while (rs.next());
						}
					}
					catch (Exception e) {MyLogger.handleException(e);}
				}
			}
			LinkedHashMap<String, Vector<String[]>> sqlsAll = new LinkedHashMap<String, Vector<String[]>>();
			sqlsAll.put(null, PlausibilityChecker.getPlausibilityRow(null, myT, 0, "ID"));
			if (myT.getTablename().equals("Versuchsbedingungen")) {
				sqlsAll.put("Messwerte", PlausibilityChecker.getPlausibilityRow(null, DBKernel.myList.getTable("Messwerte"), 0, "Versuchsbedingungen"));
			}
			LinkedHashMap<Integer, Vector<String>> v = showOnlyDataFromCurrentUser ? DBKernel.getUsersFromChangeLog(tn, DBKernel.getUsername()) : null;   
			for (Map.Entry<String, Vector<String[]>> entry : sqlsAll.entrySet()) {
				String tblname = entry.getKey();
				Vector<String[]> sqls2 = entry.getValue();
				if (sqls2 != null) {
					for (int i=0;i<sqls2.size();i++) {
						String[] res = sqls2.get(i);
						ResultSet rs = DBKernel.getResultSet(res[0], false);
						try {
							if (rs != null && rs.first()) {
								do {
									Integer id = rs.getInt(1);
									String tn2 = tn;
									if (tn2.equals("Literatur") && id <= 231 || tn2.equals("Methoden") && id <= 1481 ||
											tn2.equals("Kontakte") && id <= 119 || tn2.equals("Messwerte") && id <= 18) {
										
									}
									else {
										if ((!showOnlyDataFromCurrentUser || v != null && v.containsKey(id)) &&
												(id1 < 0 && id2 < 0 || id >= id1 && id <= id2)) {
											String toAdd = tn2 + " (ID=" + getFormattedID(id) + "): ";
											if (tblname != null) {
												toAdd += tblname + " (ID=" + getFormattedID(rs.getInt(2)) + "): ";
											}
											toAdd += res[1];
		    								result.add(toAdd);
										}
									}
								} while (rs.next());
							}
						}
						catch (Exception e) {MyLogger.handleException(e);}
					}
				}
			}			
		}
	}
  	private String vectorToString(final Vector<String> vector, final String delimiter) {
	    StringBuilder vcTostr = new StringBuilder();
	    if (vector.size() > 0) {
	        vcTostr.append(vector.get(0));
	        for (int i=1; i<vector.size(); i++) {
	            vcTostr.append(delimiter);
	            vcTostr.append(vector.get(i));
	        }
	    }
	    return vcTostr.toString();
	}  
  	private String getFormattedID(final Object... id) {
  		if (id == null) {
			return null;
		}
  		return String.format("%5s", id);
  	}
  	private Vector<int[]> parseIDs(final String ids) { // 23-28
  		try  {
  			int i1 = Integer.parseInt(ids.substring(0, ids.indexOf("-")).trim());
  			int i2 = Integer.parseInt(ids.substring(ids.indexOf("-") + 1).trim());
  			if (i2 < i1) {
  				int temp = i2;
  				i2 = i1;
  				i1 = temp;
  			}
  			int[] res = new int[]{i1, i2};
  			Vector<int []> result = new Vector<int[]>();
  			result.add(res);
  	  		return result;  			
  		}
  		catch (Exception e) {return null;}
  	}
  	private void checkSonstigesInProzessketten() {
		ResultSet rs = DBKernel.getResultSet("SELECT " + DBKernel.delimitL("ID") + "," + DBKernel.delimitL("Prozess_ID") + "," + DBKernel.delimitL("ProzessElement") +
				" FROM " + DBKernel.delimitL("ProzessElemente") + " WHERE LCASE (" + DBKernel.delimitL("ProzessElement") + ") LIKE  '%onstige%'", false);
		try {
		    if (rs != null && rs.first()) {
		    	do {
		    		System.out.println(rs.getString("ID") + "\t" + rs.getString("Prozess_ID") + "\t" + rs.getString("ProzessElement"));
		    		ResultSet rs2 = DBKernel.getResultSet("SELECT " + DBKernel.delimitL("ID") + " FROM " + DBKernel.delimitL("Prozessdaten") +
		    				" WHERE " + DBKernel.delimitL("Prozess_CARVER") + " = " + rs.getInt("Prozess_ID"), false);
		    		try {
		    		    if (rs2 != null && rs2.first()) {
		    		    	do {
		    		    		System.out.println("\t" + rs2.getString("ID"));
		    		    	} while (rs2.next());
		    		    }
		    		}
		    	    catch(Exception e2) {MyLogger.handleException(e2);}	
		    	} while (rs.next());
		    }
		}
	    catch(Exception e) {MyLogger.handleException(e);}	
  		
  	}
  	
  	private void downloadCombase() {
  		String folder = "Q:/BfR/Desktop/combaseFetch/websites/";
  		String baseURL = "http://browser.combase.cc";
  		File dir = new File(folder);

  		int sumEntriesInCB = 0;
  		String[] children = dir.list();
	    for (int i=0; i<children.length; i++) {
	        // Get filename of file or directory
	        String filename = children[i];
	        File input = new File(folder + filename);
	        try {
				Document doc = Jsoup.parse(input, "UTF-8");
				Element table = doc.select("table").first();
				if (table != null) {
					Iterator<Element> tr = table.select("tr").iterator();
				    while (tr.hasNext()) {
						Iterator<Element> td = tr.next().select("td").iterator();
						if (td.hasNext()) {
					    	String source_id = td.next().text();
					    	String title_blabla = td.next().text();
					    	int numRecords = Integer.parseInt(td.next().text());
					    	sumEntriesInCB += numRecords;
					    	Element e = td.next();
					    	String Summary_Details = e.text();
					    	// Summary
					    	Element link = e.select("a").first();
					    	String url = link.attr("href");
					    	String summaryUrl = url.substring(url.lastIndexOf("/") + 1);
					    	System.out.println(baseURL + "/" + summaryUrl);
						    getCSVFile(folder, baseURL, summaryUrl, numRecords);
					    	// Details
					    	link = e.select("a").last();
					    	url = link.attr("href");
					    	System.out.println(baseURL + "/" + url.substring(url.lastIndexOf("/") + 1));
					    	Integer id = DBKernel.getID("Literatur", "Kommentar", source_id);
					    	if (id == null) {
								System.err.println(source_id + " nicht in DB");
							} else {
					    		int numInDB = DBKernel.getRowCount("Versuchsbedingungen", " WHERE " + DBKernel.delimitL("Referenz") + " = " + id);
					    		if (numRecords != numInDB) {
									System.err.println(source_id + "(" + id + "): " + numRecords + " in Combase vs. " + numInDB + " in DB...");
								}
					    	}
						}
					}
				}
	        }
	        catch (IOException e) {
				e.printStackTrace();
			}	        
	    }
	    System.out.println("sumEntriesInCB: " + sumEntriesInCB);  		
  	}
  	private void getCSVFile(final String folder2Save, final String baseURL, final String urlString, final int numRecords) {
		try {
			File csvFile = new File(folder2Save + urlString.substring(urlString.indexOf("?") + 1) + "&numRecords=" + numRecords + ".csv");
			if (csvFile.exists()) {
				System.out.println("exists");
			}
			else {
				System.err.println("exists not: " + csvFile.getName());
				
  	
  	
				/*
Sub doit()
Dim Count As Integer
Count = 0
    For Counter = 1 To 65000
        Set curCell = Worksheets(1).Cells(Counter, 1)
        If curCell.Value = "RecordID" Then Count = Count + 1
    Next Counter
    Debug.Print Count
End Sub

				Sausage fehlt und:
				
http://browser.combase.cc/ResultSummary.aspx?SourceID=O%27Mahony_01&Foodtype=Other%2fmix
				*/
				
				URL myUrl = new URL(baseURL + "/" + urlString);
				URLConnection urlConn = myUrl.openConnection();
				urlConn.setRequestProperty("Cookie", "DisclaimerSigned=Signed");
				urlConn.connect();
			    BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			    String html = "";
				String inputLine;
			    while ((inputLine = in.readLine()) != null)
				 {
					html += inputLine;//System.out.println(inputLine);
				}
			    in.close();
			    Document doc = Jsoup.parse(html, "UTF-8");
			    Element el = doc.select("#ContentPlaceHolder1_CombaseResultSummary1_lnkDownloadCSV").first();
			    System.out.println(el.attr("href"));
				myUrl = new URL(baseURL + "/" + el.attr("href"));
				urlConn = myUrl.openConnection();
				urlConn.setRequestProperty("Cookie", "DisclaimerSigned=Signed");
				urlConn.connect();
				saveUrl(folder2Save + urlString.substring(urlString.indexOf("?") + 1) + ".csv", urlConn.getInputStream());
				
  	
  	
  	
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
  	}
  	private void saveUrl(final String filename, final InputStream urlStream) throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
                in = new BufferedInputStream(urlStream);
                fout = new FileOutputStream(filename);

                byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1)  {
                        fout.write(data, 0, count);
                }
        }
        finally {
                if (in != null) {
					in.close();
				}
                if (fout != null) {
					fout.close();
				}
        }
    }
  	private void check4EmptyPKsMWs(final String tablename, final String nullFeldname) {
		ResultSet rs = DBKernel.getResultSet("SELECT * FROM " + DBKernel.delimitL(tablename) + " WHERE " + DBKernel.delimitL(nullFeldname) + " IS NULL", false);
		try {
			if (rs != null && rs.first()) {
				do {
					String tt = "";
					LinkedHashMap<Integer, Vector<String>> v = DBKernel.getUsersFromChangeLog(tablename, rs.getInt("ID"));
				  	for (Map.Entry<Integer, Vector<String>> entry : v.entrySet()) {
						for (String entr : entry.getValue()) {
							tt += entry.getKey() + "\t" + entr + "\n"; 
						}
				  	}			
					System.err.println(tablename + "\t" + rs.getInt("ID") + "\n" + tt);
				} while (rs.next());
			}
		}
		catch (Exception e) {MyLogger.handleException(e);}
		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL(tablename) + " WHERE " + DBKernel.delimitL(nullFeldname) + " IS NULL", false);
  	}
  	private void checkAllEntriesIfOthersAlreadyEditedUpdates() {
		LinkedHashMap<String, MyTable> myTables = DBKernel.myList.getAllTables();

		for(String key : myTables.keySet()) {
			MyTable myT = myTables.get(key);
			String tn = myT.getTablename();
			if (!tn.equals("ChangeLog") && !tn.equals("DateiSpeicher") && !tn.equals("Infotabelle")) {
				System.err.println(tn);

				  String sql = "SELECT ARRAY_AGG(" + DBKernel.delimitL("Alteintrag") + " ORDER BY " + DBKernel.delimitL("Zeitstempel") + " ASC)," +
				    "ARRAY_AGG(" + DBKernel.delimitL("Zeitstempel") + " ORDER BY " + DBKernel.delimitL("Zeitstempel") + " ASC)," + 
				    "ARRAY_AGG(" + DBKernel.delimitL("Username") + " ORDER BY " + DBKernel.delimitL("Zeitstempel") + " ASC)," +
				    "MAX(" + DBKernel.delimitL("TabellenID") + ")" +
				    " FROM " + DBKernel.delimitL("ChangeLog") +
				    	" WHERE " + DBKernel.delimitL("Tabelle") + " = '" + tn + "'" +
				    	" GROUP BY " + DBKernel.delimitL("TabellenID");
				    ResultSet rs2 = DBKernel.getResultSet(sql, false);
				try {
				    if (rs2 != null && rs2.first()) {
				    	do {
						    Array a = rs2.getArray(1);
						    if (a != null) {
						    	Array un = rs2.getArray(3);
						    	if (un != null) {
								    Object[] usernamen = (Object[])un.getArray();				
								    for (int i=1;i<usernamen.length;i++) {
								    	if (!usernamen[i].equals(usernamen[0])) { // verschiedene Usernamen
								    		ResultSet rs3 = DBKernel.getResultSet("SELECT * FROM " + DBKernel.delimitL(tn) +
								    				" WHERE " + DBKernel.delimitL("ID") + "=" + rs2.getInt(4), false);
								    		if (rs3 != null && rs3.first()) {
											    Object[] ts = (Object[])rs2.getArray(2).getArray();				
											    Object[] alteintraege = (Object[])a.getArray();	
									    		for (int j=0;j<usernamen.length - 1;j++) {
									    			System.err.print(usernamen[j] + "\t" + ts[j]);
												    Object[] arr = (Object[]) alteintraege[j+1];
												    if (arr == null) {
												    	System.err.print("\t" + rs2.getInt(4) + "\tnull"); // das hier sollte nie passieren
												    }
												    else {
													    for (int k=0;k<arr.length;k++) {
												    		System.err.print("\t" + arr[k]);								    	
													    }											    	
												    }
												    System.err.println();
									    		}
									    		// Letzter Datensatz - in der Datenbank noch drin
									    		System.err.print(usernamen[usernamen.length - 1] + "\t" + ts[usernamen.length - 1]);
								    			for (int k=1;k<=rs3.getMetaData().getColumnCount();k++) {
								    				System.err.print("\t" + rs3.getObject(k));	
								    			}
								    			rs3.close();
									    		System.err.println();
									    		break;
								    		}
								    		else {
								    			// Datensatz bereits wieder gelöscht - System.err.print("\t" + rs2.getInt(4) + "\tnull"); 
								    		}
								    	}
								    }
						    	}
						    }				    		
				    	} while (rs2.next());
				    	rs2.close();
				    }
				}
				catch (Exception e) {e.printStackTrace();}
			}
		}
		System.out.println("Fertig");
  	}
  	private void doSpecialThings() {
  		//checkAllEntriesIfOthersAlreadyEditedUpdates();
  		
  		/*
		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("Messwerte") +
				" WHERE " + DBKernel.delimitL("Versuchsbedingungen") + " <= 2 AND " + DBKernel.delimitL("ID") + " > 18", false);
  		DBKernel.dontLog = true;
  		check4EmptyPKsMWs("Messwerte", "Versuchsbedingungen");
  		//check4EmptyPKsMWs("Messwerte_Sonstiges", "Messwerte");
  		//check4EmptyPKsMWs("Versuchsbedingungen_Sonstiges", "Versuchsbedingungen");
  		check4EmptyPKsMWs("Prozessdaten", "Workflow");
  		//check4EmptyPKsMWs("ComBaseImport", "Referenz");
  		DBKernel.dontLog = false;
  		*/
  		/*
  		//DBKernel.dontLog = true;
  		DBKernel.importing = true;new MySQLImporter(10000, true, false, true).doImport("", null, true);DBKernel.importing = false;
  		//DBKernel.dontLog = false;
  		*/
  		
  		//checkSonstigesInProzessketten();
  		//downloadCombase();
  		/*
  		DBKernel.dontLog = true;
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("Codes_Methoden"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("Codes_Methodiken"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("Methodiken"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("Methoden"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ICD10_Kodes"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ICD10_MortL4"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ICD10_MortL3"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ICD10_MortL3Grp"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ICD10_MortL2"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ICD10_MortL1"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ICD10_MortL1Grp"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ICD10_MorbL"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ICD10_Gruppen"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ICD10_Kapitel"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("Codes_Agenzien"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("Codes_Matrices"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("Users"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("Kontakte"), false);
  		DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("Labore"), false);
  		//DBKernel.sendRequest("CREATE USER " + DBKernel.delimitL("SA") + " PASSWORD '' ADMIN", false);
  		//DBKernel.sendRequest("DELETE FROM " + DBKernel.delimitL("ComBaseImport"), false);
		//new GeneralXLSImporter().doImport("/org/hsh/bfr/db/res/" + "ComBaseImport.xls", null, false);						
  		DBKernel.sendRequest("DROP USER " + DBKernel.delimitL("defad"), false);
  		DBKernel.dontLog = false;
  		*/
  		/*
  		try {
	    	PreparedStatement ps = DBKernel.getDBConnection().prepareStatement("INSERT INTO " + DBKernel.delimitL("Users") +
					" (" + DBKernel.delimitL("Username") + "," + DBKernel.delimitL("Vorname") + "," + DBKernel.delimitL("Name") + "," + DBKernel.delimitL("Zugriffsrecht") + ") VALUES (?,?,?,?)");
	    	ps.setString(1, "SA"); ps.setString(2, "S"); ps.setString(3, "A"); ps.setInt(4, Users.ADMIN); ps.execute();				  			
  		}
  		catch (Exception e1) {e1.printStackTrace();}
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Versuchsbedingungen") +
				" DROP CONSTRAINT " + DBKernel.delimitL("Versuchsbedingungen_fk_Luftfeuchtigkeit_12"), false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Versuchsbedingungen") +
				" DROP COLUMN " + DBKernel.delimitL("Luftfeuchtigkeit"), false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Messwerte") +
				" DROP CONSTRAINT " + DBKernel.delimitL("Messwerte_fk_Luftfeuchtigkeit_11"), false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Messwerte") +
				" DROP COLUMN " + DBKernel.delimitL("Luftfeuchtigkeit"), false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Modellkatalog") + " ALTER COLUMN " + DBKernel.delimitL("Name") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Modellkatalog") + " ALTER COLUMN " + DBKernel.delimitL("Notation") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Modellkatalog") + " ALTER COLUMN " + DBKernel.delimitL("Eingabedatum") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("ModellkatalogParameter") + " ALTER COLUMN " + DBKernel.delimitL("Modell") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("ModellkatalogParameter") + " ALTER COLUMN " + DBKernel.delimitL("Parametername") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("ModellkatalogParameter") + " ALTER COLUMN " + DBKernel.delimitL("Parametertyp") + " SET DEFAULT 1", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("ModellkatalogParameter") + " ALTER COLUMN " + DBKernel.delimitL("ganzzahl") + " SET DEFAULT FALSE", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Modell_Referenz") + " ALTER COLUMN " + DBKernel.delimitL("Modell") + " SET NOT NULL ", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Modell_Referenz") + " ALTER COLUMN " + DBKernel.delimitL("Literatur") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("GeschaetzteModelle") + " ALTER COLUMN " + DBKernel.delimitL("Modell") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("GeschaetzteModelle") + " ALTER COLUMN " + DBKernel.delimitL("manuellEingetragen") + " SET DEFAULT FALSE", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("GeschaetztesModell_Referenz") + " ALTER COLUMN " + DBKernel.delimitL("GeschaetztesModell") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("GeschaetztesModell_Referenz") + " ALTER COLUMN " + DBKernel.delimitL("Literatur") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("GeschaetzteParameter") + " ALTER COLUMN " + DBKernel.delimitL("GeschaetztesModell") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("GeschaetzteParameter") + " ALTER COLUMN " + DBKernel.delimitL("Parameter") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("GeschaetzteParameterCovCor") + " ALTER COLUMN " + DBKernel.delimitL("param1") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("GeschaetzteParameterCovCor") + " ALTER COLUMN " + DBKernel.delimitL("param2") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("GeschaetzteParameterCovCor") + " ALTER COLUMN " + DBKernel.delimitL("GeschaetztesModell") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("GeschaetzteParameterCovCor") + " ALTER COLUMN " + DBKernel.delimitL("cor") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Sekundaermodelle_Primaermodelle") + " ALTER COLUMN " + DBKernel.delimitL("GeschaetztesPrimaermodell") + " SET NOT NULL", false);
		DBKernel.sendRequest("ALTER TABLE " + DBKernel.delimitL("Sekundaermodelle_Primaermodelle") + " ALTER COLUMN " + DBKernel.delimitL("GeschaetztesSekundaermodell") + " SET NOT NULL", false);
  		*/
  	}
}
