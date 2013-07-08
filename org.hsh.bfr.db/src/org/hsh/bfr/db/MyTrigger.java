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
package org.hsh.bfr.db;

import org.hsqldb.Trigger;
/**
 * @author Armin
 *
 */

public class MyTrigger implements Trigger {
		
  @Override
public void fire(final int triggerType, final String triggerName, final String tableName, final Object rowBefore[], final Object rowAfter[]) {
        try {
        	if (triggerType == Trigger.INSERT_BEFORE_ROW || triggerType == Trigger.UPDATE_BEFORE_ROW || triggerType == Trigger.DELETE_BEFORE_ROW) {
        		if (tableName.equals("Users")) {
        			if (triggerType == Trigger.INSERT_BEFORE_ROW) {
	          			//System.out.println(rowBefore + "\t" + rowAfter[4]);
	          			if (rowAfter[4] == null) {
							rowAfter[4] = new Integer(Users.READ_ONLY);
						}        				
        			}
        			else if (triggerType == Trigger.UPDATE_BEFORE_ROW) {
        				// Achtung: Aktiver User darf die Kennung nicht �ndern. Daher: �nderung nicht zulassen!!!
	        	  		if (rowBefore != null && rowBefore[1] != null) {
	        	  			String un = MainKernel.getUsername(); 
	        	  			if (un.length() == 0) {
								MyLogger.handleMessage("ServerKernel.getUsername()...WWEWEW: " + rowBefore[1].toString());
							}
		    	  			if (un.equals(rowBefore[1].toString())) { // klappt das denn auch mit ServerKernel???
		        	  				if (rowAfter[1] == null || !rowAfter[1].toString().equals(rowBefore[1].toString())) {
		        	  					rowAfter[1] = rowBefore[1].toString();
		        	  				}
		        	  			}
		        	  	}
	        	  		/*
        				// Achtung: es sollte immer mindestens ein Admin vorhanden sein. Daher: �nderung nicht zulassen!!!
        				if (rowBefore != null && rowBefore[4] != null) {
        					int oldAccRight = ((Integer) rowBefore[4]).intValue();
        					if (oldAccRight == Users.ADMIN) {
        						int newAccRight = (rowAfter == null || rowAfter[4] == null) ? -1 : ((Integer) rowAfter[4]).intValue();
        						if (newAccRight != oldAccRight) {
	          						if (DBKernel.countUsers(true) == 1) {
			                  			//System.out.println(DBKernel.countUsers(true));     
			                  			rowAfter[4] = new Integer(Users.ADMIN);  
	          						}        							
        						}
        					}
        				}
        				*/
        				// Achtung: Usernamen die leer sind werden nicht zugelassen
        				if (rowAfter != null && (rowAfter[1] == null || rowAfter[1].toString().length() == 0)) {
        					if (rowBefore[1] == null) {
								rowAfter[1] = rowBefore[1];
							} else {
								rowAfter[1] = rowBefore[1].toString();
							}
        				}
        				// Userrechte sollten auch nicht leer sein 
        				if (rowAfter != null && rowAfter[4] == null) {
        					int oldAccRight = (rowBefore == null || rowBefore[4] == null) ? Users.READ_ONLY : ((Integer) rowBefore[4]).intValue();
        					rowAfter[4] = new Integer(oldAccRight);
        				}
        			}
        		}
        		else if (tableName.equals("ProzessWorkflow") && triggerType == Trigger.UPDATE_BEFORE_ROW) { // XML sollte nicht gel�scht werden d�rfen!
        			if (rowAfter != null && (rowAfter[9] == null)) { // XML
    					rowAfter[9] = rowBefore[9];
        			}
			    }
        	}        		
        	else if (triggerType == Trigger.INSERT_AFTER_ROW || triggerType == Trigger.UPDATE_AFTER_ROW || triggerType == Trigger.DELETE_AFTER_ROW) {
          	
	          	if (tableName.equals("Users")) {
	          		if (triggerType == Trigger.DELETE_AFTER_ROW) {
	          			deleteUser(rowBefore);          			
	          		}
	          		else if (triggerType == Trigger.INSERT_AFTER_ROW) {
	          			changeUser(rowBefore, rowAfter);         
	          		}
	          		else {
	            		changeUser(rowBefore, rowAfter);          			
	          		}
	          	}

	          	if (!MainKernel.dontLog) {
		          	if (MainKernel.isServer() || DBKernel.isKNIME || DBKernel.getLocalConn(false) == null) {
		          		//DBKernel.importing = true; // nur, damit nicht unn�tig sone kacke wie undo gemacht wird in insertIntoChangeLog...
		          		if (!MainKernel.insertIntoChangeLog(tableName, rowBefore, rowAfter)) {
		          			MyLogger.handleMessage("Something went wrong in MyTrigger...." + tableName + "\t" + rowBefore + "\t" + rowAfter);
		          		}
		          		//System.out.println("isserver");
		          	}
		          	else {
		          		//System.out.println("isnotserver");
		          		if (!DBKernel.insertIntoChangeLog(tableName, rowBefore, rowAfter)) {
		          			MyLogger.handleMessage("Something went wrong in MyTrigger...." + tableName + "\t" + rowBefore + "\t" + rowAfter);
		          		}
		          	}	          		
	          	}
	          	
        	}
        }
        catch (Exception e) {
        	MyLogger.handleException(e);
        }
  }

