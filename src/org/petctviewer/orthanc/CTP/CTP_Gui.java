package org.petctviewer.orthanc.CTP;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.Dimension;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class CTP_Gui extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField CTP_Username;
	private JPasswordField CTP_Password;
	private JTable table;
	private JComboBox<String> comboBox_Studies, comboBox_Visits;
	private CTP ctp;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CTP_Gui dialog = new CTP_Gui();
			dialog.pack();
			dialog.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CTP_Gui() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			{
			}
		}
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			contentPanel.add(panel, BorderLayout.NORTH);
			panel.setLayout(new GridLayout(2, 1, 0, 0));
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1);
				{
					JLabel lblNewLabel = new JLabel("Username");
					panel_1.add(lblNewLabel);
				}
				{
					CTP_Username = new JTextField();
					panel_1.add(CTP_Username);
					CTP_Username.setColumns(10);
				}
				{
					JLabel lblPassword = new JLabel("Password");
					panel_1.add(lblPassword);
				}
				{
					CTP_Password = new JPasswordField(10);
					panel_1.add(CTP_Password);
					CTP_Password.setColumns(10);
				}
				{
					JButton btnConnect = new JButton("Connect");
					panel_1.add(btnConnect);
					JPanel panel_2 = new JPanel();
					panel.add(panel_2);
					panel_2.setLayout(new BorderLayout(0, 0));
					{
						JPanel panel_1_1 = new JPanel();
						panel_2.add(panel_1_1, BorderLayout.CENTER);
						{
							JLabel lblAvailableStudy = new JLabel("Available Study");
							panel_1_1.add(lblAvailableStudy);
						}
						comboBox_Studies = new JComboBox<String>();
						panel_1_1.add(comboBox_Studies);
						{
							JLabel label = new JLabel("");
							panel_1_1.add(label);
						}
						{
							JLabel lblAvailableVisite = new JLabel("Available Visite");
							panel_1_1.add(lblAvailableVisite);
						}
						{
							comboBox_Visits = new JComboBox<String>();
							comboBox_Visits.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent arg0) {
									//SK A FAIRE ICI
								}
							});
							panel_1_1.add(comboBox_Visits);
						}
						comboBox_Studies.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								
								if (! (comboBox_Studies.getSelectedIndex()==0) ) {
									comboBox_Visits.removeAllItems();
									String[] visits=ctp.getAvailableVisits((String) comboBox_Studies.getSelectedItem());
									if (visits !=null) {
										for (int i=0; i<visits.length; i++) {
											comboBox_Visits.addItem(visits[i]);
										}
									}
									else {
										comboBox_Visits.addItem("none");
									}
									
								}
								
							}
						});
					}
					btnConnect.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							comboBox_Studies.removeAllItems();
							ctp=new CTP(CTP_Username.getText(), CTP_Password.getText());
							String[] studies=ctp.getAvailableStudies();
							comboBox_Studies.addItem("Choose");
							for (int i=0; i<studies.length; i++) {
								comboBox_Studies.addItem(studies[i]);
							}
							
						}
					});
				}
			}
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1, BorderLayout.NORTH);
				{
					JLabel lblPatientAwaitingUpload = new JLabel("Matching Patient Awaiting Upload");
					panel_1.add(lblPatientAwaitingUpload);
				}
			}
			{
				JScrollPane scrollPane = new JScrollPane();
				panel.add(scrollPane);
				{
					table = new JTable();
					table.setPreferredScrollableViewportSize(new Dimension(300, 50));
					table.setModel(new DefaultTableModel(
						new Object[][] {
						},
						new String[] {
							"New column", "New column"
						}
					) {
						Class[] columnTypes = new Class[] {
							String.class, String.class
						};
						public Class getColumnClass(int columnIndex) {
							return columnTypes[columnIndex];
						}
					});
					scrollPane.setViewportView(table);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
