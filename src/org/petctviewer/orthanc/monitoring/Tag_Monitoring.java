package org.petctviewer.orthanc.monitoring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextArea;

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
				JDBC_Monitoring db=null;
				try {
					db=new JDBC_Monitoring();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if (level.equals("patient")) {
					for (int i=0 ; i<monitoring.newPatientID.size(); i++) {
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/patients/"+monitoring.newPatientID.get(i));
						JSONObject patientJson = null;
						try {
							patientJson = (JSONObject) parser.parse(sb.toString());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						HashMap<String, String> hashmapTagPatient=getMainPatientTag((JSONObject) patientJson.get("MainDicomTags"));
						
						//On ecrit dans la BDD
						//SK BOOLEAN A GERER SI IL Y A UNE BDD D ACTIVE
						db.InsertPatient(hashmapTagPatient.get("lastName"), hashmapTagPatient.get("firstName") ,  hashmapTagPatient.get("patientID"), monitoring.newPatientID.get(i),hashmapTagPatient.get("birthdate"), hashmapTagPatient.get("sex") );
					}
					
				}
				else if (level.equals("study")) {
					for (int i=0 ; i<monitoring.newStudyID.size(); i++) {
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/studies/"+monitoring.newStudyID.get(i));
						HashMap<String, String> studyTag=null;
						try {
							studyTag=getMainStudyTag((JSONObject) parser.parse(sb.toString()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						//On ecrit dans la BDD
						//SK BOOLEAN A GERER SI IL Y A UNE BDD D ACTIVE
						db.InsertPatient(studyTag.get("lastName"), studyTag.get("firstName") ,  studyTag.get("patientID"), monitoring.newPatientID.get(i), studyTag.get("birthdate"), studyTag.get("sex") );
						db.InsertStudy(studyTag.get("studyID"), studyTag.get("studyInstanceUID"), studyTag.get(monitoring.newStudyID.get(i)), studyTag.get("accessionNumber"), studyTag.get("institutionName"), studyTag.get("referringPhysicianName"), studyTag.get("studyDate"), studyTag.get("studyDescription"), studyTag.get("studyTime"));
						
					}
				}
				else if (level.equals("serie")) {
					for (int i=0 ; i<monitoring.newStableSeriesID.size(); i++) {

						//Methode parsing Series
						HashMap<String, String> foundTags=getTagFromInstance(monitoring.newStableSeriesID.get(i));
					
						
						StringBuilder sbSharedTags=parametre.makeGetConnectionAndStringBuilder("/series/"+monitoring.newStableSeriesID.get(i)+"/shared-tags");
						textAreaConsole.append("Shared-Tags"+sbSharedTags+",");
						foundTags.put("Shared_Tags", sbSharedTags.toString());
						
						//SK RESTE A FAIRE POUR LE NIVEAU SERIES (CREATION TABLE ET INSCRIPTION)
						//SK A FAIRE POUR PATIENT ET STUDY
						System.out.println(foundTags.toString());
						
						
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
	
	private HashMap<String, String> getMainPatientTag(JSONObject mainPatientTag) {
		
		String birthDate=(String) mainPatientTag.get("PatientBirthDate");
		String patientID=(String) mainPatientTag.get("PatientID");
		String patientName=(String) mainPatientTag.get("PatientName");
		String patientSex=(String) mainPatientTag.get("PatientSex");
		
		textAreaConsole.append("New patient"+ ",");
		textAreaConsole.append("Name= "+patientName+",");
		textAreaConsole.append("ID= " + patientID+ ",");
		textAreaConsole.append("Sex= "+ patientSex+ ",");
		textAreaConsole.append("DOB= " +birthDate+ "\n");
		
		String[] name =patientName.split("^");
		HashMap<String, String> hashmapTagPatient=new HashMap<String, String>();
		hashmapTagPatient.put("birthdate", birthDate);
		hashmapTagPatient.put("patientID", patientID);
		hashmapTagPatient.put("lastName", name[0]);
		hashmapTagPatient.put("firstName", name[1]);
		hashmapTagPatient.put("sex", patientSex);
		
		return hashmapTagPatient;
	}
	
	private HashMap<String, String> getMainStudyTag(JSONObject jsonStudy) {
		textAreaConsole.append("New Study"+ "\n");
		JSONObject jsonMainStudyTag=(JSONObject) jsonStudy.get("MainDicomTags");
		
		//On recupere les info Patients
		HashMap<String, String> hashmapTagPatient=getMainPatientTag((JSONObject) jsonStudy.get("PatientMainDicomTags"));
		
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
		
		hashmapTagPatient.put("accessionNumber", accessionNumber);
		hashmapTagPatient.put("institutionName", institutionName);
		hashmapTagPatient.put("referringPhysicianName", referringPhysicianName);
		hashmapTagPatient.put("studyDate", studyDate);
		hashmapTagPatient.put("studyDescription", studyDescription);
		hashmapTagPatient.put("studyID", studyID);
		hashmapTagPatient.put("studyInstanceUID", studyInstanceUID);
		hashmapTagPatient.put("studyTime", studyTime);
		
		return hashmapTagPatient;
		
	}
	
	
	private HashMap<String, String> getTagFromInstance(String seriesID) {
		//HashMap ou on va mettre tout les tag trouves pour les retourner a la BDD
		HashMap<String, String> hashmapTag=new HashMap<String, String>();
		
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
				hashmapTag.put(name, value);
			}
			
		}
		
		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestStudy.length; i++) {
			if (tags.containsKey(Tag_Of_Interest.tagOfInterestStudy[i])) {
				JSONObject jsonTag=(JSONObject) tags.get(Tag_Of_Interest.tagOfInterestStudy[i]);
				String name=(String) jsonTag.get("Name");
				String value=(String) jsonTag.get("Value");
				textAreaConsole.append(name +" "+value +",");
				hashmapTag.put(name, value);
			}
			
		}
		
		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestSeries.length; i++) {
			if (tags.containsKey(Tag_Of_Interest.tagOfInterestSeries[i])) {
				JSONObject jsonTag=(JSONObject) tags.get(Tag_Of_Interest.tagOfInterestSeries[i]);
				String name=(String) jsonTag.get("Name");
				String value=(String) jsonTag.get("Value");
				textAreaConsole.append(name +" "+value+",");
				hashmapTag.put(name, value);
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
					hashmapTag.put(name, value);
				}
				
			}
		}
		
		
		if (tags.containsKey(Tag_Of_Interest.radiationDoseModule)) {
			JSONObject radiationDoseModule = (JSONObject) tags.get(Tag_Of_Interest.radiationDoseModule);
			JSONArray radiationSequenceTags= (JSONArray) radiationDoseModule.get("Value");
			textAreaConsole.append(radiationSequenceTags+",");
			hashmapTag.put(Tag_Of_Interest.radiationDoseModule, radiationSequenceTags.toString());
			
		}
		
		textAreaConsole.append("\n");

		return hashmapTag;
		
	}
	
	public void stopTagMonitoring() {
		timer.cancel();
		textAreaConsole.append("Tag-Monitoring Stoped"+ "\n");
	}
	


}
