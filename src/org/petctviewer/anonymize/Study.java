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

package org.petctviewer.anonymize;

import java.util.Date;

public class Study {
	private String studyDescription;
	private Date date;
	private String accession;
	private String id;
	private String oldStudyInstanceUID;
	private String newStudyInstanceUID;
	private String patientName;
	private String patientID;
	
	public Study(String studyDescription, Date date, String accession, 
			String id, String oldStudyInstanceUID, String patientName, String patientID, String newStudyInstanceUID) {
		this.studyDescription = studyDescription;
		this.date = date;
		this.accession = accession;
		this.id = id;
		this.oldStudyInstanceUID = oldStudyInstanceUID;
		this.patientName = patientName;
		this.patientID = patientID;
		this.newStudyInstanceUID = newStudyInstanceUID;
	}

	public String getStudyDescription() {
		return studyDescription;
	}

	public void setStudyDescription(String studyDescription) {
		this.studyDescription = studyDescription;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	public String getOldStudyInstanceUID() {
		return oldStudyInstanceUID;
	}

	public void setOldStudyInstanceUID(String oldStudyInstanceUID) {
		this.oldStudyInstanceUID = oldStudyInstanceUID;
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

	public String getNewStudyInstanceUID() {
		return newStudyInstanceUID;
	}

	public void setNewStudyInstanceUID(String newStudyInstanceUID) {
		this.newStudyInstanceUID = newStudyInstanceUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((studyDescription == null) ? 0 : studyDescription.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Study other = (Study) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (studyDescription == null) {
			if (other.studyDescription != null)
				return false;
		} else if (!studyDescription.equals(other.studyDescription))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (accession == null) {
			if (other.accession != null)
				return false;
		} else if (!accession.equals(other.accession))
			return false;
		if (patientName == null) {
			if (other.patientName != null)
				return false;
		} else if (!patientName.equals(other.patientName))
			return false;
		Study s = (Study) obj;
		return this.getStudyDescription().equals(s.getStudyDescription())&&
				this.getDate().equals(s.getDate()) &&
				this.getAccession().equals(s.getAccession()) &&
				this.getPatientName().equals(s.getPatientName()) &&
				this.getId().equals(s.getId());
	}
	
	
}
