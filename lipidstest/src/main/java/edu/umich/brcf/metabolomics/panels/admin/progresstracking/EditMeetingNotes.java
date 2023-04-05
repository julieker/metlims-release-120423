////////////////////////////////////////////////////
// EditMeetingNotes.java
// 
// Created by Julie Keros June 1st, 2020
////////////////////////////////////////////////////

// issue 61
package edu.umich.brcf.metabolomics.panels.admin.progresstracking;

/*****************
 * Created by Julie Keros
 * Aug 20 2022
 * For Progress tracking
 ********************/

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.MeetingNotes;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.LocationService;
import edu.umich.brcf.shared.layers.service.MeetingNotesService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;
import edu.umich.brcf.shared.layers.dto.MeetingNotesDTO;
public class EditMeetingNotes extends WebPage
	{
	@SpringBean
	AliquotService aliquotService;	
	@SpringBean
	AssayService assayService;	
	@SpringBean
	LocationService locationService;
	@SpringBean
	InventoryService inventoryService;
	@SpringBean
	UserService userService;
	@SpringBean
	ProcessTrackingService processTrackingService;
	@SpringBean
	MeetingNotesService meetingNotesService;
	@SpringBean
	CompoundService compoundService;
	// issue 79
	FeedbackPanel aFeedback;
	
	Label noteIDLabel;
	Label dateEnteredLabel;
	Label notesLabel;
	Label createdByLabel;
	
	TextArea textAreaNotes;
	
	DropDownChoice<String> aliquotNeatOrDilutionDD;
	DropDownChoice<String> aliquotNeatOrDilutionUnitsDD;
	DropDownChoice<String> selectedParentInventroyDrop;	
	DropDownChoice<String> aliquotUnitDD;
	DropDownChoice<String> locationsDD;
	DropDownChoice<String> userNamesDD;
	List<String> userNamesChoices = new ArrayList<String>();		
	DropDownChoice<String> solventDD;
	DropDownChoice<String> dConcentrationUnitsDD;
	DropDownChoice<String> weightedAmountUnitsDD;
	AjaxCheckBox dryCheckBox;
	EditMeetingNotes editMeetingNotes = this;// issue 61 2020
	MeetingNotesDTO meetingNotesDTO = new MeetingNotesDTO();
	Button calculateNeatButton;
	Button calculateDilutionButton;
	Button saveChangesButton;
	WebMarkupContainer dilutionContainer;
	int maxSolventLength = 94; // issue 79 allow for OTHER:	
	boolean calculateOnly = true;
	ListMultipleChoice<String> selectedAssays;
	boolean isAssayListUpdated = false;
	EditMeetingNotesForm editMeetingNotesForm;
	
	public MeetingNotesDTO getMeetingNotesDTO() { return meetingNotesDTO; }
	public void setMeetingNotesDTO(MeetingNotesDTO meetingNotesDTO)  { this.meetingNotesDTO = meetingNotesDTO; }
		
	public EditMeetingNotes(Page backPage, final ModalWindow window) 
		{		
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);		
		add(aFeedback);		
		add(new Label("titleLabel", "Add Meeting Note"));
		add(editMeetingNotesForm = new EditMeetingNotesForm("editMeetingNotesForm", "to be assigned", meetingNotesDTO,  window,editMeetingNotes, null ));
		}
		
	public EditMeetingNotes(Page backPage, IModel cmpModel, final ModalWindow window) 
		{
		this (backPage,cmpModel,window, false);
		}
	
	public EditMeetingNotes(Page backPage, IModel cmpModel, final ModalWindow window, boolean isViewOnly) 
		{
		MeetingNotes mn = (MeetingNotes) cmpModel.getObject();	
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);		
		add(aFeedback);	
		add(new Label("titleLabel",  "Edit Progress Tracking Detail"));
		setMeetingNotesDTO(MeetingNotesDTO.instance(mn));
		// issue 196
		add(editMeetingNotesForm = new EditMeetingNotesForm("editMeetingNotesForm", mn.getNoteID(), meetingNotesDTO,  window, editMeetingNotes, mn, isViewOnly));
		}
	
	public final class EditMeetingNotesForm extends Form 
		{
		DropDownChoice selectedParentInventoryDD;
		String aliquotIdAssigned = "";	
		String unit= "";
		boolean isNoInventory = false; // issue 196
		AjaxCheckBox isNoInventoryCheckBox; // issue 196
		public EditMeetingNotesForm(final String id, final String notesID, final MeetingNotesDTO meetingNotesDTO,   final ModalWindow window, final EditMeetingNotes editMeetingNotes, final MeetingNotes mn) // issue 27 2020
			{
			this (id, notesID, meetingNotesDTO, window, editMeetingNotes, mn, false ) ;
			}
		
		public EditMeetingNotesForm(final String id, final String notesID, final MeetingNotesDTO meetingNotesDTO,   final ModalWindow window, final EditMeetingNotes editMeetingNotes, final MeetingNotes mn, boolean viewOnly ) // issue 27 2020
			{
			super(id, new CompoundPropertyModel(meetingNotesDTO));
			noteIDLabel = new Label("noteID");
			add(noteIDLabel);
			textAreaNotes = new TextArea("notes");
			textAreaNotes.add(StringValidator.maximumLength(4000));
			add(textAreaNotes);	
			textAreaNotes.setRequired(true);
			if (mn != null)
				setValuesForEdit(mn);	
			
			
			METWorksAjaxUpdatingDateTextField dateFld =  new METWorksAjaxUpdatingDateTextField("dateEntered", new PropertyModel<String>(meetingNotesDTO, "dateEntered"), "dateEntered")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)  
			        { 
			        }
				};		
				dateFld.setDefaultStringFormat(ProcessTracking.ProcessTracking_DATE_FORMAT);
				add(dateFld);
				dateFld.setRequired(true);

			
			
			userNamesDD= new DropDownChoice("createdBy",    userNamesChoices)
			    {
				
				}
				;			
			userNamesDD.setOutputMarkupId(true);
			add(userNamesDD);
			//userNamesDD.setChoices(userService.allUserNames());	
			userNamesDD.setChoices(userService.allAdminNames(false));
			userNamesDD.setRequired(true);
			
			add( new AjaxLink<Void>("close")
				{
				public void onClick(AjaxRequestTarget target)
					{ 
					window.close(target);
					}
				});		
			
			saveChangesButton = new Button("saveChanges")
				{
				@Override
		
				public void onSubmit() 
					{	
					try
						{
						User userAssignedTo;
						userAssignedTo = userService.loadUserByFullName(meetingNotesDTO.getCreatedBy());
					    MeetingNotes meetingNotes = meetingNotesService.save(meetingNotesDTO, userAssignedTo);
						
						if (meetingNotes.getNoteID() != null && (meetingNotesDTO.getNoteID()== null || meetingNotesDTO.getNoteID().equals("to be assigned")))
							{
							meetingNotesDTO.setNoteID(meetingNotes.getNoteID());
							}
					    
					    
					    String msg = "<span style=\"color:blue;\">" +   "Meeting note saved for ID: " + meetingNotes.getNoteID() +  "." + "</span>";	
						EditMeetingNotes.this.info(msg);
						/********************************************/
						}
					catch (RuntimeException r)
						{
						r.printStackTrace();
						String msg = "<span style=\"color:red;\">" +   "There was an issue saving the note" + "</span>";	
						EditMeetingNotes.this.info(msg);
						}
					
					catch (Exception e)
						{
						e.printStackTrace();
						String msg = "<span style=\"color:red;\">" +   "There was an issue saving the note: " + e.getMessage() +  "</span>";
						}
					    
					}				
				public void onError(AjaxRequestTarget target, Form form)
					{
					target.add(EditMeetingNotes.this.get("feedback")); 
					} 
				};	
			add(saveChangesButton);			
			}
		
		public void setValuesForEdit (MeetingNotes  mn)
			{			
			meetingNotesDTO.setNotes(mn.getNotes());
			meetingNotesDTO.setDateEntered(mn.convertToDateString(mn.getDateEntered()));			
			meetingNotesDTO.setCreatedBy(mn.getCreatedBy().getFullNameByLast());
			}
		
		}////// END FORM
	       
	}
