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

package org.petctviewer.orthanc.anonymize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.petctviewer.orthanc.Orthanc_Tools;
import org.petctviewer.orthanc.Jsonsettings.SettingsGUI;
import org.petctviewer.orthanc.OTP.OTP;
import org.petctviewer.orthanc.OTP.OTP_Gui;
import org.petctviewer.orthanc.anonymize.controllers.Controller_Anonymize_Btn;
import org.petctviewer.orthanc.anonymize.controllers.Controller_Csv_Btn;
import org.petctviewer.orthanc.anonymize.controllers.Controller_Export_Zip;
import org.petctviewer.orthanc.anonymize.controllers.Controller_Read_Series;
import org.petctviewer.orthanc.anonymize.controllers.DeleteActionMainPanel;
import org.petctviewer.orthanc.anonymize.datastorage.Patient;
import org.petctviewer.orthanc.anonymize.datastorage.PatientAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Serie;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.anonymize.datastorage.Study2Anon;
import org.petctviewer.orthanc.anonymize.gui.AboutBoxFrame;
import org.petctviewer.orthanc.anonymize.gui.Custom_Cell_Renderer;
import org.petctviewer.orthanc.anonymize.gui.DateRenderer;
import org.petctviewer.orthanc.anonymize.listeners.AnonActionProfileListener;
import org.petctviewer.orthanc.anonymize.listeners.AnonymizeListener;
import org.petctviewer.orthanc.anonymize.listeners.Tab_Change_Listener;
import org.petctviewer.orthanc.anonymize.listeners.TableAnonPatientsMouseListener;
import org.petctviewer.orthanc.anonymize.listeners.TableExportStudiesMouseListener;
import org.petctviewer.orthanc.anonymize.listeners.TablePatientsMouseListener;
import org.petctviewer.orthanc.anonymize.listeners.TableStudiesMouseListener;
import org.petctviewer.orthanc.anonymize.listeners.Window_Custom_Listener;
import org.petctviewer.orthanc.export.ExportZip;
import org.petctviewer.orthanc.export.SendFilesToRemote;
import org.petctviewer.orthanc.importdicom.ImportDCM;
import org.petctviewer.orthanc.modify.Modify;
import org.petctviewer.orthanc.monitoring.Monitoring_GUI;
import org.petctviewer.orthanc.query.VueQuery;
import org.petctviewer.orthanc.setup.ConnectionSetup;
import org.petctviewer.orthanc.setup.OrthancRestApis;
import org.petctviewer.orthanc.setup.Run_Orthanc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.michaelbaranov.microba.calendar.DatePicker;

public class VueAnon extends JFrame {
	
	
	private static final long serialVersionUID = 1L;
	
	// Settings preferences
	public static Preferences jprefer = Preferences.userNodeForPackage(Orthanc_Tools.class);
	private DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	
	public JTabbedPane tabbedPane;
	
	private VueAnon gui=this;
	
	//Objet de connexion aux restFul API, prend les settings des registery et etabli les connexion a la demande
	private OrthancRestApis connexionHttp;
	private QueryOrthancData queryOrthanc;
	protected JPanel tablesPanel, mainPanel, topPanel, anonBtnPanelTop;
	
	//Search settings
	JCheckBox cr, ct, mr , nm , pt , us, xa , mg, dx;
	JTextField customModalities;
	// Tables and models
	private JTable tableauPatients;
	private JTable tableauStudies;
	public JTable tableauSeries;
	public TablePatientsModel modelePatients;
	public TableStudiesModel modeleStudies;
	public TableSeriesModel modeleSeries;
	
	public JTable anonPatientTable;
	public JTable anonStudiesTable;
	public TableAnonPatientsModel modeleAnonPatients;
	public TableAnonStudiesModel modeleAnonStudies;
	
	JButton searchBtn;

	// Orthanc toolbox (p1)
	private JLabel state ;
	private JButton displayAnonTool;
	private JButton displayExportTool;
	private JButton displayManageTool;
	private JButton addToAnon;
	public JButton anonBtn;
	private JButton removeFromAnonList;
	protected JButton importCTP;
	protected JButton queryCTPBtn;
	private JButton exportZip;
	private JButton removeFromZip;
	private JButton addToZip;
	private JLabel zipSize;
	private JLabel manageSize;
	private JTextField userInputSearch;
	
	//Manage Buttons
	private JButton addManage = new JButton("Add to List");
	private JButton removeFromManage = new JButton("Remove from List");
	private JButton deleteManage = new JButton("Delete list");
	//End manage buttons
	public JComboBox<String> zipShownContent;
	private JComboBox<String> manageShownContent;
	private JPanel oToolRight, oToolRightManage;
	private JComboBox<String> listeAET;
	public JComboBox<String> comboToolChooser;
	private JPopupMenu popMenuPatients = new JPopupMenu();
	private JPopupMenu popMenuStudies = new JPopupMenu();
	private JPopupMenu popMenuSeries = new JPopupMenu();
	public ArrayList<String> zipContent = new ArrayList<String>();
	private ArrayList<String> manageContent = new ArrayList<String>();
	private JPanel anonTablesPanel;
	
	//Read Image Button
	private JButton btnReadSeries;
	

	// Tab Export (p2)
	private JLabel stateExports = new JLabel("");
	protected JButton peerExport,csvReport, exportToZip, exportBtn, dicomStoreExport;
	protected JComboBox<String> listePeers;
	protected JComboBox<String> listeAETExport;
	private JTable tableauExportStudies;
	private JTable tableauExportSeries;
	public TableExportStudiesModel modeleExportStudies;
	public TableExportSeriesModel modeleExportSeries;
	private StringBuilder remoteFileName;
	

	//Monitoring (p3)
	private Monitoring_GUI monitoring;
	
	// Tab Setup (p4)
	private JComboBox<String> anonProfiles;
	//RadioButton for each group in Array 0 for Keep, 1 for Clear
	public JRadioButton[] settingsBodyCharButtons = new JRadioButton[2];
	public JRadioButton[] settingDatesButtons = new JRadioButton[2];
	public JRadioButton[] settingsBirthDateButtons = new JRadioButton[2];
	public JRadioButton[] settingsPrivateTagButtons = new JRadioButton[2];
	public JRadioButton[] settingsSecondaryCaptureButtons = new JRadioButton[2];
	public JRadioButton[] settingsStudySerieDescriptionButtons = new JRadioButton[2];
	private JTextField centerCode;
	private JTextField remoteServer;
	private JTextField remotePort;
	private JTextField servUsername;
	private JPasswordField servPassword;
	private JTextField remoteFilePath;
	private JComboBox<String> exportType;
	
	//CTP
	private JTextField addressFieldCTP;
	public JComboBox<String> listePeersCTP ;
	public JButton exportCTP;
	private String CTPUsername;
	private String CTPPassword;
	public boolean autoSendCTP=false;

	
	//Run Orthanc
	private Run_Orthanc runOrthanc;
	
	// Last Table focus
	private JTable lastTableFocusMain;
	private JTable lastTableFocusAnon;
	
	//CustomListener
	public AnonymizeListener anonymizeListener;

	public boolean fijiEnvironement=false;
	
	public VueAnon() {
		super("Orthanc Tools");
		connexionHttp= new OrthancRestApis(null);
		queryOrthanc=new QueryOrthancData(connexionHttp);
		runOrthanc=new Run_Orthanc();
		//Until we reach the Orthanc Server we give the setup panel
		int check=0;
		while (!connexionHttp.isConnected() && check<3) {
				if (check>0) JOptionPane.showMessageDialog(null, "Settings Attempt " + (check+1) +"/3", "Attempt", JOptionPane.INFORMATION_MESSAGE);
				ConnectionSetup setup = new ConnectionSetup(runOrthanc, null);
				setup.setVisible(true);
				connexionHttp=new OrthancRestApis(null);
				check++;
				if(check ==3) JOptionPane.showMessageDialog(null, "Programme is starting without connexion (no services)", "Failure", JOptionPane.ERROR_MESSAGE);
		}
		buildGui();
		
	}
	
