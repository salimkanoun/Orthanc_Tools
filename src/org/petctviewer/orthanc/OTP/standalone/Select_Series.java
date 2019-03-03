package org.petctviewer.orthanc.OTP.standalone;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.petctviewer.orthanc.anonymize.TableSeriesModel;
import org.petctviewer.orthanc.setup.ParametreConnexionHttp;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Select_Series extends JDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the dialog.
	 */
	public Select_Series(ParametreConnexionHttp connexionHttp, String studyUID) {
		TableSeriesModel tableSeriesModel=new TableSeriesModel(connexionHttp);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		tableSeriesModel.addSerie(studyUID);
		tableSeriesModel.detectAllSecondaryCaptures();
		JTable tableSeries = new JTable(tableSeriesModel);
		tableSeries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tableSeries.getColumnModel().getColumn(3).setMinWidth(0);;
		tableSeries.getColumnModel().getColumn(4).setMinWidth(0);
		tableSeries.getColumnModel().getColumn(3).setMaxWidth(0);
		tableSeries.getColumnModel().getColumn(4).setMaxWidth(0);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane);
		scrollPane.setViewportView(tableSeries);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				{
					JButton btnDeleteSelected = new JButton("Delete Selected");
					btnDeleteSelected.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							int[] selectedRows=tableSeries.getSelectedRows();
							for(int i=0; i<selectedRows.length; i++) {
								connexionHttp.makeDeleteConnection("/series/"+tableSeries.getValueAt(selectedRows[i], 4));
								
							}
							tableSeriesModel.clear();
							tableSeriesModel.addSerie(studyUID);
							
						}
					});
					buttonPane.add(btnDeleteSelected);
				}
				closeButton.setActionCommand("Close");
				buttonPane.add(closeButton);
			}
		}
	}

}
