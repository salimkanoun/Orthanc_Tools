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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.petctviewer.orthanc.monitoring.Monitoring_GUI;
import org.petctviewer.orthanc.setup.Run_Orthanc;

public class CloseWindowAdapter extends WindowAdapter{

	private VueAnon mainFrame;
	private ArrayList<String>  zipContent;
	private TableAnonPatientsModel patientAnonList;
	private TableExportStudiesModel exportlist;
	private Monitoring_GUI monitoring;
	private Run_Orthanc runOrthanc;

	public CloseWindowAdapter(VueAnon mainFrame, ArrayList<String> zipContent, 
			TableAnonPatientsModel patientAnonList, TableExportStudiesModel exportlist, Monitoring_GUI monitoring, Run_Orthanc runOrthanc){
		this.exportlist = exportlist;
		this.mainFrame = mainFrame;
		this.zipContent = zipContent;
		this.patientAnonList = patientAnonList;
		this.monitoring=monitoring;
		this.runOrthanc=runOrthanc;
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		if(zipContent.size()>0 || patientAnonList.getRowCount()>0 || exportlist.getRowCount()>0 || monitoring.isRunningMonitoringService()){
			int PromptResult = JOptionPane.showConfirmDialog(null,"Are you sure you want to exit?","Orthanc Tools",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
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
			runOrthanc.stopOrthanc(mainFrame.connexionHttp);
		}
		mainFrame.dispose();
	}
}
