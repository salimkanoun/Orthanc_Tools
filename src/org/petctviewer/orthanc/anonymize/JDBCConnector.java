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

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class JDBCConnector {

	private Connection connection;
	private ArrayList<String> newName = new ArrayList<String>();
	private ArrayList<String> newId = new ArrayList<String>();
	private ArrayList<String> oldFirstName = new ArrayList<String>();
	private ArrayList<String> oldLastName = new ArrayList<String>();
	private Preferences jprefer = Preferences.userRoot().node("<unnamed>/anonPlugin");

	public JDBCConnector() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		if(jprefer.get("dbAdress", null) != null && jprefer.get("dbPort", null) != null && jprefer.get("dbName", null) != null &&
				jprefer.get("dbUsername", null) != null && jprefer.get("dbPassword", null) != null){
			connection = DriverManager.getConnection("jdbc:mysql://" + jprefer.get("dbAdress", null) + ":" 
				+ jprefer.get("dbPort", null)  + "/" + jprefer.get("dbName", null), jprefer.get("dbUsername", null), jprefer.get("dbPassword", null));
		}
	}

	public void disconnect() throws SQLException{
		connection.close();
	}

	public void newValuesQuery(Date birthday, String codeCentre) throws SQLException{
		this.newName.clear();
		this.newId.clear();
		Statement st = connection.createStatement();
		String sql = ("SELECT nameAnon, idAnon, name, firstName, codeCentre, birthday "
				+ "FROM CTP "
				+ "WHERE birthday = " + "\"" + birthday + "\""
				+ " AND codeCentre = " + "\"" + codeCentre + "\""
				+ ";");
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()) { 
			if(rs.getString(1).length() > 0){
				this.newName.add(rs.getString(1));
				this.newId.add(rs.getString(2));
				this.oldLastName.add(rs.getString(3));
				this.oldFirstName.add(rs.getString(4));
			}
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
	
	public ArrayList<String> getNewName(){
		return this.newName;
	}

	public ArrayList<String> getNewId(){
		return this.newId;
	}
	
	public ArrayList<String> getOldFirstName(){
		return this.oldFirstName;
	}
	
	public ArrayList<String> getOldLastName(){
		return this.oldLastName;
	}
}
