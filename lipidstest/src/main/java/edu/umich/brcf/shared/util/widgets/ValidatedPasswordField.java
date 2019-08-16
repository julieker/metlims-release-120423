////////////////////////////////////////////////////
// ValidatedPasswordField.java
// Written by Jan Wigginton, Nov 20, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.widgets;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.util.validator.PasswordPolicyValidator;

public class ValidatedPasswordField extends PasswordTextField
	{
	public ValidatedPasswordField(String id, IModel<String> model)
		{
		this(id, model, 8);
		}
	
	public ValidatedPasswordField(String id, IModel<String> model, int min_length)
		{
		this(id, model, min_length, 15);
		}
	
	public ValidatedPasswordField(String id, IModel<String> model, int min_length, int max_length)
		{
		super(id, model);
		setRequired(true);
		setResetPassword(false);
		add(StringValidator.maximumLength(max_length));
		add(StringValidator.minimumLength(min_length));
		add(new PasswordPolicyValidator());
		}
	}
