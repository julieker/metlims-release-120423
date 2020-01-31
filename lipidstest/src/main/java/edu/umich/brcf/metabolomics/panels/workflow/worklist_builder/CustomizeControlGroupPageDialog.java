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
	protected Integer masterPoolsBefore = 0, masterPoolsAfter = 0, batchPoolsBefore = 0, batchPoolsAfter = 0, nCE10Reps = 0, nCE20Reps = 0, nCE40Reps = 0;
	private static final long serialVersionUID = 1L;
	protected int masterPoolsBeforePrev =  0, masterPoolsAfterPrev =  0, batchPoolsBeforePrev =  0, batchPoolsAfterPrev =  0, nCE10RepsPrev =  0, nCE20RepsPrev =  0, nCE40RepsPrev =  0;
	final List<Integer> countOptions = Arrays.asList(new Integer [] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15} );
    ExperimentRandomization gEr = null;
	protected Form<?> form;
	final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback");
    public DialogButton submitButton = new DialogButton("submit", "Done");
    WorklistSimple originalWorklist;
	public CustomizeControlGroupPageDialog(String id, String title, WorklistSimple workList)
		{
		super(id, title,  true);
	      //  gOriginalWorklist = originalWorklist;
			// Form //
		originalWorklist = workList;
		this.form = new Form<Integer>("form");
		this.add(this.form);
		form.setOutputMarkupId(true);
		form.add(buildCountDropdown("masterPoolsBefore", "masterPoolsBefore")); 
		form.add(buildCountDropdown("masterPoolsAfter", "masterPoolsAfter")); 
		form.add(buildCountDropdown("batchPoolsBefore", "batchPoolsBefore")); 
		form.add(buildCountDropdown("batchPoolsAfter", "batchPoolsAfter")); 
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
		DropDownChoice<Integer> drp = new DropDownChoice<Integer>(id, new PropertyModel<Integer>(originalWorklist, property), countOptions);
		drp.add(new AjaxFormComponentUpdatingBehavior("change")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)  {   }
			});
		return drp;
		}
	
	public Integer getMasterPoolsBefore() 
		{
		return masterPoolsBefore;
		}

	public void setMasterPoolsBefore(Integer nMasterB) 
		{
		masterPoolsBefore = nMasterB;
		}

	public Integer getMasterPoolsAfter() 
		{
		return masterPoolsAfter;
		}

	public void setMasterPoolsAfter(Integer nMAfter) 
		{
		masterPoolsAfter = nMAfter;
		}

	public Integer getBatchPoolsBefore() 
		{
		return batchPoolsBefore;
		}
	
	public void setBatchPoolsBefore(Integer nBBefore) 
		{
		batchPoolsBefore = nBBefore;
		}
	
	public Integer getBatchPoolsAfter() 
		{
		return batchPoolsAfter;
		}

	public void setBatchPoolsAfter(Integer nAfter) 
		{
		batchPoolsBefore = nAfter;
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
	
	// issue 6
    public void clearPrevValues ()
	    {
    	masterPoolsBeforePrev =  0; 
    	masterPoolsAfterPrev =  0; 
    	batchPoolsBeforePrev =  0; 
    	batchPoolsAfterPrev =  0; 
    	nCE10RepsPrev =  0; 
    	nCE20RepsPrev =  0; 
    	nCE40RepsPrev =  0;
	    }	
    }
    
