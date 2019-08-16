////////////////////////////////////////////////////
// PrepSearchResultsPage.java
// Written by Jan Wigginton, Sep 8, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newestprep;

import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.MedWorksSecurePage;
import edu.umich.brcf.shared.layers.service.ExperimentService;



public class PrepSearchResultsPage extends  MedWorksSecurePage
	{
	String windowTitle= "";
	
	@SpringBean 
	ExperimentService experimentService;
	
	public PrepSearchResultsPage(String id,  List<String> eids, ModalWindow modal)
		{
		add(new Label("windowTitle", new PropertyModel<String>(this, "windowTitle")));
		
		add(new NewPrepSearchResultsPanel("searchResults"));
		}
	
	public String getWindowTitle()
		{
		return windowTitle;
		}
	
	public void setWindowTitle(String windowTitle)
		{
		this.windowTitle = windowTitle;
		}
	}