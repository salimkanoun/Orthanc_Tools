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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.prefs.Preferences;

public class JDBC_Monitoring<accessionNumber> {

	private Connection connection;
	
	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");
		

	public JDBC_Monitoring() {
		//Class.forName("com.mysql.jdbc.Driver");
		if(jprefer.get("dbAdress", null) != null && jprefer.get("dbPort", null) != null && jprefer.get("dbName", null) != null &&
				jprefer.get("dbUsername", null) != null && jprefer.get("dbPassword", null) != null){
			try {
				connection = DriverManager.getConnection("jdbc:mysql://" + jprefer.get("dbAdress", null) + ":" 
					+ jprefer.get("dbPort", null)  + "/" + jprefer.get("dbName", null), jprefer.get("dbUsername", null), jprefer.get("dbPassword", null));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			String studyDate, String studyDescription, String studyTime, String parentPatientOrthanc  ) {
	
		try {
			Statement st = connection.createStatement();
			String sql = ("INSERT INTO `studies`(`accessionNumber`, `institutionName`, `referringPhysicianName`, `studyDate`, `studyDescription`, `studyID`, `studyInstanceUID`, `studyTime`, `Orthanc_Study_ID`, `parentPatientOrthanc`) "
					+ "VALUES ('"+accessionNumber+"','"+institutionName+"','"+referringPhysicianName+"','"+studyDate+"','"+studyDescription+"','"+studyID+"','"+studyInstanceUID+"','"+studyTime+"','"+Orthanc_Study_ID+"','"+parentPatientOrthanc+"')");
			System.out.println(sql);
			boolean rs = st.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}

	public void InsertSeries(String size, String age, String weight, String Manifacturer, String Manifacturer_Model, String Performing_Physician_Name, String Series_Description, String Station_Name, String Content_Date, String Content_Time,
			String Protocol_Name, String Series_Instance_UID, String  Comment_Radiation_Dose, String Radiopharmaceutical_sequence, String Radiopharmaceutical, String RadiopharmaceuticalStartTime, String RadionuclideTotalDose, String RadionuclideHalfLife, String RadionuclidePositronFraction,
			String Radiation_Dose_Module, String Shared_Tags, String Orthanc_Serie_ID, String parentStudyOrthanc) {
	
		try {
			Statement st = connection.createStatement();
			String sql = ("INSERT INTO `series`(`size`, `age`, `weight`, `Manifacturer`, `Manifacturer_Model`, `Performing_Physician_Name`, `Series_Description`, `Station_Name`, `Content_Date`, `Content_Time`, `Protocol_Name`, `Series_Instance_UID`, `Comment_Radiation_Dose`, `Radiopharmaceutical_sequence`, `Radiopharmaceutical`, `RadiopharmaceuticalStartTime`, `RadionuclideTotalDose`, `RadionuclideHalfLife`, `RadionuclidePositronFraction`, `Radiation_Dose_Module`, `shared_Tags`, `Orthanc_Serie_ID`,`parentStudyOrthanc` ) "
					+ "VALUES ('"+size+"','"+age+"','"+weight+"','"+Manifacturer+"','"+Manifacturer_Model+"','"+Performing_Physician_Name+"','"+Series_Description+"','"+Station_Name+"','"+Content_Date+"','"+Content_Time+"','"+Protocol_Name+"','"+Series_Instance_UID+"','"+Comment_Radiation_Dose+"','"+Radiopharmaceutical_sequence+"','"+Radiopharmaceutical+"','"+RadiopharmaceuticalStartTime+"','"+RadionuclideTotalDose+"','"+RadionuclideHalfLife+"','"+RadionuclidePositronFraction+"','"+Radiation_Dose_Module+"','"+Shared_Tags+"','"+Orthanc_Serie_ID+"','"+parentStudyOrthanc+"')");
			System.out.println(sql);
			boolean rs = st.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	

}
