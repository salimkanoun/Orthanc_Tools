package org.petctviewer.orthanc.OTP.standalone;

import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.petctviewer.orthanc.anonymize.QueryOrthancData;
import org.petctviewer.orthanc.anonymize.datastorage.Serie;
import org.petctviewer.orthanc.anonymize.datastorage.Study2;
import org.petctviewer.orthanc.setup.OrthancRestApis;

public class TableOTPSeriesModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;

	private String[] entetes = {"Serie Description", "Modality", "Serie Number", "Number of Instances","isSC", "OrthancSerieID", "Serie Object"};
	private Class<?>[] typeEntetes = {String.class, String.class, String.class, Integer.class, Boolean.class, String.class, Serie.class};

	private OTP_Tab otpTab;
	private OrthancRestApis connexionHttp;
	private Study2 currentStudy;
	private QueryOrthancData queryOrthanc;
	
	public TableOTPSeriesModel(OTP_Tab otpTab, OrthancRestApis connexionHttp, QueryOrthancData queryOrthanc){
		super(0,7);
		this.otpTab=otpTab;
		this.connexionHttp=connexionHttp;
		this.queryOrthanc=queryOrthanc;
	}
	
	@Override
	public String getColumnName(int columnIndex){
		return entetes[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return typeEntetes[columnIndex];
	}

	public boolean isCellEditable(int row, int col){
		if(col == 0){
			return true; 
		}
		return false;
	}
	
	public void removeAllSecondaryCaptures() {
		SwingWorker<Void,Void> worker=new SwingWorker<Void,Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				for(int i=0; i<getRowCount(); i++) {
					if ((Boolean) getValueAt(i, 3)) {
						otpTab.getStatusOTP().setText("Deleting SC "+getValueAt(i, 0));
						connexionHttp.makeDeleteConnection("/series/"+getValueAt(i, 5));
					}
				}
				return null;
				
			}
			
			@Override
			protected void done() {
				refresh();
				otpTab.getStatusOTP().setText("Sec Capture Deletion Done");
			}
			
		};
		
		worker.execute();
		
	}
	
	public void clear(){
		this.setRowCount(0);
	}
	
	public void addSerie(Study2 study) {
		this.currentStudy=study;
		study.refreshChildSeries(queryOrthanc);
		clear();
		for(Serie serie:study.getSeries()) {
			this.addRow(new Object[] {serie.getSerieDescription(), 
				serie.getModality(), 
				serie.getSeriesNumber(),
				serie.getNbInstances(), 
				serie.isSecondaryCapture(), 
				serie.getId(), 
				serie});
			
		}

	}
	
	public void refresh() {
		addSerie(currentStudy);
	}
}
