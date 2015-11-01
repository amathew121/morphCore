package com.ashish.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class FSTGenerator {

	private static final String LEMMA_FST_FILENAME = "lemma.txt";
	private static FSTGenerator generator = null;
	private File lemmaFST;

	public static final String EPSILON = "<epsilon>";

	private FSTGenerator() {
	}

	public static FSTGenerator getFSTGenerator() {
		if (generator == null) {
			generator = new FSTGenerator();
		}
		return generator;
	}

	public FileWriter openLemmaFST() throws IOException {
		lemmaFST = new File(LEMMA_FST_FILENAME);
		if (lemmaFST.exists()) {
			lemmaFST.delete();
		}
		lemmaFST.createNewFile();
		FileWriter lemmaFSTWriter = new FileWriter(lemmaFST);
		return lemmaFSTWriter;

	}

	public void closeLemmaFST(FileWriter lemmaFSTWriter) throws IOException {
		lemmaFSTWriter.close();
	}

	// private String lemmaFST = "";

	public void createInputFST(File input_text, String text) throws IOException {
		if (input_text.exists()) {
			input_text.delete();
		}
		input_text.createNewFile();

		int count = 0;
		FileWriter inputFst = new FileWriter(input_text);

		try {
			for (int j = 0; j < text.length(); j++) {
				String word = text;
				if (' ' == word.charAt(j)) {
					append(inputFst, count++, count, "<space>",
							"<space>");

				} else if ('\n' == word.charAt(j)) {
					append(inputFst, count++, count, "<newline>",
							"<newline>");

				} else {
					append(inputFst, count++, count, "" + word.charAt(j),
						"" + word.charAt(j));
				}
				/*
				 * inputFst.append("" + count++).append(" ").append("" +
				 * (count)) .append(" ").append("" + word.charAt(j)).append(" ")
				 * .append(EPSILON).append("\n");
				 */
			}
			inputFst.append("" + count);
		} finally {
			inputFst.close();
		}
	}

	public static void append(Writer fileWriter, int start, int end, String in,
			String out) throws IOException {
		fileWriter.append("" + start).append(" ").append("" + end).append(" ")
				.append("" + in).append(" ").append(out).append("\n");
	}

	public static HashSet<String> writeFSTRules(File fst, List<SNode> suffixes)
			throws IOException {

		if (fst.exists()) {
			fst.delete();
		}

		if (!fst.createNewFile()) {
			return null;
		}

		FileWriter fstRules = new FileWriter(fst);
		HashSet<String> tags = new HashSet<String>();
		try {
			int suffixStartState = 3;
			int lemmaStartState = 2;
			int finalState = 4;
			int count = 5;
			for (int i = 0; i < suffixes.size(); i++, count++) {
				suffixStartState = count;
				for (String branch : suffixes.get(i).getBranches()) {
					String ch = "" + branch.charAt(branch.length() - 1);
					append(fstRules, lemmaStartState, suffixStartState, ch, ch);
					// append(fstRules, count++, suffixStartState, EPSILON,
					// EPSILON);
				}

				count++;
				SNode suffixStartNode = suffixes.get(i);
				boolean taggedSuffix = false;
				for (String tag : suffixStartNode.getTags()) {
					append(fstRules, suffixStartState, count, ""
							+ suffixStartNode.getWord().charAt(0), tag);
					tags.add(tag);
					taggedSuffix = true;
				}
				if (!taggedSuffix) {
					append(fstRules, suffixStartState, count, ""
							+ suffixStartNode.getWord().charAt(0), EPSILON);
				}
					for (int j = 1; j < suffixes.get(i).getWord().length(); j++) {

						String word = suffixes.get(i).getWord();
						append(fstRules, (j == 0 ? suffixStartState : count++),
								count, "" + word.charAt(j), EPSILON);
					}
					append(fstRules, count++, finalState, "" + EPSILON, EPSILON);
				

			}
			fstRules.append("" + finalState);
			return tags;
			/*
			 * append(fstRules, finalState, suffixStartState, ""+EPSILON,
			 * EPSILON); fstRules.append("" +finalState) .append(" ") .append(""
			 * +suffixStartState) .append(" ") .append("<eps>") .append(" ")
			 * .append("<eps>") .append("\n");
			 */
			// fstRules.append("EOF");
			// inputSymbols.append("EOF");
		} finally {
			fstRules.close();
		}
	}

	public static void writeSymbols(File fst, File osyms, HashSet<String> tags)
			throws IOException {
		if (fst.exists()) {
			fst.delete();
		}

		if (osyms.exists()) {
			osyms.delete();
		}

		if (!fst.createNewFile()) {
			return;
		}

		if (!osyms.createNewFile()) {
			return;
		}

		int count = 20000;
		FileWriter inputSymbols = new FileWriter(fst);
		FileWriter outputSymbols = new FileWriter(osyms);
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(FSTGenerator.EPSILON).append(" ").append("0")
					.append("\n");
			sb.append("<space>").append(" ").append("" + count++)
			.append("\n");
			sb.append("<newline>").append(" ").append(""+ count++)
			.append("\n");
			sb.append(".").append(" ").append(""+ count++)
			.append("\n");
			sb.append(",").append(" ").append(""+ count++)
			.append("\n");
			for (int i = (int) Trie.getRangeStart(); i <= (int) Trie.getRangeEnd(); i++) {
				sb.append((char) i).append(" ").append("" + i)
						.append("\n");
			}
			inputSymbols.append(sb.toString());
			outputSymbols.append(sb.toString());
			
			for (String tag : tags) {
				outputSymbols.append(tag).append(" ").append("" + (count++))
						.append("\n");
			}

		} finally {
			inputSymbols.close();
			outputSymbols.close();
		}
	}

	public static void main(String[] args) throws IOException {
		FSTGenerator fstGenerator = new FSTGenerator();
		fstGenerator.createInputFST(new File(
				"/home/cryptic/projectFiles/FST/input.txtfst"), "പക്ഷികൾ പട്ടിയുടെ മാംസം തിന്നു  ");
	}

	public static void writeLettersFST(File fst) throws IOException {
		if (fst.exists()) {
			fst.delete();
		}

		if (!fst.createNewFile()) {
			return;
		}
		int count = 1;
		FileWriter lettersFST = new FileWriter(fst);
		try {

			for (int i = (int) Trie.getRangeStart(); i <= Trie.getRangeEnd(); i++) {
				append(lettersFST, 0, 1, "" + (char) i, "" + (char) i);
			}
			append(lettersFST, 1, 0, EPSILON, EPSILON);
			lettersFST.append("" + 0);
		} finally {
			lettersFST.close();
		}
	}

	public String parseOutputFile(File f) {
		StringBuilder outputText = new StringBuilder();
		try {
			Scanner sc = new Scanner(f);
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				System.out.println(line);
				String split[] = line.split("\\s+");
				System.out.println(split.length);

				if (split.length == 4) {
					outputText.append(split[3]);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return outputText.toString();
	}
}
