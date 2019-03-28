package org.petctviewer.orthanc.OTP.standalone;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.petctviewer.orthanc.anonymize.QueryOrthancData;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.anonymize.listeners.AnonymizeListener;
import org.petctviewer.orthanc.importdicom.ImportDCM;
import org.petctviewer.orthanc.importdicom.ImportListener;

public class OTP_Import_GUI extends VueAnon implements ImportListener, AnonymizeListener {
	
	//SK DEFINITION DES PEERS A TENTER EN API
	private static final long serialVersionUID = 1L;
	private ImportDCM importFrame;
	private OTP_Import_GUI importGUI=this;
	
	
	public OTP_Import_GUI() {
		super("OrthancCTP.json");
		
		this.setAnonymizeListener(this);
		//Make a simplified version of Orthanc Tools
		tablesPanel.setVisible(false);
		topPanel.setVisible(false);
		anonBtnPanelTop.setVisible(false);
		importCTP.setVisible(true);
		
		peerExport.setVisible(false);
		csvReport.setVisible(false);
		exportToZip.setVisible(false);
		exportRemoteBtn.setVisible(false);
		dicomStoreExport.setVisible(false);
		getExportCTPbtn().setText("Send");
		
		listePeers.setVisible(false);
		listeAETExport.setVisible(false);
		
		setCTPaddress("https://kanoun.fr/");
		
		queryCTPBtn.setVisible(true);
		
		importCTP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				importFrame=new ImportDCM(getOrthancApisConnexion(), importGUI);
				importFrame.setImportListener(importGUI);
				importFrame.setModal(true);
				importFrame.setVisible(true);
				
			}
			
		});
		anonBtn.setText("Send");
		openCloseAnonTool(true);
		tabbedPane.remove(2);
		listePeersCTP.setSelectedIndex(1);
		
		//PopUp menu for series selections in Study anonymization list (most usefull for CTP)
		JPopupMenu popMenuSelectSeries = new JPopupMenu();
		JMenuItem menuItemSelectSeries = new JMenuItem("Select Series");
		popMenuSelectSeries.add(menuItemSelectSeries);
		
		popMenuSelectSeries.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = anonStudiesTable.rowAtPoint(SwingUtilities.convertPoint(anonStudiesTable, MouseInfo.getPointerInfo().getLocation() , anonStudiesTable));
                        if (rowAtPoint > -1) {
                        	anonStudiesTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
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
		
		menuItemSelectSeries.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Select_Series selectSeriesDialog=new Select_Series(getOrthancApisConnexion(), (String)anonStudiesTable.getValueAt(anonStudiesTable.getSelectedRow(), 2), importGUI);
				selectSeriesDialog.pack();
				selectSeriesDialog.setLocationRelativeTo(importGUI);
				selectSeriesDialog.setVisible(true);
				
			}
			
		});
		
		anonStudiesTable.setComponentPopupMenu(popMenuSelectSeries);

		this.revalidate();
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		//Run the import app
		importCTP.doClick();
		
	}

	@Override
	public void ImportFinished(HashMap<String, HashMap<String, String>> importedStudy) {
		
		QueryOrthancData queryOrthanc=new QueryOrthancData(getOrthancApisConnexion());
		
		HashMap<String, HashMap<String, String>> importedstudy=importFrame.getImportedStudy();
		
		Set<String> keys=importedstudy.keySet();
		String[] keysArray=new String[keys.size()];
		keys.toArray(keysArray);
		
		modeleAnonPatients.clear();
		for (int i=0; i<keysArray.length; i++) {
			try {
				Study2 study = queryOrthanc.getStudyDetails(keysArray[i], true);
				modeleAnonPatients.addStudy(study);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		importFrame.dispose();
		
	}

	@Override
	public void AnonymizationDone() {
		openCloseAnonTool(true);
		
	}
	
	public static void main(String[] args) {
		OTP_Import_GUI ctpImport=new OTP_Import_GUI();
		ctpImport.setVisible(true);
	}

}
