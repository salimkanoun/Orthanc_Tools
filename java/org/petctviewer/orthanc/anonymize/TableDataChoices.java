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

import java.sql.Date;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class TableDataChoices extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	private String[] entetes = {"Former last name", "Former first name", "New name", "New ID", "Center code", "Birthdate"};
	private Class<?>[] typeEntetes = {String.class, String.class, String.class, String.class, String.class, Date.class};
	private ArrayList<Object[]> choices = new ArrayList<Object[]>();
	
	@Override
	public int getColumnCount() {
		return entetes.length;
	}
	
	@Override
	public int getRowCount() {
		return choices.size();
	}

	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex){
		return typeEntetes[columnIndex];
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return choices.get(rowIndex)[0];
		case 1:
			return choices.get(rowIndex)[1];
		case 2:
			return choices.get(rowIndex)[2];
		case 3:
			return choices.get(rowIndex)[3];
		case 4:
			return choices.get(rowIndex)[4];
		case 5:
			return choices.get(rowIndex)[5];
		default:
			return null; //Ne devrait jamais arriver
		}
	}

	public void addRow(Object[] row){
		if(!this.choices.contains(row)){
			this.choices.add(row);
			fireTableRowsInserted(choices.size() - 1, choices.size() - 1);
			fireTableDataChanged();
		}
	}
	
}
