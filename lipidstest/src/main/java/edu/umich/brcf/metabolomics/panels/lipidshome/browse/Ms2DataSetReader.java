package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.io.FileUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;


// TO DO : CLEAN THIS UP!!!
public class Ms2DataSetReader
	{
	Ms2RawData parsedData;

	private final int EXPECTED_LIPIDNAME_COL = 0;
	private final int EXPECTED_STARTMASS_COL = 1;
	private final int EXPECTED_ENDMASS_COL = 2;
	private final int EXPECTED_RT_COL = 3;
	private final int EXPECTED_LIPIDCLASS_COL = 4;
	private final int EXPECTED_KNOWNSTATUS_COL = 5;
	private final int EXPECTED_FIRSTDATA_COL = 6;

	private int lipidNameCol = -1, retentionTimeCol = -1, startMassCol = -1,
			endMassCol = -1, lipidClassCol = -1, firstDataCol = -1,
			knownStatusCol = -1;

	private int nTokensPerLine = 0, nSideLabels = 0;
	String fileName, exceptionMsg;

	public enum FileType
		{
		FILE_TYPE_BINARY, FILE_TYPE_NOTTABLE, FILE_TYPE_TRANSPOSED, FILE_TYPE_EXPECTED, FILE_TYPE_UNEXPECTED, FILE_TYPE_EMPTY;
		}

	public Ms2DataSetReader()
		{
		}

	public Ms2DataSetReader(String filename, ArrayList<Integer> extraColIndices)
		{
		fileName = filename;

		ArrayList<Integer> sideColIndices = new ArrayList<Integer>();

		for (int i = 0; i < extraColIndices.size(); i++)
			if (extraColIndices.get(i) != -1)
				sideColIndices.add(extraColIndices.get(i));

		// lipidId
		nSideLabels = sideColIndices.size();

		lipidNameCol = extraColIndices.get(EXPECTED_LIPIDNAME_COL);
		retentionTimeCol = extraColIndices.get(EXPECTED_RT_COL);
		startMassCol = extraColIndices.get(EXPECTED_STARTMASS_COL);
		endMassCol = extraColIndices.get(EXPECTED_ENDMASS_COL);
		lipidClassCol = extraColIndices.get(EXPECTED_LIPIDCLASS_COL);
		firstDataCol = extraColIndices.get(EXPECTED_FIRSTDATA_COL);
		knownStatusCol = extraColIndices.get(EXPECTED_KNOWNSTATUS_COL);

		parsedData = new Ms2RawData();
		}

	/*
	 * public MS2Results parseResults(String eid, Calendar dte) { parsedData =
	 * parseRawData();
	 * 
	 * if (parsedData == null)
	 * System.out.println("Initializing with null data");
	 * 
	 * return new MS2Results(parsedData, dte, eid); }
	 */

	public Ms2RawData parseRawData()
		{
		File file = new File(fileName);

		try
			{
			boolean canRead = file.canRead();

			FileType ft = getFileType(file);
			boolean isLegal = (ft == FileType.FILE_TYPE_EXPECTED);
			System.out.println("File type is :" + ft.toString());

			System.out.println(" SM " + startMassCol + " EM : " + endMassCol
					+ " RT :" + retentionTimeCol + " LC " + lipidClassCol
					+ " KS " + knownStatusCol);

			// if (canRead && isLegal)
			readData(file);
			// else
			// explainFileTypeError(file.getName(), ft);

			System.out.println("Sample labels are " + parsedData.sampleLabels
					+ System.getProperty("line.separator") + System.getProperty("line.separator"));
			System.out.println("Compound labels are "
					+ parsedData.compoundLabels);

			// for (int i = 0; i < parsedData.peakAreas.size(); i++)
			// if (i < 20)
			// for (int j =0; j < parsedData.peakAreas.get(i).size(); j++)
			// if (j < 20)
			// System.out.println("Peak area row " + i + " and col " + j +
			// " has value" + parsedData.peakAreas.get(i).get(j));
			for (int j = 0; j < 1; j++)
				{
				System.out.println("CL : " + parsedData.compoundLabels.get(j));
				// System.out.println("SL : " + parsedData.sampleLabels.get(j));
				if (parsedData.startMasses != null
						&& parsedData.startMasses.size() > j)
					System.out.println("SM : " + parsedData.startMasses.get(j));

				if (parsedData.retentionTimes != null
						&& parsedData.retentionTimes.size() > j)
					System.out.println("RT : "
							+ parsedData.retentionTimes.get(j));

				if (parsedData.endMasses != null
						&& parsedData.endMasses.size() > j)
					System.out.println("EM : " + parsedData.endMasses.get(j));

				if (parsedData.knownStatuses != null
						&& parsedData.knownStatuses.size() > j)
					System.out.println("KS : "
							+ parsedData.knownStatuses.get(j));

				if (parsedData.lipidClasses != null
						&& parsedData.lipidClasses.size() > j)
					System.out.println("LC  : "
							+ parsedData.lipidClasses.get(j) + " j is " + j);
				}
			}

		catch (FileNotFoundException e)
			{
			System.out.println("MetLIMS error :" + e.getMessage());
			} catch (IOException e)
			{
			System.out.println("MetLIMS error :" + e.getMessage());
			} catch (METWorksException e)
			{
			System.out.println("MetLIMS err : " + e.getMetworksMessage());
			}

		System.out.println("Data has been read in - returning to panel");
		return parsedData;
		}

	public void readData(File file) throws METWorksException
		{
		try
			{
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line = input.readLine();

			System.out.println("Line is " + line);
			if (line != null)
				readHeader(line);

			line = input.readLine();

			while (line != null)
				{
				parsedData.peakAreas.add(parseDataLine(line));
				line = input.readLine();
				}

			input.close();
			System.out.println("Sample-labelled data read successfully");
			}

		catch (Exception e)
			{
			throw new METWorksException(e);
			}
		}

	public void explainFileTypeError(String filename, FileType ft)
		{
		exceptionMsg = "Unable to read file " + filename + " ";

		switch (ft)
			{
			case FILE_TYPE_BINARY:
			exceptionMsg += "because it is a binary file. A text file in table (row/column) format was expected.";
				break;

			case FILE_TYPE_NOTTABLE:
			exceptionMsg += "because it isn't in table format (doesn't have the same number of column entries per row)" + System.getProperty("line.separator");
			exceptionMsg += "If your data has missing values, please indicate this by recording N/A in the corresponding column";
				break;

			case FILE_TYPE_UNEXPECTED:
			exceptionMsg += "because it isn't in the proper format for a result file." + System.getProperty("line.separator");
			exceptionMsg += "Result files should have a header row with column labels. ";
			exceptionMsg += "The first three columns should be labeled 'SampleName' 'SampleID' and 'SampleType'" + System.getProperty("line.separator");
			exceptionMsg += "The remaining column headers should be compound labels." + System.getProperty("line.separator");
			exceptionMsg += "The first row is followed by 1 or more rows with a sample name in the first column and ";
			exceptionMsg += "peak areas in each of the remaining columns. Missing values should be recorded as N/A";
				break;

			case FILE_TYPE_TRANSPOSED:
			exceptionMsg += "because it looks like it's transposed)" + System.getProperty("line.separator");
				break;
			}

		System.out.println(exceptionMsg);
		}

	private FileType getFileType(File file) throws IOException,
			FileNotFoundException, METWorksException
		{
		if (FileUtils.isFileBinary(file))
			return FileType.FILE_TYPE_BINARY;

		ArrayList<String> lines = FileUtils.getNLines(file, 10);

		if (lines.size() < 0)
			return FileType.FILE_TYPE_EMPTY;

		nTokensPerLine = StringUtils.splitAndTrim(lines.get(0), "\\t").length;

		if (!FileUtils.hasDataByLine(lines))
			return FileType.FILE_TYPE_EMPTY;

		if (!FileUtils.hasExactlyNTokensThroughLine(lines, nTokensPerLine))
			return FileType.FILE_TYPE_NOTTABLE;

		Boolean isTransposed = false;
		try
			{
			isTransposed = checkForCompoundsHeader(lines);
			} catch (METWorksException e)
			{
			throw e;
			}

		if (!verifyNumericEntries(lines))
			return FileType.FILE_TYPE_UNEXPECTED;

		if (isTransposed)
			return FileType.FILE_TYPE_TRANSPOSED;

		return FileType.FILE_TYPE_EXPECTED;
		}

	private boolean checkForCompoundsHeader(ArrayList<String> lines)
			throws METWorksException
		{
		if (lines == null || lines.size() < 1)
			return false;

			List<String> headers = Arrays.asList(StringUtils.splitAndTrim(
				lines.get(0), "\\t"));

			List<String> headerEntries = headers.size() >= firstDataCol ? (List<String>) headers
				.subList(0, firstDataCol) : null;

		// TO DO : Better error messaging here
		if (headerEntries == null)
			throw new METWorksException("Not enough columns in this file");

		return false;
		}

	private boolean verifyNumericEntries(ArrayList<String> lines)
		{
		for (int j = firstDataCol; j < lines.size(); j++)
			{
			String line = lines.get(j);
			if (line == null)
				return false;

			String[] tokens = StringUtils.splitAndTrim(line, "\\t");

			int i;
			for (i = firstDataCol; i < tokens.length; i++)
				{
				if (tokens[i].trim().equals("N/A"))
					continue;

				if (tokens[i].trim().length() == 0)
					return false;

				try
					{
					Double.parseDouble(tokens[i].trim());
					} catch (NumberFormatException e)
					{
					System.out
							.println("Encountered number format exception on value : "
									+ tokens[i].trim());
					return false;
					} catch (NullPointerException e)
					{
					System.out
							.println("Encountered null pointer exception while reading data "
									+ tokens[i].trim());
					return false;
					}
				}
			System.out.println("\n");
			}

		return true;
		}

	public void readHeader(String line)
		{
		String[] headerEntries = StringUtils.splitAndTrim(line, "\\t");

		// parseCompoundInfoHeader(headerEntries);
		// NOTE : Fix this
		for (int i = 0; i < firstDataCol; i++)
			if (i < headerEntries.length && !isSkipCol(i))
				parsedData.sideColumnLabels.add(headerEntries[i]);

		for (int i = firstDataCol; i < headerEntries.length; i++)
			parsedData.sampleLabels.add(headerEntries[i]);

		System.out.println("Read header " + headerEntries.toString());
		}

	private boolean isSkipCol(int i)
		{
		if (i == this.endMassCol)
			return false;

		if (i == this.startMassCol)
			return false;

		if (i == this.lipidClassCol)
			return false;

		if (i == this.lipidNameCol)
			return false;

		if (i == this.retentionTimeCol)
			return false;

		return true;
		}

	public ArrayList<Double> parseDataLine(String line)
			throws METWorksException
		{
		String[] strData = StringUtils.splitAndTrim(line, "\\t");
		ArrayList<Double> doubleValues = new ArrayList<Double>();

		if (strData.length < 1)
			return null;

		parsedData.compoundLabels.add(grabLipidName(strData[0]));
		String rt = grabRetentionTime(strData[0]);
		parseCompoundInfo(strData, rt);

		int i = this.firstDataCol;
		try
			{
			for (i = firstDataCol; i < strData.length; i++)
				{
				String val = strData[i];
				doubleValues.add(val.equals("N/A") ? null : Double
						.parseDouble(val));
				}
			} catch (NumberFormatException e)
			{
			String errMsg = "Unable to parse double value from token "
					+ strData[i] + " while reading data line " + i + " :" + System.getProperty("line.separator")
					+ line;
			throw new METWorksException(errMsg, false);
			} catch (NullPointerException e)
			{
			String errMsg = "Null pointer exception while reading data line :" + System.getProperty("line.separator")
					+ line;
			throw new METWorksException(errMsg, false);
			}

		return doubleValues;
		}

	private String grabRetentionTime(String nameWithAt)
		{
		String[] tokens = StringUtils.splitAndTrim(nameWithAt, "@");
		return (tokens.length < 2) ? "" : tokens[1];
		}

	private String grabLipidName(String nameWithAt)
		{
		String[] tokens = StringUtils.splitAndTrim(nameWithAt, "@");
		return (tokens.length < 1) ? "" : tokens[0];
		}

	
	public void parseCompoundInfo(String[] strData, String altRt)
		{
		Double rt = Double.NaN, sm = Double.NaN, em = Double.NaN;
		String lc = "-", ks = "-";

		try
			{
			parsedData.startMasses.add(parseDoubleEntry(startMassCol, strData, "start mass"));
			parsedData.endMasses.add(parseDoubleEntry(endMassCol, strData, "end mass"));

			Double nativeRt = parseDoubleEntry(retentionTimeCol, strData, "retention time");
			if (nativeRt == null)
				nativeRt = parseDoubleEntry(altRt);

			parsedData.retentionTimes.add(nativeRt);

			parsedData.lipidClasses.add(parseStringEntry(lipidClassCol, strData));
			parsedData.knownStatuses.add(parseStringEntry(knownStatusCol, strData));
			} 
		catch (Exception e) {  }
		}

	
	private Double parseDoubleEntry(String strValue)
		{
		Double value = null;
		try
			{
			value = Double.parseDouble(strValue);
			} 
		catch (Exception e)
			{
			System.out.println("Cant parse " + strValue + "as a double");
			}

		return value;
		}

	private Double parseDoubleEntry(int col, String[] strData, String label)
		{
		if (col == -1 || strData.length <= col)
			return null;

		Double value = null;
		try
			{
			value = Double.parseDouble(strData[col]);
			} catch (Exception e)
			{
			System.out.println("Cant parse " + label + " " + strData[col]);
			}

		return value;
		}

	private String parseStringEntry(int col, String[] strData)
		{
		if (col == -1 || strData.length <= col)
			return "-";

		return strData[col] == "." ? "-" : strData[col];
		}
	}


