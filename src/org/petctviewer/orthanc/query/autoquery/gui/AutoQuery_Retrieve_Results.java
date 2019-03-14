package org.petctviewer.orthanc.query.autoquery.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.sql.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.query.QueryRetrieve;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class AutoQuery_Retrieve_Results extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private tableResultModel tableModel;
	private VueAnon vueAnon;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AutoQuery_Retrieve_Results frame = new AutoQuery_Retrieve_Results(null);
					frame.pack();
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
	public AutoQuery_Retrieve_Results(VueAnon vueAnon) {
		this.vueAnon=vueAnon;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel_main_center = new JPanel();
		contentPane.add(panel_main_center, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		panel_main_center.add(scrollPane);
		
		tableModel=new tableResultModel();
		table = new JTable(tableModel);
		scrollPane.setViewportView(table);
		
		JPanel panel_main_south = new JPanel();
		contentPane.add(panel_main_south, BorderLayout.SOUTH);
		
		JButton btnToAnonymize = new JButton("To Anonymize");
		panel_main_south.add(btnToAnonymize);
		
		JButton btnToExport = new JButton("To Export");
		panel_main_south.add(btnToExport);
	}
	
	public void addStudy(Study2[] study) {
		for(int i=0; i<study.length; i++) {
			tableModel.addRow(new Object[] {study[i].getPatientName(), study[i].getPatientID(), study[i].getDate(),study[i].getStudyDescription(), study[i]});
			
		}
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
	
	public void sendToAnonymize() {
		
	}
	
	public void sendToExport() {
		
	}
	
	

}
