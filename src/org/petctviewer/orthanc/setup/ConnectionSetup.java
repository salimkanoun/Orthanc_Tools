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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.petctviewer.orthanc.anonymize.VueAnon;

import ij.plugin.PlugIn;
import test.Run_Orthanc;

public class ConnectionSetup extends JFrame implements PlugIn{
	
	private static final long serialVersionUID = 1L;
	private Preferences jpreferPerso = Preferences.userRoot().node("<unnamed>/queryplugin");
	private JFrame gui=this;
	private Run_Orthanc orthanc;
	
	public ConnectionSetup(){
		
		this.setTitle("Setup");
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
		
		
		
		//SK A VOIR COMMENT INSERER LE DEMARAGE DE ORTHANC LOCAL ET GERER LE STOP
		// ET LE REFRESH DE LA CONNEXION // PEUT ETRE REMPLACER L OBJET CONNEXION DANS VUE ANON
		JButton runOrthancLocal = new JButton("Run Local Orthanc");
		
		runOrthancLocal.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
					if(runOrthancLocal.getText()=="Run Local Orthanc") {
					SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() {
							runOrthancLocal.setText("Stop Orthanc Local");
								orthanc= new Run_Orthanc();
								try {
									orthanc.start();
									VueAnon anon=new VueAnon();
									anon.setVisible(true);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							return null;
						}

						@Override
						protected void done(){
							//;
						}
					};
					worker.execute();
					}else {
						orthanc.stopOrthanc();
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
	}
	
	public void openWebPage(String url){
		try {         
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		}
		catch (java.io.IOException e) {
		}
	}
	
	public static void main(String... arg0){
		ConnectionSetup vue = new ConnectionSetup();
		vue.setSize(1200, 400);
		vue.pack();
		vue.setVisible(true);
	}
	
	@Override
	public void run(String arg0) {
		ConnectionSetup vue = new ConnectionSetup();
		vue.setSize(1200, 400);
		vue.pack();
		vue.setVisible(true);
	}
}
