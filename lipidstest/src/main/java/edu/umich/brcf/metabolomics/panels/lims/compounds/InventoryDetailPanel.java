package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.BarcodePrintingService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.structures.PrintableBarcode;

public class InventoryDetailPanel extends Panel
	{
	@SpringBean
	CompoundService compoundService;
	
	@SpringBean 
	BarcodePrintingService barcodePrintingService;
	
	// issue 61
	@SpringBean 
	AliquotService aliquotService;
	@SpringBean 
	InventoryService inventoryService;
	@SpringBean 
	UserService userService;
	ListView listView;
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;	
	ListView listViewAliquots; // issue 61
	private List<Aliquot> aliquots; // issue 61
	Label invLabel ;
	Label supLabel;
	Label catnumLabel;
	Label botsizeLabel;
	Label locidLabel;
	Label locdescLabel;
	Label invdateLabel;
	Label purityLabel;
	Label priLabel;
	Label cidLabel;
	Label cidcLabel;
	
	
	Map<String, String> aliquotRetiredMixtureMap = new HashMap<String, String>();	
	// itemList
	public InventoryDetailPanel(String id,  final Compound cmpd) 
		{
		super(id);
		setCompound(cmpd);
		final ModalWindow modal2= new ModalWindow("modal2");
		aliquotRetiredMixtureMap = buildRetiredMixtureMap(aliquotRetiredMixtureMap);
		modal2.setInitialWidth(650);
        modal2.setInitialHeight(450);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em");
        final InventoryDetailPanel detailPanel = this;
        detailPanel.setOutputMarkupId(true);
        add(buildLinkToModalAliquot("addAliquot", modal2, detailPanel, (compound.getCid().equals(getCompound().getCid())), null));
        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	setCompound(compoundService.loadCompoundById(getCompound().getCid()));
            	target.add(container);
            	target.add(detailPanel);// issue 61 
            	}
        	});
        add(modal2);		
		listView = new ListView("parentageList",new PropertyModel(this, "parentageList")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				
				final Compound compound = (Compound) listItem.getModelObject();
				listItem.add(cidLabel = new Label("cid", compound.getCid()));				
				listItem.add(buildLinkToModalAliquot("viewDeletedAliquots", modal2, detailPanel, (compound.getCid().equals(getCompound().getCid())), null));				
				listItem.add(priLabel = new Label("priname",compound.getPrimaryName()));
				listItem.add(cidcLabel = new Label("CIDC","<span style=\"color:red;\">" +    "* CIDC compound, check with Charles Evans.  See inventory in red." + "</span>"));
				//cidcLabel.add(AttributeModifier.replace("style", "color: red;"));
				// issue 224
				cidcLabel.setEscapeModelStrings(false);
				if (isCIDC(compound))
					cidcLabel.add(AttributeAppender.replace("style", "display:block"));
				else
					cidcLabel.add(AttributeAppender.replace("style", "display:none"));
				ListView childListView = new ListView("invList",new PropertyModel(compound, "inventory")) 
					{
					// issue 224
					public void populateItem(final ListItem childListItem)
						{
						final Inventory inv = (Inventory) childListItem.getModelObject();
						childListItem.add(invLabel = new Label("invId", inv.getInventoryId()));
						childListItem.add(supLabel = new Label("sup", inv.getSupplier()));
						childListItem.add(catnumLabel = new Label("catnum",inv.getCatalogueNumber()));
						childListItem.add(botsizeLabel = new Label("botsize", inv.getContainerSize()));				
						childListItem.add(locidLabel = new Label("locid", inv.getLocation().getLocationId()));
						childListItem.add(locdescLabel = new Label("locDesc", inv.getLocation().getDescription()));
						childListItem.add(invdateLabel = new Label("invDate", inv.getInventoryDateStr()));
						childListItem.add(purityLabel = new Label("purity", inv.getPurity().toString()));						
						childListItem.add(buildEditLink(inv, detailPanel, modal2));
						childListItem.add(OddEvenAttributeModifier.create(childListItem));
						if (inv.getLocation() != null && inv.getLocation().getLocationId().equals("LC1043"))
							{						
							invLabel.add(AttributeModifier.replace("style", "color: red;"));	
							supLabel.add(AttributeModifier.replace("style", "color: red;"));	
							catnumLabel.add(AttributeModifier.replace("style", "color: red;"));	
							botsizeLabel.add(AttributeModifier.replace("style", "color: red;"));	
							locidLabel.add(AttributeModifier.replace("style", "color: red;"));
							locdescLabel.add(AttributeModifier.replace("style", "color: red;"));
							invdateLabel.add(AttributeModifier.replace("style", "color: red;"));
							purityLabel.add(AttributeModifier.replace("style", "color: red;"));
	
							}
						
						else
							{
							invLabel.add(AttributeModifier.replace("style", "color: black;"));	
							supLabel.add(AttributeModifier.replace("style", "color: black;"));	
							catnumLabel.add(AttributeModifier.replace("style", "color: black;"));	
							botsizeLabel.add(AttributeModifier.replace("style", "color: black;"));	
							locidLabel.add(AttributeModifier.replace("style", "color: black;"));
							locdescLabel.add(AttributeModifier.replace("style", "color: black;"));
							invdateLabel.add(AttributeModifier.replace("style", "color: black;"));
							purityLabel.add(AttributeModifier.replace("style", "color: black;"));
							}
						}
					};
				// issue 224
				listItem.add(childListView);	
				}
			};			
		listView.setOutputMarkupId(true);
		///// issue 61
		// issue 61
		add(listViewAliquots = new ListView("aliquots", new PropertyModel(this, "aliquots")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final Aliquot alq = (Aliquot) listItem.getModelObject();		
				listItem.add(new Label("aliquotId", new Model(alq.getAliquotId())));
				listItem.add(buildLinkToModalAliquot("aliquotLink", modal2, detailPanel , true, alq)).setVisible(true);
				listItem.add(new Label("aliquotLabel", new Model(alq.getAliquotLabel())));
				// issue 196
				listItem.add(new Label("location", new Model(alq.getLocation() == null ? null : alq.getLocation().getLocationId())));	
				listItem.add(new Label("aliquotNeatDilutionUnits",  new Model(alq.getNeat().equals('1') && alq.getDry().equals('1') ? (alq.getWeightedAmount() + " " + alq.getWeightedAmountUnits()) : (alq.getNeat().equals('1') ? alq.getDconc() : alq.getDcon()) + " " + (alq.getNeat().equals('1') ? alq.getDConcentrationUnits() : alq.getNeatSolVolUnits() ))));
				listItem.add(new Label("parentInventory", new Model(alq.getInventory() == null ? "none" : alq.getInventory().getInventoryId())));	
				listItem.add(new Label("compoundAliquot", new Model(alq.getCompound() == null ? "none" : alq.getCompound().getCid())));	
				listItem.add(new Label("isdry", new Model(alq.getDry().toString().equals("1") ? "Yes" : "No" )));	
				listItem.add(new Label("retiredmixture", new Model(aliquotRetiredMixtureMap.get(alq.getAliquotId()))));	
				listItem.add(new Label("createDate", new Model(alq.getCreateDateString())));
				listItem.add(new Label("createdBy", new Model(userService.getFullNameByUserId(alq.getCreatedBy()))));
			    listItem.add(buildLinkToModalAliquot("editAliquot", modal2, detailPanel , true, alq)).setVisible(true);
				listItem.add(buildLinkToModalDeleteAliquot("deleteAliquot",alq,modal2,  detailPanel));
				listItem.add(buildLinkPrintAliquot("printAliquot",alq));
				}
			});			
		container = new WebMarkupContainer("itemList");
		container.setOutputMarkupId(true);
		container.add(listView);
		add(container);
        }
		
	// issue 61
	private AjaxLink buildLinkToModalDeleteAliquot(final String id, final Aliquot alq, final ModalWindow modal1, final InventoryDetailPanel idp ) 
		{
		// issue 39
		 AjaxLink lnk =  new AjaxLink<Void> (id)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				setModalDimensions(id, modal1);
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage() {   return setPage(id, modal1, idp, alq);   }
					});			
				    modal1.show(target);
				}
			};
		if (!alq.getCreatedBy().equals(((MedWorksSession) Session.get()).getCurrentUserId())) 
		    return lnk;	
		return lnk;
		}
	
	
	
	// issue 199
	public Map<String, String> buildRetiredMixtureMap (Map <String, String> aliquotRetiredMixtureMap)
		{
		List <Object []> retiredMixtureObjects = aliquotService.getRetiredToMixture();
	
		for (Object[] result : retiredMixtureObjects)
			aliquotRetiredMixtureMap.put(result[0].toString(),result[1].toString());
		return aliquotRetiredMixtureMap;
		}
	
	
	// issue 61
	private AjaxLink buildLinkToModalAliquot(final String linkID, final ModalWindow modal1, final InventoryDetailPanel idp, final boolean aliqutButtonVisible, final Aliquot alq) 
		{
		// issue 39
		AjaxLink alqLink;
		alqLink =  new AjaxLink <Void>(linkID)
			{
			@Override
			public boolean isEnabled()
				{
				return true; // issue 196
				//return (getCompound().getInventory().size() > 0);	
				}
			@Override
			public boolean isVisible()
				{
				return true; // issue 196
				//return aliqutButtonVisible;	
				}
			@Override
			public void onClick(AjaxRequestTarget target)
				{	
				setModalDimensions(linkID, modal1);
				
				if (linkID.equals("editAliquot"))
				    {
					List <Object []> aliquotObjects = aliquotService.getAliquotExistInMixtureInfo(alq.getAliquotId());
					String mixStr = "";
					if (aliquotObjects.size() > 0)
						{
						for (Object[] result : aliquotObjects)
							mixStr = mixStr + " " + result[1].toString();
						target.appendJavaScript(StringUtils.makeAlertMessage("Aliquot:" + alq.getAliquotId() + " belongs to mixture(s):" + mixStr + " Please remove this aliquot from mixture(s):" + mixStr + " in order to edit it."));
						return;
						}
				    }
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage() {   return setPage(linkID, modal1, idp, alq);   }
					});			
				modal1.show(target);
				}
			};
		if (linkID.equals("aliquotLink"))
		    alqLink.add(new Label("aliquotId", alq.getAliquotId()));
			return alqLink;
		}
	
	private AjaxLink buildLinkPrintAliquot(final String linkID,  final Aliquot alq) 
		{
		// issue 39
		AjaxLink priAliquotLink;
		priAliquotLink =  new AjaxLink <Void>(linkID)
			{
			public void onClick(AjaxRequestTarget target)
				{
				try
					{
					// issue 120
					// issue 162					
					int i = StringUtils.isEmptyOrNull(alq.getCompound().getPrimaryName()) ? 0 : (alq.getCompound().getPrimaryName().length() >= 30 ? 30 : alq.getCompound().getPrimaryName().length());
					List <String> aliquotPrintList = new ArrayList<String> ();
					aliquotPrintList.add(alq.getAliquotId());
					String barcodeStr = aliquotService.getInventoryDateList(aliquotPrintList).get(0).replace("<br>",  "\\&");
					new PrintableBarcode(barcodePrintingService, "Compound Zebra",null).printBarcodes(barcodeStr, true);
					target.appendJavaScript(StringUtils.makeAlertMessage("Printed Aliquot:" + alq.getAliquotId()));
					}
				catch (RuntimeException r)
					{
					target.appendJavaScript(StringUtils.makeAlertMessage("There was a problem printing :" + r.getMessage()));
					r.printStackTrace();
					}
				catch (Exception e)
					{
					target.appendJavaScript(StringUtils.makeAlertMessage(e.getMessage()));
					e.printStackTrace();
					}				
				}
			};
		return priAliquotLink;
		}
	
	private void setModalDimensions(String linkID, ModalWindow modal1)
		{
		switch(linkID)
			{
			case "addAliquot" :
				modal1.setInitialWidth(1000);
				modal1.setInitialHeight(580); break;
			case "deleteAliquot" :
				modal1.setInitialWidth(650);
				modal1.setInitialHeight(200); break;
			default : 
				modal1.setInitialWidth(1000);
				modal1.setInitialHeight(580);
			}
		}

	// issue 61
	private Page setPage(String linkID, final ModalWindow modal1, final InventoryDetailPanel idp, Aliquot alq)
		{
		switch(linkID)
			{
			case"aliquotLink" : return new EditAliquot(getPage(), new Model <Aliquot> (alq),idp, modal1, true);
			case"addAliquot" : return new EditAliquot(getPage(), idp, modal1);
			case "editAliquot" : return new EditAliquot(getPage(), new Model <Aliquot> (alq),idp, modal1);
			case "viewDeletedAliquots" : return new AliquotDeleteDetail("viewDeletedAliquots", compound, idp);
			case "deleteAliquot" : return new DeleteReason("deleteAliquot",alq.getAliquotId(), modal1);
			default :
				return new EditAliquot(getPage(), idp, modal1);
			}
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
		// issue 15
		if (tempCompound.getParent()!=null)
		    {
			while (tempCompound.getCid()!=tempCompound.getParent().getCid())
				{
				tempCompound=compoundService.loadCompoundById(tempCompound.getParent().getCid());			
				if (tempCompound.getInventory().size()>0)
					parentageList.add(tempCompound);
				// issue 15
				if (tempCompound.getParent()==null)
					break;
				}
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
	
	// issue 61
	public List<Aliquot> getAliquots()
		{
		List<Aliquot> nList = aliquotService.loadByCid(getCompound().getCid());
		return nList;
		}

	public void setAliquots(List<Aliquot> aliquots)
		{
		this.aliquots= aliquots;
		}
	
	public boolean isCIDC (Compound compound)
		{
		for (Inventory inv : compound.getInventory())
			{
			if (inv.getLocation() != null && inv.getLocation().getLocationId().equals("LC1043"))
				return true;
			}
		return false;
		}
	
	}
