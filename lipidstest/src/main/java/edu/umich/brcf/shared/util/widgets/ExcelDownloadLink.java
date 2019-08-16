/////////////////////////////////////////////
//ExcelDownloadLink.java
//Written by Jan Wigginton September 2015
/////////////////////////////////////////////

package edu.umich.brcf.shared.util.widgets;

import java.io.OutputStream;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;



public class ExcelDownloadLink extends Link // IndicatingAjaxLink
	{
	IWriteableSpreadsheet report;
	
	public ExcelDownloadLink(String id, IWriteableSpreadsheet report)
		{
		super(id, null); 
		this.report = report;
		}
	
	@Override
	public void onClick()
		{
		doClick(report);
		}
	
	
	public void doClick(final IWriteableSpreadsheet report)
		{
		IResourceStream resourceStream = new AbstractResourceStreamWriter() 
			{
			@Override 
			public void write(OutputStream output) 
				{
				try {   
					report.generateExcelReport(output); }
				catch (Exception e) { }
				}
			
			@Override
			public String getContentType() { return "application/xls";  }
			
			@Override
			public String getStyle() { return null;  }
	
			@Override
			public String getVariation() { return null;  }
	
			@Override
			public Bytes length() { return null; } 
	
			@Override
			public void setStyle(String arg0) { }
	
			@Override
			public void setVariation(String arg0) { } 
			};
			
		String reportName = (report == null ? "Report" : report.getReportFileName());
	
		RequestCycle cycle = getRequestCycle();
		ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(resourceStream);
		handler.setFileName(reportName);
		cycle.scheduleRequestHandlerAfterCurrent(handler);
		}
	
	
	public void setReport(IWriteableSpreadsheet rpt)
		{
		this.report = rpt;
		}
	
	public IWriteableSpreadsheet getReport()
		{
		return this.report;
		}

	
	@Override // issue 464
	public MarkupContainer setDefaultModel(IModel model) 
	    {
		// TODO Auto-generated method stub
		return this;
	    }
	}
	




/*

public class ExcelDownloadLink extends Link
	{
	IWriteableSpreadsheet report;
	
	public ExcelDownloadLink(String id, IWriteableSpreadsheet report)
		{
		super(id, null); 
		this.report = report;
		}
	
	@Override
	public void onClick() 	
		{
		doClick(report);
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
		public void write(OutputStream str)
			{
			StringResourceStream s;
			try {   
				//OutputStream str = output.getOutputStream();
					report.generateExcelReport(str); }
				catch (Exception e) { }
			}

		@Override
		public String getStyle()
			{
			// TODO Auto-generated method stub
			return null;
			}

		@Override
		public String getVariation()
			{
			// TODO Auto-generated method stub
			return null;
			}

		@Override
		public Bytes length()
			{
			// TODO Auto-generated method stub
			return null;
			}

		@Override
		public void setStyle(String arg0)
			{
			// TODO Auto-generated method stub
			
			}

		@Override
		public void setVariation(String arg0)
			{
			// TODO Auto-generated method stub
			
			}
		};
		
	String reportName = report.getReportFileName();
	
	RequestCycle cycle = getRequestCycle();
	ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(resourceStream);
	handler.setFileName(reportName);
	cycle.scheduleRequestHandlerAfterCurrent(handler);
	}
	
	
	public void setReport(IWriteableSpreadsheet rpt)
		{
		this.report = rpt;
		}
	}
	



*/