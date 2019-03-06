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

package org.petctviewer.orthanc.modify;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.anonymize.VueAnon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.awt.GridLayout;
import java.awt.Dimension;
import javax.swing.JSpinner;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

@SuppressWarnings("serial")
public class Modify_Gui extends JDialog {

	private JPanel contentPane;
	private JPanel study_panel ;
	private JPanel serie_panel ;
	private JTable table_patient;
	private JPanel patient_panel ;
	private JTable table_study;
	private JTable table_serie;
	private JTable table_SharedTags;
	private JButton btnShowTags;
	private JSpinner spinner_instanceNumber;
	
	private Modify modify;
	
	private VueAnon guiParent;
	
	//Build modification list Replace and Remove
	JSONObject queryReplace=new JSONObject();
	JSONArray queryRemove=new JSONArray();

	/**
	 * Make edition dialog box
	 * @param modify
	 * @param guiParent
	 * @param state
	 */
	public Modify_Gui(Modify modify, VueAnon guiParent) {
		super(guiParent, "Modify", true);
		this.modify=modify;
		this.guiParent=guiParent;
		makegui();
	}
	
	private void makegui() {
		this.setIconImage(new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel button_panel = new JPanel();
		FlowLayout fl_button_panel = (FlowLayout) button_panel.getLayout();
		fl_button_panel.setAlignment(FlowLayout.RIGHT);
		contentPane.add(button_panel, BorderLayout.SOUTH);
		
		JLabel label = new JLabel("");
		button_panel.add(label);
		
		JCheckBox chckbxRemovePrivateTags = new JCheckBox("Remove Private Tags");
		button_panel.add(chckbxRemovePrivateTags);
		
		JButton btnModify = new JButton("Modify");
		
		btnModify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JSONObject query =modify.buildModifyQuery(queryReplace, queryRemove, chckbxRemovePrivateTags.isSelected());
				
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() {

						if (query !=null) {
							dispose();
							guiParent.setStateMessage("Modifying...", "red", -1);
							modify.sendQuery(query);
						}
						return null;
					}
					@Override
					protected void done() {
						guiParent.setStateMessage("Modified DICOM created (refresh list)", "green", -1);
						//SK Tenter le refresh Auto
					}
				};
				
				worker.execute();
				
			}
		});
		button_panel.add(btnModify);
		
		JButton btnCancel = new JButton("Cancel");
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		button_panel.add(btnCancel);
		
		JPanel center_panel = new JPanel();
		contentPane.add(center_panel, BorderLayout.CENTER);
		center_panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_tags = new JPanel();
		center_panel.add(panel_tags);
		panel_tags.setLayout(new GridLayout(0, 2, 0, 0));
		
		patient_panel = new JPanel();
		panel_tags.add(patient_panel);
		patient_panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_patient = new JScrollPane();
		patient_panel.add(scrollPane_patient);
		
		table_patient = new JTable();
		
		table_patient.setModel(new DefaultTableModel(new String[] {"Tag", "Value", "Remove"},0) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class,String.class, Boolean.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			@Override
			public boolean isCellEditable(int row, int column){  
		          if (column==0) return false;  else return true;
		     }
		});
		
		scrollPane_patient.setViewportView(table_patient);
		
		JLabel lblPatient = new JLabel("Patient");
		patient_panel.add(lblPatient, BorderLayout.NORTH);
		
		study_panel = new JPanel();
		panel_tags.add(study_panel);
		study_panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_study = new JScrollPane();
		study_panel.add(scrollPane_study, BorderLayout.CENTER);
		
		table_study = new JTable();
		
		table_study.setModel(new DefaultTableModel(new String[] {"Tag", "Value", "Remove"},0) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class, Boolean.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			@Override
			public boolean isCellEditable(int row, int column){  
		          if (column==0) return false;  else return true;
		     }
		});
		
		scrollPane_study.setViewportView(table_study);
		
		JLabel lblStudy = new JLabel("Study");
		study_panel.add(lblStudy, BorderLayout.NORTH);
		
		serie_panel = new JPanel();
		panel_tags.add(serie_panel);
		serie_panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_serie = new JScrollPane();
		serie_panel.add(scrollPane_serie, BorderLayout.CENTER);
		
		table_serie = new JTable();
		
		table_serie.setModel(new DefaultTableModel(new String[] {"Tag", "name", "Remove"},0) {
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class, Boolean.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			@Override
			public boolean isCellEditable(int row, int column){  
		          if (column==0) return false;  else return true;
		     }
		});
		
		scrollPane_serie.setViewportView(table_serie);
		
		JLabel lblSerie = new JLabel("Serie");
		serie_panel.add(lblSerie, BorderLayout.NORTH);
		
		JPanel panel_others = new JPanel();
		panel_tags.add(panel_others);
		panel_others.setLayout(new BorderLayout(0, 0));
		
		JLabel lblOther = new JLabel("Other");
		panel_others.add(lblOther, BorderLayout.NORTH);
		
		JPanel panel_other = new JPanel();
		panel_others.add(panel_other, BorderLayout.CENTER);
		panel_other.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_showTags = new JPanel();
		panel_other.add(panel_showTags, BorderLayout.CENTER);
		panel_showTags.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_Instance = new JPanel();
		panel_showTags.add(panel_Instance, BorderLayout.NORTH);
		
		JButton btnSharedTags = new JButton("Get Shared Tags");
		panel_Instance.add(btnSharedTags);
		
		JScrollPane scrollPane = new JScrollPane();
		panel_showTags.add(scrollPane);
		
		table_SharedTags = new JTable();
		table_SharedTags.setPreferredScrollableViewportSize(new Dimension(300, 100));
		
		DefaultTableModel table_customChange_model= new DefaultTableModel(new String[] {"Tag", "Name", "Value", "Delete"},0) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, Boolean.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			@Override
			public boolean isCellEditable(int row, int column){  
		          if (column==0 ||column==1 ) return false;  else return true;
		     }
		};
		table_SharedTags.setModel(table_customChange_model);
		scrollPane.setViewportView(table_SharedTags);
		
		btnSharedTags.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int answer = JOptionPane.showConfirmDialog(
					    null,
					    "Editing Shared tags will discard changes in other tables, continue ?",
					    "Shared Tags",
					    JOptionPane.YES_NO_OPTION);
				
				if (answer==JOptionPane.OK_OPTION) {
					removeAllRow (table_SharedTags);				
					JSONObject response = null;
					
					try {
						response = modify.getSharedTags();
					} catch (IOException | ParseException e1) {
						e1.printStackTrace();
					}
					Object[] sharedTags=response.keySet().toArray();
					for (int i=0; i<sharedTags.length; i++) {
						String address = (String) sharedTags[i];
						JSONObject response2 =(JSONObject) response.get(sharedTags[i]);
						String tag = response2.get("Name").toString() ;
						String value = response2.get("Value").toString() ;
						
						table_customChange_model.addRow(new Object[] {address, tag, value, Boolean.FALSE});
					}
					// Unable all other table because risk of redundency
					hideTables("all");
					queryReplace.clear();
					queryRemove.clear();
					table_SharedTags.putClientProperty("terminateEditOnFocusLost", true);
					table_SharedTags.getModel().addTableModelListener(tablechangeListenerSharedTags);
					table_SharedTags.setAutoCreateRowSorter(true);
					table_SharedTags.getRowSorter().toggleSortOrder(0);
					btnSharedTags.setEnabled(false);
					
					}

			}
		});
		
		JPanel panel_otherButtons = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_otherButtons.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_showTags.add(panel_otherButtons, BorderLayout.SOUTH);
		
		JLabel lblSeeTagInstance = new JLabel("See tag instance number");
		panel_otherButtons.add(lblSeeTagInstance);
		
		spinner_instanceNumber = new JSpinner();
		spinner_instanceNumber.setEnabled(false);
		spinner_instanceNumber.setToolTipText("only available for Serie level");
		spinner_instanceNumber.setModel(new SpinnerNumberModel(0, 0, 9999, 1));
		panel_otherButtons.add(spinner_instanceNumber);
		
		btnShowTags = new JButton("Show");
		btnShowTags.setEnabled(false);
		btnShowTags.setToolTipText("only available for Serie level");
		panel_otherButtons.add(btnShowTags);
		btnShowTags.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JSONObject instanceTags=modify.getInstanceTags((int) spinner_instanceNumber.getValue());
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					JsonParser jp = new JsonParser();
					JsonElement je = jp.parse(instanceTags.toString());
					String prettyJsonString = gson.toJson(je);
					
					JTextArea textArea = new JTextArea(prettyJsonString);
					JScrollPane scrollPane = new JScrollPane(textArea);  
					textArea.setLineWrap(true);  
					textArea.setWrapStyleWord(true); 
					scrollPane.setPreferredSize( new Dimension( 500, 500 ) );
					JOptionPane.showMessageDialog(null, scrollPane, "DICOM Tags", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException | ParseException e1) {
					e1.printStackTrace();
				}
				
			}
		});

	}
	
	public void setTables(JSONObject MainTags, String level) {
		Object[] mainPatientTag=MainTags.keySet().toArray();	
		
		if (level.equals("patient")) {
			DefaultTableModel patientModel =(DefaultTableModel) table_patient.getModel();
			for (int i=0; i<mainPatientTag.length;i++) {
			
			String tag=(String) mainPatientTag[i];
			String value=(String) MainTags.get(mainPatientTag[i]);
			patientModel.addRow(new Object[] {tag, value, Boolean.FALSE});
			//On ajoute le listener pour ecouter les changement de l'utilisateur
			}
			table_patient.putClientProperty("terminateEditOnFocusLost", true);
			table_patient.getModel().addTableModelListener(tablechangeListenerPatient);
		}
		
		else if (level.equals("study")) {
			DefaultTableModel studyModel =(DefaultTableModel) table_study.getModel();
			for (int i=0; i<mainPatientTag.length;i++) {
				String tag=(String) mainPatientTag[i];
				String value=(String) MainTags.get(mainPatientTag[i]);
				studyModel.addRow(new Object[] {tag, value, Boolean.FALSE});
			}
			table_study.putClientProperty("terminateEditOnFocusLost", true);
			table_study.getModel().addTableModelListener(tablechangeListenerStudy);
		}
		
		else if (level.equals("serie")) {
			DefaultTableModel serieModel =(DefaultTableModel) table_serie.getModel();
			for (int i=0; i<mainPatientTag.length;i++) {
				String tag=(String) mainPatientTag[i];
				String value=(String) MainTags.get(mainPatientTag[i]);
				serieModel.addRow(new Object[] {tag, value, Boolean.FALSE});
			}
			table_serie.putClientProperty("terminateEditOnFocusLost", true);
			table_serie.getModel().addTableModelListener(tablechangeListenerSeries);
			btnShowTags.setEnabled(true);
			spinner_instanceNumber.setEnabled(true);
		}
		
	}
	
	public void hideTables(String level) {
		if (level.equals("patients")) {
			study_panel.setVisible(false);
			serie_panel.setVisible(false);
		}
			
		
		else if (level.equals("studies")) {
			serie_panel.setVisible(false);
		}
		
		else if (level.equals("all")) {
			removeAllRow(table_patient);
			removeAllRow(table_serie);
			removeAllRow(table_study);
		}
			
	}
	
	
	private void removeAllRow (JTable table) {
		DefaultTableModel model =(DefaultTableModel) table.getModel();
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}
	
	
	TableModelListener tablechangeListenerPatient =new TableModelListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void tableChanged(TableModelEvent e) {
			if (e.getType()==TableModelEvent.UPDATE) {
				// If item not to remove, add to replace list and remove if present in the remove list
				if (table_patient.getValueAt(e.getFirstRow(), 2).equals(Boolean.FALSE)) {
					queryReplace.put( table_patient.getValueAt(e.getFirstRow(), 0), table_patient.getValueAt(e.getFirstRow(), 1));
					queryRemove.remove(table_patient.getValueAt(e.getFirstRow(), 0));
				}
				//else add item to replace list and remove it from remove list
				else {
					queryRemove.add(table_patient.getValueAt(e.getFirstRow(), 0));
					queryReplace.remove(table_patient.getValueAt(e.getFirstRow(), 0));
				}
			}
			
		}
    };
    
    TableModelListener tablechangeListenerStudy =new TableModelListener() {
    	@SuppressWarnings("unchecked")
    	@Override
		public void tableChanged(TableModelEvent e) {
    		if (e.getType()==TableModelEvent.UPDATE) {
    			if (!(boolean) table_study.getValueAt(e.getFirstRow(), 2)) {
					queryReplace.put( table_study.getValueAt(e.getFirstRow(), 0), table_study.getValueAt(e.getFirstRow(), 1));
					queryRemove.remove(table_study.getValueAt(e.getFirstRow(), 0));
				}
				//else add item to replace list and remove it from remove list
				else {
					queryRemove.add(table_study.getValueAt(e.getFirstRow(), 0));
					queryReplace.remove(table_study.getValueAt(e.getFirstRow(), 0));
				}
    		}
	    		
		}
    };
    
    TableModelListener tablechangeListenerSeries =new TableModelListener() {
    	@SuppressWarnings("unchecked")
    	@Override
		public void tableChanged(TableModelEvent e) {
    		if (e.getType()==TableModelEvent.UPDATE) {
				if (!(boolean) table_serie.getValueAt(e.getFirstRow(), 2)) {
				queryReplace.put( table_serie.getValueAt(e.getFirstRow(), 0), table_serie.getValueAt(e.getFirstRow(), 1));
				queryRemove.remove(table_serie.getValueAt(e.getFirstRow(), 0));
				}
				//else add item to replace list and remove it from remove list
				else {
				queryRemove.add(table_serie.getValueAt(e.getFirstRow(), 0));
				queryReplace.remove(table_serie.getValueAt(e.getFirstRow(), 0));
				}
    		}

    		
			
		}
    };
    
    TableModelListener tablechangeListenerSharedTags =new TableModelListener() {
    	@SuppressWarnings("unchecked")
    	@Override
		public void tableChanged(TableModelEvent e) {
    		if (e.getType()==TableModelEvent.UPDATE) {
	    		if (!(boolean) table_SharedTags.getValueAt(e.getFirstRow(), 3)) {
	    			queryReplace.put( table_SharedTags.getValueAt(e.getFirstRow(), 1), table_SharedTags.getValueAt(e.getFirstRow(), 2));
	    			queryRemove.remove(table_SharedTags.getValueAt(e.getFirstRow(), 1));
				}
				//else add item to replace list and remove it from remove list
				else {
					queryRemove.add(table_SharedTags.getValueAt(e.getFirstRow(), 1));
					queryReplace.remove(table_SharedTags.getValueAt(e.getFirstRow(), 1));
				}
	    		
    		}
    		
			
		}
    };
    
	

}
