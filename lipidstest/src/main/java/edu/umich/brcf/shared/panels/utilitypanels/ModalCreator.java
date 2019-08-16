package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import edu.umich.brcf.shared.panels.login.MedWorksSession;

public class ModalCreator
	{
	public static ModalWindow createModalWindow(String id, int width, int height)
		{
		ModalWindow modal1 = new ModalWindow(id);
		modal1.setInitialWidth(width);
		modal1.setInitialHeight(height);
		modal1.setWidthUnit("em");
		modal1.setHeightUnit("em");
		return modal1;
		}

	
	public static ModalWindow createScalingModalWindow(String id,
			double widthPct, double heightPct, MedWorksSession session)
		{
		ModalWindow modal1 = new ModalWindow(id);

		int pageHeight = ((MedWorksSession) session).getClientProperties().getBrowserHeight();
		modal1.setInitialHeight((int) Math.round(pageHeight * heightPct));

		int pageWidth = ((MedWorksSession) session).getClientProperties().getBrowserWidth();
		modal1.setInitialWidth((int) Math.round(pageWidth * widthPct));

		return modal1;
		}
	
	
	public static ModalWindow buildPageRenewingModal()
		{
		return buildPageRenewingModal("modal1");
		}
	
	
	public static ModalWindow buildPageRenewingModal(String id)
		{
		final ModalWindow modal1 = ModalCreator.createModalWindow(id, 500, 350);
		
		 	
		 modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	    	{
	        public void onClose(AjaxRequestTarget target)
	        	{	        	
	        	modal1.setOutputMarkupId(true);
	        	target.add(modal1);
	    		if (modal1.getParent()!= null )
	    			{
	    			modal1.getParent().setOutputMarkupId(true);
	    			target.add(modal1.getParent());
	    			}
	        	//target.add(c);
	        	}
	    	});
		 
		return modal1;
		}

	}
