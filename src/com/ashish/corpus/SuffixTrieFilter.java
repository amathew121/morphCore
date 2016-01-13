package com.ashish.corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import com.ashish.mam.Config;
import com.ashish.util.Node;
import com.ashish.util.SNode;
import com.ashish.util.STrie;
import com.ashish.util.Trie;

public class SuffixTrieFilter {

	private STrie trie;
	public void setTrie(STrie trie) {
		this.trie = trie;
		root =trie.getRoot();
	}


	private static SNode root;

	public static void main(String[] args) throws FileNotFoundException {
		SuffixTrieFilter cg = new SuffixTrieFilter();
		STrie trie = new STrie();
		cg.setTrie(trie);

		File file = new File("/home/cryptic/projectFiles/data/datuk.txt");
		cg.readFile(file);
		cg.filter();
		//System.out.println(trie.print());
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
		System.out.println("Filtering Suffixes ");
		nodesToVisit.addFirst(root);
		Node current = null;
		boolean skipNext = false;
		Node parent = root; 

		StandardDeviation deviation = new StandardDeviation(true);
		do {
			current = nodesToVisit.removeFirst();

			if (current.isEndsWord()) {
				//Calculate weight by formula 1/(length * log(occ))
				int length = current.getLevel();
				double suffixWeight = 1/(length*Math.log10(current.getOccurrences())); 
				System.out.println(current.getWord() + " --> " + suffixWeight);
				if(current instanceof SNode) {
					((SNode)current).setSuffixWeight(suffixWeight);
				}
				
				//Calculate confidence by standard deviation.
				if (Config.inflectionsIdentification) {
					double[] values = ((SNode)current).getInflectionPattern();
					((SNode)current).setInflectionsConfidence(deviation.evaluate(values));
				}
			}
			for (int i = 0; i <Trie.NUM_LETTERS ;i++) {
				Node childNode = current.getNthChild(i);
				if(childNode != null ) {
					
					if (childNode.getOccurrences() <= Config.suffixOccThreshold ) { 
						current.setNthChild(null, i);
						current.decNumChildren();
						deleteCount++;
					} /*else if (isStemWord(childNode)) {
						
					}*/ else {
						
						nodesToVisit.addFirst(childNode);
					}
				}
			}
		} while (!nodesToVisit.isEmpty());
		System.out.println(deleteCount + " Suffixes deleted");
	}


	private boolean isStemWord(Node childNode) {
		SNode current;
		if (childNode instanceof SNode) {
			current = (SNode) childNode;
			//FIXME
		}
		return false;
	}

	public int getDeleteCount() {
		return deleteCount;
	}

}
