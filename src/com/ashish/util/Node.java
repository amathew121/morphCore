/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ashish.util;

import static com.ashish.util.Trie.NUM_LETTERS;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Ashish
 */
public abstract class Node implements Comparable<Node> {

    private String word;
    private Node parent;
    private Node child[] = new Node[NUM_LETTERS];
    private boolean visited;
    private int occurrences;
    private int numChildren;
    private int level = 0;
	protected boolean endsWord;

	@Override
	public int compareTo(Node o) {
	    Integer myOcc = this.occurrences;
	    Integer yourOcc = o.occurrences;
	    return -myOcc.compareTo(yourOcc);
	}
	
    Node() {
        word = "";
    }

    Node(Node parent, char c) {
        this.word = parent.word + c;
        this.parent = parent;
    }



    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getNthChild(int index) {
        return child[index];
    }

    public void setNthChild(Node childNode,int index) {
        this.child[index] = childNode;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }
    public void incNumChildren() {
        this.numChildren++;
    }

    public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }
    
    public void incOccurrences() {
        this.occurrences++;
    }
    
    public void setLevel(int level) {
    	this.level = level;
    }
   
    public int getLevel() {
		return level;
	}

	public boolean isEndsWord() {
		return endsWord;
	}
	
	public void setEndsWord(boolean value) {
		if(value == true) {
			endsWord = true;
		}
	}
	
	@Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Arrays.deepHashCode(this.child);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (!Arrays.deepEquals(this.child, other.child)) {
            return false;
        }
        return true;
    }
   
    public abstract Node newChild(Node parent, char c);

  
    
}