package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import edu.umich.brcf.shared.panels.login.MedWorksSession;


public abstract class METWorksModalContentPanel extends Panel {
	public static int MODE_SAVE_AND_CANCEL_BUTTONS = 0;
	public static int MODE_CLOSE_BUTTON_ONLY = 1;
	public static int MODE_SAVE_AND_CLOSE_BUTTONS = 2;

	// private Form form;

	public METWorksModalContentPanel(String id, final Panel panel, int mode) {
		super(id);

		// Create the form, to use latter for the buttons
		// form = new Form("form", new CompoundPropertyModel(panel.getModel()));
		// add(form);
		//
		// form.
		add(panel);
		AjaxLink save;
		AjaxLink cancel;
		AjaxLink close;

		// form.
		// issue 39
		add(save = new AjaxLink <Void>("save") {
			public void onClick(AjaxRequestTarget target) {
				((MedWorksSession) getSession()).resetTimeoutClock();
				onSave(target, panel.getDefaultModel());
			}
		});

		// Add a cancel / close button.
		// form.
		// issue 39
		add(cancel = new AjaxLink <Void>("cancel") {
			public void onClick(AjaxRequestTarget target) {
				((MedWorksSession) getSession()).resetTimeoutClock();
				onCancel(target);
			}
		});

		// form.
		// issue 39
		add(close = new AjaxLink <Void>("close") {
			public void onClick(AjaxRequestTarget target) {
				((MedWorksSession) getSession()).resetTimeoutClock();
				onCancel(target);
			}
		});

		
		if (mode == MODE_CLOSE_BUTTON_ONLY) 
			{
			save.setVisible(false);
			cancel.setVisible(false);
			} 
		else if (mode == MODE_SAVE_AND_CANCEL_BUTTONS) 
			{
			close.setVisible(false);
			}
		else if (mode == MODE_SAVE_AND_CLOSE_BUTTONS)
			cancel.setVisible(false);
		
		MedWorksSession s;
		
		((MedWorksSession) getSession()).resetTimeoutClock();
	}

	protected abstract void onCancel(AjaxRequestTarget target);

	protected abstract void onSave(AjaxRequestTarget target, IModel model);
}
