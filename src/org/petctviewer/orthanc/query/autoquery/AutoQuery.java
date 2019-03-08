/**
Copyright (C) 2017 KANOUN Salim

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

package org.petctviewer.orthanc.query.autoquery;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.query.QueryRetrieve;
import org.petctviewer.orthanc.query.StudyDetails;

public class AutoQuery  {

	private QueryRetrieve api;
	private Preferences jPrefer;
	
	public long fONCE_PER_DAY=1000*60*60*24;
	public int fTEN_PM=22;
	public int fZERO_MINUTES=00;
	public int discard=10;
	public String serieDescriptionContains, serieDescriptionExclude, serieNumberExclude, serieNumberMatch;
	public boolean chckbxCr , chckbxCt, chckbxCmr, chckbxNm, chckbxPt, chckbxUs, chckbxXa , chckbxMg, chckbxSeriesFilter;
	
	private DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	
	public AutoQuery(QueryRetrieve rest) {
		api=rest;

		//Get Jprefer Value
		jPrefer = VueAnon.jprefer;
		//dicard value
		discard=jPrefer.getInt("AutoQuery_discard", 10);
		//time value
		fTEN_PM=jPrefer.getInt("AutoQuery_hour", 22);
		fZERO_MINUTES=jPrefer.getInt("AutoQuery_minutes", 00);
		//serie filter
		chckbxSeriesFilter=jPrefer.getBoolean("AutoQuery_useSeriesFilter", false);
		serieDescriptionContains=jPrefer.get("AutoQuery_seriesDescriptionContains", "").toLowerCase();
		serieDescriptionExclude=jPrefer.get("AutoQuery_seriesDescriptionExclude", "").toLowerCase();
		serieNumberMatch=jPrefer.get("AutoQuery_seriesNumberContains", "");
		serieNumberExclude=jPrefer.get("AutoQuery_seriesNumberExclude", "");
		chckbxCr=jPrefer.getBoolean("AutoQuery_useSeriesCRFilter", false);
		chckbxCt=jPrefer.getBoolean("AutoQuery_useSeriesCTFilter", false);
		chckbxCmr=jPrefer.getBoolean("AutoQuery_useSeriesCMRFilter", false);
		chckbxNm=jPrefer.getBoolean("AutoQuery_useSeriesNMFilter", false);
		chckbxPt=jPrefer.getBoolean("AutoQuery_useSeriesPTFilter", false);
		chckbxUs=jPrefer.getBoolean("AutoQuery_useSeriesUSFilter", false);
		chckbxXa=jPrefer.getBoolean("AutoQuery_useSeriesXAFilter", false);
		chckbxMg=jPrefer.getBoolean("AutoQuery_useSeriesMGFilter", false);
		
		
	}
	/***
	 * Send a Query to a remote AET
	 * @param name
	 * @param id
	 * @param dateFrom
	 * @param dateTo
	 * @param modality
	 * @param studyDescription
	 * @param accessionNumber
	 * @param aet
	 * @return
	 * @throws IOException
	 */
	public StudyDetails[] sendQuery(String name, String id, String dateFrom, String dateTo, String modality, String studyDescription, String accessionNumber, String aet) {
		StudyDetails[] results=null;
		try {
			//Selectionne l'AET de query
			//On format les date pour avoir la bonne string
			Date From=null;
			Date To=null;
			String date=null;

			if (StringUtils.equals(dateFrom, "*")==false) {From=df.parse(dateFrom);};
			if (StringUtils.equals(dateTo, "*")==false) {To=df.parse(dateTo);};
			
			if (From!=null && To!=null) date=df.format(From)+"-"+df.format(To);
			if (From==null && To!=null) date="-"+df.format(To);
			if (From!=null && To==null) date=df.format(From)+"-";
			if (From==null && To==null) date="*";getClass();
			//On lance la query
			if (StringUtils.equals(name, "*")==false || StringUtils.equals(id, "*")==false || StringUtils.equals(dateFrom, "*")==false || StringUtils.equals(dateTo, "*")==false || StringUtils.equals(modality, "*")==false || StringUtils.equals(studyDescription, "*")==false|| StringUtils.equals(accessionNumber, "*")==false) {
				results=api.getStudiesResults("Study", name, id, date, modality, studyDescription, accessionNumber, aet);
			}
			else {
				results=null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return results;
		
	}
	/**
	 * Retrive all results if lower than discard value
	 * @param results
	 * @param aetRetrieve
	 * @param discard
	 * @throws IOException
	 */
	public void retrieveQuery(StudyDetails[] results, String aetRetrieve, int discard, int queryNumberList) {
	
		if (results.length<=discard){
			int studiesRetrievedSuccess=0;
			for (int i=0; i<results.length; i++) {
				try {
					api.retrieve(results[i].getQueryID(), results[i].getAnswerNumber(), aetRetrieve );
					studiesRetrievedSuccess++;
				} catch (Exception e) {
					System.out.println( "Error During Retrieve Patient ID"+results[i].getPatientID() +" Study Date "+ results[i].getStudyDate() );
					e.printStackTrace();
				}
			}
			System.out.println( studiesRetrievedSuccess + " studies Retrieved");
		}
		else {
			System.out.println("Discarted because Study Query answers over discard limit");
		}
	}
	
	/**
	 * Date programmation
	 * @return
	 */
	public Date getStartTime() {
		
		Calendar currentDate = new GregorianCalendar();
        
        Calendar currentTheoricalDate = new GregorianCalendar();
        currentTheoricalDate.set(Calendar.HOUR_OF_DAY, fTEN_PM );
        currentTheoricalDate.set(Calendar.MINUTE, fZERO_MINUTES);
        
        Calendar tenPM;
        
        if (currentDate.before(currentTheoricalDate)) {
        	tenPM =new GregorianCalendar(
        	currentDate.get(Calendar.YEAR),
        	currentDate.get(Calendar.MONTH),
        	currentDate.get(Calendar.DAY_OF_MONTH),
			fTEN_PM,
			fZERO_MINUTES
        	);
        }
        else {
        	currentDate.add(Calendar.DAY_OF_MONTH, 1);
        	//SK AJOUTER 24h
        	tenPM =new GregorianCalendar(
                	currentDate.get(Calendar.YEAR),
                	currentDate.get(Calendar.MONTH),
                	currentDate.get(Calendar.DAY_OF_MONTH),
        			fTEN_PM,
        			fZERO_MINUTES
        			);
        }
		
		return tenPM.getTime();
	}
	
	/**
	 * CSV reader and inject value in table
	 * @param file
	 * @param table
	 * @throws IOException
	 */
	public void csvReading(File file, JTable table) throws IOException {
		
  	  CSVFormat csvFileFormat = CSVFormat.EXCEL.withFirstRecordAsHeader().withIgnoreEmptyLines();
  	  CSVParser csvParser=CSVParser.parse(file, StandardCharsets.UTF_8,  csvFileFormat);
  	  
  	  //If only one column maybe French CSV with semi column Separator
  	  if(csvParser.getHeaderMap().size()==1) {
  		CSVFormat csvFileFormatFrench = CSVFormat.EXCEL.withDelimiter(';').withFirstRecordAsHeader().withIgnoreEmptyLines();
  		csvParser=CSVParser.parse(file, StandardCharsets.UTF_8,  csvFileFormatFrench);
  	  };
  	  //On met les records dans une list
  	  List<CSVRecord> csvRecord=csvParser.getRecords();
  	  // On balaie le fichier ligne par ligne
  	  int discarded=0;
  	  Boolean error=false;
  	 
	  for (int i=0 ; i<csvRecord.size(); i++) { 
			  try {
			  //On recupere les variables
	  		  String name=csvRecord.get(i).get(0);
	  		  String prenom=csvRecord.get(i).get(1);
	  		  String id=csvRecord.get(i).get(2);
	  		  String accession=csvRecord.get(i).get(3);
	  		  String dateFrom=csvRecord.get(i).get(4);
	  		  String dateTo=csvRecord.get(i).get(5);
	  		  String modality=csvRecord.get(i).get(6);
	  		  String studyDescription=csvRecord.get(i).get(7);
	  		  //On les pousse dans le tableau
	  		  DefaultTableModel model = (DefaultTableModel) table.getModel();
	  		  model.addRow(new Object[] {name, prenom, id, accession,dateFrom, dateTo, modality, studyDescription });
	  		  }
		   catch(NullPointerException | ArrayIndexOutOfBoundsException e) {
			  System.out.println("Error in line "+ i +" discarding");
			  discarded++;
			  error=true;
		  
		   }
	  	}
  		if (error)  JOptionPane.showMessageDialog(null,
  				discarded +" lines discarded, see console for more details",
  			    "Wrong Input",
  			    JOptionPane.WARNING_MESSAGE);
  	  }

}

