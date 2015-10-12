package com.ashish.util;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.ashish.corpus.SuffixTrieFilter;

public class LTrie extends Trie<LNode> {

	private static final int CUTWORD = 1;
	private int mode;

	static Logger logger = LogManager.getLogManager();
	
	public LTrie() {
		super(new LNode());
	}


	

	public void add(String str, boolean isRootForm)
			throws UnsupportedEncodingException {
		LNode current = getRoot();
		LNode child = add(str);
		if (mode == CUTWORD) {
			current.cutWordProbability = calculateProbability(current);
		}
		if (isRootForm) {
			((LNode) child).rootWord = true;
			((LNode) child).incNumRootChildren();
			// context.addMessage(null, new FacesMessage("Successful",
			// "Added as Root Word" + str));
		}
	}

	//FIXME: THis function needs to be fixed or removed.
	protected float calculateProbability(Node node) {
		ArrayList<String> editWords = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();
		if (node.getNumChildren() < 2)
			return 0.0f;
		if (node.equals(getRoot()) || node.getParent().equals(getRoot())
				|| node.getParent().getParent().equals(getRoot()))
			return 0.0f;
		Node inflexNode = node.getParent().getParent();
		NodeIterator<Node> iterator = new NodeIterator<Node>(inflexNode);
		while (iterator.hasNextWord()) {
			String word = iterator.nextWord();
			words.add(word);
		}
		while (iterator.hasNextRootWord()) {
			editWords.add(iterator.nextRootWord());
		}
		return 0.0f;
	}
	public Node searchRootWord(String str) {
		LNode current = getRoot();
		try {
			for (int i = 0; i < str.length(); i++) {
				int index;
				try {
					index = toIndex(str.charAt(i));
				} catch (PunctuationException e) {
					continue;
				}
				if (current.getNthChild(index) == null) {
					return null;
				}
				current = (LNode) current.getNthChild(index);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (current.rootWord) {
			return current;
		}
		return null;
	}
	
	public List<LNode> getBranches() {
		return null;
	}
	
	public static void main(String args[]) throws ClassNotFoundException, SQLException {
		
		String s = "";
		LTrie t = new LTrie();
		
		ResultSetHandler<List<Word>> h = new BeanListHandler<Word>(Word.class);
		
		DbUtils utils = new DbUtils();
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://127.0.0.1:3306/datuk";
		Connection con = DriverManager.getConnection(url, "amathew", "amathew");
		QueryRunner run = new QueryRunner();
		List<Word> words = run.query(con, "SELECT word FROM datuk.word", h);
		//FIXME:
		try {
			t.initLexicon(s);
			t.add("പട്ടി", true);
			t.add("പട്ടിയെ", false);
			t.add("പട്ടിക്കു", false);
			t.add("പടനം", false);
			t.add("പട്ടികള്‍");
			t.add("പട്ടികള്‍ക്ക്");
			t.add("മരം", true);
			t.add("മരമേ", false);
			
			//Get from database. 
			/*
			for (Word word : words) {
				String str = word.getWord();
				//System.out.println(str);
				//logger.info(str);
				try {
					t.add(str,false);
				} catch (UnsupportedEncodingException e) {
					continue;
				}
			}*/
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(0);
		}

		// String s =
		// "à´‰à´¤àµ�à´¤à´°à´¾à´«àµ�à´°à´¿à´•àµ�à´•à´¯à´¿àµ½ à´¨à´¿à´¨àµ�à´¨àµ�à´³àµ�à´³ à´²à´¤àµ�à´¤àµ€àµ» à´•àµ�à´°à´¿à´¸àµ�à´¤àµ€à´¯à´šà´¿à´¨àµ�à´¤à´•à´¨àµ�à´‚, à´¦àµˆà´µà´¶à´¾à´¸àµ�à´¤àµ�à´°à´œàµ�à´žà´¨àµ�à´‚ à´®àµ†à´¤àµ�à´°à´¾à´¨àµ�à´®à´¾à´¯à´¿à´°àµ�à´¨àµ�à´¨àµ� à´¹à´¿à´ªàµ�à´ªàµ‹à´¯à´¿à´²àµ† à´…à´—à´¸àµ�à´¤àµ€à´¨àµ‹à´¸àµ�. à´µà´¿à´¶àµ�à´¦àµ�à´§ à´…à´—à´¸àµ�à´±àµ�à´±à´¿àµ» (à´¸àµ†à´¯àµ�à´¨àµ�à´±àµ� à´…à´—à´¸àµ�à´±àµ�à´±à´¿àµ»), à´µà´¿à´¶àµ�à´¦àµ�à´§ à´“à´¸àµ�à´±àµ�à´±à´¿àµ», à´”à´±àµ‡à´²à´¿à´¯àµ�à´¸àµ� à´…à´—à´¸àµ�à´¤àµ€à´¨àµ‹à´¸àµ� à´Žà´¨àµ�à´¨àµ€ à´ªàµ‡à´°àµ�à´•à´³à´¿à´²àµ�à´‚ à´…à´¦àµ�à´¦àµ‡à´¹à´‚ à´…à´±à´¿à´¯à´ªàµ�à´ªàµ†à´Ÿàµ�à´¨àµ�à´¨àµ�. à´±àµ‹à´®àµ» à´•à´¤àµ�à´¤àµ‹à´²à´¿à´•àµ�à´•à´¾ à´¸à´­à´¯àµ�à´‚ à´†à´‚à´—àµ�à´²à´¿à´•àµ�à´•àµ» à´•àµ‚à´Ÿàµ�à´Ÿà´¾à´¯àµ�à´®à´¯àµ�à´‚ à´…à´—à´¸àµ�à´¤àµ€à´¨àµ‹à´¸à´¿à´¨àµ† à´µà´¿à´¶àµ�à´¦àµ�à´§à´¨àµ�à´‚ à´µàµ‡à´¦à´ªà´¾à´°à´‚à´—à´¤à´¨àµ�à´®à´¾à´°à´¿àµ½ à´®àµ�à´®àµ�à´ªà´¨àµ�à´‚ à´†à´¯à´¿ à´®à´¾à´¨à´¿à´•àµ�à´•àµ�à´¨àµ�à´¨àµ�. à´ªàµ�à´°àµŠà´Ÿàµ�à´Ÿà´¸àµ�à´±àµ�à´±à´¨àµ�à´±àµ� à´¨à´µàµ€à´•à´°à´£à´¤àµ�à´¤àµ† à´�à´±àµ�à´±à´µàµ�à´®àµ‡à´±àµ† à´¸àµ�à´µà´¾à´§àµ€à´¨à´¿à´šàµ�à´š à´¸à´­à´¾à´ªà´¿à´¤à´¾à´µàµ� à´…à´¦àµ�à´¦àµ‡à´¹à´®à´¾à´£àµ�. à´…à´—à´¸àµ�à´¤àµ€à´¨àµ‹à´¸à´¿à´¨àµ�à´±àµ† à´šà´¿à´¨àµ�à´¤à´¯àµ�à´‚, à´¤à´¤àµ�à´¤àµ�à´µà´šà´¿à´¨àµ�à´¤à´¯à´¿à´²àµ�à´‚ à´¦àµˆà´µà´¶à´¾à´¸àµ�à´¤àµ�à´°à´¤àµ�à´¤à´¿à´²àµ�à´‚ à´…à´¦àµ�à´¦àµ‡à´¹à´‚ à´°àµ‚à´ªà´ªàµ�à´ªàµ†à´Ÿàµ�à´¤àµ�à´¤à´¿à´¯ à´¨à´¿à´²à´ªà´¾à´Ÿàµ�à´•à´³àµ�à´‚ à´®à´¦àµ�à´§àµ�à´¯à´•à´¾à´² à´²àµ‹à´•à´µàµ€à´•àµ�à´·à´£à´¤àµ�à´¤àµ† à´…à´Ÿà´¿à´¸àµ�à´¥à´¾à´¨à´ªà´°à´®à´¾à´¯à´¿ à´¸àµ�à´µà´¾à´§àµ€à´¨à´¿à´šàµ�à´šàµ�. à´®à´¨àµ�à´·àµ�à´¯à´¸àµ�à´µà´¾à´¤à´¨àµ�à´¤àµ�à´°àµ�à´¯à´¤àµ�à´¤à´¿à´¨àµ�â€Œ à´¦àµˆà´µà´¤àµ�à´¤à´¿à´¨àµ�à´±àµ† à´•àµƒà´ª à´’à´´à´¿à´šàµ�à´šàµ�à´•àµ‚à´Ÿà´¾à´¤àµ�à´¤à´¤à´¾à´£àµ†à´¨àµ�à´¨àµ� à´…à´¦àµ�à´¦àµ‡à´¹à´‚ à´µà´¿à´¶àµ�à´µà´¸à´¿à´šàµ�à´šàµ�. à´¤àµ�à´Ÿà´™àµ�à´™à´¿à´¯ à´®à´¤, à´°à´¾à´·àµ�à´Ÿàµ�à´°àµ€à´¯ à´¸à´™àµ�à´•à´²àµ�à´ªà´™àµ�à´™àµ¾ à´•àµ�à´°àµˆà´¸àµ�à´¤à´µà´²àµ‹à´•à´¤àµ�à´¤à´¿à´¨àµ�â€Œ à´¸à´®àµ�à´®à´¾à´¨à´¿à´šàµ�à´šà´¤àµ� à´…à´—à´¸àµ�à´¤àµ€à´¨àµ‹à´¸à´¾à´£àµ�â€Œ";

		// method1HeuristicCut(t);
		//t.method2RootWordCut(t);
		t.method3AshishAlgo();
	}

	public void initLexicon(String s) throws UnsupportedEncodingException {
		StringTokenizer st = new StringTokenizer(s);

		while (st.hasMoreTokens()) {
			String tkn = st.nextToken();
			// logger.info(tkn);
			add(tkn, false);
		}
	}

	private void method2RootWordCut(LTrie t) {
		while (t.hasNext()) {
			LNode temp = t.next();
			temp.cutWordProbability = getProbability(temp);
			String msg = "Node:" + temp.getNodeChar() + "["
					+ temp.cutWordProbability + "]";
			logger.info(msg);
			//System.out.println(msg);
		}
		
		RootWordSplitter splitter = new RootWordSplitter();
		splitter.suffixSearch(t);
		
	}
	
	public LTrie method3AshishAlgo(){
		StemsAndAffixes sa = new StemsAndAffixes(this);
		LTrie suffixes = null;
		try {
			suffixes = sa.ashishAlgo2(this);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		SuffixTrieFilter stf = new SuffixTrieFilter(); 
		stf.setTrie(suffixes);
		stf.filter();
		System.out.println("Suffixes filtered. Words deleted - " + stf.getDeleteCount());
		//String Msg = "" + suffixes.print();
		//logger.info(Msg);
		//System.out.println(Msg);
		return suffixes;
	}

	private static void method1HeuristicCut(LTrie trie)
			throws UnsupportedEncodingException {
		logger.info("PRINTING ALL IN TRIE");
		for (String str : trie.print()) {
			logger.info(str);
		}
		/*
		 * for (String str : t.getSuffixHashes()) { logger.info(str); }
		 * Iterator<LNode> iterator = t.suffixes; while (iterator.hasNext()) {
		 * LNode temp = iterator.next(); logger.info(temp); }
		 */

		StemsAndAffixes stemsAndAffixes = new StemsAndAffixes(trie);
		logger.info("" +stemsAndAffixes.getStems());
		logger.info("" +stemsAndAffixes.getAffixes());
	}

	public void addOnlyBranch(String word, LNode node) throws UnsupportedEncodingException, PunctuationException{
		LNode current = node;
		LNode currentTemp = getRoot();
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			int trieIndex =toIndex(c);
			current = (LNode) current.getNthChild(trieIndex);
			currentTemp = (LNode) addChild(currentTemp,trieIndex);
			//System.out.println(c + " - " + trieIndex+ " - " + this.print());
			merge(currentTemp,current);
		}
	}
	
	public void add(LNode node) throws UnsupportedEncodingException, PunctuationException {
		HashMap<LNode,LNode> mapping = new HashMap<>();
		LNode sNode = add(this.getRoot(),node);
		mapping.put(node, sNode);
		NodeIterator<LNode> iterator = new NodeIterator<LNode>(node);
		while(iterator.hasNext()) {
			LNode nextNode  = iterator.next();
			sNode = mapping.get(nextNode.getParent());
			if(sNode == null) {
				//new Exception().printStackTrace();
				continue;
			}
			sNode = add(sNode,nextNode);
			mapping.put(nextNode, sNode);
		}
	}
	
	public LNode add(LNode baseNode,LNode toAddNode) throws UnsupportedEncodingException, PunctuationException {
		int index = toIndex(toAddNode.nodeChar);
		LNode sNode = (LNode) addChild(baseNode, index);
		merge(sNode, toAddNode);
		return sNode;
	}
	
	private void merge(LNode sNode, LNode toAddNode) {
		//sNode.setOccurrences(toAddNode.getOccurrences());
		if(toAddNode.isEndsWord()) {
			sNode.setEndsWord(true);
			for(String tag : toAddNode.tags) {
				sNode.addTags(tag);
			}
		}
		
	}

	
	private double getProbability(LNode n) {
		if(n.rootWord) {
			return 1;
		}
		// Have to confirm if the above step has certain effects or not
		logger.info("Node recieved : " + n.getWord() + " at level " + n.getLevel());
		int backstep = 2;
		Node current = n;
		while (backstep-- > 0 && current.getLevel()>1) {
			current = current.getParent();
		}
		if (current != null && current.getLevel() > 0) {
			logger.info(n.getWord() + " backstepped to " + current.getWord() + "[level:"+current.getLevel()+"]");
			return searchByLevel(current);
		}
		return 0;
	}

	private double searchByLevel(Node n) {
		int rootCount = 0;
		int wordCount = 0;
		logger.info("Searching for node "+ n.getWord());
		DepthLimitedIterator<Node> iterator = new DepthLimitedIterator<Node>(n, n.getLevel() +4);
		while (iterator.hasNext()) {
			Node current = iterator.next();
			LNode lCurrent = (LNode) current;
			if(lCurrent.isEndsWord()) wordCount++;
			if(lCurrent.rootWord) rootCount++;
			//wordCount += current.getNumChildren();
			//rootCount += ((LNode) current).getNumRootChildren();
			logger.info("-->"+ current.getWord()+ "==>"  + rootCount + "/" + wordCount);
		}
		double probability = (double) rootCount / wordCount;
		return probability;
	}

	
	
}