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

package org.petctviewer.orthanc.query;

public class SeriesDetails {
	private String modality;
	private String seriesInstanceUID;
	private String seriesDescription;
	private String serieNumber;

	public SeriesDetails(String seriesDescription, String modality, String seriesInstanceUID, String serieNumber) {
		this.modality = modality;
		this.seriesInstanceUID = seriesInstanceUID;
		this.seriesDescription = seriesDescription;
		this.serieNumber=serieNumber;
	}

	public String getModality() {
		return this.modality;
	}

	public String getSeriesDescription() {
		return seriesDescription;
	}

	public String getSeriesInstanceUID() {
		return this.seriesInstanceUID;
	}

	public String getSeriesNumber() {
		return this.serieNumber;
	}
	
}