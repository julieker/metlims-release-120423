////////////////////////////////////////////
//AjaxExcelDownloadLink.java
//Written by Jan Wigginton, October 2015
////////////////////////////////////////////

package edu.umich.brcf.shared.util.widgets;


import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.OutputStream;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;



public abstract class AjaxExcelDownloadLink extends Link 
	{
	IWriteableSpreadsheet report;
	
	public AjaxExcelDownloadLink(String id, IWriteableSpreadsheet report)
		{
		super(id);
		this.report = report;
		}
	
	public void doClick(final IWriteableSpreadsheet report)
		{
		IResourceStream resourceStream = new AbstractResourceStreamWriter() 
			{
			@Override
			public String getContentType() 
				{                        
				return "application/xls";
				}

			@Override
			public void write(OutputStream output)
				{
				try {   
					//OutputStream str = output.getOutputStream();
						report.generateExcelReport(output); }
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
			
		String reportName = report.getReportFileName();
		//getRequestCycle().setRequestTarget(new ResourceStreamRequest(resourceStream)
		//	.setFileName(reportName));
		
		RequestCycle cycle = getRequestCycle();
		ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(resourceStream);
		handler.setFileName(reportName);
		cycle.scheduleRequestHandlerAfterCurrent(handler);
		}


	public void setReport(IWriteableSpreadsheet rpt)
		{
		this.report = rpt;
		}
	
	
	@Override
	public void onClick() 	
		{
		doClick(report);
		}



	public abstract boolean validate(AjaxRequestTarget target, IWriteableSpreadsheet report);
	}

	
	
