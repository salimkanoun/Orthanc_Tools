/**
Copyright (C) 2017 VONGSALAT Anousone

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


package org.petctviewer.orthanc.query;

import java.util.Date;


public class PatientsDetails {
	private String patientName;
	private String patientID;
	private String accessionNumber;
	private Date studyDate;
	private String studyDescription;
	private String studyInstanceUID;
	private String queryID;
	private String sourceAet;
	private int answserNumber;
	
	public PatientsDetails(String patientName, String patientID, Date studyDate,
			String studyDescription, String accessionNumber, String studyInstanceUID, String sourceAet, String queryID, int answerNumber) {
		this.patientName = patientName;
		this.patientID = patientID;
		this.accessionNumber = accessionNumber;
		this.studyDate = studyDate;
		this.studyDescription = studyDescription;
		this.studyInstanceUID = studyInstanceUID;
		this.sourceAet=sourceAet;
		this.queryID=queryID;
		this.answserNumber=answerNumber;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getPatientID() {
		return patientID;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public Date getStudyDate() {
		return this.studyDate;
	}

	public String getStudyDescription() {
		return this.studyDescription;
	}

	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}
	
	public String getSourceAet() {
		return sourceAet;
	}
	
	public String getQueryID() {
		return this.queryID;
	}
	
	public int getAnswerNumber() {
		return this.answserNumber;
	}
	
	

}