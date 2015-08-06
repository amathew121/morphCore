package com.ashish.corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Scanner;

import com.ashish.util.LNode;
import com.ashish.util.LTrie;
import com.ashish.util.Node;
import com.ashish.util.PunctuationException;
import com.ashish.util.Trie;

public class SuffixTrieFilter {

	private LTrie trie;
	public void setTrie(LTrie trie) {
		this.trie = trie;
		root =trie.getRoot();
	}


	private static LNode root;

	public static void main(String[] args) throws FileNotFoundException {
		SuffixTrieFilter cg = new SuffixTrieFilter();
		LTrie trie = new LTrie();
		cg.setTrie(trie);

		File file = new File("D:\\projectFiles\\i2.txt");
		cg.readFile(file);
		cg.filter();
		System.out.println(trie.print());
		System.out.println("Words deleted" + cg.getDeleteCount());
	}


	private int deleteCount=0;
	
	
	public void readFile(File file) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		while (sc.hasNext()) {
			String token = sc.next();
			try {
				trie.add(token);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void filter(){
		

		// for improvement, also include occurrences as a factor
		LinkedList<Node> nodesToVisit = new LinkedList<Node>();
		LinkedList<Node> pathNodes = new LinkedList<Node>();
		LinkedList<Integer> pathNodesCount = new LinkedList<Integer>();

		nodesToVisit.addFirst(root);
		Node current = null;
		boolean skipNext = false;
		Node parent = root; 

		do {
			current = nodesToVisit.removeFirst();

			for (int i = 0; i <128 ;i++) {
				Node childNode = current.getNthChild(i);
				if(childNode != null ) {
					if (childNode.getOccurrences() <= 2 ) { 
						current.setNthChild(null, i);
						deleteCount++;
					} else {
						nodesToVisit.addFirst(childNode);
					}
				}
			}
		} while (!nodesToVisit.isEmpty());
		
	}


	public int getDeleteCount() {
		return deleteCount;
	}

}
