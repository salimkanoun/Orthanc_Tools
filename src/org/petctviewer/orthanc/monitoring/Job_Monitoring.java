package org.petctviewer.orthanc.monitoring;

import java.util.Timer;
import java.util.TimerTask;

import org.petctviewer.orthanc.setup.OrthancRestApis;

public class Job_Monitoring {
	
	private Timer timer;
	private OrthancRestApis orthancApis;

	private String speed;
	private int progress;
	
	
	public Job_Monitoring(OrthancRestApis orthancApis, String jobID) {
		
		this.orthancApis=orthancApis;
		
		
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				StringBuilder sb=orthancApis.makeGetConnectionAndStringBuilder("/jobs/"+jobID);
				
				
				
				
				

				
			}	
		};
		
        //running timer task as daemon thread
        timer = new Timer(true);
        //each 2 seconds
        timer.scheduleAtFixedRate(timerTask, 0, (2*1000) );
	}

}
