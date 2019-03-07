package org.petctviewer.orthanc.OTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.petctviewer.orthanc.setup.HttpsTrustModifier;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OTP {
	private String username;
	private String password;
	private String serverAdress;
	private JsonParser parser=new JsonParser();
	
	public OTP(String username, String password, String serverAdress) {
		this.username=username;
		this.password=password;
		this.serverAdress=serverAdress;
	}
	
	public boolean checkLogin() {
		JsonObject jsonPost=new JsonObject();
		jsonPost.addProperty("username", username);
		jsonPost.addProperty("password", password);
		String answser=makePostConnection("/Rest_Api/check_login.php",jsonPost.toString());
		System.out.println(answser);
		JsonObject response = null;

		response=parser.parse(answser).getAsJsonObject();

		if(!response.get("login").getAsString().equals("Allowed") ) {
			JOptionPane.showMessageDialog(null, response.get("login").toString(), "Login Error",  JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
		
	}
	
	public String[] getAvailableStudies(){
		JsonObject jsonPost=new JsonObject();
		jsonPost.addProperty("username", username);
		jsonPost.addProperty("password", password);
		
		JsonArray studies = null;
		String answser=makePostConnection("/Rest_Api/get-studies.php",jsonPost.toString());
		System.out.println(answser);
		studies=parser.parse(answser).getAsJsonArray();

		List<String> studiesList=new ArrayList<String>();
		for(int i=0; i<studies.size(); i++) {
			studiesList.add(studies.get(i).getAsString());
		}
		String[] studiesTable=new String[studies.size()];
		studiesList.toArray(studiesTable);
		return studiesTable;
		
	}

	public String[] getAvailableVisits(String studyName) {
		JsonObject jsonPost=new JsonObject();
		jsonPost.addProperty("username", username);
		jsonPost.addProperty("password", password);
		jsonPost.addProperty("studyName", studyName);
		System.out.println(jsonPost);
		JsonArray visits = null;
		String answser=makePostConnection("/Rest_Api/get-visits.php", jsonPost.toString());
		System.out.println(answser);
		System.out.println(answser==null);
		//SK A GERER COTE SERVEUR POUR AVOIR TOUJOURS UNE REPONSE EN JSON
		try {
		visits=parser.parse(answser).getAsJsonArray();
		}catch (Exception e){
			e.printStackTrace();
		}
		
		List<String> visitsList=new ArrayList<String>();
		if (visits !=null) {
			for(int i=0; i<visits.size(); i++) {
				visitsList.add(visits.get(i).getAsString());
			}
			String[] visitsTable=new String[visitsList.size()];
			visitsList.toArray(visitsTable);
			return visitsTable;
		}
		else {
			return null;
		}
		
	}
	
	public JsonArray getAvailableImports(String studyName, String visitName) {
		JsonObject jsonPost=new JsonObject();
		jsonPost.addProperty("username", username);
		jsonPost.addProperty("password", password);
		jsonPost.addProperty("studyName", studyName);
		jsonPost.addProperty("visit", visitName);
		
		JsonArray visits = null;
		String answser=makePostConnection("/Rest_Api/get-possible-import.php", jsonPost.toString());
		visits=parser.parse(answser).getAsJsonArray();

		if (visits.size() !=0) {	
			return visits;
		} else {
			return null;
		}
		
	}

	public boolean validateUpload(JsonArray studiesArray) {
		
		JsonObject jsonPost=new JsonObject();
		jsonPost.addProperty("username", username);
		jsonPost.addProperty("password", password);
		jsonPost.add("studies", studiesArray);
		//SK A MODFIER COTE PLATEFORME POUR SUPPORTER L ENVOI MULTIPLE
		System.out.println(jsonPost.toString());
		
		JsonObject visits = null;
		String answser=makePostConnection("/Rest_Api/validate-upload.php", jsonPost.toString());
		System.out.println(answser);
		visits=parser.parse(answser).getAsJsonObject();
			
		return visits.get("recivedConfirmation").getAsBoolean();
		
		
	}
	
	//SK DUPLICATE DE METHODE DANS CONNEXION HTTP  A VOIR
	private String makePostConnection(String apiUrl, String post) {
		URL url = null;
		StringBuilder sb=new StringBuilder();
		try {
			url = new URL(serverAdress+apiUrl);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			if((serverAdress != null && serverAdress.contains("https")) ){
				HttpsTrustModifier.Trust(conn);		
			}
			OutputStream os = conn.getOutputStream();
			os.write(post.getBytes());
			os.flush();
			conn.getResponseMessage();

			if (conn !=null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
				String output;
				while ((output = br.readLine()) != null) {
					sb.append(output);
					System.out.println(output);
				}
				conn.disconnect();
			}
		} catch (Exception ex) { 
			ex.printStackTrace();
		};
		System.out.println(sb.length());
		return sb.toString();
	}

}
