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
