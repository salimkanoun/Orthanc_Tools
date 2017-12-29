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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.petctviewer.*;

public class Modify {
	
	private Modify_Gui gui;
	private String levelUrl;
	private String id;
	private JSONArray seriesInstancesID;
	private ParametreConnexionHttp connexion;
	private JSONParser parser=new JSONParser();
	
	public Modify(String level, String id, JFrame guiParent, ParametreConnexionHttp connexion, JLabel state){
		this.connexion= connexion;
		gui = new Modify_Gui(this, guiParent, state);
		this.id=id;
		try {
			setUrlAndFetch(level);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} 
	}
	
	private void setUrlAndFetch(String level) throws IOException, ParseException {
		
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
	public void getPatientsTags(String patientID) throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/patients/"+patientID);
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		JSONObject patientsMainTags=(JSONObject) response.get("MainDicomTags");
		gui.setTables(patientsMainTags, "patient");
	}
	
	
	/**
	 * Retrieve and set Series tags and call set Study/Patients main tags in the GUI
	 * @param seriesID
	 * @throws IOException
	 * @throws ParseException
	 */
	public void getSeriesTags(String seriesID) throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/series/"+seriesID);
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		JSONObject seriesMainTags=(JSONObject) response.get("MainDicomTags");
		String parentStudyID=(String) response.get("ParentStudy");
		seriesInstancesID= (JSONArray) response.get("Instances");
		System.out.println(seriesMainTags);
		gui.setTables(seriesMainTags, "serie");
		getStudiesTags(parentStudyID);
		
	}
	/**
	 * Retrieve and set study/Patient main tags in the gui
	 * @param studyID
	 * @throws IOException
	 * @throws ParseException
	 */
	public void getStudiesTags(String studyID) throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/studies/"+studyID);
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		JSONObject studyMainTags=(JSONObject) response.get("MainDicomTags");
		JSONObject patientMainTags=(JSONObject) response.get("PatientMainDicomTags");
		gui.setTables(studyMainTags, "study");
		gui.setTables(patientMainTags, "patient");
		
	}
	
	/**
	 * get Shared tag of the level to be parsed in the gui
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public JSONObject getSharedTags() throws IOException, ParseException{
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder(levelUrl+id+"/shared-tags");
		JSONObject response=(JSONObject) parser.parse(sb.toString());
		return response;
	}
	
	/**
	 * get the full tags of a specific instnce
	 * @param instance
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
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
	
	/**
	 * Build the final modify query and send it to Orthanc
	 * @param replaceTags
	 * @param removeTags
	 * @param removePrivateTags
	 */
	@SuppressWarnings("unchecked")
	public void sendModifyQuery(JSONObject replaceTags, JSONArray removeTags, boolean removePrivateTags) {
		boolean cancel=false;
		
		JSONObject modifyRequest=new JSONObject();
		modifyRequest.put("Replace", replaceTags);
		modifyRequest.put("Remove", removeTags);
		modifyRequest.put("RemovePrivateTags", removePrivateTags);
		
		System.out.println(modifyRequest.toString());
		
		if (replaceTags.containsKey("PatientID") || replaceTags.containsKey("StudyInstanceUID") || replaceTags.containsKey("SeriesInstanceUID") || replaceTags.containsKey( "SOPInstanceUID" ) || removeTags.contains("PatientID") || removeTags.contains("StudyInstanceUID") || removeTags.contains("SeriesInstanceUID") || removeTags.contains( "SOPInstanceUID" ) ) {
            int response=JOptionPane.showConfirmDialog (null, "You are modifying key idenditifaction patients (Patient ID...) would you can to continue ?","Warning",JOptionPane.YES_NO_OPTION);
            if (response==JOptionPane.NO_OPTION) cancel=true;
            else modifyRequest.put("Force", Boolean.TRUE);
		}
		if (!cancel) {
			try {
			connexion.makePostConnectionAndStringBuilder(this.levelUrl+this.id+"/modify", modifyRequest.toString());
			} catch (IOException e) {e.printStackTrace();}
		}

	}
	
}
