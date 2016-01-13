package com.ashish.corpus;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.ashish.util.LogManager;
import com.ashish.util.SNode;
import com.ashish.util.STrie;
import com.ashish.util.Trie;

public class RecursiveSuffixFilterAndExtractor {

	private STrie trie;
	private static SNode root;
	static Logger logger = LogManager.getLogManager();

	public void setTrie(STrie trie) {
		this.trie = trie;
		root = trie.getRoot();
	}

	public static void main(String[] args) {
		int countAdded = 0;
		int maxStep = 0;
		do { 
			countAdded = 2;
			maxStep++;
			String msg = maxStep + " round of filtering completed.";
			System.out.println(msg);
			//logger.info(msg);
		} while (countAdded !=0 && maxStep <5);
	}
	
	public void filterRecursively() {
		int countAdded = 0;
		int maxStep = 0;
		do { 
			countAdded = extractSuffixes();
			maxStep++;
			String msg = maxStep + " round of filtering completed.";
			System.out.println(msg);
			logger.info(msg);
		} while (countAdded !=0 && maxStep <5);
	}

	
	private int extractSuffixes() {
		int countAdded=0;
		
		// for improvement, also include occurrences as a factor
		LinkedList<SNode> nodesToVisit = new LinkedList<SNode>();
		LinkedList<SNode> pathNodes = new LinkedList<SNode>();
		LinkedList<Integer> pathNodesCount = new LinkedList<Integer>();

		nodesToVisit.addFirst(root);
		SNode current = null;
		boolean skipNext = false;
		SNode parent = root;

		do {
			boolean addChildren = false;
			current = nodesToVisit.removeFirst();
			
			int count = 0;
			boolean getParentAgain = false;
			do {
				if (pathNodes.size() > 0 && !skipNext) {
					parent = pathNodes.getLast();
					count = pathNodesCount.removeLast();
					if (count == 0) {
						pathNodes.removeLast();
						getParentAgain = true;
					}
					else {
						pathNodesCount.addLast(--count);
						getParentAgain = false;
					}
				}
			} while (getParentAgain);

			skipNext = false;
			if (current.getNumChildren() > 1) {
				pathNodes.addLast(current);
				pathNodesCount.addLast(current.getNumChildren());
				addChildren = true;
			} else if (current.getNumChildren() == 1) {
				addChildren = true;
				if (current.isEndsWord()) {
					pathNodes.addLast(current);
					pathNodesCount.addLast(1);
				} else {
					skipNext = true;
				}

			} else if (current.getNumChildren() == 0) {
					countAdded += extractSuffixesByGoingUpThroughPath(pathNodes, current);
			}

			if (addChildren) {
				for (int i = 0; i < Trie.NUM_LETTERS; i++) {
					SNode childNode = (SNode) current.getNthChild(i);
					if (childNode != null) {
						nodesToVisit.addFirst(childNode);
					}
				}
			}

		} while (!nodesToVisit.isEmpty());
		
		//  SuffixTrieFilter sg = new SuffixTrieFilter();
		//  sg.setTrie(fullReturnTrie); sg.filter();
		 String msg = "SuffixExtraction Complete, branches identified and added " + countAdded;
		 System.out.println(msg);
		logger.info(msg);
		return countAdded;
	}

	private int extractSuffixesByGoingUpThroughPath(
			LinkedList<SNode> pathNodes, SNode current)  {
		int countAdded = 0; 
			SNode temp = pathNodes.getFirst();
			try {
				String suffix = current.getWord().substring(
						temp.getWord().length());
				SNode suffixNode  = trie.getNode(suffix);
				if (!current.isDirty()) {
					if(temp.isEndsWord() || (suffixNode != null && suffixNode.isEndsWord())) {
	
						logger.fine(suffix + " added to suffix trie");
						suffixNode = trie.add(suffix);
						countAdded++;
						current.setDirty(true);
						suffixNode.addBranch(temp.getWord());
						suffixNode.setOccurrences(suffixNode.getOccurrences() + current.getOccurrences() -1 );
						if(current.getTags() != null) {
							for(String tag : current.getTags()) {
								if(!temp.getTags().contains(tag)) {
									suffixNode.addTags(tag);
								}
							}
						}
						
						//temp.setNthChild(null,Trie.toIndex(suffix.charAt(0)));
						//temp.decNumChildren();
						
					}
				}
			} catch (UnsupportedEncodingException e) {
				 e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				logger.severe("ERROR:" + temp.getWord()
						+ " not found in " + current.getWord());
				throw e;
			}

		return countAdded; 
	}
}
