/**
Copyright (C) 2017 VONGSALAT Anousone

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

package org.petctviewer.orthanc.query.listeners;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ChangeTabListener implements ChangeListener{

	private JFrame frame;
	private JButton ajouter;
	private JButton filter;
	
	public ChangeTabListener(JFrame frame, JButton ajouter, JButton filter){
		this.frame = frame;
		this.ajouter = ajouter;
		this.filter = filter;
	}

	public void stateChanged(ChangeEvent changeEvent){
		JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
		int index = sourceTabbedPane.getSelectedIndex();
		if(sourceTabbedPane.getTitleAt(index).equals("History")){
			frame.getRootPane().setDefaultButton(filter);
		}else{
			frame.getRootPane().setDefaultButton(ajouter);
		}
	}
}
