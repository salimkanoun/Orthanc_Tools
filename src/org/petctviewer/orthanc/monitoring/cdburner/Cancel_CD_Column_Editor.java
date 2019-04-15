package org.petctviewer.orthanc.monitoring.cdburner;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

public class Cancel_CD_Column_Editor extends DefaultCellEditor{

	private static final long serialVersionUID = 1L;

	protected JButton button;

	  private String label;

	  private boolean isPushed;

	public Cancel_CD_Column_Editor(JCheckBox checkBox) {
		super(checkBox);
		// TODO Auto-generated constructor stub
	}
	
	
	public Component getTableCellEditorComponent(JTable table, Object value,
		      boolean isSelected, int row, int column) {
		    if (isSelected) {
		      System.out.println("selected");
		    } else {
		    	System.out.println("not selected");
		    }
		    label = (value == null) ? "" : value.toString();
		    button.setText(label);
		    isPushed = true;
		    return button;
		  }

		  public Object getCellEditorValue() {
		    if (isPushed) {
		      // 
		      // 
		      JOptionPane.showMessageDialog(button, label + ": Ouch!");
		      // System.out.println(label + ": Ouch!");
		    }
		    isPushed = false;
		    return new String(label);
		  }

		  public boolean stopCellEditing() {
		    isPushed = false;
		    return super.stopCellEditing();
		  }

		  protected void fireEditingStopped() {
		    super.fireEditingStopped();
		  }

}
