////////////////////////////////////////////////////
// SearchForContactPanel.java
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

import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.util.widgets.AjaxClientField;



public abstract class NewSearchForContactPanel extends Panel
	{
	@SpringBean
	ClientService clientService;
	
	private String contact, input, searchType = "Name";
	private Boolean showButton = false;

	public NewSearchForContactPanel(String id)
		{
		super(id);
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new NewSearchForContactForm("searchByContactForm"));
		}

	
	public final class NewSearchForContactForm extends Form 
		{
		IndicatingAjaxButton selectButton;
		
		
		public NewSearchForContactForm(final String id)
			{
			super(id);
			AjaxClientField contactFld;
			add(contactFld = new AjaxClientField("contact")
				{
				@Override
				public boolean isVisible()  { return "Name".equals(getSearchType()); }
				});
			contactFld.add(buildFormSubmitBehavior(false));
			contactFld.setOutputMarkupId(true);
			
			add(selectButton = new IndicatingAjaxButton("select")
				{
				@Override
				public boolean isVisible() { return showButton; }
				
				@Override
				public void onSubmit(AjaxRequestTarget target, Form form) 
					{
					try  { NewSearchForContactPanel.this.onSelect(contact, target); }
					catch (Exception e) {  doError(target); } 
					}
				});
			
			DropDownChoice<String> contactDrop;
			add(contactDrop = (DropDownChoice<String>) buildDropdown("contactDrop").setOutputMarkupId(true));
			contactDrop.add(buildFormSubmitBehavior(true));
			}
		
	
		private DropDownChoice buildDropdown(String id)
			{
			LoadableDetachableModel <List<String>> contactListModel = new LoadableDetachableModel<List<String>>() 
				{
				@Override
				protected List<String> load() { return clientService.allContacts();  }
				};
				
			return new DropDownChoice<String>(id, new PropertyModel<String>(this, "contact"), contactListModel)
				{
				@Override
				public boolean isVisible()  { return "Id".equals(getSearchType()); }
				};
			}
		
		
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop)
		 	{
		 	return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					try
						{
						input = isDrop ? contact : ((AutoCompleteTextField)(this.getComponent())).getInput();
					
						String contactId= input;
					
						setContact(contactId);
						target.add(this.getComponent());
						if (selectButton != null)
							target.add(selectButton);
						onSelect(contactId, target);
						}
					catch (Exception e) { doError(target); }
					}
					
				@Override
				protected void onError(AjaxRequestTarget arg0) { doError(arg0); }
				};
			}
	
		
		public String getSearchType() { return searchType; }
		public void setSearchType(String st) { searchType = st; }
		
		public String getContact() { return contact; }
		public void setContact(String e) { contact = e; }
		}	
	

	private void doError(AjaxRequestTarget target)
		{
		String output = (input == null ? "" : input);
		NewSearchForContactPanel.this.error("Can't find contact ( " + output + "). Please verify that the search id (or name) is valid.");
		target.add(NewSearchForContactPanel.this.get("feedback"));
		}
	
	
	public void setContact(String input) 
		{
		this.contact=input;
		}
	
	public String getContact() 
		{
		return contact;
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

	protected abstract void onSelect(String contact, AjaxRequestTarget target);
	}
