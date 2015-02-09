package com.ashish.util;

class LNode extends Node {
	boolean rootWord;
	boolean endsWord;
	char nodeChar;
	float cutWordProbability;

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
}
