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

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class TableStudyMouseListener extends MouseAdapter {

	private JTable tableauPatients;
	private ModelTableStudy modeleStudy;
	private ModelTableSeries modeleSeries;
	private JLabel state;

	public TableStudyMouseListener(JTable tableau, ModelTableStudy modeleStudy, 
			ModelTableSeries modeleSeries, JLabel state) {
		this.tableauPatients = tableau;
		this.modeleStudy = modeleStudy;
		this.modeleSeries = modeleSeries;
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
		
		// If the state's text is "Done" we make it disappear
		if(this.state != null){
			this.state.setText(null);
		}

		// We clear the details
		this.modeleSeries.clear();
		//Get Patient Object and add it's series in Series table model
		if(this.modeleStudy.getRowCount() != 0){
			StudyDetails patient = (StudyDetails)this.tableauPatients.getValueAt(this.tableauPatients.getSelectedRow(), 6);
			this.modeleSeries.addSeriesDetails(patient);
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