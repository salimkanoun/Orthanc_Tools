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

package org.petctviewer.orthanc.query;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.michaelbaranov.microba.calendar.DatePicker;

public class FilterAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private JLabel state;
	private JPanel checkboxes;
	private TableDataPatient modeleH;
	private TableDataDetails modeleDetailsH;
	private DatePicker from;
	private DatePicker to;
	private JComboBox<Object> queryAET;
	private String name;
	private String id;

	public FilterAction(JLabel state, JPanel checkboxes, TableDataPatient modeleH, TableDataDetails modeleDetailsH, 
			DatePicker from, DatePicker to, JComboBox<Object> queryAET) {
		super("Filter");
		this.state = state;
		this.checkboxes = checkboxes;
		this.modeleH = modeleH;
		this.modeleDetailsH = modeleDetailsH;
		this.from = from;
		this.to = to;
		this.queryAET = queryAET;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(state != null){
			state.setText(null);
		}
		
		// We store the relevant data for the new query
		if(modeleH.getRowCount()!=0){
			this.name = (String)modeleH.getValueAt(0,0);
			this.id = (String)modeleH.getValueAt(0,1);
	
		}
		
		// Making a DateFormat for the query
		DateFormat df = new SimpleDateFormat("yyyyMMdd");

		// We append a StringBuilder with every selected modalities.
		// We append "\\\\" in order to get the double \ which allows for multiple modalities in the query
		StringBuilder modalities = new StringBuilder();
		for(Component c : checkboxes.getComponents()){
			if(c instanceof JCheckBox){
				if(((JCheckBox) c).isSelected()){
					if((((JCheckBox) c).getText()) == "CT-PT"){
						modalities.append("CT\\\\PT");
						modalities.append("\\\\");
					}else{
						modalities.append((((JCheckBox) c).getText()));
						modalities.append("\\\\");
					}
				}
			}
		}
		// If the checkbox is the last chosen checkbox, we delete the '\\\\' at the end
		if(modalities.length() != 0 && modalities.charAt(modalities.length() - 1) == '\\'){
			modalities.deleteCharAt(modalities.length() - 1);
			if(modalities.charAt(modalities.length() - 1) == '\\'){
				modalities.deleteCharAt(modalities.length() - 1);
			}
		}

		// We clear the tables completely before any queries
		modeleH.clear();
		modeleDetailsH.clear();

		// We make the query, based on the user's input
		try {
			if(modalities.toString().length() == 0){
				modalities.append("*");
			}
			modeleH.addPatient(this.name, this.id, df.format(from.getDate().getTime())+"-"+df.format(to.getDate().getTime()), 
					modalities.toString(), "*", "*", queryAET.getSelectedItem().toString());

		} catch (IOException e1) {
			e1.printStackTrace();

		} catch (Exception e1) {
			// Ignore
		}
	}
}