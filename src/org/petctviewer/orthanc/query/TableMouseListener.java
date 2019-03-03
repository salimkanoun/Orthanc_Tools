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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class TableMouseListener extends MouseAdapter {

	private JTable tableau;
	private TableDataPatient modele;
	private TableDataDetails modeleDetails;
	private ListSelectionModel listSelection;
	private JComboBox<String> queryAET;
	private JLabel state;

	public TableMouseListener(JTable tableau, TableDataPatient modele, 
			TableDataDetails modeleDetails, JComboBox<String> queryAET, 
			ListSelectionModel listSelection, JLabel state) {
		this.tableau = tableau;
		this.modele = modele;
		this.modeleDetails = modeleDetails;
		this.queryAET = queryAET;
		this.listSelection = listSelection;
		this.state = state;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		// selects the row at which point the mouse is clicked
		if(!event.isControlDown()){
			// selects the row at which point the mouse is clicked
			Point point = event.getPoint();
			int currentRow = tableau.rowAtPoint(point);
			tableau.setRowSelectionInterval(currentRow, currentRow);
		}
		DateFormat df = new SimpleDateFormat("yyyyMMdd");

		// If the state's text is "Done" we make it disappear
		if(this.state != null){
			this.state.setText(null);
		}

		// We clear the details
		this.modeleDetails.clearQueriesIDs();
		this.modeleDetails.clear();

		try {
			if(this.modele.getRowCount() != 0){
				Date date = (Date)this.tableau.getValueAt(this.tableau.getSelectedRow(), 2);
				String patientName = (String)this.tableau.getValueAt(this.tableau.getSelectedRow(), 0);
				String patientID = (String)this.tableau.getValueAt(this.tableau.getSelectedRow(), 1);
				String studyDate = df.format(date); 
				String studyDescription = (String)this.tableau.getValueAt(this.tableau.getSelectedRow(), 3);
				String accessionNumber = (String)this.tableau.getValueAt(this.tableau.getSelectedRow(), 4);
				String studyInstanceUID = (String)this.tableau.getValueAt(this.tableau.getSelectedRow(), 5);

				this.modeleDetails.addDetails(patientName, patientID, studyDate, studyDescription, accessionNumber, studyInstanceUID, queryAET.getSelectedItem().toString());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.isControlDown() && SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 1) {
            int row = tableau.rowAtPoint(event.getPoint());
            listSelection.addSelectionInterval(row, row);
        }
	}
}