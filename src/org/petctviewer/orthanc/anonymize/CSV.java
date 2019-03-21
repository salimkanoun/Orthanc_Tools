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

import java.text.SimpleDateFormat;
import java.util.Date;

public class CSV {

	private StringBuilder content;
	
	public CSV(){
		content = new StringBuilder();
		content.append("Old patient name");
		content.append(',');
		content.append("Old patient id");
		content.append(',');
		content.append("New patient name");
		content.append(',');
		content.append("New patient id");
		content.append(',');
		content.append("Old study date");
		content.append(',');
		content.append("Old study description");
		content.append(',');
		content.append("New study description");	
		content.append(',');
		content.append("Nb series");
		content.append(',');
		content.append("Nb instances");
		content.append(',');
		content.append("Size");
		content.append(',');
		content.append("Study instance uid");
		content.append('\n');
	}

	public void addStudy(String oldPatientName, String oldPatientId, String newPatientName, String newPatientId,
			Date oldStudyDate, String oldStudyDesc, String newStudyDesc, int nbSeries, int nbInstances, 
			int size, String studyInstanceUid){
		SimpleDateFormat df=new SimpleDateFormat("MMddyyyy");
		content.append(oldPatientName);
		content.append(',');
		content.append(oldPatientId);
		content.append(',');
		content.append(newPatientName);
		content.append(',');
		content.append(newPatientId);
		content.append(',');
		content.append(df.format(oldStudyDate));
		content.append(',');
		content.append(oldStudyDesc);
		content.append(',');
		content.append(newStudyDesc);
		content.append(',');
		content.append(nbSeries);
		content.append(',');
		content.append(nbInstances);
		content.append(',');
		content.append(size);
		content.append(',');
		content.append(studyInstanceUid);
		content.append('\n');
	}
	
	public String getCsv() {
		return content.toString();
	}
	

}
