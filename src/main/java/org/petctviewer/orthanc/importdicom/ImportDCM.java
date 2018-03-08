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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import ij.plugin.PlugIn;

import org.petctviewer.orthanc.ParametreConnexionHttp;

public class ImportDCM extends JFrame implements PlugIn{
	private static final long serialVersionUID = 1L;
	private Preferences jpreferPerso = Preferences.userRoot().node("<unnamed>/queryplugin");
	private JLabel state;
	private ParametreConnexionHttp connexion=new ParametreConnexionHttp();

	public ImportDCM(){
		super("Import DICOM files");
		JPanel mainPanel = new JPanel(new GridBagLayout());
		JLabel labelPath = new JLabel("DICOM files path");
		JTextField path = new JTextField(jpreferPerso.get("filesLocation", System.getProperty("user.dir")));
		path.setMinimumSize(new Dimension(250, 27));
		path.setMaximumSize(new Dimension(250, 27));
		path.setEditable(false);
		JButton browse = new JButton("...");
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File(jpreferPerso.get("filesLocation", System.getProperty("user.dir"))));
				chooser.setDialogTitle("Export zip to...");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					path.setText(chooser.getSelectedFile().toPath().toString());
					jpreferPerso.put("filesLocation", path.getText());
				}
			}
		});

		state = new JLabel("");

		JButton importBtn = new JButton("Import");
		importBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(path.getText().length() > 0){
					SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){

						@Override
						protected Void doInBackground() throws Exception { 
							importFiles(Paths.get(jpreferPerso.get("filesLocation", System.getProperty("user.dir"))));
							return null;
						}
					};
					worker.execute();
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
	}

	public void importFiles(Path path){
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
						successCount++;
					}else{
						System.out.println("=> Failure (Is it a DICOM file ? is there a password ?)\n");
					}
					conn.disconnect();
					totalFiles++;
					state.setText(successCount + "/" + totalFiles + " files were imported. (Fiji>Window>Console)");
					System.out.println(successCount + "/" + totalFiles + " files were imported.");
					return FileVisitResult.CONTINUE;
				}
			});		

		} catch (MalformedURLException e) {
			System.out.println("Bad URL");
		} catch (IOException e) {
			System.out.println("=> Unable to connect (Is Orthanc running ? Is there a password ?)\n");
		}
	}



	public static void main(String... args){
		ImportDCM vue = new ImportDCM();
		vue.setSize(1200,640);
		vue.setLocationRelativeTo(null);
		vue.setVisible(true);
		vue.pack();
	}

	@Override
	public void run(String arg0) {
		ImportDCM vue = new ImportDCM();
		vue.setSize(1200, 400);
		vue.setLocationRelativeTo(null);
		vue.pack();
		vue.setVisible(true);
	}
}
