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
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import org.petctviewer.orthanc.ParametreConnexionHttp;
import org.petctviewer.orthanc.query.Rest;

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
import javax.swing.ButtonGroup;

import java.awt.FlowLayout;
import javax.swing.JList;
import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public class Monitoring_GUI extends JFrame {

	private JPanel contentPane;
	
	//CD Burner Service
	private JButton btnStopMonitoring, btnStartMonitoring;
	private Preferences jPrefer;
	private JTextArea textAreaCD;
	CD_Burner cdBurner;
	
	//Service Status in Main tab
	private JLabel lbl_CD_Status, lbl_DoseMonitoring_Status, lbl_AutoFecth_Status;
	
	//Boolean activity services
	private boolean cdMonitoringStarted, autoFetchStarted, tagMonitoringStarted;
	
	private JTable table_Patient_TagMonitoring;
	private JTable table_Study_TagMonitoring;
	
	//Tag Monitoring
	ButtonGroup levelTagMonitoring=new ButtonGroup();
	Tag_Monitoring tagMonitoring;
	
	//AutoFetch
	JComboBox<String> comboBoxAET_AutoFetch;
	JCheckBox chckbxNewPatientAutoFetch, chckbxNewStudyAutoFetch;
	ButtonGroup levelAutoFecth= new ButtonGroup();
	JTextField textField_AutoFecth_Modality_Study, textField_AutoFecth_Date, textField_AutoFetch_StudyDescription;
	Auto_Fetch autoFetch;
	ButtonGroup levelAutoRouting= new ButtonGroup();
	
	// parametre http
	ParametreConnexionHttp parametre;
	/**
	 * @wbp.nonvisual location=-24,419
	 */
	private final JList list = new JList();
	private JTable table;
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
		this.parametre=parametre;
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
		
		JLabel lblDoseMonitoring = new JLabel("Tag Monitoring");
		panel_Status.add(lblDoseMonitoring);
		
		lbl_DoseMonitoring_Status = new JLabel("Stop");
		panel_Status.add(lbl_DoseMonitoring_Status);
		
		JLabel lblAutorouting = new JLabel("Auto-Routing");
		panel_Status.add(lblAutorouting);
		
		JLabel lblStop = new JLabel("Stop");
		panel_Status.add(lblStop);
		
		JLabel lblAutofetch = new JLabel("Auto-Fetch");
		panel_Status.add(lblAutofetch);
		
		lbl_AutoFecth_Status = new JLabel("Stop");
		panel_Status.add(lbl_AutoFecth_Status);
		
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
								cdBurner.setCDPreference();
								//On ouvre le watcher dans un nouveau thread pour ne pas bloquer l'interface				
								cdBurner.startCDMonitoring();
								cdMonitoringStarted=true;
								//On grise le boutton pour empecher la creation d'un nouveau watcher
								btnStartMonitoring.setEnabled(false);
								btnStopMonitoring.setEnabled(true);
								updateStatusLabel();
												
							}
						});
						JButton btnSettings = new JButton("Settings");
						btnSettings.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								Burner_Settings settings=new Burner_Settings();
								settings.setVisible(true);
								settings.setModal(true);
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
						
						JPanel panel_TagMonitoring_Main = new JPanel();
						panel_tag_monitoring.add(panel_TagMonitoring_Main);
						panel_TagMonitoring_Main.setLayout(new GridLayout(0, 1, 0, 0));
						
						JPanel panel_1 = new JPanel();
						panel_TagMonitoring_Main.add(panel_1);
						panel_1.setLayout(new GridLayout(0, 3, 0, 0));
						
						JPanel panel_TagMonitoring_Patients = new JPanel();
						panel_1.add(panel_TagMonitoring_Patients);
						panel_TagMonitoring_Patients.setLayout(new BorderLayout(0, 0));
						
						JScrollPane scrollPane_1 = new JScrollPane();
						panel_TagMonitoring_Patients.add(scrollPane_1, BorderLayout.CENTER);
						
						table_Patient_TagMonitoring = new JTable();
						table_Patient_TagMonitoring.setPreferredScrollableViewportSize(new Dimension(300, 100));
						table_Patient_TagMonitoring.setModel(new DefaultTableModel(
							new Object[][] {
								{"0010,0010", "Name"},
								{"0010,0020", "ID"},
								{"0010,0030", "Date Of Birth"},
								{"0010,0040", "Sex"},
							},
							new String[] {
								"Tag", "Name"
							}
						) {
							Class[] columnTypes = new Class[] {
								String.class, Object.class
							};
							public Class getColumnClass(int columnIndex) {
								return columnTypes[columnIndex];
							}
						});
						scrollPane_1.setViewportView(table_Patient_TagMonitoring);
						
						JCheckBox chckbxNewPatient = new JCheckBox("New Patient");
						chckbxNewPatient.setActionCommand("patient");
						levelTagMonitoring.add(chckbxNewPatient);
						panel_TagMonitoring_Patients.add(chckbxNewPatient, BorderLayout.NORTH);
						
						JPanel panel_TagMonitoring_Studies = new JPanel();
						panel_1.add(panel_TagMonitoring_Studies);
						panel_TagMonitoring_Studies.setLayout(new BorderLayout(0, 0));
						
						JScrollPane scrollPane_2 = new JScrollPane();
						panel_TagMonitoring_Studies.add(scrollPane_2, BorderLayout.CENTER);
						
						table_Study_TagMonitoring = new JTable();
						table_Study_TagMonitoring.setPreferredScrollableViewportSize(new Dimension(300, 100));
						table_Study_TagMonitoring.setModel(new DefaultTableModel(
							new Object[][] {
								{"0008,0020", "StudyDate"},
								{"0008,0030", "StudyTime"},
								{"0008,1030", "StudyDescription"},
								{"0008,0050", "AccessionNumber"},
								{"0020,0010", "StudyID"},
								{"0020,000D", "StudyInstanceUID"},
							},
							new String[] {
								"Tag", "Name"
							}
						) {
							Class[] columnTypes = new Class[] {
								String.class, String.class
							};
							public Class getColumnClass(int columnIndex) {
								return columnTypes[columnIndex];
							}
						});
						scrollPane_2.setViewportView(table_Study_TagMonitoring);
						
						JCheckBox chckbxNewStudy = new JCheckBox("New Study");
						chckbxNewStudy.setActionCommand("study");
						levelTagMonitoring.add(chckbxNewStudy);
						panel_TagMonitoring_Studies.add(chckbxNewStudy, BorderLayout.NORTH);
						
						JPanel panel_TagMonitoring_Series = new JPanel();
						panel_1.add(panel_TagMonitoring_Series);
						panel_TagMonitoring_Series.setLayout(new BorderLayout(0, 0));
						
						JCheckBox chckbx_Serie_TagMonitoring = new JCheckBox("New Serie");
						chckbx_Serie_TagMonitoring.setSelected(true);
						chckbx_Serie_TagMonitoring.setActionCommand("serie");
						levelTagMonitoring.add(chckbx_Serie_TagMonitoring);
						panel_TagMonitoring_Series.add(chckbx_Serie_TagMonitoring, BorderLayout.NORTH);
						
						JPanel panel_SeriesTagChooser = new JPanel();
						panel_TagMonitoring_Series.add(panel_SeriesTagChooser, BorderLayout.CENTER);
						panel_SeriesTagChooser.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
						
						JComboBox comboBox = new JComboBox();
						comboBox.setModel(new DefaultComboBoxModel(new String[] {"Shared-Tags", "Defined List"}));
						panel_SeriesTagChooser.add(comboBox);
						
						JScrollPane scrollPane_3 = new JScrollPane();
						panel_TagMonitoring_Main.add(scrollPane_3);
						
						JTextArea textArea_Log_tagMonitoring = new JTextArea();
						textArea_Log_tagMonitoring.setColumns(30);
						textArea_Log_tagMonitoring.setRows(5);
						scrollPane_3.setViewportView(textArea_Log_tagMonitoring);
						
						JPanel panel_TagMonitoring_Buttons = new JPanel();
						panel_tag_monitoring.add(panel_TagMonitoring_Buttons, BorderLayout.SOUTH);
						
						JButton btnStart_tagMonitoring = new JButton("Start Collecting");
						btnStart_tagMonitoring.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								if (!tagMonitoringStarted) {
									tagMonitoring=new Tag_Monitoring(parametre, levelTagMonitoring.getSelection().getActionCommand(), textArea_Log_tagMonitoring);
									tagMonitoring.startTagMonitoring();
									btnStart_tagMonitoring.setText("Stop Collecting");
									tagMonitoringStarted=true;
									updateStatusLabel();
									
								}
								else if (tagMonitoringStarted) {
									tagMonitoring.stopTagMonitoring();
									tagMonitoringStarted=false;
									btnStart_tagMonitoring.setText("Start Collecting");
									updateStatusLabel();
								}
								
							}
						});
						panel_TagMonitoring_Buttons.add(btnStart_tagMonitoring);
						
						JPanel panel_AutoRouting = new JPanel();
						tabbedPane.addTab("Auto-Routing", null, panel_AutoRouting, null);
						
						JPanel panel_AutoRouting_main = new JPanel();
						panel_AutoRouting.add(panel_AutoRouting_main);
						panel_AutoRouting_main.setLayout(new BorderLayout(0, 0));
						
						JPanel panel_AutoRouting_sendTo = new JPanel();
						panel_AutoRouting_main.add(panel_AutoRouting_sendTo, BorderLayout.SOUTH);
						
						JButton btnStartAutorouting = new JButton("Start Auto-Routing");
						panel_AutoRouting_sendTo.add(btnStartAutorouting);
						
						JPanel panel_AutoRouting_selection = new JPanel();
						panel_AutoRouting_main.add(panel_AutoRouting_selection, BorderLayout.WEST);
						panel_AutoRouting_selection.setLayout(new BorderLayout(0, 0));
						
						JPanel panel_AutoRouting_North = new JPanel();
						panel_AutoRouting_selection.add(panel_AutoRouting_North, BorderLayout.NORTH);
						
						JLabel lblEach_1 = new JLabel("Each ");
						panel_AutoRouting_North.add(lblEach_1);
						
						JCheckBox chckbxStablePatient = new JCheckBox("Stable Patient");
						chckbxStablePatient.setActionCommand("Stable Patient");
						levelAutoRouting.add(chckbxStablePatient);
						panel_AutoRouting_North.add(chckbxStablePatient);
						
						JCheckBox chckbxStableStudy = new JCheckBox("Stable Study");
						chckbxStableStudy.setSelected(true);
						chckbxStableStudy.setActionCommand("Stable Study");
						levelAutoRouting.add(chckbxStableStudy);
						panel_AutoRouting_North.add(chckbxStableStudy);
						
						JCheckBox chckbxStableSerie = new JCheckBox("Stable Serie");
						chckbxStableSerie.setActionCommand("Stable Serie");
						levelAutoRouting.add(chckbxStableSerie);
						panel_AutoRouting_North.add(chckbxStableSerie);
						
						JPanel panel_2 = new JPanel();
						panel_AutoRouting_selection.add(panel_2, BorderLayout.CENTER);
						
						JScrollPane scrollPane_AutoRouting = new JScrollPane();
						panel_2.add(scrollPane_AutoRouting);
						
						table = new JTable();
						table.setPreferredScrollableViewportSize(new Dimension(400, 100));
						table.setModel(new DefaultTableModel(
							new Object[][] {
							},
							new String[] {
								"Conditions", "Destination AET"
							}
						) {
							Class[] columnTypes = new Class[] {
								String.class, String.class
							};
							public Class getColumnClass(int columnIndex) {
								return columnTypes[columnIndex];
							}
						});
						
						scrollPane_AutoRouting.setViewportView(table);
						
						
						JButton btnAddRule = new JButton("Add Rule");
						btnAddRule.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								Rule_AutoRouting_Gui rule=new Rule_AutoRouting_Gui();
								rule.setVisible(true);
							}
						});
						panel_2.add(btnAddRule);
						
						JPanel panel_AutoFetch = new JPanel();
						tabbedPane.addTab("Auto-Fetch", null, panel_AutoFetch, null);
						
						JPanel panel_AutoFetch_Main = new JPanel();
						panel_AutoFetch.add(panel_AutoFetch_Main);
						panel_AutoFetch_Main.setLayout(new BorderLayout(0, 0));
						
						JPanel panel_AutoFetch_Center = new JPanel();
						panel_AutoFetch_Main.add(panel_AutoFetch_Center, BorderLayout.NORTH);
						panel_AutoFetch_Center.setLayout(new BorderLayout(0, 0));
						
						JPanel panel_AutoFetch_AetLevel_Selection = new JPanel();
						panel_AutoFetch_Center.add(panel_AutoFetch_AetLevel_Selection, BorderLayout.CENTER);
						
						JLabel lblRetrieveFrom = new JLabel("Retrieve Patient's studies From :");
						panel_AutoFetch_AetLevel_Selection.add(lblRetrieveFrom);
						
						comboBoxAET_AutoFetch = new JComboBox<String>();
						panel_AutoFetch_AetLevel_Selection.add(comboBoxAET_AutoFetch);
						
						JLabel lblEach = new JLabel("each");
						panel_AutoFetch_AetLevel_Selection.add(lblEach);
						
						
						chckbxNewPatientAutoFetch = new JCheckBox("New Patient");
						chckbxNewPatientAutoFetch.setActionCommand("patient");
						levelAutoFecth.add(chckbxNewPatientAutoFetch);
						panel_AutoFetch_AetLevel_Selection.add(chckbxNewPatientAutoFetch);
						
						chckbxNewStudyAutoFetch = new JCheckBox("New Study");
						chckbxNewStudyAutoFetch.setSelected(true);
						chckbxNewStudyAutoFetch.setActionCommand("study");
						levelAutoFecth.add(chckbxNewStudyAutoFetch);
						panel_AutoFetch_AetLevel_Selection.add(chckbxNewStudyAutoFetch);
						
						JPanel panel_AutoFetch_Filter = new JPanel();
						panel_AutoFetch_Center.add(panel_AutoFetch_Filter, BorderLayout.SOUTH);
						panel_AutoFetch_Filter.setLayout(new GridLayout(0, 2, 10, 10));
						
						JLabel lblFilters = new JLabel("Filters : ");
						panel_AutoFetch_Filter.add(lblFilters);
						
						Component horizontalStrut = Box.createHorizontalStrut(20);
						panel_AutoFetch_Filter.add(horizontalStrut);
						
						JLabel lblModalityInStudy = new JLabel("Modality in study");
						panel_AutoFetch_Filter.add(lblModalityInStudy);
						
						textField_AutoFecth_Modality_Study = new JTextField();
						textField_AutoFecth_Modality_Study.setText("*");
						panel_AutoFetch_Filter.add(textField_AutoFecth_Modality_Study);
						textField_AutoFecth_Modality_Study.setColumns(10);
						
						JLabel lblDateFilter = new JLabel("Date");
						panel_AutoFetch_Filter.add(lblDateFilter);
						
						textField_AutoFecth_Date = new JTextField();
						textField_AutoFecth_Date.setToolTipText("Format : YYYYMMDD-YYYYMMDD");
						textField_AutoFecth_Date.setText("*");
						panel_AutoFetch_Filter.add(textField_AutoFecth_Date);
						textField_AutoFecth_Date.setColumns(10);
						
						JLabel lblStudyDescription = new JLabel("Study Description Contains");
						panel_AutoFetch_Filter.add(lblStudyDescription);
						
						textField_AutoFetch_StudyDescription = new JTextField();
						textField_AutoFetch_StudyDescription.setText("*");
						panel_AutoFetch_Filter.add(textField_AutoFetch_StudyDescription);
						textField_AutoFetch_StudyDescription.setColumns(10);
						
						JPanel panel_AutoFetch_Start = new JPanel();
						panel_AutoFetch_Main.add(panel_AutoFetch_Start, BorderLayout.SOUTH);
						
						JLabel lblStatus_AutoFetch = new JLabel("Status : Idle");
						
						JButton btnStartAutoFetch = new JButton("Start Auto-Fetch");
						btnStartAutoFetch.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								if (!autoFetchStarted) {
									autoFetch=new Auto_Fetch(parametre, levelAutoFecth.getSelection().getActionCommand(), textField_AutoFecth_Date.getText(), textField_AutoFecth_Modality_Study.getText(), textField_AutoFetch_StudyDescription.getText(), comboBoxAET_AutoFetch.getSelectedItem().toString(), lblStatus_AutoFetch );
									autoFetch.startAutoFetch();
									btnStartAutoFetch.setText("Stop Auto-Fetch");
									autoFetchStarted=true;
									updateStatusLabel();
								}
								else if(autoFetchStarted) {
									autoFetch.stopAutoFecth();
									autoFetchStarted=false;
									btnStartAutoFetch.setText("Start Auto-Fetch");
									updateStatusLabel();
								}
								
								
							}
						});
						
						panel_AutoFetch_Start.add(btnStartAutoFetch);
						
					
						panel_AutoFetch_Start.add(lblStatus_AutoFetch);
						
						setAET();
	}
	
	
	
	
	/*private void autoStart(){
		if ( CD_Burner.epsonDirectory!=null && CD_Burner.fijiDirectory!=null && CD_Burner.labelFile!=null && CD_Burner.dateFormatChoix!=null ){
			btnStartMonitoring.doClick();
		}
	}*/
	
	
	private void updateStatusLabel(){
		if (cdMonitoringStarted) lbl_CD_Status.setText("Start");
		else lbl_CD_Status.setText("Stop");
		
		if (tagMonitoringStarted) lbl_DoseMonitoring_Status.setText("Start");
		else lbl_DoseMonitoring_Status.setText("Stop");
		
		if (autoFetchStarted) lbl_AutoFecth_Status.setText("Start");
		else lbl_AutoFecth_Status.setText("Stop");
		
		if (tagMonitoringStarted) lbl_DoseMonitoring_Status.setText("Start");
		else lbl_DoseMonitoring_Status.setText("Stop");
	}
	
	private void setAET() {
		Rest restApi=new Rest(parametre);
		try {
			Object[] aets=restApi.getAET();
			for (int i=0; i<aets.length ; i++) {
				comboBoxAET_AutoFetch.addItem((String) aets[i]);
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Stops all monitoring services (for exit call)
	 */
	public void closeAllMonitoringServices() {
		//Boolean activity services
		if (cdMonitoringStarted) cdBurner.stopCDMonitoring();
		if(tagMonitoringStarted) tagMonitoring.stopTagMonitoring();
		if(autoFetchStarted) autoFetch.stopAutoFecth();
		
	}
	/**
	 * check is existing running service (for exit call)
	 * @return
	 */
	public boolean isRunningMonitoringService() {
		boolean running=false;
		if (cdMonitoringStarted ||tagMonitoringStarted ||autoFetchStarted) {
			running=true;
		}
		return running;
	}
}
