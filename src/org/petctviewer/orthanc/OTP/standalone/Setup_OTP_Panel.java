package org.petctviewer.orthanc.OTP.standalone;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class Setup_OTP_Panel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	JTextField proxyAdress;
	JTextField proxyPort;
	
	public Setup_OTP_Panel() {
		super(new GridLayout());
		proxyAdress=new JTextField("10.1.50.1");
		proxyAdress.setColumns(20);
		proxyPort=new JTextField("8080");
		proxyPort.setColumns(4);
		this.add(proxyAdress);
		this.add(proxyPort);
		
		
		
		
	}
	
	public String getProxyAdress() {
		return proxyAdress.getText();
	}
	
	public String getProxyPort() {
		return proxyPort.getText();
	}

}
