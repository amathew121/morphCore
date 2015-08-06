package com.ashish.util;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class STrie extends Trie<SNode> {

	static Logger logger = LogManager.getLogManager();

	public STrie() {
		super(new SNode());
	}

	@Override
	public void add(String str) throws UnsupportedEncodingException {
		// FacesContext context = FacesContext.getCurrentInstance();
		SNode current = getRoot();
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
				addChild(current, index);
				current = (SNode) current.getNthChild(index);
			}
		}
		// context.addMessage(null, new FacesMessage("Successful", "Added " +
		// str));
		current.setEndsWord(true);
	}

	public void add(LNode node) throws UnsupportedEncodingException, PunctuationException {
		HashMap<LNode,SNode> mapping = new HashMap<>();
		SNode sNode = add(this.getRoot(),node);
		mapping.put(node, sNode);
		NodeIterator<LNode> iterator = new NodeIterator<LNode>(node);
		while(iterator.hasNext()) {
			LNode nextNode  = iterator.next();
			sNode = mapping.get(nextNode.getParent());
			if(sNode == null) {
				//new Exception().printStackTrace();
				continue;
			}
			sNode = add(sNode,nextNode);
			mapping.put(nextNode, sNode);
		}
	}
	
	public SNode add(SNode baseNode,LNode toAddNode) throws UnsupportedEncodingException, PunctuationException {
		int index = toIndex(toAddNode.nodeChar);
		SNode sNode = (SNode) addChild(baseNode, index);
		merge(sNode, toAddNode);
		return sNode;
	}
	
	private void merge(SNode sNode, LNode toAddNode) {
		if(toAddNode.isEndsWord()) {
			sNode.setEndsWord(true);
			for(Tags tag : toAddNode.tags) {
				sNode.addTags(tag);
			}
		}
		
	}

	@Override
	public Node search(String str) {
		SNode current = getRoot();
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
				current = (SNode) current.getNthChild(index);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (current.isEndsWord()) {
			return current;
		}
		return current;
	}

	public List<String> print() {
		SNode current = getRoot();
		List<String> allWords;
		allWords = new ArrayList<String>();
		print(allWords, new StringBuilder(), current);
		return allWords;
	}

	@Override
	public List<String> print(String prefix) {
		List<String> suggestWords;
		suggestWords = new ArrayList<String>();
		SNode current;
		current = (SNode) search(prefix);
		print(suggestWords, new StringBuilder(current.getWord()), current);
		return suggestWords;
	}

	private void print(List<String> words, StringBuilder prefix, SNode node) {
		if (node.isEndsWord()) {
			words.add(prefix.toString());
		}
		for (int i = 0; i < NUM_LETTERS; i++) {
			if (node.getNthChild(i) != null) {
				prefix.append(toLetter(i));
				print(words, prefix, (SNode) node.getNthChild(i));
				prefix.deleteCharAt(prefix.length() - 1);
			}
		}
	}

	public static void main(String args[]) throws ClassNotFoundException,
			SQLException {
	}

}