////////////////////////////////////////////////////////////////////
// UserRegistrationPage.java
// Written by Jan Wigginton December 2015
////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.admin.accounts;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import edu.umich.brcf.shared.layers.domain.Viewpoint;
import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.AjaxUpdatingRequiredTextField;
import edu.umich.brcf.shared.util.widgets.AjaxUpdatingTextField;
import edu.umich.brcf.shared.util.widgets.ValidatedPasswordField;
import edu.umich.brcf.shared.util.utilpackages.PasswordAccountUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;


public abstract class UserRegistrationPage extends WebPage
	{
	@SpringBean
	UserService userService;
	
	private String titleLabel;
	private Page backPage;
	private Boolean ifNew = false;


	public UserRegistrationPage(Page backPage, UserDTO dto, ModalWindow modal) 
		{
		this(backPage, dto, dto.getId() == null,  modal );
		}


	public UserRegistrationPage(Page backPage, UserDTO dto, boolean ifNew, ModalWindow modal)
		{
		this.backPage = backPage;
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new UserRegistrationForm("editUserForm", "to be assigned", dto, modal));
		this.ifNew = ifNew;
		setTitleLabel(ifNew ? "Register User" : "Edit User");
		
		if(ifNew)
			{
			String randomPassword = PasswordAccountUtils.createUserPassword(12); //
			dto.setPassword1(randomPassword);
			dto.setPassword2(randomPassword);
			dto.setViewpoint(Viewpoint.instance(1L, "Client", 1L));
			}
		add(new Label("titleLabel", new PropertyModel<String>(this, "titleLabel")));
		}
		
	
	public class UserRegistrationForm extends Form 
		{
		private AjaxUpdatingRequiredTextField userNameFld;
		private IndicatingAjaxButton saveButton;
		
		public UserRegistrationForm(final String id, final String userId, UserDTO dto, final ModalWindow modal) 
			{
			super(id, new CompoundPropertyModel(dto));
			addFields(userId,dto, modal); 
			}
	
	
	private void addFields(String userId, final UserDTO user, ModalWindow modal) 
		{
		setDefaultModel(new CompoundPropertyModel<UserDTO>(user));
		
		add(new Label("id", userId));
		
		AjaxUpdatingRequiredTextField lastNameFld, firstNameFld;
		add(lastNameFld = new AjaxUpdatingRequiredTextField("lastName", "blur", 30));
		lastNameFld.setLabel(new Model<String>("Last Name"));
		lastNameFld.add(addUpdatingBehavior("name", user));
		
		add(firstNameFld = new AjaxUpdatingRequiredTextField("firstName", "blur", 20));
		firstNameFld.setLabel(new Model<String>("First Name"));
		firstNameFld.add(addUpdatingBehavior("name", user));
		
		
		AjaxUpdatingRequiredTextField phoneFld = new AjaxUpdatingRequiredTextField("phone", "blur", 26);
		phoneFld.setLabel(new Model<String>("Phone"));
		//phoneFld.add(new PhoneNumberValidator());
		
		AjaxUpdatingTextField faxFld = new AjaxUpdatingTextField("faxNumber","blur", 26);
		faxFld.setLabel(new Model<String>("Fax"));
		//faxFld.add(new PhoneNumberValidator());
		
		add(phoneFld);
		add(faxFld);
		
		AjaxUpdatingRequiredTextField emailFld = new AjaxUpdatingRequiredTextField("email", "blur", 50);
		emailFld.setLabel(new Model<String>("E-mail"));
		add(emailFld); 
		emailFld.add(EmailAddressValidator.getInstance());
		
		add(new AjaxUpdatingTextField("lab", "blur", 50).setLabel(new Model<String>("Lab")));
		
		
		add(userNameFld = new AjaxUpdatingRequiredTextField("userName", "blur", 15)
			{
			public boolean isEnabled() { return ifNew; }
			});
		
		userNameFld.setLabel(new Model<String>("User Name"));
	

		DropDownChoice viewpointChoice = new DropDownChoice("viewpoint", new PropertyModel<Viewpoint>(user, "viewpoint"),
			
		userService.allViewpoints(), new ChoiceRenderer("name", "id"))
			{
			@Override
			public boolean isEnabled()
				{ 
				String userId = (((MedWorksSession) getSession()).getCurrentUserId());
				return userService.isAccountAdmin(userId);
				}
			};
		
		viewpointChoice.setRequired(true);
		viewpointChoice.add(addUpdatingBehavior("", user));
		viewpointChoice.setLabel(new Model<String>("Role"));
		add(viewpointChoice);
		
		ValidatedPasswordField password1, password2;
		
		add(password1 = new ValidatedPasswordField("password1", new PropertyModel<String>(user, "password1"), 8, ifNew ? 15 : 61 )
			{
			@Override 
			public boolean isEnabled() { return ifNew; }
			});
	
		password1.setLabel(new Model<String>("Password 1"));
		
		add(password2 = new ValidatedPasswordField("password2", new PropertyModel<String>(user, "password2"), 8, ifNew ? 15 : 61)
			{
			@Override 
			public boolean isEnabled() { return ifNew; }
			});
			
		password1.setEnabled(ifNew);
		password2.setEnabled(ifNew);
		password1.setLabel(new Model<String>("Password 1"));
		password2.setLabel(new Model<String>("Password 2"));
		
		add(new AjaxCancelLink(modal));
		add(saveButton = buildSaveButton("saveChanges", user));
		}
	
	
	private IndicatingAjaxButton buildSaveButton(String id, final UserDTO dto)
		{
		return new IndicatingAjaxButton("saveChanges", this)
			{
			@Override 
			public boolean isEnabled()
				{
				return !StringUtils.isEmptyOrNull(dto.getUserName());
				}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target) 
				{
				UserDTO dto = (UserDTO) getForm().getModelObject();
				try	
					{
					UserDTO user;
					
					if (ifNew)
						user = userService.saveOrUpdateUser(dto);
					else
						user = userService.updateWithoutPassword(dto);
					
					UserRegistrationPage.this.info("User '"+ user.getUserName() +"' saved successfully.");
					target.add(UserRegistrationPage.this.get("feedback"));
					UserRegistrationPage.this.onSave(user, target);
					}
				catch (Exception e)
					{
					e.printStackTrace();
					if (e.getMessage().startsWith("Duplicate"))
						UserRegistrationPage.this.error("Please choose another user name");
					else if (e.getMessage().startsWith("Password"))
						UserRegistrationPage.this.error("User passwords do not match");
					else
						UserRegistrationPage.this.error("Save unsuccessful. Please make sure that phone # does not exceed 26 characters, userName is less than 15 characters, and lab is less than 50 characters");
					
					target.add(UserRegistrationPage.this.get("feedback"));
					}
				}
			
			@Override
			protected void onError(AjaxRequestTarget target) // issue 464
				{
				UserRegistrationPage.this.error("Save unsuccessful. Please re-check values entered.");
				target.add(UserRegistrationPage.this.get("feedback"));
				target.add(UserRegistrationPage.this.get("feedback").getParent());
				}
				};
			}
	
	
		private AjaxFormComponentUpdatingBehavior addUpdatingBehavior(final String type, final UserDTO dto) 
			{
			return new AjaxFormComponentUpdatingBehavior("blur") 
				{
				protected void onUpdate(AjaxRequestTarget target)  
					{
					if ("name".equals(type))
						{
						if (StringUtils.isEmptyOrNull(dto.getUserName()))
							{
							String userName = PasswordAccountUtils.createUserName(dto.getFirstName(), dto.getLastName());
							dto.setUserName(PasswordAccountUtils.createUserName(dto.getFirstName(), dto.getLastName()));
							target.add(userNameFld);
							target.add(saveButton);
							}
						}
						
					target.add(getFormComponent()); }
				};
			}
		}
	
	
	void setTitleLabel(String label)
		{
		this.titleLabel = label;
		}
	
	String getTitleLabel()
		{
		return titleLabel;
		}
	
	protected abstract void onSave(UserDTO dto, AjaxRequestTarget target);	
	}


