
package org.petctviewer.orthanc.query.autoquery.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import java.awt.GridLayout;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.event.ChangeListener;

import org.petctviewer.orthanc.anonymize.VueAnon;

import javax.swing.event.ChangeEvent;

@SuppressWarnings("serial")
public class AutoQueryOptions extends JDialog {

	private final JPanel contentPanel;
	private JSpinner spinnerDiscard;
	private JSpinner spinnerHour;
	private JSpinner spinnerMin;
	private JTextField serieDescriptionContains,serieDescriptionExclude, serieNumberExclude, serieNumberMatch;
	private JCheckBox chckbxCr ,chckbxCt,chckbxCmr,chckbxNm,chckbxPt,chckbxUs ,chckbxXa , chckbxMg, chckbxSeriesFilter;
	private List<JCheckBox> checkboxList;
	private int discard, hour, min;

	/**
	 * Create the dialog.
	 */
	
	public AutoQueryOptions() {
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel Title_panel = new JPanel();
		Title_panel.setBorder(new LineBorder(Color.RED, 2));
		contentPanel.add(Title_panel, BorderLayout.NORTH);
		JLabel lblAutoQueryOptions = new JLabel("Auto Query Options");
		Title_panel.add(lblAutoQueryOptions);

		
		JPanel Option_panel = new JPanel();
		contentPanel.add(Option_panel, BorderLayout.CENTER);
		Option_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel main_Panel_discard = new JPanel();
		Option_panel.add(main_Panel_discard);
		main_Panel_discard.setLayout(new BorderLayout(0, 0));

		JPanel panel_Discard = new JPanel();
		panel_Discard.setBorder(new LineBorder(new Color(0, 0, 0)));
		main_Panel_discard.add(panel_Discard, BorderLayout.NORTH);
		
		JLabel lblDiscardIfQuery = new JLabel("Discard if Study Query result size over");
		panel_Discard.add(lblDiscardIfQuery);

		spinnerDiscard = new JSpinner();
		panel_Discard.add(spinnerDiscard);
		spinnerDiscard.setModel(new SpinnerNumberModel(10, 0, 100, 1));

		JPanel Series_Filter = new JPanel();
		Series_Filter.setBorder(new LineBorder(new Color(0, 0, 0)));
		main_Panel_discard.add(Series_Filter, BorderLayout.CENTER);
		Series_Filter.setLayout(new BorderLayout(0, 0));
		
		chckbxSeriesFilter = new JCheckBox("Series Filter");
		chckbxSeriesFilter.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (chckbxSeriesFilter.isSelected()) activateSeriesFiler(true);
				else if (!chckbxSeriesFilter.isSelected()) activateSeriesFiler(false);
			}
		});
		Series_Filter.add(chckbxSeriesFilter, BorderLayout.NORTH);
		chckbxSeriesFilter.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel serieFilterPanel = new JPanel();
		Series_Filter.add(serieFilterPanel);
		serieFilterPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_serieDescription = new JPanel();
		serieFilterPanel.add(panel_serieDescription);
		
		JLabel lblSerieDescription = new JLabel("Serie Description : ");
		panel_serieDescription.add(lblSerieDescription);
	
		JLabel lblContains = new JLabel("Contains");
		panel_serieDescription.add(lblContains);
	
		serieDescriptionContains = new JTextField();
		serieDescriptionContains.setToolTipText("Split term with ; (example : \"dose report;scout\")");
		panel_serieDescription.add(serieDescriptionContains);
		serieDescriptionContains.setColumns(10);
	
		JLabel lblExclude = new JLabel("Exclude");
		panel_serieDescription.add(lblExclude);
	
		serieDescriptionExclude = new JTextField();
		serieDescriptionExclude.setToolTipText("Split term with ; (example : \"dose report;scout\")");
		panel_serieDescription.add(serieDescriptionExclude);
		serieDescriptionExclude.setColumns(10);

		JPanel panel_serieNumber = new JPanel();
		serieFilterPanel.add(panel_serieNumber);
		
		JLabel lblSerieNumber = new JLabel("Serie Number :");
		panel_serieNumber.add(lblSerieNumber);
	
		JLabel lblMatch = new JLabel("Match : ");
		panel_serieNumber.add(lblMatch);

		serieNumberMatch = new JTextField();
		serieNumberMatch.setToolTipText("Split term with ; (example : \"dose report;scout\")");
		panel_serieNumber.add(serieNumberMatch);
		serieNumberMatch.setColumns(10);

		JLabel lblExclude_1 = new JLabel("Exclude");
		panel_serieNumber.add(lblExclude_1);

		serieNumberExclude = new JTextField();
		serieNumberExclude.setToolTipText("Split term with ; (example : \"dose report;scout\")");
		panel_serieNumber.add(serieNumberExclude);
		serieNumberExclude.setColumns(10);

		JPanel panel_serieModality = new JPanel();
		serieFilterPanel.add(panel_serieModality);
		
		JLabel lblSerieModality = new JLabel("Serie Modality :");
		panel_serieModality.add(lblSerieModality);

		JPanel panel_modalities = new JPanel();
		panel_serieModality.add(panel_modalities);
		panel_modalities.setLayout(new GridLayout(0, 4, 0, 0));
		checkboxList=new ArrayList<JCheckBox>();

		chckbxCr = new JCheckBox("CR");
		chckbxCt = new JCheckBox("CT");
		chckbxCmr = new JCheckBox("CMR");
		chckbxNm = new JCheckBox("NM");
		chckbxPt = new JCheckBox("PT");
		chckbxUs = new JCheckBox("US");
		chckbxXa = new JCheckBox("XA");
		chckbxMg = new JCheckBox("MG");
		
		checkboxList.add(chckbxCr);
		checkboxList.add(chckbxCt);
		checkboxList.add(chckbxCmr);
		checkboxList.add(chckbxNm);
		checkboxList.add(chckbxPt);
		checkboxList.add(chckbxUs);
		checkboxList.add(chckbxXa);
		checkboxList.add(chckbxMg);
		
		panel_modalities.add(chckbxCr);
		panel_modalities.add(chckbxCt);
		panel_modalities.add(chckbxCmr);
		panel_modalities.add(chckbxNm);
		panel_modalities.add(chckbxPt);
		panel_modalities.add(chckbxUs);
		panel_modalities.add(chckbxXa);
		panel_modalities.add(chckbxMg);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Save in registery
				Preferences jPrefer=VueAnon.jprefer;
				jPrefer.putInt("AutoQuery_discard", getDiscard());
				jPrefer.putInt("AutoQuery_hour", getHour());
				jPrefer.putInt("AutoQuery_minutes", getMin());
				jPrefer.putBoolean("AutoQuery_useSeriesFilter", getUseSeriesFilter());
				jPrefer.put("AutoQuery_seriesDescriptionContains", serieDescriptionContains.getText());
				jPrefer.put("AutoQuery_seriesDescriptionExclude", serieDescriptionExclude.getText());
				jPrefer.put("AutoQuery_seriesNumberContains", serieNumberMatch.getText());
				jPrefer.put("AutoQuery_seriesNumberExclude", serieNumberExclude.getText());
				jPrefer.putBoolean("AutoQuery_useSeriesCRFilter", chckbxCr.isSelected());
				jPrefer.putBoolean("AutoQuery_useSeriesCTFilter", chckbxCt.isSelected());
				jPrefer.putBoolean("AutoQuery_useSeriesCMRFilter", chckbxCmr.isSelected());
				jPrefer.putBoolean("AutoQuery_useSeriesNMFilter", chckbxNm.isSelected());
				jPrefer.putBoolean("AutoQuery_useSeriesPTFilter", chckbxPt.isSelected());
				jPrefer.putBoolean("AutoQuery_useSeriesUSFilter", chckbxUs.isSelected());
				jPrefer.putBoolean("AutoQuery_useSeriesXAFilter", chckbxXa.isSelected());
				jPrefer.putBoolean("AutoQuery_useSeriesMGFilter", chckbxMg.isSelected());
				//dispose
				dispose();
			}
		});
		
			
		JPanel panel_schedule = new JPanel();
		buttonPane.add(panel_schedule);
		panel_schedule.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblScheduleTime = new JLabel("Schedule Time : ");
		panel_schedule.add(lblScheduleTime);
	
		JLabel lblHh = new JLabel("HH");
		panel_schedule.add(lblHh);
	
		spinnerHour = new JSpinner();
		spinnerHour.setModel(new SpinnerNumberModel(22, 0, 23, 1));
		panel_schedule.add(spinnerHour);

		JLabel lblMm = new JLabel("mm");
		panel_schedule.add(lblMm);
	
		spinnerMin = new JSpinner();
		spinnerMin.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		panel_schedule.add(spinnerMin);
		
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
		setSize(getPreferredSize());
		setOptionValuefromRegistery();
	}
	
	private void setOptionValuefromRegistery() {
		Preferences jPrefer = VueAnon.jprefer;
		discard=jPrefer.getInt("AutoQuery_discard", 10);
		spinnerDiscard.setValue(discard);
		hour=jPrefer.getInt("AutoQuery_hour", 22);
		spinnerHour.setValue(hour);
		min=jPrefer.getInt("AutoQuery_minutes", 00);
		spinnerMin.setValue(min);
		
		//serie filter
		chckbxSeriesFilter.setSelected(jPrefer.getBoolean("AutoQuery_useSeriesFilter", false));
		activateSeriesFiler(chckbxSeriesFilter.isSelected());
		serieDescriptionContains.setText(jPrefer.get("AutoQuery_seriesDescriptionContains", ""));
		serieDescriptionExclude.setText(jPrefer.get("AutoQuery_seriesDescriptionExclude", ""));
		serieNumberMatch.setText(jPrefer.get("AutoQuery_seriesNumberContains", ""));
		serieNumberExclude.setText(jPrefer.get("AutoQuery_seriesNumberExclude", ""));
		
		chckbxCr.setSelected(jPrefer.getBoolean("AutoQuery_useSeriesCRFilter", false));
		chckbxCt.setSelected(jPrefer.getBoolean("AutoQuery_useSeriesCTFilter", false));
		chckbxCmr.setSelected(jPrefer.getBoolean("AutoQuery_useSeriesCMRFilter", false));
		chckbxNm.setSelected(jPrefer.getBoolean("AutoQuery_useSeriesNMFilter", false));
		chckbxPt.setSelected(jPrefer.getBoolean("AutoQuery_useSeriesPTFilter", false));
		chckbxUs.setSelected(jPrefer.getBoolean("AutoQuery_useSeriesUSFilter", false));
		chckbxXa.setSelected(jPrefer.getBoolean("AutoQuery_useSeriesXAFilter", false));
		chckbxMg.setSelected(jPrefer.getBoolean("AutoQuery_useSeriesMGFilter", false));	
	}
	
	protected void activateSeriesFiler(boolean activate) {
		serieDescriptionContains.setEnabled(activate);
		serieDescriptionExclude.setEnabled(activate);
		serieNumberExclude.setEnabled(activate);
		serieNumberMatch.setEnabled(activate);
		chckbxCr.setEnabled(activate);
		chckbxCt.setEnabled(activate);
		chckbxCmr.setEnabled(activate);
		chckbxNm.setEnabled(activate);
		chckbxPt.setEnabled(activate);
		chckbxUs.setEnabled(activate);
		chckbxXa.setEnabled(activate);
		chckbxMg.setEnabled(activate);
		
	}

	public int getDiscard() {
		return (int) spinnerDiscard.getValue();
	}
	
	public int getHour() {
		return (int) spinnerHour.getValue();
	}
	
	public int getMin() {
		return (int) spinnerMin.getValue();
	}
	
	public boolean getUseSeriesFilter() {
		return chckbxSeriesFilter.isSelected();
	}
	
	public String getSerieDescriptionContains() {
		return serieDescriptionContains.getText().toLowerCase();
	}
	
	public String getSerieDescriptionExclude() {
		return serieDescriptionExclude.getText().toLowerCase();
	}
	
	public String getSerieNumberContains() {
		return serieNumberMatch.getText();
	}
	
	public String getSerieNumberExclude() {
		return serieNumberExclude.getText();
	}
	
	public List<JCheckBox> getSeriesModalities(){
		return checkboxList;
	}

}
