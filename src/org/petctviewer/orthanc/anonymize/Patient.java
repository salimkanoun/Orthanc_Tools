package org.petctviewer.orthanc.anonymize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Patient {
	
	private String name;
	private String patientId;
	private String orthancID;
	private String birthDate;
	private String sex;
	private ArrayList<String> selectedStudyOrthancID = new ArrayList<String>();
	private HashMap<String, Study2> childStudies;
	
	public Patient(String name, String patientId, String birthDate, String sex, String orthancID) {
		this.name=name;
		this.patientId=patientId;
		this.orthancID=orthancID;
		this.birthDate=birthDate;
		this.sex=sex;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPatientId() {
		return this.patientId;
	}
	
	public String getPatientOrthancId() {
		return this.orthancID;
	}
	
	public String getPatientBirthDate() {
		return this.birthDate;
	}
	
	public String getPatientSex() {
		return this.sex;
	}
	
	public ArrayList<Study2> getSelectedStudyOrthancID() {
		ArrayList<Study2> selectedStudies=new ArrayList<Study2>();
		for(String studyId:selectedStudyOrthancID) {
			selectedStudies.add(childStudies.get(studyId));
		}
		return selectedStudies;
	}
	
	public void setSelectedStudy(ArrayList<String> selectedStudy) {
		selectedStudyOrthancID=selectedStudy;
	}
	
	public void addStudyOrthancIDtoSelected(String studyOrthancID) {
		selectedStudyOrthancID.add(studyOrthancID);
	}
	
	public void addStudyOrthancIDtoSelected(ArrayList<String> selectedStudy) {
		selectedStudyOrthancID.addAll(selectedStudy);
	}
	
	public void removeOrthancIDfromSelected(String studyOrthancID) {
		selectedStudyOrthancID.remove(studyOrthancID);
	}
	
	public void storeChildStudies(QueryOrthancData queryOrthanc) {
		childStudies= new HashMap<String, Study2>();
		ArrayList<Study2> allStudies=queryOrthanc.getStudiesOfPatient(orthancID);
		for(Study2 study: allStudies) {
			this.childStudies.put(study.getOrthancId(), study);

		}
	}
	
	public void selectAllChildStudies() {
		Set<String> availableStudiesId=childStudies.keySet();
		for(String studyId:availableStudiesId) {
			selectedStudyOrthancID.add(studyId);
		}
		
	}
	
	public Study2 getChildStudy(String studyOrthancID) {
		return childStudies.get(studyOrthancID);
	}

}
