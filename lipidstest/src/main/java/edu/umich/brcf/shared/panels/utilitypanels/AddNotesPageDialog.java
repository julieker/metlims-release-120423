package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
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

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistItemSimple;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class AddNotesPageDialog extends AbstractFormDialog
	{
	
	public TextArea notesTextArea ;
	int maxLength =  60;
	boolean isPropagated = false;
	public DialogButton submitButton = new DialogButton("submit", "Done");
	public DialogButton submitButton2 = new DialogButton("submit2", "ResetDefault");
	public Form<?> form;
	AjaxCheckBox propcBox;
	public AddNotesPageDialog(String id, String title, WorklistItemSimple wi)
		{
		super(id, title,  true);
		add(new NotesFormDialog("notesFormDialog",  new Model(""), "", wi));
		}
	
	
	public AddNotesPageDialog(  final PropertyModel<String> oldNotes, String pageTitle, String id, String title, WorklistItemSimple wi)
		{ 
		super(id, title,  true);
		
		add(form = new NotesFormDialog("notesFormDialog",  oldNotes, pageTitle, wi));
		}
	
// issue 229
	public final class NotesFormDialog extends Form 
		{
		public NotesFormDialog(final String id, IModel <String> oldNotes, String pageTitle, WorklistItemSimple wi)
			{
			
			super(id);
			add(new FeedbackPanel("feedback"));
			add (propcBox = buildPropCheckBox(wi));
			add(new Label("pageTitle", pageTitle));
			add(notesTextArea = new TextArea("notes", oldNotes));
			notesTextArea.add(StringValidator.maximumLength(maxLength));
			notesTextArea.add(buildStandardFormComponentUpdateBehavior("change", "updateComments"));
			/*add(new Button("save")
				{
				@Override
				public void onSubmit() 
					{
					String notes = ((TextArea)getForm().get("notes")).getInput();
					AddNotesPageDialog.this.onSave(notes);
					}
				}); */
			
			/* add(new AjaxCancelLink("cancelButton", modal1)
				{
				@Override
				public boolean isVisible()
					{
					return modal1 != null;
					}
				});	*/
			}
		}
	
	
	
	
	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public MarkupContainer setDefaultModel(IModel model) {
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public DialogButton getSubmitButton() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Form getForm() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void onError(AjaxRequestTarget target, DialogButton button) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton button) {
		// TODO Auto-generated method stub
		
	}
	
	protected AjaxCheckBox buildPropCheckBox(WorklistItemSimple wi)
	    {
		//System.out.println("OKay building prop box....");
	    AjaxCheckBox prop = new AjaxCheckBox("propagate", new PropertyModel(this, "isPropagated"))
		    {
	    	
	        @Override
	        public boolean isEnabled()
				{ 
	        	return wi.getRepresentsControl();
				}
		    @Override
		    public void onUpdate(AjaxRequestTarget target)
			    {
			    }
		    };
	     return prop;
	    }

	public Boolean getIsPropagated() 
	    {
		return isPropagated;
	    }

//issue 196
	public void setIsPropagated(Boolean isPropagated )
	    {
		this.isPropagated = isPropagated;
	    } 
	
	//////////////////////////////////////
	
	
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final String response)
	{
	
	return new AjaxFormComponentUpdatingBehavior(event)
		{
		
		// issue 212
			/* (non-Javadoc)
			 * @see org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior#onUpdate(org.apache.wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			protected void onUpdate(AjaxRequestTarget target)
				{					    
				switch (response)
					{
					case "updateComments" :
						break;
					}
				}
		};
	}
	
	
	
	
	
	////////////////////////////////////////
	
	
	
	
	
	
	protected abstract void onSave(String notes);
	}
