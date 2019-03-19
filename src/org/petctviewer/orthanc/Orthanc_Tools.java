package org.petctviewer.orthanc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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

	public static void writeCSV(String text, File file) {
		// On ecrit les CSV
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
			pw.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}
	}

}


//SK TO DO
//Search at study level
//Run auto a tester sur Mac
//Reoganiser le package Query = +-
//Tester toutes les fonctions!
//Tester JsonEditor

//AutoQuery doc a refaire : 
//Valeur global au click
//Interface resultats
//recurrent query 

//+Tard
//Faire detection des SC dans table export Series comme sur main Tab
//Faire item listener dans query pour repondre au changement clavier comme main tab
