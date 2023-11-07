////////////////////////////////////////////////////
// EditProcessTrackingDetailDialog.java
// 
// Created by Julie Keros June 1st, 2020
////////////////////////////////////////////////////

// issue 61
package edu.umich.brcf.metabolomics.panels.admin.progresstracking;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;

/*****************
 * Created by Julie Keros
 * Aug 20 2022
 * For Progress tracking
 ********************/


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.datetime.DateConverter;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.h2.util.StringUtils;

import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.LocationService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;
import edu.umich.brcf.shared.layers.dto.CompoundDTO;
import edu.umich.brcf.shared.layers.dto.ProcessTrackingDetailsDTO;
import edu.umich.brcf.shared.layers.dto.UserDTO;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistItemSimple;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import org.apache.wicket.ajax.AjaxEventBehavior;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.DateFormat.*;

import org.apache.wicket.ajax.attributes.*;


public class EditProcessTrackingDetailDialog extends AbstractFormDialog
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
	CompoundService compoundService;
	// issue 79
	FeedbackPanel aFeedback;   
	EditProcessTrackingDetailDialog editProcessTrackingDetailDialog = this;
	String dateDPInputStr; 
	String dateDPInputCompletedStr; 
	ProcessTrackingDetails gPtd ;
	//List<ProcessTrackingDetails> nList;
	public DropDownChoice<String> userNamesDD;
	public DropDownChoice<String> taskDescDD;
	public DropDownChoice<String> statusDD;
	public DropDownChoice<String> daysExpectedDD;   
	int amountToMove = 0;
	public String maxCompltDate = "";
	public String maxOnHoldDate = "";
	List<String> userNamesChoices = new ArrayList<String>();
	List<String> taskNamesChoices = new ArrayList<String>();   
	boolean offHoldInProcess = false;
	EditProcessTrackingDetailDialog EditProcessTrackingDetailDialog = this;// issue 61 2020
	ProcessTrackingDetailsDTO processTrackingDetailsDTO = new ProcessTrackingDetailsDTO();
	Button saveChangesButton;
	EditProcessTrackingDetailDialogForm editProcessTrackingDetailDialogForm;
	ProcessTrackingDetails ptd;
	public ProcessTrackingDetailsDTO getProcessTrackingDetailsDTO() { return processTrackingDetailsDTO; }
	public void setProcessTrackingDetailsDTO(ProcessTrackingDetailsDTO processTrackingDetailsDTO)  { this.processTrackingDetailsDTO = processTrackingDetailsDTO; }
		
	public DialogButton submitButton = new DialogButton("submit", "Done");
	public DialogButton submitButton2 = new DialogButton("submit2", "ResetDefault");
	public METWorksAjaxUpdatingDateTextField dateFld;
	public METWorksAjaxUpdatingDateTextField dateFldStarted;
	public METWorksAjaxUpdatingDateTextField dateFldOnHold;
	/// issue 303
	public TextField dateFldOnHoldDP, dateFldCompletedDP ;   
	
	// issue 303
	boolean isKeyClear = false;
	boolean isCompletedKeyClear = false;
	
	public Form<?> form;   
    private Date date = new Date();
	//private Date date;
	IModel<Date> model;
	IModel<Date> modelDate;
	public EditProcessTrackingDetailDialog(String id, String title) 
		{	
		super(id, title,  true);
		DialogButton submitButton = new DialogButton("submit", "Done");
		DialogButton submitButton2 = new DialogButton("submit2", "ResetDefault");
		Form<?> form;
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);		
		add(aFeedback);		
		add(new Label("titleLabel", "Add Task"));
		add(form = editProcessTrackingDetailDialogForm = new EditProcessTrackingDetailDialogForm("editProcessTrackingDetailDialogForm", "to be assigned", processTrackingDetailsDTO, EditProcessTrackingDetailDialog, null ));
		//public EditProcessTrackingDetailDialogForm(final String id, final String jobid, final ProcessTrackingDetailsDTO ProcessTrackingDetailsDTO,    final ModalWindow window, final EditProcessTrackingDetailDialog EditProcessTrackingDetailDialog, final ProcessTrackingDetails ptd, final boolean isViewOnly)
		}
		
	public EditProcessTrackingDetailDialog(String id, String title, IModel cmpModel) 
		{
		this (id, title, cmpModel, false, null);
		}
	
	public EditProcessTrackingDetailDialog(String id, String title, IModel cmpModel,  boolean isViewOnly, ProcessTrackingDetailsDTO lprocessTrackingDetailsDTO) 
		{
		super(id, title,  true);
		ptd = (ProcessTrackingDetails) cmpModel.getObject();	
	   // lprocessTrackingDetailsDTO = (ProcessTrackingDetailsDTO) new Model <ProcessTrackingDetailsDTO> (lprocessTrackingDetailsDTO).getObject();	
		processTrackingService.initializeProcessKids(ptd);
		
		// issue 292
		maxCompltDate = (processTrackingService.grabMaxCompletedDate 
		         (ptd.getExperiment().getExpID(),  ptd.getAssay().getAssayId(),  ptd.getDetailOrder()));  
		maxOnHoldDate = (processTrackingService.grabMaxOnHoldDate 
		         (ptd.getExperiment().getExpID(),  ptd.getAssay().getAssayId(), ptd.getDetailOrder()));  
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);	    	
		add(aFeedback);	
		add(new Label("titleLabel",  "Edit Assigned Task"));    
	    setProcessTrackingDetailsDTO(ProcessTrackingDetailsDTO.instance(ptd));   
	    lprocessTrackingDetailsDTO = this.processTrackingDetailsDTO;
		// issue 196
		add(form = editProcessTrackingDetailDialogForm = new EditProcessTrackingDetailDialogForm("editProcessTrackingDetailDialogForm", ptd.getJobid(), processTrackingDetailsDTO,  EditProcessTrackingDetailDialog, ptd, isViewOnly, lprocessTrackingDetailsDTO));
		}
	
	
	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public MarkupContainer setDefaultModel(IModel model) {
		// TODO Auto-generated method stub
		return this;    
	}


	@Override
	public DialogButton getSubmitButton() {
		// TODO Auto-generated method stub
		return null; 
	}


	@Override
	public Form getForm() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void onError(AjaxRequestTarget target, DialogButton button) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton button) {
		// TODO Auto-generated method stub
		
	}
	
	public final class EditProcessTrackingDetailDialogForm extends Form 
		{
		
		DropDownChoice selectedParentInventoryDD;
		public TextArea textAreaNotes;
	   
		AjaxCheckBox isNoInventoryCheckBox; // issue 196
		public EditProcessTrackingDetailDialogForm(final String id, final String jobid, final ProcessTrackingDetailsDTO processTrackingDetailsDTO,  final EditProcessTrackingDetailDialog editProcessTrackingDetailDialog, ProcessTrackingDetails ptd) // issue 27 2020
			{
			this (id,  jobid,  processTrackingDetailsDTO,   editProcessTrackingDetailDialog,  ptd, false, null);
			}
		    
		public EditProcessTrackingDetailDialogForm(final String id, final String jobid, final ProcessTrackingDetailsDTO processTrackingDetailsDTO,     final EditProcessTrackingDetailDialog editProcessTrackingDetailDialog, final ProcessTrackingDetails ptd, final boolean isViewOnly, ProcessTrackingDetailsDTO lprocessTrackingDetailsDTO) // issue 27 2020
			{
			super(id, new CompoundPropertyModel(processTrackingDetailsDTO));
			////// Task descriptions 
			/////
		  // setDefaultModel(new CompoundPropertyModel<ProcessTrackingDetails>(ptd));
			 setDefaultModel(new CompoundPropertyModel<ProcessTrackingDetailsDTO>(lprocessTrackingDetailsDTO));
			statusDD = new DropDownChoice("status",  Arrays.asList(new String[] { "On hold", "In progress", "Completed", "In queue"
                   })) // issue 199

					{    
					
					};
			statusDD.setOutputMarkupId(true);   
			add (statusDD);
			statusDD.add(buildStandardFormComponentUpdateBehavior("change", "updateStatus"));
			/////////////////////////////////
			daysExpectedDD = new DropDownChoice("daysExpected",  Arrays.asList(new String[] { "1","2","3","4","5","6","7"
            })) // issue 199

				{
				
				};
			daysExpectedDD.setOutputMarkupId(true);
		
			add (daysExpectedDD);
			taskDescDD= new DropDownChoice("taskDesc",    processTrackingService.allTaskDesc())
		    	{
			
		    	};			
			taskDescDD.setOutputMarkupId(true);	
		    add(taskDescDD);
		    taskDescDD.setRequired(true);
			textAreaNotes = new TextArea("comments");
			//textAreaNotes.setRequired(true);			
			textAreaNotes.add(StringValidator.maximumLength(3000));
			textAreaNotes.add(buildStandardFormComponentUpdateBehavior("change", "updateNotes"));
			add(textAreaNotes);	
			if (ptd != null)
				setValuesForEdit(ptd);	
			
			dateFldStarted =  new METWorksAjaxUpdatingDateTextField("dateStarted", new PropertyModel<String>(processTrackingDetailsDTO, "dateStarted"), "dateStarted")
				{
				@Override
			    public boolean isEnabled()
			    	{
			    	return false;
			    	}
			   
				@Override
				protected void onUpdate(AjaxRequestTarget target)  
			        { 
			        }
				};		
			dateFldStarted.setDefaultStringFormat(ProcessTracking.ProcessTracking_DATE_FORMAT);
			dateFldStarted.setRequired(true);
			dateFldStarted.add(buildStandardFormComponentUpdateBehavior("change", "updateStarted"));
			add(dateFldStarted);    
			
			
		// issue 303
			   processTrackingDetailsDTO.setDateOnHoldDP(null);
			   processTrackingDetailsDTO.setDateCompletedDP(null);
			   
			   // issue 303
			   dateFldOnHoldDP = new DateTextField("dateOnHoldDP", new PropertyModel<Date>(processTrackingDetailsDTO, "dateOnHoldDP"))
					   {
					   @Override
				       public boolean isEnabled()
				           {
				    	   return true;
				    	   }
					   }				   
					   ;    
			  // dateFldOnHoldDP.clearInput();
			   dateFldCompletedDP = new DateTextField("dateCompletedDP", new PropertyModel<Date>(processTrackingDetailsDTO, "dateCompletedDP"));    
			  // dateFldCompletedDP.clearInput();
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
				String startDateString = "10/31/2023";
				Date startDate = null;
				try {
				startDate = df.parse(startDateString);
				    } catch (ParseException e) {
					// TODO Auto-generated catch block    
					e.printStackTrace();
				    }
				// issue 303
				add (dateFldOnHoldDP); 
				add (dateFldCompletedDP); 
			 
		    // issue 303
			dateFldOnHoldDP.add(new AjaxEventBehavior("click") {
		        @Override
		        protected void onEvent(AjaxRequestTarget target) {
				    String changeBackground = "  document.getElementById('dateHold').style.backgroundColor = 'white';  " +
			     //   "  document.getElementById('dateHold').style.color = 'black';  "   + "document.getElementById('dateHold').value= '" +   "';" ;
			          
 					"  document.getElementById('dateHold').style.color = 'black';  "  ;
			        target.appendJavaScript( changeBackground  );  
		        	
		        }  });    
			    
			// issue 303
			dateFldOnHoldDP.add(new AjaxEventBehavior("keydown") {			
				 @Override
			        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
			            super.updateAjaxAttributes(attributes);

			            IAjaxCallListener listener = new AjaxCallListener(){
			                @Override
			                public CharSequence getPrecondition(Component component) {
			                    //this javascript code evaluates wether an ajaxcall is necessary.
			                    //Here only by keyocdes for F9 and F10 
			                    return  "var keycode = Wicket.Event.keyCode(attrs.event);" +
			                            "if ((keycode == 13) || (keycode == 46))" +
			                            "    return true;" +
			                            "else" +
			                            "    return false;";
			                }
			            };
			            attributes.getAjaxCallListeners().add(listener);

			            //Append the pressed keycode to the ajaxrequest 
			            attributes.getDynamicExtraParameters()
			                .add("var eventKeycode = Wicket.Event.keyCode(attrs.event);" +
			                     "return {keycode: eventKeycode};");

			            //whithout setting, no keyboard events will reach any inputfield
			          //  attributes.setAllowDefault(true);	
				
				 };			
		      @Override
		        protected void onEvent(AjaxRequestTarget target) {
				  Request request = RequestCycle.get().getRequest();
				  String jsKeycode = request.getRequestParameters()
                          .getParameterValue("keycode").toString();
				  isKeyClear = true;
				  dateDPInputStr = "";
		        }  });       
	
			///////////
			
			 // issue 303  
			dateFldOnHoldDP.add(new AjaxEventBehavior("change") {       
		        @Override
		        protected void onEvent(AjaxRequestTarget target) {
		            // some requests..
		           dateDPInputStr = dateFldOnHoldDP.getInput();
		           
		          // String dateOnHoldCoverted = parseDateToddmmyyyy(dateDPInputStr);
		           String dateOnHoldCoverted  = dateDPInputStr;
		           //  String dateCompletedConverted = parseDateToddmmyyyy(dateDPInputStr);
		           String changeStyle = "document.getElementById('dateHold').value= '" + dateOnHoldCoverted + "';";
			    //	String changeStyle2 = "document.getElementById('dateComplete').value= '" + dateCompletedConverted + "';";
			        String changeBackground = "  document.getElementById('dateHold').style.backgroundColor = 'white';  " +
		        	"  document.getElementById('dateHold').style.color = 'black';  "   + "document.getElementById('dateHold').value= '" + dateOnHoldCoverted  +  "';" ;
		        	    target.appendJavaScript( changeBackground  ); 
		        	    if (StringUtils.isNullOrEmpty(dateDPInputStr))
		        	        isKeyClear = true;
		        	    else
		        	    	isKeyClear = false;   
		        }  });
			
			      
			// issue 303
			dateFldCompletedDP.add(new AjaxEventBehavior("click") {
		        @Override
		        protected void onEvent(AjaxRequestTarget target) {
		        	String changeBackground = "  document.getElementById('dateComplete').style.backgroundColor = 'white';  " +
					        "  document.getElementById('dateComplete').style.color = 'black';    " ;
					          target.appendJavaScript( changeBackground  );  
		        }  });   
			   
			   
			dateFldCompletedDP.add(new AjaxEventBehavior("change") {       
		        @Override
		        protected void onEvent(AjaxRequestTarget target) {
		            // some requests..
		           dateDPInputCompletedStr = dateFldCompletedDP.getInput();
		           String dateCompletedCoverted  = dateDPInputCompletedStr ;
			        String changeBackground = "  document.getElementById('dateComplete').style.backgroundColor = 'white';  " +
		        	"  document.getElementById('dateComplete').style.color = 'black';  "   + "document.getElementById('dateComplete').value= '" + dateCompletedCoverted +   "';" ;
			        		
		        	    target.appendJavaScript( changeBackground  ); 
		        	    if (StringUtils.isNullOrEmpty(dateDPInputCompletedStr ))
		        	    	isCompletedKeyClear = true;
		        	    else
		        	    	isCompletedKeyClear = false;    
		           
		        }  });
			
		  ///////////   issue 303 key down /////////////////////////////////
			       
			
			// issue 303
			dateFldCompletedDP.add(new AjaxEventBehavior("keydown") {			
						 @Override
					        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
					            super.updateAjaxAttributes(attributes);

					            IAjaxCallListener listener = new AjaxCallListener(){
					                @Override
					                public CharSequence getPrecondition(Component component) {
					                    //this javascript code evaluates wether an ajaxcall is necessary.
					                    //Here only by keyocdes for F9 and F10 
					                    return  "var keycode = Wicket.Event.keyCode(attrs.event);" +
					                            "if ((keycode == 13) || (keycode == 46))" +
					                            "    return true;" +
					                            "else" +
					                            "    return false;";
					                }
					            };
					            attributes.getAjaxCallListeners().add(listener);

					            //Append the pressed keycode to the ajaxrequest 
					            attributes.getDynamicExtraParameters()
					                .add("var eventKeycode = Wicket.Event.keyCode(attrs.event);" +
					                     "return {keycode: eventKeycode};");

					            //whithout setting, no keyboard events will reach any inputfield
					          //  attributes.setAllowDefault(true);	
						
						 };			
				      @Override
					        protected void onEvent(AjaxRequestTarget target) {
							  Request request = RequestCycle.get().getRequest();
							  String jsKeycode = request.getRequestParameters()
			                          .getParameterValue("keycode").toString();
							  isCompletedKeyClear = true;
							  dateDPInputCompletedStr= "";
							  
				        }  });    
			
			
		 //////////////////////////////////////////////////
		
			dateFldOnHoldDP.setDefaultModel(null);
			
			dateFldOnHoldDP.add(buildStandardFormComponentUpdateBehavior("change", "updateOnHoldDP"));
			
			/// issue 303
			dateFldCompletedDP.setDefaultModel(null);
			
			dateFldCompletedDP.add(buildStandardFormComponentUpdateBehavior("change", "updateCompletedDP"));
			
			
			
			/////   add(dateFldOnHold);
		/////	dateFldOnHold.add(buildStandardFormComponentUpdateBehavior("change", "updateOnHold"));
			// issue 277		
			userNamesDD= new DropDownChoice("assignedTo",    userNamesChoices)    
			    {
				
				}
				;			
			userNamesDD.setOutputMarkupId(true);
			add(userNamesDD);
			     
			userNamesDD.add(buildStandardFormComponentUpdateBehavior("change", "updateUsers"));
			
			// issue 262
			List <String> adminNamesAssignedToList = userService.allAdminNames(false);
			adminNamesAssignedToList.remove("All Users");
			userNamesDD.setChoices(adminNamesAssignedToList);	
			userNamesDD.setRequired(true);	
				
			}   
		
			public int saveChanges(ProcessTrackingDetailsDTO lprocessTrackingDetailsDTO, AjaxRequestTarget target)
					{	
					int retCode = 0;    
					try   
						{     
						// issue 303
						String dateOnHoldCoverted = parseDateToddmmyyyy(dateDPInputStr);
						lprocessTrackingDetailsDTO = (ProcessTrackingDetailsDTO) editProcessTrackingDetailDialogForm.getModelObject();
						processTrackingDetailsDTO = (ProcessTrackingDetailsDTO) editProcessTrackingDetailDialogForm.getModelObject();
						if  (!StringUtils.isNullOrEmpty(dateOnHoldCoverted)  && !isKeyClear )
							processTrackingDetailsDTO.setDateOnHold(dateOnHoldCoverted);
						else 
							{
							if (isKeyClear)
								processTrackingDetailsDTO.setDateOnHold("");
							}	
						String dateCompletedCoverted = parseDateToddmmyyyy(dateDPInputCompletedStr);
						lprocessTrackingDetailsDTO = (ProcessTrackingDetailsDTO) editProcessTrackingDetailDialogForm.getModelObject();
						processTrackingDetailsDTO = (ProcessTrackingDetailsDTO) editProcessTrackingDetailDialogForm.getModelObject();
						if  (! StringUtils.isNullOrEmpty(dateCompletedCoverted) && !isCompletedKeyClear )
							processTrackingDetailsDTO.setDateCompleted(dateCompletedCoverted);
						else 
							{
							if (isCompletedKeyClear)
								processTrackingDetailsDTO.setDateCompleted("");
							}
						
						
						
						SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy"); 
						// issue 303
						processTrackingDetailsDTO.setDateOnHoldDP((Date) dateFldOnHoldDP.getDefaultModelObject());
						processTrackingDetailsDTO.setDateCompletedDP((Date) dateFldCompletedDP.getDefaultModelObject());
						offHoldInProcess = false;                  
						// issue 292    
						User userAssignedTo;
						userAssignedTo = userService.loadUserByFullName(processTrackingDetailsDTO.getAssignedTo());						
						Calendar expectedCompletionDate;
						Calendar actualCompletionDate;
						ProcessTrackingDetails originalPtd = processTrackingService.loadById(processTrackingDetailsDTO.getJobID());
						String originalDaysExpStr = originalPtd.getDaysExpected();
						Calendar originalCompletedDate = originalPtd.getDateCompleted();
						Calendar originalCompletingDate = originalPtd.getDateStarted();
						Calendar originalOnHoldDate = originalPtd.getDateOnHold();
						long dayCompletedToAdd = 0L;	
						long dayOnHoldToAdd = 0L;
						// issue 210   
						Calendar lCalOnHold = Calendar.getInstance();
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold()))
							processTrackingDetailsDTO.setDateOnHold(DateUtils.dateAsFullString(new Date (processTrackingDetailsDTO.getDateOnHold())));			
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateStarted()))
							processTrackingDetailsDTO.setDateStarted(DateUtils.dateAsFullString(new Date (processTrackingDetailsDTO.getDateStarted())));
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted()))
							processTrackingDetailsDTO.setDateCompleted(DateUtils.dateAsFullString(new Date (processTrackingDetailsDTO.getDateCompleted())));
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold()))
							lCalOnHold.setTime(new Date(processTrackingDetailsDTO.getDateOnHold()));
						Calendar lCalCompleted = Calendar.getInstance();
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted()))
							lCalCompleted.setTime(new Date(processTrackingDetailsDTO.getDateCompleted()));
						Calendar lCalStartDate = Calendar.getInstance();
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateStarted()))
							lCalStartDate.setTime(new Date(processTrackingDetailsDTO.getDateStarted()));
						// issue 292
						// issue 298
						Calendar lMaxCompletedDate = Calendar.getInstance();
					    if (!StringUtils.isNullOrEmpty(maxCompltDate))		
							lMaxCompletedDate.setTime(new Date (maxCompltDate));  
					    else 
					    	lMaxCompletedDate.setTime(new Date (processTrackingDetailsDTO.getDateStarted()));
			            
						// issue 292
					    Calendar lMaxOnHoldDate = Calendar.getInstance();
					    if (!StringUtils.isNullOrEmpty(maxOnHoldDate))
					    	lMaxOnHoldDate.setTime(new Date (maxOnHoldDate));               
					    else 
					    	lMaxOnHoldDate.setTime(new Date (processTrackingDetailsDTO.getDateStarted()));
					    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
						String dateToConvertstr = lMaxOnHoldDate == null ? "" : sdf.format(lMaxOnHoldDate.getTime());
						String dateToConver2str = lMaxCompletedDate == null ? "" : sdf.format(lMaxCompletedDate.getTime());
						// issue 273
						if (processTrackingDetailsDTO.getStatus().equals("On hold")  &&  
						    StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold()))
						    {
							Calendar dateToConvert = Calendar.getInstance();							
							SimpleDateFormat sdfH = new SimpleDateFormat("MM/dd/yyyy");
							String dateHoldString =   sdfH.format(dateToConvert.getTime());
							processTrackingDetailsDTO.setDateOnHold(dateHoldString);
						    }
						// issue 273
						if (processTrackingDetailsDTO.getStatus().equals("Completed")  &&  
						    StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted()))
						    {
							Calendar dateToConvert = Calendar.getInstance();
							SimpleDateFormat sdfC = new SimpleDateFormat("MM/dd/yyyy");
							String dateCompleteString =   sdfC.format(dateToConvert.getTime());
							processTrackingDetailsDTO.setDateCompleted(dateCompleteString);
						    }
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted()) && 
							//	processTrackingDetailsDTO.getDateOnHold().compareTo(processTrackingDetailsDTO.getDateCompleted()) > 0  )
								!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold()) &&
								lCalCompleted.compareTo(lCalOnHold) < 0  )
									{
									String errMsg =   "The completed date must be later than the on hold date." ;
									target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errMsg));								
									return 1;
									}   
						if (!processTrackingDetailsDTO.getStatus().equals("Completed") && !StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted()))
								//	processTrackingDetailsDTO.getDateOnHold().compareTo(processTrackingDetailsDTO.getDateCompleted()) > 0  ) )
										{
										String errMsg =   "There is a completed date.  Please set status to complete." ;
										target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errMsg));
										return 1;
										}
						
						if (originalOnHoldDate == null && !processTrackingDetailsDTO.getStatus().equals("On hold") && !StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold())
								 && StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted() ) )
							//	processTrackingDetailsDTO.getDateOnHold().compareTo(processTrackingDetailsDTO.getDateCompleted()) > 0  ) )
									{
									String errMsg = "There is an on hold date.  Please set status to on hold." ;
									target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errMsg));
									return 1;    
									}
						
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold()) && 
								//processTrackingDetailsDTO.getDateStarted().compareTo(processTrackingDetailsDTO.getDateOnHold()) > 0  )
								!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateStarted()) &&
								lCalStartDate.compareTo(lCalOnHold) > 0 && lCalOnHold != null &&  lCalOnHold.compareTo(lMaxOnHoldDate)  < 0 )
								
								{
								String errMsg =  "The on hold date must be later than the start date " + (StringUtils.isNullOrEmpty(maxOnHoldDate) ? " " :  "or greater than the latest on hold date of " + maxOnHoldDate) ;
								String changeBackground = "  document.getElementById('dateHold').style.backgroundColor = 'white';  " +
					    	    		"  document.getElementById('dateHold').style.color = 'black';  "   + "document.getElementById('dateHold').value= '" +   "';" ;
								
								target.appendJavaScript(changeBackground + edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errMsg));
								//EditProcessTrackingDetailDialog.this.error(errMsg); 
								//target.add(this);
								return 1;   
								}
						
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateStarted()) &&   
								//processTrackingDetailsDTO.getDateStarted().compareTo(processTrackingDetailsDTO.getDateOnHold()) > 0  )
								!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted()) && 
								     lCalStartDate.compareTo(lCalCompleted) > 0  &&  lCalCompleted!= null &&  (lCalCompleted.compareTo(lMaxCompletedDate)  < 0 ) )
								   
								{  							
								String errMsg =   "The completed date must be later than the start date " + (StringUtils.isNullOrEmpty(maxCompltDate) ? " " :  "or greater than the latest completed date of " + maxCompltDate) ;
								String changeBackgroundc = "  document.getElementById('dateComplete').style.backgroundColor = 'white';  " +
					    	    		"  document.getElementById('dateComplete').style.color = 'black';  "   + "document.getElementById('dateComplete').value= '" +   "';" ;
								
								target.appendJavaScript(changeBackgroundc  + edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errMsg)); 
								return 1;                   
								}
						// issue 292    
						 if (   ! StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted()) &&    lCalCompleted != null && lCalStartDate.compareTo(lCalCompleted) > 0 && lCalCompleted.compareTo(lMaxCompletedDate)  >= 0 )
							processTrackingDetailsDTO.setDateStarted(processTrackingDetailsDTO.getDateCompleted());							
						 if (     ! StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold()) &&   processTrackingDetailsDTO.getStatus().equals("On hold")  &&  lCalOnHold != null && lCalStartDate.compareTo(lCalOnHold) > 0 && lCalOnHold.compareTo(lMaxOnHoldDate)  >= 0 )
							processTrackingDetailsDTO.setDateStarted(processTrackingDetailsDTO.getDateOnHold());
						processTrackingDetailsDTO.setTaskDesc(lprocessTrackingDetailsDTO.getTaskDesc());
						/// issue 287    
						if (originalOnHoldDate != null && processTrackingDetailsDTO.getStatus().equals("In progress") && !StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold())
								 && StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted() ) )
							//	processTrackingDetailsDTO.getDateOnHold().compareTo(processTrackingDetailsDTO.getDateCompleted()) > 0  ) )
							{	
							offHoldInProcess = true;
							processTrackingDetailsDTO.setDateOnHold("");    
							}
						ProcessTrackingDetails ptd = processTrackingService.save(processTrackingDetailsDTO, userAssignedTo, null, null);				
						
						gPtd = ptd;           
						String lStatus = ptd.getStatus();
						int diffDaysExp = Integer.parseInt(ptd.getDaysExpected())- Integer.parseInt(originalDaysExpStr);
						
						if (diffDaysExp != 0)
							{
						   // moveDependentTasks(ptd, diffDaysExp);							
							amountToMove = diffDaysExp;
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateStartedString = lsdf.format(ptd.getDateStarted().getTime());
							processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateStartedString);
							}
						
						// issue 292
						
						 if (ptd.getDateCompleted() != null && lCalStartDate.compareTo(lCalCompleted) > 0 && lCalCompleted.compareTo(lMaxCompletedDate)  >= 0 )
						 	{
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateCompletedString = lsdf.format(ptd.getDateCompleted().getTime());  
							processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateCompletedString);
						 	}    
						
						if ((originalCompletedDate == null && ptd.getDateOnHold() == null && ptd.getDateCompleted() != null) ||  ( ptd.getDateCompleted() != null && ptd.getDateOnHold() == null ))
							{
							originalCompletingDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(originalDaysExpStr));							
							if (originalCompletedDate == null)   
								{   
								
								dayCompletedToAdd = ChronoUnit.DAYS.between(originalCompletingDate.toInstant(),ptd.getDateCompleted().toInstant());
								if (ptd.getDateCompleted() != null && ptd.getDateStarted()!= null && ptd.getDateCompleted().compareTo(ptd.getDateStarted()) == 0)
									{   
									//moveDependentTasks(ptd, 0);									
									amountToMove = 0;
									SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
									String dateCompletedString = lsdf.format(ptd.getDateCompleted().getTime()); 
									processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateCompletedString);
									//processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus);
									}
								else
									{
									// issue 292
									//moveDependentTasks(ptd, (int) dayCompletedToAdd * ptd.getDateCompleted().compareTo(originalCompletingDate));								
									amountToMove = (int) dayCompletedToAdd * ptd.getDateCompleted().compareTo(originalCompletingDate);
									SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
									String dateCompletedString = lsdf.format(ptd.getDateCompleted().getTime());
									processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateCompletedString);
									}
									// issue 269									
								}
							else
								{
								dayCompletedToAdd = ChronoUnit.DAYS.between(originalCompletedDate.toInstant(),ptd.getDateCompleted().toInstant());
								amountToMove = (int) dayCompletedToAdd;
								SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
								String dateCompletedString = lsdf.format(ptd.getDateCompleted().getTime());
								processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateCompletedString);
								}							
							}
						
						if (ptd.getDateOnHold() != null && ptd.getDateCompleted() == null && originalOnHoldDate == null)
							{
							dayOnHoldToAdd = ChronoUnit.DAYS.between(ptd.getDateStarted().toInstant(),ptd.getDateOnHold().toInstant());
							amountToMove =(int) dayOnHoldToAdd ;
							// issue 287
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateOnHoldString = lsdf.format(ptd.getDateOnHold().getTime());
						
							// issue 292
							processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateOnHoldString);
							}
						
						if (ptd.getDateOnHold() != null && ptd.getDateCompleted() == null && originalOnHoldDate != null)
							{
							
							//dayOnHoldToAdd = ChronoUnit.DAYS.between(ptd.getDateStarted().toInstant(),ptd.getDateOnHold().toInstant());
							dayOnHoldToAdd = ChronoUnit.DAYS.between(originalOnHoldDate.toInstant(),ptd.getDateOnHold().toInstant());
							// moveDependentTasks(ptd, (int) dayOnHoldToAdd );
						
							//amountToMove = (int) dayOnHoldToAdd -1;
							amountToMove = (int) dayOnHoldToAdd;
							
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateOnHoldString = lsdf.format(ptd.getDateOnHold().getTime());
							//(dateToConvert == null) ? "" : sdf.format(dateToConvert.getTime());			
							//processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus);
							// issue 292
							processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateOnHoldString);
							}
						
						// issue 292
						if (ptd.getDateOnHold() != null && ptd.getDateCompleted() != null && originalCompletedDate == null)
							{
							dayOnHoldToAdd = ChronoUnit.DAYS.between(ptd.getDateOnHold().toInstant(),ptd.getDateCompleted().toInstant());
							// moveDependentTasks(ptd, (int) dayOnHoldToAdd );
							//amountToMove = (int) dayOnHoldToAdd -1;
							amountToMove = (int) dayOnHoldToAdd;
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateCompletedString = lsdf.format(ptd.getDateCompleted().getTime());
							//(dateToConvert == null) ? "" : sdf.format(dateToConvert.getTime());			
							//processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus);
							processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateCompletedString);
							}     
						
						if (ptd.getDateOnHold() != null && ptd.getDateCompleted() != null && originalCompletedDate != null)
							{
							dayOnHoldToAdd = ChronoUnit.DAYS.between(originalCompletedDate.toInstant(),ptd.getDateCompleted().toInstant());
							amountToMove = (int) dayOnHoldToAdd;
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateCompletedString = lsdf.format(ptd.getDateCompleted().getTime());
							processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateCompletedString);
							} 
					
						if (ptd.getJobid() != null && (processTrackingDetailsDTO.getJobID()== null || processTrackingDetailsDTO.getJobID().equals("to be assigned")))
							processTrackingDetailsDTO.setJobID(ptd.getJobid());
						String msg = "<span style=\"color:blue;\">" +   "Task Assignment saved for job ID: " + ptd.getJobid() +  "." + "</span>";	
						EditProcessTrackingDetailDialog.this.info(msg);
						isKeyClear = false;
						isCompletedKeyClear = false;
						// issue 303
						dateDPInputStr = "";
						dateDPInputCompletedStr = "";
						return 0;
						}
					catch (RuntimeException r)
						{
						r.printStackTrace();
						String errMsg =  "<span style=\"color:red;\">" + r.getMessage() +  "</span>";
				        EditProcessTrackingDetailDialog.this.error(errMsg);
				        return 1;
						}
					catch (Exception e)
						{ 
						e.printStackTrace(); 
						EditProcessTrackingDetailDialog.this.error("Save unsuccessful. Please make sure that smiles is valid."); 
						return 1;
						}		
					
					/********************************************/
					}				
		
			
	
		public void moveDependentTasks (ProcessTrackingDetails ptd, int diffNewOrig)
			{
			List<ProcessTrackingDetails> nList = new ArrayList <ProcessTrackingDetails> () ;
			nList = processTrackingService.loadAllTasksAssigned(ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), false, null);
			int trackingOrder = ptd.getDetailOrder();
			int bi = 0;			
		  //  if (ptd.getDateCompleted() != null && ptd.getDateStarted()!= null && ptd.getDateCompleted().compareTo(ptd.getDateStarted()) == 0)
		  //      diffNewOrig = 0;	
			for (int i = trackingOrder; i<nList.size(); i++)
				{
				nList.get(i).updateDaysExpected(diffNewOrig);
				if ((bi == 0 && (i-1) < 0) || bi == 0 && !nList.get(i-1).getStatus().equals("On hold"))
					nList.get(i).updateStatus();
				bi++;
				}
			}
		
		
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
	        {
		    @Override
		    protected void onUpdate(AjaxRequestTarget target)
		    	{
		    	List<String> newAliquotList = new ArrayList<String>();
		    	// issue 290
		    	// issue 292
		    	switch (response)
		        	{		    	   
		    	    case "updateUsers" : 
		    	    	// issue 292
		    	    	break;
		    	    case "updateNotes" : 
		    	    	// issue 292
		    	    	break;	
		    	    case "updateStarted" : 
		    	    	// issue 292
		    	        break;
		    	    case "updateCompleted" : 
		    	    	// issue 292
		    	        break;
		       	    case "updateOnHold" : 
		    	    	// issue 292
		    	        break;
		       	    case "updateStatus" : 
		    	    	// issue 292
		    	        break;   
		       	    case "updateOnHoldDP" : 
		    	    	// issue 292 
		    	        break; 
		       	  case "updateCompletedDP" : 
		    	    	// issue 292 
		    	        break;  
		    	    
		    	    	    
		    	    }
		    	}
		    };
		}
			
		// issue 100   
		public void setValuesForEdit (ProcessTrackingDetails  ptd)
			{
			processTrackingDetailsDTO.setComments(ptd.getComments());
			processTrackingDetailsDTO.setTaskDesc(ptd.getProcessTracking().getTaskDesc());
			processTrackingDetailsDTO.setAssignedTo(ptd.getAssignedTo().getFullNameByLast());
			processTrackingDetailsDTO.setStatus(ptd.getStatus());
			processTrackingDetailsDTO.setDaysExpected(ptd.getDaysExpected());
			processTrackingDetailsDTO.setDetailOrder(ptd.getDetailOrder());
			processTrackingDetailsDTO.setDateOnHold(ptd.convertToDateString(ptd.getDateOnHold()));
			//processTrackingDetailsDTO.setDetailOrder(ptd.getDetailOrder());
			}
		
		public String parseDateToddmmyyyy  (String dateyyyymmdd)
			{
			if (StringUtils.isNullOrEmpty(dateyyyymmdd))
				{
				return null;
				}   
			try
				{   
				String year = dateyyyymmdd.substring(0,dateyyyymmdd.indexOf("-"));
				String month = dateyyyymmdd.substring(dateyyyymmdd.indexOf("-") + 1,dateyyyymmdd.indexOf("-",1)+ 3);
				String day =  dateyyyymmdd.substring(dateyyyymmdd.indexOf("-")+4);
				return  month + "/" + day + "/"  + year;        
				}
			catch (Exception e)    
				{
				e.printStackTrace();
				return null;   
				}
			}
		
		public String parseDateToyyyymmdd (String datemmddyyyy)
			{
			if (StringUtils.isNullOrEmpty(datemmddyyyy))
				{
				return null;
				}   
			try
				{   
				String month = datemmddyyyy.substring(0,datemmddyyyy.indexOf("/"));
				String day = datemmddyyyy.substring(datemmddyyyy.indexOf("/") + 1,datemmddyyyy.indexOf("/",1)+ 3);
				String year =  datemmddyyyy.substring(datemmddyyyy.indexOf("/")+4);
				return  year + "-" + month + "-"  + day;        
				}
			catch (Exception e)    
				{
				e.printStackTrace();
				return null;   
				}
			}
		
		}////// END FORM
	       
	}
