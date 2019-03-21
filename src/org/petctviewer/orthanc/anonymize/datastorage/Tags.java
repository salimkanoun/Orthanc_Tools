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

/**
 * Store Choice for each Tag for anonymization query construction
 *
 */

public class Tags {

	public enum Choice {
		REPLACE,
		KEEP,
		CLEAR
	}
	
	private String code;
	private Choice choice;
	private String replaceValue;
	
	public Tags(String code, Choice choice, String replaceValue){
		this.choice=choice;
		this.code = code;
		this.replaceValue=replaceValue;
	}

	public String getCode() {
		return code;
	}
	
	public Choice getChoice() {
		return choice;
	}
	
	public String getReplaceValue() {
		return replaceValue;
	}
	
}
