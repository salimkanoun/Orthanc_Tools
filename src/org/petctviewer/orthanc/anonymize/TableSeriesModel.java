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
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.setup.OrthancRestApis;

public class TableSeriesModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Serie description", "Modality", "Instances", "SC", "Orthanc Id", "Serie Num", "SeriesObj"};
	private Class<?>[] classEntetes = {String.class, String.class, Integer.class, Boolean.class, String.class, String.class, Serie.class};
	private OrthancRestApis connexionHttp;
	private Study2 currentStudy;
	private VueAnon gui;
	private QueryOrthancData queryOrthanc;

	public TableSeriesModel(OrthancRestApis connexionHttp, VueAnon gui, QueryOrthancData queryOrthanc){
		super(0,7);
		//Recupere les settings
		this.connexionHttp=connexionHttp;
		this.gui=gui;
		this.queryOrthanc=queryOrthanc;
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

	/**
	 * Remove all Secondary capture, called form right click on serie Table
	 */
	public void removeAllSecondaryCaptures() {
		SwingWorker<Void,Void> worker=new SwingWorker<Void,Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				for(int i=0; i<getRowCount(); i++) {
					if ((Boolean) getValueAt(i, 3)) {
						gui.setStateMessage("Deleting SC "+getValueAt(i, 0), "red", -1);
						connexionHttp.makeDeleteConnection("/series/"+getValueAt(i, 4));
					}
				}
				return null;
				
			}
			
			@Override
			protected void done() {
				//refresh the table
				refresh();
				gui.setStateMessage("Sec Capture Deletion Done", "green", -1);
			}
			
		};
		
		worker.execute();
		
	}

	
	public void addSerie(Study2 study) {
		this.currentStudy=study;
		clear();
		study.refreshChildSeries(queryOrthanc);
		for(Serie serie:study.getSeries()) {
			this.addRow(new Object[] {serie.getSerieDescription(), 
				serie.getModality(), 
				serie.getNbInstances(), 
				serie.isSecondaryCapture(), 
				serie.getId(), 
				serie.getSeriesNumber(), serie});
			
		}

	}
	
	
	public void addSerie(String studyOrthancID) {
		clear();
		QueryOrthancData querySeries = new QueryOrthancData(connexionHttp);
		try {
			Study2 study = querySeries.getStudyDetails(studyOrthancID, true);
			this.currentStudy=study;
			for(Serie serie:study.getSeries()) {
				this.addRow(new Object[] {serie.getSerieDescription(), 
					serie.getModality(), 
					serie.getNbInstances(), 
					serie.isSecondaryCapture(), 
					serie.getId(), 
					serie.getSeriesNumber(), serie});
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}

	/*
	 * This method clears the series list
	 */
	public void clear(){
		this.setRowCount(0);
	}
	
	public void refresh() {
		currentStudy.refreshChildSeries(queryOrthanc);
		addSerie(currentStudy);
	}
	
}
