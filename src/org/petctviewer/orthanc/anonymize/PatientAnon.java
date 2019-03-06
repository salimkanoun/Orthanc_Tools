/**
Copyright (C) 2017 VONGSALAT Anousone & KANOUN Salim

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public v.3 License as published by
the Free Software Foundation;

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.petctviewer.orthanc.anonymize;

import java.util.HashMap;
import java.util.Set;

public class PatientAnon extends Patient{
	
	private String newPatientName;
	private String newPatientID;
	private String newOrthancId;
	private HashMap<String, Study2Anon> selectedStudyAnon = new HashMap<String, Study2Anon>();
	
	public PatientAnon(String patientName, String patientId, String birthDate, String sex, String patientOrthancId){
		super(patientName, patientId, birthDate, sex, patientOrthancId);
	}
	
	public PatientAnon(Patient patient) {
		super(patient.getName(), patient.getPatientId(), patient.getPatientBirthDate(), patient.getPatientSex(), patient.getPatientOrthancId());
	}
	
	public void setNewPatientName(String newPatientName) {
		this.newPatientName=newPatientName;
	}
	
	public void setNewPatientId(String newPatientID) {
		this.newPatientID=newPatientID;
	}
	
	public void setNewOrthancId(String newOrthancId) {
		this.newOrthancId=newOrthancId;
	}
	
	public String getNewPatientName() {
		return newPatientName;
	}
	
	public String getNewPatientId() {
		return newPatientID;
	}
	
	public String getNewOrthancId() {
		return this.newOrthancId;
	}

	public void addNewAnonymizeStudy(Study2Anon anonymizeStudy) {
		selectedStudyAnon.put(anonymizeStudy.getOrthancId(), anonymizeStudy);
	}
	
	public void addNewAnonymizeStudyFromExistingStudy(String studyOrthancId) {
		Study2Anon newAnonStudy=new Study2Anon(getChildStudy(studyOrthancId));
		selectedStudyAnon.put(studyOrthancId, newAnonStudy);
	}
	
	public HashMap<String, Study2Anon> getAnonymizeStudies() {
		return selectedStudyAnon;
	}
	
	public Study2Anon getAnonymizeStudy(String studyId) {
		return selectedStudyAnon.get(studyId);
	}
	
	public void removeOrthancIDfromAnonymize(String studyOrthancID) {
		selectedStudyAnon.remove(studyOrthancID);
	}
	
	public void addAllChildStudiesToAnonymizeList() {
		Set<String> availableStudiesId=childStudies.keySet();
		for(String studyId:availableStudiesId) {
			Study2 study =this.getChildStudy(studyId);
			Study2Anon studyAnon=new Study2Anon(study);
			selectedStudyAnon.put(studyAnon.getOrthancId(), studyAnon);
		}
		
	}
	
	
	
}
