package org.petctviewer.orthanc.CTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.HttpsTrustModifier;

public class CTP {
	private String username;
	private String password;
	private String serverAdress="http://localhost/Rest_Api";
	private String authentication=null;
	private JSONParser parser=new JSONParser();
	
	public static void main (String[] args) {
		CTP ctp=new CTP("Imagerie", "Salim1985");
		
	}
	
	public CTP(String username, String password) {
		this.username=username;
		this.password=password;
		getAvailableStudies();
		//String authentication = Base64.getEncoder().encodeToString(("httpLogin" + ":" + "httpPassword").getBytes());
		
	}
	
	public String[] getAvailableStudies(){
		JSONObject jsonPost=new JSONObject();
		jsonPost.put("username", username);
		jsonPost.put("password", password);
		JSONArray studies = null;
		try {
			String answser=makePostConnection("/get-studies",jsonPost.toString());
			studies=(JSONArray) parser.parse(answser);
		} catch (IOException | ParseException e) {
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
	
	
	private String makePostConnection(String apiUrl, String post) throws IOException {
		URL url = null;
		try {
		url = new URL(serverAdress+apiUrl);
		} catch ( MalformedURLException ex) { }
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		if((serverAdress != null && serverAdress.contains("https")) ){
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
		
		StringBuilder sb=new StringBuilder();
		if (conn !=null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			conn.disconnect();
		}
		return sb.toString();
	}

}
