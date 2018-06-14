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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import ij.plugin.PlugIn;

public class ConnectionSetup extends JDialog implements PlugIn{
	
	private static final long serialVersionUID = 1L;
	private Preferences jpreferPerso = Preferences.userRoot().node("<unnamed>/queryplugin");
	private JDialog gui=this;

	
	public ConnectionSetup(Run_Orthanc orthanc){
		this.setTitle("Setup");
		this.setModal(true);
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		JPanel setupPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JPanel disclaimerPanel = new JPanel(new FlowLayout());
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
		
		JTextField ipTxt = new JTextField();
		ipTxt.setPreferredSize(new Dimension(100,18));
		JTextField portTxt = new JTextField();
		portTxt.setPreferredSize(new Dimension(100,18));
		JTextField usernameTxt = new JTextField();
		usernameTxt.setPreferredSize(new Dimension(100,18));
		JPasswordField passwordTxt = new JPasswordField();
		passwordTxt.setPreferredSize(new Dimension(100,18));
		
		ipTxt.setText(jpreferPerso.get("ip", "http://"));
		portTxt.setText(jpreferPerso.get("port", ""));
		usernameTxt.setText(jpreferPerso.get("username", ""));
		passwordTxt.setText(jpreferPerso.get("password", ""));
		
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		setupPanel.add(new JLabel("Address"), gbc);
		gbc.gridx = 1;
		setupPanel.add(ipTxt, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		setupPanel.add(new JLabel("Port"), gbc);
		gbc.gridx = 1;
		setupPanel.add(portTxt, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		setupPanel.add(new JLabel("Username"), gbc);
		gbc.gridx = 1;
		setupPanel.add(usernameTxt, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		setupPanel.add(new JLabel("Password"), gbc);
		gbc.gridx = 1;
		setupPanel.add(passwordTxt, gbc);
		
		JButton submit = new JButton("Submit");
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ipTxt.getText().toLowerCase().startsWith("http://") || ipTxt.getText().toLowerCase().startsWith("https://")) {
					jpreferPerso.put("ip", ipTxt.getText());
					jpreferPerso.put("port", portTxt.getText());
					jpreferPerso.put("password", new String(passwordTxt.getPassword()));
					jpreferPerso.put("username", usernameTxt.getText());
					JOptionPane.showMessageDialog(gui,
					    "please restart app");
					dispose();
				}
				else {
					JOptionPane.showMessageDialog(gui,
						    "IP should start with http:// or https://");
					
				}
				
				
			}
		});
		
		
		//Display start or stop button depending on local run of orthanc
			JButton runOrthancLocal;
			if(orthanc.getIsStarted()) {
				runOrthancLocal = new JButton("Stop Orthanc Local");
			}
			else {
				runOrthancLocal = new JButton("Run Local Orthanc");
			}
				

		
		
		runOrthancLocal.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String os=System.getProperty("os.name");
				if(os.startsWith("Windows")) {
					if(runOrthancLocal.getText()=="Run Local Orthanc") {
						try {
							orthanc.start();
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
		
		gbc.gridy = 4;
		setupPanel.add(submit, gbc);
		
		Image image = new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage();
		this.setIconImage(image);
		mainPanel.add(disclaimerPanel);
		mainPanel.add(setupPanel);
		mainPanel.add(runOrthancLocal);
		this.getContentPane().add(mainPanel);
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
	
	//IJ run method
	@Override
	public void run(String arg0) {
		ConnectionSetup vue = new ConnectionSetup(new Run_Orthanc());
		vue.setSize(1200, 400);
		vue.pack();
		vue.setVisible(true);
	}
}
