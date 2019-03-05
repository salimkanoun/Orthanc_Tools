package org.petctviewer.orthanc;

public class Patient {
	
	private String name;
	private String patientId;
	private String orthancID;
	private String birthDate;
	private String sex;
	
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

}
