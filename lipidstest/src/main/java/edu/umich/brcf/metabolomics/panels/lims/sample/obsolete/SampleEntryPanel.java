package edu.umich.brcf.metabolomics.panels.lims.sample;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;

import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;



public class SampleEntryPanel extends Panel {

	public SampleEntryPanel(String id) 
		{
		super(id);
		
		final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 250, 250);
		add(modal1);
		add(buildLinkToModal("support", modal1));
		add(buildLinkToModal("printBarcodes", modal1));
		add(buildLinkToModal("uploadSamples", modal1));
//		add(buildLinkToModal("createSamples", modal1));
		}
	
	private IndicatingAjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1)
		{
		return new IndicatingAjaxLink(linkID)
        	{
            @Override
            public void onClick(AjaxRequestTarget target)
            	{
           		modal1.setInitialWidth(linkID.startsWith("p") ? 600 : 800);
           		
           		int height = 400;
           		if (linkID.startsWith("p"))
           			height = 250;
           		else if (linkID.startsWith("u"))
           			height = 200;
           			
           		modal1.setInitialHeight(height);
            	
           		
            	modal1.setPageCreator(new ModalWindow.PageCreator()
	            	{
                    public Page createPage()
	                    {
	                  //  if(linkID.startsWith("p"))
	                   // 	return new PrintBarcodesPage(getPage());
	                   return null;
	                  //  if(linkID.startsWith("u"))
	                  //  	return new SampleFormUploadPage(getPage());
	                    
	                  ///  return new SampleSubmissionSupportPage(getPage());
	                    }
		            });
            	 modal1.show(target);
            	}
        	};
		}

}
