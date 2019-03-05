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

import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.setup.OrthancRestApis;

public class TableAnonPatientsModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Old name", "Old ID", "Old OrthancId", "New name*", "New ID*", "New OrthancId", "patientAnonObject"};
	private Class<?>[] classEntetes = {String.class, String.class, String.class, String.class, String.class, String.class, Patient.class};
	private OrthancRestApis connexion;
	
	public TableAnonPatientsModel(OrthancRestApis connexion){
		super(0,7);
		this.connexion=connexion;
	}


	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int column){
		return classEntetes[column];
	}

	public boolean isCellEditable(int row, int col){
		if(col == 3 || col == 4){
			return true; 
		}
		return false;
	}

	
	public void removePatient(int rowIndex){
		this.removeRow(rowIndex);
	}

	public ArrayList<Patient> getPatientList(){
		ArrayList<Patient> patients=new ArrayList<Patient>();
		
		for(int i=0; i<this.getRowCount(); i++) {
			Patient patient=(Patient) getValueAt(i, 2);
			patients.add(patient);;
		}
		
		return patients;
	}
	
	public Patient getPatient(int rowIndex){
		return (Patient) this.getValueAt(rowIndex, 6);
	}
	
	private int searchPatientOrthancIdRow(String patientOrthancId) {
		for(int i=0; i<this.getRowCount(); i++) {
			if(this.getValueAt(i, 2)==patientOrthancId) {
				return i;
			}
		}
		return -1;
		
	}
	
	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addPatient(Patient patient) {
		
		int patientExistingRow=searchPatientOrthancIdRow(patient.getPatientOrthancId());
		if(patientExistingRow==-1) {
			//"Old name", "Old ID", "Old OrthancId", "New name*", "New ID*", "New OrthancId", "patientAnonObject"
			this.addRow(new Object[]{patient.getName(), patient.getPatientId(), patient.getPatientOrthancId(),
				"","","",patient});
		}
	}
		
	public void addStudy(Study2 study) {
		
		int patientExistingRow=searchPatientOrthancIdRow(study.getParentPatientId());
		
		if(patientExistingRow==-1) {
			
			this.addRow(new Object[]{study.getPatientName(), study.getPatientID(), study.getParentPatientId(),
				"","","",new Patient(study.getPatientName(), study.getPatientID(),null,null,study.getParentPatientId())});
			
		}else {
			this.getPatient(patientExistingRow).addStudyOrthancIDtoSelected(study.getOrthancId());
		}
		
	}

	/*
	 * This method clears the patients list
	 */
	public void clear(){
		this.setRowCount(0);
	}
}
