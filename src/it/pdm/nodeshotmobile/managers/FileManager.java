package it.pdm.nodeshotmobile.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FileManager {

	public FileOutputStream openFile(String path) throws FileNotFoundException{
		File file = new File(path);
		return new FileOutputStream(file);
	}
	
	/**
     * Write a file with given name and given text
     * @param		name the path of the file
     * @param		text things to be written
     */
	public void writeFile(String name,String text) throws FileNotFoundException{
			PrintStream output = new PrintStream(openFile(name));
			output.print(text);

	}
	
}
