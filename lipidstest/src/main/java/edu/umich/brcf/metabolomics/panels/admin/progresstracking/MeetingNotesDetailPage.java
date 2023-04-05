/*********************************
 * 
 * Created by Julie Keros Dec 1 2020
 */
package edu.umich.brcf.metabolomics.panels.admin.progresstracking;


import java.util.List;
import org.apache.commons.text.WordUtils;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
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
import edu.umich.brcf.shared.layers.domain.MeetingNotes;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.MeetingNotesService;
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;

public class MeetingNotesDetailPage extends WebPage
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
	@SpringBean
	MeetingNotesService meetingNotesService;
	
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;
	ListView listViewProgressTracking; // issue 61
	MeetingNotesDetailPage MeetingNotesDetailPage = this;
	EditMeetingNotes editMeetingNotes;
	// itemList
	// issue 118
;
	
	public MeetingNotesDetailPage(final String id) 
		{
	   // super(id);
		final ModalWindow modal2= new ModalWindow("modal2");
		modal2.setInitialWidth(1500);
        modal2.setInitialHeight(600);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em");     
        add(new Label("titleLabel", "View Meeting Notes"));
        add(buildLinkToEditMeeting("addMeetingNote", null,modal2));
        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	//System.out.println("on close");
            	target.add(MeetingNotesDetailPage);
            	}
        	}); 
        add(modal2);
        
		///// issue 94
		//// issue 120
        add(listViewProgressTracking = new ListView("meetings", new PropertyModel(this, "meetings")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final MeetingNotes meetingNotes = (MeetingNotes) listItem.getModelObject();		
				listItem.add(new Label("noteID", new Model(meetingNotes.getNoteID())));
				listItem.add(new Label("dateEntered", new Model( meetingNotes.convertToDateString( meetingNotes.getDateEntered() ))));     
				listItem.add(new MultiLineLabel("notes", new Model(WordUtils.wrap( meetingNotes.getNotes(), 90, "\n", true))));
				listItem.add(new Label("createdBy", new Model( meetingNotes.getCreatedBy().getFullName())));
				listItem.add(buildLinkToEditMeeting("editMeetingNotes", meetingNotes,modal2));				
				}
			});					
        }
	

	// issue 94	
	private AjaxLink buildLinkToEditMeeting(final String id, final MeetingNotes mn, final ModalWindow modal1 ) 
		{
		// issue 39
		 AjaxLink lnk =  new AjaxLink<Void> (id)
			{
		//	@Override 
			/*public boolean isEnabled()
				{
				return ! (id.equals("editMixture") && mixtureService.isMixturesSecondaryMixture(mix.getMixtureId()));
				}*/
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				setModalDimensions(id, modal1);
				 modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage() {   return setPage(id, modal1, mn);   }
					});			
				    modal1.show(target); 
				}
			};
		return lnk;
		} 
	
	 private Page setPage(String linkID, final ModalWindow modal1, MeetingNotes mn)
		{
		switch(linkID)
		    {
		   
			//case "detailMixture" : return new MixtureAliquotDetail (linkID, mix);
			case "editMeetingNotes"   : editMeetingNotes = new EditMeetingNotes (getPage(), new Model <MeetingNotes> (mn), modal1, false);
			                         return editMeetingNotes;
			case "addMeetingNote" :    editMeetingNotes =  new EditMeetingNotes (getPage(), modal1);                   
			       
			//default :              return new MixtureAliquotDetail (linkID, mix);
			}
		return editMeetingNotes;
		} 
	
	
	private void setModalDimensions(String linkID, ModalWindow modal1)
		{ 
		modal1.setInitialWidth(1500);
		modal1.setInitialHeight(790);
		}
	
	public List<MeetingNotes> getMeetings()
		{
		List<MeetingNotes> meetingList = meetingNotesService.loadAllMeetings();
		return meetingList;
		}
	
	
	
	
	
	
	
	}
