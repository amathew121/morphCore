package com.ashish.util;

import java.util.Iterator;
import java.util.LinkedList;

public class DepthLimitedIterator<T extends Node> implements Iterator<T> {
	//static protected final int NUM_LETTERS = 128;
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
				for (int i = 0; i < Trie.NUM_LETTERS; i++) {
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