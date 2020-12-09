/*********************************
 * 
 * Created by Julie Keros Dec 1 2020
 */
package edu.umich.brcf.metabolomics.panels.lims.mixtures;


import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.UserService;

public class MixtureDetailPanel extends Panel
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
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;
	ListView listViewMixtures; // issue 61
	MixtureDetailPanel MixtureDetailPanel = this;
	// itemList
	public MixtureDetailPanel(String id) 
		{
	    super(id);
		final ModalWindow modal2= new ModalWindow("modal2");
		modal2.setInitialWidth(650);
        modal2.setInitialHeight(450);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em");     
        add(new Label("titleLabel", "View Mixtures"));
        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	}
        	}); 
        add(modal2);
        
		///// issue 94
		add(listViewMixtures = new ListView("mixtureDetail", new PropertyModel(this, "mixtureList")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final Mixture mixture = (Mixture) listItem.getModelObject();		
				listItem.add(new Label("mixtureId", new Model(mixture.getMixtureId())));
				listItem.add(new Label("createDate", new Model( mixture.getCreateDateString())));
				listItem.add(new Label("createdBy", new Model(userService.getFullNameByUserId(mixture.getCreatedBy().getId()))));
				listItem.add(new Label("volumeSolvent", new Model(mixture.getVolSolvent())));
				listItem.add(new Label("desiredFinalVolume", new Model(mixture.getDesiredFinalVol())));
				listItem.add(buildLinkToModalMixtureDetail("detailMixture",mixture,modal2));			
				}
			});			
		container = new WebMarkupContainer("itemList");
		container.setOutputMarkupId(true);
		// add back jak container.add(listView);
		add(container);
        }
	// issue 94	
	private AjaxLink buildLinkToModalMixtureDetail(final String id, final Mixture mix, final ModalWindow modal1 ) 
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
					public Page createPage() {   return setPage(id, modal1, mix);   }
					});			
				    modal1.show(target);
				}
			};
		return lnk;
		}
	
	private Page setPage(String linkID, final ModalWindow modal1, Mixture mix)
		{
		switch(linkID)
			{
			case "detailMixture" : return new MixtureAliquotDetail (linkID, mix);
			default :              return new MixtureAliquotDetail (linkID, mix);
			}
		}
	
	private void setModalDimensions(String linkID, ModalWindow modal1)
		{ 
		modal1.setInitialWidth(1000);
		modal1.setInitialHeight(580);
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
		
	public List<Mixture> getMixtureList()
		{
		List<Mixture> nList = mixtureService.loadAllMixtures();
		return nList;
		}
	
	}
