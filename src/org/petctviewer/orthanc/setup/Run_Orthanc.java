package org.petctviewer.orthanc.setup;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.io.IOUtils;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.monitoring.CD_Burner;

public class Run_Orthanc {
	
	 private Preferences jprefer =VueAnon.jprefer;
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
	
	public Run_Orthanc() {
		//SK AJOUTER ORTHANC TRANSFERS
		if(System.getProperty("os.name").toLowerCase().startsWith("win")) {
			resourceName="Orthanc_Standalone/Orthanc-1.5.6-Release_32.exe";
			fileExecName="Orthanc-1.5.6-Release_32.exe";
			resourceLibName.add("OrthancWebViewer.dll");
			resourceLibName.add("OrthancTransfers.dll");
		//Still to Update	
		} else if (System.getProperty("os.name").toLowerCase().startsWith("mac")){
			resourceName="Orthanc_Standalone/Orthanc-1.5.1-ReleaseMac";
			fileExecName="Orthanc-1.5.1-ReleaseMac";
			resourceLibName.add("libOsimisWebViewer.dylib");
		} else if (System.getProperty("os.name").toLowerCase().startsWith("linux")){
			resourceName="Orthanc_Standalone/Orthanc-1.5.6-ReleaseLinux";
			fileExecName="Orthanc-1.5.1-ReleaseLinux";
			resourceLibName.add("libOrthancWebViewer.so");
			resourceLibName.add("libOrthancTransfers.so");
		}
	}
	
	/**
	 * Copy compiled ressources (orthanc+plugin+create storage path) to destination (temp file or user defined path) 
	 * @param installPath
	 * @return
	 * @throws Exception
	 */
	public void copyOrthanc(String installPath) throws Exception {
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
		
		//Add libs to orthanc (orthanc viewer to get GDCM decoder)
		for (int i=0; i<resourceLibName.size(); i++) {
			File FileLib=new File(file.toString()+File.separator+resourceLibName.get(i));
			InputStream inLib = ClassLoader.getSystemResourceAsStream(resourceLibPath+resourceLibName.get(i));
			OutputStream outLib = new FileOutputStream(FileLib);
			IOUtils.copy(inLib, outLib);
			outLib.close();
		}
		orthancExe=FileExe;
		orthancJson=FileJSON;
  
    }
	
	/**
	 * Start orthanc locally
	 */
	public void startOrthanc() {
		
        orthancThread=new Thread(new Runnable() {
        	JFrame splashScreen;
        	JLabel openStatus;
			public void run() {
					showSplashScreen(true);
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
				      //InputStream stderr = process.getErrorStream();
				      //OutputStream stdin = process.getOutputStream(); 
					  BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
					  //BufferedReader error = new BufferedReader (new InputStreamReader(stderr));
					  //BufferedReader stinn = new BufferedReader (new OutputStreamReader(stdin));
					  String line = null;
			
					  while ( (line = reader.readLine()) != null ) {
							 	//If JSON Object parse it
							 System.out.println(line);
							 if (line.contains("Orthanc has started")) {
								 isStarted=true;
								 showSplashScreen(false);
							 }
						 }
					

				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}	
			} 	
			
			public void showSplashScreen(boolean show) {
				if (show) {
					splashScreen= new JFrame();
					JPanel mainPanel=new JPanel(new BorderLayout());
					JLabel imageLabel=new JLabel();
					Image image = new ImageIcon(ClassLoader.getSystemResource("logos/orthanc-logo.png")).getImage();
					ImageIcon incon=new ImageIcon(image);
					imageLabel.setIcon(incon);
					JLabel openStatus=new JLabel("Starting Orthanc");
					openStatus.setFont(new Font("TimesRoman", Font.PLAIN, 30));
					openStatus.setHorizontalAlignment(JLabel.CENTER);
					
					mainPanel.add(imageLabel, BorderLayout.CENTER);
					mainPanel.add(openStatus, BorderLayout.SOUTH);
					
					splashScreen.add(mainPanel);
					splashScreen.pack();
					splashScreen.setLocationRelativeTo(null);
					splashScreen.setVisible(true);
					
				}else {
					splashScreen.dispose();
				}
				
			}
        });
        
       orthancThread.start();
       
        
       int loop=0;
       while(!isStarted && loop<10) {
    	   try {
    		   System.out.println(loop);
    		  
    		   Thread.sleep(1000);
    		   loop++;
    	   } catch (InterruptedException e) {
    		   e.printStackTrace();
    	   }
       }
       
       if(loop==10) {
    	   System.exit(0);
       }
       

	}
	
	public boolean isCopyAvailable() {
		
		if (!jprefer.get("OrthancLocalPath", "None").equals("None")) {
			String installPath=jprefer.get("OrthancLocalPath", "None");
			orthancExe=new File(installPath+File.separator+fileExecName);
			orthancJson=new File(installPath+File.separator+"Orthanc.json");
			if(orthancExe.exists() && orthancJson.exists()) {
				return true;
			}
				
		}
			
		return false;
	}
	
	public void stopOrthanc(OrthancRestApis connexionHttp) {
		
		if(connexionHttp==null || !connexionHttp.isConnected()) {
			return;
		}
		System.out.println("Stoping Orthanc");
		//Destroy the process
		try {
			//Ask Orthanc to shutdown
			connexionHttp.makePostConnection("/tools/shutdown", "");
			while (process.isAlive()) {
				Thread.sleep(1000);
			}
			process.destroy();
			orthancThread.interrupt();
			isStarted=false;
		} catch (Exception e) {
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
