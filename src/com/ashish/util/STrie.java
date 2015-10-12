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
	
	public SNode add(Node baseNode,LNode toAddNode) throws UnsupportedEncodingException, PunctuationException {
		int index = toIndex(toAddNode.nodeChar);
		SNode sNode = (SNode) addChild(baseNode, index);
		merge(sNode, toAddNode);
		return sNode;
	}
	
	private void merge(SNode sNode, LNode toAddNode) {
		if(toAddNode.isEndsWord()) {
			sNode.setEndsWord(true);
			for(String tag : toAddNode.tags) {
				sNode.addTags(tag);
			}
		}
		
	}


}