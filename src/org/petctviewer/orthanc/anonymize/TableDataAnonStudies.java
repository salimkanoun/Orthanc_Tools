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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.petctviewer.orthanc.*;

import javax.swing.table.AbstractTableModel;

public class TableDataAnonStudies extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Study description*", "Study date", "Old UID", "Patient id"};
	private Class<?>[] typeEntetes = {String.class, Date.class, String.class, String.class};
	private ArrayList<Study> shownStudies = new ArrayList<Study>();
	private ArrayList<Study> studies = new ArrayList<Study>();
	private ArrayList<String> listeOldOrthancUIDs = new ArrayList<String>();
	private ArrayList<String> listeNewOrthancUIDs = new ArrayList<String>();
	private HashMap<String, String> newDescriptions = new HashMap<String, String>();
	private String url;
	private ParametreConnexionHttp connexionHttp;

	public TableDataAnonStudies(ParametreConnexionHttp connexionHttp){
		super();
		this.connexionHttp=connexionHttp;
	}

	@Override
	public int getColumnCount() {
		return entetes.length;
	}

	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return typeEntetes[columnIndex];
	}

	@Override
	public int getRowCount() {
		return shownStudies.size();
	}

	public boolean isCellEditable(int row, int col){
		if(col == 0){
			return true; 
		}
		return false;
	}

	public void setValueAt(Object value, int row, int col) {
		shownStudies.get(row).setStudyDescription(value.toString());
		fireTableCellUpdated(row, col);
		String uid = this.getValueAt(row, 4).toString();
		if(!newDescriptions.containsKey(uid)){
			newDescriptions.put(uid, value.toString());
		}else{
			newDescriptions.replace(uid, value.toString());
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return shownStudies.get(rowIndex).getStudyDescription();
		case 1:
			return shownStudies.get(rowIndex).getDate();
		case 2:
			return shownStudies.get(rowIndex).getOldStudyInstanceUID();
		case 3:
			return shownStudies.get(rowIndex).getPatientID();
		case 4:
			return shownStudies.get(rowIndex).getId();
		default:
			return null; //Ne devrait jamais arriver
		}
	}

	public ArrayList<String> getModalities() {
		ArrayList<String> listeModalities = new ArrayList<String>();
		for(Study s : this.studies){
			this.url="/studies/" + s.getId() + "/series";
			
			StringBuilder sb =connexionHttp.makeGetConnectionAndStringBuilder(url);
			JSONParser parser=new JSONParser();
			try {
				JSONArray series=(JSONArray) parser.parse(sb.toString());
				for(int i=0; i<series.size(); i++) {
					JSONObject serieData=(JSONObject) series.get(i);
					JSONObject sereiesMain=(JSONObject) serieData.get("MainDicomTags");
					listeModalities.add(sereiesMain.get("Modality").toString());
				}
				
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return listeModalities;
	}

	// This method removes the secondary captures and structured reports, after anonymization
	public void removeScAndSr() throws IOException{
		ArrayList<String> listeUidsToRemove = new ArrayList<String>();
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

		ArrayList<String> listeSerieUids = new ArrayList<String>();
		listeSerieUids = this.fillListIDs(this.listeNewOrthancUIDs, "studies", "Series");

		ArrayList<String> listeInstanceUids = new ArrayList<String>();
		listeInstanceUids = this.fillListIDs(listeSerieUids,"series", "Instances");

		for(String instanceUid : listeInstanceUids){
			this.url="/instances/" + instanceUid + "/metadata/SopClassUid";
			StringBuilder sb =connexionHttp.makeGetConnectionAndStringBuilder(url);
			if(sopClassUIDs.contains(sb.toString())){
				listeUidsToRemove.add(instanceUid);
			}
		}
		
		for(String uidToRemove : listeUidsToRemove){
			this.url="/instances/" + uidToRemove;
			connexionHttp.makeDeleteConnection(url);
		}
	}

	public String getNewDesc(String oldUid){
		return this.newDescriptions.getOrDefault(oldUid, "");
	}

	public ArrayList<String> getOldOrthancUIDs(){
		return this.listeOldOrthancUIDs;
	}

	public ArrayList<String> getOldOrthancUIDsWithID(String patientID){
		ArrayList<String> listeUIDs = new ArrayList<String>();
		for(Study s : this.studies){
			if(s.getPatientID().equals(patientID)){
				listeUIDs.add(s.getId());
			}
		}
		return listeUIDs;
	}

	public ArrayList<String> getNewOrthancUIDs(){
		return this.listeNewOrthancUIDs;
	}

	public ArrayList<String> getPatientIDs(){
		ArrayList<String> listeIDs = new ArrayList<String>();
		for(Study s : this.studies){
			if(!listeIDs.contains(s.getPatientID())){
				listeIDs.add(s.getPatientID());
			}	
		}
		return listeIDs;
	}

	public void removeShownStudy(int rowIndex){
		this.shownStudies.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}


	// This method removes completely a study, and give its UID in order to remove it
	// in the patient's data
	public String removeStudy(int rowIndex){
		Study shownStudy = this.shownStudies.get(rowIndex);
		// To avoid java.util.ConcurrentModificationException we create a list of elements to remove
		Study studyToRemove = null;
		String uidToRemove = "";
		for(Study s : this.studies){
			if(s.equals(shownStudy)){
				studyToRemove = s;
				uidToRemove = s.getId();
			}
		}
		this.studies.remove(studyToRemove);
		this.shownStudies.remove(rowIndex);
		this.listeOldOrthancUIDs.remove(uidToRemove);
		this.newDescriptions.clear();
		fireTableRowsDeleted(rowIndex, rowIndex);
		return uidToRemove;
	}

	public ArrayList<Study> getStudies(){
		return this.studies;
	}

	public ArrayList<Study> getShownStudies(){
		return this.shownStudies;
	}
	
	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addStudies(String patientName, String patientID, ArrayList<String> listeUIDs) throws IOException, ParseException{
		DateFormat parser = new SimpleDateFormat("yyyyMMdd");
		QueryFillStore query = new QueryFillStore(connexionHttp);
		for(String uid : listeUIDs){
			Date studyDate;
			String[] separatedResponse=query.getStudyDescriptionAndUID(uid);
			try{
			studyDate = parser.parse(separatedResponse[2]);
			}catch(ParseException e){
				studyDate=parser.parse("19000101");
			}
			
			// We insert dummy dates and accession numbers which won't be useful for the anonymization
			Study s = new Study(separatedResponse[0], studyDate , "0", uid, separatedResponse[1], patientName, patientID, null);
			if(newDescriptions.containsKey(uid)){
				s.setStudyDescription(newDescriptions.get(uid));
			}
			if(!this.studies.contains(s)){
				this.studies.add(s);
				this.listeOldOrthancUIDs.add(uid);
			}
			
			for(Study study : this.studies){
				if(study.getPatientID().equals(patientID) && !shownStudies.contains(study)){
					shownStudies.add(study);
					fireTableRowsInserted(shownStudies.size() - 1, shownStudies.size() - 1);
					fireTableDataChanged();
				}
			}
		}
	}

	/*
	 * This method empties completely the studies list
	 */
	public void empty(){
		if(this.getRowCount() !=0){
			this.studies.removeAll(this.studies);
			this.shownStudies.removeAll(this.shownStudies);
			this.listeOldOrthancUIDs.removeAll(this.listeOldOrthancUIDs);
			this.newDescriptions.clear();
			fireTableRowsDeleted(0, 0);
		}
	}

	public void addNewUid(String uid){
		if(!this.listeNewOrthancUIDs.contains(uid)){
			this.listeNewOrthancUIDs.add(uid);
		}
	}

	public void removeFromList(String uid){
		this.listeNewOrthancUIDs.remove(uid);
	}

	/*
	 * This method clears the series list
	 */
	public void clear(){
		if(this.getRowCount() !=0){
			for(int i = this.getRowCount(); i > 0; i--){
				this.removeShownStudy(i-1);
			}
		}
	}
	
	/**
	 * retrieve list series IDs of a given study
	 * @param sourceList
	 * @param level
	 * @param pattern
	 * @return
	 * @throws IOException
	 */

	private ArrayList<String> fillListIDs(ArrayList<String> sourceList, String level, String pattern) throws IOException{
		ArrayList<String> list = new ArrayList<String>();
		for(String uid : sourceList){
			this.url="/"+ level + "/" + uid;
			StringBuilder sb =connexionHttp.makeGetConnectionAndStringBuilder(url);
			JSONParser parser=new JSONParser();
				try {
					JSONObject json=(JSONObject) parser.parse(sb.toString());
					JSONArray jsonArray=(JSONArray) json.get(pattern);
					for (int i=0; i<jsonArray.size(); i++){
						list.add(jsonArray.get(i).toString());
					} 
				}catch (org.json.simple.parser.ParseException e) {
					
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		return list;
	}
}
