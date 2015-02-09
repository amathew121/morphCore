package com.ashish.util;

import java.io.UnsupportedEncodingException;

public class RootWordSplitter {

	public void suffixSearch(LTrie t) {
		NodeIterator<Node> words = new NodeIterator<Node>(t.root);
		while (words.hasNextCutWord()) {
			LNode node = words.nextCutNode();
		}	
		while (words.hasNextRootWord()) {
			System.out.println(words.nextRootWord());
		}	
	}
	
	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		String s = "അകാലം അകൃതം കാമലം കാമതം";
		LTrie t = new LTrie();
		t.initLexicon(s);
		t.add("മരം", true); 

		NodeIterator<Node> words = new NodeIterator<Node>(t.root);
		while(words.hasNext()) {
			System.out.println(words.nextWord ());
		}
		//System.out.println(words.hasNextRootWord());
		/*while (words.hasNextRootWord()) {
			System.out.println(words.nextRootWord());
		}*/
		
	}

}
