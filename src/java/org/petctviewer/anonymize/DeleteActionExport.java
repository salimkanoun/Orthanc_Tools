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


package org.petctviewer.anonymize;

import java.io.IOException;

import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.petctviewer.*;

public class DeleteActionExport{

	private String url;
	private JTable tableauExportStudies;
	private TableDataExportStudies modeleExportStudies;
	private JTable tableauExportSeries;
	private TableDataExportSeries modeleExportSeries;
	private ParametreConnexionHttp connexion;
	/**
	 * Gere le delete de l'export tab
	 * @param connexion
	 * @param tableauExportStudies
	 * @param modeleExportStudies
	 */
	public DeleteActionExport(ParametreConnexionHttp connexion,JTable tableauExportStudies, TableDataExportStudies modeleExportStudies){
		this.connexion=connexion;
		this.tableauExportStudies = tableauExportStudies;
		this.modeleExportStudies = modeleExportStudies;
	}

	public DeleteActionExport(ParametreConnexionHttp connexion, JTable tableauExportSeries, TableDataExportSeries modeleExportSeries){
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
				try {
					connexion.makeDeleteConnection(url);
				} catch (IOException e) {e.printStackTrace();}
				return null;
			}
		};
		worker.execute();
	}

}
