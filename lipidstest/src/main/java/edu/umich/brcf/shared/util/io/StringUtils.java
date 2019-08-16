// StringUtils.java
// Written by Jan Wigginton
package edu.umich.brcf.shared.util.io;

import java.util.ArrayList;

import edu.umich.brcf.shared.util.METWorksException;

public class StringUtils
	{
	public static String capitalize(String str)
		{
		if (isEmptyOrNull(str))
			return "";

		if (str.length() == 1)
			return str.toUpperCase();

		String capitalizedStr = str.toUpperCase();

		return (capitalizedStr.charAt(0) + str.substring(1));
		}

	public static String camelToWords(String camelString)
		{
		String wordsString = "", oldCase = "lower";
		for (int c = 0; c < camelString.length(); c++)
			{
			Character letter = (camelString.charAt(c));
			boolean isUpper = letter.toString().toUpperCase()
					.equals(letter.toString());
			String newCase = isUpper ? "upper" : "lower";
			if (c == 0)
				wordsString = letter.toString().toUpperCase();
			else if (c > 0 && !newCase.equals(oldCase))
				wordsString += " " + letter.toString().toUpperCase();
			else
				wordsString += letter.toString();
			}
		return wordsString;
		}

	
	public static String makeAlertMessage(String msg)
		{
		return "alert('" + msg + "');";
		}
	
	
	public static boolean isEmpty(String str)
		{
		return str == null || "".equals(str.trim());
		}

	public static boolean isNonEmpty(String str)
		{
		return str != null && !"".equals(str.trim());
		}

	public static String[] subSpacesForTabs(String line)
		{
		return new String[5];
		}

	public static String[] splitAndTrim(String line)
		{
		return splitAndTrim(line, "\\s");
		}

	public static String[] splitAndTrim(String line, String regex)
		{
		return splitAndTrim(line, regex, false);
		}
	
	public static String[] splitAndTrim(String line, String regex, boolean keepAll)
		{
		String[] rawTokens = line != null ? line.split(regex) : null;
        // Issue 439
		if (line == null)
		    return null;	
		ArrayList<String> trimTokens = new ArrayList<String>();

		for (int i = 0; i < rawTokens.length; i++)
			{
			String val = rawTokens[i].trim();
			if (keepAll || val.length() > 0)
				trimTokens.add(val);
			}
		String[] processedTokens = new String[trimTokens.size()];

		for (int i = 0; i < trimTokens.size(); i++)
			processedTokens[i] = trimTokens.get(i);

		return processedTokens;
		}

	
	public static String nThTokenSplitOn(String strToSplit, String splitRegex,
			int targetToken)
		{
		String[] tokens = strToSplit != null ? strToSplit.split(splitRegex)
				: null;

		return StringUtils.safeNthToken(tokens, targetToken);
		}

	public static String lastTokenSplitOn(String strToSplit, String splitRegex)
		{
		String[] tokens = strToSplit != null ? strToSplit.split(splitRegex)
				: null;

		if (tokens == null)
			return null;

		int targetToken = tokens.length - 1;
		return StringUtils.safeNthToken(tokens, targetToken);
		}

	public static String safeNthToken(String[] tokens, int targetToken)
		{
		return (tokens != null && tokens.length > targetToken) ? tokens[targetToken]
				: null;
		}

	// System.out.println
	public static Boolean valueIsInt(String token)
		{
		try
			{
			Integer.parseInt(token);
			} catch (NumberFormatException | NullPointerException e)
			{
			return false;
			}

		return true;
		}

	public static Boolean valueIsDouble(String token)
		{
		try
			{
			Double.parseDouble(token);
			} catch (NumberFormatException | NullPointerException e)
			{
			return false;
			}

		return true;
		}

	public static Integer extractIntegerPortion(String str, int i)
			throws METWorksException
		{
		Integer value = 0;

		try
			{
			// String tester = str.replaceAll("[^\\d]", "");
			value = Integer.parseInt(str.replaceAll("[^\\d]", ""));
			} catch (Exception e)
			{
			System.out.println("Threw an exception");
			throw new METWorksException(
					"Error while parsing integer from string");
			}

		return value;
		}

	public static boolean checkEmptyOrNull(String checked)
		{
		return checked == null || checked.trim().equals("");
		}

	public static boolean isEmptyOrNull(String str)
		{
		return checkEmptyOrNull(str);
		}

	public static boolean trimEquals(String str1, String str2)
		{
		if (str1 == null && str2 == null)
			return true;

		if (str1 == null || str2 == null)
			return false;

		return str1.trim().equals(str2.trim());
		}

	public static String grabRandomDigitString(int nDigits)
		{
		Integer digit = (int) Math.floor(Math.random() * 10);
		String digitString = "";

		for (int i = 0; i < nDigits; i++)
			{
			digitString += digit.toString();
			digit = (int) Math.floor(Math.random() * 10);
			}
		return digitString;
		}

	public static String removeSpaces(String input)
		{
		if (input == null)
			return "";

		return input.replaceAll("[\\s]", "");
		}

	public static String removeParens(String input)
		{
		if (input == null)
			return "";

		String temp = input.replaceAll(")", "");

		return temp.replaceAll("(", "");
		}

	public static String removeUnderscores(String input)
		{
		if (input == null)
			return "";

		return input.replaceAll("_", "");
		}

	public static String removeCommas(String input)
		{
		if (input == null)
			return "";

		return input.replaceAll(",", "");
		}

	public static String removeSlashes(String input)
		{
		if (input == null)
			return "";

		return input.replaceAll("/", "");
		}

	public static String removeDashes(String input)
		{
		if (input == null)
			return "";

		return input.replaceAll("-", "");
		}

	public static String cleanAndTrim(String input)
		{
		if (isEmpty(input))
			return "";

		String temp = removeCommas(input);
		String temp2 = removeUnderscores(temp);
		String temp3 = removeParens(temp2);
		String temp4 = removeSpaces(temp3);
		String temp5 = removeDashes(temp4);
		return removeSlashes(temp5);
		}

	public static String trimPhrase(String input, String phrase)
		{
		if (input == null || phrase == null)
			return "";

		String temp = input;
		if (temp.startsWith(phrase))
			temp = input.substring(0, phrase.length());

		if (temp.endsWith(phrase))
			return temp.substring(temp.length() - phrase.length() - 1,
					temp.length());

		return temp;
		}
	
	public static String toStringWithNulls(Object o)
		{
		if (o == null)
			return "";
		
		return o.toString();
		}
	}