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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.ParametreConnexionHttp;

public class Orthanc_Monitoring {
	
	private JSONObject changes=new JSONObject();
	private JSONParser parser=new JSONParser();
	private int last=0;
	private boolean done;
	
	//CD Burner variable
	protected List<String> newStableStudyID = new ArrayList<String>();
	protected List<String> newStablePatientID = new ArrayList<String>();
	protected List<String> newStableSeriesID = new ArrayList<String>();
	protected List<String> newPatientID = new ArrayList<String>();
	protected List<String> newStudyID = new ArrayList<String>();
	protected List<String> newSerieID = new ArrayList<String>();
	
	//Connxion API
	ParametreConnexionHttp connexion;

	public Orthanc_Monitoring(ParametreConnexionHttp connexion) {
		this.connexion=connexion;
		
	}
	
	public void makeMonitor() {
		try {
			StringBuilder sb;
			do {
				sb = connexion.makeGetConnectionAndStringBuilder("/changes?since="+String.valueOf(last));
				parseOutput(sb.toString());
			}
			while(!done);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return curent last line of the change API
	 * @return
	 */
	public int getChangeLastLine() {
		StringBuilder sb;
		JSONObject changes = null;
		try {
			sb = connexion.makeGetConnectionAndStringBuilder("/changes?last");
			changes=(JSONObject) parser.parse(sb.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		int last=Integer.parseInt(changes.get("Last").toString());
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
	private void parseOutput(String outputStream) throws ParseException {
		
		changes=(JSONObject) parser.parse(outputStream);
		System.out.println(changes.toString());
		JSONArray changesArray=(JSONArray) changes.get("Changes");
		for (int i=0; i<changesArray.size(); i++) {
			JSONObject changeEvent=(JSONObject) changesArray.get(i);
			String ID= (String) changeEvent.get("ID");
			
			if (changeEvent.get("ChangeType").equals("NewPatient")) {
				newPatientID.add(ID);
			}
			 
			else if (changeEvent.get("ChangeType").equals("NewStudy")) {
				newStudyID.add(ID);
				
				//parseStudy(ID);
			}
			
			else if (changeEvent.get("ChangeType").equals("NewSeries")) {
				newSerieID.add(ID);
				//parseSerie(ID);
			}
			
			else if (changeEvent.get("ChangeType").equals("StablePatient")) {
				newStablePatientID.add((String) changeEvent.get("ID"));
				System.out.println("StablePatient");
			}
			
			else if (changeEvent.get("ChangeType").equals("StableStudy")) {
				newStableStudyID.add((String) changeEvent.get("ID"));
			}

			else if (changeEvent.get("ChangeType").equals("StableSeries")) {
				newStablePatientID.add((String) changeEvent.get("ID"));
			}
			
		}
		
		last=Integer.parseInt(changes.get("Last").toString());
		done=Boolean.parseBoolean(changes.get("Done").toString());
	}



		

}
