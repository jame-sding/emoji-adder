package support;

import java.util.Scanner;

public class SimilarityCalc {

	//9-26-2022
		
	public static double compare(String mainStr, String otherStr) {
		
		return rawcompare(mainStr, otherStr) / getMaxRawScore(Math.max(mainStr.length(), otherStr.length()));
		
	}
	
	private static double rawcompare(String mainStr, String otherStr) {
		
		double score = 0;
		
		//Loops through every possible length of a substring of otherStr
		for(int substringLength = 1; substringLength <= otherStr.length(); substringLength++) {
			
			//Loops through each substring of otherStr with length substringLength
			for(int i = 0; i < otherStr.length() - (substringLength - 1); i++) {
				
				try {
					
					int closestRelativeIndex = closestRelativeIndexOf(mainStr, otherStr.substring(i, i + substringLength), i);
//					System.out.println(otherStr.substring(i, i + substringLength) + "     " + 1 / (double) (Math.abs(closestRelativeIndex) + 1));
					score += (1 / (double) (Math.abs(closestRelativeIndex) + 1)) * substringLength;
					
				} catch(RelativeIndexNotFoundException e) {
					
//					System.out.println(otherStr.substring(i, i + substringLength) + " n/a");
					score += 0;
					
				}
								
			}
			
		}
		
		return score;
		
	}
		
	/**
	 * 
	 * <html>
	 * Calculates the relative index.
	 * <br>
	 * The relative index of "p" in "spaghetti" based on index 1 is 0 because "spaghetti" has a p directly at index 1.
	 * <br>
	 * The relative index of "h" in "spaghetti" based on index 3 is 1 because the closest p from index 3 is 1 character to the right.
	 * <br>
	 * The relative index of "s" in "spaghetti" based on index 6 is negative 6 because the closest s from index 6 is 6 characters to the left.
	 * </html>
	 * 
	 * @param str The string to find the target in
	 * @param target The target
	 * @param index The focus index
	 * @return The relative index of the target in relativeTo based on index
	 * @throws IndexOutOfBoundsException When the index is not found anywhere
	 */
	private static int closestRelativeIndexOf(String str, String target, int index) {
		
		//Divide the relativeTo String into a substring left of the index and a substring right of the index
		
		int substringDivision = index;
		
		if(substringDivision >= str.length()) {
			
			substringDivision = str.length() - 1;
			
		}
		
		String lhsStr = str.substring(0, substringDivision);
		String rhsStr = str.substring(substringDivision);
		
		
		//Finding the indices of the target in both substrings
		int lhs = lhsStr.lastIndexOf(target);
		int rhs = rhsStr.indexOf(target);
		boolean hasLHS = true;
		boolean hasRHS = true;
		
		//Converting the left index from being relative to the start of str to being relative to the parameter index
		if(lhs == -1) {
			
			//(If there was no index found, set hasLHS to be false)
			hasLHS = false;
			
		} else {
			
			lhs -= index;
			
		}
		
		if(rhs == -1) {
			
			//(If there was no index found, set hasRHS to be false)
			hasRHS = false;
			
		}
		
		if(hasLHS && hasRHS) {
			
			//If there is both a left index and a right index
			
			if(Math.abs(lhs) < Math.abs(rhs)) {
				
				return lhs;
				
			} else {
				
				return rhs;
				
			}
			
		} else if(hasLHS) {
			
			//If there is only a left index
			return lhs;
			
		} else if(hasRHS) {
			
			//If there is only a right index
			return rhs;
			
		} else {
			
			//If the index was not found anywhere
			throw new RelativeIndexNotFoundException();
			
		}
		
	}
	
	private static void displayComparison(String str1, String str2, boolean isRaw) {
		
		if(isRaw) {
			
			System.out.println("\"" + str1 + "\" \"" + str2 + "\" || [raw] " + rawcompare(str1, str2) + " (out of a possible " + getMaxRawScore(str2.length()) + ")");
			
		} else {
			
			System.out.println("\"" + str1 + "\" \"" + str2 + "\" || [unraw] " + compare(str1, str2));
			
		}
		
	}
	
	private static double getMaxRawScore(int length) {
		
		double score = 0;
		
		for(int n = 1; n <= length; n++) {
			
			score += n * (length - n + 1);
			
		}
		
		return score;
		
//		return (1 + length) * length / 2;
		
	}
	
	public static void main(String[] args) {
		
		Scanner keyboard = new Scanner(System.in);
//		
//		displayComparison("apple", "applesauce", true);
//		displayComparison("apple", "airplane", true);
//		displayComparison("apple", "red apple", true);
//		displayComparison("apple", "apple", true);
//		displayComparison("apple", "apples", true);
//		System.out.println();
//		displayComparison("apple", "applesauce", false);
//		displayComparison("apple", "airplane", false);
//		displayComparison("apple", "red apple", false);
//		displayComparison("apple", "apple", false);
//		displayComparison("apple", "apples", false);
		
		String curr1 = null;
		String curr2 = null;
		while(true) {
			
			curr1 = keyboard.nextLine();
			if(curr1.equals("/e")) {
				
				break;
				
			}
			curr2 = keyboard.nextLine();
			if(curr2.equals("/e")) {
				
				break;
				
			}

			displayComparison(curr1, curr2, false);
			
		}
		
		keyboard.close();
		
	}
	
}
