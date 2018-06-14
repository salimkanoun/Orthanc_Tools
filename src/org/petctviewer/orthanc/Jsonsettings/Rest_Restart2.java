package org.petctviewer.orthanc.Jsonsettings;
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

import javax.swing.JOptionPane;

import org.json.simple.parser.ParseException;

public class Rest_Restart2 {
	private String url;
	private String line;
	private HttpURLConnection conn;
	
	public Rest_Restart2() {
		
		}
	
	public static void main(String... args) throws IOException, ParseException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		Rest_Restart2 restart=new Rest_Restart2();
		restart.getSystem("http://localhost:8042/system", null, null);
		
	}
		
		
		
		public void restartOrthanc(String url, String login, String password) throws IOException, ParseException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
			this.url = url;
			URL url2 = new URL(this.url);
			String authentication = Base64.getEncoder().encodeToString((login + ":" + password).getBytes());
			conn = (HttpURLConnection) url2.openConnection();
			String post="";
			conn.setDoInput(true);
			conn.setDoOutput(true);
			if((url.contains("https"))){
				HttpsTrustModifier.Trust(conn);
			}
			if(login != null){
			conn.setRequestProperty("Authorization", "Basic " + authentication);
			}	
			conn.setRequestMethod("POST");
			conn.setRequestProperty("content-length", String.valueOf(post.getBytes()));
			conn.setRequestProperty("content-type", "application/json");
			OutputStream os = conn.getOutputStream();
			os.write(post.getBytes());
			os.close();
			os.flush();
			//On doit lire l'input stream dans tous les cas
			InputStreamReader input=new InputStreamReader (conn.getInputStream());
			BufferedReader br = new BufferedReader(input);
			if (br.readLine()!=null) {
				JOptionPane.showMessageDialog(null,"Server Sucessfully restarted");
			}
	
			}
		
		public boolean getSystem(String url, String login, String password) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
			this.url = url;
			URL url2 = new URL(this.url);
			String authentication = Base64.getEncoder().encodeToString((login + ":" + password).getBytes());
			BufferedReader br =null;
			
			try {
				conn = (HttpURLConnection) url2.openConnection();
				if((url.contains("https"))){
					HttpsTrustModifier.Trust(conn);
				}
				if(login != null){
				conn.setRequestProperty("Authorization", "Basic " + authentication);
				}	
				conn.setDoInput(true);
				conn.setRequestMethod("GET");
				InputStreamReader input=new InputStreamReader (conn.getInputStream());
				br = new BufferedReader(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			StringBuilder sb = new StringBuilder();
			if (br.toString()!="null") {
				do {
				line = br.readLine();
				sb.append(line);
				}
			while (line != null);}
				return true;
		}
		
		public void restPost() throws IOException {

		}
}

