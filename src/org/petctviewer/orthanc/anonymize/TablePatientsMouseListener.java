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

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;


public class TablePatientsMouseListener extends MouseAdapter {

	private JFrame frame;
	private JTable tableau;
	private TableDataPatientsAnon modele;
	private TableDataStudies modeleStudies;
	private TableDataSeries modeleSeries;

	public TablePatientsMouseListener(JFrame frame, JTable tableau, TableDataPatientsAnon modele, TableDataStudies modeleStudies, TableDataSeries modeleSeries, 
			ListSelectionModel listSelection) {
		this.frame = frame;
		this.tableau = tableau;
		this.modele = modele;
		this.modeleStudies = modeleStudies;
		this.modeleSeries = modeleSeries;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		this.modeleStudies.clear();
		this.modeleSeries.clear();

		try {
			if(this.modele.getRowCount() != 0 && tableau.getSelectedRow() != -1){
				String patientName = (String)this.tableau.getValueAt(this.tableau.getSelectedRow(), 0);
				String patientID = (String)this.tableau.getValueAt(this.tableau.getSelectedRow(), 1);
				String patientUID = (String)this.tableau.getValueAt(this.tableau.getSelectedRow(), 2);
				this.modeleStudies.addStudy(patientName, patientID, patientUID);
			}
		}catch (Exception e1) {
			e1.printStackTrace();
		}
		frame.pack();
	}

	
}
