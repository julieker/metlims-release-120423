////////////////////////////////////////////////////
// SearchForOrganizationPanel.java
// Written by Jan Wigginton, Aug 6, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.experimentsearch.obsolete;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.widgets.AjaxOrganizationField;



public abstract class NewSearchForOrganizationPanel extends Panel
	{
	@SpringBean 
	OrganizationService organizationService;
	
	private String organization, input, searchType = "Id";
	private Boolean showButton = false;
	private IndicatingAjaxButton selectButton;
	
	public NewSearchForOrganizationPanel(String id)
		{
		super(id);
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new NewSearchForOrganizationForm("searchByOrganizationForm"));
		}


	public final class NewSearchForOrganizationForm extends Form 
		{
		public NewSearchForOrganizationForm(final String id)
			{
			super(id);
	
			AjaxOrganizationField orgField = new AjaxOrganizationField("org")
				{
				public boolean isVisible() { return "Name".equals(getSearchType()); }
				};
			
			orgField.add(buildFormSubmitBehavior(false));
			orgField.setOutputMarkupId(true);
			add(orgField);
			
			add(selectButton = new IndicatingAjaxButton("selectButton")
				{
				@Override
				public boolean isVisible() { return showButton; }
				
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form fom)
					{
					try
						{
						if(organizationService.isValidOrganizationSearch(organization))
							NewSearchForOrganizationPanel.this.onSelect(organization, target);
						else
							doError(target);
						}
					catch (Exception e) { doError(target); }
					}
				});
			
			
			DropDownChoice<String> orgIdDrop;
			add(orgIdDrop = (DropDownChoice<String>) buildDropdown("orgDrop").setOutputMarkupId(true));
			orgIdDrop.add(buildFormSubmitBehavior(true));
			}
		
		
		
		/* 
		 * private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop)
			{
			return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = isDrop ? project : ((AutoCompleteTextField)(this.getComponent())).getInput();
					
					System.out.println("Project search is " + input);
					if(projectService.isValidProjectSearch(input))
						{
						String projId = isDrop ? input : StringParser.parseId(input);
						
						setProject(projId);
						target.add(this.getComponent());
						if (selectButton != null)
							target.add(selectButton);
						onSelect(projId, target);
						}
					else
						System.out.println("Not a valid project search" + input);
				
					}
				
		 * */
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop)
		 	{
		 	return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = isDrop ? organization : ((AutoCompleteTextField)(this.getComponent())).getInput();
					
					System.out.println("Submit in the org field with input" + input);
					if (organizationService.isValidOrganizationSearch(input))
						{
						String orgId = StringParser.parseId(input);
						
						setOrganization(orgId);
					
						System.out.println("Organization selection is  " + organization);
						target.add(this.getComponent());
						if (selectButton != null)
							target.add(selectButton);
						onSelect(orgId, target);
						}
					else
						System.out.println("Not a valid organization search" + input);
					
					}
					
				@Override
				protected void onError(AjaxRequestTarget arg0) { doError(arg0); }
				};
		 	}
		
		
		private DropDownChoice<String> buildDropdown(String id)
			{
			LoadableDetachableModel <List<String>> orgListModel = new LoadableDetachableModel<List<String>>() 
				{
				@Override
				protected List<String> load() 
					{
					return organizationService.allOrganizations(); 
					}
				};
				
			return new DropDownChoice<String>(id, new PropertyModel<String>(this, "organization"), orgListModel)
				{
				@Override
				public boolean isVisible()  { return "Id".equals(getSearchType()); }
				};
			}
		
		
		public String getOrganization() { return organization; }
		public void setOrganization(String org) { organization = org; }
		}
	
	
	

	private void doError(AjaxRequestTarget target)
		{
		String output = (input == null ? "" : input);
		NewSearchForOrganizationPanel.this.error("Can't find organization (" + output + "). Please verify that the search id (or name) is valid.");
		target.add(NewSearchForOrganizationPanel.this.get("feedback"));
		}
	
	
	public void setOrganization(String input) 
		{
		this.organization=input;
		}
	
	
	public String getOrganization() 
		{
		return organization;
		}
	
	
	public Boolean getShowButton()
		{
		return showButton;
		}

	public void setShowButton(Boolean showButton)
		{
		this.showButton = showButton;
		}
	
	public String getSearchType()
		{
		return searchType;
		}


	public void setSearchType(String searchType)
		{
		this.searchType = searchType;
		}


	protected abstract void onSelect(String organization, AjaxRequestTarget target);
	}
