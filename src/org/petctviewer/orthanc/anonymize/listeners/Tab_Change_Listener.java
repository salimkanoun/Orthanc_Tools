package org.petctviewer.orthanc.anonymize.listeners;

import java.util.prefs.Preferences;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.petctviewer.orthanc.anonymize.VueAnon;

public class Tab_Change_Listener implements ChangeListener {
	
	VueAnon vue;
	
	public Tab_Change_Listener(VueAnon vue) {
		this.vue=vue;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		
		Preferences jprefer=VueAnon.jprefer;
		
		String selectedAnonProfile=vue.getSelectedAnonProfile();
		
		//////////// Filling the user preferences In Registery ////////////
		if(selectedAnonProfile.equals("Custom")){
			for(int i = 0; i < 2; i++){
				
				if(vue.settingsBodyCharButtons[i].isSelected()){
					if(jprefer.getInt("bodyCharac", 0) != i){
						jprefer.putInt("bodyCharac", i);
					}
				}
				
				if(vue.settingDatesButtons[i].isSelected()){
					if(jprefer.getInt("Dates", 0) != i){
						jprefer.putInt("Dates", i);
					}
				}
				
				if(vue.settingsBirthDateButtons[i].isSelected()){
					if(jprefer.getInt("BD", 0) != i){
						jprefer.putInt("BD", i);
					}
				}
				
				if(vue.settingsPrivateTagButtons[i].isSelected()){
					if(jprefer.getInt("PT", 0) != i){
						jprefer.putInt("PT", i);
					}
				}
				
				if(vue.settingsPrivateTagButtons[i].isSelected()){
					if(jprefer.getInt("PT", 0) != i){
						jprefer.putInt("PT", i);
					}
				}
				
				if(vue.settingsPrivateTagButtons[i].isSelected()){
					if(jprefer.getInt("PT", 0) != i){
						jprefer.putInt("PT", i);
					}
				}
				
				if(vue.settingsSecondaryCaptureButtons[i].isSelected()){
					if(jprefer.getInt("SC", 0) != i){
						jprefer.putInt("SC", i);
					}
				}
				
				if(vue.settingsStudySerieDescriptionButtons[i].isSelected()){
					if(jprefer.getInt("DESC", 0) != i){
						jprefer.putInt("DESC", i);
					}
				}
				
			}
			
		}
		jprefer.put("profileAnon", selectedAnonProfile);
		jprefer.put("centerCode", vue.getCenterCode());
		jprefer.put("CTPAddress", vue.getCTPaddress());
		
		String[] exportparams=vue.getExportRemoteServer();
		// Putting the export preferences in the anon plugin registry

		jprefer.put("remoteServer", exportparams[0]);
		jprefer.put("remotePort", exportparams[1]);
		jprefer.put("servUsername",  exportparams[2]);
		jprefer.put("servPassword",  exportparams[3]);
		jprefer.put("remoteFilePath", exportparams[4]);
		jprefer.put("exportType",  exportparams[5]);
		
		
		if(vue.getCTPaddress().equals("http://") || vue.getCTPaddress().equals("https://") || vue.getCTPaddress().isEmpty()){
			vue.showCTPButtons(false);
		}else {
			vue.showCTPButtons(true);
		}
		
		if(exportparams[0].length() == 0){
			vue.showRemoteExportBtn(false);
		}else{
			vue.showRemoteExportBtn(true);
		}
		//Save Peer position
		jprefer.putInt("CTPPeer", vue.listePeersCTP.getSelectedIndex());
		vue.pack();
		
	}

}
