/**
Copyright (C) 2017 VONGSALAT Anousone & KANOUN Salim

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

package org.petctviewer.orthanc.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.github.stephenc.javaisotools.eltorito.impl.ElToritoConfig;
import com.github.stephenc.javaisotools.iso9660.ISO9660RootDirectory;
import com.github.stephenc.javaisotools.iso9660.impl.CreateISO;
import com.github.stephenc.javaisotools.iso9660.impl.ISO9660Config;
import com.github.stephenc.javaisotools.iso9660.impl.ISOImageFileHandler;
import com.github.stephenc.javaisotools.joliet.impl.JolietConfig;
import com.github.stephenc.javaisotools.rockridge.impl.RockRidgeConfig;
import com.github.stephenc.javaisotools.sabre.StreamHandler;

public class ZipAndViewer {
	
	private File zipDicom;
	private File viewerPackage;
	private File destination;
	
	public ZipAndViewer(File zipDicom, File destination, File viewerPackage) throws IOException {
		this.zipDicom=zipDicom;
		this.destination=destination;
		this.viewerPackage=viewerPackage;
		
	}

	/**
	 * Merge zip to a same ZIP destination (destination file)
	 * @throws Exception 
	 */
	public void ZipAndViewerToZip() throws Exception {
		destination.createNewFile();
		ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(destination));
		readZip(outStream , viewerPackage.toString());
		readZip(outStream , zipDicom.toString());
		outStream.close();
		
	}
	
	/**
	 * Unzip a zip to a destination
	 * @param zip
	 * @param tempFolder
	 */
	private void unzip(File zip, Path tempFolder){
		 try {
		     byte[] buffer = new byte[1024];
	    
	    	//get the zip file content
	    	ZipInputStream zis;
			zis = new ZipInputStream(new FileInputStream(zip));
			
	    	//get the zipped file list entry
	    	ZipEntry ze = zis.getNextEntry();
	    	
	    	while(ze!=null){
	     	   	String fileName = ze.getName();
	     	    
	            File newFile = new File(tempFolder + File.separator + fileName);
	            
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
	    	recursiveDeleteOnExit(tempFolder);
	     } catch (IOException e) {
				e.printStackTrace();
			}
	     
	     
	}
	
	/**
	 * put ZIP content to outputStream (to concatenate two zip to the same output stream)
	 * @param outStream
	 * @param inputZip
	 * @throws Exception
	 */
	private void readZip(ZipOutputStream outStream, String inputZip) throws Exception {
	    ZipInputStream inStream = new ZipInputStream(new FileInputStream(inputZip));
	    byte[] buffer = new byte[1024];
	    int len = 0;
	    for (ZipEntry e; (e = inStream.getNextEntry()) != null;) {
	        ZipEntry destEntry =  new ZipEntry (e.getName());
	    	outStream.putNextEntry(destEntry);
	        while ((len = inStream.read(buffer)) > 0) {
	            outStream.write(buffer, 0, len);
	        }
	    }
	    inStream.close();
	}

	/**
	 * Generate the ISO file (in destination file) containing DICOM and Viewer
	 * @throws Exception 
	 */
	public void generateIsoFile() throws Exception {
		//Create Temp folder and unzip both Viewer and Dicom in it
		Path tempFolder = null;
		
		tempFolder = Files.createTempDirectory("ISO_");

		unzip(zipDicom, tempFolder);
		unzip(viewerPackage, tempFolder);
		
		//Build ISO Image in destination
		// from https://github.com/stephenc/java-iso-tools/blob/master/iso9660-writer/ISOtest.java
		
		// Directory hierarchy, starting from the root
		ISO9660RootDirectory.MOVED_DIRECTORIES_STORE_NAME = "rr_moved";
		ISO9660RootDirectory root = new ISO9660RootDirectory();
		
		root.addContentsRecursively(tempFolder.toFile());
				
		// ISO9660 support
		ISO9660Config iso9660Config = new ISO9660Config();
		iso9660Config.allowASCII(false);
		iso9660Config.setInterchangeLevel(1);
		iso9660Config.restrictDirDepthTo8(true);
		iso9660Config.setPublisher("petctviewer.org");
		iso9660Config.setVolumeID("Patient Images");
		iso9660Config.setDataPreparer("Salim Kanoun");
		//A mettre
		//iso9660Config.setCopyrightFile(new File("Copyright.txt"));
		iso9660Config.forceDotDelimiter(true);

		RockRidgeConfig rrConfig = null;
		// Rock Ridge support
		rrConfig = new RockRidgeConfig();
		rrConfig.setMkisofsCompatibility(false);
		rrConfig.hideMovedDirectoriesStore(true);
		rrConfig.forcePortableFilenameCharacterSet(true);
		

		JolietConfig jolietConfig = null;
		// Joliet support
		jolietConfig = new JolietConfig();
		jolietConfig.setPublisher("petctviewer.org");
		jolietConfig.setVolumeID("Patient Images");
		jolietConfig.setDataPreparer("Salim Kanoun");
		// A mettre
		//jolietConfig.setCopyrightFile(new File("Copyright.txt"));
		jolietConfig.forceDotDelimiter(true);
		
		//Pas besoin de secteur bootable
		ElToritoConfig elToritoConfig = null;

		// Create ISO
		StreamHandler streamHandler = new ISOImageFileHandler(destination);
		CreateISO iso = new CreateISO(streamHandler, root);
		iso.process(iso9660Config, rrConfig, jolietConfig, elToritoConfig);
		
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
