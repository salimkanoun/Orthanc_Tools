/**
Copyright (C) 2017 VONGSALAT Anousone & KANOUN Salim

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public v.3 License as published by
the Free Software Foundation;

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.petctviewer.orthanc.importdicom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.petctviewer.orthanc.anonymize.QueryOrthancData;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ImportDCM extends JDialog implements WindowListener{
	
	private static final long serialVersionUID = 1L;
	private Preferences jprefer = VueAnon.jprefer;
	private JLabel state;
	private OrthancRestApis connexion;
	private JDialog gui;
	private ArrayList<String> importAnswer=new ArrayList<String>();
	private JsonParser parser=new JsonParser();
	private HashMap<String, Study2 > importedstudy=new HashMap<String, Study2 >();
	private SwingWorker<Void,Void> importWorker;
	
	private ImportListener listener;
	
	private static ImportDCM importDcm=null;
	
	public static ImportDCM getImportDcm(VueAnon vueAnon) {
		if(importDcm==null) {
			importDcm=new ImportDCM(vueAnon);
		}
		return importDcm;
	}

	private ImportDCM(VueAnon vueAnon)  {
		this.setTitle("Import DICOM files");
		this.connexion=vueAnon.getOrthancApisConnexion();
		this.gui=this;
		JPanel mainPanel = new JPanel(new GridBagLayout());
		JLabel labelPath = new JLabel("DICOM files path");
		JTextField path = new JTextField(jprefer.get("filesLocation", System.getProperty("user.dir")));
		path.setMinimumSize(new Dimension(250, 27));
		path.setMaximumSize(new Dimension(250, 27));
		path.setEditable(false);
		
		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File(jprefer.get("filesLocation", System.getProperty("user.dir"))));
				chooser.setDialogTitle("Export zip to...");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					path.setText(chooser.getSelectedFile().toPath().toString());
					gui.pack();
					jprefer.put("filesLocation", path.getText());
				}
			}
		});

		state = new JLabel("Select folder and start sending process");

		JButton importBtn = new JButton("Import");
		importBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(path.getText().length() > 0){
					state.setForeground(Color.black);
					importWorker = new SwingWorker<Void,Void>(){

						@Override
						protected Void doInBackground() throws Exception { 
							
							importFiles(Paths.get(jprefer.get("filesLocation", System.getProperty("user.dir"))));
							return null;
						}
						
						@Override 
						protected void done() {
							state.setText(state.getText()+" - Finished");
							state.setForeground(Color.BLUE);
							gui.pack();
							if(listener !=null) {
								getImportedStudy();
								listener.ImportFinished(importedstudy);
							}
						}
					};
					importWorker.execute();
				}
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(20, 20, 20, 20);
		gbc.gridx = 0;
		gbc.gridy = 0;
		mainPanel.add(labelPath, gbc);

		gbc.insets = new Insets(20, 0, 20, 0);
		gbc.gridx = 1;
		gbc.gridy = 0;
		mainPanel.add(path, gbc);

		gbc.insets = new Insets(20, 0, 20, 20);
		gbc.gridx = 2;
		gbc.gridy = 0;
		mainPanel.add(browse, gbc);

		gbc.insets = new Insets(0, 0, 20, 20);
		gbc.gridx = 1;
		gbc.gridy = 1;
		mainPanel.add(state, gbc);
		
		gbc.insets = new Insets(0, 0, 20, 20);
		gbc.gridx = 1;
		gbc.gridy = 2;
		mainPanel.add(importBtn, gbc);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Image image = new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage();
		this.setIconImage(image);
		this.getContentPane().add(mainPanel);
		addWindowListener(this);
		pack();
		setLocationRelativeTo(vueAnon);
		
		
		
	}
	
	public void setImportListener(ImportListener listener) {
		this.listener=listener;
	}

	private void importFiles(Path path){
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				int successCount = 0;
				long totalFiles = 0;
				
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					System.out.println("Importing " + file);
					
					HttpURLConnection conn = connexion.sendDicom("/instances", (Files.readAllBytes(file)));
					
					if(conn.getResponseCode() == 200){
						System.out.println("=> Success \n");
						//Get response after save
						BufferedReader br = new BufferedReader(new InputStreamReader( (conn.getInputStream() )));
						String output;
						StringBuilder sb=new StringBuilder();
						while ((output = br.readLine()) != null) {
							sb.append(output);
						}
						importAnswer.add(sb.toString());
						successCount++;
					}else{
						System.out.println("=> Failure (Is it a DICOM file ?)\n");
					}

					conn.disconnect();
					totalFiles++;
					state.setText(successCount + "/" + totalFiles + " files were imported. (Fiji>Window>Console)");
					return FileVisitResult.CONTINUE;
				}
			});		

		} catch (MalformedURLException e) {
			System.out.println("Bad URL");
		} catch (IOException e) {
			System.out.println("=> Unable to connect (Is Orthanc running ? Is there a password ?)\n");
		}
		
	}
	
	/**
	 * Return Hashmap describing imported studies
	 * @return
	 */
	public HashMap<String, Study2> getImportedStudy() {
		
		QueryOrthancData queryOrthanc = new QueryOrthancData(connexion);
		
		for (int i=0; i<importAnswer.size(); i++) {
				JsonObject importedInstance=(JsonObject) parser.parse(importAnswer.get(i));
				String parentStudyID=importedInstance.get("ParentStudy").getAsString();
				
				//If new study Add it to the global Hashmap
				if( ! importedstudy.containsKey(parentStudyID)) {
					
					try {
						Study2 study=queryOrthanc.getStudyDetails(parentStudyID, true);
						importedstudy.put(parentStudyID, study);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
		}
		
		return importedstudy;

	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if(importWorker!=null && !importWorker.isDone()) {
			importWorker.cancel(true);
		}
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
