/**
Copyright (C) 2017 KANOUN Salim

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

package org.petctviewer.orthanc.monitoring.cdburner;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.petctviewer.orthanc.anonymize.VueAnon;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class Burner_Settings extends JDialog {

	private Preferences jPrefer = VueAnon.jprefer;
	private JDialog dialogSettings;
	
	private JLabel imageJPath;
	private JLabel epsonDirectoryLabel;
	private JLabel labelFilePath;
	private JSpinner spinnerTiming;
	private JComboBox<String> comboBoxSupportType, comboBoxBurnerManufacturer, levelMonitoring, dateFormatChoice;
	private JCheckBox chckbxDeleteSentStudies;
	
	/**
	 * Create the dialog.
	 */
	public Burner_Settings() {
		dialogSettings=this;
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 750, 300);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton imageJ = new JButton("Set Viewer Directory");
		imageJ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc=new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ouvrir=fc.showOpenDialog(null);
				if (ouvrir==JFileChooser.APPROVE_OPTION){
					String fijiDirectory=fc.getSelectedFile().getAbsolutePath().toString();
					imageJPath.setText(fijiDirectory);
				}
			}
		});
	
		JLabel lblDiscburnerManufacturer = new JLabel("DiscBurner Manufacturer : ");
		lblDiscburnerManufacturer.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblDiscburnerManufacturer);
		
		comboBoxBurnerManufacturer = new JComboBox<String>(new String[] {"Epson", "Primera"});
		comboBoxBurnerManufacturer.addItemListener(new ItemListener() {
		
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(comboBoxBurnerManufacturer.getSelectedItem().equals("Primera")) {
					comboBoxSupportType.setEnabled(false);
				}else {
					comboBoxSupportType.setEnabled(true);
				}
				
			}
			
		});
		
		contentPanel.add(comboBoxBurnerManufacturer);
		contentPanel.add(imageJ);
	
	
		imageJPath = new JLabel();
		contentPanel.add(imageJPath);
	
	
		JButton labelFileButton = new JButton("Set Label File");
		labelFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc=new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int ouvrir=fc.showOpenDialog(null);
				if (ouvrir==JFileChooser.APPROVE_OPTION){
					String labelFile=fc.getSelectedFile().getAbsolutePath().toString();
					labelFilePath.setText(labelFile);
				}
			}
		});
		contentPanel.add(labelFileButton);
	
	
		labelFilePath = new JLabel();
		contentPanel.add(labelFilePath);
	
	
		JButton epsonDirectoryButton = new JButton("Set Monitored Directory");
		epsonDirectoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc=new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ouvrir=fc.showOpenDialog(null);
				if (ouvrir==JFileChooser.APPROVE_OPTION){
					String epsonDirectory=fc.getSelectedFile().getAbsolutePath().toString();
					epsonDirectoryLabel.setText(epsonDirectory);
				}
			}
		});
		contentPanel.add(epsonDirectoryButton);
	
	
		epsonDirectoryLabel = new JLabel();
		contentPanel.add(epsonDirectoryLabel);
	
	
		JPanel panel = new JPanel();
		contentPanel.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblDateFormat = new JLabel("Date Format");
		lblDateFormat.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblDateFormat);
		
		dateFormatChoice=new JComboBox<String> (new String[] {"yyyyMMdd","dd/MM/yyyy","MM/dd/yyyy"});
		contentPanel.add(dateFormatChoice);
	
		JLabel CdType = new JLabel("CD/DVD Type");
		CdType.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(CdType);
	
	
		JPanel panel_1 = new JPanel();
		contentPanel.add(panel_1);
		
		comboBoxSupportType = new JComboBox<String>();
		comboBoxSupportType.setModel(new DefaultComboBoxModel<String>(new String[] {"Auto", "CD", "DVD"}));
		
		panel_1.add(comboBoxSupportType);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//On sauve dans le registery
				jPrefer.put("Burner_buernerManufacturer", (String) comboBoxBurnerManufacturer.getSelectedItem());
				
				if (imageJPath.getText()!=null) jPrefer.put("Burner_fijiDirectory", imageJPath.getText());
				if (labelFilePath.getText()!=null) jPrefer.put("Burner_labelFile", labelFilePath.getText());
				if (epsonDirectoryLabel.getText()!=null) jPrefer.put("Burner_epsonDirectory", epsonDirectoryLabel.getText());
				;
				jPrefer.putBoolean("Burner_deleteStudies", chckbxDeleteSentStudies.isSelected());
				jPrefer.put("Burner_suportType", comboBoxSupportType.getSelectedItem().toString());
				//On ajoute la string du format date
				jPrefer.put("Burner_DateFormat", dateFormatChoice.getSelectedItem().toString());
				jPrefer.putInt("Burner_monitoringTime", (int) spinnerTiming.getValue());
				
				if(levelMonitoring.getSelectedItem().equals("Patient")) {
					jPrefer.putBoolean("Burner_levelPatient", true);
				}
				//on dispose 
				dispose();
			}
		});
		
		chckbxDeleteSentStudies = new JCheckBox("Delete Sent Studies");
		chckbxDeleteSentStudies.setSelected(jPrefer.getBoolean("Burner_deleteStudies", false));
		chckbxDeleteSentStudies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxDeleteSentStudies.isSelected()) {
					JOptionPane.showMessageDialog(dialogSettings,
						    "With this Options, once sent to the burner Orthanc will remove stored studies \n"
						    + "This will mean Orthanc will act only as a Burner manager and will not act as a storage/PACS system",
						    "Delete warning",
						    JOptionPane.WARNING_MESSAGE);
					
				}
					
			}
		});
		buttonPane.add(chckbxDeleteSentStudies);
		
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JLabel lblMonitorEachsec = new JLabel("Monitor Each (sec)");
		lblMonitorEachsec.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel2 = new JPanel();
		spinnerTiming = new JSpinner();
		spinnerTiming.setModel(new SpinnerNumberModel(new Integer(30), new Integer(10), null, new Integer(1)));
		panel2.add(spinnerTiming);
	
		
		JLabel lblLevel=new JLabel("Monitoring Level");
		lblLevel.setHorizontalAlignment(SwingConstants.CENTER);
		levelMonitoring = new JComboBox<String>(new String[] {"Study", "Patient"});
		
		contentPanel.add(lblLevel);
		contentPanel.add(new JPanel().add(levelMonitoring));
		contentPanel.add(lblMonitorEachsec);
		contentPanel.add(panel2);
		
		setCDPreference();

	}
	
	public void setCDPreference() {
		//On prends les settings du registery
		comboBoxBurnerManufacturer.setSelectedItem(jPrefer.get("Burner_buernerManufacturer", "Epson"));
		imageJPath.setText(jPrefer.get("Burner_fijiDirectory", null));
		labelFilePath.setText(jPrefer.get("Burner_labelFile", null));
		epsonDirectoryLabel.setText(jPrefer.get("Burner_epsonDirectory", null));
		dateFormatChoice.setSelectedItem(jPrefer.get("Burner_DateFormat", "yyyyMMdd"));
		chckbxDeleteSentStudies.setSelected(jPrefer.getBoolean("Burner_deleteStudies", false));
		comboBoxSupportType.setSelectedItem(jPrefer.get("Burner_suportType", "Auto"));
		spinnerTiming.setValue(jPrefer.getInt("Burner_monitoringTime", 30));
		if(jPrefer.getBoolean("Burner_levelPatient", true)) {
			levelMonitoring.setSelectedItem("Patient");
		}
	}	
	

}
