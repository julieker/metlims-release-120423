package edu.umich.brcf.metabolomics.panels.lims.mixtures;

import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.MixtureAliquotPK;
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
	Mixture mixture;
	ListView listView;
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;
	ListView listViewAliquots; // issue 61
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
			    listItem.add(new Label("aliquotId", new Model(alq.getAliquotId())));
				listItem.add(new Label("aliquotVolume", new Model(mixtureAliquot.getVolumeAliquot())));
				listItem.add(new Label("aliquotConcentrate", new Model(mixtureAliquot.getConcentrationAliquot())));				
				}
			});			
		container = new WebMarkupContainer("itemList");
		container.setOutputMarkupId(true);
		// add back jak container.add(listView);
		add(container);
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
	
	public List<Aliquot> getAliquotIds()
		{
		List<Aliquot> nList = aliquotService.aliquotIdsForMixtureId(mixture.getMixtureId());
		return nList;
		}
	}
