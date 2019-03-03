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
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.petctviewer.orthanc.setup.ParametreConnexionHttp;

public class TableExportSeriesModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Serie description*", "Modality", "Instances", "Secondary capture", "ID", "Serie Number"};
	private ArrayList<Serie> series = new ArrayList<Serie>();
	private ArrayList<String> instancesWithSecondaryCapture = new ArrayList<String>();
	private String studyID = "";
	private String url;
	private JFrame frame;
	private JLabel stateExport;
	private ParametreConnexionHttp connexionHttp;

	public TableExportSeriesModel(ParametreConnexionHttp connexionHttp, JFrame frame, JLabel stateExport){
		super();
		//On set les settings de connexion
		this.connexionHttp=connexionHttp;
		this.frame = frame;
		this.stateExport = stateExport;
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
	public int getRowCount(){
		return series.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex){
		switch(columnIndex){
		case 0:
			return series.get(rowIndex).getSerieDescription();
		case 1:
			return series.get(rowIndex).getModality();
		case 2:
			return series.get(rowIndex).getNbInstances();
		case 3:
			return series.get(rowIndex).isSecondaryCapture();
		case 4:
			return series.get(rowIndex).getId();
		case 5:
			return series.get(rowIndex).getSeriesNumber();
		default:
			return null; //Ne devrait jamais arriver
		}
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

	
	public Serie getSerie(int rowIndex){
		return this.series.get(rowIndex);
	}

	public boolean checkSopClassUid(String instanceUid) {
		if(instanceUid != null){
			url="/instances/" + instanceUid + "/metadata/SopClassUid";
			StringBuilder sb =connexionHttp.makeGetConnectionAndStringBuilder(url);

			ArrayList<String> sopClassUIDs = new ArrayList<String>();
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.1");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.2");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.3");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.4");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.11");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.22");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.33");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.40");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.50");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.59");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.65");
			sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.67");

			if(sopClassUIDs.contains(sb.toString())){
				this.instancesWithSecondaryCapture.add(instanceUid);
				return true;
			}
			return false;
		}
		return false;
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
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	public ArrayList<Serie> getSeries(){
		return this.series;
	}
	
	public void addSerie(String studyID) {
		this.studyID = studyID;
		
		QueryFillStore querySeries = new QueryFillStore(connexionHttp,"series", null, studyID, null, null);
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
			}
			if(!this.series.contains(s)){
				this.series.add(s);
				fireTableRowsInserted(series.size() - 1, series.size() - 1);
			}
		}

	}

	/*
	 * This method clears the series list
	 */
	public void clear(){
		if(this.getRowCount() !=0){
			for(int i = this.getRowCount(); i > 0; i--){
				this.removeSerie(i-1);
			}
		}
	}

}
