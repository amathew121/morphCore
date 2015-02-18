package com.ashish.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Trie<T extends Node> implements Iterator<T> {
	static protected final int NUM_LETTERS = 128;
	private static final int CUTWORD = 1;
	T root;
	private LinkedList<T> nodesToVisit;
	private int mode;

	public Trie(T root) {
		this.root = root;
		nodesToVisit = new LinkedList<T>();
		nodesToVisit.addFirst(root);
	}

	static protected char toLetter(int index) {
		return (char) (index + '\u0D00');
	}

	static protected int toIndex(char letter)
			throws UnsupportedEncodingException {
		if ((letter - '\u0D00') > 0 && (letter - '\u0D00') < 128) {
			return letter - '\u0D00';
		} else {
			throw new UnsupportedEncodingException(
					"Only Malayalam Characters are supported. Given char is "
							+ letter);
		}
	}

	public void addChild(Node node, int index) {
		node.incOccurrences();
		if (node.getNthChild(index) == null) {
			node.setNthChild(node.newChild(node, toLetter(index)), index);
			node.incNumChildren();
		}
		if (mode == CUTWORD) {
			LNode lnode = (LNode) node;
			lnode.cutWordProbability = calculateProbability(node);
		}
	}
 
	//FIXME: THis function needs to be fixed or removed.
	private float calculateProbability(Node node) {
		ArrayList<String> editWords = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();
		if (node.getNumChildren() < 2)
			return 0.0f;
		if (node.equals(root) || node.getParent().equals(root)
				|| node.getParent().getParent().equals(root))
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

class NodeIterator<T extends Node> implements Iterator<T> {
	static protected final int NUM_LETTERS = 128;
	T base;
	private LinkedList<T> nodesToVisit;
	private boolean visiting = false;
	private LinkedList<LNode> cutNodesToVisit;
	private LinkedList<T> rootNodesToVisit;

	public NodeIterator(T node) {
		super();
		this.base = node;
		nodesToVisit = new LinkedList<T>();
		cutNodesToVisit = new LinkedList<LNode>();
		rootNodesToVisit = new LinkedList<T>();
		nodesToVisit.addFirst(base);
		cutNodesToVisit.addFirst((LNode) base);
		rootNodesToVisit.addFirst(base);

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

	public boolean hasNextCutWord() {
		if (base instanceof LNode) {
			if (!cutNodesToVisit.isEmpty())
				return true;
			else {
				visiting = false;
				return false;
			}
		} else
			return false;
	}

	public String nextWord() {
		if (!visiting)
			visiting = true;
		while (hasNext()) {
			LNode n = (LNode) next();
			if (n.endsWord) {
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
	public LNode nextCutNode() {
		if (!visiting) {
			visiting = true;
			rootNodesToVisit.removeFirst();
		}
		while (hasNext()) {
			T current = nodesToVisit.removeFirst();
			for (int i = 0; i < NUM_LETTERS; i++) {
				T item;
				if ((item = (T) current.getNthChild(i)) != null)
					nodesToVisit.addFirst(item);
			}
			LNode n = (LNode) current;
			if (n.cutWordProbability > 0.75) {
				cutNodesToVisit.addFirst(n);
			}
		}
		return cutNodesToVisit.removeFirst();
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
