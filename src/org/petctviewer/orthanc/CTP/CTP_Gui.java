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
import javax.swing.ListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.Dimension;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Color;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;
import java.awt.event.ItemEvent;

@SuppressWarnings("serial")
public class CTP_Gui extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField CTP_Username;
	private JPasswordField CTP_Password;
	private JTable tablePatient;
	DefaultTableModel modelTablePatient;
	private JComboBox<String> comboBox_Studies, comboBox_Visits;
	private CTP ctp;
	private JTable tableDetailsPatient;
	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");


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
					CTP_Username.setText(jprefer.get("CTPUsername", ""));
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
							comboBox_Visits.addItemListener(new ItemListener() {
								public void itemStateChanged(ItemEvent arg0) {
									System.out.println(arg0.getSource());
									if(arg0.getStateChange() == ItemEvent.SELECTED && !comboBox_Visits.getSelectedItem().equals("None") ) {
										
										System.out.println("ici");
										JSONArray patients=ctp.getAvailableImports((String) comboBox_Studies.getSelectedItem(), (String) comboBox_Visits.getSelectedItem());
										if (tablePatient.getModel().getRowCount()>0) modelTablePatient.setRowCount(0);
										for (int i=0 ; i<patients.size() ; i++) {
											JSONObject patient=(JSONObject) patients.get(i);
											System.out.println(patient);
											String numeroPatient=(String) patient.get("numeroPatient");
											
											String firstName=(String) patient.get("firstName");
											String lastName=(String) patient.get("lastName");
											String patientSex=(String) patient.get("patientSex");
											String patientDOB=(String) patient.get("patientDOB");
											String investigatorName=(String) patient.get("investigatorName");
											String country=(String) patient.get("country");
											String centerNumber=(String) patient.get("centerNumber");
											modelTablePatient.addRow(new Object[]{numeroPatient, lastName, firstName, patientSex , patientDOB, "01/01/1900"});
											//SK MANQUE ACQUISITION DATE DANS L API
										}
									}
									
								}
							});
							panel_1_1.add(comboBox_Visits);
						}
						comboBox_Studies.addItemListener(new ItemListener() {
							
							public void itemStateChanged(ItemEvent arg0) {
								if(arg0.getStateChange() == ItemEvent.SELECTED && comboBox_Studies.getSelectedIndex() !=0 ) {
									System.out.println(comboBox_Studies.getSelectedIndex());
									
									comboBox_Visits.removeAllItems();
									if (tablePatient.getModel().getRowCount()>0) modelTablePatient.setRowCount(0);
									String[] visits=ctp.getAvailableVisits((String) comboBox_Studies.getSelectedItem());
									if (visits !=null) {
										for (int i=0; i<visits.length; i++) {
											comboBox_Visits.addItem(visits[i]);
										}
									}
									else {
										comboBox_Visits.addItem("None");
									}
										
									
								}
									
							
								
							}
						});
					}
					btnConnect.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							jprefer.put("CTPUsername", CTP_Username.getText());
							comboBox_Studies.removeAllItems();
							comboBox_Visits.removeAllItems();
							if (tablePatient.getModel().getRowCount()>0) modelTablePatient.setRowCount(0);							
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
					tablePatient = new JTable();
					
					tablePatient.setPreferredScrollableViewportSize(new Dimension(300, 50));
					tablePatient.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					tablePatient.setModel(new DefaultTableModel(
						new Object[][] {
						},
						new String[] {
								"Patient Number", "Last Name", "First Name", "Sex", "DOB", "Acquisition Date" 
						}
					) {
						Class[] columnTypes = new Class[] {
								String.class, String.class, String.class, String.class, String.class, String.class
						};
						public Class getColumnClass(int columnIndex) {
							return columnTypes[columnIndex];
						}
					});
					modelTablePatient=(DefaultTableModel) tablePatient.getModel();
					tablePatient.getColumnModel().getColumn(1).setMinWidth(0);
					tablePatient.getColumnModel().getColumn(1).setMaxWidth(0);
					tablePatient.getColumnModel().getColumn(1).setWidth(0);
					tablePatient.getColumnModel().getColumn(1).setResizable(false);
					tablePatient.getColumnModel().getColumn(2).setMinWidth(0);
					tablePatient.getColumnModel().getColumn(2).setMaxWidth(0);
					tablePatient.getColumnModel().getColumn(2).setWidth(0);
					tablePatient.getColumnModel().getColumn(2).setResizable(false);
					tablePatient.getColumnModel().getColumn(3).setMinWidth(0);
					tablePatient.getColumnModel().getColumn(3).setMaxWidth(0);
					tablePatient.getColumnModel().getColumn(3).setWidth(0);
					tablePatient.getColumnModel().getColumn(3).setResizable(false);
					tablePatient.getColumnModel().getColumn(4).setMinWidth(0);
					tablePatient.getColumnModel().getColumn(4).setMaxWidth(0);
					tablePatient.getColumnModel().getColumn(4).setWidth(0);
					tablePatient.getColumnModel().getColumn(4).setResizable(false);
					tablePatient.getColumnModel().getColumn(5).setMaxWidth(0);
					tablePatient.getColumnModel().getColumn(5).setWidth(0);
					tablePatient.getColumnModel().getColumn(5).setWidth(0);
					tablePatient.getColumnModel().getColumn(5).setResizable(false);
					
					tablePatient.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
						
						@Override
						public void valueChanged(ListSelectionEvent arg0) {
							if (tablePatient.getSelectedRow() > -1) {
								tableDetailsPatient.setValueAt( (String) tablePatient.getValueAt(tablePatient.getSelectedRow(),1) , 0, 2);
								tableDetailsPatient.setValueAt( (String) tablePatient.getValueAt(tablePatient.getSelectedRow(),2), 1, 2);
								tableDetailsPatient.setValueAt( (String) tablePatient.getValueAt(tablePatient.getSelectedRow(),3), 2, 2);
								tableDetailsPatient.setValueAt( (String) tablePatient.getValueAt(tablePatient.getSelectedRow(),4), 3, 2);
								tableDetailsPatient.setValueAt( (String) tablePatient.getValueAt(tablePatient.getSelectedRow(),5), 4, 2);
								
							}
						
							
						}
					});
					scrollPane.setViewportView(tablePatient);
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1, BorderLayout.EAST);
				{
					tableDetailsPatient = new JTable();
					tableDetailsPatient.setModel(new DefaultTableModel(
						new Object[][] {
							{"Last Name", null, null},
							{"First Name", null, null},
							{"Sex", null, null},
							{"Date Of Birth", null, null},
							{"Acquisition Date", null, null},
						},
						new String[] {
							"", "Local", "CTP"
						}
					));
					tableDetailsPatient.getColumnModel().getColumn(0).setPreferredWidth(100);
					{
						JScrollPane scrollPane = new JScrollPane();
						scrollPane.setViewportView(tableDetailsPatient);
						tableDetailsPatient.setPreferredScrollableViewportSize(new Dimension (250,100));
						panel_1.add(scrollPane);
					}
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
