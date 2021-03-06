package com.ashish.swinging;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.ashish.corpus.RecursiveSuffixFilterAndExtractor;
import com.ashish.corpus.SuffixExtractor;
import com.ashish.corpus.SuffixTrieFilter;
import com.ashish.mam.Config;
import com.ashish.mam.MorphologicalAnalyser;
import com.ashish.util.FSTGenerator;
import com.ashish.util.LNode;
import com.ashish.util.Node;
import com.ashish.util.PunctuationException;
import com.ashish.util.SNode;
import com.ashish.util.STrie;

import javax.swing.JTextPane;
import javax.swing.JTabbedPane;
import javax.swing.JList;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainWindow {

	public JFrame frame;
	private JTextArea log;
	private final Action fileChoose = new FileReaderAction();
	private final List<LNode> allWords = new ArrayList<LNode>();
	private final List<LNode> rootWords = new ArrayList<LNode>();
	private final List<SNode> suffixes = new ArrayList<SNode>();
	
	private final Action wordListAction = new RefreshWordListAction();
	private final Action suffixListAction = new RefreshSuffixesListAction();
	private final Action writeFSTtoFileAction = new WriteFSTtoFile();
	private final Action testInputFile = new TestInputFile();
	private final Action openPropertiesDialog = new OpenPropertiesDialog();

	
	private JTextPane testInputPane = new JTextPane();
	JTextPane testOutputPane = new JTextPane();
	public STrie suffixTrie = null;

	private enum ListMode {WORD,ROOTWORD,SUFFIX};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		final JList wordsList = new JList<>();
		JSplitPane wordSplitPane = initializeTab("Words", wordsList, tabbedPane);
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
				.addListSelectionListener(new SharedListSelectionHandler(
						(JTextPane) wordSplitPane.getRightComponent(),
						ListMode.WORD));

		final JList suffixList = new JList();
		JSplitPane suffixSplitPane = initializeTab("Suffixes", suffixList,
				tabbedPane);
		suffixList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				Object[] data = suffixes.toArray();
				suffixList.setListData(data);
				log.append("Displaying " + data.length
						+ " suffixes found in trie.\n");
			}
		});
		suffixList.addKeyListener(new KeyAdapter() {

			// What to do when a key is pressed?
			@Override
			public void keyPressed(KeyEvent ke) {

				// If user presses Delete key,
				if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
					// Remove the selected item
					suffixes.remove(suffixList.getSelectedValue());
					try {
						suffixTrie.removeNode(((SNode)suffixList
								.getSelectedValue()));
					} catch (UnsupportedEncodingException
							| PunctuationException e) {
						e.printStackTrace();
					}
					// Now set the updated vector (updated items)
					suffixList.setListData(suffixes.toArray());

				}
			}
		});
		ListSelectionModel suffixSelectionModel = suffixList
				.getSelectionModel();
		suffixSelectionModel
				.addListSelectionListener(new SharedListSelectionHandler(
						(JTextPane) suffixSplitPane.getRightComponent(),
						ListMode.SUFFIX));

		/*
		 * For the third tab - TEST tab. Vertically split. Text area at the top
		 * and text box at the bottom.
		 */
		JSplitPane testInput = new JSplitPane();
		tabbedPane.addTab("TEST", null, testInput, null);
		testInput.setOrientation(JSplitPane.VERTICAL_SPLIT);

		testInputPane.setEditable(true);
		testInput.setLeftComponent(testInputPane);

		testInput.setRightComponent(testOutputPane);
		testInput.setDividerLocation(150);
		;

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

		JMenuItem mntmInputarea = new JMenuItem("Test input");
		mntmInputarea.setAction(testInputFile);
		mnSearch.add(mntmInputarea);
		
		JMenuItem mntmOptions = new JMenuItem("Options");
		mntmOptions.setAction(openPropertiesDialog);
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

		@Override
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
			Node node = null;
			while (tokens.hasMoreTokens()) {
				try {
					String token = tokens.nextToken().trim();
					if (token.startsWith("<") && token.endsWith(">") && node != null) {
						((LNode) node).addTags(token);
					} else {
						node = MorphologicalAnalyser.getTrie().add(token);
						count++;
					}


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

		@Override
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
					SNode node = suffixes.get(selectedIndex);
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

		@Override
		public void actionPerformed(ActionEvent e) {
			allWords.clear();
			allWords.addAll(MorphologicalAnalyser.getTrie().getAllWords());
		
		}
	}
	
	
	private class RefreshSuffixesListAction extends AbstractAction {
		public RefreshSuffixesListAction() {
			putValue(NAME, "Refresh Suffixes List");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			log.append("Filtering words");
			SuffixExtractor cg = new SuffixExtractor();
			cg.setTrie(MorphologicalAnalyser.getTrie());
			cg.extractSuffixes();
			suffixTrie  = cg.getSuffixTrie();
			if (Config.recursiveSuffixExtraction) {
				RecursiveSuffixFilterAndExtractor recursiveExtractor = new RecursiveSuffixFilterAndExtractor();
				recursiveExtractor.setTrie(suffixTrie);
				recursiveExtractor.filterRecursively();
			}
			SuffixTrieFilter suffixFilter = new SuffixTrieFilter();
			suffixFilter.setTrie(suffixTrie);
			suffixFilter.filter();
			suffixes.clear();
			suffixes.addAll(suffixTrie.getAllWords());
			Collections.sort(suffixes);
		}
	}
	
	private class OpenPropertiesDialog extends AbstractAction {
		public OpenPropertiesDialog() {
			putValue(NAME, "Options");
			putValue(SHORT_DESCRIPTION, "Changes Configuration");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Properties dialog = new Properties();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
	}
	
	private class TestInputFile extends AbstractAction {
		public TestInputFile() {
			putValue(NAME, "Test Input");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			StringBuilder sb = new StringBuilder();
			String inputFileName = null;
			if (testInputPane.getText() != null && !testInputPane.getText().equalsIgnoreCase(""))  {
				sb.append(testInputPane.getText());
			} else {
				JFileChooser fc = new JFileChooser("D:\\");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(false);
				int returnVal = fc.showOpenDialog(new TextEditor());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// This is where a real application would open the file.
					inputFileName = file.getName();
					log.append("Reading: " + file.getName() + "." + "\n");
					log.setCaretPosition(log.getDocument().getLength());
	
					try {
						FileInputStream f = new FileInputStream(file);
						Scanner sc = new Scanner(f, "UTF-8");
						String line = null;
						while (sc.hasNextLine()) {
							sb.append(sc.nextLine());
						}
						
					} catch (FileNotFoundException e1) {
						log.append("File Not Found" + "\n");
					}
				} else {
					log.append("Open command cancelled by user." + "\n");
				}
			}
			if (sb.length() > 0 ) {
				testInputPane.setText(sb.toString());
				/*StringTokenizer sTokenizer = new StringTokenizer(sb.toString());
				String s = "";
				while (sTokenizer.hasMoreTokens()) {
					 s =s + " " + runCommands2(sTokenizer.nextToken());
					
				}*/
				String s =runCommands2(sb.toString());
				File outputFile  = new File("../out/" + inputFileName + "_out.txt");
				FileWriter fw = null;
				try { 
					fw = new FileWriter(outputFile);
					fw.append(s);
				} catch(Exception ex){
					ex.printStackTrace();
					String msg = "Unable to write to output file";
					System.out.println(msg);
					log.append(msg + "\n");
				} finally {
					if(fw != null)
						try {
							fw.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				}
				
				
				testOutputPane.setText(s);
				testInputPane.setCaretPosition(testInputPane.getDocument().getLength());
				testOutputPane.setCaretPosition(testOutputPane.getDocument().getLength());
			}
			log.setCaretPosition(log.getDocument().getLength());
		}

		private String runCommands2(String sb) {
			try {
				FSTGenerator fstGenerator = FSTGenerator.getFSTGenerator(); 
				fstGenerator.createInputFST(new File(
						"../in/input.txtfst"), sb.toString());
			} catch (IOException e1) {
				log.append("Error in reading from file." + "\n");
			} catch (PunctuationException pe) {
				log.append(pe.getMessage() + "\n");
			}
			
			Process p ;
			try {
				p = Runtime.getRuntime().exec("../bin/commands2.sh");
			    p.waitFor();
	
			    BufferedReader reader = 
			         new BufferedReader(new InputStreamReader(p.getErrorStream()));
	
			    String line = "";			
			    while ((line = reader.readLine())!= null) {
			    	System.out.println(line);
			    	log.append(line + "\n");
			    }
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			File f = new File ("../out/comp.txtfst");
			String text = "";
			if(f.exists()) {
				FSTGenerator fstGenerator = FSTGenerator.getFSTGenerator(); 
				text = fstGenerator.parseOutputFile(f);
				System.out.println("Output " + text);
			}
			if(!f.exists() || "".equalsIgnoreCase(text)) {
				return sb;
			} else {
				return text; 
			}
		}
	}
	
	private class WriteFSTtoFile extends AbstractAction {
		public WriteFSTtoFile() {
			putValue(NAME, "Write FST rules to file");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}


		
		@Override
		public void actionPerformed(ActionEvent e) {
			/*JFileChooser f = new JFileChooser();
	        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
	        //f.showSaveDialog(null);

	        if (f.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		        System.out.println(f.getCurrentDirectory());
		        System.out.println(f.getSelectedFile());
	        } else { 
	        	System.out.println("No option selected.");
	        }*/
	        String directory = "../FST";
			File isyms = new File(directory + "/in.syms");
			File osyms = new File(directory + "/out.syms");
			//File osysms = new File(directory + "/osyms.txt");
			File fst = new File(directory + "/suffixRules.txtfst");
			File letterfst = new File(directory + "/letters.txtfst");
			
			try {
				HashSet<String> tags = FSTGenerator.writeFSTRules(fst, suffixTrie, suffixes.size());
				FSTGenerator.writeLettersFST(letterfst, suffixes.size()*10);
				FSTGenerator.writeSymbols(isyms,osyms, tags);

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			Process p ;
			try {
				p = Runtime.getRuntime().exec("../bin/commands.sh");
			    p.waitFor();
	
			    BufferedReader reader = 
			         new BufferedReader(new InputStreamReader(p.getInputStream()));
	
			    String line = "";			
			    while ((line = reader.readLine())!= null) {
			    	System.out.println(line);
			    	log.append(line + "\n");
			    }
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			log.setCaretPosition(log.getDocument().getLength());
			
		}
		 
	}
}

