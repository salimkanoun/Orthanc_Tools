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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.petctviewer.orthanc.anonymize.VueAnon;

public class ConnectionSetup extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private Preferences jpreferPerso = VueAnon.jprefer;
	private JDialog gui=this;
	private JTextField ipTxt,portTxt,usernameTxt;
	private JPasswordField passwordTxt;
	private JSpinner spinnerServerChoice;

	public ConnectionSetup(Run_Orthanc orthanc, VueAnon vueAnon){
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
		
		JLabel lblServer = new JLabel("Server");
		panel_http_settings.add(lblServer);
		
		spinnerServerChoice = new JSpinner();
		spinnerServerChoice.setModel(new SpinnerNumberModel(1, 1, 10, 1));
		panel_http_settings.add(spinnerServerChoice);
		
		JLabel label = new JLabel("Address");
		panel_http_settings.add(label);
		
		ipTxt = new JTextField();
		ipTxt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				int index=(int) spinnerServerChoice.getValue();
				jpreferPerso.put("ip"+index, ipTxt.getText());
			}
		});
		panel_http_settings.add(ipTxt);
		ipTxt.setPreferredSize(new Dimension(100,18));
		
		
		JLabel label_1 = new JLabel("Port");
		portTxt = new JTextField();
		portTxt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int index=(int) spinnerServerChoice.getValue();
				jpreferPerso.put("port"+index, portTxt.getText());
			}
		});
		portTxt.setPreferredSize(new Dimension(100,18));
		
		
		JLabel label_2 = new JLabel("Username");
		usernameTxt = new JTextField();
		usernameTxt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int index=(int) spinnerServerChoice.getValue();
				jpreferPerso.put("username"+index, usernameTxt.getText());
			}
		});
		usernameTxt.setPreferredSize(new Dimension(100,18));
		
		JLabel label_3 = new JLabel("Password");
		passwordTxt = new JPasswordField();
		passwordTxt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int index=(int) spinnerServerChoice.getValue();
				jpreferPerso.put("password"+index, new String(passwordTxt.getPassword()));
			}
		});
		passwordTxt.setPreferredSize(new Dimension(100,18));
		
		panel_http_settings.add(label_1);
		panel_http_settings.add(portTxt);
		panel_http_settings.add(label_2);
		panel_http_settings.add(usernameTxt);
		panel_http_settings.add(label_3);
		panel_http_settings.add(passwordTxt);
		
		setupPanel.add(panel_http_settings);
		
		spinnerServerChoice.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int number=(int) spinnerServerChoice.getValue();
				fillParameter(number);
			}
		});
		

		JButton submit = new JButton("Submit");
		setupPanel.add(submit, BorderLayout.SOUTH);
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ipTxt.getText().toLowerCase().startsWith("http://") || ipTxt.getText().toLowerCase().startsWith("https://")) {
					jpreferPerso.putInt("currentOrthancServer", (int) spinnerServerChoice.getValue());
					vueAnon.getOrthancApisConnexion().refreshServerAddress();
					if(vueAnon.isVisible()) {
						vueAnon.refreshAets();
						vueAnon.refreshPeers();
					}
					
					
					//SK RESTE A FERMER EVENTUELLE FENETRE QUERY ET IMPORT
					//REPERCUSSION SUR LE MONITORING A VOIR
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
		JButton runOrthancLocal= new JButton("Run Temporary Orthanc");
		if(orthanc.getIsStarted()) {
			runOrthancLocal.setText("Stop Temporary Orthanc");
		}
		
		runOrthancLocal.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				if(runOrthancLocal.getText()=="Run Temporary Orthanc") {
					try {
						orthanc.copyOrthanc(null);
						orthanc.startOrthanc();
						dispose();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}else {
					orthanc.stopOrthanc(vueAnon.getOrthancApisConnexion());
					dispose();
				}
			}
		});
		
		JButton btnReusableRun = new JButton("Re-usable Run");
		button_non_install.add(btnReusableRun);
		button_non_install.add(runOrthancLocal);
		
		btnReusableRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
					boolean copy=orthanc.isCopyAvailable();
					if (!copy) {
						JOptionPane.showMessageDialog(gui, "Set folder installation", "Orthanc Install", JOptionPane.WARNING_MESSAGE);
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File(jpreferPerso.get("OrthancLocalPath", ".")));
						chooser.setDialogTitle("Install Orthanc in...");
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
							String path = chooser.getSelectedFile().getAbsolutePath();
							try {
								orthanc.copyOrthanc(path);
								jpreferPerso.put("OrthancLocalPath", path);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}else {
						orthanc.startOrthanc();
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
		
		fillParameter(jpreferPerso.getInt("currentOrthancServer", 1));
		
		selectServerSpinner();

		setSize(1200, 400);
		pack();
	}
	
	private void fillParameter(int number) {
		ipTxt.setText(jpreferPerso.get("ip"+number, "http://localhost"));
		portTxt.setText(jpreferPerso.get("port"+number, "8042"));
		passwordTxt.setText(jpreferPerso.get("password"+number, null));
		usernameTxt.setText(jpreferPerso.get("username"+number, null));
	}
	
	private void selectServerSpinner() {
		int choiceValue=jpreferPerso.getInt("currentOrthancServer", 1);
		this.spinnerServerChoice.setValue(choiceValue);
		fillParameter(choiceValue);
	}
	
	private void openWebPage(String url){
		try {         
			Desktop.getDesktop().browse(URI.create(url));
		}
		catch (IOException e) {
		}
	}
}
