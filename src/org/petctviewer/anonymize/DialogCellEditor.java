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


package org.petctviewer.anonymize;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

//https://stackoverflow.com/questions/14153544/jtable-how-to-update-cell-using-custom-editor-by-pop-up-input-dialog-box
public class DialogCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener{

	private static final long serialVersionUID = 1L;
	String newInput;
	String oldValue;
	JButton button;
	static final String EDIT = "edit";
	
	public DialogCellEditor(){
        button = new JButton();
        button.setBackground(Color.WHITE);
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
    }
	
	@Override
	public Object getCellEditorValue() {
		return newInput;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (EDIT.equals(e.getActionCommand())){
            newInput = JOptionPane.showInputDialog("Edit", oldValue);
            if (newInput == null){
                newInput = oldValue;
            }
            fireEditingStopped();
        }
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		newInput = (String) value;
		oldValue = (String) value;
		return button;
	}

}
