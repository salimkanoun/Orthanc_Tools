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

package org.petctviewer.orthanc.anonymize;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.petctviewer.orthanc.ParametreConnexionHttp;


public class ConvertZipAction{
	DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HHmmss");
	private StringBuilder ids;
	private ArrayList<String> zipContent;
	private String setupPath;
	private File f;
	private ParametreConnexionHttp connexion;
	private boolean temporary;
	
/**
 * Genere export d une liste d examen vers un ZIP	
 * @param connexion
 */
public ConvertZipAction(ParametreConnexionHttp connexion){
		//import de l'objet connexion http
		this.connexion=connexion;
	}
	
	public void setConvertZipAction(String file, ArrayList<String> zipContent, boolean temporary){
		this.setupPath = file;
		this.zipContent = zipContent;
		this.temporary = temporary;
	}
	
	public void setConvertZipAction(String file, String zipContent, boolean temporary){
		this.setupPath = file;
		this.zipContent.clear(); 
		this.zipContent.add(zipContent);
		this.temporary = temporary;
	}

	public void generateZip(boolean dicomDir) throws IOException {
		// storing the IDs in a stringbuilder
		this.ids = new StringBuilder();
		this.ids.append("[");
		for(int i = 0; i < zipContent.size(); i++){
			this.ids.append("\"" + zipContent.get(i) + "\",");
		}
		ids.replace(ids.length()-1, ids.length(), "]");

		// the absence of a setupPath or not, will define whether or not a jfilechooser will be used
		if(temporary){
			f = File.createTempFile(setupPath + File.separator + df.format(new Date()), ".zip");
			f.deleteOnExit();
		}else{
			f = new File(setupPath);
		}
		
		if(!zipContent.isEmpty()){
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				//On defini l'adresse de l'API
				String url = null;
				// SK DANS NOUVELLE VERSION D ORTHANC CHANGER A NOUVELLE API AVEC LES SERIES DESCRIPTION
				if (!dicomDir) url="/tools/create-archive" ; else url="/tools/create-media?extended";
				HttpURLConnection conn=connexion.makePostConnection(url, ids.toString());
				
				is = conn.getInputStream();
				fos = new FileOutputStream(f);
				int bytesRead = -1;
				byte[] buffer = new byte[1024];
				while ((bytesRead = is.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
				}
				fos.close();
				is.close();
				conn.disconnect();
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
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
