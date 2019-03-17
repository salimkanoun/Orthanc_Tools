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

import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.anonymize.datastorage.Serie;
import org.petctviewer.orthanc.anonymize.datastorage.Study_Anonymized;
import org.petctviewer.orthanc.setup.OrthancRestApis;

public class TableExportSeriesModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Serie description*", "Modality", "Instances", "Secondary capture", "ID", "Serie Number", "serieObject"};
	private Class<?>[] classEntetes = {String.class, String.class, Integer.class, Boolean.class, String.class, String.class, Serie.class};
	private OrthancRestApis connexionHttp;
	private QueryOrthancData queryOrthanc;
	//Store the current StudyOrthanc ID the Series came from (for refresh)
	private Study_Anonymized currentStudy;
	private VueAnon vueAnon;

	public TableExportSeriesModel(OrthancRestApis connexionHttp, QueryOrthancData queryOrthanc, VueAnon vueAnon){
		super(0,7);
		this.connexionHttp=connexionHttp;
		this.queryOrthanc=queryOrthanc;
		this.vueAnon=vueAnon;
	
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
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		
		String uid = this.getValueAt(row, 4).toString();
		String oldDesc ;
		if (this.getValueAt(row, 0)==null) oldDesc=""; else oldDesc=this.getValueAt(row, 0).toString();
		
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
			boolean success=true;
			@Override
			protected Void doInBackground() {
				String url="/series/" + uid + "/modify";
				vueAnon.setStateExportMessage("Modifying a serie description", "red", -1);
				vueAnon.pack();
				StringBuilder sb =connexionHttp.makePostConnectionAndStringBuilder(url, ("{\"Replace\":{\"SeriesDescription\":\"" + value.toString() + "\"}}"));
				if(sb!=null) {
					System.out.println("ici");
					connexionHttp.makeDeleteConnection("/series/" + uid );
					System.out.println("la");
				}else {
					success=false;
				}
				return null;
			}
			@Override
			protected void done(){
				if(success) {
					refresh();
					vueAnon.setStateExportMessage("The serie description has been changed.","green",4);
				
				}else {
					vueAnon.setStateExportMessage("Error during Dicom Edition","red",-1);
				}
				vueAnon.pack();	
				
			}
		};
		
		//If value changed, validate the change and trigger the worker
		if(!oldDesc.equals(value.toString()) && col == 0){
			super.setValueAt(value, row, col);
			worker.execute();
		}
		
	}

	public void removeAllSecondaryCaptures() {
		
		for(int i=0; i<this.getRowCount(); i++){
			Serie serie= (Serie) this.getValueAt(i, 6);
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
	
	public void removeSerie(int selectedRow, VueAnon vue) {
		Serie serie= (Serie) this.getValueAt(selectedRow,7);
		String url="/series/" + serie.getId();
		vue.setStateExportMessage("Deleting "+serie.getSerieDescription(), "red", -1);
		boolean success=connexionHttp.makeDeleteConnection(url);
		if(success) {
			vue.setStateExportMessage("Deleted suceeded", "green", 4);
		}else {
			vue.setStateExportMessage("Delete Failed", "red", -1);
		}
		
	}

	public void addSerie(Study_Anonymized studyAnonymized) {
		this.currentStudy=studyAnonymized;
		clear();
		for(Serie serie:studyAnonymized.getAnonymizedStudy().getSeries()) {
			addRow(new String[] {serie.getSerieDescription(), serie.getModality(), String.valueOf(serie.getNbInstances()), String.valueOf(serie.isSecondaryCapture()), serie.getId(), serie.getSeriesNumber()});
		}
	}
	
	public void refresh() {
		currentStudy.getAnonymizedStudy().refreshChildSeries(queryOrthanc);
		addSerie(currentStudy);
	}
	
	public void deleteAllSc() {
		currentStudy.getAnonymizedStudy().deleteAllSc(connexionHttp);
		this.refresh();
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
