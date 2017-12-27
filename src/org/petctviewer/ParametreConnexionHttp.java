package org.petctviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.petctviewer.setup.*;

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
	
	
	public ParametreConnexionHttp() {
	
			int curDb = jprefer.getInt("current database", 0);
			int typeDb = jprefer.getInt("db type" + curDb, 5);
			String ip=null;
			String port=null;
			
			if(typeDb == 5){
				
				if(!jprefer.get("db path" + curDb, "none").equals("none") && !jprefer.get("db path" + curDb, "none").equals("")){
					String pathBrut = jprefer.get("db path" + curDb, "none") + "/";
					int index = ordinalIndexOf(pathBrut, "/", 3);
					this.fullAddress = pathBrut.substring(0, index);
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
		}else{
			ip = jpreferPerso.get("ip", "http://localhost");
			port = jpreferPerso.get("port", "8042");
			this.fullAddress = ip + ":" + port;
			if(jpreferPerso.get("username", null) != null && jpreferPerso.get("username", null) != null){
				authentication = Base64.getEncoder().encodeToString((jpreferPerso.get("username", null) + ":" + jpreferPerso.get("password", null)).getBytes());
			}
		}
			
		testConnexion();
		
	}
	
	public HttpURLConnection makeGetConnection(String apiUrl) throws IOException {
		HttpURLConnection conn=null;
		
		try {
			URL url = new URL(fullAddress+apiUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		
		if((fullAddress != null && fullAddress.contains("https"))){
			try{
				HttpsTrustModifier.Trust(conn);
			}catch (Exception e){
				throw new IOException("Cannot allow self-signed certificates");
			}
		}
		
		if(authentication != null){
			conn.setRequestProperty("Authorization", "Basic " + authentication);
		}
		
		conn.getResponseMessage();
		
		return conn;

	}

	public StringBuilder makeGetConnectionAndStringBuilder(String apiUrl) throws IOException {
		HttpURLConnection conn=makeGetConnection(apiUrl);
		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		StringBuilder sb = new StringBuilder();
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		conn.disconnect();
		
		return sb;
	}
	
	
	
public HttpURLConnection makePostConnection(String apiUrl, String post) throws IOException {
		
		
		URL url = new URL(fullAddress+apiUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		if((fullAddress != null && fullAddress.contains("https")) ){
			try{
				HttpsTrustModifier.Trust(conn);
			}catch (Exception e){
				throw new IOException("Cannot allow self-signed certificates");
			}
		}
		if(this.authentication != null){
			conn.setRequestProperty("Authorization", "Basic " + this.authentication);
		}
		OutputStream os = conn.getOutputStream();
		os.write(post.getBytes());
		os.flush();
		
		conn.getResponseMessage();
		
		return conn;
}

public HttpURLConnection sendDicom(String apiUrl, byte[] post) throws IOException {
	URL url = new URL(fullAddress+apiUrl);
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setDoOutput(true);
	conn.setRequestMethod("POST");
	
	if((fullAddress != null && fullAddress.contains("https")) ){
		try{
			HttpsTrustModifier.Trust(conn);
		}catch (Exception e){
			throw new IOException("Cannot allow self-signed certificates");
		}
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
	
	return conn;
}
		
	public StringBuilder makePostConnectionAndStringBuilder(String apiUrl, String post) throws IOException {
		HttpURLConnection conn = makePostConnection(apiUrl, post);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		// We get the study ID at the end
		StringBuilder sb = new StringBuilder();
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		conn.disconnect();
		conn.getResponseMessage();
		
		return sb; 


}

public void makeDeleteConnection(String apiUrl) throws IOException {
	
	
	URL url = new URL(fullAddress+apiUrl);
	
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setDoOutput(true);
	conn.setRequestMethod("DELETE");
	if((fullAddress != null && fullAddress.contains("https")) ){
		try{
			HttpsTrustModifier.Trust(conn);
		}catch (Exception e){
			throw new IOException("Cannot allow self-signed certificates");
		}
	}
	if(this.authentication != null){
		conn.setRequestProperty("Authorization", "Basic " + this.authentication);
	}
	
	conn.getResponseMessage();
	
	conn.disconnect();

}
	
	private int ordinalIndexOf(String str, String substr, int n) {
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
			pos = str.indexOf(substr, pos + 1);
		return pos;
	}
	
	public String[] getConnexionParameter() {
		String[] parameters=new String[2];
		parameters[0]=fullAddress;
		parameters[1]=authentication;
		return parameters;
	}
	// Display Error message if connexion failed
	
	//A FAIRE SK OUVRIR PANNEAU DE CONFIG SI ECHEC
	//Panel Dans Query ? Attendre Fusion
	private void testConnexion(){
				
		try {
			HttpURLConnection conn = makeGetConnection("/system");
			conn.getResponseMessage();
		} catch (IOException e2) {
			e2.printStackTrace();
			System.out.println("ici");
			JOptionPane.showMessageDialog(null,
				    "Check Connexion Parameters",
				    "Connexion",
				    JOptionPane.ERROR_MESSAGE);
			ConnectionSetup.main();
		}
			
		
	}

	
}
