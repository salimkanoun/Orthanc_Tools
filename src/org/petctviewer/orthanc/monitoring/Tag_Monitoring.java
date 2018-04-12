package org.petctviewer.orthanc.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.ParametreConnexionHttp;

public class Tag_Monitoring {
	
	private ParametreConnexionHttp parametre;
	private String level;
	private JSONParser parser=new JSONParser();
	private Timer timer;
	
	public Tag_Monitoring(ParametreConnexionHttp parametre, String level) {
		this.parametre=parametre;
		this.level=level;
	}
	
	public void startTagMonitoring() {
		Orthanc_Monitoring monitoring=new Orthanc_Monitoring(parametre);
		monitoring.autoSetChangeLastLine();
		
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				System.out.println("starting Auto-Fetch");
				monitoring.makeMonitor();
				
				
				
				
			}
			
		};
		
        //running timer task as daemon thread
        timer = new Timer(true);
        //Toutes les 90 seconds
        timer.scheduleAtFixedRate(timerTask, 0, (90*1000));
	}
	
	public void stopTagMonitoring() {
		timer.cancel();
	}

	
	/**
	 * Parse Study Level
	 * @param id
	 */
	/*
	private void parseStudy(String id) {
		StringBuilder sb = null;
		try {
			sb = parametre.makeGetConnectionAndStringBuilder("/studies/"+id+"/series?simplify");
			JSONArray getserieInfo = (JSONArray) parser.parse(sb.toString());	
			int nombreSerie=getserieInfo.size();
		//Pour chaque Serie on recupere un objet JSON avec les informations
		for (int i=0; i<nombreSerie; i++) {
			JSONObject infoSerie=(JSONObject) parser.parse(getserieInfo.get(i).toString());
			//Nouvelle methode a creer pour parser le niveau Serie en evoyant le message
			System.out.println(infoSerie.get("ID"));
		}
	
		} catch (ParseException e) {e.printStackTrace();}
	}
	
	
	private void parseSerie(String id) {
		StringBuilder sb = null;
		try {
			
			sb = parametre.makeGetConnectionAndStringBuilder("/series/"+id+"/");
			JSONObject seriesInfo=(JSONObject) parser.parse(sb.toString());
			JSONObject mainSerieTag=(JSONObject) parser.parse(seriesInfo.get("MainDicomTags").toString());
			if (mainSerieTag.get("Modality").equals("CT")) {
				//SI CT on traite comme CT
				parseCT(id, mainSerieTag);
			}
			
		} catch (ParseException e1) {e1.printStackTrace();}	
	}
	
	@SuppressWarnings("unchecked")
	private void parseCT(String id, JSONObject mainSerieTag) {
		StringBuilder sb = null;
		sb = parametre.makeGetConnectionAndStringBuilder("/series/"+id+"/shared-tags");
		
		try {
			JSONObject sharedTag=(JSONObject) parser.parse(sb.toString());
			String[] tagcommun=new String [sharedTag.size()];
			sharedTag.keySet().toArray(tagcommun);
			for (int i=0; i<tagcommun.length; i++) {
				JSONObject tag=(JSONObject) sharedTag.get(tagcommun[i]);
				String name=(String) tag.get("Name");
				System.out.println(name);
			}
			//System.out.println(tagcommun.toString());
		} catch (ParseException e) {e.printStackTrace();}
		

*/

}
