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

package org.petctviewer.orthanc.OTP.standalone;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.anonymize.datastorage.Study2;

public class TableOTPStudiesModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Patient Name", "Patient ID", "Study description*", "Study date", "New Name", "new ID", "New Description", "studyOrthancId", "studyObject"};
	private Class<?>[] typeEntetes = {String.class, String.class, String.class, Date.class, String.class, String.class, String.class, String.class, Study2.class};

	public TableOTPStudiesModel(){
		super(0,9);
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
		if(col == 2){
			return true; 
		}
		return false;
	}
	
	public ArrayList<Study2> getStudyList(){
		ArrayList<Study2> studies=new ArrayList<Study2>();
		
		for(int i=0; i<this.getRowCount(); i++) {
			Study2 studyAnon=(Study2) getValueAt(i, 8);
			studies.add(studyAnon);
		}
		
		return studies;
	}
	
	
	public void addStudy(Study2 study) {
		this.addRow(new Object[] {study.getPatientName(), study.getParentPatientId(), study.getStudyDescription(), study.getDate(),"","","", study.getOrthancId(), study });	
	}
	
	/*
	 * This method clears the series list
	 */
	public void clear(){
		this.setRowCount(0);
	}
	
}
