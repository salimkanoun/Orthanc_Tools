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

package org.petctviewer.anonymize;

import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class AnonActionProfileListener extends AbstractAction{

	private static final long serialVersionUID = 1L;
	private JComboBox<Object> anonProfiles;
	private JLabel profileLabel;
	private JRadioButton radioBodyCharac1;
	private JRadioButton radioBodyCharac2;
	private JRadioButton radioDates1;
	private JRadioButton radioDates2;
	private JRadioButton radioBd2;
	private JRadioButton radioBd1;
	private JRadioButton radioPt1;
	private JRadioButton radioPt2;
	private JRadioButton radioSc1;
	private JRadioButton radioSc2;
	private JRadioButton radioDesc1;
	private JRadioButton radioDesc2;
	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");

	public AnonActionProfileListener(JComboBox<Object> anonProfiles, JLabel profileLabel, JRadioButton radioBodyCharac1, 
			JRadioButton radioBodyCharac2, JRadioButton radioDates1, JRadioButton radioDates2, JRadioButton radioBd2, 
			JRadioButton radioBd1, JRadioButton radioPt1, JRadioButton radioPt2,
			JRadioButton radioSc1, JRadioButton radioSc2, JRadioButton radioDesc1, JRadioButton radioDesc2){
		this.profileLabel = profileLabel;
		this.anonProfiles = anonProfiles;
		this.radioBodyCharac1 = radioBodyCharac1;
		this.radioBodyCharac2 = radioBodyCharac2;
		this.radioDates1 = radioDates1;
		this.radioDates2 = radioDates2;
		this.radioBd2 = radioBd2;
		this.radioBd1 = radioBd1;
		this.radioPt1 = radioPt1;
		this.radioPt2 = radioPt2;
		this.radioSc1 = radioSc1;
		this.radioSc2 = radioSc2;
		this.radioDesc1 = radioDesc1;
		this.radioDesc2 = radioDesc2;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(anonProfiles.getSelectedItem().equals("Default") || anonProfiles.getSelectedItem().equals("Full clearing")){
			radioBodyCharac1.setEnabled(false);
			radioBodyCharac2.setEnabled(false);
			radioDates1.setEnabled(false);
			radioDates2.setEnabled(false);
			radioBd1.setEnabled(false);
			radioBd2.setEnabled(false);
			radioPt1.setEnabled(false);
			radioPt2.setEnabled(false);
			radioSc1.setEnabled(false);
			radioSc2.setEnabled(false);
			radioDesc1.setEnabled(false);
			radioDesc2.setEnabled(false);
			
			if(anonProfiles.getSelectedItem().equals("Default")){
				this.profileLabel.setText("Default profile");
				radioBodyCharac1.setSelected(true);
				radioDates1.setSelected(true);
				radioBd2.setSelected(true);
				radioPt2.setSelected(true);
				radioSc2.setSelected(true);
				radioDesc1.setSelected(true);
			}else{
				this.profileLabel.setText("Full clearing profile");
				radioBodyCharac2.setSelected(true);
				radioDates2.setSelected(true);
				radioBd2.setSelected(true);
				radioPt2.setSelected(true);
				radioSc2.setSelected(true);
				radioDesc2.setSelected(true);
			}	
		}else{
			this.profileLabel.setText("Custom profile");
			radioBodyCharac1.setEnabled(true);
			radioBodyCharac2.setEnabled(true);
			radioDates1.setEnabled(true);
			radioDates2.setEnabled(true);
			radioBd2.setEnabled(true);
			radioBd1.setEnabled(true);
			radioPt1.setEnabled(true);
			radioPt2.setEnabled(true);
			radioSc1.setEnabled(true);
			radioSc2.setEnabled(true);
			radioDesc1.setEnabled(true);
			radioDesc2.setEnabled(true);
			
			int bodyCharacReg = jprefer.getInt("bodyCharac", 0);
			int datesReg = jprefer.getInt("Dates", 0);
			int bdReg = jprefer.getInt("BD", 0);
			int ptReg = jprefer.getInt("PT", 0);
			int scReg = jprefer.getInt("SC", 0);
			int descReg = jprefer.getInt("DESC", 0);
			
			switch (bodyCharacReg) {
			case 0:
				radioBodyCharac1.setSelected(true);
				break;
			case 1:
				radioBodyCharac2.setSelected(true);
				break;
			default:
				break;
			}
			switch (datesReg) {
			case 0:
				radioDates1.setSelected(true);
				break;
			case 1:
				radioDates2.setSelected(true);
				break;
			default:
				break;
			}
			switch (bdReg) {
			case 0:
				radioBd1.setSelected(true);
				break;
			case 1:
				radioBd2.setSelected(true);
				break;
			default:
				break;
			}
			switch (ptReg) {
			case 0:
				radioPt1.setSelected(true);
				break;
			case 1:
				radioPt2.setSelected(true);
				break;
			default:
				break;
			}
			switch (scReg) {
			case 0:
				radioSc1.setSelected(true);
				break;
			case 1:
				radioSc2.setSelected(true);
				break;
			default:
				break;
			}
			switch (descReg) {
			case 0:
				radioDesc1.setSelected(true);
				break;
			case 1:
				radioDesc2.setSelected(true);
				break;
			default:
				break;
			}
		}
	}
}

