package org.petctviewer.orthanc.monitoring;

import java.util.Timer;
import java.util.TimerTask;

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
		System.out.println("starting Tag-Monitoring");
		
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				
				monitoring.makeMonitor();
				
				if (level.equals("patient")) {
					for (int i=0 ; i<monitoring.newPatientID.size(); i++) {
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/patients/"+monitoring.newPatientID.get(i));
						JSONObject patientJson = null;
						try {
							patientJson = (JSONObject) parser.parse(sb.toString());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						getMainPatientTag((JSONObject) patientJson.get("MainDicomTags"));
					}
					
				}
				else if (level.equals("study")) {
					for (int i=0 ; i<monitoring.newStudyID.size(); i++) {
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/studies/"+monitoring.newStudyID.get(i));
						try {
							getMainStudyTag((JSONObject) parser.parse(sb.toString()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				else if (level.equals("serie")) {
					for (int i=0 ; i<monitoring.newStableSeriesID.size(); i++) {
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/series/"+monitoring.newStableSeriesID.get(i));
						JSONObject seriesTag = null;
						try {
							seriesTag = (JSONObject) parser.parse(sb.toString());	
							String parentStudyID=(String) seriesTag.get("ParentStudy");
							StringBuilder sbParentStudy=parametre.makeGetConnectionAndStringBuilder("/studies/"+parentStudyID);
							JSONObject parentStudy=(JSONObject) parser.parse(sbParentStudy.toString());
							getMainStudyTag(parentStudy);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					
						
						StringBuilder sbSharedTags=parametre.makeGetConnectionAndStringBuilder("/series/"+monitoring.newStableSeriesID.get(i)+"/shared-tags");
						System.out.println(sbSharedTags);
					}
					
					
				}
				
				monitoring.clearAllList();
				
				
			}
			
		};
		
        //running timer task as daemon thread
        timer = new Timer(true);
        //Toutes les 90 seconds
        timer.scheduleAtFixedRate(timerTask, 0, (90*1000));
	}
	
	private void getMainPatientTag(JSONObject mainPatientTag) {
		
		String birthDate=(String) mainPatientTag.get("PatientBirthDate");
		String patientID=(String) mainPatientTag.get("PatientID");
		String patientName=(String) mainPatientTag.get("PatientName");
		String patientSex=(String) mainPatientTag.get("PatientSex");
		System.out.println("Nouveau patient");
		System.out.println("Nom "+patientName);
		System.out.println("ID " + patientID);
		System.out.println("Sexe "+ patientSex);
		System.out.println("DOB " +birthDate);
		
	}
	
	private void getMainStudyTag(JSONObject jsonStudy) {
		System.out.println("Nouvelle Study");
		JSONObject jsonMainStudyTag=(JSONObject) jsonStudy.get("MainDicomTags");
		
		//On recupere les info Patients
		getMainPatientTag((JSONObject) jsonStudy.get("PatientMainDicomTags"));
		
		//Info Study
		String accessionNumber=(String) jsonMainStudyTag.get("AccessionNumber");
		String institutionName=(String) jsonMainStudyTag.get("InstitutionName");
		String referringPhysicianName=(String) jsonMainStudyTag.get("ReferringPhysicianName");
		String studyDate=(String) jsonMainStudyTag.get("StudyDate");
		String studyDescription=(String) jsonMainStudyTag.get("StudyDescription");
		String studyID=(String) jsonMainStudyTag.get("StudyID");
		String studyInstanceUID=(String) jsonMainStudyTag.get("StudyInstanceUID");
		String studyTime=(String) jsonMainStudyTag.get("StudyTime");
		
		
		System.out.println("accessionNumber "+accessionNumber);
		System.out.println("institutionName " + institutionName);
		System.out.println("referringPhysicianName "+ referringPhysicianName);
		System.out.println("studyDate " +studyDate);
		System.out.println("studyDescription "+studyDescription);
		System.out.println("studyID " + studyID);
		System.out.println("studyInstanceUID "+ studyInstanceUID);
		System.out.println("studyTime " +studyTime);
		
		
	}
	
	public void stopTagMonitoring() {
		timer.cancel();
	}


}
