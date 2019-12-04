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

import edu.umich.brcf.shared.layers.domain.Document;


public class MyFileLink extends Link <Void> // issue 39
	{
	Document doc;
	
	public MyFileLink(String id, IModel model) 
		{
		super(id);
		doc=(Document) model.getObject();
		}
	@Override
	public void onClick() 
		{
		IResourceStream resourceStream = new AbstractResourceStreamWriter() 
			{
			@Override
			public String getContentType() 
				{                        
				return doc.getFileType();
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

			@Override
			public void write(OutputStream stream) throws IOException
				{
				byte[] contents = doc.getContents();
				ByteArrayInputStream bis = new ByteArrayInputStream(contents);
				if (bis == null) 
					throw new RuntimeException("Could not find file :" + doc.getFileName());
				
				try {
					Streams.copy(bis, stream);
					} 
				catch (IOException e) { throw new RuntimeException(e); }
				
				}
			
			};
		
		String reportName = doc.getFileName(); 
	
		RequestCycle cycle = getRequestCycle();
		ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(resourceStream);
		handler.setFileName(reportName);
		cycle.scheduleRequestHandlerAfterCurrent(handler);
		}
	}
		
		
		