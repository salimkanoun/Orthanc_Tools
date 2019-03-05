/**
Copyright (C) 2017 VONGSALAT Anousone & KANOUN Salim

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

package org.petctviewer.orthanc.anonymize;

import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.setup.OrthancRestApis;

public class TableSeriesModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Serie description", "Modality", "Instances", "Secondary capture", "ID", "Serie Num", "SeriesObj"};
	private Class<?>[] classEntetes = {String.class, String.class, Integer.class, Boolean.class, String.class, String.class, Serie.class};
	private OrthancRestApis connexionHttp;

	public TableSeriesModel(OrthancRestApis connexionHttp){
		super(0,7);
		//Recupere les settings
		this.connexionHttp=connexionHttp;
	}

	@Override
	public int getColumnCount(){
		return entetes.length;
	}

	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
		
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return classEntetes[columnIndex];
	}
	
	public boolean isCellEditable(int row, int col){
		return false;
	}

	public void removeAllSecondaryCaptures() {
		for(int i=0; i<this.getRowCount(); i++) {
			if ((Boolean) this.getValueAt(i, 3)) {
				connexionHttp.makeDeleteConnection("/series/"+this.getValueAt(i, 4));
				this.removeRow(i);
			}
		}
	}

	public void addSerie(String studyID) {

		QueryFillStore querySeries = new QueryFillStore(connexionHttp,"series", null, studyID, null, null);
		
		Study2 study =querySeries.getStudyDetails(studyID, true);
		for(Serie serie:study.getSeries()) {
			this.addRow(new Object[] {serie.getSerieDescription(), 
				serie.getModality(), 
				serie.getNbInstances(), 
				serie.isSecondaryCapture(), 
				serie.getId(), 
				serie.getSeriesNumber(), serie});
			
		}

	}

	/*
	 * This method clears the series list
	 */
	public void clear(){
		this.setRowCount(0);
	}
	
}
