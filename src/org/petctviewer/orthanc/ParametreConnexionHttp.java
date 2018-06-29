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

package org.petctviewer.orthanc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Permet de recuperer l'adresse du serveur Orthanc a partir des settings de BIdatabase ou des settings defini dans le settings panel des Orthanc Plugin
 * et permet de creer les connexion get et post
 * @author kanoun_s
 *
 */
public class ParametreConnexionHttp {
	
	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/biplugins");
	private Preferences jpreferPerso = Preferences.userRoot().node("<unnamed>/queryplugin");
	private String fullAddress;
	private String authentication;
	private String orthancVersion;
	private boolean versionHigher131;
	
	
	public ParametreConnexionHttp()  {
			int curDb = jprefer.getInt("current database", 0);
			int typeDb = jprefer.getInt("db type" + curDb, 0);
			String ip=null;
			String port=null;
			
			if(typeDb == 5){
				
				if(!jprefer.get("db path" + curDb, "none").equals("none") && !jprefer.get("db path" + curDb, "none").equals("")){
					String pathBrut = jprefer.get("db path" + curDb, "none") + "/";
					if (pathBrut.contains("http://")) {
						int index = ordinalIndexOf(pathBrut, "/", 3);
						this.fullAddress = pathBrut.substring(0, index);
					}
					else {
						this.fullAddress = "http://"+ pathBrut;
					}
					
				}
				else{
					//Si le path string non defini on utilise le port par defaut et l'adresse du champ AET
					String address = jprefer.get("ODBC" + curDb, "localhost");
					ip="http://";
					ip +=address.substring((address.indexOf("@")+1), address.indexOf(":") );
					ip +=":8042";
					this.fullAddress =ip;
				}
				
				if(jprefer.get("db user" + curDb, null) != null && jprefer.get("db pass" + curDb, null) != null){
					authentication = Base64.getEncoder().encodeToString((jprefer.get("db user" + curDb, null) + ":" + jprefer.get("db pass" + curDb, null)).getBytes());
				}
				
		}
		else if (typeDb != 5){
			ip = jpreferPerso.get("ip", "http://localhost");
			port = jpreferPerso.get("port", "8042");
			this.fullAddress = ip + ":" + port;
			if(jpreferPerso.get("username", null) != null && jpreferPerso.get("username", null) != null){
				authentication = Base64.getEncoder().encodeToString((jpreferPerso.get("username", null) + ":" + jpreferPerso.get("password", null)).getBytes());
			}
			
			
		}
	System.out.println(ip+port);
		
	}
	
