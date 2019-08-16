///////////////////////////////////////
//SampleLocationsPage.java
//Written by Jan Wigginton May 2015
///////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;

public abstract class SampleLocationsPage extends WebPage 
	{
	public SampleLocationsPage(WebPage backPage, ModalWindow modal, String sampleId)
		{
		add(new SampleLocationsPanel("locationHistory", sampleId, modal));
		}
	
	protected abstract void onSave(AjaxRequestTarget target, String sampleId);
	}
