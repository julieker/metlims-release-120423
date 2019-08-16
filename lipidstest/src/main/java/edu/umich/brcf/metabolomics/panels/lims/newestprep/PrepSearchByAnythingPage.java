////////////////////////////////////////////////////
// PrepSearchByAnythingPage.java
// Written by Jan Wigginton, Sep 8, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newestprep;


import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;




public class PrepSearchByAnythingPage extends  WebPage
	{
	@SpringBean
	ExperimentService experimentService;
	
	String windowTitle= "";
	List<String> expIds = new ArrayList<String>();

	
	public PrepSearchByAnythingPage(Page backPage)
		{
		windowTitle = "Search for Sample Details";
		//	add(new Label("windowTitle", new PropertyModel<String>(this, "windowTitle")));
	//	add(buildPrepSearchPanel("searchPanel", (WebPage) backPage));
		}

	}
	//private GenericSearchByAnythingPanel buildPrepSearchPanel(String id, WebPage backPage)
	//	{
		/*
		GenericSearchByAnythingPanel panel =  new GenericSearchByAnythingPanel(id, backPage, "Sample")
			{
			@Override
			protected void doOverallSubmit()
				{
				}

			@Override
			protected void doExperimentSelect(String expId, AjaxRequestTarget target) 
				{
				expIds.clear();
				expIds.add(expId);
				}

			@Override
			protected void doContactSelect(String contact, AjaxRequestTarget target)
				{
				}

			@Override
			protected void doProjectSelect(String projId, AjaxRequestTarget target)
				{
				}

			@Override
			protected void doSampleSelect(String sampleId, AjaxRequestTarget target)
				{
				}

			@Override
			protected void doOrganizationSelect(String orgId, AjaxRequestTarget target)
				{
				}

			@Override
			protected Page grabResponsePage(final ModalWindow modal)
				{
				return new PrepSearchResultsPage("searchResults", expIds, modal);
				}
			};
			
		panel.setSearchLabel("Prep");
		return panel;
		}
	
	
	public String getWindowTitle()
		{
		return windowTitle;
		}
		
	public void setWindowTitle(String windowTitle)
		{
		this.windowTitle = windowTitle;
		} */
//	}