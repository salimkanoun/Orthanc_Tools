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

@SuppressWarnings("serial")
public class MetadataDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private DefaultTableModel model;

	/**
	 * Create the dialog.
	 */
	public MetadataDialog(Json_Settings settings) {
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				table = new JTable();
				table.setModel(new DefaultTableModel(new String[] {"Name", "Number"},0));
				table.putClientProperty("terminateEditOnFocusLost", true);
				model = (DefaultTableModel) table.getModel();
				//Ajout des Metadata existants
				if (settings.userMetadata.size()>0){
					String[] metadata=new String[settings.userMetadata.size()];
					settings.userMetadata.keySet().toArray(metadata);
					for (int i=0; i<metadata.length; i++){
						JsonArray meta=settings.userMetadata.get(metadata[i]).getAsJsonArray();
						model.addRow(new String[] {"Name","0"});
						table.setValueAt(metadata[i], i, 0);
						table.setValueAt(meta.get(0), i, 1);
						
					}
				}
				scrollPane.setViewportView(table);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnAdd = new JButton("Add");
				btnAdd.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						model.addRow(new String[] {"Name","0"});
					
					}
				});
				{
					JButton btnHelp = new JButton("Info");
					btnHelp.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							
							String message="Dictionary of symbolic names for the user-defined metadata.\n"
									+ "Each entry must map an unique string to an unique number between 1024 and 65535. \n "
									+ "Reserved values: The Orthanc whole-slide imaging plugin uses metadata 4200";
							JOptionPane.showMessageDialog(null,message);
						}
					});
					buttonPane.add(btnHelp);
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
						//envoie des données à IndexOrthanc
						for (int i=0; i<table.getRowCount(); i++){
							settings.addUserMetadata(table.getValueAt(i, 0).toString(), Integer.valueOf(table.getValueAt(i, 1).toString()));
						}
						// compteur à rafraichir
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
