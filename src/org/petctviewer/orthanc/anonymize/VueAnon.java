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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.michaelbaranov.microba.calendar.DatePicker;

import ij.IJ;
import ij.WindowManager;
import ij.plugin.PlugIn;

import org.petctviewer.orthanc.*;
import org.petctviewer.orthanc.CTP.CTP;
import org.petctviewer.orthanc.CTP.CTP_Gui;
import org.petctviewer.orthanc.importdicom.ImportDCM;
import org.petctviewer.orthanc.monitoring.Monitoring_GUI;
import org.petctviewer.orthanc.query.*;
import org.petctviewer.orthanc.setup.ConnectionSetup;


public class VueAnon extends JFrame implements PlugIn{
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private JLabel state = new JLabel();
	private DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	private DateFormat dfZip = new SimpleDateFormat("MM_dd_yyyy_HHmmss");
	private JFrame gui=this;
	
	//Objet de connexion aux restFul API, prend les settings des registery et etabli les connexion a la demande
	private ParametreConnexionHttp connexionHttp;
	
	// Tables (p1)
	private String date;
	private JTable tableauPatients;
	private JTable tableauStudies;
	private JTable tableauSeries;
	private TableDataPatientsAnon modelePatients;
	private TableDataStudies modeleStudies;
	private TableDataSeries modeleSeries;
	private TableDataAnonPatients modeleAnonPatients = new TableDataAnonPatients();
	private TableDataAnonStudies modeleAnonStudies;
	private TableRowSorter<TableDataPatientsAnon> sorterPatients;
	private TableRowSorter<TableDataStudies> sorterStudies;
	private TableRowSorter<TableDataSeries> sorterSeries;

	// Orthanc toolbox (p1)
	private JTable anonPatientTable;
	private JTable anonStudiesTable;
	private JButton displayAnonTool;
	private JButton displayExportTool;
	private JButton displayManageTool;
	private JButton addToAnon;
	private JButton anonBtn;
	private JButton removeFromAnonList;
	private JButton setNamesIdBtn;
	private JButton exportZip = new JButton("Export list");
	private JButton removeFromZip = new JButton("Remove from list");
	private JButton addToZip = new JButton("Add to list");
	private JLabel zipSize= new JLabel("");
	private JLabel manageSize= new JLabel("");
	private JTextField userInputFirstName = new JTextField();
	
	//Manage Buttons
	JButton addManage = new JButton("Add to List");
	JButton removeFromManage = new JButton("Remove from List");
	JButton deleteManage = new JButton("Delete list");
	//End manage buttons
	private JComboBox<Object> zipShownContent;
	private JComboBox<Object> manageShownContent;
	private ArrayList<String> manageShownContentList = new ArrayList<String>();
	private ArrayList<String> zipShownContentList = new ArrayList<String>();
	private JPanel oToolRight, oToolRightManage;
	private JComboBox<Object> listeAET;
	private JComboBox<String> comboToolChooser;
	private JPopupMenu popMenuPatients = new JPopupMenu();
	private JPopupMenu popMenuStudies = new JPopupMenu();
	private JPopupMenu popMenuSeries = new JPopupMenu();
	private ArrayList<String> zipContent = new ArrayList<String>();
	private ArrayList<String> manageContent = new ArrayList<String>();
	private JPanel anonTablesPanel;
	private int anonCount;
	

	// Tab Export (p2)
	private JLabel stateExports = new JLabel("");
	private JTable tableauExportStudies;
	private JTable tableauExportSeries;
	private TableDataExportStudies modeleExportStudies;
	private TableDataExportSeries modeleExportSeries;
	private TableRowSorter<TableDataExportStudies> sorterExportStudies;
	private TableRowSorter<TableDataExportSeries> sorterExportSeries;
	private StringBuilder remoteFileName;

	//Monitoring (p3)
	Monitoring_GUI monitoring;
	
	// Tab Setup (p4)
	private JComboBox<Object> anonProfiles;
	private Choice bodyCharChoice;
	private Choice datesChoice;
	private Choice bdChoice;
	private Choice ptChoice;
	private Choice scChoice;
	private Choice descChoice;
	private JRadioButton[] bodyCharList = new JRadioButton[2];
	private JRadioButton[] datesList = new JRadioButton[2];
	private JRadioButton[] bdList = new JRadioButton[2];
	private JRadioButton[] ptList = new JRadioButton[2];
	private JRadioButton[] scList = new JRadioButton[2];
	private JRadioButton[] descList = new JRadioButton[2];
	private JTextField centerCode;
	private JTextField remoteServer;
	private JTextField remotePort;
	private JTextField servUsername;
	private JPasswordField servPassword;
	private JTextField remoteFilePath;
	private JComboBox<String> exportType;
	private JButton setupButton;
	//CTP
	JTextField addressFieldCTP;
	private String CTPUsername;
	private String CTPPassword;

	// Settings preferences
	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");
	private Preferences jpreferPerso = Preferences.userRoot().node("<unnamed>/queryplugin");
	
	// Last Table focus
	private JTable lastTableFocus;

