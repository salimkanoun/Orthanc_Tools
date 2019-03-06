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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JTable;

public class TableAnonPatientsMouseListener extends MouseAdapter {

	private JTable tableau;
	private TableAnonPatientsModel modeleAnonPatient;
	private TableAnonStudiesModel modeleAnonStudies;
	private QueryOrthancData queryOrthanc;

	public TableAnonPatientsMouseListener(JTable tableau, TableAnonPatientsModel modeleAnonPatient, 
			TableAnonStudiesModel modeleAnonStudies, QueryOrthancData queryOrthanc) {
		this.tableau = tableau;
		this.modeleAnonPatient = modeleAnonPatient;
		this.modeleAnonStudies = modeleAnonStudies;
		this.queryOrthanc=queryOrthanc;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if(this.modeleAnonPatient.getRowCount() != 0){
			// We clear the details
			this.modeleAnonStudies.clear();
			
			int selectedRow =this.tableau.getSelectedRow();
			PatientAnon patientAnon=modeleAnonPatient.getPatient(selectedRow);
			
			this.modeleAnonStudies.addStudies(patientAnon);
			
		}

	}

}
