///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MedWorksMasterPage.java
//Rewritten 01/15; Imported/Updated by Jan Wigginton 02/09/16
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf;

import org.apache.wicket.request.mapper.parameter.PageParameters;


public class MedWorksMasterPage extends MedWorksSecurePage
	{
	private static final long serialVersionUID = 1L;

	public MedWorksMasterPage(final PageParameters parameters) 
		{
		MasterPanel mainPanel = new MasterPanel("tabPanel", parameters);
		add(mainPanel);
		}
	}

// Comment : 