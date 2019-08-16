////////////////////////////////////////////////////
// ChangePasswordPage.java
// Written by Jan Wigginton, Oct 19, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.accounts;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.ValidatedPasswordField;



public abstract class ChangePasswordPage extends WebPage
	{
	@SpringBean
	UserService userService;
	
	private Boolean isAccountAdmin;
	
	
	public ChangePasswordPage(String id, UserDTO user, ModalWindow modal)
		{
		FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		
		String userId = (((MedWorksSession) getSession()).getCurrentUserId());
		isAccountAdmin = userService.isAccountAdmin(userId);
		
		add(new Label("pageTitle", isAccountAdmin ? "Reset Password" : "Change Password"));
		
		add(new ChangePasswordForm("changePasswordForm", user, modal));
		}
	
	
	public class ChangePasswordForm extends Form
		{
		String oldPassword; 
		
		public ChangePasswordForm(String id, UserDTO user, ModalWindow modal)
			{
			super(id);
			
			user.setPassword1("");
			user.setPassword2("");
			
			setDefaultModel(new CompoundPropertyModel<UserDTO>(user));
			add(new TextField<String>("userName").setEnabled(false));
			
			PasswordTextField oldPasswordFld;
			add(oldPasswordFld = new PasswordTextField("oldPassword", new PropertyModel<String>(this, "oldPassword")));
			oldPasswordFld.setEnabled(!isAccountAdmin);
			oldPasswordFld.setRequired(!isAccountAdmin);
		
			ValidatedPasswordField fld1, fld2;
			add(fld1 = new ValidatedPasswordField("password1", new PropertyModel<String>(user, "password1")));
			add(fld2 = new ValidatedPasswordField("password2", new PropertyModel<String>(user, "password2")));
			
			fld1.setLabel(new Model<String>("Password 1"));
			fld2.setLabel(new Model<String>("Password 2"));
			
			add(new AjaxCancelLink("cancelButton", modal));
			add(buildSaveButton("saveChanges"));
			}
		
		
		private IndicatingAjaxButton buildSaveButton(String id)
			{
			return new IndicatingAjaxButton("saveChanges", this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) 
					{
					UserDTO dto = (UserDTO) getForm().getModelObject();
					try	{
						String currentUserId = (((MedWorksSession) getSession()).getCurrentUserId());
						userService.updatePassword(oldPassword, dto, currentUserId); //saveOrUpdateUser(dto);
						ChangePasswordPage.this.error("User password updated successfully.");
						target.add(ChangePasswordPage.this.get("feedback"));
						}
				
					catch (Exception e)
						{
						e.printStackTrace();
						String msg = e.getMessage();
						
						if (!e.getMessage().startsWith("Password"))
							msg = "Unknown error while updating password";
							
						ChangePasswordPage.this.error(msg);
						
						target.add(ChangePasswordPage.this.get("feedback"));
						}
					}
				
				@Override
				protected void onError(AjaxRequestTarget target)// issue 464
					{
					ChangePasswordPage.this.error("Save unsuccessful. Please re-check values entered.");
					target.add(ChangePasswordPage.this.get("feedback"));
					target.add(ChangePasswordPage.this.get("feedback").getParent());
					}
				
				@Override
				protected void onComponentTag(ComponentTag tag)
					{
					super.onComponentTag(tag);
					String label = isAccountAdmin ? "Reset" : "Save";
					tag.put("value", label);
					}
				};
			}
	
	
		public String getOldPassword()
			{
			return oldPassword;
			}
	
		public void setOldPassword(String oldPassword)
			{
			this.oldPassword = oldPassword;
			}
		}
	
	protected abstract void onSave(UserDTO dto, AjaxRequestTarget target);	
	}
