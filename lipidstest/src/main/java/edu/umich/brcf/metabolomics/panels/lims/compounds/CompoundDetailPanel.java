
// Revisited : October 2016 (JW), Julie Keros (12/16)
// Updated by Julie Keros Mar 4, 2020

package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.image.resource.RenderedDynamicImageResource;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.h2.util.StringUtils;
import chemaxon.formats.MolImporter;
import chemaxon.marvin.MolPrinter;
import chemaxon.struc.Molecule;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.metabolomics.layers.service.CompoundNameService;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.util.utilpackages.CompoundIdUtils;

public class CompoundDetailPanel extends Panel 
	{
	WebMarkupContainer container;
	ListView listView;
	
	@SpringBean
	CompoundNameService compoundNameService;
	
	@SpringBean 
	CompoundService compoundService;
	
	@SpringBean
	InventoryService inventoryService;
	Model<String> strMdlMultipleSmiles = Model.of("");
	Model<String> strMdlMultipleSmilesForInchi = Model.of("");
	Label msgMultipleSmiles = null;
	Label msgMultipleSmilesForInchi = null;
	List <String> smilesInchiKeyMultipleSmilesList = new ArrayList <String> (); // issue 31
	Label chem_abs_number_label = null;
	private LoadableDetachableModel getCompoundModel(final String id) 
		{
		return new LoadableDetachableModel() 
			{
			protected Object load() 
			    {
				return compoundService.loadCompoundById(getCmpId());
			    }
			};
		}
	
	public CompoundDetailPanel(String id, String cid, final boolean insider)
		{
		super(id);
		setOutputMarkupId(true);
		setCmpId(cid);
		setDefaultModel(new CompoundPropertyModel(getCompoundModel(getCmpId())));		
		add(new Label("cid",new PropertyModel(this, "compound.cid")));
		add(new Label("molecular_formula",new PropertyModel(this, "compound.molecular_formula")));
		add(new Label("molecularWeightAsDouble",new PropertyModel(this, "compound.molecularWeightAsDouble")));
		chem_abs_number_label = new Label("chem_abs_number",new PropertyModel(this, "compound.chem_abs_number"));
		add (chem_abs_number_label);
	    smilesInchiKeyMultipleSmilesList = new ArrayList <String> ();  
	    smilesInchiKeyMultipleSmilesList = getSmilesFromCompoundIdandSetTag();	    		
	    add(new Label("smiles",new PropertyModel(this, "compound.smiles")));
		add(new Label("inchiKey",new PropertyModel(this, "compound.inchiKey")));// issue 27 2020
		add(new Label("parent.cid",new PropertyModel(this, "compound.parent.cid")));
		add(new Label("logpAsDouble",new PropertyModel(this, "compound.logpAsDouble")));
		add(new Label("nominalMassAsDouble",new PropertyModel(this, "compound.nominalMassAsDouble")));
		add(new Label("pka",new PropertyModel(this, "compound.pka")));
		// issue 58 get rid of human rel
		add(new Label("solvent.name",new PropertyModel(this, "compound.solvent.name")));
		msgMultipleSmiles = new Label("multipleSmiles", strMdlMultipleSmiles );
		msgMultipleSmilesForInchi = new Label("multipleSmilesForInchi", strMdlMultipleSmilesForInchi );
		add (msgMultipleSmiles);
		add (msgMultipleSmilesForInchi);
		msgMultipleSmiles.setOutputMarkupId(true);	
		msgMultipleSmilesForInchi.setOutputMarkupId(true);	
		final CompoundDetailPanel cdp = this;
		add(new ListView("parentsList", new PropertyModel(this, "parentsList")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final Compound c = (Compound) listItem.getModelObject();
				listItem.add(getAjaxLinkForCid("parentLink","parentId", c.getCid(),cdp));
				listItem.add(new Label("parentName", new Model(c.getPrimaryName())));
				}
			});		
		add(new ListView("childrenList", new PropertyModel(this, "childrenList")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final Compound c = (Compound) listItem.getModelObject();
				listItem.add(getAjaxLinkForCid("childLink","childId", c.getCid(),cdp));
				listItem.add(new Label("childName", new Model(c.getPrimaryName())));
				}
			});
		add(new NonCachingImage("structure", new PropertyModel(this, "structure")));				
		final ModalWindow modal1= buildModalWindow("modal1", cdp);
		add(modal1);		
		add(buildLinkToModal("add", modal1, cdp).setVisible(insider));
        add(buildLinkToModal("addInv", modal1, cdp).setVisible(insider));
        add(buildLinkToModal("edit", modal1, cdp).setVisible(insider));
        add(buildLinkToModal("addName", modal1, cdp).setVisible(insider));
        add(buildInvLink().setVisible(insider));        
		add(listView = new ListView("names", new PropertyModel(this, "names")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final CompoundName cn = (CompoundName) listItem.getModelObject();
				listItem.add(new Label("name", new Model(cn.getName())));
				listItem.add(new Label("type", new Model(cn.getNameType())));
				listItem.add(new Label("html", new Model(cn.getHtml())));
				listItem.add(buildAjaxLink("editName", modal1, new EditCompoundName(getPage(), new Model(cn), container, modal1)).setVisible(insider));
				}
			});
		listView.setOutputMarkupId(true);
		}
	
   public ModalWindow buildModalWindow(String linkId, final CompoundDetailPanel cdp)
	   {
	   ModalWindow modal1 = new ModalWindow(linkId);	   
	   modal1.setWidthUnit("em");
       modal1.setHeightUnit("em");
       modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
       		{	
           public void onClose(AjaxRequestTarget target) 
               { 
        	   smilesInchiKeyMultipleSmilesList = new ArrayList <String> ();  
        	   smilesInchiKeyMultipleSmilesList = getSmilesFromCompoundIdandSetTag(); 
        	   strMdlMultipleSmiles.setObject("");
        	   strMdlMultipleSmilesForInchi.setObject("");
        	   if (!StringUtils.isNullOrEmpty(getCompound().getInchiKey()))
        		   strMdlMultipleSmilesForInchi.setObject(smilesInchiKeyMultipleSmilesList.get(1));
        	   else if (!StringUtils.isNullOrEmpty(getCompound().getChem_abs_number()) && StringUtils.isNullOrEmpty(getCompound().getSmiles()))    		   
        	       strMdlMultipleSmiles.setObject(smilesInchiKeyMultipleSmilesList.get(1));
        	   target.add(msgMultipleSmiles);
        	   target.add(msgMultipleSmilesForInchi);
        	   target.add(cdp);
               }
       		});
       
       modal1.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
	        {
           public boolean onCloseButtonClicked(AjaxRequestTarget target)
	            {
	        	List<CompoundName> nList = compoundNameService.loadByCid(getCompound().getCid());
	        	setNames(nList);
	        	return true;
	            }
	        });
       
       return modal1;
	   }

   // issue 31 2020
   private void drawImage(Graphics2D graphics, String indicator) throws IOException 
	   {
	   Molecule mol;	   
	   mol = MolImporter.importMol(smilesInchiKeyMultipleSmilesList.get(0));
       Graphics2D g = graphics;//im.createGraphics();
       g.setColor(Color.white);
       g.fillRect(0, 0, 350, 350);
       g.setColor(Color.red);
       Rectangle r = new Rectangle(20, 20, 300, 300);
       g.draw(r);
       MolPrinter p = new MolPrinter(mol);
       p.setScale(p.maxScale(r)); // fit image in the rectangle
       p.paint(g, r);
	   }
   
   // issue 27 2020
   // issue 31 2020
   private final class StructureDynamicImageResource extends RenderedDynamicImageResource
		{
	    private String compoundID;
	    private String indicator;
		private StructureDynamicImageResource(int width, int height, String indicator)
			{
			super(width, height);
			this.compoundID = compoundID;
			this.indicator = indicator;
			// issue 36
			}
		
		@Override
		protected boolean render(Graphics2D graphics, Attributes attributes)
			{
			try {
				drawImage(graphics, indicator); 
				} 
			catch (IOException e) { e.printStackTrace(); }		
			return true;
			}
		}
     
	// Issue 27 2020
	public StructureDynamicImageResource getStructure()
		{		
		return (new StructureDynamicImageResource(350, 350, !StringUtils.isNullOrEmpty(getCompound().getInchiKey()) ? "inchikey" : (!StringUtils.isNullOrEmpty(getCompound().getSmiles()) ? "smiles" : "cas")) );	
		}

	private AjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1, final CompoundDetailPanel cdp) 
		{
		// issue 39
		return new AjaxLink <Void>(linkID)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				setModalDimensions(linkID, modal1);			    
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage() {   return setPage(linkID, modal1, cdp);   }
					});			
				modal1.show(target);
				}
			};
		}
	
    private Page setPage(String linkID, final ModalWindow modal1, final CompoundDetailPanel cdp)
		{
		switch(linkID)
			{
			case "addInv" : return new EditInventory(getPage(), getCmpId(), modal1);
			case "addName" : return new EditCompoundName(getPage(), getCmpId(), container, modal1);
			case "edit" : return new EditCompound(getPage(), getCompoundModel(getCmpId()), cdp, modal1);
			case "add" :    
			default :
				return new EditCompound(getPage(), cdp, modal1);
			}
		}
			
	private void setModalDimensions(String linkID, ModalWindow modal1)
		{
		switch(linkID)
			{
			case "addInv" :
				modal1.setInitialWidth(650);
				modal1.setInitialHeight(580); break;
			case "addName" :
			case "editName" :
				modal1.setInitialWidth(650);
				modal1.setInitialHeight(350); break;
			
			default : 
				modal1.setInitialWidth(750);
				modal1.setInitialHeight(540);
			}
		}
	
	public void updateCompound(String cid)
		{
		setCmpId(cid);
		setDefaultModel(new CompoundPropertyModel(getCompoundModel(cid)));
		}
	
	public List<CompoundName> getNames()
		{
		List<CompoundName> nList = compoundNameService.loadByCid(getCompound().getCid());
		return nList;
		}
	
	public void setNames(List<CompoundName> names)
		{
		this.names=names;
		}
	
	private List<CompoundName> names;
	
	public Compound getCompound()
		{
		return compoundService.loadCompoundById(getCmpId());
		}
	
	public String getCmpId()
		{
	    return cmpId;
		}
	
	public void setCmpId(String cmpId)
		{
	    this.cmpId = cmpId;
		}
	
	private String cmpId;
	 
	private AjaxLink getAjaxLinkForCid(String linkName, String labelName, final String cid, final CompoundDetailPanel cdp) 
		{
		// issue 39
		AjaxLink link= new AjaxLink <Void> (linkName) 
			{
			public void onClick(AjaxRequestTarget target) 
			    {
				// issue 36
				setCmpId(cid);
				getCompound();
				smilesInchiKeyMultipleSmilesList = getSmilesFromCompoundIdandSetTag();
				target.add(msgMultipleSmiles);
	        	target.add(msgMultipleSmilesForInchi);
				target.add(cdp);
			    }			
		    };
		link.add(new Label(labelName, cid));
		return link;
		}
	
	public AjaxLink buildAjaxLink(String linkName, final ModalWindow modal1, final Page targetPage) 
		{
		// issue 39
		AjaxLink link=new AjaxLink <Void> (linkName)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage()
						{
						return targetPage;
						}
					});
				modal1.show(target);
				}
			};
		return link;
		}
			
	private List<Compound> getParentageList()
		{
		List<Compound> parentageList=new ArrayList<Compound>();
		getChildren(getCompound(), parentageList);
		parentageList.add(getCompound());
		Compound tempCompound=compoundService.loadCompoundById(getCompound().getCid());
		getParents(tempCompound, parentageList);
		return parentageList;
		}
		
	public List<Compound> getParentsList()
		{
		List<Compound> parentsList = new ArrayList<Compound>();
		getParents(getCompound(), parentsList);
		return parentsList;
		}
	
	
	public List<Compound> getChildrenList()
		{
		List<Compound> childrenList = new ArrayList<Compound>();
		getChildren(getCompound(), childrenList);
		return childrenList;
		}
	
	private void getChildren(Compound c, List<Compound> parentageList)
		{
		List<Compound> cmpdList = compoundService.getChildren(c);
			for (Compound listItem : cmpdList)
				{
				parentageList.add(listItem);
				getChildren(listItem, parentageList);
				}
		}
		
	private void getParents(Compound c, List<Compound> parentageList)
		{
		while (c.getParent() != null && c.getCid() != c.getParent().getCid())
			{
			c=compoundService.loadCompoundById(c.getParent().getCid());
			parentageList.add(c);
			}
		}
	
	private Link buildInvLink() 
		{
		// issue 39
		Link link = new Link <Void>("goToInv") 
			{
			public void onClick() 
				{
				List<Inventory> invList = inventoryService.loadByCid(getCmpId());
				setResponsePage(new InventorySearch(getPage(), invList, getCompound()));
				}
			};
			// issue 52
			link.setPopupSettings(new PopupSettings(PopupSettings.RESIZABLE
			| PopupSettings.STATUS_BAR | PopupSettings.SCROLLBARS).setHeight(650).setWidth(800));
		return 	link;
		}
	
	// issue 31
	private List <String> getSmilesFromCompoundIdandSetTag()
	    {
		smilesInchiKeyMultipleSmilesList.clear();
		strMdlMultipleSmilesForInchi.setObject("");
		strMdlMultipleSmiles.setObject("");
		chem_abs_number_label.add(AttributeModifier.replace("style", "color: black;"));
  	    if (!StringUtils.isNullOrEmpty(getCompound().getInchiKey()) )
  	        {
  		    smilesInchiKeyMultipleSmilesList = CompoundIdUtils.grabSmilesFromCompoundId(getCompound().getInchiKey(), "inchiKey"); 
  		    strMdlMultipleSmilesForInchi.setObject(smilesInchiKeyMultipleSmilesList.get(1));
  	        }
  	    else if (!StringUtils.isNullOrEmpty(getCompound().getSmiles()) )
      	    {
  	    	// issue 36
  	    	smilesInchiKeyMultipleSmilesList.clear();
  		    smilesInchiKeyMultipleSmilesList.add(getCompound().getSmiles());
  		    smilesInchiKeyMultipleSmilesList.add("");
      	    }
  	    else 
  	        {
  		    smilesInchiKeyMultipleSmilesList = CompoundIdUtils.grabSmilesFromCompoundId(getCompound().getChem_abs_number(), "cas");	
  		    strMdlMultipleSmiles.setObject(smilesInchiKeyMultipleSmilesList.get(1));
  	        }
  	    return smilesInchiKeyMultipleSmilesList;
		}
	}
	
