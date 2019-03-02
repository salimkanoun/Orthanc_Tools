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
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.petctviewer.orthanc.*;
import org.petctviewer.orthanc.setup.ParametreConnexionHttp;

public class TableDataExportStudies extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	private String[] entetes = {"Patient name", "Patient ID", "Study date", "Study description", "Accession number", "ID"};
	private final Class<?>[] columnClasses = new Class<?>[] {String.class, String.class, Date.class, String.class, String.class, String.class};
	private ArrayList<Study> studies = new ArrayList<Study>();
	private ArrayList<String> ids = new ArrayList<String>();
	private ParametreConnexionHttp connexionHttp;

	public TableDataExportStudies(ParametreConnexionHttp connexionHttp){
		super();
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

	@Override
	public int getRowCount() {
		return studies.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return studies.get(rowIndex).getPatientName();
		case 1:
			return studies.get(rowIndex).getPatientID();
		case 2:
			return studies.get(rowIndex).getDate();
		case 3:
			return studies.get(rowIndex).getStudyDescription();
		case 4:
			return studies.get(rowIndex).getAccession();
		case 5:
			return studies.get(rowIndex).getId();
		default:
			return null; //Ne devrait jamais arriver
		}
	}

	public ArrayList<Study> getStudiesList(){
		return this.studies;
	}

	public void removeStudy(int rowIndex){
		this.studies.remove(rowIndex);
		this.ids.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addStudy(String patientName, String patientID, String studyID) throws IOException, ParseException{
		
		DateFormat parser = new SimpleDateFormat("yyyyMMdd");
		
		StringBuilder studydetails= connexionHttp.makeGetConnectionAndStringBuilder("/studies/"+studyID);
		
		JSONParser parserJson=new JSONParser();
		JSONObject response = null;
		try {
			response=(JSONObject) parserJson.parse(studydetails.toString());
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject mainTags=(JSONObject) response.get("MainDicomTags");
		
		//Get and parse date
		String dateString=mainTags.get("StudyDate").toString();
		Date dateValue;
		if (!dateString.equals("")) dateValue=parser.parse(dateString); else dateValue=null;
		
		String[] id = {studyID};
		String[] description = {(String) mainTags.get("StudyDescription")};
		String[] accession = {(String) mainTags.get("AccessionNumber")};
		Date[] date = {dateValue};
		String[] studyInstanceUID = {(String) mainTags.get("StudyInstanceUID")};
		
		

		for(int i = 0; i < id.length; i++){
			Study s = new Study(description[i], null, accession[i], id[i], null, patientName, patientID, studyInstanceUID[i]);
			if(date[i]!=null){
				s = new Study(description[i], date[i], accession[i], id[i], null, patientName, patientID, studyInstanceUID[i]);
			}
			if(!this.studies.contains(s)){
				this.studies.add(s);
				fireTableRowsInserted(studies.size() - 1, studies.size() - 1);
				this.ids.add(s.getId());
			}
		}
	}

	public void clearIdsList(){
		this.ids.removeAll(ids);
	}
	
	public ArrayList<String> getOrthancIds(){
		return this.ids;
	}

	/*
	 * This method clears the studies list
	 */
	public void clear(){
		if(this.getRowCount() !=0){
			for(int i = this.getRowCount(); i > 0; i--){
				this.removeStudy(i-1);
			}
		}
	}
}
