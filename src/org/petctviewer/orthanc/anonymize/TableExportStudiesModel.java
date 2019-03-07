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

import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.anonymize.datastorage.Study_Anonymized;

public class TableExportStudiesModel extends DefaultTableModel{

	private static final long serialVersionUID = 1L;
	private String[] entetes = {"Patient name", "Patient ID", "Study date", "Study description", "Accession number", "orthancStudyID", "studyObject"};
	private final Class<?>[] columnClasses = new Class<?>[] {String.class, String.class, Date.class, String.class, String.class, String.class, Study_Anonymized.class};

	public TableExportStudiesModel(){
		super(0,7);
		//Recupere les settings
	}

	@Override
	public int getColumnCount() {
		return entetes.length;
	}
	
	@Override
	public boolean isCellEditable(int row, int col){
		return false;
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
	public void addStudy(Study_Anonymized studyAnonymized) {
		
		Study2 study=studyAnonymized.getAnonymizedStudy();
		this.addRow(new Object[] {study.getPatientName(),study.getPatientID(),study.getDate(), study.getStudyDescription(), study.getAccession(), study.getOrthancId(), study});	
	
	}
	
	public void removeStudy(String studyOrthancID) {
		for (int i=0; i<this.getRowCount(); i++) {
			if(this.getValueAt(i, 5).equals(studyOrthancID)) {
				this.removeRow(i);
				break;
			}
		}
		
	}
	
	public ArrayList<String> getOrthancIds() {
		ArrayList<String> studyIds=new ArrayList<String>();
		for (int i=0; i<this.getRowCount(); i++) {
			studyIds.add((String) getValueAt(i, 5));
		}
		return studyIds;
	}
	
	public ArrayList<Study_Anonymized> getAnonymizedObject(){
		ArrayList<Study_Anonymized> studies=new ArrayList<Study_Anonymized>();
		for (int i=0; i<this.getRowCount(); i++) {
			studies.add((Study_Anonymized) getValueAt(i, 6));
		}
		return studies;
	}
	
	public ArrayList<Study2> getAnonymizedStudy2Object(){
		ArrayList<Study2> studies=new ArrayList<Study2>();
		for (int i=0; i<this.getRowCount(); i++) {
			Study_Anonymized anonymized=(Study_Anonymized) getValueAt(i, 6);
			studies.add(anonymized.getAnonymizedStudy());
		}
		return studies;
	}
	
	/*
	 * This method clears the studies list
	 */
	public void clear(){
		this.setRowCount(0);
	}
}
