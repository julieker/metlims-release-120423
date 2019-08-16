////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistOverviewPage.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.workflow_tracking;

import org.apache.wicket.markup.html.WebPage;

import edu.umich.brcf.shared.panels.login.MedWorksBasePage;

public class WorklistOverviewPage extends MedWorksBasePage
	{
	public WorklistOverviewPage(String id, WebPage backPage, String expId,
			String assayId, Boolean ifEdit)
		{
		add(new WorklistOverviewPanel("searchResults", backPage, expId,
				assayId, ifEdit, false));
		}
	}
