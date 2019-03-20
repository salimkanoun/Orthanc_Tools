
package org.petctviewer.orthanc.anonymize.listeners;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.petctviewer.orthanc.anonymize.TableExportSeriesModel;
import org.petctviewer.orthanc.anonymize.datastorage.Study_Anonymized;

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
				System.out.println(tableauExportStudies.getValueAt(tableauExportStudies.getSelectedRow(),6).getClass());
				Study_Anonymized studyAnonymized =
						(Study_Anonymized) tableauExportStudies.getValueAt(tableauExportStudies.getSelectedRow(),6) ;
				modeleExportSeries.addSerie(studyAnonymized);
			}	
		}
	}

}
