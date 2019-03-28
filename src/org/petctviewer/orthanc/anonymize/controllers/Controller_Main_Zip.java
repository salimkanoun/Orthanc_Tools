package org.petctviewer.orthanc.anonymize.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.export.ExportZip;
import org.petctviewer.orthanc.export.ExportZipAndViewer;

public class Controller_Main_Zip implements ActionListener {

	private VueAnon vue;
	private DateFormat dfZip = new SimpleDateFormat("MM_dd_yyyy_HHmmss");
	
	public Controller_Main_Zip(VueAnon vue) {
		this.vue=vue;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!vue.exportContent.isEmpty()){
			//Open JFileChooser
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Export to...");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setCurrentDirectory(new File(VueAnon.jprefer.get("zipLocation", System.getProperty("user.dir"))));
			
			// Get comboToolChooser Choice
			String comboToolItem=vue.getComboToolChooserSeletedItem();
			
			if (comboToolItem.equals("Image with Viewer (iso)")) {
				chooser.setSelectedFile(new File(vue.exportShownContent.getItemAt(0).replaceAll("/", "_")+"_image.iso")); 
			}else {
				chooser.setSelectedFile(new File(dfZip.format(new Date()) + ".zip")); 
			}

			if (chooser.showSaveDialog(vue) == JFileChooser.APPROVE_OPTION) {
				VueAnon.jprefer.put("zipLocation", chooser.getSelectedFile().toPath().toString());
			
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						
						ExportZip convertzip=new ExportZip(vue.getOrthancApisConnexion());
						vue.setStateMessage("Generating Zip...", "red", -1);
						vue.activateExport(false);
						
						if ( comboToolItem.equals("ZIP File") || comboToolItem.equals("DICOMDIR Zip") ) {	
							convertzip.setConvertZipAction(chooser.getSelectedFile().getAbsolutePath().toString() , vue.exportContent, false);
							if (comboToolItem.equals("ZIP File")) convertzip.generateZip(false);
							if (comboToolItem.equals("DICOMDIR Zip")) convertzip.generateZip(true);
						//If include the viewer	
						} else {
							String viewerString=VueAnon.jprefer.get("viewerDistribution", "empty");
							
							if( viewerString.equals("empty") || ! new File(viewerString).exists() ) {
								JOptionPane.showMessageDialog(vue,"Viewer not available, please download it in the setup tab");
								throw new Exception("No Available Viewer");
							}
							
							convertzip.setConvertZipAction("Viewer", vue.exportContent, true);
							convertzip.generateZip(true);
							File tempImageZip=convertzip.getGeneratedZipFile();
							File packageViewer=new File(viewerString);
							ExportZipAndViewer zip=new ExportZipAndViewer(tempImageZip, chooser.getSelectedFile(), packageViewer);
							
							if( comboToolItem.equals("Image with Viewer (zip)") ) {
								zip.ZipAndViewerToZip();
							}else if( comboToolItem.equals("Image with Viewer (iso)") ) {
								zip.generateIsoFile();
								
							}
						
						}
						return null;
					}
					
					@Override
					protected void done() {
						try {
							this.get();
							vue.setStateMessage("The data have successfully been exported to zip", "green", 4);
							
						} catch (Exception e) {
							e.printStackTrace();
							vue.setStateMessage("Zip Export Failure", "red", -1);
						}
					
						//Reactivate component after export
						vue.activateExport(true);
						//empty exported list
						vue.emptyExportList();
						//Close export tool
						vue.openCloseExportTool(false);
						vue.pack();
					}				
			};
				
			worker.execute();
			vue.pack();
			
			}	
		}

	}

}
