
package org.petctviewer.query;

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
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class Option_Auto_Query extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JSpinner spinnerDiscard;
	private JSpinner spinnerHour;
	private JSpinner spinnerMin;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Option_Auto_Query dialog = new Option_Auto_Query(10,22,10);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Option_Auto_Query(int discard, int hour, int min) {
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel Title_panel = new JPanel();
			contentPanel.add(Title_panel, BorderLayout.NORTH);
			{
				JLabel lblAutoQueryOptions = new JLabel("Auto Query Options");
				Title_panel.add(lblAutoQueryOptions);
			}
		}
		{
			JPanel Option_panel = new JPanel();
			contentPanel.add(Option_panel, BorderLayout.CENTER);
			Option_panel.setLayout(new GridLayout(2, 1, 0, 0));
			{
				JPanel panel_discard = new JPanel();
				Option_panel.add(panel_discard);
				{
					JLabel lblDiscardIfQuery = new JLabel("Discard if Query size over");
					panel_discard.add(lblDiscardIfQuery);
				}
				{
					spinnerDiscard = new JSpinner();
					spinnerDiscard.setModel(new SpinnerNumberModel(new Integer(10), null, null, new Integer(1)));
					spinnerDiscard.setValue(discard);
					panel_discard.add(spinnerDiscard);
				}
			}
			{
				JPanel panel_schedule = new JPanel();
				Option_panel.add(panel_schedule);
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
					spinnerHour.setValue(hour);
					panel_schedule.add(spinnerHour);
				}
				{
					JLabel lblMm = new JLabel("mm");
					panel_schedule.add(lblMm);
				}
				{
					spinnerMin = new JSpinner();
					spinnerMin.setModel(new SpinnerNumberModel(0, 0, 59, 1));
					spinnerMin.setValue(min);
					panel_schedule.add(spinnerMin);
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
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
		setSize(getPreferredSize());
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

}
