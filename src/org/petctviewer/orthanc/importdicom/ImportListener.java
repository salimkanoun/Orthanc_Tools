package org.petctviewer.orthanc.importdicom;

import java.util.HashMap;

import org.petctviewer.orthanc.anonymize.datastorage.Study2;

public interface ImportListener {
	
	public abstract void ImportFinished(HashMap<String, Study2> importedStudy);

}
