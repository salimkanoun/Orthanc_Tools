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

package org.petctviewer.orthanc.monitoring;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class Burner_Settings extends JDialog {

	private final JPanel contentPanel = new JPanel();
	
	private JLabel imageJPath;
	private JLabel epsonDirectoryLabel;
	private JLabel labelFilePath;
	
	private Preferences jPrefer = null;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	private JDialog dialogSettings;

	private String dateFormatChoix;
	private String labelFile;
	private String epsonDirectory;
	private String fijiDirectory;
	private String burnerManifacturer;
	private String suportType;
	private JSpinner spinnerTiming;
	private Boolean deleteStudies;
	private int monitoringTime;
	private JComboBox<String> comboBoxSupportType, comboBoxBurnerManufacturer;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Burner_Settings dialog = new Burner_Settings();
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Burner_Settings() {
		dialogSettings=this;
		jPrefer = Preferences.userNodeForPackage(Burner_Settings.class);
		jPrefer = jPrefer.node("CDburner");
		setCDPreference();
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 750, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		{
			JButton imageJ = new JButton("Set Viewer Directory");
			imageJ.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc=new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int ouvrir=fc.showOpenDialog(null);
					if (ouvrir==JFileChooser.APPROVE_OPTION){
						fijiDirectory=fc.getSelectedFile().getAbsolutePath().toString();
						imageJPath.setText(fijiDirectory);
					}
				}
			});
			{
				JLabel lblDiscburnerManufacturer = new JLabel("DiscBurner Manufacturer : ");
				lblDiscburnerManufacturer.setHorizontalAlignment(SwingConstants.CENTER);
				contentPanel.add(lblDiscburnerManufacturer);
			}
			{
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
			}
			contentPanel.add(imageJ);
		}
		{
			imageJPath = new JLabel(fijiDirectory);
			contentPanel.add(imageJPath);
		}
		{
			JButton labelFileButton = new JButton("Set Label File");
			labelFileButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc=new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int ouvrir=fc.showOpenDialog(null);
					if (ouvrir==JFileChooser.APPROVE_OPTION){
						labelFile=fc.getSelectedFile().getAbsolutePath().toString();
						labelFilePath.setText(labelFile);
					}
				}
			});
			contentPanel.add(labelFileButton);
		}
		{
			labelFilePath = new JLabel(labelFile);
			contentPanel.add(labelFilePath);
		}
		{
			JButton epsonDirectoryButton = new JButton("Set Monitored Directory");
			epsonDirectoryButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc=new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int ouvrir=fc.showOpenDialog(null);
					if (ouvrir==JFileChooser.APPROVE_OPTION){
						epsonDirectory=fc.getSelectedFile().getAbsolutePath().toString();
						epsonDirectoryLabel.setText(epsonDirectory);
					}
				}
			});
			contentPanel.add(epsonDirectoryButton);
		}
		{
			epsonDirectoryLabel = new JLabel(epsonDirectory);
			contentPanel.add(epsonDirectoryLabel);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JLabel lblDateFormat = new JLabel("Date Format");
				lblDateFormat.setHorizontalAlignment(SwingConstants.CENTER);
				panel.add(lblDateFormat);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JRadioButton rdbtnYyyymmdd = new JRadioButton("yyyyMMdd");
				rdbtnYyyymmdd.setActionCommand("yyyyMMdd");
				buttonGroup.add(rdbtnYyyymmdd);
				panel.add(rdbtnYyyymmdd);
				if (rdbtnYyyymmdd.getActionCommand().equals(dateFormatChoix) || dateFormatChoix==null) rdbtnYyyymmdd.setSelected(true);
			}
			{
				JRadioButton rdbtnDdmmyyyy = new JRadioButton("dd/MM/yyyy");
				rdbtnDdmmyyyy.setActionCommand("dd/MM/yyyy");
				buttonGroup.add(rdbtnDdmmyyyy);
				panel.add(rdbtnDdmmyyyy);
				if (rdbtnDdmmyyyy.getActionCommand().equals(dateFormatChoix)) rdbtnDdmmyyyy.setSelected(true);
			}
			{
				JRadioButton rdbtnMmddyyyy = new JRadioButton("MM/dd/yyyy");
				rdbtnMmddyyyy.setActionCommand("MM/dd/yyyy");
				buttonGroup.add(rdbtnMmddyyyy);
				panel.add(rdbtnMmddyyyy);
				{
					JLabel CdType = new JLabel("CD/DVD Type");
					CdType.setHorizontalAlignment(SwingConstants.CENTER);
					contentPanel.add(CdType);
				}
				{
					JPanel panel_1 = new JPanel();
					contentPanel.add(panel_1);
					{
						comboBoxSupportType = new JComboBox<String>();
						comboBoxSupportType.setModel(new DefaultComboBoxModel<String>(new String[] {"Auto", "CD", "DVD"}));
						comboBoxSupportType.setSelectedItem(suportType);
						panel_1.add(comboBoxSupportType);
					}
				}
				if (rdbtnMmddyyyy.getActionCommand().equals(dateFormatChoix)) rdbtnMmddyyyy.setSelected(true);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//On sauve dans le registery
						jPrefer.put("buernerManufacturer", (String) comboBoxBurnerManufacturer.getSelectedItem());
						if (epsonDirectory!=null) jPrefer.put("epsonDirectory", epsonDirectory);
						if (fijiDirectory!=null) jPrefer.put("fijiDirectory", fijiDirectory);
						if (labelFile!=null) jPrefer.put("labelFile", labelFile);
						if (deleteStudies!=null) jPrefer.putBoolean("deleteStudies", deleteStudies);
						jPrefer.put("suportType", comboBoxSupportType.getSelectedItem().toString());
						//On ajoute la string du format date
						jPrefer.put("DateFormat", buttonGroup.getSelection().getActionCommand());
						jPrefer.putInt("monitoringTime", (int) spinnerTiming.getValue());
						//on dispose 
						dispose();
					}
				});
				{
					JCheckBox chckbxDeleteSentStudies = new JCheckBox("Delete Sent Studies");
					chckbxDeleteSentStudies.setSelected(jPrefer.getBoolean("deleteStudies", false));
					chckbxDeleteSentStudies.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							if (chckbxDeleteSentStudies.isSelected()) {
								JOptionPane.showMessageDialog(dialogSettings,
									    "With this Options, once sent to the burner Orthanc will remove stored studies \n"
									    + "This will mean Orthanc will act only as a Burner manager and will not act as a storage/PACS system",
									    "Delete warning",
									    JOptionPane.WARNING_MESSAGE);
								
							}
							deleteStudies=chckbxDeleteSentStudies.isSelected();
								
						}
					});
					buttonPane.add(chckbxDeleteSentStudies);
				}
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				//Select the Burner set in the options registery
				comboBoxBurnerManufacturer.setSelectedItem(burnerManifacturer);
			}
		}
		{
			JLabel lblMonitorEachsec = new JLabel("Monitor Each (sec)");
			lblMonitorEachsec.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblMonitorEachsec);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
		
			spinnerTiming = new JSpinner();
			spinnerTiming.setModel(new SpinnerNumberModel(new Integer(90), new Integer(10), null, new Integer(1)));
			spinnerTiming.setValue(monitoringTime);
			panel.add(spinnerTiming);
		
		}
		
	}
	
	public void setCDPreference() {
		//On prends les settings du registery
		burnerManifacturer=jPrefer.get("buernerManufacturer", "Epson");
		fijiDirectory=jPrefer.get("fijiDirectory", null);
		epsonDirectory=jPrefer.get("epsonDirectory", null);
		labelFile=jPrefer.get("labelFile", null);
		dateFormatChoix=jPrefer.get("DateFormat", null);
		deleteStudies=jPrefer.getBoolean("deleteStudies", false);
		suportType=jPrefer.get("suportType", "Auto");
		monitoringTime=jPrefer.getInt("monitoringTime", 90);

		
	}

}
