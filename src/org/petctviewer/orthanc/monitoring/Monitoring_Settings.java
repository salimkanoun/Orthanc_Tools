package org.petctviewer.orthanc.monitoring;

import java.awt.Dimension;
import java.util.prefs.Preferences;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.petctviewer.orthanc.anonymize.VueAnon;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class Monitoring_Settings extends JDialog {

	private Preferences jprefer = VueAnon.jprefer;
	private JTextField dbPort, dbName, dbUsername, dbadress;
	private JPasswordField dbPassword ;

	private JPanel panel;
	private JButton btnOk;
	private JCheckBox chckbxUseMysql;
	private JCheckBox chckbxAutodelete;

	public static void main(String[] args) {
		Monitoring_Settings settings=new Monitoring_Settings();
		settings.setVisible(true);

	}
	
	public Monitoring_Settings() {
		JPanel setting_Panel = new JPanel();
		setting_Panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		setting_Panel.add(new JLabel("Adress"));
		
		dbadress = new JTextField();
		dbadress.setText(jprefer.get("dbAdress", ""));
		setting_Panel.add(dbadress);
		dbadress.setColumns(10);
		
		setting_Panel.add(new JLabel("Port"));
		
		
		dbPort = new JTextField();
		dbPort.setText(jprefer.get("dbPort", ""));
		dbPort.setPreferredSize(new Dimension(100,20));
		setting_Panel.add(dbPort);
		
		
		setting_Panel.add(new JLabel("Database name"));
		
		dbName = new JTextField();
		dbName.setText(jprefer.get("dbName", ""));
		dbName.setPreferredSize(new Dimension(100,20));
		setting_Panel.add(dbName);
		
		
		setting_Panel.add(new JLabel("Username"));
		

		dbUsername = new JTextField();
		dbUsername.setText(jprefer.get("dbUsername", ""));
		dbUsername.setPreferredSize(new Dimension(100,20));
		setting_Panel.add(dbUsername);
		
		
		setting_Panel.add(new JLabel("Password"));
		
		
		dbPassword = new JPasswordField();
		dbPassword.setText(jprefer.get("dbPassword", ""));
		dbPassword.setPreferredSize(new Dimension(100,20));
		setting_Panel.add(dbPassword);
		
		getContentPane().add(setting_Panel);
		
		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveInRegistery();
				dispose();
			}
		});
		
		chckbxUseMysql = new JCheckBox("Use MySQL");
		chckbxUseMysql.setSelected(jprefer.getBoolean("useDBMonitoring", false));
		panel.add(chckbxUseMysql);
		
		chckbxAutodelete = new JCheckBox("Auto-Delete");
		chckbxAutodelete.setToolTipText("Dicom Deletion after tag extraction");
		chckbxAutodelete.setSelected(jprefer.getBoolean("AutoDeleteMonitoring", false));
		panel.add(chckbxAutodelete);
		panel.add(btnOk);
		pack();
	}
	
	private void saveInRegistery() {
			jprefer.put("dbPort", dbPort.getText());
			jprefer.put("dbName", dbName.getText());
			jprefer.put("dbUsername", dbUsername.getText());
			jprefer.put("dbPassword", new String(dbPassword.getPassword()));
			jprefer.put("dbAdress", dbadress.getText());
			jprefer.putBoolean("useDBMonitoring", chckbxUseMysql.isSelected());
			jprefer.putBoolean("AutoDeleteMonitoring", chckbxAutodelete.isSelected());
			
		
		
	}
	

}
