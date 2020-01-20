package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.form.button.Button;
import com.googlecode.wicket.kendo.ui.form.dropdown.DropDownList;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

import edu.umich.brcf.shared.util.METWorksException;


public abstract class CustomizeControlGroupPageDialog extends AbstractFormDialog 
{
	protected Integer nMasterBefore = 0, nMasterAfter = 0, nBatchBefore = 0, nBatchAfter = 0, nCE10Reps = 0, nCE20Reps = 0, nCE40Reps = 0;
	private static final long serialVersionUID = 1L;
	
	final List<Integer> countOptions = Arrays.asList(new Integer [] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15} );
    ExperimentRandomization gEr = null;
	protected Form<?> form;
	final FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
    public DialogButton submitButton = new DialogButton("submit", "Done");
	public CustomizeControlGroupPageDialog(String id, String title)
		{
		super(id, title,  true);
	      //  gOriginalWorklist = originalWorklist;
			// Form //
		this.form = new Form<Integer>("form");
		this.add(this.form);
		form.setOutputMarkupId(true);
		form.add(buildCountDropdown("nMasterBefore", "nMasterBefore")); 
		form.add(buildCountDropdown("nMasterAfter", "nMasterAfter")); 
		form.add(buildCountDropdown("nBatchBefore", "nBatchBefore")); 
		form.add(buildCountDropdown("nBatchAfter", "nBatchAfter")); 
		form.add(buildCountDropdown("nCE10Reps", "nCE10Reps")); 
		form.add(buildCountDropdown("nCE20Reps", "nCE20Reps")); 
		form.add(buildCountDropdown("nCE40Reps", "nCE40Reps"));
		this.form.add(this.feedback);	
			// Buttons //
		submitButton = 
		    new DialogButton("submit", "submit") 
		        {
				private static final long serialVersionUID = 1L;
				//@Override
				public void onSubmit(AjaxRequestTarget target, DialogButton button)
				    {
				    }			
			    };					
			form.setMultiPart(true);	
		}
	
	protected void onOpen(IPartialPageRequestHandler handler)
		{
		// re-attach the feedback panel to clear previously displayed error message(s)
		handler.add(this.feedback);
		}
	
	private DropDownChoice<Integer> buildCountDropdown(String id, String property)
		{
		DropDownChoice<Integer> drp = new DropDownChoice<Integer>(id, new PropertyModel<Integer>(this, property), countOptions);
		drp.add(new AjaxFormComponentUpdatingBehavior("change")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)  {   }
			});
		return drp;
		}
	
	public Integer getNMasterBefore() 
		{
		return nMasterBefore;
		}

	public void setNMasterBefore(Integer nMasterB) 
		{
		nMasterBefore = nMasterB;
		}

	public Integer getNMasterAfter() 
		{
		return nMasterAfter;
		}

	public void setNMasterAfter(Integer nMAfter) 
		{
		nMasterAfter = nMAfter;
		}

	public Integer getNBatchBefore() 
		{
		return nBatchBefore;
		}
	
	public void setNBatchBefore(Integer nBBefore) 
		{
		nBatchBefore = nBBefore;
		}
	
	// issue 432
	public Integer getNCE10Reps() 
		{
		return nCE10Reps;
		}

	public void setNCE10Reps(Integer nnCE10Reps) 
		{
		nCE10Reps = nnCE10Reps;
		}

    // issue 432
	public Integer getNCE20Reps() 
		{
		return nCE20Reps;
		}

	public void setNCE20Reps(Integer nnCE20Reps) 
		{
		nCE20Reps = nnCE20Reps;
		}
	
	// issue 432
	public Integer getNCE40Reps() 
		{
		return nCE40Reps;
		}

	public void setNCE40Reps(Integer nnCE40Reps) 
		{
		nCE40Reps = nnCE40Reps;
		}

}