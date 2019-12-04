////////////////////////////////////////////////////
// NewSampleEntryPanel.java
// Written by Jan Wigginton, Jul 11, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.sample_submission;


import java.io.File;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.Mrc2SubmissionDataService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.AbstractGenericSampleFormUploadPage;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.panels.utilitypanels.PrintBarcodesOldPage;
import edu.umich.brcf.shared.panels.utilitypanels.ReserveBarcodesPage;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2SubmissionSheetData;
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalSubmissionSheetData;
import edu.umich.brcf.shared.util.interfaces.ISavableSampleData;
import edu.umich.brcf.shared.util.sheetreaders.Mrc2OldSubmissionSheetReader;
import edu.umich.brcf.shared.util.sheetreaders.Mrc2TransitionalSubmissionSheetReader;



public class NewSampleEntryPanel extends Panel 
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	Mrc2SubmissionDataService mrc2SubmissionDataService;
	
	public NewSampleEntryPanel(String id) 
		{
		super(id);
		
		final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 250, 250);
		add(modal1);
		add(buildLinkToModal("support", modal1));
		add(buildLinkToModal("printBarcodes", modal1));
		add(buildLinkToModal("reserveBarcodes", modal1));
		add(buildLinkToModal("uploadSamplesNew", modal1));
		add(buildLinkToModal("uploadNewReaderOldForm", modal1));
	//	add(buildLinkToModal("uploadSamplesOld", modal1));
		}
	
	
	private IndicatingAjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1)
		{
		// issue 39
		return new IndicatingAjaxLink <Void>(linkID)
        	{
	        @Override
            public void onClick(AjaxRequestTarget target)
            	{
           		modal1.setInitialWidth(linkID.startsWith("p") ? 600 : (linkID.startsWith("s") ? 800 : 650));
           		modal1.setInitialHeight(linkID.startsWith("p") ? 200 : (linkID.startsWith("s") ? 400 : 200));
           		
            	modal1.setPageCreator(new ModalWindow.PageCreator()
	            	{
                    public Page createPage()
	                    {
	                    if("printBarcodes".equals(linkID))
	                    	return new PrintBarcodesOldPage(getPage());
	                    
	                    // Issue 205
	                    if("reserveBarcodes".equals(linkID))
	                    	return new ReserveBarcodesPage(getPage());
	                   
	                    if("uploadSamplesNew".equals(linkID))
	                    	return buildReaderPage(getPage());
	                 
	                 
	                // JAK take out old reader link    
	               //     if ("uploadNewReaderOldForm".equals(linkID))
	               //     	return buildOldReaderPage(getPage());
	                    	
	              //      if("uploadSamplesOld".equals(linkID))
	               //     	return new SampleFormUploadPage(getPage());
	                    
	                    return new SampleSubmissionSupportPage(getPage());
	                    }
		            });
            	 modal1.show(target);
            	}
        	};
		}
	
	//AjaxChearSampleField
	private AbstractGenericSampleFormUploadPage buildOldReaderPage(Page page)
		{
		return new AbstractGenericSampleFormUploadPage(page)
			{
			@Override
			protected ISavableSampleData readData(File newFile, FileUpload upload) throws SampleSheetIOException 
				{
				Mrc2OldSubmissionSheetReader reader = new Mrc2OldSubmissionSheetReader();
				return (Mrc2SubmissionSheetData) reader.readWorkBook(newFile, upload);
				}
	
			@Override
			protected int saveData(ISavableSampleData data) throws METWorksException
				{
				return sampleService.saveSamples((Mrc2SubmissionSheetData) data); 
				}
			
			@Override
			protected String getMailAddress() { return "metabolomics@med.umich.edu"; }

			@Override
			protected String getMailTitle() { return "METLIMS Sample Registration Message"; }
			};
		}
	
	
	private AbstractGenericSampleFormUploadPage buildReaderPage(Page page)
		{
		return new AbstractGenericSampleFormUploadPage(page)
			{
			@Override
			protected ISavableSampleData readData(File newFile, FileUpload upload) throws SampleSheetIOException 
				{
				Mrc2TransitionalSubmissionSheetReader reader = new Mrc2TransitionalSubmissionSheetReader();
				return (Mrc2TransitionalSubmissionSheetData) reader.readWorkBook(newFile, upload);
				}
	
			@Override
			protected int saveData(ISavableSampleData data) throws METWorksException
				{
				int nSaved = mrc2SubmissionDataService.saveSamples((Mrc2TransitionalSubmissionSheetData) data); 
				return nSaved;
				}
			
			@Override
			protected String getMailAddress() { return "metabolomics@med.umich.edu"; }

			@Override
			protected String getMailTitle() { return "METLIMS Sample Registration Message"; }
			};
		}
	}
