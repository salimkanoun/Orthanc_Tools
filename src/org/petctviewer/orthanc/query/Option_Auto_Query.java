
package org.petctviewer.orthanc.query;

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
import javax.swing.event.ChangeEvent;

@SuppressWarnings("serial")
public class Option_Auto_Query extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JSpinner spinnerDiscard;
	private JSpinner spinnerHour;
	private JSpinner spinnerMin;
	private JTextField serieDescriptionContains,serieDescriptionExclude, serieNumberExclude, serieNumberMatch;
	private JCheckBox chckbxCr ,chckbxCt,chckbxCmr,chckbxNm,chckbxPt,chckbxUs ,chckbxXa , chckbxMg, chckbxSeriesFilter;
	private List<JCheckBox> checkboxList=new ArrayList<JCheckBox>();
	private int discard, hour, min;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Option_Auto_Query dialog = new Option_Auto_Query();
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Option_Auto_Query() {
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel Title_panel = new JPanel();
			Title_panel.setBorder(new LineBorder(Color.RED, 2));
			contentPanel.add(Title_panel, BorderLayout.NORTH);
			{
				JLabel lblAutoQueryOptions = new JLabel("Auto Query Options");
				Title_panel.add(lblAutoQueryOptions);
			}
		}
		{
			JPanel Option_panel = new JPanel();
			contentPanel.add(Option_panel, BorderLayout.CENTER);
			Option_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JPanel panel_discard = new JPanel();
				Option_panel.add(panel_discard);
				panel_discard.setLayout(new BorderLayout(0, 0));
				{
					JPanel panel = new JPanel();
					panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
					panel_discard.add(panel);
					panel.setLayout(new BorderLayout(0, 0));
					{
						JPanel panel_Discard = new JPanel();
						panel_Discard.setBorder(new LineBorder(new Color(0, 0, 0)));
						panel.add(panel_Discard, BorderLayout.NORTH);
						{
							JLabel lblDiscardIfQuery = new JLabel("Discard if Study Query result size over");
							panel_Discard.add(lblDiscardIfQuery);
						}
						{
							spinnerDiscard = new JSpinner();
							panel_Discard.add(spinnerDiscard);
							spinnerDiscard.setModel(new SpinnerNumberModel(10, 0, 100, 1));
							
						}
					}
					{
						JPanel Series_Filter = new JPanel();
						Series_Filter.setBorder(new LineBorder(new Color(0, 0, 0)));
						panel.add(Series_Filter, BorderLayout.CENTER);
						Series_Filter.setLayout(new BorderLayout(0, 0));
						{
							chckbxSeriesFilter = new JCheckBox("Series Filter");
							chckbxSeriesFilter.addChangeListener(new ChangeListener() {
								public void stateChanged(ChangeEvent arg0) {
									if (chckbxSeriesFilter.isSelected()) unactivateSeriesFiler(false);
									else if (!chckbxSeriesFilter.isSelected()) unactivateSeriesFiler(true);
								}
							});
							Series_Filter.add(chckbxSeriesFilter, BorderLayout.NORTH);
							chckbxSeriesFilter.setHorizontalAlignment(SwingConstants.CENTER);
						}
						{
							JPanel panel_1_1 = new JPanel();
							Series_Filter.add(panel_1_1);
							panel_1_1.setLayout(new GridLayout(0, 1, 0, 0));
							{
								JPanel panel_serieDescription = new JPanel();
								panel_1_1.add(panel_serieDescription);
								{
									JLabel lblSerieDescription = new JLabel("Serie Description : ");
									panel_serieDescription.add(lblSerieDescription);
								}
								{
									JLabel lblContains = new JLabel("Contains");
									panel_serieDescription.add(lblContains);
								}
								{
									serieDescriptionContains = new JTextField();
									serieDescriptionContains.setToolTipText("Split term with ; (example : \"dose report;scout\")");
									serieDescriptionContains.setEnabled(false);
									panel_serieDescription.add(serieDescriptionContains);
									serieDescriptionContains.setColumns(10);
								}
								{
									JLabel lblExclude = new JLabel("Exclude");
									panel_serieDescription.add(lblExclude);
								}
								{
									serieDescriptionExclude = new JTextField();
									serieDescriptionExclude.setToolTipText("Split term with ; (example : \"dose report;scout\")");
									serieDescriptionExclude.setEnabled(false);
									panel_serieDescription.add(serieDescriptionExclude);
									serieDescriptionExclude.setColumns(10);
								}
							}
							{
								JPanel panel_serieNumber = new JPanel();
								panel_1_1.add(panel_serieNumber);
								{
									JLabel lblSerieNumber = new JLabel("Serie Number :");
									panel_serieNumber.add(lblSerieNumber);
								}
								{
									JLabel lblMatch = new JLabel("Match : ");
									panel_serieNumber.add(lblMatch);
								}
								{
									serieNumberMatch = new JTextField();
									serieNumberMatch.setToolTipText("Split term with ; (example : \"dose report;scout\")");
									serieNumberMatch.setEnabled(false);
									panel_serieNumber.add(serieNumberMatch);
									serieNumberMatch.setColumns(10);
								}
								{
									JLabel lblExclude_1 = new JLabel("Exclude");
									panel_serieNumber.add(lblExclude_1);
								}
								{
									serieNumberExclude = new JTextField();
									serieNumberExclude.setToolTipText("Split term with ; (example : \"dose report;scout\")");
									serieNumberExclude.setEnabled(false);
									panel_serieNumber.add(serieNumberExclude);
									serieNumberExclude.setColumns(10);
								}
							}
							{
								JPanel panel_serieModality = new JPanel();
								panel_1_1.add(panel_serieModality);
								{
									JLabel lblSerieModality = new JLabel("Serie Modality :");
									panel_serieModality.add(lblSerieModality);
								}
								{
									JPanel panel_modalities = new JPanel();
									panel_serieModality.add(panel_modalities);
									panel_modalities.setLayout(new GridLayout(0, 4, 0, 0));
									{
										chckbxCr = new JCheckBox("CR");
										chckbxCr.setEnabled(false);
										panel_modalities.add(chckbxCr);
										checkboxList.add(chckbxCr);
									}
									{
										chckbxCt = new JCheckBox("CT");
										chckbxCt.setEnabled(false);
										panel_modalities.add(chckbxCt);
										checkboxList.add(chckbxCt);
									}
									{
										chckbxCmr = new JCheckBox("CMR");
										chckbxCmr.setEnabled(false);
										panel_modalities.add(chckbxCmr);
										checkboxList.add(chckbxCmr);
									}
									{
										chckbxNm = new JCheckBox("NM");
										chckbxNm.setEnabled(false);
										panel_modalities.add(chckbxNm);
										checkboxList.add(chckbxNm);
									}
									{
										chckbxPt = new JCheckBox("PT");
										chckbxPt.setEnabled(false);
										panel_modalities.add(chckbxPt);
										checkboxList.add(chckbxPt);
									}
									{
										chckbxUs = new JCheckBox("US");
										chckbxUs.setEnabled(false);
										panel_modalities.add(chckbxUs);
										checkboxList.add(chckbxUs);
									}
									{
										chckbxXa = new JCheckBox("XA");
										chckbxXa.setEnabled(false);
										panel_modalities.add(chckbxXa);
										checkboxList.add(chckbxXa);
									}
									{
										chckbxMg = new JCheckBox("MG");
										chckbxMg.setEnabled(false);
										panel_modalities.add(chckbxMg);
										checkboxList.add(chckbxMg);
									}
								}
							}
						}
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						//Save in registery
						Preferences jPrefer;
						jPrefer = Preferences.userNodeForPackage(AutoQuery.class);
						jPrefer = jPrefer.node("AutoQuery");
						jPrefer.putInt("discard", getDiscard());
						jPrefer.putInt("hour", getHour());
						jPrefer.putInt("minutes", getMin());
						jPrefer.putBoolean("useSeriesFilter", getUseSeriesFilter());
						jPrefer.put("seriesDescriptionContains", serieDescriptionContains.getText());
						jPrefer.put("seriesDescriptionExclude", serieDescriptionExclude.getText());
						jPrefer.put("seriesNumberContains", serieNumberMatch.getText());
						jPrefer.put("seriesNumberExclude", serieNumberExclude.getText());
						jPrefer.putBoolean("useSeriesCRFilter", chckbxCr.isSelected());
						jPrefer.putBoolean("useSeriesCTFilter", chckbxCt.isSelected());
						jPrefer.putBoolean("useSeriesCMRFilter", chckbxCmr.isSelected());
						jPrefer.putBoolean("useSeriesNMFilter", chckbxNm.isSelected());
						jPrefer.putBoolean("useSeriesPTFilter", chckbxPt.isSelected());
						jPrefer.putBoolean("useSeriesUSFilter", chckbxUs.isSelected());
						jPrefer.putBoolean("useSeriesXAFilter", chckbxXa.isSelected());
						jPrefer.putBoolean("useSeriesMGFilter", chckbxMg.isSelected());
						//dispose
						dispose();
					}
				});
				{
					JPanel panel_schedule = new JPanel();
					buttonPane.add(panel_schedule);
					panel_schedule.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
					{
						JLabel lblScheduleTime = new JLabel("Schedule Time : ");
						panel_schedule.add(lblScheduleTime);
					}
					{
						JLabel lblHh = new JLabel("HH");
						panel_schedule.add(lblHh);
					}
					{
						spinnerHour = new JSpinner();
						spinnerHour.setModel(new SpinnerNumberModel(22, 0, 23, 1));
						panel_schedule.add(spinnerHour);
					}
					{
						JLabel lblMm = new JLabel("mm");
						panel_schedule.add(lblMm);
					}
					{
						spinnerMin = new JSpinner();
						spinnerMin.setModel(new SpinnerNumberModel(0, 0, 59, 1));
						panel_schedule.add(spinnerMin);
					}
				}
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
		setSize(getPreferredSize());
		setOptionValuefromRegistery();
	}
	
	private void setOptionValuefromRegistery() {
		Preferences jPrefer = Preferences.userNodeForPackage(AutoQuery.class);
		jPrefer = jPrefer.node("AutoQuery");
		discard=jPrefer.getInt("discard", 10);
		spinnerDiscard.setValue(discard);
		hour=jPrefer.getInt("hour", 22);
		spinnerHour.setValue(hour);
		min=jPrefer.getInt("minutes", 00);
		spinnerMin.setValue(min);
		
		//serie filter
		chckbxSeriesFilter.setSelected(jPrefer.getBoolean("useSeriesFilter", false));
		serieDescriptionContains.setText(jPrefer.get("seriesDescriptionContains", ""));
		serieDescriptionExclude.setText(jPrefer.get("seriesDescriptionExclude", ""));
		serieNumberMatch.setText(jPrefer.get("seriesNumberContains", ""));
		serieNumberExclude.setText(jPrefer.get("seriesNumberExclude", ""));
		
		chckbxCr.setSelected(jPrefer.getBoolean("useSeriesCRFilter", false));
		chckbxCt.setSelected(jPrefer.getBoolean("useSeriesCTFilter", false));
		chckbxCmr.setSelected(jPrefer.getBoolean("useSeriesCMRFilter", false));
		chckbxNm.setSelected(jPrefer.getBoolean("useSeriesNMFilter", false));
		chckbxPt.setSelected(jPrefer.getBoolean("useSeriesPTFilter", false));
		chckbxUs.setSelected(jPrefer.getBoolean("useSeriesUSFilter", false));
		chckbxXa.setSelected(jPrefer.getBoolean("useSeriesXAFilter", false));
		chckbxMg.setSelected(jPrefer.getBoolean("useSeriesMGFilter", false));
		
	}
	
	protected void unactivateSeriesFiler(boolean unactivate) {
		if (unactivate) {
			serieDescriptionContains.setEnabled(false);
			serieDescriptionExclude.setEnabled(false);
			serieNumberExclude.setEnabled(false);
			serieNumberMatch.setEnabled(false);
			chckbxCr.setEnabled(false);
			chckbxCt.setEnabled(false);
			chckbxCmr.setEnabled(false);
			chckbxNm.setEnabled(false);
			chckbxPt.setEnabled(false);
			chckbxUs.setEnabled(false);
			chckbxXa.setEnabled(false);
			chckbxMg.setEnabled(false);
		}
		else {
			serieDescriptionContains.setEnabled(true);
			serieDescriptionExclude.setEnabled(true);
			serieNumberExclude.setEnabled(true);
			serieNumberMatch.setEnabled(true);
			chckbxCr.setEnabled(true);
			chckbxCt.setEnabled(true);
			chckbxCmr.setEnabled(true);
			chckbxNm.setEnabled(true);
			chckbxPt.setEnabled(true);
			chckbxUs.setEnabled(true);
			chckbxXa.setEnabled(true);
			chckbxMg.setEnabled(true);
		}
		
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
		return serieDescriptionContains.getText();
	}
	
	public String getSerieDescriptionExclude() {
		return serieDescriptionExclude.getText();
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
