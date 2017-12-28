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

package org.petctviewer.anonymize;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.petctviewer.*;

public class Modify {
	testgui gui=new testgui(this, true);
	String level;
	String levelUrl;
	String id;
	
	private JSONArray seriesInstancesID;
	ParametreConnexionHttp connexion;
	JSONParser parser=new JSONParser();
	
	public Modify(String level, String id){
		this.connexion= new ParametreConnexionHttp();
		this.level=level;
		this.id=id;
		try {
			setUrlAndFetch();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} 
	}
	
	private void setUrlAndFetch() throws IOException, ParseException {
		if (level.equals("series")){
			levelUrl="/series/";
			getSeriesTags(id);
		}
		else if (level.equals("studies")) {
			levelUrl="/studies/";
			getStudiesTags(id);
		}
		else if (level.equals("patients")) {
			levelUrl="/patients/";
			getPatientsTags(id);
		}
		//On onvre la GUI
		gui.hideTables(level);
		gui.setSize(800,750);
		gui.setVisible(true);
	}
	
	public void getPatientsTags(String patientID) throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/patients/"+patientID);
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		JSONObject patientsMainTags=(JSONObject) response.get("MainDicomTags");
		gui.setTables(patientsMainTags, "patient");
	}
	
	public void getSeriesTags(String seriesID) throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/series/"+seriesID);
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		JSONObject seriesMainTags=(JSONObject) response.get("MainDicomTags");
		String parentStudyID=(String) response.get("ParentStudy");
		seriesInstancesID= (JSONArray) response.get("Instances");
		gui.setTables(seriesMainTags, "serie");
		getStudiesTags(parentStudyID);
		
	}
	
	public void getStudiesTags(String studyID) throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/studies/"+studyID);
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		JSONObject studyMainTags=(JSONObject) response.get("MainDicomTags");
		JSONObject patientMainTags=(JSONObject) response.get("PatientMainDicomTags");
		gui.setTables(studyMainTags, "study");
		gui.setTables(patientMainTags, "patient");
		
	}
	
	
	public JSONObject getSharedTags() throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder(levelUrl+id+"/shared-tags");
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		return response;
	}
	
	public JSONObject getInstanceTags(int instance) throws IOException, ParseException{
		JSONObject responseInstance = null;
		if (instance<seriesInstancesID.size()) {
			String idInstance=(String) seriesInstancesID.get(instance);
			StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/instances/"+idInstance+"/tags");
			responseInstance=(JSONObject) parser.parse(sb.toString());
		}
		else {
			JOptionPane.showMessageDialog(null, "Series contains "+seriesInstancesID.size()+" Instances (index start a 0)");
		}
		
		return responseInstance;
	}
	
	@SuppressWarnings("unchecked")
	public void sendModifyQuery(JSONObject replaceTags, JSONArray removeTags, boolean removePrivateTags) {
		JSONObject modifyRequest=new JSONObject();
		modifyRequest.put("Replace", replaceTags);
		modifyRequest.put("Remove", removeTags);
		modifyRequest.put("RemovePrivateTags", removePrivateTags);
		if (replaceTags.containsKey("PatientID") || replaceTags.containsKey("StudyInstanceUID") || replaceTags.containsKey("SeriesInstanceUID") || replaceTags.containsKey( "SOPInstanceUID" ) || removeTags.contains("PatientID") || removeTags.contains("StudyInstanceUID") || removeTags.contains("SeriesInstanceUID") || removeTags.contains( "SOPInstanceUID" ) ) {
		modifyRequest.put("Force", true);
		}
		System.out.println(modifyRequest.toString());
		try {
			StringBuilder response=connexion.makePostConnectionAndStringBuilder(this.levelUrl+this.id+"/modify", modifyRequest.toString());
			System.out.println(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//SK A FAIRE
		// AJOUTER SI BESOIN LE FORCE AVEC WARNING ?
		
	}
	
	public  static void main(String...args){
		new Modify("studies","d3a60cca-e907c064-cec0146b-e7b20d92-767fe06d");
		
	}
}
