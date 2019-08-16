package edu.umich.brcf.lipidstest;


import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebPage;


public class HomePage extends WebPage 
	{
	private static final long serialVersionUID = 1L;
	
	public HomePage(final PageParameters parameters) 
		{
		add(new FeedbackPanel("feedback"));
		}
	}
