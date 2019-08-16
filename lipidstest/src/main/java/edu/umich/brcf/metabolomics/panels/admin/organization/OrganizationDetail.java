package edu.umich.brcf.metabolomics.panels.admin.organization;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import edu.umich.brcf.shared.layers.domain.Organization;


public class OrganizationDetail extends WebPage
	{
	public OrganizationDetail(IModel<Organization> orgModel)
		{
		Organization org = (Organization) orgModel.getObject();
		add(new Label("orgId", org.getOrganizationId()));
		add(new Label("orgName", org.getOrgName()));
		add(new Label("orgAddress", org.getOrgAddress()));
		}
	}
