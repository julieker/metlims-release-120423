////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  SampleAssayTrackingPage.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


package edu.umich.brcf.metabolomics.panels.workflow.workflow_tracking;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.panels.login.MedWorksBasePage;


public class SampleAssayTrackingPage extends MedWorksBasePage
	{
	@SpringBean
	ExperimentService experimentService;
	
	public SampleAssayTrackingPage(String id, String expId, WebPage backPage, String assayId) 
		{
		add(new SampleAssayTrackingPanel("searchResults", experimentService.loadById(expId), backPage, assayId));
		}
	 }
