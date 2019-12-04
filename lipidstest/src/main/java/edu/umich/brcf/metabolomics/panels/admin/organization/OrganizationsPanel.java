////////////////////////////////////////////////////
// OrganizationsPanel.java
// Written by Jan Wigginton, Jun 23, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.organization;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.dto.OrganizationDTO;
import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;



public class OrganizationsPanel extends Panel{
	
	@SpringBean 
	private OrganizationService organizationService;
	
	public OrganizationsPanel(String id) 
		{
		super(id);
		
		final WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);
		
		final ModalWindow modal1=ModalCreator.createModalWindow("modal1", 1000, 200);
		add(modal1);
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
			{
			@Override
			public void onClose(AjaxRequestTarget target)  {   target.add(container); }
			});
		this.setOutputMarkupId(true);
		
		container.add(new ListView("organizations", new PropertyModel(this, "organizations"))
			{
			public void populateItem(final ListItem listItem)
				{
				final String org = (String)listItem.getModelObject();
				String org_id = StringParser.parseId(org);
				String org_name = StringParser.parseName(org);

				listItem.add(buildLinkToModal("orgLink", 
						org_id, modal1, (OrganizationsPanel) getParent().getParent()).add(new Label("orgLbl", new Model(org_id))));
				listItem.add(buildLinkToModal("orgLink2", 
						org_id, modal1, (OrganizationsPanel) getParent().getParent()).add(new Label("orgLbl2", new Model(org_name))));
				listItem.add(buildLinkToModal("clientLink", 
						org_id, modal1, (OrganizationsPanel) getParent().getParent()));
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			});
		 add(buildLinkToModal("createOrganization",null, modal1, this));
		 }

	
	private AjaxLink buildLinkToModal(final String linkID, String org, final ModalWindow modal1, final OrganizationsPanel panel) 
		{
		final Organization organization = org!=null ? organizationService.loadById(org) : null;
	
		return new AjaxLink <Void>(linkID)
        	{
            @Override
            public void onClick(AjaxRequestTarget target)
            	{
            	double widthPct, heightPct;
            	switch (linkID)
	            	{
	            	case "createOrganization" :  widthPct = 0.5; heightPct = 0.35; break;
	            	case "clientLink" : widthPct = 0.9; heightPct = 0.8; break;
	            	default : widthPct = 0.5; heightPct = 0.27; break;
	            	}
            	
            	setPageDimensions(modal1, widthPct, heightPct);
            	
            	modal1.setPageCreator(new ModalWindow.PageCreator()
            		{
                     public Page createPage()
                     	{
                    	return buildModalPage(linkID, modal1, panel, organization);
                     	}
            		});
            	 modal1.show(target);
            	}
        	};
		}
		
	
	public Page buildModalPage(String linkID, final ModalWindow modal1, final OrganizationsPanel panel, final Organization organization)
		{
	   	if(linkID.startsWith("cr"))
	   		return (new EditOrganization(getPage(), new OrganizationDTO(), true, modal1)
	    		{
	    		public void onSave(Organization org, AjaxRequestTarget target)
	    			{
	    			target.add(panel);
					if (modal1 != null) modal1.close(target); 
	    		 	}
	    		});
	    	
	   	if (linkID.startsWith("cl"))
	   		return new OrganizationClientsPage(getPage(), modal1, new Model(organization));
		
	   	return (new OrganizationDetail(new Model(organization)));
		}
	
	
	public List<String> getOrganizations()
		{
		return organizationService.allOrganizations();
		}
	
	
	private void setPageDimensions(final ModalWindow modal1, double widthPct, double heightPct)
		{
		int pageHeight = ((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
		modal1.setInitialHeight(((int) Math.round(pageHeight * heightPct)));
		int pageWidth = ((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();
		modal1.setInitialWidth(((int) Math.round(pageWidth * widthPct)));
		}
	}
