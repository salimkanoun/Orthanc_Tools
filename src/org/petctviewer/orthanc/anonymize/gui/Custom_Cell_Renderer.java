package org.petctviewer.orthanc.anonymize.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class Custom_Cell_Renderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus, int row, int col) {
		
		super.getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
		if((boolean) value==true) {
			setBackground(Color.red);
		}else {
			setBackground(Color.green);
		}
		
		
		return this;
	}

}

