/**
Copyright (C) 2017 VONGSALAT Anousone

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

package org.petctviewer.anonymize;

public class Serie {
	private String serieDescription;
	private String modality;
	private String nbInstances;
	private String instance;
	private String study;
	private String id;
	private String seriesNumber;
	private boolean secondaryCapture;
	
	public Serie(String serieDescription, String modality, String nbInstances, 
			String id, String study, String instance, String seriesNumber, boolean secondaryCapture){
		this.serieDescription = serieDescription;
		this.modality = modality;
		this.nbInstances = nbInstances;
		this.id = id;
		this.study = study;
		this.instance = instance;
		this.secondaryCapture = secondaryCapture;
		this.seriesNumber=seriesNumber;
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

	public void setId(String id) {
		this.id = id;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getNbInstances() {
		return nbInstances;
	}

	public void setNbInstances(String nbInstances) {
		this.nbInstances = nbInstances;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getStudy() {
		return study;
	}

	public void setStudy(String study) {
		this.study = study;
	}
	
	public boolean isSecondaryCapture() {
		return secondaryCapture;
	}

	public void setSecondaryCapture(boolean secondaryCapture) {
		this.secondaryCapture = secondaryCapture;
	}
	
	public String getSeriesNumber(){
		return this.seriesNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((serieDescription == null) ? 0 : serieDescription.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Serie other = (Serie) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (serieDescription == null) {
			if (other.serieDescription != null)
				return false;
		} else if (!serieDescription.equals(other.serieDescription))
			return false;
		if (modality == null) {
			if (other.modality != null)
				return false;
		} else if (!modality.equals(other.modality))
			return false;
		if (nbInstances == null) {
			if (other.nbInstances != null)
				return false;
		} else if (!nbInstances.equals(other.nbInstances))
			return false;
		if (study == null) {
			if (other.study != null)
				return false;
		} else if (!study.equals(other.study))
			return false;
		Serie s = (Serie) obj;
		return this.getSerieDescription().equals(s.getSerieDescription())&&
				this.getModality().equals(s.getModality()) &&
				this.getNbInstances().equals(s.getNbInstances()) &&
				this.getStudy().equals(s.getStudy()) &&
				this.getId().equals(s.getId());
	}
	
}
