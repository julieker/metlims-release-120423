////////////////////////////////////////////////////
// GrabProjectLabelPanel.java
// Written by Jan Wigginton, Oct 25, 2016
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

import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.util.widgets.AjaxProjectField;


public abstract class GrabProjectLabelPanel extends Panel
	{
	@SpringBean 
	private ProjectService projectService;
	
	private String projectLabel, input, searchType = "Id";
	private boolean searchValid = false;
	private FeedbackPanel feedback;
	
	
	public GrabProjectLabelPanel(String id)
		{
		super(id);
		
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		add(new GrabProjectLabelForm("grabProjectLabelForm"));
		}
	
	
	public final class GrabProjectLabelForm extends Form 
		{
		public GrabProjectLabelForm(final String id)
			{
			super(id);
			
			AjaxProjectField projNameField;
			add(projNameField = new AjaxProjectField("projNameField")
				{
				@Override
				public boolean isVisible()  
					{ 
					return "Name".equals(getSearchType()); }
					});
			
			projNameField.setOutputMarkupId(true);
			projNameField.add(buildFormSubmitBehavior(false, projNameField));
			
			DropDownChoice<String> projIdDrop;
			add(projIdDrop = (DropDownChoice<String>) buildDropdown("projIdDrop").setOutputMarkupId(true));
			projIdDrop.add(buildFormSubmitBehavior(true, projNameField));
			}
		
	
		private DropDownChoice buildDropdown(String id)
			{
			LoadableDetachableModel <List<String>> projectListModel = new LoadableDetachableModel<List<String>>() 
				{
				@Override
				protected List<String> load() { return projectService.projectIdsByStartDate(); } 
				};
			
			
			return new DropDownChoice<String>(id, new PropertyModel<String>(this, "projectLabel"), projectListModel)
				{
				@Override
				public boolean isVisible()  { return "Id".equals(getSearchType()); }
				};
			}
		
		
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop, final AjaxProjectField nameField)
			{
			return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
				//	input = isDrop ? projectLabel : ((AutoCompleteTextField)(nameField)).getInput();
				//	GrabProjectLabelPanel.this.onSelect(input, target);
					
					System.out.println("Running the inner submit behavior");
					input = isDrop ? projectLabel : ((AutoCompleteTextField)(nameField)).getInput();
					
					try
						{
						String expId = projectService.getProjectIdForSearchString(input, "with name");
						searchValid = true;
						target.add(feedback);
						GrabProjectLabelPanel.this.onSelect(input, target);
						}	
					catch (Exception e) 
						{ 
						searchValid = false;
						System.out.println("Invalid search string was " + input);
						GrabProjectLabelPanel.this.error(e.getMessage());
						target.add(feedback);
						}
					}
					
				};
			}
			
		
		public String getSearchType() { return searchType; }
		public void setSearchType(String st) { searchType = st; }
		
		public String getProjectLabel() { return projectLabel; }
		public void setProjectLabel(String label) {  projectLabel = label ; }
		}
		
	

	
	
	public String getProjectLabel()
		{
		return projectLabel;
		}

	public void setProjectLabel(String projectLabel)
		{
		this.projectLabel = projectLabel;
		}

	public String getSearchType()
		{
		return searchType;
		}
		
	public void setSearchType(String searchType)
		{
		this.searchType = searchType;
		}
	
	
	protected abstract void onSelect(String projectLabel, AjaxRequestTarget target);
	}
	
	
