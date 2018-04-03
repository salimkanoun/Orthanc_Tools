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
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.ButtonGroup;

@SuppressWarnings("serial")
public class Burner_Settings extends JDialog {

	private final JPanel contentPanel = new JPanel();
	
	private JLabel imageJPath;
	private JLabel epsonDirectoryLabel;
	private JLabel labelFilePath;
	
	private Preferences jPrefer = null;
	private final ButtonGroup buttonGroup = new ButtonGroup();

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
		jPrefer = Preferences.userNodeForPackage(Burner_Settings.class);
		jPrefer = jPrefer.node("CDburner");
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 750, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		{
			JButton imageJ = new JButton("Set ImageJ viewer");
			imageJ.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc=new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int ouvrir=fc.showOpenDialog(null);
					if (ouvrir==JFileChooser.APPROVE_OPTION){
						CD_Burner.fijiDirectory=fc.getSelectedFile().getAbsolutePath().toString();
						imageJPath.setText(CD_Burner.fijiDirectory);
					}
				}
			});
			contentPanel.add(imageJ);
		}
		{
			imageJPath = new JLabel(CD_Burner.fijiDirectory);
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
						CD_Burner.labelFile=fc.getSelectedFile().getAbsolutePath().toString();
						labelFilePath.setText(CD_Burner.labelFile);
					}
				}
			});
			contentPanel.add(labelFileButton);
		}
		{
			labelFilePath = new JLabel(CD_Burner.labelFile);
			contentPanel.add(labelFilePath);
		}
		{
			JButton epsonDirectoryButton = new JButton("Set Epson Directory");
			epsonDirectoryButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc=new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int ouvrir=fc.showOpenDialog(null);
					if (ouvrir==JFileChooser.APPROVE_OPTION){
						CD_Burner.epsonDirectory=fc.getSelectedFile().getAbsolutePath().toString();
						epsonDirectoryLabel.setText(CD_Burner.epsonDirectory);
					}
				}
			});
			contentPanel.add(epsonDirectoryButton);
		}
		{
			epsonDirectoryLabel = new JLabel(CD_Burner.epsonDirectory);
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
				if (rdbtnYyyymmdd.getActionCommand().equals(jPrefer.get("DateFormat", null))==true || jPrefer.get("DateFormat", null)==null) rdbtnYyyymmdd.setSelected(true);
			}
			{
				JRadioButton rdbtnDdmmyyyy = new JRadioButton("dd/MM/yyyy");
				rdbtnDdmmyyyy.setActionCommand("dd/MM/yyyy");
				buttonGroup.add(rdbtnDdmmyyyy);
				panel.add(rdbtnDdmmyyyy);
				jPrefer = Preferences.userNodeForPackage(Burner_Settings.class);
				jPrefer = jPrefer.node("CDburner");
				if (rdbtnDdmmyyyy.getActionCommand().equals(jPrefer.get("DateFormat", null))==true) rdbtnDdmmyyyy.setSelected(true);
			}
			{
				JRadioButton rdbtnMmddyyyy = new JRadioButton("MM/dd/yyyy");
				rdbtnMmddyyyy.setActionCommand("MM/dd/yyyy");
				buttonGroup.add(rdbtnMmddyyyy);
				panel.add(rdbtnMmddyyyy);
				jPrefer = Preferences.userNodeForPackage(Burner_Settings.class);
				jPrefer = jPrefer.node("CDburner");
				if (rdbtnMmddyyyy.getActionCommand().equals(jPrefer.get("DateFormat", null))==true) rdbtnMmddyyyy.setSelected(true);
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
						jPrefer = Preferences.userNodeForPackage(Burner_Settings.class);
						jPrefer = jPrefer.node("CDburner");
						//On sauve dans le registery
						if (CD_Burner.epsonDirectory!=null) jPrefer.put("epsonDirectory", CD_Burner.epsonDirectory);
						if (CD_Burner.fijiDirectory!=null) jPrefer.put("fijiDirectory", CD_Burner.fijiDirectory);
						if (CD_Burner.labelFile!=null) jPrefer.put("labelFile", CD_Burner.labelFile);
						//On ajoute la string du format date
						jPrefer.put("DateFormat", buttonGroup.getSelection().getActionCommand());
						//on dispose 
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
