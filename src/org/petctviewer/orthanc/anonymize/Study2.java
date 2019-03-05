package org.petctviewer.orthanc.anonymize;

import java.util.ArrayList;
import java.util.Date;

public class Study2 {

	private String studyDescription;
	private Date date;
	private String accession;
	private String StudyOrthancId;
	private String patientName;
	private String patientID;
	private String patientOrthancId;
	private ArrayList<Serie> childSeries;
	
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
