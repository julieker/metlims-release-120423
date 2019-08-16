// Revisited : October 2016 (JW)

package edu.umich.brcf.metabolomics.panels.lims.project.obsolete;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.panels.utilitypanels.EditDocumentPage;
import edu.umich.brcf.shared.util.ModalSizes;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;


public class ClientsPanel  extends Panel 
	{
	@SpringBean 
	private ClientService clientService;

	PageableListView listView;
	WebMarkupContainer container;
	
	public ClientsPanel(String id) 
		{
		super(id);
		}}

/*
		container = new WebMarkupContainer("container");
		add(container);
		container.setOutputMarkupId(true);
		
		final ModalWindow modal1 = new ModalWindow("modal1"); 
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
			{
			@Override
			public void onClose(AjaxRequestTarget target)  {  target.add(container);  }
			});
		add(modal1);
		
		
		container.add(listView = new PageableListView("clients", new PropertyModel(this, "clients"), 28)
			{
			public void populateItem(final ListItem listItem)
				{
				final String clientStr = (String)listItem.getModelObject();
				String clientId = StringParser.parseId(clientStr);
				String clientName = StringParser.parseName(clientStr);
				
				listItem.add(buildLinkToModal("clientLink", clientId, modal1).add(new Label("clientLbl", new Model(clientName))));
			
				listItem.add(buildLinkToModal("clientLink2", clientId, modal1).add(new Label("clientLbl2", new Model(clientId))));
			
				//listItem.add(DialogLinkBuilder.buildLinkToDocumentModal("uploadDoc", clientId, modal1));
				listItem.add(buildLinkToModal("uploadDoc", clientId, modal1));
				listItem.add(buildLinkToModal("edit", clientId, modal1));
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			});
		
		add(new PagingNavigator("navigator", listView));
		add(buildLinkToModal("createClient",null, modal1));
		}
	
		
	private AjaxLink buildLinkToModal(final String linkID, final String clientId, final ModalWindow modal1) 
		{
		final Client client = (clientId != null ? clientService.loadById(clientId): null);
		
		return new AjaxLink(linkID)
        	{
            @Override
            public void onClick(AjaxRequestTarget target)
            	{
            	modal1.setInitialWidth(linkID.startsWith("uplaod") ? ModalSizes.DOC_DIALOG_WIDTH:  500);
			    modal1.setInitialHeight(linkID.startsWith("uplaod") ? ModalSizes.DOC_DIALOG_HEIGHT:  450);
            	
            	modal1.setPageCreator(new ModalWindow.PageCreator()
            		{
                    public Page createPage()
                     	{
                    	if(!linkID.startsWith("cr") && !linkID.startsWith("ed"))
                    		 return (new ClientDetails(new Model(client), modal1));
                    	
                    	if (linkID.startsWith("upload"))
                    		return new EditDocumentPage(getPage(),clientId, modal1, false, true);
                    	
                    	if (linkID.startsWith("ed"))
	                	 	{
	                		 return (new EditClient(getPage(), new Model(client), modal1)
	                		 	{
	 							@Override
								protected void onSave(Client client, AjaxRequestTarget target)  {  } 
	 							
	 							protected void onError(AjaxRequestTarget target) { }
	                		 	});
	                	 	}
                    	
                    		
                    	return (new EditClient(getPage(), null, modal1)
                		 	{
 							@Override
							protected void onSave(Client client, AjaxRequestTarget target) 
 								{
 								if (modal1 != null) modal1.close(target);
 								}
                		 	});
                		}
                    });
            	
            	modal1.show(target);
            	}
        	};
		}
	
	
	public List<String> getClients()
		{
		return clientService.allClientNames();
		}
	}
*/