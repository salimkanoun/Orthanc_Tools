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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class QueryFillStore {

	private String url;
	private String query;
	private String level;
	private String input;
	private ArrayList<String> ids = new ArrayList<String>();
	private JSONParser parser = new JSONParser();
	private JsonParser parserJson = new JsonParser();
	private OrthancRestApis connexion;
	
	public QueryFillStore(OrthancRestApis connexion, String level, String inputType, String input, 
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
	
	public ArrayList<Study2>  getStudiesOfPatient(String patientOrthancID) {
		
		
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/patients/"+patientOrthancID);
		
		JsonObject patientData=(JsonObject) parserJson.parse(sb.toString());
		System.out.println(patientData);
		
		JsonArray studyIdArray=patientData.get("Studies").getAsJsonArray();
		
		ArrayList<Study2> studies=new ArrayList<Study2>();
		
		Iterator<JsonElement> iterator = studyIdArray.iterator();
		while(iterator.hasNext()) {
			Study2 study=getStudyDetails(iterator.next().getAsString(), false);
			studies.add(study);
		}
		return studies;
	}
	
	public Study2 getStudyDetails(String studyOrthancID, boolean includeSerieLevel) {
		
		System.out.println(studyOrthancID);
		
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/studies/"+studyOrthancID);
		
		SimpleDateFormat format =new SimpleDateFormat("YYYYMMdd");
		
		JsonObject studyData=(JsonObject) parserJson.parse(sb.toString());
		System.out.println(studyData);
		
		JsonObject studyDetails = (JsonObject) studyData.get("MainDicomTags");
		String accessionNumber=studyDetails.get("AccessionNumber").getAsString();
		String studyDate=studyDetails.get("StudyDate").getAsString();
		Date studyDateObject=null;
		try {
			studyDateObject=format.parse(studyDate);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String studyDescription=studyDetails.get("StudyDescription").getAsString();
		
		JsonObject patientDetails = (JsonObject) studyData.get("PatientMainDicomTags");
		String patientName=patientDetails.get("PatientName").getAsString();
		String patientId=patientDetails.get("PatientID").getAsString();
		String patientOrthancID=studyData.get("ParentPatient").getAsString();
		
		ArrayList<Serie> series=null;
		
		if(includeSerieLevel) {
			series=new ArrayList<Serie>();
			JsonArray childSeries= studyData.get("Series").getAsJsonArray();
			System.out.println(childSeries);
			Iterator<JsonElement> iterator = childSeries.iterator();
			while(iterator.hasNext()) {
				String seriesOrthancId=iterator.next().getAsString();
				Serie serie=getSeriesDetails(seriesOrthancId);
				series.add(serie);
			}
		}

		Study2 study=new Study2(studyDescription, studyDateObject, accessionNumber, studyOrthancID, patientName, patientId, patientOrthancID, series);
		
		return study;
		
	}
	
	public Serie getSeriesDetails(String seriesOrthancID) {
		
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/series/"+seriesOrthancID);
		JsonObject serieData=(JsonObject) parserJson.parse(sb.toString());
		
		JsonObject seriesDetails = (JsonObject) serieData.get("MainDicomTags");
		String modality = seriesDetails.get("Modality").getAsString();
		int nbOfSlice= serieData.get("Instances").getAsJsonArray().size();
		//String seriesDate= seriesDetails.get("SeriesDate").getAsString();
		String seriesDescription= seriesDetails.get("SeriesDescription").getAsString();
		String seriesNumber= seriesDetails.get("SeriesNumber").getAsString();
		
		String parentStudyOrthancID= serieData.get("ParentStudy").getAsString();
		
		StringBuilder sb2=connexion.makeGetConnectionAndStringBuilder("/instances/"+serieData.get("Instances").getAsJsonArray().get(0).getAsString()+"/metadata/SopClassUid");
		String sopClassUid= sb2.toString();

		
		Serie serie=new Serie(seriesDescription, modality, nbOfSlice, seriesOrthancID, parentStudyOrthancID, serieData.get("Instances").getAsJsonArray().get(0).getAsString() , seriesNumber, sopClassUid);
		
		return serie;
	}

	private String sendQuery(String action){
		StringBuilder sb = new StringBuilder();
		
		if(action.equals("storeIDs") && this.url.toString().contains("tools/find")){
			sb=connexion.makePostConnectionAndStringBuilder(url, this.query);
		} else {
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

	
}
