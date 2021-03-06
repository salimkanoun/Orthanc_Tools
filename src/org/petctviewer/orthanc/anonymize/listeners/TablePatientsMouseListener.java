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

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.petctviewer.orthanc.anonymize.TablePatientsModel;
import org.petctviewer.orthanc.anonymize.TableSeriesModel;
import org.petctviewer.orthanc.anonymize.TableStudiesModel;
import org.petctviewer.orthanc.anonymize.datastorage.Patient;


public class TablePatientsMouseListener implements ListSelectionListener {

	private JFrame frame;
	private JTable tableau;
	private TablePatientsModel modele;
	private TableStudiesModel modeleStudies;
	private TableSeriesModel modeleSeries;

	public TablePatientsMouseListener(JFrame frame, JTable tableau, TablePatientsModel modele, TableStudiesModel modeleStudies, TableSeriesModel modeleSeries, 
			ListSelectionModel listSelection) {
		this.frame = frame;
		this.tableau = tableau;
		this.modele = modele;
		this.modeleStudies = modeleStudies;
		this.modeleSeries = modeleSeries;
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if(!arg0.getValueIsAdjusting()) {
			this.modeleStudies.clear();
			this.modeleSeries.clear();

			if(this.modele.getRowCount() != 0 && tableau.getSelectedRow() != -1){
				Patient patientObject = (Patient) this.tableau.getValueAt(this.tableau.getSelectedRow(), 5);
				this.modeleStudies.addStudy(patientObject);
			}

			frame.pack();
		}
		
		
	}

	
}
