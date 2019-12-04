////////////////////////////////////////////////////
// SortableOrganizationPanel.java
// Written by Jan Wigginton, Oct 27, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.organization;

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

import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.dto.OrganizationDTO;
import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.panels.utilitypanels.ButtonColumn3;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;




public class SortableOrganizationPanel extends Panel 
	{
	@SpringBean
	private OrganizationService organizationService;
	
	METWorksPctSizableModal modal1;
	IModel organizations = new LoadableDetachableModel() 
		{
		protected Object load() { return ((List<Organization>)  organizationService.allOrganizationObjects()); }
		};
		
	
	public SortableOrganizationPanel(String id) 
		{
		super(id);
		
		final WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);
		
		modal1 = new METWorksPctSizableModal("modal1", 0.4, .3);
		add(modal1);
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
			{
			public void onClose(AjaxRequestTarget target) { target.add(container); }
			});
		
		add(buildLinkToCreateModal("createOrganization",null));
		container.add(buildDataTable("table", modal1));
		}
	
	
	private DefaultDataTable <Client, String>  buildDataTable(String id, final METWorksPctSizableModal modal)
		{	
		SortableOrganizationDataProvider cmpdProvider = new SortableOrganizationDataProvider(getOrganizations());
		
		
		List<IColumn<?, ?>> columns = new ArrayList<IColumn<?, ?>>();
		columns.add(getPropertyColumn("Organization Name", "orgName", "orgName"));
		columns.add(getPropertyColumn("Organization ID", "organizationId", "organizationId"));
		
		columns.add(new ButtonColumn3(new Model(""), "   Details...   ")
			{
			@Override
			protected void onClick(IModel clicked, AjaxRequestTarget target)
				{
				doDetailClick(clicked, target);
				}
			
			@Override
			public String getCssClass() { return "borderColumn3"; }
			});
		
		
		columns.add(new ButtonColumn3(new Model(""), "   Client List...   ")
			{
			@Override
			protected void onClick(IModel clicked, AjaxRequestTarget target)
				{
				doListClick(clicked, target);
				}
				
				@Override
				public String getCssClass() { return "borderColumn3"; }
			});
		
		
		columns.add(new ButtonColumn3(new Model(""), "   Edit...  ")
			{
			@Override
			protected void onClick(IModel clicked, AjaxRequestTarget arg0) 
				{
				doEditClick(clicked, arg0);
				}
			
			@Override
			public String getCssClass() { return "borderColumn3"; }
			});
		
		
		return new DefaultDataTable(id, columns, cmpdProvider, 8000);
		}
		
	
	private void doListClick(final IModel <Organization>clicked, AjaxRequestTarget target)
		{
		modal1.setPageDimensions(0.8, 0.8);
		
		modal1.setPageCreator(new ModalWindow.PageCreator()
			{
			public Page createPage()
				{
			   	return new OrganizationClientsPage(getPage(), modal1, clicked);	
			   	}
				
			});
		
		if (modal1 != null) modal1.show(target);
		}
		
	
	private void doEditClick(final IModel <Organization>clicked, AjaxRequestTarget target)
		{
		modal1.setPageDimensions(0.5, 0.35);
		
		modal1.setPageCreator(new ModalWindow.PageCreator()
			{
			public Page createPage()
				{
				OrganizationDTO dto = new OrganizationDTO(clicked.getObject());
				
				return (new EditOrganization(getPage(), dto, false, modal1)
		    		{
		    		public void onSave(Organization org, AjaxRequestTarget target) { }
//		    			{
//		    			//setResponsePage(getPage());
//		    		 	}
		    		});
				}
			});
		
		modal1.show(target);
		}
	
	
	private void doDetailClick(final IModel <Organization>clicked, AjaxRequestTarget target)
		{
		modal1.setPageDimensions(0.5, 0.35);
		
		modal1.setPageCreator(new ModalWindow.PageCreator()
			{
			public Page createPage()
				{
				return (new OrganizationDetail(clicked));
				}
			});
		
		modal1.show(target);
		}
	
	
	private AjaxLink buildLinkToCreateModal(final String linkID, final String clientId) 
		{
		final Organization organization = ( clientId != null ? organizationService.loadById(clientId) : null);
		// issue 39
		return new AjaxLink <Void> (linkID)
			{
			@Override
			public void onClick(final AjaxRequestTarget target)
				{
				modal1.setPageDimensions(0.5, 0.35);
				
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage()
						{
						return (new EditOrganization(getPage(), new OrganizationDTO(), true, modal1)
				    		{
				    		public void onSave(Organization org, AjaxRequestTarget target)
				    			{
				    			///if (modal1 != null) modal1.close(target); 
				    		 	}
				    		});
						}
					});
					
				modal1.show(target);
				}
			
			};
		}
		
	
	public IModel<List<Organization>> getOrganizations() 
		{
		return organizations;
		}
	
	
	private <T, S> PropertyColumn<?, ?> getPropertyColumn(final String label, final String  propertyExpression, String sortVar)
		{
		if (sortVar.equals(""))
			return new PropertyColumn(new Model<String>(label), propertyExpression)
				{
				@Override
				public String getCssClass() { return "borderColumn"; }
				
				@Override
				public Component getHeader(String componentId) 
				{
				Component header=super.getHeader(componentId);
				header.add(new AttributeModifier("style","color : text-decoration : underline; blue; background: red; width:100%; text-align : left; font-size ;110%")); 
				return header;
				}
			};
		
		return new PropertyColumn(new Model<String>(label), propertyExpression, sortVar)
			{
			@Override
			public String getCssClass() { return "borderColumn"; }
			
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
		
		
		
		
		
		
		
		
		
		
		
