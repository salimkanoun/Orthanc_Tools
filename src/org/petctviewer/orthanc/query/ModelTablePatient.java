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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.DefaultTableModel;

public class ModelTablePatient extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Patient name", "Patient ID", "Study date", "Study description", "Accession number", "Study UID", "aet"};
	private Class<?>[] columnClasses = new Class<?>[] {String.class, String.class, Date.class, String.class, String.class, String.class, String.class};
	private ArrayList<PatientsDetails> patients;
	private Rest rest;


	public ModelTablePatient(Rest rest){
		super(0,7);
		this.rest = rest;
	}

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
	public void addPatient(String patientName, String patientID, String studyDate, String modality, 
			String studyDescription, String accessionNumber, String aet) {
		
		String[] queryIDandSize = rest.getQueryAnswerIndexes("Study", patientName, patientID, studyDate, modality, studyDescription, accessionNumber, aet);
		DateFormat dateParser = new SimpleDateFormat("yyyyMMdd");
		patients = new ArrayList<PatientsDetails>();
		for(int i = 0; i < Integer.parseInt(queryIDandSize[1]); i++){
			String name = (String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "PatientName").toString();
			String id = (String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "PatientID");
			String number = (String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "AccessionNumber").toString();
			Date date=null;
			try {
				date = dateParser.parse((String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "StudyDate"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String desc = (String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "StudyDescription").toString();
			String studyUID = (String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "StudyInstanceUID").toString();
			String modalityResult=(String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "Modality").toString();
			//Create Patient object with these data and add it in the list
			PatientsDetails patient = new PatientsDetails(name, id, date, desc, number, studyUID,modalityResult, aet);
			patients.add(patient);
				
		}
		updateTable();

	}
	
	public void updateTable() {
		//Erase Table
		this.setRowCount(0);
		//Insert new patients
		for (PatientsDetails patient : patients) {
			this.addRow(new Object[] {patient.getPatientName(), patient.getPatientID(), 
					patient.getStudyDate(), patient.getStudyDescription(), patient.getAccessionNumber(), patient.getStudyInstanceUID(), patient.getSourceAet()});
			
		}
	}

	/*
	 * This method clears the patients list
	 */
	public void clear(){
		setRowCount(0);
	}
	
}
