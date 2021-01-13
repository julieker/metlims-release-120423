package edu.umich.brcf.metabolomics.panels.lims.mixtures;

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
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.metabolomics.layers.service.CompoundNameService;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.metabolomics.panels.lims.compounds.EditAliquot;
import edu.umich.brcf.metabolomics.panels.lims.compounds.InventoryDetailPanel;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.MixtureAliquotPK;
import edu.umich.brcf.shared.layers.domain.MixtureChildren;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenPK;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.UserService;

public class MixtureAliquotDetail extends WebPage
	{
	@SpringBean
	CompoundService compoundService;
	
	// issue 61
	@SpringBean 
	AliquotService aliquotService;
	@SpringBean 
	MixtureService mixtureService;
	@SpringBean 
	InventoryService inventoryService;
	@SpringBean
	UserService userService;
	@SpringBean
	CompoundNameService compoundNameService;
	Mixture mixture;
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;
	ListView listViewAliquots; // issue 61
	ListView listViewMixtures; // issue 110
	MixtureAliquotDetail MixtureAliquotDetail = this;
	// itemList
	public MixtureAliquotDetail(String id,  Mixture mix) 
		{
		final ModalWindow modal2= new ModalWindow("modal2");
		setMixture(mix);
		modal2.setInitialWidth(650);
        modal2.setInitialHeight(450);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em");     
        add(new Label("titleLabel", "View Details for Mixture:" + getMixture().getMixtureId()));
        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	}
        	});
        add(modal2);		
		///// issue 61
		add(listViewAliquots = new ListView("aliquotIds", new PropertyModel(this, "aliquotIds")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final Aliquot alq = (Aliquot) listItem.getModelObject();	
			    MixtureAliquotPK mixtureAliquotPK   = MixtureAliquotPK.instance(mixture, alq);	
			    MixtureAliquot mixtureAliquot = mixtureService.loadMixtureAliquotById(mixtureAliquotPK);				
			    listItem.add(buildAliquotAjaxLink("aliquotLink", alq, modal2 ));
			    listItem.add(new Label("aliquotName", new Model(getCompoundName(alq))));
				listItem.add(new Label("aliquotVolume", new Model(mixtureAliquot.getVolumeAliquot())));
				listItem.add(new Label("aliquotConcentrate", new Model(mixtureAliquot.getConcentrationAliquot())));				
				}
			});
		add(listViewMixtures = new ListView("childrenMixtureIds", new PropertyModel(this, "childrenMixtureIds")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final Mixture mix = (Mixture) listItem.getModelObject();	
			    MixtureChildrenPK mixtureChildrenPK   = MixtureChildrenPK.instance(mix, mixture );	
			    MixtureChildren mixtureChildren = mixtureService.loadMixtureChildrenById(mixtureChildrenPK);				
			    listItem.add(new Label("mixtureId", new Model(mixtureChildren.getMixture().getMixtureId())));
				listItem.add(new Label("mixtureVolume", new Model(mixtureChildren.getVolumeMixture())));
				listItem.add(new Label("mixtureConcentrate", new Model(mixtureChildren.getConcentrationMixture())));				
				}
			});	
		container = new WebMarkupContainer("itemList");
		container.setOutputMarkupId(true);
		// add back jak container.add(listView);
		add(container);
        }
		
	private AjaxLink buildAliquotAjaxLink(String id, final Aliquot aliquot, final ModalWindow modal2)
		{
		AjaxLink link;		
		// Issue 237
		// issue 39
		Compound cmpd  = aliquot.getCompound();
		final InventoryDetailPanel idp = new InventoryDetailPanel ("inventory", cmpd);
	    link =  new AjaxLink <Void>(id)
			{
			@Override
			public void onClick(AjaxRequestTarget target) 
				{
				try
					{
					modal2.setInitialHeight(900);
					modal2.setInitialWidth(1100);
					modal2.setPageCreator(new ModalWindow.PageCreator()
						{
						public Page createPage()
							{
													
							return new EditAliquot(getPage(), new Model <Aliquot> (aliquot),idp, modal2, true);
							}
						});
					//issue 239
					modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
					    {
					    @Override
					    public void onClose(AjaxRequestTarget target)  
					        {    
					    	target.add(modal2.getParent());				    	
					        }
					    });
					modal2.show(target);
					}
				catch (Exception e) {  }
				}
			};
	    link.add(new Label("aliquotid", aliquot.getAliquotId()));
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
			
	public Mixture getMixture()
		{
		return mixture;
		}
	
	public void setMixture(Mixture mixture)
		{
		this.mixture=mixture;
		}
	
	// issue 110 
	public List<Mixture> getChildrenMixtureIds()
		{
		List<Mixture> nList = mixtureService.mixtureChildrenForMixtureId(mixture.getMixtureId());
		return nList;
		}
	
	
	public List<Aliquot> getAliquotIds()
		{
		List<Aliquot> nList = aliquotService.aliquotIdsForMixtureId(mixture.getMixtureId());
		return nList;
		}
	
	public String getCompoundName(Aliquot alq)
		{
		List<CompoundName> cnList = compoundNameService.loadByCid(alq.getCompound().getCid());
		for (CompoundName cname : cnList)
			{
			if (cname.getNameType().equals("pri"))
			    return cname.getName().substring(0, (cname.getName().length() >=50 ? 50 : cname.getName().length() ) );
			}
		return "";
		}
	}
