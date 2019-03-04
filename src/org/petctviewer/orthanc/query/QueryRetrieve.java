/**
Copyright (C) 2017 VONGSALAT Anousone

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


package org.petctviewer.orthanc.query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class QueryRetrieve {

	private OrthancRestApis connexion;
	private JsonParser parserJson = new JsonParser();

	public QueryRetrieve(OrthancRestApis connexion){
		this.connexion=connexion;
	}

	/*
	 * This method gets the answer's indexes to an Orthanc query, as an Object[].
	 * An Object[] should be instantiated to store the values inside it.
	 */
	public StudyDetails[] getPatientsResults(String level, String name, String id, String studyDate, String modality, String studyDescription, String accessionNumber, String aet) {
		// We call getQueryID to generate a query ID
		String idQuery =  this.queryStudies(level, name, id, studyDate, modality, studyDescription, accessionNumber, aet);
		
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/queries/" + idQuery + "/answers/");
		
		JsonArray answers=(JsonArray) parserJson.parse(sb.toString());
		
		StudyDetails[] patients=new StudyDetails[answers.size()];
		DateFormat dateParser = new SimpleDateFormat("yyyyMMdd");
		
		for (int i=0; i<answers.size(); i++) {
			
			int answer=answers.get(i).getAsInt();
			String indexContent = getIndexContent(idQuery,answer);
			JsonObject contentJson= (JsonObject) parserJson.parse(indexContent);

			String patientName=contentJson.get("0010,0010").getAsJsonObject().get("Value").getAsString();
			String patientID=contentJson.get("0010,0020").getAsJsonObject().get("Value").getAsString();
			String studyInstanceUID=contentJson.get("0020,000d").getAsJsonObject().get("Value").getAsString();
			String studyDate2=contentJson.get("0008,0020").getAsJsonObject().get("Value").getAsString();
			String accessionNumber2=contentJson.get("0008,0050").getAsJsonObject().get("Value").getAsString();
			String studyDescription2=contentJson.get("0008,1030").getAsJsonObject().get("Value").getAsString();
			
			Date studyDateParsed = null;
			try {
				studyDateParsed = dateParser.parse(studyDate2);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			
			patients[i] = new StudyDetails(patientName, patientID, studyDateParsed, studyDescription2, accessionNumber2, studyInstanceUID, aet, idQuery,answer);
			
		}
		
		return patients;
	}

	/*
	 * This method returns all the series's data of a queries ID result in a SeriesDetails array
	 *
	 */
	public SerieDetails[] getSeriesAnswers(String studyInstanceUID, String aet) {
		
		String idQuery=querySeries(studyInstanceUID, aet);
		
		SerieDetails[] seriesDetails=null;
		
		StringBuilder sb = connexion.makeGetConnectionAndStringBuilder("/queries/" + idQuery + "/answers/");
		JsonArray answersID=(JsonArray) parserJson.parse(sb.toString());
		
		if(answersID.size() == 0){
		 return null;
		}
		
		seriesDetails = new SerieDetails[answersID.size()];
		
		for(int i = 0; i < answersID.size(); i++){
			
			String answer=getIndexContent(idQuery, i);
			JsonObject contentJson= (JsonObject) parserJson.parse(answer);
			
			String seriesDescriptions, modality, number;
		
			if (contentJson.has("0008,103e")) {
				JsonObject serieDescriptionJson=contentJson.get("0008,103e").getAsJsonObject();
				seriesDescriptions=serieDescriptionJson.get("Value").getAsString();	
			} else {
				seriesDescriptions="";	
			}
			
			if (contentJson.has("0008,0060")) {
				JsonObject modalityJson=contentJson.get("0008,0060").getAsJsonObject();
				modality=modalityJson.get("Value").getAsString();
			}else {
				modality="";
			}
			
			if (contentJson.has("0020,0011")) {
				JsonObject serieNumberJson=contentJson.get("0020,0011").getAsJsonObject();
				number=serieNumberJson.get("Value").getAsString();
			}else {
				number="";
			}
			
			seriesDetails[i]= new SerieDetails(seriesDescriptions,modality, studyInstanceUID, number,aet, idQuery, i );
			
			
		}
			
		return seriesDetails;
	}

	/*
	 * This method retrieves an query answer, depending on its query ID / number 
	 */
	public void retrieve(String queryID, int answer, String retrieveAET) throws Exception {
		StringBuilder sb=connexion.makePostConnectionAndStringBuilder("/queries/" + queryID + "/answers/" + answer + "/retrieve/", retrieveAET);
		if(sb==null) {
			throw new Exception("Retrieved Failed");
		}
	}
	
	/*
	 * This method returns the content of a specified index.
	 * The index is obtained by using the getQueryAnswerIndexes.
	 */
	private String getIndexContent(String queryId, int index) {
		String content = null;
		content = connexion.makeGetConnectionAndStringBuilder( "/queries/" + queryId + "/answers/" + index + "/content/").toString();
		return content;
	}
	
	/*
	 * This method returns the series's descriptions's ID.
	 * It is treated separately because we only need the sole series's descriptions here. 
	 */
	private String querySeries(String studyInstanceUID, String aet) {
		// getting the query ID
		JsonObject query = new JsonObject();
		query.addProperty("Level", "Series");
		
		JsonObject queryDetails = new JsonObject();
		queryDetails.addProperty("Modality", "*");
		queryDetails.addProperty("ProtocolName", "*");
		queryDetails.addProperty("SeriesDescription", "*");
		queryDetails.addProperty("StudyInstanceUID", "*");
		queryDetails.addProperty("StudyInstanceUID", studyInstanceUID);
		
		query.add("Query", queryDetails);
		
		StringBuilder sb=connexion.makePostConnectionAndStringBuilder("/modalities/" + aet + "/query/", query.toString());
		JsonObject answer=(JsonObject) parserJson.parse(sb.toString());
		String idURL=answer.get("ID").getAsString();
		
		return idURL;
	}

	
	/**
	 * Send Query and return it's query ID to fetch it's results
	 * @param level
	 * @param name
	 * @param id
	 * @param studyDate
	 * @param modality
	 * @param studyDescription
	 * @param accessionNumber
	 * @param aet
	 * @return String QueryID
	 */
	private String queryStudies(String level, String name, String id, String studyDate, String modality, String studyDescription, String accessionNumber, String aet) {
		// We build the query
		String query =buildQuery(level, name, id, studyDate, modality, studyDescription, accessionNumber);
		String ID = null;
		//Send the query to Orthanc and get it's ID to fetch answers
		StringBuilder response=connexion.makePostConnectionAndStringBuilder("/modalities/" + aet + "/query/", query);
		JsonObject answer = (JsonObject) parserJson.parse(response.toString());
		ID=answer.get("ID").getAsString();
		
		return ID;
	}
	

	private String buildQuery(String level, String name, String id, String studyDate, String modality, String studyDescription, String accessionNumber) {
		JsonObject query = new JsonObject();
		query.addProperty("Level", level);
		
		JsonObject queryDetails = new JsonObject();
		queryDetails.addProperty("PatientName", name);
		queryDetails.addProperty("PatientID", id);
		queryDetails.addProperty("StudyDate", studyDate);
		queryDetails.addProperty("ModalitiesInStudy", modality);
		queryDetails.addProperty("StudyDescription", studyDescription);
		queryDetails.addProperty("AccessionNumber", accessionNumber);
		
		query.add("Query", queryDetails);
		
		return query.toString();
	}
	
	/**
	 * Get available distant AETs in Orthanc
	 * @return String
	 */
	public String[] getAets() {
		return connexion.getAET();
	}
	
	/**
	 * Get Local AET of this Orthanc Server
	 * @return String
	 */
	public String getLocalAet() {
		return connexion.getLocalAET();
	}

}
