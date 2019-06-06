/**
Copyright (C) 2017 KANOUN Salim

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

package org.petctviewer.orthanc.monitoring.cdburner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.petctviewer.orthanc.Orthanc_Tools;
import org.petctviewer.orthanc.anonymize.QueryOrthancData;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Patient;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.export.ExportZip;
import org.petctviewer.orthanc.monitoring.Orthanc_Monitoring;
import org.petctviewer.orthanc.setup.OrthancRestApis;

public class CD_Burner {
	
	private String burnerManifacturer;
	private String dateFormatChoix;
	private String labelFile;
	private String epsonDirectory;
	private String viewerDirectory;
	private int monitoringTime;
	private Boolean deleteStudies;
	private Boolean playSounds;
	private String suportType;
	private JTable table_burning_history;
	private Path folder;
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private SimpleDateFormat parserDate = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat formatter;
	private QueryOrthancData ortancQuery;
	
	private Date datenow;
	private OrthancRestApis connexion;
	private Timer timer;
	private HashMap<String, Object[]> burningStatus=new HashMap<String, Object[]>();
	
	private boolean levelPatient;
	
	public CD_Burner (OrthancRestApis connexion, JTable table_burning_history) {
		this.connexion=connexion;
		ortancQuery=new QueryOrthancData(connexion); 
		this.table_burning_history=table_burning_history;
		setCDPreference();
		formatter= new SimpleDateFormat(dateFormatChoix);
	}

	
	/**
	 * Start Monitoring of Orthanc Change API every 90secs
	 */
	public boolean startCDMonitoring() {
		if ( epsonDirectory==null ||viewerDirectory==null ||labelFile==null || dateFormatChoix==null ){
			//Message d'erreur doit faire le set de output folder
			JOptionPane.showMessageDialog(null, "Go to settings Menu to set missing paths", "Set directories and date format", JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			Orthanc_Monitoring monitoring=new Orthanc_Monitoring(connexion);
			//Met la derniere ligne pour commencer le monitoring
			monitoring.autoSetChangeLastLine();
			
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					monitoring.makeMonitor();
					try {
						if(levelPatient) {
								makeCDFromPatient(monitoring.newStablePatientID);
						}else {
							makeCD(monitoring.newStableStudyID);
						}
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					monitoring.clearAllList();
					try {
						updateProgress();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}	
			};
			
	        //running timer task as daemon thread
	        timer = new Timer(true);
	        //Toutes les 90 seconds
	        timer.scheduleAtFixedRate(timerTask, 0, (monitoringTime*1000));
	        return true;
		}
		
	}
	
	/**
	 * Stop the monitoring every 90secs
	 */
	public void stopCDMonitoring() {
		if(timer!=null) {
			timer.cancel();
		}
	}
	
	private void makeCDFromPatient(List<String> newStablePatientID) throws Exception {
		for (String patientID : newStablePatientID) {
			//Store the Row number where we are going to display progress
			int rownumber=table_burning_history.getRowCount();
			Patient patient =ortancQuery.getPatient(patientID);
			
			ArrayList<Study2> studies= ortancQuery.getAllStudiesOfPatient(patientID, true);
			
			if(studies.size()==1) {
				List<String> newStableStudyID=new ArrayList<String>();
				newStableStudyID.add(studies.get(0).getOrthancId());
				makeCD(newStableStudyID);
				continue;
			}
			
			String formattedPatientDOB="N/A";
			try {
				String patientDOB=patient.getPatientBirthDate();
				Date patientDOBDate = parserDate.parse(patientDOB);
				formattedPatientDOB = formatter.format(patientDOBDate);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			DatInfos[] datInfos=new DatInfos[studies.size()];
			
			ArrayList<String> uniqueModalitiesForPrimera = new ArrayList<String>();
			
			
			for(int i=0; i<studies.size() ; i++) {
				
				String formattedDateExamen = "N/A";
				if(studies.get(i).getDate()!=null) formattedDateExamen=formatter.format(studies.get(i).getDate());
				String studyDescription=studies.get(i).getStudyDescription();	
				String accessionNumber=studies.get(i).getAccession();
				String modalitiesInStudy = String.join("//", studies.get(i).getModalitiesInStudy());
				for(String modality: studies.get(i).getModalitiesInStudy()) {
					if(!uniqueModalitiesForPrimera.contains(modality)) {
						uniqueModalitiesForPrimera.add(modality);
					}
				}
				datInfos[i]=new DatInfos(patient.getName(), patient.getPatientId(), formattedDateExamen, studyDescription, accessionNumber, formattedPatientDOB, modalitiesInStudy);
				
			}
			
			String modalitiesInStudyPrimera = String.join("//", uniqueModalitiesForPrimera);

			//Update display status
			(( DefaultTableModel) table_burning_history.getModel()).addRow(new String[]{patient.getName(), patient.getPatientId(), formattedPatientDOB ,"Mutiples", studies.size()+" studies" ,"Recieved", null });
			table_burning_history.setValueAt("Retriving DICOMs", rownumber, 5);
			
			//Generate the ZIP with Orthanc IDs dicom
			ArrayList<String> orthancIds=new ArrayList<String>();
			for(Study2 study:studies) {
				orthancIds.add(study.getOrthancId());
			}
			File zip=generateZip(orthancIds);
			
			// Unzip du fichier ZIP recupere
			table_burning_history.setValueAt("Unzipping", rownumber, 5);
			unzip(zip);
			
			
			Object[] requestFileAndID=null;
			// Creation du Cd
			if (burnerManifacturer.equals("Epson")) {
				String discType=determineDiscType();
				File dat=printDat(datInfos);
				//Generation du Dat
				//File dat = printDat(nom, id, formattedDateExamen, studyDescription, accessionNumber, formattedPatientDOB );
				requestFileAndID=createCdBurnerEpson(dat, discType, patient.getName(), "Mutiples");
				
			} else if(burnerManifacturer.equals("Primera")) {
				requestFileAndID=createCdBurnerPrimera(patient.getName(), patient.getPatientId(), "Mutiples", studies.size()+" studies", "Mutiples", formattedPatientDOB ,studies.size(), modalitiesInStudyPrimera);
			}
			
			//Put the JDF base name associated to the Row number of the table for Monitoring
			String requestFileName=FilenameUtils.getBaseName(((File)requestFileAndID[0]).getAbsolutePath().toString());
			burningStatus.put(requestFileName, new Object[] {rownumber, folder.toFile()});
			
			table_burning_history.setValueAt("Sent to Burner", rownumber, 5);
			
			
			//Add cancel Button
			table_burning_history.setValueAt(requestFileAndID[1], rownumber, 7);
			table_burning_history.setValueAt(requestFileName, rownumber, 8);
			
			//On efface tout a la sortie JVM
			recursiveDeleteOnExit(folder);
			//Efface le zip dezipe
			zip.delete();
			
			//On efface la study de Orthanc
			if (deleteStudies) {
				connexion.makeDeleteConnection("/patients/"+patientID);
			}
			
		}

	}
	
	/**
	 * Make CD structure, download ZIP from Orthanc at study level and make CD process creation
	 * @param newStableStudyID
	 * @throws Exception 
	 */
	public void makeCD(List<String> newStableStudyID) throws Exception {
	
		for (String studyID : newStableStudyID) {
			
			Study2 study=ortancQuery.getStudyDetails(studyID, true);
			Patient patient= ortancQuery.getPatient(study.getParentPatientId());
			//Store the Row number where we are going to display progress
			int rownumber=table_burning_history.getRowCount();
			
			//Get value of interest : Patient Name / ID / DOB / study date and description
			String nom=patient.getName();
			String id=patient.getPatientId();
			String studyDescription=study.getStudyDescription();
			String accessionNumber=study.getAccession();
		
			String formattedDateExamen="N/A";
			if(study.getDate()!=null) {
				formattedDateExamen = formatter.format(study.getDate());
			}
			
			String formattedPatientDOB="N/A";
			try {
				Date patientDOBDate = parserDate.parse(patient.getPatientBirthDate());
				formattedPatientDOB = formatter.format(patientDOBDate);
			}catch (Exception e) { }
			
			String modalitiesInStudy = String.join("//", study.getModalitiesInStudy());
			
			//Update display status
			(( DefaultTableModel) table_burning_history.getModel()).addRow(new String[]{nom,id, formattedPatientDOB , formattedDateExamen ,studyDescription,"Recieved" });
			table_burning_history.setValueAt("Retriving DICOMs", rownumber, 5);
			
			//Generate the ZIP with Orthanc IDs dicom
			ArrayList<String> orthancIds=new ArrayList<String>();
			orthancIds.add(studyID);
			File zip=generateZip(orthancIds);
			
			// Unzip du fichier ZIP recupere
			table_burning_history.setValueAt("Unzipping", rownumber, 5);
			unzip(zip);

			Object[] requestFileAndID=null;
			// Creation du Cd
			if (burnerManifacturer.equals("Epson")) {
				String discType=determineDiscType();
				//Generation du Dat
				File dat = printDat(nom, id, formattedDateExamen, studyDescription, accessionNumber, formattedPatientDOB, modalitiesInStudy );
				requestFileAndID=createCdBurnerEpson(dat, discType, nom, formattedDateExamen);
				
			} else if(burnerManifacturer.equals("Primera")) {
				requestFileAndID=createCdBurnerPrimera(nom, id, formattedDateExamen, studyDescription, accessionNumber, formattedPatientDOB, 1, modalitiesInStudy);
			}
			
			//Put the JDF base name associated to the Row number of the table for Monitoring
			String requestFileName=FilenameUtils.getBaseName(((File) requestFileAndID[0]).getAbsolutePath().toString());
			burningStatus.put(requestFileName, new Object[] {rownumber, folder.toFile()});
			
			table_burning_history.setValueAt("Sent to Burner", rownumber, 5);
			//Add cancel Button
			table_burning_history.setValueAt(requestFileAndID[1], rownumber, 7);
			table_burning_history.setValueAt(requestFileName, rownumber, 8);
			
			//On efface tout a la sortie JVM
			recursiveDeleteOnExit(folder);
			//Efface le zip dezipe
			zip.delete();
			
			//On efface la study de Orthanc
			if (deleteStudies) connexion.makeDeleteConnection("/studies/"+studyID);
				
		}
	}
	
	private File generateZip(ArrayList<String> orthancIds) {
		File zip=null;
		try {
			datenow=new Date();
			Path file = Files.createTempFile("CD_"+dateFormat.format(datenow) , ".zip");
			file.toFile().deleteOnExit();			
			ExportZip zipDownloader=new ExportZip(connexion);
			zipDownloader.setConvertZipAction(file.toString(), orthancIds, true);
			//generate ZIP of DICOMs
			zipDownloader.generateZip(true);
			zip=zipDownloader.getGeneratedZipFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zip;
	}
	
	private String determineDiscType() {
		String discType;
		if ( !suportType.equals("Auto")) {
			discType=suportType;
		} else {
			//Get size of viewer and images to determine if CD or DVD to Burn
			Long imageSize=FileUtils.sizeOfDirectory(folder.toFile());
			Long ViewerSize=FileUtils.sizeOfDirectory(new File(viewerDirectory));
			//If size over 630 Mo
			if(Long.sum(imageSize,ViewerSize) > 630000000) {
				discType="DVD";
			} else {
				discType="CD";
			}
		}
		
		return discType;
	}
	
	private void unzip(File zipFile){
	     byte[] buffer = new byte[1024];
	     try {
	    	//create output directory is not exists
	    	folder = Files.createTempDirectory("CD_"+dateFormat.format(datenow));
	    
	    	//get the zip file content
	    	ZipInputStream zis;
			zis = new ZipInputStream(new FileInputStream(zipFile));
			
	    	//get the zipped file list entry
	    	ZipEntry ze = zis.getNextEntry();
	    	
	    	while(ze!=null){
	     	   	String fileName = ze.getName();
	     	    
	     	   File newFile = new File(folder + File.separator + "DICOM" +File.separator+fileName);
	            
	            if (ze.isDirectory()) {
	         	// if the entry is a directory, make the directory
	                newFile.mkdirs();
	            }
	            else {
	         	    new File(newFile.getParent()).mkdirs();
	                 //create all non exists folders else you will hit FileNotFoundException for compressed folder
	                 FileOutputStream fos = new FileOutputStream(newFile);
	                 int len;
	                 while ((len = zis.read(buffer)) > 0) {
	            		fos.write(buffer, 0, len);
	                 }
	
	                 fos.close();
	                 
	            }
	            ze = zis.getNextEntry();
	     	}
	        zis.closeEntry();
	    	zis.close();
	    
	     } catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	
	/**
	 * Cree fichier JDF pour le graveur (parametrage de la gravure)
	 * @param nom
	 * @param id
	 * @param date
	 * @param studyDescription
	 * @param dat
	 */
	private Object[] createCdBurnerEpson(File dat, String discType, String name, String formattedStudyDate){
		//REalisation du texte pour le Robot
		String txtRobot= "# Making data CD\n";
		String jobId=createJobID(name, formattedStudyDate);
		//Peut definir le Job ID et le mettre le compteur dans registery si besoin de tracer les operation avec fichier STF
		if(jobId!=null) txtRobot+="JOB_ID="+jobId+"\n";
		
		txtRobot+="#nombre de copies\n"
		+ "COPIES=1\n"
		+ "#CD ou DVD\n"
		+ "DISC_TYPE="+discType+"\n"
		+ "FORMAT=UDF102\n"
		+ "DATA="+viewerDirectory+"\n"
		+ "DATA="+folder+ File.separator+ "DICOM" +File.separator+"\n"
		+ "#Instruction d'impression\n"
		+ "LABEL="+labelFile+"\n"
		+ "REPLACE_FIELD="+dat.getAbsolutePath().toString();

		// On ecrit le fichier JDF
		File f = new File(epsonDirectory + File.separator + "CD_"+dateFormat.format(datenow)+".JDF");
		
		Orthanc_Tools.writeCSV(txtRobot, f);
		
		Object[] answer=new Object[] {f, jobId};
		return answer;
				
	}
	
	/**
	 * Method for Primera Disc Burner
	 * @param nom
	 * @param id
	 * @param date
	 * @param studyDescription
	 * @param discType
	 */
	private Object[] createCdBurnerPrimera(String nom, String id, String date, String studyDescription, String accessionNumber, String patientDOB, int nbStudies, String modalities){
		//Command Keys/Values for Primera Robot
		String txtRobot=new String();
		String jobId=createJobID(nom, date);
		
		if(jobId != null) txtRobot +="JobID="+jobId+"\n";
			
		txtRobot+="ClientID = Orthanc-Tools"
				+"Copies = 1\n"
				+ "DataImageType = UDF\n"
				+ "Data="+viewerDirectory+"\n"
				+ "Data="+folder+ File.separator+ "DICOM\n"
				+ "RejectIfNotBlank=YES\n"
				+ "CloseDisc=YES\n"
				+ "VerifyDisc=YES\n"
				/* PrintQuality - This key specifies the print quality. Key is optional.
				The possible values : Low = 0, Medium =1, Better =2 High =3 Best =4*/
				+ "PrintQuality=1\n"
				/*PrintLabel - This specifies path and filename of the label to print on disc.
                The possible file types are .STD (SureThingTM), .jpg (JPEG), .bmp (Windows Bitmap), or .PRN (printed to file through any application). 
                If this key is not given then no printing will be performed. 
                */
				+ "PrintLabel="+labelFile+"\n"
				/* MergeField - This key specifies a merge field for SureThing printing.
				The print file specified within the JRQ must be a SureThing file, 
				and it must have been designed with a Merge File specified.
				Fields should be specified in the correct order to match the SureThing design.
				*/
				+ "MergeField="+nom+"\n"
				+ "MergeField="+id+"\n"
				+ "MergeField="+date+"\n"
				+ "MergeField="+studyDescription+"\n"
				+ "MargeField="+patientDOB+"\n"
				+ "MergeField="+accessionNumber+"\n"
				+ "MergeField="+nbStudies+"\n"
				+ "MergeField="+modalities+"\n";
		
		// Making a .JRQ file in the watched folder
		File f = new File(epsonDirectory + File.separator + "CD_"+dateFormat.format(datenow)+".JRQ");
		
		Orthanc_Tools.writeCSV(txtRobot, f);
		
		Object[] answer=new Object[] {f, jobId};
		return answer;
					
	}
	
	//Creer le fichier DAT pour injecter NOM, Date, Modalite
	private File printDat(String nom, String id, String date, String studyDescription, String accessionNumber, String patientDOB, String modalities) {

       //On parse le nom pour enlever les ^ et passer le prenom en minuscule
       int separationNomPrenom=nom.indexOf("^", 0);
       if (separationNomPrenom!=-1) {
    	   nom=nom.substring(0, separationNomPrenom+2)+nom.substring(separationNomPrenom+2).toLowerCase();
       }
       
		String datFile = "patientName="+nom.replaceAll("\\^", " ")+"\n"
					+ "patientId=" + id +"\n"
					+ "studyDate="+ date + "\n"
					//patient date is a duplicate of studydate (depreciated)
					+ "patientDate="+ date + "\n"
					+ "studyDescription="+ studyDescription+"\n"
					+ "accessionNumber="+ accessionNumber+"\n"
					+ "patientDOB="+patientDOB+"\n"
					+ "modalities="+modalities+"\n"
					+ "numberOfStudies=1";
		
		
		File dat = new File(folder + File.separator + "CD"+dateFormat.format(datenow)+".dat");
		
		Orthanc_Tools.writeCSV(datFile, dat);
		return dat;
	}
	
	
	private File printDat(DatInfos[] infos) {

	       //On parse le nom pour enlever les ^ et passer le prenom en minuscule
		   String nom=infos[0].nom;
	       int separationNomPrenom=nom.indexOf("^", 0);
	       if (separationNomPrenom!=-1) {
	    	   nom=nom.substring(0, separationNomPrenom+2)+nom.substring(separationNomPrenom+2).toLowerCase();
	       }
	       
			String datFile = "patientName="+nom.replaceAll("\\^", " ")+"\n"
						+ "patientId=" + infos[0].id +"\n"
						+ "studyDate="+ infos[0].date + "\n"
						+ "modalities="+infos[0].modalities+"\n"
						//patient date is a duplicate of studydate (depreciated)
						+ "patientDate="+ infos[0].patientDOB + "\n"
						+ "studyDescription="+ infos[0].studyDescription+"\n"
						+ "accessionNumber="+ infos[0].accessionNumber+"\n"
						+ "patientDOB="+infos[0].patientDOB+"\n"
						+ "numberOfStudies="+infos.length+"\n";
			
			for(int i=1; i<infos.length ; i++) {
				datFile+= "studyDate"+(i+1)+"="+ infos[i].date + "\n"
						+ "studyDescription"+(i+1)+"="+ infos[i].studyDescription+"\n"
						+ "accessionNumber"+(i+1)+"="+ infos[i].accessionNumber+"\n"
						+ "modalities"+(i+1)+"="+infos[i].modalities+"\n";
			}
			
			
			File dat = new File(folder + File.separator + "CD"+dateFormat.format(datenow)+".dat");
			
			Orthanc_Tools.writeCSV(datFile, dat);
			return dat;
	}
	
	private void updateProgress() throws Exception {
		File folder = new File(epsonDirectory);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	String baseName=FilenameUtils.getBaseName(file.toString());
		    	if(burningStatus.containsKey(baseName)) {
		    		String extension=FilenameUtils.getExtension(file.toString());
		    		int rowNubmer=(int) burningStatus.get(baseName)[0];
		    		File tempFolder=(File) burningStatus.get(baseName)[1];
		    		if(extension.equals("ERR")) {
		    			table_burning_history.setValueAt("Burning Error", rowNubmer, 5);
		    			burningStatus.remove(baseName);
		    			playSound(false);
		    			FileUtils.deleteDirectory(tempFolder);
		    		}else if(extension.equals("INP")) {
		    			table_burning_history.setValueAt("Burning In Progress", rowNubmer, 5);
		    		}else if(extension.equals("DON")) {
		    			playSound(true);
		    			burningStatus.remove(baseName);
		    			table_burning_history.setValueAt("Burning Done", rowNubmer, 5);
		    			FileUtils.deleteDirectory(tempFolder);
		    		}else if(extension.equals("STP")) {
		    			table_burning_history.setValueAt("Paused", rowNubmer, 5);
		    		}
		    	}
		    }
		}
		
	}
	
	private String createJobID(String name, String formattedStudyDate) {
		String lastName = null;
		String firstName= "";
		//prepare JOB_ID string.
		if(name.contains("^")) {
			String[] names=name.split(Pattern.quote("^"));
			//Get 10 first character of lastname and first name if input over 10 characters
			if(names[0].length()>5) lastName=names[0].substring(0, 5); else lastName=names[0];
			if(names[1].length()>5) firstName=names[1].substring(0, 5); else firstName=names[1];
			
		}else {
			if(!StringUtils.isEmpty(name)) {
				if(name.length()>10) lastName=name.substring(0, 10); else lastName=name;
			//No name information return null
			}else {
				return null;
			}
			
		}
		
		String results=lastName+"_"+firstName+"_"+formattedStudyDate+"_"+( (int) Math.round(Math.random()*1000));
		//Remove Accent and space to match requirement of burners
		results=StringUtils.stripAccents(results);
		results=StringUtils.deleteWhitespace(results);
		//Remove non alpha numeric character (except let _)
		results=results.replaceAll("[^a-zA-Z0-9_]", ""); 
		
		
		return results;
	}
	
	/**
	 * Delete a path itself and all subdirectories
	 * @param path
	 * @throws IOException
	 */
	public static void recursiveDeleteOnExit(Path path) {
		  try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult visitFile(Path file,
			        BasicFileAttributes attrs) {
			      file.toFile().deleteOnExit();
			      return FileVisitResult.CONTINUE;
			    }
			    @Override
			    public FileVisitResult preVisitDirectory(Path dir,
			        BasicFileAttributes attrs) {
			      dir.toFile().deleteOnExit();
			      return FileVisitResult.CONTINUE;
			    }
			  });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setCDPreference() {
				//On prends les settings du registery
				Preferences jPrefer = VueAnon.jprefer;
				burnerManifacturer=jPrefer.get("Burner_buernerManufacturer", "Epson");
				viewerDirectory=jPrefer.get("viewerDistribution", null);
				epsonDirectory=jPrefer.get("Burner_epsonDirectory", null);
				labelFile=jPrefer.get("Burner_labelFile", null);
				dateFormatChoix=jPrefer.get("Burner_DateFormat", "yyyyMMdd");
				deleteStudies=jPrefer.getBoolean("Burner_deleteStudies", false);
				suportType=jPrefer.get("Burner_suportType", "Auto");
				monitoringTime=jPrefer.getInt("Burner_monitoringTime", 90);
				levelPatient=jPrefer.getBoolean("Burner_levelPatient", false);
				playSounds=jPrefer.getBoolean("Burner_playSounds", false);
				
	}
	
	public boolean getIsPrimera() {
		return burnerManifacturer.equals("Primera")?true:false;
		
	}
	
	public void cancelJob(String jobId, String requestFileName) {
		if(getIsPrimera()) {
			String ptmString = "Message = ABORT\n"
					+ "ClientID = Orthanc-Tools";
			File ptm = new File(epsonDirectory + File.separator + requestFileName+".PTM");
			Orthanc_Tools.writeCSV(ptmString, ptm);
		}else {
			String jcfString="[CANCEL]\n"
					+ "JOB_ID="+jobId;
			File jcf = new File(epsonDirectory + File.separator + requestFileName+".JCF");
			Orthanc_Tools.writeCSV(jcfString, jcf);
		}
		
		
		
	}
	
	/**
	 * Play sounds (sucess or error sounds depending on boolean) only if sounds activated in the options
	 * @param success
	 */
	private void playSound(boolean success) {
		if(this.playSounds) {
			try {
				URL url=null;
				if(success) {
					url=ClassLoader.getSystemResource("logos/cd_Success.wav");
				}else {
					url=ClassLoader.getSystemResource("logos/cd_Error.wav");
				}
				
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(url);
		        
		        DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
		        Clip clip = (Clip) AudioSystem.getLine(info);
				
		        clip.open(inputStream);
		        clip.start(); 
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
	
	private class DatInfos {
		
		 public String nom, id, date, studyDescription, accessionNumber, patientDOB, modalities;
		
		 public DatInfos(String nom, String id, String date, String studyDescription, String accessionNumber, String patientDOB, String modalities) {
			 this.nom=nom;
			 this.id=id;
			 this.date=date;
			 this.studyDescription=studyDescription;
		     this.accessionNumber=accessionNumber;
		     this.patientDOB=patientDOB;
		     this.modalities=modalities;
		 }
	}
}
