package org.petctviewer.orthanc.anonymize.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingWorker;

import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.export.ExportZip;
import org.petctviewer.orthanc.export.SendFilesToRemote;

public class Controller_Export_Remote_Btn implements ActionListener {
	
	VueAnon vue;
	
	public Controller_Export_Remote_Btn(VueAnon vue) {
		this.vue=vue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {


		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
			
			@Override
			protected Void doInBackground() throws Exception {
				vue.setStateExportMessage("Exporting...", "red", -1);
				vue.getExportRemoteBtn().setEnabled(false);
				
				ExportZip convertzip=new ExportZip(vue.getOrthancApisConnexion());
				convertzip.setConvertZipAction("tempZipOrthanc", vue.modeleExportStudies.getOrthancIds(), true);
				convertzip.generateZip(false);
				String zipPath = convertzip.getGeneratedZipPath();
				String zipName = convertzip.getGeneratedZipName();
				StringBuilder remoteFileName = new StringBuilder();
				
				//removing the temporary file default name value
				remoteFileName.append(zipName.substring(0,14));
				remoteFileName.append(zipName.substring(zipName.length() - 4));
				SendFilesToRemote export = new SendFilesToRemote(VueAnon.jprefer.get("exportType", SendFilesToRemote.OPTION_FTP), 
						VueAnon.jprefer.get("remoteFilePath", "/"), remoteFileName.toString(), zipPath, VueAnon.jprefer.get("remoteServer", ""), 
						VueAnon.jprefer.getInt("remotePort", 21), VueAnon.jprefer.get("servUsername", ""), VueAnon.jprefer.get("servPassword", ""));
				export.export();
				
				
				return null;
			}

			@Override
			public void done(){
				try {
					get();
					vue.setStateExportMessage("The data has been successfully been exported", "green", 4);
				} catch (Exception e) {
					vue.setStateExportMessage("The data export failed", "red", -1);
					e.printStackTrace();
				}
				vue.getExportRemoteBtn().setText("Remote server");
				vue.getExportRemoteBtn().setEnabled(true);
			}
		};
		
		if(!vue.modeleExportStudies.getOrthancIds().isEmpty()){
			worker.execute();
		}
		
	}

}
