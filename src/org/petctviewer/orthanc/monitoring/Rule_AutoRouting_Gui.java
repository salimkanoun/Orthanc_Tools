package org.petctviewer.orthanc.monitoring;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Rule_AutoRouting_Gui extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField_Study_description;
	private JTextField textField_Series_Description;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Rule_AutoRouting_Gui dialog = new Rule_AutoRouting_Gui();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Rule_AutoRouting_Gui() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel_AutoRoutingGui_South = new JPanel();
			contentPanel.add(panel_AutoRoutingGui_South, BorderLayout.CENTER);
			{
				JLabel lblSendTo = new JLabel("Send To");
				panel_AutoRoutingGui_South.add(lblSendTo);
			}
			{
				JComboBox comboBox = new JComboBox();
				panel_AutoRoutingGui_South.add(comboBox);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			{
				JLabel lblStudyDescription = new JLabel("Study Description");
				panel.add(lblStudyDescription);
			}
			{
				textField_Study_description = new JTextField();
				panel.add(textField_Study_description);
				textField_Study_description.setColumns(10);
			}
			{
				JLabel lblSeriesDescription = new JLabel("Series Description");
				panel.add(lblSeriesDescription);
			}
			{
				textField_Series_Description = new JTextField();
				panel.add(textField_Series_Description);
				textField_Series_Description.setColumns(10);
			}
			{
				JLabel lblModalities = new JLabel("Modalities");
				panel.add(lblModalities);
			}
			{
				JLabel lblDate = new JLabel("Date");
				panel.add(lblDate);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
