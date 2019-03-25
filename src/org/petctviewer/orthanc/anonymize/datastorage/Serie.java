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

package org.petctviewer.orthanc.anonymize.datastorage;

import java.util.ArrayList;

public class Serie {
	
	private String serieDescription;
	private String modality;
	private int nbInstances;
	private String firstInstanceId;
	private String studyOrthancId;
	private String id;
	private String seriesNumber;
	private String sopClassUid;
	private boolean secondaryCapture;
	
	public Serie(String serieDescription, String modality, int nbInstances, 
			String id, String studyOrthancId, String firstInstanceId, String seriesNumber, String sopClassUid){
		this.serieDescription = serieDescription;
		this.modality = modality;
		this.nbInstances = nbInstances;
		this.id = id;
		this.studyOrthancId = studyOrthancId;
		this.firstInstanceId = firstInstanceId;
		this.sopClassUid = sopClassUid;
		this.seriesNumber=seriesNumber;
		checkIsSecondaryCapture();
	}
	
	private void checkIsSecondaryCapture() {
		ArrayList<String> sopClassUIDs = new ArrayList<String>();
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.1");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.2");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.3");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.7.4");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.11");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.22");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.33");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.40");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.50");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.59");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.65");
		sopClassUIDs.add("1.2.840.10008.5.1.4.1.1.88.67");

		if(sopClassUIDs.contains(sopClassUid)){
			secondaryCapture=true;
		}else {
			secondaryCapture=false;
		}
		
	}
	public String getSerieDescription(){
		return serieDescription;
	}
	
	public void setSerieDescription(String serieDescription){
		this.serieDescription = serieDescription;
	}

	public String getId() {
		return id;
	}

	public String getModality() {
		return modality;
	}

	public int getNbInstances() {
		return nbInstances;
	}

	public String getFistInstanceId() {
		return firstInstanceId;
	}

	public String getStudyOrthancId() {
		return studyOrthancId;
	}

	public boolean isSecondaryCapture() {
		return secondaryCapture;
	}
	
	public String getSeriesNumber(){
		return this.seriesNumber;
	}
	
}
