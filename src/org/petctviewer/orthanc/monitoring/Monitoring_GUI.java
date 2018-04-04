package org.petctviewer.orthanc.monitoring;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import org.petctviewer.orthanc.ParametreConnexionHttp;

import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class Monitoring_GUI extends JFrame {

	private JPanel contentPane;
	
	private JButton btnStopMonitoring, btnStartMonitoring;
	//SK Penser a interrompre ce Thread a la sortie
	private Thread background;
	private Preferences jPrefer;
	private CD_Burner cdBurner;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Monitoring_GUI frame = new Monitoring_GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Monitoring_GUI() {
		// SK A CORRIGER
		cdBurner=new CD_Burner(new ParametreConnexionHttp());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel Main_Tab = new JPanel();
		tabbedPane.addTab("Main", null, Main_Tab, null);
		
		JPanel CD_Burner_Tab = new JPanel();
		CD_Burner_Tab.add(buildCDPanel());
		tabbedPane.addTab("CD Burner", null, CD_Burner_Tab, null);
	}
	
	private JPanel buildCDPanel() {
		//On prends les settings du registery
				jPrefer = Preferences.userNodeForPackage(Burner_Settings.class);
				jPrefer = jPrefer.node("CDburner");
				CD_Burner.fijiDirectory=jPrefer.get("fijiDirectory", null);
				CD_Burner.epsonDirectory=jPrefer.get("epsonDirectory", null);
				CD_Burner.labelFile=jPrefer.get("labelFile", null);
				CD_Burner.dateFormatChoix=jPrefer.get("DateFormat", null);
				
				JPanel contentPane = new JPanel();
				contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				contentPane.setLayout(new BorderLayout(0, 0));
				
				JTextArea textArea = new JTextArea();
				textArea.setRows(5);
				DefaultCaret caret = (DefaultCaret) textArea.getCaret();
				caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setViewportView(textArea);
				contentPane.add(scrollPane, BorderLayout.CENTER);
				
				JPanel panel = new JPanel();
				contentPane.add(panel, BorderLayout.SOUTH);
				
				btnStartMonitoring = new JButton("Start monitoring");
				btnStartMonitoring.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if ( CD_Burner.epsonDirectory==null ||CD_Burner.fijiDirectory==null ||CD_Burner.labelFile==null || CD_Burner.dateFormatChoix==null ){
							//Message d'erreur doit faire le set de output folder
							JOptionPane.showMessageDialog(null, "Go to settings Menu to set missing paths", "Set directories and date format", JOptionPane.ERROR_MESSAGE);
						}
						
						else {
								textArea.append("Monitoring Orthanc \n");
								//On ouvre le watcher dans un nouveau thread pour ne pas bloquer l'interface
								background=new Thread (new Runnable() 
							    {
							      public void run()
							      {
							    	  cdBurner.watchOrthancStableStudies();
							    	 /*try {
							    		  //SK ICI A IMPLEMENTER
							    		 // Remplacer par le Watch API
							    		 //cdBurner.watchFolder();
										} catch (IOException | InterruptedException | ParseException e) {
											e.printStackTrace();
										}*/
							      }
							    });
							   background.start();
							   //On grise le boutton pour empecher la creation d'un nouveau watcher
							   btnStartMonitoring.setEnabled(false);
							   btnStopMonitoring.setEnabled(true);
							}			
					}
				});
				JButton btnSettings = new JButton("Settings");
				btnSettings.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Burner_Settings settings=new Burner_Settings();
						settings.setVisible(true);
						settings.setModal(true);
						//On refresh les changement de variable � la fin de l'operation
						CD_Burner.fijiDirectory=jPrefer.get("fijiDirectory", null);
						CD_Burner.epsonDirectory=jPrefer.get("epsonDirectory", null);
						CD_Burner.labelFile=jPrefer.get("labelFile", null);
						CD_Burner.dateFormatChoix=jPrefer.get("DateFormat", null);
					}
				});
				panel.add(btnSettings);
				panel.add(btnStartMonitoring);
				
				
				
				btnStopMonitoring = new JButton("Stop Monitoring");
				btnStopMonitoring.setEnabled(false);
				btnStopMonitoring.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						background.interrupt();
						textArea.append("Monitoring Terminated \n");
						btnStartMonitoring.setEnabled(true);
						btnStopMonitoring.setEnabled(false);
						
					}
				});
				panel.add(btnStopMonitoring);
				
				
				JPanel Title = new JPanel();
				contentPane.add(Title, BorderLayout.NORTH);
				
				JLabel lblCdburnerBySassa = new JLabel("CD Burner Activity");
				Title.add(lblCdburnerBySassa);
				
				return contentPane;
				
	}
	
	public void autostart() {
		//Si parametre OK on monitor d�s le startup
		if ( CD_Burner.epsonDirectory!=null && CD_Burner.fijiDirectory!=null && CD_Burner.labelFile!=null && CD_Burner.dateFormatChoix!=null ){
			btnStartMonitoring.doClick();
		}
		
	}

}
