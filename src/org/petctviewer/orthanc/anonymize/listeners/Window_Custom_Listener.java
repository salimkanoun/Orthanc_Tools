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

package org.petctviewer.orthanc.anonymize.listeners;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.petctviewer.orthanc.anonymize.TableAnonPatientsModel;
import org.petctviewer.orthanc.anonymize.TableExportStudiesModel;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.monitoring.Monitoring_GUI;
import org.petctviewer.orthanc.setup.Run_Orthanc;

public class Window_Custom_Listener implements WindowListener{

	private VueAnon gui;
	private ArrayList<String>  zipContent;
	private TableAnonPatientsModel patientAnonList;
	private TableExportStudiesModel exportlist;
	private Monitoring_GUI monitoring;
	private Run_Orthanc runOrthanc;

	public Window_Custom_Listener(VueAnon vue, ArrayList<String> zipContent, 
			TableAnonPatientsModel patientAnonList, TableExportStudiesModel exportlist, Monitoring_GUI monitoring, Run_Orthanc runOrthanc){
		this.exportlist = exportlist;
		this.gui = vue;
		this.zipContent = zipContent;
		this.patientAnonList = patientAnonList;
		this.monitoring=monitoring;
		this.runOrthanc=runOrthanc;
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		if(zipContent.size()>0 || patientAnonList.getRowCount()>0 || exportlist.getRowCount()>0 || monitoring.isRunningMonitoringService()){
			int PromptResult = JOptionPane.showConfirmDialog(gui,"Are you sure you want to exit?","Orthanc Tools",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
			if(PromptResult==JOptionPane.YES_OPTION) {
				closeAll();
			}
		}else{
			closeAll();
		}
	}
	
	private void closeAll() {
		if (monitoring.isRunningMonitoringService())  monitoring.closeAllMonitoringServices();
		if(runOrthanc.getIsStarted()) {
			runOrthanc.stopOrthanc(gui.getOrthancApisConnexion());
		}
		gui.timerState.cancel();
		gui.dispose();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		
	}
}
