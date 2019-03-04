/**
Copyright (C) 2017 VONGSALAT Anousone

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

package org.petctviewer.orthanc.query;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class TablePatientMouseListener extends MouseAdapter {

	private JTable tableauPatients;
	private TableDataPatient modelePatients;
	private TableDataSeries modeleSeries;
	private JLabel state;

	public TablePatientMouseListener(JTable tableau, TableDataPatient modele, 
			TableDataSeries modeleDetails, JLabel state) {
		this.tableauPatients = tableau;
		this.modelePatients = modele;
		this.modeleSeries = modeleDetails;
		this.state = state;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		// selects the row at which point the mouse is clicked
		if(!event.isControlDown()){
			// selects the row at which point the mouse is clicked
			Point point = event.getPoint();
			int currentRow = tableauPatients.rowAtPoint(point);
			tableauPatients.setRowSelectionInterval(currentRow, currentRow);
		}
		DateFormat df = new SimpleDateFormat("yyyyMMdd");

		// If the state's text is "Done" we make it disappear
		if(this.state != null){
			this.state.setText(null);
		}

		// We clear the details
		this.modeleSeries.clearQueriesIDs();
		this.modeleSeries.clear();

		try {
			if(this.modelePatients.getRowCount() != 0){
				Date date = (Date)this.tableauPatients.getValueAt(this.tableauPatients.getSelectedRow(), 2);
				String patientName = (String)this.tableauPatients.getValueAt(this.tableauPatients.getSelectedRow(), 0);
				String patientID = (String)this.tableauPatients.getValueAt(this.tableauPatients.getSelectedRow(), 1);
				String studyDate = df.format(date); 
				String studyDescription = (String)this.tableauPatients.getValueAt(this.tableauPatients.getSelectedRow(), 3);
				String accessionNumber = (String)this.tableauPatients.getValueAt(this.tableauPatients.getSelectedRow(), 4);
				String studyInstanceUID = (String)this.tableauPatients.getValueAt(this.tableauPatients.getSelectedRow(), 5);
				String aet = (String)this.tableauPatients.getValueAt(this.tableauPatients.getSelectedRow(), 6);

				this.modeleSeries.addDetails(patientName, patientID, studyDate, studyDescription, accessionNumber, studyInstanceUID, 
						aet);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.isControlDown() && SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 1) {
            int row = tableauPatients.rowAtPoint(event.getPoint());
            tableauPatients.addRowSelectionInterval(row, row);
        }
	}
}