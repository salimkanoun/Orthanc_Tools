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

package org.petctviewer.orthanc.anonymize.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.petctviewer.orthanc.anonymize.TablePatientsModel;
import org.petctviewer.orthanc.anonymize.TableSeriesModel;
import org.petctviewer.orthanc.anonymize.TableStudiesModel;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.setup.OrthancRestApis;

public class DeleteActionMainPanel implements ActionListener{

	private String level;
	private TableSeriesModel modeleSeries;
	private TableStudiesModel modeleStudies;
	private TablePatientsModel modelePatients;
	private JTable tableauSeries;
	private JTable tableauStudies;
	private JTable tableauPatients;
	private VueAnon vue;
	private JButton searchBtn;
	private OrthancRestApis connexion;
	
	/**
	 * Gere le delete du main panel
	 * @param connexion
	 * @param level
	 * @param modeleStudies
	 * @param tableauStudies
	 * @param modeleSeries
	 * @param tableauSeries
	 * @param modelePatients
	 * @param tableauPatients
	 * @param state
	 * @param frame
	 * @param searchBtn
	 */
	public DeleteActionMainPanel(OrthancRestApis connexion, String level, TableStudiesModel modeleStudies, JTable tableauStudies, TableSeriesModel modeleSeries, 
			JTable tableauSeries, TablePatientsModel modelePatients, JTable tableauPatients, VueAnon vue, JButton searchBtn){
		this.connexion=connexion;
		this.level = level;
		this.modelePatients = modelePatients;
		this.modeleStudies = modeleStudies;
		this.modeleSeries = modeleSeries;
		this.tableauPatients = tableauPatients;
		this.tableauStudies = tableauStudies;
		this.tableauSeries = tableauSeries;
		this.vue = vue;
		this.searchBtn = searchBtn;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {

		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
			
			@Override
			protected Void doInBackground() {
				
				String url=null;
				if(level.equals("Study")){
					url="/studies/" + modeleStudies.getValueAt(tableauStudies.convertRowIndexToModel(tableauStudies.getSelectedRow()), 3);
				}else if(level.equals("Serie")){
					url="/series/" + modeleSeries.getValueAt(tableauSeries.convertRowIndexToModel(tableauSeries.getSelectedRow()), 4);
				}else{
					url="/patients/" + modelePatients.getValueAt(tableauPatients.convertRowIndexToModel(tableauPatients.getSelectedRow()), 2);
				}
				
				vue.setStateMessage("Deleting a" + level + " (Do not use the toolbox while the current operation is not done", "red", -1);
				connexion.makeDeleteConnection(url);
				return null;
				
			}
			
			@Override
			protected void done(){
				//SK QUAND LA DERNIERE STUDY OU SERIE EST EFFACE UPDATE EST NON GERER, PEUT ETRE BESOIN D EXCEPTIOn
				if(level.equals("Study")){
					modeleStudies.clear();
				}else if(level.equals("Serie")){
					modeleSeries.clear();
				}else{
					searchBtn.doClick();
				}
				vue.setStateMessage("Delete Done ", "green", 4);
				
			}
			
		};
		
		//Ask User confirmation for delete, if confirmed start the deletion in another thread
		boolean confirmation=false;
		
		if(!level.equals("Serie")) {
			int answer= JOptionPane.showConfirmDialog (null, "Are you sure you want to delete this "+level, "Delete", JOptionPane.WARNING_MESSAGE); 
			confirmation=(answer==JOptionPane.YES_OPTION);
		}else {
			confirmation=true;
		}
		
		if(confirmation) {
			worker.execute();
		}
			
	}
	
}