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

package org.petctviewer.query;

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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
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

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.michaelbaranov.microba.calendar.DatePicker;

import ij.plugin.PlugIn;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

public class VueRest extends JFrame implements PlugIn{
	
	private static final long serialVersionUID = 1L;
	// Instancie la classe rest qui fournit les services de connexion et input
	private Rest rest =new Rest();
	
	private JTabbedPane tabbedPane;
	private TableDataPatient modele = new TableDataPatient(rest); // model for the main JTable (tableau)
	private TableDataDetails modeleDetails = new TableDataDetails(rest); // model for the details JTable (tableauDetails) in the main tab
	private TableDataPatient modeleH = new TableDataPatient(rest); // model for the history JTable (tab History)
	private TableDataDetails modeleDetailsH = new TableDataDetails(rest); // model for the details JTable (tableauDetails) in the history tab
	private JTable tableau; // displayed table in the main tab
	private JTable tableauDetails; // displayed table containing the details in the main tab
	private JTable tableauH; // displayed table in the history tab
	private JTable tableauDetailsH; // displayed table containing the details in the history tab
	private JPopupMenu popMenu = new JPopupMenu(); // popMenu that will pop when the user right-clicks on a row
	//	private JPopupMenu popMenuH = new JPopupMenu(); // popMenu that will pop when the user right-clicks on a row
	private Preferences jPrefer;
	/*
	 * The following components will be used to filter the tables, or make new searches
	 */
	private JComboBox<String> searchingParam; // indexes the "main" searching parameter (name, id, accession number)
	private JComboBox<Object> queryAET; // indexes every AETs available that the user can query from
	private JComboBox<Object> retrieveAET; // indexes every AETs available that the user can retrieve instances to
	private JLabel state; // allows the user to know the state of the retrieve query 
	private JTextField userInput; // associated with searchingParam to get the input
	private JPanel checkboxes; // contains every checkboxes
	private JCheckBox cr,ct,cmr,nm,pt,us,xa,mg; // the chosen modalities 
	private JTextField description; // allows to search for a particular description
	private DatePicker from, to; // allow to make a research in a user defined time frame
	private TableRowSorter<TableDataPatient> sorter; // used to filter and sort the rows for the main JTable
	private TableRowSorter<TableDataDetails> sorterDetails; // used to filter and sort the rows for the details JTable
	private JButton retrieve;
	
	// Tab History
	private JComboBox<Object> queryAETH; // indexes every AETs available that the user can get patient from (usually PACS)
	private JComboBox<Object> retrieveAETH; // indexes every AETs available that the user can retrieve instances to
	private JLabel stateH; // allows the user to know the state of the retrieve query
	private JPanel checkboxesH;
	private JCheckBox crH,ctH,cmrH,nmH,ptH,usH,xaH,mgH;  
	private DatePicker fromH, toH; // allow to make a research in a user defined time frame
	private TableRowSorter<TableDataPatient> sorterH; // used to sort the rows for the main JTable
	private TableRowSorter<TableDataDetails> sorterDetailsH; // used to filter and sort the rows for the details JTable
	private JButton retrieveH;
	
	// Tab Setup
	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/biplugins");
	private Preferences jpreferPerso = Preferences.userRoot().node("<unnamed>/queryplugin");

	// these list contains the selected rows's model's indexes in order to retrieve the series
	private ArrayList<Integer> rowsModelsIndexes = new ArrayList<>();
	private ArrayList<Integer> rowsModelsIndexesH = new ArrayList<>();
	
	//Working status
	boolean working;
	
	//Processus Swing
	SwingWorker<Void,Void> workerCsvRetrieve, workerRetrieve, worker ;
	
	//AutoQuery
	private JTextField studyDescription;
	private JTable table;
	private DateFormat df = new SimpleDateFormat("yyyyMMdd");
	private JComboBox<String> comboBox;
	private JComboBox<String> Aet_Retrieve;
	private JCheckBox chckbxCr , chckbxCt , chckbxCmr ,chckbxNm , chckbxPt ,chckbxUs ,chckbxXa ,chckbxMg ,chckbxToday;
	private JTextField textFieldNameIDAcc;
	private JButton btnScheduleDaily;
	private JLabel info;
	private AutoQuery autoQuery=new AutoQuery(rest);
	
	//timer
	private boolean timerOn;
	private boolean timerOnDaily;
	private Timer timer;
	private Timer timerDaily;
	
	
    
	public VueRest() {
		
		super("Orthanc queries");
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

	    @Override
	    public void windowClosing(WindowEvent e) {
	    	if (working==true) {
		    	String ObjButtons[] = {"Yes","No"};
				int PromptResult = JOptionPane.showOptionDialog(null,"Operation pending Are you sure you want to exit?","Orthanc Query",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
				if (PromptResult==JOptionPane.YES_OPTION) {
					//SK A FAIRE Terminer les processus SWING Voir dans sources de script editor comment evite la sortie de Fiji
					dispose();
					
				}
	    	}
	    	else {
	    		dispose();
	    	}
	    }});
		


		///////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////     TAB 1 : QUERIES/RETRIEVE ///////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		this.sorter = new TableRowSorter<TableDataPatient>(modele);
		this.sorterDetails = new TableRowSorter<TableDataDetails>(modeleDetails);
		this.sorter.setSortsOnUpdates(true);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		JPanel north = new JPanel(new GridLayout(1,2));
		JPanel northG = new JPanel(new GridLayout(2,1));
		JPanel south = new JPanel();
		south.setLayout(new BoxLayout(south, BoxLayout.LINE_AXIS));
		JPanel southD = new JPanel(new FlowLayout());

