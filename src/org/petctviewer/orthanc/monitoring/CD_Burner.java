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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.petctviewer.orthanc.ParametreConnexionHttp;
import org.petctviewer.orthanc.anonymize.ConvertZipAction;

import javax.swing.JTextArea;

public class CD_Burner {
	
	protected static String dateFormatChoix;
	protected static String labelFile;
	protected static String epsonDirectory;
	protected static String fijiDirectory;
	private JTextArea textArea;
	private Path folder;
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private Date datenow;
	private ParametreConnexionHttp connexion;
	private Timer timer;
	
	public CD_Burner (ParametreConnexionHttp connexion, JTextArea textArea) {
		this.connexion=connexion;
		this.textArea=textArea;
	}

	
	/**
	 * Start Monitoring of Orthanc Change API every 90secs
	 */
	public void watchOrthancStableStudies() {
		
		Orthanc_Monitoring monitoring=new Orthanc_Monitoring(connexion);
		//Met la derniere ligne pour commencer le monitoring
		int last=monitoring.getChangeLastLine();
		monitoring.setChangeLastLine(last);
		
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				System.out.println("starting scann");
				monitoring.makeMonitor();
				makeCD(monitoring.newStableStudyID);
				monitoring.newStableStudyID.clear();
				
			}
			
		};
		
        //running timer task as daemon thread
        timer = new Timer(true);
        //Toutes les 90 seconds
        timer.scheduleAtFixedRate(timerTask, 0, (90*1000));
               
 

		
	}
	
	/**
	 * Stop the monitoring every 90secs
	 */
	public void stopMonitoring() {
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
				//SK METHODE A MODIFIER POUR AVOIR LES SERIES NAME
				zipDownloader.generateZip(true);
				File zip=zipDownloader.getGeneratedZipFile();
				// Recuperation des données patients
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
				// Creation du Cd
				createCdBurner(nom, id, date, studyDescription, dat);
				//On efface la study de Orthanc
				connexion.makeDeleteConnection("/studies/"+newStableStudyID.get(i));
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
	private void createCdBurner(String nom, String id, String date, String studyDescription, File dat){
		
		//REalisation du texte pour le Robot
		String txtRobot= "# Making data CD\n"
				//Peut definir le Job ID et le mettre le compteur dans registery si besoin de tracer les operation avec fichier STF
				+ "#nombre de copies\n"
				+ "COPIES=1\n"
				+ "#CD ou DVD\n"
				+ "DISC_TYPE=DVD\n"
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
       SimpleDateFormat formatter = new SimpleDateFormat(CD_Burner.dateFormatChoix);
       String formattedDate = formatter.format(dateExamen);
       
       //On parse le nom pour enlever les _ et passer le prenom en minuscule
       int separationNomPrenom=nom.indexOf("_", 0);
       if (separationNomPrenom!=-1) {
       	nom=nom.substring(0, separationNomPrenom+2)+nom.substring(separationNomPrenom+2).toLowerCase();
       }
       
       
		String datFile = "patientName="+nom.replaceAll("_", " ")+"\n"
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
	
	private static void recursiveDeleteOnExit(Path path) throws IOException {
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
}
