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

package org.petctviewer.orthanc.anonymize.listeners;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.petctviewer.orthanc.anonymize.TableAnonPatientsModel;
import org.petctviewer.orthanc.anonymize.TableAnonStudiesModel;
import org.petctviewer.orthanc.anonymize.datastorage.PatientAnon;

public class TableAnonPatientsMouseListener implements ListSelectionListener {

	private JTable tableAnonPatient, tableAnonStudies;
	private TableAnonPatientsModel modeleAnonPatient;
	private TableAnonStudiesModel modeleAnonStudies;

	public TableAnonPatientsMouseListener(JTable tableAnonPatient, TableAnonPatientsModel modeleAnonPatient, 
			TableAnonStudiesModel modeleAnonStudies, JTable tableAnonStudies) {
		this.tableAnonPatient = tableAnonPatient;
		this.tableAnonStudies = tableAnonStudies;
		this.modeleAnonPatient = modeleAnonPatient;
		this.modeleAnonStudies = modeleAnonStudies;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) {
			if(this.modeleAnonPatient.getRowCount() != 0 && this.tableAnonPatient.getSelectedRow() !=(-1)){
				//SK BUG//
				//tableAnonStudies.changeSelection(tableAnonStudies.getSelectedRow(), 1, false, false);
				if(tableAnonStudies.isEditing()) {
					System.out.println("editing");
					tableAnonStudies.getCellEditor().cancelCellEditing();
				}
				// We clear the details
				this.modeleAnonStudies.clear();
				int selectedRow =this.tableAnonPatient.getSelectedRow();
				PatientAnon patientAnon=modeleAnonPatient.getPatient(selectedRow);
				
				
				this.modeleAnonStudies.addStudies(patientAnon);
			}	
		}
	}

}
