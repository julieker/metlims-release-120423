////////////////////////////////////////////////////
// LipidMapsMatchesWriter.java
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

import edu.umich.brcf.metabolomics.layers.domain.LipidMapsEntry;
///import edu.umich.brcf.metabolomics.panels.admin.organization.TabbedDataWriter;
import edu.umich.brcf.shared.util.interfaces.IWriteableTextData;



public class LipidMapsMatchesWriter  implements IWriteableTextData, Serializable
	{
	public LipidMapsMatchesWriter() {}
	
	List <String> colTitles = Arrays.asList(new String [] { "Lipid Maps Id", "Systematic Name", "Common Name","Main Class", 
		 "SubClass", "Class Level 4", "Molecular Formula", "Exact Mass", "Inchi Key", "Category", "Smiles"});
	
	List<LipidMapsEntry> entryList;
	String fileName;
	
	
	public LipidMapsMatchesWriter(String fullName, List<LipidMapsEntry> lst)
		{
		fileName = fullName;
		entryList = lst;
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
		
		for (LipidMapsEntry entry : entryList)
			sb.append(entry.toCharDelimited(delimiterAsString) + System.getProperty("line.separator"));
		
		return sb.toString();
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
	
	
	public void setColTitles(List<String> titles)
		{
		this.colTitles = titles;
		}
	}