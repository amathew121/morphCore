/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ashish.util;

import static com.ashish.util.Trie.NUM_LETTERS;

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
	private char nodeChar;

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
        this.nodeChar = c;
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
    public void decNumChildren() {
    	this.numChildren--;
    	if(this.numChildren ==0) {
    		this.endsWord = true;
    	}
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
		final int prime = 3;
		int result = 1;
		result = prime * result + level;
		result = prime * result + nodeChar;
		result = prime * result + numChildren;
		result = prime * result + occurrences;
		if (parent != null)
		result = prime * result + parent.word.hashCode()  ;
		else 
		result = prime * result + 5  ;

		if (result < 0) {
			result = -result;
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (level != other.level)
			return false;
		if (nodeChar != other.nodeChar)
			return false;
		if (numChildren != other.numChildren)
			return false;
		if (occurrences != other.occurrences)
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

	public abstract Node newChild(Node parent, char c);

	public char getNodeChar() {
		return nodeChar;
	}


  
    
}