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

@SuppressWarnings("serial")
public class Monitoring_GUI extends JFrame {

	private JPanel contentPane;
	
	private JButton btnStopMonitoring, btnStartMonitoring;
	private Preferences jPrefer;
	
	private CD_Burner cdBurner;
	private JTextArea textAreaCD;
	private ParametreConnexionHttp parametre=new ParametreConnexionHttp();
	
	private JLabel lbl_CD_Status, lbl_DoseMonitoring_Status;
	
	
	private boolean cdMonitoringStarted, doseMonitoringStarted;
	private JTable table_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Monitoring_GUI frame = new Monitoring_GUI();
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
	public Monitoring_GUI() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
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
		
		tabbedPane.addTab("CD Burner", null, CD_Burner_Tab, null);
		
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
						tabbedPane.addTab("Tag Monitoring", null, panel_tag_monitoring, null);
						
						JPanel panel_2 = new JPanel();
						panel_tag_monitoring.add(panel_2);
						panel_2.setLayout(new BorderLayout(0, 0));
						
						JLabel lblNewPatients = new JLabel("New Patients");
						panel_2.add(lblNewPatients, BorderLayout.NORTH);
						
						JScrollPane scrollPane_1 = new JScrollPane();
						panel_2.add(scrollPane_1, BorderLayout.CENTER);
						
						table_1 = new JTable();
						table_1.setPreferredScrollableViewportSize(new Dimension(50, 50));
						table_1.setModel(new DefaultTableModel(
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
						scrollPane_1.setViewportView(table_1);
						
						JPanel panel_1 = new JPanel();
						panel_2.add(panel_1, BorderLayout.SOUTH);
						
						JButton btnAdd = new JButton("Add");
						panel_1.add(btnAdd);
						
						JButton btnRemove = new JButton("Remove");
						panel_1.add(btnRemove);
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
