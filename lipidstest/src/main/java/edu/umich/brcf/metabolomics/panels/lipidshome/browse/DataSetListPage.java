///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//DataSetListPage.java
//Written by Jan Wigginton March 2015,  Rewritten 05/01/15
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.util.Calendar;

import org.apache.wicket.markup.html.WebPage;


import edu.umich.brcf.shared.panels.login.MedWorksBasePage;


public class DataSetListPage extends MedWorksBasePage
	{
	public DataSetListPage(String id, WebPage backPage, String expId, Boolean ifEdit)
		{
		//super(null);
		add(new DataSetListPanel("searchResults", backPage, expId, ifEdit, false));
		}
	
	
	public DataSetListPage(String id, WebPage backPage, Calendar fromDateCal, Calendar toDateCal, Boolean ifEdit, Boolean useRunDate)
		{
		//super(null);
		add(new DataSetListPanel("searchResults", backPage, fromDateCal, toDateCal, ifEdit, useRunDate));
		}
	}
