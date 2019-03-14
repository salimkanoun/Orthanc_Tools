package org.petctviewer.orthanc.query.listeners;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.petctviewer.orthanc.query.QueryRetrieve;
import org.petctviewer.orthanc.query.VueQuery;
import org.petctviewer.orthanc.query.datastorage.SerieDetails;
import org.petctviewer.orthanc.query.datastorage.StudyDetails;

public class Retrieve_Action extends AbstractAction{
	
	private static final long serialVersionUID = 1L;
	private VueQuery gui;
	private boolean main;
	private QueryRetrieve rest;
	
	public Retrieve_Action(VueQuery gui, boolean main){
		this.main=main;
		this.gui=gui;
		this.rest=gui.getRestObject();	
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		SwingWorker<Void,Void> worker= new SwingWorker<Void,Void> () {

			@Override
			protected Void doInBackground() throws Exception {
				gui.getRetrieveButton(main).setEnabled(false);
				gui.setWorkingBoolean(true);
				JTable lastFocusedTable = gui.getLastFocusedTable(main);
				int[] selectedrows=lastFocusedTable.getSelectedRows();
				System.out.println(selectedrows.length);
				for(int i=0; i<selectedrows.length ; i++) {
					Object details = lastFocusedTable.getValueAt(selectedrows[i], lastFocusedTable.getColumnCount()-1);
					gui.getStatusLabel(main).setText("<html>Retrieve state " + (i+1) + "/" + selectedrows.length + 
							" <font color='red'> (Do not touch any buttons or any tables while the retrieve is not done)</font></html>");

					if(details instanceof StudyDetails) {
						rest.retrieve( ((StudyDetails) details).getQueryID(), ((StudyDetails) details).getAnswerNumber(), gui.getRetrieveAet(main) );
						
					}else if(details instanceof SerieDetails) {
						rest.retrieve(((SerieDetails) details).getIdQuery(), ((SerieDetails) details).getAnswerNumber(),  gui.getRetrieveAet(main));
						
					}
				}
				return null;
			}
			
			@Override
			protected void done(){
				try {
					get();
					gui.getStatusLabel(main).setText("<html><font color='green'>The data have successfully been retrieved.</font></html>");
				} catch (Exception e) {
					gui.getStatusLabel(main).setText("<html><font color='red'>Error During Retrieve</font></html>");
				}
				gui.getRetrieveButton(main).setEnabled(true);
				gui.setWorkingBoolean(false);
			}
			
		};
		
		worker.execute();	
		
	}
}
