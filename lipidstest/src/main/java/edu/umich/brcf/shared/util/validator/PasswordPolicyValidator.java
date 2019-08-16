////////////////////////////////////////////////////
// PasswordPolicyValidator.java
// Written by Jan Wigginton, Nov 20, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.validator;

import java.util.regex.Pattern;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public class PasswordPolicyValidator implements IValidator<String>
	{
	public static final Pattern UPPER = Pattern.compile("[A-Z]");
	public static final Pattern LOWER = Pattern.compile("[a-z]");
	public static final Pattern NUMBER = Pattern.compile("[0-9]");

	@Override
	public void validate(IValidatable<String> validatable)
		{
		final String password =validatable.getValue();
		
		if (!NUMBER.matcher(password).find())
			error(validatable,  "no-digit");
		
		if (!LOWER.matcher(password).find())
			error(validatable,  "no-lower");
		
		if (!UPPER.matcher(password).find())
			error(validatable,  "no-upper");
		}
	
	private void error(IValidatable<String> validatable, String errorKey)
		{
		ValidationError error = new ValidationError();
		error.addKey(getClass().getSimpleName() + "." + errorKey); // issue 464
		validatable.error(error);
		}

	}
