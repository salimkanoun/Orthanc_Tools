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


public class TableDataStudies extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	private String[] entetes = {"Study date", "Study description", "Accession number", "ID"};
	private final Class<?>[] columnClasses = new Class<?>[] {Date.class, String.class, String.class, String.class};
	private ArrayList<Study> studies = new ArrayList<Study>();
	private ArrayList<String> ids = new ArrayList<String>();
	private ParametreConnexionHttp connexionHttp;

	public TableDataStudies(ParametreConnexionHttp connexionHttp){
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
			return studies.get(rowIndex).getDate();
		case 1:
			return studies.get(rowIndex).getStudyDescription();
		case 2:
			return studies.get(rowIndex).getAccession();
		case 3:
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
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	/*
	 * This method adds patient to the patients list, which will eventually be used by the JTable
	 */
	public void addStudy(String patientName, String patientID, String patientUID) throws IOException, ParseException{
		ids.removeAll(ids);
		DateFormat parser = new SimpleDateFormat("yyyyMMdd");
		
		QueryFillStore queryStudies = new QueryFillStore(connexionHttp, "studies", null, patientUID, null, null);
		
		List<JSONObject> jsonResponsesPatient=queryStudies.getJsonResponse();
		
		//On prepare les variables de stockage
		String[] id = new String[jsonResponsesPatient.size()];
		String[] description = new String[jsonResponsesPatient.size()];
		String[] accession = new String[jsonResponsesPatient.size()];
		Date[] date = new Date[jsonResponsesPatient.size()];
		
		//On boucle pour extraire les valeurs des JSONs
		for(int i=0; i<jsonResponsesPatient.size();i++){
			JSONObject mainDicomTag=(JSONObject) jsonResponsesPatient.get(i).get("MainDicomTags");
			id[i]=(String) jsonResponsesPatient.get(i).get("ID");
			
			if (mainDicomTag.containsKey("StudyDescription")) {
				description[i]=((String) mainDicomTag.get("StudyDescription"));
			} else {
				description[i]="";
			}
			
			if (mainDicomTag.containsKey("AccessionNumber")) {
				accession[i]=((String) mainDicomTag.get("AccessionNumber"));
			} else {
				accession[i]="";
			}
			
			if (mainDicomTag.containsKey("StudyDate")) {
				String dateString=mainDicomTag.get("StudyDate").toString();
				if (!dateString.equals("")) date[i]=parser.parse(dateString); else date[i]=null;
			} else {
				date[i]=null;
			}
			
		}
		
		for(int i = 0; i < id.length; i++){
			Study s = new Study(description[i], null, accession[i], id[i], null, patientName, patientID, null);
			if(date[i]!=null){
				s = new Study(description[i], date[i], accession[i], id[i], null, patientName, patientID, null);
			}
			if(!this.studies.contains(s)){
				this.studies.add(s);
				fireTableRowsInserted(studies.size() - 1, studies.size() - 1);
				this.ids.add(s.getId());
			}
		}
		
	}

	public ArrayList<String> getIds(){
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
