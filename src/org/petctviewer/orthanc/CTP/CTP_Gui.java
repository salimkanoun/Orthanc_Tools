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
import java.awt.event.ActionEvent;

public class CTP_Gui extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField CTP_Username;
	private JPasswordField CTP_Password;
	private JTable table;
	private JComboBox<String> comboBox_Studies;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CTP_Gui dialog = new CTP_Gui();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			{
				JLabel lblNewLabel = new JLabel("Username");
				panel.add(lblNewLabel);
			}
			{
				CTP_Username = new JTextField();
				panel.add(CTP_Username);
				CTP_Username.setColumns(10);
			}
			{
				JLabel lblPassword = new JLabel("Password");
				panel.add(lblPassword);
			}
			{
				CTP_Password = new JPasswordField(10);
				panel.add(CTP_Password);
				CTP_Password.setColumns(10);
			}
			{
				JButton btnConnect = new JButton("Connect");
				panel.add(btnConnect);
				btnConnect.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						CTP ctp=new CTP(CTP_Username.getText(), CTP_Password.getText());
						String[] studies=ctp.getAvailableStudies();
						for (int i=0; i<studies.length; i++) {
							comboBox_Studies.addItem(studies[i]);
						}
						
					}
				});
			}
		}
		{
			JLabel lblAvailableStudy = new JLabel("Available Study");
			contentPanel.add(lblAvailableStudy);
		}
		{
			comboBox_Studies = new JComboBox<String>();
			contentPanel.add(comboBox_Studies);
		}
		{
			JLabel label = new JLabel("");
			contentPanel.add(label);
		}
		{
			JLabel lblAvailableVisite = new JLabel("Available Visite");
			contentPanel.add(lblAvailableVisite);
		}
		{
			JComboBox comboBox = new JComboBox();
			contentPanel.add(comboBox);
		}
		{
			JButton btnSearchAnonymizationKey = new JButton("Search Anonymization Key");
			contentPanel.add(btnSearchAnonymizationKey);
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				table = new JTable();
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
