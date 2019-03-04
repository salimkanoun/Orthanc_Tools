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

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TableListSelectionListener implements ListSelectionListener{
	
	private ArrayList<Integer> rowsModelsIndexes;
	private JTable tableauDetails;
	
	public TableListSelectionListener(ArrayList<Integer> rowsModelsIndexes, JTable tableauDetails){
		this.rowsModelsIndexes = rowsModelsIndexes;
		this.tableauDetails = tableauDetails;
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()){
			try{
				// Before filling the indexes, we empty it
				rowsModelsIndexes.removeAll(rowsModelsIndexes);
				int[] selectedRows = tableauDetails.getSelectedRows();

				// We fill the list
				for(int i = 0; i < selectedRows.length; i++){
					rowsModelsIndexes.add(tableauDetails.convertRowIndexToModel(selectedRows[i]));
				}
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
}