	public HttpURLConnection makeGetConnection(String apiUrl) {
		
		HttpURLConnection conn=null;
		URL url = null;
			try {
				url = new URL(fullAddress+apiUrl);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("GET");
				if((fullAddress != null && fullAddress.contains("https"))){
						HttpsTrustModifier.Trust(conn);
				}
				if(authentication != null){
					conn.setRequestProperty("Authorization", "Basic " + authentication);
				}
				conn.getResponseMessage();
				
			} catch (IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		

		return conn;
	
	}

	public StringBuilder makeGetConnectionAndStringBuilder(String apiUrl) {
		HttpURLConnection conn = null;
		StringBuilder sb = new StringBuilder() ;
		try {
			conn = makeGetConnection(apiUrl);
			if (conn !=null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
				String output;
				while ((output = br.readLine()) != null) {
					sb.append(output);
				}
				conn.disconnect();
			}
			
			
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb;
	}
	
	
	
	public HttpURLConnection makePostConnection(String apiUrl, String post) {
		URL url = null;
		HttpURLConnection conn = null ;
		try {
			url = new URL(fullAddress+apiUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			if((fullAddress != null && fullAddress.contains("https")) ){
					HttpsTrustModifier.Trust(conn);
			}
			if(this.authentication != null){
				conn.setRequestProperty("Authorization", "Basic " + this.authentication);
			}
			OutputStream os = conn.getOutputStream();
			os.write(post.getBytes());
			os.flush();
			conn.getResponseMessage();
		} catch ( KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException ex) { 
			
		}
		return conn;
	}

	public HttpURLConnection sendDicom(String apiUrl, byte[] post) {
		
		URL url = null;
		HttpURLConnection conn =null;
		
		try {
			url=new URL(fullAddress+apiUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			if((fullAddress != null && fullAddress.contains("https")) ){
				HttpsTrustModifier.Trust(conn);
				
			}
			if(this.authentication != null){
				conn.setRequestProperty("Authorization", "Basic " + this.authentication);
			}
			conn.setRequestProperty("content-length", Integer.toString(post.length));
			conn.setRequestProperty("content-type", "application/dicom");
			OutputStream os = conn.getOutputStream();
			os.write(post);
			os.flush();
			conn.getResponseMessage();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return conn;
	}
		
	public StringBuilder makePostConnectionAndStringBuilder(String apiUrl, String post) {
		
		HttpURLConnection conn = makePostConnection(apiUrl, post);
		BufferedReader br;
		StringBuilder sb =new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			// We get the study ID at the end
			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			conn.disconnect();
			conn.getResponseMessage();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb; 
	}

	public void makeDeleteConnection(String apiUrl) {
		
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url=new URL(fullAddress+apiUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			if((fullAddress != null && fullAddress.contains("https")) ){		
					HttpsTrustModifier.Trust(conn);
			}
			if(this.authentication != null){
				conn.setRequestProperty("Authorization", "Basic " + this.authentication);
			}
			
			conn.getResponseMessage();
			conn.disconnect();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
	}
	
	private int ordinalIndexOf(String str, String substr, int n) {
		
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
			pos = str.indexOf(substr, pos + 1);
		return pos;
	}
	
	// Display Error message if connexion failed
	public Boolean testConnexion() {	
		Boolean test=true;
		try {
		makeGetConnection("/system");
		StringBuilder sb= makeGetConnectionAndStringBuilder("/system");
		JSONParser parser=new JSONParser();
		JSONObject systemJson=(JSONObject) parser.parse(sb.toString());
		orthancVersion=(String) systemJson.get("Version");
		versionHigher131=isVersionAfter131();
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(null,
				    "Can't connect to Orthanc");
			test=false;
			e.printStackTrace();
		}
	
		return test;
	}
	
	/**
	 * Test if version is higher than 1.3.1
	 * @return
	 */
	private boolean isVersionAfter131() {
		int test=versionCompare(orthancVersion, "1.3.1");
		if (test>0) return true; else return false;
		
	}
	/**
	 * Get the boolean test version
	 * @return
	 */
	public boolean getIfVersionAfter131() {
		return versionHigher131;
	}
	
	/**
	 * Compares two version strings. 
	 * 
	 * Use this instead of String.compareTo() for a non-lexicographical 
	 * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
	 * 
	 * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
	 * 
	 * @param str1 a string of ordinal numbers separated by decimal points. 
	 * @param str2 a string of ordinal numbers separated by decimal points.
	 * @return The result is a negative integer if str1 is _numerically_ less than str2. 
	 *         The result is a positive integer if str1 is _numerically_ greater than str2. 
	 *         The result is zero if the strings are _numerically_ equal.
	 */
	public static int versionCompare(String str1, String str2) {
	    String[] vals1 = str1.split("\\.");
	    String[] vals2 = str2.split("\\.");
	    int i = 0;
	    // set index to first non-equal ordinal or length of shortest version string
	    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
	      i++;
	    }
	    // compare first non-equal ordinal number
	    if (i < vals1.length && i < vals2.length) {
	        int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
	        return Integer.signum(diff);
	    }
	    // the strings are equal or one string is a substring of the other
	    // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
	    return Integer.signum(vals1.length - vals2.length);
	}
	
	public void restartOrthanc() {
		StringBuilder sb=makePostConnectionAndStringBuilder("/tools/reset", "");
		if (sb !=null) {
			JOptionPane.showMessageDialog(null,"Server Sucessfully restarted");
		}

	}
	
	

	
}
