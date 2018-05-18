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

package org.petctviewer.orthanc.monitoring;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class JDBC_Monitoring<accessionNumber> {

	private Connection connection;
	
	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");
	
	public static void main(String arg[]) {
		
		try {
			JDBC_Monitoring monitoring=new JDBC_Monitoring();
			monitoring.InsertPatient("a", "b", "c", "dd","e","f");
			monitoring.InsertStudy("gd", "dfg", "dfg", "dg","dg", "dfg", "dfg", "dg", "gd");
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		

	public JDBC_Monitoring() throws ClassNotFoundException, SQLException{
		//Class.forName("com.mysql.jdbc.Driver");
		if(jprefer.get("dbAdress", null) != null && jprefer.get("dbPort", null) != null && jprefer.get("dbName", null) != null &&
				jprefer.get("dbUsername", null) != null && jprefer.get("dbPassword", null) != null){
			connection = DriverManager.getConnection("jdbc:mysql://" + jprefer.get("dbAdress", null) + ":" 
				+ jprefer.get("dbPort", null)  + "/" + jprefer.get("dbName", null), jprefer.get("dbUsername", null), jprefer.get("dbPassword", null));
		}
	}

	public void disconnect() throws SQLException{
		connection.close();
	}

	public void InsertPatient(String patientLastName, String patientFirstName, String patientID, String orthancID, String DOB, String Sex) {
		
		try {
			Statement st = connection.createStatement();
			String sql = ("INSERT INTO `patients`(`Orthanc_Patient_ID`, `Last_Name`, `First_Name`, `Patient_ID`, `DOB`, `Sex`) "
					+ "VALUES ('"+orthancID+"','"+patientLastName+"','"+patientFirstName+"','"+patientID+"','"+DOB+"','"+Sex+"')");
			System.out.println(sql);
			boolean rs = st.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
	public void InsertStudy(String studyID, String studyInstanceUID, String Orthanc_Study_ID, String accessionNumber, String institutionName, String referringPhysicianName,
			String studyDate, String studyDescription, String studyTime  ) {
	
		try {
			Statement st = connection.createStatement();
			String sql = ("INSERT INTO `studies`(`accessionNumber`, `institutionName`, `referringPhysicianName`, `studyDate`, `studyDescription`, `studyID`, `studyInstanceUID`, `studyTime`, `Orthanc_Study_ID`) "
					+ "VALUES ('"+accessionNumber+"','"+institutionName+"','"+referringPhysicianName+"','"+studyDate+"','"+studyDescription+"','"+studyID+"','"+studyInstanceUID+"','"+studyTime+"','"+Orthanc_Study_ID+"')");
			System.out.println(sql);
			boolean rs = st.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}

	public boolean sendSizeAndNewUID(String newName, String size, String studyInstanceUID) throws SQLException{
		Statement st = connection.createStatement();
		int response;
		String sql = ("UPDATE CTP "
				+ "SET size = " + size + ","
				+ "studyInstanceUID = " + "\"" + studyInstanceUID + "\" "
				+ "WHERE nameAnon = " + "\"" + newName + "\""
				+ ";");
		response = st.executeUpdate(sql);		
		return response > 0;
	}
	
	public boolean sendFileName(String newName, String zipName) throws SQLException{
		Statement st = connection.createStatement();
		int response;
		String sql = ("UPDATE CTP "
				+ "SET zipName = " + "\"" + zipName + "\"" 
				+ " WHERE nameAnon = " + "\"" + newName + "\""
				+ ";");
		response = st.executeUpdate(sql);		
		return response > 0;
	}
	

}
