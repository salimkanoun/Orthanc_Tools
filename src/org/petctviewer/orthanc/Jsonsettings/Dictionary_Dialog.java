package org.petctviewer.orthanc.Jsonsettings;
/**
Copyright (C) 2017 KANOUN Salim
This
 program is free software; you can redistribute it and/or modify
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@SuppressWarnings("serial")
public class Dictionary_Dialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private DefaultTableModel model;

	/**
	 * Create the dialog.
	 */
	public Dictionary_Dialog(Json_Settings settings) {
		setModal(true);
		setBounds(100, 100, 700, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			table = new JTable();
			table.setModel(new DefaultTableModel(new String[] {"Name","Nickname", "VR", "Minimum", "Maximum", "Private Creator"},0));
			table.putClientProperty("terminateEditOnFocusLost", true);
			model = (DefaultTableModel) table.getModel();
			//Ajout des Dictionnary existant
			if (settings.dictionary.size()>0){
				String[] indexDictionary=new String[settings.dictionary.size()];
				settings.dictionary.keySet().toArray(indexDictionary);
				for (int i=0 ; i<indexDictionary.length; i++){
					JsonArray dictionaire=settings.dictionary.get(indexDictionary[i]).getAsJsonArray();
					model.addRow(new Object[]{"Name","Nickname", "VR", "Min", "Maximum", "Private Creator"});
					table.setValueAt(indexDictionary[i], i, 0);
					table.setValueAt(dictionaire.get(0).toString(), i, 1);
					table.setValueAt(dictionaire.get(1).toString(), i, 2);
					table.setValueAt(dictionaire.get(2).toString(), i, 3);
					table.setValueAt(dictionaire.get(3).toString(), i, 4);
					table.setValueAt(dictionaire.get(4).toString(), i, 5);
				}
				
			}
			
			// AJOUTER LES DICTIONARY EXISTANT
		}
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			scrollPane.setViewportView(table);
			
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnAdd = new JButton("Add");
				btnAdd.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						model.addRow(new Object[] {"Name","NickName", "VR", "1", "1", "Private Creator"});
					}
				});
				{
					JButton btnInfo = new JButton("Info");
					btnInfo.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String message= "A new tag in the dictionary of DICOM tags that are known to Orthanc.\n"
									+ "Each line must contain the Name of tag (formatted as 2 hexadecimal numbers), \n"
									+ "the value representation (2 upcase characters),\n"
									+ "a nickname for the tag, \n"
									+ "possibly the minimum multiplicity (> 0 with defaults to 1), \n"
									+ "possibly the maximum multiplicity (0 means arbitrary multiplicity, defaults to 1), and \n"
									+ "possibly the Private Creator (for private tags)";
									JOptionPane.showMessageDialog(null,message);
						}
					});
					buttonPane.add(btnInfo);
				}
				buttonPane.add(btnAdd);
			}
			{
				JButton btnRemove = new JButton("Remove");
				btnRemove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (table.getSelectedRow()!=-1){
							model.removeRow(table.getSelectedRow());
						}
						
					}
				});
				buttonPane.add(btnRemove);
			}
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//On efface le dictionnaire existant avant de le re creer
						settings.dictionary=new JsonObject();
						for (int i=0; i<table.getRowCount();i++){
							settings.addDictionary(table.getValueAt(i, 0).toString(), table.getValueAt(i, 1).toString(),table.getValueAt(i, 2).toString(), Integer.parseInt(table.getValueAt(i, 3).toString()), Integer.parseInt(table.getValueAt(i, 4).toString()), table.getValueAt(i, 5).toString());
						}
						//On ferme
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
