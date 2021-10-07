////////////////////////////////////////////////////
// GrabPILabelPanel.java
// Written by Julie Keros Sep 30 2021 
// for issue 181
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels.experimentsearch;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.util.widgets.AjaxPIField;


public abstract class GrabPILabelPanel extends Panel
	{
	@SpringBean 
	private ClientService clientService;
	
	private String pi, input; 
	private String searchType = "Name";
	private FeedbackPanel feedback;
	
	public GrabPILabelPanel(String id)
		{
		super(id);
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new GrabPILabelForm("GrabPILabelForm"));
		}
	
	public final class GrabPILabelForm extends Form 
		{
		public GrabPILabelForm(final String id)
			{
			super(id);
			
		//	AjaxClientField userNameField;
			AjaxPIField userNameField;
			
			add(userNameField = new AjaxPIField("PI"));
			userNameField.setOutputMarkupId(true);
			userNameField.add(buildFormSubmitBehavior(false, userNameField));
			}
		
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop, final AjaxPIField nameField)
			{
			return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = isDrop ? pi : ((AutoCompleteTextField)(nameField)).getInput();
					
					Boolean contactExists = false;
					try
						{
						// issue 181
						contactExists = clientService.verifyContactExists(input);
						target.add(feedback);
						GrabPILabelPanel.this.onSelect(input, target);
						}	
					catch (Exception e) 
						{ 
						GrabPILabelPanel.this.error(e.getMessage());
						target.add(feedback);
						}
					if (!contactExists)
						{
						GrabPILabelPanel.this.error("Unable to locate contact with name " + input + " in the database");
						target.add(feedback);
						}
					}
				};
			}
	
		public String getPi() { return pi; }
		public void setPi(String e) { pi = e; }
		}
		
	public String getSearchType()
		{
		return searchType;
		}
	
	public void setSearchType(String searchType)
		{
		this.searchType = searchType;
		}
	
	public String getPi() { return pi; }
	public void setPi(String e) { pi = e; }
	
	protected abstract void onSelect(String exp, AjaxRequestTarget target);
	}
	


/*
public abstract class GrabPILabelPanel extends Panel
	{
	@SpringBean 
	private ClientService clientService;
	
	private String contact, input, searchType = "Id";
	
	private FeedbackPanel feedback;
	
	
	public GrabPILabelPanel(String id)
		{
		super(id);
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new GrabPILabelForm("GrabPILabelForm"));
		}
		
	
	public final class GrabPILabelForm extends Form 
		{
		public GrabPILabelForm(final String id)
			{
			super(id);
			
			AjaxClientField contactNameField;
			add(contactNameField = new AjaxClientField("PI")
				{
				@Override
				public boolean isVisible()  { return "Name".equals(getSearchType()); }
				});
			contactNameField.setOutputMarkupId(true);
			contactNameField.add(buildFormSubmitBehavior(false, contactNameField));
			
			DropDownChoice<String> contactIdDrop;
			add(contactIdDrop = (DropDownChoice<String>) buildDropdown("contactIdDrop").setOutputMarkupId(true));
			contactIdDrop.add(buildFormSubmitBehavior(true, contactNameField));
			}
	
		
		private DropDownChoice buildDropdown(String id)
			{
			LoadableDetachableModel <List<String>> contactListModel = new LoadableDetachableModel<List<String>>() 
				{
				@Override
				protected List<String> load() { return clientService.allContactsWithIds(); }
				};
			
			return new DropDownChoice<String>(id, new PropertyModel<String>(this, "PI"), contactListModel)
				{
				@Override
				public boolean isVisible()  { return "Id".equals(getSearchType()); }
				};
			}
		
		
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop, final AjaxClientField nameField)
			{
			return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					System.out.println("Running the inner submit behavior");
					input = isDrop ? contact : ((AutoCompleteTextField)(nameField)).getInput();
					
					Boolean contactExists = false;
					try
						{
						contactExists = clientService.verifyContactExists(input);
						target.add(feedback);
						GrabPILabelPanel.this.onSelect(input, target);
						}	
					catch (Exception e) 
						{ 
						GrabPILabelPanel.this.error(e.getMessage());
						target.add(feedback);
						}
					
					if (!contactExists)
						{
						GrabPILabelPanel.this.error("Unable to locate contact with name " + input + " in the database");
						target.add(feedback);
						}
					}
				};
			}
	
	
		public String getSearchType() { return searchType; }
		public void setSearchType(String st) { searchType = st; }
		
		public String getPi() { return contact; }
		public void setPi(String e) { contact = e; }
		}
	
	
	public String getPi() { return contact; }
	public void setPi(String e) { contact = e; }
	
	
	public String getSearchType()  { return searchType; }
	public void setSearchType(String searchType) { this.searchType = searchType; }

	
	protected abstract void onSelect(String exp, AjaxRequestTarget target);
	} */
	
	
