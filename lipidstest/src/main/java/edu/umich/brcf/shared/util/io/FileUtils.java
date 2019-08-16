///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 	FileUtils.java
// 	Written by Jan Wigginton February 2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
	{

	public static boolean hasDataThroughLine(File file, int n_lines_to_check)
			throws IOException, FileNotFoundException
		{
		return hasNTokensThroughLine(file, n_lines_to_check, 1,
				Integer.MAX_VALUE);
		}

	public static boolean hasExactlyNTokensThroughLine(File file,
			int n_lines_to_check, int n_tokens) throws IOException,
			FileNotFoundException
		{
		return hasNTokensThroughLine(file, n_lines_to_check, n_tokens, n_tokens);
		}

	public static boolean hasAtLeastNTokensThroughLine(File file,
			int n_lines_to_check, int n_tokens) throws IOException,
			FileNotFoundException
		{
		return hasNTokensThroughLine(file, n_lines_to_check, n_tokens,
				Integer.MAX_VALUE);
		}

	public static boolean hasNTokensThroughLine(File file,
			int n_lines_to_check, int min_tokens, int max_tokens)
			throws IOException, FileNotFoundException
		{
		BufferedReader input = new BufferedReader(new FileReader(file));
		String line = input.readLine();

		int nLines = 0;
		while (line != null && nLines++ < n_lines_to_check)
			{
			String[] tokens = StringUtils.splitAndTrim(line);

			int nTokens = tokens != null ? tokens.length : 0;

			if (nTokens > max_tokens || nTokens < min_tokens)
				return false;

			input.readLine();
			}

		return true;
		}

	public static boolean hasDataByLine(ArrayList<String> lines)
		{
		int nLines = 0;
		while (nLines < lines.size())
			{
			String[] tokens = StringUtils.splitAndTrim(lines.get(nLines++));

			int nTokens = tokens != null ? tokens.length : 0;

			if (nTokens > 0)
				return true;
			}
		return false;
		}

	public static boolean hasExactlyNTokensThroughLine(ArrayList<String> lines,
			int n_tokens)
		{
		return hasNTokensThroughLine(lines, n_tokens, n_tokens);
		}

	public static boolean hasAtLeastNTokensThroughLine(ArrayList<String> lines,
			int n_tokens)
		{
		return hasNTokensThroughLine(lines, n_tokens, Integer.MAX_VALUE);
		}

	public static boolean hasNTokensThroughLine(ArrayList<String> lines,
			int min_tokens, int max_tokens)
		{
		int nLines = 0;
		while (nLines < lines.size())
			{
			String[] tokens = StringUtils.splitAndTrim(lines.get(nLines++));

			int nTokens = tokens != null ? tokens.length : 0;

			if (nTokens > max_tokens || nTokens < min_tokens)
				return false;
			}

		return true;
		}

	public static boolean isFileBinary(File f) throws FileNotFoundException,
			IOException
		{
		System.out.println("Testing if file is binary");

		FileInputStream in = new FileInputStream(f);

		int size = Math.min(1024, in.available());

		byte[] data = new byte[size];
		in.read(data);

		in.close();

		int ascii = 0;
		int other = 0;

		for (int i = 0; i < data.length; i++)
			{
			byte b = data[i];
			if (b < 0x09)
				return true;

			if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D)
				ascii++;
			else if (b >= 0x20 && b <= 0x7E)
				ascii++;
			else
				other++;
			}

		return other == 0 ? false : 100 * other / (ascii + other) > 95;
		}

	public static ArrayList<String> getNLines(File file, int n_lines)
			throws FileNotFoundException, IOException
		{
		BufferedReader input = new BufferedReader(new FileReader(file));
		String line = input.readLine();
		ArrayList<String> lines = new ArrayList<String>();

		int lines_read = 0;
		while (line != null && lines_read < n_lines)
			{
			lines.add(line);
			lines_read++;
			line = input.readLine();
			}

		return lines;
		}

	/*
	 * public static void getNNonEmptyLines(int n_lines) {public static File
	 * writeToFile(List <String> lines, String fileName) { String pathFileName =
	 * System.getProperty("user.home");// + System.getProperty("file.separator")
	 * + fileName; File newTextFile = new File(pathFileName, fileName);
	 * 
	 * BufferedWriter writer = null; try { FileWriter fw = new
	 * FileWriter(newTextFile);
	 * 
	 * writer = new BufferedWriter(fw); for (int i =0; i < lines.size(); i++)
	 * writer.write( lines.get(i) == null ? "" : lines.get(i));
	 * 
	 * } catch ( IOException e) { } finally { try { if ( writer != null)
	 * writer.close( ); } catch ( IOException e) { } } return newTextFile; }
	 * BufferedReader input = new BufferedReader(new FileReader(file)); String
	 * line = input.readLine().trim(); ArrayList<String> lines = new
	 * ArrayList<String>();
	 * 
	 * int lines_read = 0; while (line != null && lines_pulled < n_lines) { if
	 * (line.length > 0)
	 * 
	 * lines.add(line); lines_read++; line = input.readLine().trim(); }
	 * 
	 * return lines; }
	 * 
	 * public static checkForLines(ArrayList<String> linesRequired) {
	 * 
	 * }
	 */

	public static File writeToFile(List<String> lines, String fileName)
		{
		String pathFileName = System.getProperty("user.home");// +
																// System.getProperty("file.separator")
																// + fileName;
		File newTextFile = new File(pathFileName, fileName);

		BufferedWriter writer = null;
		try
			{
			FileWriter fw = new FileWriter(newTextFile);

			writer = new BufferedWriter(fw);
			for (int i = 0; i < lines.size(); i++)
				writer.write(lines.get(i) == null ? "" : lines.get(i));

			} catch (IOException e)
			{
			} finally
			{
			try
				{
				if (writer != null)
					writer.close();
				} catch (IOException e)
				{
				}
			}
		return newTextFile;
		}

	}
