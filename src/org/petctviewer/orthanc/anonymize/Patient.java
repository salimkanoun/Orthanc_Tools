package org.petctviewer.orthanc.anonymize;

import java.util.ArrayList;
import java.util.HashMap;

public class Patient {
	
	protected String name;
	protected String patientId;
	protected String orthancID;
	protected String birthDate;
	protected String sex;
	protected HashMap<String, Study2> childStudies;
	
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
	
	public void storeChildStudies(QueryOrthancData queryOrthanc) {
		childStudies= new HashMap<String, Study2>();
		ArrayList<Study2> allStudies=queryOrthanc.getStudiesOfPatient(orthancID);
		for(Study2 study: allStudies) {
			this.childStudies.put(study.getOrthancId(), study);
		}
	}
	
	public Study2 getChildStudy(String studyOrthancID) {
		return childStudies.get(studyOrthancID);
	}
	



}
