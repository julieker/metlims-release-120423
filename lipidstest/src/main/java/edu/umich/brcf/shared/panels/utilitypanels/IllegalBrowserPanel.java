////////////////////////////////////////////////////
// IllegalBrowserPanel.java
// Written by Jan Wigginton, Jun 23, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import edu.umich.brcf.shared.panels.login.MedWorksSession;


public class IllegalBrowserPanel extends Panel
	{
	public IllegalBrowserPanel(String id)
		{
		super(id);
		setOutputMarkupId(true);

		boolean isFireFox = (((MedWorksSession) getSession()).getClientProperties().isBrowserMozillaFirefox());
		boolean isInternetExplorer = (((MedWorksSession) getSession()).getClientProperties().isBrowserInternetExplorer());
		
		String msg = "";
		
		if (isFireFox)
			msg = "The Firefox browser isn't compatible with MetLIMS and has been disabled for this site -- please try again using Chrome or Safari";
		else if (isInternetExplorer)
			msg = "Internet Explorer isn't compatible with MetLIMS and has been disabled for this site -- please try again using Chrome or Safari";

		add(new Label("errMsg", msg));
		}
	}
