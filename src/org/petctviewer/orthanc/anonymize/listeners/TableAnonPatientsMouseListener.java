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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.petctviewer.orthanc.anonymize.TableAnonPatientsModel;
import org.petctviewer.orthanc.anonymize.TableAnonStudiesModel;
import org.petctviewer.orthanc.anonymize.datastorage.PatientAnon;

public class TableAnonPatientsMouseListener implements MouseListener, ListSelectionListener {

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
	public void mouseClicked(MouseEvent arg0) {

		// TODO Auto-generated method stub
		
	}

	/**
	 * Terminate edition of Study description to trigger the setvalueAt and store the new value
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		if(tableAnonStudies.isEditing()) {
			tableAnonStudies.getCellEditor().stopCellEditing();
		}
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		if(this.modeleAnonPatient.getRowCount() != 0 && this.tableAnonPatient.getSelectedRow() !=(-1)){
			// We clear the details
			this.modeleAnonStudies.clear();
			int selectedRow =this.tableAnonPatient.getSelectedRow();
			PatientAnon patientAnon=modeleAnonPatient.getPatient(selectedRow);
			
			this.modeleAnonStudies.addStudies(patientAnon);
		}	
		
	}

}
