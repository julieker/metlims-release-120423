////////////////////////////////////////////////////
// SortableClientPanel.java
// Written by Jan Wigginton, Oct 27, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lims.client;


import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.lims.client.ClientDetails;
import edu.umich.brcf.metabolomics.panels.lims.client.EditClient;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.panels.utilitypanels.ButtonColumn3;
import edu.umich.brcf.shared.panels.utilitypanels.ClickablePropertyColumn;
import edu.umich.brcf.shared.panels.utilitypanels.EditDocumentPage;




public class SortableClientPanel extends Panel 
	{
	@SpringBean
	private ClientService clientService;
	
	ModalWindow modal1;
	IModel clients = new LoadableDetachableModel() 
		{
		protected Object load() { return ((List<Client>)  clientService.allClientsSmall()); }
		};
	
	
	public SortableClientPanel(String id) 
		{
		super(id);
		
		final WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);
		
		modal1 = new ModalWindow("modal1");
		add(modal1);	
		
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
			{
			public void onClose(AjaxRequestTarget target) { target.add(container); }
			});
			
		add(buildLinkToCreateModal("createClient", null, modal1));
		container.add(buildDataTable("table", modal1));
		}
	
	
	private DefaultDataTable <Client, String>  buildDataTable(String id, final ModalWindow modal)
		{	
		SortableClientDataProvider cmpdProvider = new SortableClientDataProvider(getClients());
		
		List<IColumn<?, ?>> columns = new ArrayList<IColumn<?, ?>>();
		columns.add(getPropertyColumn("Client/Lab Name", "clientName", "lab"));
		columns.add(getPropertyColumn("Client ID", "clientID", "clientID"));
		
		
		columns.add(new ButtonColumn3(new Model(""), "   Details...   ")
			{
			@Override
			protected void onClick(IModel clicked, AjaxRequestTarget target)
				{
				doDetailClick(modal, clicked, target);
				}
			
			@Override
			public String getCssClass() { return "borderColumn4"; }
			});
			
		
		columns.add(new ButtonColumn3(new Model(""), "   Edit...  ")
			{
			@Override
			protected void onClick(IModel clicked, AjaxRequestTarget arg0) 
				{
				doAttachClick(modal, clicked, arg0);
				}
		
			@Override
			public String getCssClass() { return "borderColumn4"; }
			});
		
		
		columns.add(new ButtonColumn3(new Model(""), "Attach Doc...")
			{
			@Override
			protected void onClick(IModel clicked, AjaxRequestTarget arg0) 
				{
				doUploadClick(modal, clicked, arg0);
				}
			@Override
			public String getCssClass() { return "borderColumn4"; }
			});
		
		DefaultDataTable table =  new DefaultDataTable(id, columns, cmpdProvider, 8000);
		//table.getTopToolbars().getBody().setVisible(false);
		//	table.addBottomToolbar(new NavigationToolbar(table));
		//	 table.addTopToolbar(new HeadersToolbar(table, cmpdProvider));
		
		return table;
		}
	
	
	private void doUploadClick(final ModalWindow modal, final IModel <Client>clicked, AjaxRequestTarget target)
		{
		modal.setInitialHeight(300);
		modal.setInitialWidth(820);
		
		modal.setPageCreator(new ModalWindow.PageCreator()
			{
			public Page createPage()
				{
				return new EditDocumentPage(getPage(), clicked.getObject().getClientID(), modal, false, true); 
				//return new EditDocumentPage(getPage(), clicked.getObject().), modal, false, true);
				}
			});
		
		if (modal != null) modal.show(target);
		}
	
	
	private void doAttachClick(final ModalWindow modal1, final IModel <Client>clicked, AjaxRequestTarget target)
		{
		modal1.setInitialWidth(625);
		modal1.setInitialHeight(400);
		
		String clientId = ((Client) clicked.getObject()).getClientID();
		final Client withDocs = clientService.loadById(clientId); 
		
		modal1.setPageCreator(new ModalWindow.PageCreator()
			{
			public Page createPage()
				{
				return (new EditClient(getPage(), new Model(withDocs), modal1)
					{
					@Override
					protected void onSave(Client client, AjaxRequestTarget target1) {   }
					});
				}
			});
	
		modal1.show(target);
		}
	
	
	private void doDetailClick(final ModalWindow modal, final IModel <Client>clicked, AjaxRequestTarget target)
		{
		modal1.setInitialWidth(625);
	    modal1.setInitialHeight(400);
	    
	    final Client withDocs = clientService.loadById(clicked.getObject().getClientID());

	    modal1.setPageCreator(new ModalWindow.PageCreator()
    		{
            public Page createPage()
             	{
            	return (new ClientDetails(new Model(withDocs), modal));
             	}
    		});
    	
    	modal1.show(target);
		}
	
	
	private AjaxLink buildLinkToCreateModal(final String linkID, final String clientId, final ModalWindow modal1) 
		{
		final Client client = ( clientId != null ? clientService.loadById(clientId) : null);
		// issue 39
		return new AjaxLink <Void>(linkID)
			{
			@Override
			public void onClick(final AjaxRequestTarget target)
				{
				modal1.setInitialWidth(625);
				modal1.setInitialHeight(400);
				
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage()
						{
						return (new EditClient(getPage(), null, modal1)
                		 	{
 							@Override
							protected void onSave(Client client, AjaxRequestTarget target)  { }
						 	});
						}
					});
				
				modal1.show(target);
				}
			};
		}
	
	
	public IModel<List<Client>> getClients() 
		{
		return clients;
		}
	
	
	
	public ClickablePropertyColumn <Client, ?> buildClickableProjectCol(final String colTitle, final String property)
		{
		return new ClickablePropertyColumn<Client, String> (Model.of(colTitle), property, property)
			{
			@Override
			protected void onClick(final IModel<Client> client, AjaxRequestTarget target)
				{
				modal1.setInitialWidth(625);
				modal1.setInitialHeight(600);
				
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage()
						{
						return new ClientDetails(client, modal1);
						}
					});
				
				modal1.show(target);
				}
	
			@Override
			public String getCssClass() { return "clientId".equals(colTitle) ? "borderColumn3" : "borderColumn3b"; }
			
			@Override
			public Component getHeader(String componentId) 
				{
				Component header=super.getHeader(componentId);
				header.add(new AttributeModifier("style","width : 100%;  padding-top : 7px; font-size : 90%; color : blue; background : green; text-align : center; border-bottom : 1px groove white")); 
				return header;
				}
			};
		}
	
	
	
	private <T, S> PropertyColumn<?, ?> getPropertyColumn(final String label, final String  propertyExpression, String sortVar)
		{
		if (sortVar.equals(""))
			return new PropertyColumn(new Model<String>(label), propertyExpression)
				{
				@Override
				public String getCssClass() { return "Client ID".equals(label) ? "borderColumn" : "borderColumnb"; }
				
				@Override
				public Component getHeader(String componentId) 
					{
					Component header=super.getHeader(componentId);
					header.add(new AttributeModifier("style","color : blue;  text-decoration : underline; background: transparent; width:100%; text-align : left; font-size ;110%")); 
					return header;
					}
				};
	
		return new PropertyColumn(new Model<String>(label), propertyExpression, sortVar)
			{
			@Override
			public String getCssClass() { return "Client ID".equals(label) ? "borderColumn" : "borderColumnb"; }
			
			@Override
			public Component getHeader(String componentId) 
				{
				Component header=super.getHeader(componentId);
				header.add(new AttributeModifier("style","width: 100%;  padding-top : 7px; font-weight ; 700;  text-decoration : underline; color : blue; background : transparent ; text-align : left; font-size : 110% ")); 
				return header;
				}
			};	
		}
	}
	










