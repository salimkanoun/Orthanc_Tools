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

package org.petctviewer.orthanc.monitoring;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.petctviewer.orthanc.ParametreConnexionHttp;
import org.petctviewer.orthanc.anonymize.ConvertZipAction;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class CD_Burner {
	
	private  String dateFormatChoix;
	private  String labelFile;
	private  String epsonDirectory;
	private  String fijiDirectory;
	private  Boolean deleteStudies;
	private String suportType;
	private JTextArea textArea;
	private Path folder;
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private Date datenow;
	private ParametreConnexionHttp connexion;
	private Timer timer;
	
	public CD_Burner (ParametreConnexionHttp connexion, JTextArea textArea) {
		this.connexion=connexion;
		this.textArea=textArea;
		setCDPreference();
	}

	
	/**
	 * Start Monitoring of Orthanc Change API every 90secs
	 */
	public void startCDMonitoring() {
		if ( epsonDirectory==null ||fijiDirectory==null ||labelFile==null || dateFormatChoix==null ){
			//Message d'erreur doit faire le set de output folder
			JOptionPane.showMessageDialog(null, "Go to settings Menu to set missing paths", "Set directories and date format", JOptionPane.ERROR_MESSAGE);
		}
		else {
			textArea.append("Monitoring Orthanc \n");
			Orthanc_Monitoring monitoring=new Orthanc_Monitoring(connexion);
			//Met la derniere ligne pour commencer le monitoring
			monitoring.autoSetChangeLastLine();
			
			TimerTask timerTask = new TimerTask() {
	
				@Override
				public void run() {
					System.out.println("starting scann");
					monitoring.makeMonitor();
					makeCD(monitoring.newStableStudyID);
					monitoring.clearAllList();
					
				}
				
			};
			
	        //running timer task as daemon thread
	        timer = new Timer(true);
	        //Toutes les 90 seconds
	        timer.scheduleAtFixedRate(timerTask, 0, (90*1000));
		}
		
	}
	
	/**
	 * Stop the monitoring every 90secs
	 */
	public void stopCDMonitoring() {
		timer.cancel();
		System.out.println("Stoping scann");
	}
	
	/**
	 * Make CD structure, download ZIP from Orthanc at study level and make CD process creation
	 * @param newStableStudyID
	 */
	public void makeCD(List<String> newStableStudyID) {
		for (int i=0; i<newStableStudyID.size(); i++) {
			ConvertZipAction zipDownloader=new ConvertZipAction(connexion);
			Path file;
			try {
				datenow=new Date();
				file = Files.createTempFile("CD_"+dateFormat.format(datenow) , ".zip");
				file.toFile().deleteOnExit();
				zipDownloader.setConvertZipAction(file.toString(), newStableStudyID.get(i), true);
				//generate ZIP of DICOMs
				zipDownloader.generateZip(true);
				File zip=zipDownloader.getGeneratedZipFile();
				// Recuperation des donn�es patients
				JSONParser parser=new JSONParser();
				JSONObject response=(JSONObject) parser.parse(connexion.makeGetConnectionAndStringBuilder("/studies/"+ newStableStudyID.get(i)).toString());			JSONObject mainPatientTag=(JSONObject) response.get("PatientMainDicomTags");
				String nom=(String) mainPatientTag.get("PatientName");
				String id=(String) mainPatientTag.get("PatientID");
				JSONObject mainDicomTag=(JSONObject) response.get("MainDicomTags");
				String date=(String) mainDicomTag.get("StudyDate");
				String studyDescription=(String) mainDicomTag.get("StudyDescription");
				if (studyDescription==null) studyDescription="N/A";
				// Unzip du fichier ZIP recupere
				unzip(zip);
				//Generation du Dat
				File dat = printDat(nom, id, date, studyDescription);
				//On efface tout a la sortie JVM
				recursiveDeleteOnExit(folder);
				//Efface le zip dezipe
				zip.delete();
				//Get size of viewer and images to determine if CD or DVD to Burn
				Long imageSize=FileUtils.sizeOfDirectory(folder.toFile());
				Long ViewerSize=FileUtils.sizeOfDirectory(new File(fijiDirectory));
				String discType;
				if (suportType.equals("Auto")) {
					//If size over 630 Mo
					if(Long.sum(imageSize,ViewerSize) > 630000000) {
						discType="DVD";
					}
					//else CD
					else {
						discType="CD";
					}
				}
				//If fixed by user get value from registery
				else {
					discType=suportType;
				}
				
				// Creation du Cd
				createCdBurner(nom, id, date, studyDescription, dat, discType);
				//On efface la study de Orthanc
				if (deleteStudies) connexion.makeDeleteConnection("/studies/"+newStableStudyID.get(i));
			} catch (IOException | org.json.simple.parser.ParseException | ParseException e) {
				e.printStackTrace();
			}
			
		}
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
	    	textArea.append("Unzipping ");
	    	
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
	private void createCdBurner(String nom, String id, String date, String studyDescription, File dat, String discType){
		
		//REalisation du texte pour le Robot
		String txtRobot= "# Making data CD\n"
				//Peut definir le Job ID et le mettre le compteur dans registery si besoin de tracer les operation avec fichier STF
				+ "#nombre de copies\n"
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
				PrintWriter pw = null;
				try {
					pw = new PrintWriter(f);
					pw.write(txtRobot);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					pw.close();
				}
				
		textArea.append("Request Sent , Patient name "+nom+" id "+id+" date "+date+" study "+studyDescription+"\n");
	}
	
	//Creer le fichier DAT pour injecter NOM, Date, Modalite
	private File printDat(String nom, String id, String date, String studyDescription) throws ParseException {
		
       SimpleDateFormat parser = new SimpleDateFormat("yyyyMMdd");
       Date dateExamen = parser.parse(date);
       //dateExamen=DateUtils.truncate(dateExamen, Calendar.DAY_OF_MONTH);
       SimpleDateFormat formatter = new SimpleDateFormat(dateFormatChoix);
       String formattedDate = formatter.format(dateExamen);
       
       //On parse le nom pour enlever les ^ et passer le prenom en minuscule
       int separationNomPrenom=nom.indexOf("^", 0);
       if (separationNomPrenom!=-1) {
       	nom=nom.substring(0, separationNomPrenom+2)+nom.substring(separationNomPrenom+2).toLowerCase();
       }
       
		String datFile = "patientName="+nom.replaceAll("\\^", " ")+"\n"
					+ "patientId=" + id +"\n"
					+ "patientDate="+ formattedDate + "\n"
					+ "studyDescription="+ studyDescription+"\n";
		
		
		File dat = new File(folder + File.separator + "CD"+dateFormat.format(datenow)+".dat");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(dat);
			pw.write(datFile);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}
		return dat;
	}
	
	/**
	 * Delete a path itself and all subdirectories
	 * @param path
	 * @throws IOException
	 */
	public static void recursiveDeleteOnExit(Path path) throws IOException {
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
	}
	
	public void setCDPreference() {
				//On prends les settings du registery
				Preferences jPrefer = Preferences.userNodeForPackage(Burner_Settings.class);
				jPrefer = jPrefer.node("CDburner");
				fijiDirectory=jPrefer.get("fijiDirectory", null);
				epsonDirectory=jPrefer.get("epsonDirectory", null);
				labelFile=jPrefer.get("labelFile", null);
				dateFormatChoix=jPrefer.get("DateFormat", null);
				deleteStudies=jPrefer.getBoolean("deleteStudies", false);
				suportType=jPrefer.get("suportType", "Auto");
		
				
	}
}