		// Creating the main JTable containing the patients (the left one)
		tableau = new JTable(modele);
		tableau.setRowSorter(sorter);
		tableau.getTableHeader().setReorderingAllowed(false);

		// We configure the columns
		tableau.getColumnModel().getColumn(0).setMinWidth(170);
		tableau.getColumnModel().getColumn(0).setMaxWidth(170);
		tableau.getColumnModel().getColumn(0).setResizable(false);
		tableau.getColumnModel().getColumn(1).setMinWidth(140);
		tableau.getColumnModel().getColumn(1).setMaxWidth(140);
		tableau.getColumnModel().getColumn(1).setResizable(false);
		tableau.getColumnModel().getColumn(2).setMinWidth(95);
		tableau.getColumnModel().getColumn(2).setMaxWidth(95);
		tableau.getColumnModel().getColumn(2).setResizable(false);
		tableau.getColumnModel().getColumn(3).setMinWidth(130);
		tableau.getColumnModel().getColumn(3).setResizable(false);
		tableau.getColumnModel().getColumn(4).setMinWidth(120);
		tableau.getColumnModel().getColumn(4).setMaxWidth(120);
		tableau.getColumnModel().getColumn(4).setResizable(false);
		tableau.getColumnModel().getColumn(5).setMinWidth(0);
		tableau.getColumnModel().getColumn(5).setMaxWidth(0);
		tableau.getColumnModel().getColumn(5).setResizable(false);
		tableau.setPreferredScrollableViewportSize(new Dimension(655,400));

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
		popMenu.add(menuItemDisplayH);
		tableau.setComponentPopupMenu(popMenu);
		tableau.addMouseListener(new TableMouseListener(tableau, modele, modeleDetails, queryAET, tableau.getSelectionModel(), state));

		tableau.setRowSorter(sorter);
		tableau.setDefaultRenderer(Date.class, new DateRenderer());

		// Creating tableauDetails which will contain the patients's details
		tableauDetails = new JTable(modeleDetails);
		tableauDetails.getTableHeader().setReorderingAllowed(false);

