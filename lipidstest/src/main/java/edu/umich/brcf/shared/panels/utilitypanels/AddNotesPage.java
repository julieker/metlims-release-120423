package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class AddNotesPage extends WebPage
	{
	TextArea notesTextArea ;
	int maxLength =  60;
	public AddNotesPage(Page backPage)
		{
		add(new NotesForm("notesForm", null, new Model(""), ""));
		}
	
	
	public AddNotesPage(Page backPage, ModalWindow modal1, final PropertyModel<String> oldNotes, String pageTitle)
		{
		add(new NotesForm("notesForm", modal1, oldNotes, pageTitle));
		}
	
// issue 229
	public final class NotesForm extends Form 
		{
		public NotesForm(final String id, final ModalWindow modal1, IModel <String> oldNotes, String pageTitle)
			{
			
			super(id);
			add(new FeedbackPanel("feedback"));
			
			add(new Label("pageTitle", pageTitle));
			add(notesTextArea = new TextArea("notes", oldNotes));
			notesTextArea.add(StringValidator.maximumLength(maxLength));
			
			add(new Button("save")
				{
				@Override
				public void onSubmit() 
					{
					String notes = ((TextArea)getForm().get("notes")).getInput();
					AddNotesPage.this.onSave(notes);
					}
				});
			
			add(new AjaxCancelLink("cancelButton", modal1)
				{
				@Override
				public boolean isVisible()
					{
					return modal1 != null;
					}
				});	
			}
		}
	
	protected abstract void onSave(String notes);
	}
