package org.petctviewer.orthanc.CTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.HttpsTrustModifier;

public class CTP {
	private String username;
	private String password;
	private String serverAdress;
	private String authentication=null;
	private JSONParser parser=new JSONParser();
	
	public CTP(String username, String password, String serverAdress) {
		this.username=username;
		this.password=password;
		this.serverAdress=serverAdress;
		//String authentication = Base64.getEncoder().encodeToString(("httpLogin" + ":" + "httpPassword").getBytes());
		
	}
	
	public boolean checkLogin() {
		JSONObject jsonPost=new JSONObject();
		jsonPost.put("username", username);
		jsonPost.put("password", password);
		String answser=makePostConnection("/Rest_Api/check_login.php",jsonPost.toString());
		System.out.println(answser);
		JSONObject response = null;
		try {
			response=(JSONObject) parser.parse(answser);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!response.get("login").equals("Allowed") ) {
			JOptionPane.showMessageDialog(null, response.get("login").toString(), "Login Error",  JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
		
	}
	
	@SuppressWarnings("unchecked")
	public String[] getAvailableStudies(){
		JSONObject jsonPost=new JSONObject();
		jsonPost.put("username", username);
		jsonPost.put("password", password);
		JSONArray studies = null;
		try {
			String answser=makePostConnection("/Rest_Api/get-studies.php",jsonPost.toString());
			System.out.println(answser);
			studies=(JSONArray) parser.parse(answser);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> studiesList=new ArrayList<String>();
		for(int i=0; i<studies.size(); i++) {
			studiesList.add((String) studies.get(i));
		}
		String[] studiesTable=new String[studies.size()];
		studiesList.toArray(studiesTable);
		return studiesTable;
		
	}
	
	@SuppressWarnings("unchecked")
	public String[] getAvailableVisits(String studyName) {
		JSONObject jsonPost=new JSONObject();
		jsonPost.put("username", username);
		jsonPost.put("password", password);
		jsonPost.put("studyName", studyName);
		JSONArray visits = null;
		try {
			String answser=makePostConnection("/Rest_Api/get-visits.php",jsonPost.toString());
			visits=(JSONArray) parser.parse(answser);
		} catch ( ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> visitsList=new ArrayList<String>();
		if (visits !=null) {
			for(int i=0; i<visits.size(); i++) {
				visitsList.add((String) visits.get(i));
			}
			String[] visitsTable=new String[visitsList.size()];
			visitsList.toArray(visitsTable);
			return visitsTable;
		}
		else {
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getAvailableImports(String studyName, String visitName) {
		JSONObject jsonPost=new JSONObject();
		jsonPost.put("username", username);
		jsonPost.put("password", password);
		jsonPost.put("studyName", studyName);
		jsonPost.put("visit", visitName);
		JSONArray visits = null;
		try {
			String answser=makePostConnection("/Rest_Api/get-possible-import.php", jsonPost.toString());
			visits=(JSONArray) parser.parse(answser);
		} catch ( ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (visits !=null) {	
			return visits;
		}
		else {
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean validateUpload(JSONArray studiesArray) {
		
		JSONObject jsonPost=new JSONObject();
		jsonPost.put("username", username);
		jsonPost.put("password", password);
		jsonPost.put("studies", studiesArray);
		//SK A MODFIER COTE PLATEFORME POUR SUPPORTER L ENVOI MULTIPLE
		System.out.println(jsonPost.toString());
		
		JSONObject visits = null;

		String answser=makePostConnection("/Rest_Api/validate-upload.php", jsonPost.toString());
		System.out.println(answser);
		try {
			visits=(JSONObject) parser.parse(answser);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			
		return (boolean) visits.get("recivedConfirmation");
		
		
	}
	
	
	private String makePostConnection(String apiUrl, String post) {
		URL url = null;
		StringBuilder sb=new StringBuilder();
		try {
			url = new URL(serverAdress+apiUrl);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			if((serverAdress != null && serverAdress.contains("https")) ){
				try{
					HttpsTrustModifier.Trust(conn);
				}catch (Exception e){
					
				}
			}
			if(this.authentication != null){
				conn.setRequestProperty("Authorization", "Basic " + this.authentication);
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
				}
				conn.disconnect();
			}
		} catch ( IOException ex) { };
		
		return sb.toString();
	}

}