/*

public abstract class UserRegistrationPage extends WebPage
	{
	@SpringBean
	UserService userService;

	String titleLabel;
	Page backPage;
	Boolean ifNew = false;
	
	
	public UserRegistrationPage(Page backPage, UserDTO dto, ModalWindow modal) 
		{
		this(backPage, dto, dto.getId() == null,  modal );
		}
	
	
	public UserRegistrationPage(Page backPage, UserDTO dto, boolean ifNew, ModalWindow modal)
		{
		this.backPage = backPage;
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new UserRegistrationForm("editUserForm", "to be assigned", dto, modal));
		this.ifNew = ifNew;
		setTitleLabel(ifNew ? "Register User" : "Edit User");
		add(new Label("titleLabel", new PropertyModel<String>(this, "titleLabel")));
		}
	

	public class UserRegistrationForm extends Form 
		{
		public UserRegistrationForm(final String id, final String userId, UserDTO dto, final ModalWindow modal) 
			{
			super(id, new CompoundPropertyModel(dto));
			addFields(userId,dto, modal); 
			}
		
		
		private void addFields(String userId, final UserDTO user, ModalWindow modal) 
			{
			setDefaultModel(new CompoundPropertyModel<UserDTO>(user));
			
			add(new Label("id", userId));
			
			add(new AjaxUpdatingRequiredTextField("lastName", "blur", 30));
			add(new AjaxUpdatingRequiredTextField("firstName", "blur", 20));
			add(new AjaxUpdatingRequiredTextField("phone", "blur", 26));
			add(new AjaxUpdatingTextField("faxNumber","blur", 26 ));
			add(new AjaxUpdatingRequiredTextField("email", "blur", 50));

			add(new AjaxUpdatingTextField("lab", "blur", 50));
			add(new AjaxUpdatingRequiredTextField("userName", "blur", 15)
				{
				public boolean isEnabled() { return ifNew; }
				}
			);
			
			DropDownChoice viewpointChoice = new DropDownChoice("viewpoint", new PropertyModel<Viewpoint>(user, "viewpoint"),
					userService.allViewpoints(), new ChoiceRenderer("name", "id"))
				{
				@Override
				public boolean isEnabled()
					{ 
					String userId = (((MedWorksSession) getSession()).getCurrentUserId());
					return userService.isAccountAdmin(userId);
					}
				};
			
			viewpointChoice.setRequired(true);
			viewpointChoice.add(addUpdatingBehavior());
			add(viewpointChoice);
			
			ValidatedPasswordField password1, password2;
			
			add(password1 = new ValidatedPasswordField("password1", new PropertyModel<String>(user, "password1"), 8, ifNew ? 15 : 61 )
				{
				@Override 
				public boolean isEnabled()
					{
					return ifNew;
					}
				}
			);
			
			add(password2 = new ValidatedPasswordField("password2", new PropertyModel<String>(user, "password2"), 8, ifNew ? 15 : 61)
				{
				@Override 
				public boolean isEnabled()
					{
					return ifNew;
					}
				});
			
			password1.setEnabled(ifNew);
			password2.setEnabled(ifNew);
			
			add(new AjaxCancelLink(modal));
			add(buildSaveButton("saveChanges"));
			}
		
		
		private IndicatingAjaxButton buildSaveButton(String id)
			{
			return new IndicatingAjaxButton("saveChanges", this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) 
					{
					UserDTO dto = (UserDTO) form.getModelObject();
					try	{
						UserDTO user;
						
						if (ifNew)
							user = userService.saveOrUpdateUser(dto);
						else
							user = userService.updateWithoutPassword(dto);
						
						UserRegistrationPage.this.info("User '"+ user.getUserName() +"' saved successfully.");
						target.add(UserRegistrationPage.this.get("feedback"));
						UserRegistrationPage.this.onSave(user, target);
						}
				
					catch (Exception e)
						{
						e.printStackTrace();
						if (e.getMessage().startsWith("Duplicate"))
							UserRegistrationPage.this.error("Please choose another user name");
						else if (e.getMessage().startsWith("Password"))
							UserRegistrationPage.this.error("User passwords do not match");
						else
						     UserRegistrationPage.this.error("Save unsuccessful. Please make sure that phone # does not exceed 26 characters, userName is less than 15 characters, and lab is less than 50 characters");
						
						target.add(UserRegistrationPage.this.get("feedback"));
						}
					}
				
				@Override
				protected void onError(AjaxRequestTarget target,Form form)
					{
					UserRegistrationPage.this.error("Save unsuccessful. Please re-check values entered.");
					target.add(UserRegistrationPage.this.get("feedback"));
					target.add(UserRegistrationPage.this.get("feedback").getParent());
					}
				};
			}
			
		
		private AjaxFormComponentUpdatingBehavior addUpdatingBehavior() 
			{
			return new AjaxFormComponentUpdatingBehavior("blur") 
				{
				protected void onUpdate(AjaxRequestTarget target)  { target.add(getFormComponent()); }
				};
			}
		}
	

	void setTitleLabel(String label)
		{
		this.titleLabel = label;
		}

	String getTitleLabel()
		{
		return titleLabel;
		}
	
	protected abstract void onSave(UserDTO dto, AjaxRequestTarget target);	
	} */
	