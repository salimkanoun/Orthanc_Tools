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
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.google.gson.JsonArray;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class FolderDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;

	/**
	 * Create the dialog.
	 */
	public FolderDialog(boolean lua, Json_Settings settings) {
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			table = new JTable();
			table.setModel(new DefaultTableModel(new Object[]{"Path"},0));
			table.putClientProperty("terminateEditOnFocusLost", true);
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			//Si on est dans le plugin on traite la variable plugin
			if (lua==false){
				for (int i=0; i<settings.pluginsFolder.size();i++){
					model.addRow(new Object [] {settings.pluginsFolder.get(i).toString()});
				}
			}
			//Si on est dans Lua on traite la variable Lua
			if (lua==true){
				for (int i=0; i<settings.luaFolder.size();i++){
					model.addRow(new Object [] {settings.luaFolder.get(i).toString()});
				}
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
					public void actionPerformed(ActionEvent arg0) {
						JFileChooser fc=new JFileChooser();
						// Si plugin on ne selectionne que le repertoire
						if (lua==false){
							fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						}
						//Si Lua on selectionne les fichiers seulement
						if (lua==true){
							fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						}
						
						int ouvrir=fc.showOpenDialog(null);
						DefaultTableModel model = (DefaultTableModel) table.getModel();
						if (ouvrir==JFileChooser.APPROVE_OPTION) {
							File repertoire=fc.getSelectedFile().getAbsoluteFile();
							model.addRow(new Object []{repertoire.toString()});
						}
						
					}
				});
				buttonPane.add(btnAdd);
			}
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//On vide les array ancienne et on les remplace
						if (lua) settings.luaFolder=new JsonArray();
						if (!lua) settings.pluginsFolder=new JsonArray();
						DefaultTableModel model = (DefaultTableModel) table.getModel();
						for (int i=0; i<model.getRowCount();i++){
							if (!lua) settings.addplugins(model.getValueAt(i, 0).toString());
							if (lua)settings.addLua(model.getValueAt(i, 0).toString()); 
						}
						dispose();
						
					}
				});
				{
					JButton btnRemove = new JButton("Remove");
					btnRemove.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (table.getSelectedRow()!=-1){
						DefaultTableModel model = (DefaultTableModel) table.getModel();
						model.removeRow(table.getSelectedRow());
							}
						
						}
					});
					buttonPane.add(btnRemove);
				}
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
}
