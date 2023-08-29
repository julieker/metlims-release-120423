////////////////////////////////////////////////////
// EditProcessTrackingDetail.java
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
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.h2.util.StringUtils;

import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.domain.Workflow;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.LocationService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;
import edu.umich.brcf.shared.layers.dto.ProcessTrackingDetailsDTO;

public class EditProcessTrackingDetail extends WebPage
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
	TextArea textAreaNotes;
	ProcessTrackingDetails gPtd ;
	//List<ProcessTrackingDetails> nList;
	DropDownChoice<String> userNamesDD;
	DropDownChoice<String> taskDescDD;
	DropDownChoice<String> statusDD;
	DropDownChoice<String> daysExpectedDD;
	int amountToMove = 0;
	String maxCompltDate = "";
	String maxOnHoldDate = "";
	List<String> userNamesChoices = new ArrayList<String>();
	List<String> taskNamesChoices = new ArrayList<String>();
	boolean offHoldInProcess = false;
	EditProcessTrackingDetail editProcessTrackingDetail = this;// issue 61 2020
	ProcessTrackingDetailsDTO processTrackingDetailsDTO = new ProcessTrackingDetailsDTO();
	Button saveChangesButton;
	EditProcessTrackingDetailForm editProcessTrackingDetailForm;
	
	public ProcessTrackingDetailsDTO getProcessTrackingDetailsDTO() { return processTrackingDetailsDTO; }
	public void setProcessTrackingDetailsDTO(ProcessTrackingDetailsDTO processTrackingDetailsDTO)  { this.processTrackingDetailsDTO = processTrackingDetailsDTO; }
		

	
	public EditProcessTrackingDetail(Page backPage, final ModalWindow window) 
		{		
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);		
		add(aFeedback);		
		add(new Label("titleLabel", "Add Task"));
		add(editProcessTrackingDetailForm = new EditProcessTrackingDetailForm("editProcessTrackingDetailForm", "to be assigned", processTrackingDetailsDTO, backPage, window,editProcessTrackingDetail, null ));
		}
		
	public EditProcessTrackingDetail(Page backPage, IModel cmpModel, final ModalWindow window) 
		{
		this (backPage,cmpModel,window, false);
		}
	
	public EditProcessTrackingDetail(Page backPage, IModel cmpModel, final ModalWindow window, boolean isViewOnly) 
		{
		ProcessTrackingDetails ptd = (ProcessTrackingDetails) cmpModel.getObject();	
		processTrackingService.initializeProcessKids(ptd);
		
		// issue 292
		maxCompltDate = (processTrackingService.grabMaxCompletedDate 
		         (ptd.getExperiment().getExpID(),  ptd.getAssay().getAssayId()));  
		maxOnHoldDate = (processTrackingService.grabMaxOnHoldDate 
		         (ptd.getExperiment().getExpID(),  ptd.getAssay().getAssayId()));  
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);		
		add(aFeedback);	
		add(new Label("titleLabel",  "Edit Assigned Task"));               
		setProcessTrackingDetailsDTO(ProcessTrackingDetailsDTO.instance(ptd));
		// issue 196
		add(editProcessTrackingDetailForm = new EditProcessTrackingDetailForm("editProcessTrackingDetailForm", ptd.getJobid(), processTrackingDetailsDTO, backPage, window, editProcessTrackingDetail, ptd, isViewOnly));
		}
	
	public final class EditProcessTrackingDetailForm extends Form 
		{
		DropDownChoice selectedParentInventoryDD;
	   
		AjaxCheckBox isNoInventoryCheckBox; // issue 196
		public EditProcessTrackingDetailForm(final String id, final String jobid, final ProcessTrackingDetailsDTO processTrackingDetailsDTO, final Page backPage,  final ModalWindow window, final EditProcessTrackingDetail editProcessTrackingDetail, ProcessTrackingDetails ptd) // issue 27 2020
			{
			this (id, jobid, processTrackingDetailsDTO,backPage, window, editProcessTrackingDetail, ptd, false);
			}
		
		public EditProcessTrackingDetailForm(final String id, final String jobid, final ProcessTrackingDetailsDTO ProcessTrackingDetailsDTO, final Page backPage,   final ModalWindow window, final EditProcessTrackingDetail EditProcessTrackingDetail, final ProcessTrackingDetails ptd, final boolean isViewOnly) // issue 27 2020
			{
			super(id, new CompoundPropertyModel(ProcessTrackingDetailsDTO));
			////// Task descriptions 
			/////
			
			statusDD = new DropDownChoice("status",  Arrays.asList(new String[] { "On hold", "In progress", "Completed", "In queue"
                   })) // issue 199

					{
					
					};
			statusDD.setOutputMarkupId(true);
			add (statusDD);
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
			add(textAreaNotes);	
			if (ptd != null)
				setValuesForEdit(ptd);	
			
			METWorksAjaxUpdatingDateTextField dateFld =  new METWorksAjaxUpdatingDateTextField("dateCompleted", new PropertyModel<String>(processTrackingDetailsDTO, "dateCompleted"), "dateCompleted")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)  
			        { 
			        }
				};		
				dateFld.setDefaultStringFormat(ProcessTracking.ProcessTracking_DATE_FORMAT);
				add(dateFld);
				
			
			METWorksAjaxUpdatingDateTextField dateFldStarted =  new METWorksAjaxUpdatingDateTextField("dateStarted", new PropertyModel<String>(processTrackingDetailsDTO, "dateStarted"), "dateStarted")
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
			add(dateFldStarted);
		
			METWorksAjaxUpdatingDateTextField dateFldOnHold =  new METWorksAjaxUpdatingDateTextField("dateOnHold", new PropertyModel<String>(processTrackingDetailsDTO, "dateOnHold"), "dateOnHold")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)  
			        { 
			        }
				};		
			dateFldOnHold.setDefaultStringFormat(ProcessTracking.ProcessTracking_DATE_FORMAT);
			add(dateFldOnHold);
			// issue 277		
			userNamesDD= new DropDownChoice("assignedTo",    userNamesChoices)
			    {
				
				}
				;			
			userNamesDD.setOutputMarkupId(true);
			add(userNamesDD);
			// issue 262
			List <String> adminNamesAssignedToList = userService.allAdminNames(false);
			adminNamesAssignedToList.remove("All Users");
			userNamesDD.setChoices(adminNamesAssignedToList);	
			userNamesDD.setRequired(true);	
			
			// issue 79
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
						offHoldInProcess = false;
						// issue 292    
					/*	String maxCompltDate = (processTrackingService.grabMaxCompletedDate 
						         (processTrackingDetailsDTO.getExpID(),   processTrackingDetailsDTO.getAssayID())); */
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
						Calendar lMaxCompletedDate = Calendar.getInstance();
					    if (!StringUtils.isNullOrEmpty(maxCompltDate))
							{
							lMaxCompletedDate.setTime(new Date (maxCompltDate));         
							}
						// issue 292
					    Calendar lMaxOnHoldDate = Calendar.getInstance();
					    if (!StringUtils.isNullOrEmpty(maxOnHoldDate))
							{
					    	lMaxOnHoldDate.setTime(new Date (maxOnHoldDate));         
							}
					    
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
									String errMsg =  "<span style=\"color:red;\">" + "The completed date must be later than the on hold date." +  "</span>";
									EditProcessTrackingDetail.this.error(errMsg);
									return;
									}
						
						if (!processTrackingDetailsDTO.getStatus().equals("Completed") && !StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted()))
								//	processTrackingDetailsDTO.getDateOnHold().compareTo(processTrackingDetailsDTO.getDateCompleted()) > 0  ) )
										{
										String errMsg =  "<span style=\"color:red;\">" + "There is a completed date.  Please set status to complete." +  "</span>";
										EditProcessTrackingDetail.this.error(errMsg);
										return;
										}
					
						if (originalOnHoldDate == null && !processTrackingDetailsDTO.getStatus().equals("On hold") && !StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold())
								 && StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted() ) )
							//	processTrackingDetailsDTO.getDateOnHold().compareTo(processTrackingDetailsDTO.getDateCompleted()) > 0  ) )
									{
									String errMsg =  "<span style=\"color:red;\">" + "There is a on hold date.  Please set status to on hold." +  "</span>";
									EditProcessTrackingDetail.this.error(errMsg);
									return;    
									}
						
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold()) && 
								//processTrackingDetailsDTO.getDateStarted().compareTo(processTrackingDetailsDTO.getDateOnHold()) > 0  )
								!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateStarted()) &&
								lCalStartDate.compareTo(lCalOnHold) > 0 && lCalOnHold != null &&  lCalOnHold.compareTo(lMaxOnHoldDate) < 0 )
								
								{
								String errMsg =  "<span style=\"color:red;\">" + "The on hold date must be later than the start date." +  "</span>";
								EditProcessTrackingDetail.this.error(errMsg);
								return;
								}
						
						if (!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateStarted()) && 
								//processTrackingDetailsDTO.getDateStarted().compareTo(processTrackingDetailsDTO.getDateOnHold()) > 0  )
								!StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted()) && 
								     lCalStartDate.compareTo(lCalCompleted) > 0  &&  lCalCompleted!= null &&  (lCalCompleted.compareTo(lMaxCompletedDate)  < 0 ) )
								   
								{   							  
								String errMsg =  "<span style=\"color:red;\">" + "The completed date must be later than the start date or greater than the latest complete date of ." + maxCompltDate +   "</span>";
								EditProcessTrackingDetail.this.error(errMsg);   
								return;      
								}
						
						
						// issue 292    
						 if (   ! StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateCompleted()) &&    lCalCompleted != null && lCalStartDate.compareTo(lCalCompleted) > 0 && lCalCompleted.compareTo(lMaxCompletedDate)  >= 0 )
							processTrackingDetailsDTO.setDateStarted(processTrackingDetailsDTO.getDateCompleted());							
						 if (     ! StringUtils.isNullOrEmpty(processTrackingDetailsDTO.getDateOnHold()) &&   processTrackingDetailsDTO.getStatus().equals("On hold")  &&  lCalOnHold != null && lCalStartDate.compareTo(lCalOnHold) > 0 && lCalOnHold.compareTo(lMaxOnHoldDate)  >= 0 )
							processTrackingDetailsDTO.setDateStarted(processTrackingDetailsDTO.getDateOnHold());
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
						
						// issue 292 on hold taken off by in process
					/*	if (offHoldInProcess)
							{
							System.out.println("in off h9old in process");
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateStartedString = lsdf.format(ptd.getDateStarted().getTime());
							processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateStartedString);
							} */
						
						if (diffDaysExp != 0)
							{
						   // moveDependentTasks(ptd, diffDaysExp);							
							amountToMove = diffDaysExp;
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateStartedString = lsdf.format(ptd.getDateStarted().getTime());
							//(dateToConvert == null) ? "" : sdf.format(dateToConvert.getTime());			
							//processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus);
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
									//(dateToConvert == null) ? "" : sdf.format(dateToConvert.getTime());			
									//processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus);
									processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateCompletedString);
									
									
									//processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus);
									}
									// issue 269									
								}
							else
								{
								dayCompletedToAdd = ChronoUnit.DAYS.between(originalCompletedDate.toInstant(),ptd.getDateCompleted().toInstant());
								//moveDependentTasks(ptd, (int) dayCompletedToAdd);
								amountToMove = (int) dayCompletedToAdd;
								SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
								String dateCompletedString = lsdf.format(ptd.getDateCompleted().getTime());
								//(dateToConvert == null) ? "" : sdf.format(dateToConvert.getTime());			
								//processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus);
								processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateCompletedString);
								}							
							}
						
						if (ptd.getDateOnHold() != null && ptd.getDateCompleted() == null && originalOnHoldDate == null)
							{
							dayOnHoldToAdd = ChronoUnit.DAYS.between(ptd.getDateStarted().toInstant(),ptd.getDateOnHold().toInstant());
							//moveDependentTasks(ptd, (int) dayOnHoldToAdd -1);
							//amountToMove =(int) dayOnHoldToAdd -1;
							amountToMove =(int) dayOnHoldToAdd ;
							//amountToMove = (int) dayOnHoldToAdd -1;
							// issue 287
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateOnHoldString = lsdf.format(ptd.getDateOnHold().getTime());
							//(dateToConvert == null) ? "" : sdf.format(dateToConvert.getTime());			
							//processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus);
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
						
						if (ptd.getDateOnHold() != null && ptd.getDateCompleted() != null && originalCompletedDate == null)
							{
							dayOnHoldToAdd = ChronoUnit.DAYS.between(ptd.getDateOnHold().toInstant(),ptd.getDateCompleted().toInstant());
							// moveDependentTasks(ptd, (int) dayOnHoldToAdd );
							//amountToMove = (int) dayOnHoldToAdd -1;
							amountToMove = (int) dayOnHoldToAdd;
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateOnHoldString = lsdf.format(ptd.getDateOnHold().getTime());
							//(dateToConvert == null) ? "" : sdf.format(dateToConvert.getTime());			
							//processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus);
							processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateOnHoldString);
							} 
						
						if (ptd.getDateOnHold() != null && ptd.getDateCompleted() != null && originalCompletedDate != null)
							{
							dayOnHoldToAdd = ChronoUnit.DAYS.between(originalCompletedDate.toInstant(),ptd.getDateCompleted().toInstant());
							 //moveDependentTasks(ptd, (int) dayOnHoldToAdd );
							//amountToMove = (int) dayOnHoldToAdd -1;
							amountToMove = (int) dayOnHoldToAdd;
							SimpleDateFormat lsdf = new SimpleDateFormat("MM/dd/yyyy");
							String dateCompletedString = lsdf.format(ptd.getDateCompleted().getTime());
							//(dateToConvert == null) ? "" : sdf.format(dateToConvert.getTime());			
							//processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus);
							processTrackingService.doMoveAhead(ptd.getWorkflow().getWfID(), ptd.getExperiment().getExpID(), ptd.getAssay().getAssayId(), amountToMove, ptd.getDetailOrder(), lStatus,dateCompletedString);
							} 
					
						if (ptd.getJobid() != null && (processTrackingDetailsDTO.getJobID()== null || processTrackingDetailsDTO.getJobID().equals("to be assigned")))
							processTrackingDetailsDTO.setJobID(ptd.getJobid());
						String msg = "<span style=\"color:blue;\">" +   "Task Assignment saved for job ID: " + ptd.getJobid() +  "." + "</span>";	
						EditProcessTrackingDetail.this.info(msg);
						}
					catch (RuntimeException r)
						{
						r.printStackTrace();
						String errMsg =  "<span style=\"color:red;\">" + r.getMessage() +  "</span>";
				        EditProcessTrackingDetail.this.error(errMsg);
						}
					catch (Exception e)
						{ 
						e.printStackTrace(); 
						EditProcessTrackingDetail.this.error("Save unsuccessful. Please make sure that smiles is valid."); 
						}		
					
					/********************************************/
					}				
				public void onError(AjaxRequestTarget target, Form form)
					{
					target.add(EditProcessTrackingDetail.this.get("feedback")); 
					} 
				};	
			add(saveChangesButton);
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
		
		}////// END FORM
	       
	}