	public VueAnon(){
		
		super("Orthanc Tools");
		
		connexionHttp= new ParametreConnexionHttp();
		//On set les objets necessaires
		modelePatients = new TableDataPatientsAnon(connexionHttp);
		modeleExportSeries = new TableDataExportSeries(connexionHttp, this, stateExports);
		modeleSeries = new TableDataSeries(connexionHttp, state, this);
		modeleExportStudies = new TableDataExportStudies(connexionHttp);
		modeleAnonStudies = new TableDataAnonStudies(connexionHttp);
		modeleStudies = new TableDataStudies(connexionHttp);

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////// PANEL 1 : ANONYMIZATION ////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

		/////////////////////////////////////////////////////////////////////////////
		////////////////////////// TOP PANEL ////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		String[] patientParam = {"Patient name", "Patient ID", "Accession number"};
		JComboBox<String> inputType = new JComboBox<String>(patientParam);
		
		topPanel.add(inputType);
		inputType.addItemListener(new ItemListener() {
				@Override
			    public void itemStateChanged(ItemEvent event) {
			       if (event.getStateChange() == ItemEvent.SELECTED) {
			          if (inputType.getSelectedIndex()==0) {
			        	  userInputFirstName.setEnabled(true);
			          }
			          else userInputFirstName.setEnabled(false);
			       }
				}
		});
		inputType.setSelectedIndex(jpreferPerso.getInt("InputParameter", 0));
		
		JTextField userInput = new JTextField();
		userInput.setToolTipText("Set your input accordingly to the field combobox on the left. ('*' stands for any character)");
		userInput.setText("*");
		userInput.setPreferredSize(new Dimension(125,20));
		userInputFirstName.setText("*");
		userInputFirstName.setToolTipText("Set your input accordingly to the field combobox on the left. ('*' stands for any character)");
		userInputFirstName.setPreferredSize(new Dimension(125,20));
		topPanel.add(userInput);
		topPanel.add(new JLabel("First Name : "));
		topPanel.add(userInputFirstName);

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

		JButton search = new JButton("Search");
		search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					search.setText("Searching");
					search.setEnabled(false);
					modelePatients.clear();
					modeleStudies.clear();
					modeleSeries.clear();

					DateFormat df = new SimpleDateFormat("yyyyMMdd");
					date = df.format(from.getDate())+"-"+df.format(to.getDate());
					String userInputString=null;
					if (inputType.getSelectedIndex()==0) {
						userInputString=userInput.getText()+"^"+userInputFirstName.getText();
						if (userInputString.equals("*^*")) userInputString="*"; 
					}
					else userInputString=userInput.getText();
					modelePatients.addPatient(inputType.getSelectedItem().toString(), userInputString, date, 
							studyDesc.getText());
					pack();
				} catch (Exception e1) { System.out.println("Exception"+e1);}
				finally{
					state.setText("");
					search.setEnabled(true);
					search.setText("Search");
					jpreferPerso.putInt("InputParameter", inputType.getSelectedIndex());
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
						VueRest.main();
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
						ImportDCM.main();
					}
					
				});
			}
		});

		topPanel.add(search);
		topPanel.add(queryRetrieveBtn);
		topPanel.add(queryImportBtn);
		this.state.setText("");
		mainPanel.add(topPanel);

		/////////////////////////////////////////////////////////////////////////////
		////////////////////////// TABLES ///////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////
		

		
		JPanel tablesPanel = new JPanel(new FlowLayout());
		this.tableauPatients = new JTable(modelePatients);
		this.tableauStudies = new JTable(modeleStudies);
		this.tableauSeries = new JTable(modeleSeries);
		this.sorterPatients = new TableRowSorter<TableDataPatientsAnon>(modelePatients);
		this.sorterStudies = new TableRowSorter<TableDataStudies>(modeleStudies);
		this.sorterSeries = new TableRowSorter<TableDataSeries>(modeleSeries);
		this.sorterPatients.setSortsOnUpdates(true);
		this.sorterStudies.setSortsOnUpdates(true);
		this.sorterSeries.setSortsOnUpdates(true);
		
		//Listener pour savoir quelle table a le dernier focus
		FocusListener tableFocus=new FocusListener() {
			Color background= tableauSeries.getSelectionBackground();
			@Override
				public void focusGained(FocusEvent e) {
					//memorise le dernier focus de table
					JTable source= (JTable) e.getSource();
					lastTableFocus=source;
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
		
		//Ajout des listener focus tableau
		
		this.tableauPatients.addFocusListener(tableFocus);
		this.tableauStudies.addFocusListener(tableFocus);
		this.tableauSeries.addFocusListener(tableFocus);
		
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

		this.tableauPatients.setDefaultRenderer(Date.class, new DateRendererAnon());
		this.tableauPatients.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.tableauPatients.addMouseListener(new TablePatientsMouseListener(
				this, this.tableauPatients, this.modelePatients, this.modeleStudies, this.modeleSeries, 
				tableauPatients.getSelectionModel()));
		List<RowSorter.SortKey> sortKeysPatients = new ArrayList<>();
		sortKeysPatients.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sortKeysPatients.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sorterPatients.setSortKeys(sortKeysPatients);
		sorterPatients.sort();
		this.tableauPatients.setRowSorter(sorterPatients);
				
		

		JMenuItem menuItemModifyPatients = new JMenuItem("Show tags/ Modify");
		menuItemModifyPatients.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					new Modify("patients",(String)tableauPatients.getValueAt(tableauPatients.getSelectedRow(),2), gui, connexionHttp, state);
				}
			});
		
		JMenuItem menuItemDeletePatients = new JMenuItem("Delete this patient");
		menuItemDeletePatients.addActionListener(new DeleteActionMainPanel(connexionHttp, "Patient", this.modeleStudies, this.tableauStudies, 
				this.modeleSeries, this.tableauSeries, this.modelePatients, this.tableauPatients, this.state, this, search));

		popMenuPatients.add(menuItemModifyPatients);
		popMenuPatients.addSeparator();
		popMenuPatients.add(menuItemDeletePatients);
		//Selectionne la ligne avant affichage du popupMenu
		popMenuPatients.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = tableauPatients.rowAtPoint(SwingUtilities.convertPoint(popMenuPatients, new Point(0, 0), tableauPatients));
                        if (rowAtPoint > -1) {
                        	tableauPatients.setRowSelectionInterval(rowAtPoint, rowAtPoint);
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
		
		this.tableauPatients.setComponentPopupMenu(popMenuPatients);

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

		this.tableauStudies.addMouseListener(new TableStudiesMouseListener(this, this.tableauStudies, this.modeleStudies, this.modeleSeries, tableauStudies.getSelectionModel()));
		List<RowSorter.SortKey> sortKeysStudies = new ArrayList<>();
		sortKeysStudies.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sortKeysStudies.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
		sorterStudies.setSortKeys(sortKeysStudies);
		sorterStudies.sort();
		this.tableauStudies.setRowSorter(sorterStudies);
		this.tableauStudies.setDefaultRenderer(Date.class, new DateRendererAnon());
		
		JMenuItem menuItemModifyStudy = new JMenuItem("Show tags / Modify");
		menuItemModifyStudy.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					new Modify("studies",(String)tableauStudies.getValueAt(tableauStudies.getSelectedRow(),3), gui, connexionHttp, state);
				}
			});
		
		
		JMenuItem menuItemDeleteStudy = new JMenuItem("Delete this study");
		menuItemDeleteStudy.addActionListener(new DeleteActionMainPanel(connexionHttp, "Study", this.modeleStudies, this.tableauStudies, 
				this.modeleSeries, this.tableauSeries, this.modelePatients, this.tableauPatients, this.state, this, search));
		
		popMenuStudies.add(menuItemModifyStudy);
		popMenuStudies.addSeparator();
		popMenuStudies.add(menuItemDeleteStudy);
		popMenuStudies.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = tableauStudies.rowAtPoint(SwingUtilities.convertPoint(popMenuStudies, new Point(0, 0), tableauStudies));
                        if (rowAtPoint > -1) {
                        	tableauStudies.setRowSelectionInterval(rowAtPoint, rowAtPoint);
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
		
		
		this.tableauStudies.setComponentPopupMenu(popMenuStudies);

		////////////////////////// SERIES ///////////////////////////////

		this.tableauSeries.getTableHeader().setReorderingAllowed(false);
		this.tableauSeries.getColumnModel().getColumn(0).setMinWidth(260);
		this.tableauSeries.getColumnModel().getColumn(0).setMaxWidth(260);
		this.tableauSeries.getColumnModel().getColumn(0).setResizable(false);
		this.tableauSeries.getColumnModel().getColumn(1).setMinWidth(100);
		this.tableauSeries.getColumnModel().getColumn(1).setMaxWidth(100);
		this.tableauSeries.getColumnModel().getColumn(1).setResizable(false);
		this.tableauSeries.getColumnModel().getColumn(2).setMinWidth(100);
		this.tableauSeries.getColumnModel().getColumn(2).setMaxWidth(100);
		this.tableauSeries.getColumnModel().getColumn(2).setResizable(false);
		this.tableauSeries.getColumnModel().getColumn(3).setMinWidth(0);
		this.tableauSeries.getColumnModel().getColumn(3).setMaxWidth(0);
		this.tableauSeries.getColumnModel().getColumn(3).setResizable(false);
		this.tableauSeries.getColumnModel().getColumn(4).setMinWidth(0);
		this.tableauSeries.getColumnModel().getColumn(4).setMaxWidth(0);
		this.tableauSeries.getColumnModel().getColumn(4).setResizable(false);
		this.tableauSeries.getColumnModel().getColumn(5).setMinWidth(70);
		this.tableauSeries.getColumnModel().getColumn(5).setMaxWidth(70);
		this.tableauSeries.getColumnModel().getColumn(5).setResizable(false);
		this.tableauSeries.setPreferredScrollableViewportSize(new Dimension(530,267));
		this.tableauSeries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		TableColumn serieDescCol = tableauSeries.getColumnModel().getColumn(0);
		serieDescCol.setCellEditor(new DialogCellEditor());

		List<RowSorter.SortKey> sortKeysSeries = new ArrayList<>();
		sortKeysSeries.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sortKeysSeries.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorterSeries.setSortKeys(sortKeysSeries);
		sorterSeries.sort();
		this.tableauSeries.setRowSorter(sorterSeries);

		JMenuItem menuItemModifySeries = new JMenuItem("Show tags / Modify");
		menuItemModifySeries.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					new Modify("series",(String)tableauSeries.getValueAt(tableauSeries.getSelectedRow(),4), gui, connexionHttp, state);
				}
			});
		
		JMenuItem menuItemSopClass = new JMenuItem("Check if secondary capture");
		menuItemSopClass.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String instanceUid = modeleSeries.getSerie(tableauSeries.convertRowIndexToModel(tableauSeries.getSelectedRow())).getInstance();
					modeleSeries.checkSopClassUid(instanceUid);
					modeleSeries.setValueAt(modeleSeries.checkSopClassUid(instanceUid), tableauSeries.convertRowIndexToModel(tableauSeries.getSelectedRow()), 3);
					modeleSeries.fireTableCellUpdated(tableauSeries.getSelectedRow(), 3);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		JMenuItem menuItemAllSopClass = new JMenuItem("Detect all secondary captures");
		menuItemAllSopClass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					modeleSeries.detectAllSecondaryCaptures();
					modeleSeries.clear();
					modeleSeries.addSerie(tableauStudies.getValueAt(tableauStudies.convertRowIndexToModel(tableauStudies.getSelectedRow()), 3).toString());
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		JMenuItem menuItemDeleteAllSop = new JMenuItem("Remove all secondary captures");
		menuItemDeleteAllSop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					modeleSeries.removeAllSecondaryCaptures();
					modeleSeries.clear();
					modeleSeries.addSerie(tableauStudies.getValueAt(tableauStudies.convertRowIndexToModel(tableauStudies.getSelectedRow()), 3).toString());
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		JMenuItem menuItemDeleteSeries = new JMenuItem("Delete this serie");
		menuItemDeleteSeries.addActionListener(new DeleteActionMainPanel(connexionHttp, "Serie", this.modeleStudies, this.tableauStudies, 
				this.modeleSeries, this.tableauSeries, this.modelePatients, this.tableauPatients, this.state, this, search));

		popMenuSeries.add(menuItemModifySeries);
		popMenuSeries.addSeparator();
		popMenuSeries.add(menuItemSopClass);
		popMenuSeries.add(menuItemAllSopClass);
		popMenuSeries.add(menuItemDeleteAllSop);
		popMenuSeries.addSeparator();
		popMenuSeries.add(menuItemDeleteSeries);
		popMenuSeries.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = tableauSeries.rowAtPoint(SwingUtilities.convertPoint(popMenuSeries, new Point(0, 0), tableauSeries));
                        if (rowAtPoint > -1) {
                        	tableauSeries.setRowSelectionInterval(rowAtPoint, rowAtPoint);
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
		
		this.tableauSeries.setComponentPopupMenu(popMenuSeries);

		this.tableauSeries.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row, int col) {

				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

				boolean status = (boolean)table.getModel().getValueAt(tableauSeries.convertRowIndexToModel(row), 3);
				if (status && !isSelected) {
					setBackground(Color.RED);
					setForeground(Color.black);
				}else if(isSelected){
					setBackground(tableauExportStudies.getSelectionBackground());
				}else{
					setBackground(tableauExportStudies.getBackground());
				}
				return this;
			}   
		});

		/////////////////////////////////////////////////////////////////////////////
		///////////////////////// ORTHANC TOOLBOX ///////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////

		JPanel toolbox = new JPanel(new BorderLayout());
		JPanel labelAndAnon = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel ortToolbox = new JLabel("<html><font size=\"5\">Orthanc toolbox</font></html>");
		ortToolbox.setBorder(new EmptyBorder(0, 0, 0, 50));
		labelAndAnon.add(ortToolbox);
		labelAndAnon.add(this.state);
		zipShownContent = new JComboBox<Object> (zipContent.toArray());
		zipShownContent.setPreferredSize(new Dimension(297,27));

		oToolRight = new JPanel();
		oToolRight.setLayout(new BoxLayout(oToolRight, BoxLayout.PAGE_AXIS));

		JPanel storeTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton storeBtn = new JButton("Store list");
		try {
			QueryFillStore query = new QueryFillStore(connexionHttp);
			listeAET = new JComboBox<Object>(query.getAET());

			listeAET.setPreferredSize(new Dimension(297, 27));
			storeTool.add(listeAET);
			storeBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(!zipContent.isEmpty()){
						state.setText("<html>Storing data <font color='red'> <br>(Do not use the toolbox while the current operation is not done)</font></html>");
						storeBtn.setEnabled(false);
						pack();
						SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								try {
									query.store(listeAET.getSelectedItem().toString(), zipContent);
									
									
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								return null;
							}

							@Override
							protected void done(){
								state.setText("<html><font color='green'>The data have successfully been stored.</font></html>");
								zipShownContent.removeAllItems();
								zipShownContentList.removeAll(zipShownContentList);
								zipContent.removeAll(zipContent);
								storeBtn.setEnabled(true);
								pack();
							}
						};
						worker.execute();
					}
				}
			});
			storeTool.add(storeBtn);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e){
			JOptionPane.showMessageDialog(gui, "You should set an AET before using this app (some functions may not work).",
					"No AET found", JOptionPane.INFORMATION_MESSAGE);
		}
		JPanel comboBoxBtn = new JPanel(new FlowLayout(FlowLayout.LEFT));
		comboBoxBtn.add(zipShownContent);
		
		removeFromZip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeFromToolList(zipContent, zipShownContent, zipShownContentList, zipSize, state);
					
			}
		});
		
		removeFromManage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeFromToolList(manageContent, manageShownContent, manageShownContentList, manageSize, state);
			}
		});
		
		addToZip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Ajout dans la tool list		
				addToToolList(zipContent,zipShownContent, zipShownContentList, zipSize);
			}
		});
		
		addManage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Ajout dans la tool list
				addToToolList(manageContent,manageShownContent, manageShownContentList, manageSize);
			}
		});
		
		// Efface la liste de Orthanc
		deleteManage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Liste des orthanc ID A supprimer qu'on va delete un a un
				
				ArrayList<String> deleteSeries=new ArrayList<String>();
				ArrayList<String> deleteStudies=new ArrayList<String>();
				ArrayList<String> deletePatients=new ArrayList<String>();
				
				for (int i=0; i<manageContent.size(); i++){
					if (manageShownContentList.get(i).contains("Study -")){
						deleteStudies.add("/studies/"+manageContent.get(i));
					}
					else if (manageShownContentList.get(i).contains("Serie -")){
						deleteSeries.add("/series/"+manageContent.get(i));
					}
					else if (manageShownContentList.get(i).contains("Patient -")){
						deletePatients.add("/patients/"+manageContent.get(i));
					}
				}
				state.setText("<html><font color='red'>Deleting please wait</font></html>");
				enableManageButtons(false);
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws IOException {
						int progress=0;
						for (int i=0 ; i<deleteSeries.size(); i++){
							connexionHttp.makeDeleteConnection(deleteSeries.get(i));
							progress++;
							state.setText("<html><font color='red'>Deleted "+ progress +"/"+manageContent.size()+"</font></html>");
						}
						for (int i=0 ; i<deleteStudies.size(); i++){
							connexionHttp.makeDeleteConnection(deleteStudies.get(i));
							progress++;
							state.setText("<html><font color='red'>Deleted "+ progress +"/"+manageContent.size()+"</font></html>");
						}
						for (int i=0 ; i<deletePatients.size(); i++){
							connexionHttp.makeDeleteConnection(deletePatients.get(i));
							progress++;
							state.setText("<html><font color='red'>Deleted "+ progress +"/"+manageContent.size()+"</font></html>");
						}
						return null;
					}

					@Override
					protected void done(){
						state.setText("<html><font color='green'>Delete Done</font></html>");
						enableManageButtons(true);
						manageSize.setText("empty list");
						manageShownContent.removeAllItems();
						manageShownContentList.removeAll(manageShownContentList);
						manageContent.removeAll(manageContent);
						search.doClick();
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
		manageShownContent=new JComboBox<Object>(manageContent.toArray());
		manageShownContent.setPreferredSize(new Dimension(297,27));
		
	
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
		anonPatientTable.getColumnModel().getColumn(6).setMinWidth(0);
		anonPatientTable.getColumnModel().getColumn(6).setMaxWidth(0);
		anonPatientTable.getColumnModel().getColumn(7).setMinWidth(0);
		anonPatientTable.getColumnModel().getColumn(7).setMaxWidth(0);
		anonPatientTable.setPreferredScrollableViewportSize(new Dimension(440,130));
		anonPatientTable.addMouseListener(new TableAnonPatientsMouseListener(anonPatientTable, modeleAnonPatients, modeleAnonStudies));
		anonPatientTable.putClientProperty("terminateEditOnFocusLost", true);

		anonStudiesTable = new JTable(modeleAnonStudies);
		anonStudiesTable.getTableHeader().setToolTipText("Click on the description cells to change their values");
		anonStudiesTable.getColumnModel().getColumn(0).setMinWidth(200);
		anonStudiesTable.getColumnModel().getColumn(1).setMinWidth(80);
		anonStudiesTable.getColumnModel().getColumn(1).setMaxWidth(80);
		anonStudiesTable.getColumnModel().getColumn(2).setMinWidth(150);
		anonStudiesTable.getColumnModel().getColumn(2).setMaxWidth(150);
		anonStudiesTable.getColumnModel().getColumn(3).setMinWidth(0);
		anonStudiesTable.getColumnModel().getColumn(3).setMaxWidth(0);
		anonStudiesTable.setPreferredScrollableViewportSize(new Dimension(430,130));
		anonStudiesTable.setDefaultRenderer(Date.class, new DateRendererAnon());

		TableColumn studyDescCol = anonStudiesTable.getColumnModel().getColumn(0);
		studyDescCol.setCellEditor(new DialogCellEditor());

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
				if(tableauPatients.getSelectedRow() != -1){
					try {
						String patientName = tableauPatients.getValueAt(tableauPatients.getSelectedRow(), 0).toString();
						String patientID = tableauPatients.getValueAt(tableauPatients.getSelectedRow(), 1).toString();
						String patientUID = tableauPatients.getValueAt(tableauPatients.getSelectedRow(), 2).toString();
						Date patientBirthDate = (Date)tableauPatients.getValueAt(tableauPatients.getSelectedRow(), 3);
						String patientSex = tableauPatients.getValueAt(tableauPatients.getSelectedRow(), 4).toString();
						ArrayList<String> listeDummy = new ArrayList<String>();
						if((tableauSeries.getSelectedRow() != -1 || tableauStudies.getSelectedRow() != -1) && tableauPatients.getSelectedRows().length == 1){
							listeDummy.add(modeleStudies.getValueAt(tableauStudies.convertRowIndexToModel(tableauStudies.getSelectedRow()), 3).toString());
							modeleAnonPatients.addPatient(connexionHttp,patientName, patientID, patientBirthDate, patientSex, listeDummy);
							modeleAnonStudies.clear();
							modeleAnonStudies.addStudies(patientName, patientID, listeDummy);
							for(int i = 0; i < modeleAnonPatients.getPatientList().size(); i++){
								if(modeleAnonPatients.getPatient(i).getPatientId().equals(patientID) && 
										modeleAnonPatients.getPatient(i).getPatientName().equals(patientName)){
									anonPatientTable.setRowSelectionInterval(i, i);
								}
							}
						}else {
							for(Integer i : tableauPatients.getSelectedRows()){
								modeleStudies.clear();
								patientName = tableauPatients.getValueAt(i, 0).toString();
								patientID = tableauPatients.getValueAt(i, 1).toString();
								patientUID = tableauPatients.getValueAt(i, 2).toString();
								patientBirthDate = (Date)tableauPatients.getValueAt(i, 3);
								patientSex=tableauPatients.getValueAt(i, 4).toString();
								ArrayList<String> listeUIDs = new ArrayList<String>();
								modeleStudies.addStudy(patientName, patientID, patientUID);
								listeUIDs.addAll(modeleStudies.getIds());
								modeleAnonPatients.addPatient(connexionHttp,patientName, patientID, patientBirthDate, patientSex, listeUIDs);
								modeleAnonStudies.clear();
								modeleAnonStudies.addStudies(patientName, patientID, listeUIDs);
							}
							for(int i = 0; i < modeleAnonPatients.getPatientList().size(); i++){
								if(modeleAnonPatients.getPatient(i).getPatientId().equals(patientID) && 
										modeleAnonPatients.getPatient(i).getPatientName().equals(patientName)){
									anonPatientTable.setRowSelectionInterval(i, i);
								}
							}
						}
					}catch (IOException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (Exception e){
						e.printStackTrace();
					}
				}
				pack();
			}
		});
		removeFromAnonList = new JButton("Remove");
		removeFromAnonList.setPreferredSize(new Dimension(120,27));
		removeFromAnonList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(anonStudiesTable.getSelectedRow() != -1){
					String patientID = modeleAnonStudies.getValueAt(
							anonStudiesTable.convertRowIndexToModel(anonStudiesTable.getSelectedRow()), 3).toString();
					String uidToRemove = modeleAnonStudies.removeStudy(anonStudiesTable.getSelectedRow());
					modeleAnonPatients.removeStudy(uidToRemove);
					if(anonStudiesTable.getRowCount() == 0){
						for(int i = 0; i< modeleAnonPatients.getPatientList().size(); i++){
							if(modeleAnonPatients.getPatientList().get(i).getPatientId().equals(patientID)){
								modeleAnonPatients.removePatient(i);
							}
						}
					}
				}else if(anonPatientTable.getSelectedRow() != -1){
					modeleAnonPatients.removePatient(anonPatientTable.getSelectedRow());
					modeleAnonStudies.empty();
				}
				pack();
			}
		});
		
		setNamesIdBtn = new JButton("CTP");
		setNamesIdBtn.setPreferredSize(new Dimension(120,27));
		setNamesIdBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Si il y a des patients dans la liste
				if(!modeleAnonPatients.getPatientList().isEmpty()){
					// Si pas de study selectionnees on selectionne de force le 1er
					if (anonStudiesTable.getSelectedRow()==-1) anonStudiesTable.setRowSelectionInterval(0, 0);
					//On genere l'objet qui gere le CTP
					CTP_Gui dialog = new CTP_Gui();
					//On prepare les donnees locales dans l'objet
					String patientName=(String) anonPatientTable.getValueAt(anonPatientTable.getSelectedRow(), 0);
					//String patientID=(String) anonPatientTable.getValueAt(anonPatientTable.getSelectedRow(), 1);
					Date patientDOB=(Date) anonPatientTable.getValueAt(anonPatientTable.getSelectedRow(), 6);
					String patientSex=(String) anonPatientTable.getValueAt(anonPatientTable.getSelectedRow(), 7);
					if (patientSex.equals("")) patientSex="N/A";
					//String studyDescription=(String) anonStudiesTable.getValueAt(anonStudiesTable.getSelectedRow(), 0);
					Date studyDate=(Date) anonStudiesTable.getValueAt(anonStudiesTable.getSelectedRow(), 1);
					//SK Si pas de date on injecte la date du jour ? ou on passe la string ici et on gere ds le CTP?
					if (studyDate==null) studyDate=new Date();
					if (patientDOB==null) patientDOB=new Date();
					//envoi des donnes dans objet GUI pour CTP
					dialog.setStudyLocalValue(patientName, df.format(studyDate), patientSex, df.format(patientDOB));
					dialog.pack();
					dialog.setModal(true);
					dialog.setLocationRelativeTo(gui);
					dialog.setVisible(true);
					//On recupere les donnees et on met dans l'anonymisation
					if(dialog.getOk()) {
						CTPUsername=dialog.getLogin();
						CTPPassword=dialog.getPassword();
						String patientNewName=dialog.getAnonName();
						String patientNewID=dialog.getAnonID();
						String visitName=dialog.getVisitName();
						anonPatientTable.setValueAt(patientNewName, anonPatientTable.getSelectedRow(), 3);
						anonPatientTable.setValueAt(patientNewID, anonPatientTable.getSelectedRow(), 4);
						anonStudiesTable.setValueAt(visitName, anonStudiesTable.getSelectedRow(), 0);
						//Si un seul patient
						if (anonPatientTable.getSelectedRowCount()==1) anonBtn.doClick();
					}

				}
			}
		});
		
		anonBtn = new JButton("Anonymize");
		anonBtn.setPreferredSize(new Dimension(120,27));
		anonBtn.addActionListener(new AnonAction());

		JPanel anonBtnPanelTop = new JPanel(new FlowLayout());
		anonBtnPanelTop.add(addToAnon);
		anonBtnPanelTop.add(displayAnonTool);
		anonBtnPanelTop.add(displayExportTool);
		anonBtnPanelTop.add(displayManageTool);
		anonDetailed.add(anonBtnPanelTop, BorderLayout.NORTH);
		anonTablesPanel.add(new JScrollPane(anonPatientTable));
		anonTablesPanel.add(new JScrollPane(anonStudiesTable));
		JPanel anonBtnPanelRight = new JPanel(new GridBagLayout());
		GridBagConstraints gbBtnPanel = new GridBagConstraints();
		gbBtnPanel.gridx = 0;
		gbBtnPanel.gridy = 0;
		anonBtnPanelRight.add(removeFromAnonList, gbBtnPanel);
		gbBtnPanel.insets = new Insets(10, 0, 0, 0);
		gbBtnPanel.gridy = 1;
		anonBtnPanelRight.add(setNamesIdBtn, gbBtnPanel);
		gbBtnPanel.gridy = 2;
		anonBtnPanelRight.add(anonBtn, gbBtnPanel);
		anonTablesPanel.add(anonBtnPanelRight);
		anonTablesPanel.setVisible(false);
		addToAnon.setVisible(false);
		anonDetailed.add(anonTablesPanel, BorderLayout.WEST);

		exportZip.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!zipContent.isEmpty()){
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(new java.io.File(jprefer.get("zipLocation", System.getProperty("user.dir"))));
					if (comboToolChooser.getSelectedItem().equals("Image with Viewer (iso)")) chooser.setSelectedFile(new File(zipShownContentList.get(0).replaceAll("/", "_")+"_image.iso")); 
					else chooser.setSelectedFile(new File(dfZip.format(new Date()) + ".zip")); 
					chooser.setDialogTitle("Export to...");
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);
					
					if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						jprefer.put("zipLocation", chooser.getSelectedFile().toPath().toString());
						ConvertZipAction convertzip=new ConvertZipAction(connexionHttp);
						exportZip.setText("Generating Zip...");
						exportZip.setEnabled(false);
						addToZip.setEnabled(false);
						removeFromZip.setEnabled(false);
						comboToolChooser.setEnabled(false);
						state.setText("Generating Zip...");
							
							SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws IOException {
								
								if (comboToolChooser.getSelectedItem().equals("ZIP File") || comboToolChooser.getSelectedItem().equals("DICOMDIR Zip") ) {
										convertzip.setConvertZipAction(chooser.getSelectedFile().getAbsolutePath().toString() , zipContent, false);
										try{
										if (comboToolChooser.getSelectedItem().equals("ZIP File")) convertzip.generateZip(false);
										if (comboToolChooser.getSelectedItem().equals("DICOMDIR Zip")) convertzip.generateZip(true);
										}catch(IOException e){
										e.printStackTrace();
										}
									
									}
								
								else if ( (comboToolChooser.getSelectedItem().equals("Image with Viewer (zip)")) ) {
									convertzip.setConvertZipAction("Viewer", zipContent, true);
									convertzip.generateZip(true);
									File tempImageZip=convertzip.getGeneratedZipFile();
									String viewerString=jpreferPerso.get("viewerDistribution", "empty");
									
									if (!viewerString.equals("empty") && new File(viewerString).exists()){
										File packageViewer=new File(viewerString);
										ZipAndViewer zip=new ZipAndViewer(tempImageZip, chooser.getSelectedFile(), packageViewer);
										zip.ZipAndViewerToZip();
										JOptionPane.showMessageDialog(gui,"Image and Viewer exported in "+viewerString.toString());
									}
									else {
										JOptionPane.showMessageDialog(gui,"Viewer not available, please download it in the setup tab");
									}
								}
								
								else if ( (comboToolChooser.getSelectedItem().equals("Image with Viewer (iso)"))) {
									convertzip.setConvertZipAction("Viewer", zipContent, true);
									convertzip.generateZip(true);
									File tempImageZip=convertzip.getGeneratedZipFile();
									String distinationString=jpreferPerso.get("viewerDistribution", "empty");
									if (!distinationString.equals("empty")){
										File packageViewer=new File(distinationString);
										ZipAndViewer zip=new ZipAndViewer(tempImageZip, chooser.getSelectedFile(), packageViewer);
										zip.unzip();
										zip.generateIsoFile();
										JOptionPane.showMessageDialog(gui,"Image and Viewer ISO generated at "+chooser.getSelectedFile().getAbsolutePath().toString());
									}
									else {
										JOptionPane.showMessageDialog(gui,"Viewer not available, please download it in the setup tab");
									}
								}
								return null;
							}
							
							@Override
							protected void done() {
								state.setText("<html><font color='green'>The data have successfully been exported to zip</font></html>");
								//Reactivate component after export
								exportZip.setText("Export list");
								exportZip.setEnabled(true);
								addToZip.setEnabled(true);
								removeFromZip.setEnabled(true);
								comboToolChooser.setEnabled(true);
								//empty exported list
								zipShownContent.removeAllItems();
								zipShownContentList.removeAll(zipShownContentList);
								zipContent.removeAll(zipContent);	
								//Close export tool
								openCloseExportTool(false);
								pack();
							}
							
						};
						worker.execute();
						pack();
					
					}	
				}

				
			}
		});
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

		JScrollPane jscp3 = new JScrollPane(tableauSeries);
		jscp3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		c.gridx = 2;
		c.gridy = 0;
		tablesPanel.add(jscp3,c);

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
		this.sorterExportStudies = new TableRowSorter<TableDataExportStudies>(modeleExportStudies);
		this.sorterExportSeries = new TableRowSorter<TableDataExportSeries>(modeleExportSeries);		

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

		this.tableauExportStudies.setDefaultRenderer(Date.class, new DateRendererAnon());

		JPopupMenu popMenuExportStudies = new JPopupMenu();
		this.tableauExportStudies.setComponentPopupMenu(popMenuExportStudies);

		JMenuItem menuItemExportStudiesRemove = new JMenuItem("Remove from list");
		menuItemExportStudiesRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				modeleExportSeries.clear();
				modeleExportStudies.removeStudy(tableauExportStudies.convertRowIndexToModel(tableauExportStudies.getSelectedRow()));
			}
		});
		popMenuExportStudies.add(menuItemExportStudiesRemove);

		JMenuItem menuItemExportStudiesDelete = new JMenuItem("Delete this study");
		menuItemExportStudiesDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				DeleteActionExport del = new DeleteActionExport(connexionHttp, tableauExportStudies, modeleExportStudies);
				del.delete();
				modeleExportStudies.removeStudy(tableauExportStudies.convertRowIndexToModel(tableauExportStudies.getSelectedRow()));
				modeleExportSeries.clear();
			}
		});
		popMenuExportStudies.add(menuItemExportStudiesDelete);

		JMenuItem menuItemEmptyList = new JMenuItem("Empty the export list");
		menuItemEmptyList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int dialogResult;
				dialogResult = JOptionPane.showConfirmDialog (gui, 
						"Are you sure you want to clear the export list ?",
						"Clearing the export list",
						JOptionPane.YES_NO_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION){
					modeleExportSeries.clear();
					modeleExportStudies.clear();
					modeleExportStudies.clearIdsList();
				}
			}
		});
		popMenuExportStudies.add(menuItemEmptyList);

		sorterExportStudies.setSortKeys(sortKeysStudies);
		sorterExportStudies.sort();
		this.tableauExportStudies.setRowSorter(sorterExportStudies);

		this.tableauExportStudies.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				// selects the row at which point the mouse is clicked
				Point point = event.getPoint();
				int currentRow = tableauExportStudies.rowAtPoint(point);
				tableauExportStudies.setRowSelectionInterval(currentRow, currentRow);
				// We clear the details
				modeleExportSeries.clear();
				try {
					if(modeleExportStudies.getRowCount() != 0){
						String studyID = (String)tableauExportStudies.getValueAt(tableauExportStudies.getSelectedRow(), 5);
						modeleExportSeries.addSerie(studyID);
					}
				}catch (RuntimeException e1){
					//Ignore
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});

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

		this.tableauExportSeries.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				// selects the row at which point the mouse is clicked
				Point point = event.getPoint();
				int currentRow = tableauExportSeries.rowAtPoint(point);
				tableauExportSeries.setRowSelectionInterval(currentRow, currentRow);
			}
		});

		this.tableauExportSeries.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row, int col) {

				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

				boolean status = (boolean)table.getModel().getValueAt(tableauExportSeries.convertRowIndexToModel(row), 3);
				if (status & !isSelected) {
					setBackground(Color.RED);
					setForeground(Color.black);
				}else if(isSelected){
					setBackground(tableauExportStudies.getSelectionBackground());
				}else{
					setBackground(tableauExportStudies.getBackground());
				}
				return this;
			}   
		});

		JPopupMenu popMenuExportSeries = new JPopupMenu();
		this.tableauExportSeries.setComponentPopupMenu(popMenuExportSeries);

		JMenuItem menuItemExportSeriesDelete = new JMenuItem("Delete this serie");
		menuItemExportSeriesDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String uid = tableauExportStudies.getValueAt(tableauExportStudies.getSelectedRow(), 4).toString();
				DeleteActionExport del = new DeleteActionExport(connexionHttp, tableauExportSeries, modeleExportSeries);
				del.delete();
				if(tableauExportSeries.getRowCount() == 1){
					modeleAnonStudies.removeFromList(uid);
					modeleExportStudies.removeStudy(tableauExportStudies.convertRowIndexToModel(tableauExportStudies.getSelectedRow()));
				}
				modeleExportSeries.removeSerie(tableauExportSeries.convertRowIndexToModel(tableauExportSeries.getSelectedRow()));
			}
		});

		JMenuItem menuItemExportSeriesDeleteAllSc = new JMenuItem("Delete all secondary captures");
		menuItemExportSeriesDeleteAllSc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					boolean[] studyExist = {true};
					modeleExportSeries.removeAllSecondaryCaptures();

					if(modeleExportSeries.getSeries().isEmpty()){
						modeleExportStudies.removeStudy(tableauExportStudies.convertRowIndexToModel(tableauExportStudies.getSelectedRow()));
						studyExist[0] = false;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		popMenuExportSeries.add(menuItemExportSeriesDelete);
		popMenuExportSeries.add(menuItemExportSeriesDeleteAllSc);
		sorterExportSeries.setSortKeys(sortKeysSeries);
		sorterExportSeries.sort();
		this.tableauExportSeries.setRowSorter(sorterExportSeries);

		tableExportPanel.add(new JScrollPane(this.tableauExportStudies));
		tableExportPanel.add(new JScrollPane(this.tableauExportSeries));

		stateExports.setBorder(new EmptyBorder(0, 0, 0, 40));

		JPanel exportPanel = new JPanel(new FlowLayout());

		JPanel labelPanelExport = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel exportToLabel = new JLabel("<html><font size=\"5\">Export list to...</font></html>");
		exportToLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
		labelPanelExport.add(exportToLabel);
		labelPanelExport.add(stateExports);

		JButton exportBtn = new JButton("Remote server");
		exportBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					boolean[] successful = {true};
					String exception = "";
					@Override
					protected Void doInBackground() {
						// Putting the export preferences in the anon plugin registry
						if(remoteServer.getText() != null){
							jprefer.put("remoteServer", remoteServer.getText());
						}
						
						if(remotePort.getText() != null){
							jprefer.put("remotePort", remotePort.getText());
						}
						
						if(servUsername.getText() != null){
							jprefer.put("servUsername", servUsername.getText());
						}
						if(new String(servPassword.getPassword()) != null){
							jprefer.put("servPassword", new String(servPassword.getPassword()));
						}
						if(remoteFilePath.getText() != null){
							jprefer.put("remoteFilePath", remoteFilePath.getText());
						}
						jprefer.put("exportType", exportType.getSelectedItem().toString());

							exportBtn.setText("Exporting...");
							exportBtn.setEnabled(false);
							
							try {
								ConvertZipAction convertzip=new ConvertZipAction(connexionHttp);
								convertzip.setConvertZipAction("tempZipOrthanc", modeleExportStudies.getOrthancIds(), true);
								convertzip.generateZip(false);
								String zipPath = convertzip.getGeneratedZipPath();
								String zipName = convertzip.getGeneratedZipName();
								remoteFileName = new StringBuilder();
								
								//removing the temporary file default name value
								remoteFileName.append(zipName.substring(0,14));
								remoteFileName.append(zipName.substring(zipName.length() - 4));
								ExportFiles export = new ExportFiles(jprefer.get("exportType", ExportFiles.OPTION_FTP), 
										jprefer.get("remoteFilePath", "/"), remoteFileName.toString(), zipPath, jprefer.get("remoteServer", ""), 
										jprefer.getInt("remotePort", 21), jprefer.get("servUsername", ""), jprefer.get("servPassword", ""));
								export.export();
								if(export.getResult() != null){
									successful[0] = false;
									exception = export.getResult();
								}
							} catch (FileNotFoundException e){
								successful[0] = false;
								stateExports.setText("<html><font color='red'>The data export failed (the zip was not created)</font></html>");
							} catch (IOException e) {
								successful[0] = false;
								exception = e.getMessage();								
							} 
						return null;
					}

					@Override
					public void done(){
						if(successful[0]){
							stateExports.setText("<html><font color='green'>The data has been successfully been exported</font></html>");
						}else{
							if(!stateExports.getText().contains("The zip was not created")){
								stateExports.setText("<html><font color='red'>The data export failed (" + exception + ") </font></html>");	
							}
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

		exportBtn.setToolTipText("Fill the remote server parameters in the setup tab before attempting an export.");

		JComboBox<String> reportType = new JComboBox<String>();
		reportType.addItem("CSV");
		
		JButton reportBtn = new JButton("Report");
		reportBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JSONParser parser=new JSONParser();
				if(reportType.getSelectedItem().equals("CSV")){
					jprefer.put("reportType", "CSV");
					CSV csv = new CSV();
					if(!modeleExportStudies.getOrthancIds().isEmpty()){
						for(String uid : modeleExportStudies.getOrthancIds()){
							try {
								StringBuilder sb =connexionHttp.makeGetConnectionAndStringBuilder("/studies/" + uid);
								JSONObject cdfResponse = (JSONObject) parser.parse(sb.toString());
								StringBuilder sb2 =connexionHttp.makeGetConnectionAndStringBuilder("/studies/" + uid +"/statistics");
								JSONObject cdfResponseStats = (JSONObject) parser.parse(sb2.toString());
								
								//Recupere Elements d'origine
								JSONObject cdfResponseMainPatientTags=(JSONObject) cdfResponse.get("PatientMainDicomTags");
								JSONObject cdfResponseMainDicomTags=(JSONObject) cdfResponse.get("MainDicomTags");
								
								String AnonymizedFrom=cdfResponse.get("AnonymizedFrom").toString();
								
								String newPatientName ;
								String newPatientId ;
								String newStudyDesc;
								String studyInstanceUid;
								String nbSeries;
								String nbInstances ;
								String size;
								
								// On recupere les data apres anonymization
								
								if (cdfResponseMainPatientTags.containsKey("PatientName") ) {
									newPatientName =cdfResponseMainPatientTags.get("PatientName").toString();
								} else {
									newPatientName ="";
								}
								
								if (cdfResponseMainPatientTags.containsKey(("PatientID"))) {
									newPatientId= cdfResponseMainPatientTags.get("PatientID").toString();
								}else {
									newPatientId="";
								}
								
								if (cdfResponseMainDicomTags.containsKey("StudyDescription")) {
									newStudyDesc =cdfResponseMainDicomTags.get("StudyDescription").toString();
								}else {
									newStudyDesc="";
								}
								
								if (cdfResponseMainDicomTags.containsKey("StudyInstanceUID")) {
									studyInstanceUid=cdfResponseMainDicomTags.get("StudyInstanceUID").toString();
								}else {
									studyInstanceUid="";
								}
								
								if (cdfResponseStats.containsKey("CountSeries")) {
									nbSeries = cdfResponseStats.get("CountSeries").toString();
								}else {
									nbSeries="";
								}
								
								if(cdfResponseStats.containsKey("CountInstances")) {
									nbInstances = cdfResponseStats.get("CountInstances").toString();
								} else {
									nbInstances ="";
								}
								
								if(cdfResponseStats.containsKey("DiskSizeMB")) {
									size = cdfResponseStats.get("DiskSizeMB").toString();
								} else {
									size ="";
								}
								
								
								//Recupere data avant anonymization
								StringBuilder sb3 =connexionHttp.makeGetConnectionAndStringBuilder("/studies/" + AnonymizedFrom);
								JSONObject cdfOriginalStudyDataResponse = (JSONObject) parser.parse(sb3.toString());
								
								JSONObject cdfOriginalStudyDataResponseMainPatientTags=(JSONObject) cdfOriginalStudyDataResponse.get("PatientMainDicomTags");
								JSONObject cdfOriginalStudyDataResponseMainDicomTags=(JSONObject) cdfOriginalStudyDataResponse.get("MainDicomTags");
								
								String oldPatientName;
								String oldPatientId;
								String oldStudyDate;
								String oldStudyDesc;
								
								if (cdfOriginalStudyDataResponseMainPatientTags.containsKey("PatientName")){
									oldPatientName=cdfOriginalStudyDataResponseMainPatientTags.get("PatientName").toString();
								} else {
									oldPatientName="";
								}
								
								if (cdfOriginalStudyDataResponseMainPatientTags.containsKey("PatientID")){
									oldPatientId=cdfOriginalStudyDataResponseMainPatientTags.get("PatientID").toString();
								} else {
									oldPatientId="";
								}
								
								if (cdfOriginalStudyDataResponseMainDicomTags.containsKey("StudyDate")){
									oldStudyDate=cdfOriginalStudyDataResponseMainDicomTags.get("StudyDate").toString();
								} else {
									oldStudyDate="";
								}
								
								if (cdfOriginalStudyDataResponseMainDicomTags.containsKey("StudyDescription")){
									oldStudyDesc=cdfOriginalStudyDataResponseMainDicomTags.get("StudyDescription").toString();
								} else {
									oldStudyDesc="";
								}
								
								csv.addStudy(oldPatientName, oldPatientId, newPatientName, newPatientId, oldStudyDate, oldStudyDesc, newStudyDesc, nbSeries, nbInstances, size, studyInstanceUid);
								
							} catch (org.json.simple.parser.ParseException e1) {
								e1.printStackTrace();
							}
						}
						try {
							csv.genCSV();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
						
					}
				}else {
					CTP ctp=new CTP(CTPUsername, CTPPassword);
					for(Study study : modeleExportStudies.getStudiesList()){
						boolean answer=ctp.validateUpload(study.getStudyDescription(), study.getNewStudyInstanceUID(), study.getPatientName());
						if(answer) stateExports.setText("study correctly Sent to CTP");
					}
					
					/*
					jprefer.put("reportType", "CTP");
					boolean dataSent = true;
					if(!modeleExportStudies.getOrthancIds().isEmpty()){
						try {
							JDBCConnector jdbc = new JDBCConnector();
							for(Study study : modeleExportStudies.getStudiesList()){
								StringBuilder sb =connexionHttp.makeGetConnectionAndStringBuilder("/studies/" + study.getId() +"/statistics");
								JSONObject cdfResponseStats = (JSONObject) parser.parse(sb.toString());
								
								String size;
								
								if (cdfResponseStats.containsKey("DiskSizeMB")) {
									size=cdfResponseStats.get("DiskSizeMB").toString();
								}else {
									size="";
								}
								
								if(!jdbc.sendSizeAndNewUID(study.getPatientName(), size, study.getNewStudyInstanceUID())){
									dataSent = false;
								}
								if(remoteFileName != null){
									if(!jdbc.sendFileName(study.getPatientName(), remoteFileName.toString())){
										dataSent = false;										
									}
								}
							}
							remoteFileName = null;
							jdbc.disconnect();
							if(!dataSent){
								stateExports.setText("<html><font color = 'red'>The report was not sent to the database</font></html>");
							}else{
								stateExports.setText("<html><font color = 'green'>The report was sent to the database</font></html>");
							}
						} catch (ClassNotFoundException | SQLException |org.json.simple.parser.ParseException e1) {
							e1.printStackTrace();
						} 
					}*/
				}
			}
		});

		try {
			QueryFillStore query = new QueryFillStore(connexionHttp);

			JButton exportToZip = new JButton("Zip");
			exportToZip.addActionListener(new ActionListener() {
				boolean confirm = true;
				@Override
				public void actionPerformed(ActionEvent e) {
					SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() {
							confirm = true;
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
							if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
								file = chooser.getSelectedFile();
							}else{
								confirm = false;
							}
							if(confirm){
								try{
									ConvertZipAction convertzip=new ConvertZipAction(connexionHttp);
									convertzip.setConvertZipAction(file.getAbsolutePath().toString(), modeleExportStudies.getOrthancIds(), false);
									convertzip.generateZip(false);
								}catch(IOException e){
									e.printStackTrace();
								}
							}
							return null;
						}

						@Override
						public void done(){
							if(confirm){
								stateExports.setText("<html><font color='green'>The data has been successfully been converted to zip</font></html>");
							}else{
								stateExports.setText("");
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


			JComboBox<Object> listeAETExport = new JComboBox<Object>(query.getAET());
			JButton storeExport = new JButton("Store");
			storeExport.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() {
							try {
								storeExport.setEnabled(false);
								storeExport.setText("Storing...");
								query.store(listeAETExport.getSelectedItem().toString(), modeleExportStudies.getOrthancIds());
							} catch (IOException e1) {
								stateExports.setText("<html><font color= 'red'>The request was not received (" + e1.getMessage() + ") </font></html>");
							}
							return null;
						}

						@Override
						protected void done(){
							stateExports.setText("<html><font color= 'green'>The request was successfully received</font></html>");
							storeExport.setText("Store");
							storeExport.setEnabled(true);
						}
					};
					if(!modeleExportStudies.getOrthancIds().isEmpty()){
						stateExports.setText("Storing data...");
						worker.execute();
					}
				}
			});

			JComboBox<Object> listePeers = new JComboBox<Object>(query.getPeers());
			JButton peerExport = new JButton("OrthancPeer");
			peerExport.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() {
							peerExport.setEnabled(false);
							peerExport.setText("Sending...");
							boolean sendok=true;
							try {
								query.sendPeer(listePeers.getSelectedItem().toString(), modeleExportStudies.getOrthancIds());
							} catch (IOException e1) {
								sendok=false;
								stateExports.setText("<html><font color= 'red'>The upload was not received (" + e1.getMessage() + ") </font></html>");
							}
							if (sendok) stateExports.setText("<html><font color= 'green'>The upload was successfully received</font></html>");
							
							return null;
						}

						@Override
						protected void done(){
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

			exportPanel.add(reportType);
			exportPanel.add(reportBtn);
			JLabel dummyLabel0 = new JLabel("");
			dummyLabel0.setBorder(new EmptyBorder(0,0,0,50));
			exportPanel.add(dummyLabel0);
			exportPanel.add(exportToZip);
			JLabel dummyLabel1 = new JLabel("");
			dummyLabel1.setBorder(new EmptyBorder(0,0,0,50));
			exportPanel.add(dummyLabel1);
			exportPanel.add(exportBtn);
			JLabel dummyLabel2 = new JLabel("");
			dummyLabel2.setBorder(new EmptyBorder(0,0,0,50));
			exportPanel.add(dummyLabel2);
			exportPanel.add(listeAETExport);
			exportPanel.add(storeExport);
			JLabel dummyLabel3 = new JLabel("");
			dummyLabel3.setBorder(new EmptyBorder(0,0,0,50));
			exportPanel.add(dummyLabel3);
			exportPanel.add(listePeers);
			exportPanel.add(peerExport);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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

		this.bodyCharChoice = Choice.KEEP;
		this.datesChoice = Choice.KEEP;
		this.bdChoice = Choice.REPLACE;
		this.ptChoice = Choice.CLEAR;
		this.scChoice = Choice.CLEAR;
		this.descChoice = Choice.CLEAR;

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
		anonProfiles = new JComboBox<Object>(new String[]{"Default", "Full clearing", "Custom"});
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
		this.bodyCharList[0] = radioBodyCharac1;
		this.bodyCharList[1] = radioBodyCharac2;
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
		this.datesList[0] = radioDates1;
		this.datesList[1] = radioDates2;
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
		this.bdList[0] = radioBd1;
		this.bdList[1] = radioBd2;
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
		this.ptList[0] = radioPt1;
		this.ptList[1] = radioPt2;
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
		this.scList[0] = radioSc1;
		this.scList[1] = radioSc2;
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
		this.descList[0] = radioDesc1;
		this.descList[1] = radioDesc2;
		bgDesc.add(radioDesc1);
		gbSetup.gridx = 1;
		tabSetup.add(radioDesc1, gbSetup);
		bgDesc.add(radioDesc2);
		gbSetup.gridx = 2;
		tabSetup.add(radioDesc2, gbSetup);

		JLabel profileLabel = new JLabel();

		anonProfiles.addActionListener(
				new AnonActionProfileListener(anonProfiles, profileLabel, radioBodyCharac1, 
						radioBodyCharac2, radioDates1, radioDates2, radioBd2, 
						radioBd1, radioPt1, radioPt2, radioSc1, radioSc2, radioDesc1, radioDesc2));

		anonProfiles.setSelectedItem(jprefer.get("profileAnon", "Default"));

		// Showing the currently selected profile in the main panel
		gbBtnPanel.gridy = 3;
		anonBtnPanelRight.add(profileLabel, gbBtnPanel);

		JTabbedPane eastSetupPane = new JTabbedPane();
		eastSetupPane.add("Export setup", eastExport);
		eastSetupPane.addTab("Other", clinicalTrialProcessor);

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
		JLabel address=new JLabel("Address");
		addressFieldCTP=new JTextField();
		addressFieldCTP.setToolTipText("Include http:// or https://");
		addressFieldCTP.setPreferredSize(new Dimension(300,20));
		addressFieldCTP.setText(jprefer.get("CTPAddress", "http://"));
		clinicalTrialProcessorGrid.add(address);
		clinicalTrialProcessorGrid.add(addressFieldCTP);
		
		
		JPanel aboutPanel = new JPanel(new FlowLayout());
		JButton viewerDistribution = new JButton("Download Viewer Distribution");
		
		viewerDistribution.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Select folder for CD/DVD output");
				chooser.setSelectedFile(new File("ImageJ.zip"));
				chooser.setDialogTitle("Dowload Viewer to...");
				if (! jpreferPerso.get("viewerDistribution", "empty").equals("empty") ) {
					chooser.setSelectedFile(new File (jpreferPerso.get("viewerDistribution", "empty")));
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
							jpreferPerso.put("viewerDistribution", chooser.getSelectedFile().toString());
							viewerDistribution.setBackground(null);
						}
					};
					worker.execute();
				}
				

				}
				
			
		});
		
		//Setup button only for starting outside Fiji
		setupButton = new JButton("Orthanc HTTP Setup");
		setupButton.setVisible(false);
		
		setupButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConnectionSetup.main();
				
			}
			
		});
		
		JButton aboutBtn = new JButton("About us");
		aboutBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutBoxFrame ab = new AboutBoxFrame();
				ab.setLocationRelativeTo(gui);
				ab.pack();
				ab.setVisible(true);
			}
		});
		
		aboutPanel.add(viewerDistribution);
		aboutPanel.add(setupButton);
		aboutPanel.add(aboutBtn);
		
		if( ! addressFieldCTP.getText().equals("http://")){
			reportType.addItem("CTP");
			reportType.setSelectedItem(jprefer.get("reportType", "CSV"));
		}
		
		if( addressFieldCTP.getText().equals("http://")){
			setNamesIdBtn.setVisible(false);
		}
		
		mainPanelSetup.add(westSetup, BorderLayout.WEST);
		mainPanelSetup.add(eastSetupPane, BorderLayout.EAST);
		mainPanelSetup.add(aboutPanel, BorderLayout.SOUTH);

		JPanel p3 = new JPanel(new FlowLayout());

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////// END TAB 3 : SETUP //////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		monitoring = new Monitoring_GUI(connexionHttp);
		JPanel panelMonitoring = (JPanel) monitoring.getContentPane();
		
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////// TAB 4 : Monitor //////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
				
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////// END TAB 4 : Monitor //////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				//////////// Filling the user preferences ////////////
				if(anonProfiles.getSelectedItem().equals("Custom")){
					for(int i = 0; i < 2; i++){
						if(bodyCharList[i].isSelected()){
							if(jprefer.getInt("bodyCharac", 0) != i){
								jprefer.putInt("bodyCharac", i);
							}
						}	
					}
					for(int i = 0; i < 2; i++){
						if(datesList[i].isSelected()){
							if(jprefer.getInt("Dates", 0) != i){
								jprefer.putInt("Dates", i);
							}
						}
					}
					for(int i = 0; i < 2; i++){
						if(bdList[i].isSelected()){
							if(jprefer.getInt("BD", 0) != i){
								jprefer.putInt("BD", i);
							}
						}
					}
					for(int i = 0; i < 2; i++){
						if(ptList[i].isSelected()){
							if(jprefer.getInt("PT", 0) != i){
								jprefer.putInt("PT", i);
							}
						}
					}
					for(int i = 0; i < 2; i++){
						if(scList[i].isSelected()){
							if(jprefer.getInt("SC", 0) != i){
								jprefer.putInt("SC", i);
							}
						}
					}
					for(int i = 0; i < 2; i++){
						if(descList[i].isSelected()){
							if(jprefer.getInt("DESC", 0) != i){
								jprefer.putInt("DESC", i);
							}
						}
					}
				}
				jprefer.put("profileAnon", anonProfiles.getSelectedItem().toString());
				jprefer.put("centerCode", centerCode.getText());
				jprefer.put("CTPAddress", addressFieldCTP.getText());
				
				// Putting the export preferences in the anon plugin registry
				if(remoteServer.getText() != null){
					jprefer.put("remoteServer", remoteServer.getText());
				}
				if(remotePort.getText() != null){
					jprefer.put("remotePort", remotePort.getText());
				}
				if(servUsername.getText() != null){
					jprefer.put("servUsername", servUsername.getText());
				}
				if(new String(servPassword.getPassword()) != null){
					jprefer.put("servPassword", new String(servPassword.getPassword()));
				}
				if(remoteFilePath.getText() != null){
					jprefer.put("remoteFilePath", remoteFilePath.getText());
				}
				jprefer.put("exportType", exportType.getSelectedItem().toString());
				
				if(addressFieldCTP.getText().isEmpty()  ){
					reportType.removeAllItems();
					reportType.addItem("CSV");
				}else{
					reportType.removeAllItems();
					reportType.addItem("CSV");
					reportType.addItem("CTP");
				}

				if(	addressFieldCTP.getText().equals("http://") ){
					setNamesIdBtn.setVisible(false);
				}else{
					setNamesIdBtn.setVisible(true);
				}
				
				if(remoteServer.getText().length() == 0){
					exportBtn.setEnabled(false);
				}else{
					exportBtn.setEnabled(true);
				}
				/*if(tabbedPane.getSelectedIndex() == 0){
					if(!modeleAnonStudies.getOldOrthancUIDs().isEmpty()){
						anonTablesPanel.setVisible(true);
						displayAnonTool.setText("Close anonymization tool");
						displayExportTool.setVisible(false);
						addToAnon.setVisible(true);
					}
				}else{
					displayAnonTool.setText("Anonymize");
					openCloseAnonTool(false);
					pack();
				}*/
				pack();
			}
		});

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
		this.getRootPane().setDefaultButton(search);
		this.addWindowListener(new CloseWindowAdapter(this, this.zipContent, this.modeleAnonStudies.getOldOrthancUIDs(), this.modeleExportStudies.getStudiesList(), monitoring));
	}
	
	private void openCloseAnonTool(boolean open) {
		if (open) {
			oToolRight.setVisible(false);
			anonTablesPanel.setVisible(true);
			addToAnon.setVisible(true);
			displayExportTool.setVisible(false);
			displayManageTool.setVisible(false);
			displayAnonTool.setText("Close anonymization tool");
		}
		else {
			anonTablesPanel.setVisible(false);
			addToAnon.setVisible(false);
			displayAnonTool.setText("Anonymize");
			displayExportTool.setVisible(true);
			displayManageTool.setVisible(true);
		}
		
	}
	
	private void openCloseExportTool(boolean open) {
		if (open) {
			oToolRight.setVisible(true);
			displayExportTool.setText("Close Export Tool");
			displayAnonTool.setVisible(false);
			displayManageTool.setVisible(false);
		}
		else {
			oToolRight.setVisible(false);
			displayExportTool.setText("Export");
			displayAnonTool.setVisible(true);
			displayManageTool.setVisible(true);
		}
	}
	
	private void openCloseManageTool(boolean open) {
		if (open) {
			oToolRightManage.setVisible(true);
			displayManageTool.setText("Close Manage Tool");
			displayAnonTool.setVisible(false);
			displayExportTool.setVisible(false);
		}
		else {
			oToolRightManage.setVisible(false);
			displayManageTool.setText("Manage");
			displayAnonTool.setVisible(true);
			displayExportTool.setVisible(true);
		}
	}
	
	private void enableManageButtons(boolean enable) {
		if (enable) {
			addManage.setEnabled(true);
			removeFromManage.setEnabled(true);
		}
		else {
			addManage.setEnabled(false);
			removeFromManage.setEnabled(false);
		}
		
	}
	// Ajoute seletion a la tool list
	private void addToToolList(ArrayList<String> zipContent, JComboBox<Object> zipShownContent, ArrayList<String > zipShownContentList, JLabel zipSize){

		//On recupere la table qui a eu le dernier focus pour la selection
		JTable tableau =  lastTableFocus;
		int[] selectedLines=tableau.getSelectedRows();
		boolean duplicate = false;
		if(tableau.equals(tableauPatients)){
			for (int i=0; i<selectedLines.length; i++){
				String name = "Patient - " + tableauPatients.getValueAt(selectedLines[i], 0).toString();
				String id = tableauPatients.getValueAt(selectedLines[i], 2).toString();
				if(!zipContent.contains(id)){
					zipShownContent.addItem(name);
					zipShownContentList.add(name);
					zipContent.add(id);
				}else{
					duplicate=true;
					
				}
			}
			
		}else if(tableau.equals(tableauStudies)){
			for (int i=0; i<selectedLines.length; i++){
				String date = "Study - " + df.format(((Date)tableauStudies.getValueAt(selectedLines[i], 0))) + "  " + tableauStudies.getValueAt(selectedLines[i], 1);
				String id = tableauStudies.getValueAt(selectedLines[i], 3).toString();
				if(!zipContent.contains(id)){
					zipShownContent.addItem(date);
					zipShownContentList.add(date);
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
					zipShownContentList.add(desc);
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
	
	// remove ligne active a la tool list
	private void removeFromToolList(ArrayList<String> zipContent, JComboBox<Object> zipShownContent, ArrayList<String> zipShownContentList, JLabel zipSize, JLabel state){
		if(!zipContent.isEmpty()){
			zipContent.remove(zipShownContent.getSelectedIndex());
			zipShownContentList.remove(zipShownContent.getSelectedIndex());
			zipShownContent.removeAllItems();
			for(String s : zipShownContentList){
			zipShownContent.addItem(s);
			}
			if(zipContent.size() >= 1){
				zipSize.setText(zipContent.size() + " element(s)");
			}else{
				state.setText("");
				zipSize.setText(" Empty List");
				pack();
			}
		}
	}
	
	

	// Anonymization query
	private class AnonAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		int dialogResult = JOptionPane.YES_OPTION;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			dialogResult = JOptionPane.YES_OPTION;
			anonCount = 0;
			SwingWorker<Void,Void> workerRemoveScAndSr = new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() {
					try {
						modeleAnonStudies.removeScAndSr();
					} catch(FileNotFoundException e){
						// Ignore
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() {
					//////////// Filling the user preferences ////////////
					if(anonProfiles.getSelectedItem().equals("Custom")){
						for(int i = 0; i < 2; i++){
							if(bodyCharList[i].isSelected()){
								if(jprefer.getInt("bodyCharac", 0) != i){
									jprefer.putInt("bodyCharac", i);
								}
							}	
						}
						for(int i = 0; i < 2; i++){
							if(datesList[i].isSelected()){
								if(jprefer.getInt("Dates", 0) != i){
									jprefer.putInt("Dates", i);
								}
							}
						}
						for(int i = 0; i < 2; i++){
							if(bdList[i].isSelected()){
								if(jprefer.getInt("BD", 0) != i){
									jprefer.putInt("BD", i);
								}
							}
						}
						for(int i = 0; i < 2; i++){
							if(ptList[i].isSelected()){
								if(jprefer.getInt("PT", 0) != i){
									jprefer.putInt("PT", i);
								}
							}
						}
						for(int i = 0; i < 2; i++){
							if(scList[i].isSelected()){
								if(jprefer.getInt("SC", 0) != i){
									jprefer.putInt("SC", i);
								}
							}
						}
						for(int i = 0; i < 2; i++){
							if(descList[i].isSelected()){
								if(jprefer.getInt("DESC", 0) != i){
									jprefer.putInt("DESC", i);
								}
							}
						}
					}

					jprefer.put("profileAnon", anonProfiles.getSelectedItem().toString());

					anonBtn.setEnabled(false);
					setNamesIdBtn.setEnabled(false);
					addToAnon.setEnabled(false);
					removeFromAnonList.setEnabled(false);

					anonBtn.setText("Anonymizing");
					// SETTING UP THE CHOICES
					for(int i = 0; i < 2; i++){
						if(i == 0){
							if(bodyCharList[i].isSelected())
								bodyCharChoice = Choice.KEEP;
							if(datesList[i].isSelected())
								datesChoice = Choice.KEEP;
							if(bdList[i].isSelected())
								bdChoice = Choice.KEEP;
							if(ptList[i].isSelected())
								ptChoice = Choice.KEEP;
							if(scList[i].isSelected())
								scChoice = Choice.KEEP;
							if(descList[i].isSelected())
								descChoice = Choice.KEEP;
						}else{
							if(bodyCharList[i].isSelected())
								bodyCharChoice = Choice.CLEAR;
							if(datesList[i].isSelected())
								datesChoice = Choice.CLEAR;
							if(bdList[i].isSelected())
								bdChoice = Choice.REPLACE;
							if(ptList[i].isSelected())
								ptChoice = Choice.CLEAR;
							if(scList[i].isSelected())
								scChoice = Choice.CLEAR;
							if(descList[i].isSelected())
								descChoice = Choice.CLEAR;					
						}
					}

					int i = 0;
					int j = 0;
					try {
						if(anonProfiles.getSelectedItem().equals("Full clearing")){
							if(modeleAnonStudies.getModalities().contains("NM") || 
									modeleAnonStudies.getModalities().contains("PT")){
								dialogResult = JOptionPane.showConfirmDialog (gui, 
										"Full clearing is not recommended for NM or PT modalities."
												+ "Are you sure you want to anonymize ?",
												"Warning anonymizing PT/NM",
												JOptionPane.WARNING_MESSAGE,
												JOptionPane.YES_NO_OPTION);
							}
						}
						if(modeleAnonStudies.getModalities().contains("US")){
							JOptionPane.showMessageDialog (gui, 
									"DICOM files with the US modality may have hard printed informations, "
											+ "you may want to check your files.",
											"Warning anonymizing US",
											JOptionPane.WARNING_MESSAGE);
						}
						
						// Checking if several anonymized patients have the same ID or not
						boolean similarIDs = false;
						ArrayList<String> newIDs = new ArrayList<String>();
						for(int n = 0; n < anonPatientTable.getRowCount(); n++){
							String newID = modeleAnonPatients.getPatient(anonPatientTable.convertRowIndexToModel(n)).getNewID();
							if(newID != "" && !newIDs.contains(newID)){
								newIDs.add(newID);
							}else if(newIDs.contains(newID)){
								similarIDs = true;
							}
						}
						if(similarIDs){
							dialogResult = JOptionPane.showConfirmDialog (gui, 
									"You have defined 2 or more identical IDs for anonymized patients, which is not recommended."
											+ " Are you sure you want to anonymize ?",
											"Warning similar IDs",
											JOptionPane.WARNING_MESSAGE,
											JOptionPane.YES_NO_OPTION);
						}
						
						if(dialogResult == JOptionPane.YES_OPTION){

							String substituteName = "A-" + jprefer.get("centerCode", "12345");

							SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
							String substituteID = "A-" + df.format(new Date());

							for(String patientID : modeleAnonStudies.getPatientIDs()){
								String newName = modeleAnonPatients.getPatient(anonPatientTable.convertRowIndexToModel(j)).getNewName();
								String newID = modeleAnonPatients.getPatient(anonPatientTable.convertRowIndexToModel(j)).getNewID();
								String newUID = "";
								if((newName == null || newName.equals("")) || (newID == null || newID.equals(""))){
									anonCount++;
								}
								if(newName == null || newName.equals("")){
									newName = substituteName + "^" + anonCount;
									modeleAnonPatients.setValueAt(newName, anonPatientTable.convertRowIndexToModel(j), 3);
								}

								if(newID == null || newID.equals("")){
									newID = substituteID + "^" + anonCount;
									modeleAnonPatients.setValueAt(newID, anonPatientTable.convertRowIndexToModel(j), 4);
								}

								for(String uid : modeleAnonStudies.getOldOrthancUIDsWithID(patientID)){
									String newDesc = modeleAnonStudies.getNewDesc(uid);
									QueryAnon quAnon;
									quAnon = new QueryAnon(connexionHttp, bodyCharChoice, datesChoice, bdChoice, ptChoice, scChoice, descChoice, newName, newID, newDesc);
									quAnon.setQuery();
									state.setText("<html>Anonymization state - " + (i+1) + "/" + modeleAnonStudies.getStudies().size() + 
											" <font color='red'> <br>(Do not use the toolbox while the current operation is not done)</font></html>");
									quAnon.sendQuery("studies", uid);
									modeleAnonStudies.addNewUid(quAnon.getNewUID());
									i++;
									newUID = quAnon.getNewPatientUID();
								}
								//SK A VOIR CETTE METHODE CF REMARQUE DANS TABLEDATAEXPORTSTUDIES
								modeleExportStudies.addStudy(newName, newID, newUID);
								j++;
							}
							//SK AJOUTE A TESTER RISQUE BUG
							modeleAnonStudies.empty();
							modeleAnonPatients.clear();
							
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void done(){
					if(scList[1].isSelected()){
						workerRemoveScAndSr.execute();
					}
					anonBtn.setEnabled(true);
					addToAnon.setEnabled(true);
					setNamesIdBtn.setEnabled(true);
					removeFromAnonList.setEnabled(true);
					anonBtn.setText("Anonymize");
					if(dialogResult == JOptionPane.YES_OPTION){
						state.setText("<html><font color='green'>The data has successfully been anonymized.</font></html>");
						openCloseAnonTool(false);
						pack();
						tabbedPane.setSelectedIndex(1);
						modeleAnonPatients.clear();
						modeleAnonStudies.empty();
						
					}
					if(tableauExportStudies.getRowCount() > 0){
						tableauExportStudies.setRowSelectionInterval(tableauExportStudies.getRowCount() - 1, tableauExportStudies.getRowCount() - 1);
					}
					modeleExportSeries.clear();
					try {
						if(modeleExportStudies.getRowCount() > 0){
							String studyID = (String)tableauExportStudies.getValueAt(tableauExportStudies.getSelectedRow(), 5);
							modeleExportSeries.addSerie(studyID);
							tableauExportSeries.setRowSelectionInterval(0,0);
						}
					} catch (Exception e1) {
						// IGNORE
					}
				}
			};
			if(!modeleAnonStudies.getOldOrthancUIDs().isEmpty()){
				if(dialogResult == JOptionPane.YES_OPTION){
					worker.execute();
				}
			}
		}
	}
	
	// LAUNCHERS
	public static void main(String... args){
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		VueAnon vue = new VueAnon();
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				vue.pack();
				vue.setupButton.setVisible(true);
				vue.setLocationRelativeTo(null);
				vue.setVisible(true);
				
			}
		});
	}

	@Override
	public void run(String string) {
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				VueAnon vue = new VueAnon();
				vue.pack();
				vue.setLocationRelativeTo(null);
				WindowManager.addWindow(gui);
				IJ.register(VueAnon.class);
				vue.setVisible(true);
			}
		
		});
	}
		
		
	
}
