package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.util.Date;

import org.apache.wicket.markup.html.WebPage;


import edu.umich.brcf.shared.panels.login.MedWorksBasePage;


public class DrccMainPage extends MedWorksBasePage
	{
	public DrccMainPage(String id, WebPage backPage, String expId, String selectedMode, Date analysisDate)
		{
		add(new DrccTabbedMainPanel("drccMainPanel", expId, backPage, selectedMode, analysisDate));
		}
	}
