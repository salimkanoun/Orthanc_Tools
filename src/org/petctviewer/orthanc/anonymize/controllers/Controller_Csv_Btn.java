package org.petctviewer.orthanc.anonymize.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import org.petctviewer.orthanc.anonymize.CSV;
import org.petctviewer.orthanc.anonymize.QueryOrthancData;
import org.petctviewer.orthanc.anonymize.TableExportStudiesModel;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.anonymize.datastorage.Study_Anonymized;

public class Controller_Csv_Btn implements ActionListener {
	
	TableExportStudiesModel modeleExportStudies;
	QueryOrthancData queryOrthanc;

	public Controller_Csv_Btn(TableExportStudiesModel modeleExportStudies, QueryOrthancData queryOrthanc) {
		this.modeleExportStudies=modeleExportStudies;
		this.queryOrthanc=queryOrthanc;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {

		VueAnon.jprefer.put("reportType", "CSV");
		CSV csv = new CSV();
		if(!modeleExportStudies.getOrthancIds().isEmpty()){
			for(Study_Anonymized anonymizedObj : modeleExportStudies.getAnonymizedObject()){
				Study2 anonymizedstudy=anonymizedObj.getAnonymizedStudy();
				Study2 originStudy=anonymizedObj.getOriginalStudy();
				
				anonymizedstudy.storeStudyStatistics(queryOrthanc);
				
				csv.addStudy(originStudy.getPatientName(), originStudy.getPatientID(), 
						anonymizedstudy.getPatientName(), anonymizedstudy.getPatientID(),
						originStudy.getDate(), originStudy.getStudyDescription(), 
						anonymizedstudy.getStudyDescription(), anonymizedstudy.getStatNbSeries(), 
						anonymizedstudy.getStatNbInstance(), anonymizedstudy.getMbSize(), anonymizedstudy.getStudyInstanceUid());
					
		
			}
			try {
				csv.genCSV();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			
		}
		
	
		
	}

}
