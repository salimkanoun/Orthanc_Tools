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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.petctviewer.orthanc.setup.ParametreConnexionHttp;

import com.google.gson.JsonArray;


public class ExportZip{
	
	private ArrayList<String> zipContent;
	private String zipDestination;
	private File f;
	private ParametreConnexionHttp connexion;
	private boolean temporary;
	
	/**
	 * Send list of Orthanc IDs and generate a zip file	
	 * @param connexion
	 */
	public ExportZip(ParametreConnexionHttp connexion){
		this.connexion=connexion;
	}
	
	public void setConvertZipAction(String fileDestination, ArrayList<String> zipContent, boolean temporary){
		this.zipDestination = fileDestination;
		this.zipContent = zipContent;
		this.temporary = temporary;
	}
	
	public void setConvertZipAction(String fileDestination, String zipContent, boolean temporary){
		this.zipDestination = fileDestination;
		this.zipContent = new ArrayList<String>(); 
		this.zipContent.add(zipContent);
		this.temporary = temporary;
	}

	public void generateZip(boolean dicomDir) throws IOException  {
		
		JsonArray idArray=new JsonArray();
		for(String id : zipContent) {
			idArray.add(id);
		}
		//If temporary file create a temp zip file, else use defined path for zip export destination
		if(temporary){
			DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HHmmss");
			f = File.createTempFile(zipDestination + File.separator + df.format(new Date()), ".zip");
			f.deleteOnExit();
		}else{
			f = new File(zipDestination);
		}
		
		if(!zipContent.isEmpty()){
			//URL API to define
			String url = null;
			// if hierachical structure else (dicomdir)
			if (!dicomDir) {
				url="/tools/create-archive" ;
			}else  {
				if (connexion.getIfVersionAfter131()) {
					//If new version of Orthanc extended api to get series name
					url="/tools/create-media-extended";
				}else {
					//old API without serie's name
					url="/tools/create-media";
				}					
			}
			HttpURLConnection conn=connexion.makePostConnection(url, idArray.toString());
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(f);
			int bytesRead = -1;
			byte[] buffer = new byte[1024];
			while ((bytesRead = is.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}
			fos.close();
			is.close();
			conn.disconnect();
			
		}

	}

	public String getGeneratedZipPath(){
		return this.f.getAbsolutePath();
	}

	public String getGeneratedZipName(){
		return this.f.getName();
	}
	
	public File getGeneratedZipFile(){
		return this.f;
	}
	


}
