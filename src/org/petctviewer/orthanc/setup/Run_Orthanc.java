package org.petctviewer.orthanc.setup;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

import org.apache.commons.io.IOUtils;
import org.petctviewer.orthanc.monitoring.CD_Burner;

public class Run_Orthanc {
	
	 private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");
	 private Process process;
	 private Path file;
	 private Thread orthancThread;
	 private boolean isStarted;
	 private File orthancExe;
	 private File orthancJson;
	
	public Run_Orthanc() {

		
	}
	
	public String copyOrthanc(String installPath) throws Exception {
		String resourceName="Orthanc_Standalone/Orthanc-1.3.2-Release.exe";
		String resourceNameJSON="Orthanc_Standalone/Orthanc.json";
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//Si pas de destination on met dans le temp directory
		if (installPath ==null) {
			file = Files.createTempDirectory("Orthanc_"+dateFormat.format(new Date()));
			CD_Burner.recursiveDeleteOnExit(file);
			file.toFile().deleteOnExit();
		}
		//Si destination choisie on enregistre le repertoire
		else file=Paths.get(installPath);
		File FileExe=new File(file.toString()+File.separator+"Orthanc-1.3.2-Release.exe");
		File FileJSON=new File(file.toString()+File.separator+"Orthanc.json");
		
		InputStream in = ClassLoader.getSystemResourceAsStream(resourceName);
		InputStream inJson = ClassLoader.getSystemResourceAsStream(resourceNameJSON);
		OutputStream out = new FileOutputStream(FileExe);
		IOUtils.copy(in, out);
		OutputStream outJson = new FileOutputStream(FileJSON);
		IOUtils.copy(inJson, outJson);
		out.close();
		outJson.close();
		
		orthancExe=FileExe;
		orthancJson=FileJSON;
		
	    startOrthanc();

        return resourceName;
  
    }
	
	public void startOrthanc() {
        orthancThread=new Thread(new Runnable() {

			public void run() {
				  ProcessBuilder pb = new ProcessBuilder(orthancExe.getAbsolutePath().toString(),orthancJson.getAbsolutePath().toString());
				  pb.redirectErrorStream(true); 
				
				  try {
				  process = pb.start();
				  InputStream stdout = process.getInputStream(); 
			      InputStream stderr = process.getErrorStream();
			      //OutputStream stdin = process.getOutputStream(); 
				  BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
				  BufferedReader error = new BufferedReader (new InputStreamReader(stderr));
				  //BufferedReader stinn = new BufferedReader (new OutputStreamReader(stdin));
				  String line = null;
		
				  while ( (line = reader.readLine()) != null) {
						 	//If JSON Object parse it
						 System.out.println(line);	 	
					 }
					
				  while ( (line = error.readLine()) != null) {
					 	//If JSON Object parse it
					 System.out.println(line);				 	
				    }
					

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
        	
        });
		
        orthancThread.start();
        isStarted=true;

		
		
		

	}
	
	public boolean isCopyAvailable() {
		
		if (!jprefer.get("OrthancLocalPath", "None").equals("None")) {
			String installPath=jprefer.get("OrthancLocalPath", "None");
			orthancExe=new File(installPath+File.separator+"Orthanc-1.3.2-Release.exe");
			orthancJson=new File(installPath+File.separator+"Orthanc.json");
			startOrthanc();
			return true;
		}
		else return false;
	}
	
	public void stopOrthanc() {
		System.out.println("Stoping Orthanc");
		process.destroy();
		orthancThread.interrupt();;
		try {
			//On Attend sortie d'Orthanc pour liberer le JSON
			Thread.sleep(2000);
			 isStarted=false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public boolean getIsStarted() {
		return this.isStarted;
	}

}
