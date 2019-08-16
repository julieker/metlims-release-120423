///////////////////////////////////////
//OrganizationClientsPage.java
//Written by Jan Wigginton  June 2015
/////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.admin.organization;


import org.apache.wicket.markup.html.WebPage;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.interfaces.IWriteableTextData;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.TextDownloadLink;


public class OrganizationClientsPage extends WebPage 
	{
	@SpringBean
	ClientService clientService;
	
	
	public OrganizationClientsPage()
		{
		super();
		}
	
	
	public OrganizationClientsPage(Page backPage, final ModalWindow modal1, IModel<Organization> organization) 
		{	
		this();	
		OrganizationClientsForm rlf = new OrganizationClientsForm("organizationClientsForm", modal1, organization);
		add(rlf);
		}
	
	
	public final class OrganizationClientsForm extends Form 
		{
		static final int maxItemsPerList = 15;
		
		private List<Client> clientsList;
		private PageableListView infoListView;
		private String boxTitle = "", boxSubTitle = "";
		private int nonEmptyItems = 0;
		
		public OrganizationClientsForm(final String id, final ModalWindow modal1, IModel<Organization> organization) 
			{
			super(id);
			
			add(new FeedbackPanel("feedback"));
			this.setOutputMarkupId(true);
			setMultiPart(true);
			
			String orgId = organization.getObject().getOrganizationId();
			String fullName = "clients." + organization.getObject().getOrgName();
			clientsList = buildInfoList(orgId);
			
			setBoxTitle("Clients affiliated with : " + organization.getObject().getOrgName());
			setBoxSubTitle("(" + nonEmptyItems +  " Client" + (nonEmptyItems == 1 ? ")" : "s)"));
			add(new Label("boxTitle", new PropertyModel<String>(this, "boxTitle")));
			add(new Label("boxSubTitle", new PropertyModel<String>(this, "boxSubTitle")));
			
			add(infoListView = buildInfoListView("infoListView", modal1)); 
			add(new PagingNavigator("navigator", infoListView));
			
			IWriteableTextData writer = new OrganizationClientsWriter(fullName + ".tsv", new ArrayList(clientsList.subList(0, nonEmptyItems)));
			add(new TextDownloadLink("downloadData", writer));
			
			add(new AjaxCancelLink("cancelButton", modal1));
			}

		
		private void setPageDimensions(ModalWindow modal1)
			{
			int pageHeight = ((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
			modal1.setInitialHeight(((int) Math.round(pageHeight * 0.9)));
			int pageWidth = ((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();
			modal1.setInitialWidth(((int) Math.round(pageWidth * 0.9)));
			}
		
		
		private List<Client> buildInfoList(String orgId)
			{
			List <Client> infoList = clientService.allClientsForOrganization(orgId);
			
			nonEmptyItems = infoList.size();
			int rem = (nonEmptyItems % maxItemsPerList);
			String blankStr = "";
			if (rem > 0 || infoList.size() == 0)
			for (int i = rem; i < maxItemsPerList; i++)
			infoList.add(new Client(blankStr, blankStr, blankStr, blankStr, new User(), new User())); 
			
			return infoList;
			}
		
		
		private PageableListView buildInfoListView(String id, final ModalWindow modal)
			{
			return new PageableListView(id, new PropertyModel<List<Client>>(this, "clientsList"), maxItemsPerList)
				{	
				public void populateItem(ListItem listItem) 
					{
					Client client =   (Client) listItem.getModelObject();
					
					listItem.add(new Label("lab", new PropertyModel<String>(client, "lab") ));
					listItem.add(new Label("investigatorName", new PropertyModel<String>(client, "investigatorNameForTable") ));
					listItem.add(new Label("contactName", new PropertyModel<String>(client, "contactNameForTable") ));
					listItem.add(new Label("dept", new PropertyModel<String>(client, "dept") ));
					
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}
		
		
		public List<Client> getClientsList()
			{
			return this.clientsList;
			}
		
		@Override
		protected void onSubmit()
			{
			}
			
		public String getBoxTitle()
			{
			return boxTitle;
			}
		
		
		public void setBoxTitle(String boxTitle)
			{
			this.boxTitle = boxTitle;
			}
		
		
		public String getBoxSubTitle()
			{
			return boxSubTitle;
			}
		
		
		public void setBoxSubTitle(String boxSubTitle)
			{
			this.boxSubTitle = boxSubTitle;
			}
		}
	}
		
	
