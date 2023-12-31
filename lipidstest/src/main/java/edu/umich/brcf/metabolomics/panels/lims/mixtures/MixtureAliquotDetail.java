package edu.umich.brcf.metabolomics.panels.lims.mixtures;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
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
import edu.umich.brcf.shared.layers.domain.MixtureChildrenAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenAliquotPK;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenPK;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

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
	Mixture mix;
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	Map<String, Boolean> isExpandedMap = new HashMap <String, Boolean> ();
	List<Compound> parentageList;
	ListView listViewAliquots; // issue 61
	ListView listViewMixtures; // issue 110
	ListView listViewAliquotsOfMixtures; // issue 123
	boolean isExpand = false;
	MixtureInfo mixtureInfo;
	MixAliquotInfo mixAliquotInfo;
	String gMixId = null;
	IModel<String> labelModel;
	Label label;
	
	
	MixtureAliquotDetail mixtureAliquotDetail = this;
	// itemList
	// issue 118
	IModel <List<Mixture>> mixtureChildrenModel = new LoadableDetachableModel() 
		{
		protected Object load() { return mixtureService.mixtureChildrenForMixtureId(mixture.getMixtureId());}
		}	;
	IModel <List<Aliquot>> aliquotModel = new LoadableDetachableModel() 
		{
		protected Object load() { return aliquotService.aliquotIdsForMixtureId(mixture.getMixtureId());}
		}	;
	public MixtureAliquotDetail(String id,  final Mixture mix) 
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
        ///// issue 118
		add(listViewAliquots = new ListView("aliquotIds", aliquotModel) 
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
		
		add(listViewMixtures = new ListView("childrenMixtureIds", mixtureChildrenModel) 
			{
			Link lnk;
			private  Link<?> buildExpandMixtureButton(String id, final String mixId, final MixtureInfo mixtureInfo)    
				{
				lnk  = new Link<Object>(id)
				    {
				    @Override
				    public void onClick()
				    	{
				    	gMixId = mixId;
				    	// issue 123
				    	Iterator it = isExpandedMap.entrySet().iterator(); 
				    	while (it.hasNext()) 
							{ 
						    Map.Entry pairs = (Map.Entry)it.next(); 
						    if ( pairs.getKey().equals(mixId))
						        {
						    	isExpandedMap.put(mixId, !(Boolean) pairs.getValue());
						    	mixtureInfo.setExpandText(isExpandedMap.get(mixId) ? "-" : "+");
						    	break;
						        }						  
							} 
				    	}
				    @Override
					protected void onComponentTag(ComponentTag tag)
						{
						super.onComponentTag(tag);	
						if ( isExpandedMap == null || isExpandedMap.isEmpty() || mix == null || StringUtils.isEmptyOrNull(mix.getMixtureId()))
						 	tag.put("value", "+");
						else 
							{
							if (StringUtils.isEmptyOrNull(gMixId))
								tag.put("value",  "+");
							else 
								{
								String label = isExpandedMap.get(gMixId) ? "-" : "+";	
								}
							}
						}
				    };
				    labelModel = Model.of("+");
				    label = new Label("labelId", labelModel);
					label.setOutputMarkupId(true);
				    return lnk;
			    }
				    				      
			public void populateItem(final ListItem listItem) 
				{				
				final Mixture mix = (Mixture) listItem.getModelObject();
				if (isExpandedMap.isEmpty() || ! isExpandedMap.containsKey(mix.getMixtureId()))
					{
					isExpandedMap.put(mix.getMixtureId(), false);
					}				
			    MixtureChildrenPK mixtureChildrenPK   = MixtureChildrenPK.instance(mix, mixture );	
			    MixtureChildren mixtureChildren = mixtureService.loadMixtureChildrenById(mixtureChildrenPK);				
			    listItem.add(new Label("mixtureId", new Model(mixtureChildren.getMixture().getMixtureId())));		   
			    listItem.add(new Label("mixtureName", new Model(mixtureChildren.getMixture().getMixtureName())));// issue 118
				listItem.add(new Label("mixtureVolume", new Model(mixtureChildren.getVolumeMixture())));			
				mixtureInfo = new MixtureInfo();
				mixtureInfo.setMixtureId(mix.getMixtureId());
				mixtureInfo.setListObject(mixtureService.aliquotsForMixtureId(mix.getMixtureId()));	
				List<MixAliquotInfo> mAliquotList   = mixtureInfo.getMAliquotList();
			    mixtureInfo.setMAliquotList(mAliquotList);
			    
			    MixtureInfo mxInfo = mixtureInfo;
			    mxInfo.setExpandText(isExpandedMap.get(mxInfo.getMixtureId()) ? "-" : "+");
			    listItem.add(buildExpandMixtureButton("expandMixtureButton", mix.getMixtureId(), mxInfo));
				lnk.add(new Label("linktext", new PropertyModel<String>(mxInfo, "expandText")));
				// issue 123
				listItem.add(new Label("labelAliquotId", new Model("Aliquot Id"))				
				    {
					@Override
					public boolean isVisible()
						{
						if (isExpandedMap.size() == 0)
						   return false;
						if (StringUtils.isEmptyOrNull(gMixId))
						   return false;		
						return isExpandedMap.get(mix.getMixtureId());
						}
				    });
				listItem.add(new Label("labelAliquotName", new Model("Aliquot Name"))				
				    {
					@Override
					public boolean isVisible()
						{
						if (isExpandedMap.size() == 0)
						   return false;
						if (StringUtils.isEmptyOrNull(gMixId))
						   return false;		
						return isExpandedMap.get(mix.getMixtureId());
						}
				    });
				listItem.add(new Label("labelAliquotConcentrate", new Model("Aliquot Concentrate"))				
				    {
					@Override
					public boolean isVisible()
						{
						if (isExpandedMap.size() == 0)
						   return false;
						if (StringUtils.isEmptyOrNull(gMixId))
						   return false;		
						return isExpandedMap.get(mix.getMixtureId());
						}
				    });
				
				listItem.add(listViewAliquotsOfMixtures =  new ListView<MixAliquotInfo>("aliquotMixtureInfo", new PropertyModel(mixtureInfo, "mAliquotList"))
				    {
					public void populateItem(final ListItem listItema) 
						{
						mixAliquotInfo = (MixAliquotInfo) listItema.getModelObject();
						String aliquotIdStr = mixAliquotInfo.getAliquotId();						
						Aliquot lAliquot = aliquotService.loadByIdForMixture(aliquotIdStr);
						// issue 123
                        MixtureChildrenAliquotPK mixtureChildrenAliquotPK = MixtureChildrenAliquotPK.instance(mix, mixture, lAliquot );	
                        MixtureChildrenAliquot mixtureChildrenAliquot = mixtureService.loadMixtureChildrenAliquotById(mixtureChildrenAliquotPK);
                        String aliquotConcentrationStr = mixtureChildrenAliquot.getConcentrationFinal().toString();
						String cid = aliquotService.getCompoundIdFromAliquot(aliquotIdStr);
						listItema.add(new Label("aliquotChildId", new Model(aliquotIdStr))
						   {
							@Override
							public boolean isVisible()
								{
								if (isExpandedMap.size() == 0)
								   return false;
								if (StringUtils.isEmptyOrNull(gMixId))
								   return false;		
								return isExpandedMap.get(mix.getMixtureId());
								}
						   });
						listItema.add(new Label("aliquotChildName", new Model
								(compoundNameService.getCompoundName(cid)))
						
						   {
							@Override
							public boolean isVisible()
								{
								if (isExpandedMap.size() == 0)
								   return false;
								if (StringUtils.isEmptyOrNull(gMixId))
								   return false;		
								return isExpandedMap.get(mix.getMixtureId());
								}
						   });
						
						listItema.add(new Label("aliquotFinalConcentration", new Model
								(aliquotConcentrationStr))
						
						   {
							@Override
							public boolean isVisible()
								{
								if (isExpandedMap.size() == 0)
								   return false;
								if (StringUtils.isEmptyOrNull(gMixId))
								   return false;		
								return isExpandedMap.get(mix.getMixtureId());
								}
						   });						
						} 
				    });
				}
			});	
		container = new WebMarkupContainer("itemList");
		container.setOutputMarkupId(true);
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
				catch (Exception e) { e.printStackTrace(); }
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
