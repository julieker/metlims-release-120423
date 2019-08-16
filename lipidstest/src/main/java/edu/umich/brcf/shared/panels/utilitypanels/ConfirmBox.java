////////////////////////////////////////////////////
// ConfirmBox.java
// Written by Jan Wigginton, April 2018
////////////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;

public abstract class ConfirmBox extends WebPage
	{
	public ConfirmBox(String id, String message, ModalWindow modal)
		{
		//add(new Label("message", new Model<String>(message)));
		add(new MultiLineLabel("message", new Model<String>(message))); // issue 268
		add(buildConfirmButton("confirmButton", modal));
		add(new AjaxCancelLink(modal));
		}

	public IndicatingAjaxLink buildConfirmButton(String id, final ModalWindow modal)
		{
		return new IndicatingAjaxLink(id)
			{
			@Override
			public void onClick(AjaxRequestTarget target) {
				doAction(target);
				modal.close(target);
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		}
	
	protected abstract void doAction(AjaxRequestTarget target);
	}
