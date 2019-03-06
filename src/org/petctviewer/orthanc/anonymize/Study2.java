package org.petctviewer.orthanc.anonymize;

import java.util.ArrayList;
import java.util.Date;

public class Study2 {

	protected String studyDescription;
	protected Date date;
	protected String accession;
	protected String StudyOrthancId;
	protected String patientName;
	protected String patientID;
	protected String patientOrthancId;
	protected ArrayList<Serie> childSeries;
	
	public Study2(String studyDescription, Date date, String accession,
			String StudyOrthancId, String patientName, String patientID, String patientOrthancId, ArrayList<Serie> childSeries) {
		this.studyDescription = studyDescription;
		this.date = date;
		this.accession = accession;
		this.StudyOrthancId = StudyOrthancId;
		this.patientName = patientName;
		this.patientID = patientID;
		this.childSeries=childSeries;
		this.patientOrthancId=patientOrthancId;
	}

	public String getStudyDescription() {
		return studyDescription;
	}
	
	public String getOrthancId() {
		return StudyOrthancId;
	}

	public Date getDate() {
		return date;
	}
	
	public String getAccession() {
		return accession;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getPatientID() {
		return patientID;
	}
	
	public String getParentPatientId() {
		return patientOrthancId;
	}
	
	public ArrayList<Serie> getSeries() {
		return childSeries;
	}
	
	public int numberOfSeries() {
		return childSeries.size();
	}
	

}
