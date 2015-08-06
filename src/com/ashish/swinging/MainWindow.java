package com.ashish.swinging;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JToolBar;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;

import java.awt.Panel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JInternalFrame;

import com.ashish.corpus.LTrieFilter;
import com.ashish.mam.MorphologicalAnalyser;
import com.ashish.util.LNode;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTabbedPane;
import javax.swing.JList;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class MainWindow {

	public JFrame frame;
	private JTextArea log;
	private final Action fileChoose = new FileReaderAction();
	private final List<LNode> allWords = new ArrayList<LNode>();
	private final List<LNode> rootWords = new ArrayList<LNode>();
	private final List<LNode> suffixes = new ArrayList<LNode>();
	
	private final Action wordListAction = new RefreshWordListAction();
	private final Action rootWordListAction = new RefreshRootWordListAction();
	private final Action suffixListAction = new RefreshSuffixesListAction();
	private final Action writeFSTtoFileAction = new WriteFSTtoFile();

	private JTable table_1;
	private JTable table_2;
	private enum ListMode {WORD,ROOTWORD,SUFFIX};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 697, 466);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		log = new JTextArea(5, 20);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);
		frame.getContentPane().add(logScrollPane, BorderLayout.SOUTH);
		
		initializeMenuBar();
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		final JList wordsList = new JList<>();
		JSplitPane wordSplitPane = initializeTab("Words",wordsList, tabbedPane);
		wordsList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				Object[] data = allWords.toArray();
				wordsList.setListData(data);
				log.append("Displaying " + data.length
						+ " words found in trie.\n");
			}
		});
		ListSelectionModel listSelectionModel = wordsList.getSelectionModel();
		listSelectionModel
		.addListSelectionListener(new SharedListSelectionHandler((JTextPane)wordSplitPane.getRightComponent(), ListMode.WORD));

		final JList rootWordList = new JList(); 
		JSplitPane rootWordSplitPane = initializeTab("RootWords",rootWordList,tabbedPane);

		final JList suffixList = new JList(); 
		JSplitPane suffixSplitPane = initializeTab("Suffixes",suffixList, tabbedPane);
		suffixList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				Object[] data = suffixes.toArray();
				suffixList.setListData(data);
				log.append("Displaying " + data.length
						+ " suffixes found in trie.\n");
			}
		});
		ListSelectionModel suffixSelectionModel = suffixList.getSelectionModel();
		suffixSelectionModel
		.addListSelectionListener(new SharedListSelectionHandler((JTextPane)suffixSplitPane.getRightComponent(), ListMode.SUFFIX));


	}
	private JSplitPane initializeTab(String tabName, JList list, JTabbedPane tabbedPane) {
		JSplitPane wordSplitPane = new JSplitPane();
		tabbedPane.addTab(tabName, null, wordSplitPane, null);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		wordSplitPane.setDividerLocation(150);
		JScrollPane listScrollPane = new JScrollPane();
		wordSplitPane.setLeftComponent(listScrollPane);
		listScrollPane.setMinimumSize(new Dimension(100, 50));
		listScrollPane.setViewportView(list);
		
		JTextPane textPane = new JTextPane();
		wordSplitPane.setRightComponent(textPane);
		return wordSplitPane;
	}

	private void initializeMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenu mnAdd = new JMenu("Add");
		menuBar.add(mnAdd);

		JMenuItem mntmFromFile = new JMenuItem("From File");
		mntmFromFile.setAction(fileChoose);
		mnAdd.add(mntmFromFile);

		JMenuItem mntmInputarea = new JMenuItem("InputArea");
		mntmInputarea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TextEditor myNewFrame = new TextEditor();
				myNewFrame.setVisible(true);
			}
		});
		mnAdd.add(mntmInputarea);

		JMenu mnSearch = new JMenu("Actions");
		JMenuItem mntmRefreshWordList = new JMenuItem("Get All Words");
		mntmRefreshWordList.setAction(wordListAction);
		mnSearch.add(mntmRefreshWordList);

		JMenuItem mntmGetSuffixes = new JMenuItem("Get Suffixes");
		mntmGetSuffixes.setAction(suffixListAction);
		mnSearch.add(mntmGetSuffixes);

		JMenuItem mntmWriteFSTtoFile = new JMenuItem("Write FST rules to File");
		mntmWriteFSTtoFile.setAction(writeFSTtoFileAction);
		mnSearch.add(mntmWriteFSTtoFile);
		
		JMenuItem mntmOptions = new JMenuItem("Options");
		mnSearch.add(mntmOptions);

		menuBar.add(mnSearch);
	}

	private class FileReaderAction extends AbstractAction {
		int count = 0;
		int errorCount = 0;

		public FileReaderAction() {
			putValue(NAME, "Choose from File");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser("D:\\");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(false);
			int returnVal = fc.showOpenDialog(new TextEditor());

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				// This is where a real application would open the file.
				log.append("Reading: " + file.getName() + "." + "\n");
				log.setCaretPosition(log.getDocument().getLength());

				try {
					FileInputStream f = new FileInputStream(file);
					Scanner sc = new Scanner(f, "UTF-8");
					String line = null;
					count = 0;
					errorCount = 0;
					while (sc.hasNextLine()) {
						addTextInTrie(sc.nextLine());
					}
					log.append(count + " words added in Trie. ");
					if (errorCount > 0) {
						log.append(errorCount + " words skipped");
					}
					log.append("\n");
					log.append("Filtering words");
					LTrieFilter cg = new LTrieFilter();
					cg.setTrie(MorphologicalAnalyser.getTrie());
					cg.filter();
					log.append(cg.getDeleteCount() + " words deleted while filtering");

					

				} catch (FileNotFoundException e1) {
					log.append("File Not Found" + "\n");
				} catch (IOException e1) {
					log.append("Error in reading from file." + "\n");
				}

			} else {
				log.append("Open command cancelled by user." + "\n");
			}
			log.setCaretPosition(log.getDocument().getLength());
		}

		private void addTextInTrie(String textToBeAdded) {
			StringTokenizer tokens = new StringTokenizer(textToBeAdded);
			while (tokens.hasMoreTokens()) {
				try {
					MorphologicalAnalyser.getTrie().add(tokens.nextToken());
					count++;
				} catch (UnsupportedEncodingException e1) {
					errorCount++;
				}
			}

		}
	}

	private class SharedListSelectionHandler implements ListSelectionListener {
		private JTextPane textPane;
		private ListMode mode;

		public SharedListSelectionHandler(JTextPane textPane, ListMode mode) {
			this.textPane = textPane;
			this.mode = mode;
		}

		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			int selectedIndex = -1;
			int firstIndex = e.getFirstIndex();
			int lastIndex = e.getLastIndex();
			boolean isAdjusting = e.getValueIsAdjusting();
			//log.append("Event for indexes " + firstIndex + " - " + lastIndex
			//		+ "; isAdjusting is " + isAdjusting + "; selected indexes:");

			if (lsm.isSelectionEmpty()) {
				log.append(" <none>");
				log.setCaretPosition(log.getDocument().getLength());

			} else {
				// Find out which indexes are selected.
				int minIndex = lsm.getMinSelectionIndex();
				int maxIndex = lsm.getMaxSelectionIndex();
				for (int i = minIndex; i <= maxIndex; i++) {
					if (lsm.isSelectedIndex(i)) {
						selectedIndex = i;
						//log.append(" " + i);
					}
				}
				StringBuilder nodeText = new StringBuilder();
				if(mode == ListMode.WORD) {
					LNode node = allWords.get(selectedIndex);
					textPane.setText(node.getNodeText());
				} else if(mode == ListMode.SUFFIX) {
					LNode node = suffixes.get(selectedIndex);
					textPane.setText(node.getNodeText());
				} 
			}
			//log.append("\n");
			
		}
	}

	private class RefreshWordListAction extends AbstractAction {
		public RefreshWordListAction() {
			putValue(NAME, "Refresh Word List");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			allWords.clear();
			allWords.addAll(MorphologicalAnalyser.getTrie().getAllWords());
		}
	}
	
	private class RefreshRootWordListAction extends AbstractAction {
		public RefreshRootWordListAction() {
			putValue(NAME, "Refresh Root Word List");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			rootWords.clear();
			rootWords.addAll(MorphologicalAnalyser.getTrie().getAllWords());
		}
	}
	
	private class RefreshSuffixesListAction extends AbstractAction {
		public RefreshSuffixesListAction() {
			putValue(NAME, "Refresh Suffixes List");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			suffixes.clear();
			suffixes.addAll(MorphologicalAnalyser.getTrie().method3AshishAlgo().getAllWords());
			Collections.sort(suffixes);
		}
	}
	
	private class WriteFSTtoFile extends AbstractAction {
		public WriteFSTtoFile() {
			putValue(NAME, "Write FST rules to file");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			File isyms = new File("D:\\FST\\isyms.txt");
			File osysms = new File("D:\\FST\\osyms.txt");
			File fst = new File("D:\\FST\\fst.txt");

			try {
				writeFSTRules(isyms, fst);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		private void writeFSTRules(File isyms, File fst) throws IOException {
			if (isyms.exists()) {
				isyms.delete();
			}
			if (fst.exists()) {
				fst.delete();
			}
			
			if(!isyms.createNewFile() || !fst.createNewFile()) {
				return;
			}
			
			FileWriter inputSymbols = new FileWriter(isyms);
			FileWriter fstRules = new FileWriter(fst);
		
			try {
				inputSymbols.append("<eps> 0\n");
				int suffixStartState  = 2;
				int finalState = 4;
				int count = 5;
				for (int i =0; i < suffixes.size(); i++, count++) {
					inputSymbols.append("" + suffixes.get(i));
					inputSymbols.append(" ");
					inputSymbols.append("" + (i+1));
					inputSymbols.append("\n");
					
					fstRules.append("" + suffixStartState)
					.append(" ")
					.append("" + count)
					.append(" ")
					.append(""  +suffixes.get(i))
					.append(" ")
					.append("null")
					.append("\n");
					
					fstRules.append("" + count)
					.append(" ")
					.append("" + finalState)
					.append(" ")
					.append("<eps>")
					.append(" ")
					.append("<eps>")
					.append("\n");
					
				}
				
				fstRules.append("" +finalState)
				.append(" ")
				.append("" +suffixStartState)
				.append(" ")
				.append("<eps>")
				.append(" ")
				.append("<eps>")
				.append("\n");
				
				fstRules.append("EOF");
				inputSymbols.append("EOF");
			} finally {
				fstRules.close();
				inputSymbols.close();
			}
		}
	}
	
	
}
