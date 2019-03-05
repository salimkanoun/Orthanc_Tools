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

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.setup.OrthancRestApis;

public class TableExportSeriesModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Serie description*", "Modality", "Instances", "Secondary capture", "ID", "Serie Number"};
	private ArrayList<Serie> series = new ArrayList<Serie>();
	private String studyID = "";
	private String url;
	private JFrame frame;
	private JLabel stateExport;
	private OrthancRestApis connexionHttp;

	public TableExportSeriesModel(OrthancRestApis connexionHttp, JFrame frame, JLabel stateExport){
		super(0,6);
		//On set les settings de connexion
		this.connexionHttp=connexionHttp;
		this.frame = frame;
		this.stateExport = stateExport;
	}

	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}

	public boolean isCellEditable(int row, int col){
		if(col == 0){
			return true; 
		}
		return false;
	}
	
	public void setValueAt(Object value, int row, int col) {
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
	}

	public void removeAllSecondaryCaptures() {
		ArrayList<Serie> seriesToRemove = new ArrayList<Serie>();
		for(Serie s : series){
			if(s.isSecondaryCapture()){
				url="/series/" + s.getId();
				boolean success=connexionHttp.makeDeleteConnection(url);
				if(success) {
					seriesToRemove.add(s);
				}
			}
		}
		for(Serie s : seriesToRemove){
			removeSerie(series.indexOf(s));
		}
	}

	public void removeSerie(int rowIndex){
		this.series.remove(rowIndex);
		this.removeRow(rowIndex);
	}

	public ArrayList<Serie> getSeries(){
		return this.series;
	}
	
	public void addSerie(String studyID) {
		this.studyID = studyID;

		QueryOrthancData querySeries = new QueryOrthancData(connexionHttp);
		
		Study2 study =querySeries.getStudyDetails(studyID,true);
		for(Serie serie:study.getSeries()) {
			series.add(serie); 
			//"Serie description*", "Modality", "Instances", "Secondary capture", "ID", "Serie Number"
			this.addRow(new String[] {serie.getSerieDescription(), serie.getModality(), String.valueOf(serie.getNbInstances()), String.valueOf(serie.isSecondaryCapture()), serie.getFistInstanceId(), serie.getSeriesNumber()});
		}
		fireTableRowsInserted(series.size() - 1, series.size() - 1);

		
		
		/*querySeries.getSeriesOfStudy(studyID);
		List<JSONObject> jsonResponsesPatient=querySeries.getJsonResponse();
		
		String[] id = new String[jsonResponsesPatient.size()];
		String[] description = new String[jsonResponsesPatient.size()];
		String[] serieNumber = new String[jsonResponsesPatient.size()];
		String[] modality = new String[jsonResponsesPatient.size()];
		String[] nbInstances = new String[jsonResponsesPatient.size()];
		String[] instance = new String[jsonResponsesPatient.size()];
		String[] study = new String[jsonResponsesPatient.size()];
		
		//On boucle pour extraire les valeurs des JSONs
		for(int i=0; i<jsonResponsesPatient.size();i++){
			JSONObject mainDicomTag=(JSONObject) jsonResponsesPatient.get(i).get("MainDicomTags");
			id[i]=(String) jsonResponsesPatient.get(i).get("ID");
			
			if (mainDicomTag.containsKey("SeriesDescription")) {
				description[i]=((String) mainDicomTag.get("SeriesDescription"));
			} else {
				description[i]="";
			}
			
			if (mainDicomTag.containsKey("SeriesNumber")) {
				serieNumber[i]=((String) mainDicomTag.get("SeriesNumber"));
			} else {
				serieNumber[i]="";
			}
			
			if (mainDicomTag.containsKey("Modality")) {
				modality[i]=((String) mainDicomTag.get("SeriesDescription"));
			} else {
				modality[i]="";
			}
			
			JSONArray instancesArray=(JSONArray) jsonResponsesPatient.get(i).get("Instances");
			nbInstances[i]=String.valueOf(instancesArray.size());
			instance[i]=(String) instancesArray.get(0);
			
			study[i]=  (String) jsonResponsesPatient.get(i).get("ParentStudy");
		}

		//checkSopClass will be set on click
		for(int i = 0; i < id.length; i++){
			Serie s = new Serie(description[i], modality[i], nbInstances[i], id[i], study[i], instance[i],serieNumber[i], this.checkSopClassUid(instance[i]));
			if(instancesWithSecondaryCapture.contains(instance[i])){
				s = new Serie(description[i], modality[i], nbInstances[i], id[i], study[i], instance[i],serieNumber[i], true);
			}*/
			
		//}

	}

	/*
	 * This method clears the series list
	 */
	public void clear(){
		this.series.clear();
		this.setRowCount(0);
		
	}

}
