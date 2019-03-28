package org.petctviewer.orthanc.anonymize.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingWorker;

import org.petctviewer.orthanc.OTP.OTP;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.monitoring.Job_Monitoring;
import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Controller_Export_OTP implements ActionListener{
	
	private VueAnon vue;
	
	public Controller_Export_OTP(VueAnon vue) {
		this.vue=vue;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
			
			boolean validateOk=false;
			
			@Override
			protected Void doInBackground() throws InterruptedException {
				
				OrthancRestApis connexionHttp=vue.getOrthancApisConnexion();
				//Send DICOM to CTP selected Peer
				vue.getExportCTPbtn().setEnabled(false);
				String jobID=connexionHttp.sendStudiesToPeerAccelerator(vue.getSelectedCTPPeer(), vue.modeleExportStudies.getOrthancIds());
				Job_Monitoring jobMonitoring=new Job_Monitoring(connexionHttp, jobID);
				
				do {
					vue.setStateExportMessage("Step 1/3 Sending to CTP Peer : Progress "+jobMonitoring.getProgress()+" %", "orange", -1);
					Thread.sleep(500);
				} while (jobMonitoring.isRunning() );
				
				if(jobMonitoring.getState().equals("Success")) {
					validateUpload();
				}else if(jobMonitoring.getState().equals("Failure")) {
					vue.setStateExportMessage("Send Failed", "red", -1);
					return null;
					
				}

				//If everything OK, says validated and remove anonymized studies from local
				if(validateOk) {
					vue.setStateExportMessage("Step 3/3 : Deleting local study", "red", -1);
					for(Study2 study : vue.modeleExportStudies.getAnonymizedStudy2Object()){
						//deleted anonymized and sent study
						connexionHttp.makeDeleteConnection("/studies/"+study.getOrthancId());
					}
					// empty the export table
					vue.modeleExportStudies.clear();
					vue.modeleExportSeries.clear();
				}
				
				return null;
			}
	
			private void validateUpload() {
				vue.setStateExportMessage("Step 2/3 : Validating upload", "red", -1);
				//Create CTP object to manage CTP communication
				OTP ctp=new OTP(vue.getCTPLogin(), vue.getCTPPassword(), vue.getCTPaddress());
				//Create the JSON to send
				JsonArray sentStudiesArray=new JsonArray();
				//For each study populate the array with studies details of send process
				for(Study2 study : vue.modeleExportStudies.getAnonymizedStudy2Object()){
					study.storeStudyStatistics(vue.getOrthancQuery());
					//Creat Object to send to OTP
					JsonObject studyObject=new JsonObject();
					studyObject.addProperty("visitName", study.getStudyDescription());
					studyObject.addProperty("StudyInstanceUID", study.getStudyInstanceUid());
					studyObject.addProperty("patientNumber", study.getPatientName());
					studyObject.addProperty("instanceNumber", study.getStatNbInstance());
					sentStudiesArray.add(studyObject);

				}
				validateOk=ctp.validateUpload(sentStudiesArray);
				if (validateOk) {
					vue.setStateExportMessage("CTP Export Done", "green", -1);
				}else {
					vue.setStateExportMessage("Validation Failed", "red", -1);
				}
				
			}
			
			@Override
			protected void done(){
				vue.getExportCTPbtn().setEnabled(true);
			}
			
		};
		
		if(!vue.modeleExportStudies.getOrthancIds().isEmpty()){
			worker.execute();
		}
		
	}

}
