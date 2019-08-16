////////////////////////////////////////////////////
// PeakSetWriter.java
// Written by Jan Wigginton, Sep 16, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.util.io.Streams;

import edu.umich.brcf.metabolomics.layers.domain.Ms2Peak;
import edu.umich.brcf.shared.util.interfaces.IWriteableTextData;



public class PeakSetWriter  implements IWriteableTextData, Serializable
	{
	public PeakSetWriter() {}
	
	private List<String> colTitles = Arrays.asList(new String [] {"MRC2 Sample Id", "Column Label", "Client Sample Label", "Peak Area"});
	
	private List<Ms2Peak> peaksList;
	private String fileName;
	
	
	public PeakSetWriter(String fullName, List<Ms2Peak> lst)
		{
		fileName = fullName;
		peaksList = lst;
		}

	@Override
	public String getReportFileName()
		{
		return fileName;
		}


	@Override
	public Character getDelimiter()
		{
		return '\t';	
		}


	protected String getReportString()
		{
		StringBuilder sb = new StringBuilder();
		String delimiterAsString = getDelimiter().toString();
		
		for (String title : colTitles)
			sb.append(title + delimiterAsString + " ");
				
		for (Ms2Peak peak : peaksList)
			sb.append(peak.toCharDelimited(delimiterAsString) + System.getProperty("line.separator"));
		
		return sb.toString();
		}

	
	@Override
	public void generateTextReport(OutputStream output)
		{
		String contents = getReportString();
		ByteArrayInputStream bytes = null;

		try { bytes = new ByteArrayInputStream(contents.getBytes("UTF-8")); }
		catch (UnsupportedEncodingException e1) { e1.printStackTrace(); }

		
		try { Streams.copy(bytes, output); } 
		catch (IOException e) { throw new RuntimeException(e); }
		}

	}



/*
 * 
 * LipidBlastMatchesWriter
 * 
/////////////////////////////////////////////////////
//Ms2DataSetWriter.java
//Written by Jan Wigginton, Aug 3, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.util.io.Streams;

import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.shared.util.interfaces.IWriteableTextData;


public class Ms2DataSetWriter implements IWriteableTextData, Serializable
	{
	private Ms2DataSetHandler dataHandler;
	private Character delimiter = '\t';
	
	public Ms2DataSetWriter()
		{
		this(null);
		}
	
	public Ms2DataSetWriter(Ms2DataSetHandler handler)
		{
		this.dataHandler = handler;
		}
	
	@Override
	public String getReportFileName()
		{
		String fileName =  "peak_areas" + dataHandler.getDataSet().getExpId();
		
		if (dataHandler.getDataSet().getIonMode() != null && !dataHandler.getDataSet().getIonMode().trim().equals(""))
			fileName += "_" + dataHandler.getDataSet().getIonMode();
		
		if (dataHandler.getDataSet().getDataNotation() != null && !dataHandler.getDataSet().getDataNotation().trim().equals(""))
			fileName += "_" + dataHandler.getDataSet().getDataNotation();
		
		return fileName;
		}

	
	@Override
	public void generateTextReport(OutputStream output)
		{
		String contents = getReportString();
		ByteArrayInputStream bytes = null;
		try
			{
			bytes = new ByteArrayInputStream(contents.getBytes("UTF-8"));
			} 
		catch (UnsupportedEncodingException e1)
			{
			e1.printStackTrace();
			}
		
		try {
			Streams.copy(bytes, output);
			} 
		catch (IOException e) { throw new RuntimeException(e); }
		}

	
	
	
	private String getReportString()
		{
		StringBuilder builder  = new StringBuilder();
		List<String> colTitles = getColTitles();

		for (String title : colTitles)
			builder.append(title + delimiter.toString() + " ");
				
		builder.append(System.getProperty("line.separator"));
		
		if (dataHandler != null && dataHandler.getDataSet() != null && dataHandler.getDataSet().getPeakSets() != null)
			for (Ms2PeakSet set : dataHandler.getDataSet().getPeakSets())
				{
				builder.append(set.toCharDelimited(delimiter.toString() +  " "));
				builder.append(System.getProperty("line.separator"));
				}
		
		return builder.toString();
		}
	
	
	
	public List<String> getColTitles()
		{
		List<String> headers = Arrays.asList(new String [] {"Peak Set", "Compound Name", "Start Mass", "End Mass",
				"Expected Rt", "Lipid Class", "Known Status"});

		List <String> colTitles = new ArrayList<String>();
		for (int i = 0; i < headers.size();i++)
			colTitles.add(headers.get(i));

		if (dataHandler == null || dataHandler.tableColumnLabels == null)
			return colTitles;
		
		for (int i = 0; i < dataHandler.tableColumnLabels.size(); i++) 
			{
			String val = dataHandler.tableColumnLabels.get(i);
			colTitles.add(val == null ? "-" : val);
			}
		return colTitles;
		}
	
	public Character getDelimiter()
		{
		return delimiter;
		}

	public void setDelimiter(Character delimiter)
		{
		this.delimiter = delimiter;
		}
	}




private ResourceLink buildDownloadLink(final String fullName)
{
List<String> headers = Arrays.asList(new String [] {"Peak Set", "Compound Name", "Start Mass", "End Mass",
"Expected Rt", "Lipid Class", "Known Status"});

List <String> colTitles = new ArrayList<String>();
for (int i = 0; i < headers.size();i++)
	colTitles.add(headers.get(i));

for (int i = 0; i < dataHandler.tableColumnLabels.size(); i++) 
	{
	String val = dataHandler.tableColumnLabels.get(i);
	colTitles.add(val == null ? "-" : val);
	}

String fileName =  "peak_areas" + dataHandler.dataSet.getExpId();
if (dataHandler.dataSet.getIonMode() != null && !dataHandler.dataSet.getIonMode().trim().equals(""))
fileName += "_" + dataHandler.dataSet.getIonMode();
if (dataHandler.dataSet.getDataNotation() != null && !dataHandler.dataSet.getDataNotation().trim().equals(""))
fileName += "_" + dataHandler.dataSet.getDataNotation();
fileName += ".tsv";

//MetWorksDataDownload resource = new MetWorksDataDownload(dataHandler.dataSet.peakSets, fileName, colTitles);
//resource.setMimeType("text/tsv");

//return resource.getResourceLink();
}*/
