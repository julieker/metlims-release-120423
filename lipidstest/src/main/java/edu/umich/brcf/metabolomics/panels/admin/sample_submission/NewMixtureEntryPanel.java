////////////////////////////////////////////////////
// NewMixtureEntryPanel.java
// Written by  Julie Keros Nov 24 2020 for uploading mixtures
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.sample_submission;

import java.io.File;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.metabolomics.layers.service.Mrc2MixtureDataService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.AbstractGenericMixtureFormUploadPage;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.MixtureSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalMixtureSheetData;
import edu.umich.brcf.shared.util.interfaces.ISavableMixtureData;
import edu.umich.brcf.shared.util.sheetreaders.Mrc2TransitionalMixtureSheetReader;

public class NewMixtureEntryPanel extends Panel 
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	Mrc2MixtureDataService mrc2MixtureDataService;
	
	public NewMixtureEntryPanel(String id) 
		{
		super(id);	
		final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 250, 250);
		add(modal1);
		add(buildLinkToModal("uploadMixtures", modal1));
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
	                    return buildReaderMixturePage(getPage());
	                    }
		            });
            	 modal1.show(target);
            	}
        	};
		}
	
	private AbstractGenericMixtureFormUploadPage buildReaderMixturePage(Page page)
		{
		return new AbstractGenericMixtureFormUploadPage(page)
			{
			@Override
			protected ISavableMixtureData readData(File newFile, FileUpload upload) throws MixtureSheetIOException 
				{
				Mrc2TransitionalMixtureSheetReader reader = new Mrc2TransitionalMixtureSheetReader();
				
				return (Mrc2TransitionalMixtureSheetData) reader.readWorkBook(newFile, upload);
				}
	
			@Override
			protected int saveData(ISavableMixtureData data) throws METWorksException
				{
			    int nSaved = mrc2MixtureDataService.saveMixtures((Mrc2TransitionalMixtureSheetData) data); 
			    return nSaved;			
				}
			
			@Override
			protected String getMailAddress() { return "metabolomics@med.umich.edu"; }

			@Override
			protected String getMailTitle() { return "METLIMS Mixture Registration Message"; }
			};
		}
	}
