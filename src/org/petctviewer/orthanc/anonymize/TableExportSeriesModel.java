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

import org.petctviewer.orthanc.anonymize.datastorage.Serie;
import org.petctviewer.orthanc.anonymize.datastorage.Study_Anonymized;
import org.petctviewer.orthanc.setup.OrthancRestApis;

public class TableExportSeriesModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Serie description*", "Modality", "Instances", "Secondary capture", "ID", "Serie Number", "serieObject"};
	private Class<?>[] classEntetes = {String.class, String.class, Integer.class, Boolean.class, String.class, String.class, Serie.class};
	private OrthancRestApis connexionHttp;
	//Store the current StudyOrthanc ID the Series came from (for refresh)
	private Study_Anonymized currentStudy;

	public TableExportSeriesModel(OrthancRestApis connexionHttp){
		super(0,7);
		this.connexionHttp=connexionHttp;
	
	}

	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}
	
	@Override
	public Class<?> getColumnClass(int column){
		return classEntetes[column];
	}

	public boolean isCellEditable(int row, int col){
		if(col == 0){
			return true; 
		}
		return false;
	}

	//SK A REFLECHIR COMMENT RE IMPEMENTER
	/*public void setValueAt(Object value, int row, int col) {
		String uid = this.getValueAt(row, 4).toString();
		String oldDesc ;
		if (this.getValueAt(row, 0)==null) oldDesc=""; else oldDesc=this.getValueAt(row, 0).toString();
		
		if(!oldDesc.equals(value.toString()) && col == 0){
			series.get(row).setSerieDescription(value.toString());
			fireTableCellUpdated(row, col);
		}
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
			boolean success=true;
			@Override
			protected Void doInBackground() {
				url="/series/" + uid + "/modify";
				stateExport.setText("<html>Modifying a serie description <font color='red'> <br>(Do not use the toolbox while the current operation is not done)</font></html>");
				frame.pack();
				StringBuilder sb =connexionHttp.makePostConnectionAndStringBuilder(url, ("{\"Replace\":{\"SeriesDescription\":\"" + value.toString() + "\"}}"));
				if(sb!=null) {
					connexionHttp.makeDeleteConnection("/series/" + uid );
				}else {
					success=false;
				}
				return null;
			}
			@Override
			protected void done(){
				if(success) {
					clear();
					addSerie(studyID);
					stateExport.setText("<html><font color='green'>The description has been changed.</font></html>");
					frame.pack();	
				}else {
					stateExport.setText("<html><font color='red'>Error during Dicom Edition</font></html>");
				}
				
			}
		};
		if(!oldDesc.equals(value.toString()) && col == 0){
			worker.execute();
		}
	}*/

	public void removeAllSecondaryCaptures() {
		
		for(int i=0; i<this.getRowCount(); i++){
			Serie serie= (Serie) this.getValueAt(i, 7);
			if(serie.isSecondaryCapture()){
				String url="/series/" + serie.getId();
				boolean success=connexionHttp.makeDeleteConnection(url);
				if(!success) {
					System.out.println("Error Erasing "+serie.getSerieDescription());
				}
				
			}
		}
		//Refresh the Table by quering again Orthanc
		this.addSerie(currentStudy);

	}
	
	public void removeSerie(int selectedRow) {
		Serie serie= (Serie) this.getValueAt(selectedRow,7);
		String url="/series/" + serie.getId();
		boolean success=connexionHttp.makeDeleteConnection(url);
		if(!success) {
			System.out.println("Error Erasing "+serie.getSerieDescription());
		}
		
	}

	public void addSerie(Study_Anonymized studyAnonymized) {
		this.currentStudy=studyAnonymized;
		clear();
		for(Serie serie:studyAnonymized.getAnonymizedStudy().getSeries()) {
			addRow(new String[] {serie.getSerieDescription(), serie.getModality(), String.valueOf(serie.getNbInstances()), String.valueOf(serie.isSecondaryCapture()), serie.getFistInstanceId(), serie.getSeriesNumber()});
		}
	}

	/*
	 * This method clears the series list
	 */
	public void clear(){
		this.setRowCount(0);
		
	}
	
	public String getStudyOriginID() {
		return currentStudy.getOriginalStudy().getOrthancId();
	}


}