	/**
	 * Force temporary session of Orthanc, with a specified JSON config file
	 * @param startTemporaryOrthanc
	 */
	public VueAnon(String orthancJsonName) {
		super("Orthanc Tools");
		
		try {
			runOrthanc=new Run_Orthanc();
			runOrthanc.orthancJsonName=orthancJsonName;
			runOrthanc.copyOrthanc(null);
			runOrthanc.startOrthanc();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		connexionHttp= new OrthancRestApis("http://localhost:8043");
		buildGui();
		
	}

	public void buildGui(){
		//Instanciate needed Table and their model
		modelePatients = new TablePatientsModel();
		modeleStudies = new TableStudiesModel(queryOrthanc);
		modeleSeries = new TableSeriesModel(connexionHttp, this, queryOrthanc);
		modeleExportStudies = new TableExportStudiesModel();
		modeleExportSeries = new TableExportSeriesModel(connexionHttp, queryOrthanc, this);
		modeleAnonStudies = new TableAnonStudiesModel();
		modeleAnonPatients = new TableAnonPatientsModel();
		
		//Instanciate label status
		state= new JLabel();
		//Instanciate other objects
		//SK A FINIR
		exportZip = new JButton("Export list");
		removeFromZip = new JButton("Remove from list");
		addToZip = new JButton("Add to list");
		zipSize= new JLabel("");
		manageSize= new JLabel("");
		userInputSearch = new JTextField();
		
		zipShownContent= new JComboBox<String>();
		manageShownContent= new JComboBox<String>();
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////// PANEL 1 : ANONYMIZATION ////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

		/////////////////////////////////////////////////////////////////////////////
		////////////////////////// TOP PANEL ////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////

		topPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		String[] patientParam = {"Patient name", "Patient ID", "Accession number"};
		JComboBox<String> inputType = new JComboBox<String>(patientParam);
		
		topPanel.add(inputType);
		inputType.addItemListener(new ItemListener() {
				@Override
			    public void itemStateChanged(ItemEvent event) {
			       if (event.getStateChange() == ItemEvent.SELECTED) {
			          if (inputType.getSelectedIndex()==0) {
			        	  userInputSearch.setEnabled(true);
			          }
			          else userInputSearch.setEnabled(false);
			       }
				}
		});
		inputType.setSelectedIndex(jprefer.getInt("InputParameter", 0));
		
		JTextField userInput = new JTextField();
		userInput.setToolTipText("Set your input accordingly to the field combobox on the left. ('*' stands for any character)");
		userInput.setText("*");
		userInput.setPreferredSize(new Dimension(125,20));
		userInputSearch.setText("*");
		userInputSearch.setToolTipText("Set your input accordingly to the field combobox on the left. ('*' stands for any character)");
		userInputSearch.setPreferredSize(new Dimension(125,20));
		topPanel.add(userInput);
		topPanel.add(new JLabel("First Name : "));
		topPanel.add(userInputSearch);

		topPanel.add(new JLabel("Study description"));
		JTextField studyDesc = new JTextField("*");
		studyDesc.setPreferredSize(new Dimension(125,20));
		topPanel.add(studyDesc);

		DatePicker from, to;
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		try {
			d = sdf.parse("01-01-1980");
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
		from = new DatePicker(d, sdf);
		from.setBorder(new EmptyBorder(0, 5, 0 ,0));
		from.setToolTipText("Date format : MM-dd-yyyy");
		to = new DatePicker(new Date(), sdf);
		to.setBorder(new EmptyBorder(0, 5, 0 ,0));
		to.setToolTipText("Date format : MM-dd-yyyy");
		topPanel.add(new JLabel("From"));
		topPanel.add(from);
		topPanel.add(new JLabel("To"));
		topPanel.add(to);
		
		JPanel panelModalities=new JPanel(new BorderLayout());
		JPanel checkboxes = new JPanel(new GridLayout(2,4));
		cr = new JCheckBox("CR");
		ct = new JCheckBox("CT");
		mr = new JCheckBox("MR");
		nm = new JCheckBox("NM");
		pt = new JCheckBox("PT");
		us = new JCheckBox("US");
		xa = new JCheckBox("XA");
		mg = new JCheckBox("MG");
		dx = new JCheckBox("DX");
		customModalities=new JTextField();
		customModalities.setToolTipText("Add custom modalities ex \"OT\\PR\" ");
		checkboxes.add(cr); checkboxes.add(ct);
		checkboxes.add(mr); checkboxes.add(nm);
		checkboxes.add(pt); checkboxes.add(us);
		checkboxes.add(xa); checkboxes.add(mg);
		checkboxes.add(dx); checkboxes.add(customModalities);
		panelModalities.add(checkboxes, BorderLayout.CENTER);
		panelModalities.add(new JLabel("Contains Modalities"), BorderLayout.NORTH);
		
		topPanel.add(panelModalities);

		searchBtn = new JButton("Search");
		searchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					searchBtn.setText("Searching");
					searchBtn.setEnabled(false);
					modelePatients.clear();
					modeleStudies.clear();
					modeleSeries.clear();

					DateFormat df = new SimpleDateFormat("yyyyMMdd");
					String date = df.format(from.getDate())+"-"+df.format(to.getDate());
					String userInputString=null;
					if (inputType.getSelectedIndex()==0 && !userInputSearch.getText().equals("*") && !StringUtils.isEmpty(userInputSearch.getText()) ) {
						userInputString=userInput.getText()+"^"+userInputSearch.getText();
						if (userInputString.equals("*^*")) userInputString="*"; 
					}
					else userInputString=userInput.getText();

					
					StringBuilder modalities=new StringBuilder();
					if (cr.isSelected()) modalities.append("CR\\");
					if (ct.isSelected()) modalities.append("CT\\");
					if (mr.isSelected()) modalities.append("MR\\");
					if (nm.isSelected()) modalities.append("NM\\");
					if (pt.isSelected()) modalities.append("PT\\");
					if (us.isSelected()) modalities.append("US\\");
					if (xa.isSelected()) modalities.append("XA\\");
					if (mg.isSelected()) modalities.append("MG\\");
					if (dx.isSelected()) modalities.append("DX\\");
					modalities.append(customModalities.getText());
					
					String modality=StringUtils.removeEnd(modalities.toString(), "\\");
					
					
					ArrayList<Patient> patients =queryOrthanc.findStudies(inputType.getSelectedItem().toString(), userInputString, date, studyDesc.getText(), modality);
					modelePatients.addPatient(patients);
					pack();
					
				} catch (Exception e1) { System.out.println("Exception"+e1);}
				finally{
					state.setText("");
					searchBtn.setEnabled(true);
					searchBtn.setText("Search");
					jprefer.putInt("InputParameter", inputType.getSelectedIndex());
				}
			}
		});
		
		JButton queryRetrieveBtn = new JButton("Queries/Retrieve");
		queryRetrieveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable () {
					@Override
					public void run() {
						VueQuery query=new VueQuery(connexionHttp, gui);
						query.pack();
						query.setLocationRelativeTo(gui);
						query.setVisible(true);
					}
				});
			}
		});
		
		JButton queryImportBtn = new JButton("Import");
		queryImportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable () {
					@Override
					public void run() {
						ImportDCM importFrame=new ImportDCM(connexionHttp,gui);
						importFrame.pack();
						importFrame.setLocationRelativeTo(gui);
						importFrame.setVisible(true);
					}
				});
			}
		});

		topPanel.add(searchBtn);
		topPanel.add(queryRetrieveBtn);
		topPanel.add(queryImportBtn);
		mainPanel.add(topPanel);

		/////////////////////////////////////////////////////////////////////////////
		////////////////////////// TABLES ///////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////
		

		tablesPanel = new JPanel(new FlowLayout());
		this.tableauPatients = new JTable(modelePatients);
		this.tableauStudies = new JTable(modeleStudies);
		this.tableauSeries = new JTable(modeleSeries);
		
		//Listener pour savoir quelle table a le dernier focus
		FocusListener tableFocus=new FocusListener() {
			Color background= tableauSeries.getSelectionBackground();
			@Override
			public void focusGained(FocusEvent e) {
				//memorise le dernier focus de table
				JTable source= (JTable) e.getSource();
				lastTableFocusMain=source;
				//Tracking Visuel de la selection
				if (source==tableauStudies){
					tableauPatients.setSelectionBackground(Color.LIGHT_GRAY);
					tableauStudies.setSelectionBackground(background);
				}
				else if (source==tableauPatients){
					tableauStudies.setSelectionBackground(background);
					tableauPatients.setSelectionBackground(background);
				}
				else if (source==tableauSeries){
					tableauStudies.setSelectionBackground(Color.LIGHT_GRAY);
					tableauPatients.setSelectionBackground(Color.LIGHT_GRAY);
				}

			}

			@Override
			public void focusLost(FocusEvent e) {
				JTable source= (JTable) e.getSource();
				if (e.getOppositeComponent() instanceof JTable) {
					if (source==tableauPatients) {
						tableauPatients.setSelectionBackground(Color.lightGray);
					}
						else if(source==tableauStudies){
							tableauStudies.setSelectionBackground(Color.lightGray);
					}
				}
			}
		};
		
		FocusListener tableFocusAnon=new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				//memorise le dernier focus de table
				JTable source= (JTable) e.getSource();
				lastTableFocusAnon=source;
				
			}
		};
		
		//Add Focus listener to tables
		tableauPatients.addFocusListener(tableFocus);
		tableauStudies.addFocusListener(tableFocus);
		tableauSeries.addFocusListener(tableFocus);
		tableauPatients.setAutoCreateRowSorter(true);
		tableauStudies.setAutoCreateRowSorter(true);
		tableauSeries.setAutoCreateRowSorter(true);
		
		////////////////////////// PATIENTS ///////////////////////////////

		this.tableauPatients.getTableHeader().setReorderingAllowed(false);
		this.tableauPatients.getColumnModel().getColumn(0).setMinWidth(170);
		this.tableauPatients.getColumnModel().getColumn(0).setMaxWidth(170);
		this.tableauPatients.getColumnModel().getColumn(0).setResizable(false);
		this.tableauPatients.getColumnModel().getColumn(1).setMinWidth(120);
		this.tableauPatients.getColumnModel().getColumn(1).setMaxWidth(120);
		this.tableauPatients.getColumnModel().getColumn(1).setResizable(false);
		this.tableauPatients.getColumnModel().getColumn(2).setMinWidth(0);
		this.tableauPatients.getColumnModel().getColumn(2).setMaxWidth(0);
		this.tableauPatients.getColumnModel().getColumn(2).setResizable(false);
		this.tableauPatients.getColumnModel().getColumn(3).setMinWidth(0);
		this.tableauPatients.getColumnModel().getColumn(3).setMaxWidth(0);
		this.tableauPatients.getColumnModel().getColumn(3).setResizable(false);
		this.tableauPatients.getColumnModel().getColumn(4).setMinWidth(0);
		this.tableauPatients.getColumnModel().getColumn(4).setMaxWidth(0);
		this.tableauPatients.getColumnModel().getColumn(4).setResizable(false);
		this.tableauPatients.setPreferredScrollableViewportSize(new Dimension(290,267));

		this.tableauPatients.setDefaultRenderer(Date.class, new DateRenderer());
		this.tableauPatients.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.tableauPatients.getSelectionModel().addListSelectionListener(new TablePatientsMouseListener(
				this, this.tableauPatients, this.modelePatients, this.modeleStudies, this.modeleSeries, 
				tableauPatients.getSelectionModel()));

		JMenuItem menuItemModifyPatients = new JMenuItem("Show tags/ Modify");
		menuItemModifyPatients.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					new Modify("patients",(String)tableauPatients.getValueAt(tableauPatients.getSelectedRow(),2), gui, connexionHttp);
				}
			});
		
		JMenuItem menuItemDeletePatients = new JMenuItem("Delete this patient");
		menuItemDeletePatients.addActionListener(new DeleteActionMainPanel(connexionHttp, "Patient", this.modeleStudies, this.tableauStudies, 
				this.modeleSeries, this.tableauSeries, this.modelePatients, this.tableauPatients, this, searchBtn));

		popMenuPatients.add(menuItemModifyPatients);
		popMenuPatients.addSeparator();
		popMenuPatients.add(menuItemDeletePatients);
		//Selectionne la ligne avant affichage du popupMenu
		addPopUpMenuListener(popMenuPatients,tableauPatients);
		tableauPatients.setComponentPopupMenu(popMenuPatients);

		////////////////////////// STUDIES ///////////////////////////////

		this.tableauStudies.getTableHeader().setReorderingAllowed(false);
		this.tableauStudies.getColumnModel().getColumn(0).setMinWidth(80);
		this.tableauStudies.getColumnModel().getColumn(0).setMaxWidth(80);
		this.tableauStudies.getColumnModel().getColumn(0).setResizable(false);
		this.tableauStudies.getColumnModel().getColumn(1).setMinWidth(180);
		this.tableauStudies.getColumnModel().getColumn(1).setMaxWidth(180);
		this.tableauStudies.getColumnModel().getColumn(1).setResizable(false);
		this.tableauStudies.getColumnModel().getColumn(2).setMinWidth(150);
		this.tableauStudies.getColumnModel().getColumn(2).setMaxWidth(150);
		this.tableauStudies.getColumnModel().getColumn(2).setResizable(false);
		this.tableauStudies.getColumnModel().getColumn(3).setMinWidth(0);
		this.tableauStudies.getColumnModel().getColumn(3).setMaxWidth(0);
		this.tableauStudies.getColumnModel().getColumn(3).setResizable(false);
		this.tableauStudies.setPreferredScrollableViewportSize(new Dimension(410,267));
		this.tableauStudies.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		this.tableauStudies.getSelectionModel().addListSelectionListener(new TableStudiesMouseListener(this, this.tableauStudies, this.modeleStudies, this.modeleSeries, tableauStudies.getSelectionModel()));
		this.tableauStudies.setDefaultRenderer(Date.class, new DateRenderer());
		
		JMenuItem menuItemModifyStudy = new JMenuItem("Show tags / Modify");
		menuItemModifyStudy.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					new Modify("studies",(String)tableauStudies.getValueAt(tableauStudies.getSelectedRow(),3), gui, connexionHttp);
				}
			});
		
		
		JMenuItem menuItemDeleteStudy = new JMenuItem("Delete this study");
		menuItemDeleteStudy.addActionListener(new DeleteActionMainPanel(connexionHttp, "Study", this.modeleStudies, this.tableauStudies, 
				this.modeleSeries, this.tableauSeries, this.modelePatients, this.tableauPatients, this, searchBtn));
		
		popMenuStudies.add(menuItemModifyStudy);
		popMenuStudies.addSeparator();
		popMenuStudies.add(menuItemDeleteStudy);
		addPopUpMenuListener(popMenuStudies,tableauStudies);
		tableauStudies.setComponentPopupMenu(popMenuStudies);

		////////////////////////// SERIES ///////////////////////////////

		this.tableauSeries.getTableHeader().setReorderingAllowed(false);
		this.tableauSeries.getColumnModel().getColumn(0).setMinWidth(260);
		this.tableauSeries.getColumnModel().getColumn(1).setMinWidth(100);
		this.tableauSeries.getColumnModel().getColumn(2).setMinWidth(100);
		this.tableauSeries.getColumnModel().getColumn(3).setMinWidth(0);
		this.tableauSeries.getColumnModel().getColumn(3).setMaxWidth(0);
		this.tableauSeries.getColumnModel().getColumn(3).setResizable(false);
		this.tableauSeries.getColumnModel().getColumn(3).setCellRenderer(new Custom_Cell_Renderer());
		this.tableauSeries.getColumnModel().getColumn(4).setMinWidth(0);
		this.tableauSeries.getColumnModel().getColumn(4).setMaxWidth(0);
		this.tableauSeries.getColumnModel().getColumn(4).setResizable(false);
		this.tableauSeries.getColumnModel().getColumn(5).setMinWidth(70);
		this.tableauSeries.getColumnModel().getColumn(6).setMinWidth(0);
		this.tableauSeries.getColumnModel().getColumn(6).setMaxWidth(0);
		this.tableauSeries.setPreferredScrollableViewportSize(new Dimension(580,267));
		this.tableauSeries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.tableauSeries.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		JMenuItem menuItemModifySeries = new JMenuItem("Show tags / Modify");
		menuItemModifySeries.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					new Modify("series",(String)tableauSeries.getValueAt(tableauSeries.getSelectedRow(),4), gui, connexionHttp);
				}
			});
		
		JMenuItem menuItemAllSopClass = new JMenuItem("Detect all secondary captures");
		
		menuItemAllSopClass.addActionListener(new ActionListener() {
			boolean activated=false;
			@Override
			public void actionPerformed(ActionEvent e) {
				activated=!activated;
				if(activated) {
					tableauSeries.getColumnModel().getColumn(3).setMaxWidth(50);
					tableauSeries.getColumnModel().getColumn(3).setMinWidth(50);
					menuItemAllSopClass.setText("Undetect all secondary captures");
					
				} else {
					tableauSeries.getColumnModel().getColumn(3).setMinWidth(0);
					tableauSeries.getColumnModel().getColumn(3).setMaxWidth(0);
					menuItemAllSopClass.setText("Detect all secondary captures");
				}
			}
		});
		
		JMenuItem menuItemDeleteAllSop = new JMenuItem("Remove all secondary captures");
		menuItemDeleteAllSop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				modeleSeries.removeAllSecondaryCaptures();
			}
		});
		JMenuItem menuItemDeleteSeries = new JMenuItem("Delete this serie");
		menuItemDeleteSeries.addActionListener(new DeleteActionMainPanel(connexionHttp, "Serie", this.modeleStudies, this.tableauStudies, 
				this.modeleSeries, this.tableauSeries, this.modelePatients, this.tableauPatients, this, searchBtn));

		popMenuSeries.add(menuItemModifySeries);
		popMenuSeries.addSeparator();
		popMenuSeries.add(menuItemAllSopClass);
		popMenuSeries.add(menuItemDeleteAllSop);
		popMenuSeries.addSeparator();
		popMenuSeries.add(menuItemDeleteSeries);
		addPopUpMenuListener(popMenuSeries, tableauSeries);
		
		this.tableauSeries.setComponentPopupMenu(popMenuSeries);

		/////////////////////////////////////////////////////////////////////////////
		///////////////////////// ORTHANC TOOLBOX ///////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////

		JPanel toolbox = new JPanel(new BorderLayout());
		JPanel labelAndAnon = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel ortToolbox = new JLabel("<html><font size=\"5\">Orthanc toolbox</font></html>");
		ortToolbox.setBorder(new EmptyBorder(0, 0, 0, 50));
		labelAndAnon.add(ortToolbox);
		labelAndAnon.add(this.state);
		
		oToolRight = new JPanel();
		oToolRight.setLayout(new BoxLayout(oToolRight, BoxLayout.PAGE_AXIS));

		JPanel storeTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton storeBtn = new JButton("Store list");
		
		listeAET = new JComboBox<String>();
		listeAET.setPreferredSize(new Dimension(297, 27));
		storeTool.add(listeAET);
		storeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!zipContent.isEmpty()){
					
					SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
						boolean success;
						@Override
						protected Void doInBackground()  {
							setStateMessage("Storing data (Do not use the toolbox while the current operation is not done)", "red", -1);
							storeBtn.setEnabled(false);
							pack();
							success=connexionHttp.sendToAet(listeAET.getSelectedItem().toString(), zipContent);
							return null;
						}

						@Override
						protected void done(){
							if(success) {
								state.setText("<html><font color='green'>The data have successfully been stored.</font></html>");
								zipShownContent.removeAllItems();
								zipContent.removeAll(zipContent);
							}else {
								setStateMessage("DICOM Send Failed", "red", -1);
								
							}
							storeBtn.setEnabled(true);
							pack();
						}
					};
					worker.execute();
				}
			}
		});
		storeTool.add(storeBtn);

		JPanel comboBoxBtn = new JPanel(new FlowLayout(FlowLayout.LEFT));
		comboBoxBtn.add(zipShownContent);
		
		removeFromZip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeFromToolList(zipContent, zipShownContent, zipSize, state);
					
			}
		});
		
		removeFromManage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeFromToolList(manageContent, manageShownContent, manageSize, state);
			}
		});
		
		addToZip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Ajout dans la tool list		
				addToToolList(zipContent,zipShownContent, zipSize);
			}
		});
		
		addManage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Ajout dans la tool list
				addToToolList(manageContent,manageShownContent, manageSize);
			}
		});
		
		// Efface la liste de Orthanc
		deleteManage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Liste des orthanc ID A supprimer qu'on va delete un a un
				ArrayList<String> deleteSeries=new ArrayList<String>();
				ArrayList<String> deleteStudies=new ArrayList<String>();
				
				for (int i=0; i<manageContent.size(); i++){
					//Patient or Studies are defined as study level
					if (manageShownContent.getItemAt(i).contains("Study -") || manageShownContent.getItemAt(i).contains("Patient -")){
						deleteStudies.add("/studies/"+manageContent.get(i));
					}
					else if (manageShownContent.getItemAt(i).contains("Serie -")){
						deleteSeries.add("/series/"+manageContent.get(i));
					}
				}
				setStateMessage("Deleting please wait", "red", -1);
				enableManageButtons(false);
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws IOException {
						int progress=0;
						for (int i=0 ; i<deleteSeries.size(); i++){
							connexionHttp.makeDeleteConnection(deleteSeries.get(i));
							progress++;
							setStateMessage("Deleted "+ progress +"/"+manageContent.size(), "red", -1);
						}
						for (int i=0 ; i<deleteStudies.size(); i++){
							connexionHttp.makeDeleteConnection(deleteStudies.get(i));
							progress++;
							setStateMessage("Deleted "+ progress +"/"+manageContent.size(), "red", -1);
						}
						return null;
					}

					@Override
					protected void done(){
						setStateMessage("Delete Done", "green", 4);
						enableManageButtons(true);
						manageSize.setText("empty list");
						manageShownContent.removeAllItems();
						manageContent.removeAll(manageContent);
						searchBtn.doClick();
					}
					
					
				};
				worker.execute();
				
			}
		});
		
		comboToolChooser= new JComboBox<String>(new String[] {"ZIP File","DICOMDIR Zip", "Image with Viewer (zip)", "Image with Viewer (iso)"});
		comboToolChooser.setPreferredSize(new Dimension(297, 27));
		comboBoxBtn.add(addToZip);
		comboBoxBtn.add(removeFromZip);
		
		JPanel exportTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		exportTool.add(comboToolChooser);
		exportTool.add(exportZip);;
		comboBoxBtn.add(this.zipSize);
		oToolRight.add(comboBoxBtn);
		oToolRight.add(exportTool);
		oToolRight.add(storeTool);
		
		toolbox.add(labelAndAnon,BorderLayout.WEST);
		oToolRight.setVisible(false);
		toolbox.add(oToolRight);
		
		//Manage Panel//
		oToolRightManage = new JPanel();
		oToolRightManage.setLayout(new BoxLayout(oToolRightManage, BoxLayout.PAGE_AXIS));

		JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel deletePanelGrid = new JPanel(new GridLayout(2,1));
		
		JPanel deletePanelComboButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
	
		deletePanelComboButton.add(manageShownContent);
		deletePanelComboButton.add(addManage);
		deletePanelComboButton.add(removeFromManage);
		deletePanelComboButton.add(manageSize);
		
		JPanel metadataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton metadata = new JButton("Metadata");
		metadata.setEnabled(false);
		metadataPanel.add(deleteManage);
		metadataPanel.add(metadata);
		
		deletePanelGrid.add(deletePanelComboButton);
		deletePanelGrid.add(metadataPanel);
		deletePanel.add(deletePanelGrid);
		oToolRightManage.add(deletePanel);
		
		oToolRightManage.setVisible(false);
		toolbox.add(oToolRightManage, BorderLayout.SOUTH);
		

		/////////////////////////////////////////////////////////////////////////////
		///////////////////////// ANONYMIZATION DETAILS /////////////////////////////
		/////////////////////////////////////////////////////////////////////////////

		JPanel anonDetailed = new JPanel(new BorderLayout());

		anonTablesPanel = new JPanel(new FlowLayout());
		anonPatientTable = new JTable(modeleAnonPatients);
		anonPatientTable.getTableHeader().setToolTipText("Double click on the new name/ID cells to change their values (otherwise, a name/ID will be generated automatically)");
		anonPatientTable.getColumnModel().getColumn(0).setMinWidth(100);
		anonPatientTable.getColumnModel().getColumn(0).setMaxWidth(100);
		anonPatientTable.getColumnModel().getColumn(1).setMinWidth(70);
		anonPatientTable.getColumnModel().getColumn(1).setMaxWidth(70);
		anonPatientTable.getColumnModel().getColumn(2).setMinWidth(0);
		anonPatientTable.getColumnModel().getColumn(2).setMaxWidth(0);
		anonPatientTable.getColumnModel().getColumn(3).setMinWidth(150);
		anonPatientTable.getColumnModel().getColumn(4).setMinWidth(120);
		anonPatientTable.getColumnModel().getColumn(5).setMinWidth(0);
		anonPatientTable.getColumnModel().getColumn(5).setMaxWidth(0);
		anonPatientTable.setPreferredScrollableViewportSize(new Dimension(440,130));
		anonPatientTable.putClientProperty("terminateEditOnFocusLost", true);

		//override edditing stoped and Set Value At to store the new studyDescription value in the AnonPatient object
		anonStudiesTable = new JTable(modeleAnonStudies) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if(column==0) {
					storeNewStudyDescription(row, aValue);
				}
			}

			private void storeNewStudyDescription(int row, Object studyDescription) {
				PatientAnon patient=(PatientAnon) modeleAnonPatients.getValueAt(anonPatientTable.convertRowIndexToModel(anonPatientTable.getSelectedRow()), 6);
				Study2Anon editingStudy=patient.getAnonymizeStudy((String) getValueAt(row, 2));
				editingStudy.setNewStudyDescription((String) studyDescription);
			}
		};

		anonPatientTable.addMouseListener(new TableAnonPatientsMouseListener(anonPatientTable, modeleAnonPatients, modeleAnonStudies, anonStudiesTable));
		anonPatientTable.getSelectionModel().addListSelectionListener(new TableAnonPatientsMouseListener(anonPatientTable, modeleAnonPatients, modeleAnonStudies, anonStudiesTable));
		
		anonStudiesTable.getTableHeader().setToolTipText("Click on the description cells to change their values");
		anonStudiesTable.getColumnModel().getColumn(0).setMinWidth(200);
		anonStudiesTable.getColumnModel().getColumn(1).setMinWidth(80);
		anonStudiesTable.getColumnModel().getColumn(1).setMaxWidth(80);
		anonStudiesTable.getColumnModel().getColumn(2).setMinWidth(150);
		anonStudiesTable.getColumnModel().getColumn(2).setMaxWidth(150);
		anonStudiesTable.getColumnModel().getColumn(3).setMinWidth(0);
		anonStudiesTable.getColumnModel().getColumn(3).setMaxWidth(0);
		anonStudiesTable.setPreferredScrollableViewportSize(new Dimension(430,130));
		anonStudiesTable.setDefaultRenderer(Date.class, new DateRenderer());
		anonStudiesTable.putClientProperty("terminateEditOnFocusLost", true);
		
		anonPatientTable.addFocusListener(tableFocusAnon);
		anonStudiesTable.addFocusListener(tableFocusAnon);

		displayAnonTool = new JButton("Anonymize");
		displayAnonTool.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(anonTablesPanel.isVisible()){
					//Deja ouverte on ferme
					openCloseAnonTool(false);
				}else{
					openCloseAnonTool(true);
				}
				pack();
			}
		});
		
		displayExportTool = new JButton("Export");
		displayExportTool.setToolTipText("Zip export, Dicom Send, CD Generation");
		displayExportTool.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (displayExportTool.getText().equals("Export")) {
					openCloseExportTool(true);
				}
				else if (displayExportTool.getText().equals("Close Export Tool")) {
					openCloseExportTool(false);
				}
				pack();
			}
		});
		
		displayManageTool = new JButton("Manage");
		displayManageTool.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (displayManageTool.getText().equals("Manage")){
					openCloseManageTool(true);
					pack();
					
				}
				else if (displayManageTool.getText().equals("Close Manage Tool")){
					openCloseManageTool(false);
					pack();
				}
			}
			
		});

		addToAnon = new JButton("Add to anonymization list");

		addToAnon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//If request for patient Table Add all studies in Anon
				if(lastTableFocusMain==tableauPatients) {
					int[] selectedRows=tableauPatients.getSelectedRows();
					for(int row:selectedRows) {
						int modelRow=tableauPatients.convertRowIndexToModel(row);
						Patient patient=modelePatients.getPatient(modelRow);
						//Get the studies listed in this patients
						ArrayList<Study2> studiesInPatient=patient.getStudies();
						//Conversion into a PatientAnon object to store the future anonymized name / id ...
						PatientAnon patientAnon=new PatientAnon(patient);
						//Set all listed studies in this patient
						patientAnon.addStudies(studiesInPatient);
						patientAnon.addAllChildStudiesToAnonymizeList();
						modeleAnonPatients.addPatient(patientAnon);	
					}	
				//Else Add Selected study
				}else if(lastTableFocusMain==tableauStudies) {
					int[] selectedRows=tableauStudies.getSelectedRows();
					for(int row:selectedRows) {
						int modelRow=tableauStudies.convertRowIndexToModel(row);
						modeleAnonPatients.addStudy(modeleStudies.getStudy(modelRow));
						
					}		
				}
				else {
					setStateMessage("Selection to Anonymize only possible from Patient Or Study table", "orange",4);
					
				}
			}
		});
		removeFromAnonList = new JButton("Remove");
		removeFromAnonList.setPreferredSize(new Dimension(120,27));
		removeFromAnonList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(lastTableFocusAnon==anonPatientTable) {
					//Erase patient row in reverse order to avoid index change problems
					int[] rows =anonPatientTable.getSelectedRows();
					Arrays.sort(rows);
				    for (int i = rows.length - 1; i >= 0; i--) {
				    	modeleAnonPatients.removePatient(lastTableFocusAnon.convertRowIndexToModel(anonPatientTable.getSelectedRow()));						
				    }
				    modeleAnonStudies.clear();
				}else if(lastTableFocusAnon==anonStudiesTable)  {
					PatientAnon patient=(PatientAnon)anonPatientTable.getValueAt(anonPatientTable.getSelectedRow(), anonPatientTable.convertColumnIndexToView(6));
					patient.removeOrthancIDfromAnonymize((String)anonStudiesTable.getValueAt(anonStudiesTable.getSelectedRow(), anonStudiesTable.convertRowIndexToView(2)));
					modeleAnonStudies.refresh();
					if(patient.getAnonymizeStudies().size()==0) {
						modeleAnonPatients.removePatient(anonPatientTable.convertRowIndexToModel(anonPatientTable.getSelectedRow()));
					}
				}
			}
		});
		
		importCTP = new JButton("Import DICOM");
		importCTP.setVisible(false);
		
		queryCTPBtn = new JButton("Query CTP");
		queryCTPBtn.setPreferredSize(new Dimension(120,27));
		queryCTPBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Si il y a des patients dans la liste
				if(!modeleAnonPatients.getPatientList().isEmpty()){
					// Si pas de study selectionnees on selectionne de force le 1er
					if (anonStudiesTable.getSelectedRow()==-1) anonStudiesTable.setRowSelectionInterval(0, 0);
					//On genere l'objet qui gere le CTP
					OTP_Gui dialog = new OTP_Gui(addressFieldCTP.getText());
					Study2 studyAnon=(Study2) anonStudiesTable.getValueAt(anonStudiesTable.getSelectedRow(), 4);
					//On prepare les donnees locales dans l'objet
					String patientName=studyAnon.getPatientName();
					String patientSex=studyAnon.getPatientSex();
					Date studyDate=studyAnon.getDate();
					Date patientDOB=studyAnon.getPatientDob();
					
					//envoi des donnes dans objet GUI pour CTP
					dialog.setStudyLocalValue(patientName, df.format(studyDate), patientSex, df.format(patientDOB));
					dialog.pack();
					dialog.setModal(true);
					dialog.setLocationRelativeTo(gui);
					dialog.setVisible(true);
					//On recupere les donnees et on met dans l'anonymisation
					if(dialog.getOk()) {
						//Change autoSend boolean to get the automatic send at the anonymize button click
						autoSendCTP=true;
						CTPUsername=dialog.getLogin();
						CTPPassword=dialog.getPassword();
						String patientNewName=dialog.getAnonName();
						String patientNewID=dialog.getAnonID();
						String visitName=dialog.getVisitName();
						anonPatientTable.setValueAt(patientNewName, anonPatientTable.getSelectedRow(), 3);
						anonPatientTable.setValueAt(patientNewID, anonPatientTable.getSelectedRow(), 4);
						anonStudiesTable.setValueAt(visitName, anonStudiesTable.getSelectedRow(), 0);
						//If only One patient and one study in the list, click the anonymize button to start the process
						if (anonPatientTable.getRowCount()==1 && anonStudiesTable.getRowCount()==1) {
							anonBtn.doClick();
						}
					}

				}
			}
		});
		
		anonBtn = new JButton("Anonymize");
		anonBtn.setPreferredSize(new Dimension(120,27));
		anonBtn.addActionListener(new Controller_Anonymize_Btn(this, connexionHttp));
		
		//Label to show the currently selected profile in the main panel
		JLabel profileLabel = new JLabel();

		anonBtnPanelTop = new JPanel(new FlowLayout());
		anonBtnPanelTop.add(addToAnon);
		anonBtnPanelTop.add(displayAnonTool);
		anonBtnPanelTop.add(displayExportTool);
		anonBtnPanelTop.add(displayManageTool);
		anonDetailed.add(anonBtnPanelTop, BorderLayout.NORTH);
		anonTablesPanel.add(new JScrollPane(anonPatientTable));
		anonTablesPanel.add(new JScrollPane(anonStudiesTable));
		
		JPanel anonBtnPanelRight = new JPanel(new GridLayout(0,1));
		anonBtnPanelRight.add(importCTP);
		anonBtnPanelRight.add(removeFromAnonList);
		anonBtnPanelRight.add(queryCTPBtn);
		anonBtnPanelRight.add(anonBtn);
		anonBtnPanelRight.add(profileLabel);
		
		anonTablesPanel.add(anonBtnPanelRight);
		anonTablesPanel.setVisible(false);
		addToAnon.setVisible(false);
		anonDetailed.add(anonTablesPanel, BorderLayout.WEST);

		exportZip.addActionListener(new Controller_Export_Zip(this));
		
		/////////////////////////////// ADDING COMPONENTS ////////////////
		JPanel p1 = new JPanel(new FlowLayout());
		GridBagConstraints c = new GridBagConstraints();
		JScrollPane jscp = new JScrollPane(tableauPatients);
		jscp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		tablesPanel.add(jscp,c);

		JScrollPane jscp2 = new JScrollPane(tableauStudies);
		jscp2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		c.gridx = 1;
		c.gridy = 0;
		tablesPanel.add(jscp2,c);

		JPanel panelTableauSeries=new JPanel(new BorderLayout());
		JScrollPane jscp3 = new JScrollPane(tableauSeries);
		jscp3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		c.gridx = 2;
		c.gridy = 0;
		panelTableauSeries.add(jscp3, BorderLayout.CENTER);
		JPanel panelButton=new JPanel();
		btnReadSeries=new JButton("Open Images");
		btnReadSeries.addActionListener(new Controller_Read_Series(this));
		
		panelButton.add(btnReadSeries);
		panelTableauSeries.add(panelButton, BorderLayout.EAST);
		tablesPanel.add(panelTableauSeries,c);

		mainPanel.add(tablesPanel);
		mainPanel.add(toolbox);
		mainPanel.add(anonDetailed);

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////// END TAB 1 : ANONYMIZATION //////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////// PANEL 2 : EXPORT ///////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		JPanel mainPanelExport = new JPanel(new BorderLayout());
		JPanel tableExportPanel = new JPanel(new FlowLayout());	

		this.tableauExportStudies = new JTable(modeleExportStudies);
		this.tableauExportStudies.getTableHeader().setReorderingAllowed(false);
		this.tableauExportStudies.getColumnModel().getColumn(0).setMinWidth(170);
		this.tableauExportStudies.getColumnModel().getColumn(0).setMaxWidth(170);
		this.tableauExportStudies.getColumnModel().getColumn(0).setResizable(false);
		this.tableauExportStudies.getColumnModel().getColumn(1).setMinWidth(120);
		this.tableauExportStudies.getColumnModel().getColumn(1).setMaxWidth(120);
		this.tableauExportStudies.getColumnModel().getColumn(1).setResizable(false);
		this.tableauExportStudies.getColumnModel().getColumn(2).setMinWidth(80);
		this.tableauExportStudies.getColumnModel().getColumn(2).setMaxWidth(80);
		this.tableauExportStudies.getColumnModel().getColumn(2).setResizable(false);
		this.tableauExportStudies.getColumnModel().getColumn(3).setMinWidth(180);
		this.tableauExportStudies.getColumnModel().getColumn(3).setMaxWidth(180);
		this.tableauExportStudies.getColumnModel().getColumn(3).setResizable(false);
		this.tableauExportStudies.getColumnModel().getColumn(4).setMinWidth(150);
		this.tableauExportStudies.getColumnModel().getColumn(4).setMaxWidth(150);
		this.tableauExportStudies.getColumnModel().getColumn(4).setResizable(false);
		this.tableauExportStudies.getColumnModel().getColumn(5).setMinWidth(0);
		this.tableauExportStudies.getColumnModel().getColumn(5).setMaxWidth(0);
		this.tableauExportStudies.getColumnModel().getColumn(5).setResizable(false);
		this.tableauExportStudies.setPreferredScrollableViewportSize(new Dimension(700,267));

		this.tableauExportStudies.setDefaultRenderer(Date.class, new DateRenderer());

		JPopupMenu popMenuExportStudies = new JPopupMenu();
		this.tableauExportStudies.setComponentPopupMenu(popMenuExportStudies);

		JMenuItem menuItemExportStudiesRemove = new JMenuItem("Remove from list");
		menuItemExportStudiesRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				modeleExportStudies.removeRow(tableauExportStudies.getSelectedRow());
			}
		});
		
		popMenuExportStudies.add(menuItemExportStudiesRemove);

		JMenuItem menuItemExportStudiesDelete = new JMenuItem("Delete this study");
		menuItemExportStudiesDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String url="/studies/" + tableauExportStudies.getValueAt(tableauExportStudies.getSelectedRow(), 5);
				
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() {
						boolean deleted=connexionHttp.makeDeleteConnection(url);
						if(deleted) {
							modeleExportStudies.removeRow(tableauExportStudies.getSelectedRow());
							modeleExportSeries.clear();
						}
					
						return null;
					}
				};
				worker.execute();
				
			}
		});
		
		popMenuExportStudies.add(menuItemExportStudiesDelete);

		JMenuItem menuItemEmptyList = new JMenuItem("Empty the export list");
		menuItemEmptyList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int dialogResult = JOptionPane.showConfirmDialog (gui, 
						"Are you sure you want to clear the export list ?",
						"Clearing the export list",
						JOptionPane.YES_NO_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION){
					modeleExportSeries.clear();
					modeleExportStudies.clear();
				}
			}
		});
		
		popMenuExportStudies.add(menuItemEmptyList);
		addPopUpMenuListener(popMenuExportStudies, tableauExportStudies);


		tableauExportStudies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.tableauExportStudies.getSelectionModel().addListSelectionListener(new TableExportStudiesMouseListener(tableauExportStudies, modeleExportSeries) );

		this.tableauExportSeries = new JTable(modeleExportSeries);
		this.tableauExportSeries.getTableHeader().setReorderingAllowed(false);
		this.tableauExportSeries.getColumnModel().getColumn(0).setMinWidth(260);
		this.tableauExportSeries.getColumnModel().getColumn(0).setMaxWidth(260);
		this.tableauExportSeries.getColumnModel().getColumn(0).setResizable(false);
		this.tableauExportSeries.getColumnModel().getColumn(1).setMinWidth(100);
		this.tableauExportSeries.getColumnModel().getColumn(1).setMaxWidth(100);
		this.tableauExportSeries.getColumnModel().getColumn(1).setResizable(false);
		this.tableauExportSeries.getColumnModel().getColumn(2).setMinWidth(100);
		this.tableauExportSeries.getColumnModel().getColumn(2).setMaxWidth(100);
		this.tableauExportSeries.getColumnModel().getColumn(2).setResizable(false);
		this.tableauExportSeries.getColumnModel().getColumn(3).setMinWidth(0);
		this.tableauExportSeries.getColumnModel().getColumn(3).setMaxWidth(0);
		this.tableauExportSeries.getColumnModel().getColumn(3).setResizable(false);
		this.tableauExportSeries.getColumnModel().getColumn(4).setMinWidth(0);
		this.tableauExportSeries.getColumnModel().getColumn(4).setMaxWidth(0);
		this.tableauExportSeries.getColumnModel().getColumn(4).setResizable(false);
		this.tableauExportSeries.setPreferredScrollableViewportSize(new Dimension(460,267));
		
		tableauExportSeries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPopupMenu popMenuExportSeries = new JPopupMenu();
		addPopUpMenuListener(popMenuExportSeries , tableauExportSeries);
		this.tableauExportSeries.setComponentPopupMenu(popMenuExportSeries);

		JMenuItem menuItemExportSeriesDelete = new JMenuItem("Delete this serie");
		menuItemExportSeriesDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					
				SwingWorker<Void, Void> worker = new SwingWorker<Void,Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						int selectedRow = tableauExportSeries.getSelectedRow();
						Serie serie= (Serie) tableauExportSeries.getValueAt(selectedRow,6);
						
						String url="/series/" + serie.getId();
						setStateExportMessage("Deleting "+serie.getSerieDescription(), "red", -1);
						boolean success=connexionHttp.makeDeleteConnection(url);
						if(success) {
							setStateExportMessage("Deleted suceeded", "green", 4);
						}else {
							setStateExportMessage("Delete Failed", "red", -1);
						}
						
						return null;
					}
					
					@Override
					protected void done() {
						try {
							get();
							//SK ICI GENERE UNE ERREUR
							modeleExportSeries.refresh();
							if(modeleExportSeries.getRowCount()==0) {
								modeleExportStudies.removeStudy(modeleExportSeries.getStudyAnonymizedID());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					
				};
				worker.execute();
				
			}
		});

		JMenuItem menuItemExportSeriesDeleteAllSc = new JMenuItem("Delete all secondary captures");
		menuItemExportSeriesDeleteAllSc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modeleExportSeries.removeAllSecondaryCaptures();
				if(modeleExportSeries.getRowCount()==0){
					modeleExportStudies.removeStudy(modeleExportSeries.getStudyAnonymizedID());
				}
			}
		});

		popMenuExportSeries.add(menuItemExportSeriesDelete);
		popMenuExportSeries.add(menuItemExportSeriesDeleteAllSc);

		tableExportPanel.add(new JScrollPane(this.tableauExportStudies));
		tableExportPanel.add(new JScrollPane(this.tableauExportSeries));

		stateExports.setBorder(new EmptyBorder(0, 0, 0, 40));

		JPanel labelPanelExport = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel exportToLabel = new JLabel("<html><font size=\"5\">Export list to...</font></html>");
		exportToLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
		labelPanelExport.add(exportToLabel);
		labelPanelExport.add(stateExports);

		exportBtn = new JButton("Remote server");
		exportBtn.setToolTipText("Fill the remote server parameters in the setup tab before attempting an export.");

		exportBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					
					@Override
					protected Void doInBackground() throws Exception {
						exportBtn.setText("Exporting...");
						exportBtn.setEnabled(false);
						
						ExportZip convertzip=new ExportZip(connexionHttp);
						convertzip.setConvertZipAction("tempZipOrthanc", modeleExportStudies.getOrthancIds(), true);
						convertzip.generateZip(false);
						String zipPath = convertzip.getGeneratedZipPath();
						String zipName = convertzip.getGeneratedZipName();
						remoteFileName = new StringBuilder();
						
						//removing the temporary file default name value
						remoteFileName.append(zipName.substring(0,14));
						remoteFileName.append(zipName.substring(zipName.length() - 4));
						SendFilesToRemote export = new SendFilesToRemote(jprefer.get("exportType", SendFilesToRemote.OPTION_FTP), 
								jprefer.get("remoteFilePath", "/"), remoteFileName.toString(), zipPath, jprefer.get("remoteServer", ""), 
								jprefer.getInt("remotePort", 21), jprefer.get("servUsername", ""), jprefer.get("servPassword", ""));
						export.export();
						
						
						return null;
					}

					@Override
					public void done(){
						try {
							get();
							setStateExportMessage("The data has been successfully been exported", "green", 4);							stateExports.setText("<html><font color='green'>The data has been successfully been exported</font></html>");
						} catch (Exception e) {
							setStateExportMessage("The data export failed", "red", -1);
							e.printStackTrace();
						}
						exportBtn.setText("Remote server");
						exportBtn.setEnabled(true);
					}
				};
				
			if(!modeleExportStudies.getOrthancIds().isEmpty()){
				stateExports.setText("Exporting...");
				worker.execute();
			}
		}
		});

		csvReport = new JButton("CSV Report");
		csvReport.addActionListener(new Controller_Csv_Btn(modeleExportStudies, this));
		
		exportCTP = new JButton("OTP");
		exportCTP.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
	
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					
					boolean sendOk;
					boolean validateOk=false;
					
					@Override
					protected Void doInBackground() {
						//Send DICOM to CTP selected Peer
						setStateExportMessage("Step 1/3 Sending to CTP Peer :"+listePeers.getSelectedItem().toString(), "red", -1);
						exportCTP.setEnabled(false);
						sendOk=connexionHttp.sendToPeer(listePeersCTP.getSelectedItem().toString(), modeleExportStudies.getOrthancIds());
						
						if (sendOk) {
							setStateExportMessage("Step 2/3 : Validating upload", "red", -1);
							//Create CTP object to manage CTP communication
							OTP ctp=new OTP(CTPUsername, CTPPassword, addressFieldCTP.getText());
							//Create the JSON to send
							JsonArray sentStudiesArray=new JsonArray();
							//For each study populate the array with studies details of send process
							for(Study2 study : modeleExportStudies.getAnonymizedStudy2Object()){
								study.storeStudyStatistics(queryOrthanc);
								//Creat Object to send to OTP
								JsonObject studyObject=new JsonObject();
								studyObject.addProperty("visitName", study.getStudyDescription());
								studyObject.addProperty("StudyInstanceUID", study.getStudyInstanceUid());
								studyObject.addProperty("patientNumber", study.getPatientName());
								studyObject.addProperty("instanceNumber", study.getStatNbInstance());
								sentStudiesArray.add(studyObject);

							}
							validateOk=ctp.validateUpload(sentStudiesArray);
							//If everything OK, says validated and remove anonymized studies from local
							if(validateOk) {
								setStateExportMessage("Step 3/3 : Deleting local study", "red", -1);
								for(Study2 study : modeleExportStudies.getAnonymizedStudy2Object()){
									//deleted anonymized and sent study
									connexionHttp.makeDeleteConnection("/studies/"+study.getOrthancId());
								}
								// empty the export table
								modeleExportStudies.clear();
								modeleExportSeries.clear();
							}
						}
						
						return null;
					}
			
					@Override
					protected void done(){
						exportCTP.setEnabled(true);
						if (sendOk && validateOk) {
							setStateExportMessage("CTP Export Done", "green", -1);
						} else if (!sendOk) {
							setStateExportMessage("Upload Failed", "red", -1);
						} else if (!validateOk) {
							setStateExportMessage("Validation Failed", "red", -1);
						}
					}
				};
				
				if(!modeleExportStudies.getOrthancIds().isEmpty()){
					worker.execute();
				}
			

			}
		});

		exportToZip = new JButton("Zip");
		exportToZip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						stateExports.setText("Converting to Zip...");
						exportToZip.setText("Converting to Zip...");
						exportToZip.setEnabled(false);
						File file = null;
						DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HHmmss");
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File(jprefer.get("zipLocation", ".")));
						chooser.setSelectedFile(new File(df.format(new Date()) + ".zip"));
						chooser.setDialogTitle("Export zip to...");
						chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						chooser.setAcceptAllFileFilterUsed(false);
						if (chooser.showSaveDialog(gui) == JFileChooser.APPROVE_OPTION) {
							file = chooser.getSelectedFile();
							ExportZip convertzip=new ExportZip(connexionHttp);
							convertzip.setConvertZipAction(file.getAbsolutePath().toString(), modeleExportStudies.getOrthancIds(), false);
							convertzip.generateZip(false);
						}
						return null;
					}

					@Override
					public void done(){
						try {
							get();
							setStateExportMessage("The data has been successfully been converted to zip", "green", -1);
						} catch (Exception e) {
							setStateExportMessage("Zip Export Failed", "red", -1);
							e.printStackTrace();
						}
						exportToZip.setText("Zip");
						exportToZip.setEnabled(true);
					}
				};
				
				if(!modeleExportStudies.getOrthancIds().isEmpty()){
					worker.execute();
				}
			}
		});


		listeAETExport = new JComboBox<String>();
		//Fill Aets combobox with values from Orthanc
		this.refreshAets();
		dicomStoreExport = new JButton("Store");
		dicomStoreExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					boolean storeSuccess;
					@Override
					protected Void doInBackground() {
						dicomStoreExport.setEnabled(false);
						dicomStoreExport.setText("Storing...");
						storeSuccess=connexionHttp.sendToAet(listeAETExport.getSelectedItem().toString(), modeleExportStudies.getOrthancIds());
						return null;
					}

					@Override
					protected void done(){
						if(storeSuccess) {
							stateExports.setText("<html><font color= 'green'>The request was successfully received</font></html>");
						}else {
							stateExports.setText("<html><font color= 'red'>The request was not received</font></html>");
						}
						
						dicomStoreExport.setText("Store");
						dicomStoreExport.setEnabled(true);
					}
				};
				if(!modeleExportStudies.getOrthancIds().isEmpty()){
					stateExports.setText("Storing data...");
					worker.execute();
				}
			}
		});

		listePeers = new JComboBox<String>();
		peerExport = new JButton("OrthancPeer");
		peerExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					boolean sendok;
					@Override
					protected Void doInBackground() {
						peerExport.setEnabled(false);
						peerExport.setText("Sending...");
						sendok=connexionHttp.sendToPeer(listePeers.getSelectedItem().toString(), modeleExportStudies.getOrthancIds());
						return null;
					}

					@Override
					protected void done(){
						if(sendok) {
							stateExports.setText("<html><font color= 'green'>The upload was successfully received</font></html>");
						}else {
							stateExports.setText("<html><font color= 'red'>The upload was not received </font></html>");
						}
						peerExport.setText("OrthancPeer");
						peerExport.setEnabled(true);
					}
				};
				if(!modeleExportStudies.getOrthancIds().isEmpty()){
					stateExports.setText("Sending to "+listePeers.getSelectedItem().toString());
					worker.execute();
				}
			}
		});
		
		JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,50,10));
		exportPanel.add(exportCTP);
		exportPanel.add(csvReport);
		exportPanel.add(exportToZip);
		exportPanel.add(exportBtn);

		JPanel dicomExport=new JPanel();
		dicomExport.add(listeAETExport);
		dicomExport.add(dicomStoreExport);
		
		JPanel peersExport=new JPanel();
		peersExport.add(listePeers);
		peersExport.add(peerExport);
		
		exportPanel.add(dicomExport);
		exportPanel.add(peersExport);
			
		JPanel southExport = new JPanel();
		southExport.setLayout(new BoxLayout(southExport, BoxLayout.PAGE_AXIS));
		southExport.add(labelPanelExport);
		southExport.add(exportPanel);

		mainPanelExport.add(southExport, BorderLayout.SOUTH);
		mainPanelExport.add(tableExportPanel, BorderLayout.CENTER);
		JPanel p2 = new JPanel(new FlowLayout());

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////// END PANEL 2 : EXPORT ///////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////


		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////// PANEL 3 : SETUP ////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		JPanel mainPanelSetup = new JPanel();
		mainPanelSetup.setLayout(new BorderLayout());

		JPanel westSetup = new JPanel(new GridBagLayout());
		JPanel tabSetup = new JPanel(new GridBagLayout());
		JPanel westNorth1Setup = new JPanel(new FlowLayout());
		JPanel westNorth2Setup = new JPanel(new FlowLayout());
		JPanel eastExport = new JPanel(new GridBagLayout());
		
		JPanel clinicalTrialProcessorGrid = new JPanel(new GridLayout(3,2));
		JPanel clinicalTrialProcessor =new JPanel();
		clinicalTrialProcessor.add(clinicalTrialProcessorGrid);
		

		GridBagConstraints gbSetup = new GridBagConstraints();
		gbSetup.gridx = 0;
		gbSetup.gridy = 0;
		JLabel centerCodeLabel = new JLabel("Center code");
		centerCodeLabel.setBorder(new EmptyBorder(0, 0, 0, 73));
		westNorth1Setup.add(centerCodeLabel);
		this.centerCode = new JTextField();
		this.centerCode.setText(jprefer.get("centerCode", "12345"));
		this.centerCode.setPreferredSize(new Dimension(100,20));
		westNorth1Setup.add(this.centerCode);
		westSetup.add(westNorth1Setup);

		gbSetup.gridx = 0;
		gbSetup.gridy = 1;
		anonProfiles = new JComboBox<String>(new String[]{"Default", "Full clearing", "Custom"});
		anonProfiles.setPreferredSize(new Dimension(100,27));
		JLabel anonProfilesLabel = new JLabel("Anonymization profile");
		anonProfilesLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
		westNorth2Setup.add(anonProfilesLabel);
		westNorth2Setup.add(anonProfiles);
		westNorth2Setup.setBorder(new EmptyBorder(10, 0, 20, 0));
		westSetup.add(westNorth2Setup, gbSetup);

		JLabel bcLabel = new JLabel("Body characteristics");
		JLabel datesLabel = new JLabel("Full dates");
		JLabel bdLabel = new JLabel("Birth date*");
		bdLabel.setToolTipText("Choosing clear will actually change the birth date to 01/01/1900");
		JLabel ptLabel = new JLabel("Private tags");
		JLabel scLabel = new JLabel("Secondary capture/Structured reports");
		JLabel descLabel = new JLabel("Series/Study descriptions");

		gbSetup.gridy = 0;
		gbSetup.anchor = GridBagConstraints.WEST;
		gbSetup.insets = new Insets(0, 0, 20, 0);
		gbSetup.gridx = 1;
		JLabel keepLabel = new JLabel("Keep");
		tabSetup.add(keepLabel, gbSetup);
		gbSetup.gridx = 2;
		JLabel clearLabel = new JLabel("Clear");
		tabSetup.add(clearLabel, gbSetup);

		// Body characteristics
		gbSetup.insets = new Insets(0, 0, 10, 50);
		gbSetup.gridx = 0;
		gbSetup.gridy = 1;
		tabSetup.add(bcLabel, gbSetup);
		ButtonGroup bgBodyCharac = new ButtonGroup();

		JRadioButton radioBodyCharac1 = new JRadioButton();
		JRadioButton radioBodyCharac2 = new JRadioButton();
		this.settingsBodyCharButtons[0] = radioBodyCharac1;
		this.settingsBodyCharButtons[1] = radioBodyCharac2;
		bgBodyCharac.add(radioBodyCharac1);
		gbSetup.gridx = 1;
		tabSetup.add(radioBodyCharac1, gbSetup);
		bgBodyCharac.add(radioBodyCharac2);
		gbSetup.gridx = 2;
		tabSetup.add(radioBodyCharac2, gbSetup);

		// Dates
		gbSetup.gridx = 0;
		gbSetup.gridy = 2;
		tabSetup.add(datesLabel, gbSetup);
		ButtonGroup bgDates = new ButtonGroup();
		JRadioButton radioDates1 = new JRadioButton();
		JRadioButton radioDates2 = new JRadioButton();
		this.settingDatesButtons[0] = radioDates1;
		this.settingDatesButtons[1] = radioDates2;
		bgDates.add(radioDates1);
		gbSetup.gridx = 1;
		tabSetup.add(radioDates1, gbSetup);
		bgDates.add(radioDates2);
		gbSetup.gridx = 2;
		tabSetup.add(radioDates2, gbSetup);

		// Birth date
		gbSetup.gridx = 0;
		gbSetup.gridy = 3;
		tabSetup.add(bdLabel, gbSetup);
		ButtonGroup bgBd = new ButtonGroup();
		JRadioButton radioBd1 = new JRadioButton();
		JRadioButton radioBd2 = new JRadioButton();
		this.settingsBirthDateButtons[0] = radioBd1;
		this.settingsBirthDateButtons[1] = radioBd2;
		bgBd.add(radioBd1);
		gbSetup.gridx = 1;
		tabSetup.add(radioBd1, gbSetup);
		bgBd.add(radioBd2);
		gbSetup.gridx = 2;
		tabSetup.add(radioBd2, gbSetup);

		// Private tags
		gbSetup.gridx = 0;
		gbSetup.gridy = 4;
		tabSetup.add(ptLabel, gbSetup);
		ButtonGroup bgPt = new ButtonGroup();
		JRadioButton radioPt1 = new JRadioButton();
		JRadioButton radioPt2 = new JRadioButton();
		this.settingsPrivateTagButtons[0] = radioPt1;
		this.settingsPrivateTagButtons[1] = radioPt2;
		bgPt.add(radioPt1);
		gbSetup.gridx = 1;
		tabSetup.add(radioPt1, gbSetup);
		bgPt.add(radioPt2);
		gbSetup.gridx = 2;
		tabSetup.add(radioPt2, gbSetup);

		// Secondary capture
		gbSetup.gridx = 0;
		gbSetup.gridy = 5;
		tabSetup.add(scLabel, gbSetup);
		ButtonGroup bgSc = new ButtonGroup();
		JRadioButton radioSc1 = new JRadioButton();
		JRadioButton radioSc2 = new JRadioButton();
		this.settingsSecondaryCaptureButtons[0] = radioSc1;
		this.settingsSecondaryCaptureButtons[1] = radioSc2;
		bgSc.add(radioSc1);
		gbSetup.gridx = 1;
		tabSetup.add(radioSc1, gbSetup);
		bgSc.add(radioSc2);
		gbSetup.gridx = 2;
		tabSetup.add(radioSc2, gbSetup);

		// Study/serie description
		gbSetup.gridx = 0;
		gbSetup.gridy = 6;
		tabSetup.add(descLabel, gbSetup);
		ButtonGroup bgDesc = new ButtonGroup();
		JRadioButton radioDesc1 = new JRadioButton();
		JRadioButton radioDesc2 = new JRadioButton();
		this.settingsStudySerieDescriptionButtons[0] = radioDesc1;
		this.settingsStudySerieDescriptionButtons[1] = radioDesc2;
		bgDesc.add(radioDesc1);
		gbSetup.gridx = 1;
		tabSetup.add(radioDesc1, gbSetup);
		bgDesc.add(radioDesc2);
		gbSetup.gridx = 2;
		tabSetup.add(radioDesc2, gbSetup);

		anonProfiles.addActionListener(
				new AnonActionProfileListener(anonProfiles, profileLabel, settingsBodyCharButtons,settingDatesButtons,
						settingsBirthDateButtons,settingsPrivateTagButtons, settingsSecondaryCaptureButtons,
						settingsStudySerieDescriptionButtons));

		anonProfiles.setSelectedItem(jprefer.get("profileAnon", "Default"));

		JTabbedPane eastSetupPane = new JTabbedPane();
		eastSetupPane.add("Export setup", eastExport);
		eastSetupPane.addTab("CTP", null, clinicalTrialProcessor, "Clinical Trial Processor");

		gbSetup.insets = new Insets(20, 10, 0, 10);
		gbSetup.gridx = 0;
		gbSetup.gridy = 2;
		westSetup.add(tabSetup, gbSetup);
		
		mainPanelSetup.add(westSetup, BorderLayout.WEST);

		gbSetup.gridx = 0;
		gbSetup.gridy = 0;
		eastExport.add(new JLabel("Adress"), gbSetup);

		gbSetup.gridx = 1;
		gbSetup.gridy = 0;
		this.remoteServer = new JTextField();
		this.remoteServer.setText(jprefer.get("remoteServer", ""));
		this.remoteServer.setPreferredSize(new Dimension(300,20));
		eastExport.add(this.remoteServer, gbSetup);

		gbSetup.gridx = 0;
		gbSetup.gridy = 1;
		eastExport.add(new JLabel("Port"), gbSetup);

		gbSetup.gridx = 1;
		gbSetup.gridy = 1;
		this.remotePort = new JTextField();
		this.remotePort.setText(jprefer.get("remotePort", ""));
		this.remotePort.setPreferredSize(new Dimension(300,20));
		eastExport.add(this.remotePort, gbSetup);
		
		gbSetup.gridx = 0;
		gbSetup.gridy = 2;
		eastExport.add(new JLabel("Username"), gbSetup);

		gbSetup.gridx = 1;
		gbSetup.gridy = 2;
		this.servUsername = new JTextField();
		this.servUsername.setText(jprefer.get("servUsername", ""));
		this.servUsername.setPreferredSize(new Dimension(300,20));
		eastExport.add(this.servUsername, gbSetup);

		gbSetup.gridx = 0;
		gbSetup.gridy = 3;
		eastExport.add(new JLabel("Password"), gbSetup);

		gbSetup.gridx = 1;
		gbSetup.gridy = 3;
		this.servPassword = new JPasswordField();
		this.servPassword.setText(jprefer.get("servPassword", ""));
		this.servPassword.setPreferredSize(new Dimension(300,20));
		eastExport.add(this.servPassword, gbSetup);

		gbSetup.gridx = 0;
		gbSetup.gridy = 4;
		eastExport.add(new JLabel("Remote file path"), gbSetup);

		gbSetup.gridx = 1;
		gbSetup.gridy = 4;
		this.remoteFilePath = new JTextField();
		this.remoteFilePath.setText(jprefer.get("remoteFilePath", "/"));
		this.remoteFilePath.setPreferredSize(new Dimension(300,20));
		eastExport.add(this.remoteFilePath, gbSetup);

		gbSetup.gridx = 0;
		gbSetup.gridy = 5;
		eastExport.add(new JLabel("Export protocol"), gbSetup);

		gbSetup.gridx = 1;
		gbSetup.gridy = 5;
		String[] exportTypeList = {"FTP", "SFTP", "WEBDAV"};
		this.exportType = new JComboBox<String>(exportTypeList);
		this.exportType.setSelectedItem(jprefer.get("exportType", "FTP"));
		this.exportType.setPreferredSize(new Dimension(140,20));
		eastExport.add(this.exportType, gbSetup);

		//add CTP Panel
		JLabel address=new JLabel("CTP Address");
		addressFieldCTP=new JTextField();
		addressFieldCTP.setToolTipText("Include http:// or https://");
		addressFieldCTP.setPreferredSize(new Dimension(300,20));
		addressFieldCTP.setText(jprefer.get("CTPAddress", "http://"));
		JLabel peerLabel=new JLabel("CTP Peer");
		listePeersCTP = new JComboBox<String>();
		this.refreshPeers();
		listePeersCTP.insertItemAt("Choose", 0);
		if(jprefer.getInt("CTPPeer", 0) <= listePeersCTP.getItemCount()-1) listePeersCTP.setSelectedIndex(jprefer.getInt("CTPPeer", 0));
		else listePeersCTP.setSelectedIndex(0);
		clinicalTrialProcessorGrid.add(address);
		clinicalTrialProcessorGrid.add(addressFieldCTP);
		clinicalTrialProcessorGrid.add(peerLabel);
		clinicalTrialProcessorGrid.add(listePeersCTP);
		
		JPanel aboutPanel = new JPanel(new FlowLayout());
		JButton viewerDistribution = new JButton("Download Viewer Distribution");
		viewerDistribution.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Select folder for CD/DVD output");
				chooser.setSelectedFile(new File("ImageJ.zip"));
				chooser.setDialogTitle("Dowload Viewer to...");
				if (! jprefer.get("viewerDistribution", "empty").equals("empty") ) {
					chooser.setSelectedFile(new File (jprefer.get("viewerDistribution", "empty")));
				}
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showSaveDialog(gui) == JFileChooser.APPROVE_OPTION) {
					viewerDistribution.setBackground(Color.ORANGE);
					SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() {
							try {
								FileUtils.copyURLToFile(new URL("http://petctviewer.org/images/ImageJ.zip"), chooser.getSelectedFile());
								//Message confirmation
								JOptionPane.showMessageDialog(gui, "Viewer distribution sucessfully downloaded");
							} catch (IOException e) {
								e.printStackTrace();
								JOptionPane.showMessageDialog(gui, "Download Failed",  "Error", JOptionPane.ERROR_MESSAGE);
							}
							return null;
						}

						@Override
						protected void done(){
							// Enregistre la destination du fichier dans le registery
							jprefer.put("viewerDistribution", chooser.getSelectedFile().toString());
							viewerDistribution.setBackground(null);
						}
					};
					worker.execute();
				}

			}
			
		});
		
		//Setup button only for starting outside Fiji
		JButton setupButton = new JButton("Orthanc HTTP Setup");
		setupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConnectionSetup setup = new ConnectionSetup(runOrthanc, connexionHttp);
				setup.setLocationRelativeTo(gui);
				setup.setVisible(true);
				connexionHttp=new OrthancRestApis(null);
				if (setup.ok)JOptionPane.showMessageDialog(null,"Restart to take account");
				
			}
			
		});
		
		JButton jsonEditor = new JButton("Edit Orthanc config");
		jsonEditor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SettingsGUI settings=new SettingsGUI();
				settings.pack();
				settings.setLocationRelativeTo(gui);
				settings.updateGUI();
				settings.setVisible(true);
			}
			
		});
		
		JButton aboutBtn = new JButton("About us");
		aboutBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutBoxFrame ab = new AboutBoxFrame(gui);
				ab.setVisible(true);
			}
		});
		
		aboutPanel.add(viewerDistribution);
		aboutPanel.add(setupButton);
		aboutPanel.add(jsonEditor);
		aboutPanel.add(aboutBtn);
		
		mainPanelSetup.add(westSetup, BorderLayout.WEST);
		mainPanelSetup.add(eastSetupPane, BorderLayout.EAST);
		mainPanelSetup.add(aboutPanel, BorderLayout.SOUTH);

		JPanel p3 = new JPanel(new FlowLayout());

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////// END TAB 3 : SETUP //////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
	
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////// TAB 4 : Monitor //////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		monitoring = new Monitoring_GUI(connexionHttp);
		JPanel panelMonitoring = (JPanel) monitoring.getContentPane();
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////// END TAB 4 : Monitor //////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new Tab_Change_Listener(this));

		p1.add(mainPanel);
		tabbedPane.add("Main", p1);

		p2.add(mainPanelExport);
		tabbedPane.add("Export Anonymized", p2);
		
		//Add monitoring
		tabbedPane.addTab("Monitoring", panelMonitoring);

		p3.add(mainPanelSetup);
		tabbedPane.add("Setup", p3);
		
		Image image = new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage();
		this.setIconImage(image);
		this.getContentPane().add(tabbedPane);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.getRootPane().setDefaultButton(searchBtn);
		this.addWindowListener(new Window_Custom_Listener(this, zipContent, modeleAnonPatients, modeleExportStudies, monitoring, runOrthanc));
		pack();
		userInput.requestFocus();
	}
	
	//private enum panels {Anon,Export,Manage};
	//private panels currentopenPanel;
	
	public void openCloseAnonTool(boolean open) {
		
		if (open) {
			closeAllPanel();
			oToolRight.setVisible(false);
			anonTablesPanel.setVisible(true);
			addToAnon.setVisible(true);
			displayExportTool.setVisible(false);
			displayManageTool.setVisible(false);
			displayAnonTool.setText("Close anonymization tool");
		}
		else {
			closeAllPanel();
		}
		pack();
	}
	
	public void openCloseExportTool(boolean open) {
		if (open) {
			closeAllPanel();
			oToolRight.setVisible(true);
			displayExportTool.setText("Close Export Tool");
			displayAnonTool.setVisible(false);
			displayManageTool.setVisible(false);
		}
		else {
			closeAllPanel();
		}
		pack();
	}
	
	private void openCloseManageTool(boolean open) {
		if (open) {
			closeAllPanel();
			oToolRightManage.setVisible(true);
			displayManageTool.setText("Close Manage Tool");
			displayAnonTool.setVisible(false);
			displayExportTool.setVisible(false);
		}
		else {
			closeAllPanel();
		}
		pack();
	}
	
	private void closeAllPanel() {
		//Hide all panels
		oToolRightManage.setVisible(false);
		oToolRight.setVisible(false);
		anonTablesPanel.setVisible(false);
		addToAnon.setVisible(false);
		//reset button to default value
		displayManageTool.setText("Manage");
		displayExportTool.setText("Export");
		displayAnonTool.setText("Anonymize");
		//Show buttons
		displayAnonTool.setVisible(true);
		displayExportTool.setVisible(true);
		displayManageTool.setVisible(true);
	}
	
	private void enableManageButtons(boolean enable) {
		addManage.setEnabled(enable);
		removeFromManage.setEnabled(enable);
	}
	
	public void activateExport(boolean activate){
		exportZip.setEnabled(activate);
		addToZip.setEnabled(activate);
		removeFromZip.setEnabled(activate);
		comboToolChooser.setEnabled(activate);
	}
	
	// Ajoute seletion a la tool list
	private void addToToolList(ArrayList<String> zipContent, JComboBox<String> zipShownContent, JLabel zipSize){

		//On recupere la table qui a eu le dernier focus pour la selection
		JTable tableau =  lastTableFocusMain;
		int[] selectedLines=tableau.getSelectedRows();
		boolean duplicate = false;
		if(tableau.equals(tableauPatients)){
			for (int i=0; i<selectedLines.length; i++){
				Patient selectedPatient = (Patient) tableauPatients.getValueAt(selectedLines[i], 5);
				ArrayList<Study2> studiesInPatient= selectedPatient.getStudies();
				
				for (Study2 study : studiesInPatient) {
					if(!zipContent.contains(study.getOrthancId())){
						String name = "Patient - " + tableauPatients.getValueAt(selectedLines[i], 0).toString()+" Study Date "+df.format(study.getDate())+" Desc "+study.getStudyDescription();
						zipShownContent.addItem(name);
						zipContent.add(study.getOrthancId());
					}else{
						duplicate=true;
					}
				}	
			}
			
		}else if(tableau.equals(tableauStudies)){
			for (int i=0; i<selectedLines.length; i++){
				Study2 selectedStudy=(Study2)tableauStudies.getValueAt(selectedLines[i], 4);
				
				String date = "Patient - "+selectedStudy.getPatientName()+" Study - " + df.format(selectedStudy.getDate()) + " Desc " + selectedStudy.getStudyDescription();
				String id = tableauStudies.getValueAt(selectedLines[i], 3).toString();
				if(!zipContent.contains(id)){
					zipShownContent.addItem(date);
					zipContent.add(id);
				}else{
				duplicate=true;
				}
			}
			
			
		}else if (tableau.equals(tableauSeries)){
			for (int i=0; i<selectedLines.length; i++){
				String desc = "Serie - [" + tableauSeries.getValueAt(selectedLines[i], 1) + "] "+ tableauSeries.getValueAt(selectedLines[i], 2)+ " instances - " + tableauSeries.getValueAt(selectedLines[i], 0);
				String id = tableauSeries.getValueAt(selectedLines[i], 4).toString();
				if(!zipContent.contains(id)){
					zipShownContent.addItem(desc);
					zipContent.add(id);
				}else{
					duplicate=true;
				}
			}
			
		}
		if (duplicate) state.setText("<html><font color = 'red'> Some elements already in list</font></html>");
		if(zipSize != null){
			zipSize.setText(zipContent.size() + " element(s)");
		}
		pack();
	
	}
	
	/**
	 * Add studies to export list (for AutoQuery result import)
	 * @param study
	 */
	public void importStudiesInExportList(Study2[] study){
		//Clear previous list
		zipShownContent.removeAllItems();
		zipContent.clear();
		//Add new studies
		for (int i=0; i<study.length; i++){
			String date = "Study - " + df.format(study[i].getDate()) + "  " + study[i].getStudyDescription();
			String id = study[i].getOrthancId();
			if(!zipContent.contains(id)){
				zipShownContent.addItem(date);
				zipContent.add(id);
			}
		}
	}
	
	public void importStudiesInAnonList(Study2[] study) {
		modeleAnonPatients.clear();
		for (int i=0; i<study.length; i++){
			modeleAnonPatients.addStudy(study[i]);
		}
		
	}
	
	// remove ligne active a la tool list
	private void removeFromToolList(ArrayList<String> zipContent, JComboBox<String> zipShownContent, JLabel zipSize, JLabel state){
		if(!zipContent.isEmpty()){
			zipContent.remove(zipShownContent.getSelectedIndex());
			zipShownContent.removeItemAt(zipShownContent.getSelectedIndex());
			if(zipContent.size() >= 1){
				zipSize.setText(zipContent.size() + " element(s)");
			}else{
				state.setText("");
				zipSize.setText(" Empty List");
				pack();
			}
		}
	}
	
	public void enableAnonButton(boolean enable) {
		anonBtn.setEnabled(enable);
		addToAnon.setEnabled(enable);
		queryCTPBtn.setEnabled(enable);
		removeFromAnonList.setEnabled(enable);
		importCTP.setEnabled(enable);
	}
	
	public void enableReadButton(boolean enable) {
		btnReadSeries.setEnabled(enable);
	}
	
	public void refreshAets() {
		String[] aets=connexionHttp.getAET();
		listeAET.setModel(new DefaultComboBoxModel<String>(aets)) ;
		listeAETExport.setModel(new DefaultComboBoxModel<String>(aets));
	}
	
	public void refreshPeers() {
		String[] peers=connexionHttp.getPeers();
		listePeersCTP.setModel(new DefaultComboBoxModel<String>(peers)) ;
		listePeers.setModel(new DefaultComboBoxModel<String>(peers));
	}
	
	public void setAnonymizeListener(AnonymizeListener anonymizeListener) {
		this.anonymizeListener=anonymizeListener;
	}
	
	
	private void addPopUpMenuListener(JPopupMenu popupMenu , JTable table) {
		popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
                        if (rowAtPoint > -1) {
                        	table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                        }
                    }
                });
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}
        });
	}
	
	//SK A revoir ne MARCHE PAS
	public static void toogleScRenderer(JTable table, boolean activate, int scColumn) {
		if(activate) {
			table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
				private static final long serialVersionUID = 1L;

				@Override
				public Component getTableCellRendererComponent(JTable table,
						Object value, boolean isSelected, boolean hasFocus, int row, int col) {
					
					boolean status = (boolean) table.getModel().getValueAt(row, 3);
					if (status && !isSelected) {
						setBackground(Color.RED);
						setForeground(Color.black);
					}else if(isSelected){
						super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
					}else{
						super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
					}
					return this;
				}   
			});
			
		}else {
			table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
		}
		
	}
	
	
	public void setStateMessage(String message, String color, int seconds) {
		state.setText("<html><font color='"+color+"'>"+message+"</font></html>");
		if(seconds !=(-1)) {
			Timer timer=new Timer();
			timer.schedule(new TimerTask() {
				  @Override
				  public void run() {
					  state.setText("");
				  }
				}, seconds*1000);
		}
		
	}
	
	public void setStateExportMessage(String message, String color, int seconds) {
		stateExports.setText("<html><font color='"+color+"'>"+message+"</font></html>");
		if(seconds !=(-1)) {
			Timer timer=new Timer();
			timer.schedule(new TimerTask() {
				  @Override
				  public void run() {
					  stateExports.setText("");
				  }
				}, seconds*1000);
		}
		
	}
	
	public String getComboToolChooserSeletedItem() {
		return (String) comboToolChooser.getSelectedItem();
	}
	 
	//Getters Setup Tab
	public String getSelectedAnonProfile() {
		return (String) anonProfiles.getSelectedItem();
	}
	
	public String getCenterCode() {
		return centerCode.getText();
	}
	
	public String getCTPaddress() {
		return addressFieldCTP.getText();
	}
	
	public void setCTPaddress(String address) {
		addressFieldCTP.setText(address);
	}
	
	public String[] getExportRemoteServer() {
		String[] remoteServerParameter=new String[6];
		remoteServerParameter[0]=remoteServer.getText();
		remoteServerParameter[1]=remotePort.getText();
		remoteServerParameter[2]=servUsername.getText();
		remoteServerParameter[3]= new String(servPassword.getPassword());
		remoteServerParameter[4]= remoteFilePath.getText();
		remoteServerParameter[5]= exportType.getSelectedItem().toString();
		
		return remoteServerParameter;
	}
	
	public void showRemoteExportBtn(boolean show) {
		exportBtn.setVisible(show);
		exportBtn.setVisible(show);
	}
	
	public String getExportRemotePort() {
		return remotePort.getText();
	}
	
	public void showCTPButtons(boolean show) {
		exportCTP.setVisible(show);
		queryCTPBtn.setVisible(show);
	}
	
	public OrthancRestApis getOrthancApisConnexion() {
		return connexionHttp;
	}
	
	public JButton getSearchButton() {
		return searchBtn;
	}
	
	public QueryOrthancData getOrthancQuery() {
		return queryOrthanc;
	}

}
