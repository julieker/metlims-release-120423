////////////////////////////////////////////////////
//GrabOrganizationLabelPanel.java
//Written by Jan Wigginton, Oct 26, 2016
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

import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.util.widgets.AjaxOrganizationField;



public abstract class GrabOrganizationLabelPanel extends Panel
	{
	@SpringBean 
	private OrganizationService organizationService;
	
	private String org, input, searchType = "Id";
	
	FeedbackPanel feedback;
	Boolean searchValid = false;
	
	public GrabOrganizationLabelPanel(String id)
		{
		super(id);
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new GrabOrganizationLabelForm("grabOrganizationLabelForm"));
		}
	
	
	public final class GrabOrganizationLabelForm extends Form 
		{
		public GrabOrganizationLabelForm(final String id)
			{
			super(id);
			
			AjaxOrganizationField orgNameField;
			add(orgNameField = new AjaxOrganizationField("org")
				{
				@Override
				public boolean isVisible()  { return "Name".equals(getSearchType()); }
				});
			orgNameField.setOutputMarkupId(true);
			orgNameField.add(buildFormSubmitBehavior(false, orgNameField));
			
			DropDownChoice<String> orgIdDrop;
			add(orgIdDrop = (DropDownChoice<String>) buildDropdown("orgIdDrop").setOutputMarkupId(true));
			orgIdDrop.add(buildFormSubmitBehavior(true, orgNameField));
			}
		

		private DropDownChoice buildDropdown(String id)
			{
			LoadableDetachableModel <List<String>> expListModel = new LoadableDetachableModel<List<String>>() 
				{
				@Override
				protected List<String> load() 
					{
					return organizationService.allOrganizations(); 
					}
				};
			
			return new DropDownChoice<String>(id, new PropertyModel<String>(this, "org"), expListModel)
				{
				@Override
				public boolean isVisible()  { return "Id".equals(getSearchType()); }
				};
			}
			
		
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop, final AjaxOrganizationField nameField)
			{
			return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					System.out.println("Running the inner submit behavior");
					input = isDrop ? org : ((AutoCompleteTextField)(nameField)).getInput();
					
					try
						{
						String orgId = organizationService.getOrganizationIdForSearchString(input, "with name"); 

						target.add(feedback);
						GrabOrganizationLabelPanel.this.onSelect(input, target);
						}	
						catch (Exception e) 
						{ 
						searchValid = false;
						GrabOrganizationLabelPanel.this.error(e.getMessage());
						target.add(feedback);
						}
					}
				};
			}
	
		public void showFeedback(AjaxRequestTarget target)
			{
			target.add(feedback);
			}
		
		
		public String getSearchType() { return searchType; }
		public void setSearchType(String st) { searchType = st; }
		
		public String getOrg() { return org; }
		public void setOrg(String e) { org = e; }
		}


		
	public String getOrg() { return org; }
	public void setOrg(String e) { org = e; }
	
	
	public String getSearchType()  { return searchType; }
	public void setSearchType(String searchType) { this.searchType = searchType; }
	
	
	protected abstract void onSelect(String exp, AjaxRequestTarget target);
	}


