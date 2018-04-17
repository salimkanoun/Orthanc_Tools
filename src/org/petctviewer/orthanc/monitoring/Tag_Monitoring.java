package org.petctviewer.orthanc.monitoring;

import java.awt.BorderLayout;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.ParametreConnexionHttp;
import org.petctviewer.orthanc.query.AutoQueryResultTableDialog;

public class Tag_Monitoring {
	
	private ParametreConnexionHttp parametre;
	private String level;
	private JSONParser parser=new JSONParser();
	private Timer timer;
	JTextArea textAreaConsole=new JTextArea(10,80);
	
	public Tag_Monitoring(ParametreConnexionHttp parametre, String level) {
		this.parametre=parametre;
		this.level=level;
	}
	
	public void startTagMonitoring() {
		Orthanc_Monitoring monitoring=new Orthanc_Monitoring(parametre);
		monitoring.autoSetChangeLastLine();
		System.out.println("starting Tag-Monitoring");
		textAreaConsole.append("starting Tag-Monitoring"+ "\n");
		showConsoleFrame();
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				
				monitoring.makeMonitor();
				
				if (level.equals("patient")) {
					for (int i=0 ; i<monitoring.newPatientID.size(); i++) {
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/patients/"+monitoring.newPatientID.get(i));
						JSONObject patientJson = null;
						try {
							patientJson = (JSONObject) parser.parse(sb.toString());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						getMainPatientTag((JSONObject) patientJson.get("MainDicomTags"));
					}
					
				}
				else if (level.equals("study")) {
					for (int i=0 ; i<monitoring.newStudyID.size(); i++) {
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/studies/"+monitoring.newStudyID.get(i));
						try {
							getMainStudyTag((JSONObject) parser.parse(sb.toString()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				else if (level.equals("serie")) {
					for (int i=0 ; i<monitoring.newStableSeriesID.size(); i++) {
						StringBuilder sb=parametre.makeGetConnectionAndStringBuilder("/series/"+monitoring.newStableSeriesID.get(i));
						JSONObject seriesTag = null;
						try {
							seriesTag = (JSONObject) parser.parse(sb.toString());	
							String parentStudyID=(String) seriesTag.get("ParentStudy");
							StringBuilder sbParentStudy=parametre.makeGetConnectionAndStringBuilder("/studies/"+parentStudyID);
							JSONObject parentStudy=(JSONObject) parser.parse(sbParentStudy.toString());
							getMainStudyTag(parentStudy);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					
						
						StringBuilder sbSharedTags=parametre.makeGetConnectionAndStringBuilder("/series/"+monitoring.newStableSeriesID.get(i)+"/shared-tags");
						System.out.println(sbSharedTags);
						textAreaConsole.append("SeriesSharedTags " +sbSharedTags+ "\n");
					}
					
					
				}
				
				monitoring.clearAllList();
				
				
			}
			
		};
		
        //running timer task as daemon thread
        timer = new Timer(true);
        //Toutes les 90 seconds
        timer.scheduleAtFixedRate(timerTask, 0, (90*1000));
	}
	
	private void getMainPatientTag(JSONObject mainPatientTag) {
		
		String birthDate=(String) mainPatientTag.get("PatientBirthDate");
		String patientID=(String) mainPatientTag.get("PatientID");
		String patientName=(String) mainPatientTag.get("PatientName");
		String patientSex=(String) mainPatientTag.get("PatientSex");
		System.out.println("Nouveau patient");
		System.out.println("Nom "+patientName);
		System.out.println("ID " + patientID);
		System.out.println("Sexe "+ patientSex);
		System.out.println("DOB " +birthDate);
		
		textAreaConsole.append("Nouveau patient"+ "\n");
		textAreaConsole.append("Nom= "+patientName+"\n");
		textAreaConsole.append("ID= " + patientID+ "\n");
		textAreaConsole.append("Sexe= "+ patientSex+ "\n");
		textAreaConsole.append("DOB= " +birthDate+ "\n");
		
	}
	
	private void getMainStudyTag(JSONObject jsonStudy) {
		System.out.println("Nouvelle Study");
		textAreaConsole.append("Nouvelle Study"+ "\n");
		JSONObject jsonMainStudyTag=(JSONObject) jsonStudy.get("MainDicomTags");
		
		//On recupere les info Patients
		getMainPatientTag((JSONObject) jsonStudy.get("PatientMainDicomTags"));
		
		//Info Study
		String accessionNumber=(String) jsonMainStudyTag.get("AccessionNumber");
		String institutionName=(String) jsonMainStudyTag.get("InstitutionName");
		String referringPhysicianName=(String) jsonMainStudyTag.get("ReferringPhysicianName");
		String studyDate=(String) jsonMainStudyTag.get("StudyDate");
		String studyDescription=(String) jsonMainStudyTag.get("StudyDescription");
		String studyID=(String) jsonMainStudyTag.get("StudyID");
		String studyInstanceUID=(String) jsonMainStudyTag.get("StudyInstanceUID");
		String studyTime=(String) jsonMainStudyTag.get("StudyTime");
		
		
		System.out.println("accessionNumber= "+accessionNumber);
		System.out.println("institutionName= " + institutionName);
		System.out.println("referringPhysicianName= "+ referringPhysicianName);
		System.out.println("studyDate= " +studyDate);
		System.out.println("studyDescription= "+studyDescription);
		System.out.println("studyID= " + studyID);
		System.out.println("studyInstanceUID= "+ studyInstanceUID);
		System.out.println("studyTime= " +studyTime);
		
		textAreaConsole.append("accessionNumber= "+accessionNumber+ "\n");
		textAreaConsole.append("institutionName= " + institutionName+ "\n");
		textAreaConsole.append("referringPhysicianName= "+ referringPhysicianName+ "\n");
		textAreaConsole.append("studyDate= " +studyDate+ "\n");
		textAreaConsole.append("studyDescription= "+studyDescription+ "\n");
		textAreaConsole.append("studyID= " + studyID+ "\n");
		textAreaConsole.append("studyInstanceUID= "+ studyInstanceUID+ "\n");
		textAreaConsole.append("studyTime= " +studyTime+ "\n");
		
	}
	
	//SK A CONTINUER
	private void getTagFromInstance(String seriesID) {
		StringBuilder sbSeries=parametre.makeGetConnectionAndStringBuilder("/series/"+seriesID);
		JSONObject seriesJson = null;
		try {
			seriesJson = (JSONObject) parser.parse(sbSeries.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray instanceArray=(JSONArray) seriesJson.get("Instances");
		StringBuilder instanceJson=parametre.makeGetConnectionAndStringBuilder("/instances/"+instanceArray.get(0)+"/tags");
		JSONObject tags = null;
		try {
			tags = (JSONObject) parser.parse(instanceJson.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestPatient.length; i++) {
			JSONObject jsonTag=(JSONObject) tags.get(Tag_Of_Interest.tagOfInterestPatient[i]);
			String name=(String) jsonTag.get("Name");
			String value=(String) jsonTag.get("Value");
			System.out.println(name +" "+value);
		}
		
		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestStudy.length; i++) {
			JSONObject jsonTag=(JSONObject) tags.get(Tag_Of_Interest.tagOfInterestStudy[i]);
			String name=(String) jsonTag.get("Name");
			String value=(String) jsonTag.get("Value");
			System.out.println(name +" "+value);
		}
		
		for (int i=0 ; i<Tag_Of_Interest.tagOfInterestSeries.length; i++) {
			JSONObject jsonTag=(JSONObject) tags.get(Tag_Of_Interest.tagOfInterestSeries[i]);
			String name=(String) jsonTag.get("Name");
			String value=(String) jsonTag.get("Value");
			System.out.println(name +" "+value);
		}
		
		JSONObject radiopharmaceuticalSequence = (JSONObject) tags.get(Tag_Of_Interest.radiopharmaceuticalTag);
		JSONArray radiopharmaceuticalSequenceTags= (JSONArray) radiopharmaceuticalSequence.get("Value");
		JSONObject radiopharmaceuticalSequenceTagsValue = (JSONObject) radiopharmaceuticalSequenceTags.get(0);
		for (int i=0 ; i<Tag_Of_Interest.radiopharmaceutical.length; i++) {
			JSONObject jsonTag=(JSONObject) radiopharmaceuticalSequenceTagsValue.get(Tag_Of_Interest.radiopharmaceutical[i]);
			String name=(String) jsonTag.get("Name");
			String value=(String) jsonTag.get("Value");
			System.out.println(name +" "+value);
		}
		
		
	}
	
	public void stopTagMonitoring() {
		timer.cancel();
	}
	
	private void showConsoleFrame() {
		JFrame console=new JFrame();
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());
		console.add(panel);
		JScrollPane scrollPane=new JScrollPane();
		textAreaConsole.setAutoscrolls(true);
		DefaultCaret caret = (DefaultCaret) textAreaConsole.getCaret();
		caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
		scrollPane.setViewportView(textAreaConsole);
		panel.add(scrollPane, BorderLayout.CENTER);
		
	/*JButton btnCsvRetrieveReport = new JButton("Save To CSV");
		btnCsvRetrieveReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser csvReport=new JFileChooser();
				csvReport.setFileSelectionMode(JFileChooser.FILES_ONLY);
				csvReport.setSelectedFile(new File("Report_AutoRetrieve_"+df.format(new Date())+".csv"));
				int ok=csvReport.showSaveDialog(null);
				if (ok==JFileChooser.APPROVE_OPTION ) {
					AutoQueryResultTableDialog.writeCSV(textAreaConsole.getText(), csvReport.getSelectedFile().getAbsolutePath().toString());
					}
			}
		});*/
		JPanel button=new JPanel();
		//btnCsvRetrieveReport.setToolTipText("Set Folder to generate report of AutoQuery");
		//button.add(btnCsvRetrieveReport);
		panel.add(button, BorderLayout.SOUTH);
		
		console.pack();
		console.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		console.setVisible(true);
		
	}


}
