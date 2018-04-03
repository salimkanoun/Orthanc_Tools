package org.petctviewer.orthanc.monitoring;

import java.io.IOException;
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
	private boolean cdBurnerService;
	protected List<String> newStudyID = new ArrayList<String>();
	
	//Connxion API
	ParametreConnexionHttp connexion=new ParametreConnexionHttp();
	
public static void main(String[] args) throws IOException, ParseException {
	Orthanc_Monitoring monitor=new Orthanc_Monitoring(false);
	monitor.makeMonitor();
	
	
}

public Orthanc_Monitoring(boolean cdBurnerService) {
	this.cdBurnerService=cdBurnerService;
}

public void makeMonitor() {
	try {
		StringBuilder sb;
		do {
			sb = connexion.makeGetConnectionAndStringBuilder("/changes?since="+String.valueOf(last));
			parseOutput(sb.toString());
		}
		while(!done);
		
	} catch (ParseException | IOException e) {
		// TODO Auto-generated catch block
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
		sb = connexion.makeGetConnectionAndStringBuilder("/changes");
		changes=(JSONObject) parser.parse(sb.toString());
	} catch (IOException | ParseException e) {
		e.printStackTrace();
	}
	
	int last=(int) changes.get("Last");
	
	return last;
}

public void setChangeLastLine(int last) {
	this.last=last;
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
		
		if (changeEvent.get("ChangeType").equals("NewStudy")) {
			if (cdBurnerService) {
				newStudyID.add((String) changeEvent.get("ID"));
				
			}
			else parseStudy((String) changeEvent.get("ID"));
		}
		
		else if (changeEvent.get("ChangeType").equals("NewSeries")) {
			parseSerie((String) changeEvent.get("ID"));
		}
	}
	
	last=Integer.parseInt(changes.get("Last").toString());
	done=Boolean.parseBoolean(changes.get("Done").toString());
}


/**
 * Parse Study Level
 * @param id
 */

private void parseStudy(String id) {
	StringBuilder sb = null;
	try {
		sb = connexion.makeGetConnectionAndStringBuilder("/studies/"+id+"/series?simplify");
		JSONArray getserieInfo = (JSONArray) parser.parse(sb.toString());	
		int nombreSerie=getserieInfo.size();
	//Pour chaque Serie on recupere un objet JSON avec les informations
	for (int i=0; i<nombreSerie; i++) {
		JSONObject infoSerie=(JSONObject) parser.parse(getserieInfo.get(i).toString());
		//Nouvelle methode a creer pour parser le niveau Serie en evoyant le message
		System.out.println(infoSerie.get("ID"));
	}

	} catch (ParseException | IOException e) {e.printStackTrace();}
}
/**
 * test if study is stable
 * @param id
 */

public boolean studyIsStable(String id) {
	StringBuilder sb = null;
	boolean isStable = false;
	try { 
		sb = connexion.makeGetConnectionAndStringBuilder("/studies/"+id+"/");
		JSONObject getStudiesInfo = (JSONObject) parser.parse(sb.toString());	
		isStable= (boolean) getStudiesInfo.get("IsStable");
	
	} catch (ParseException | IOException e) {e.printStackTrace();}
	
	return isStable;
	
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


		

}
