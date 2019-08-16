////////////////////////////////////////////////////////////////////
//AjaxBackButton.java
//Written by Jan Wigginton June 2015
////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.widgets;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;

public class AjaxBackButton extends AjaxLink<Object>
	{
	WebPage backPage;

	public AjaxBackButton(String id, WebPage backPage)
		{
		super(id);
		this.backPage = backPage;
		}

	@Override
	public void onClick(AjaxRequestTarget arg0)
		{
		setResponsePage(backPage);
		}
	}
