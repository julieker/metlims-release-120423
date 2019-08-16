////////////////////////////////////////////////////
// TabbedDataWriter.java
// Written by Jan Wigginton, Aug 22, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.wicket.util.io.Streams;


public abstract class TabbedDataWriter
	{
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



	protected abstract String getReportString();
	}
