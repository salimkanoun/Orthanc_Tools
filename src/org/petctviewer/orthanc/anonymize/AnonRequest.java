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

import java.util.ArrayList;

import org.petctviewer.orthanc.anonymize.Tags.Choice;
import org.petctviewer.orthanc.setup.ParametreConnexionHttp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AnonRequest {

	private ArrayList<Tags> tags = new ArrayList<Tags>();
	private boolean keepPrivateTags;
	private String newUID;
	private String newPatientUID;
	private String newPatientName;
	private String newPatientID;
	
	private ParametreConnexionHttp connexionHttp;

	public AnonRequest(ParametreConnexionHttp connexionHttp, Choice bodyChar, Choice dates, Choice birthdate, 
			Choice privateTags, Choice secondaryCapture, Choice descriptionStudySerie, 
			String newPatientName, String newPatientID, String newDescription) {

		this.connexionHttp=connexionHttp;
		this.newPatientName = newPatientName;
		this.newPatientID = newPatientID;

		// Add date related tags with defined choice (keep or clear)
		tags.add(new Tags("0008,0022",dates,null)); // Acquisition Date
		tags.add(new Tags("0008,002A",dates,null)); // Acquisition DateTime
		tags.add(new Tags("0008,0032",dates,null)); // Acquisition Time
		tags.add(new Tags("0038,0020",dates,null)); // Admitting Date
		tags.add(new Tags("0038,0021",dates,null)); // Admitting Time
		tags.add(new Tags("0008,0035",dates,null)); // Curve Time
		tags.add(new Tags("0008,0025",dates,null)); // Curve Date
		tags.add(new Tags("0008,0023",dates,null)); // Content Date
		tags.add(new Tags("0008,0033",dates,null)); // Content Time
		tags.add(new Tags("0008,0024",dates,null)); // Overlay Date
		tags.add(new Tags("0008,0034",dates,null)); // Overlay Time
		tags.add(new Tags("0040,0244",dates,null)); // ...Start Date
		tags.add(new Tags("0040,0245",dates,null)); // ...Start Time
		tags.add(new Tags("0008,0021",dates,null)); // Series Date
		tags.add(new Tags("0008,0031",dates,null)); // Series Time
		tags.add(new Tags("0008,0020",dates,null)); // Study Date
		tags.add(new Tags("0008,0030",dates,null)); // Study Time
		tags.add(new Tags("0010,21D0",dates,null)); // Last menstrual date
		tags.add(new Tags("0008,0201",dates,null)); // Timezone offset from UTC
		tags.add(new Tags("0040,0002",dates,null)); // Scheduled procedure step start date
		tags.add(new Tags("0040,0003",dates,null)); // Scheduled procedure step start time
		tags.add(new Tags("0040,0004",dates,null)); // Scheduled procedure step end date
		tags.add(new Tags("0040,0005",dates,null)); // Scheduled procedure step end time
		// same for Body characteristics
		tags.add(new Tags("0010,2160",bodyChar,null)); // Patient's ethnic group
		tags.add(new Tags("0010,21A0",bodyChar,null)); // Patient's smoking status
		tags.add(new Tags("0010,0040",bodyChar,null)); // Patient's sex
		tags.add(new Tags("0010,2203",bodyChar,null)); // Patient's sex neutered
		tags.add(new Tags("0010,1010",bodyChar,null)); // Patient's age
		tags.add(new Tags("0010,21C0",bodyChar,null)); // Patient's pregnancy status
		tags.add(new Tags("0010,1020",bodyChar,null)); // Patient's size
		tags.add(new Tags("0010,1030",bodyChar,null)); // Patient's weight
		// Other tags
		tags.add(new Tags("0008,0050", Choice.REPLACE, "petctviewer.org")); // Accession Number hardcoded to our website
		tags.add(new Tags("0010,0020", Choice.REPLACE, newPatientID)); // Patient ID
		tags.add(new Tags("0010,0010", Choice.REPLACE, newPatientName)); //Patient's name
		tags.add(new Tags("0008,103E", descriptionStudySerie, null)); // Serie's description
		
		//For simplicity Study and Series descriptions are linked. So A keep value is meaning a 
		//Replace for study description
		if(descriptionStudySerie.equals(Choice.KEEP)){
			tags.add(new Tags("0008,1030", Choice.REPLACE, newDescription));
		}else if (descriptionStudySerie.equals(Choice.CLEAR)){
			tags.add(new Tags("0008,1030", Choice.CLEAR,null));
		}
		
		//BirthDate
		if(birthdate.equals(Choice.REPLACE)){
			tags.add(new Tags("0010,0030", birthdate, "19000101")); // Patient's birth date
		}else if (birthdate.equals(Choice.KEEP)){
			tags.add(new Tags("0010,0030", birthdate,null)); // Patient's birth date
		}
		
		// Keep some Private tags usefull for PET/CT or Scintigraphy
		tags.add(new Tags("7053,1000", Choice.KEEP,null)); // Philips
		tags.add(new Tags("7053,1009", Choice.KEEP,null)); // Philips
		tags.add(new Tags("0009,103b", Choice.KEEP,null)); // GE
		tags.add(new Tags("0009,100d", Choice.KEEP,null)); // GE
		tags.add(new Tags("0011,1012", Choice.KEEP,null));
		
		if (!this.connexionHttp.getIfVersionAfter131()) {
				tags.add(new Tags("0020,0052", Choice.KEEP, null)); // Frame of Reference UID
				System.out.println("Old Version of Orthanc, Frame Of Reference UID not modified, Please upgrade your Orthanc Server");
		}

		if(privateTags==Choice.KEEP) {
			this.keepPrivateTags=true;
		}else if(privateTags==Choice.CLEAR) {
			this.keepPrivateTags=false;
		}
	}

	

	/**
	 * Send the Anonymization request (only study level) and get the new anonymized study
	 * @param level
	 * @param id
	 * @throws IOException
	 */
	public void sendQuery(String id) {
		String query=buildQuery();		
		StringBuilder sb=connexionHttp.makePostConnectionAndStringBuilder("/studies/" + id +"/anonymize", query);
		JsonParser parser =new JsonParser();
		JsonObject answer=(JsonObject) parser.parse(sb.toString());
		newUID=answer.get("ID").getAsString();
		newPatientUID=answer.get("PatientID").getAsString();
	}
	
	private String buildQuery(){
		
		JsonObject query = new JsonObject();
		JsonObject replace = new JsonObject();
		JsonArray keep = new JsonArray();
		
		for(Tags tag : tags){
			if(tag.getChoice()==Choice.KEEP) {
				keep.add(tag.getCode());
			}else if(tag.getChoice()==Choice.REPLACE) {
				replace.addProperty(tag.getCode(), tag.getReplaceValue());
			}
		}

		query.addProperty("KeepPrivateTags", keepPrivateTags);
		query.addProperty("Force", true);
		query.add("Replace", replace);
		query.add("Keep", keep);
		
		return query.toString();
		
	}
	
	/**
	 * Getters of new anonymized dicom
	 * 
	 */
	
	public String getNewPatientUID(){
		return this.newPatientUID;
	}

	public String getNewUID(){
		return this.newUID;
	}
	
	public String getNewPatientName(){
		return this.newPatientName;
	}
	
	public String getNewNewPatientID(){
		return this.newPatientID;
	}

}
