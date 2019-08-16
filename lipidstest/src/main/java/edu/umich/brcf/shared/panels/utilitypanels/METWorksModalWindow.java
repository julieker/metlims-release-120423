package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


public abstract class METWorksModalWindow extends ModalWindow 
	{
	protected METWorksModalWindow(String id, String title) 
		{
		super(id);
		setInitialWidth(450);
		setInitialHeight(300);

		setTitle(title);
		}

	/*
	public METWorksModalWindow(String id, String title, final Panel panel, final int mode) 
		{
		super(id);

		// Set sizes of this ModalWindow. You can also do this from the HomePage
		// but its not a bad idea to set some good default values.
		setInitialWidth(450);
		setInitialHeight(300);

		// setTitle(title);

		// Set the content panel, implementing the abstract methods
		setContent(new METWorksModalContentPanel(this.getContentId(), panel, mode) 
			{
			protected void onCancel(AjaxRequestTarget target) { METWorksModalWindow.this.onCancel(target);
			}

			protected void onSave(AjaxRequestTarget target, IModel model) { METWorksModalWindow.this.onSave(target, panel.getDefaultModel());
			}
		});
		*/
	
	public METWorksModalWindow(String id, String title, final Panel panel, final int mode) 
		{
		super(id);

		// Set sizes of this ModalWindow. You can also do this from the HomePage
		// but its not a bad idea to set some good default values.
		setInitialWidth(450);
		setInitialHeight(300);

		// setTitle(title);

		// Set the content panel, implementing the abstract methods
		setContent(new METWorksModalContentPanel(this.getContentId(), panel, mode) 
			{
			protected void onCancel(AjaxRequestTarget target) { METWorksModalWindow.this.onCancel(target);
			}

			protected void onSave(AjaxRequestTarget target, IModel model) { METWorksModalWindow.this.onSave(target, panel.getDefaultModel());
			}
		});
	}

	protected abstract void onCancel(AjaxRequestTarget target);

	protected abstract void onSave(AjaxRequestTarget target, IModel model);
}
