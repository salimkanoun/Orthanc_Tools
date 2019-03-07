package org.petctviewer.orthanc.anonymize.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.petctviewer.orthanc.anonymize.VueAnon;
import org.apache.commons.lang3.StringUtils;
import org.petctviewer.orthanc.anonymize.AnonRequest;
import org.petctviewer.orthanc.anonymize.QueryOrthancData;
import org.petctviewer.orthanc.anonymize.Tags.Choice;
import org.petctviewer.orthanc.anonymize.datastorage.PatientAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.anonymize.datastorage.Study2Anon;
import org.petctviewer.orthanc.anonymize.datastorage.Study_Anonymized;
import org.petctviewer.orthanc.setup.OrthancRestApis;

public class Controller_Anonymize_Btn implements ActionListener {
	
	private VueAnon vue;
	private int anonCount;
	private QueryOrthancData queryOrthanc;
	private ArrayList<Study_Anonymized> anonymizedstudies;
	public Controller_Anonymize_Btn(VueAnon vue, OrthancRestApis connexion) {
		this.vue=vue;
		queryOrthanc=new QueryOrthancData(connexion);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingWorker<Void,Void> worker=new SwingWorker<Void,Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				vue.enableAnonButton(false);
				anonimyze();
				return null;
			}
			
			@Override
			protected void done() {
				try {
					get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				vue.enableAnonButton(true);
				vue.anonBtn.setText("Anonymize");
				vue.setStateMessage("The data has successfully been anonymized.", "green", 4);
				vue.enableAnonButton(true);
				vue.openCloseAnonTool(false);
				vue.pack();
				vue.tabbedPane.setSelectedIndex(1);
				//SK Avant de faire le Clear Faire Passer les lignes à l'Export
				vue.modeleAnonPatients.clear();
				vue.modeleAnonStudies.clear();
				
				//Si fonction a ete fait avec le CTP on fait l'envoi auto A l'issue de l'anon
//				if(autoSendCTP) {
//					exportCTP.doClick();
//					autoSendCTP=false;
//				}
//				if(anonymizeListener!=null) {
//					anonymizeListener.AnonymizationDone();
//				}
				return;
			}
			
			
		};
		//Sk Avant de Lancer faire check Modalites
		worker.execute();
	}

	public void anonimyze() {
		
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
		int totalAnonStudyList=calculateTotalNbOfSeries();
		for(int i=0 ; i<vue.anonPatientTable.getRowCount(); i++) {
			
			PatientAnon patientAnon=(PatientAnon) vue.anonPatientTable.getValueAt(i, 6);
			//SK CHECK DES MODALITE A FAIRE
			HashMap<String, Study2Anon> studyAnon=patientAnon.getAnonymizeStudies();
			Set<String> studyIds=studyAnon.keySet();
			for(String studyId : studyIds) {
				
				vue.setStateMessage("Anonymizing study "+(anonCount+1)+"/"+totalAnonStudyList, "red", -1);
				
				Study2Anon studyToAnon = (Study2Anon) studyAnon.get(studyId);
				//Get the New ID
				String newPatientName;
				String newPatientId;
				//Prepare Substitute if new PatientName/ ID are not Filled
				String substituteName = "A-" + vue.jprefer.get("centerCode", "12345");
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
				AnonRequest anonRequest= new AnonRequest(vue.connexionHttp, bodyCharChoice, datesChoice, bdChoice, 
						ptChoice, scChoice, descChoice, 
						patientAnon.getNewPatientName(), patientAnon.getNewPatientId(), studyToAnon.getNewStudyDescription());
				//Start the Anonymization
				anonRequest.sendQuery(studyId);
				//Store the new Anonymized Study ID
				studyToAnon.setNewAnonymizedStudyOrthancId(anonRequest.getNewOrthancID());
				Study2 anonymizedStudy=queryOrthanc.getStudyDetails(anonRequest.getNewOrthancID(), true);
				//Create object with the Anonymized Study and old study object
				Study_Anonymized anonymizedStudyResult=new Study_Anonymized(anonymizedStudy, studyToAnon);
				vue.modeleExportStudies.addStudy(anonymizedStudyResult);
				
				if(vue.settingsSecondaryCaptureButtons[1].isSelected()){
					//A FAIRE
					//modeleAnonStudies.removeScAndSr();
				}
				anonCount++;
			}
		}
		
	
		//vue.modeleAnonPatients
		
	}
	
	private int calculateTotalNbOfSeries() {
		int total=0;
		for(int i=0 ; i<vue.anonPatientTable.getRowCount(); i++) {
			PatientAnon patientAnon=(PatientAnon) vue.anonPatientTable.getValueAt(i, 6);
			HashMap<String, Study2Anon> studyAnon=patientAnon.getAnonymizeStudies();
			Set<String> studyIds=studyAnon.keySet();
			total+=studyIds.size();
		}
		return total;
	}

}




//public void actionPerformed(ActionEvent arg0) {
	/*
	anonCount = 0;
	
	SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
		int dialogResult=JOptionPane.YES_OPTION;
		@Override
		protected Void doInBackground() {

			//Disable the anons button during anonymization
			enableAnonButton(false);

			anonBtn.setText("Anonymizing");
			
			Choice bodyCharChoice=Choice.CLEAR,
					datesChoice=Choice.CLEAR,
					bdChoice=Choice.CLEAR,
					ptChoice=Choice.CLEAR,
					scChoice=Choice.CLEAR,
					descChoice =Choice.CLEAR;
			
			// Change Choice to Keep if position 0 is selected
			if(settingsBodyCharButtons[0].isSelected()) bodyCharChoice = Choice.KEEP;
			if(settingDatesButtons[0].isSelected()) datesChoice = Choice.KEEP;
			if(settingsBirthDateButtons[0].isSelected()) bdChoice = Choice.KEEP;
			if(settingsPrivateTagButtons[0].isSelected()) ptChoice = Choice.KEEP;
			if(settingsSecondaryCaptureButtons[0].isSelected()) scChoice = Choice.KEEP;
			if(settingsStudySerieDescriptionButtons[0].isSelected()) descChoice = Choice.KEEP;			
		

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
						String newStudyID = "";
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
							AnonRequest quAnon;
							quAnon = new AnonRequest(connexionHttp, bodyCharChoice, datesChoice, bdChoice, ptChoice, scChoice, descChoice, newName, newID, newDesc);
							state.setText("<html>Anonymization state - " + (i+1) + "/" + modeleAnonStudies.getStudies().size() + 
									" <font color='red'> <br>(Do not use the toolbox while the current operation is not done)</font></html>");
							quAnon.sendQuery(uid);
							modeleAnonStudies.addNewUid(quAnon.getNewUID());
							i++;
							newStudyID = quAnon.getNewUID();
							//Add anonymized study in export list
							modeleExportStudies.addStudy(newStudyID);
						}

						j++;
					}
					
					if(settingsSecondaryCaptureButtons[1].isSelected()){
						modeleAnonStudies.removeScAndSr();
					}
					
					//Empty list
					modeleAnonStudies.empty();
					modeleAnonPatients.clear();
					
					
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void done(){
			
			//Re-enable anon button
			enableAnonButton(true);
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
			//Si fonction a ete fait avec le CTP on fait l'envoi auto A l'issue de l'anon
			if(autoSendCTP) {
				exportCTP.doClick();
				autoSendCTP=false;
			}
			if(anonymizeListener!=null) {
				anonymizeListener.AnonymizationDone();
			}
		}
	};
	if(!modeleAnonStudies.getOldOrthancUIDs().isEmpty()){
			worker.execute();
	}
	*/
//}
