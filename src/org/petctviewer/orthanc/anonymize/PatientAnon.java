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

import java.util.ArrayList;

public class PatientAnon {
	private String patientName;
	private String patientId;;
	private String id;
	private String newName;
	private String newID;
	private String newUID;
	private ArrayList<String> selectedStudyUID = new ArrayList<String>();
	
	public PatientAnon(String patientName, String patientId, String patientOrthancId, ArrayList<String> selectedStudyUID){
		this.patientName = patientName;
		this.patientId = patientId;
		this.id = patientOrthancId;
		this.newName = "";
		this.newID = "";
		this.newUID = "";
		this.selectedStudyUID = selectedStudyUID;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewID() {
		return newID;
	}

	public void setNewID(String newID) {
		this.newID = newID;
	}

	public String getNewUID() {
		return newUID;
	}

	public void setNewUID(String newUID) {
		this.newUID = newUID;
	}

	public ArrayList<String> getSelectedStudyUID() {
		return selectedStudyUID;
	}

	public void setSelectedStudyUID(ArrayList<String> selectedStudyUID) {
		this.selectedStudyUID = selectedStudyUID;
	}
	
	public void addUID(ArrayList<String> uid){
		this.selectedStudyUID.addAll(uid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PatientAnon other = (PatientAnon) obj;
		if (patientId == other.patientId) {
				return true;
		} else {
			return false;
		}
	}
	
	
}
