package main;

import java.util.ArrayList;
import java.util.Scanner;

import support.SimilarityCalc;

public class Adder {

	//11-6-2022
	//11-26-2022: added omit emoji functionality (double colon)
	//12-3-2023: made the program functioning (for some reason it was broken), and made to prepare for review for University of Toronto Engineering
	
	public static final String VERSION = "1.0.2 ";
	
	private static final double SIMILARITY_THRESHOLD = 0.5; 
	
	private static final ArrayList<String> ALL_EMOJIS = DictFileEditor.getAllEmojis();
	private static final ArrayList<String> ALL_SHORTNAMES = DictFileEditor.getAllShortnames();
	
	public static String add(String str) {
		
		Scanner wordReader = new Scanner(str);
		
		String temp = ""; //The String that will get returned
		String currKey = null; //The current word
		while(wordReader.hasNext()) {
			
			//Getting the current word
			currKey = wordReader.next();
			
			//Adding the actual word to the String that will get returned
			temp += addWord(currKey);
			
		}
		
		wordReader.close();
		
		return temp;
		
	}
	
	public static String addWord(String word) {
		
		String str = word.replace("-", " ").trim();
		
		//If this "word" is actually just a user-inputed short name, then just replace the short name with its emoji
		if(str.charAt(0) == ':' && str.charAt(str.length() - 1) == ':') {
			
			try {
				
				return ALL_EMOJIS.get(ALL_SHORTNAMES.indexOf(str.substring(1, str.length() - 1)));
				
			} catch (IndexOutOfBoundsException e) {
				
				//If the user's shortname was not found in our file
				return " ";
				
			}
			
		}
		
		
		if(str.substring(str.length() - 2).equals("::")) {
			
			System.out.println(str.substring(str.length() - 2));
			return word.substring(0, str.length() - 2) + " ";
			
		}
		
		//Getting the ArrayList that comes with the current word (if there is one)
		ArrayList<String> currEmojis;
		try {
			
			currEmojis = DictFileEditor.getUserDict().get(str);
			
		} catch (NullPointerException e) {
			
			currEmojis = null;
			
		}
		
		if(currEmojis != null) {
			
			//If there is an ArrayList
			return str + " " + currEmojis.get((int) (Math.random() * currEmojis.size())) + " ";
			
		} else {
			
			//If there is not an ArrayList
			String emojiStr = DictFileEditor.getGeneratedDict().get(str);
			
			if(emojiStr != null) {
				
				//If there is an emoji already mapped to this word in the generated dictionary
				System.out.println("mapped");
				
				if(emojiStr.equals("")) {
					
					return word + " ";
					
				} else {
					
					return word + " " + emojiStr;
					
				}
				
			} else {
				
				//If there isn't an emoji already mapped to this word in the generated dictionary
				emojiStr = findEmoji(str);
				DictFileEditor.addToGeneratedDict(str, emojiStr);
				System.out.println("put " + str + " " + emojiStr);
				if(emojiStr != "") {
					
					return word + " " + emojiStr;
					
				} else {
					
					return word + " ";
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * 
	 * Use this method conservatively. It is resource-intensive.
	 * @param str - The word to find an emoji for.
	 * @return The emoji if an emoji was found. An empty string if not.
	 */
	private static String findEmoji(String str) {
		
		String input = str.replace('-', ' ').toLowerCase();
//		System.out.println("[INPUT]: " + input);
		
		int indexWithMaxSimilarity = -1;
		double maxScore = -1;
		double currScore = 0;
		
		for(int i = 0; i < ALL_SHORTNAMES.size(); i++) {
			
			currScore = SimilarityCalc.compare(input, ALL_SHORTNAMES.get(i));
			
			if(currScore > maxScore) {
				
				maxScore = currScore;
				indexWithMaxSimilarity = i;
//				System.out.println(allShortnames.get(i) + " " + currScore);
				
			}
			
		}
		
//		System.out.println(str + " " + maxScore + " " + allShortnames.get(indexWithMaxSimilarity));
		if(maxScore >= SIMILARITY_THRESHOLD) {
			
			return ALL_EMOJIS.get(indexWithMaxSimilarity);
			
		} else {
			
			return "";
			
		}
		
	}
	
	public static void main(String[] args) {
		
		Scanner keyboard = new Scanner(System.in);
		String curr = null;
		while(true) {
			
			curr = keyboard.nextLine();
			if(curr.equals("/e")) {
				
				break;
				
			}
			
			System.out.println(Adder.add(curr));
			
		}
		
		keyboard.close();
		
	}
	
}
