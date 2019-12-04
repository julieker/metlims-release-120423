// Upgraded/rewritten for Wicket 6.24 August 2016 (JW)

package edu.umich.brcf.shared.util.widgets;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import edu.umich.brcf.shared.layers.domain.ClientReport;


public class MyReportLink  extends Link <Void>
// issue 39
	{
	ClientReport cRep;
	
	public MyReportLink(String id, IModel model) 
		{
		super(id);
		cRep=(ClientReport) model.getObject();
		}
	@Override
	public void onClick() 
		{
		IResourceStream resourceStream = new AbstractResourceStreamWriter() 
			{
			
			@Override
			public String getContentType() 
				{                        
				return cRep.getFileType();
				}
	
			@Override
			public void write(OutputStream str )
				{
				byte[] contents = cRep.getContents();
				ByteArrayInputStream bis = new ByteArrayInputStream(contents);
				if (bis == null) 
					throw new RuntimeException("Could not find file :" + cRep.getFileName());
				
				try {
					Streams.copy(bis, str);
					} 
				catch (IOException e) { throw new RuntimeException(e); }
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
		
		String reportName = cRep.getFileName(); 
	
		RequestCycle cycle = getRequestCycle();
		ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(resourceStream);
		handler.setFileName(reportName);
		cycle.scheduleRequestHandlerAfterCurrent(handler);
		}
	}