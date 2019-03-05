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

import org.petctviewer.orthanc.Patient;
import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class QueryOrthancData {
	private JsonParser parserJson = new JsonParser();
	private OrthancRestApis connexion;
	
	public QueryOrthancData(OrthancRestApis connexion) {
		this.connexion=connexion;
	}
	
	public ArrayList<Patient> findPatients(String inputType, String input, String date, String studyDesc) {
		
		JsonObject query=new JsonObject();
		query.addProperty("Level", "Patients");
		query.addProperty("Expand", true);
		
		JsonObject queryDetails=new JsonObject();
		
		queryDetails.addProperty("StudyDate", date);
		
		if(!studyDesc.equals("*")){
			queryDetails.addProperty("StudyDescription", studyDesc);
		}
		
		switch (inputType) {
			case "Patient name":
				queryDetails.addProperty("PatientName", input);
				break;
			case "Accession number":
				queryDetails.addProperty("AccessionNumber", input);
				break;
				
			case "Patient ID":
				queryDetails.addProperty("PatientID", input);
			default:
				break;
		}
		
		query.add("Query", queryDetails);
		
		StringBuilder sb=connexion.makePostConnectionAndStringBuilder("/tools/find?expand", query.toString());
		JsonArray patients=(JsonArray) parserJson.parse(sb.toString());
		ArrayList<Patient> patientList=new ArrayList<Patient>();
		
		Iterator<JsonElement> patientsArray=patients.iterator();
		while(patientsArray.hasNext()) {
			JsonObject patientJson=patientsArray.next().getAsJsonObject();
			String patientOrthancId=patientJson.get("ID").getAsString();
			String patientId="N/A";
			String patientName="N/A";
			String patientBirthDate="N/A";
			String patientSex="N/A";
			
			if(patientJson.has("PatientBirthDate")) {
				patientBirthDate=patientJson.get("PatientBirthDate").getAsString();
			}
			
			if(patientJson.has("PatientSex")) {
				patientSex=patientJson.get("PatientSex").getAsString();
			}
			
			if(patientJson.get("MainDicomTags").getAsJsonObject().has("PatientName")) {
				patientName=patientJson.get("MainDicomTags").getAsJsonObject().get("PatientName").getAsString();
			}
			if(patientJson.get("MainDicomTags").getAsJsonObject().has("PatientID")) {
				patientId=patientJson.get("MainDicomTags").getAsJsonObject().get("PatientID").getAsString();
			}
			
			Patient patient=new Patient(patientName,patientId,patientBirthDate,patientSex,patientOrthancId);
			patientList.add(patient);
		}
		return patientList;
		
		
	}
	
	public ArrayList<Study2>  getStudiesOfPatient(String patientOrthancID) {
		
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/patients/"+patientOrthancID);
		JsonObject patientData=(JsonObject) parserJson.parse(sb.toString());
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
		
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/studies/"+studyOrthancID);
		
		SimpleDateFormat format =new SimpleDateFormat("yyyyMMdd");
		
		JsonObject studyData=(JsonObject) parserJson.parse(sb.toString());
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
		String seriesDescription= seriesDetails.get("SeriesDescription").getAsString();
		String seriesNumber= seriesDetails.get("SeriesNumber").getAsString();
		
		String parentStudyOrthancID= serieData.get("ParentStudy").getAsString();
		
		StringBuilder sb2=connexion.makeGetConnectionAndStringBuilder("/instances/"+serieData.get("Instances").getAsJsonArray().get(0).getAsString()+"/metadata/SopClassUid");
		String sopClassUid= sb2.toString();

		
		Serie serie=new Serie(seriesDescription, modality, nbOfSlice, seriesOrthancID, parentStudyOrthancID, serieData.get("Instances").getAsJsonArray().get(0).getAsString() , seriesNumber, sopClassUid);
		
		return serie;
	}

	
}