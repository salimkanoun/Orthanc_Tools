/**
Copyright (C) 2017 KANOUN Salim

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

package org.petctviewer.orthanc.monitoring;
import java.util.ArrayList;
import java.util.List;
import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Orthanc_Monitoring {
	
	private JsonObject changes=new JsonObject();
	private JsonParser parser=new JsonParser();
	private int last=0;
	private boolean done;
	
	//CD Burner variable
	public List<String> newStableStudyID = new ArrayList<String>();
	public List<String> newStablePatientID = new ArrayList<String>();
	public List<String> newStableSeriesID = new ArrayList<String>();
	public List<String> newPatientID = new ArrayList<String>();
	public List<String> newStudyID = new ArrayList<String>();
	public List<String> newSerieID = new ArrayList<String>();
	
	//Connxion API
	OrthancRestApis connexion;

	public Orthanc_Monitoring(OrthancRestApis connexion) {
		this.connexion=connexion;
		
	}
	
	public void makeMonitor() {
			StringBuilder sb;
			do {
				sb = connexion.makeGetConnectionAndStringBuilder("/changes?since="+String.valueOf(last));
				parseOutput(sb.toString());
			}
			while(!done);
			
	}
	
	/**
	 * Return curent last line of the change API
	 * @return
	 */
	public int getChangeLastLine() {
		StringBuilder sb = connexion.makeGetConnectionAndStringBuilder("/changes?last");
		JsonObject changes = parser.parse(sb.toString()).getAsJsonObject();
		int last=Integer.parseInt(changes.get("Last").getAsString());
		System.out.println(last);
		
		return last;
	}
	
	public void setChangeLastLine(int last) {
		this.last=last;
	}
	
	public void autoSetChangeLastLine() {
		int last=getChangeLastLine();
		this.last=last;
	}
	
	/**
	 * Parse le 1er niveau de message avec les valeurs des 100 derniers changements
	 * @param outputStream
	 * @throws ParseException
	 */
	private void parseOutput(String outputStream) {
		
		changes=parser.parse(outputStream).getAsJsonObject();
		System.out.println(changes.toString());
		JsonArray changesArray=changes.get("Changes").getAsJsonArray();
		for (int i=0; i<changesArray.size(); i++) {
			JsonObject changeEvent=changesArray.get(i).getAsJsonObject();
			String ID=changeEvent.get("ID").getAsString();
			
			if (changeEvent.get("ChangeType").getAsString().equals("NewPatient")) {
				newPatientID.add(ID);
			}
			 
			else if (changeEvent.get("ChangeType").getAsString().equals("NewStudy")) {
				newStudyID.add(ID);

			}
			
			else if (changeEvent.get("ChangeType").getAsString().equals("NewSeries")) {
				newSerieID.add(ID);
			}
			
			else if (changeEvent.get("ChangeType").getAsString().equals("StablePatient")) {
				newStablePatientID.add(ID);
			}
			
			else if (changeEvent.get("ChangeType").getAsString().equals("StableStudy")) {
				newStableStudyID.add(ID);
			}

			else if (changeEvent.get("ChangeType").getAsString().equals("StableSeries")) {
				newStableSeriesID.add(ID);
			}
			
		}
		
		last=Integer.parseInt(changes.get("Last").toString());
		done=Boolean.parseBoolean(changes.get("Done").toString());
	}

	public void clearAllList() {
		newStableStudyID.clear();
		newStablePatientID.clear();
		newStableSeriesID.clear();
		newPatientID.clear();
		newStudyID.clear();
		newSerieID.clear(); 
	}

		

}
