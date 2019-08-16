package edu.umich.brcf.metabolomics.panels.lims.project;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.dto.ProjectDTO;
import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.behavior.FocusOnLoadBehavior;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.AjaxUpdatingRequiredTextField;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;



public abstract class EditProject2 extends WebPage 
	{
	@SpringBean
	ProjectService projectService;

	@SpringBean
	ClientService clientService;

	@SpringBean
	UserService userService;
	
	UserDAO userDao;
	
	String pageTitle;
	
	//closeButton
	
	
	public EditProject2(Page backPage, ProjectDTO dto, ModalWindow modal, boolean ifNew) 
		{
		pageTitle = ifNew ? "Add Project" : "Edit Project";
		add(new Label("pageTitle", new PropertyModel<String>(this, "pageTitle")));
		this.setOutputMarkupId(true);
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new EditProject2Form("editProjectForm", dto.getId(), dto, modal));
		}
	
	public EditProject2(Page backPage, IModel<Project> projectModel, ModalWindow modal) 
		{
		pageTitle = projectModel == null ? "Add Project" : "Edit Project";
		add(new Label("pageTitle", new PropertyModel<String>(this, "pageTitle")));
		Project project = (Project) projectModel.getObject();		
		this.setOutputMarkupId(true);
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new EditProject2Form("editProjectForm", project.getProjectID(), ProjectDTO.instance(project), modal));
		}

	public EditProject2(Page backPage, ModalWindow modal) 
		{
		pageTitle = "Add Project";
		add(new Label("pageTitle", new PropertyModel<String>(this, "pageTitle")));
		this.setOutputMarkupId(true);
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new EditProject2Form("editProjectForm", "to be assigned", new ProjectDTO(), modal));
		}

	public final class EditProject2Form extends Form 
		{
		public EditProject2Form(final String id, final String projectId, ProjectDTO project, final ModalWindow modal) 
			{
			super(id, new CompoundPropertyModel(project));
			
			add(new Label("id", projectId));
			add(newRequiredTextField("projectName", FieldLengths.PROJECT_NAME_FIELD_LENGTH).add(new FocusOnLoadBehavior()));
			add(newRequiredTextField("description", FieldLengths.PROJECT_DESCRIPTION_FIELD_LENGTH));
			
			// JAK fix bug 166
			AutoCompleteTextField clientField = newAjaxField("clientID", 110, "Client");
			add(newHiddenLabel(this,"hiddenclient", clientField));
			add(clientField);
			
			AutoCompleteTextField contactField=newAjaxField("contactPerson", 60, "User");
			add(newHiddenLabel(this,"hiddencontact", contactField));
			add(contactField);
			
			final DropDownChoice statusDD = buildStatusDropDown("statusID", Project.STATUS_TYPES);
			statusDD.setRequired(true);
			add(statusDD);
	
			METWorksAjaxUpdatingDateTextField dateFld =  new METWorksAjaxUpdatingDateTextField("startDate", new PropertyModel<String>(project, "startDate"), "change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)  { }
				};
			
			dateFld.setRequired(true);
			dateFld.setDefaultStringFormat(Project.PROJECT_DATE_FORMAT);
			add(dateFld);
			
			TextArea notesField;
			add(notesField = new TextArea("notes", new PropertyModel(project, "notes")){public boolean isRequired() {
			       return ((statusDD.getConvertedInput()!=null)&&(statusDD.getInput().equals("I")));
		    	}});
			notesField.add(StringValidator.maximumLength(FieldLengths.PROJECT_NOTES_FIELD_LENGTH));

			add(new IndicatingAjaxButton("saveChanges", this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) 
					{
					boolean clientMissing = true, contactMissing = true;
					ProjectDTO projectDto = (ProjectDTO) getForm().getModelObject();
					
					try
						{
						if(!FormatVerifier.verifyFormat(Client.fullIdFormat, projectDto.getClientID()))
							projectDto.setClientID(StringParser.parseId(projectDto.getClientID()));
						
						if(!FormatVerifier.verifyFormat(User.fullIdFormat, projectDto.getContactPerson()))
							projectDto.setContactPerson(StringParser.parseId(projectDto.getContactPerson()));
						Project project = projectService.save(projectDto);
						
						EditProject2.this.onSave(project, target);
						EditProject2.this.info("Project "+project.getProjectID()+" saved successfully");
						
						target.add(EditProject2.this.get("feedback"));
						}
					catch (Exception e)
						{
						//e.printStackTrace();
						String msg = e.getMessage();
						
						
						if (msg.startsWith("Client") || msg.startsWith("Contact") || msg.indexOf("Duplicate") > -1 )
							EditProject2.this.error(msg);
						else
							EditProject2.this.error("Save unsuccessful. Please re-check entered values.");
						
						target.add(EditProject2.this.get("feedback"));
						} 
					}

				@Override
				protected void onError(AjaxRequestTarget target) // issue 464
					{
					EditProject2.this.error("Save unsuccessful. Please re-check values entered.");
					target.add(EditProject2.this.get("feedback"));
					target.add(EditProject2.this.get("feedback").getParent());
					}
				});
				
			add(new AjaxCancelLink("closeButton", modal)); 
			}
		
		
		
		private RequiredTextField newRequiredTextField(String id, int maxLength) 
			{
			AjaxUpdatingRequiredTextField textField = new AjaxUpdatingRequiredTextField(id, "onblur");
			textField.add(StringValidator.maximumLength(maxLength));
			return textField;
			}
		
		private AutoCompleteTextField newAjaxField(String id, int maxLength, final String type) 
			{
			final AutoCompleteTextField field = new AutoCompleteTextField(id) 
				{
				@Override
				protected Iterator getChoices(String input) 
					{
					if (Strings.isEmpty(input)) { return Collections.EMPTY_LIST.iterator(); }
					if (type.equals("User"))
						return getUserChoices(input);
						
					return getClientChoices(input);
					}
				};
			field.add(StringValidator.maximumLength(maxLength));
			return field;
			}
		
		private Label newHiddenLabel(EditProject2Form form, String id, AutoCompleteTextField field) 
			{
			final Label label = new Label(id, field.getModel());
			label.setVisible(false);
			label.setOutputMarkupId(true);
			field.add(new AjaxFormSubmitBehavior(form, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					target.add(label);
					}

				@Override
				protected void onError(AjaxRequestTarget target) {	}
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
		
		private Iterator getClientChoices(String input)
			{
			List<String> choices = new ArrayList();
			for (Client client : clientService.allClients()) 
				{
				if (client.getLab().toUpperCase().contains(input.toUpperCase()))
					choices.add(client.getLab()+" ("+client.getClientID()+")");
				}
			return choices.iterator();
			}
		

		private DropDownChoice buildStatusDropDown(String id, List list)
			{
			return new DropDownChoice(id, list, new ChoiceRenderer() // issue 464
				{
				public Object getDisplayValue(Object object)
			    	{
			        String stringrep;
			        String temp = (String) object;		
			        switch (temp.charAt(0))
			        	{
			            case 'A' :  stringrep = "Active";   break;
			            case 'I' : stringrep = "Inactive";  break;
			            default :
			                throw new IllegalStateException(temp.charAt(0) + " is not mapped!");
			        	}
			        return stringrep;
			    	}				
				public String getIdValue(Object object, int index)
			    	{
					return ((String) object).trim();
			    	}							
				});
			}
		}	
	
	protected abstract void onSave(Project project, AjaxRequestTarget target);
	}
	