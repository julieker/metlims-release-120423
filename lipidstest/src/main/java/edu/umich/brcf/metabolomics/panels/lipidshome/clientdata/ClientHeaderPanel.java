package edu.umich.brcf.metabolomics.panels.lipidshome.clientdata;
/////////////////////////////////////////
//ClientHeaderPanel.java
//Written by Jan Wigginton July 2015
/////////////////////////////////////////

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.layers.service.ProjectService;


public class ClientHeaderPanel extends Panel {

	@SpringBean
	private ProjectService projectService;
	
	@SpringBean
	private ClientService clientService;
	
	@SpringBean 
	private OrganizationService organizationService;

	public ClientHeaderPanel(String id, Client client) 
		{
		super(id, new CompoundPropertyModel(client));
		String orgId = client.getOrganizationID();
		Organization org = organizationService.loadById(orgId);
		String orgName = org == null ? "" : org.getOrgName();
		
		add(new Label("organization", orgName));
		add(new Label("dept"));
		add(new Label("lab"));
		add(new Label("investigator.fullName"));
		add(new Label("contact.fullName"));
		}

	public void updateData(Client client) 
		{
		setDefaultModel(new CompoundPropertyModel(clientService.loadById(client.getClientID())));
		}
	}
