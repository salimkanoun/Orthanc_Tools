package org.petctviewer.orthanc;

import org.petctviewer.orthanc.anonymize.VueAnon;

import ij.plugin.PlugIn;

public class Orthanc_Tools extends VueAnon implements PlugIn {
	private static final long serialVersionUID = 1L;

	// LAUNCHERS
	public static void main(String... args){
		VueAnon anon=new VueAnon();
		anon.setLocationRelativeTo(null);
		anon.setVisible(true);
	}

	@Override
	public void run(String string) {
		setLocationRelativeTo(null);
		this.setVisible(true);
		fijiEnvironement=true;
	}

}


//SK TO DO
//Renderer SC
//Modality Check Anon
//SC Deletion Anon
//Tester toutes les fonctions!
//Reoganiser le package Query