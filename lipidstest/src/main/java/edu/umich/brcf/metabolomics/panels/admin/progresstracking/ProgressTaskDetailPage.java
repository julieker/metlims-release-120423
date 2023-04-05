/*********************************
 * 
 * Created by Julie Keros Dec 1 2020
 */
package edu.umich.brcf.metabolomics.panels.admin.progresstracking;


import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixtureAliquotDetail;
import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixturesAdd;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;

public class ProgressTaskDetailPage extends WebPage
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
	ProcessTrackingService processTrackingService;
	
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;
	ListView listViewTasks; // issue 61
	ProgressTaskDetailPage ProgressTaskDetailPage = this;
	EditProcessTaskDetail editProcessTaskDetail;
	// itemList
	// issue 118
;
	
	public ProgressTaskDetailPage(final String id) 
		{
	   // super(id);
		final ModalWindow modal2= new ModalWindow("modal2");
		modal2.setInitialWidth(750);
        modal2.setInitialHeight(600);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em"); 
        add(buildLinkToEditTask("addTask", null,modal2));
        add(new Label("titleLabel", "View Tasks"));
        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	//System.out.println("on close");
            	target.add(ProgressTaskDetailPage);
            	}
        	}); 
        add(modal2);
        
		///// issue 94
		//// issue 120
        add(listViewTasks = new ListView("trackingDetail", new PropertyModel(this, "trackingDetail")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final ProcessTracking procTrac = (ProcessTracking) listItem.getModelObject();		
				listItem.add(new Label("taskdescription", new Model(procTrac.getTaskDesc())));
				listItem.add(buildLinkToEditTask("editTask",procTrac,modal2));	
				}
			});			
	
		
		// add mixture issue 123
		
        }
	

	// issue 94	
	private AjaxLink buildLinkToEditTask(final String id, final ProcessTracking pt, final ModalWindow modal1 ) 
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
					public Page createPage() {   return setPage(id, modal1, pt);   }
					});	
				    modal1.show(target); 
				}
			};
		return lnk;
		} 
	     
	 private Page setPage(String linkID, final ModalWindow modal1, ProcessTracking pt)
		{
		switch(linkID)
			{
			case "editTask"   :   editProcessTaskDetail = new EditProcessTaskDetail (getPage(), new Model <ProcessTracking> (pt), modal1, false);
			                      return editProcessTaskDetail;
		
			//case "editTrackingAdmin"   : editProcessTrackingDetail = new EditProcessTrackingDetail (getPage(), new Model <ProcessTrackingDetails> (ptd), modal1, false);
			 //                        return editProcessTrackingDetail;
			case "addTask"  : editProcessTaskDetail =  new EditProcessTaskDetail (getPage(), modal1);  
				                      return editProcessTaskDetail;
			//default :              return new MixtureAliquotDetail (linkID, mix);
			}
		return editProcessTaskDetail;
		} 
	
	
	private void setModalDimensions(String linkID, ModalWindow modal1)
		{ 
		modal1.setInitialWidth(1500);
		modal1.setInitialHeight(790);
		}
	

	
	public List<ProcessTracking> getTrackingDetail()
		{
		List<ProcessTracking> nList = processTrackingService.loadAllTasks();
		return nList;
		}
	
	
	
	
	
	
	
	}
