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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.petctviewer.orthanc.monitoring.Monitoring_GUI;
import org.petctviewer.orthanc.setup.Run_Orthanc;

public class CloseWindowAdapter extends WindowAdapter{

	private JFrame frame;
	private ArrayList<String> zipContent;
	private ArrayList<String> oldOrthancUIDs;
	private ArrayList<Study> listeExport;
	private Monitoring_GUI monitoring;
	private Run_Orthanc runOrthanc;

	public CloseWindowAdapter(JFrame frame, ArrayList<String> zipContent, 
			ArrayList<String> oldOrthancUIDs, ArrayList<Study> listeExport, Monitoring_GUI monitoring, Run_Orthanc runOrthanc){
		this.listeExport = listeExport;
		this.frame = frame;
		this.zipContent = zipContent;
		this.oldOrthancUIDs = oldOrthancUIDs;
		this.monitoring=monitoring;
		this.runOrthanc=runOrthanc;
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		
		if(!zipContent.isEmpty() || !oldOrthancUIDs.isEmpty() || !listeExport.isEmpty() || monitoring.isRunningMonitoringService()){
			String ObjButtons[] = {"Yes","No"};
			int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to exit?","Orthanc Tools",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
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
			runOrthanc.stopOrthanc();
		}
		frame.dispose();
	}
}
