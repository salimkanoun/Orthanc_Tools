/**
Copyright (C) 2017 VONGSALAT Anousone

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public v.3 License as published by
the Free Software Foundation;

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */


package org.petctviewer.orthanc.query;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class TableDataDetails extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Series name", "Modality", "Serie n�"};
	private String idURL;
	private ArrayList<Details> details = new ArrayList<Details>();
	private ArrayList<String> listIndexes = null;
	private Rest rest;
	

	public TableDataDetails(Rest rest){
		super();
		this.rest=rest;
	}

	public int getRowCount(){
		return details.size();
	}

	public int getColumnCount(){
		return entetes.length;
	}

	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		switch(columnIndex){
		case 0:
			return details.get(rowIndex).getSeriesDescription();
		case 1:
			return details.get(rowIndex).getModality();
		case 2:
			return details.get(rowIndex).getSeriesNumber();
		default:
			return null; // Should never happen
		}
	}

	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addDetails(String patientName, String patientID, String studyDate, 
			String studyDescription, String accessionNumber, String studyInstanceUID, String aet) throws Exception{
		this.listIndexes = new ArrayList<String>();
		this.idURL = rest.getSeriesDescriptionID(studyInstanceUID, aet);
		String[][] studyDescriptionAndModality = rest.getSeriesDescriptionValues(idURL);
		
		Details d;
		if(studyDescriptionAndModality[0].length != 0){
			if(studyInstanceUID == null){
				throw new Exception("This study doesn't have an instance UID !");
			}
			for(int i = 0; i < studyDescriptionAndModality[0].length; i++){
				d = new Details(studyDescriptionAndModality[0][i], studyDescriptionAndModality[1][i], studyInstanceUID, studyDescriptionAndModality[2][i]);
				if(!details.contains(d)){
					details.add(d);
					// Whenever we add details, we store the query ID, in order to use it for the retrieve queries
					this.listIndexes.add(this.idURL);
					fireTableRowsInserted(details.size() - 1, details.size() - 1);
				}
			}
		}

	}

	public void removeDetails(int rowIndex){
		this.details.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	/*
	 * This method gets every available AETs and put them in an Object[]
	 */
	public String[] getAETs(){
		String[] listeAETs = null;
		listeAETs = rest.getAets();
		return listeAETs;
	}

	/*
	 * This method clears the details list
	 */
	public void clear(){
		if(this.getRowCount() !=0){
			for(int i = this.getRowCount(); i > 0; i--){
				this.removeDetails(i-1);
			}
		}
	}

	public String getQueryID(int index){
		return this.listIndexes.get(index);
	}

	/*
	 * This method retrieves the needed result
	 */
	public void retrieve(String queryID, int answer, String retrieveAET) throws IOException{
		rest.retrieve(queryID, answer, retrieveAET);
	}


	/*
	 * This method clears the queries IDs list
	 */
	public void clearQueriesIDs(){
		if(this.listIndexes != null){
			this.listIndexes.removeAll(listIndexes);
		}
	}
}
