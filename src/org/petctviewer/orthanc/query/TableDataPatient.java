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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

public class TableDataPatient extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Patient name", "Patient ID", "Study date", "Study description", "Accession number", "Study UID"};
	private final Class<?>[] columnClasses = new Class<?>[] {String.class, String.class, Date.class, String.class, String.class, String.class};
	private ArrayList<Patient> patients = new ArrayList<Patient>();
	private Rest rest;

	public TableDataPatient(Rest rest){
		super();
		this.rest = rest;
	}

	public int getRowCount(){
		return patients.size();
	}

	public int getColumnCount(){
		return entetes.length;
	}

	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int column){
		return columnClasses[column];
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		switch(columnIndex){
		case 0:
			return patients.get(rowIndex).getPatientName();
		case 1:
			return patients.get(rowIndex).getPatientID();
		case 3:
			return patients.get(rowIndex).getStudyDescription();
		case 2:
			return patients.get(rowIndex).getStudyDate();
		case 4:
			return patients.get(rowIndex).getAccessionNumber();
		case 5:
			return patients.get(rowIndex).getStudyInstanceUID();
		default:
			return null; //Ne devrait jamais arriver
		}
	}
	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addPatient(String patientName, String patientID, String studyDate, String modality, 
			String studyDescription, String accessionNumber, String aet) throws Exception{
		DateFormat parser = new SimpleDateFormat("yyyyMMdd");
		int i;
		String[] queryIDandSize = rest.getQueryAnswerIndexes("Study", patientName, patientID, studyDate, modality, studyDescription, accessionNumber, aet);
		
		Patient p;
		for(i = 0; i < Integer.parseInt(queryIDandSize[1]); i++){
			String name = (String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "PatientName").toString();
			String id = (String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "PatientID");
			String number = (String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "AccessionNumber").toString();
			Date date = parser.parse((String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "StudyDate"));
			String desc = (String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "StudyDescription").toString();
			String studyUID = (String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "StudyInstanceUID").toString();
			String modalityResult=(String)rest.getValue(rest.getIndexContent(queryIDandSize[0],i), "Modality").toString();
			p = new Patient(name, id, date, desc, number, studyUID,modalityResult);
			if(!patients.contains(p)){
				patients.add(p);
				fireTableRowsInserted(patients.size() - 1, patients.size() - 1);
			}
		}

	}

	public void removePatient(int rowIndex){
		this.patients.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	/*
	 * This method gets every available AETs and put them in an Object[]
	 */
	public Object[] getAETs() throws IOException{
		Object[] listeAETs = null;
		listeAETs = rest.getAET();
		return listeAETs;
	}

	/*
	 * This method clears the patients list
	 */
	public void clear(){
		if(this.getRowCount() !=0){
			for(int i = this.getRowCount(); i > 0; i--){
				this.removePatient(i-1);
			}
		}
	}
}
