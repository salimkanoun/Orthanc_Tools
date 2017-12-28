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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;


public class TableStudiesMouseListener extends MouseAdapter {
	
	private JFrame frame;
	private JTable tableau;
	private JTable tableauSeries;
	private TableDataStudies modele;
	private TableDataSeries modeleSeries;
	private ListSelectionModel listSelection;

	public TableStudiesMouseListener(JFrame frame, JTable tableau, TableDataStudies modele, JTable tableauSeries,
			TableDataSeries modeleSeries, ListSelectionModel listSelection) {
		this.frame = frame;
		this.tableau = tableau;
		this.tableauSeries = tableauSeries;
		this.modele = modele;
		this.modeleSeries = modeleSeries;
		this.listSelection = listSelection;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		// We clear the details
		this.modeleSeries.clear();
		if(!event.isControlDown()){
			// selects the row at which point the mouse is clicked
			Point point = event.getPoint();
			int currentRow = tableau.rowAtPoint(point);
			tableau.setRowSelectionInterval(currentRow, currentRow);
		}
		try {
			if(this.modele.getRowCount() != 0){
				String studyID = (String)this.tableau.getValueAt(this.tableau.getSelectedRow(), 3);
				this.modeleSeries.addSerie(studyID);
				this.tableauSeries.setRowSelectionInterval(0,0);
			}
		} catch (IOException e1) {
			e1.printStackTrace();

		} catch (Exception e1) {
			//ignore
		}
		frame.pack();
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.isControlDown() && SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 1) {
            int row = tableau.rowAtPoint(event.getPoint());
            listSelection.addSelectionInterval(row, row);
        }
	}
}
