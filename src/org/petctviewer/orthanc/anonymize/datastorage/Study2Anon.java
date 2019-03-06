package org.petctviewer.orthanc.anonymize.datastorage;

import java.util.ArrayList;
import java.util.Date;

public class Study2Anon extends Study2 {
	
	private String newStudyDescription=null;

	public Study2Anon(String studyDescription, Date date, String accession, String StudyOrthancId, String patientName,
			String patientID, String patientOrthancId, ArrayList<Serie> childSeries) {
		super(studyDescription, date, accession, StudyOrthancId, patientName, patientID, patientOrthancId, childSeries);
		
	}
	
	@Override
	public String getStudyDescription() {
		if(newStudyDescription==null) {
			return studyDescription;
		}else {
			return newStudyDescription;
		}
		
	}
	public Study2Anon(Study2 study) {
		super(study.getStudyDescription(), study.getDate(), study.getAccession(), study.getOrthancId(), study.getPatientName(),
				study.getPatientID(), study.getParentPatientId(), study.childSeries);
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

}
