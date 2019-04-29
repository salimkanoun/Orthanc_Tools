package org.petctviewer.orthanc.OTP.standalone;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.OTP.OTP_Gui;
import org.petctviewer.orthanc.anonymize.AnonRequest;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.anonymize.datastorage.Study_Anonymized;
import org.petctviewer.orthanc.anonymize.datastorage.Tags.Choice;
import org.petctviewer.orthanc.importdicom.ImportDCM;
import org.petctviewer.orthanc.importdicom.ImportListener;

public class OTP_Tab extends JPanel implements ImportListener, ListSelectionListener{
	
	private static final long serialVersionUID = 1L;
	private JTable tableStudy;
	private JTable tableSeries;
	private ImportDCM importFrame;
	private OTP_Tab guiOTP=this;
	private JLabel lblStatusOTP;
	private VueAnon anon;
	
	/**
	 * Create the frame.
	 */
	public OTP_Tab(VueAnon anon) {
		setLayout(new BorderLayout(0, 0));
		this.anon=anon;
		JPanel panel_north_main = new JPanel();
		add(panel_north_main, BorderLayout.NORTH);
		
		JButton btnImport = new JButton("Load Local DICOM");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					importFrame=new ImportDCM(anon.getOrthancApisConnexion(), anon);
					importFrame.setImportListener(guiOTP);
					importFrame.setModal(true);
					importFrame.setVisible(true);
					
				}
		});
		panel_north_main.add(btnImport);
		
		JPanel panel_center_main = new JPanel();
		add(panel_center_main, BorderLayout.CENTER);
		panel_center_main.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_grid_center = new JPanel();
		panel_center_main.add(panel_grid_center, BorderLayout.CENTER);
		panel_grid_center.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel panel_studies = new JPanel();
		panel_grid_center.add(panel_studies);
		panel_studies.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_Study = new JScrollPane();
		panel_studies.add(scrollPane_Study);
		
		tableStudy = new JTable();
		tableStudy.setModel(new TableOTPStudiesModel());
		tableStudy.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableStudy.getSelectionModel().addListSelectionListener(this);
		tableStudy.setDefaultRenderer(String.class, new MyTableCellRenderer());
		tableStudy.setDefaultRenderer(Date.class, new MyTableCellRenderer());
		tableStudy.setDefaultRenderer(Study2.class, new MyTableCellRenderer());
		
		tableStudy.getColumnModel().getColumn(7).setMinWidth(0);
		tableStudy.getColumnModel().getColumn(7).setMaxWidth(0);
		tableStudy.getColumnModel().getColumn(8).setMinWidth(0);
		tableStudy.getColumnModel().getColumn(8).setMaxWidth(0);
		
		scrollPane_Study.setViewportView(tableStudy);
		
		JPanel panel_button_query = new JPanel();
		panel_studies.add(panel_button_query, BorderLayout.SOUTH);
		panel_button_query.setLayout(new BorderLayout(0, 0));
		
		JButton btnNewButton = new JButton("Query Anon Key");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(tableStudy.getRowCount()>0){
					// Si pas de study selectionnees on selectionne de force le 1er
					if (tableStudy.getSelectedRow()==-1) tableStudy.setRowSelectionInterval(0, 0);

					Study2 studyAnon=(Study2) tableStudy.getValueAt(tableStudy.getSelectedRow(), 8);
					//On prepare les donnees locales dans l'objet
					String patientName=studyAnon.getPatientName();
					String patientSex=studyAnon.getPatientSex();
					Date studyDate=studyAnon.getDate();
					Date patientDOB=studyAnon.getPatientDob();
					DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
					//envoi des donnes dans objet GUI pour CTP
					//On genere l'objet qui gere le CTP
					OTP_Gui dialog = new OTP_Gui(anon.getCTPaddress());
					dialog.setStudyLocalValue(patientName, df.format(studyDate), patientSex, df.format(patientDOB));
					if(anon.getCTPUsername()!=null) {
						dialog.setLogin(anon.getCTPLogin());
						dialog.setPassword(anon.getCTPPassword());
					}
					dialog.pack();
					dialog.setModal(true);
					dialog.setLocationRelativeTo(anon);
					dialog.setVisible(true);
					//On recupere les donnees et on met dans l'anonymisation
					if(dialog.getOk()) {
						System.out.println("ok");
						//Change autoSend boolean to get the automatic send at the anonymize button click
						anon.autoSendCTP=true;
						anon.setCTPUsername(dialog.getLogin());
						anon.setCTPPassword(dialog.getPassword());
						anon.setOrthancPeerOTP(dialog.getOrthancServerReciever());
						//Add new value to table
						tableStudy.setValueAt(dialog.getAnonName(), tableStudy.getSelectedRow(), 4);
						tableStudy.setValueAt(dialog.getAnonID(), tableStudy.getSelectedRow(), 5);
						tableStudy.setValueAt(dialog.getVisitName(), tableStudy.getSelectedRow(), 6);
						//Update display of all the row for color feedback
						((AbstractTableModel) tableStudy.getModel()).fireTableRowsUpdated(tableStudy.getSelectedRow(), tableStudy.getSelectedRow());
						//If only One patient and one study in the list, click the anonymize button to start the process
						if (!isMissingChoice()) {
							anonymize();
						}
					}

				}
				
			}
		});
		panel_button_query.add(btnNewButton, BorderLayout.NORTH);
		
		lblStatusOTP = new JLabel("Status : Idle");
		panel_button_query.add(lblStatusOTP, BorderLayout.SOUTH);

		JScrollPane scrollPane_Series = new JScrollPane();
		panel_grid_center.add(scrollPane_Series);
		
		tableSeries = new JTable();
		tableSeries.setModel(new TableOTPSeriesModel(guiOTP, anon.getOrthancApisConnexion(), anon.getOrthancQuery()));

		tableSeries.getColumnModel().getColumn(5).setMinWidth(0);
		tableSeries.getColumnModel().getColumn(5).setMaxWidth(0);
		tableSeries.getColumnModel().getColumn(6).setMinWidth(0);
		tableSeries.getColumnModel().getColumn(6).setMaxWidth(0);
		scrollPane_Series.setViewportView(tableSeries);

		JPopupMenu popMenuSelectSeries = new JPopupMenu();
		JMenuItem menuItemDeleteSeries = new JMenuItem("Delete Serie");
		popMenuSelectSeries.add(menuItemDeleteSeries);
		popMenuSelectSeries.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = tableSeries.rowAtPoint(SwingUtilities.convertPoint(tableSeries, MouseInfo.getPointerInfo().getLocation() , tableSeries));
                        if (rowAtPoint > -1) {
                        	tableSeries.setRowSelectionInterval(rowAtPoint, rowAtPoint);
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
		
		menuItemDeleteSeries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				anon.getOrthancApisConnexion().makeDeleteConnection("/series/"+tableSeries.getValueAt(tableSeries.getSelectedRow(), 5));
				((TableOTPSeriesModel) tableSeries.getModel()).refresh();
			}
		});
		
		JMenuItem menuItemDeleteSeriesSC = new JMenuItem("Delete All Secondary Captures");
		popMenuSelectSeries.add(menuItemDeleteSeriesSC);
		
		menuItemDeleteSeriesSC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((TableOTPSeriesModel) tableSeries.getModel()).removeAllSecondaryCaptures();
			}
		});
		
		tableSeries.setComponentPopupMenu(popMenuSelectSeries);
		
		
		JPopupMenu popMenuSelectStudies = new JPopupMenu();
		JMenuItem menuItemRemoveStudy = new JMenuItem("Remove Study");
		popMenuSelectStudies.add(menuItemRemoveStudy);
		popMenuSelectStudies.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = tableStudy.rowAtPoint(SwingUtilities.convertPoint(tableStudy, MouseInfo.getPointerInfo().getLocation() , tableStudy));
                        if (rowAtPoint > -1) {
                        	tableStudy.setRowSelectionInterval(rowAtPoint, rowAtPoint);
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
		
		
		menuItemRemoveStudy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((DefaultTableModel)tableStudy.getModel()).removeRow(tableStudy.convertRowIndexToModel(tableStudy.getSelectedRow()));
				((TableOTPSeriesModel)tableSeries.getModel()).clear();
			}
		});
		
		tableStudy.setComponentPopupMenu(popMenuSelectStudies);
	}

	@Override
	public void ImportFinished(HashMap<String, Study2> importedStudy) {
		HashMap<String, Study2> studies=importFrame.getImportedStudy();
		for(String study:studies.keySet()) {
			Study2 studyObj=studies.get(study);
			((TableOTPStudiesModel) tableStudy.getModel()).addStudy(studyObj);
		}
		importFrame.dispose();
	}
	
	public JLabel getStatusOTP() {
		return lblStatusOTP;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(tableStudy.getRowCount() != 0 && tableStudy.getSelectedRow() !=(-1)){
			// We clear the details
			TableOTPSeriesModel seriesModel=(TableOTPSeriesModel) tableSeries.getModel();
			int selectedRow =this.tableStudy.getSelectedRow();
			Study2 study=(Study2) tableStudy.getValueAt(selectedRow, 8);
			seriesModel.clear();
			seriesModel.addSerie(study);
		}	
		
	}
	
	/**
	 * Return if there are studies still awaiting for Anon Key
	 * @return
	 */
	private boolean isMissingChoice() {
		
		for(int i=0; i<tableStudy.getRowCount(); i++) {
			if(tableStudy.getValueAt(i, 5).equals("")) {
				return true;
			}
		}
		return false;
	}
	
	private void anonymize() {
		SwingWorker<Void, Void> worker=new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				//Define Anon profile (equivalent to default profile), here forced
				Choice bodyCharChoice=Choice.KEEP,
						datesChoice=Choice.KEEP,
						bdChoice=Choice.CLEAR,
						ptChoice=Choice.CLEAR,
						scChoice=Choice.CLEAR,
						descChoice =Choice.KEEP;
				
				
				int anonCount=0;
				//SK AJOUT ANALYSE DE LA TABLE AVANT ANONYMISATION
				for(int i=0 ; i<tableStudy.getRowCount(); i++) {
					
						lblStatusOTP.setText("Anonymizing study "+(anonCount+1)+"/"+tableStudy.getRowCount());
						
						String patientNewName=(String) tableStudy.getValueAt(tableStudy.getSelectedRow(), 4);
						String patentNewId=(String) tableStudy.getValueAt(tableStudy.getSelectedRow(), 5);
						String patientNewStudyDesc=(String) tableStudy.getValueAt(tableStudy.getSelectedRow(), 6);
						String studyId=(String) tableStudy.getValueAt(tableStudy.getSelectedRow(), 7);
						Study2 studyToAnon=(Study2) tableStudy.getValueAt(tableStudy.getSelectedRow(), 8);
						
						//Prepare the Anon Request
						AnonRequest anonRequest= new AnonRequest(anon.getOrthancApisConnexion(), bodyCharChoice, datesChoice, bdChoice, 
								ptChoice, scChoice, descChoice, 
								patientNewName, patentNewId, patientNewStudyDesc);
						//Start the Anonymization
						anonRequest.sendQuery(studyId);
						
						Study2 anonymizedStudy = anon.getOrthancQuery().getStudyDetails(anonRequest.getNewOrthancID(), true);
						
						//Remove all SC in the anonymized study
						anonymizedStudy.deleteAllSc(anon.getOrthancApisConnexion());
						anonymizedStudy.refreshChildSeries(anon.getOrthancQuery());
												
						//Create object with the Anonymized Study and old study object
						Study_Anonymized anonymizedStudyResult=new Study_Anonymized(anonymizedStudy, studyToAnon);
						anon.modeleExportStudies.addStudy(anonymizedStudyResult);
						
						anonCount++;
						
					}
				return null;
			}
			
			@Override
			public void done() {
				try {
					this.get();
					anon.tabbedPane.setSelectedIndex(1);
					((TableOTPStudiesModel)tableStudy.getModel()).clear();
					((TableOTPSeriesModel)tableSeries.getModel()).clear();
					anon.getExportCTPbtn().doClick();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				
			}
			
		};
		worker.execute();

		
	}
	
	
	
	private class MyTableCellRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = 1L;

		@Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	TableOTPStudiesModel model = (TableOTPStudiesModel) table.getModel();
	        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        if(!model.getValueAt(row, 4).equals("")) {
	        	 c.setBackground(Color.GREEN);
	        }
	       
	        return c;
	    }
	}

}

// TO DO
// Listener de Series pour Renommer Series Description
// Gerer demarrage auto Orthanc differment ?
// Vider ligne study quand delete deniere serie possible
// Dans export tab ne laisser que OTP pour envoi
//Boutton Anon ? ou lancement auto ? => voir avec groupe d'utilisateur
