package com.ashish.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Logger;

public class InputFileProcessor extends Thread {
	public boolean shutdown = false;
	
	static Logger logger = LogManager.getLogManager();

	public void run() {
		while(!shutdown) {
			if(shutdown) {
				return;
			}
			File[] files = finder("../in");
			for(File file: files) {
				StringBuilder sb = new StringBuilder();
				FileInputStream f = null; 
				try {
					 f = new FileInputStream(file);
					Scanner sc = new Scanner(f, "UTF-8");
					String line = null;
					while (sc.hasNextLine()) {
						sb.append(sc.nextLine());
					}
					
				} catch (FileNotFoundException e1) {
					logger.info("File Not Found " + file.getName() + "\n");
				} finally {
					if (f != null) {
						try {
							f.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				logger.info(file.getName() + " read.");
				file.renameTo(new File("../in/bak/" + file.getName()));
				String s =runCommands2(sb.toString(), file.getName());
				File outputFile  = new File("../out/" + file.getName() + "_out.txt");
				FileWriter fw = null;
				try { 
					fw = new FileWriter(outputFile);
					fw.append(s);
				} catch(Exception ex){
					ex.printStackTrace();
					String msg = "Unable to write to output file " + outputFile.getName();
					System.out.println(msg);
					logger.severe(msg);
				} finally {
					if(fw != null)
						try {
							fw.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				}
			}
		}
	}
	
	private String runCommands2(String sb, String fileName) {
		try {
			FSTGenerator fstGenerator = FSTGenerator.getFSTGenerator(); 
			fstGenerator.createInputFST(new File(
					"../in/input.txtfst"), sb.toString());
		} catch (IOException e1) {
			logger.severe("Error in creating from file. input.txtfst" + "\n");
		} catch (PunctuationException pe) {
			logger.severe(pe.getMessage() + "\n");
		}
		
		Process p ;
		try {
			p = Runtime.getRuntime().exec("../bin/commands2.sh " + fileName + "_out");
		    p.waitFor();

		    BufferedReader reader = 
		         new BufferedReader(new InputStreamReader(p.getErrorStream()));

		    String line = "";			
		    while ((line = reader.readLine())!= null) {
		    	System.out.println(line);
		    	logger.severe(line + "\n");
		    }
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		File f = new File ("../out/comp.txtfst");
		String text = "";
		if(f.exists()) {
			FSTGenerator fstGenerator = FSTGenerator.getFSTGenerator(); 
			text = fstGenerator.parseOutputFile(f);
			System.out.println("Output " + text);
		}
		if(!f.exists() || "".equalsIgnoreCase(text)) {
			return sb;
		} else {
			return text; 
		}
	}
	
    public File[] finder( String dirName){
    	File dir = new File(dirName);
    	return dir.listFiles(new FilenameFilter() { 
   	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".txt"); }
    	} );
    }

}