  private void changeUser(final Object oldUser[], final Object newUser[]) {
		if (newUser != null && newUser[1] != null && newUser[1].toString().length() > 0) {
			String newUsername = newUser[1].toString();
			int newAccRight = Users.READ_ONLY;
			if (newUser[4] != null && newUser[4] instanceof Integer) {
				newAccRight = ((Integer) newUser[4]).intValue();
			}
		  	// 1. komplette Neudefinition
		  	if (oldUser == null || oldUser[1] == null) {
		  		createUser(newUsername, newAccRight);
		  	}
		  	else {
		  		int oldAccRight = (oldUser[4] == null) ? Users.READ_ONLY : ((Integer) oldUser[4]).intValue();
		  		String oldUsername = oldUser[1].toString();
			  	// 2. Username hat sich ge�ndert
		  		if (oldUsername.length() > 0 && !oldUsername.equals(newUsername)) {
			  		if (createUser(newUsername, newAccRight)) {
						deleteUser(oldUser);
					}	  			
		  		}
			  	// 3. Access Rights haben sich ge�ndert
			  	else if (oldAccRight != newAccRight) {
			  		if (removeAccRight(newUsername, oldAccRight)) {
						createAccRight(newUsername, newAccRight);
					}
			  	}	  	
		  	}
		}
  }
  private boolean removeAccRight(final String username, final int oldAccRight) {
  	boolean success = true;
  	//System.out.println("removeAccRight\t" + username + "\t" + oldAccRight + "\t");
  	if (oldAccRight == Users.ADMIN) {
		success = MainKernel.sendRequest("REVOKE " + MainKernel.delimitL("DBA") + " FROM " + MainKernel.delimitL(username) + " RESTRICT", false);
	} else if (oldAccRight == Users.SUPER_WRITE_ACCESS) {
		success = MainKernel.sendRequest("REVOKE " + MainKernel.delimitL("SUPER_WRITE_ACCESS") + " FROM " + MainKernel.delimitL(username) + " RESTRICT", false);
	} else if (oldAccRight == Users.WRITE_ACCESS) {
		success = MainKernel.sendRequest("REVOKE " + MainKernel.delimitL("WRITE_ACCESS") + " FROM " + MainKernel.delimitL(username) + " RESTRICT", false);
	}			
  	return success;
  }
  private boolean createAccRight(final String username, final int newAccRight) {
  	boolean success = true;
  	//System.out.println("createAccRight\t" + username + "\t" + newAccRight + "\t");
  	if (newAccRight == Users.ADMIN) {
		success = MainKernel.sendRequest("GRANT " + MainKernel.delimitL("DBA") + " TO " + MainKernel.delimitL(username), false);
	} else if (newAccRight == Users.SUPER_WRITE_ACCESS) {
		success = MainKernel.sendRequest("GRANT " + MainKernel.delimitL("SUPER_WRITE_ACCESS") + " TO " + MainKernel.delimitL(username), false);
	} else if (newAccRight == Users.WRITE_ACCESS) {
		success = MainKernel.sendRequest("GRANT " + MainKernel.delimitL("WRITE_ACCESS") + " TO " + MainKernel.delimitL(username), false);
	}			
  	return success;
  }
  private boolean createUser(final String username, final int accRight) {
		boolean success = MainKernel.sendRequest("CREATE USER " + MainKernel.delimitL(username) + " PASSWORD ''", true); // + (accRight == Users.ADMIN ? " ADMIN" : "") " + MD5.encode("", "UTF-8") + "
  	//System.out.println("createUser\t" + username + "\t" + accRight + "\t");
		if (success) {
			success = createAccRight(username, accRight);
		}
  	return success;
  }
  private boolean deleteUser(final Object oldUser[]) {
		if (oldUser != null && oldUser[1] != null && oldUser[1].toString().length() > 0) {
			String username = oldUser[1].toString();
	  	//System.out.println("deleteUser\t" + username);
  		return MainKernel.sendRequest("DROP USER " + MainKernel.delimitL(username), false);
  	}
		return false;
  }
}