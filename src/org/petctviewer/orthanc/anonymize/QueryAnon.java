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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.ParametreConnexionHttp;
import org.petctviewer.orthanc.anonymize.Tags.Choice;

public class QueryAnon {

	//private static final int NBTAGS = 40	;
	private JSONParser parser=new JSONParser();
	private ArrayList<Tags> tags = new ArrayList<Tags>();
	private Choice privateTags;
	private String query;
	private String newUID;
	private String newPatientUID;
	private String newPatientName;
	private String newPatientID;
	
	private ParametreConnexionHttp connexionHttp;

	public QueryAnon(ParametreConnexionHttp connexionHttp, Choice bodyChar, Choice dates, Choice birthdate, 
			Choice privateTags, Choice secondaryCapture, Choice descriptionSerie, 
			String newPatientName, String newPatientID, String newDescription) throws IOException{

		this.connexionHttp=connexionHttp;
		this.newPatientName = newPatientName;
		this.newPatientID = newPatientID;

		// DATE
		tags.add(new Tags("0008,0022",dates)); // Acquisition Date
		tags.add(new Tags("0008,002A",dates)); // Acquisition DateTime
		tags.add(new Tags("0008,0032",dates)); // Acquisition Time
		tags.add(new Tags("0038,0020",dates)); // Admitting Date
		tags.add(new Tags("0038,0021",dates)); // Admitting Time
		tags.add(new Tags("0008,0035",dates)); // Curve Time
		tags.add(new Tags("0008,0025",dates)); // Curve Date
		tags.add(new Tags("0008,0023",dates)); // Content Date
		tags.add(new Tags("0008,0033",dates)); // Content Time
		tags.add(new Tags("0008,0024",dates)); // Overlay Date
		tags.add(new Tags("0008,0034",dates)); // Overlay Time
		tags.add(new Tags("0040,0244",dates)); // ...Start Date
		tags.add(new Tags("0040,0245",dates)); // ...Start Time
		tags.add(new Tags("0008,0021",dates)); // Series Date
		tags.add(new Tags("0008,0031",dates)); // Series Time
		tags.add(new Tags("0008,0020",dates)); // Study Date
		tags.add(new Tags("0008,0030",dates)); // Study Time
		tags.add(new Tags("0010,21D0",dates)); // Last menstrual date
		tags.add(new Tags("0008,0201",dates)); // Timezone offset from UTC
		tags.add(new Tags("0040,0002",dates)); // Scheduled procedure step start date
		tags.add(new Tags("0040,0003",dates)); // Scheduled procedure step start time
		tags.add(new Tags("0040,0004",dates)); // Scheduled procedure step end date
		tags.add(new Tags("0040,0005",dates)); // Scheduled procedure step end time
		// Body characteristics
		tags.add(new Tags("0010,2160",bodyChar)); // Patient's ethnic group
		tags.add(new Tags("0010,21A0",bodyChar)); // Patient's smoking status
		tags.add(new Tags("0010,0040",bodyChar)); // Patient's sex
		tags.add(new Tags("0010,2203",bodyChar)); // Patient's sex neutered
		tags.add(new Tags("0010,1010",bodyChar)); // Patient's age
		tags.add(new Tags("0010,21C0",bodyChar)); // Patient's pregnancy status
		tags.add(new Tags("0010,1020", bodyChar)); // Patient's size
		tags.add(new Tags("0010,1030", bodyChar)); // Patient's weight
		// Other tags
		tags.add(new Tags("0008,0050\":\"petctviewer.org", Choice.REPLACE)); // Accession N
		tags.add(new Tags("0010,0020\":\"" + this.newPatientID, Choice.REPLACE)); // Patient ID
		tags.add(new Tags("0010,0010\":\"" + this.newPatientName, Choice.REPLACE)); //Patient's name
		tags.add(new Tags("0008,103E", descriptionSerie)); // Serie's description /!\ IL FAUT DONNER LE CHOIX DE MODIFY
		if(descriptionSerie.equals(Choice.KEEP)){
			tags.add(new Tags("0008,1030\":\"" + newDescription, Choice.REPLACE));
			if( newDescription == null ||newDescription.equals("")){
				tags.add(new Tags("0008,1030", Choice.KEEP));
			}
		}else{
			tags.add(new Tags("0008,1030", Choice.CLEAR));
		}
		if(birthdate.equals(Choice.REPLACE)){
			tags.add(new Tags("0010,0030\":\"19000101", birthdate)); // Patient's birth date
		}else{
			tags.add(new Tags("0010,0030", birthdate)); // Patient's birth date
		}
		// Private tags
		tags.add(new Tags("7053,1000", Choice.KEEP)); // Philips
		tags.add(new Tags("7053,1009", Choice.KEEP)); // Philips
		tags.add(new Tags("0009,103b", Choice.KEEP)); // GE
		tags.add(new Tags("0009,100d", Choice.KEEP)); // GE
		tags.add(new Tags("0011,1012", Choice.KEEP));
		
		if (!this.connexionHttp.getIfVersionAfter131()) {
				tags.add(new Tags("0020,0052", Choice.KEEP)); // Frame of Reference UID
				System.out.println("Old Version of Orthanc, Frame Of Reference UID not modified");
		}

		this.privateTags = privateTags;
	}

	public void setQuery(){
		this.query = null;
		StringBuilder replace = new StringBuilder();
		replace.append("\"Replace\":{");
		StringBuilder keep = new StringBuilder();
		keep.append("\"Keep\":[");
		if(tags != null){
			for(Tags t : tags){
				if(t != null){
					switch (t.getChoice()) {
					case REPLACE:
					{
						replace.append("\"" + t.getCode() +"\",");
					}
					break;
					case KEEP:
						keep.append("\"" + t.getCode() +"\",");
						break;
					default:
						break;
					}
				}
			}
			// We remove the ',' at the end of the strings
			if(replace.toString().charAt(replace.toString().length() - 1) == ','){
				replace.deleteCharAt(replace.toString().length() - 1);
			}
			replace.append("},");
			if(keep.toString().charAt(keep.toString().length() - 1) == ','){
				keep.deleteCharAt(keep.toString().length() - 1);
			}keep.append("]");
			if(this.privateTags.equals(Choice.KEEP)){
				this.query = "{" + replace.toString() + keep.toString() + ",\"KeepPrivateTags\": true" + ",\"Force\": true" + "}";
			}else{
				this.query = "{" + replace.toString() + keep.toString() + ",\"Force\": true" + "}";
			}
		}
	}

	/**
	 * Send the Anonymization request
	 * @param level
	 * @param id
	 * @throws IOException
	 */
	public void sendQuery(String level, String id) throws IOException{
		this.setQuery();
		HttpURLConnection conn=connexionHttp.makePostConnection("/"+level + "/" + id +"/anonymize", this.query);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		
		// We get the study ID at the end
		StringBuilder sb = new StringBuilder();
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		conn.disconnect();
		
		JSONObject response = null;
		try {
			response = (JSONObject) parser.parse(sb.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String newStudyID=(String) response.get("ID");
		String newPatientID=(String) response.get("PatientID");

		this.newUID=newStudyID;
		this.newPatientUID=newPatientID;
		
	}
	
	/* 
	 * This method gets the currently anonymized study's new patient ID
	 */
	public String getNewPatientUID(){
		return this.newPatientUID;
	}
	
	/* 
	 * This method gets the currently anonymized study's new ID
	 */
	public String getNewUID(){
		return this.newUID;
	}

}
