package com.pukai.loader.loader.main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

public class Test {
	
	public static void main(String[] args) throws IOException {
		FileReader reader = new FileReader(args[0]);
		FileWriter writer = new FileWriter(args[1], true);
		
		LineNumberReader br = new LineNumberReader(reader);
		
		String str = null;
		
		while ((str = br.readLine()) != null) {
			writer.write(str + "\r\n");
		}
		
		reader.close();
		writer.close();
		br.close();
	}
}
