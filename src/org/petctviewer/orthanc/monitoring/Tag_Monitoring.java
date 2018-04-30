package org.petctviewer.orthanc.monitoring;

import java.awt.BorderLayout;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.ParametreConnexionHttp;
import org.petctviewer.orthanc.query.AutoQueryResultTableDialog;

public class Tag_Monitoring {
	
	private ParametreConnexionHttp parametre;
	private String level;
	private JSONParser parser=new JSONParser();
	private Timer timer;
	JTextArea textAreaConsole;
	
	public Tag_Monitoring(ParametreConnexionHttp parametre, String level, JTextArea textAreaConsole) {
		this.parametre=parametre;
		this.level=level;
		this.textAreaConsole=textAreaConsole;
	}
	
	public void startTagMonitoring() {
		Orthanc_Monitoring monitoring=new Orthanc_Monitoring(parametre);
		monitoring.autoSetChangeLastLine();
		textAreaConsole.append("Starting Tag-Monitoring"+ "\n");
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
						
						/* methode using Orthanc Level
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/series/"+monitoring.newStableSeriesID.get(i));
						JSONObject seriesTag = null;
						seriesTag = (JSONObject) parser.parse(sb.toString());	
						String parentStudyID=(String) seriesTag.get("ParentStudy");
						StringBuilder sbParentStudy=parametre.makeGetConnectionAndStringBuilder("/studies/"+parentStudyID);
						JSONObject parentStudy=(JSONObject) parser.parse(sbParentStudy.toString());
						getMainStudyTag(parentStudy);
						*/
						
						//Methode parsing Series
						getTagFromInstance(monitoring.newStableSeriesID.get(i));
					
						//SK A FAIRE POUR SHARED TAGS
						//StringBuilder sbSharedTags=parametre.makeGetConnectionAndStringBuilder("/series/"+monitoring.newStableSeriesID.get(i)+"/shared-tags");
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
		
		textAreaConsole.append("New patient"+ ",");
		textAreaConsole.append("Name= "+patientName+",");
		textAreaConsole.append("ID= " + patientID+ ",");
		textAreaConsole.append("Sex= "+ patientSex+ ",");
		textAreaConsole.append("DOB= " +birthDate+ "\n");
		
	}
	
	private void getMainStudyTag(JSONObject jsonStudy) {
		textAreaConsole.append("New Study"+ "\n");
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
		
		textAreaConsole.append("accessionNumber= "+accessionNumber+ ",");
		textAreaConsole.append("institutionName= " + institutionName+ ",");
		textAreaConsole.append("referringPhysicianName= "+ referringPhysicianName+ ",");
		textAreaConsole.append("studyDate= " +studyDate+ ",");
		textAreaConsole.append("studyDescription= "+studyDescription+ ",");
		textAreaConsole.append("studyID= " + studyID+ ",");
		textAreaConsole.append("studyInstanceUID= "+ studyInstanceUID+ ",");
		textAreaConsole.append("studyTime= " +studyTime+ "\n");
		
	}
	
	//SK A CONTINUER
	private void getTagFromInstance(String seriesID) {
		StringBuilder sbSeries=parametre.makeGetConnectionAndStringBuilder("/series/"+seriesID);
		JSONObject seriesJson = null;
		try {
			seriesJson = (JSONObject) parser.parse(sbSeries.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray instanceArray=(JSONArray) seriesJson.get("Instances");
		StringBuilder instanceJson=parametre.makeGetConnectionAndStringBuilder("/instances/"+instanceArray.get(0)+"/tags");
		JSONObject tags = null;
		try {
			tags = (JSONObject) parser.parse(instanceJson.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestPatient.length; i++) {
			if (tags.containsKey(Tag_Of_Interest.tagOfInterestPatient[i])) {
				JSONObject jsonTag=(JSONObject) tags.get(Tag_Of_Interest.tagOfInterestPatient[i]);
				String name=(String) jsonTag.get("Name");
				String value=(String) jsonTag.get("Value");
				textAreaConsole.append(name +" "+value +",");
			}
			
		}
		
		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestStudy.length; i++) {
			if (tags.containsKey(Tag_Of_Interest.tagOfInterestStudy[i])) {
				JSONObject jsonTag=(JSONObject) tags.get(Tag_Of_Interest.tagOfInterestStudy[i]);
				String name=(String) jsonTag.get("Name");
				String value=(String) jsonTag.get("Value");
				textAreaConsole.append(name +" "+value +",");
			}
			
		}
		
		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestSeries.length; i++) {
			if (tags.containsKey(Tag_Of_Interest.tagOfInterestSeries[i])) {
				JSONObject jsonTag=(JSONObject) tags.get(Tag_Of_Interest.tagOfInterestSeries[i]);
				String name=(String) jsonTag.get("Name");
				String value=(String) jsonTag.get("Value");
				textAreaConsole.append(name +" "+value+",");
			}
			
		}
		
		if (tags.containsKey(Tag_Of_Interest.radiopharmaceuticalTag)) {
			JSONObject radiopharmaceuticalSequence = (JSONObject) tags.get(Tag_Of_Interest.radiopharmaceuticalTag);
			JSONArray radiopharmaceuticalSequenceTags= (JSONArray) radiopharmaceuticalSequence.get("Value");
			JSONObject radiopharmaceuticalSequenceTagsValue = (JSONObject) radiopharmaceuticalSequenceTags.get(0);
			for (int i=0 ; i<Tag_Of_Interest.radiopharmaceutical.length; i++) {
				if (tags.containsKey(Tag_Of_Interest.radiopharmaceutical[i])) {
					JSONObject jsonTag=(JSONObject) radiopharmaceuticalSequenceTagsValue.get(Tag_Of_Interest.radiopharmaceutical[i]);
					String name=(String) jsonTag.get("Name");
					String value=(String) jsonTag.get("Value");
					textAreaConsole.append(name +" "+value+",");
				}
				
			}
		}
		
		textAreaConsole.append("\n");

		
		
	}
	
	public void stopTagMonitoring() {
		timer.cancel();
		textAreaConsole.append("Tag-Monitoring Stoped"+ "\n");
	}
	


}
