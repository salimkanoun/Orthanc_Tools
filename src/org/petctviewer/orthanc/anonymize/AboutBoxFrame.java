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

package org.petctviewer.orthanc.anonymize;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AboutBoxFrame extends JFrame{

	private static final long serialVersionUID = 1L;

	public AboutBoxFrame(){
		super("About us");
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
		JLabel title = new JLabel("Dicom Tools v.1.1");
		title.setBorder(new EmptyBorder(3, 150, 3, 150));
		labelPanel.add(title);
		JLabel orthancSite = new JLabel("<html><i>Based on Orthanc http://www.orthanc-server.com</i></html>");
		orthancSite.setBorder(new EmptyBorder(3, 100, 3, 100));
		labelPanel.add(orthancSite);
		
		JPanel authorsLicencePanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(20, 5, 5, 20);
		gbc.gridx = 0;
		gbc.gridy = 0;
		authorsLicencePanel.add(new JLabel("Anousone Vongsalat"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		authorsLicencePanel.add(new JLabel("<html><i>anousonevongsalat@yahoo.com</i></html>"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		authorsLicencePanel.add(new JLabel("Salim Kanoun"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		authorsLicencePanel.add(new JLabel("<html><i>salim.kanoun@gmail.com</i></html>"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		authorsLicencePanel.add(new JLabel("Licence"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		authorsLicencePanel.add(new JLabel("GPL V.3"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		authorsLicencePanel.add(new JLabel("Website"), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		authorsLicencePanel.add(new JLabel("<html><i>petctviewer.org</i><html>"), gbc);
		
		JPanel logosPanel = new JPanel(new GridBagLayout());
		gbc.insets = new Insets(20, 10, 0, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		Image logoICR = new ImageIcon(ClassLoader.getSystemResource("logos/claudiusregaud.png")).getImage();
		logosPanel.add(new JLabel(new ImageIcon(logoICR)), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		Image logoCHU = new ImageIcon(ClassLoader.getSystemResource("logos/chu.png")).getImage();
		logosPanel.add(new JLabel(new ImageIcon(logoCHU)), gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		Image logoIUT = new ImageIcon(ClassLoader.getSystemResource("logos/iut.png")).getImage();
		logosPanel.add(new JLabel(new ImageIcon(logoIUT)), gbc);

		gbc.insets = new Insets(0, 10, 0, 10);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		logosPanel.add(new JLabel("Institut Claudius Regaud"), gbc);
		Image image = new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage();
		this.setIconImage(image);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		logosPanel.add(new JLabel("CHU de Toulouse"), gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		logosPanel.add(new JLabel("IUT Paul Sabatier"), gbc);
		
		mainPanel.add(labelPanel, BorderLayout.NORTH);
		mainPanel.add(authorsLicencePanel, BorderLayout.CENTER);
		mainPanel.add(logosPanel, BorderLayout.SOUTH);
		
		this.getContentPane().add(mainPanel);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public static void main(String[] args0){
		AboutBoxFrame vue = new AboutBoxFrame();
		vue.setVisible(true);
		vue.pack();
	}
}
