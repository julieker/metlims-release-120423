package edu.umich.brcf.metabolomics.panels.admin.accounts.obsolete;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.layers.domain.Viewpoint;
import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.widgets.AjaxUpdatingRequiredTextField;
import edu.umich.brcf.shared.util.widgets.AjaxUpdatingTextField;
import edu.umich.brcf.shared.panels.login.MedWorksSession;


public final class EditUser extends Panel
	{
	@SpringBean
	UserService userService;

	FeedbackPanel feedback;
	
	public EditUser(String id, UserDTO user)
		{
		super(id);
		add(feedback = new FeedbackPanel("feedback"));
		addFields(user.getId(), user);
		}

	
	public EditUser(String id)
		{
		super(id);
		add(new FeedbackPanel("feedback"));
		addFields("to be assigned", new UserDTO());
		}
	

	private void addFields(String userId, UserDTO user)
		{
		setDefaultModel(new CompoundPropertyModel<UserDTO>(user));
		add(new Label("id", userId).setEnabled(false));
		add(buildDisappearingLabel("roleLabel", "Role"));

		add(new AjaxUpdatingRequiredTextField("lastName", "onblur"));
		add(new AjaxUpdatingRequiredTextField("firstName", "onblur"));
		
		AjaxUpdatingRequiredTextField nameFld;
		
		add(nameFld = new AjaxUpdatingRequiredTextField("userName", "onblur", 15));
		nameFld.setEnabled("to be assigned".equals(userId));

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

		add(passwordField("password1", new PropertyModel<String>(user, "password1")));
		add(passwordField("password2", new PropertyModel<String>(user, "password2")));

		add(new AjaxUpdatingRequiredTextField("phone", "onblur", 26));
		add(new AjaxUpdatingRequiredTextField("email", "onblur", 50));

		add(new AjaxUpdatingTextField("lab", "onblur", 50));
		add(new AjaxUpdatingTextField("faxNumber", "onblur", 26));
		}

	
	private Label buildDisappearingLabel(String id, String text)
		{
		return new Label(id, text)
			{
			@Override
			public boolean isVisible()
				{
				String userId = (((MedWorksSession) getSession()).getCurrentUserId());
				return userService.isTrustedAdmin(userId);
				}
			};
		}

	private PasswordTextField passwordField(String name, IModel model)
		{
		PasswordTextField password = new PasswordTextField(name, model);
		password.setRequired(true);
		password.setResetPassword(false);
		password.add(StringValidator.maximumLength(15));
		password.add(addUpdatingBehavior());
		return password;
		}

	private AjaxFormComponentUpdatingBehavior addUpdatingBehavior()
		{
		return new AjaxFormComponentUpdatingBehavior("onblur")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
				{
				// target.add(getFormComponent());
				target.add(getFormComponent());
				target.add(feedback);
				}
			};
		}
	}
