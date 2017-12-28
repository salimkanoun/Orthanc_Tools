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

package org.petctviewer.anonymize;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.json.simple.JSONObject;

import org.petctviewer.*;

public class TableDataPatientsAnon extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Patient Name", "Patient ID", "ID", "Birthdate"};
	private ArrayList<PatientAnon> patients = new ArrayList<PatientAnon>();
	private final Class<?>[] columnClasses = new Class<?>[] {String.class, String.class, String.class, Date.class};
	private ParametreConnexionHttp connexionHttp;

	public TableDataPatientsAnon(ParametreConnexionHttp connexionHttp){
		super();
		//Set des settings
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
			return patients.get(rowIndex).getBirthdate();
		default:
			return null; //Ne devrait jamais arriver
		}
	}

	public void removePatient(int rowIndex){
		this.patients.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addPatient(String inputType, String input, String date, String studyDesc) throws IOException, ParseException{
		DateFormat parser = new SimpleDateFormat("yyyyMMdd");
		
		QueryFillStore queryPatients = new QueryFillStore(connexionHttp, "patients", inputType, input, date, studyDesc);
		
		//On recupere la liste des reponses du niveau patients 
		
		List<JSONObject> jsonResponsesPatient=queryPatients.getJsonResponse();
		
		//On prepare les variables de stockage
		String[] name = new String[jsonResponsesPatient.size()];
		String[] patientID = new String[jsonResponsesPatient.size()];
		String[] id = new String[jsonResponsesPatient.size()];
		String[] birthdateBrut = new String[jsonResponsesPatient.size()];
		Date[] birthdate = new Date[jsonResponsesPatient.size()];
		
		//On boucle pour extraire les valeurs des JSONs
		for(int i=0; i<jsonResponsesPatient.size();i++){
			JSONObject mainDicomTag=(JSONObject) jsonResponsesPatient.get(i).get("MainDicomTags");
			name[i]=(String) mainDicomTag.get("PatientName");
			patientID[i]=((String) mainDicomTag.get("PatientID"));
			id[i]=(String) (String)jsonResponsesPatient.get(i).get("ID");
			birthdateBrut[i]=(String) mainDicomTag.get("PatientBirthDate");
			// SK VOIR SI ON REPASSE PAS EN STRING POUR METTRE UNKNOW EN CAS DE DATE ABSENTE
			if (! birthdateBrut[i].equals("")) birthdate[i]= parser.parse(birthdateBrut[i]) ; else birthdate[i]=null;
		}
		
		for(int i = 0; i < id.length; i++){
			PatientAnon p = new PatientAnon(name[i], patientID[i], id[i], null, null);
			if(birthdate[i] != null){
				p = new PatientAnon(name[i], patientID[i], id[i], birthdate[i], null);
			}
			if(!this.patients.contains(p) && id[i].length() > 0){
				this.patients.add(p);
				fireTableRowsInserted(patients.size() - 1, patients.size() - 1);
			}
		}
	}
	
	/*
	 * This method clears the series list
	 */
	public void clear(){
		if(this.getRowCount() !=0){
			for(int i = this.getRowCount(); i > 0; i--){
				this.removePatient(i-1);
			}
		}
	}
}
