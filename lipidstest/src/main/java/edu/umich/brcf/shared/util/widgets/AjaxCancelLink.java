////////////////////////////////////////
//AjaxCancelLink.java
//Written by Jan Wigginton June 2015
//////////////////////////////////////

package edu.umich.brcf.shared.util.widgets;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;


public class AjaxCancelLink<T> extends AjaxLink<T>
	{
	ModalWindow modal;
	private String buttonLabel = "Close";

	public AjaxCancelLink()
		{
		this("cancelButton", null);
		}

	public AjaxCancelLink(ModalWindow modal)
		{
		this("cancelButton", modal);
		}

	public AjaxCancelLink(String id, ModalWindow modal)
		{
		this(id, modal, "Close");
		}
	
	public AjaxCancelLink(String id, ModalWindow modal, String buttonLabel)
		{
		super(id);
		setButtonLabel(buttonLabel);
		this.modal = modal;
		}

	public void onClick(AjaxRequestTarget target)
		{
		if (modal != null)
			modal.close(target);
		}

	public String getButtonLabel()
		{
		return buttonLabel;
		}

	public void setButtonLabel(String buttonLabel)
		{
		this.buttonLabel = buttonLabel;
		}
	
	
	@Override
	protected void onComponentTag(ComponentTag tag)
		{
		super.onComponentTag(tag);
		String label = getButtonLabel();
		tag.put("value", label);
		}
	}
