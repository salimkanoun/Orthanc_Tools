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
	private String modality;
	private String sourceAet;
	
	public PatientsDetails(String patientName, String patientID, Date studyDate,
			String studyDescription, String accessionNumber, String studyInstanceUID, String modality, String sourceAet) {
		this.patientName = patientName;
		this.patientID = patientID;
		this.accessionNumber = accessionNumber;
		this.studyDate = studyDate;
		this.studyDescription = studyDescription;
		this.studyInstanceUID = studyInstanceUID;
		this.modality=modality;
		this.sourceAet=sourceAet;
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
	
	public String getModality() {
		return this.modality;
	}
	
	public String getSourceAet() {
		return sourceAet;
	}

}