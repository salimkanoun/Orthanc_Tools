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
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.petctviewer.orthanc.setup.ParametreConnexionHttp;


public class QueryFillStore {

	private String url;
	private String query;
	private String level;
	private String input;
	private ArrayList<String> ids = new ArrayList<String>();
	private String toolboxListContent;
	private JSONParser parser = new JSONParser();
	ParametreConnexionHttp connexion;

	public QueryFillStore(ParametreConnexionHttp connexion){
		this.connexion=connexion;
		this.url="/tools/find";
	}
	
	public QueryFillStore(ParametreConnexionHttp connexion, String level, String inputType, String input, 
			String date, String studyDesc) {
		
		this.connexion=connexion;
	
		this.url="/tools/find";
		
		this.level = level;
		this.input = input;
		this.query = "{ \"Level\" : \"" + "Patients" + "\","
				+ "\"Query\" : {\"PatientName\" : \"" + input +"\"}"
				+ "}";
		
		if(level != null && level.equals("patients")){
			switch (inputType) {
			case "Patient name":
				if(studyDesc.equals("*")){
					this.query = "{ \"Level\" : \"" + "Patients" + "\","
							+ "\"Query\" : {\"PatientID\" : \"" + "*" +"\", "
							+ "\"PatientName\" : \"" + input +"\","
							+ "\"StudyDate\" : \"" + date +"\","
							+ "\"AccessionNumber\" : \"" + "*" +"\" }"
							+ "}";
				}else{
					this.query = "{ \"Level\" : \"" + "Patients" + "\","
							+ "\"Query\" : {\"PatientID\" : \"" + "*" +"\", "
							+ "\"PatientName\" : \"" + input +"\","
							+ "\"StudyDate\" : \"" + date +"\","
							+ "\"StudyDescription\" : \"" + studyDesc + "\","
							+ "\"AccessionNumber\" : \"" + "*" +"\" }"
							+ "}";
				}
				break;
			case "Accession number":
				if(studyDesc.equals("*")){
					this.query = "{ \"Level\" : \"" + "Patients" + "\","
							+ "\"Query\" : {\"PatientID\" : \"" + "*" +"\", "
							+ "\"PatientName\" : \"" + "*" +"\","
							+ "\"StudyDate\" : \"" + date +"\","
							+ "\"AccessionNumber\" : \"" + input +"\" }"
							+ "}";
				}else{
					this.query = "{ \"Level\" : \"" + "Patients" + "\","
							+ "\"Query\" : {\"PatientID\" : \"" + "*" +"\", "
							+ "\"PatientName\" : \"" + "*" +"\","
							+ "\"StudyDate\" : \"" + date +"\","
							+ "\"StudyDescription\" : \"" + studyDesc + "\","
							+ "\"AccessionNumber\" : \"" + input +"\" }"
							+ "}";
				}
				break;
			case "Patient ID":
				if(studyDesc.equals("*")){
					this.query = "{ \"Level\" : \"" + "Patients" + "\","
							+ "\"Query\" : {\"PatientID\" : \"" + input +"\", "
							+ "\"PatientName\" : \"" + "*" +"\","
							+ "\"StudyDate\" : \"" + date +"\","
							+ "\"AccessionNumber\" : \"" + "*" +"\" }"
							+ "}";
				}else{
					this.query = "{ \"Level\" : \"" + "Patients" + "\","
							+ "\"Query\" : {\"PatientID\" : \"" + input +"\", "
							+ "\"PatientName\" : \"" + "*" +"\","
							+ "\"StudyDate\" : \"" + date +"\","
							+ "\"StudyDescription\" : \"" + studyDesc + "\","
							+ "\"AccessionNumber\" : \"" + "*" +"\" }"
							+ "}";
				}
				break;
			default:
				break;
			}
		}
		
	}

	public String getQuery(){
		return this.query;
	}

	private String sendQuery(String action){
		StringBuilder sb = new StringBuilder();
		
		if(action.equals("storeIDs") && this.url.toString().contains("tools/find")){
			sb=connexion.makePostConnectionAndStringBuilder(url, this.query);
		}else if(action.equals("store")){
			sb=connexion.makePostConnectionAndStringBuilder(url, this.toolboxListContent);
		}
		else {
			sb=connexion.makeGetConnectionAndStringBuilder(this.url);
		}
		return sb.toString();
	}
	
