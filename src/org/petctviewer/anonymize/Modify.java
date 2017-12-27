
package org.petctviewer.anonymize;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.petctviewer.*;

public class Modify {
	testgui gui=new testgui(this, true);
	String level;
	String id;
	
	String url = "/series/ec1c4786-99dc6c3e-4ca9cd5d-a1bc96f3-b240c19e";
	String urlStudy = "/studies/";
	
	private JSONArray seriesInstancesID;
	ParametreConnexionHttp connexion;
	JSONParser parser=new JSONParser();
	
	public Modify(/*ParametreConnexionHttp connexion, String level, String id*/){
		this.connexion= new ParametreConnexionHttp();
		
	}
	public void getPatientsTags() throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder(url);
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		JSONObject patientsMainTags=(JSONObject) response.get("MainDicomTags");
		gui.setTables(patientsMainTags, "patient");
	}
	
	public void getSeriesTags() throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder(url);
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		JSONObject seriesMainTags=(JSONObject) response.get("MainDicomTags");
		String parentStudyID=(String) response.get("ParentStudy");
		urlStudy+=parentStudyID;
		seriesInstancesID= (JSONArray) response.get("Instances");
		gui.setTables(seriesMainTags, "serie");
		getStudiesTags();
		
	}
	
	public void getStudiesTags() throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder(urlStudy);
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		JSONObject studyMainTags=(JSONObject) response.get("MainDicomTags");
		JSONObject patientMainTags=(JSONObject) response.get("PatientMainDicomTags");
		gui.setTables(studyMainTags, "study");
		gui.setTables(patientMainTags, "patient");
		gui.setSize(800,750);
		gui.setVisible(true);
	}
	
	public JSONObject getSharedTags() throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder(url+"/shared-tags");
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		return response;
	}
	
	public JSONObject getInstanceTags(int instance) throws IOException, ParseException{
		String idInstance=(String) seriesInstancesID.get(instance);
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/instances/"+idInstance+"/tags");
		System.out.println(sb.toString());
		//A FAIRE VOIR POUR L AFFICHAGE DE TOUTE LA REPONSE AVEC ELEMENTS IMBRIQUES
		return null;
	}
	
	public  static void main(String...args){
		Modify modify=new Modify();
		try {
			modify.getSeriesTags();
			//modify.getStudiesTags();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
