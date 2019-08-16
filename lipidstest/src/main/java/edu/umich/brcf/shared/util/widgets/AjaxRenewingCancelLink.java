////////////////////////////////////////////////////
// AjaxRenewingCancelLink.java
// Written by Jan Wigginton, Oct 19, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.widgets;




import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;


public class AjaxRenewingCancelLink<T> extends AjaxLink<T>
	{
	ModalWindow modal;
	Page sourcePage;
	
	public AjaxRenewingCancelLink()
		{
		this("cancelButton", null, null);
		}
	
	public AjaxRenewingCancelLink(ModalWindow modal, Page page)
		{
		this("cancelButton", modal, page);
		}
	
	public AjaxRenewingCancelLink(String id, ModalWindow modal, Page page)
		{
		super(id);
		this.sourcePage = page;
		this.modal = modal;
		}
	
	public void onClick(AjaxRequestTarget target)
		{
		//if (modal != null)
		//	modal.close(target);
		if (sourcePage != null)
			setResponsePage(sourcePage);
		}
	}
