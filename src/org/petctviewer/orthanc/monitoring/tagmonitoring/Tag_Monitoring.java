package org.petctviewer.orthanc.monitoring.tagmonitoring;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.JTextArea;

import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.monitoring.Orthanc_Monitoring;
import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Tag_Monitoring {
	
	private Preferences jprefer = VueAnon.jprefer;
	private OrthancRestApis parametre;
	private String level;
	private JsonParser parser=new JsonParser();
	private Timer timer;
	JTextArea textAreaConsole;
	
	public Tag_Monitoring(OrthancRestApis parametre, String level, JTextArea textAreaConsole) {
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
				JDBC_Monitoring db=new JDBC_Monitoring();
				
				if (level.equals("patient")) {
					for (int i=0 ; i<monitoring.newPatientID.size(); i++) {
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/patients/"+monitoring.newPatientID.get(i));
						JsonObject patientJson = parser.parse(sb.toString()).getAsJsonObject();
						
						HashMap<String, String> hashmapTagPatient=getMainPatientTag(patientJson.get("MainDicomTags").getAsJsonObject());
						
						//On ecrit dans la BDD
						if(jprefer.getBoolean("useDBMonitoring", false)) {
							db.InsertPatient(hashmapTagPatient.get("LastName"), hashmapTagPatient.get("FirstName") ,  hashmapTagPatient.get("PatientID"), monitoring.newPatientID.get(i),hashmapTagPatient.get("PatientBirthDate"), hashmapTagPatient.get("PatientSex") );
						}
						if(jprefer.getBoolean("AutoDeleteMonitoring", false)) {
							parametre.makeDeleteConnection("/patients/"+monitoring.newPatientID.get(i)+"/");
						}
					}
					
				}
				else if (level.equals("study")) {
					for (int i=0 ; i<monitoring.newStudyID.size(); i++) {
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/studies/"+monitoring.newStudyID.get(i));
						HashMap<String, String> studyTag=getMainStudyTag(parser.parse(sb.toString()).getAsJsonObject());
						
						if(jprefer.getBoolean("useDBMonitoring", false)) {
							db.InsertPatient(studyTag.get("LastName"), studyTag.get("FirstName") ,  studyTag.get("PatientID"), monitoring.newPatientID.get(i), studyTag.get("PatientBirthDate"), studyTag.get("PatientSex") );
							db.InsertStudy(studyTag.get("StudyID"), studyTag.get("StudyInstanceUID"), studyTag.get(monitoring.newStudyID.get(i)), studyTag.get("AccessionNumber"), studyTag.get("InstitutionName"), studyTag.get("ReferringPhysicianName"), studyTag.get("StudyDate"), studyTag.get("StudyDescription"), studyTag.get("StudyTime"), studyTag.get("ParentPatient"));
							
						}
						if(jprefer.getBoolean("AutoDeleteMonitoring", false)) {
							parametre.makeDeleteConnection("/studies/"+monitoring.newStudyID.get(i)+"/");
						}
						
						
					}
				}
				else if (level.equals("serie")) {
					for (int i=0 ; i<monitoring.newStableSeriesID.size(); i++) {

						//Methode parsing Series
						HashMap<String, String> foundTags=getTagFromInstance(monitoring.newStableSeriesID.get(i));
					
						
						StringBuilder sbSharedTags=parametre.makeGetConnectionAndStringBuilder("/series/"+monitoring.newStableSeriesID.get(i)+"/shared-tags");
						//textAreaConsole.append("Shared-Tags"+sbSharedTags+",");
						foundTags.put("Shared_Tags", sbSharedTags.toString());
						
						if(jprefer.getBoolean("useDBMonitoring", false)) {
							db.InsertPatient(foundTags.get("LastName"), foundTags.get("FirstName") ,  foundTags.get("PatientID"), foundTags.get("ParentPatient") , foundTags.get("PatientBirthDate"), foundTags.get("PatientSex") );
							db.InsertStudy(foundTags.get("StudyID"), foundTags.get("StudyInstanceUID"), foundTags.get("ParentStudy") , foundTags.get("AccessionNumber"), foundTags.get("InstitutionName"), foundTags.get("ReferringPhysicianName"), foundTags.get("StudyDate"), foundTags.get("StudyDescription"), foundTags.get("StudyTime"), foundTags.get("ParentPatient"));
							db.InsertSeries(foundTags.get("PatientSize"), foundTags.get("PatientAge"), foundTags.get("PatientWeight"),foundTags.get("Manufacturer"), foundTags.get("ManufacturerModelName"), foundTags.get("PerformingPhysicianName"), foundTags.get("SeriesDescription"), foundTags.get("StationName"), foundTags.get("ContentDate"), foundTags.get("ContentTime"), foundTags.get("ProtocolName"), foundTags.get("SeriesInstanceUID"), foundTags.get("CommentsOnRadiationDose"), foundTags.get("RadiopharmaceuticalInformationSequence"), foundTags.get("Radiopharmaceutical"), foundTags.get("RadiopharmaceuticalStartTime"), foundTags.get("RadionuclideTotalDose"), foundTags.get("RadionuclideHalfLife"), foundTags.get("RadionuclidePositronFraction"), foundTags.get(Tag_Of_Interest.radiationDoseModule), foundTags.get("Shared_Tags"), monitoring.newStableSeriesID.get(i), foundTags.get("ParentStudy"));
							
						}
						if(jprefer.getBoolean("AutoDeleteMonitoring", false)) {
							parametre.makeDeleteConnection("/series/"+monitoring.newStableSeriesID.get(i)+"/");
						}
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
	
	private HashMap<String, String> getMainPatientTag(JsonObject mainPatientTag) {
		
		String birthDate=mainPatientTag.get("PatientBirthDate").getAsString();
		String patientID=mainPatientTag.get("PatientID").getAsString();
		String patientName=mainPatientTag.get("PatientName").getAsString();
		String patientSex=mainPatientTag.get("PatientSex").getAsString();
		
		textAreaConsole.append("New patient"+ ",");
		textAreaConsole.append("Name= "+patientName+",");
		textAreaConsole.append("PatientID= " + patientID+ ",");
		textAreaConsole.append("PatientSex"+ patientSex+ ",");
		textAreaConsole.append("PatientBirthDate= " +birthDate+ "\n");
		
		HashMap<String, String> hashmapTagPatient=new HashMap<String, String>();
		
		if(patientName.indexOf("^") != -1) {
			String[] namePatient =patientName.split("\\^");
			hashmapTagPatient.put("LastName", namePatient[0]);
			hashmapTagPatient.put("FirstName", namePatient[1]);
		}
		else {
			hashmapTagPatient.put("LastName", patientName);
			hashmapTagPatient.put("FirstName", "N/A");
		}
		
		hashmapTagPatient.put("PatientBirthDate", birthDate);
		hashmapTagPatient.put("PatientID", patientID);
		hashmapTagPatient.put("PatientSex", patientSex);
		
		return hashmapTagPatient;
	}
	
	private HashMap<String, String> getMainStudyTag(JsonObject jsonStudy) {
		textAreaConsole.append("New Study"+ "\n");
		JsonObject jsonMainStudyTag=jsonStudy.get("MainDicomTags").getAsJsonObject();
		
		//On recupere les info Patients
		HashMap<String, String> hashmapTagPatient=getMainPatientTag(jsonStudy.get("PatientMainDicomTags").getAsJsonObject());
		
		//Info Study
		String accessionNumber=jsonMainStudyTag.get("AccessionNumber").getAsString();
		String institutionName=jsonMainStudyTag.get("InstitutionName").getAsString();
		String referringPhysicianName=jsonMainStudyTag.get("ReferringPhysicianName").getAsString();
		String studyDate=jsonMainStudyTag.get("StudyDate").getAsString();
		String studyDescription=jsonMainStudyTag.get("StudyDescription").getAsString();
		String studyID=jsonMainStudyTag.get("StudyID").getAsString();
		String studyInstanceUID=jsonMainStudyTag.get("StudyInstanceUID").getAsString();
		String studyTime=jsonMainStudyTag.get("StudyTime").getAsString();
		String parentPatientID=jsonMainStudyTag.get("ParentPatient").getAsString();
		
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
		hashmapTagPatient.put("ParentPatient", parentPatientID);
		
		return hashmapTagPatient;
		
	}
	
	
	private HashMap<String, String> getTagFromInstance(String seriesID) {
		//HashMap ou on va mettre tout les tag trouves pour les retourner a la BDD
		HashMap<String, String> hashmapTag=new HashMap<String, String>();
		
		StringBuilder sbSeries=parametre.makeGetConnectionAndStringBuilder("/series/"+seriesID);
		JsonObject seriesJson = parser.parse(sbSeries.toString()).getAsJsonObject();
		
		String parentStudy=seriesJson.get("ParentStudy").getAsString();
		hashmapTag.put("ParentStudy", parentStudy);
		StringBuilder patient=parametre.makeGetConnectionAndStringBuilder("/studies/"+parentStudy);
		JsonObject patientJson = parser.parse(patient.toString()).getAsJsonObject();
		String parentPatient=patientJson.get("ParentPatient").getAsString();
		hashmapTag.put("ParentPatient", parentPatient);

		JsonArray instanceArray=seriesJson.get("Instances").getAsJsonArray();
		StringBuilder instanceJson=parametre.makeGetConnectionAndStringBuilder("/instances/"+instanceArray.get(0)+"/tags");
		JsonObject tags = parser.parse(instanceJson.toString()).getAsJsonObject();

		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestPatient.length; i++) {
			if (tags.has(Tag_Of_Interest.tagOfInterestPatient[i])) {
				JsonObject jsonTag=tags.get(Tag_Of_Interest.tagOfInterestPatient[i]).getAsJsonObject();
				String name=jsonTag.get("Name").getAsString();
				String value=jsonTag.get("Value").getAsString();
				textAreaConsole.append(name +" "+value +",");
				
				hashmapTag.put(name, value);
			}
			
		}
		
		
		if(hashmapTag.get("PatientName").indexOf("^") != -1) {
			String[] namePatient =hashmapTag.get("PatientName").split("\\^");
			hashmapTag.put("LastName", namePatient[0]);
			hashmapTag.put("FirstName", namePatient[1]);
		}
		else {
			hashmapTag.put("LastName", hashmapTag.get("PatientName"));
			hashmapTag.put("FirstName", "N/A");
		}
		
		
		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestStudy.length; i++) {
			if (tags.has(Tag_Of_Interest.tagOfInterestStudy[i])) {
				JsonObject jsonTag=tags.get(Tag_Of_Interest.tagOfInterestStudy[i]).getAsJsonObject();
				String name=jsonTag.get("Name").getAsString();
				String value=jsonTag.get("Value").getAsString();
				textAreaConsole.append(name +" "+value +",");
				hashmapTag.put(name, value);
			}
			
		}
		
		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestSeries.length; i++) {
			if (tags.has(Tag_Of_Interest.tagOfInterestSeries[i])) {
				JsonObject jsonTag=tags.get(Tag_Of_Interest.tagOfInterestSeries[i]).getAsJsonObject();
				String name=jsonTag.get("Name").getAsString();
				String value=jsonTag.get("Value").getAsString();
				textAreaConsole.append(name +" "+value+",");
				hashmapTag.put(name, value);
			}
			
		}
		
		if (tags.has(Tag_Of_Interest.radiopharmaceuticalTag)) {
			JsonObject radiopharmaceuticalSequence = tags.get(Tag_Of_Interest.radiopharmaceuticalTag).getAsJsonObject();
			hashmapTag.put("RadiopharmaceuticalInformationSequence", radiopharmaceuticalSequence.toString());
			JsonArray radiopharmaceuticalSequenceTags=radiopharmaceuticalSequence.get("Value").getAsJsonArray();
			JsonObject radiopharmaceuticalSequenceTagsValue = radiopharmaceuticalSequenceTags.get(0).getAsJsonObject();
			for (int i=0 ; i<Tag_Of_Interest.radiopharmaceutical.length; i++) {
				if (radiopharmaceuticalSequenceTagsValue.has(Tag_Of_Interest.radiopharmaceutical[i])) {
					JsonObject jsonTag=radiopharmaceuticalSequenceTagsValue.get(Tag_Of_Interest.radiopharmaceutical[i]).getAsJsonObject();
					String name=jsonTag.get("Name").getAsString();
					String value=jsonTag.get("Value").getAsString();
					textAreaConsole.append(name +" "+value+",");
					hashmapTag.put(name, value);
				}
				
			}
		}
		
		
		if (tags.has(Tag_Of_Interest.radiationDoseModule)) {
			JsonObject radiationDoseModule = tags.get(Tag_Of_Interest.radiationDoseModule).getAsJsonObject();
			JsonArray radiationSequenceTags= radiationDoseModule.get("Value").getAsJsonArray();
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
