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
import java.util.HashMap;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.anonymize.datastorage.PatientAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.anonymize.datastorage.Study2Anon;

public class TableAnonStudiesModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Study description*", "Study date", "studyOrthancId", "Patient id", "studyObject", "patientAnon"};
	private Class<?>[] typeEntetes = {String.class, Date.class, String.class, String.class, Study2.class};
	
	private PatientAnon patientAnon;

	public TableAnonStudiesModel(){
		super(0,6);
		
	}

	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return typeEntetes[columnIndex];
	}

	public boolean isCellEditable(int row, int col){
		if(col == 0){
			return true; 
		}
		return false;
	}
	
	public ArrayList<Study2> getStudyList(){
		ArrayList<Study2> studies=new ArrayList<Study2>();
		
		for(int i=0; i<this.getRowCount(); i++) {
			Study2 study=(Study2) getValueAt(i, 4);
			studies.add(study);
		}
		
		return studies;
	}
	
	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addStudies(PatientAnon patientAnon) {
		this.patientAnon=patientAnon;
		clear();
		HashMap<String, Study2Anon> studiesAnonmyze=patientAnon.getAnonymizeStudies();
		Set<String> keys=studiesAnonmyze.keySet();
		for(String studyID: keys) {
			Study2Anon anonStudy=studiesAnonmyze.get(studyID);
			//"Study description*", "Study date", "studyOrthancId", "Patient id", "studyObject"
			this.addRow(new Object[] {anonStudy.getStudyDescription(), anonStudy.getDate(), anonStudy.getOrthancId(), anonStudy.getPatientID(), anonStudy, patientAnon });
		}
	}
		
	public void refresh() {
		this.setRowCount(0);
		this.addStudies(patientAnon);
	}
		
	/*
	 * This method clears the series list
	 */
	public void clear(){
		this.setRowCount(0);
	}
	
}
