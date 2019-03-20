package org.petctviewer.orthanc.anonymize.datastorage;

public class Study_Anonymized {
	
	private Study2 anonymizedStudy, originalStudy;

	public Study_Anonymized(Study2 anonymizedStudy, Study2 originalStudy) {
		this.anonymizedStudy=anonymizedStudy;
		this.originalStudy=originalStudy;
	}
	
	public Study2 getAnonymizedStudy() {
		return anonymizedStudy;
	}
	
	public Study2 getOriginalStudy() {
		return originalStudy;
	}
	
}
