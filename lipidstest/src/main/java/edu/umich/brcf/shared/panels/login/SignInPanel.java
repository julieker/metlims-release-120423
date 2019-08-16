package edu.umich.brcf.shared.panels.login;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;

import edu.umich.brcf.shared.util.behavior.FocusOnLoadBehavior;


public abstract class SignInPanel extends Panel
	{
	/** True if the panel should display a remember-me checkbox */
	private boolean includeRememberMe = true;

	/** Field for password. */
	private PasswordTextField password;

	/** True if the user should be remembered via form persistence (cookies) */
	private boolean rememberMe = true;

	/** Field for user name. */
	private TextField username;

	/**
	 * Sign in form.
	 */
	public final class SignInForm extends Form
		{
		/** El-cheapo model for form. */
		private final ValueMap properties = new ValueMap();

		public SignInForm(final String id)
			{
			super(id);

			add(username = new TextField("username", new PropertyModel(properties, "username")));
			username.add(new FocusOnLoadBehavior());
			
			add(password = new PasswordTextField("password", new PropertyModel(properties, "password")));

			// MarkupContainer row for remember me checkbox
			WebMarkupContainer rememberMeRow = new WebMarkupContainer("rememberMeRow");
			add(rememberMeRow);

			// Add rememberMe checkbox
			rememberMeRow.add(new CheckBox("rememberMe", new PropertyModel(SignInPanel.this, "rememberMe")));

			// Make form values persistent
			setPersistent(rememberMe);

			// Show remember me checkbox?
			rememberMeRow.setVisible(includeRememberMe);
			}

		public final void onSubmit()
			{
			if (signIn(getUsername(), getPassword()))
				{
				continueToOriginalDestination();
				setResponsePage(getApplication().getHomePage());
				} 
			else
				{
				final String errmsg = getLocalizer().getString("loginError",
						this, "Unable to sign you in");

				error(errmsg);
				}
			}
		}

	public SignInPanel(final String id)
		{
		this(id, true);
		}

	public SignInPanel(final String id, final boolean includeRememberMe)
		{
		super(id);

		this.includeRememberMe = includeRememberMe;

		// Create feedback panel and add to page
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);

		// Add sign-in form to page, passing feedback panel as
		// validation error handler
		add(new SignInForm("signInForm"));
		}

	public final void forgetMe()
		{
		// Remove persisted user data. Search for child component
		// of type SignInForm and remove its related persistence values.
		// getPage().removePersistedFormData(SignInPanel.SignInForm.class,
		// true);
		}

	public String getPassword()
		{
		return password.getDefaultModelObjectAsString();
		}

	public boolean getRememberMe()
		{
		return rememberMe;
		}

	public String getUsername()
		{
		return username.getDefaultModelObjectAsString();
		}

	public void setPersistent(boolean enable)
		{
		// username.setPersistent(enable);
		}

	public void setRememberMe(boolean rememberMe)
		{
		this.rememberMe = rememberMe;
		this.setPersistent(rememberMe);
		}

	public abstract boolean signIn(final String username, final String password);
	}
