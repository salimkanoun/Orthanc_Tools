package org.petctviewer.orthanc.monitoring;
/**
 * Defini une condition d'autoRouting
 * @author kanoun_s
 *
 */
public class AutoRouting_Condition {
	
	//Destination AET
	private String destinationAET;
	
	//Conditions SK Peut etre mettre dans une HashMap
	private String modalities;
	private String seriesDescription;
	private String studyDescritions;
	private String dateFrom;
	private String dateTo;
	
	
	
	public AutoRouting_Condition(String destinationAET) {
		this.destinationAET=destinationAET;
	}
	
	
	
	

}
