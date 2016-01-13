package com.ashish.swinging;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.GridLayout;

import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import com.ashish.mam.Config;

import javax.swing.JToggleButton;

public class Properties extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtOccthreshold;
	private JTextField txtLettersWeight;
	private JToggleButton tglbtnRecursiveSuffixExtraction;
	private final Action action = new SwingAction();
	private final Action cancel = new CancelAction();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Properties dialog = new Properties();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Properties() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(5, 2, 0, 0));
		{
			JLabel lblSuffixOccThreshold = new JLabel("Suffix Occurences Threshold");
			contentPanel.add(lblSuffixOccThreshold);
		}
		{
			txtOccthreshold = new JTextField();
			txtOccthreshold.setText("" +Config.suffixOccThreshold);
			contentPanel.add(txtOccthreshold);
			txtOccthreshold.setColumns(10);
		}
		{
			JLabel lblLettersWeight = new JLabel("Letters Weight");
			contentPanel.add(lblLettersWeight);
		}
		{
			txtLettersWeight = new JTextField();
			txtLettersWeight.setText("" + Config.lettersMaxWeight);
			contentPanel.add(txtLettersWeight);
			txtLettersWeight.setColumns(10);
		}
		{
			JLabel lblRecursiveSuffixExtraction = new JLabel("Recursive Suffix Extraction");
			contentPanel.add(lblRecursiveSuffixExtraction);
		}
		{
			tglbtnRecursiveSuffixExtraction = new JToggleButton("recursiveSuffixExtraction");
			tglbtnRecursiveSuffixExtraction.setSelected(Config.recursiveSuffixExtraction);
			contentPanel.add(tglbtnRecursiveSuffixExtraction);
		}
		{
			JLabel label = new JLabel("");
			contentPanel.add(label);
		}
		{
			JLabel label = new JLabel("");
			contentPanel.add(label);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setAction(action);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setAction(cancel);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "OK");
			putValue(SHORT_DESCRIPTION, "Changes Configuration");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
		    Config.suffixOccThreshold = Integer.parseInt(txtOccthreshold.getText());
		    Config.lettersMaxWeight = Integer.parseInt(txtLettersWeight.getText());
		    Config.recursiveSuffixExtraction  = tglbtnRecursiveSuffixExtraction.isSelected();
		    dispose();
		}
	}
	
	private class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Cancel changes");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
		    dispose();
		}
	}
}
