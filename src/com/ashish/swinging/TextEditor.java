package com.ashish.swinging;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JEditorPane;

import javax.swing.JTextPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import javax.swing.Action;

import com.ashish.mam.MorphologicalAnalyser;

public class TextEditor extends JFrame {

	private JPanel contentPane;
	private JEditorPane editorPane;
	private final Action action = new SwingAction();
	private JTextPane txtpnStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					TextEditor frame = new TextEditor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TextEditor() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAction(action);
		mnNewMenu.add(mntmSave);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnNewMenu.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		editorPane = new JEditorPane();
		editorPane.setContentType("text/html;charset=UTF-8");
		contentPane.add(editorPane, BorderLayout.CENTER);
		
		txtpnStatus = new JTextPane();
		txtpnStatus.setText("Status");
		contentPane.add(txtpnStatus, BorderLayout.SOUTH);
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Adds the words to Trie");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String textToBeAdded = editorPane.getText();
			addTextInTrie(textToBeAdded);
		}
		private void addTextInTrie(String textToBeAdded) {
			int count = 0;
			int errorCount = 0;
			StringTokenizer tokens = new StringTokenizer(textToBeAdded);
			while(tokens.hasMoreTokens()) {
				try {
					MorphologicalAnalyser.getTrie().add(tokens.nextToken());
					count ++;
				} catch (UnsupportedEncodingException e1) {
					errorCount++;
				}
			}
			txtpnStatus.setText(count + " words added in Trie." + " " + errorCount + " words not added." );
		}
	}
}
