package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public abstract class MedworksErrorPage extends WebPage
	{
	public MedworksErrorPage(Page backPage, String error)
		{
		add(new Label("error", error));
		// issue 39
		add(new AjaxLink <Void>("ok")
			{
				@Override
				public void onClick(AjaxRequestTarget target)
					{
					MedworksErrorPage.this.onOk(target);
					}
			});
		}

	protected abstract void onOk(AjaxRequestTarget target);
	}

// Javascript