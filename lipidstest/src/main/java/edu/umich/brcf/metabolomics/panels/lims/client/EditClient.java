package edu.umich.brcf.metabolomics.panels.lims.client;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
//import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxSubmitButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.dto.ClientDTO;
import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.behavior.FocusOnLoadBehavior;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;



public abstract class EditClient extends WebPage 
	{
	@SpringBean
	ClientService clientService;

	@SpringBean
	OrganizationService organizationService;

	@SpringBean
	UserService userService;
	IndicatingAjaxLink  saveLink;
	
	Boolean openForEdits;
	
	
	public EditClient(Page backPage, IModel clientModel, ModalWindow modal)
		{
		this(backPage, clientModel, modal, true);
		}
		

	public EditClient(Page backPage, IModel clientModel, ModalWindow modal, boolean openForEdits) 
		{
		this.openForEdits = openForEdits;
		
		String title = (clientModel == null ? "Add Client" : "Edit Client");
		add(new Label("pageTitle", title));
		
		Client client = (Client) (clientModel == null ? null : clientModel.getObject());
		String label = (client == null ? "To be assigned" : client.getClientID());
		ClientDTO dto = (client == null ?  new ClientDTO() : ClientDTO.instance(client));
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new EditClientForm("editClientForm", label, dto, modal));
		}
	
	public EditClient(Page backPage, IModel clientModel, ModalWindow modal, boolean openForEdits, ClientDTO dtoForInitialization) 
		{
		this.openForEdits = openForEdits;
		
		String title = (clientModel == null ? "Add Client" : "Edit Client");
		add(new Label("pageTitle", title));
		
		Client client = (Client) (clientModel == null ? null : clientModel.getObject());
		String label = (client == null ? "To be assigned" : client.getClientID());
		ClientDTO dto = (client == null ?  dtoForInitialization : ClientDTO.instance(client));
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new EditClientForm("editClientForm", label, dto, modal));
		}


	public final class EditClientForm extends Form 
		{
		public EditClientForm(final String id, final String clientId, ClientDTO client, ModalWindow modal) 
			{	
			super(id, new CompoundPropertyModel(client));

			add(new Label("id", clientId));
			add(newRequiredTextField("dept", 50, "Department").add(new FocusOnLoadBehavior()));
			add(newRequiredTextField("lab", 100, "Lab").setEnabled(openForEdits));
			
			// JAK fix bug 165
			AutoCompleteTextField organizationField=newAjaxField("organizationID", 130, "Organization");
			add(newHiddenLabel(this,"hiddenorganization", organizationField));
			add(organizationField);
		//	organizationField.setRequired(true);
			
			
			AutoCompleteTextField investigatorField=newAjaxField("investigatorID", 60, "User");
			add(newHiddenLabel(this,"hiddeninvestigator", investigatorField));
			add(investigatorField);
		///	investigatorField.setRequired(true);
			
			AutoCompleteTextField contactField=newAjaxField("contact", 60, "User");
			add(newHiddenLabel(this,"hiddencontact", contactField));
			add(contactField);
		//	contactField.setRequired(true);
			
			this.setOutputMarkupId(true);
		
			add(buildSaveLink("saveChanges", this));
			add(new AjaxCancelLink("cancelButton", modal)); 
   	        }
		}

	    String project = "cat"; 
	    
		public String getProject()
			{
			return project;
			}
		
		public void setProject(String p)
			{
			project = p; 
			}
		
		// Issue 464
		private IndicatingAjaxButton buildSaveLink(String id, final EditClientForm form)
			{
			return new IndicatingAjaxButton(id, form)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) 
					{
					ClientDTO clientDto = (ClientDTO) form.getModelObject();
					
					try
						{
						if(!FormatVerifier.verifyFormat(Organization.idFormat, clientDto.getOrganizationID()))
							clientDto.setOrganizationID(StringParser.parseId(clientDto.getOrganizationID()));
						
						if(!FormatVerifier.verifyFormat(User.fullIdFormat, clientDto.getContact()))
							clientDto.setContact(StringParser.parseId(clientDto.getContact()));
						
						if(!FormatVerifier.verifyFormat(User.fullIdFormat, clientDto.getInvestigatorID()))
							clientDto.setInvestigatorID(StringParser.parseId(clientDto.getInvestigatorID()));
						
						Client client = clientService.save(clientDto);
						EditClient.this.info("Client " + client.getClientID()+ " saved successfully.");
						target.add(EditClient.this.get("feedback"));
						EditClient.this.onSave(client, target);
						}
					catch (Exception e)
						{
						String msg = e.getMessage();
						if (msg.startsWith("Investigator") || msg.startsWith("Contact") || msg.startsWith("Organization") || msg.startsWith("Duplicate"))
							EditClient.this.error(msg);	
						else 
							EditClient.this.error("Save unsuccessful. Please make sure that Department does not exceed 50 characters and lab does not exceed 100 characters.");
						
						
						e.printStackTrace();
						target.add(EditClient.this.get("feedback"));
						}
					}
				
				@Override
				// ISsue 464
				protected void onError(AjaxRequestTarget target) 
					{
					target.add(EditClient.this.get("feedback"));
					}
				};
			}
		
	
		

		private RequiredTextField<String> newRequiredTextField(String id, int maxLength, String label) 
			{
			RequiredTextField textField = new RequiredTextField(id)
				{
				@Override
				public boolean isEnabled()
					{
					return openForEdits;
					}
				};
				
			textField.setLabel(new Model<String>(label));
			textField.add(StringValidator.maximumLength(maxLength));
			return textField;
			}
		
		
		private AutoCompleteTextField<String> newAjaxField(String id, int maxLength, final String type) 
			{
			final AutoCompleteTextField<String> field = new AutoCompleteTextField<String>(id) 
				{
				@Override
				protected Iterator getChoices(String input) 
					{
					if (Strings.isEmpty(input)) 
						return Collections.EMPTY_LIST.iterator();
						
					if (type.equals("User"))
						return getUserChoices(input);
					
					return getOrganizationChoices(input);
					}
				
				@Override
				public boolean isEnabled()
					{
					return openForEdits;
					}
				};
				
			field.add(StringValidator.maximumLength(maxLength));
			
			return field;
			}
		
		
		private Label newHiddenLabel(EditClientForm form, String id, AutoCompleteTextField field) 
			{
			final Label label = new Label(id, field.getModel());
			label.setVisible(false);
			label.setOutputMarkupId(true);
			field.add(new AjaxFormSubmitBehavior(form, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) { target.add(label);  }

				@Override
				protected void onError(AjaxRequestTarget target) {  }
				});
			
			return label;
			}
		
		
		private Iterator getUserChoices(String input)
			{
			List<String> choices = new ArrayList();
			
			for (UserDTO user : userService.allUsers()) 
				{
				final String userName = user.getFirstName()+" " +user.getLastName();
				final String userId = " (" + user.getId() + ")";
				if (userName.toUpperCase().contains(input.toUpperCase()))
					choices.add(userName + userId);
				}
			
			return choices.iterator();
			}
		
		
		private Iterator getOrganizationChoices(String input)
			{
			List<String> choices = new ArrayList();
			for (String org : organizationService.allOrganizations()) 
				{
				if (org.toUpperCase().contains(input.toUpperCase()))
					choices.add(org);
				}
		
			return choices.iterator();
			}
		
	
	protected abstract void onSave(Client client, AjaxRequestTarget target);
	}


