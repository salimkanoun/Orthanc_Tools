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

import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

/**
 * Listener when Anonymization profile (combobox)is changed, 
 * Set predifined profile or load custom profile
 */
public class AnonActionProfileListener extends AbstractAction{

	private static final long serialVersionUID = 1L;
	private JComboBox<String> anonProfiles;
	private JLabel profileLabel;
	private JRadioButton[] settingsBodyCharButtons;
	private JRadioButton[] settingDatesButtons;
	private JRadioButton[] settingsBirthDateButtons;
	private JRadioButton[] settingsPrivateTagButtons; 
	private JRadioButton[] settingsSecondaryCaptureButtons;
	private JRadioButton[] settingsStudySerieDescriptionButtons;
	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");

	public AnonActionProfileListener(JComboBox<String> anonProfiles, JLabel profileLabel, 
			JRadioButton[] settingsBodyCharButtons,
			JRadioButton[] settingDatesButtons,
			JRadioButton[] settingsBirthDateButtons,
			JRadioButton[] settingsPrivateTagButtons, 
			JRadioButton[] settingsSecondaryCaptureButtons,
			JRadioButton[] settingsStudySerieDescriptionButtons){
		
		this.profileLabel = profileLabel;
		this.anonProfiles = anonProfiles;
		this.settingsBodyCharButtons = settingsBodyCharButtons;
		this.settingDatesButtons = settingDatesButtons;
		this.settingsBirthDateButtons = settingsBirthDateButtons;
		this.settingsPrivateTagButtons = settingsPrivateTagButtons;
		this.settingsSecondaryCaptureButtons = settingsSecondaryCaptureButtons;
		this.settingsStudySerieDescriptionButtons = settingsStudySerieDescriptionButtons;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//If predefined Default or full clearing profile unactivate buttons and set predifined settings
		if(anonProfiles.getSelectedItem().equals("Default") || anonProfiles.getSelectedItem().equals("Full clearing")){
			changeEnableRadioButtons(false);
			if(anonProfiles.getSelectedItem().equals("Default")){
				profileLabel.setText("Default profile");
				settingsBodyCharButtons[0].setSelected(true);
				settingDatesButtons[0].setSelected(true);
				settingsBirthDateButtons[1].setSelected(true);
				settingsPrivateTagButtons[1].setSelected(true);
				settingsSecondaryCaptureButtons[1].setSelected(true);
				settingsStudySerieDescriptionButtons[0].setSelected(true);
			}else{
				profileLabel.setText("Full clearing profile");
				settingsBodyCharButtons[1].setSelected(true);
				settingDatesButtons[1].setSelected(true);
				settingsBirthDateButtons[1].setSelected(true);
				settingsPrivateTagButtons[1].setSelected(true);
				settingsSecondaryCaptureButtons[1].setSelected(true);
				settingsStudySerieDescriptionButtons[1].setSelected(true);
			}
		//If Custom Profile	
		}else{
			changeEnableRadioButtons(true);
			profileLabel.setText("Custom profile");
			//Get activated button value from the registery
			int bodyCharacReg = jprefer.getInt("bodyCharac", 0);
			int datesReg = jprefer.getInt("Dates", 0);
			int bdReg = jprefer.getInt("BD", 0);
			int ptReg = jprefer.getInt("PT", 0);
			int scReg = jprefer.getInt("SC", 0);
			int descReg = jprefer.getInt("DESC", 0);
			//Select the buttons accordically
			settingsBodyCharButtons[bodyCharacReg].setSelected(true);
			settingDatesButtons[datesReg].setSelected(true);
			settingsBirthDateButtons[bdReg].setSelected(true);
			settingsPrivateTagButtons[ptReg].setSelected(true);
			settingsSecondaryCaptureButtons[scReg].setSelected(true);
			settingsStudySerieDescriptionButtons[descReg].setSelected(true);
			
		}
	}
	
	private void changeEnableRadioButtons(boolean enable) {
		for(int i=0 ; i<2; i++) {
			settingsBodyCharButtons[i].setEnabled(enable);
			settingDatesButtons[i].setEnabled(enable);
			settingsBirthDateButtons[i].setEnabled(enable);
			settingsPrivateTagButtons[i].setEnabled(enable); 
			settingsSecondaryCaptureButtons[i].setEnabled(enable);
			settingsStudySerieDescriptionButtons[i].setEnabled(enable);
		}
	}
	
}

