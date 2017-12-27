package org.petctviewer.anonymize;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.awt.GridLayout;
import java.awt.Dimension;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class testgui extends JFrame {

	private JPanel contentPane;
	private JPanel study_panel ;
	private JPanel serie_panel ;
	private JTable table_patient;
	private JTable table_study;
	private JTable table_serie;
	private JTable table_customChange;
	
	private Modify modify;
	private boolean enableInstance=true;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					testgui frame = new testgui();
					frame.setSize(700,650);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public testgui() {
		makegui();
	}
	public testgui(Modify modify, boolean enableInstance) {
		this.modify=modify;
		this.enableInstance=enableInstance;
		makegui();
	}
	
	private void makegui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel button_panel = new JPanel();
		FlowLayout fl_button_panel = (FlowLayout) button_panel.getLayout();
		fl_button_panel.setAlignment(FlowLayout.RIGHT);
		contentPane.add(button_panel, BorderLayout.SOUTH);
		
		JButton btnModify = new JButton("Modify");
		button_panel.add(btnModify);
		
		JButton btnCancel = new JButton("Cancel");
		button_panel.add(btnCancel);
		
		JPanel top_panel = new JPanel();
		contentPane.add(top_panel, BorderLayout.NORTH);
		
		JLabel lblModify = new JLabel("Modify");
		top_panel.add(lblModify);
		
		JPanel center_panel = new JPanel();
		contentPane.add(center_panel, BorderLayout.CENTER);
		center_panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_tags = new JPanel();
		center_panel.add(panel_tags);
		panel_tags.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel patient_panel = new JPanel();
		panel_tags.add(patient_panel);
		patient_panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_patient = new JScrollPane();
		patient_panel.add(scrollPane_patient);
		
		table_patient = new JTable();
		table_patient.setModel(new DefaultTableModel(new String[] {"Tag", "Value"},0) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				Object.class, String.class
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
		table_study.setModel(new DefaultTableModel(new String[] {"Tag", "Value"},0) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				Object.class, String.class
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
		table_serie.setModel(new DefaultTableModel(new String[] {"Tag", "Value"},0) {
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class
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
		
		JLabel lblSeeTagInstance = new JLabel("See tag instance n\u00B0");
		panel_Instance.add(lblSeeTagInstance);
		
		JSpinner spinner_instanceNumber = new JSpinner();
		panel_Instance.add(spinner_instanceNumber);
		
		JButton btnShowTags = new JButton("Show");
		btnShowTags.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeAllRow (table_customChange);
				try {
					modify.getInstanceTags((int) spinner_instanceNumber.getValue());
				} catch (IOException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// A FAIRE
				
				
			}
		});
		panel_Instance.add(btnShowTags);
		panel_Instance.setVisible(enableInstance);
		
		JScrollPane scrollPane = new JScrollPane();
		panel_showTags.add(scrollPane);
		
		table_customChange = new JTable();
		table_customChange.setPreferredScrollableViewportSize(new Dimension(300, 100));
		DefaultTableModel table_customChange_model= new DefaultTableModel(new String[] {"Tag", "Name", "Value", "Replace/Delete"},0) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		table_customChange.setModel(table_customChange_model);
		scrollPane.setViewportView(table_customChange);
		
		JPanel panel_otherButtons = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_otherButtons.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_showTags.add(panel_otherButtons, BorderLayout.SOUTH);
		
		JButton btnaddCustomTag = new JButton("Add");
		panel_otherButtons.add(btnaddCustomTag);
		
		JButton btnremoveCustomTag = new JButton("Remove");
		panel_otherButtons.add(btnremoveCustomTag);
		
		JButton btnSharedTags = new JButton("Get Shared Tags");
		btnSharedTags.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeAllRow (table_customChange);
				JSONObject response = null;
				try {
					response = modify.getSharedTags();
				} catch (IOException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Object[] sharedTags=response.keySet().toArray();
				for (int i=0; i<sharedTags.length; i++) {
					String[] row=new String[3];
					row[0] = (String) sharedTags[i];
					JSONObject response2 =(JSONObject) response.get(sharedTags[i]);
					row[1] = response2.get("Name").toString() ;
					row[2] = response2.get("Value").toString() ;
					
					table_customChange_model.addRow(row);
				}
				
			}
		});
		panel_otherButtons.add(btnSharedTags);

	}
	
	public void setTables(JSONObject MainTags, String level) {
		Object[] mainPatientTag=MainTags.keySet().toArray();
		String[] tags=new String[2];	
		if (level.equals("patient")) {
			DefaultTableModel patientModel =(DefaultTableModel) table_patient.getModel();
			for (int i=0; i<mainPatientTag.length;i++) {
			
			tags[0]=(String) mainPatientTag[i];
			tags[1]=(String) MainTags.get(mainPatientTag[i]);
			patientModel.addRow(tags);
			
			}
			
		}
		else if (level.equals("study")) {
			DefaultTableModel studyModel =(DefaultTableModel) table_study.getModel();
			for (int i=0; i<mainPatientTag.length;i++) {
				tags[0]=(String) mainPatientTag[i];
				tags[1]=(String) MainTags.get(mainPatientTag[i]);
				studyModel.addRow(tags);
			}
			
		}
		else if (level.equals("serie")) {
			DefaultTableModel serieModel =(DefaultTableModel) table_serie.getModel();
			for (int i=0; i<mainPatientTag.length;i++) {
				tags[0]=(String) mainPatientTag[i];
				tags[1]=(String) MainTags.get(mainPatientTag[i]);
				serieModel.addRow(tags);
			}
			
		}
		//A FAIRE SK DIPARAITRE PANEL NON UTILES
		//study_panel.setVisible(false);
		//serie_panel.setVisible(false);
		
	}
	
	private void removeAllRow (JTable table) {
		DefaultTableModel model =(DefaultTableModel) table.getModel();
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}
	

}
