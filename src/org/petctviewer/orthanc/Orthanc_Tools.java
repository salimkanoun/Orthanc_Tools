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
//Debeug clique droit dans export (study et series)
//Renderer SC =>OK tester sur deux table main et export
//SC Deletion Anon => A priori OK a tester
//Tester toutes les fonctions!
//Reoganiser le package Query
//Finir sortie de AutoQuery
//Search at study level
