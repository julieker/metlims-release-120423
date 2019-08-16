////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistCommentViewerPage.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.workflow_tracking;

import org.apache.wicket.markup.html.WebPage;

import edu.umich.brcf.shared.panels.login.MedWorksBasePage;


public class WorklistDetailViewerPage extends MedWorksBasePage
	{
	public WorklistDetailViewerPage(String id, WebPage backPage, String expId, String assayId, String dataSetId, 
			String date, String mode, String uploadedBy)
		{
		add(new WorklistDetailViewerPanel("searchResults", backPage, expId, assayId, dataSetId, date, mode, uploadedBy));
		}
	}
