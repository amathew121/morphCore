package com.ashish.util;

import java.util.ArrayList;
import java.util.HashMap;

class SNode extends Node {
	private boolean endsWord;
	HashMap<Tags,Integer> tags = new HashMap<Tags, Integer>();;
	char nodeChar;
	int frequency; 
	
	SNode() {
	};

	SNode(Node parent, char c) {
		super(parent, c);
		this.nodeChar = c;
	}

	@Override
	public String toString() {
		return "Node{" + "word=" + getWord()
				+ ", endsWord=" + endsWord + "}\n" + "Occurences"
				+ getOccurrences() + "Hashcode" + hashCode();
	}

	@Override
	public Node newChild(Node parent, char c) {
		return new SNode(parent, c);
	}

	public char getNodeChar() {
		return nodeChar;
	}
	
	public void setEndsWord(boolean value) {
		if(value == true) {
			endsWord = true;
		}
	}
	
	public boolean isEndsWord() {
		return endsWord;
	}
	
	public void addTags(Tags tag) {
		if (endsWord) {
			  int count = this.tags.get(tag);
			  tags.put(tag, count++);
		}
	}
	public void getTags() {
		if (endsWord) {
			
		}
	}
}

