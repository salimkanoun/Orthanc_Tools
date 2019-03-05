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

import org.petctviewer.orthanc.setup.OrthancRestApis;

public class TableExportStudiesModel extends DefaultTableModel{

	private static final long serialVersionUID = 1L;
	private String[] entetes = {"Patient name", "Patient ID", "Study date", "Study description", "Accession number", "ID", "studyObject"};
	private final Class<?>[] columnClasses = new Class<?>[] {String.class, String.class, Date.class, String.class, String.class, String.class, Study2.class};
	private OrthancRestApis connexionHttp;

	public TableExportStudiesModel(OrthancRestApis connexionHttp){
		super(0,7);
		//Recupere les settings
		this.connexionHttp=connexionHttp;
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
	public Class<?> getColumnClass(int column){
		return columnClasses[column];
	}
	
	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addStudy(String studyOrthancID) {
		
		QueryOrthancData queryStudies = new QueryOrthancData(connexionHttp);
		
		ArrayList<Study2> studies=queryStudies.getStudiesOfPatient(studyOrthancID);
		
		for(Study2 study :studies) {
			this.addRow(new Object[] {study.getPatientName(),study.getPatientID(),study.getDate(), study.getStudyDescription(), study.getAccession(), study.getOrthancId(), study});	
		}
		
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
	
	/*
	 * This method clears the studies list
	 */
	public void clear(){
		this.setRowCount(0);
	}
}
