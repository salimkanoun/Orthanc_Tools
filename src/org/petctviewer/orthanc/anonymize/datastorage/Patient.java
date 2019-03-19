package org.petctviewer.orthanc.anonymize.datastorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.petctviewer.orthanc.anonymize.QueryOrthancData;

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
	
	public void storeAllChildStudies(QueryOrthancData queryOrthanc) {
		childStudies= new HashMap<String, Study2>();
		ArrayList<Study2> allStudies=queryOrthanc.getAllStudiesOfPatient(orthancID);
		for(Study2 study: allStudies) {
			this.childStudies.put(study.getOrthancId(), study);
		}
	}
	
	public void addStudy(Study2 study) {
		if(childStudies==null) {
			childStudies= new HashMap<String, Study2>();
		}
		childStudies.put(study.getOrthancId(), study);
	}
	
	/**
	 * Return the studies of this object
	 * @return
	 */
	public ArrayList<Study2> getStudies(){
		Study2[] studyArray=childStudies.values().toArray(new Study2[0]);
		return new ArrayList<Study2>(Arrays.asList(studyArray));
	}
	
	public Study2 getChildStudy(String studyOrthancID) {
		return childStudies.get(studyOrthancID);
	}
	



}
