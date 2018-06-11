package org.petctviewer.orthanc.run;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.petctviewer.orthanc.monitoring.CD_Burner;

public class Run_Orthanc {
	
	 private Process process;
	 private Path file;
	 private Thread orthancThread;
	 private boolean isStarted;
	
	public Run_Orthanc() {
	}
	
	public String start() throws Exception {
		String resourceName="Orthanc_Standalone/Orthanc-1.3.2-Release.exe";
		String resourceNameJSON="Orthanc_Standalone/Orthanc.json";
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		file = Files.createTempDirectory("Orthanc_"+dateFormat.format(new Date()));
		File FileExe=new File(file.toString()+File.separator+"Orthanc-1.3.2-Release.exe");
		File FileJSON=new File(file.toString()+File.separator+"Orthanc.json");
		
		InputStream in = ClassLoader.getSystemResourceAsStream(resourceName);
		InputStream inJson = ClassLoader.getSystemResourceAsStream(resourceNameJSON);
		System.out.println(inJson);
		OutputStream out = new FileOutputStream(FileExe);
		IOUtils.copy(in, out);
		OutputStream outJson = new FileOutputStream(FileJSON);
		IOUtils.copy(inJson, outJson);
		out.close();
		outJson.close();
		
	    startOrthanc(FileExe.getAbsolutePath().toString(), FileJSON.getAbsolutePath().toString());

        return resourceName;
        
  
    }
	
	public void startOrthanc(String exe, String json) {
        orthancThread=new Thread(new Runnable() {

			public void run() {
				  ProcessBuilder pb = new ProcessBuilder(exe, json);
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
	
	public void stopOrthanc() {
		System.out.println("Stoping Orthanc");
		process.destroy();
		orthancThread.interrupt();;
		try {
			//On Attend sortie d'Orthanc pour liberer le JSON
			Thread.sleep(2000);
			CD_Burner.recursiveDeleteOnExit(file);
			file.toFile().deleteOnExit();
			 isStarted=false;
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public boolean getIsStarted() {
		return this.isStarted;
	}

}
