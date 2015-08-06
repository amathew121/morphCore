package com.ashish.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public abstract class Trie<T extends Node> implements Iterator<T>{
	static protected final int NUM_LETTERS = 128;
	private T root;
	private LinkedList<T> nodesToVisit;

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
		return (char) (index + '\u0D00');
	}

	static public int toIndex(char letter)
			throws UnsupportedEncodingException, PunctuationException {
		//logger.info("Got letter to convert to index: " + letter + " having int value of " + (int) letter );
		if ((letter - '\u0D00') > 0 && (letter - '\u0D00') < 128) {
			return letter - '\u0D00';
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
 
	public abstract void add(String s) throws UnsupportedEncodingException;

	public abstract Node search(String s);

	public abstract List<String> print(String prefix);

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
		throw new UnsupportedOperationException();
	}


}

class BranchIterator<T extends Node> implements Iterator<T> {
	static protected final int NUM_LETTERS = 128;
	private T base;
	private LinkedList<T> nodesToVisit;
	private LinkedList<T> branchesToVisit;

	public BranchIterator(T node) {
		super();
		this.base = node;
		nodesToVisit = new LinkedList<T>();
		for (int i = 0; i < NUM_LETTERS; i++) {
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
			LNode node = (LNode) current;
			if(node.isEndsWord()) {
				return current;
			}
		} 
		if (current.getNumChildren() > 1 ) {
			//System.out.println("Found Branch");
			return current;
		} else {
//			System.out.println("c");
			for (int i = 0; i < NUM_LETTERS; i++) {
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

class DepthLimitedIterator<T extends Node> implements Iterator<T> {
	static protected final int NUM_LETTERS = 128;
	private T base;
	private LinkedList<T> nodesToVisit;
	private int depth = 0;

	public DepthLimitedIterator(T node, int depth) {
		super();
		this.base = node;
		this.depth = depth;
		nodesToVisit = new LinkedList<T>();
		nodesToVisit.addFirst(base);
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
			if (current.getLevel() < depth) {
				for (int i = 0; i < NUM_LETTERS; i++) {
					T item;
					if ((item = (T) current.getNthChild(i)) != null)
						nodesToVisit.addFirst(item);
				}
			}
			return current;
		}
		return null;
	}

	@Override
	public void remove() {
	}
	
}

class NodeIterator<T extends Node> implements Iterator<T> {
	static protected final int NUM_LETTERS = 128;
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
			LNode n = (LNode) next();
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
		for (int i = 0; i < NUM_LETTERS; i++) {
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
