package org.petctviewer.orthanc.reader;

import java.util.ArrayList;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

public class Run_Pet_Ct {
	
	
	private Run_Pet_Ct(ArrayList<ImagePlus> imagePlusList) {
		String seriesUIDs = ChoosePetCt.buildSeriesUIDs(imagePlusList);
		if( seriesUIDs == null) return;
		if( seriesUIDs.startsWith("2CTs")) seriesUIDs = "";
		IJ.runPlugIn("Pet_Ct_Viewer", seriesUIDs);
		wait4bkgd();
	}
	
	private void wait4bkgd() {
		Integer i = 0, j;
		while( ChoosePetCt.loadingData == 1 || ChoosePetCt.loadingData == 3) {
			mySleep(200);
			i++;
			if( (i % 20) == 0 && ChoosePetCt.loadingData == 1) {
				ImageJ ij = IJ.getInstance();
				if( ij != null) ij.toFront();
				j = i/5;
				IJ.showStatus("Loading data, please wait " + j.toString());
			}
		}
	}
	
	private void mySleep(int msec) {
		try {
			Thread.sleep(msec);
		} catch (Exception e) { e.printStackTrace();}
	}

}
