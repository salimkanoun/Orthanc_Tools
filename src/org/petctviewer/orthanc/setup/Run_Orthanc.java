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
import java.nio.file.attribute.PosixFilePermission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import org.apache.commons.io.IOUtils;
import org.petctviewer.orthanc.ParametreConnexionHttp;
import org.petctviewer.orthanc.monitoring.CD_Burner;

public class Run_Orthanc {
	
	 private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");
	 private Process process;
	 private Path file;
	 private Thread orthancThread;
	 private boolean isStarted;
	 private File orthancExe;
	 private File orthancJson;
	 private String resourceName;
	 private String fileExecName;
	 public String orthancJsonName="Orthanc.json";
	 private String resourceLibPath="Orthanc_Standalone/";
	 private List<String> resourceLibName=new ArrayList<String>();
	 private boolean temp;
	 private ParametreConnexionHttp connexionHttp;
	
	public Run_Orthanc(ParametreConnexionHttp connexionHttp) {
		this.connexionHttp=connexionHttp;
		
		//SK AJOUTER ORTHANC TRANSFERS
		//LIBRAIRIE A COMPILER POUR WINDOWS ET MAC
		
		if(System.getProperty("os.name").toLowerCase().startsWith("win")) {
			if (System.getProperty("os.arch").contains("86")){
				resourceName="Orthanc_Standalone/Orthanc-1.5.1-Release_32.exe";
				fileExecName="Orthanc-1.5.1-Release_32.exe";
				resourceLibName.add("OrthancWebViewer-2.4_32.dll");
				
			}else {
				resourceName="Orthanc_Standalone/Orthanc-1.5.1-Release.exe";
				fileExecName="Orthanc-1.5.1-Release.exe";
				resourceLibName.add("OrthancWebViewer.dll");
			}
			
			
		}
		else if (System.getProperty("os.name").toLowerCase().startsWith("mac")){
			resourceName="Orthanc_Standalone/Orthanc-1.5.1-ReleaseMac";
			fileExecName="Orthanc-1.5.1-ReleaseMac";
			resourceLibName.add("libOsimisWebViewer.dylib");
			
		}
		else if (System.getProperty("os.name").toLowerCase().startsWith("linux")){
			resourceName="Orthanc_Standalone/Orthanc-1.5.1-ReleaseLinux";
			fileExecName="Orthanc-1.5.1-ReleaseLinux";
			resourceLibName.add("libOrthancWebViewer.so");
			resourceLibName.add("libOrthancTransfers.so");
			
			
			
		}
	}
	
	public String copyOrthanc(String installPath) throws Exception {
		String resourceNameJSON=resourceLibPath+orthancJsonName;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//Si pas de destination on met dans le temp directory
		if (installPath ==null) {
			temp=true;
			file = Files.createTempDirectory("Orthanc_"+dateFormat.format(new Date()));
		}
		//Si destination choisie on enregistre le repertoire
		else file=Paths.get(installPath);
		
		File FileExe=new File(file.toString()+File.separator+fileExecName);
		File FileJSON=new File(file.toString()+File.separator+orthancJsonName);
		
		InputStream in = ClassLoader.getSystemResourceAsStream(resourceName);
		InputStream inJson = ClassLoader.getSystemResourceAsStream(resourceNameJSON);
		
		OutputStream out = new FileOutputStream(FileExe);
		IOUtils.copy(in, out);
		out.close();
		OutputStream outJson = new FileOutputStream(FileJSON);
		IOUtils.copy(inJson, outJson);
		outJson.close();
		//Create OrthancStorageDirectory
		new File(file.toString()+File.separator+"OrthancStorage").mkdirs();
		
		//Add lib to get GDCM decoder
		for (int i=0; i<resourceLibName.size(); i++) {
			File FileLib=new File(file.toString()+File.separator+resourceLibName.get(i));
			InputStream inLib = ClassLoader.getSystemResourceAsStream(resourceLibPath+resourceLibName.get(i));
			OutputStream outLib = new FileOutputStream(FileLib);
			IOUtils.copy(inLib, outLib);
			outLib.close();
		}

		
		orthancExe=FileExe;
		orthancJson=FileJSON;
		
	    startOrthanc();
	    
	    

        return resourceName;
  
    }
	
	public void startOrthanc() {
        orthancThread=new Thread(new Runnable() {

			public void run() {
				
				 	if ( ! System.getProperty("os.name").toLowerCase().startsWith("win")) {
				 		Set<PosixFilePermission> perms = new HashSet<>();
				 		 	perms.add(PosixFilePermission.OWNER_READ);
						    perms.add(PosixFilePermission.OWNER_WRITE);
						    perms.add(PosixFilePermission.OWNER_EXECUTE);

						    perms.add(PosixFilePermission.OTHERS_READ);
						    perms.add(PosixFilePermission.OTHERS_WRITE);
						    perms.add(PosixFilePermission.OTHERS_EXECUTE);

						    perms.add(PosixFilePermission.GROUP_READ);
						    perms.add(PosixFilePermission.GROUP_WRITE);
						    perms.add(PosixFilePermission.GROUP_EXECUTE);

						    try {
								Files.setPosixFilePermissions(orthancExe.toPath(), perms);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
				 	}
				   
				    
				  ProcessBuilder pb = new ProcessBuilder(orthancExe.getAbsolutePath().toString(),orthancJson.getAbsolutePath().toString());
				  pb.directory(orthancExe.getParentFile());
				  pb.redirectErrorStream(true); 
				
				  try {
				  process = pb.start();
				  Thread.sleep(2000);
				  InputStream stdout = process.getInputStream(); 
			      InputStream stderr = process.getErrorStream();
			      //OutputStream stdin = process.getOutputStream(); 
				  BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
				  BufferedReader error = new BufferedReader (new InputStreamReader(stderr));
				  //BufferedReader stinn = new BufferedReader (new OutputStreamReader(stdin));
				  String line = null;
		
				  while ( (line = reader.readLine()) != null ) {
						 	//If JSON Object parse it
						 System.out.println(line);
						 if (line.contains("Orthanc has started")) {
							 isStarted=true;
							 System.out.println("confirmation");
							//SK A REVOIR
							connexionHttp.testConnexion();
						 }
					 }
					
				  while ( (line = error.readLine()) != null) {
					 	//If JSON Object parse it
					 //System.out.println(line);				 	
				    }
					

				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
        	
        });
        
        orthancThread.start();

        
       if(!isStarted) {
    	   try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       }


		
		
		

	}
	
	public boolean isCopyAvailable() {
		
		if (!jprefer.get("OrthancLocalPath", "None").equals("None")) {
			String installPath=jprefer.get("OrthancLocalPath", "None");
			orthancExe=new File(installPath+File.separator+fileExecName);
			orthancJson=new File(installPath+File.separator+"Orthanc.json");
			if(orthancExe.exists() && orthancJson.exists()) {
				startOrthanc();
				return true;
			}
				
		}
			
		return false;
	}
	
	public void stopOrthanc() {
		System.out.println("Stoping Orthanc");
		//Ask Orthanc to shutdown
		connexionHttp.makePostConnection("/tools/shutdown", "");
		//Destroy the process
		try {
			while (process.isAlive()) {
				Thread.sleep(1000);
			}
			process.destroy();
			orthancThread.interrupt();
			isStarted=false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//If temp session make the directory in the delete list
		if (temp) {
			try {
				CD_Burner.recursiveDeleteOnExit(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		

	}
	
	public boolean getIsStarted() {
		return this.isStarted;
	}

}
