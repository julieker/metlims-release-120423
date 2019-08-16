package edu.umich.brcf.shared.util.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.request.Response;


public class FocusOnLoadBehavior extends Behavior 	
	{
	@Override
	public void bind(Component component) 
		{
		super.bind(component);
		component.setOutputMarkupId(true);
		}
	
	@Override
	public void afterRender(Component component) 
		{
		final Response response = component.getResponse();
		response.write("<script type=\"text/javascript\" language=\"javascript\">document.getElementById(\""
						+ component.getMarkupId() + "\").focus()</script>");
		}

	@Override
	public boolean isTemporary(Component component)
		{
		return true;
		}
	}
