package org.petctviewer.orthanc.Jsonsettings;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class Connection_Dialog extends JDialog {
	private JTextField txtHttpipport;
	private JTextField textField_Login;
	private JTextField textField_Password;
	private String address;
	private String login;
	private String password;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Connection_Dialog dialog = new Connection_Dialog();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public Connection_Dialog() {
		setBounds(100, 100, 450, 300);
		setModal(true);
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				address=txtHttpipport.getText();
				login=textField_Login.getText();
				password=textField_Password.getText();
				dispose();
			}
		});
		panel.add(btnOk);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblServerAdres = new JLabel("Server address");
		panel_1.add(lblServerAdres);
		
		txtHttpipport = new JTextField();
		txtHttpipport.setText("http://localhost:8042");
		panel_1.add(txtHttpipport);
		txtHttpipport.setColumns(10);
		
		JLabel label_4 = new JLabel("");
		panel_1.add(label_4);
		
		JLabel lblLogin = new JLabel("Login");
		panel_1.add(lblLogin);
		
		textField_Login = new JTextField();
		panel_1.add(textField_Login);
		textField_Login.setColumns(10);
		
		JLabel label = new JLabel("");
		panel_1.add(label);
		
		JLabel lblPassword = new JLabel("Password");
		panel_1.add(lblPassword);
		
		textField_Password = new JTextField();
		panel_1.add(textField_Password);
		textField_Password.setColumns(10);
		
		JLabel label_3 = new JLabel("");
		panel_1.add(label_3);
		
		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.NORTH);
		
		JLabel lblEnterCorrectSettings = new JLabel("Enter correct settings and retry connection");
		panel_2.add(lblEnterCorrectSettings);

	}
protected String getAddress(){
	return address;
	}
protected String getLogin(){
	return login;
	}
protected String getPassword(){
	return password;
	}
}
