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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CSV {

	private StringBuilder content;
	private CSVPrinter csv;
	private DateFormat df=new SimpleDateFormat("MMddyyyy");
	
	public CSV(){
		content = new StringBuilder();
		
		try {
			csv=new CSVPrinter(content, CSVFormat.DEFAULT);
			csv.printRecord(new Object[] {"Old patient name", "Old patient id", "New patient name",
					"New patient id", "Old study date", "Old study description", "New study description", 
					"Nb series", "Nb instances", "Size" , "Study instance uid"});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addStudy(String oldPatientName, String oldPatientId, String newPatientName, String newPatientId,
			Date oldStudyDate, String oldStudyDesc, String newStudyDesc, int nbSeries, int nbInstances, 
			int size, String studyInstanceUid){
		
		try {
			csv.printRecord(new Object[] {oldPatientName, oldPatientId, newPatientName, newPatientId,
					df.format(oldStudyDate), oldStudyDesc, newStudyDesc, nbSeries, nbInstances, 
					size,  studyInstanceUid});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getCsv() {
		return content.toString();
		
	}
	

}
