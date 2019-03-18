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
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class AboutBoxFrame extends JDialog{

	private static final long serialVersionUID = 1L;

	public AboutBoxFrame(JFrame gui){
		super(gui, "About us");
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		Image image = new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage();
		this.setIconImage(image);
		
		this.getContentPane().add(mainPanel);
		
		JPanel panel_center = new JPanel();
		mainPanel.add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new BorderLayout(5, 10));
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_center.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		JLabel title = new JLabel("Dicom Tools v.1.5alfa");
		title.setForeground(Color.RED);
		panel.add(title);
		title.setBorder(new EmptyBorder(3, 150, 3, 150));
		JLabel orthancSite = new JLabel("<html><i>Based on Orthanc :  http://www.orthanc-server.com</i></html>");
		panel.add(orthancSite);
		orthancSite.setBorder(new EmptyBorder(3, 100, 3, 100));
		
		JPanel authorsLicencePanel = new JPanel();
		panel_center.add(authorsLicencePanel, BorderLayout.SOUTH);
		authorsLicencePanel.setLayout(new GridLayout(0, 2, 10, 10));
			
		JLabel label = new JLabel("Salim Kanoun");
		authorsLicencePanel.add(label);

		JLabel label_1 = new JLabel("<html><i>salim.kanoun@gmail.com</i></html>");
		authorsLicencePanel.add(label_1);
		authorsLicencePanel.add(new JLabel("Licence"));
		authorsLicencePanel.add(new JLabel("GPL V.3"));
		authorsLicencePanel.add(new JLabel("Website"));

		authorsLicencePanel.add(new JLabel("<html><i>petctviewer.org</i><html>"));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(gui);
	}
	
}
