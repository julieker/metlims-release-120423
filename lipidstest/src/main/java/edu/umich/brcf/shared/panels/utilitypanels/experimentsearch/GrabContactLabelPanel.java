////////////////////////////////////////////////////
// GrabContactLabelPanel.java
// Written by Jan Wigginton, Oct 26, 2016
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
import edu.umich.brcf.shared.util.widgets.AjaxContactField;


public abstract class GrabContactLabelPanel extends Panel
	{
	@SpringBean 
	private ClientService clientService;
	
	private String contact, input; 
	private String searchType = "Name";
	private FeedbackPanel feedback;
	
	
	public GrabContactLabelPanel(String id)
		{
		super(id);
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new GrabContactLabelForm("grabContactLabelForm"));
		}
	
	
	public final class GrabContactLabelForm extends Form 
		{
		public GrabContactLabelForm(final String id)
			{
			super(id);
			
		//	AjaxClientField userNameField;
			AjaxContactField userNameField;
			
			add(userNameField = new AjaxContactField("contact"));
			userNameField.setOutputMarkupId(true);
			userNameField.add(buildFormSubmitBehavior(false, userNameField));
			}
	
		
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop, final AjaxContactField nameField)
			{
			return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = isDrop ? contact : ((AutoCompleteTextField)(nameField)).getInput();
					
					Boolean contactExists = false;
					try
						{
						contactExists = clientService.verifyContactExists(input);
						target.add(feedback);
						GrabContactLabelPanel.this.onSelect(input, target);
						}	
					catch (Exception e) 
						{ 
						GrabContactLabelPanel.this.error(e.getMessage());
						target.add(feedback);
						}
					
					if (!contactExists)
						{
						GrabContactLabelPanel.this.error("Unable to locate contact with name " + input + " in the database");
						target.add(feedback);
						}
					}
				};
			}
	
	
		public String getContact() { return contact; }
		public void setContact(String e) { contact = e; }
		}
	
	
	public String getSearchType()
		{
		return searchType;
		}
	
	public void setSearchType(String searchType)
		{
		this.searchType = searchType;
		}
	
	public String getContact() { return contact; }
	public void setContact(String e) { contact = e; }
	
	protected abstract void onSelect(String exp, AjaxRequestTarget target);
	}
	


/*
public abstract class GrabContactLabelPanel extends Panel
	{
	@SpringBean 
	private ClientService clientService;
	
	private String contact, input, searchType = "Id";
	
	private FeedbackPanel feedback;
	
	
	public GrabContactLabelPanel(String id)
		{
		super(id);
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new GrabContactLabelForm("grabContactLabelForm"));
		}
		
	
	public final class GrabContactLabelForm extends Form 
		{
		public GrabContactLabelForm(final String id)
			{
			super(id);
			
			AjaxClientField contactNameField;
			add(contactNameField = new AjaxClientField("contact")
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
			
			return new DropDownChoice<String>(id, new PropertyModel<String>(this, "contact"), contactListModel)
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
						GrabContactLabelPanel.this.onSelect(input, target);
						}	
					catch (Exception e) 
						{ 
						GrabContactLabelPanel.this.error(e.getMessage());
						target.add(feedback);
						}
					
					if (!contactExists)
						{
						GrabContactLabelPanel.this.error("Unable to locate contact with name " + input + " in the database");
						target.add(feedback);
						}
					}
				};
			}
	
	
		public String getSearchType() { return searchType; }
		public void setSearchType(String st) { searchType = st; }
		
		public String getContact() { return contact; }
		public void setContact(String e) { contact = e; }
		}
	
	
	public String getContact() { return contact; }
	public void setContact(String e) { contact = e; }
	
	
	public String getSearchType()  { return searchType; }
	public void setSearchType(String searchType) { this.searchType = searchType; }

	
	protected abstract void onSelect(String exp, AjaxRequestTarget target);
	} */
	
	
