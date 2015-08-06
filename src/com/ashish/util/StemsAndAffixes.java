package com.ashish.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

public class StemsAndAffixes {
	static protected final int NUM_LETTERS = 128;
	static Logger logger = LogManager.getLogManager();

	private LTrie lexiconTrie;
	private HashMap<String, Integer> stems = new HashMap<String, Integer>();
	private HashMap<String, Integer> affixes = new HashMap<String, Integer>();
	private boolean firstTimeExecuted = false;

	public StemsAndAffixes(LTrie lexiconTrie) {
		super();
		this.lexiconTrie = lexiconTrie;
	}

	private LTrie fullReturnTrie = new LTrie();
	
	public LTrie ashishAlgo(LTrie trie) throws CloneNotSupportedException {
		LTrie suffixes = new LTrie();
		if (!firstTimeExecuted) {
			firstTimeExecuted = true;
		}
		BranchIterator<LNode> iterator = new BranchIterator<>(trie.getRoot());
		boolean present = false;
		while (iterator.hasNext()) {
			LNode node = iterator.next();
			if (node != null) {
				 System.out.println(node.getWord());

				try {
					String s = node.getWord();
					if (node.isEndsWord() && firstTimeExecuted) {
						LTrie temp = new LTrie();
						temp.addOnlyBranch(node.getWord(), trie.getRoot());
						LNode branchNode = (LNode) temp.getRoot().getNthChild(Trie
								.toIndex(s.charAt(0)));
						//System.out.println("Temp trie" + temp.print());
						suffixes.add(branchNode); // Have to clone it.
						//fullReturnTrie.add(branchNode);
						System.out.println("isEndsWord, Added" + node.getWord());
					} 
					if(node.getNumChildren() > 1){
						// trie.root.setNthChild(null,
						// Trie.toIndex(s.charAt(0)));
						present = true;
						for (int i = 0; i < NUM_LETTERS; i++) {
							LNode item;
							if ((item = (LNode) node.getNthChild(i)) != null) {
								suffixes.add(item);
								fullReturnTrie.add(item);
								System.out.println("childNodes, Added of " + node.getWord());
							}
						}
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (PunctuationException e) {
					e.printStackTrace();
				}
				 System.out.println("Partial Trie: "+ suffixes.print());
				// logger.info(node.getWord());
				if (present) {
						logger.info("" + suffixes.print());
						System.out.println("Present: DOWN" + suffixes.print());
						ashishAlgo(suffixes);
				}
			}
		}
		
		//s{
			System.out.println("NotPresent. UP.");
			firstTimeExecuted = false;
			return fullReturnTrie;
		//}
	}

	
	public LTrie ashishAlgo2(LTrie trie) throws CloneNotSupportedException {
		LTrie suffixes = new LTrie();
		if (!firstTimeExecuted) {
			firstTimeExecuted = true;
		}
		BranchIterator<LNode> iterator = new BranchIterator<>(trie.getRoot());
		boolean present = false;
		while (iterator.hasNext()) {
			LNode node = iterator.next();
			if (node != null) {
				 System.out.println(node.getWord());

				try {
					String s = node.getWord();
					if (node.isEndsWord() && firstTimeExecuted) {
						LTrie temp = new LTrie();
						temp.addOnlyBranch(node.getWord(), trie.getRoot());
						LNode branchNode = (LNode) temp.getRoot().getNthChild(Trie
								.toIndex(s.charAt(0)));
						//System.out.println("Temp trie" + temp.print());
						suffixes.add(branchNode); // Have to clone it.
						//fullReturnTrie.add(branchNode);
						System.out.println("isEndsWord, Added" + node.getWord());
					} 
					if(node.getNumChildren() > 1){
						// trie.root.setNthChild(null,
						// Trie.toIndex(s.charAt(0)));
						present = true;
						for (int i = 0; i < NUM_LETTERS; i++) {
							LNode item;
							if ((item = (LNode) node.getNthChild(i)) != null) {
								suffixes.add(item);
								fullReturnTrie.add(item);
								System.out.println("childNodes, Added of " + node.getWord());
							}
						}
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (PunctuationException e) {
					e.printStackTrace();
				}
				 System.out.println("Partial Trie: "+ suffixes.print());
				// logger.info(node.getWord());

			}
		}
		if (present) {
			logger.info("" + suffixes.print());
			System.out.println("Present: DOWN" + suffixes.print());
			ashishAlgo2(suffixes);
		} 		
			System.out.println("NotPresent. UP.");
			firstTimeExecuted = false;
			return fullReturnTrie;
		
	}
	
/*	public LTrie ashishAlgo3(LTrie trie) {
	}*/

	private void branchOf(LTrie trie, LNode node) {

		while (node.getParent() != trie.getRoot()) {
			node = (LNode) node.getParent();
		}

	}

	private void generateStems() {
		while (lexiconTrie.hasNext()) {
			LNode n = lexiconTrie.next();
			if (generateAffixes(n)) {
				putInHashMap(stems, n.getWord());
				// stems.add(n.getWord());
			}
		}
	}

	private void putInHashMap(HashMap<String, Integer> map, String word) {
		Integer wordCount = map.get(word);
		if (wordCount == null) {
			map.put(word, 1);
		} else
			map.put(word, wordCount.intValue() + 1);
	}

	private boolean generateAffixes(LNode n) {
		// affixes.addAll(lexiconTrie.print(n.getWord()));
		Vector<String> possibleAffixes = new Vector<String>();
		NodeIterator<LNode> bIterator = new NodeIterator<LNode>(n);
		while (bIterator.hasNext()) {
			String branch = bIterator.nextWord();
			if (branch.length() > n.getWord().length())
				return false;
			possibleAffixes.add(branch);
		}
		for (String branch : possibleAffixes) {
			// System.out.println("branch " + branch);
			putInHashMap(affixes, branch);
		}
		// affixes.addAll(possibleAffixes);
		return true;
	}

	public Set<String> getStems() {
		if (stems.isEmpty()) {
			System.out.println("No stems found");
			generateStems();
		}
		return stems.keySet();
	}

	public Set<String> getAffixes() {
		if (affixes.isEmpty()) {
			System.out.println("No affixes found");
			generateStems();
		}
		return affixes.keySet();
	}

	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		//String s = "അകാലം അകൃതം കാമലം കാമതം";
		LTrie t = new LTrie();
		//t.initLexicon(s);
		t.add("പട്ടി", true);
		t.add("പട്ടിയെ", false);
		t.add("പട്ടിക്കു", false);
		t.add("പടനം", false);
		t.add("പട്ടികള്‍");
		t.add("പട്ടികള്‍ക്ക്");
		t.add("മരം", true);
		t.add("മരമേ", false);

		StemsAndAffixes sa = new StemsAndAffixes(t);

		LTrie suffixes = null;
		try {
		suffixes = sa.ashishAlgo(t);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		System.out.println(suffixes.print());

	}

}
