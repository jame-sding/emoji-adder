package support;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class TwemojiListFormatter {

	//9-18-2022
	//12-3-2023
	
	//https://gist.githubusercontent.com/oliveratgithub/0bf11a9aff0d6da7b46f1490f86a71eb/raw/d8e4b78cfe66862cf3809443c1dba017f37b61db/emojis.json
	
	public static void main(String[] args) {

		
		
	}
	
	@SuppressWarnings("unused")
	private static void convertRawGithubTextIntoTextList() {
		
		try {
			
			Scanner input = new Scanner(new FileInputStream("emojis-data.txt"));
			PrintWriter output = new PrintWriter(new FileOutputStream("emojilist.txt"));
			
			String curr = null;
			input.nextLine();
			input.nextLine();
			while(input.hasNextLine()) {
				
				curr = input.nextLine();
				if(curr.trim().equals("]")) {
					
					break;
					
				}
				
				int emojiStrIndex = curr.indexOf("\"emoji\"");
				int shortnameStrIndex = curr.indexOf("\"shortname\"");
				output.println(curr.substring(emojiStrIndex + 10, curr.indexOf('"', emojiStrIndex + 10)) + " " + curr.substring(shortnameStrIndex + 15, curr.indexOf(':', shortnameStrIndex + 15)).replace('_', ' '));
				
			}
			
			input.close();
			output.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private static void convertTextListIntoDAT() {
		
		Scanner input = null;
		try {
			
			input = new Scanner(new FileInputStream("D:\\Programming\\Java\\1.0 Backup\\src\\emojilist.txt"));
		
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			
		}
		
		ArrayList<String> emojis = new ArrayList<String>();
		ArrayList<String> shortnames = new ArrayList<String>();
		
		String curr = null;
		while(input.hasNextLine()) {
			
			curr = input.nextLine();
			emojis.add(curr.substring(0, curr.indexOf(" ") + 1));
			
		}
		
		input.close();
		try {
			
			input = new Scanner(new FileInputStream("D:\\Programming\\Java\\1.0 Backup\\src\\emojilist.txt"));
		
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			
		}
		
		while(input.hasNextLine()) {
			
			curr = input.nextLine();
			shortnames.add(curr.substring(curr.indexOf(" ") + 1));
			
		}
		
		input.close();
		
		System.out.println(emojis);
		System.out.println(shortnames);
		
		try {
			
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("emojilistdata.dat"));
			output.writeObject(emojis);
			output.writeObject(shortnames);
			output.close();
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		
		} catch (IOException e) {

			e.printStackTrace();
			
		}
		
		
	}

}
