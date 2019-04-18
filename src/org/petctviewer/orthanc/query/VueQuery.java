/**
Copyright (C) 2017 VONGSALAT Anousone

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

package org.petctviewer.orthanc.query;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.anonymize.gui.DateRenderer;
import org.petctviewer.orthanc.query.autoquery.AutoQuery;
import org.petctviewer.orthanc.query.autoquery.gui.AutoQueryOptions;
import org.petctviewer.orthanc.query.autoquery.gui.AutoQueryShowResultDialog;
import org.petctviewer.orthanc.query.autoquery.gui.AutoQuery_Retrieve_Results;
import org.petctviewer.orthanc.query.autoquery.gui.Daily_Retrieve_Gui;
import org.petctviewer.orthanc.query.datastorage.StudyDetails;
import org.petctviewer.orthanc.query.listeners.ChangeTabListener;
import org.petctviewer.orthanc.query.listeners.FilterAction;
import org.petctviewer.orthanc.query.listeners.Retrieve_Action;
import org.petctviewer.orthanc.query.listeners.TableStudyMouseListener;
import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.michaelbaranov.microba.calendar.DatePicker;

public class VueQuery extends JFrame {
	
	private static final long serialVersionUID = 1L;
	// Instancie la classe rest qui fournit les services de connexion et input
	private QueryRetrieve rest;
	private JFrame gui;
	private VueAnon vueAnon;
	
	private JTabbedPane tabbedPane;
	
	private ModelTableStudy modeleTablePatients; // model for the main JTable (tableau)
	private ModelTableSeries modeleTableSeries; // model for the details JTable (tableauDetails) in the main tab
	private ModelTableStudy modeleTablePatientHistory; // model for the history JTable (tab History)
	private ModelTableSeries modeleTableSeriesHistory; // model for the details JTable (tableauDetails) in the history tab
	private JTable tablePatients; // displayed table in the main tab
	private JTable tableSeries; // displayed table containing the details in the main tab
	private JTable tablePatientsHistory; // displayed table in the history tab
	private JTable tableSeriesHistory; // displayed table containing the details in the history tab
	
	/*
	 * The following components will be used to filter the tables, or make new searches
	 */
	private JComboBox<String> searchingParam; // indexes the "main" searching parameter (name, id, accession number)
	private JComboBox<String> queryAET; // indexes every AETs available that the user can query from
	private JComboBox<String> retrieveAET; // indexes every AETs available that the user can retrieve instances to
	private JLabel state; // allows the user to know the state of the retrieve query 
	private JTextField userInput; // associated with searchingParam to get the input
	private JTextField userInputFirstName; //First Name input in case of Name search
	private JPanel checkboxes; // contains every checkboxes
	private JCheckBox cr,ct,mr,nm,pt,us,xa,mg, dx;
	private JTextField customModalities; // the chosen modalities 
	private JTextField description; // allows to search for a particular description
	private DatePicker from, to; // allow to make a research in a user defined time frame
	private TableRowSorter<ModelTableStudy> sorter; // used to filter and sort the rows for the main JTable
	private TableRowSorter<ModelTableSeries> sorterDetails; // used to filter and sort the rows for the details JTable
	private JButton retrieve;
	
	// Tab History
	private JComboBox<String> queryAETH; // indexes every AETs available that the user can get patient from (usually PACS)
	private JComboBox<String> retrieveAETH; // indexes every AETs available that the user can retrieve instances to
	private JLabel stateH; // allows the user to know the state of the retrieve query
	private JPanel checkboxesH;
	private JCheckBox crH,ctH,mrH,nmH,ptH,usH,xaH,mgH, dxH;
	private JTextField customModalitiesH;  
	private DatePicker fromH, toH; // allow to make a research in a user defined time frame
	private TableRowSorter<ModelTableStudy> sorterH; // used to sort the rows for the main JTable
	private TableRowSorter<ModelTableSeries> sorterDetailsH; // used to filter and sort the rows for the details JTable
	private JButton retrieveH;
	
	// Tab Setup
	private Preferences jprefer = VueAnon.jprefer;
	
	//Working status
	private boolean working;
	
	//Processus Swing
	SwingWorker<Void,Void> workerCsvRetrieve, workerRetrieve, worker ;
	
	//AutoQuery
	
	private JTable table;
	private DateFormat df = new SimpleDateFormat("yyyyMMdd");
	private JComboBox<String> comboBox_RetrieveAet;
	private JComboBox<String> Aet_Retrieve;
	private JButton btnScheduleDaily;
	
	private JButton btnSchedule_1 ;
	private JLabel info;
	private AutoQuery autoQuery;
	//private JTextArea textAreaConsole;
	private SwingWorker<Void, Void> workerAutoRetrieve;
	private JButton btnStart ;
	
	//timer
	private boolean timerOn;
	private boolean timerOnDaily;
	private Timer timer;
	private Timer timerDaily;
	
	//Aets
	String[] listRetrieveAET;
	String[] distantAets;
	
	//Last focused table
	private JTable lastFocusMain;
	private JTable lastFocusHistory;
    
	public VueQuery(OrthancRestApis http, VueAnon vueAnon) {
		
		super("Orthanc queries");
		this.vueAnon=vueAnon;
		rest=new QueryRetrieve(http);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			
	    @Override
	    public void windowClosing(WindowEvent e) {
	    	if (working==true) {
		    	String ObjButtons[] = {"Yes","No"};
				int PromptResult = JOptionPane.showOptionDialog(null,"Operation pending Are you sure you want to exit?","Orthanc Query",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
				if (PromptResult==JOptionPane.YES_OPTION) {
					dispose();
				}
	    	}
	    	else {
	    		dispose();
	    	}
	    }});
		
		//Stores AET
		storeAets();
		createModelTable();



		///////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////     TAB 1 : QUERIES/RETRIEVE ///////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		this.sorter = new TableRowSorter<ModelTableStudy>(modeleTablePatients);
		this.sorterDetails = new TableRowSorter<ModelTableSeries>(modeleTableSeries);
		this.sorter.setSortsOnUpdates(true);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		JPanel north = new JPanel(new GridLayout(1,2));
		JPanel northG = new JPanel(new GridLayout(2,1));
		JPanel south = new JPanel();
		south.setLayout(new BoxLayout(south, BoxLayout.LINE_AXIS));
		JPanel southD = new JPanel(new FlowLayout());

		// Creating the main JTable containing the patients (the left one)
		tablePatients = new JTable(modeleTablePatients);
		tablePatients.setRowSorter(sorter);
		tablePatients.getTableHeader().setReorderingAllowed(false);

		// We configure the columns
		tablePatients.getColumnModel().getColumn(0).setMinWidth(170);
		tablePatients.getColumnModel().getColumn(0).setMaxWidth(170);
		tablePatients.getColumnModel().getColumn(0).setResizable(false);
		tablePatients.getColumnModel().getColumn(1).setMinWidth(140);
		tablePatients.getColumnModel().getColumn(1).setMaxWidth(140);
		tablePatients.getColumnModel().getColumn(1).setResizable(false);
		tablePatients.getColumnModel().getColumn(2).setMinWidth(95);
		tablePatients.getColumnModel().getColumn(2).setMaxWidth(95);
		tablePatients.getColumnModel().getColumn(2).setResizable(false);
		tablePatients.getColumnModel().getColumn(3).setMinWidth(130);
		tablePatients.getColumnModel().getColumn(3).setResizable(false);
		tablePatients.getColumnModel().getColumn(4).setMinWidth(120);
		tablePatients.getColumnModel().getColumn(4).setMaxWidth(120);
		tablePatients.getColumnModel().getColumn(4).setResizable(false);
		tablePatients.getColumnModel().getColumn(5).setMinWidth(0);
		tablePatients.getColumnModel().getColumn(5).setMaxWidth(0);
		tablePatients.getColumnModel().getColumn(5).setResizable(false);
		tablePatients.getColumnModel().getColumn(6).setMinWidth(0);
		tablePatients.getColumnModel().getColumn(6).setMaxWidth(0);
		tablePatients.getColumnModel().getColumn(6).setResizable(false);
		tablePatients.setPreferredScrollableViewportSize(new Dimension(655,400));

		// We sort the array this way by default :
		// first, alphabetically by name, then by date.
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();

		// Setting the table's sorter, renderer and popupmenu
		JMenuItem menuItemDisplayH = new JMenuItem("Display history");
		menuItemDisplayH.addActionListener(new displayHistoryAction());
		JPopupMenu popMenu = new JPopupMenu();
		popMenu.add(menuItemDisplayH);
		tablePatients.setComponentPopupMenu(popMenu);
		tablePatients.addMouseListener(new TableStudyMouseListener(tablePatients, modeleTablePatients, modeleTableSeries, state));
		
		tablePatients.setRowSorter(sorter);
		tablePatients.setDefaultRenderer(Date.class, new DateRenderer());

		// Creating tableauDetails which will contain the patients's details
		tableSeries = new JTable(modeleTableSeries);
		tableSeries.getTableHeader().setReorderingAllowed(false);

		// We configure the columns
		tableSeries.getColumnModel().getColumn(0).setMinWidth(200);
		tableSeries.getColumnModel().getColumn(0).setResizable(false);
		tableSeries.getColumnModel().getColumn(1).setMinWidth(100);
		tableSeries.getColumnModel().getColumn(1).setMaxWidth(100);
		tableSeries.getColumnModel().getColumn(1).setResizable(false);
		tableSeries.getColumnModel().getColumn(2).setMaxWidth(100);
		tableSeries.getColumnModel().getColumn(2).setResizable(false);
		tableSeries.getColumnModel().getColumn(3).setMinWidth(0);
		tableSeries.getColumnModel().getColumn(3).setMaxWidth(0);
		tableSeries.getColumnModel().getColumn(3).setResizable(false);
		tableSeries.setPreferredScrollableViewportSize(new Dimension(400,400));

		List<RowSorter.SortKey> sortKeysDetails = new ArrayList<>();
		sortKeysDetails.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sortKeysDetails.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorterDetails.setSortKeys(sortKeysDetails);
		sorterDetails.setSortsOnUpdates(true);
		sorterDetails.sort();
		tableSeries.setRowSorter(sorterDetails);

		// Creating the modalities checkboxes
		JPanel filtersPanel = new JPanel(new FlowLayout());
		checkboxes = new JPanel(new GridLayout(2,4));
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

		
		queryAET = new JComboBox<String>(distantAets);
		queryAET.setBorder(new EmptyBorder(0, 30, 0, 0));
		

		// Creating the text inputs
		JPanel textInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchingParam = new JComboBox<String>(new String[] {"Patient name", "Patient ID", "Accession number"});
		userInput = new JTextField();
		userInput.setToolTipText("Set your input accordingly to the field combobox on the left. ('*' stands for any character)");
		description = new JTextField();
		description.setToolTipText("Study's description. ('*' stands for any character)");
		userInput.setText("*");
		description.setText("*");
		userInput.setPreferredSize(new Dimension(90,20));
		description.setPreferredSize(new Dimension(90,20));
		
		searchingParam.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange()==ItemEvent.SELECTED) {
					if (searchingParam.getSelectedIndex()==0) {
						userInputFirstName.setEnabled(true);
					}
					else userInputFirstName.setEnabled(false);
				}
				
			}
			
		});
		
		// Creating the "search" button (ajouter)
		JButton ajouter = new JButton(new SearchAction(this));
		//Combobox to select Destination AET
		retrieveAET = new JComboBox<String>(listRetrieveAET);
		
		// Creating the datepickers
		JPanel dates = new JPanel(new FlowLayout(FlowLayout.LEFT));
		from = new DatePicker(new Date(), new SimpleDateFormat("MM-dd-yyyy"));
		from.setBorder(new EmptyBorder(0, 5, 0 ,0));
		from.setToolTipText("Date format : MM-dd-yyyy");
		to = new DatePicker(new Date(), new SimpleDateFormat("MM-dd-yyyy"));
		to.setBorder(new EmptyBorder(0, 5, 0 ,0));
		to.setToolTipText("Date format : MM-dd-yyyy");

		// Creating the dates JPanel
		dates.add(new JLabel("From", SwingConstants.RIGHT));
		dates.add(from);
		dates.add(new JLabel("To", SwingConstants.RIGHT));
		dates.add(to);
		dates.add(queryAET);
		dates.add(ajouter);

		// Building the components for the southern part of the window : the AETs combobox, the Search and Filter buttons
		this.state = new JLabel();
		southD.add(retrieveAET);
		retrieve = new JButton("Retrieve");
		retrieve.addActionListener(new Retrieve_Action(this, true));
		southD.add(retrieve);
		southD.add(state);
		south.add(southD);
		
		// Setting the rowSelection that will allow for retrieves on specific series
		ListSelectionModel rowSelectionModel = tableSeries.getSelectionModel();
		rowSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		///////////////////////////////////////////////////////////////////////////////////////////////////////		
		////////////////////////////////// END OF TAB 1 : QUERIES/RETRIEVE ////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////



		///////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////     TAB 2 : HISTORY ////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		sorterH = new TableRowSorter<ModelTableStudy>(modeleTablePatientHistory);
		sorterH.setSortsOnUpdates(true);
		sorterDetailsH = new TableRowSorter<ModelTableSeries>(modeleTableSeriesHistory);

		JPanel mainPanelH = new JPanel(new GridBagLayout());
		JPanel northH = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tablePatientsHistory = new JTable(modeleTablePatientHistory);

		tablePatientsHistory.setRowSorter(sorterH);
		tablePatientsHistory.getTableHeader().setReorderingAllowed(false);

		// We configure the columns
		tablePatientsHistory.getColumnModel().getColumn(0).setMinWidth(170);
		tablePatientsHistory.getColumnModel().getColumn(0).setMaxWidth(170);
		tablePatientsHistory.getColumnModel().getColumn(0).setResizable(false);
		tablePatientsHistory.getColumnModel().getColumn(1).setMinWidth(140);
		tablePatientsHistory.getColumnModel().getColumn(1).setMaxWidth(140);
		tablePatientsHistory.getColumnModel().getColumn(1).setResizable(false);
		tablePatientsHistory.getColumnModel().getColumn(2).setMinWidth(95);
		tablePatientsHistory.getColumnModel().getColumn(2).setMaxWidth(95);
		tablePatientsHistory.getColumnModel().getColumn(2).setResizable(false);
		tablePatientsHistory.getColumnModel().getColumn(3).setMinWidth(130);
		tablePatientsHistory.getColumnModel().getColumn(3).setResizable(false);
		tablePatientsHistory.getColumnModel().getColumn(4).setMinWidth(120);
		tablePatientsHistory.getColumnModel().getColumn(4).setMaxWidth(120);
		tablePatientsHistory.getColumnModel().getColumn(4).setResizable(false);
		tablePatientsHistory.getColumnModel().getColumn(5).setMinWidth(0);
		tablePatientsHistory.getColumnModel().getColumn(5).setMaxWidth(0);
		tablePatientsHistory.getColumnModel().getColumn(5).setResizable(false);
		tablePatientsHistory.getColumnModel().getColumn(6).setMinWidth(0);
		tablePatientsHistory.getColumnModel().getColumn(6).setMaxWidth(0);
		tablePatientsHistory.getColumnModel().getColumn(6).setResizable(false);
		// 655 400
		tablePatientsHistory.setPreferredScrollableViewportSize(new Dimension(655,400));

		// We sort the array this way by default :
		// first, alphabetically by name, then by modality and finally, by description.
		sorterH.setSortKeys(sortKeys);
		sorterH.sort();

		// Setting the table's sorter and renderer
		tablePatientsHistory.setRowSorter(sorterH);
		tablePatientsHistory.setDefaultRenderer(Date.class, new DateRenderer());


		// Creating tableauDetailsH which will contain the patients's details
		tableSeriesHistory = new JTable(modeleTableSeriesHistory);		
		tableSeriesHistory.getTableHeader().setReorderingAllowed(false);

		// We configure the columns
		tableSeriesHistory.getColumnModel().getColumn(0).setMinWidth(200);
		tableSeriesHistory.getColumnModel().getColumn(0).setResizable(false);
		tableSeriesHistory.getColumnModel().getColumn(1).setMinWidth(100);
		tableSeriesHistory.getColumnModel().getColumn(1).setMaxWidth(100);
		tableSeriesHistory.getColumnModel().getColumn(1).setResizable(false);
		tableSeriesHistory.getColumnModel().getColumn(2).setMinWidth(100);
		tableSeriesHistory.getColumnModel().getColumn(2).setMaxWidth(100);
		tableSeriesHistory.getColumnModel().getColumn(2).setResizable(false);
		tableSeriesHistory.getColumnModel().getColumn(3).setMinWidth(0);
		tableSeriesHistory.getColumnModel().getColumn(3).setMaxWidth(0);
		tableSeriesHistory.getColumnModel().getColumn(3).setResizable(false);
		tableSeriesHistory.setPreferredScrollableViewportSize(new Dimension(400,400));

		//Setting the sorter for tableauDetailsH
		sorterDetailsH.setSortsOnUpdates(true);
		sorterDetailsH.setSortKeys(sortKeysDetails);
		sorterDetailsH.sort();
		tableSeriesHistory.setRowSorter(sorterDetailsH);


		// Creating the modalities checkboxes
		JPanel filtersPanelH = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		checkboxesH = new JPanel(new GridLayout(2,4));
		crH = new JCheckBox("CR");
		ctH = new JCheckBox("CT");
		mrH = new JCheckBox("MR");
		nmH = new JCheckBox("NM");
		ptH = new JCheckBox("PT");
		usH = new JCheckBox("US");
		xaH = new JCheckBox("XA");
		mgH = new JCheckBox("MG");
		dxH = new JCheckBox("DX");
		customModalitiesH=new JTextField();
		customModalitiesH.setToolTipText("Add custom modalities ex \"OT\\PR\" ");
		checkboxesH.add(crH); checkboxesH.add(ctH);
		checkboxesH.add(mrH); checkboxesH.add(nmH);
		checkboxesH.add(ptH); checkboxesH.add(usH);
		checkboxesH.add(xaH); checkboxesH.add(mgH);
		checkboxesH.add(dxH); checkboxesH.add(customModalitiesH);

		// Creating the user input's components
		JPanel datesH = new JPanel(new FlowLayout());		 

		// For the tab history, fromH date is set to 01/01/1980 by default
		try {
			fromH = new DatePicker(new SimpleDateFormat("MM-dd-yy").parse("01-01-1980"), new SimpleDateFormat("MM-dd-yyyy"));
			fromH.setBorder(new EmptyBorder(0, 5, 0 ,0));
			fromH.setToolTipText("Date format : MM-dd-yyyy");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		toH = new DatePicker(new Date(), new SimpleDateFormat("MM-dd-yyyy"));
		toH.setBorder(new EmptyBorder(0, 5, 0 ,0));
		toH.setToolTipText("Date format : MM-dd-yyyy");

		queryAETH = new JComboBox<String>(distantAets);
		queryAETH.setMinimumSize(new Dimension(200,20));
		queryAETH.setMaximumSize(new Dimension(200,20));
		
		queryAETH.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				jprefer.putInt("HistoryAET", queryAETH.getSelectedIndex());
				if(tablePatients.getSelectedRowCount() > 0){
					String patientName = (String)tablePatients.getValueAt(tablePatients.getSelectedRow(), 0);
					String patientID = (String)tablePatients.getValueAt(tablePatients.getSelectedRow(), 1);
					// We clear the table completely before any queries
					modeleTablePatientHistory.clear();
					modeleTableSeriesHistory.clear();
					try {
						modeleTablePatientHistory.addPatient(patientName, patientID, "*", "*", "*", "*", queryAETH.getSelectedItem().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		retrieveAETH = new JComboBox<String>(listRetrieveAET);

		datesH.add(new JLabel("From"));
		datesH.add(fromH);
		datesH.add(new JLabel("To"));
		datesH.add(toH);

		// Creating the JPanel for datesH and queryAETH
		JPanel northGH = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northGH.add(queryAETH);
		northGH.add(datesH);
		JButton filter = new JButton("Filter");
		filter.addActionListener(new FilterAction(stateH, checkboxesH, modeleTablePatientHistory, modeleTableSeriesHistory, fromH, toH, queryAETH));

		//Creating the southern panel
		JPanel southH = new JPanel(new FlowLayout());
		stateH = new JLabel();
		stateH.setText(null);
		southH.add(this.retrieveAETH);
		retrieveH  = new JButton("Retrieve");
		retrieveH.addActionListener(new Retrieve_Action(this, false));
		southH.add(retrieveH);
		southH.add(this.stateH);
		tablePatientsHistory.addMouseListener(new TableStudyMouseListener(tablePatientsHistory, modeleTablePatientHistory, modeleTableSeriesHistory, stateH));		

		// Setting the rowSelection that will allow for retrieves
		ListSelectionModel rowSelectionModelH = tableSeriesHistory.getSelectionModel();
		rowSelectionModelH.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		///////////////////////////////////////////////////////////////////////////////////////////////////////		
		////////////////////////////////// END OF TAB 2 : HISTORY /////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////     TAB 3 : AutoRetrieve ////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		autoQuery=new AutoQuery(rest);
		JPanel Panel_Center = new JPanel(new BorderLayout());
		JPanel panelTableBatch = new JPanel(new GridLayout(1, 1));
		
		JPanel panel_Batch = new JPanel();
		panel_Batch.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelTableBatch.add(panel_Batch);
		panel_Batch.setLayout(new BorderLayout(0, 0));
		
		JPanel Batch_Panel = new JPanel();
		panel_Batch.add(Batch_Panel, BorderLayout.SOUTH);
		Batch_Panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panelButtonTableInteraction = new JPanel(new GridLayout(0,1));
		JPanel panelButtonTable = new JPanel();
		panelButtonTable.add(panelButtonTableInteraction);
		Panel_Center.add(panelButtonTable, BorderLayout.EAST);
		
		JButton btnImportCsv = new JButton("Import CSV");
		btnImportCsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int ouvrir=fileChooser.showOpenDialog(null);
				if (ouvrir==JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile().toString().endsWith(".csv")) {
					try {
						autoQuery.csvReading(fileChooser.getSelectedFile(), table);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		panelButtonTableInteraction.add(btnImportCsv);
		
		JButton btnAddPatient = new JButton("Add Patient");
		btnAddPatient.setToolTipText("Date format YYYY/MM/DD or YYYYMMDD, Modality Format CT\\\\PT\\\\NM");
		panelButtonTableInteraction.add(btnAddPatient);
		
		JButton btnRemove = new JButton("Remove");
		panelButtonTableInteraction.add(btnRemove);
		
		JButton btnResultsToCsv = new JButton("Show Results");
		btnResultsToCsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (table.getRowCount()!=0) {
					workerCsvRetrieve = new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground()  {	
							working=true;
							ArrayList<StudyDetails> patientArray =new ArrayList<StudyDetails>();
							
							for (int i=0; i<table.getRowCount(); i++) {
								info.setText("Query "+(i+1)+"/"+(table.getRowCount()));
								String name=(table.getValueAt(i, 0).toString()+"^"+table.getValueAt(i, 1).toString());
								if (name.equals("*^*")) name="*" ;
								StudyDetails[] studies=autoQuery.sendQuery(name.toString(),table.getValueAt(i, 2).toString(),table.getValueAt(i, 4).toString().replaceAll("/", ""),table.getValueAt(i, 5).toString().replaceAll("/", ""),table.getValueAt(i, 6).toString(),table.getValueAt(i, 7).toString(),table.getValueAt(i, 3).toString(), comboBox_RetrieveAet.getSelectedItem().toString() );
								if(studies.length==0) {
									System.out.println("Query "+ (i+1) +" Empty result or undefined parameters");
								}else {
									System.out.println("Query "+ (i+1) +" Found "+studies.length+" results");
									for(StudyDetails study:studies) {
										patientArray.add(study);
									}
								}
							}
							
							//On a les resulats en stock on appelle la fonction pour creer et recuperer les results de la dialogbox
							if (patientArray.size()!=0) showResultTable(patientArray);
							
							return null;
							
					}
					@Override
					protected void done(){
						info.setText("<html><font color='green'>Done.</font></html>");
						working=false;
					}
				};

				workerCsvRetrieve.execute();
			
				}
			}
				
		});
		
		panelButtonTableInteraction.add(btnResultsToCsv);
		
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow()!=-1) {
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					int[] rows = table.getSelectedRows();
					Arrays.sort(rows);
					for(int i=(rows.length-1);i>=0;i--){
						model.removeRow(rows[i]);
					}
				}
			}
		});
		
		btnAddPatient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.addRow(new Object[] {"*", "*", "*", "*","*", "*", "*", "*" });
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		Panel_Center.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(450, 150));
		table.putClientProperty("terminateEditOnFocusLost", true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(new DefaultTableModel(new Object[] {"Last Name", "First Name", "ID", "Accession Nb", "Study Date From","Study Date To", "Modality", "Study Description" },0));
		table.setToolTipText("Date Format YYYYMMDD, Modality example : CT\\\\MRI");
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		// listener
		table.getTableHeader().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int col = table.columnAtPoint(e.getPoint());
		        String colName = table.getColumnName(col);
		        String name = JOptionPane.showInputDialog("Set value for all "+colName, "*");
		        for(int i=0; i<table.getRowCount();i++) {
		        	table.setValueAt(name, i, col);
		        }
		    }
		});
		scrollPane.setViewportView(table);
		
		JPanel Batch_Title = new JPanel();
		panel_Batch.add(Batch_Title, BorderLayout.NORTH);
		Batch_Title.setBorder(new LineBorder(Color.GRAY));
		
		JLabel lblBatchImport = new JLabel("Batch Retrieve");
		Batch_Title.add(lblBatchImport);
		
		JPanel Panel_Top = new JPanel();
		
		comboBox_RetrieveAet = new JComboBox<String>(distantAets);
		
		comboBox_RetrieveAet.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange()==ItemEvent.SELECTED) {
					//On sauve dans le registery chaque changement de selection pour le prochain lancement
					jprefer.putInt("retrieveSelection", comboBox_RetrieveAet.getSelectedIndex());
				}
				
			}
		});
		
		JLabel lblRetrieveFrom = new JLabel("Retrieve From :");
		Panel_Top.add(lblRetrieveFrom);
		Panel_Top.add(comboBox_RetrieveAet);
		
		JPanel panel_Bottom = new JPanel();
		
		Aet_Retrieve = new JComboBox<String>(listRetrieveAET);

		panel_Bottom.add(Aet_Retrieve);
		
		
		btnStart = new JButton("Start Retrieve");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (btnStart.getText().equals("Start Retrieve")){
					makeWorker();
					workerAutoRetrieve.execute();
					btnStart.setText("Stop Retrieve");
				}
				else {
					workerAutoRetrieve.cancel(true);
					btnStart.setText("Start Retrieve");	
				}

			}
	   });
		
		panel_Bottom.add(btnStart);
		
		btnScheduleDaily = new JButton("Daily Schedule");
		btnScheduleDaily.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//On defini le task
				if(!timerOnDaily) {
					
					Daily_Retrieve_Gui dailyInterface= new Daily_Retrieve_Gui();
					dailyInterface.pack();
					dailyInterface.setLocationRelativeTo(gui);
					dailyInterface.setModal(true);
					dailyInterface.setVisible(true);
					if(dailyInterface.validate) {
						TimerTask task=new TimerTask() {
						    public void run() {
						    	AutoQuery_Retrieve_Results resultGui=new AutoQuery_Retrieve_Results(vueAnon);
								resultGui.pack();
								resultGui.setLocationRelativeTo(gui);
								resultGui.setVisible(true);
								resultGui.getConsole().append("Retrieved AET,"+comboBox_RetrieveAet.getSelectedItem().toString()+"\n");
						    	//Ne marchera que pour le jour J (sans prendre en compte le calendar)
								//On construit la string modalities
								String modalities=dailyInterface.GetModalitiesString();
								String queryOptions=dailyInterface.getSelectedComboBoxQueryOption();
								String reseachString=dailyInterface.getResearchTextParam();
								String studyDescParam=dailyInterface.getStudyDescriptionParam();
								StudyDetails[] results=null;
								
								if (StringUtils.equals(queryOptions, "Name")) {
									results=autoQuery.sendQuery(reseachString,"*",df.format(new Date()),df.format(new Date()),modalities,studyDescParam,"*", comboBox_RetrieveAet.getSelectedItem().toString());
								}
								else if (StringUtils.equals(queryOptions, "ID")) {
									results=autoQuery.sendQuery("*",reseachString,df.format(new Date()),df.format(new Date()),modalities,studyDescParam,"*", comboBox_RetrieveAet.getSelectedItem().toString());
								}
								else if (StringUtils.equals(queryOptions, "Accession")) {
									results=autoQuery.sendQuery("*","*",df.format(new Date()),df.format(new Date()),modalities,studyDescParam,reseachString, comboBox_RetrieveAet.getSelectedItem().toString());
								}
								
								resultGui.getConsole().append("found "+ results.length +" studies,");
								autoQuery.retrieveQuery(results, Aet_Retrieve.getSelectedItem().toString(),resultGui.getConsole());
								//SK CONTINUER ICI
								ArrayList<Study2> studiesRecieves=autoQuery.recievedStudiesAsStudiesObject();
								Study2[] studiesRecieved=new Study2[studiesRecieves.size()];
								studiesRecieves.toArray(studiesRecieved);
								resultGui.addStudy(studiesRecieved);
								
						    }};
						    
							timerDaily=new Timer();
							timerDaily.scheduleAtFixedRate(task, autoQuery.getStartTime(), autoQuery.fONCE_PER_DAY);
							btnScheduleDaily.setBackground(Color.ORANGE);
							timerOnDaily=true;
							info.setText("Daily scheduled "+ autoQuery.fTEN_PM+"H");
					}
					
					
				} else {
					timerDaily.cancel();
					timerDaily.purge();
					btnScheduleDaily.setBackground(Color.gray);
					timerOnDaily=false;
					info.setText("Daily cancelled");
				}
				
			}
		});
		
		btnSchedule_1 = new JButton("Schedule");
		btnSchedule_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Switcher On/off 
				if(!timerOn) {
					//On calcule la difference de temps entre maintenant et le scheduler
					Date now=new Date();
					long difference =  autoQuery.getStartTime().getTime() - now.getTime();
					info.setText("Retrieve Sheduled at " + autoQuery.getStartTime().toString());
					//On cree le Timer
					if (difference>0) {
						TimerTask task=new TimerTask() {
						    public void run() {
						    	btnStart.doClick();
						    	btnSchedule_1.setBackground(Color.RED);
						    }};
						timer=new Timer();
						timer.schedule(task, difference);
						btnSchedule_1.setBackground(Color.ORANGE);
						timerOn=true ;
					} else {
						info.setText("Schedule Time already passed, reset time");
					}
					
				} else {
					 timerOn=false;
					 btnSchedule_1.setBackground(Color.GRAY);
					 info.setText("Schedule Cancelled");
					 timer.cancel();
					 timer.purge();
				 }
			}
		});
		panel_Bottom.add(btnSchedule_1);
		panel_Bottom.add(btnScheduleDaily);
		
		JButton btnOptions = new JButton("Options");
		btnOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AutoQueryOptions options=new AutoQueryOptions();
				options.setLocationRelativeTo(null);
				options.setModal(true);
				options.setVisible(true);
				autoQuery.discard=options.getDiscard();
				autoQuery.fTEN_PM=options.getHour();
				autoQuery.fZERO_MINUTES=options.getMin();
				
				//serie filter
				autoQuery.chckbxSeriesFilter=options.getUseSeriesFilter();
				autoQuery.serieDescriptionContains=options.getSerieDescriptionContains();
				autoQuery.serieDescriptionExclude=options.getSerieDescriptionExclude();
				autoQuery.serieNumberMatch=options.getSerieNumberContains();
				autoQuery.serieNumberExclude=options.getSerieNumberExclude();
				
				autoQuery.chckbxCr=options.getSeriesModalities().get(0).isSelected();
				autoQuery.chckbxCt=options.getSeriesModalities().get(1).isSelected();
				autoQuery.chckbxCmr=options.getSeriesModalities().get(2).isSelected();
				autoQuery.chckbxNm=options.getSeriesModalities().get(3).isSelected();
				autoQuery.chckbxPt=options.getSeriesModalities().get(4).isSelected();
				autoQuery.chckbxUs=options.getSeriesModalities().get(5).isSelected();
				autoQuery.chckbxXa=options.getSeriesModalities().get(6).isSelected();
				autoQuery.chckbxMg=options.getSeriesModalities().get(7).isSelected();
				autoQuery.chckbxDx=options.getSeriesModalities().get(8).isSelected();
				autoQuery.customModalities=options.getCustomModalities();
				
				
			}
		});
		panel_Bottom.add(btnOptions);
		info=new JLabel();
		panel_Bottom.add(info);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////		
		////////////////////////////////// END OF TAB 3 : AutoRetrieve /////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		// Adding the components for the main tab p1
		textInput.add(searchingParam);
		textInput.add(userInput);
		textInput.add(new JLabel("First name : "));
		userInputFirstName= new JTextField("*");
		userInputFirstName.setPreferredSize(new Dimension(90,20));
		textInput.add(userInputFirstName);
		textInput.add(new JLabel("Description"));
		textInput.add(description);
		northG.add(textInput);
		northG.add(dates);
		north.add(northG);
		filtersPanel.add(checkboxes);
		north.add(filtersPanel);

		GridBagConstraints c = new GridBagConstraints();
		JScrollPane jscp = new JScrollPane(tablePatients);
		jscp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jscp.setPreferredSize(new Dimension((int)tablePatients.getPreferredSize().getWidth() + 20 , (int)tablePatients.getPreferredSize().getHeight()));
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(jscp,c);

		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(new JScrollPane(tableSeries),c);

		// Creating and adding the JPanels to the contentPane
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1,BoxLayout.PAGE_AXIS));
		p1.add(north);
		p1.add(mainPanel);
		p1.add(south);

		// Adding the components for the main tab p1
		northH.add(northGH);
		filtersPanelH.add(checkboxesH);
		northH.add(filtersPanelH);
		northH.add(filter);

		GridBagConstraints cH = new GridBagConstraints();
		JScrollPane jscpH = new JScrollPane(tablePatientsHistory);
		jscpH.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jscpH.setPreferredSize(new Dimension((int)tablePatientsHistory.getPreferredSize().getWidth() + 20 , (int)tablePatientsHistory.getPreferredSize().getHeight()));
		cH.gridx = 0;
		cH.gridy = 0;
		cH.weightx = 1;
		cH.weighty = 1;
		cH.fill = GridBagConstraints.BOTH;
		mainPanelH.add(jscpH,cH);

		cH.gridx = 1;
		cH.gridy = 0;
		cH.weightx = 1;
		cH.weighty = 1;
		cH.fill = GridBagConstraints.BOTH;
		mainPanelH.add(new JScrollPane(tableSeriesHistory),c);
		mainPanelH.setBackground(Color.DARK_GRAY);

		// Adding components to p2
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2,BoxLayout.PAGE_AXIS));
		p2.add(northH);
		p2.add(mainPanelH);
		p2.add(southH);
		
		// Adding components to p3
		JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		p3.add(Panel_Center, BorderLayout.CENTER);
		p3.add(Panel_Top, BorderLayout.NORTH);
		p3.add(panel_Bottom, BorderLayout.SOUTH);
				
		tabbedPane = new JTabbedPane();
		tabbedPane.add("Query/Retrieve", p1);
		tabbedPane.add("History", p2);
		tabbedPane.add("Auto Retrieve", p3);
		
		

		// Initially, the default button is ajouter, but we add a changelistener
		// on the tab so that the default button changes accordingly
		this.getRootPane().setDefaultButton(ajouter);
		tabbedPane.addChangeListener(new ChangeTabListener(this, ajouter, filter));
		
		setFocusListener(tablePatients,true);
		setFocusListener(tableSeries,true);
		setFocusListener(tablePatientsHistory,false);
		setFocusListener(tableSeriesHistory,false);
		

		applyPreferences();
		Image image = new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage();
		this.setIconImage(image);
		this.getContentPane().add(tabbedPane);
	}
	
	private void applyPreferences() {
		//SK BUG A VOIR ICI?
		if(distantAets.length > 0){
			int searchAetPosition = jprefer.getInt("SearchAET", 0);
			if(searchAetPosition <= (distantAets.length-1)){
				queryAET.setSelectedIndex(searchAetPosition);
			}else{
				queryAET.setSelectedIndex(0);	
			}
			
			int searchHistoryPosition = jprefer.getInt("HistoryAET", 0);
			if(searchHistoryPosition <= (distantAets.length-1)){
				queryAETH.setSelectedIndex(searchHistoryPosition);
			}else{
				queryAETH.setSelectedIndex(0);
			}
		}
		
		searchingParam.setSelectedIndex(jprefer.getInt("InputParameter", 0));
		
		if ((jprefer.getInt("retrieveSelection", 0) <= comboBox_RetrieveAet.getItemCount()) && (comboBox_RetrieveAet.getItemCount() !=0) ) comboBox_RetrieveAet.setSelectedIndex(jprefer.getInt("retrieveSelection", 0));
		

		
	}

	/*
	 * This class defines the action on the "Search" button, that is, adding patients to the model.
	 */
	private class SearchAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private SearchAction(JFrame frame) {
			super("Search");
		}

		public void actionPerformed(ActionEvent e) {
			state.setText(null);

			// Saving search parameters
			jprefer.putInt("InputParameter", searchingParam.getSelectedIndex());
			jprefer.putInt("SearchAET", queryAET.getSelectedIndex());
			

			// Making a DateFormat for the query
			DateFormat df = new SimpleDateFormat("yyyyMMdd");

			StringBuilder modalities = new StringBuilder();

			// We append a StringBuilder with every selected modalities.
			// We append "\\" which allows for multiple modalities in the query
			for(Component c : checkboxes.getComponents()){
				if(c instanceof JCheckBox){
					if(((JCheckBox) c).isSelected()){
						modalities.append((((JCheckBox) c).getText()));
						modalities.append("\\");
					}
				}else if(c instanceof JTextField){
					modalities.append(((JTextField) c).getText());
				}
			}
			
			// If the checkbox is the last chosen checkbox, we delete the '\\' at the end
			if(modalities.length() != 0 && modalities.charAt(modalities.length() - 1) == '\\'){
				modalities.deleteCharAt(modalities.length() - 1);
				if(modalities.charAt(modalities.length() - 1) == '\\'){
					modalities.deleteCharAt(modalities.length() - 1);
				}
			}
			// We clear the tables completely before any queries
			modeleTablePatients.clear();
			modeleTablePatientHistory.clear();
			modeleTableSeries.clear();
			modeleTableSeriesHistory.clear();

			// We make the query, based on the user's input
			try {
				if(modalities.toString().length() == 0){
					modalities.append("*");
				}

				// Query with the patient's name
				if (searchingParam.getSelectedItem().equals("Patient name")){
					String inputString=userInput.getText()+"^"+userInputFirstName.getText();
					if (inputString.equals("*^*")) inputString="*";
					
					String date;
					if(df.format(from.getDate().getTime()) .equals(df.format(to.getDate().getTime()))){
						date=df.format(from.getDate().getTime());
						
					}else {
						date=df.format(from.getDate().getTime())+"-"+df.format(to.getDate().getTime());
					}
					
					modeleTablePatients.addPatient(inputString.toUpperCase(), "*", date , 
					modalities.toString(), description.getText(),"*", queryAET.getSelectedItem().toString());
				}
				// Query with the patient's ID
				else if(searchingParam.getSelectedItem().equals("Patient ID")){
					 modeleTablePatients.addPatient("*", userInput.getText(), 
					 df.format(from.getDate().getTime())+"-"+df.format(to.getDate().getTime()), 
					 modalities.toString(), description.getText(),"*", queryAET.getSelectedItem().toString());
				}else{
					// Query with the patient's accession number
					modeleTablePatients.addPatient("*", "*", df.format(from.getDate().getTime())+"-"+df.format(to.getDate().getTime()), 
							modalities.toString(), description.getText(),userInput.getText(), queryAET.getSelectedItem().toString());
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
	private void createModelTable() {
		modeleTablePatients = new ModelTableStudy(rest); // model for the main JTable (tableau)
		modeleTableSeries = new ModelTableSeries(rest); // model for the details JTable (tableauDetails) in the main tab
		modeleTablePatientHistory = new ModelTableStudy(rest); // model for the history JTable (tab History)
		modeleTableSeriesHistory = new ModelTableSeries(rest); // model for the details JTable (tableauDetails) in the history tab
	}

	/*
	 * This class defines the action on pop menu, that is, displaying the patient's history.
	 */
	private class displayHistoryAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event) {
			state.setText(null);
			String patientName = (String)tablePatients.getValueAt(tablePatients.getSelectedRow(), 0);
			String patientID = (String)tablePatients.getValueAt(tablePatients.getSelectedRow(), 1);
			// We clear the table completely before any queries
			modeleTablePatientHistory.clear();
			modeleTableSeriesHistory.clear();
			try {
				modeleTablePatientHistory.addPatient(patientName, patientID, "*", "*", "*", "*", queryAETH.getSelectedItem().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			tabbedPane.setSelectedIndex(1);
		}
		
	}
	
	
	
	/**
	 * AutoQuery :Appelle la dialog d'affichage des resultat et recup�re les resultat valid�s pour l'injecter dans la table de la main frame
	 * @param patientArray
	 */
	private void showResultTable(ArrayList<StudyDetails> studyArray) {
		AutoQueryShowResultDialog resultDialog=new AutoQueryShowResultDialog();
		resultDialog.setModal(true);
		resultDialog.populateTable(studyArray);
		resultDialog.pack();
		resultDialog.setLocationRelativeTo(this);
		resultDialog.setVisible(true);
	
		if (resultDialog.isValidate() && resultDialog.getTable().getRowCount()!=0) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.setRowCount(0);
			JTable tableNouvelle =resultDialog.getTable();
			for (int i=0; i<tableNouvelle.getRowCount(); i++) {
				//Si champ vide on met "*"
				if (tableNouvelle.getValueAt(i, 0).equals("")) tableNouvelle.setValueAt("*", i, 0);
				if (tableNouvelle.getValueAt(i, 1).equals("")) tableNouvelle.setValueAt("*", i, 1);
				if (tableNouvelle.getValueAt(i, 2).equals("")) tableNouvelle.setValueAt("*", i, 2);
				if (tableNouvelle.getValueAt(i, 3).equals("")) tableNouvelle.setValueAt("*", i, 3);
				if (tableNouvelle.getValueAt(i, 4).equals("")) tableNouvelle.setValueAt("*", i, 4);
				if (tableNouvelle.getValueAt(i, 5).equals("")) tableNouvelle.setValueAt("*", i, 5);
				if (tableNouvelle.getValueAt(i, 6).equals("")) tableNouvelle.setValueAt("*", i, 6);
				if (tableNouvelle.getValueAt(i, 7).equals("")) tableNouvelle.setValueAt("*", i, 7);
				//On importe la ligne vers la main frame
				model.addRow(new Object[] {tableNouvelle.getValueAt(i, 0), tableNouvelle.getValueAt(i, 1), tableNouvelle.getValueAt(i, 2), tableNouvelle.getValueAt(i, 3), tableNouvelle.getValueAt(i, 4),tableNouvelle.getValueAt(i, 5), tableNouvelle.getValueAt(i, 6), tableNouvelle.getValueAt(i, 7)} );
			}
		}
	}
	

	private void makeWorker() {
		
		workerAutoRetrieve =new SwingWorker<Void,Void>(){
			AutoQuery_Retrieve_Results resultGui;
			ArrayList<Study2> studiesRecieves=new ArrayList<Study2>();
			
			@Override
			protected Void doInBackground()  {
				resultGui=new AutoQuery_Retrieve_Results(vueAnon);
				resultGui.pack();
				resultGui.setLocationRelativeTo(gui);
				resultGui.setVisible(true);
				resultGui.getConsole().append("Retrieved AET,"+comboBox_RetrieveAet.getSelectedItem().toString()+"\n");
				btnSchedule_1.setEnabled(false);
				
				if (table.getRowCount()!=0) {
					
					for (int i=0; i<table.getRowCount(); i++) {
						if (isCancelled()) return null;
						resultGui.getConsole().append("Query "+(i+1)+ "/" + table.getRowCount()+",");
						working=true;
						//Construction String Name
						String name=(table.getValueAt(i, 0).toString()+"^"+table.getValueAt(i, 1).toString());
						if (name.equals("*^*")) name="*" ;
						StudyDetails[] results=autoQuery.sendQuery(name.toString(),table.getValueAt(i, 2).toString(),table.getValueAt(i, 4).toString().replaceAll("/", ""),table.getValueAt(i, 5).toString().replaceAll("/", ""),table.getValueAt(i, 6).toString(),table.getValueAt(i, 7).toString(),table.getValueAt(i, 3).toString(), comboBox_RetrieveAet.getSelectedItem().toString());
						resultGui.getConsole().append("["+ name.toString() + "_"+ table.getValueAt(i, 2).toString()+ "_"+ table.getValueAt(i, 4).toString().replaceAll("/", "") + "_" +table.getValueAt(i, 5).toString().replaceAll("/", "")+"_"+table.getValueAt(i, 6).toString()+"_"+table.getValueAt(i, 7).toString()+"_"+table.getValueAt(i, 3).toString()+"],");
						//On retrieve toutes les studies 
						if (results!=null) {
							autoQuery.retrieveQuery(results, Aet_Retrieve.getSelectedItem().toString(), resultGui.getConsole());
							studiesRecieves.addAll(autoQuery.recievedStudiesAsStudiesObject());
							System.out.println(studiesRecieves.size());
						} else { 
							info.setText("Empty Results");
							resultGui.getConsole().append("empty Results,");
						}	
					}
				
				}
				return null;
			}
				
			@Override
			protected void done(){
				
				Study2[] studyArray=new Study2[studiesRecieves.size()];
				studiesRecieves.toArray(studyArray);
				resultGui.addStudy(studyArray);
				info.setText("<html><font color='green'>Done, see Console for details</font></html>");
				btnSchedule_1.setEnabled(true);
				working=false;
				btnStart.setText("Start Retrieve");
			}

		};

	}
	
	/**
	 * Add Focus listener To table, store the last focused table in main and history table
	 * Usefull for retrieve triggering
	 * @param table
	 * @param main
	 */
	private void setFocusListener(JTable table, boolean main) {
		
		table.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {	
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if(main) {
					lastFocusMain=table;
				}else {
					lastFocusHistory=table;
				}
			}	
		});
		
	}
	
	private void storeAets() {
		listRetrieveAET = ArrayUtils.addAll(new String[] {rest.getLocalAet()}, rest.getAets());
		distantAets=rest.getAets();
	}
	
	public JTable getLastFocusedTable(boolean main) {
		if(main) {
			return lastFocusMain;
		}else {
			return lastFocusHistory;
		}
	}

	public JLabel getStatusLabel(boolean main) {
		if (main) {
			return state;
		}else {
			return stateH;
		}
	}
	
	public QueryRetrieve getRestObject() {
		return this.rest;
	}
	
	public String getRetrieveAet(boolean main) {
		if (main) {
			return (String) retrieveAET.getSelectedItem();
		}else {
			return (String) retrieveAETH.getSelectedItem();
		}
	}
	
	public JButton getRetrieveButton(boolean main) {
		if (main) {
			return (JButton) this.retrieve;
		}else {
			return (JButton) this.retrieveH;
		}
	}
	
	public void setWorkingBoolean(boolean working) {
		this.working=working;
	}

}

