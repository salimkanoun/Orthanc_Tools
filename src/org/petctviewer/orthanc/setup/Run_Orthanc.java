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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
	 private String resourceName;
	 private String fileExecName;
	 private String resourceLibPath, resourceLibName;
	 
	
	public Run_Orthanc() {
		System.out.println(System.getProperty("os.name"));
		if(System.getProperty("os.name").toLowerCase().startsWith("win")) {
			resourceName="Orthanc_Standalone/Orthanc-1.4.1-Release.exe";
			fileExecName="Orthanc-1.4.1-Release.exe";
			resourceLibPath="Orthanc_Standalone/OrthancWebViewer.dll";
			resourceLibName="OrthancWebViewer.dll";
			
		}
		else if (System.getProperty("os.name").toLowerCase().startsWith("mac")){
			resourceName="Orthanc_Standalone/Orthanc-1.4.1-ReleaseMac";
			fileExecName="Orthanc-1.4.1-ReleaseMac";
			resourceLibPath="Orthanc_Standalone/libOsimisWebViewer.dylib";
			resourceLibName="libOsimisWebViewer.dylib";
		}
		else if (System.getProperty("os.name").toLowerCase().startsWith("linux")){
			resourceName="Orthanc_Standalone/Orthanc-1.4.1-ReleaseLinux";
			fileExecName="Orthanc-1.4.1-ReleaseLinux";
			resourceLibPath="Orthanc_Standalone/libOrthancWebViewer.so";
			resourceLibName="libOrthancWebViewer.so";
			
			
		}
	}
	
	public String copyOrthanc(String installPath) throws Exception {
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
		
		File FileExe=new File(file.toString()+File.separator+fileExecName);
		File FileJSON=new File(file.toString()+File.separator+"Orthanc.json");
		
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
		new File(file.toString()+File.separator+"lib").mkdirs();
		File FileLib=new File(file.toString()+File.separator+"lib"+File.separator+resourceLibName);
		InputStream inLib = ClassLoader.getSystemResourceAsStream(resourceLibPath);
		OutputStream outLib = new FileOutputStream(FileLib);
		IOUtils.copy(inLib, outLib);
		outLib.close();

		
		
		
	
		
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
				  pb.redirectErrorStream(true); 
				
				  try {
				  process = pb.start();
				  Thread.sleep(1000);
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
							 System.out.println("confirmation"); ;
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
			startOrthanc();
			return true;
		}
		else return false;
	}
	
	public void stopOrthanc() {
		System.out.println("Stoping Orthanc");
		process.destroy();
		orthancThread.interrupt();
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
