package com.ashish.util;

import java.util.ArrayList;

class LNode extends Node {
	boolean rootWord;
	private boolean endsWord;
	ArrayList<Tags> tags;
	char nodeChar;
	double cutWordProbability;
	private int numRootChildren = 0;

	LNode() {
	};

	LNode(Node parent, char c) {
		super(parent, c);
		this.nodeChar = c;
	}

	@Override
	public String toString() {
		return "Node{" + "word=" + getWord() + ", rootWord=" + rootWord
				+ ", endsWord=" + endsWord + "}\n" + "Occurences"
				+ getOccurrences() + "Hashcode" + hashCode();
	}

	@Override
	public Node newChild(Node parent, char c) {
		return new LNode(parent, c);
	}

	public char getNodeChar() {
		return nodeChar;
	}
	
	public int getNumRootChildren() {
		return numRootChildren;
	}

	public void incNumRootChildren() {
		numRootChildren++;
	}
	
	public void setEndsWord(boolean value) {
		if(value == true) {
			endsWord = true;
			tags = new ArrayList<Tags>();
		}
	}
	
	public boolean isEndsWord() {
		return endsWord;
	}
	
	public void addTags(Tags tag) {
		if (endsWord) {
			tags.add(tag);
		}
	}
	public void getTags() {
		if (endsWord) {
			
		}
	}
}
