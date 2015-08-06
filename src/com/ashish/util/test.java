
package com.ashish.util;

import java.io.UnsupportedEncodingException;

public class test {
 public static void main(String[] args) throws UnsupportedEncodingException {
	LTrie t = new LTrie();
	t.add("പട്ടി", true);
	/*t.add("പട്ടിയെ", false);
	t.add("പട്ടിക്കു", false);
	t.add("പടനം", false);*/
	t.add("പട്ടികള്‍");
	t.add("പട്ടികള്‍ക്ക്");
	t.add("മരം", true);
	/*t.add("മരമേ", false);*/
	
	BranchIterator<LNode> iterator = new BranchIterator<>(t.getRoot());
	while (iterator.hasNext()) {
		LNode node = iterator.next();
		if (node != null) {
			System.out.println("Branch:" + node.getWord());
		} else System.out.println("Null");
	}

}
}
