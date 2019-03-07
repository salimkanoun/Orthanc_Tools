
package org.petctviewer.orthanc.anonymize.listeners;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.petctviewer.orthanc.anonymize.TableExportSeriesModel;

public class TableExportStudiesMouseListener implements ListSelectionListener {

	JTable tableauExportStudies;
	TableExportSeriesModel modeleExportSeries;
	
	public TableExportStudiesMouseListener(JTable tableauExportStudies, TableExportSeriesModel modeleExportSeries) {
		this.tableauExportStudies=tableauExportStudies;
		this.modeleExportSeries=modeleExportSeries;
	}
	

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) {
			if(tableauExportStudies.getSelectedRow() != -1){
				System.out.println("iciSelectedStudy");
				String studyID = (String) tableauExportStudies.getValueAt(tableauExportStudies.getSelectedRow(), 5);
				modeleExportSeries.addSerie(studyID);
			}	
		}
	}

}
