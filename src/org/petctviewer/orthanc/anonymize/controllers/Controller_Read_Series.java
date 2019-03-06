package org.petctviewer.orthanc.anonymize.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.reader.Read_Orthanc;

import ij.ImagePlus;

public class Controller_Read_Series implements ActionListener {
	
	private VueAnon vue;
	
	public Controller_Read_Series(VueAnon vue) {
		this.vue=vue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int[] selectedListes=vue.tableauSeries.getSelectedRows();
		
		if(selectedListes.length==0) {
			JOptionPane.showMessageDialog(vue, "Select Series to read", "No series", JOptionPane.ERROR_MESSAGE);
		}
		List<String> ids=new ArrayList<String>();
		ArrayList<ImagePlus> imagestacks=new ArrayList<ImagePlus>();
		
		boolean ct = false;
		boolean pet = false;
		
		for( int line : selectedListes) {
			ids.add((String) vue.tableauSeries.getValueAt(line, 4));
			if(vue.tableauSeries.getValueAt(line, 1).equals("PT")) pet=true;
			if(vue.tableauSeries.getValueAt(line, 1).equals("CT")) ct=true;
			
		}
		
		boolean startViewer=(pet && ct && vue.fijiEnvironement);
		
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
			
			@SuppressWarnings("rawtypes")
			@Override
			protected Void doInBackground() {

				for(int i=0; i<ids.size(); i++) {
					vue.btnReadSeries.setText("Reading Series");
					vue.setStateMessage("Reading Series"+(i+1)+"/"+ids.size(), "red", -1);
					Read_Orthanc reader=new Read_Orthanc(vue.connexionHttp);
					ImagePlus ip=reader.readSerie(ids.get(i));
					ip.show();
					imagestacks.add(ip);
					
				}
				
				if(startViewer) {
					Class Run_Pet_Ct = null;
					try {
						Run_Pet_Ct = Class.forName("Run_Pet_Ct");
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
					try {
						@SuppressWarnings("unchecked")
						Constructor cs=Run_Pet_Ct.getDeclaredConstructor(ArrayList.class);
						cs.newInstance(imagestacks);
					} catch (Exception e) {
						e.printStackTrace();
					} 
					
				}
				
				return null;
			}

			@Override
			public void done(){
				vue.btnReadSeries.setText("Open Images");
				vue.setStateMessage("Reading Done", "gree", 4);
			
			}
		};
		
		worker.execute();

	}

}
