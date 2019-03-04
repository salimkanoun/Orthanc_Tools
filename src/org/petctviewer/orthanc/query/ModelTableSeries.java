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

	private String[] entetes = {"Series Desc", "Modality", "Serie num", "serieObject"};
	private Class<?>[] columnClasses = new Class<?>[] {String.class, String.class, String.class, SeriesDetails.class};
	
	private SeriesDetails[] series ;
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
	public void addDetails(PatientsDetails patient) {
		
		series =rest.getSeriesAnswers(patient.getStudyInstanceUID(), patient.getSourceAet());
		updateTable();

	}
	
	private void updateTable() {
		//Empty Table
		setRowCount(0);
		//Fill with Series
		for(SeriesDetails serie : series) {
			this.addRow(new Object[] {serie.getSeriesDescription(), serie.getModality(), serie.getSeriesNumber(), serie});
		}
	}

	
	/*
	 * This method clears the details list
	 */
	public void clear(){
		setRowCount(0);
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
