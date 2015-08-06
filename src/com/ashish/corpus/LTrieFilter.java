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

public class LTrieFilter {

	private LTrie trie;
	public void setTrie(LTrie trie) {
		this.trie = trie;
		root =trie.getRoot();
	}


	private static LNode root;

	public static void main(String[] args) throws FileNotFoundException {
		LTrieFilter cg = new LTrieFilter();
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
			/*for (int i =0; i < pathNodes.size(); i++)
				System.out.println(pathNodes.get(i) + "\t" + pathNodesCount.get(i) ) ;
*/			int count =0;
			if (pathNodes.size()>0 && !skipNext) {
				parent = pathNodes.getLast();
				 count = pathNodesCount.removeLast();
				if(--count == 0) {
					pathNodes.removeLast();
				} else {
					pathNodesCount.addLast(count);
				}
				//count = pathNodesCount.getLast();
				//System.out.println(parent.getWord() + "\t" +count + "\t" + current.getWord());

			}

			skipNext = false;
			//System.out.println(current.getWord() + " has parent " + parent.getWord());
			if(current.getNumChildren() > 1) {
				pathNodes.addLast(current);
				pathNodesCount.addLast(current.getNumChildren());
			} else if (current.getNumChildren() == 1) {
				skipNext=true;
			} else if (current.getNumChildren() == 0) {
				int stemSize = parent.getLevel();
				int pathSize = current.getLevel();
				int branchSize = (pathSize-stemSize);
				if (stemSize < branchSize) {
					//System.out.println(current.getWord() + " to be deleted after" + parent.getWord());
					deleteWord(current,parent);
					
				} else {
					//System.out.println(current.getWord() + " not cut");

				}
			}
			
			for (int i = 0; i <128 ;i++) {
				Node childNode = current.getNthChild(i);
				if(childNode != null) {
					nodesToVisit.addFirst(childNode);
				}
			}
		} while (!nodesToVisit.isEmpty());
		
	}


	private void deleteWord(Node current, Node parent) {
		Node node= root;
		for (int i = 0; i < current.getWord().length(); i++) {
			if (node.getWord().equals(parent.getWord())) {
				try {
					node.setNthChild(null, Trie.toIndex(current.getWord().charAt(i)));
				} catch (UnsupportedEncodingException | PunctuationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(current.getWord() + "\t deleted after \t" + parent.getWord());
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
