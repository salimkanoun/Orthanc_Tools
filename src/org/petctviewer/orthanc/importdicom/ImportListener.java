package org.petctviewer.orthanc.importdicom;

import java.util.HashMap;

public interface ImportListener {
	
	public abstract void ImportFinished(HashMap<String, HashMap<String,String>> importedStudy);

}
