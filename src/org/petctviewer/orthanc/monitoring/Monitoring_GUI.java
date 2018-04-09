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

package org.petctviewer.orthanc.monitoring;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import org.petctviewer.orthanc.ParametreConnexionHttp;

import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.Box;
import java.awt.FlowLayout;

@SuppressWarnings("serial")
public class Monitoring_GUI extends JFrame {

	private JPanel contentPane;
	
	private JButton btnStopMonitoring, btnStartMonitoring;
	private Preferences jPrefer;
	
	private CD_Burner cdBurner;
	private JTextArea textAreaCD;
	
	private JLabel lbl_CD_Status, lbl_DoseMonitoring_Status;
	
	
	private boolean cdMonitoringStarted, doseMonitoringStarted;
	private JTable table_1;
	private JTextField textField_If_Autorouting;
	private JTextField textField_Modality_Study_AutroRetrieve;
	private JTextField textField_Date_AuToRetrieve;
	private JTextField textField_StudyDescription_Study;
	private JTable table;
	private JTable table_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Monitoring_GUI frame = new Monitoring_GUI(new ParametreConnexionHttp());
					frame.pack();
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
	public Monitoring_GUI(ParametreConnexionHttp parametre) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel_Main_Tab = new JPanel();
		tabbedPane.addTab("Status", null, panel_Main_Tab, null);
		
		JPanel Main_Tab = new JPanel();
		Main_Tab.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_Main_Tab.add(Main_Tab);
		Main_Tab.setLayout(new BorderLayout(0, 0));
		
		JPanel main_Panel = new JPanel();
		Main_Tab.add(main_Panel);
		main_Panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_Main_Title = new JPanel();
		main_Panel.add(panel_Main_Title, BorderLayout.NORTH);
		panel_Main_Title.setLayout(new BorderLayout(10, 10));
		
		JLabel MainLabelTitle = new JLabel("Monitoring Services");
		panel_Main_Title.add(MainLabelTitle, BorderLayout.NORTH);
		
		JPanel panel_Status = new JPanel();
		main_Panel.add(panel_Status);
		panel_Status.setLayout(new GridLayout(0, 2, 10, 10));
		
		JLabel lblCdBurner = new JLabel("CD Burner");
		panel_Status.add(lblCdBurner);
		
		lbl_CD_Status = new JLabel("Stop");
		panel_Status.add(lbl_CD_Status);
		
		JLabel lblDoseMonitoring = new JLabel("Dose Monitoring");
		panel_Status.add(lblDoseMonitoring);
		
		lbl_DoseMonitoring_Status = new JLabel("Stop");
		panel_Status.add(lbl_DoseMonitoring_Status);
		
		JPanel CD_Burner_Tab = new JPanel();
		
		tabbedPane.addTab("CD-Burner", null, CD_Burner_Tab, null);
		
		textAreaCD = new JTextArea();
		textAreaCD.setColumns(30);
		textAreaCD.setAutoscrolls(true);
		textAreaCD.setRows(5);
		DefaultCaret caret = (DefaultCaret) textAreaCD.getCaret();
		caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
						CD_Burner_Tab.setLayout(new BorderLayout(0, 0));
		
						JScrollPane scrollPane = new JScrollPane();
						CD_Burner_Tab.add(scrollPane, BorderLayout.CENTER);
						scrollPane.setViewportView(textAreaCD);
						
						JPanel panel = new JPanel();
						CD_Burner_Tab.add(panel, BorderLayout.SOUTH);
						
						btnStartMonitoring = new JButton("Start monitoring");
						btnStartMonitoring.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								cdBurner=new CD_Burner(parametre, textAreaCD);
								setCDPreference();
								if ( CD_Burner.epsonDirectory==null ||CD_Burner.fijiDirectory==null ||CD_Burner.labelFile==null || CD_Burner.dateFormatChoix==null ){
									//Message d'erreur doit faire le set de output folder
									JOptionPane.showMessageDialog(null, "Go to settings Menu to set missing paths", "Set directories and date format", JOptionPane.ERROR_MESSAGE);
								}
								
