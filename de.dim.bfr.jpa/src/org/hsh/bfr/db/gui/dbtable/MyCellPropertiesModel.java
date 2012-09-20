/**
 * 
 */
package org.hsh.bfr.db.gui.dbtable;

import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.hsh.bfr.db.MyLogger;

import quick.dbtable.CellPropertiesModel;
import quick.dbtable.DBTableCellListener;

/**
 * @author Armin
 *
 */
public class MyCellPropertiesModel extends CellPropertiesModel {

	private LinkedHashMap<String, int[]> myFounds = new LinkedHashMap<String, int[]>();	
	private LinkedHashMap<Integer, HashSet<Integer>> modifiedCells = new LinkedHashMap<Integer, HashSet<Integer>>();
	private MyDBTable myDBTable;
	//Font ft = new Font();
	
	public MyCellPropertiesModel(MyDBTable myDBTable) {
		this.myDBTable = myDBTable;
	}

  public Font getFont(int row, int col) {
     return super.getFont(row, col);
  }

  public Color getBackground(int row, int col) {
	  if (isFound(row, col)) return Color.YELLOW;
	  if (isEdited(row, col)) return Color.YELLOW;
	  return super.getSelectionBackground(row, col);
  }

  public Color getForeground(int row, int col) {
	  if (isFound(row, col)) return Color.BLACK;
	  if (isEdited(row, col)) return Color.BLACK;
	  return super.getForeground(row, col);
  }

  public Color getSelectionBackground(int row, int col) {
	  if (isFound(row, col)) return Color.ORANGE;
	  if (isEdited(row, col)) return Color.YELLOW;
	  return super.getSelectionBackground(row, col);
  }

  public Color getSelectionForeground(int row, int col) {
	  if (isFound(row, col)) return Color.BLACK;
	  if (isEdited(row, col)) return Color.BLACK;
	  return super.getSelectionForeground(row, col);
  }

  public int getAlignment(int row, int col) {
  	return super.getAlignment(row, col);
  	/*
     if( col== 0 ) return SwingConstants.LEFT;
     else return SwingConstants.RIGHT;
     */
  }

  public int getCellEditable(int row, int col) {
  	return super.getCellEditable(row, col); // (col > 0 ? 1 : 0);//
  }

  public void setFoundVector(LinkedHashMap<String, int[]> myFounds) {
  	this.myFounds = myFounds;
  }
  public LinkedHashMap<Integer, HashSet<Integer>> getModifiedCellsColl() {
	  return modifiedCells;
  }
  
  private boolean isEdited(int row, int col) {
	  boolean result = false;
	  Object o = null;
	  try {
		  o = myDBTable.getValueAt(row, 0);
		  if (o == null) return true;
		  if (o instanceof Integer) {
			  int id = (Integer) o;
			  if (modifiedCells != null && modifiedCells.containsKey(id)) {
					HashSet<Integer> vec = modifiedCells.get(id);
					  //System.out.println(row + "\t" + id + "\t" + col + "\t" + vec.contains(col) + "\t" + vec);
					if (vec.contains(col)) return true;
			  }		 		  
		  }
		  else {
			  MyLogger.handleMessage("nanunana, was los hier???\t" + row + "\t" + col + (o != null ? "\t" + o.toString().length() : "null") + "\t" + o + "\t" + myDBTable.getActualTable().getTablename());
		  }
	  }
	  catch (Exception e) {MyLogger.handleException(e);MyLogger.handleMessage("was los hier???\t" + row + "\t" + col + (o != null ? "\t" + o.toString().length() : "null") + "\t" + o + "\t" + myDBTable.getActualTable().getTablename());}	  
	  return result;
  }
  private boolean isFound(int row, int col) {
	  boolean result = false;
	  if (myFounds != null && myFounds.size() > 0) {
		  if (myFounds.containsKey("." + row + "." + col + ".")) {
	//  		System.out.println(row + "." + column + "\t" + myFounds.keySet().toArray()[0] + "\t" + value);
			  return true;	
		  }
	  }
	  return result;
  }
}
