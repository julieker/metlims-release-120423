/////////////////////////////////////////
//LipidsMainTabPanel.java
//Written by Jan Wigginton July 2015
/////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lipidshome; 

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import org.wicketstuff.security.extensions.markup.html.tabs.SecureTabbedPanel;

import edu.umich.brcf.metabolomics.panels.lipidshome.browse.LipidsMainPanel;


@AuthorizeAction(action = Action.RENDER, roles = "Admin")
@AuthorizeInstantiation("Admin")
public class LipidsMainTabPanel extends Panel {

	public LipidsMainTabPanel(String id, final SecureTabbedPanel parent) 
		{
		super(id);

		setDefaultModel(new Model("tabpanel1"));
		List tabs = new ArrayList();
		tabs.add(new AbstractTab(new Model("LIPIDS DATA")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new LipidsMainPanel(panelId);
				}
			});
		
		
	add(new TabbedPanel("lipidTabs", tabs));	
	
	//SecureTabbedPanel secureTabbedPanel = (SecureTabbedPanel) new SecureTabbedPanel("tabs", tabs)
	//			.add(new AttributeModifier("class", true, LipidsMainTabPanel.this.getDefaultModel()));
	//	add(secureTabbedPanel);
		}
}