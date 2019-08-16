////////////////////////////////////////////////////
// TextDownloadLink.java
// Written by Jan Wigginton, Aug 2, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.widgets;

import java.io.OutputStream;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import edu.umich.brcf.shared.util.interfaces.IWriteableTextData;


public class TextDownloadLink extends Link
	{
	private boolean useTsv = false;
	private IWriteableTextData data;
	
	
	public TextDownloadLink(String id, IWriteableTextData data)
		{
		super(id, null); 
		this.data = data;
		}
	
	@Override
	public void onClick() 	
		{
		doClick(data);
		}
	
	@Override // issue 464
	public MarkupContainer setDefaultModel(IModel model) 
	    {
		// TODO Auto-generated method stub
		return this;
	    }
	
	public void doClick(final IWriteableTextData data)
		{
		IResourceStream resourceStream = new AbstractResourceStreamWriter() 
			{
			@Override
			public String getContentType() 
				{     
				return  data.getDelimiter().equals(',') ?  "application/csv" : "application/tsv";
				}
			
			@Override
			public void write(OutputStream str )
				{
				try 
					{   
					//OutputStream str = output.getOutputStream();
					data.generateTextReport(str);
					}
				catch (Exception e) { }
				}
		
			
			@Override
			public String getStyle()
				{
				return null;
				}
			
			@Override
			public String getVariation()
				{
				return null;
				}
			
			@Override
			public Bytes length()
				{
				return null;
				}
			
			@Override
			public void setStyle(String arg0)
				{
				}
			
			@Override
			public void setVariation(String arg0)
				{
				}
			};
			
		String fileName = data.getReportFileName() +  (data.getDelimiter().equals(',') ?  ".csv" : ".tsv");
		RequestCycle cycle = getRequestCycle();
		ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(resourceStream);
		handler.setFileName(fileName);
		cycle.scheduleRequestHandlerAfterCurrent(handler);
		}
		
	
	public void setData(IWriteableTextData data)
		{
		this.data = data;
		}
	}


