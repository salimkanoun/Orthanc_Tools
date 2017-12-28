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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.petctviewer.*;

public class QueryAnon {

	private static final int NBTAGS = 40	;
	private Tags[] tags = new Tags[NBTAGS];
	private Choice privateTags;
	private String query;
	private String serieUID;
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
		this.tags[0] = new Tags("0008,0022",dates); // Acquisition Date
		this.tags[1] = new Tags("0008,002A",dates); // Acquisition DateTime
		this.tags[2] = new Tags("0008,0032",dates); // Acquisition Time
		this.tags[3] = new Tags("0038,0020",dates); // Admitting Date
		this.tags[4] = new Tags("0038,0021",dates); // Admitting Time
		this.tags[5] = new Tags("0008,0035",dates); // Curve Time
		this.tags[6] = new Tags("0008,0025",dates); // Curve Date
		this.tags[7] = new Tags("0008,0023",dates); // Content Date
		this.tags[8] = new Tags("0008,0033",dates); // Content Time
		this.tags[9] = new Tags("0008,0024",dates); // Overlay Date
		this.tags[10] = new Tags("0008,0034",dates); // Overlay Time
		this.tags[11] = new Tags("0040,0244",dates); // ...Start Date
		this.tags[12] = new Tags("0040,0245",dates); // ...Start Time
		this.tags[13] = new Tags("0008,0021",dates); // Series Date
		this.tags[14] = new Tags("0008,0031",dates); // Series Time
		this.tags[15] = new Tags("0008,0020",dates); // Study Date
		this.tags[16] = new Tags("0008,0030",dates); // Study Time
		this.tags[17] = new Tags("0010,21D0",dates); // Last menstrual date
		this.tags[18] = new Tags("0008,0201",dates); // Timezone offset from UTC
		this.tags[19] = new Tags("0040,0002",dates); // Scheduled procedure step start date
		this.tags[20] = new Tags("0040,0003",dates); // Scheduled procedure step start time
		this.tags[21] = new Tags("0040,0004",dates); // Scheduled procedure step end date
		this.tags[22] = new Tags("0040,0005",dates); // Scheduled procedure step end time
		// Body characteristics
		this.tags[23] = new Tags("0010,2160",bodyChar); // Patient's ethnic group
		this.tags[21] = new Tags("0010,21A0",bodyChar); // Patient's smoking status
		this.tags[22] = new Tags("0010,0040",bodyChar); // Patient's sex
		this.tags[23] = new Tags("0010,2203",bodyChar); // Patient's sex neutered
		this.tags[24] = new Tags("0010,1010",bodyChar); // Patient's age
		this.tags[25] = new Tags("0010,21C0",bodyChar); // Patient's pregnancy status
		this.tags[26] = new Tags("0010,1020", bodyChar); // Patient's size
		this.tags[27] = new Tags("0010,1030", bodyChar); // Patient's weight
		// Other tags
		this.tags[28] = new Tags("0008,0050\":\"petctviewer.org", Choice.REPLACE); // Accession N
		this.tags[29] = new Tags("0010,0020\":\"" + this.newPatientID, Choice.REPLACE); // Patient ID
		this.tags[30] = new Tags("0010,0010\":\"" + this.newPatientName, Choice.REPLACE); //Patient's name
		this.tags[31] = new Tags("0008,103E", descriptionSerie); // Serie's description /!\ IL FAUT DONNER LE CHOIX DE MODIFY
		if(descriptionSerie.equals(Choice.KEEP)){
			this.tags[32] = new Tags("0008,1030\":\"" + newDescription, Choice.REPLACE);
			if( newDescription == null ||newDescription.equals("")){
				this.tags[32] = new Tags("0008,1030", Choice.KEEP);
			}
		}else{
			this.tags[32] = new Tags("0008,1030", Choice.CLEAR);
		}
		if(birthdate.equals(Choice.REPLACE)){
			this.tags[33] = new Tags("0010,0030\":\"19000101", birthdate); // Patient's birth date
		}else{
			this.tags[33] = new Tags("0010,0030", birthdate); // Patient's birth date
		}
		// Private tags
		this.tags[34] = new Tags("7053,1000", Choice.KEEP); // Philips
		this.tags[35] = new Tags("7053,1009", Choice.KEEP); // Philips
		this.tags[36] = new Tags("0009,103b", Choice.KEEP); // GE
		this.tags[37] = new Tags("0009,100d", Choice.KEEP); // GE
		this.tags[38] = new Tags("0011,1012", Choice.KEEP);
		//SK AJOUT FRAME OF REFERENCE UID - A GERER
		this.tags[39] = new Tags("0020,0052", Choice.KEEP);
		
		this.privateTags = privateTags;
	}

	public void setQuery(){
		this.query = null;
		StringBuilder replace = new StringBuilder();
		replace.append("\"Replace\":{");
		StringBuilder keep = new StringBuilder();
		keep.append("\"Keep\":[");
		if(tags != null){
			for(Tags t : Arrays.asList(tags)){
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

	public void deleteSerie() throws IOException{
		HttpURLConnection conn=connexionHttp.makeGetConnection("/series" + "/" + this.serieUID +"/delete");
		conn.disconnect();
	}

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
		String pattern1 = "\"ID\" : \"";
		String pattern2 = "\",";
		Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
		Matcher m = p.matcher(sb.toString());
		while(m.find()){
			this.newUID = m.group(1);
		}
		pattern1 = "\"PatientID\" : \"";
		pattern2 = "\",";
		p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
		m = p.matcher(sb.toString());
		while(m.find()){
			this.newPatientUID = m.group(1);
		}
		
		
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

	public ArrayList<String> getNewStudyIDs() throws IOException{
		ArrayList<String> newStudyIDs = new ArrayList<String>();
		StringBuilder sb =connexionHttp.makeGetConnectionAndStringBuilder("/patients/" + getNewUID());
		String pattern1 = "Studies\" : [";
		String pattern2 = "],";
		Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
		Matcher m = p.matcher(sb.toString());
		while(m.find()){
			String pattern3 = "\"";
			String pattern4 = "\"";
			Pattern p2 = Pattern.compile(Pattern.quote(pattern3) + "(.*?)" + Pattern.quote(pattern4));
			Matcher m2 = p2.matcher(m.group(1));
			while(m2.find()){
				newStudyIDs.add(m2.group(1));
			}
		}
		return newStudyIDs;
	}

}
