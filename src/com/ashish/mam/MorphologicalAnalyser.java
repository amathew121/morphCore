package com.ashish.mam;

import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.UIManager;

import com.ashish.swinging.MainWindow;
import com.ashish.util.LTrie;
import com.ashish.util.LogManager;
import com.ashish.util.Trie;

public class MorphologicalAnalyser {
	private MorphologicalAnalyser morpher = null;
	
	static Logger logger = LogManager.getLogManager();

	private static LTrie trie = new LTrie();

	public static LTrie getTrie() {
		return trie;
	}
	
	public static void setGlobalFont( Font font ) {  
        Enumeration enum1 = UIManager.getDefaults().keys();  
        while ( enum1.hasMoreElements() ) {  
            Object key = enum1.nextElement();  
            Object value = UIManager.get( key );  
            if ( value instanceof Font ) {  
                UIManager.put( key, font );  
            }  
        }  
    }  
	
	public static void main(String[] args) {
		File configFile = new File("../conf/config.properties");
		 
		try {
		    FileReader reader = new FileReader(configFile);
		    Properties props = new Properties();
		    props.load(reader);
		 
		    String host = props.getProperty("host");
		    String charset = props.getProperty("charset");
		    System.setProperty("file.encoding", charset);
		    String startChar = props.getProperty("rangeStart");
		    String endChar = props.getProperty("rangeEnd");
		    char rangeStart = (char) Integer.parseInt(startChar);
		    char rangeEnd = (char) Integer.parseInt(endChar);
		    Trie.setProperties(rangeStart,rangeEnd);
		    		    
		    System.out.print("Host name is: " + host);
		    reader.close();
		} catch (FileNotFoundException ex) {
		    // file does not exist
		} catch (IOException ex) {
		    // I/O error
		}
		
		System.setProperty("file.encoding", "UTF-8");
		logger.info(System.getProperty("file.encoding"));
		Font font = new Font("Arial Unicode MS", Font.BOLD,12);
		setGlobalFont(font);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
