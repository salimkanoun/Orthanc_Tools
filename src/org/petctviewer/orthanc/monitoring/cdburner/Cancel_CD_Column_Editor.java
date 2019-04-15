package org.petctviewer.orthanc.monitoring.cdburner;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import org.petctviewer.orthanc.monitoring.Monitoring_GUI;

public class Cancel_CD_Column_Editor extends DefaultCellEditor{

	private static final long serialVersionUID = 1L;

	protected JButton button;

	private boolean isPushed;
	private Monitoring_GUI parentGui;
	
	JTable parentTable;

	public Cancel_CD_Column_Editor(JCheckBox checkBox, JTable parentTable, Monitoring_GUI parentGui) {
		super(checkBox);
		button = new JButton();
	    button.setOpaque(true);
	    button.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	          fireEditingStopped();
	        }
	      });
	    this.parentTable=parentTable;
	    this.parentGui=parentGui;
	}
	
	
	public Component getTableCellEditorComponent(JTable table, Object value,
		      boolean isSelected, int row, int column) {
		    isPushed = true;
		    return button;
		  }

	public Object getCellEditorValue() {
		if (isPushed) {
			String jobId=(String) parentTable.getValueAt(parentTable.getSelectedRow(), 7);
			String requestFileName=(String) parentTable.getValueAt(parentTable.getSelectedRow(), 8);
			parentGui.getCdBunerObject().cancelJob(jobId, requestFileName);
			
		}
		isPushed = false;
		return null;
  }

	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}
	
	protected void fireEditingStopped() {
		  super.fireEditingStopped();
	}

}
