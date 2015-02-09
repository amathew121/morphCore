package com.ashish.util;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class StemsAndAffixes {

	private LTrie lexiconTrie;
	private HashMap<String, Integer> stems = new HashMap<String,Integer>();
	private HashMap<String, Integer> affixes = new HashMap<String,Integer>();
	
	public StemsAndAffixes(LTrie lexiconTrie) {
		super();
		this.lexiconTrie = lexiconTrie;
	}

	private void generateStems() {
		while(lexiconTrie.hasNext()) {
			LNode n = lexiconTrie.next();
			if(generateAffixes(n)){
				putInHashMap(stems,n.getWord());
				//stems.add(n.getWord());
			}
		}
	}
	
	private void putInHashMap(HashMap<String, Integer> map, String word) {
		Integer wordCount = map.get(word);
		if (wordCount == null) {
			map.put(word, 1);
		}
		else map.put(word, wordCount.intValue() + 1);
	}

	private boolean generateAffixes(LNode n) {
		//affixes.addAll(lexiconTrie.print(n.getWord()));		
		Vector <String> possibleAffixes = new Vector<String>();
		NodeIterator<LNode> bIterator = new NodeIterator<LNode>(n);
		while (bIterator.hasNext()){
			String branch = bIterator.nextWord();
			if (branch.length() > n.getWord().length()) return false;
			possibleAffixes.add(branch);
		}
		for(String branch : possibleAffixes) {
			//System.out.println("branch " + branch);
			putInHashMap(affixes, branch); 
		}
		//affixes.addAll(possibleAffixes);
		return true;
	}

	public Set<String> getStems() {
		if(stems.isEmpty()){
			System.out.println("No stems found");
			generateStems();
		}
		return stems.keySet();
	}

	public Set<String> getAffixes() {
		if(affixes.isEmpty()){
			System.out.println("No affixes found");
			generateStems();
		}
		return affixes.keySet();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
