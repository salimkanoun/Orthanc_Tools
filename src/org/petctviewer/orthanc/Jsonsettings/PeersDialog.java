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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONArray;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class PeersDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private DefaultTableModel model;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PeersDialog dialog = new PeersDialog(new Json_Settings());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PeersDialog(Json_Settings settings) {
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		{
			table = new JTable();
			table.setModel(new DefaultTableModel(new String[] {"Name", "URL", "Login", "Password"},0));
			table.putClientProperty("terminateEditOnFocusLost", true);
			model = (DefaultTableModel) table.getModel();
			String[] peerName=new String[settings.orthancPeer.size()];
			settings.orthancPeer.keySet().toArray(peerName);
			for (int i=0; i<settings.orthancPeer.size();i++){
				model.addRow(new String[] {"Name", "URL", "Login", "Password"});
				JSONArray peer=settings.orthancPeer.get(peerName[i]);
				table.setValueAt(peerName[i], i, 0);
				table.setValueAt(peer.get(0).toString(), i, 1);
				table.setValueAt(peer.get(1).toString(), i, 2);
				table.setValueAt(peer.get(2).toString(), i, 3);
				
			}
		}
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(table);
			contentPanel.add(scrollPane);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnAdd = new JButton("Add");
				btnAdd.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					model.addRow(new String[] {"Name", "URL", "Login", "Password"});
					}
				});
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
						//on vide les sauvegarde precedente
						settings.orthancPeer.clear();
						//On enregistre les nouveaux
						for (int i=0; i<table.getRowCount();i++){
							settings.addorthancPeer(table.getValueAt(i, 0).toString(), table.getValueAt(i, 1).toString(), table.getValueAt(i, 2).toString(), table.getValueAt(i, 3).toString());
						}
						//on quite
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
