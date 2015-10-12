package com.ashish.util;

import java.util.ArrayList;
import java.util.Collections;

public class LNode extends Node {
	boolean rootWord;
	ArrayList<String> tags;
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
		return getWord();
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
			tags = new ArrayList<String>();
		}
	}
	
	public void addTags(String tag) {
		if (endsWord) {
			tags.add(tag);
		}
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	
	public String getNodeText() {
		StringBuilder nodeText = new StringBuilder();
		nodeText.append("Occurrences:\t" + this.getOccurrences() + "\n");
		nodeText.append("Parent:\t" + this.getParent().getWord() + "\n");
		nodeText.append("Level:\t" + this.getLevel() + "\n");
		nodeText.append("Tags:\t" + this.getTags() + "\n");
		nodeText.append("Children:\t" + this.getNumChildren() + "\n");
		return nodeText.toString();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		LNode newNode = new LNode();
		newNode.rootWord = this.rootWord;
		newNode.endsWord = this.endsWord;
		Collections.copy(newNode.tags, this.tags);
		newNode.nodeChar = this.nodeChar;
		newNode.cutWordProbability = this.cutWordProbability;
		newNode.numRootChildren = this.numRootChildren;
		return newNode;
	}
	
	
}
