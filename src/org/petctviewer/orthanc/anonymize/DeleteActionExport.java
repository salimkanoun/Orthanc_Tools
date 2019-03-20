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

import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.petctviewer.orthanc.setup.OrthancRestApis;

public class DeleteActionExport{

	private String url;
	private JTable tableauExportStudies;
	private TableExportStudiesModel modeleExportStudies;
	private JTable tableauExportSeries;
	private TableExportSeriesModel modeleExportSeries;
	private OrthancRestApis connexion;
	/**
	 * Gere le delete de l'export tab
	 * @param connexion
	 * @param tableauExportStudies
	 * @param modeleExportStudies
	 */
	public DeleteActionExport(OrthancRestApis connexion,JTable tableauExportStudies, TableExportStudiesModel modeleExportStudies){
		this.connexion=connexion;
		this.tableauExportStudies = tableauExportStudies;
		this.modeleExportStudies = modeleExportStudies;
	}

	public DeleteActionExport(OrthancRestApis connexion, JTable tableauExportSeries, TableExportSeriesModel modeleExportSeries){
		this.connexion=connexion;
		this.tableauExportSeries = tableauExportSeries;
		this.modeleExportSeries = modeleExportSeries;
	}

	public void delete() {
		if(tableauExportStudies != null){
			this.url="/studies/" + modeleExportStudies.getValueAt(tableauExportStudies.convertRowIndexToModel(tableauExportStudies.getSelectedRow()), 5);
		}else{
			this.url="/series/" + modeleExportSeries.getValueAt(tableauExportSeries.convertRowIndexToModel(tableauExportSeries.getSelectedRow()), 4);
		}
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() {
				connexion.makeDeleteConnection(url);
				return null;
			}
		};
		worker.execute();
	}

}
