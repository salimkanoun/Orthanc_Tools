package org.petctviewer.orthanc.anonymize.datastorage;

import java.util.ArrayList;
import java.util.Date;

public class Study2Anon extends Study2 {
	
	private String newStudyDescription=null;
	private String newAnonymizedStudyOrthancId=null;

	public Study2Anon(String studyDescription, Date date, String accession, String StudyOrthancId, String patientName,
			String patientID, String patientOrthancId, String studyInstanceUid, ArrayList<Serie> childSeries) {
		super(studyDescription, date, accession, StudyOrthancId, patientName, patientID, patientOrthancId,studyInstanceUid, childSeries);
		
	}
	
	@Override
	public String getStudyDescription() {
		if(newStudyDescription==null) {
			return studyDescription;
		}else {
			return newStudyDescription;
		}
		
	}
	
	public void setNewAnonymizedStudyOrthancId(String orthancId) {
		this.newAnonymizedStudyOrthancId=orthancId;
	}
	
	public Study2Anon(Study2 study) {
		super(study.getStudyDescription(), study.getDate(), study.getAccession(), study.getOrthancId(), study.getPatientName(),
				study.getPatientID(), study.getParentPatientId(), study.getStudyInstanceUid(), study.childSeries);
	}
	
	public void setNewStudyDescription(String newStudyDescription) {
		this.newStudyDescription=newStudyDescription;
	}
	
	public String getNewStudyDescription() {
		if (newStudyDescription==null) {
			return studyDescription;
		}else {
			return newStudyDescription;
		}
	}
	
	public String getNewAnonymizedOrthancStudyId() {
		return newAnonymizedStudyOrthancId;
	}

}
