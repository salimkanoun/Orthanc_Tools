package org.petctviewer.orthanc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.setup.ConnectionSetup;
import org.petctviewer.orthanc.setup.OrthancRestApis;

import ij.plugin.PlugIn;

public class Orthanc_Tools implements PlugIn {
	
	VueAnon anon;

	public Orthanc_Tools(){
		OrthancRestApis restApis= new OrthancRestApis(null);
		//Until we reach the Orthanc Server we give the setup panel
		ConnectionSetup setup=null;
		int check=0;
		while (!restApis.isConnected() && check<3) {
				if (check>0) JOptionPane.showMessageDialog(null, "Settings Attempt " + (check+1) +"/3", "Attempt", JOptionPane.INFORMATION_MESSAGE);
				setup = new ConnectionSetup();
				setup.setVisible(true);
				restApis=new OrthancRestApis(null);
				check++;
				if(check ==3) {
					JOptionPane.showMessageDialog(null, "Can't reach Orthanc, terminating", "Failure", JOptionPane.ERROR_MESSAGE);	
					return;
				}
		}
		anon=new VueAnon(restApis);
		if(setup!=null && setup.getRunOrthanc()!=null) anon.setRunOrthanc(setup.getRunOrthanc());
		anon.setLocationRelativeTo(null);
		anon.setVisible(true);
	}
	
	public static void main(String... args){
		new Orthanc_Tools();
		
	}

	@Override
	public void run(String string) {
		anon.fijiEnvironement=true;
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
//Tester envoie DICOM plateforme
// pb Renderer envoie DICOM

//+Tard
//Faire detection des SC dans table export Series comme sur main Tab
//Faire item listener dans query pour repondre au changement clavier comme main tab
//Envoi manuel de cd/dvd ?
//Job monitoring pour toutes les operation longue
//Voir pour sauvegarde BF quand ouvert depius Orthanc (bf.gr)

//Idee refactor 
//sortir panel modalities dans un objet a part
