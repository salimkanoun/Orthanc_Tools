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

package org.petctviewer.query;

import java.text.ParseException;

public class Details {
	private String modality;
	private String seriesInstanceUID;
	private String seriesDescription;

	public Details(String seriesDescription, String modality, String seriesInstanceUID) throws ParseException {
		super();

		this.modality = modality;
		this.seriesInstanceUID = seriesInstanceUID;
		this.seriesDescription = seriesDescription;
	}

	public String getModality() {
		return this.modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getSeriesDescription() {
		return seriesDescription;
	}

	public void setSeriesDescription(String seriesDescription) {
		this.seriesDescription = seriesDescription;
	}

	public String getSeriesInstanceUID() {
		return this.seriesInstanceUID;
	}

	public void setSeriesInstanceUID(String seriesInstanceUID) {
		this.seriesInstanceUID = seriesInstanceUID;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Details other = (Details) obj;
		if (modality == null) {
			if (other.modality != null)
				return false;
		} else if (!modality.equals(other.modality))
			return false;
		if (seriesDescription == null) {
			if (other.seriesDescription != null)
				return false;
		} else if (!seriesDescription.equals(other.seriesDescription == null))
			return false;
		if (seriesInstanceUID == null) {
			if (other.seriesInstanceUID != null)
				return false;
		} else if (!seriesInstanceUID.equals(other.seriesInstanceUID == null))
			return false;
		Details details = (Details) obj;
		return this.getModality().equals(details.getModality()) &&
				this.getSeriesDescription().equals(details.getSeriesDescription()) &&
				this.getSeriesInstanceUID().equals(details.getSeriesInstanceUID());
	}
	
}