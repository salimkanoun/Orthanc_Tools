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
import org.petctviewer.orthanc.anonymize.QueryOrthancData;
import org.petctviewer.orthanc.anonymize.VueAnon;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.query.QueryRetrieve;
import org.petctviewer.orthanc.query.datastorage.SerieDetails;
import org.petctviewer.orthanc.query.datastorage.StudyDetails;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AutoQuery  {

	private QueryRetrieve queryRetrieve;
	
	private Preferences jPrefer;
	public long fONCE_PER_DAY=1000*60*60*24;
	public int fTEN_PM=22;
	public int fZERO_MINUTES=00;
	public int discard=10;
	public String serieDescriptionContains, serieDescriptionExclude, serieNumberExclude, serieNumberMatch;
	public boolean chckbxCr , chckbxCt, chckbxCmr, chckbxNm, chckbxPt, chckbxUs, chckbxXa , chckbxMg, chckbxSeriesFilter;
	
	private DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	private ArrayList<JsonObject> retrievedStudies;
	
	
	public AutoQuery(QueryRetrieve api) {
		this.queryRetrieve=api;
	}
	
	private void updatePreferences() {
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
				results=queryRetrieve.getStudiesResults("Study", name, id, date, modality, studyDescription, accessionNumber, aet);
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
	public void retrieveQuery(StudyDetails[] results, String aetRetrieve) {
	
		updatePreferences();
		retrievedStudies=new ArrayList<JsonObject>();
		
		if(results.length==0) {
			System.out.println("Empty result");
			return;
		}
		
		if (results.length<=discard){
			int studiesRetrievedSuccess=0;
			for (int i=0; i<results.length; i++) {
				JsonObject answer=null;
				try {
					
					if(this.chckbxSeriesFilter) {
						//ICI RECUPERER L ARRAY LISTE DES STUDYUID?
						filterSerie(results[i], aetRetrieve);
					}else {
						answer=queryRetrieve.retrieve(results[i].getQueryID(), results[i].getAnswerNumber(), aetRetrieve );
					}
				
					
					System.out.println(answer);
					studiesRetrievedSuccess++;
				} catch (Exception e) {
					System.out.println( "Error During Retrieve Patient ID"+results[i].getPatientID() +" Study Date "+ results[i].getStudyDate() );
					e.printStackTrace();
				}
				retrievedStudies.add(answer);
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
	
	public ArrayList<Study2> recievedStudiesAsStudiesObject() {
		QueryOrthancData queryOrthanc=new QueryOrthancData(queryRetrieve.getConnexion());
		
		ArrayList<Study2> studies=new ArrayList<Study2>();
		
		for(JsonObject studyanswer :retrievedStudies) {
			JsonArray queryArray=studyanswer.get("Query").getAsJsonArray();
			for(int i=0 ; i<queryArray.size() ; i++){
				JsonObject query=queryArray.get(i).getAsJsonObject();
				String studyUID=query.get("0020,000d").getAsString();
				Study2 studyObject=queryOrthanc.getStudyObjbyStudyInstanceUID(studyUID);
				studies.add(studyObject);
				
			}
		}
		
		
		
		return studies;
		
		
	}
	
	private ArrayList<JsonObject> filterSerie(StudyDetails studyResults, String destinationAet) throws Exception {
		//counter to log number of series retrieved
		int serieCountRevtrieved=0;
		
		StringBuilder seriesModalities=new StringBuilder();
		if (chckbxCr) seriesModalities.append("/CR/");
		if (chckbxCt) seriesModalities.append("/CT/");
		if (chckbxCmr) seriesModalities.append("/CMR/");
		if (chckbxNm) seriesModalities.append("/NM/");
		if (chckbxPt) seriesModalities.append("/PT/");
		if (chckbxUs) seriesModalities.append("/US/");
		if (chckbxXa) seriesModalities.append("/XA/");
		if (chckbxMg) seriesModalities.append("/MG/");
		
		//on recupere le nombre de condition a checker
		int nombreFiltre=0;
		boolean filtreSerieDescription = false;
		boolean filtreSerieNumber = false;
		boolean filtreSerieModality = false;
		String[] serieDescriptionArray = null;
		String[] serieNumberArray = null;
		String[] serieNumberExcludeArray = null;
		String[] serieDescriptionExcludeArray = null;
		//Convert string in Array splited by ; in which we will look at correspondencies
		if (!StringUtils.isEmpty(seriesModalities.toString())) {
			filtreSerieModality=true; 
			nombreFiltre++;}
		if (!StringUtils.isEmpty(serieDescriptionContains)) {
			serieDescriptionArray=serieDescriptionContains.split(";");
			filtreSerieDescription=true; 
			nombreFiltre++;}
		if (!StringUtils.isEmpty(serieNumberMatch)) {
			serieNumberArray=serieNumberMatch.split(";");
			filtreSerieNumber=true; 
			nombreFiltre++;
			}
		if(!StringUtils.isEmpty(serieDescriptionExclude)) {
			serieDescriptionExcludeArray=serieDescriptionExclude.split(";");
		}
		if(!StringUtils.isEmpty(serieNumberExclude)) {
			serieNumberExcludeArray=serieNumberExclude.split(";");
		}
		
		//On scann tous les results la 1ere dimension contient l'ID de la query et la deuxime le nombre de reponse study a scanner	
		SerieDetails[] seriesDetails=queryRetrieve.getSeriesAnswers(studyResults.getStudyInstanceUID(), studyResults.getSourceAet());
		ArrayList<JsonObject> answers=new ArrayList<JsonObject>();
		//On verifie qu'au moins un parameter est bien defini
		if (!StringUtils.isEmpty(seriesModalities) || !StringUtils.isEmpty(serieDescriptionContains) || !StringUtils.isEmpty(serieNumberMatch)  || !StringUtils.isEmpty(serieDescriptionExclude) || !StringUtils.isEmpty(serieNumberExclude)) {
			//Alors on boucle les reponse	
			for (int k=0; k<seriesDetails.length ; k++) {
				//On definit le candidat:
				String seriesDescription=seriesDetails[k].getSeriesDescription().toLowerCase();
				String modality=seriesDetails[k].getModality();
				String seriesNumber=seriesDetails[k].getSeriesNumber();
			
				JsonObject asnwerSeries = null;
				if ( ! ((!StringUtils.isEmpty(serieDescriptionExclude) && StringUtils.indexOfAny(seriesDescription,serieDescriptionExcludeArray )!=(-1) ) || (!StringUtils.isEmpty(serieNumberExclude) && (StringUtils.indexOfAny(seriesNumber ,serieNumberExcludeArray)) !=(-1) ) )  ) {
					
					//Si on a defini un contains ou un modalitie on prend que si existe un match
					if ( (!StringUtils.isEmpty(seriesModalities.toString()) && StringUtils.contains(seriesModalities.toString(), modality)) || (!StringUtils.isEmpty(serieDescriptionContains) && (StringUtils.indexOfAny(seriesDescription, serieDescriptionArray))!=-1) || ( !StringUtils.isEmpty(serieNumberMatch) && (StringUtils.indexOfAny(seriesNumber, serieNumberArray)!=(-1)))  ) {
						
						// Une condition match
						
						//si plus d'un filtre active on verifie que ca passe les autre filtres
						if (nombreFiltre>1) {
							//Si 3 filtre on demande le perfect match et on retrieve
							if (nombreFiltre==3) {
								if ( StringUtils.contains(seriesModalities.toString(), modality) && (StringUtils.indexOfAny(seriesDescription, serieDescriptionArray)!=(-1)) &&  (StringUtils.indexOfAny(seriesNumber, serieNumberArray)!=(-1)) ){
									asnwerSeries=queryRetrieve.retrieve(seriesDetails[k].getIdQuery(), seriesDetails[k].getAnswerNumber(),  destinationAet);
									
								}
							}
							//Si deux filtre il faut chercher le match des deux conditions
							else if (nombreFiltre==2) {
								if (!filtreSerieDescription) {
									if ( StringUtils.contains(seriesModalities.toString(), modality) &&  (StringUtils.indexOfAny(seriesNumber, serieNumberArray)!=(-1)) ){
										asnwerSeries=queryRetrieve.retrieve(seriesDetails[k].getIdQuery(), seriesDetails[k].getAnswerNumber(),  destinationAet);
									}
								}
								else if (!filtreSerieNumber) {
									if ( StringUtils.contains(seriesModalities.toString(), modality) && (StringUtils.indexOfAny(seriesDescription, serieDescriptionArray)!=(-1)) ){
										asnwerSeries=queryRetrieve.retrieve(seriesDetails[k].getIdQuery(), seriesDetails[k].getAnswerNumber(),  destinationAet);
									}
									
								}
								else if (!filtreSerieModality) {
									if ( (StringUtils.indexOfAny(seriesDescription, serieDescriptionArray)!=(-1)) &&  (StringUtils.indexOfAny(seriesNumber, serieNumberArray)!=(-1)) ){
										asnwerSeries=queryRetrieve.retrieve(seriesDetails[k].getIdQuery(), seriesDetails[k].getAnswerNumber(),  destinationAet);
									}
									
								}
							}
						}
						//Si un seul filtre on retrieve la serie qui a matche
						else {
							asnwerSeries=queryRetrieve.retrieve(seriesDetails[k].getIdQuery(), seriesDetails[k].getAnswerNumber(),  destinationAet);
						}
						
					}
					//Si on a pas defini de contains ou de modalitie on telecharge tout ce qui n'est pas exclu
					else if ( StringUtils.isEmpty(seriesModalities.toString()) && StringUtils.isEmpty(serieDescriptionContains) && StringUtils.isEmpty(serieNumberMatch) ) {
						asnwerSeries=queryRetrieve.retrieve(seriesDetails[k].getIdQuery(), seriesDetails[k].getAnswerNumber(), destinationAet);
					}
				}
				
				if(asnwerSeries!=null) {
					//info.setText("Retrieve Serie "+(k+1)+"/"+(seriesDetails.length+1)+" Query "+(i+1)+"/"+table.getRowCount());
					answers.add(asnwerSeries);
					serieCountRevtrieved++;
				}
				
			}
		}
		
		
		return answers;
		
	}

}

