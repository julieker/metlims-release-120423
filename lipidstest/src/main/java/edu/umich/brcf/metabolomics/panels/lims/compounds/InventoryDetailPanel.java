package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.util.ArrayList;
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

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;


public class InventoryDetailPanel extends Panel
	{
	@SpringBean
	CompoundService compoundService;

	ListView listView;
	WebMarkupContainer container;
	List<Compound> parentageList;
	
	// itemList
	public InventoryDetailPanel(String id,  final Compound cmpd) 
		{
		super(id);
		setCompound(cmpd);
		final ModalWindow modal2= new ModalWindow("modal2");
		modal2.setInitialWidth(650);
        modal2.setInitialHeight(450);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em");
        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	setCompound(compoundService.loadCompoundById(getCompound().getCid()));
            	target.add(container);
            	}
        	});

        add(modal2);
       
		final InventoryDetailPanel detailPanel = this;
		
		listView = new ListView("parentageList",new PropertyModel(this, "parentageList")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final Compound compound = (Compound) listItem.getModelObject();
				listItem.add(new Label("cid", compound.getCid()));
				listItem.add(new Label("priname",compound.getPrimaryName()));
				ListView childListView = new ListView("invList",new PropertyModel(compound, "inventory")) 
					{
					public void populateItem(final ListItem childListItem)
						{
						final Inventory inv = (Inventory) childListItem.getModelObject();
						childListItem.add(new Label("invId", inv.getInventoryId()));
						childListItem.add(new Label("sup", inv.getSupplier()));
						childListItem.add(new Label("catnum",inv.getCatalogueNumber()));
						childListItem.add(new Label("botsize", inv.getContainerSize()));
						//childListItem.add(new Label("locid", "LC0000"));
						//childListItem.add(new Label("locDesc", "Description"));					
						childListItem.add(new Label("locid", inv.getLocation().getLocationId()));
						childListItem.add(new Label("locDesc", inv.getLocation().getDescription()));
						childListItem.add(new Label("invDate", inv.getInventoryDateStr()));
						childListItem.add(new Label("purity", inv.getPurity().toString()));
						
						childListItem.add(buildEditLink(inv, detailPanel, modal2));
						childListItem.add(OddEvenAttributeModifier.create(childListItem));
						}
					};
					listItem.add(childListView);
				}};
				
		listView.setOutputMarkupId(true);
	
		container = new WebMarkupContainer("itemList");
		container.setOutputMarkupId(true);
		container.add(listView);
		add(container);
        }
	
	
	private AjaxLink buildEditLink(Inventory inv, final InventoryDetailPanel detailPanel, final ModalWindow modal2) 
		{
		// issue 39
		AjaxLink link=new AjaxLink <Void>("editInv", new Model(inv))
			{
            @Override
            public void onClick(AjaxRequestTarget target)
            	{
            	 modal2.setPageCreator(new ModalWindow.PageCreator()
            		 {
                     public Page createPage()
                    	 {
                         return new EditInventory(getPage(), getModel(), detailPanel, modal2);
                    	 }
            		 });
            	 modal2.show(target);
            	}
			};
		return link;
		}
	
	private List<Inventory> invList;
	public List<Inventory> getInvList()
		{
		return invList;
		}
	
	public void setInvList(List<Inventory> invList)
		{
		this.invList=invList;
		}
	
	
	
	private Compound compound;
	public Compound getCompound()
		{
		Compound c = null;
		try { c = compoundService.loadCompoundById(compound.getCid()); }
		catch (Exception e){ c = null; }
		
		return c;
		}
	
	public void setCompound(Compound compound)
		{
		this.compound=compound;
		}
	
	
	public List<Compound> getParentageList()
		{
		parentageList=new ArrayList<Compound>();
		if (getCompound() == null)
			return new ArrayList<Compound>();
		
		getChildren(getCompound());
		if (getCompound().getInventory().size()>0)
			parentageList.add(getCompound());
		Compound tempCompound=compoundService.loadCompoundById(getCompound().getCid());
		while (tempCompound.getCid()!=tempCompound.getParent().getCid())
			{
			tempCompound=compoundService.loadCompoundById(tempCompound.getParent().getCid());
			if (tempCompound.getInventory().size()>0)
				parentageList.add(tempCompound);
			}
		return parentageList;
		}
	
	
	public void getChildren(Compound c)
		{
		if (c == null)
			return;
		
		List<Compound> cmpdList = compoundService.getChildren(c);
		for (Compound listItem : cmpdList)
			{
			if (listItem.getInventory().size()>0)
				parentageList.add(listItem);
			getChildren(listItem);
			}
		}
	}
