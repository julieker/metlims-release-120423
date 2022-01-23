package edu.umich.brcf.metabolomics.panels.lims.assays;
/*************************************
 * Created by Julie Keros Nov 2020 for issue 100 associating Aliquots to Assays
 */
import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.metabolomics.panels.lims.compounds.EditAliquot;
import edu.umich.brcf.metabolomics.panels.lims.compounds.InventoryDetailPanel;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.dto.AliquotDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.StringParser;

public class AssayAliquotDetailPanel extends Panel
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
	@SpringBean
	AssayService assayService;	
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;
	ListView listViewAliquots; // issue 61
	private List<Aliquot> aliquots; // issue 61
 	AssayAliquotDetailPanel AssayAliquotDetailPanel = this;
	AliquotDTO aliquotDto = new AliquotDTO();
	// itemList
	//public AssayAliquotDetailPanel(String id,  final Compound cmpd, final InventoryDetailPanel idp) 
	public AssayAliquotDetailPanel(String id)
		{	
		super(id);
		//setCompound(cmpd);
		final ModalWindow modal2= new ModalWindow("modal2");
		setDefaultModel(new CompoundPropertyModel<AliquotDTO>(aliquotDto));
		
		modal2.setInitialWidth(650);
        modal2.setInitialHeight(450);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em");     
        add(new Label("titleLabel", "Assays and related Aliquots"));
        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	}
        	});
        add(modal2);	       
        DropDownChoice assayDD = new DropDownChoice("assayId",assayService.allAssayNames() );			
		add (assayDD); 
		assayDD.setOutputMarkupId(true);
		assayDD.setOutputMarkupPlaceholderTag(true);
		
		assayDD.add(buildStandardFormComponentUpdateBehavior("change", "updateAssays", aliquotDto)); 
		////// issue 100
		containerAliquot = new WebMarkupContainer("itemAliquotList");
		containerAliquot.setOutputMarkupId(true);
		// add back jak container.add(listView);
		add(containerAliquot);
		containerAliquot.add(listViewAliquots = new ListView("aliquots", new PropertyModel(this, "aliquots")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final Aliquot alq = (Aliquot) listItem.getModelObject();		
				listItem.add(buildLinkToModalAliquot("aliquotLink", modal2,  true, alq)).setVisible(true);
				listItem.add(new Label("aliquotLabel", new Model(alq.getAliquotLabel())));
				listItem.add(new Label("location", new Model(alq.getLocation()== null ? "" :alq.getLocation().getLocationId())));	
				listItem.add(new Label("aliquotNeatDilutionUnits",  new Model(alq.getNeat().equals('1') && alq.getDry().equals('1') ? (alq.getWeightedAmount() + " " + alq.getWeightedAmountUnits()) : (alq.getNeat().equals('1') ? alq.getDconc() : alq.getDcon()) + " " + (alq.getNeat().equals('1') ? alq.getDConcentrationUnits() : alq.getNeatSolVolUnits() ))));
				// issue 199
				listItem.add(new Label("parentInventory", new Model(alq.getInventory()== null ? "" : alq.getInventory().getInventoryId())));	
				listItem.add(new Label("createDate", new Model(alq.getCreateDateString())));
				listItem.add(new Label("createdBy", new Model(userService.getFullNameByUserId(alq.getCreatedBy()))));
				}
			});					
		listViewAliquots.setOutputMarkupId(true);
		listViewAliquots.setOutputMarkupPlaceholderTag(true);	
		modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        {
	        public void onClose(AjaxRequestTarget target)
	            {
	            target.add(containerAliquot);// issue 61 
	            }
	        });
        }
		
	// issue 100
	private AjaxLink buildLinkToModalAliquot(final String linkID, final ModalWindow modal1,  final boolean aliqutButtonVisible, final Aliquot alq) 
		{
		Compound cmpd  = alq.getCompound();
		final InventoryDetailPanel idp = new InventoryDetailPanel ("inventory", cmpd);
		// Issue 100
			
		AjaxLink link =  new AjaxLink <Void>(linkID)
			{
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
		link.add(new Label("aliquotId", alq.getAliquotId()));
		return link;	
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
			case "aliquotLink" : return new EditAliquot(getPage(), new Model <Aliquot> (alq),idp, modal1, true); // issue 199
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
	
	// issue 100
	public List<Aliquot> getAliquots()
		{
		List<Aliquot> nList = aliquotService.getAliquotsFromAssay(StringParser.parseId(aliquotDto.getAssayId()));
		return nList;
		}

	public void setAliquots(List<Aliquot> aliquots)
		{
		this.aliquots= aliquots;
		}
	
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, final AliquotDTO aliquotDto)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
	    	{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
	    		{
				switch (response)
	        		{
	        		case "updateAssays" :
	        			List <Aliquot> aliquots = aliquotService.getAliquotsFromAssay(StringParser.parseId(aliquotDto.getAssayId()));
	        			target.add(containerAliquot);
	        			break;
	                }
	    		}
	    	};
		}
	}