								else {
										textAreaCD.append("Monitoring Orthanc \n");
										//On ouvre le watcher dans un nouveau thread pour ne pas bloquer l'interface				
									   cdBurner.startCDMonitoring();
									   cdMonitoringStarted=true;
									   //On grise le boutton pour empecher la creation d'un nouveau watcher
									   btnStartMonitoring.setEnabled(false);
									   btnStopMonitoring.setEnabled(true);
									   updateStatusLabel();
									}			
							}
						});
						JButton btnSettings = new JButton("Settings");
						btnSettings.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								Burner_Settings settings=new Burner_Settings();
								settings.setVisible(true);
								settings.setModal(true);
								//On refresh les changement de variable � la fin de l'operation
								CD_Burner.fijiDirectory=jPrefer.get("fijiDirectory", null);
								CD_Burner.epsonDirectory=jPrefer.get("epsonDirectory", null);
								CD_Burner.labelFile=jPrefer.get("labelFile", null);
								CD_Burner.dateFormatChoix=jPrefer.get("DateFormat", null);
							}
						});
						panel.add(btnSettings);
						panel.add(btnStartMonitoring);
						
						
						
						btnStopMonitoring = new JButton("Stop Monitoring");
						btnStopMonitoring.setEnabled(false);
						btnStopMonitoring.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								cdBurner.stopCDMonitoring();
								cdMonitoringStarted=false;
								updateStatusLabel();
								textAreaCD.append("Monitoring Terminated \n");
								btnStartMonitoring.setEnabled(true);
								btnStopMonitoring.setEnabled(false);
								
							}
						});
						panel.add(btnStopMonitoring);
						
						
						JPanel Title = new JPanel();
						CD_Burner_Tab.add(Title, BorderLayout.NORTH);
						
						JLabel lblCdburnerBySassa = new JLabel("CD Burner Activity");
						Title.add(lblCdburnerBySassa);
						
						JPanel panel_tag_monitoring = new JPanel();
						tabbedPane.addTab("Tag-Monitoring", null, panel_tag_monitoring, null);
						panel_tag_monitoring.setLayout(new BorderLayout(0, 0));
						
						JPanel panel_2 = new JPanel();
						panel_tag_monitoring.add(panel_2);
						panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
						
						JPanel panel_TagMonitoring_Patients = new JPanel();
						panel_2.add(panel_TagMonitoring_Patients);
						panel_TagMonitoring_Patients.setLayout(new BorderLayout(0, 0));
						
						JLabel lblNewPatients = new JLabel("New Patients");
						panel_TagMonitoring_Patients.add(lblNewPatients, BorderLayout.NORTH);
						
						JScrollPane scrollPane_1 = new JScrollPane();
						panel_TagMonitoring_Patients.add(scrollPane_1, BorderLayout.CENTER);
						
						table_1 = new JTable();
						table_1.setPreferredScrollableViewportSize(new Dimension(50, 50));
						table_1.setModel(new DefaultTableModel(
							new Object[][] {
								{"0010,0010", "Name", Boolean.FALSE},
								{"0010,0020", "ID", null},
								{"0010,0030", "Date Of Birth", null},
								{"0010,0040", "Sex", null},
							},
							new String[] {
								"Tag", "Name", "New column"
							}
						) {
							Class[] columnTypes = new Class[] {
								String.class, Object.class, Boolean.class
							};
							public Class getColumnClass(int columnIndex) {
								return columnTypes[columnIndex];
							}
						});
						scrollPane_1.setViewportView(table_1);
						
						JPanel panel_1 = new JPanel();
						panel_TagMonitoring_Patients.add(panel_1, BorderLayout.SOUTH);
						
						JButton btnAdd = new JButton("Add");
						panel_1.add(btnAdd);
						
						JButton btnRemove = new JButton("Remove");
						panel_1.add(btnRemove);
						
						JPanel panel_TagMonitoring_Studies = new JPanel();
						panel_2.add(panel_TagMonitoring_Studies);
						panel_TagMonitoring_Studies.setLayout(new BorderLayout(0, 0));
						
						JLabel lblNewStudies = new JLabel("New Studies");
						panel_TagMonitoring_Studies.add(lblNewStudies, BorderLayout.NORTH);
						
						JPanel panel_9 = new JPanel();
						panel_TagMonitoring_Studies.add(panel_9, BorderLayout.SOUTH);
						
						JButton btnAdd_1 = new JButton("Add");
						panel_9.add(btnAdd_1);
						
						JButton btnRemove_1 = new JButton("Remove");
						panel_9.add(btnRemove_1);
						
						JScrollPane scrollPane_2 = new JScrollPane();
						panel_TagMonitoring_Studies.add(scrollPane_2, BorderLayout.CENTER);
						
						table = new JTable();
						table.setPreferredScrollableViewportSize(new Dimension(50, 50));
						table.setModel(new DefaultTableModel(
							new Object[][] {
							},
							new String[] {
								"Tag"
							}
						) {
							Class[] columnTypes = new Class[] {
								String.class
							};
							public Class getColumnClass(int columnIndex) {
								return columnTypes[columnIndex];
							}
						});
						scrollPane_2.setViewportView(table);
						
						JPanel panel_TagMonitoring_Series = new JPanel();
						panel_2.add(panel_TagMonitoring_Series);
						panel_TagMonitoring_Series.setLayout(new BorderLayout(0, 0));
						
						JLabel lblNewSeries = new JLabel("New Series");
						panel_TagMonitoring_Series.add(lblNewSeries, BorderLayout.NORTH);
						
						JPanel panel_11 = new JPanel();
						panel_TagMonitoring_Series.add(panel_11, BorderLayout.SOUTH);
						
						JButton btnAdd_2 = new JButton("Add");
						panel_11.add(btnAdd_2);
						
						JButton btnRemove_2 = new JButton("Remove");
						panel_11.add(btnRemove_2);
						
						JScrollPane scrollPane_3 = new JScrollPane();
						panel_TagMonitoring_Series.add(scrollPane_3, BorderLayout.CENTER);
						
						table_2 = new JTable();
						table_2.setModel(new DefaultTableModel(
							new Object[][] {
							},
							new String[] {
								"Tag"
							}
						) {
							Class[] columnTypes = new Class[] {
								String.class
							};
							public Class getColumnClass(int columnIndex) {
								return columnTypes[columnIndex];
							}
						});
						table_2.setPreferredScrollableViewportSize(new Dimension(50, 50));
						scrollPane_3.setViewportView(table_2);
						
						JPanel panel_7 = new JPanel();
						panel_tag_monitoring.add(panel_7, BorderLayout.SOUTH);
						
						JButton btnStart = new JButton("Start Collecting");
						panel_7.add(btnStart);
						
						JPanel panel_AutoRouting = new JPanel();
						tabbedPane.addTab("Auto-Routing", null, panel_AutoRouting, null);
						
						JPanel panel_AutoRouting_main = new JPanel();
						panel_AutoRouting.add(panel_AutoRouting_main);
						panel_AutoRouting_main.setLayout(new BorderLayout(0, 0));
						
						JPanel panel_sendTo = new JPanel();
						panel_AutoRouting_main.add(panel_sendTo, BorderLayout.SOUTH);
						
						JLabel lblSendTo = new JLabel("Send To");
						panel_sendTo.add(lblSendTo);
						
						JComboBox comboBox_remoteAET = new JComboBox();
						panel_sendTo.add(comboBox_remoteAET);
						
						JPanel panel_4 = new JPanel();
						panel_AutoRouting_main.add(panel_4, BorderLayout.WEST);
						panel_4.setLayout(new GridLayout(0, 1, 0, 0));
						
						JPanel panel_Each = new JPanel();
						panel_4.add(panel_Each);
						
						JLabel lblEach_1 = new JLabel("Each ");
						panel_Each.add(lblEach_1);
						
						JCheckBox chckbxStablePatient = new JCheckBox("Stable Patient");
						panel_Each.add(chckbxStablePatient);
						
						JCheckBox chckbxStableStudy = new JCheckBox("Stable Study");
						panel_Each.add(chckbxStableStudy);
						
						JCheckBox chckbxStableSerie = new JCheckBox("Stable Serie");
						panel_Each.add(chckbxStableSerie);
						
						JPanel panel_If = new JPanel();
						panel_4.add(panel_If);
						
						JLabel lblIf = new JLabel("If");
						panel_If.add(lblIf);
						
						JComboBox comboBox_If = new JComboBox();
						panel_If.add(comboBox_If);
						
						JLabel label = new JLabel("=");
						panel_If.add(label);
						
						textField_If_Autorouting = new JTextField();
						panel_If.add(textField_If_Autorouting);
						textField_If_Autorouting.setColumns(10);
						
						JPanel panel_AutoFetch = new JPanel();
						tabbedPane.addTab("Auto-Fetch", null, panel_AutoFetch, null);
						
						JPanel panel_AutoRetrieve_Main = new JPanel();
						panel_AutoFetch.add(panel_AutoRetrieve_Main);
						panel_AutoRetrieve_Main.setLayout(new BorderLayout(0, 0));
						
						JPanel panel_3 = new JPanel();
						panel_AutoRetrieve_Main.add(panel_3, BorderLayout.NORTH);
						panel_3.setLayout(new BorderLayout(0, 0));
						
						JPanel panel_5 = new JPanel();
						panel_3.add(panel_5, BorderLayout.CENTER);
						
						JLabel lblRetrieveFrom = new JLabel("Retrieve Patient's studies From :");
						panel_5.add(lblRetrieveFrom);
						
						JComboBox comboBox = new JComboBox();
						panel_5.add(comboBox);
						
						JLabel lblEach = new JLabel("each");
						panel_5.add(lblEach);
						
						JCheckBox chckbxNewPatient = new JCheckBox("New Patient");
						panel_5.add(chckbxNewPatient);
						
						JCheckBox chckbxNewStudy = new JCheckBox("New Study");
						panel_5.add(chckbxNewStudy);
						
						JPanel panel_AutoRetrieve_Filter = new JPanel();
						panel_3.add(panel_AutoRetrieve_Filter, BorderLayout.SOUTH);
						panel_AutoRetrieve_Filter.setLayout(new GridLayout(0, 2, 10, 10));
						
						JLabel lblFilters = new JLabel("Filters : ");
						panel_AutoRetrieve_Filter.add(lblFilters);
						
						Component horizontalStrut = Box.createHorizontalStrut(20);
						panel_AutoRetrieve_Filter.add(horizontalStrut);
						
						JLabel lblModalityInStudy = new JLabel("Modality in study");
						panel_AutoRetrieve_Filter.add(lblModalityInStudy);
						
						textField_Modality_Study_AutroRetrieve = new JTextField();
						textField_Modality_Study_AutroRetrieve.setText("*");
						panel_AutoRetrieve_Filter.add(textField_Modality_Study_AutroRetrieve);
						textField_Modality_Study_AutroRetrieve.setColumns(10);
						
						JLabel lblDateFilter = new JLabel("Date");
						panel_AutoRetrieve_Filter.add(lblDateFilter);
						
						textField_Date_AuToRetrieve = new JTextField();
						textField_Date_AuToRetrieve.setText("*-*");
						panel_AutoRetrieve_Filter.add(textField_Date_AuToRetrieve);
						textField_Date_AuToRetrieve.setColumns(10);
						
						JLabel lblStudyDescription = new JLabel("Study Description Contains");
						panel_AutoRetrieve_Filter.add(lblStudyDescription);
						
						textField_StudyDescription_Study = new JTextField();
						textField_StudyDescription_Study.setText("*");
						panel_AutoRetrieve_Filter.add(textField_StudyDescription_Study);
						textField_StudyDescription_Study.setColumns(10);
						
						JPanel panel_6 = new JPanel();
						panel_AutoRetrieve_Main.add(panel_6, BorderLayout.SOUTH);
						
						JButton btnStartAutoretrieve = new JButton("Start Auto-Retrieve");
						panel_6.add(btnStartAutoretrieve);
	}
	
	private void setCDPreference() {
		//On prends les settings du registery
				jPrefer = Preferences.userNodeForPackage(Burner_Settings.class);
				jPrefer = jPrefer.node("CDburner");
				CD_Burner.fijiDirectory=jPrefer.get("fijiDirectory", null);
				CD_Burner.epsonDirectory=jPrefer.get("epsonDirectory", null);
				CD_Burner.labelFile=jPrefer.get("labelFile", null);
				CD_Burner.dateFormatChoix=jPrefer.get("DateFormat", null);
		
				
	}
	
	private void autoStart(){
		if ( CD_Burner.epsonDirectory!=null && CD_Burner.fijiDirectory!=null && CD_Burner.labelFile!=null && CD_Burner.dateFormatChoix!=null ){
			btnStartMonitoring.doClick();
		}
	}
	
	
	private void updateStatusLabel(){
		if (cdMonitoringStarted) lbl_CD_Status.setText("Start");
		else lbl_CD_Status.setText("Stop");
		
		if (doseMonitoringStarted) lbl_DoseMonitoring_Status.setText("Start");
		else lbl_DoseMonitoring_Status.setText("Stop");
	}
}
