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

package org.petctviewer.orthanc.modify;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class Modify {
	
	private Modify_Gui gui;
	private String levelUrl;
	private String id;
	private JsonArray seriesInstancesID;
	private OrthancRestApis connexion;
	private VueAnon guiParent;
	private JsonParser parser;
	
	public Modify(String level, String id, VueAnon guiParent, OrthancRestApis connexion){
		this.connexion= connexion;
		parser=new JsonParser();
		gui = new Modify_Gui(this, guiParent);
		this.id=id;
		this.guiParent=guiParent;

		setUrlAndFetch(level);

	}
	
	private void setUrlAndFetch(String level) {
		
		if (level.equals("series")){
			levelUrl="/series/";
			getSeriesTags(id);
			//Open GUI and enable instance button
			
		}
		else if (level.equals("studies")) {
			levelUrl="/studies/";
			getStudiesTags(id);
			//Open GUI and disable instance button because level is too high
			
		}
		else if (level.equals("patients")) {
			levelUrl="/patients/";
			getPatientsTags(id);
			
			
		}
		//On ouvre la GUI
		gui.setSize(800,750);
		gui.hideTables(level);
		gui.setVisible(true);
	}
	
	/**
	 * Retrieve and set Patients level main tags in the gui
	 * @param patientID
	 * @throws IOException
	 * @throws ParseException
	 */
	private void getPatientsTags(String patientID) {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/patients/"+patientID);
		JsonObject response=parser.parse(sb.toString()).getAsJsonObject();
		JsonObject patientsMainTags=response.get("MainDicomTags").getAsJsonObject();
		gui.setTables(patientsMainTags, "patient");
	}
	
	
	/**
	 * Retrieve and set Series tags and call set Study/Patients main tags in the GUI
	 * @param seriesID
	 * @throws IOException
	 * @throws ParseException
	 */
	private void getSeriesTags(String seriesID) {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/series/"+seriesID);
		JsonObject response=parser.parse(sb.toString()).getAsJsonObject();
		JsonObject seriesMainTags=response.get("MainDicomTags").getAsJsonObject();
		String parentStudyID=response.get("ParentStudy").getAsString();
		seriesInstancesID= response.get("Instances").getAsJsonArray();
		gui.setTables(seriesMainTags, "serie");
		getStudiesTags(parentStudyID);
		
	}
	/**
	 * Retrieve and set study/Patient main tags in the gui
	 * @param studyID
	 * @throws IOException
	 * @throws ParseException
	 */
	private void getStudiesTags(String studyID) {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/studies/"+studyID);
		JsonObject response=parser.parse(sb.toString()).getAsJsonObject();
		JsonObject studyMainTags=response.get("MainDicomTags").getAsJsonObject();
		JsonObject patientMainTags=response.get("PatientMainDicomTags").getAsJsonObject();
		gui.setTables(studyMainTags, "study");
		gui.setTables(patientMainTags, "patient");
		
	}
	
	/**
	 * get Shared tag of the level to be parsed in the gui
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public JsonObject getSharedTags() {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder(levelUrl+id+"/shared-tags");
		JsonObject response=parser.parse(sb.toString()).getAsJsonObject();
		return response;
	}
	
	/**
	 * get the full tags of a specific instnce
	 * @param instance
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public JsonObject getInstanceTags(int instance) {
		JsonObject responseInstance = null;
		if (instance<seriesInstancesID.size()) {
			String idInstance=seriesInstancesID.get(instance).getAsString();
			StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/instances/"+idInstance+"/tags");
			responseInstance=parser.parse(sb.toString()).getAsJsonObject();
		}
		else {
			JOptionPane.showMessageDialog(null, "Series contains "+seriesInstancesID.size()+" Instances (index start a 0)");
		}
		
		return responseInstance;
	}
	
	/**
	 * Build the final modify query and send it to Orthanc
	 * @param replaceTags
	 * @param removeTags
	 * @param removePrivateTags
	 */
	public JsonObject buildModifyQuery(JsonObject replaceTags, JsonArray removeTags, boolean removePrivateTags) {

		JsonObject modifyRequest=new JsonObject();
		modifyRequest.add("Replace", replaceTags);
		modifyRequest.add("Remove", removeTags);
		modifyRequest.addProperty("RemovePrivateTags", removePrivateTags);
		
		//JsonPrimitives:
		JsonElement jsonPatientID=new JsonPrimitive("PatientID");
		JsonElement jsonStudyInstanceUID=new JsonPrimitive("StudyInstanceUID");
		JsonElement jsonSeriesInstanceUID=new JsonPrimitive("SeriesInstanceUID");
		JsonElement jsonSOPInstanceUID=new JsonPrimitive("SOPInstanceUID");
		
		
		if (replaceTags.has("PatientID") || replaceTags.has("StudyInstanceUID") || replaceTags.has("SeriesInstanceUID") || replaceTags.has( "SOPInstanceUID" ) || removeTags.contains(jsonPatientID) || removeTags.contains(jsonStudyInstanceUID) || removeTags.contains(jsonSeriesInstanceUID) || removeTags.contains(jsonSOPInstanceUID) ) {
            modifyRequest.addProperty("Force", Boolean.TRUE);
		}
		
		if (levelUrl.contains("patients") && !replaceTags.has("PatientID") || removeTags.contains(jsonPatientID) ) {
			JOptionPane.showMessageDialog(null, "For Patient edition, PatientID must be set to a new value, please edit it");
			modifyRequest=null;
		}

		return modifyRequest;

	}
	
	public void sendQuery(JsonObject query, boolean deleteOriginal) throws Exception {
		StringBuilder sb=connexion.makePostConnectionAndStringBuilder(this.levelUrl+this.id+"/modify", query.toString());
		if (sb==null) {
			throw new Exception("Not Allowed");
		}
		if(deleteOriginal) {
			connexion.makeDeleteConnection(this.levelUrl+this.id);
		}
		
	}
	
	public void refreshTable() {
		
		if (levelUrl.equals("/series/")){
		
			guiParent.modeleSeries.refresh();
			
		} else if (levelUrl.equals("/studies/")) {
			
			guiParent.modeleStudies.refresh();
			
		} else if (levelUrl.equals("/patients/")) {
			guiParent.getSearchButton().doClick();
			
		}
	}
	
}
