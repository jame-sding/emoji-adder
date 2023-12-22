package main;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DictFileEditor {
	
	//11-26-2022
		
	/**
	 * The directory of the user dictionary file.
	 */
	private static final String USER_DIR = "emojidictionary.dat";
	
	/**
	 * The directory of the generated dictionary file.
	 */
	private static final String GEN_DIR = "generatedemojidictionary.dat";
	
	/**
	 * The directory of the file with all emojis and their descriptions
	 */
	private static final String LIST_DIR = "/emojilistdata.dat";
	
	private static HashMap<String, ArrayList<String>> userDict = readUserDictFile();
	private static HashMap<String, String> generatedDict = readGeneratedDictFile();
	
	/**
	 * 
	 * Adds the keyword and its emojis to the user dictionary file.
	 * Adds the keyword and its emojis to the static user dictionary object.
	 * 
	 * <b>Auto prevents repeats.</br></br>
	 * <i>If you want to add a word with no associated emojis, use an empty ArrayList for dict. </br>DO NOT PUT null FOR dict.</b></i>
	 * @param keyword The word
	 * @param value The emoji
	 * @return False if the word was already in the dictionary
	 */
	public synchronized static boolean addWordToUserDict(String keyword, ArrayList<String> dict) {
		
		if(userDict.putIfAbsent(keyword, dict) == null) {
			
			//If the code ends up here, then that means that the keyword was added to the static user dictionary object
						
			try {
				
				//Replacing the old dictionary in the file with the newly updated one
				
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(USER_DIR));
				output.writeObject(userDict);
				output.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}

	/**
	 * 
	 * Adds an emoji to the keyword in the user dictionary file.
	 * Adds an emoji to the keyword in the static user dictionary object.
	 * <b>Auto prevents repeats</b>
	 * @param keyword The word
	 * @param value The emoji
	 * @return False if the word was already in the dictionary
	 */
	public synchronized static boolean addEmojiToWordInUserDict(String keyword, String emoji) {
		
		//First adding it to the keyword in the static user dictionary object
		ArrayList<String> currentAssociatedEmojis = userDict.get(keyword);
		if(currentAssociatedEmojis == null) {
			
			//If there is no ArrayList, then create a blank one
			currentAssociatedEmojis = new ArrayList<String>();
			
		}
		
		if(!currentAssociatedEmojis.contains(emoji)) {
			
			//If the code ends up here, then that means that the emoji is not associated in the user dict object and (probably) not associated in the file either
			
			currentAssociatedEmojis.add(emoji);
			userDict.put(keyword, currentAssociatedEmojis);
			
			try {
				
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(USER_DIR));
				output.writeObject(userDict);
				output.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	/**
	 * 
	 * @param keyword The word
	 * @param emoji The emoji
	 * @return True if the word was added, false if the word wasn't added (because the word was already in the dictionary)
	 */
	public synchronized static boolean addToGeneratedDict(String keyword, String emoji) {
		
		if(generatedDict.putIfAbsent(keyword, emoji) == null) {
			
			try {
				
				//Replacing the old dictionary in the file with the newly updated one
				
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(GEN_DIR));
				output.writeObject(generatedDict);
				output.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	/**
	 * Removes the keyword and all of its emojis from the user dictionary text file.
	 * Removes the keyword and all of its emojis from the static user dictionary object.
	 * @param keyword The keyword to remove
	 * @return True if the word was found and removed, false if the word was not found or if it was removed unsuccessfully
	 */
	public synchronized static boolean removeWordFromUserDict(String keyword) {
		
		if(userDict.remove(keyword) != null) {
			
			//If the code ends up here, then that means that the keyword was found in the static user dictionary object
			
			try {
				
				//Replacing the old dictionary in the file with the newly updated one
				
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(USER_DIR));
				output.writeObject(userDict);
				output.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	/**
	 * Removes the emoji from the keyword's list of emojis in the user dictionary text file.
	 * Removes the emoji from the keyword's list of emojis in the static user dictionary object.
	 * @param keyword The keyword that contains the value
	 * @param emoji 
	 * @return True if the emoji was found and removed, false if the emoji was not found or if it was removed unsuccessfully
	 */
	public synchronized static boolean removeEmojiFromUserDict(String keyword, String emoji) {
		
		//First removing the emoji from the static object
		ArrayList<String> associatedEmojis = userDict.get(keyword);
		if(associatedEmojis == null) {
			
			//If there is no ArrayList, then create a blank one
			associatedEmojis = new ArrayList<String>();
			
		}
		if(associatedEmojis.contains(emoji)) {
			
			//If the code ends up here, then that means that the emoji is associated in the user dict object and (probably) not associated in the file either
			
			associatedEmojis.remove(emoji);
			
			try {
				
				//Replacing the old dictionary in the file with the newly updated one
				
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(USER_DIR));
				output.writeObject(userDict);
				output.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	/**
	 * 
	 * Changes the original keyword into a new keyword. The new key will retain the old key's associated emojis.
	 * 
	 * @param originalKeyword Original keyword
	 * @param newKeyword New keyword
	 * @return False if the original keyword was not found, or if the new keyword already exists
	 */
	public synchronized static boolean editWord(String originalKeyword, String newKeyword) {
		
		ArrayList<String> associatedEmojis = userDict.get(originalKeyword);
		
		//The if statement first finds if the new keyword already exists, and then after that does it only try to remove the original keyword 
		if(!userDict.containsKey(newKeyword) && userDict.remove(originalKeyword) != null) {
			
			//If the code ends up here, then that means that the keyword was found in the static user dictionary object
			//and that the new keyword does not already exist.
			
			//Putting the new keyword into the dictionary, along with the original associated emojis
			userDict.put(newKeyword, associatedEmojis);
			
			try {
				
				//Replacing the old dictionary in the file with the newly updated one
				
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(USER_DIR));
				output.writeObject(userDict);
				output.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	/**
	 * 
	 * Replaces the original emoji with the new emoji in the specified keyword's associated emojis.
	 * 
	 * @param keyword
	 * @param originalEmoji
	 * @param newEmoji
	 * @return False if the original emoji was not found, or if the new emoji already exists
	 */
	public synchronized static boolean editEmoji(String keyword, String originalEmoji, String newEmoji) {
		
		ArrayList<String> associatedEmojis = userDict.get(keyword);
		
		if(!associatedEmojis.contains(newEmoji)) {
			
			int emojiIndex = associatedEmojis.indexOf(originalEmoji);
			if(emojiIndex != -1) {
				
				//If the code ends up here, then that means that the emoji was found in the keyword in the static user dictionary object
				//and that the new emoji does not already exist.
				
				//Replacing the index 
				associatedEmojis.set(emojiIndex, newEmoji);
				
				try {
					
					//Replacing the old dictionary in the file with the newly updated one
					
					ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(USER_DIR));
					output.writeObject(userDict);
					output.close();
					
				} catch (IOException e) {
					
					e.printStackTrace();
					
				}
				
				return true;
				
			} else {
				
				//Original emoji was not found in the keyword
				return false;
				
			}
			
		} else {
			
			//New emoji already exists
			return false;
			
		}
		
	}
	
	/**
	 * Gets the user dictionary from the file
	 */
	private static HashMap<String, ArrayList<String>> readUserDictFile() {
		
		try {
			
			ObjectInputStream input = new ObjectInputStream(new FileInputStream(USER_DIR));
			@SuppressWarnings("unchecked")
			HashMap<String, ArrayList<String>> temp = (HashMap<String, ArrayList<String>>) input.readObject();
			input.close();
			return temp;
			
		} catch (EOFException e) {
			
			//If there is nothing in the file
			return new HashMap<String, ArrayList<String>>();
			
		} catch (FileNotFoundException e) {
			
			//Create a new file
			File userFile = new File(USER_DIR);
			try {

				userFile.createNewFile();

			} catch (IOException e1) {

				e1.printStackTrace();

			}
			return new HashMap<String, ArrayList<String>>();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
			
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
			
		}
		
	}
	
	/**
	 * Gets the generated dictionary from the file
	 */
	private static HashMap<String, String> readGeneratedDictFile() {
		
		try {
			
			ObjectInputStream input = new ObjectInputStream(new FileInputStream(GEN_DIR));
			@SuppressWarnings("unchecked")
			HashMap<String, String> temp = (HashMap<String, String>) input.readObject();
			input.close();
			return temp;
			
		} catch (EOFException e) {
			
			//If there is nothing in the file
			return new HashMap<String, String>();
			
		} catch (FileNotFoundException e) {
			
			//Create a new file
			File genFile = new File(GEN_DIR);
			try {

				genFile.createNewFile();

			} catch (IOException e1) {

				e1.printStackTrace();

			}
			return new HashMap<String, String>();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
			
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
			
		}
		
	}
	
	/**
	 * 
	 * @return A list of every single <b>emoji <i>(not short name)</i> </b> from emojilist.txt
	 * <br>Indices correlate to the indices from getAllShortnames
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getAllEmojis() {
		
		/*
		ArrayList<String> emojis = new ArrayList<String>();
		
		Scanner input = new Scanner(DictFileEditor.class.getResourceAsStream(LIST_DIR));
		
		String curr = null;
		while(input.hasNextLine()) {
			
			curr = input.nextLine();
			emojis.add(curr.substring(0, curr.indexOf(" ") + 1));
			
		}
		
		input.close();
		
		return emojis;
		*/
		
		ObjectInputStream input = null;
		try {
			
			input = new ObjectInputStream(DictFileEditor.class.getResourceAsStream(LIST_DIR));
			
			return (ArrayList<String>) input.readObject();
			
		} catch (IOException e) {

			e.printStackTrace();
			return null;
			
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
			
		}
		
	}
	
	/**
	 * 
	 * @return A list of every single <b>short name <i>(not emoji)</i> </b> from emojilist.txt
	 * <br>Indices correlate to the indices from getAllEmojis
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getAllShortnames() {
		
		/*
		ArrayList<String> shortnames = new ArrayList<String>();
		Scanner input = new Scanner(DictFileEditor.class.getResourceAsStream(LIST_DIR));
		
		String curr = null;
		while(input.hasNextLine()) {
			
			curr = input.nextLine();
			shortnames.add(curr.substring(curr.indexOf(" ") + 1));
			
		}
		
		input.close();
		
		return shortnames;
		*/
		
		ObjectInputStream input = null;
		try {
			
			input = new ObjectInputStream(DictFileEditor.class.getResourceAsStream(LIST_DIR));
			
			input.readObject();
			return (ArrayList<String>) input.readObject();
			
		} catch (IOException e) {

			e.printStackTrace();
			return null;
			
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			return null;
			
		}
		
	}
	
	/**
	 * 
	 * @return A shallow copy of this class's static userDict field
	 */
	public static HashMap<String, ArrayList<String>> getUserDict() {
		
		return new HashMap<String, ArrayList<String>>(userDict);
		
	}
	
	/**
	 * 
	 * @return A shallow copy of this class's static generatedDict field
	 */
	public static HashMap<String, String> getGeneratedDict() {
		
		return new HashMap<String, String>(generatedDict);
		
	}
	
	/**
	 * 
	 * Replaces the current generated dict in the file with the parameter
	 * Also updates generatedDict
	 * @deprecated
	 * @param dictToReplace
	 */
	public synchronized static void updateGeneratedDictFile(HashMap<String, String> dictToReplace) {
		
		generatedDict = dictToReplace;
		
		try {
			
			HashMap<String, String> genDict = readGeneratedDictFile();
			
			genDict.putAll(dictToReplace);
			
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(GEN_DIR));
			output.writeObject(genDict);
			output.close();
			
		} catch (EOFException e) {
			
			//The file has nothing in it
			
			try {
				
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(GEN_DIR));
				output.writeObject(dictToReplace);
				output.close();
				
			} catch (FileNotFoundException e1) {
				
				e1.printStackTrace();
				
			} catch (IOException e1) {
				
				e1.printStackTrace();
				
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		
	}

	public static void main(String[] args) {
		
		//Testing
		
		addEmojiToWordInUserDict("balls", ":flushed:");
		
//		System.out.println(removeEmojiFromUserDict("ale", "peen"));
//		System.out.println(addEmojiToWordInUserDict("ale", "ppen"));
//		System.out.println(addEmojiToWordInUserDict("ale", "peen"));
		
//		System.out.println(removeWordFromUserDict("ale"));
		
	}
	
}
