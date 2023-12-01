////////////////////////////////////////////////////
// ValidatingAjaxExcelDownloadLink.java
// Written by Jan Wigginton, Mar 21, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import java.nio.file.Path;

import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Duration;
import org.hibernate.result.Output;
import org.springframework.core.env.Environment;

import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;
import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheetReturnStream;
import edu.umich.brcf.shared.util.io.FileUtils;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.util.Date;  

import java.nio.*;
import java.nio.channels.FileChannel;
import javax.servlet.http.HttpServletResponse;

import java.nio.file.Path;

import java.nio.file.Paths;
//import javax.faces.context.FacesContext;



public abstract class ValidatingAjaxExcelDownloadLink extends Link <Void> // issue 39
	{
	IWriteableSpreadsheetReturnStream report;
	ValidatingAjaxExcelDownloadLink validatingAjaxExcelDownloadLink = this;
	OutputStream vOutputStream;
	List <String> worklistDataFromGenerate = new ArrayList <String> ();
	ResourceStreamRequestHandler handler;
	
	public ValidatingAjaxExcelDownloadLink(String id, IWriteableSpreadsheetReturnStream report)
		{
		super(id);
		this.report = report;
		}
	
	public void doClick(final IWriteableSpreadsheetReturnStream report)
		
		{
		IResourceStream resourceStream = new AbstractResourceStreamWriter() 
			{
			@Override
			public String getContentType() 
				{                        
				return "application/xls";
				}

			@Override
			// issue 313
			public void write(OutputStream output)   
			{
			try
				{
				worklistDataFromGenerate = report.generateExcelReport(output); 	
				output.flush();
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}
			
			}
			
			
			@Override
			public String getStyle(){ return null; } 
			
			@Override
			public String getVariation(){ return null; } 
			
			@Override
			public Bytes length() { return null; } 

			@Override
			public void setStyle(String arg0) { } 

			@Override
			public void setVariation(String arg0) { }
			};
			
			
			String reportName = report.getReportFileName();	
			// issue 308
			RequestCycle cycle = getRequestCycle();
			ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(resourceStream).setCacheDuration(Duration.ONE_SECOND);
			handler.setFileName(reportName);
			RequestCycle.get().scheduleRequestHandlerAfterCurrent(handler);
			
		    handler.detach(cycle);
		    cycle.detach();  
		    RequestCycle cycle2 = getRequestCycle();  
			//////////////////
			
			
		    }

   
	public void setReport(IWriteableSpreadsheetReturnStream rpt)
		{
		this.report = rpt;
		}
	
	
	@Override
	public void onClick() 	
		{
		if (validate())
			{
			doClick(report); 
			//doClick(report);  
			}
		}


	public abstract boolean validate();

	public abstract boolean validate(AjaxRequestTarget target, IWriteableSpreadsheetReturnStream report);
	}

	
	