		// We configure the columns
		tableauDetails.getColumnModel().getColumn(0).setMinWidth(200);
		tableauDetails.getColumnModel().getColumn(0).setResizable(false);
		tableauDetails.getColumnModel().getColumn(1).setMinWidth(100);
		tableauDetails.getColumnModel().getColumn(1).setMaxWidth(100);
		tableauDetails.getColumnModel().getColumn(1).setResizable(false);
		tableauDetails.setPreferredScrollableViewportSize(new Dimension(300,400));
		tableauDetails.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				try{
					if(state.getText().equals("Done") && state != null){
						state.setText(null);
					}
				}catch(Exception e1){
					// Ignore
				}
			}
		});

		List<RowSorter.SortKey> sortKeysDetails = new ArrayList<>();
		sortKeysDetails.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sortKeysDetails.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorterDetails.setSortKeys(sortKeysDetails);
		sorterDetails.setSortsOnUpdates(true);
		sorterDetails.sort();
		tableauDetails.setRowSorter(sorterDetails);

		// Creating the modalities checkboxes
		JPanel filtersPanel = new JPanel(new FlowLayout());
		checkboxes = new JPanel(new GridLayout(2,4));
		cr = new JCheckBox("CR");
		ct = new JCheckBox("CT");
		cmr = new JCheckBox("CMR");
		nm = new JCheckBox("NM");
		pt = new JCheckBox("PT");
		us = new JCheckBox("US");
		xa = new JCheckBox("XA");
		mg = new JCheckBox("MG");
		checkboxes.add(cr); checkboxes.add(ct);
		checkboxes.add(cmr); checkboxes.add(nm);
		checkboxes.add(pt); checkboxes.add(us);
		checkboxes.add(xa); checkboxes.add(mg);

		Object[] tabAETs = {""};

		// Creating the queryAET comboBox
		try{
			tabAETs = modele.getAETs();
			queryAET = new JComboBox<Object>(tabAETs);
		}catch(IOException e1){
			e1.printStackTrace();
		}catch(NullPointerException e){
			JOptionPane.showMessageDialog(null, "Please set an AET before using this app (You will have to close it).",
					"No AET found", JOptionPane.INFORMATION_MESSAGE);
		}finally{
			queryAET = new JComboBox<Object>(tabAETs);
			try {
				tabAETs = modele.getAETs();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (NullPointerException e){
				//Ignore
			}

		}

		queryAET.setBorder(new EmptyBorder(0, 30, 0, 0));
		if(tabAETs.length > 0){
			if(jpreferPerso.getInt("SearchAET", 99) < tabAETs.length){
				queryAET.setSelectedIndex(jpreferPerso.getInt("SearchAET", 99));
			}else{
				queryAET.setSelectedIndex(0);	
			}
		}
		queryAET.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				modele.clear();
				modeleDetails.clear();
			}
		});

		// Creating the text inputs
		JPanel textInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
		String[] patientParam = {"Patient name", "Patient ID", "Accession number"};
		searchingParam = new JComboBox<String>(patientParam);
		searchingParam.setSelectedIndex(jpreferPerso.getInt("InputParameter", 0));
		userInput = new JTextField();
		userInput.setToolTipText("Set your input accordingly to the field combobox on the left. ('*' stands for any character)");
		description = new JTextField();
		description.setToolTipText("Study's description. ('*' stands for any character)");
		userInput.setText("*");
		description.setText("*");
		userInput.setPreferredSize(new Dimension(90,20));
		description.setPreferredSize(new Dimension(90,20));

		retrieveAET = new JComboBox<Object>(new Object[]{""});

		// Creating the "search" button (ajouter)
		JButton ajouter = new JButton(new SearchAction(this));
		try {
			Object[] listRetrieveAET = ArrayUtils.addAll(modeleDetails.getDicomAETs(), tabAETs);
			retrieveAET = new JComboBox<Object>(listRetrieveAET);
		} catch (IOException e3) {
			e3.printStackTrace();
		} catch (NullPointerException e){
			//Ignore
		}

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
		retrieve = new JButton(new RetrieveAction(rowsModelsIndexes, tableauDetails, modeleDetails, state, retrieveAET));
		southD.add(retrieve);
		southD.add(state);
		south.add(southD);

		// Setting the mouse listener on tableau
		tableau.addMouseListener(new TableMouseListener(tableau, modele, modeleDetails, queryAET, tableau.getSelectionModel(), state));

		// Setting the rowSelection that will allow for retrieves on specific series
		ListSelectionModel rowSelectionModel = tableauDetails.getSelectionModel();
		rowSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		rowSelectionModel.addListSelectionListener(new TableListSelectionListener(rowsModelsIndexes, tableauDetails));

		///////////////////////////////////////////////////////////////////////////////////////////////////////		
		////////////////////////////////// END OF TAB 1 : QUERIES/RETRIEVE ////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////



		///////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////     TAB 2 : HISTORY ////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		sorterH = new TableRowSorter<TableDataPatient>(modeleH);
		sorterH.setSortsOnUpdates(true);
		sorterDetailsH = new TableRowSorter<TableDataDetails>(modeleDetailsH);

		JPanel mainPanelH = new JPanel(new GridBagLayout());
		JPanel northH = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tableauH = new JTable(modeleH);

		tableauH.setRowSorter(sorterH);
		tableauH.getTableHeader().setReorderingAllowed(false);

		// We configure the columns
		tableauH.getColumnModel().getColumn(0).setMinWidth(170);
		tableauH.getColumnModel().getColumn(0).setMaxWidth(170);
		tableauH.getColumnModel().getColumn(0).setResizable(false);
		tableauH.getColumnModel().getColumn(1).setMinWidth(140);
		tableauH.getColumnModel().getColumn(1).setMaxWidth(140);
		tableauH.getColumnModel().getColumn(1).setResizable(false);
		tableauH.getColumnModel().getColumn(2).setMinWidth(95);
		tableauH.getColumnModel().getColumn(2).setMaxWidth(95);
		tableauH.getColumnModel().getColumn(2).setResizable(false);
		tableauH.getColumnModel().getColumn(3).setMinWidth(130);
		tableauH.getColumnModel().getColumn(3).setResizable(false);
		tableauH.getColumnModel().getColumn(4).setMinWidth(120);
		tableauH.getColumnModel().getColumn(4).setMaxWidth(120);
		tableauH.getColumnModel().getColumn(4).setResizable(false);
		tableauH.getColumnModel().getColumn(5).setMinWidth(0);
		tableauH.getColumnModel().getColumn(5).setMaxWidth(0);
		tableauH.getColumnModel().getColumn(5).setResizable(false);
		// 655 400
		tableauH.setPreferredScrollableViewportSize(new Dimension(655,400));

		// We sort the array this way by default :
		// first, alphabetically by name, then by modality and finally, by description.
		sorterH.setSortKeys(sortKeys);
		sorterH.sort();

		// Setting the table's sorter and renderer
		tableauH.setRowSorter(sorterH);
		tableauH.setDefaultRenderer(Date.class, new DateRenderer());


		// Creating tableauDetailsH which will contain the patients's details
		tableauDetailsH = new JTable(modeleDetailsH);		
		tableauDetailsH.getTableHeader().setReorderingAllowed(false);

		// We configure the columns
		tableauDetailsH.getColumnModel().getColumn(0).setMinWidth(200);
		tableauDetailsH.getColumnModel().getColumn(0).setResizable(false);
		tableauDetailsH.getColumnModel().getColumn(1).setMinWidth(100);
		tableauDetailsH.getColumnModel().getColumn(1).setMaxWidth(100);
		tableauDetailsH.getColumnModel().getColumn(1).setResizable(false);
		tableauDetailsH.setPreferredScrollableViewportSize(new Dimension(300,400));

		//Setting the sorter for tableauDetailsH
		sorterDetailsH.setSortsOnUpdates(true);
		sorterDetailsH.setSortKeys(sortKeysDetails);
		sorterDetailsH.sort();
		tableauDetailsH.setRowSorter(sorterDetailsH);


		// Creating the modalities checkboxes
		JPanel filtersPanelH = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		checkboxesH = new JPanel(new GridLayout(2,4));
		crH = new JCheckBox("CR");
		ctH = new JCheckBox("CT");
		cmrH = new JCheckBox("CMR");
		nmH = new JCheckBox("NM");
		ptH = new JCheckBox("PT");
		usH = new JCheckBox("US");
		xaH = new JCheckBox("XA");
		mgH = new JCheckBox("MG");
		checkboxesH.add(crH); checkboxesH.add(ctH);
		checkboxesH.add(cmrH); checkboxesH.add(nmH);
		checkboxesH.add(ptH); checkboxesH.add(usH);
		checkboxesH.add(xaH); checkboxesH.add(mgH);

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

		Object[] tabAETsH = {""};
		try {
			tabAETsH = modeleH.getAETs();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e){
			//Ignore
		}

		queryAETH = new JComboBox<Object>(tabAETsH);
		queryAETH.setMinimumSize(new Dimension(200,20));
		queryAETH.setMaximumSize(new Dimension(200,20));
		if(tabAETsH.length > 0){
			if(jpreferPerso.getInt("HistoryAET", 0) < tabAETsH.length){
				queryAETH.setSelectedIndex(jpreferPerso.getInt("HistoryAET", 0));
			}else{
				queryAETH.setSelectedIndex(0);
			}
		}
		queryAETH.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if(tableau.getRowCount() != 0){
					String patientName = (String)tableau.getValueAt(tableau.getSelectedRow(), 0);
					String patientID = (String)tableau.getValueAt(tableau.getSelectedRow(), 1);
					// We clear the table completely before any queries
					modeleH.clear();
					modeleDetailsH.clear();
					try {
						modeleH.addPatient(patientName, patientID, "*", "*", "*", "*", queryAETH.getSelectedItem().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		Object[] listRetrieveAETH = {""};

		try {
				listRetrieveAETH = ArrayUtils.addAll(modeleDetailsH.getDicomAETs(), tabAETsH);
		} catch (IOException e3) {
			e3.printStackTrace();
		} catch (NullPointerException e){
			//Ignore
		}

		retrieveAETH = new JComboBox<Object>(listRetrieveAETH);

		datesH.add(new JLabel("From"));
		datesH.add(fromH);
		datesH.add(new JLabel("To"));
		datesH.add(toH);

		// Creating the JPanel for datesH and queryAETH
		JPanel northGH = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northGH.add(queryAETH);
		northGH.add(datesH);
		JButton filter = new JButton("Filter");
		filter.addActionListener(new FilterAction(stateH, checkboxesH, modeleH, modeleDetailsH, fromH, toH, queryAETH));

		//Creating the southern panel
		JPanel southH = new JPanel(new FlowLayout());
		stateH = new JLabel();
		stateH.setText(null);
		southH.add(this.retrieveAETH);
		retrieveH = new JButton(new RetrieveAction(rowsModelsIndexesH, tableauDetailsH, modeleDetailsH, stateH, retrieveAETH));
		southH.add(retrieveH);
		southH.add(this.stateH);
		tableauH.addMouseListener(new TableMouseListener(tableauH, modeleH, modeleDetailsH, queryAETH, tableauH.getSelectionModel(), stateH));		

		// Setting the rowSelection that will allow for retrieves
		ListSelectionModel rowSelectionModelH = tableauDetailsH.getSelectionModel();
		rowSelectionModelH.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		rowSelectionModelH.addListSelectionListener(new TableListSelectionListener(rowsModelsIndexesH, tableauDetailsH));


		///////////////////////////////////////////////////////////////////////////////////////////////////////		
		////////////////////////////////// END OF TAB 2 : HISTORY /////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////     TAB 3 : AutoRetrieve ////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		
		JPanel Panel_Center = new JPanel();
		Panel_Center.setLayout(new GridLayout(2, 0, 0, 0));
		
		JPanel panel_Batch = new JPanel();
		panel_Batch.setBorder(new LineBorder(new Color(0, 0, 0)));
		Panel_Center.add(panel_Batch);
		panel_Batch.setLayout(new BorderLayout(0, 0));
		
		JPanel Batch_Panel = new JPanel();
		panel_Batch.add(Batch_Panel, BorderLayout.SOUTH);
		Batch_Panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		Batch_Panel.add(panel, BorderLayout.SOUTH);
		
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
		panel.add(btnImportCsv);
		
		JButton btnAddPatient = new JButton("Add Patient");
		btnAddPatient.setToolTipText("Date format YYYY/MM/DD or YYYYMMDD, Modality Format CT\\\\PT\\\\NM");
		panel.add(btnAddPatient);
		
		JButton btnRemove = new JButton("Remove");
		panel.add(btnRemove);
		
		JButton btnResultsToCsv = new JButton("Show Results");
		btnResultsToCsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (table.getRowCount()!=0) {
					workerCsvRetrieve = new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground()  {	
							working=true;
							ArrayList<Patient> patientArray =new ArrayList<Patient>();
							for (int i=0; i<table.getRowCount(); i++) {
								info.setText("Query "+(i+1)+"/"+(table.getRowCount()));
								try {
										//Construction String Name
										StringBuilder name=new StringBuilder();
										if (StringUtils.equals(table.getValueAt(i, 1).toString(),"*")==true && StringUtils.equals(table.getValueAt(i, 0).toString(),"*")==false) name.append(table.getValueAt(i, 0).toString()); if(name.toString().endsWith("*")==false) name.append("*");
										if (StringUtils.equals(table.getValueAt(i, 1).toString(),"*")==true && StringUtils.equals(table.getValueAt(i, 0).toString(),"*")==true) name.append("*");
										if (StringUtils.equals(table.getValueAt(i, 1).toString(),"*")==false && StringUtils.equals(table.getValueAt(i, 0).toString(),"*")==false) name.append(table.getValueAt(i, 0).toString()+"^"+table.getValueAt(i, 1).toString());
								
										String[] results=autoQuery.sendQuery(name.toString(),table.getValueAt(i, 2).toString(),table.getValueAt(i, 4).toString().replaceAll("/", ""),table.getValueAt(i, 5).toString().replaceAll("/", ""),table.getValueAt(i, 6).toString(),table.getValueAt(i, 7).toString(),table.getValueAt(i, 3).toString(), comboBox.getSelectedItem().toString() );
										
										//On recupe les infos toutes les studies 
										if (results!=null) {
											autoQuery.getContent(results, patientArray);
										}
										
										//else { JOptionPane.showMessageDialog(null,"Empty Results, no CSV generated", "Empty Results", JOptionPane.WARNING_MESSAGE);}
										
								} catch (IOException | ParseException e) {e.printStackTrace();}
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
		panel.add(btnResultsToCsv);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow()!=-1) {
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					int[] rows = table.getSelectedRows();
					   for(int i=0;i<rows.length;i++){
						   model.removeRow(rows[i]-i);
					   }
				}
			}
		}
		);
		btnAddPatient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.addRow(new Object[] {"*", "*", "*", "*","*", "*", "*", "*" });
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		panel_Batch.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(450, 150));
		table.putClientProperty("terminateEditOnFocusLost", true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(new DefaultTableModel(new Object[] {"Last Name", "First Name", "ID", "Accession Nb", "Study Date From","Study Date To", "Modality", "Study Description" },0));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		scrollPane.setViewportView(table);
		
		JPanel Batch_Title = new JPanel();
		panel_Batch.add(Batch_Title, BorderLayout.NORTH);
		Batch_Title.setBorder(new LineBorder(Color.GRAY));
		
		JLabel lblBatchImport = new JLabel("Batch Retrieve");
		Batch_Title.add(lblBatchImport);
		
		JPanel panel_AutoRetrieve = new JPanel();
		panel_AutoRetrieve.setBorder(new LineBorder(new Color(0, 0, 0)));
		Panel_Center.add(panel_AutoRetrieve);
		panel_AutoRetrieve.setLayout(new BorderLayout(0, 0));
		
		JPanel AutoRetrieve_Title = new JPanel();
		AutoRetrieve_Title.setBorder(new LineBorder(Color.GRAY));
		panel_AutoRetrieve.add(AutoRetrieve_Title, BorderLayout.NORTH);
		
		JLabel lblAutoquery = new JLabel("Auto-Retrieve");
		AutoRetrieve_Title.add(lblAutoquery);
		
		JPanel AutoRetrieve_Panel = new JPanel();
		panel_AutoRetrieve.add(AutoRetrieve_Panel, BorderLayout.CENTER);
		AutoRetrieve_Panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JComboBox<String> comboBox_NameIDAcc = new JComboBox<String>();
		comboBox_NameIDAcc.setModel(new DefaultComboBoxModel<String>(new String[] {"Name", "ID", "Accession"}));
		AutoRetrieve_Panel.add(comboBox_NameIDAcc);
		
		textFieldNameIDAcc = new JTextField();
		textFieldNameIDAcc.setText("*");
		AutoRetrieve_Panel.add(textFieldNameIDAcc);
		textFieldNameIDAcc.setColumns(10);
		
		JLabel lblDate = new JLabel("Date :");
		AutoRetrieve_Panel.add(lblDate);
		
		JPanel Panel_Date = new JPanel();
		AutoRetrieve_Panel.add(Panel_Date);
		
		
		DatePicker from = new DatePicker(new Date(), new SimpleDateFormat("MM-dd-yyyy"));
		from.setBorder(new EmptyBorder(0, 5, 0 ,0));
		from.setToolTipText("Date format : MM-dd-yyyy");
		DatePicker to = new DatePicker(new Date(), new SimpleDateFormat("MM-dd-yyyy"));
		to.setBorder(new EmptyBorder(0, 5, 0 ,0));
		to.setToolTipText("Date format : MM-dd-yyyy");
		Panel_Date.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblFrom = new JLabel("From ");
		Panel_Date.add(lblFrom);
		
		Panel_Date.add(from);
		
		JLabel lblTo = new JLabel("To");
		Panel_Date.add(lblTo);
		Panel_Date.add(to);
		
		chckbxToday = new JCheckBox("Today");
		chckbxToday.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxToday.isSelected()==false) {btnScheduleDaily.setEnabled(false); from.setEnabled(true); to.setEnabled(true);};
				if (chckbxToday.isSelected()==true) {btnScheduleDaily.setEnabled(true); from.setEnabled(false); to.setEnabled(false);};
			}
		});
		
		Panel_Date.add(chckbxToday);
		
		JLabel lblStudyDescription = new JLabel("Study Description :");
		AutoRetrieve_Panel.add(lblStudyDescription);
		
		studyDescription = new JTextField();
		studyDescription.setText("*");
		AutoRetrieve_Panel.add(studyDescription);
		studyDescription.setColumns(10);
		
		JLabel lblModality = new JLabel("Modality :");
		AutoRetrieve_Panel.add(lblModality);
		
		JPanel panel_2 = new JPanel();
		AutoRetrieve_Panel.add(panel_2);
		panel_2.setLayout(new GridLayout(2, 4, 0, 0));
		
		chckbxCr = new JCheckBox("CR");
		panel_2.add(chckbxCr);
		
		chckbxCt = new JCheckBox("CT");
		panel_2.add(chckbxCt);
		
		chckbxCmr = new JCheckBox("CMR");
		panel_2.add(chckbxCmr);
		
		chckbxNm = new JCheckBox("NM");
		panel_2.add(chckbxNm);
		
		chckbxPt = new JCheckBox("PT");
		panel_2.add(chckbxPt);
		
		chckbxUs = new JCheckBox("US");
		panel_2.add(chckbxUs);
		
		chckbxXa = new JCheckBox("XA");
		panel_2.add(chckbxXa);
		
		chckbxMg = new JCheckBox("MG");
		panel_2.add(chckbxMg);
		
		JPanel Panel_Top = new JPanel();
		
		comboBox = new JComboBox<String>();
		for (int i=0; i<autoQuery.aet.length;i++) {
			comboBox.addItem(autoQuery.aet[i].toString());
		}
		
		JLabel lblRetrieveFrom = new JLabel("Retrieve From :");
		Panel_Top.add(lblRetrieveFrom);
		
		Panel_Top.add(comboBox);
		
		JPanel panel_Bottom = new JPanel();
		
		Aet_Retrieve = new JComboBox<String>();
		for (int i=0; i<autoQuery.aetRetrieve.length;i++) {
			Aet_Retrieve.addItem(autoQuery.aetRetrieve[i].toString());
		}
		panel_Bottom.add(Aet_Retrieve);
		
		JButton btnStart = new JButton("Start Retrieve");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//On met dans un swing worker
				SwingWorker<Void,Void> workerRetrieve = new SwingWorker<Void,Void>(){

					@Override
					protected Void doInBackground()  {
					
					
						if (table.getRowCount()!=0) {
							for (int i=0; i<table.getRowCount(); i++) {
								working=true;
								info.setText("Retrive "+(i+1)+" From "+table.getRowCount());
										try {
											//Construction String Name
											StringBuilder name=new StringBuilder();
											if (StringUtils.equals(table.getValueAt(i, 1).toString(),"*")==true && StringUtils.equals(table.getValueAt(i, 0).toString(),"*")==false) name.append(table.getValueAt(i, 0).toString()); if(name.toString().endsWith("*")==false) name.append("*") ;
											if (StringUtils.equals(table.getValueAt(i, 1).toString(),"*")==true && StringUtils.equals(table.getValueAt(i, 0).toString(),"*")==true) name.append("*");
											if (StringUtils.equals(table.getValueAt(i, 1).toString(),"*")==false && StringUtils.equals(table.getValueAt(i, 0).toString(),"*")==false) name.append(table.getValueAt(i, 0).toString()+"^"+table.getValueAt(i, 1).toString());
										
											String[] results=autoQuery.sendQuery(name.toString(),table.getValueAt(i, 2).toString(),table.getValueAt(i, 4).toString().replaceAll("/", ""),table.getValueAt(i, 5).toString().replaceAll("/", ""),table.getValueAt(i, 6).toString(),table.getValueAt(i, 7).toString(),table.getValueAt(i, 3).toString(), comboBox.getSelectedItem().toString());
											//On retrieve toutes les studies 
											if (results!=null) {
												autoQuery.retrieveQuery(results, Aet_Retrieve.getSelectedItem().toString(), autoQuery.discard);
											}
											
											else { JOptionPane.showMessageDialog(null,"Empty Results", "Empty Results", JOptionPane.WARNING_MESSAGE);}
											
											
											
											} catch (IOException e) {e.printStackTrace();}
									}
						}
					
					else {
							//On construit la string modalities
							StringBuilder modalities=sbModalitiesAutoQuery();
							
							try{
								//Requette selon la comboBox pour name, id, accession
								if (StringUtils.equals(comboBox_NameIDAcc.getSelectedItem().toString(), "Name")) autoQuery.sendQuery(textFieldNameIDAcc.getText(),"*",df.format(from.getDate()),df.format(to.getDate()),modalities.toString(),studyDescription.getText(),"*", comboBox.getSelectedItem().toString());
								if (StringUtils.equals(comboBox_NameIDAcc.getSelectedItem().toString(), "ID")) autoQuery.sendQuery("*",textFieldNameIDAcc.getText(),df.format(from.getDate()),df.format(to.getDate()),modalities.toString(),studyDescription.getText(),"*", comboBox.getSelectedItem().toString());
								if (StringUtils.equals(comboBox_NameIDAcc.getSelectedItem().toString(), "Accession")) autoQuery.sendQuery("*","*",df.format(from.getDate()),df.format(to.getDate()),modalities.toString(),studyDescription.getText(),textFieldNameIDAcc.getText(), comboBox.getSelectedItem().toString());
							} catch (IOException e) {e.printStackTrace();}
						}
						return null;
				}
					
				@Override
				protected void done(){
					info.setText("<html><font color='green'>Done.</font></html>");
					working=false;
				}
			};
			workerRetrieve.execute();
			
			
			}
	
			});
		panel_Bottom.add(btnStart);
		
		btnScheduleDaily = new JButton("Daily Schedule");
		btnScheduleDaily.setEnabled(false);
		btnScheduleDaily.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//On defini le task
				if(!timerOnDaily) {
					TimerTask task=new TimerTask() {
				    public void run() {
				    	try {
				    		//Ne marchera que pour le jour J (sans prendre en compte le calendar)
				    		//On construit la string modalities
				    		StringBuilder modalities=sbModalitiesAutoQuery();
							
							String[] results=autoQuery.sendQuery("*","*",df.format(new Date()),df.format(new Date()),modalities.toString(),studyDescription.getText(),"*", comboBox.getSelectedItem().toString());
							//On retrieve toutes les studies 
							if (results!=null) {
								autoQuery.retrieveQuery(results, Aet_Retrieve.getSelectedItem().toString(), autoQuery.discard);
							}
							else { System.out.println("Empty results");}
						} catch (IOException e) {
							e.printStackTrace();
						}
				    	btnScheduleDaily.setBackground(Color.RED);
				    }};
				timerDaily=new Timer();
				timerDaily.scheduleAtFixedRate(task, autoQuery.tenPM(), autoQuery.fONCE_PER_DAY);
				btnScheduleDaily.setBackground(Color.ORANGE);
				timerOnDaily=true;
				info.setText("Daily scheduled "+ autoQuery.fTEN_PM+"H");
				}
				else {
				timerDaily.cancel();
				timerDaily.purge();
				btnScheduleDaily.setBackground(Color.gray);
				timerOnDaily=false;
				info.setText("Daily cancelled");
				}

				
			}
		});
		
		JButton btnSchedule_1 = new JButton("Schedule");
		btnSchedule_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Switcher On/off 
				if(!timerOn) {
					//On calcule la difference de temps entre maintenant et le scheduler
					Date now=new Date();
					long difference =  autoQuery.tenPM().getTime() - now.getTime();
					info.setText("Retrieve Sheduled at " + autoQuery.tenPM().toString());
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
					}
					else {
						info.setText("Schedule Time already passed, reset time");
					}
					
				}
				 else {
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
				Option_Auto_Query options=new Option_Auto_Query(autoQuery.discard, autoQuery.fTEN_PM, autoQuery.fZERO_MINUTES);
				options.setLocationRelativeTo(null);
				options.setModal(true);
				options.setVisible(true);
				autoQuery.discard=options.getDiscard();
				autoQuery.fTEN_PM=options.getHour();
				autoQuery.fZERO_MINUTES=options.getMin();
				jPrefer = Preferences.userNodeForPackage(AutoQuery.class);
				jPrefer = jPrefer.node("AutoQuery");
				jPrefer.putInt("discard", options.getDiscard());
				jPrefer.putInt("hour", options.getHour());
				jPrefer.putInt("minutes", options.getMin());
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
		textInput.add(new JLabel("Description"));
		textInput.add(description);
		northG.add(textInput);
		northG.add(dates);
		north.add(northG);
		filtersPanel.add(checkboxes);
		north.add(filtersPanel);

		GridBagConstraints c = new GridBagConstraints();
		JScrollPane jscp = new JScrollPane(tableau);
		jscp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jscp.setPreferredSize(new Dimension((int)tableau.getPreferredSize().getWidth() + 20 , (int)tableau.getPreferredSize().getHeight()));
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(jscp,c);

		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(new JScrollPane(tableauDetails),c);

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
		JScrollPane jscpH = new JScrollPane(tableauH);
		jscpH.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jscpH.setPreferredSize(new Dimension((int)tableauH.getPreferredSize().getWidth() + 20 , (int)tableauH.getPreferredSize().getHeight()));
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
		mainPanelH.add(new JScrollPane(tableauDetailsH),c);
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
		tabbedPane.add("Queries/Retrieve", p1);
		tabbedPane.add("History", p2);
		tabbedPane.add("Auto Query", p3);
		
		

		// Initially, the default button is ajouter, but we add a changelistener
		// on the tab so that the default button changes accordingly
		this.getRootPane().setDefaultButton(ajouter);
		tabbedPane.addChangeListener(new ChangeButtonListener(this, ajouter, filter));

		Image image = new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage();
		this.setIconImage(image);
		this.getContentPane().add(tabbedPane);
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
			jpreferPerso.putInt("InputParameter", searchingParam.getSelectedIndex());
			jpreferPerso.putInt("SearchAET", queryAET.getSelectedIndex());
			jpreferPerso.putInt("HistoryAET", queryAETH.getSelectedIndex());

			// Making a DateFormat for the query
			DateFormat df = new SimpleDateFormat("yyyyMMdd");

			StringBuilder modalities = new StringBuilder();

			// We append a StringBuilder with every selected modalities.
			// We append "\\\\" in order to get the double \ which allows for multiple modalities in the query
			for(Component c : checkboxes.getComponents()){
				if(c instanceof JCheckBox){
					if(((JCheckBox) c).isSelected()){
						modalities.append((((JCheckBox) c).getText()));
						modalities.append("\\\\");
					}
				}
			}

			boolean successful = false;
			// If the checkbox is the last chosen checkbox, we delete the '\\\\' at the end
			if(modalities.length() != 0 && modalities.charAt(modalities.length() - 1) == '\\'){
				modalities.deleteCharAt(modalities.length() - 1);
				if(modalities.charAt(modalities.length() - 1) == '\\'){
					modalities.deleteCharAt(modalities.length() - 1);
				}
			}
			// We clear the tables completely before any queries
			modele.clear();
			modeleH.clear();
			modeleDetails.clear();
			modeleDetailsH.clear();

			// We make the query, based on the user's input
			try {
				if(modalities.toString().length() == 0){
					modalities.append("*");
				}

				// Query with the patient's name
				if (searchingParam.getSelectedItem().equals("Patient name")){
					successful = modele.addPatient(userInput.getText().toUpperCase(), "*", 
							df.format(from.getDate().getTime())+"-"+df.format(to.getDate().getTime()), 
							modalities.toString(), description.getText(),"*", queryAET.getSelectedItem().toString());
				}
				// Query with the patient's ID
				else if(searchingParam.getSelectedItem().equals("Patient ID")){
					successful = modele.addPatient("*", userInput.getText(), 
							df.format(from.getDate().getTime())+"-"+df.format(to.getDate().getTime()), 
							modalities.toString(), description.getText(),"*", queryAET.getSelectedItem().toString());
				}else{
					// Query with the patient's accession number
					successful = modele.addPatient("*", "*", df.format(from.getDate().getTime())+"-"+df.format(to.getDate().getTime()), 
							modalities.toString(), description.getText(),userInput.getText(), queryAET.getSelectedItem().toString());
				}
			} catch (Exception e1) {
				// ignore
			}finally{
				if(!successful){
					String curDb = jprefer.get("current database", "99");
					if(jprefer.get("db type" + curDb , "99") == "5"){
						String newAdress = JOptionPane.showInputDialog("Re-write a valid path "
								+ "(check your settings in BI database plugin; orthanc query will"
								+ " have to be reloaded)", jprefer.get("db path" + curDb, "http://localhost:8042"));
						jprefer.put("db path" + curDb, newAdress);
					}else{
						// SK ICI LANCER SETUP ?
						//e1.printStackTrace();
					}
					//frame.dispose();
				}
			}
		}
	}



	/*
	 * This class defines the action on pop menu, that is, displaying the patient's history.
	 */
	private class displayHistoryAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event) {
			state.setText(null);
			String patientName = (String)tableau.getValueAt(tableau.getSelectedRow(), 0);
			String patientID = (String)tableau.getValueAt(tableau.getSelectedRow(), 1);
			// We clear the table completely before any queries
			modeleH.clear();
			modeleDetailsH.clear();
			try {
				modeleH.addPatient(patientName, patientID, "*", "*", "*", "*", queryAETH.getSelectedItem().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			tabbedPane.setSelectedIndex(1);
		}
		
	}
	
	 
	private class RetrieveAction extends AbstractAction{

		private static final long serialVersionUID = 1L;
		private ArrayList<Integer> rowsModelsIndexes;
		private JTable tableauDetails;
		private TableDataDetails modeleDetails;
		private JLabel state;
		private JComboBox<Object> retrieveAET;

		public RetrieveAction(ArrayList<Integer> rowsModelsIndexes, JTable tableauDetails, 
				TableDataDetails modeleDetails, JLabel state, JComboBox<Object> retrieveAET){
			super("Retrieve");
			this.rowsModelsIndexes = rowsModelsIndexes;
			this.tableauDetails = tableauDetails;
			this.modeleDetails = modeleDetails;
			this.state = state;
			this.retrieveAET = retrieveAET;
		}


		@Override
		public void actionPerformed(ActionEvent arg0) {
			SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					working=true;
					retrieve.setEnabled(false);
					retrieveH.setEnabled(false);
					try {
						if(rowsModelsIndexes.size() == 0){
							// If whole studies/study were/was selected
							DateFormat df = new SimpleDateFormat("yyyyMMdd");
							for(Integer row : tableau.getSelectedRows()){
								modeleDetails.clear();
								Date date = (Date)tableau.getValueAt(row, 2);
								String patientName = (String)tableau.getValueAt(row, 0);
								String patientID = (String)tableau.getValueAt(row, 1);
								String studyDate = df.format(date); 
								String studyDescription = (String)tableau.getValueAt(row, 3);
								String accessionNumber = (String)tableau.getValueAt(row, 4);
								String studyInstanceUID = (String)tableau.getValueAt(row, 5);

								modeleDetails.addDetails(patientName, patientID, studyDate, studyDescription, accessionNumber, studyInstanceUID, queryAET.getSelectedItem().toString());
								for(int i = 0; i < tableauDetails.getRowCount(); i++){
									state.setText("<html>Patient " + (row+1) + "/" + tableau.getSelectedRows().length + " - Retrieve state  " + (i+1) + "/" + tableauDetails.getRowCount() + 
											" <font color='red'> (Do not touch any buttons or any tables while the retrieve is not done)</font></html>");
									modeleDetails.retrieve(modeleDetails.getQueryID(i), String.valueOf(i), 
											retrieveAET.getSelectedItem().toString());
								}
							}
							tableau.setRowSelectionInterval(0,0);
						}else{
							// If only series were selected
							int i = 0;
							for(int j : rowsModelsIndexes){
								state.setText("<html>Retrieve state  " + (i+1) + "/" + rowsModelsIndexes.size()  + 
										" <font color='red'>(Do not touch any buttons or any tables while the retrieve is not done)</font></html>");
								modeleDetails.retrieve(modeleDetails.getQueryID(j), String.valueOf(j), 
										retrieveAET.getSelectedItem().toString());
							}
							i++;
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}catch (Exception e){
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void done(){
					retrieve.setEnabled(true);
					retrieveH.setEnabled(true);
					state.setText("<html><font color='green'>The data have successfully been retrieved.</font></html>");
					working=false;
				}
				
			};
			worker.execute();
		}
		
	}
	
	protected StringBuilder sbModalitiesAutoQuery() {
		StringBuilder modalities=new StringBuilder();
		if (chckbxCr.isSelected()) modalities.append("\\\\CR") ;
		if (chckbxCt.isSelected()) modalities.append("\\\\CT") ;
		if (chckbxCmr.isSelected()) modalities.append("\\\\CMR") ;
		if (chckbxNm.isSelected()) modalities.append("\\\\NM") ;
		if (chckbxPt.isSelected()) modalities.append("\\\\PT") ;
		if (chckbxUs.isSelected()) modalities.append("\\\\US") ;
		if (chckbxXa.isSelected()) modalities.append("\\\\XA") ;
		if (chckbxMg.isSelected()) modalities.append("\\\\MG") ;
		if (modalities.length()==0) modalities.append("*"); else modalities.delete(0, 2);
		return modalities;
		
	}
	/**
	 * AutoQuery :Appelle la dialog d'affichage des resultat et recup�re les resultat valid�s pour l'injecter dans la table de la main frame
	 * @param patientArray
	 */
	private void showResultTable(ArrayList<Patient> patientArray) {
		AutoQueryResultTableDialog resultDialog=new AutoQueryResultTableDialog(patientArray);
		resultDialog.setModal(true);
		resultDialog.populateTable();
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
	/*
	 * Displaying the frame
	 */
	public static void main(String... args) throws IOException{
		VueRest vue = new VueRest();
		vue.setSize(1200, 400);
		vue.pack();
		vue.setLocationRelativeTo(null);
		vue.setVisible(true);
		
	}

	@Override
	public void run(String string) {
		VueRest vue = new VueRest();
		vue.setSize(1200, 400);
		vue.pack();
		vue.setLocationRelativeTo(null);
		vue.setVisible(true);
	
		
	}
}

