package com.ashish.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LTrie extends Trie<LNode> {

	public LTrie() {
		super(new LNode());
	}

	@Override
	public void add(String str) throws UnsupportedEncodingException {
		add(str, false);
	}

	public void add(String str, boolean isRootForm)
			throws UnsupportedEncodingException {
		// FacesContext context = FacesContext.getCurrentInstance();
		LNode current = root;
		for (int i = 0; i < str.length(); i++) {
			int index = toIndex(str.charAt(i));
			System.out.println("index added " + index + " -- " + str.charAt(i));
			if (index >= 0 && index < NUM_LETTERS) {
				addChild(current, index);
				current = (LNode) current.getNthChild(index);
			}
		}
		// context.addMessage(null, new FacesMessage("Successful", "Added " +
		// str));
		current.endsWord = true;
		if (isRootForm) {
			((LNode) current).rootWord = true;
			// context.addMessage(null, new FacesMessage("Successful",
			// "Added as Root Word" + str));

		}
	}

	@Override
	public Node search(String str) {
		LNode current = root;
		try {
			for (int i = 0; i < str.length(); i++) {
				int index = toIndex(str.charAt(i));
				if (current.getNthChild(index) == null) {
					return current;
				}
				current = (LNode) current.getNthChild(index);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (current.endsWord) {
			return current;
		}
		return current;
	}

	public Node searchRootWord(String str) {
		LNode current = root;
		try {
			for (int i = 0; i < str.length(); i++) {
				int index = toIndex(str.charAt(i));
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

	public List<String> print() {
		LNode current = root;
		List<String> allWords;
		allWords = new ArrayList<String>();
		print(allWords, new StringBuilder(), current);
		return allWords;
	}

	@Override
	public List<String> print(String prefix) {
		List<String> suggestWords;
		suggestWords = new ArrayList<String>();
		LNode current;
		current = (LNode) search(prefix);
		print(suggestWords, new StringBuilder(current.getWord()), current);
		return suggestWords;
	}

	private void print(List<String> words, StringBuilder prefix, LNode node) {
		if (node.endsWord) {
			words.add(prefix.toString());
		}
		for (int i = 0; i < NUM_LETTERS; i++) {
			if (node.getNthChild(i) != null) {
				prefix.append(toLetter(i));
				print(words, prefix, (LNode) node.getNthChild(i));
				prefix.deleteCharAt(prefix.length() - 1);
			}
		}
	}

	public List<String> getBranches() {
		throw new UnsupportedOperationException();
	}

	/*
	 * public HashMap<String, Integer> hashMap; public LTrie suffixes;
	 * 
	 * public ArrayList<String> getSuffixHashes() { hashMap = new
	 * HashMap<String, Integer>(); suffixes = new LTrie(); LNode current = root;
	 * return getSuffixHashes(current, hashMap); // Collection<Integer> co =
	 * hashMap.values(); // return co; }
	 */

	/*
	 * private ArrayList<String> getSuffixHashes(LNode node, HashMap<String,
	 * Integer> hashMap) { ArrayList<String> suffixHashes = new
	 * ArrayList<String>(); if (node.endsWord) { String hash =
	 * ""+node.getNodeChar(); try { suffixes.add(hash); suffixHashes.add(hash);
	 * countHashes(hash, hashMap); return suffixHashes; } catch
	 * (UnsupportedEncodingException e) { e.printStackTrace(); } } for (int i =
	 * 0; i < NUM_LETTERS; i++) { LNode tempNode; if ((tempNode = (LNode)
	 * node.getNthChild(i)) != null) { ArrayList<String> tempList =
	 * appendHashes(tempNode, node.getNodeChar(), hashMap);
	 * suffixHashes.addAll(tempList); } } return suffixHashes; }
	 */
	/*
	 * private ArrayList<String> appendHashes(LNode node, char hash,
	 * HashMap<String, Integer> hashMap) { ArrayList<String> temp =
	 * getSuffixHashes(node, hashMap); try { for (int i = 0; i < temp.size();
	 * i++) { String str = temp.get(i); str = hash+str; temp.set(i, str);
	 * suffixes.add(str); countHashes(str, hashMap); } } catch
	 * (UnsupportedEncodingException e) { e.printStackTrace(); } return temp; }
	 */

	/*
	 * private void countHashes(String temp, HashMap<String, Integer> hashMap) {
	 * Integer count = hashMap.get(temp); if (count != null) { //
	 * System.out.println("key "+ temp+" value = "+count); hashMap.put(temp,
	 * ++count); } else hashMap.put(temp, 1); }
	 */

	public static void main(String args[]) {
		String s = "അകാലം അകൃതം കാമലം കാമതം";
		LTrie t = new LTrie();
		try {
			t.initLexicon(s);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(0);
		}

		// String s =
		// "à´‰à´¤àµ�à´¤à´°à´¾à´«àµ�à´°à´¿à´•àµ�à´•à´¯à´¿àµ½ à´¨à´¿à´¨àµ�à´¨àµ�à´³àµ�à´³ à´²à´¤àµ�à´¤àµ€àµ» à´•àµ�à´°à´¿à´¸àµ�à´¤àµ€à´¯à´šà´¿à´¨àµ�à´¤à´•à´¨àµ�à´‚, à´¦àµˆà´µà´¶à´¾à´¸àµ�à´¤àµ�à´°à´œàµ�à´žà´¨àµ�à´‚ à´®àµ†à´¤àµ�à´°à´¾à´¨àµ�à´®à´¾à´¯à´¿à´°àµ�à´¨àµ�à´¨àµ� à´¹à´¿à´ªàµ�à´ªàµ‹à´¯à´¿à´²àµ† à´…à´—à´¸àµ�à´¤àµ€à´¨àµ‹à´¸àµ�. à´µà´¿à´¶àµ�à´¦àµ�à´§ à´…à´—à´¸àµ�à´±àµ�à´±à´¿àµ» (à´¸àµ†à´¯àµ�à´¨àµ�à´±àµ� à´…à´—à´¸àµ�à´±àµ�à´±à´¿àµ»), à´µà´¿à´¶àµ�à´¦àµ�à´§ à´“à´¸àµ�à´±àµ�à´±à´¿àµ», à´”à´±àµ‡à´²à´¿à´¯àµ�à´¸àµ� à´…à´—à´¸àµ�à´¤àµ€à´¨àµ‹à´¸àµ� à´Žà´¨àµ�à´¨àµ€ à´ªàµ‡à´°àµ�à´•à´³à´¿à´²àµ�à´‚ à´…à´¦àµ�à´¦àµ‡à´¹à´‚ à´…à´±à´¿à´¯à´ªàµ�à´ªàµ†à´Ÿàµ�à´¨àµ�à´¨àµ�. à´±àµ‹à´®àµ» à´•à´¤àµ�à´¤àµ‹à´²à´¿à´•àµ�à´•à´¾ à´¸à´­à´¯àµ�à´‚ à´†à´‚à´—àµ�à´²à´¿à´•àµ�à´•àµ» à´•àµ‚à´Ÿàµ�à´Ÿà´¾à´¯àµ�à´®à´¯àµ�à´‚ à´…à´—à´¸àµ�à´¤àµ€à´¨àµ‹à´¸à´¿à´¨àµ† à´µà´¿à´¶àµ�à´¦àµ�à´§à´¨àµ�à´‚ à´µàµ‡à´¦à´ªà´¾à´°à´‚à´—à´¤à´¨àµ�à´®à´¾à´°à´¿àµ½ à´®àµ�à´®àµ�à´ªà´¨àµ�à´‚ à´†à´¯à´¿ à´®à´¾à´¨à´¿à´•àµ�à´•àµ�à´¨àµ�à´¨àµ�. à´ªàµ�à´°àµŠà´Ÿàµ�à´Ÿà´¸àµ�à´±àµ�à´±à´¨àµ�à´±àµ� à´¨à´µàµ€à´•à´°à´£à´¤àµ�à´¤àµ† à´�à´±àµ�à´±à´µàµ�à´®àµ‡à´±àµ† à´¸àµ�à´µà´¾à´§àµ€à´¨à´¿à´šàµ�à´š à´¸à´­à´¾à´ªà´¿à´¤à´¾à´µàµ� à´…à´¦àµ�à´¦àµ‡à´¹à´®à´¾à´£àµ�. à´…à´—à´¸àµ�à´¤àµ€à´¨àµ‹à´¸à´¿à´¨àµ�à´±àµ† à´šà´¿à´¨àµ�à´¤à´¯àµ�à´‚, à´¤à´¤àµ�à´¤àµ�à´µà´šà´¿à´¨àµ�à´¤à´¯à´¿à´²àµ�à´‚ à´¦àµˆà´µà´¶à´¾à´¸àµ�à´¤àµ�à´°à´¤àµ�à´¤à´¿à´²àµ�à´‚ à´…à´¦àµ�à´¦àµ‡à´¹à´‚ à´°àµ‚à´ªà´ªàµ�à´ªàµ†à´Ÿàµ�à´¤àµ�à´¤à´¿à´¯ à´¨à´¿à´²à´ªà´¾à´Ÿàµ�à´•à´³àµ�à´‚ à´®à´¦àµ�à´§àµ�à´¯à´•à´¾à´² à´²àµ‹à´•à´µàµ€à´•àµ�à´·à´£à´¤àµ�à´¤àµ† à´…à´Ÿà´¿à´¸àµ�à´¥à´¾à´¨à´ªà´°à´®à´¾à´¯à´¿ à´¸àµ�à´µà´¾à´§àµ€à´¨à´¿à´šàµ�à´šàµ�. à´®à´¨àµ�à´·àµ�à´¯à´¸àµ�à´µà´¾à´¤à´¨àµ�à´¤àµ�à´°àµ�à´¯à´¤àµ�à´¤à´¿à´¨àµ�â€Œ à´¦àµˆà´µà´¤àµ�à´¤à´¿à´¨àµ�à´±àµ† à´•àµƒà´ª à´’à´´à´¿à´šàµ�à´šàµ�à´•àµ‚à´Ÿà´¾à´¤àµ�à´¤à´¤à´¾à´£àµ†à´¨àµ�à´¨àµ� à´…à´¦àµ�à´¦àµ‡à´¹à´‚ à´µà´¿à´¶àµ�à´µà´¸à´¿à´šàµ�à´šàµ�. à´¤àµ�à´Ÿà´™àµ�à´™à´¿à´¯ à´®à´¤, à´°à´¾à´·àµ�à´Ÿàµ�à´°àµ€à´¯ à´¸à´™àµ�à´•à´²àµ�à´ªà´™àµ�à´™àµ¾ à´•àµ�à´°àµˆà´¸àµ�à´¤à´µà´²àµ‹à´•à´¤àµ�à´¤à´¿à´¨àµ�â€Œ à´¸à´®àµ�à´®à´¾à´¨à´¿à´šàµ�à´šà´¤àµ� à´…à´—à´¸àµ�à´¤àµ€à´¨àµ‹à´¸à´¾à´£àµ�â€Œ";

		//method1HeuristicCut(t);
		t.method2RootWordCut();
	}

	public void initLexicon(String s)
			throws UnsupportedEncodingException {
		StringTokenizer st = new StringTokenizer(s);

		while (st.hasMoreTokens()) {
			String tkn = st.nextToken();
			// System.out.println(tkn);
			add(tkn, false);
		}
	}

	private void method2RootWordCut() {
		generateCutWordProbability();
	}

	private void generateCutWordProbability() {
		
		
	}

	private static void method1HeuristicCut(LTrie trie)
			throws UnsupportedEncodingException {
		System.out.println("PRINTING ALL IN TRIE");
		for (String str : trie.print()) {
			System.out.println(str);
		}
		/*
		 * for (String str : t.getSuffixHashes()) { System.out.println(str); }
		 * Iterator<LNode> iterator = t.suffixes; while (iterator.hasNext()) {
		 * LNode temp = iterator.next(); System.out.println(temp); }
		 */

		StemsAndAffixes stemsAndAffixes = new StemsAndAffixes(trie);
		System.out.println(stemsAndAffixes.getStems());
		System.out.println(stemsAndAffixes.getAffixes());
	}

}