/**
Copyright (C) 2017 KANOUN Salim

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

package org.petctviewer.orthanc.query.autoquery.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.Orthanc_Tools;
import org.petctviewer.orthanc.query.datastorage.StudyDetails;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class AutoQueryShowResultDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	boolean validate;

	
	//SK AJOUTER EXPORT VERS MAIN FRAME DANS ANONYMISATION LIST ET EXPORT LIST
	
	/**
	 * Create the dialog.
	 */
	public AutoQueryShowResultDialog() {
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(850, 250));
		table.putClientProperty("terminateEditOnFocusLost", true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(new DefaultTableModel(new Object[] {"Last Name", "First Name", "ID", "Accession Nb", "Study Date From","Study Date To", "Modality", "Study Description" },0));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoCreateRowSorter(true);
		scrollPane.setViewportView(table);
			
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("Import to retrieve list");
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				validate=true;
				dispose();
			}
		});
		
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
			
			
		JButton btnAddPatient = new JButton("Add Patient");
		btnAddPatient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.addRow(new Object[] {"*", "*", "*", "*","*", "*", "*", "*" });
			}
		});
		buttonPane.add(btnAddPatient);
	
	
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow()!=-1) {
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					int[] rows = table.getSelectedRows();
					Arrays.sort(rows);
					for(int i=(rows.length-1); i>=0; i--){
						model.removeRow(rows[i]);
					}
				}
			}
		});
		buttonPane.add(btnRemove);

		JButton btnExportToCsv = new JButton("Export to CSV");
		btnExportToCsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setSelectedFile(new File("results.csv"));
				int ouvrir=fileChooser.showSaveDialog(null);
				if(ouvrir==JFileChooser.APPROVE_OPTION) {
				//String Builder ou sera sauve le contenu du CSV
				StringBuilder csv=new StringBuilder();
				csv.append("last name"+","+"first name"+","+"id"+","+"accession"+","+"date From"+","+"date To"+","+"modality"+","+"studyDescription"+"\n");
				for (int i=0; i<table.getRowCount(); i++) {
					buildCSV(table.getValueAt(i, 0).toString(), table.getValueAt(i, 1).toString(), table.getValueAt(i, 2).toString(), table.getValueAt(i, 3).toString(), table.getValueAt(i, 4).toString(),table.getValueAt(i, 5).toString(), table.getValueAt(i, 6).toString(), table.getValueAt(i, 7).toString(), csv);
					
				}
				//On ecrit le CSV
				Orthanc_Tools.writeCSV(csv.toString(), fileChooser.getSelectedFile());
			
				}
			}
		});
		
		buttonPane.add(btnExportToCsv);
			
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			dispose();
			}
		});
		buttonPane.add(btnCancel);

		this.setSize(this.getPreferredSize());
	}
	
	/**
	 * Ajoute les patients de l'array dans la table qu'on construit
	 */
	public void populateTable(ArrayList<StudyDetails> studyList) {
		DateFormat parser = new SimpleDateFormat("yyyyMMdd");
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i=0; i<studyList.size(); i++) {
			String name=studyList.get(i).getPatientName();
			int separator=name.indexOf("^");
			String lastName=null;
			String firstName=null;
			if (separator!=-1) {
				lastName=name.substring(0, separator);
				firstName=name.substring(separator+1);
			}
			else {
				lastName=name;
				firstName="";
			}
			//On passe le  \ en \\
			String modalityString=studyList.get(i).getModalities();
			if (modalityString.contains("\\")) modalityString = modalityString.replace("\\", "\\\\");
			// on ajoute la ligne
			model.addRow(new Object[] {lastName, firstName, studyList.get(i).getPatientID(), studyList.get(i).getAccessionNumber(),parser.format(studyList.get(i).getStudyDate()), parser.format(studyList.get(i).getStudyDate()), modalityString, studyList.get(i).getStudyDescription() });
		}
	}
	
	/**
	 * populate the csv string builder for each result
	 * @param name
	 * @param id
	 * @param accession
	 * @param date
	 * @param modality
	 * @param studyDescription
	 * @param csv
	 */
	private void buildCSV(String nom, String prenom, String id, String accession, String dateFrom, String dateTo, String modality, String studyDescription, StringBuilder csv) {
		csv.append(nom+","+prenom+","+id+","+accession+","+dateFrom+","+dateTo+","+modality+","+studyDescription+"\n");
	}
	
	public JTable getTable() {
		return this.table;
	}
	
	public boolean isValidate() {
		return validate;
	}

}
