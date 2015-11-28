package com.ashish.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Trie<T extends Node> implements Iterator<T>{
	static public int NUM_LETTERS = 128;
	private T root;
	private LinkedList<T> nodesToVisit;
	private static char rangeStart;
	private static char rangeEnd; 

	public static char getRangeStart() {
		return rangeStart;
	}

	public static void setRangeStart(char rangeStart) {
		Trie.rangeStart = rangeStart;
	}

	public static char getRangeEnd() {
		return rangeEnd;
	}

	public static void setRangeEnd(char rangeEnd) {
		Trie.rangeEnd = rangeEnd;
	}

	public static void setProperties(char rangeStart,char rangeEnd) {
		Trie.rangeStart = rangeStart;
		Trie.rangeEnd = rangeEnd;
		NUM_LETTERS = rangeEnd -rangeStart;
		logger.log(Level.INFO, NUM_LETTERS + " charachters supported from " + rangeStart  + " to " + rangeEnd );
	}
	
	
	static Logger logger = LogManager.getLogManager();

	public T getRoot() {
		return root;
	}

	private void setRoot(T root) {
		this.root = root;
	}

	public Trie(T root) {
		this.setRoot(root);
		nodesToVisit = new LinkedList<T>();
		nodesToVisit.addFirst(root);
	}

	static protected char toLetter(int index) {
		return (char) (index + rangeStart);
	}

	static public int toIndex(char letter)
			throws UnsupportedEncodingException, PunctuationException {
		//logger.info("Got letter to convert to index: " + letter + " having int value of " + (int) letter );
		if ((letter - rangeStart) > 0 && (letter - rangeStart) < NUM_LETTERS ) {
			return letter - rangeStart;
		} else if (letter == '\u200D') {
			throw new PunctuationException("Zero Width Jointer");
		} else {
			throw new UnsupportedEncodingException(
					"Only Malayalam Characters are supported. Given char is "
							+ letter);
		}
	}

	/**
	 * Returns the node at the index given, if the node does not exist, a new node is created and returned.
	 * 
	 * @param node
	 * @param index
	 * @return
	 */
	public Node addChild(Node node, int index) {
		if (node.getNthChild(index) == null) {
			Node newChild = node.newChild(node, toLetter(index));
			newChild.setLevel(node.getLevel()+1);
			node.setNthChild(newChild, index);
			node.incNumChildren();
		}
		Node childNode = node.getNthChild(index);
		childNode.incOccurrences();
		//System.out.println(childNode.getWord() + ":\t" + childNode.getOccurrences());
		return childNode;
	}
 
	public T add(String str) throws UnsupportedEncodingException {
		// FacesContext context = FacesContext.getCurrentInstance();
		T current = root;
		for (int i = 0; i < str.length(); i++) {
			int index;
			try {
				index = toIndex(str.charAt(i));
			} catch (PunctuationException e) {
				logger.info("General Punctuation found, letter skipped");
				continue;
			}
			logger.info("index added " + index + " -- " + str.charAt(i));
			if (index >= 0 && index < NUM_LETTERS) {
				current = (T) addChild(current, index);
			}
		}
		current.setEndsWord(true);
		return current;
	}
	
	public List<T> getAllWords() {
		T current = getRoot();
		List<T> allWords;
		allWords = new ArrayList<T>();
		getWordsInternal(allWords, new StringBuilder(), current);
		Collections.sort(allWords);
		return allWords;
	}
	
	private void getWordsInternal(List<T> words, StringBuilder prefix, T node) {
		if (node.isEndsWord()) {
			words.add(node);
		}
		for (int i = 0; i < NUM_LETTERS; i++) {
			if (node.getNthChild(i) != null) {
				prefix.append(toLetter(i));
				getWordsInternal(words, prefix, (T) node.getNthChild(i));
				prefix.deleteCharAt(prefix.length() - 1);
			}
		}
	}
	
	@Override
	public boolean hasNext() {
		if (!nodesToVisit.isEmpty())
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T next() {
		if (hasNext()) {
			T current = nodesToVisit.removeFirst();
			for (int i = 0; i < NUM_LETTERS; i++) {
				T item;
				if ((item = (T) current.getNthChild(i)) != null)
					nodesToVisit.addFirst(item);
			}
			return current;
		}
		return null;
	}
	
	
	
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	public void removeNode(Node current) throws UnsupportedEncodingException, PunctuationException {
		Node parent = current.getParent() ;
		char lastChar = current.getNodeChar();
		while ( parent != null && parent.getNumChildren() == 0 ) {
			lastChar = parent.getNodeChar();
			parent = parent.getParent();
		}
		
		if (parent != null ) 
		parent.setNthChild(null, Trie.toIndex(lastChar));
	}

	public T search(String str) {
		T current = root;
		try {
			for (int i = 0; i < str.length(); i++) {
				int index;
				try {
					index = toIndex(str.charAt(i));
				} catch (PunctuationException e) {
					continue;
				}
				if (current.getNthChild(index) == null) {
					return current;
				}
				current = (T) current.getNthChild(index);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (current.isEndsWord()) {
			return current;
		}
		return current;
	}
	
	public T getNode(String str) {
		T current = root;
		int i=0;
		try {
			for ( i = 0; i < str.length(); i++) {
				int index;
				try {
					index = toIndex(str.charAt(i));
				} catch (PunctuationException e) {
					return null;
				}
				if (current.getNthChild(index) == null) {
					return null;
				}
				current = (T) current.getNthChild(index);
			}
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		if (i == str.length())
			return current;
		else 
			return null;
	}

	public List<String> print() {
		Node current = getRoot();
		List<String> allWords;
		allWords = new ArrayList<String>();
		print(allWords, new StringBuilder(), current);
		return allWords;
	}

	public List<String> print(String prefix) {
		List<String> suggestWords;
		suggestWords = new ArrayList<String>();
		LNode current;
		current = (LNode) search(prefix);
		print(suggestWords, new StringBuilder(current.getWord()), current);
		return suggestWords;
	}

	private void print(List<String> words, StringBuilder prefix, Node node) {
		if (node.isEndsWord() || node.getNumChildren() == 0) {
			words.add(prefix.toString());
		}
		for (int i = 0; i < NUM_LETTERS; i++) {
			if (node.getNthChild(i) != null) {
				prefix.append(toLetter(i));
				print(words, prefix, (T) node.getNthChild(i));
				prefix.deleteCharAt(prefix.length() - 1);
			}
		}
	}

}


class BranchIterator<T extends Node> implements Iterator<T> {
	//static protected final int NUM_LETTERS = 128;
	private T base;
	private LinkedList<T> nodesToVisit;
	private LinkedList<T> branchesToVisit;

	public BranchIterator(T node) {
		super();
		this.base = node;
		nodesToVisit = new LinkedList<T>();
		for (int i = 0; i < Trie.NUM_LETTERS; i++) {
			T item;
			if ((item = (T) base.getNthChild(i)) != null)
				nodesToVisit.addFirst(item);
		}
		branchesToVisit = new LinkedList<T> ();
		T nextNode = getNextBranch();
		if(nextNode != null) {
			branchesToVisit.add(nextNode);
		}
	}
	
	@Override
	public boolean hasNext() {
		if (!branchesToVisit.isEmpty())
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T next() {
		if (hasNext()) {
			T branchNode = getNextBranch();
			if (branchNode != null) {
				branchesToVisit.addFirst(branchNode);
			}
			return branchesToVisit.removeFirst();
		}
			
			return null;
	}
	
	private T getNextBranch() {
		if (!nodesToVisit.isEmpty()) {
		T current = nodesToVisit.removeFirst();
		//System.out.print(current.getWord() + "->");
		if(current instanceof LNode) {
			Node node = current;
			if(node.isEndsWord()) {
				return current;
			}
		} 
		if (current.getNumChildren() > 1 ) {
			//System.out.println("Found Branch");
			return current;
		} else {
//			System.out.println("c");
			for (int i = 0; i < Trie.NUM_LETTERS; i++) {
				T item;
				if ((item = (T) current.getNthChild(i)) != null)
					if ((item.getLevel() - current.getLevel())<current.getLevel() )
						nodesToVisit.addFirst(item);
			}
			//System.out.println("" +nodesToVisit);

			return getNextBranch();
		}
		} else {
			//System.out.println("Finished");
			return null;
		} 
	}

	@Override
	public void remove() {
	}
	
}



class NodeIterator<T extends Node> implements Iterator<T> {
	//static protected final int NUM_LETTERS = 128;
	T base;
	private LinkedList<T> nodesToVisit;
	private boolean visiting = false;
	private LinkedList<T> cutNodesToVisit;
	private LinkedList<T> rootNodesToVisit;
	private boolean firstTime = true;

	static Logger logger = LogManager.getLogManager();
	
	public NodeIterator(T node) {
		super();
		this.base = node;
		nodesToVisit = new LinkedList<T>();
		cutNodesToVisit = new LinkedList<T>();
		rootNodesToVisit = new LinkedList<T>();
		nodesToVisit.addFirst(base);
		cutNodesToVisit.addFirst(base);
		rootNodesToVisit.addFirst(base);
		firstTime = true;

	}

	@Override
	public boolean hasNext() {
		if (!nodesToVisit.isEmpty())
			return true;
		else {
			visiting = false;
			nodesToVisit = new LinkedList<T>();
			nodesToVisit.addFirst(base);
			return false;
		}
	}

	public boolean hasNextWord() {
		return hasNext();
	}

	public boolean hasNextRootWord() {
		if (!rootNodesToVisit.isEmpty())
			return true;
		else {
			if (!visiting)
				rootNodesToVisit.addFirst(base);
			visiting = false;
			return false;
		}
	}

	public boolean hasNextCutNode() {
		if (base instanceof LNode) {
			if(firstTime) {
				firstTime = false;
				findAllCutNodes();
			}
			if(!cutNodesToVisit.isEmpty())
				return true;
			else
				return false;						
		}
		return false;
	}
	
	public T nextCutNode() {
		return cutNodesToVisit.removeFirst();
	}
	
	private void findAllCutNodes() {
		cutNodesToVisit.removeFirst();
		while (hasNext()) {
			T current = next();
			if(current instanceof LNode) {
				LNode n = (LNode) current;
				if (n.cutWordProbability > 0.75) {
					cutNodesToVisit.addFirst(current);
					logger.info("Node added as cut node : [" + current.getWord() + "]");
				}
			}
		}		
	}

	public String nextWord() {
		if (!visiting)
			visiting = true;
		while (hasNext()) {
			Node n =  next();
			if (n.isEndsWord()) {
				return n.getWord();
			}
		}
		return "";
	}

	public String nextRootWord() {
		if (!visiting) {
			visiting = true;
			rootNodesToVisit.removeFirst();
		}

		while (hasNext()) {
			T n = next();
			if (((LNode) n).rootWord) {
				rootNodesToVisit.addFirst(n);
			}
		}
		return rootNodesToVisit.removeFirst().getWord();
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	public T next() {
		T current = nodesToVisit.removeFirst();
		for (int i = 0; i < Trie.NUM_LETTERS; i++) {
			T item;
			if ((item = (T) current.getNthChild(i)) != null)
				nodesToVisit.addFirst(item);
		}
		return current;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
