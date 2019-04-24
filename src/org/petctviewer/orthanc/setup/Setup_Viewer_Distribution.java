package org.petctviewer.orthanc.setup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.petctviewer.orthanc.anonymize.VueAnon;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;

public class Setup_Viewer_Distribution extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	
	private Preferences jprefer = VueAnon.jprefer;
	private JDialog dialogDowload = this;
	private JLabel lblFolder;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Setup_Viewer_Distribution dialog = new Setup_Viewer_Distribution();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Setup_Viewer_Distribution() {
		setBounds(100, 100, 450, 300);
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
									downloadAction(new URL("http://petctviewer.org/images/ImageJ.zip"), btnDownloadWaesisViewer);
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
									downloadAction(new URL("http://petctviewer.org/images/ImageJ.zip"), btnDownloadFijiViewer);
								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						panel_buttons_download.add(btnDownloadFijiViewer);
					}
				}
				{
					JPanel panel_1 = new JPanel();
					panel_3.add(panel_1);
					panel_1.setLayout(new BorderLayout(0, 0));
					{
						JButton btnCustomViewerFolder = new JButton("Custom Viewer Folder");
						panel_1.add(btnCustomViewerFolder, BorderLayout.CENTER);
					}
					{
						JPanel panel_2 = new JPanel();
						panel_1.add(panel_2, BorderLayout.EAST);
						{
							lblFolder = new JLabel("N/A");
							panel_2.add(lblFolder);
						}
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
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private void downloadAction(URL url , JButton button) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select folder for CD/DVD output");
		chooser.setSelectedFile(new File("ImageJ.zip"));
		chooser.setDialogTitle("Dowload Viewer to...");
		if (! jprefer.get("viewerDistribution", "empty").equals("empty") ) {
			chooser.setSelectedFile(new File (jprefer.get("viewerDistribution", "empty")));
		}
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			button.setBackground(Color.ORANGE);
			SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() {
					try {
						FileUtils.copyURLToFile(url, chooser.getSelectedFile());
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
					jprefer.put("viewerDistribution", chooser.getSelectedFile().toString());
					updateFolder();
					button.setBackground(null);
				}
			};
			worker.execute();
		}
	}
	
	private void updateFolder() {
		lblFolder.setText(jprefer.get("viewerDistribution", "N/A"));
	}

}
