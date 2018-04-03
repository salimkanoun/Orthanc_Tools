package org.petctviewer.orthanc.monitoring;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.ParametreConnexionHttp;

public class Orthanc_Monitoring {
	private JSONObject changes=new JSONObject();
	private JSONParser parser=new JSONParser();
	private int last;
	private boolean done;
	ParametreConnexionHttp connexion=new ParametreConnexionHttp();
	
	public static void main(String[] args) throws IOException, ParseException {
		Orthanc_Monitoring monitoring=new Orthanc_Monitoring();
		StringBuilder sb=monitoring.connexion.makeGetConnectionAndStringBuilder("/changes");
		monitoring.parseOutput(sb.toString());
		monitoring.getData();
		System.out.println(monitoring.done);
		System.out.println(monitoring.last);
		

	}
	
	public Orthanc_Monitoring() {

	}

/**
 * Parse le 1er niveau de message avec les valeurs des 100 derniers changements
 * @param outputStream
 * @throws ParseException
 */
private void parseOutput(String outputStream) throws ParseException {
	
	changes=(JSONObject) parser.parse(outputStream);
	JSONArray changesArray=(JSONArray) changes.get("Changes");
	for (int i=0; i<changesArray.size(); i++) {
		JSONObject changeEvent=(JSONObject) changesArray.get(i);
		parseChangeObject(changeEvent);
	}
	
	last=Integer.parseInt(changes.get("Last").toString());
	done=Boolean.parseBoolean(changes.get("Done").toString());
}

/**
 * Parse chaque changement
 * @param Change
 */
private void parseChangeObject (JSONObject change) {
	if (change.get("ChangeType").equals("NewStudy")) {
		parseStudy((String) change.get("ID"));
	}
	if (change.get("ChangeType").equals("NewSeries")) {
		parseSerie((String) change.get("ID"));
	}
}

/**
 * Parse Study Level
 * @param id
 */

private void parseStudy(String id) {
	StringBuilder sb = null;
	try {
		
		sb = connexion.makeGetConnectionAndStringBuilder("/studies/"+id+"/series?simplify");
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	//setUrl("studies/"+id+"/series?simplify");
		
	try { 
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
/**
 * Parse info Series pour determiner modalite
 * @param id
 */
private void parseSerie(String id) {
	StringBuilder sb = null;
	try {
		
		sb = connexion.makeGetConnectionAndStringBuilder("/series/"+id+"/");
		JSONObject seriesInfo=(JSONObject) parser.parse(sb.toString());
		JSONObject mainSerieTag=(JSONObject) parser.parse(seriesInfo.get("MainDicomTags").toString());
		if (mainSerieTag.get("Modality").equals("CT")) {
			//SI CT on traite comme CT
			parseCT(id, mainSerieTag);
		}
		
	} catch (ParseException | IOException e1) {e1.printStackTrace();}	
}

private void parseCT(String id, JSONObject mainSerieTag) {
	StringBuilder sb = null;
	try {
		
		sb = connexion.makeGetConnectionAndStringBuilder("/series/"+id+"/shared-tags");
	} catch (IOException e1) {e1.printStackTrace();}
	
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
	
}

/**
 * Fait une boucle jusqu'a épuiser les message, méthode à relancer periodiquement
 * @throws ParseException
 * @throws IOException
 */
private void getData() throws ParseException, IOException {
	do {
		StringBuilder sb = connexion.makeGetConnectionAndStringBuilder("/changes?since="+String.valueOf(last));
		parseOutput(sb.toString());
	}
	while(!done);
}
		

}
