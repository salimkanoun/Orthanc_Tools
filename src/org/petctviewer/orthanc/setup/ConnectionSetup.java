/*
Copyright (C) 2017 VONGSALAT Anousone

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

package org.petctviewer.orthanc.setup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class ConnectionSetup extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private Preferences jpreferPerso = Preferences.userRoot().node("<unnamed>/queryplugin");
	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");
	private JDialog gui=this;
	public boolean ok=false;

	public ConnectionSetup(Run_Orthanc orthanc){
		this.setTitle("Setup");
		this.setModal(true);
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel mainPanel = new JPanel();

		Image image = new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage();
		this.setIconImage(image);
		
		this.getContentPane().add(mainPanel);
		mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JPanel setupPanel = new JPanel();
		mainPanel.add(setupPanel);
		setupPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		setupPanel.setLayout(new BorderLayout(0, 0));
		
		
		JLabel lblExistingOrthancServer = new JLabel("Existing Orthanc Server");
		setupPanel.add(lblExistingOrthancServer, BorderLayout.NORTH);
		lblExistingOrthancServer.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel_http_settings = new JPanel();
		panel_http_settings.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel label = new JLabel("Address");
		panel_http_settings.add(label);
		
		JTextField ipTxt = new JTextField();
		panel_http_settings.add(ipTxt);
		ipTxt.setPreferredSize(new Dimension(100,18));
		ipTxt.setText(jpreferPerso.get("ip", "http://"));
		
		
		JLabel label_1 = new JLabel("Port");
		JTextField portTxt = new JTextField();
		portTxt.setPreferredSize(new Dimension(100,18));
		portTxt.setText(jpreferPerso.get("port", ""));
		
		
		JLabel label_2 = new JLabel("Username");
		JTextField usernameTxt = new JTextField();
		usernameTxt.setPreferredSize(new Dimension(100,18));
		usernameTxt.setText(jpreferPerso.get("username", ""));
		
		JLabel label_3 = new JLabel("Password");
		JPasswordField passwordTxt = new JPasswordField();
		passwordTxt.setPreferredSize(new Dimension(100,18));
		passwordTxt.setText(jpreferPerso.get("password", ""));
		
		
		panel_http_settings.add(label_1);
		panel_http_settings.add(portTxt);
		panel_http_settings.add(label_2);
		panel_http_settings.add(usernameTxt);
		panel_http_settings.add(label_3);
		panel_http_settings.add(passwordTxt);
		
		setupPanel.add(panel_http_settings);
		
		JButton submit = new JButton("Submit");
		setupPanel.add(submit, BorderLayout.SOUTH);
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ipTxt.getText().toLowerCase().startsWith("http://") || ipTxt.getText().toLowerCase().startsWith("https://")) {
					jpreferPerso.put("ip", ipTxt.getText());
					jpreferPerso.put("port", portTxt.getText());
					jpreferPerso.put("password", new String(passwordTxt.getPassword()));
					jpreferPerso.put("username", usernameTxt.getText());
					ok=true;
					dispose();
					
				}
				else {
					JOptionPane.showMessageDialog(gui,
						    "IP should start with http:// or https://");
					
				}
				
				
			}
		});
		
		JPanel panel_non_install = new JPanel();
		mainPanel.add(panel_non_install);
		panel_non_install.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_non_install.setLayout(new BorderLayout(0, 0));
		
		JLabel lblStartOrthancWithout = new JLabel("Start Orthanc without Install");
		lblStartOrthancWithout.setHorizontalAlignment(SwingConstants.CENTER);
		panel_non_install.add(lblStartOrthancWithout, BorderLayout.NORTH);
		
		JPanel button_non_install = new JPanel();
		panel_non_install.add(button_non_install, BorderLayout.SOUTH);
		
		//Display start or stop button depending on local run of orthanc
		JButton runOrthancLocal= new JButton("Run Local Orthanc");
		if(orthanc.getIsStarted()) {
			runOrthancLocal.setText("Stop Orthanc Local");
		}
		
		runOrthancLocal.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String os=System.getProperty("os.name");
			if(os.startsWith("Windows")) {
				if(runOrthancLocal.getText()=="Run Local Orthanc") {
					try {
						orthanc.copyOrthanc(null);
						dispose();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else {
					orthanc.stopOrthanc();
					dispose();
				}
			}
			else {
				JOptionPane.showMessageDialog(gui, "Only available for Windows", "Error", JOptionPane.ERROR_MESSAGE);
			}
				
			
			

			}
			
		
		});
		
		JButton btnReusableRun = new JButton("Re-usable Run");
		button_non_install.add(btnReusableRun);
		button_non_install.add(runOrthancLocal);
		
		btnReusableRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String os=System.getProperty("os.name");
				if(os.startsWith("Windows")) {	
					boolean copy=orthanc.isCopyAvailable();
					if (!copy) {
						JOptionPane.showMessageDialog(gui, "Set folder installation", "Orthanc Install", JOptionPane.WARNING_MESSAGE);
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File(jprefer.get("OrthancLocalPath", ".")));
						chooser.setDialogTitle("Install Orthanc in...");
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
							String install = chooser.getSelectedFile().getAbsolutePath();
							try {
								orthanc.copyOrthanc(install);
								jprefer.put("OrthancLocalPath", install);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					
				}
				dispose();
			}
		});
		
		FlowLayout fl_disclaimerPanel = new FlowLayout();
		fl_disclaimerPanel.setVgap(10);
		JPanel disclaimerPanel = new JPanel(fl_disclaimerPanel);
		getContentPane().add(disclaimerPanel, BorderLayout.NORTH);
		JLabel disclaimer = new JLabel("This service needs the installation of the Orthanc Server, see our");
		disclaimerPanel.add(disclaimer);
		JButton link = new JButton("<html><font color = 'blue'>documentation</font></html>");
		link.setFocusPainted(false);
		link.setMargin(new Insets(0, 0, 0, 0));
		link.setContentAreaFilled(false);
		link.setBorderPainted(false);
		link.setOpaque(false);
		link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		link.addActionListener( new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		        openWebPage("http://petctviewer.org/images/QuickSetupGuide_Networking_DICOM.pdf");
		    }
		});
		
		
		disclaimerPanel.add(link);
		setSize(1200, 400);
		pack();
	}
	
	public void openWebPage(String url){
		try {         
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		}
		catch (java.io.IOException e) {
		}
	}
}
