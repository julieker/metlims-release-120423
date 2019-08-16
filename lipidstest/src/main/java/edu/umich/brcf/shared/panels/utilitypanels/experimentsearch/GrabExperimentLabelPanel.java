////////////////////////////////////////////////////
// GrabExperimentLabelPanel.java
// Written by Jan Wigginton, Oct 23, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels.experimentsearch;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.widgets.AjaxExperimentField;



public abstract class GrabExperimentLabelPanel extends Panel
	{
	@SpringBean 
	private ExperimentService experimentService;
	
	private String exp, input, searchType = "Id";
	private FeedbackPanel feedback;

	
	public GrabExperimentLabelPanel(String id)
		{
		super(id);
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new GrabExperimentLabelForm("grabExperimentLabelForm"));
		}
	
	
	public final class GrabExperimentLabelForm extends Form 
		{
		public GrabExperimentLabelForm(final String id)
			{
			super(id);
			
			AjaxExperimentField expNameField;
			add(expNameField = new AjaxExperimentField("exp")
				{
				@Override
				public boolean isVisible()  { return "Name".equals(getSearchType()); }
				});
			expNameField.setOutputMarkupId(true);
			expNameField.add(buildFormSubmitBehavior(false, expNameField));
			
			DropDownChoice<String> expIdDrop;
			add(expIdDrop = (DropDownChoice<String>) buildDropdown("expDrop").setOutputMarkupId(true));
			expIdDrop.add(buildFormSubmitBehavior(true, expNameField));
			}
			
		
		private DropDownChoice buildDropdown(String id)
			{
			LoadableDetachableModel <List<String>> expListModel = new LoadableDetachableModel<List<String>>() 
				{
				@Override
				protected List<String> load() { return experimentService.expIdsByInceptionDate();  }
				};
			
			return new DropDownChoice<String>(id, new PropertyModel<String>(this, "exp"), expListModel)
				{
				@Override
				public boolean isVisible()  { return "Id".equals(getSearchType()); }
				};
			}
		
		
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop, final AjaxExperimentField nameField)
			{
			return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = isDrop ? exp : ((AutoCompleteTextField)(nameField)).getInput();
					
					try
						{
						String expId = experimentService.getExperimentIdForSearchString(input, "with name");
					//	GrabExperimentLabelPanel.this.error("Search string '" + input + "' was valid. Push Select to search");
					//	System.out.println("Valid search string was " + input);
						target.add(feedback);
						GrabExperimentLabelPanel.this.onSelect(input, target);
						}	
					catch (Exception e) 
						{ 
						System.out.println("Invalid search string was " + input);
						GrabExperimentLabelPanel.this.error(e.getMessage());
						target.add(feedback);
						}
					}
				};
			}
	
	
		public String getExp() { return exp; }
		public void setExp(String e) { exp = e; }
		}
		
	
	public String getSearchType()
		{
		return searchType;
		}
	
	public void setSearchType(String searchType)
		{
		this.searchType = searchType;
		}
	

	protected abstract void onSelect(String exp, AjaxRequestTarget target);
	}
	

