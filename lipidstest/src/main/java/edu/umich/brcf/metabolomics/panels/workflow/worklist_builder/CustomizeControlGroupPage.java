////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  CustomizeControlGroupPage.java
//  Written by Jan Wigginton
//  March 2019
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class CustomizeControlGroupPage extends WebPage
	{
	protected Integer nMasterBefore = 3, nMasterAfter = 1, nBatchBefore = 1, nBatchAfter = 1, nCE10Reps = 1, nCE20Reps = 1, nCE40Reps;
	
	
	public CustomizeControlGroupPage(final ModalWindow modal)
		{
		add(new FeedbackPanel("feedback"));
	add(new CustomizeControlGroupForm("customControlGroupsForm", modal));
	}

public final class CustomizeControlGroupForm extends Form 
	{
	protected List<Integer> countOptions = Arrays.asList(new Integer [] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15} );
	
	public CustomizeControlGroupForm(final String id, final ModalWindow modal1)
		{
		super(id);
		
		initializeValuesFromSession();
		
		add(buildCountDropdown("nMasterBefore", "nMasterBefore")); 
		add(buildCountDropdown("nMasterAfter", "nMasterAfter")); 
		add(buildCountDropdown("nBatchBefore", "nBatchBefore")); 
		add(buildCountDropdown("nBatchAfter", "nBatchAfter")); 
		add(buildCountDropdown("nCE10Reps", "nCE10Reps")); 
		add(buildCountDropdown("nCE20Reps", "nCE20Reps")); 
		add(buildCountDropdown("nCE40Reps", "nCE40Reps")); 
	    add(new AjaxCancelLink("cancelButton", modal1, "Done")
			{
			@Override
			public void onClick(AjaxRequestTarget target) 
				{
				CustomizeControlGroupPage.this.onSave(getNMasterBefore(), getNMasterAfter(), getNBatchBefore(), getNBatchAfter(), getNCE10Reps(),  getNCE20Reps(), getNCE40Reps());
				if (modal1 != null)
					modal1.close(target);
				}
			});
		}
	
	private void initializeValuesFromSession() 
		{
		nMasterBefore = ((MedWorksSession) Session.get()).getNMasterPoolsBefore();	
		nMasterAfter = ((MedWorksSession) Session.get()).getNMasterPoolsAfter();	
		nBatchBefore = ((MedWorksSession) Session.get()).getNBatchPoolsBefore();	
		nBatchAfter = ((MedWorksSession) Session.get()).getNBatchPoolsAfter();	
		nBatchAfter = ((MedWorksSession) Session.get()).getNBatchPoolsAfter();
		nCE10Reps = ((MedWorksSession) Session.get()).getNCE10Reps();
		nCE20Reps = ((MedWorksSession) Session.get()).getNCE20Reps();
		nCE40Reps = ((MedWorksSession) Session.get()).getNCE40Reps();
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

	public Integer getNBatchAfter() 
		{
		return nBatchAfter;
		}

	public void setNBatchAfter(Integer nBAfter) 
		{
		nBatchAfter = nBAfter;
		}
	}


protected abstract void onSave(Integer nMasterBefore, Integer nMasterAfter, Integer nBatchBefore, Integer nBatchAfter, Integer nCE10Reps, Integer nCE20Reps, Integer nCE40Reps);
}
