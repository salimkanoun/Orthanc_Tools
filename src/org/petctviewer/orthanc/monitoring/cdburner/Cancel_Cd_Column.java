package org.petctviewer.orthanc.monitoring.cdburner;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class Cancel_Cd_Column extends JButton implements TableCellRenderer  {
	private static final long serialVersionUID = 1L;
	
	public Cancel_Cd_Column() {
		super("Cancel Job");

	}


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		
		return this;
	}
	
	
	

}
