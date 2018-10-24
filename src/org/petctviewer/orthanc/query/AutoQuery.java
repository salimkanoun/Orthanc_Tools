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

package org.petctviewer.orthanc.query;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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



public class AutoQuery  {

	protected Rest api;
	protected Object[] aet=new String[] {""};
	protected Object[] aetRetrieve=new String[] {""};
	private Preferences jPrefer;
	
	protected long fONCE_PER_DAY=1000*60*60*24;
	protected int fTEN_PM=22;
	protected int fZERO_MINUTES=00;
	protected int discard=10;
	protected String serieDescriptionContains, serieDescriptionExclude, serieNumberExclude, serieNumberMatch;
	protected boolean chckbxCr , chckbxCt, chckbxCmr, chckbxNm, chckbxPt, chckbxUs, chckbxXa , chckbxMg, chckbxSeriesFilter;
	private DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	
	public AutoQuery(Rest rest) {
		api=rest;
		try {
			aet=api.getAET();
			aetRetrieve=api.getLocalAET();
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
		//Get Jprefer Value
		jPrefer = Preferences.userNodeForPackage(AutoQuery.class);
		jPrefer = jPrefer.node("AutoQuery");
		//dicard value
		discard=jPrefer.getInt("discard", 10);
		//time value
		fTEN_PM=jPrefer.getInt("hour", 22);
		fZERO_MINUTES=jPrefer.getInt("minutes", 00);
		//serie filter
		chckbxSeriesFilter=jPrefer.getBoolean("useSeriesFilter", false);
		serieDescriptionContains=jPrefer.get("seriesDescriptionContains", "").toLowerCase();
		serieDescriptionExclude=jPrefer.get("seriesDescriptionExclude", "").toLowerCase();
		serieNumberMatch=jPrefer.get("seriesNumberContains", "");
		serieNumberExclude=jPrefer.get("seriesNumberExclude", "");
		chckbxCr=jPrefer.getBoolean("useSeriesCRFilter", false);
		chckbxCt=jPrefer.getBoolean("useSeriesCTFilter", false);
		chckbxCmr=jPrefer.getBoolean("useSeriesCMRFilter", false);
		chckbxNm=jPrefer.getBoolean("useSeriesNMFilter", false);
		chckbxPt=jPrefer.getBoolean("useSeriesPTFilter", false);
		chckbxUs=jPrefer.getBoolean("useSeriesUSFilter", false);
		chckbxXa=jPrefer.getBoolean("useSeriesXAFilter", false);
		chckbxMg=jPrefer.getBoolean("useSeriesMGFilter", false);
		
		
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
	public String[] sendQuery(String name, String id, String dateFrom, String dateTo, String modality, String studyDescription, String accessionNumber, String aet) {
		String[] results=null;
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
				results=api.getQueryAnswerIndexes("Study", name , id, date, modality, studyDescription , accessionNumber, aet);
				
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
	public void retrieveQuery(String[] results, String aetRetrieve, int discard, int queryNumberList) {
	
		if (Integer.valueOf(results[1])<=discard){		
			for (int i=0; i<Integer.valueOf(results[1]); i++) {
			api.retrieve(results[0], i, aetRetrieve );
			}
			System.out.println( results[1] + " studies Retrieved");
		}
		else {
			System.out.println("Discarted because Study Query answers over discard limit");
		}
	}
	
	
	
	
	/**
	 * Date programmation
	 * @return
	 */
	protected Date tenPM() {
		
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
	protected void csvReading(File file, JTable table) throws IOException {
		
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
	/**
	 * Get content of a result.
	 * @param results
	 * @param path
	 * @throws IOException
	 * @throws ParseException 
	 */
	protected void getContent(String[] results, ArrayList<Patient> patientArray) throws IOException, ParseException {
		DateFormat parser = new SimpleDateFormat("yyyyMMdd");
		
		for (int i=0; i<Integer.parseInt(results[1]); i++) {
			String name = (String)api.getValue(api.getIndexContent(results[0],i), "PatientName");
			String id = (String)api.getValue(api.getIndexContent(results[0],i), "PatientID");
			String accNumber = (String)api.getValue(api.getIndexContent(results[0],i), "AccessionNumber");
			Date date = parser.parse((String)api.getValue(api.getIndexContent(results[0],i), "StudyDate"));
			String studyDesc = (String)api.getValue(api.getIndexContent(results[0],i), "StudyDescription");
			String modality = (String)api.getValue(api.getIndexContent(results[0],i), "ModalitiesInStudy");
			String studyUID = (String)api.getValue(api.getIndexContent(results[0],i), "StudyInstanceUID");
			
			Patient patient=new Patient(name, id, date, studyDesc, accNumber, studyUID, modality);
			patientArray.add(patient);
		}
		
		
	}
	
	
	
	
	
	


}

