package com.ashish.util;

import java.util.HashMap;
import java.util.Set;

public class SNode extends Node {
	HashMap<String,Integer> tags = new HashMap<String, Integer>();
	HashMap<String, Integer> branches = new  HashMap<String, Integer>();
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
		return getWord();
	}

	@Override
	public Node newChild(Node parent, char c) {
		return new SNode(parent, c);
	}

	public char getNodeChar() {
		return nodeChar;
	}
	
	public void addTags(String tag) {
		if (endsWord) {
			  Integer count = this.tags.get(tag);
			  if (count == null ) {
				  tags.put(tag,1);
			  } else {
				  tags.put(tag, count++);
			  }
		}
	}
	
	public Set<String> getTags() {
		return this.tags.keySet();
	}
	
	public Set<String> getBranches() {
		return this.branches.keySet();
	}
	
	public void addBranch(String branch) {
		if (endsWord) {
			  Integer count = this.branches.get(branch);
			  if (count == null)
				  branches.put(branch, 1);
			  else 
				  branches.put(branch, count++);
		}
	}
	

	
	public String getNodeText() {
		StringBuilder nodeText = new StringBuilder();
		nodeText.append("Occurrences:\t" + this.getOccurrences() + "\n");
		nodeText.append("Parent:\t" + this.getParent().getWord() + "\n");
		nodeText.append("Level:\t" + this.getLevel() + "\n");
		nodeText.append("Tags:\t" + this.getTags() + "\n");
		nodeText.append("Brances:\t" + this.getBranches() + "\n");
		nodeText.append("Children:\t" + this.getNumChildren() + "\n");
		return nodeText.toString();
	}
}

