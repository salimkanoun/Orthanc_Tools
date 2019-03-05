package org.petctviewer.orthanc.anonymize;

import java.util.ArrayList;
import java.util.Date;

public class Study2Anon extends Study2 {
	
	private String newStudyDescription;

	public Study2Anon(String studyDescription, Date date, String accession, String StudyOrthancId, String patientName,
			String patientID, String patientOrthancId, ArrayList<Serie> childSeries) {
		super(studyDescription, date, accession, StudyOrthancId, patientName, patientID, patientOrthancId, childSeries);
	}

}
