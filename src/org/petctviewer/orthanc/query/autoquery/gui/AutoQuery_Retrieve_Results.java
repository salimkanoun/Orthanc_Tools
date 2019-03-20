package org.petctviewer.orthanc.query.autoquery.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import org.petctviewer.orthanc.Orthanc_Tools;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class AutoQuery_Retrieve_Results extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private tableResultModel tableModel;
	private JTextArea textAreaConsoleProgress;
	private Study2[] studies;
	private DateFormat df = new SimpleDateFormat("yyyyMMdd");
	private JPanel panel_result_import;
	private VueAnon vueAnon;
	
	/**
	 * Create the frame.
	 */
	public AutoQuery_Retrieve_Results(VueAnon vueAnon) {
		this.vueAnon=vueAnon;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Auto-Query Progress / Results");
		Image image = new ImageIcon(ClassLoader.getSystemResource("logos/OrthancIcon.png")).getImage();
		this.setIconImage(image);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panelNorth=new JPanel();
		panelNorth.setLayout(new BorderLayout());
		JScrollPane scrollPaneProgress=new JScrollPane();
		textAreaConsoleProgress = new JTextArea(10, 80);
		textAreaConsoleProgress.setAutoscrolls(true);
		DefaultCaret caret = (DefaultCaret) textAreaConsoleProgress.getCaret();
		caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
		scrollPaneProgress.setViewportView(textAreaConsoleProgress);
		panelNorth.add(scrollPaneProgress, BorderLayout.CENTER);
		
		JButton btnCsvRetrieveReport = new JButton("Save To CSV");
		btnCsvRetrieveReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser csvReport=new JFileChooser();
				csvReport.setFileSelectionMode(JFileChooser.FILES_ONLY);
				csvReport.setSelectedFile(new File("Report_AutoRetrieve_"+df.format(new Date() )+".csv"));
				int ok=csvReport.showSaveDialog(null);
				if (ok==JFileChooser.APPROVE_OPTION ) {
					Orthanc_Tools.writeCSV(textAreaConsoleProgress.getText(), csvReport.getSelectedFile());
					}
			}
		});
		JPanel button=new JPanel();
		btnCsvRetrieveReport.setToolTipText("Set Folder to generate report of AutoQuery");
		button.add(btnCsvRetrieveReport);
		panelNorth.add(button, BorderLayout.SOUTH);
		
		contentPane.add(panelNorth, BorderLayout.NORTH);
		
		JLabel lblProgress = new JLabel("Progress :");
		lblProgress.setHorizontalAlignment(SwingConstants.CENTER);
		panelNorth.add(lblProgress, BorderLayout.NORTH);
		
		
		panel_result_import = new JPanel();
		contentPane.add(panel_result_import, BorderLayout.CENTER);
		panel_result_import.setLayout(new BorderLayout(0, 0));
		panel_result_import.setVisible(false);
		
		JScrollPane scrollPane = new JScrollPane();
		panel_result_import.add(scrollPane);
		
		tableModel=new tableResultModel();
		table = new JTable(tableModel);
		scrollPane.setViewportView(table);
		
		JPanel panel_main_south = new JPanel();
		panel_result_import.add(panel_main_south, BorderLayout.SOUTH);
		
		JButton btnToAnonymize = new JButton("To Anonymize");
		btnToAnonymize.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				vueAnon.importStudiesInAnonList(studies);
				vueAnon.toFront();
				vueAnon.openCloseAnonTool(true);
			}
			
		});
		panel_main_south.add(btnToAnonymize);
		
		JButton btnToExport = new JButton("To Export");
		btnToExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				vueAnon.importStudiesInExportList(studies);
				vueAnon.toFront();
				vueAnon.openCloseExportTool(true);
				
			}
			
		});
		panel_main_south.add(btnToExport);
		
		JLabel lblRetrieveResults = new JLabel("Retrieve Results : ");
		lblRetrieveResults.setHorizontalAlignment(SwingConstants.CENTER);
		panel_result_import.add(lblRetrieveResults, BorderLayout.NORTH);
	}
	
	public void addStudy(Study2[] studies) {
		this.studies=studies;
		for(int i=0; i<studies.length; i++) {
			tableModel.addRow(new Object[] {studies[i].getPatientName(), studies[i].getPatientID(), studies[i].getDate(),studies[i].getStudyDescription(), studies[i]});
			
		}
		panel_result_import.setVisible(true);
		pack();
		setLocationRelativeTo(vueAnon);
	}
	
	public class tableResultModel extends DefaultTableModel{
		private static final long serialVersionUID = 1L;
		private String[] titles= {"PatientName", "PatientID", "StudyDate", "StudyDescription", "studyObject"};
		private Class<?>[] classColumn= {String.class, String.class, Date.class,String.class, Study2.class};
		
		public tableResultModel(){
			super(0,5);
		}

		public String getColumnName(int columnIndex){
			return titles[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int column){
			return classColumn[column];
		}
		
	}
	
	public JTextArea getConsole() {
		return textAreaConsoleProgress;
	}
	
	

}
