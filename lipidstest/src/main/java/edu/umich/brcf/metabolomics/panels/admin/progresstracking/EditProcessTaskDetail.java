////////////////////////////////////////////////////
// EditProcessTaskDetail.java
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
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.LocationService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.layers.dto.ProcessTrackingDTO;

public class EditProcessTaskDetail extends WebPage
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
	TextArea textAreaTask;

	DropDownChoice<String> userNamesDD;
	DropDownChoice<String> taskDescDD;
	List<String> userNamesChoices = new ArrayList<String>();
	List<String> taskNamesChoices = new ArrayList<String>();
	
	EditProcessTaskDetail editProcessTaskDetail = this;// issue 61 2020
	ProcessTrackingDTO processTrackingDTO = new ProcessTrackingDTO();
	Button saveChangesButton;
	EditProcessTaskDetailForm editProcessTaskDetailForm;
	
	public ProcessTrackingDTO getProcessTrackingDTO() { return processTrackingDTO; }
	public void setProcessTrackingDTO(ProcessTrackingDTO processTrackingDTO)  { this.processTrackingDTO = processTrackingDTO; }
		
	public EditProcessTaskDetail(Page backPage, final ModalWindow window) 
		{		
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);		
		add(aFeedback);		
		add(new Label("titleLabel", "Add Task"));
		add(editProcessTaskDetailForm = new EditProcessTaskDetailForm("editProcessTaskDetailForm", "to be assigned", processTrackingDTO, backPage, window,editProcessTaskDetail, null , false));
		}
		
	public EditProcessTaskDetail(Page backPage, IModel cmpModel, final ModalWindow window) 
		{
		this (backPage,cmpModel,window, false);
		}
	
	public EditProcessTaskDetail(Page backPage, IModel cmpModel, final ModalWindow window, boolean isViewOnly) 
		{
		ProcessTracking pt = (ProcessTracking) cmpModel.getObject();	
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);		
		add(aFeedback);	
		add(new Label("titleLabel",  "Edit Task"));
		setProcessTrackingDTO(ProcessTrackingDTO.instance(pt));
		// issue 196
		add(editProcessTaskDetailForm = new EditProcessTaskDetailForm("editProcessTaskDetailForm", pt.getProcessTrackingId(), processTrackingDTO, backPage, window, editProcessTaskDetail, pt, isViewOnly));
		}
	
	public final class EditProcessTaskDetailForm extends Form 
		{
		DropDownChoice selectedParentInventoryDD;
	
		AjaxCheckBox isNoInventoryCheckBox; // issue 196
		public EditProcessTaskDetailForm(final String id, final String taskid, final ProcessTrackingDTO processTrackingsDTO, final Page backPage,  final ModalWindow window, final EditProcessTaskDetail EditProcessTaskDetail, ProcessTracking pt) // issue 27 2020
			{
			this (id, taskid, processTrackingDTO,backPage, window, editProcessTaskDetail, pt, false);
			}
		
		public EditProcessTaskDetailForm(final String id, final String taskid, final ProcessTrackingDTO processTrackingDTO, final Page backPage,   final ModalWindow window, final EditProcessTaskDetail editProcessTaskDetail, final ProcessTracking pt, final boolean isViewOnly) // issue 27 2020
			{
			super(id, new CompoundPropertyModel(processTrackingDTO));
			////// Task descriptions 
			/////
			
			/* taskDescDD= new DropDownChoice("taskDesc",    processTrackingService.allTaskDesc())
		    	{
			
		    	};			
			taskDescDD.setOutputMarkupId(true);	
		    add(taskDescDD); 
			
		    taskDescDD.setRequired(true); */
			
			
			textAreaTask = new TextArea("taskDescNew");
			textAreaTask.setRequired(true);			
			textAreaTask.add(StringValidator.maximumLength(300));
			add(textAreaTask);	
			if (pt != null)
				setValuesForEdit(pt);	
	
			// issue 79
			add( new AjaxLink<Void>("close")
				{
				public void onClick(AjaxRequestTarget target)
					{ 
					window.close(target);
					}
				});	
			
			/**********************/
			
			saveChangesButton = new Button("saveChanges")
				{
				@Override
		
				public void onSubmit() 
					{	
					
					try
						{
						//User userAssignedTo;
						//userAssignedTo = userService.loadUserByFullName(processTrackingDetailsDTO.getAssignedTo());
						ProcessTracking pt = processTrackingService.saveTask(processTrackingDTO);	
						if (pt.getProcessTrackingId() != null && (processTrackingDTO.getTaskID()== null || processTrackingDTO.getTaskID().equals("to be assigned")))
							processTrackingDTO.setTaskID(pt.getProcessTrackingId());
						String msg = "<span style=\"color:blue;\">" +   "Task  saved for job ID: " + pt.getProcessTrackingId() +  "." + "</span>";	
						EditProcessTaskDetail.this.info(msg);
						}
					catch (RuntimeException r)
						{
					    r.printStackTrace();
						String errMsg =  "<span style=\"color:red;\">" + r.getMessage() +  "</span>";
				        EditProcessTaskDetail.this.error(errMsg);
						}
					catch (Exception e)
						{ 
						e.printStackTrace(); 
						EditProcessTaskDetail.this.error("Save unsuccessful. Please make sure that smiles is valid."); 
						}		
					
					/********************************************/
					}				
				public void onError(AjaxRequestTarget target, Form form)
					{
					target.add(EditProcessTaskDetail.this.get("feedback")); 
					} 
				};	
			add(saveChangesButton);
			}
	
	
		// issue 100
		public void setValuesForEdit (ProcessTracking  pt)
			{
			processTrackingDTO.setTaskDesc(pt.getTaskDesc());
			}
		
		}////// END FORM
	       
	}
