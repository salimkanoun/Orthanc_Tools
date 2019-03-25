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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
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

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.petctviewer.orthanc.Orthanc_Tools;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.export.ExportZip;
import org.petctviewer.orthanc.monitoring.Orthanc_Monitoring;
import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CD_Burner {
	
	private String burnerManifacturer;
	private String dateFormatChoix;
	private String labelFile;
	private String epsonDirectory;
	private String fijiDirectory;
	private int monitoringTime;
	private Boolean deleteStudies;
	private String suportType;
	private JTable table_burning_history;
	private Path folder;
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private SimpleDateFormat parserDate = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat formatter;
	
	private Date datenow;
	private OrthancRestApis connexion;
	private Timer timer;
	private JsonParser parser=new JsonParser();
	private HashMap<String, Object[]> burningStatus=new HashMap<String, Object[]>();
	
	private boolean levelPatient;
	
	public CD_Burner (OrthancRestApis connexion, JTable table_burning_history) {
		this.connexion=connexion;
		this.table_burning_history=table_burning_history;
		setCDPreference();
		formatter= new SimpleDateFormat(dateFormatChoix);
	}

	
	/**
	 * Start Monitoring of Orthanc Change API every 90secs
	 */
	public boolean startCDMonitoring() {
		if ( epsonDirectory==null ||fijiDirectory==null ||labelFile==null || dateFormatChoix==null ){
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
					if(levelPatient) {
						makeCDFromPatient(monitoring.newStablePatientID);
					}else {
						makeCD(monitoring.newStableStudyID);
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
	
	private void makeCDFromPatient(List<String> newStablePatientID) {
		for (String patientID : newStablePatientID) {
			//Store the Row number where we are going to display progress
			int rownumber=table_burning_history.getRowCount();
			
			StringBuilder answer=connexion.makeGetConnectionAndStringBuilder("/patients/"+ patientID);
			JsonObject jsonResponse=parser.parse(answer.toString()).getAsJsonObject();			
			JsonObject mainPatientTag=jsonResponse.get("MainDicomTags").getAsJsonObject();
			JsonArray studiesOrthancId=jsonResponse.get("Studies").getAsJsonArray();
			
			int nbOfStudies=studiesOrthancId.size();
			
			if(nbOfStudies==1) {
				List<String> newStableStudyID=new ArrayList<String>();
				newStableStudyID.add(studiesOrthancId.get(0).getAsString());
				makeCD(newStableStudyID);
				return;
			}
			
			
			//Get value of interest : Patient Name / ID / DOB / study date and description
			String nom="N/A";
			if(mainPatientTag.has("PatientName")) {
				nom=mainPatientTag.get("PatientName").getAsString();
			}
			
			String id="N/A";
			if(mainPatientTag.has("PatientID")) {
				id=mainPatientTag.get("PatientID").getAsString();
			}

			
			String formattedPatientDOB="N/A";
			try {
				String patientDOB=mainPatientTag.get("PatientBirthDate").getAsString();
				Date patientDOBDate = parserDate.parse(patientDOB);
				formattedPatientDOB = formatter.format(patientDOBDate);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			
			StringBuilder answerStudies=connexion.makeGetConnectionAndStringBuilder("/patients/"+ patientID+"/studies?expand");
			JsonArray jsonStudiesResponse=parser.parse(answerStudies.toString()).getAsJsonArray();
			
			DatInfos[] datInfos=new DatInfos[jsonStudiesResponse.size()];
			
			for(int i=0; i<jsonStudiesResponse.size() ; i++) {
				JsonObject studyJsonObj=jsonStudiesResponse.get(i).getAsJsonObject();
				JsonObject mainDicomTags=studyJsonObj.get("MainDicomTags").getAsJsonObject();
				
				String formattedDateExamen = "N/A";
				if(mainDicomTags.has("StudyDate")) {
					try {
						Date dateExamen = parserDate.parse(mainDicomTags.get("StudyDate").getAsString());
						formattedDateExamen = formatter.format(dateExamen);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
				}
				
				
				String studyDescription="N/A";
				if(mainDicomTags.has("StudyDescription")) {
					studyDescription=mainDicomTags.get("StudyDescription").getAsString();
				}
						
				String accessionNumber="N/A";
				if(mainDicomTags.has("AccessionNumber")) {
					accessionNumber=mainDicomTags.get("AccessionNumber").getAsString();
				}
				datInfos[i]=new DatInfos(nom, id, formattedDateExamen, studyDescription, accessionNumber, formattedPatientDOB);
				
			}

			//Update display status
			(( DefaultTableModel) table_burning_history.getModel()).addRow(new String[]{nom, id, formattedPatientDOB ,"Mutiples", nbOfStudies+" studies" ,"Recieved" });
			table_burning_history.setValueAt("Retriving DICOMs", rownumber, 5);
			
			//Generate the ZIP with Orthanc IDs dicom
			ArrayList<String> orthancIds=new ArrayList<String>();
			for(JsonElement studyID : studiesOrthancId) {
				orthancIds.add(studyID.getAsString());
			}
			File zip=generateZip(orthancIds);
			
			// Unzip du fichier ZIP recupere
			table_burning_history.setValueAt("Unzipping", rownumber, 5);
			unzip(zip);
			
			File robotRequestFile=null;
			// Creation du Cd
			if (burnerManifacturer.equals("Epson")) {
				String discType=determineDiscType();
				File dat=printDat(datInfos);
				//Generation du Dat
				//File dat = printDat(nom, id, formattedDateExamen, studyDescription, accessionNumber, formattedPatientDOB );
				robotRequestFile=createCdBurnerEpson(dat, discType, nom, "Mutiples");
				
			} else if(burnerManifacturer.equals("Primera")) {
				robotRequestFile=createCdBurnerPrimera(nom, id, "Mutiples", nbOfStudies+" studies", "Mutiples", formattedPatientDOB ,nbOfStudies);
			}
			
			//Put the JDF base name associated to the Row number of the table for Monitoring
			burningStatus.put(FilenameUtils.getBaseName(robotRequestFile.getAbsolutePath().toString()), new Object[] {rownumber, folder.toFile()});
			
			table_burning_history.setValueAt("Sent to Burner", rownumber, 5);
			
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
	 */
	public void makeCD(List<String> newStableStudyID) {
		for (String studyID : newStableStudyID) {
			
			//Store the Row number where we are going to display progress
			int rownumber=table_burning_history.getRowCount();
			StringBuilder answer=connexion.makeGetConnectionAndStringBuilder("/studies/"+ studyID);
			JsonObject response=parser.parse(answer.toString()).getAsJsonObject();			
			JsonObject mainPatientTag=response.get("PatientMainDicomTags").getAsJsonObject();
			
			//Get value of interest : Patient Name / ID / DOB / study date and description
			String nom="N/A";
			if(mainPatientTag.has("PatientName")) {
				nom=mainPatientTag.get("PatientName").getAsString();
			}
			
			String id="N/A";
			if(mainPatientTag.has("PatientID")) {
				id=mainPatientTag.get("PatientID").getAsString();
			}

			JsonObject mainDicomTag=response.get("MainDicomTags").getAsJsonObject();
			
			String studyDescription="N/A";
			if(mainDicomTag.has("StudyDescription")) {
				studyDescription=mainDicomTag.get("StudyDescription").getAsString();
			}

			String accessionNumber="N/A";
			if(mainDicomTag.has("AccessionNumber")) {
				mainDicomTag.get("AccessionNumber").getAsString();
			}

			String formattedDateExamen="N/A";
			try {
				String studyDate=mainDicomTag.get("StudyDate").getAsString();
				Date dateExamen = parserDate.parse(studyDate);
				formattedDateExamen = formatter.format(dateExamen);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			String formattedPatientDOB="N/A";
			try {
				String patientDOB=mainPatientTag.get("PatientBirthDate").getAsString();
				Date patientDOBDate = parserDate.parse(patientDOB);
				formattedPatientDOB = formatter.format(patientDOBDate);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
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

			File robotRequestFile=null;
			// Creation du Cd
			if (burnerManifacturer.equals("Epson")) {
				String discType=determineDiscType();
				//Generation du Dat
				File dat = printDat(nom, id, formattedDateExamen, studyDescription, accessionNumber, formattedPatientDOB );
				robotRequestFile=createCdBurnerEpson(dat, discType, nom, formattedDateExamen);
				
			} else if(burnerManifacturer.equals("Primera")) {
				robotRequestFile=createCdBurnerPrimera(nom, id, formattedDateExamen, studyDescription, accessionNumber, formattedPatientDOB, 1);
			}
			
			//Put the JDF base name associated to the Row number of the table for Monitoring
			burningStatus.put(FilenameUtils.getBaseName(robotRequestFile.getAbsolutePath().toString()), new Object[] {rownumber, folder.toFile()});
			
			table_burning_history.setValueAt("Sent to Burner", rownumber, 5);
			
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
			Long ViewerSize=FileUtils.sizeOfDirectory(new File(fijiDirectory));
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
	private File createCdBurnerEpson(File dat, String discType, String name, String formattedStudyDate){
		//REalisation du texte pour le Robot
		String txtRobot= "# Making data CD\n";
		//Peut definir le Job ID et le mettre le compteur dans registery si besoin de tracer les operation avec fichier STF
		if(createJobID(name, formattedStudyDate)!=null) txtRobot+="JOB_ID="+createJobID(name, formattedStudyDate)+"\n";
		
		txtRobot+="#nombre de copies\n"
		+ "COPIES=1\n"
		+ "#CD ou DVD\n"
		+ "DISC_TYPE="+discType+"\n"
		+ "FORMAT=UDF102\n"
		+ "DATA="+fijiDirectory+"\n"
		+ "DATA="+folder+ File.separator+ "DICOM" +File.separator+"\n"
		+ "#Instruction d'impression\n"
		+ "LABEL="+labelFile+"\n"
		+ "REPLACE_FIELD="+dat.getAbsolutePath().toString();

		// On ecrit le fichier JDF
		File f = new File(epsonDirectory + File.separator + "CD_"+dateFormat.format(datenow)+".JDF");
		
		Orthanc_Tools.writeCSV(txtRobot, f);
		
		return f;
				
	}
	
	/**
	 * Method for Primera Disc Burner
	 * @param nom
	 * @param id
	 * @param date
	 * @param studyDescription
	 * @param discType
	 */
	private File createCdBurnerPrimera(String nom, String id, String date, String studyDescription, String accessionNumber, String patientDOB, int nbStudies){
		//Command Keys/Values for Primera Robot
		String txtRobot=new String();
		
		if(createJobID(nom, date) != null) txtRobot +="JobID="+createJobID(nom,date)+"\n";
			
		txtRobot+="Copies = 1\n"
				+ "DataImageType = UDF\n"
				+ "Data="+fijiDirectory+"\n"
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
				+ "MergeField="+nbStudies+"\n";
		
				// Making a .JRQ file in the watched folder
				File f = new File(epsonDirectory + File.separator + "CD_"+dateFormat.format(datenow)+".JRQ");
				
				Orthanc_Tools.writeCSV(txtRobot, f);
				
				return f;
					
	}
	
	//Creer le fichier DAT pour injecter NOM, Date, Modalite
	private File printDat(String nom, String id, String date, String studyDescription, String accessionNumber, String patientDOB) {

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
						//patient date is a duplicate of studydate (depreciated)
						+ "patientDate="+ infos[0].patientDOB + "\n"
						+ "studyDescription="+ infos[0].studyDescription+"\n"
						+ "accessionNumber="+ infos[0].accessionNumber+"\n"
						+ "patientDOB="+infos[0].patientDOB+"\n"
						+ "numberOfStudies="+infos.length+"\n";
			
			for(int i=1; i<infos.length ; i++) {
				datFile+= "studyDate"+(i+1)+"="+ infos[i].date + "\n"
						+ "studyDescription"+(i+1)+"="+ infos[i].studyDescription+"\n"
						+ "accessionNumber"+(i+1)+"="+ infos[i].accessionNumber+"\n";
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
		    			FileUtils.deleteDirectory(tempFolder);
		    		}else if(extension.equals("INP")) {
		    			table_burning_history.setValueAt("Burning In Progress", rowNubmer, 5);
		    		}else if(extension.equals("DON")) {
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
				fijiDirectory=jPrefer.get("Burner_fijiDirectory", null);
				epsonDirectory=jPrefer.get("Burner_epsonDirectory", null);
				labelFile=jPrefer.get("Burner_labelFile", null);
				dateFormatChoix=jPrefer.get("Burner_DateFormat", "yyyyMMdd");
				deleteStudies=jPrefer.getBoolean("Burner_deleteStudies", false);
				suportType=jPrefer.get("Burner_suportType", "Auto");
				monitoringTime=jPrefer.getInt("Burner_monitoringTime", 90);
				levelPatient=jPrefer.getBoolean("Burner_levelPatient", false);
				
				
		
				
	}
	
	private class DatInfos {
		
		 public String nom, id, date, studyDescription, accessionNumber, patientDOB;
		
		 public DatInfos(String nom, String id, String date, String studyDescription, String accessionNumber, String patientDOB) {
			 this.nom=nom;
			 this.id=id;
			 this.date=date;
			 this.studyDescription=studyDescription;
		     this.accessionNumber=accessionNumber;
		     this.patientDOB=patientDOB;
		 }
	}
}
