package org.petctviewer.orthanc.query;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class Retrieve_Action extends AbstractAction{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/*
	private class RetrieveAction extends AbstractAction{

		private static final long serialVersionUID = 1L;
		private ArrayList<Integer> rowsModelsIndexes;
		private JTable tableauDetails;
		private ModelTableSeries modeleDetails;
		private JLabel state;
		private JComboBox<String> retrieveAET;

		public RetrieveAction( JTable tableauDetails, 
				ModelTableSeries modeleDetails, JLabel state, JComboBox<String> retrieveAET){
			super("Retrieve");
			this.rowsModelsIndexes = rowsModelsIndexes;
			this.tableauDetails = tableauDetails;
			this.modeleDetails = modeleDetails;
			this.state = state;
			this.retrieveAET = retrieveAET;
		}


		@Override
		public void actionPerformed(ActionEvent arg0) {
			SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					working=true;
					retrieve.setEnabled(false);
					retrieveH.setEnabled(false);
					try {
						if(rowsModelsIndexes.size() == 0){
							// If whole studies/study were/was selected
							DateFormat df = new SimpleDateFormat("yyyyMMdd");
							int numPatient=0;
							for(Integer row : tablePatients.getSelectedRows()){
								numPatient++;
								modeleDetails.clear();
								Date date = (Date)tablePatients.getValueAt(row, 2);
								String patientName = (String)tablePatients.getValueAt(row, 0);
								String patientID = (String)tablePatients.getValueAt(row, 1);
								String studyDate = df.format(date); 
								String studyDescription = (String)tablePatients.getValueAt(row, 3);
								String accessionNumber = (String)tablePatients.getValueAt(row, 4);
								String studyInstanceUID = (String)tablePatients.getValueAt(row, 5);

								modeleDetails.addDetails(studyInstanceUID, queryAET.getSelectedItem().toString());
								for(int i = 0; i < tableauDetails.getRowCount(); i++){
									state.setText("<html>Patient " + (numPatient) + "/" + tablePatients.getSelectedColumnCount() + " - Retrieve state  " + (i+1) + "/" + tableauDetails.getRowCount() + 
											" <font color='red'> (Do not touch any buttons or any tables while the retrieve is not done)</font></html>");
									modeleDetails.retrieve(modeleDetails.getQueryID(i), i, 
											retrieveAET.getSelectedItem().toString());
								}
							}
							tablePatients.setRowSelectionInterval(0,0);
						}else{
							// If only series were selected
							int i = 0;
							for(int j : rowsModelsIndexes){
								state.setText("<html>Retrieve state  " + (i+1) + "/" + rowsModelsIndexes.size()  + 
										" <font color='red'>(Do not touch any buttons or any tables while the retrieve is not done)</font></html>");
								modeleDetails.retrieve(modeleDetails.getQueryID(j), j, 
										retrieveAET.getSelectedItem().toString());
							}
							i++;
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}catch (Exception e){
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void done(){
					retrieve.setEnabled(true);
					retrieveH.setEnabled(true);
					state.setText("<html><font color='green'>The data have successfully been retrieved.</font></html>");
					working=false;
				}
				
			};
			worker.execute();*/
		//}
		
	//}
}
