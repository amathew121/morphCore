package com.ashish.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import com.ashish.mam.Config;

public class SNode extends Node {
	HashMap<String,Integer> tags = new HashMap<String, Integer>();
	HashMap<String, Integer> branches = new  HashMap<String, Integer>();
	HashMap<String, Integer> inflections = new  HashMap<String, Integer>();

	char nodeChar;
	int frequency;
	private double suffixWeight; 
	private double inflectionsConfidence;
	private SNode startNode = null;
	
	public double getSuffixWeight() {
		return suffixWeight;
	}

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
	
	public void addInflections(String inflection) {
		if (endsWord) {
			  Integer count = this.inflections.get(inflection);
			  if (count == null ) {
				  inflections.put(inflection,1);
			  } else {
				  inflections.put(inflection, count++);
			  }
		}
	}
	
	public Set<String> getTags() {
		return this.tags.keySet();
	}
	
	public Set<String> getBranches() {
		return this.branches.keySet();
	}
	
	public Set<String> getInflections() {
		return this.inflections.keySet();
	}
	
	public int getBranchCount(String str) {
		Integer i = branches.get(str);
		if (i != null) return i.intValue(); else return 0;
	}
	
	public int getTagCount(String str) {
		Integer i = tags.get(str);
		if (i != null) return i.intValue(); else return 0;
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
		nodeText.append("Weight:\t" + this.suffixWeight + "\n");
		//nodeText.append("Confidence:\t" + this.getInflectionsConfidence() + "\n");
		//nodeText.append("Inflection Pattern:\t" + inflections.values()+ "\n");
		nodeText.append("Parent:\t" + this.getParent().getWord() + "\n");
		nodeText.append("Level:\t" + this.getLevel() + "\n");
		nodeText.append("Tags:\t" + this.getTags() + "\n");
		//nodeText.append("Brances:\t" + this.getBranches() + "\n");
		if(Config.inflectionsIdentification) {
			nodeText.append("Inflections:\t" + this.getInflections() + "\n");
		}
		nodeText.append("Children:\t" + this.getNumChildren() + "\n");

		return nodeText.toString();
	}

	public void setSuffixWeight(double suffixWeight) {
		this.suffixWeight= suffixWeight;
		
	}

	public double[] getInflectionPattern() {
		
		double[] x = new double[inflections.values().size()]; 
		int i = 0;
		for(Integer value : inflections.values()) {
			x[i++] = value.doubleValue();
		}
		return x;
	}

	public double getInflectionsConfidence() {
		return inflectionsConfidence;
	}

	public void setInflectionsConfidence(double inflectionsConfidence) {
		this.inflectionsConfidence = inflectionsConfidence;
	}

	public SNode getStartNode() {
		return startNode;
	}

	public void setStartNode(SNode startNode) {
		this.startNode = startNode;
	}



}

