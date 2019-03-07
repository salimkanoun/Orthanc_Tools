
package org.petctviewer.orthanc.anonymize.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

import org.petctviewer.orthanc.anonymize.TableExportSeriesModel;

public class TableExportStudiesMouseListener implements MouseListener {

	JTable tableauExportStudies;
	TableExportSeriesModel modeleExportSeries;
	
	public TableExportStudiesMouseListener(JTable tableauExportStudies, TableExportSeriesModel modeleExportSeries) {
		this.tableauExportStudies=tableauExportStudies;
		this.modeleExportSeries=modeleExportSeries;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if(tableauExportStudies.getSelectedRow() != -1){
			String studyID = (String) tableauExportStudies.getValueAt(tableauExportStudies.getSelectedRow(), 5);
			modeleExportSeries.addSerie(studyID);
		}	
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
