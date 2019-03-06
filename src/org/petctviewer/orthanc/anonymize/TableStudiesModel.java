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
import org.petctviewer.orthanc.setup.OrthancRestApis;


public class TableStudiesModel extends DefaultTableModel{

	private static final long serialVersionUID = 1L;
	private String[] entetes = {"Study date", "Study description", "Accession number", "ID", "studyObject"};
	private final Class<?>[] columnClasses = new Class<?>[] {Date.class, String.class, String.class, String.class, Study2.class};
	private OrthancRestApis connexionHttp;
	private String patientOrthancId;

	public TableStudiesModel(OrthancRestApis connexionHttp){
		super(0,5);
		//Recupere les settings
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
	
	public Study2 getStudy(int row) {
		return (Study2) this.getValueAt(row, 4);
	}
	
	public boolean isCellEditable(int row, int col){
		return false;
	}

	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addStudy(String patientOrthancId) {
		this.patientOrthancId=patientOrthancId;
		QueryOrthancData queryStudies = new QueryOrthancData(connexionHttp);
		
		ArrayList<Study2> studies=queryStudies.getStudiesOfPatient(patientOrthancId);
		
		if(studies.size()==0) {
			clear();
		}else{
			for(Study2 study :studies) {
				this.addRow(new Object[] {study.getDate(), study.getStudyDescription(), study.getAccession(), study.getOrthancId(), study});
				
			}
		}
		
	}
	/*
	 * This method clears the studies list
	 */
	public void clear(){
		this.setRowCount(0);
	}
	
	public void refresh() {
		clear();
		addStudy(patientOrthancId);
	}
	
	public ArrayList<String> getOrthancIds(){
		ArrayList<String> studyIds=new ArrayList<String>();
		for (int i=0; i<this.getRowCount(); i++) {
			studyIds.add((String) getValueAt(i, 3));
		}
		return studyIds;
	}
}
