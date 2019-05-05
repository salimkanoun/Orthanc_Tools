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

import javax.swing.JOptionPane;

import org.petctviewer.orthanc.anonymize.VueAnon;

public class Window_Custom_Listener implements WindowListener{

	private VueAnon gui;
	
	public Window_Custom_Listener(VueAnon vue){
		this.gui = vue;
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		if(gui.isCurrentWork()){
			int PromptResult = JOptionPane.showConfirmDialog(gui,"Are you sure you want to exit?","Orthanc Tools",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
			if(PromptResult==JOptionPane.YES_OPTION) {
				closeAll();
			}
		}else{
			closeAll();
		}
	}
	
	private void closeAll() {
		if (gui.getMonitoring().isRunningMonitoringService())  gui.getMonitoring().closeAllMonitoringServices();
		if(gui.getRunOrthanc()!=null && gui.getRunOrthanc().getIsStarted()) {
			gui.getRunOrthanc().stopOrthanc(gui.getOrthancApisConnexion());
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