	private void storeIDs(){
		
		if(this.level.equals("series")){
			
			this.url="/studies/" + this.input;
			JSONObject seriesResponses;
			try {
				seriesResponses = (JSONObject) parser.parse(this.sendQuery("get"));
				JSONArray seriesValues=(JSONArray) seriesResponses.get("Series");
				//On met les array des Series dans la list des IDs
				for (int i=0 ; i<seriesValues.size(); i++){
					ids.add(seriesValues.get(i).toString());
				}
			} catch (ParseException e) {e.printStackTrace();}
			
		}
		
		else if(this.level.equals("patients")){
			JSONArray patientsResponses;
			try {
				patientsResponses = (JSONArray) parser.parse(this.sendQuery("storeIDs"));
				//On met les array des Series dans la list des IDs
				for (int i=0 ; i<patientsResponses.size(); i++){
					ids.add(patientsResponses.get(i).toString());
				}
			} catch (ParseException e) {e.printStackTrace();}
		}
		
		else if(this.level.equals("studies")){
			this.url="/patients/" + this.input;
			JSONObject studyResponses;
			try {
				studyResponses = (JSONObject) parser.parse(this.sendQuery("storeIDs"));
				JSONArray studiesValues=(JSONArray) studyResponses.get("Studies");
				//On met les array des Studies dans la list des IDs
				for (int i=0 ; i<studiesValues.size(); i++){
					ids.add(studiesValues.get(i).toString());
				}
			} catch (ParseException e) {e.printStackTrace();}
			
		}
	}

	public ArrayList<String> getIDs(){
		return this.ids;
	}
	
	// Renvoie les responses JSON par niveau de query
	public List<JSONObject> getJsonResponse() {
		ids.removeAll(ids);
		this.storeIDs();
	
		List<JSONObject> jsonResponses=new ArrayList<JSONObject>();
		
		for(String id : ids){
			this.url="/" + this.level + "/" + id + "/";
			try {
				jsonResponses.add((JSONObject) parser.parse(this.sendQuery("get")));
			} catch (ParseException e) {e.printStackTrace();}
		}
		return jsonResponses;
	}
	
	public String[] getStudyDescriptionAndUID(String orthancUID) throws IOException{
		this.url="/studies/" + orthancUID;
		String[] studyDescriptionAndUID=new String[3];
		try {
			//SK Utiliser les contains key pour le parsing COMME DANS CET EXEMPLE
			JSONObject responseJson=(JSONObject) parser.parse(this.sendQuery("get"));
			JSONObject responsemaintag=(JSONObject) responseJson.get("MainDicomTags");
			
			String studyDescription;
			String studyDate;
			String studyInstanceUID;
			
			if (responsemaintag.containsKey("StudyDescription")) studyDescription =responsemaintag.get("StudyDescription").toString(); else studyDescription="";
			if (responsemaintag.containsKey("StudyDate")) studyDate=responsemaintag.get("StudyDate").toString(); else studyDate="";
			if (responsemaintag.containsKey("StudyInstanceUID")) studyInstanceUID=responsemaintag.get("StudyInstanceUID").toString(); else studyInstanceUID="";
			
			studyDescriptionAndUID[0]=studyDescription;
			studyDescriptionAndUID[1]=studyInstanceUID;
			studyDescriptionAndUID[2]=studyDate;
		} catch (ParseException e) {e.printStackTrace();}
		
		return studyDescriptionAndUID;
	}

	public Object[] getAET() {
		this.url="/modalities";
		
		// We split the server response in a tab
		ArrayList<String> indexes = new ArrayList<String>();
		
		try {
			JSONArray aet = (JSONArray) parser.parse(this.sendQuery("get"));
			for (int i=0 ; i<aet.size(); i++){
				indexes.add(aet.get(i).toString());
			}
		} catch (ParseException e) {e.printStackTrace();}
		
		// We convert the ArrayList to an Object[]
		return indexes.toArray();
	}
	
	public void store(String aet, ArrayList<String> idList) throws IOException{
		StringBuilder ids = new StringBuilder();
		ids.append("[");
		for(int i = 0; i < idList.size(); i++){
			ids.append("\"" + idList.get(i) + "\",");
		}
		ids.replace(ids.length()-1, ids.length(), "]");
		this.toolboxListContent = ids.toString();
		this.url="/modalities/" + aet + "/store";
		this.sendQuery("store");
	}
	
	public Object[] getPeers() {
		this.url="/peers";
		
		// We split the server response in a tab
		ArrayList<String> indexes = new ArrayList<String>();

		try {
			JSONArray peers = (JSONArray) parser.parse(this.sendQuery("get"));
			for (int i=0 ; i<peers.size(); i++){
				indexes.add(peers.get(i).toString());
			}
		} catch (ParseException e) {e.printStackTrace();}
		
		// We convert the ArrayList to an Object[]
		return indexes.toArray();
	}
	
	public void sendPeer(String peer, ArrayList<String> idList) throws IOException{
		StringBuilder ids = new StringBuilder();
		ids.append("[");
		for(int i = 0; i < idList.size(); i++){
			ids.append("\"" + idList.get(i) + "\",");
		}
		ids.replace(ids.length()-1, ids.length(), "]");
		this.toolboxListContent = ids.toString();
		this.url="/peers/" + peer + "/store";
		this.sendQuery("store");
	}
	
}
