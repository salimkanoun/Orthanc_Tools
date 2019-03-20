package org.petctviewer.orthanc.query.autoquery.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.petctviewer.orthanc.anonymize.VueAnon;

import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;

public class Daily_Retrieve_Gui extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	
	private JCheckBox chckbxCr , chckbxCt , chckbxCmr ,chckbxNm , chckbxPt ,chckbxUs ,chckbxXa ,chckbxMg;
	private JTextField textFieldNameIDAcc;
	private JComboBox<String> comboBox_NameIDAcc;

	private JTextField studyDescription;
	private Preferences prefs=VueAnon.jprefer;
	public boolean validate;

	/**
	 * Create the dialog.
	 */
	public Daily_Retrieve_Gui() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JPanel panel_AutoRetrieve = new JPanel();
		panel_AutoRetrieve.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPanel.add(panel_AutoRetrieve);
		panel_AutoRetrieve.setLayout(new BorderLayout(0, 0));
		
		JPanel AutoRetrieve_Title = new JPanel();
		AutoRetrieve_Title.setBorder(new LineBorder(Color.GRAY));
		panel_AutoRetrieve.add(AutoRetrieve_Title, BorderLayout.NORTH);
		
		JLabel lblAutoquery = new JLabel("Daily Auto-Retrieve");
		AutoRetrieve_Title.add(lblAutoquery);
		
		JPanel AutoRetrieve_Panel = new JPanel();
		panel_AutoRetrieve.add(AutoRetrieve_Panel, BorderLayout.CENTER);
		AutoRetrieve_Panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		comboBox_NameIDAcc = new JComboBox<String>();
		comboBox_NameIDAcc.setModel(new DefaultComboBoxModel<String>(new String[] {"Name", "ID", "Accession"}));
		
		AutoRetrieve_Panel.add(comboBox_NameIDAcc);
		
		textFieldNameIDAcc = new JTextField();
		textFieldNameIDAcc.setText("*");
		textFieldNameIDAcc.setToolTipText("Name Format : LastName^FirstName");
		AutoRetrieve_Panel.add(textFieldNameIDAcc);
		textFieldNameIDAcc.setColumns(10);
		
		JLabel lblDate = new JLabel("Date :");
		AutoRetrieve_Panel.add(lblDate);
		
		JPanel Panel_Date = new JPanel();
		AutoRetrieve_Panel.add(Panel_Date);
		JLabel today=new JLabel("Each current day");
		Panel_Date.add(today);
		
		JLabel lblStudyDescription = new JLabel("Study Description :");
		AutoRetrieve_Panel.add(lblStudyDescription);
		
		studyDescription = new JTextField();
		studyDescription.setText("*");
		AutoRetrieve_Panel.add(studyDescription);
		studyDescription.setColumns(10);
		
		JLabel lblModality = new JLabel("Modality :");
		AutoRetrieve_Panel.add(lblModality);
		
		JPanel panel_2 = new JPanel();
		AutoRetrieve_Panel.add(panel_2);
		panel_2.setLayout(new GridLayout(2, 4, 0, 0));
		
		chckbxCr = new JCheckBox("CR");
		panel_2.add(chckbxCr);
		
		chckbxCt = new JCheckBox("CT");
		panel_2.add(chckbxCt);
		
		chckbxCmr = new JCheckBox("CMR");
		panel_2.add(chckbxCmr);
		
		chckbxNm = new JCheckBox("NM");
		panel_2.add(chckbxNm);
		
		chckbxPt = new JCheckBox("PT");
		panel_2.add(chckbxPt);
		
		chckbxUs = new JCheckBox("US");
		panel_2.add(chckbxUs);
		
		chckbxXa = new JCheckBox("XA");
		panel_2.add(chckbxXa);
		
		chckbxMg = new JCheckBox("MG");
		panel_2.add(chckbxMg);
		
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				validate=true;
				saveSettingsInPrefs();
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	
	
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		
		loadSettings();
			
	}
	
	
	
	
	public String GetModalitiesString() {
		StringBuilder modalities=new StringBuilder();
		if (chckbxCr.isSelected()) modalities.append("\\\\CR") ;
		if (chckbxCt.isSelected()) modalities.append("\\\\CT") ;
		if (chckbxCmr.isSelected()) modalities.append("\\\\CMR") ;
		if (chckbxNm.isSelected()) modalities.append("\\\\NM") ;
		if (chckbxPt.isSelected()) modalities.append("\\\\PT") ;
		if (chckbxUs.isSelected()) modalities.append("\\\\US") ;
		if (chckbxXa.isSelected()) modalities.append("\\\\XA") ;
		if (chckbxMg.isSelected()) modalities.append("\\\\MG") ;
		if (modalities.length()==0) modalities.append("*"); else modalities.delete(0, 2);
		return modalities.toString();
		
	}
	
	public String getSelectedComboBoxQueryOption() {
		return (String) comboBox_NameIDAcc.getSelectedItem();
	}
	
	public String getResearchTextParam() {
		return textFieldNameIDAcc.getText();
	}
	
	public String getStudyDescriptionParam() {
		return studyDescription.getText();
	}
	
	private void saveSettingsInPrefs() {
		prefs.put("AutoQuer_Daily_SearchText", textFieldNameIDAcc.getText());
		prefs.putInt("AutoQuer_Daily_SearchOpt", comboBox_NameIDAcc.getSelectedIndex());
		prefs.put("AutoQuer_Daily_StudyDesc", studyDescription.getText());
		
		prefs.putBoolean("AutoQuer_Daily_CR", chckbxCr.isSelected());
		prefs.putBoolean("AutoQuer_Daily_CT", chckbxCt.isSelected());
		prefs.putBoolean("AutoQuer_Daily_CMR", chckbxCmr.isSelected());
		prefs.putBoolean("AutoQuer_Daily_NM", chckbxNm.isSelected());
		prefs.putBoolean("AutoQuer_Daily_PT", chckbxPt.isSelected());
		prefs.putBoolean("AutoQuer_Daily_US", chckbxUs.isSelected());
		prefs.putBoolean("AutoQuer_Daily_XA", chckbxXa.isSelected());
		prefs.putBoolean("AutoQuer_Daily_MG", chckbxMg.isSelected());
		
	}
	
	private void loadSettings() {
		textFieldNameIDAcc.setText(prefs.get("AutoQuer_Daily_SearchText", ""));
		comboBox_NameIDAcc.setSelectedIndex(prefs.getInt("AutoQuer_Daily_SearchOpt",0));
		studyDescription.setText(prefs.get("AutoQuer_Daily_StudyDesc", ""));
		
		chckbxCr.setSelected(prefs.getBoolean("AutoQuer_Daily_CR", false));
		chckbxCt.setSelected(prefs.getBoolean("AutoQuer_Daily_CT", false));
		chckbxCmr.setSelected(prefs.getBoolean("AutoQuer_Daily_CMR", false));
		chckbxNm.setSelected(prefs.getBoolean("AutoQuer_Daily_NM", false));
		chckbxPt.setSelected(prefs.getBoolean("AutoQuer_Daily_PT", false));
		chckbxUs.setSelected(prefs.getBoolean("AutoQuer_Daily_US", false));
		chckbxXa.setSelected(prefs.getBoolean("AutoQuer_Daily_XA", false));
		chckbxMg.setSelected(prefs.getBoolean("AutoQuer_Daily_MG", false));
		
	}
	
	//SK SAUVEGARDE DES SETTINGS A FAIRE !
}
