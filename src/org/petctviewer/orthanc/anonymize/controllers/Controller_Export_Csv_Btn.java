package org.petctviewer.orthanc.anonymize.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.petctviewer.orthanc.Orthanc_Tools;
import org.petctviewer.orthanc.anonymize.CSV;
import org.petctviewer.orthanc.anonymize.TableExportStudiesModel;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.anonymize.datastorage.Study_Anonymized;

public class Controller_Export_Csv_Btn implements ActionListener {
	
	TableExportStudiesModel modeleExportStudies;
	VueAnon vueAnon;

	public Controller_Export_Csv_Btn(TableExportStudiesModel modeleExportStudies, VueAnon vueAnon) {
		this.modeleExportStudies=modeleExportStudies;
		this.vueAnon=vueAnon;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {

		VueAnon.jprefer.put("reportType", "CSV");
		CSV csv = new CSV();
		if(!modeleExportStudies.getOrthancIds().isEmpty()){
			
			for(Study_Anonymized anonymizedObj : modeleExportStudies.getAnonymizedObject()){
				Study2 anonymizedstudy=anonymizedObj.getAnonymizedStudy();
				Study2 originStudy=anonymizedObj.getOriginalStudy();
				
				anonymizedstudy.storeStudyStatistics(vueAnon.getOrthancQuery());
				
				csv.addStudy(originStudy.getPatientName(), originStudy.getPatientID(), 
						anonymizedstudy.getPatientName(), anonymizedstudy.getPatientID(),
						originStudy.getDate(), originStudy.getStudyDescription(), 
						anonymizedstudy.getStudyDescription(), anonymizedstudy.getStatNbSeries(), 
						anonymizedstudy.getStatNbInstance(), anonymizedstudy.getMbSize(), anonymizedstudy.getStudyInstanceUid());

			}
			
			
			Preferences jpreferAnon = VueAnon.jprefer;
			DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HHmmss");
			
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(jpreferAnon.get("csvLocation", System.getProperty("user.dir"))));
			chooser.setSelectedFile(new File(df.format(new Date()) + ".csv"));
			chooser.setDialogTitle("Export csv to...");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			if (chooser.showSaveDialog(vueAnon) == JFileChooser.APPROVE_OPTION) {
				File fichier=chooser.getSelectedFile();
				jpreferAnon.put("csvLocation", chooser.getSelectedFile().toPath().toString() );
				Orthanc_Tools.writeCSV(csv.getCsv(), fichier);
			}
			
		}
		
	}

}
