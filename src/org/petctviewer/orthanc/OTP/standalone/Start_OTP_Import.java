package org.petctviewer.orthanc.OTP.standalone;

import org.petctviewer.orthanc.anonymize.VueAnon;

public class Start_OTP_Import   {

	public static void main(String[] args) {
		VueAnon anon=new VueAnon("OrthancCTP.json");
		anon.setLocationRelativeTo(null);
		anon.exportTabForOtp();
		anon.setVisible(true);
		
	}

	

}


