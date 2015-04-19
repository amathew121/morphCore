package com.ashish.util;

import java.io.UnsupportedEncodingException;

public class RootWordSplitter {

	public STrie suffixSearch(LTrie t) {
		STrie sTrie = new STrie();
		NodeIterator<LNode> words = new NodeIterator<LNode>(t.root);
		while (words.hasNextCutNode()) {
			LNode node = words.nextCutNode();
			System.out.println(node.getWord());
			try {
				sTrie.add(node);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				continue;
			} catch (PunctuationException e) {
				e.printStackTrace();
				continue;
			}
		}	
		/*while (words.hasNextRootWord()) {
			System.out.println(words.nextRootWord());
		}	*/
		System.out.println(sTrie.print());
		return sTrie;
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
