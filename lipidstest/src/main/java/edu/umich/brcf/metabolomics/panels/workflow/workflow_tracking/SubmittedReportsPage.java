///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//SubmittedReportsPage.java
//Written by Jan Wigginton Sept 2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.workflow_tracking;

import java.util.Calendar;

import org.apache.wicket.markup.html.WebPage;
import edu.umich.brcf.shared.panels.login.MedWorksBasePage;


public class SubmittedReportsPage extends MedWorksBasePage
	{
	public SubmittedReportsPage(String id, WebPage backPage, String expId, Boolean ifEdit)
		{
		super();
		add(new SubmittedReportsPanel("searchResults", backPage, expId, ifEdit, false));
		}

	// issue 176	
	public SubmittedReportsPage(String id, WebPage backPage, Calendar fromDateCal, Calendar toDateCal, Boolean ifEdit, Boolean useRunDate)
		{
		this ( id,  backPage,  fromDateCal,  toDateCal,  ifEdit,  useRunDate,  true);
		}
	
	public SubmittedReportsPage(String id, WebPage backPage, Calendar fromDateCal, Calendar toDateCal, Boolean ifEdit, Boolean useRunDate, boolean useClient)
		{
		super();
		add(new SubmittedReportsPanel("searchResults", backPage, fromDateCal, toDateCal, ifEdit, useRunDate, useClient));
		}
	}
