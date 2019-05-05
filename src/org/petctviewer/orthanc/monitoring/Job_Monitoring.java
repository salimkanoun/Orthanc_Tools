package org.petctviewer.orthanc.monitoring;

import java.util.Timer;
import java.util.TimerTask;

import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Job_Monitoring {
	
	private Timer timer;

	private int speed;
	private int progress;
	private String state;
	private String errorDescription;
	private JsonParser parser;
	
	/**
	 * Monitor a job (defined by its ID), store the progress/status data in it
	 * Once job finish the monitoring thread is automatically stoped
	 * @param orthancApis
	 * @param jobID
	 */
	public Job_Monitoring(OrthancRestApis orthancApis, String jobID) {
		
		parser=new JsonParser();
		speed=0;
		state="";
		
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				StringBuilder sb=orthancApis.makeGetConnectionAndStringBuilder("/jobs/"+jobID);
				JsonObject job=parser.parse(sb.toString()).getAsJsonObject();
				state=job.get("State").getAsString();
				progress=job.get("Progress").getAsInt();
				
				JsonObject content=job.getAsJsonObject("Content").getAsJsonObject();
				if(content.has("NetworkSpeedKBs")) {
					speed=content.get("NetworkSpeedKBs").getAsInt();
				}
				
				if(state.equals("Success")) {
					stopJobMonitoring();
				}
				
			}	
		};
		
        //running timer task as daemon thread
        timer = new Timer(true);
        //each 2 seconds
        timer.scheduleAtFixedRate(timerTask, 0, (1000) );
	}
	
	public void stopJobMonitoring() {
		timer.cancel();
	}
	
	public boolean isRunning() {
		if(state.equals("Success") || state.equals("Failure")) {
			return false;
		}else{
			return true;
		}
	}
	
	public String getState() {
		return this.state;
	}
	
	public String getErrorDesc() {
		return this.errorDescription;
	}
	
	public int getSpeed() {
		return this.speed;
	}
	
	public int getProgress() {
		return progress;
	}

}
