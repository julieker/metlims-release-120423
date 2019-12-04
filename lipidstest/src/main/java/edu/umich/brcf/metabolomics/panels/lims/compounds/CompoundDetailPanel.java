
// Revisited : October 2016 (JW), Julie Keros (12/16)

package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import chemaxon.formats.MolImporter;
import chemaxon.marvin.MolPrinter;
import chemaxon.struc.Molecule;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.metabolomics.layers.service.CompoundNameService;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.CompoundDocument;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.util.widgets.MyFileLink;


public class CompoundDetailPanel extends Panel 
	{
	WebMarkupContainer container, filescontainer;
	ListView listView;
	
	@SpringBean
	CompoundNameService compoundNameService;
	
	@SpringBean 
	CompoundService compoundService;
	
	@SpringBean
	InventoryService inventoryService;

	
	private LoadableDetachableModel getCompoundModel(final String id) 
		{
		return new LoadableDetachableModel() 
			{
			protected Object load() {
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
		add(new Label("chem_abs_number",new PropertyModel(this, "compound.chem_abs_number")));
		add(new Label("smiles",new PropertyModel(this, "compound.smiles")));
		add(new Label("parent.cid",new PropertyModel(this, "compound.parent.cid")));
		add(new Label("logpAsDouble",new PropertyModel(this, "compound.logpAsDouble")));
		add(new Label("nominalMassAsDouble",new PropertyModel(this, "compound.nominalMassAsDouble")));
		add(new Label("pka",new PropertyModel(this, "compound.pka")));
		add(new Label("humanRel",new PropertyModel(this, "compound.humanRel")));
		add(new Label("solvent.name",new PropertyModel(this, "compound.solvent.name")));
		
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
//				listItem.add(buildEditNameLink(cn, modal1));
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
           public void onClose(AjaxRequestTarget target) {  target.add(cdp); }
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

   
   private void drawImage(Graphics2D graphics, String smiles) throws IOException 
	   {
    	Molecule mol = MolImporter.importMol(smiles);
       //BufferedImage im = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
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
   
   
   private final class StructureDynamicImageResource extends RenderedDynamicImageResource
		{
		private String smiles;
		private StructureDynamicImageResource(int width, int height, String smiles)
			{
			super(width, height);
			this.smiles=smiles;
			}
	
	
		@Override
		protected boolean render(Graphics2D graphics, Attributes attributes)
			{
			try {
				drawImage(graphics, smiles); 
				} 
			catch (IOException e) { e.printStackTrace(); }
		
			return true;
			}
		}
	
	public StructureDynamicImageResource getStructure()
		{
		return (new StructureDynamicImageResource(350, 350, getCompound().getSmiles()));	
		}
   
   ////////////////////
   
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
			public void onClick(AjaxRequestTarget target) {
				setCmpId(cid);
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
	
	
	private Link buildFileLink(final CompoundDocument doc) 
		{
		Link link = new MyFileLink("fileLink", new Model(doc)) ;
		link.add(new Label("fileName", doc.getFileName()));
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
			link.setPopupSettings(new PopupSettings(PopupSettings.RESIZABLE
			| PopupSettings.STATUS_BAR | PopupSettings.SCROLLBARS).setHeight(300).setWidth(800));
		return 	link;
		}
	}
	



















////////////////////////////   SCRAP

/*	

	
	private AjaxLink buildEditNameLink(final CompoundName cname, final ModalWindow modal1) {
		AjaxLink link=new AjaxLink("editName", new Model(cname))
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
            	 modal1.setPageCreator(new ModalWindow.PageCreator()
                 {
                     public Page createPage()
                     {
                         return new EditCompoundName(getPage(), new Model(cname), container, modal1);
                     }
                 });
            	 modal1.show(target);
            }
        };
		return link;
	}
	
}




//filescontainer = new WebMarkupContainer("fileListContainer");
//filescontainer.add(new ListView("docList") {
//	@Override
//	protected void populateItem(ListItem item) {
//		final CompoundDocument doc = (CompoundDocument) item.getModelObject();
//		item.add(buildFileLink(doc));
//		Link nmrLink;
//		item.add(nmrLink=new MyPopupLink("nmr", 300, 650) { //, new Model(new DocumentDTO()), null)
//			public void onClick() {
//				ArrayList<String> nmrs=null;
//				try {
//					if(doc.getFileName().endsWith("zip")){
//						nmrs=fractionationService.unZip(new ZipInputStream(new ByteArrayInputStream(doc.getContents())),fractionationService.getNMRUploadFolder());}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				setResponsePage(new NMRListPage(nmrs));
//			}
//		});
//		nmrLink.setVisible(doc.getFileName().endsWith("zip"));
//	}
//});
//filescontainer.setOutputMarkupId(true);
//add(filescontainer);
//fileUploadLink = new MyPopupLink("uploadFile", 250, 800) { //, new Model(new DocumentDTO()), null)
//	public void onClick() {
//		setResponsePage(new EditDocument(getPage(), getCmpId()));
//	}
//};
//fileUploadLink.add(new AjaxEventBehavior("onfocus") {
//       protected void onEvent(AjaxRequestTarget target) {
////    	   setSample(sampleService.loadById(getSample().getSampleID()));
//    	   target.add(filescontainer);
//       }
//   });
//add(fileUploadLink);
	

//  add(new AjaxLink("add")
// 	{
//     @Override
 //    public void onClick(AjaxRequestTarget target)
  //   	{
  //   	 modal1.setPageCreator(new ModalWindow.PageCreator()
  //   		 {
   //           public Page createPage()
   //          	 {
   //               return new EditCompound(getPage(), cdp, modal1);
   //          	 }
  //   		 });
  //       modal1.show(target);
  //   	}
// 	}.setVisible(insider));

//      Page pg = new EditCompound(modal1.getPage(), cdp, modal1);
//      add(buildAjaxLink("add", modal1, new EditCompound(getPage(), cdp, modal1)));


//private AjaxLink buildAddInvLink(final ModalWindow modal1) 
//{
//AjaxLink link=new AjaxLink("addInv")
//	{
//           @Override
//          public void onClick(AjaxRequestTarget target)
//        {
//        setModalDimensions("addInv", modal1);
//        
//    	modal1.setPageCreator(new ModalWindow.PageCreator()
//             {
//             public Page createPage()
//                 {
//                 return new EditInventory(getPage(), getCmpId(), modal1);
//                 }
//             });
//    	 modal1.show(target);
//        }
//       	};
//      	
//return link;
//}



//private AjaxLink buildAddNameLink(final ModalWindow modal1) {
//	AjaxLink link=new AjaxLink("addName")
//    {
//   @Override
//   public void onClick(AjaxRequestTarget target)
//  	{
//  	setModalDimensions("addName", modal1);
//  	
//  	 modal1.setPageCreator(new ModalWindow.PageCreator()
//      {
//         public Page createPage()
 //        {
//         return new EditCompoundName(getPage(), getCmpId(), container, modal1);
 //        }
 //    });
/// 	 modal1.show(target);
// 	}
//  };
//return link;

//private AjaxLink buildEditLink(final CompoundDetailPanel detailPanel, final ModalWindow modal1) {
//AjaxLink link=new AjaxLink("edit", getCompoundModel(getCmpId()))
//       {
//           @Override
//          public void onClick(AjaxRequestTarget target)
//          	{
//         	setModalDimensions("edit", modal1);
//         	
//         	modal1.setPageCreator(new ModalWindow.PageCreator()
//             {
//                 public Page createPage()
//                 {
//                    return new EditCompound(getPage(), getCompoundModel(getCmpId()), detailPanel, modal1);
//                 }
//             });
//        	 modal1.show(target);
//        }
//    };
//	return link;
//}


//add(buildAddInvLink(modal1).setVisible(insider));
//      add(buildAjaxLink("addInv", modal1, new EditInventory(getPage(), getCmpId(), modal1)));
// add(buildAddNameLink(modal1).setVisible(insider));
//       add(buildAjaxLink("addName", modal1, new EditCompoundName(getPage(), getCmpId(), container, modal1)));


//add(buildEditLink(this, modal1).setVisible(insider));
//      add(buildAjaxLink("edit", modal1, new EditCompound(getPage(), getCompoundModel(getCmpId()), cdp, modal1)));

//}



	
	private Link buildInvLink() {
		Link link = new Link("goToInv") {
			public void onClick() {
				List<Inventory> invList = inventoryService.loadByCid(getCmpId());
				setResponsePage(new InventorySearch(getPage(), invList, getCompound()));
			}
		};
		link.setPopupSettings(new PopupSettings(PopupSettings.RESIZABLE
				| PopupSettings.STATUS_BAR | PopupSettings.SCROLLBARS).setHeight(850).setWidth(800));
		return link;
	}
	

	private AjaxLink buildEditNameLink(final CompoundName cname, final ModalWindow modal1) {
		AjaxLink link=new AjaxLink("editName", new Model(cname))
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
            	 modal1.setPageCreator(new ModalWindow.PageCreator()
                 {
                     public Page createPage()
                     {
                         return new EditCompoundName(getPage(), new Model(cname), container, modal1);
                     }
                 });
            	 modal1.show(target);
            }
        };
		return link;
	}
	
	public AjaxLink buildAjaxLink(String linkName, final ModalWindow modal1, final Page targetPage) 
		{
		AjaxLink link=new AjaxLink(linkName)
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
	}




/*
 * 
 * 	
   public ModalWindow buildModalWindow(String linkId, final CompoundDetailPanel cdp)
	   {
	   ModalWindow modal1 = new ModalWindow(linkId);
	   
	   modal1.setWidthUnit("em");
       modal1.setHeightUnit("em");
       modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
       		{	
           public void onClose(AjaxRequestTarget target) {  target.add(cdp); }
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




	package edu.umich.brcf.metabolomics.panels.lims.compounds;

	import java.util.ArrayList;
	import java.util.List;

	import org.apache.wicket.Page;
	import org.apache.wicket.ajax.AjaxRequestTarget;
	import org.apache.wicket.ajax.markup.html.AjaxLink;
	import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
	import org.apache.wicket.markup.html.WebMarkupContainer;
	import org.apache.wicket.markup.html.basic.Label;

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

	import edu.umich.brcf.metabolomics.layers.domain.Compound;
	import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
	//import chemaxon.formats.MolImporter;
	//import chemaxon.marvin.MolPrinter;
	//import chemaxon.struc.Molecule;
	import edu.umich.brcf.metabolomics.layers.service.CompoundNameService;
	import edu.umich.brcf.metabolomics.layers.service.CompoundService;
	import edu.umich.brcf.metabolomics.layers.service.InventoryService;
	import edu.umich.brcf.shared.layers.domain.CompoundDocument;
	import edu.umich.brcf.shared.layers.domain.Inventory;
	import edu.umich.brcf.shared.util.widgets.MyFileLink;


	public class CompoundDetailPanel extends Panel 
		{
		WebMarkupContainer container, filescontainer;
		ListView listView;
		
		@SpringBean
		CompoundNameService compoundNameService;
		
		@SpringBean 
		CompoundService compoundService;
		
		@SpringBean
		InventoryService inventoryService;

		
		private LoadableDetachableModel getCompoundModel(final String id) {
			return new LoadableDetachableModel() {
				protected Object load() {
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
			add(new Label("chem_abs_number",new PropertyModel(this, "compound.chem_abs_number")));
			add(new Label("smiles",new PropertyModel(this, "compound.smiles")));
			add(new Label("parent.cid",new PropertyModel(this, "compound.parent.cid")));
			add(new Label("logpAsDouble",new PropertyModel(this, "compound.logpAsDouble")));
			add(new Label("nominalMassAsDouble",new PropertyModel(this, "compound.nominalMassAsDouble")));
			add(new Label("pka",new PropertyModel(this, "compound.pka")));
			add(new Label("humanRel",new PropertyModel(this, "compound.humanRel")));
			add(new Label("solvent.name",new PropertyModel(this, "compound.solvent.name")));
			
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

			//add(new NonCachingImage("structure", new PropertyModel(this, "structure")));
					
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
//					listItem.add(buildEditNameLink(cn, modal1));
					listItem.add(buildAjaxLink("editName", modal1, new EditCompoundName(getPage(), new Model(cn), container, modal1)).setVisible(insider));
					}
				});
			listView.setOutputMarkupId(true);
			}
		


	   // structure
	   
	   private AjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1, final CompoundDetailPanel cdp) 
			{
			return new AjaxLink(linkID)
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
						modal1.setInitialHeight(850); break;
					case "addName" :
					case "editName" :
						modal1.setInitialWidth(650);
						modal1.setInitialHeight(350); break;
					
					default : 
						modal1.setInitialWidth(750);
						modal1.setInitialHeight(540);
						
						
					}
				}
		
			/*
		private final class StructureDynamicImageResource extends RenderedDynamicImageResource
			{
			private String smiles;
			private StructureDynamicImageResource(int width, int height, String smiles)
				{
				super(width, height);
				this.smiles=smiles;
				}

			protected boolean render(Graphics2D graphics)
				{
				try {
					drawImage(graphics, smiles); 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			} 
//		}
		
		public RenderedDynamicImageResource getStructure()
			{
			return new RenderedDynamicImageResource(350, 350)
				{
				@Override
				protected boolean render(Graphics2D graphics, Attributes attributes)
					{
					return false;
					}
				};
			}

		
	@Override
	protected boolean render(Graphics2D graphics, Attributes attributes)
		{
		try {
		drawImage(graphics, smiles); 
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return true;
		} 
			
		public void updateCompound(String cid){
			setCmpId(cid);
			setDefaultModel(new CompoundPropertyModel(getCompoundModel(cid)));
		}
		
		public List<CompoundName> getNames(){
			List<CompoundName> nList = compoundNameService.loadByCid(getCompound().getCid());
			return nList;
		}
		
		public void setNames(List<CompoundName> names){
			this.names=names;
		}
		private List<CompoundName> names;
		
		public Compound getCompound(){
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
		 
		private AjaxLink getAjaxLinkForCid(String linkName, String labelName, final String cid, final CompoundDetailPanel cdp) {
				AjaxLink link= new AjaxLink(linkName) {
					public void onClick(AjaxRequestTarget target) {
						setCmpId(cid);
						target.add(cdp);
					}
				};
				link.add(new Label(labelName, cid));
				return link;
		}
		
		private Link buildFileLink(final CompoundDocument doc) {
			Link link = new MyFileLink("fileLink", new Model(doc)) ;
			link.add(new Label("fileName", doc.getFileName()));
			return link;
		}
		
		private List<Compound> getParentageList(){
			List<Compound> parentageList=new ArrayList<Compound>();
			getChildren(getCompound(), parentageList);
//				List<Compound> parentageList = children;
			parentageList.add(getCompound());
			Compound tempCompound=compoundService.loadCompoundById(getCompound().getCid());
			getParents(tempCompound, parentageList);
			return parentageList;
			
		}
		
		public List<Compound> getParentsList(){
			List<Compound> parentsList = new ArrayList<Compound>();
			getParents(getCompound(), parentsList);
			return parentsList;
		}
		
		public List<Compound> getChildrenList(){
			List<Compound> childrenList = new ArrayList<Compound>();
			getChildren(getCompound(), childrenList);
			return childrenList;
		}
		
		private void getChildren(Compound c, List<Compound> parentageList){
			List<Compound> cmpdList = compoundService.getChildren(c);
				for (Compound listItem : cmpdList){
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
			
		/*
		private void drawImage(Graphics2D graphics, String smiles) throws IOException {
	        Molecule mol = MolImporter.importMol(smiles);
	        //BufferedImage im = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
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
	    
	    
	    	/*
	private void drawImage(Graphics2D graphics, String smiles) throws IOException {
        Molecule mol = MolImporter.importMol(smiles);
        //BufferedImage im = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
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
	*/
/*
	
/*	protected boolean render(Graphics2D graphics)
		{
			try {
				drawImage(graphics, smiles); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		} */
		


