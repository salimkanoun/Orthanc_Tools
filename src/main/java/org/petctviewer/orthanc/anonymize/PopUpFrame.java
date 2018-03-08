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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class PopUpFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JTable anonPatientTable;
	private TableDataChoices model = new TableDataChoices();
	private JTable choicesTable = new JTable(model);
	
	public PopUpFrame(JTable anonPatientTable){
		super("Multiple names found");
		this.anonPatientTable = anonPatientTable;
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		choicesTable.setPreferredScrollableViewportSize(new Dimension(700,300));
		choicesTable.setDefaultRenderer(Date.class, new DateRendererAnon());
		JScrollPane jscp = new JScrollPane(choicesTable);
		jscp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jscp.setPreferredSize(new Dimension(700,300));

		JPanel dummyPanelLabel = new JPanel(new FlowLayout());
		dummyPanelLabel.add(new JLabel("Please select the right name corresponding to this patient"));
		
		JPanel dummyPanelBtn = new JPanel(new FlowLayout());
		JButton selectBtn = new JButton("Select");
		selectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getChosenValues();
				dispose();
			}
		});
		
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		dummyPanelBtn.add(cancelBtn);
		dummyPanelBtn.add(selectBtn);
		
		mainPanel.add(dummyPanelLabel);
		mainPanel.add(jscp);
		mainPanel.add(dummyPanelBtn);

		Image image = new ImageIcon(ClassLoader.getSystemResource("OrthancIcon.png")).getImage();
		this.setIconImage(image);
		
		this.getContentPane().add(mainPanel);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
	}

	// this method fetches the data from the main jframe corresponding to
	// the new patients names from the db
	public void setData(String codeCentre, Date birthdate, ArrayList<String> oldFirstName, ArrayList<String> oldLastName, 
			ArrayList<String> newName, ArrayList<String> newId, JTable anonPatientTable){

		for(int i = 0; i < newName.size(); i++){
			Object[] row = {oldFirstName.get(i), oldFirstName.get(i), newName.get(i), newId.get(i), codeCentre, birthdate};
			model.addRow(row);
		}
	}
	
	public void getChosenValues(){
		String chosenName = choicesTable.getValueAt(choicesTable.convertRowIndexToModel(choicesTable.getSelectedRow()), 2).toString();
		String chosenId = choicesTable.getValueAt(choicesTable.convertRowIndexToModel(choicesTable.getSelectedRow()), 3).toString();
		
		this.anonPatientTable.setValueAt(chosenName, this.anonPatientTable.convertRowIndexToModel(this.anonPatientTable.getSelectedRow()), 3);
		this.anonPatientTable.setValueAt(chosenId, this.anonPatientTable.convertRowIndexToModel(this.anonPatientTable.getSelectedRow()), 4);
	}
}
