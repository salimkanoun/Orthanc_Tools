package org.petctviewer.orthanc.monitoring;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.ParametreConnexionHttp;
import org.petctviewer.orthanc.query.Rest;

public class Auto_Fetch {
	
	private ParametreConnexionHttp parametre;
	private String level, studyDate, modality, studyDescription, queryAet , retrieveAet;
	private JSONParser parser=new JSONParser();
	Rest restApi=new Rest(parametre);
	
	//sert a etre arrete via methode
	private Timer timer;
	
	public Auto_Fetch(ParametreConnexionHttp parametre, String level, String studyDate, String modality, String studyDescription, String queryAet) {
		this.parametre=parametre;
		this.level=level;
		this.studyDate=studyDate;
		this.studyDescription=studyDescription;
		this.queryAet=queryAet;
		this.modality=modality;
		try {
			Object[] localAET = restApi.getLocalAET();
			retrieveAet=localAET[0].toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void startAutoFetch() {
		Orthanc_Monitoring monitoring=new Orthanc_Monitoring(parametre);
		monitoring.autoSetChangeLastLine();
		
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				System.out.println("starting Auto-Fetch");
				monitoring.makeMonitor();
				List<String> idToProcess=null;
				//Si on monitore le level Study on parcours les study arrivees pour stocker les ID patients a retrieve
				if (level.equals("study")) {
					idToProcess=monitoring.newStudyID;
					for (int i=0; i<monitoring.newStudyID.size(); i++) {
						StringBuilder sb = parametre.makeGetConnectionAndStringBuilder("/studies/"+monitoring.newStudyID.get(i));
						try {
							JSONObject study = (JSONObject) parser.parse(sb.toString());
							JSONObject patientMainTag= (JSONObject) study.get("PatientMainDicomTags");
							idToProcess.add((String) patientMainTag.get("PatientID"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
					}
				}
				//Si on monitore le level Patients on parcours les patients arrivees pour stocker les ID patients a retrieve
				if (level.equals("patient")) {
					for (int i=0; i<monitoring.newPatientID.size(); i++) {
						StringBuilder sb = parametre.makeGetConnectionAndStringBuilder("/patients/"+monitoring.newPatientID.get(i));
						try {
							JSONObject patient = (JSONObject) parser.parse(sb.toString());
							JSONObject patientMainTag= (JSONObject) patient.get("MainDicomTags");
							idToProcess.add((String) patientMainTag.get("PatientID"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
					}
					
				}
				//Pour chaque ID patient on lance le retrieve
				for (int i=0 ; i<idToProcess.size(); i++) {
					makeRetrieve(idToProcess.get(i));
				}
				
				monitoring.newStableStudyID.clear();
				
			}
			
		};
		
        //running timer task as daemon thread
        timer = new Timer(true);
        //Toutes les 90 seconds
        timer.scheduleAtFixedRate(timerTask, 0, (90*1000));
	}
	
	/**
	 * Retrieve en local toutes les reponse d'un ID patient avec le filtre (date, modality, description)
	 * @param patientID
	 * @throws IOException
	 */
	public void makeRetrieve(String patientID) {
		
		String[] results=restApi.getQueryAnswerIndexes("Study", "*", patientID, studyDate, modality, studyDescription, "*", queryAet);
		for (int i=0 ; i<Integer.parseInt(results[1]) ; i++) {
			restApi.retrieve(results[0], i, retrieveAet);
		}
		
	}

}
