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

import org.petctviewer.orthanc.anonymize.datastorage.Patient;
import org.petctviewer.orthanc.anonymize.datastorage.Serie;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class QueryOrthancData {
	private JsonParser parserJson = new JsonParser();
	private OrthancRestApis connexion;
	private SimpleDateFormat format =new SimpleDateFormat("yyyyMMdd");
	
	public QueryOrthancData(OrthancRestApis connexion) {
		this.connexion=connexion;
	}
	
	public static void main(String[] arg) {
		QueryOrthancData ici=new QueryOrthancData(new OrthancRestApis(null));
		ici.getStudyObjbyStudyInstanceUID("");
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
		
		ArrayList<Study2> studies=new ArrayList<Study2>();
		
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/patients/"+patientOrthancID);
		if(sb==null) {
			return studies;
		}
		JsonObject patientData=(JsonObject) parserJson.parse(sb.toString());
		JsonArray studyIdArray=patientData.get("Studies").getAsJsonArray();
		
		
		Iterator<JsonElement> iterator = studyIdArray.iterator();
		while(iterator.hasNext()) {
			Study2 study=getStudyDetails(iterator.next().getAsString(), true);
			studies.add(study);
		}
		return studies;
	}
	
	public int[]  getStudyStatistics(String studyOrthancID) {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/studies/" + studyOrthancID +"/statistics");
		
		if(sb==null) {
			return null;
		}
		JsonObject statisticsAnswer=parserJson.parse(sb.toString()).getAsJsonObject();
		int countSeries=statisticsAnswer.get("CountSeries").getAsInt();
		int countInstances=statisticsAnswer.get("CountInstances").getAsInt();
		int sizeMb=statisticsAnswer.get("DiskSizeMB").getAsInt();
		
		int[] statistics=new int[3];
		statistics[0]=countSeries;
		statistics[1]=countInstances;
		statistics[2]=sizeMb;
		
		return statistics;
	}
	
	public Study2 getStudyDetails(String studyOrthancID, boolean includeSerieLevel) {
		
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/studies/"+studyOrthancID);
		
		
		JsonObject studyData=(JsonObject) parserJson.parse(sb.toString());
		JsonObject studyDetails = (JsonObject) studyData.get("MainDicomTags");
		
		String accessionNumber="N/A";
		if(studyDetails.has("AccessionNumber")) {
			accessionNumber=studyDetails.get("AccessionNumber").getAsString();
		}
		
		String studyInstanceUid=studyDetails.get("StudyInstanceUID").getAsString();
		
		String studyDate=studyDetails.get("StudyDate").getAsString();
		Date studyDateObject=null;
		try {
			studyDateObject=format.parse("19000101");
			studyDateObject=format.parse(studyDate);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String studyDescription="N/A";
		if(studyDetails.has("StudyDescription")){
			studyDescription=studyDetails.get("StudyDescription").getAsString();
		}
		
		JsonObject patientDetails = (JsonObject) studyData.get("PatientMainDicomTags");
		
		String patientName="N/A";
		if(patientDetails.has("PatientName")) {
			patientName=patientDetails.get("PatientName").getAsString();
		}
		
		String patientId="N/A";
		if(patientDetails.has("PatientID")) {
			patientId=patientDetails.get("PatientID").getAsString();
		}
		
		String patientSex="N/A";
		if(patientDetails.has("PatientSex")) {
			patientSex=patientDetails.get("PatientSex").getAsString();
		}
		
		Date patientDob=null;
		if(patientDetails.has("PatientBirthDate")) {
			try {
				patientDob = format.parse("19000101");
				patientDob=format.parse(patientDetails.get("PatientBirthDate").getAsString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		
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

		Study2 study=new Study2(studyDescription, studyDateObject, accessionNumber, studyOrthancID, patientName, patientId, patientDob, patientSex, patientOrthancID,studyInstanceUid, series);
		
		return study;
		
	}
	
	private Serie getSeriesDetails(String seriesOrthancID) {
		
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/series/"+seriesOrthancID);
		JsonObject serieData=(JsonObject) parserJson.parse(sb.toString());
		
		JsonObject seriesDetails = (JsonObject) serieData.get("MainDicomTags");
		int nbOfSlice= serieData.get("Instances").getAsJsonArray().size();
		
		String modality ="N/A";
		if(seriesDetails.has("Modality")) {
			modality=seriesDetails.get("Modality").getAsString();
		}
		
		String seriesDescription="N/A" ;
		if(seriesDetails.has("SeriesDescription")) {
			seriesDescription=seriesDetails.get("SeriesDescription").getAsString();
		}
		
		String seriesNumber="N/A";
		if(seriesDetails.has("SeriesNumber")) {
			seriesNumber= seriesDetails.get("SeriesNumber").getAsString();
		}
		String parentStudyOrthancID= serieData.get("ParentStudy").getAsString();
		
		StringBuilder sb2=connexion.makeGetConnectionAndStringBuilder("/instances/"+serieData.get("Instances").getAsJsonArray().get(0).getAsString()+"/metadata/SopClassUid");
		String sopClassUid= sb2.toString();

		
		Serie serie=new Serie(seriesDescription, modality, nbOfSlice, seriesOrthancID, parentStudyOrthancID, serieData.get("Instances").getAsJsonArray().get(0).getAsString() , seriesNumber, sopClassUid);
		
		return serie;
	}
	
	/**
	 * For AutoQuery
	 */
	public Study2 getStudyObjbyStudyInstanceUID(String studyInstanceUID) {

		JsonObject query=new JsonObject();
		query.addProperty("Level", "Study");
		query.addProperty("Expand", true);
		
		JsonObject queryDetails=new JsonObject();
		
		queryDetails.addProperty("StudyInstanceUID", studyInstanceUID);
		
		query.add("Query", queryDetails);
		System.out.println(query);
		
		StringBuilder sb=connexion.makePostConnectionAndStringBuilder("/tools/find?expand", query.toString());
		JsonObject studyAnswer=parserJson.parse(sb.toString()).getAsJsonArray().get(0).getAsJsonObject();
		Study2 study=answerToStudyObject(studyAnswer);
		
		return study;
		
		
	}
	
	private Study2 answerToStudyObject(JsonObject studyData) {
		
		String studyOrthancID=studyData.get("ID").getAsString();
		JsonObject studyDetails = (JsonObject) studyData.get("MainDicomTags");
		
		String accessionNumber="N/A";
		if(studyDetails.has("AccessionNumber")) {
			accessionNumber=studyDetails.get("AccessionNumber").getAsString();
		}
		
		String studyInstanceUid=studyDetails.get("StudyInstanceUID").getAsString();
		
		String studyDate=studyDetails.get("StudyDate").getAsString();
		Date studyDateObject=null;
		try {
			studyDateObject=format.parse("19000101");
			studyDateObject=format.parse(studyDate);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String studyDescription="N/A";
		if(studyDetails.has("StudyDescription")){
			studyDescription=studyDetails.get("StudyDescription").getAsString();
		}
		
		JsonObject patientDetails = (JsonObject) studyData.get("PatientMainDicomTags");
		
		String patientName="N/A";
		if(patientDetails.has("PatientName")) {
			patientName=patientDetails.get("PatientName").getAsString();
		}
		
		String patientId="N/A";
		if(patientDetails.has("PatientID")) {
			patientId=patientDetails.get("PatientID").getAsString();
		}
		
		String patientSex="N/A";
		if(patientDetails.has("PatientSex")) {
			patientSex=patientDetails.get("PatientSex").getAsString();
		}
		
		Date patientDob=null;
		if(patientDetails.has("PatientBirthDate")) {
			try {
				patientDob = format.parse("19000101");
				patientDob=format.parse(patientDetails.get("PatientBirthDate").getAsString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		
		String patientOrthancID=studyData.get("ParentPatient").getAsString();
		
		Study2 studyObject=new Study2(studyDescription, studyDateObject, accessionNumber, studyOrthancID, patientName, patientId, patientDob, patientSex, patientOrthancID,studyInstanceUid, null);
		
		return studyObject;
	}

	
}
