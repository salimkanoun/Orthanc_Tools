package org.petctviewer.orthanc.anonymize.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.petctviewer.orthanc.anonymize.AnonRequest;
import org.petctviewer.orthanc.anonymize.QueryOrthancData;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.PatientAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.anonymize.datastorage.Study2Anon;
import org.petctviewer.orthanc.anonymize.datastorage.Study_Anonymized;
import org.petctviewer.orthanc.anonymize.datastorage.Tags.Choice;
import org.petctviewer.orthanc.setup.OrthancRestApis;

public class Controller_Anonymize_Btn implements ActionListener {
	
	private VueAnon vue;
	private int anonCount;
	private QueryOrthancData queryOrthanc;
	public Controller_Anonymize_Btn(VueAnon vue, OrthancRestApis connexion) {
		this.vue=vue;
		queryOrthanc=new QueryOrthancData(connexion);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingWorker<Void,Void> worker=new SwingWorker<Void,Void>() {

			int totalToAnonymize=0;
			boolean nm, pt, us, similarIDs;
			
			@Override
			protected Void doInBackground() throws Exception {
				
				int dialogResult=0;
				//Retrieve the selected anon profile
				String anonProfile=vue.getSelectedAnonProfile();
				//Analyze anon list to detect similar ID
				analyzeAnonlist();
				
				if((pt || nm) && anonProfile.equals("Full clearing")) {
					dialogResult = JOptionPane.showConfirmDialog (vue, 
							"Full clearing is not recommended for NM or PT modalities."
									+ "Are you sure you want to anonymize ?",
									"Warning anonymizing PT/NM",
									JOptionPane.WARNING_MESSAGE,
									JOptionPane.YES_NO_OPTION);
				}
				if(us) {
					dialogResult = JOptionPane.showConfirmDialog (vue, 
							"DICOM files with the US modality may have hard printed informations, "
									+ "you may need to edit the image, do you want to anonymize?",
									"Warning anonymizing US",
									JOptionPane.WARNING_MESSAGE);
				}
				
				if(similarIDs){
					dialogResult = JOptionPane.showConfirmDialog (vue, 
							"You have defined 2 or more identical IDs for anonymized patients, which is not recommended."
									+ " Are you sure you want to anonymize ?",
									"Warning similar IDs",
									JOptionPane.WARNING_MESSAGE,
									JOptionPane.YES_NO_OPTION);
				}
				//0 is the Yes option
				if(dialogResult==0) {
					vue.enableAnonButton(false);
					anonymize();
				}else {
					throw new Exception("Anon Aborted");
				}
				
				return null;
			}
			
			@Override
			protected void done() {
				try {
					get();
					vue.enableAnonButton(true);
					vue.anonBtn.setText("Anonymize");
					vue.setStateMessage("The data has successfully been anonymized.", "green", 4);
					vue.enableAnonButton(true);
					vue.openCloseAnonTool(false);
					vue.pack();
					vue.tabbedPane.setSelectedIndex(1);
					//Clear Anon List
					vue.modeleAnonPatients.clear();
					vue.modeleAnonStudies.clear();
					
					//Si fonction a ete fait avec le CTP on fait l'envoi auto A l'issue de l'anon
					if(vue.autoSendCTP) {
						vue.exportCTP.doClick();
						vue.autoSendCTP=false;
					}
					
					if(vue.anonymizeListener!=null) {
						vue.anonymizeListener.AnonymizationDone();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				return;
			}
			
			private void anonymize() throws Exception {

				//Storage of Anon Setting
				Choice bodyCharChoice=Choice.CLEAR,
						datesChoice=Choice.CLEAR,
						bdChoice=Choice.CLEAR,
						ptChoice=Choice.CLEAR,
						scChoice=Choice.CLEAR,
						descChoice =Choice.CLEAR;
				
				//Get Current Settings
				if(vue.settingsBodyCharButtons[0].isSelected()) bodyCharChoice = Choice.KEEP;
				if(vue.settingDatesButtons[0].isSelected()) datesChoice = Choice.KEEP;
				if(vue.settingsBirthDateButtons[0].isSelected()) bdChoice = Choice.KEEP;
				if(vue.settingsPrivateTagButtons[0].isSelected()) ptChoice = Choice.KEEP;
				if(vue.settingsSecondaryCaptureButtons[0].isSelected()) scChoice = Choice.KEEP;
				if(vue.settingsStudySerieDescriptionButtons[0].isSelected()) descChoice = Choice.KEEP;
				
				anonCount=0;
				
				for(int i=0 ; i<vue.anonPatientTable.getRowCount(); i++) {
					
					PatientAnon patientAnon=(PatientAnon) vue.anonPatientTable.getValueAt(i, 6);
					//SK CHECK DES MODALITE A FAIRE
					HashMap<String, Study2Anon> studyAnon=patientAnon.getAnonymizeStudies();
					Set<String> studyIds=studyAnon.keySet();
					for(String studyId : studyIds) {
						
						vue.setStateMessage("Anonymizing study "+(anonCount+1)+"/"+totalToAnonymize, "red", -1);
						
						Study2Anon studyToAnon = (Study2Anon) studyAnon.get(studyId);
						//Get the New ID
						String newPatientName;
						String newPatientId;
						//Prepare Substitute if new PatientName/ ID are not Filled
						String substituteName = "A-" + VueAnon.jprefer.get("centerCode", "12345");
						SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
						String substituteID = "A-" + df.format(new Date());
						
						if(StringUtils.isEmpty((String) vue.anonPatientTable.getValueAt(i, 3))) {
							newPatientName = substituteName + "^" + anonCount;
							vue.anonPatientTable.setValueAt(newPatientName, i, 3);
						}else {
							newPatientName=(String) vue.anonPatientTable.getValueAt(i, 3);
						}
						
						if(StringUtils.isEmpty((String) vue.anonPatientTable.getValueAt(i, 4))) {
							newPatientId = substituteID + "^" + anonCount;
							vue.anonPatientTable.setValueAt(newPatientId, i, 4);
						}else {
							newPatientId=(String) vue.anonPatientTable.getValueAt(i, 4);
						}
						
						patientAnon.setNewPatientName(newPatientName);
						patientAnon.setNewPatientId(newPatientId);
						//Prepare the Anon Request
						AnonRequest anonRequest= new AnonRequest(vue.getOrthancApisConnexion(), bodyCharChoice, datesChoice, bdChoice, 
								ptChoice, scChoice, descChoice, 
								patientAnon.getNewPatientName(), patientAnon.getNewPatientId(), studyToAnon.getNewStudyDescription());
						//Start the Anonymization
						anonRequest.sendQuery(studyId);
						//Store the new Anonymized Study ID
						studyToAnon.setNewAnonymizedStudyOrthancId(anonRequest.getNewOrthancID());
						
						Study2 anonymizedStudy=queryOrthanc.getStudyDetails(anonRequest.getNewOrthancID(), true);
						
						if(vue.settingsSecondaryCaptureButtons[1].isSelected()){
							anonymizedStudy.deleteAllSc(vue.getOrthancApisConnexion());
							anonymizedStudy.refreshChildSeries(queryOrthanc);
						}
						
						//Create object with the Anonymized Study and old study object
						Study_Anonymized anonymizedStudyResult=new Study_Anonymized(anonymizedStudy, studyToAnon);
						vue.modeleExportStudies.addStudy(anonymizedStudyResult);
						
						anonCount++;
					}
				}
			}
			
			private void analyzeAnonlist() {
				ArrayList<String> newIDs = new ArrayList<String>();
				
				for(int i=0 ; i<vue.anonPatientTable.getRowCount(); i++) {
					PatientAnon patientAnon=(PatientAnon) vue.anonPatientTable.getValueAt(i, 6);
					//Search for similar new ID
					String newPatientId=(String) vue.anonPatientTable.getValueAt(i, 4);
					if(newPatientId != "" && !newIDs.contains(newPatientId)){
						newIDs.add(newPatientId);
					}else if(newIDs.contains(newPatientId)){
						similarIDs = true;
					}
					
					HashMap<String, Study2Anon> studyAnon=patientAnon.getAnonymizeStudies();
					Set<String> studyIds=studyAnon.keySet();
					for(String studyId:studyIds) {
						//Search for specific modality requiring special anon condition
						ArrayList<String> modalities=studyAnon.get(studyId).getModalitiesInStudy();
						if(modalities.contains("NM")) {
							nm=true;
						}
						if(modalities.contains("PT")) {
							pt=true;
						}
						if(modalities.contains("US")) {
							us=true;
						}
					}
					//Calculate the total number of studies to anonymize
					totalToAnonymize+=studyIds.size();
				}
				
			}
			
			
		};
		//Sk Avant de Lancer faire check Modalites
		worker.execute();
	}

}

