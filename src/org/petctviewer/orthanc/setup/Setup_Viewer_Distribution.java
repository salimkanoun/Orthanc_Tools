package org.petctviewer.orthanc.setup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.petctviewer.orthanc.anonymize.VueAnon;

public class Setup_Viewer_Distribution extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	
	private Preferences jprefer = VueAnon.jprefer;
	private JDialog dialogDowload = this;
	private JLabel lblFolder;
	
	/**
	 * Create the dialog.
	 */
	public Setup_Viewer_Distribution() {
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			{
				JPanel panel_3 = new JPanel();
				panel.add(panel_3);
				panel_3.setLayout(new GridLayout(0, 1, 10, 10));
				{
					JPanel panel_buttons_download = new JPanel();
					panel_3.add(panel_buttons_download);
					panel_buttons_download.setLayout(new GridLayout(0, 1, 0, 0));
					{
						JButton btnDownloadWaesisViewer = new JButton("Download Waesis Viewer");
						btnDownloadWaesisViewer.addActionListener(new ActionListener() {
							
							public void actionPerformed(ActionEvent arg0) {
								
								try {
									downloadAction(new URL("https://github.com/salimkanoun/Orthanc_Tools/releases/download/Viewers/weasis_3.0.4.zip"), btnDownloadWaesisViewer);
								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						});
						panel_buttons_download.add(btnDownloadWaesisViewer);
					}
					{
						JButton btnDownloadFijiViewer = new JButton("Download ImageJ Viewer");
						btnDownloadFijiViewer.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								try {
									
									downloadAction(new URL("https://github.com/salimkanoun/Orthanc_Tools/releases/download/Viewers/ImageJ.zip"), btnDownloadFijiViewer);
								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						panel_buttons_download.add(btnDownloadFijiViewer);
					}
					{
						JButton btnCustomViewerFolder = new JButton("Custom Viewer Folder");
						btnCustomViewerFolder.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								JFileChooser fc=new JFileChooser();
								fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								int ouvrir=fc.showOpenDialog(null);
								if (ouvrir==JFileChooser.APPROVE_OPTION){
									String viewerDirectory=fc.getSelectedFile().getAbsolutePath().toString();
									jprefer.put("viewerDistribution", viewerDirectory);
									updateFolder();
								}
							}
						});
						panel_buttons_download.add(btnCustomViewerFolder);
					}
				}
				{
					JPanel panel_1 = new JPanel();
					panel_3.add(panel_1);
					panel_1.setLayout(new BorderLayout(0, 0));
					{
						JLabel lblPath = new JLabel("Path : ");
						panel_1.add(lblPath, BorderLayout.NORTH);
					}
					{
						lblFolder = new JLabel("N/A");
						panel_1.add(lblFolder, BorderLayout.CENTER);
					}
				}
			}
		}
		{
			JLabel lblViewerDistribution = new JLabel("Viewer Distribution");
			lblViewerDistribution.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblViewerDistribution, BorderLayout.NORTH);
		}
		{
			JLabel lblNewLabel = new JLabel("Do not delete viewer folder");
			lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			contentPanel.add(lblNewLabel, BorderLayout.SOUTH);
		}
		updateFolder();
	}

	private void downloadAction(URL url , JButton button) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select folder for CD/DVD output");
		chooser.setDialogTitle("Dowload Viewer to...");
		if (jprefer.get("viewerDistribution", null) !=null ) {
			chooser.setSelectedFile(new File (jprefer.get("viewerDistribution", null)));
		}
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			if(chooser.getSelectedFile().list().length >0) {
				JOptionPane.showMessageDialog(contentPanel, "Please Choose an empty directory", "Not empty", JOptionPane.ERROR_MESSAGE);
				return;
			}
			//SK Peut ajouter un check pour verifier que le repertoire est vide avant de telecharger le viewer
			String btnText=button.getText();
			button.setBackground(Color.ORANGE);
			SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() {
					try {
						chooser.getSelectedFile().mkdirs();
						File zipTemp=File.createTempFile("OT_Viewer", ".zip");
						button.setText("Downloading");
						FileUtils.copyURLToFile(url, zipTemp);
						unzip(zipTemp, chooser.getSelectedFile());
						zipTemp.delete();
						//Message confirmation
						JOptionPane.showMessageDialog(dialogDowload, "Viewer distribution sucessfully downloaded");
					} catch (IOException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(dialogDowload, "Download Failed",  "Error", JOptionPane.ERROR_MESSAGE);
					}
					return null;
				}
	
				@Override
				protected void done(){
					// Enregistre la destination du fichier dans le registery
					button.setText(btnText);
					jprefer.put("viewerDistribution", chooser.getSelectedFile().getAbsolutePath());
					updateFolder();
					button.setBackground(null);
				}
			};
			worker.execute();
		}
	}
	
	private void updateFolder() {
		lblFolder.setText(jprefer.get("viewerDistribution", "N/A"));
		this.pack();
	}
	
	
	private void unzip(File zipFile, File destination){
	     byte[] buffer = new byte[1024];
	     try {	    	
	    	//get the zip file content
	    	ZipInputStream zis;
			zis = new ZipInputStream(new FileInputStream(zipFile));
			
	    	//get the zipped file list entry
	    	ZipEntry ze = zis.getNextEntry();
	    	
	    	while(ze!=null){
	     	   	String fileName = ze.getName();
	     	    
	     	   File newFile = new File(destination+File.separator+fileName);
	            
	            if (ze.isDirectory()) {
	         	// if the entry is a directory, make the directory
	                newFile.mkdirs();
	            }
	            else {
	         	    new File(newFile.getParent()).mkdirs();
	                 //create all non exists folders else you will hit FileNotFoundException for compressed folder
	                 FileOutputStream fos = new FileOutputStream(newFile);
	                 int len;
	                 while ((len = zis.read(buffer)) > 0) {
	            		fos.write(buffer, 0, len);
	                 }
	
	                 fos.close();
	                 
	            }
	            ze = zis.getNextEntry();
	     	}
	        zis.closeEntry();
	    	zis.close();
	    
	     } catch (IOException e) {
				e.printStackTrace();
			}
	}

}
