package org.petctviewer.orthanc.monitoring;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Monitoring_Settings extends JFrame {

	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");
	private JTextField dbPort, dbName,dbUsername, dbAdress;
	private JPasswordField dbPassword ;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		Monitoring_Settings settings=new Monitoring_Settings();
		settings.setVisible(true);

	}
	
	public Monitoring_Settings() {
		JPanel eastDB = new JPanel(new GridBagLayout());
		GridBagConstraints gbSetup = new GridBagConstraints();
		gbSetup.gridx = 0;
		gbSetup.gridy = 0;
		eastDB.add(new JLabel("Adress"), gbSetup);
		
		gbSetup.gridx = 1;
		gbSetup.gridy = 0;
		dbAdress = new JTextField();
		dbAdress.setText(jprefer.get("dbAdress", ""));
		dbAdress.setPreferredSize(new Dimension(100,20));
		eastDB.add(dbAdress, gbSetup);
		
		gbSetup.gridx = 0;
		gbSetup.gridy = 1;
		eastDB.add(new JLabel("Port"), gbSetup);
		
		gbSetup.gridx = 1;
		gbSetup.gridy = 1;
		dbPort = new JTextField();
		dbPort.setText(jprefer.get("dbPort", ""));
		dbPort.setPreferredSize(new Dimension(100,20));
		eastDB.add(dbPort, gbSetup);
		
		gbSetup.gridx = 0;
		gbSetup.gridy = 2;
		eastDB.add(new JLabel("Database name"), gbSetup);
		
		gbSetup.gridx = 1;
		gbSetup.gridy = 2;
		dbName = new JTextField();
		dbName.setText(jprefer.get("dbName", ""));
		dbName.setPreferredSize(new Dimension(100,20));
		eastDB.add(dbName, gbSetup);
		
		gbSetup.gridx = 0;
		gbSetup.gridy = 3;
		eastDB.add(new JLabel("Username"), gbSetup);
		
		gbSetup.gridx = 1;
		gbSetup.gridy = 3;
		dbUsername = new JTextField();
		dbUsername.setText(jprefer.get("dbUsername", ""));
		dbUsername.setPreferredSize(new Dimension(100,20));
		eastDB.add(dbUsername, gbSetup);
		
		gbSetup.gridx = 0;
		gbSetup.gridy = 4;
		eastDB.add(new JLabel("Password"), gbSetup);
		
		gbSetup.gridx = 1;
		gbSetup.gridy = 4;
		dbPassword = new JPasswordField();
		dbPassword.setText(jprefer.get("dbPassword", ""));
		dbPassword.setPreferredSize(new Dimension(100,20));
		eastDB.add(dbPassword, gbSetup);
		
		this.add(eastDB);
		pack();
		
	}
	
	//SK A APPELER AU DISPOSE
	private void saveInRegistery() {
			jprefer.put("dbPort", dbPort.getText());
			jprefer.put("dbName", dbName.getText());
			jprefer.put("dbUsername", dbUsername.getText());
			jprefer.put("dbPassword", new String(dbPassword.getPassword()));
			jprefer.put("dbAdress", dbAdress.getText());
		
		
	}

}
