/**
Copyright (C) 2017 VONGSALAT Anousone & KANOUN Salim

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

package org.petctviewer.orthanc.anonymize.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.event.ActionEvent;

public class AboutBoxFrame extends JDialog{

	private static final long serialVersionUID = 1L;

	public AboutBoxFrame(JFrame gui){
		super(gui, "About us");
		
		BorderLayout bl_mainPanel = new BorderLayout();
		bl_mainPanel.setHgap(10);
		bl_mainPanel.setVgap(10);
		JPanel mainPanel = new JPanel(bl_mainPanel);
		
		Image image = new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage();
		this.setIconImage(image);
		
		this.getContentPane().add(mainPanel);
		
		JPanel panel_center = new JPanel();
		mainPanel.add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new BorderLayout(5, 10));
		
		JPanel authorsLicencePanel = new JPanel();
		panel_center.add(authorsLicencePanel, BorderLayout.SOUTH);
		authorsLicencePanel.setLayout(new GridLayout(0, 2, 10, 10));
			
		JLabel label = new JLabel("Salim Kanoun");
		authorsLicencePanel.add(label);

		JLabel lblSalimkanoungmailcom = new JLabel("salim.kanoun@gmail.com");
		lblSalimkanoungmailcom.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
		authorsLicencePanel.add(lblSalimkanoungmailcom);
		authorsLicencePanel.add(new JLabel("Licence"));
		authorsLicencePanel.add(new JLabel("GPL v3"));
		authorsLicencePanel.add(new JLabel("Website"));

		JLabel label_1 = new JLabel("http://petctviewer.org");
		label_1.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
		authorsLicencePanel.add(label_1);
		
		JLabel lblNewLabel = new JLabel("Sources");
		authorsLicencePanel.add(lblNewLabel);
		
		JLabel lblHttpsgithubcomsalimkanounorthanctools = new JLabel("https://github.com/salimkanoun/Orthanc_Tools");
		lblHttpsgithubcomsalimkanounorthanctools.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
		authorsLicencePanel.add(lblHttpsgithubcomsalimkanounorthanctools);
		
		JPanel panel_north = new JPanel();
		mainPanel.add(panel_north, BorderLayout.NORTH);
		panel_north.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_north.setLayout(new GridLayout(0, 1, 0, 0));
		JLabel title = new JLabel("Orthanc Tools v.1.6");
		title.setForeground(Color.RED);
		panel_north.add(title);
		title.setBorder(new EmptyBorder(3, 150, 3, 150));
		JLabel orthancSite = new JLabel("<html><i>Based on Orthanc :  http://www.orthanc-server.com</i></html>");
		panel_north.add(orthancSite);
		orthancSite.setBorder(new EmptyBorder(3, 100, 3, 100));
		
		JPanel panel = new JPanel();
		mainPanel.add(panel, BorderLayout.SOUTH);
		
		JButton btnYouTubeChanel = new JButton("You Tube Chanel");
		btnYouTubeChanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 try {
					open(new URI("https://www.youtube.com/watch?v=cCvDfanQWCw&list=PLlWfh5HNr8mIK3sAe03qY8ynS569sHnGm"));
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		JLabel lblHelp = new JLabel("Help :");
		panel.add(lblHelp);
		panel.add(btnYouTubeChanel);
		
		JButton btnDocumentation = new JButton("Documentation");
		btnDocumentation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					open(new URI("https://github.com/salimkanoun/Orthanc_Tools/blob/master/Orthanc_Tools_Documentation.pdf"));
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel.add(btnDocumentation);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(gui);
	}
	
	private static void open(URI uri) {
	    if (Desktop.isDesktopSupported()) {
	      try {
	        Desktop.getDesktop().browse(uri);
	      } catch (IOException e) { /* TODO: error handling */ }
	    } else { /* TODO: error handling */ }
	  }
	
}
