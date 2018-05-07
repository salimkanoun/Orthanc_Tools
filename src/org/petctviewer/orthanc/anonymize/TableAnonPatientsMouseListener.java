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
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JTable;

public class TableAnonPatientsMouseListener extends MouseAdapter {

	private JTable tableau;
	private TableDataAnonPatients modele;
	private TableDataAnonStudies modeleAnonStudies;

	public TableAnonPatientsMouseListener(JTable tableau, TableDataAnonPatients modele, 
			TableDataAnonStudies modeleAnonStudies) {
		this.tableau = tableau;
		this.modele = modele;
		this.modeleAnonStudies = modeleAnonStudies;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		
		try {
			if(this.modele.getRowCount() != 0){
				// We clear the details
				this.modeleAnonStudies.clear();
				
				String patientName = this.tableau.getValueAt(this.tableau.getSelectedRow(), 0).toString();
				String patientID = this.tableau.getValueAt(this.tableau.getSelectedRow(), 1).toString();
				ArrayList<String> selectedUIDs = this.modele.getPatient(this.tableau.getSelectedRow()).getSelectedStudyUID();
				
				this.modeleAnonStudies.addStudies(patientName, patientID, selectedUIDs);
				
			}
		} catch (IOException e1) {
			e1.printStackTrace();

		} catch (Exception e1) {
			//ignore
		}
	}

}
