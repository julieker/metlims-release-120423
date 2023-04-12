////////////////////////////////////////////////////
// NewProgressTrackingPanel.java
// Written by Jan Wigginton, Jul 11, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.progresstracking;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.Mrc2SubmissionDataService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;

public class NewDefaultTrackingAdminPanel extends Panel 
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	Mrc2SubmissionDataService mrc2SubmissionDataService;
	
	public NewDefaultTrackingAdminPanel(String id) 
		{
		super(id);
		
		final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 250, 250);
		add(modal1);
	    add(buildLinkToModal("ViewDefaultTrackingInformation", modal1));
		}
	
	private IndicatingAjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1)
		{
		// issue 39
		return new IndicatingAjaxLink <Void>(linkID)
        	{
	        @Override
            public void onClick(AjaxRequestTarget target)
            	{
	        	
           		modal1.setInitialWidth(1500);
           		modal1.setInitialHeight(600);          		
            	modal1.setPageCreator(new ModalWindow.PageCreator()
	            	{
                    public Page createPage()
	                    {
	                   if("ViewDefaultTrackingInformation".equals(linkID))
	                  // return new ProgressTrackingAdminDetailPage(linkID);
	                        return new ProgressTrackingDefaultPage	(linkID, true) ;
	                	 //  return null;
					return null;
	                    }
		            });
            	 if ("ViewDefaultTrackingInformation".equals(linkID))
            		 modal1.show(target); 
	        	
            	}
        	};
		}	
	}