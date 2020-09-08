package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.UserService;

public class AliquotDeleteDetail extends WebPage
	{
	@SpringBean
	CompoundService compoundService;
	
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
	private List<Aliquot> deletedAliquots;
	AliquotDeleteDetail aliquotDeleteDetail = this;
	// itemList
	public AliquotDeleteDetail(String id,  final Compound cmpd, final InventoryDetailPanel idp) 
		{
	//	super(id);
		setCompound(cmpd);
		final ModalWindow modal2= new ModalWindow("modal2");
		modal2.setInitialWidth(650);
        modal2.setInitialHeight(450);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em");     
        add(new Label("titleLabel", "View Deleted Aliquots"));
        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	}
        	});
        add(modal2);		
		///// issue 61
		add(listViewAliquots = new ListView("deletedAliquots", new PropertyModel(this, "deletedAliquots")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final Aliquot alq = (Aliquot) listItem.getModelObject();		
				listItem.add(new Label("aliquotId", new Model(alq.getAliquotId())));
				listItem.add(new Label("aliquotLabel", new Model(alq.getAliquotLabel())));
				listItem.add(new Label("aliquotNeatDilutionUnits",  new Model(alq.getNeat().equals('1') && alq.getDry().equals('1') ? (alq.getWeightedAmount() + " " + alq.getWeightedAmountUnits()) : (alq.getNeat().equals('1') ? alq.getDconc() : alq.getDcon()) + " " + (alq.getNeat().equals('1') ? alq.getDConcentrationUnits() : alq.getNeatSolVolUnits() ))));
				listItem.add(new Label("parentInventory", new Model(alq.getInventory().getInventoryId())));	
				listItem.add(new Label("createDate", new Model(alq.getCreateDateString())));
				listItem.add(new Label("createdBy", new Model(userService.getFullNameByUserId(alq.getCreatedBy()))));
				listItem.add(new Label("deleteReason", new Model(alq.getDeleteReason())));
				listItem.add(buildLinkToModalAliquot("viewAliquot", modal2, idp , true, alq)).setVisible(true);
				}
			});			
		container = new WebMarkupContainer("itemList");
		container.setOutputMarkupId(true);
		// add back jak container.add(listView);
		add(container);
        }
		
	// issue 61
	// issue 61
	private AjaxLink buildLinkToModalAliquot(final String linkID, final ModalWindow modal1, final InventoryDetailPanel idp, final boolean aliqutButtonVisible, final Aliquot alq) 
		{
		// issue 39
		return new AjaxLink <Void>(linkID)
			{
			@Override
			public boolean isEnabled()
				{
				return (getCompound().getInventory().size() > 0);	
				}
			@Override
			public boolean isVisible()
				{
				return aliqutButtonVisible;	
				}
			@Override
			public void onClick(AjaxRequestTarget target)
				{	
				setModalDimensions(linkID, modal1);
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage() {   return setPage(linkID, modal1, idp, alq);   }
					});			
				modal1.show(target);
				}
			};
		}
		
	private void setModalDimensions(String linkID, ModalWindow modal1)
		{ 
		modal1.setInitialWidth(1000);
		modal1.setInitialHeight(580);
		}

	// issue 61
	private Page setPage(String linkID, final ModalWindow modal1, final InventoryDetailPanel idp, Aliquot alq)
		{
		switch(linkID)
			{
			case "viewAliquot" : return new EditAliquot(getPage(), new Model <Aliquot> (alq),idp, modal1, true);
			default :
				return new EditAliquot(getPage(), idp, modal1);
			}
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
	
	public List<Aliquot> getDeletedAliquots()
		{
		List<Aliquot> nList = aliquotService.loadByCidDeleted(getCompound().getCid());
		return nList;
		}
	
	public void setDeletedAliquots(List<Aliquot> deletedAliquots)
		{
		this.deletedAliquots= deletedAliquots;
		}
	
	}
