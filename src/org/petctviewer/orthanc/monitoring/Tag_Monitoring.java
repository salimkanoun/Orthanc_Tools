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
	
	private static final String parentStudy = null;
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
		
						db.InsertPatient(hashmapTagPatient.get("LastName"), hashmapTagPatient.get("FirstName") ,  hashmapTagPatient.get("PatientID"), monitoring.newPatientID.get(i),hashmapTagPatient.get("PatientBirthDate"), hashmapTagPatient.get("PatientSex") );
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
						
						db.InsertPatient(studyTag.get("LastName"), studyTag.get("FirstName") ,  studyTag.get("PatientID"), monitoring.newPatientID.get(i), studyTag.get("PatientBirthDate"), studyTag.get("PatientSex") );
						db.InsertStudy(studyTag.get("StudyID"), studyTag.get("StudyInstanceUID"), studyTag.get(monitoring.newStudyID.get(i)), studyTag.get("AccessionNumber"), studyTag.get("InstitutionName"), studyTag.get("ReferringPhysicianName"), studyTag.get("StudyDate"), studyTag.get("StudyDescription"), studyTag.get("StudyTime"));
						
					}
				}
				else if (level.equals("serie")) {
					for (int i=0 ; i<monitoring.newStableSeriesID.size(); i++) {

						//Methode parsing Series
						HashMap<String, String> foundTags=getTagFromInstance(monitoring.newStableSeriesID.get(i));
					
						
						StringBuilder sbSharedTags=parametre.makeGetConnectionAndStringBuilder("/series/"+monitoring.newStableSeriesID.get(i)+"/shared-tags");
						textAreaConsole.append("Shared-Tags"+sbSharedTags+",");
						foundTags.put("Shared_Tags", sbSharedTags.toString());
						
						System.out.println(foundTags.toString());
						db.InsertPatient(foundTags.get("LastName"), foundTags.get("FirstName") ,  foundTags.get("PatientID"), foundTags.get("ParentPatient") , foundTags.get("PatientBirthDate"), foundTags.get("PatientSex") );
						db.InsertStudy(foundTags.get("StudyID"), foundTags.get("StudyInstanceUID"), foundTags.get("ParentStudy") , foundTags.get("AccessionNumber"), foundTags.get("InstitutionName"), foundTags.get("ReferringPhysicianName"), foundTags.get("StudyDate"), foundTags.get("StudyDescription"), foundTags.get("StudyTime"));
						//A FAIRE RECUPERER LES CLE DE LA HASHMAP
						//db.InsertSeries(size, age, weight, Manifacturer, Manifacturer_Model, Performing_Physician_Name, Series_Description, Station_Name, Content_Date, Content_Time, Protocol_Name, Series_Instance_UID, Comment_Radiation_Dose, Radiopharmaceutical_sequence, Radiopharmaceutical, RadiopharmaceuticalStartTime, RadionuclideTotalDose, RadionuclideHalfLife, RadionuclidePositronFraction, Radiation_Dose_Module, Shared_Tags, Orthanc_Serie_ID);
						
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
		textAreaConsole.append("PatientID= " + patientID+ ",");
		textAreaConsole.append("PatientSex"+ patientSex+ ",");
		textAreaConsole.append("PatientBirthDate= " +birthDate+ "\n");
		
		String[] name =patientName.split("^");
		HashMap<String, String> hashmapTagPatient=new HashMap<String, String>();
		hashmapTagPatient.put("PatientBirthDate", birthDate);
		hashmapTagPatient.put("PatientID", patientID);
		hashmapTagPatient.put("LastName", name[0]);
		hashmapTagPatient.put("FirstName", name[1]);
		hashmapTagPatient.put("PatientSex", patientSex);
		
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
		
		textAreaConsole.append("AccessionNumber= "+accessionNumber+ ",");
		textAreaConsole.append("InstitutionName= " + institutionName+ ",");
		textAreaConsole.append("ReferringPhysicianName= "+ referringPhysicianName+ ",");
		textAreaConsole.append("StudyDate= " +studyDate+ ",");
		textAreaConsole.append("StudyDescription= "+studyDescription+ ",");
		textAreaConsole.append("StudyID= " + studyID+ ",");
		textAreaConsole.append("StudyInstanceUID= "+ studyInstanceUID+ ",");
		textAreaConsole.append("StudyTime= " +studyTime+ "\n");
		
		hashmapTagPatient.put("AccessionNumber", accessionNumber);
		hashmapTagPatient.put("InstitutionName", institutionName);
		hashmapTagPatient.put("ReferringPhysicianName", referringPhysicianName);
		hashmapTagPatient.put("StudyDate", studyDate);
		hashmapTagPatient.put("StudyDescription", studyDescription);
		hashmapTagPatient.put("StudyID", studyID);
		hashmapTagPatient.put("StudyInstanceUID", studyInstanceUID);
		hashmapTagPatient.put("StudyTime", studyTime);
		
		return hashmapTagPatient;
		
	}
	
	
	private HashMap<String, String> getTagFromInstance(String seriesID) {
		//HashMap ou on va mettre tout les tag trouves pour les retourner a la BDD
		HashMap<String, String> hashmapTag=new HashMap<String, String>();
		
		StringBuilder sbSeries=parametre.makeGetConnectionAndStringBuilder("/series/"+seriesID);
		JSONObject seriesJson = null;
		try {
			seriesJson = (JSONObject) parser.parse(sbSeries.toString());
			String parentStudy=(String) seriesJson.get("ParentStudy");
			hashmapTag.put("ParentStudy", parentStudy);
			StringBuilder patient=parametre.makeGetConnectionAndStringBuilder("/studies/"+parentStudy);
			JSONObject patientJson = (JSONObject) parser.parse(patient.toString());
			String parentPatient=(String) patientJson.get("ParentPatient");
			hashmapTag.put("ParentPatient", parentPatient);
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
		
		String[] namePatient =hashmapTag.get("PatientName").split("^");
		hashmapTag.put("LastName", namePatient[0]);
		hashmapTag.put("FirstName", namePatient[1]);
		
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
