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
import java.util.Date;

import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.Patient;
import org.petctviewer.orthanc.setup.OrthancRestApis;

public class TablePatientsModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Patient Name", "Patient ID", "ID", "Birthdate", "Sex", "patientObject"};
	private final Class<?>[] columnClasses = new Class<?>[] {String.class, String.class, String.class, Date.class, String.class, Patient.class};
	private OrthancRestApis connexionHttp;

	public TablePatientsModel(OrthancRestApis connexionHttp){
		super(0,6);
		//Set des settings
		this.connexionHttp=connexionHttp;
	}

	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int column){
		return columnClasses[column];
	}

	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addPatient(String inputType, String input, String date, String studyDesc) {
		
		QueryOrthancData queryPatients = new QueryOrthancData(connexionHttp);
		
		ArrayList<Patient> patients =queryPatients.findPatients(inputType, input, date, studyDesc);
		
		for (Patient patient : patients) {
			this.addRow(new Object[] {patient.getName(), patient.getPatientId(), patient.getPatientOrthancId(), null, null, patient});
		}
	}
	
	/*
	 * This method clears the series list
	 */
	public void clear(){
		this.setRowCount(0);
	}
}
