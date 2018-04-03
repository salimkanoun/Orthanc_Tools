package org.petctviewer.orthanc.monitoring;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import javax.swing.JTextArea;

public class CD_Burner {
	
	protected static String arriveRep;
	protected static String dateFormatChoix;
	protected static String labelFile;
	protected static String epsonDirectory;
	protected static String fijiDirectory;
	private WatchService watcher;
	private JTextArea textArea;
	private Path folder;
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private Date datenow;
	
	

	public void unzip(File zipFile, String nomId){
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
	    	//On efface le ZIP et le repertoier qu'on a extrait
	    	FileUtils.deleteDirectory(zipFile.getParentFile());
	    	textArea.append("original file deleted \n");
	    
	     } catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	
	// Creation du JDF Pour le gaveur
	public void createCdBurner(String nom, String id, String date, String studyDescription, String nomId, File dat){
		
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
	public File printDat(String nom, String id, String date, String studyDescription, String nomId) throws ParseException {
		
		//On parse la date pour avoir le format francais
		//SK A AJOUTER DANS LES SETTINGS
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
	
	/*public void watchFolder() throws IOException, InterruptedException, ParseException{
		//On set le watcher
		watcher = FileSystems.getDefault().newWatchService();
		//On defini le repertoire du watch
		Path dir = Paths.get(arriveRep);
		dir.register(watcher, java.nio.file.StandardWatchEventKinds.ENTRY_CREATE);
		//Initialize la variable qui contiendra le ZIP a traiter et les donnes patients
		
		File zipFile = null;
		String nom=null;
		String id=null;
		String date=null;
		String study=null;
		String nomId=null;
		
		while (true) {
		    WatchKey key;
		    try {
		        // wait for a key to be available
		        key = watcher.take();
		    } catch (InterruptedException ex) {
		        return;
		    }
		 
		    for (WatchEvent<?> event : key.pollEvents()) {
		        // get event type
		        WatchEvent.Kind<?> kind = event.kind();

		        //Overflow si la JVM n'a pas pu suivre
		        if (kind == java.nio.file.StandardWatchEventKinds.OVERFLOW) {
		            continue;
		        //Si on detecte une creation
		        } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_CREATE) {
		        	//On attends 2 secondes pour verifier que le repertoire a ete cree
		        	Thread.sleep(2000);
		        	//On recupere l'ID et le nom du patient
		        	File[] contenuRep=dir.toAbsolutePath().toFile().listFiles();
		        	if (contenuRep.length==1 && contenuRep[0].isDirectory()){
		        		nomId=contenuRep[0].toString();
		        		int finRacine=nomId.lastIndexOf("\\");
		        		nomId=nomId.substring(finRacine+1, nomId.length());
		        		int delemiteuridNom=nomId.indexOf(" --- ");
		        		int delimiteurPatientDate=nomId.indexOf(" --- ", delemiteuridNom+1);
		        		int delemiteurPatientStudy=nomId.indexOf(" --- ", delimiteurPatientDate+1);
		        		//System.out.println(nomId+delemiteuridNom+delimiteurPatientDate+delemiteurPatientStudy);
		        		if (delemiteuridNom!=(-1) && delimiteurPatientDate!=(-1) &&delemiteurPatientStudy!=(-1)) {
		        			id=nomId.substring(0,delemiteuridNom-1);
		        			nom=nomId.substring(delemiteuridNom+5, delimiteurPatientDate);
		        			date=nomId.substring(delimiteurPatientDate+5, delemiteurPatientStudy);
		        			study=nomId.substring(delemiteurPatientStudy+5,nomId.length());
		        			textArea.append("Recieved Patient "+nom+ " Date "+ "ID "+id+" Study "+study+"\n");
		        		}
		        	//On descend d'un niveau pour recuperer le zip
		        	File[] repzip=contenuRep[0].listFiles();
		        	
		        	if (repzip.length==1 && repzip[0].isFile()) {
		        		//System.out.println("un seul fichier tout va bien");
		        		//On verifie que l'ecriture du fichier est terminee boucle d' attente
		        		boolean isFileUnlocked = false;
		        		do {
		        			try {
		        			org.apache.commons.io.FileUtils.touch(repzip[0]);
		        		    isFileUnlocked = true;
		        			} catch (IOException e) {
		        				isFileUnlocked = false;
		        				//On attends 1 sec avant de retester
		        				Thread.sleep(1000);
		        			}
		        		
		        		} while (isFileUnlocked==false);
		        		//on lance l'unzip
		        		zipFile=new File(repzip[0].getAbsoluteFile().toString());
		        		}
		        		
		        	}
		        	//On prend la date du processing
		        	datenow = new Date();
		    		// On fait l'unzip
		        	unzip(zipFile, nomId);
		        	//On ecrit le DAT pour remplacer les champs nom, id, date et study
		        	File dat = printDat(nom, id, date, study, nomId);
		        	//On lance la requette au robot
		        	createCdBurner(nom, id, date, study, nomId, dat);
		        	//ajout des fichiers dans la liste de delete
		        	recursiveDeleteOnExit(folder);

		        } 
		       
		    }
		 
		    // IMPORTANT: The key must be reset after processed
		    boolean valid = key.reset();
		    if (!valid) {
		        break;
		    }
		}
		
	}*/
	
	//SK Remplace le WatchFolder
	//A Faire
	public void watchOrthancStableStudies() {
		Orthanc_Monitoring monitoring=new Orthanc_Monitoring(true);
		monitoring.makeMonitor();
		for (int i=0; i<monitoring.newStudyID.size(); i++) {
			boolean stable=monitoring.studyIsStable(monitoring.newStudyID.get(i));
			if (stable) {
				
			}
			//Necessite d'attendre qu'elle soit stable
			else {
				
			}
			
		}
		
	}
	
	
        
	public static void recursiveDeleteOnExit(Path path) throws IOException {
		  Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
		    @Override
		    public FileVisitResult visitFile(Path file,
		        @SuppressWarnings("unused") BasicFileAttributes attrs) {
		      file.toFile().deleteOnExit();
		      return FileVisitResult.CONTINUE;
		    }
		    @Override
		    public FileVisitResult preVisitDirectory(Path dir,
		        @SuppressWarnings("unused") BasicFileAttributes attrs) {
		      dir.toFile().deleteOnExit();
		      return FileVisitResult.CONTINUE;
		    }
		  });
		}
}
