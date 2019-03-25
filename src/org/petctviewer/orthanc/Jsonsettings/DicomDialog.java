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

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.google.gson.JsonObject;

import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class DicomDialog extends JDialog {
	private JTable table;

	/**
	 * Create the dialog.
	 */
	public DicomDialog(Json_Settings settings) {
		setModal(true);
		setBounds(100, 100, 900, 300);
		getContentPane().setLayout(new BorderLayout());
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.setModel(new DefaultTableModel( new Object[] { "Name", "AET", "IP", "Port", "Manifacturer"},0));
				//Permet de valider la table à la perte du focus
				table.putClientProperty("terminateEditOnFocusLost", true);
				TableColumn manifacturer=table.getColumnModel().getColumn(4);
				String[] manifacturerString=new String[] { "Generic", "GenericNoWildcardInDates", "GenericNoUniversalWildcard", "StoreScp", "ClearCanvas", "Dcm4Chee", "Vitrea"};
				DefaultCellEditor celleditor= new DefaultCellEditor(new JComboBox<String>(manifacturerString));
				manifacturer.setCellEditor(celleditor);
				
				//On Ajoute les données du JSON si disponibles
				if (settings.dicomNode.size()>0){
					//On recupere le nombre d'AET disponible et leur noms
					String [] aetDispo=new String[settings.dicomNode.size()];
					settings.dicomNode.keySet().toArray(aetDispo);
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					
					//On boucle pour remplir le tableau
					for (int i=0; i<aetDispo.length; i++){
						//On cree la nouvelle ligne
						model.addRow(new Object[]{"Name", "AET", "IP", "0", "GenericNoUniversalWildcard"});
						//On recupere les variable des arrays de la Hasmap des dicomNode
						table.setValueAt(aetDispo[i], i, 0);
						table.setValueAt(settings.dicomNode.get(aetDispo[i]).getAsJsonArray().get(0).getAsString(), i, 1);
						table.setValueAt(settings.dicomNode.get(aetDispo[i]).getAsJsonArray().get(1).getAsString(), i, 2);
						table.setValueAt(settings.dicomNode.get(aetDispo[i]).getAsJsonArray().get(2).getAsString(), i, 3);
						//Le manifacturer n'est pas obligatoire
						if (settings.dicomNode.get(aetDispo[i]).getAsJsonArray().size()==3) table.setValueAt("Generic", i, 4);
						if (settings.dicomNode.get(aetDispo[i]).getAsJsonArray().size()==4) table.setValueAt(settings.dicomNode.get(aetDispo[i]).getAsJsonArray().get(3).getAsString(), i, 4);
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
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						//On vide l'ancienne variable des dicom node (pour effacer ceux qui aurait ete supprimee)
						settings.dicomNode=new JsonObject();
						//On en construit une nouvelle à partir des valeurs du tableau
						for (int i=0; i<table.getRowCount();i++)
						{
							settings.addDicomNode(table.getValueAt(i, 0).toString(), table.getValueAt(i, 1).toString(), table.getValueAt(i, 2).toString(), Integer.valueOf(table.getValueAt(i, 3).toString()), table.getValueAt(i, 4).toString());
						}
						//on ferme la fenetre
						dispose();
						
					}
				});
				{
					JButton tips=new JButton("info");
					tips.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String message= ("A fourth parameter is available to enable patches for specific PACS manufacturers.\n The allowed values are currently: \n Generic : default value \n GenericNoWildcardInDates(to replace * by in date fields)"
									+ "\n GenericNoUniversalWildcard (to erase * in all fields) "
									+ "\n StoreScp (storescp tool from DCMTK) "
									+ "\n ClearCanvas"
									+ "\n Dcm4Chee"
									+ "\n Vitrea");
							JOptionPane.showMessageDialog(null,message);
						}});
					
					buttonPane.add(tips);
				}
				{
					JButton btnAdd = new JButton("Add");
					btnAdd.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							DefaultTableModel model = (DefaultTableModel) table.getModel();
							model.addRow(new Object[]{"Name", "AET", "IP", "0", "GenericNoUniversalWildcard"});
							
						}
					});
					buttonPane.add(btnAdd);
				}
				{
					JButton btnRemove = new JButton("Remove");
					btnRemove.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							if (table.getSelectedRow()!=-1) {
							DefaultTableModel model = (DefaultTableModel) table.getModel();
							model.removeRow(table.getSelectedRow());
							}
						}
					});
					buttonPane.add(btnRemove);
				}
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//On vide la liste des dicomnodes avant de la reconstruire
						settings.dicomNode=new JsonObject();
						for (int i=0; i<table.getRowCount(); i++){
							settings.addDicomNode(table.getValueAt(i, 0).toString(), table.getValueAt(i, 1).toString(), table.getValueAt(i, 2).toString(), Integer.valueOf(table.getValueAt(i, 3).toString()), table.getValueAt(i, 4).toString());
						}
						//on ferme
						dispose();
					}
				});
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
