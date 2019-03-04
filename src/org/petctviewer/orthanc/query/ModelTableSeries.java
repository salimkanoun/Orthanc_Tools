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

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class ModelTableSeries extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Series Desc", "Modality", "Serie num"};
	private Class<?>[] columnClasses = new Class<?>[] {String.class, String.class, String.class};
	
	private String idURL;
	private ArrayList<SeriesDetails> series ;
	private ArrayList<String> listIndexes = null;
	private Rest rest;
	

	public ModelTableSeries(Rest rest){
		super(0,3);
		this.rest=rest;
	}

	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}
	
	@Override
	public Class<?> getColumnClass(int column){
		return columnClasses[column];
	}

	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addDetails(String patientName, String patientID, String studyDate, 
			String studyDescription, String accessionNumber, String studyInstanceUID, String aet) throws Exception{
		this.listIndexes = new ArrayList<String>();
		this.idURL = rest.getSeriesDescriptionID(studyInstanceUID, aet);
		String[][] studyDescriptionAndModality = rest.getSeriesDescriptionValues(idURL);
		series = new ArrayList<SeriesDetails>();
		SeriesDetails d;
		if(studyDescriptionAndModality[0].length != 0){
			if(studyInstanceUID == null){
				throw new Exception("This study doesn't have an instance UID !");
			}
			for(int i = 0; i < studyDescriptionAndModality[0].length; i++){
				d = new SeriesDetails(studyDescriptionAndModality[0][i], studyDescriptionAndModality[1][i], studyInstanceUID, studyDescriptionAndModality[2][i]);
				series.add(d);
				// Whenever we add details, we store the query ID, in order to use it for the retrieve queries
				this.listIndexes.add(this.idURL);
				//fireTableRowsInserted(details.size() - 1, details.size() - 1);
				
			}
			updateTable();
		}

	}
	
	private void updateTable() {
		//Empty Table
		setRowCount(0);
		//Fill with Series
		for(SeriesDetails serie : series) {
			this.addRow(new String[] {serie.getSeriesDescription(), serie.getModality(), serie.getSeriesNumber()});
		}
	}

	
	/*
	 * This method clears the details list
	 */
	public void clear(){
		setRowCount(0);
	}

	public String getQueryID(int index){
		return this.listIndexes.get(index);
	}

	/*
	 * This method retrieves the needed result
	 */
	public void retrieve(String queryID, int answer, String retrieveAET) {
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
