package org.petctviewer.query;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class AutoQueryResultTableDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private ArrayList<Patient> patientList;
	boolean validate;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AutoQueryResultTableDialog dialog = new AutoQueryResultTableDialog(new ArrayList<Patient> ());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setSize(dialog.getPreferredSize());
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AutoQueryResultTableDialog(ArrayList<Patient> patientList) {
		this.patientList= patientList;
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.setPreferredScrollableViewportSize(new Dimension(850, 250));
				table.putClientProperty("terminateEditOnFocusLost", true);
				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				table.setModel(new DefaultTableModel(new Object[] {"Last Name", "First Name", "ID", "Accession Nb", "Study Date From","Study Date To", "Modality", "Study Description" },0));
				table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				table.setAutoCreateRowSorter(true);
				scrollPane.setViewportView(table);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Import to retrieve list");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						validate=true;
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton btnAddPatient = new JButton("Add Patient");
				btnAddPatient.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					model.addRow(new Object[] {"*", "*", "*", "*","*", "*", "*", "*" });
					}
				});
				buttonPane.add(btnAddPatient);
			}
			{
				JButton btnRemove = new JButton("Remove");
				btnRemove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (table.getSelectedRow()!=-1) {
							DefaultTableModel model = (DefaultTableModel) table.getModel();
							int[] rows = table.getSelectedRows();
							   for(int i=0;i<rows.length;i++){
								   model.removeRow(rows[i]-i);
							   }
						}
					}
				});
				buttonPane.add(btnRemove);
			}
			{
				JButton btnExportToCsv = new JButton("Export to CSV");
				btnExportToCsv.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser=new JFileChooser();
						fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						fileChooser.setSelectedFile(new File("results.csv"));
						int ouvrir=fileChooser.showSaveDialog(null);
						if(ouvrir==JFileChooser.APPROVE_OPTION) {
						//String Builder ou sera sauve le contenu du CSV
						StringBuilder csv=new StringBuilder();
						csv.append("last name"+","+"first name"+","+"id"+","+"accession"+","+"date From"+","+"date To"+","+"modality"+","+"studyDescription"+"\n");
						for (int i=0; i<table.getRowCount(); i++) {
							buildCSV(table.getValueAt(i, 0).toString(), table.getValueAt(i, 1).toString(), table.getValueAt(i, 2).toString(), table.getValueAt(i, 3).toString(), table.getValueAt(i, 4).toString(),table.getValueAt(i, 5).toString(), table.getValueAt(i, 6).toString(), table.getValueAt(i, 7).toString(), csv);
							
						}
						//On ecrit le CSV
						writeCSV(csv, fileChooser.getSelectedFile().getAbsolutePath().toString());
					
						
						
					
				}
				}
				});
				buttonPane.add(btnExportToCsv);
			}
			{
				JButton btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					dispose();
					}
				});
				buttonPane.add(btnCancel);
			}
		}
		this.setSize(this.getPreferredSize());
	}
	/**
	 * Ajoute les patients de l'array dans la table qu'on construit
	 */
	public void populateTable() {
		DateFormat parser = new SimpleDateFormat("yyyyMMdd");
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i=0; i<patientList.size(); i++) {
			String name=patientList.get(i).getPatientName();
			int separator=name.indexOf("^");
			String lastName=null;
			String firstName=null;
			if (separator!=-1) {
				lastName=name.substring(0, separator);
				firstName=name.substring(separator+1);
			}
			else   {
				lastName=name;
				firstName="";
			}
			
			//On passe le  \ en \\
			String modalityString=patientList.get(i).getModality();
			if (modalityString.contains("\\")) modalityString = modalityString.replace("\\", "\\\\");
			// on ajoute la ligne
			model.addRow(new Object[] {lastName, firstName, patientList.get(i).getPatientID(), patientList.get(i).getAccessionNumber(),parser.format(patientList.get(i).getStudyDate()), parser.format(patientList.get(i).getStudyDate()), modalityString, patientList.get(i).getStudyDescription() });
			}
	}
	
	/**
	 * populate the csv string builder for each result
	 * @param name
	 * @param id
	 * @param accession
	 * @param date
	 * @param modality
	 * @param studyDescription
	 * @param csv
	 */
	private void buildCSV(String nom, String prenom, String id, String accession, String dateFrom, String dateTo, String modality, String studyDescription, StringBuilder csv) {
		/*String nom;
		String prenom;
		//On split le name
		int separateur=name.indexOf("^");
		if (separateur!=-1) {
		nom=name.substring(0, separateur);
		prenom=name.substring(separateur+1, name.length());
		}
		else {
			nom=name;
			prenom=null;
		}*/
		
		csv.append(nom+","+prenom+","+id+","+accession+","+dateFrom+","+dateTo+","+modality+","+studyDescription+"\n");
	}
	
	protected void writeCSV(StringBuilder csv, String path) {
		File f = new File(path);

		// On ecrit les CSV
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(f);
			pw.write(csv.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}
	}
	
	protected JTable getTable() {
		return this.table;
	}
	
	protected boolean isValidate() {
		return validate;
	}

}
