package com.ashish.corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Scanner;

import com.ashish.mam.MorphologicalAnalyser;
import com.ashish.util.LNode;
import com.ashish.util.LTrie;
import com.ashish.util.Node;
import com.ashish.util.SNode;
import com.ashish.util.PunctuationException;
import com.ashish.util.STrie;
import com.ashish.util.Trie;

public class LTrieFilter {

	private LTrie trie;
	private STrie fullReturnTrie = new STrie();

	public void setTrie(LTrie trie) {
		this.trie = trie;
		root = trie.getRoot();
	}

	public STrie getSuffixTrie() {
		return fullReturnTrie;
	}

	private static LNode root;

	public static void main(String[] args) throws IOException {
		LTrieFilter cg = new LTrieFilter();
		LTrie trie = new LTrie();
		cg.setTrie(trie);

		File file = new File("/home/cryptic/projectFiles/i4.txt");
		cg.readFile(file);
		cg.filter();
		System.out.println(trie.print());
		System.out.println("Suffixes ---------");
		System.out.println(cg.getSuffixTrie().print());
		System.out.println("Words deleted" + cg.getDeleteCount());

		File output = new File("newAlgo");
		File output2 = new File("oldAlgo");
		if (!output.exists())
			output.createNewFile();
		if (!output2.exists())
			output2.createNewFile();
		FileWriter fw = new FileWriter(output);
		for (SNode node : cg.getSuffixTrie().getAllWords()) {
			fw.append(node.getWord());
			fw.append("\t");
			fw.append("" + node.getOccurrences());
			fw.append("\n");
		}
		fw.close();

		FileWriter fw1 = new FileWriter(output2);
		for (LNode node : trie.method3AshishAlgo().getAllWords()) {
			fw1.append(node.getWord());
			fw1.append("\t");
			fw1.append("" + node.getOccurrences());
			fw1.append("\n");
		}
		fw1.close();
	}

	private int deleteCount = 0;

	public void readFile(File file) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		while (sc.hasNext()) {
			String token = sc.next();
			try {
				trie.add(token);
			} catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
			}
		}
	}

	public void filter() {

		// for improvement, also include occurrences as a factor
		LinkedList<LNode> nodesToVisit = new LinkedList<LNode>();
		LinkedList<LNode> pathNodes = new LinkedList<LNode>();
		LinkedList<Integer> pathNodesCount = new LinkedList<Integer>();

		nodesToVisit.addFirst(root);
		LNode current = null;
		boolean skipNext = false;
		LNode parent = root;

		do {
			boolean addChildren = false;
			current = nodesToVisit.removeFirst();
			/*
			 * for (int i =0; i < pathNodes.size(); i++)
			 * System.out.println(pathNodes.get(i) + "\t" +
			 * pathNodesCount.get(i) ) ;
			 */int count = 0;
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
			if(count <0) {
				System.out.println("EXCEOTNNSADIFND");
			}
			skipNext = false;
			System.out.println(current.getWord() + " has parent "
					+ parent.getWord());
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

					extractSuffixesByGoingUpThroughPath(pathNodes, current);
			}

			if (addChildren) {
				for (int i = 0; i < Trie.NUM_LETTERS; i++) {
					LNode childNode = (LNode) current.getNthChild(i);
					if (childNode != null) {
						nodesToVisit.addFirst(childNode);
					}
				}
			}

		} while (!nodesToVisit.isEmpty());
		
		//  SuffixTrieFilter sg = new SuffixTrieFilter();
		//  sg.setTrie(fullReturnTrie); sg.filter();
		 
		System.out.println();
	}

	private void extractSuffixesByGoingUpThroughPath(
			LinkedList<LNode> pathNodes, LNode current)  {
		LinkedList<LNode> subPathNodes = new LinkedList<LNode>() ;
		
		for (int i = pathNodes.size() - 1; i >= 0; i--) {
			LNode temp = pathNodes.get(i);
			if(temp.isEndsWord()) {
				subPathNodes.addFirst(temp);
			}
			try {
				int stemSize = temp.getWord().length();
				int pathSize = current.getWord().length();
				int branchSize = (pathSize - stemSize);
				if (branchSize < stemSize) {
					String suffix = current.getWord().substring(
							temp.getWord().length());
					System.out.println(suffix + " added to suffix trie");
					SNode suffixNode = fullReturnTrie.add(suffix);
					suffixNode.addBranch(temp.getWord());
					for(String tag : temp.getTags()) {
						suffixNode.addTags(tag);
					}
				}
			} catch (UnsupportedEncodingException  e) {
				// e.printStackTrace();
			} catch (Exception e) {
				System.out.println("ERROR:" + temp.getWord()
						+ " not found in " + current.getWord());
				throw e;
			}

		}
		
		if(!subPathNodes.isEmpty()) {
			current = subPathNodes.removeLast();
			extractSuffixesByGoingUpThroughPath(subPathNodes, current);
		}
	}

	private void deleteWord(Node current, Node parent) {
		Node node = root;
		for (int i = 0; i < current.getWord().length(); i++) {
			if (node.getWord().equals(parent.getWord())) {
				try {
					node.setNthChild(null,
							Trie.toIndex(current.getWord().charAt(i)));
				} catch (UnsupportedEncodingException | PunctuationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(current.getWord() + "\t deleted after \t"
						+ parent.getWord());
				setDeleteCount(getDeleteCount() + 1);
				return;
			}
			char ch = current.getWord().charAt(i);
			try {
				node = node.getNthChild(Trie.toIndex(ch));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PunctuationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public int getDeleteCount() {
		return deleteCount;
	}

	private void setDeleteCount(int deleteCount) {
		this.deleteCount = deleteCount;
	}
}
