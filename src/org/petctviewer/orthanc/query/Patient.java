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


public class Patient {
	private String patientName;
	private String patientID;
	private String accessionNumber;
	private Date studyDate;
	private String studyDescription;
	private String studyInstanceUID;
	private String modality;
	
	public Patient(String patientName, String patientID, Date studyDate,
			String studyDescription, String accessionNumber, String studyInstanceUID, String modality) {
		super();

		this.patientName = patientName;
		this.patientID = patientID;
		this.accessionNumber = accessionNumber;
		this.studyDate = studyDate;
		this.studyDescription = studyDescription;
		this.studyInstanceUID = studyInstanceUID;
		this.modality=modality;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientID() {
		return patientID;
	}

	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public Date getStudyDate() {
		return this.studyDate;
	}

	public void setStudyDate(Date studyDate) {
		this.studyDate = studyDate;
	}

	public String getStudyDescription() {
		return this.studyDescription;
	}

	public void setStudyDescription(String studyDescription) {
		this.studyDescription = studyDescription;
	}

	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}

	public void setStudyInstanceUID(String studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
	}
	
	public String getModality() {
		return this.modality;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		if (patientID == null) {
			if (other.patientID != null)
				return false;
		} else if (!patientID.equals(other.patientID))
			return false;
		if (patientName == null) {
			if (other.patientName != null)
				return false;
		} else if (!patientName.equals(other.patientName))
			return false;
		if (accessionNumber == null) {
			if (other.accessionNumber != null)
				return false;
		} else if (!accessionNumber.equals(other.accessionNumber))
			return false;
		if (studyDate == null) {
			if (other.studyDate != null)
				return false;
		} else if (!studyDate.equals(other.studyDate))
			return false;
		if (studyDescription == null) {
			if (other.studyDescription != null)
				return false;
		} else if (!studyDescription.equals(other.studyDescription))
			return false;
		if (studyInstanceUID == null) {
			if (other.studyInstanceUID != null)
				return false;
		} else if (!studyInstanceUID.equals(other.studyInstanceUID))
			return false;
		Patient patient = (Patient) obj;
		return this.patientName.equals(patient.getPatientName()) &&
				this.getPatientID().equals(patient.getPatientID()) &&
				this.getAccessionNumber().equals(patient.getAccessionNumber()) &&
				this.getStudyDate().equals(patient.getStudyDate()) &&
				this.getStudyInstanceUID().equals(patient.getStudyInstanceUID()) &&
				this.getStudyDescription().equals(patient.getStudyDescription());
	}

}