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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.petctviewer.orthanc.*;

import javax.swing.table.AbstractTableModel;

public class TableDataAnonPatients extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Old name", "Old ID", "Old UID", "New name*", "New ID*", "New UID", "birthdate"};
	private ArrayList<PatientAnon> patients = new ArrayList<PatientAnon>();

	public TableDataAnonPatients(){
		super();
	}

	@Override
	public int getColumnCount() {
		return entetes.length;
	}

	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}

	@Override
	public int getRowCount() {
		return patients.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return patients.get(rowIndex).getPatientName();
		case 1:
			return patients.get(rowIndex).getPatientId();
		case 2:
			return patients.get(rowIndex).getId();
		case 3:
			return patients.get(rowIndex).getNewName();
		case 4:
			return patients.get(rowIndex).getNewID();
		case 5:
			return patients.get(rowIndex).getNewUID();
		case 6:
			return patients.get(rowIndex).getBirthdate();
		default:
			return null; //Ne devrait jamais arriver
		}
	}

	public boolean isCellEditable(int row, int col){
		if(col == 3 || col == 4){
			return true; 
		}
		return false;
	}

	public void setValueAt(Object value, int row, int col) {
		switch (col) {
		case 3:
			patients.get(row).setNewName((String)value);
			break;
		case 4:
			patients.get(row).setNewID((String)value);
			break;
		default:
			break;
		}
		fireTableCellUpdated(row, col);
	}

	public void removePatient(int rowIndex){
		this.patients.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	public ArrayList<PatientAnon> getPatientList(){
		return this.patients;
	}
	
	public PatientAnon getPatient(int rowIndex){
		return this.patients.get(rowIndex);
	}
	
	public void removeStudy(String uid){
		for(PatientAnon p : patients){
			if(p.getSelectedStudyUID().contains(uid)){
				p.getSelectedStudyUID().remove(uid);
			}
		}
	}
	
	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addPatient(ParametreConnexionHttp connexion, String patientName, String patientID, Date birthdate, ArrayList<String> selectedStudies) throws IOException, ParseException{
		PatientAnon p = new PatientAnon(patientName, patientID, "", birthdate, selectedStudies);
		if(!this.patients.contains(p)){
			this.patients.add(p);
			fireTableRowsInserted(patients.size() - 1, patients.size() - 1);
		}else{
			for(PatientAnon p2 : this.patients){
				if(p2.equals(p)){
					p2.addUID(selectedStudies);
				}
			}
		}
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
