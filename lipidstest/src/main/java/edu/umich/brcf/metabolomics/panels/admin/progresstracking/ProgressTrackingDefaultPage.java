/*********************************
 * 
 * Created by Julie Keros Dec 1 2020
 */
package edu.umich.brcf.metabolomics.panels.admin.progresstracking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMailMessage;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.dto.ProcessTrackingDTO;
import edu.umich.brcf.shared.layers.dto.ProcessTrackingDetailsDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;

public class ProgressTrackingDefaultPage extends WebPage 
	{
	@SpringBean
	CompoundService compoundService;
	
	@SpringBean
	ExperimentService experimentService;	
	DropDownChoice<String> userNamesDD;
	DropDownChoice<String> userNamesDDAddTask;
	DropDownChoice workflowDD;
	DropDownChoice expDD;
	DropDownChoice assayDescDD;
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
	AssayService assayService;
	@SpringBean
	METWorksMessageMailer mailer ;
	
	// issue 210
	@SpringBean
	SystemConfigService systemConfigService;
	
	boolean isPlusPressed = false;
	private List<String> availableDaysExpected = Arrays.asList(new String[] {  "1", "2", "3", "4", "5", "6", "7"});
	Object [] procTracObject;
	DropDownChoice expDrowDown;
	TextField taskDescTxt;
	List <TextField> arrayAssignedToTxtFld = new ArrayList <TextField> ();
	boolean pressedDown = false;
	boolean pressedUp = false;
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	TextArea textAreaNotes;
	List<Compound> parentageList;
	FeedbackPanel aFeedback;
	ListView listViewProgressTracking; // issue 61
	ListView listViewProgressTrackingAddedTasks;
	List <ProcessTrackingDetailsDTO> addedTasks = new ArrayList <ProcessTrackingDetailsDTO> ();
	List <ProcessTrackingDetailsDTO> addedTasksObject = new ArrayList <ProcessTrackingDetailsDTO> ();
	Map<String, Integer> daysExpectedMap = new HashMap<String, Integer >();
	Map<String, ProcessTrackingDetailsDTO> processTrackingDTOMap = new HashMap<String, ProcessTrackingDetailsDTO>();
	Map<Integer, ProcessTrackingDetailsDTO> processTrackingDTOAddedTasksMap = new HashMap<Integer, ProcessTrackingDetailsDTO>();
	EditProcessTrackingDetail editProcessTrackingDetail;
	int itemIndex = 0;
	ProcessTracking pt;
	// itemList
	// issue 118
	;
	ProgressTrackingDefaultPage progressTrackingDefaultPage = this;
	
	List<String> userNamesChoices = new ArrayList<String>();
	List<String> wfChoices = new ArrayList<String>();
	
	ProcessTrackingDetailsDTO processTrackingDetailsDTO = new ProcessTrackingDetailsDTO();
	ProcessTrackingDetailsDTO lProcessTrackingDetailsDTO = new ProcessTrackingDetailsDTO();
	private String expID, wfID, input, searchType = "Id";
	private String assignedTo;
	private String assayDesc;
	
    List<String> assignedToList = new ArrayList <String> ();
    List<String> taskDescList = new ArrayList <String> ();
    List <DropDownChoice> userDDList = new ArrayList <DropDownChoice> ();
    
    List <DropDownChoice> tUserDDList = new ArrayList <DropDownChoice> ();
   
    List <METWorksAjaxUpdatingDateTextField> dateAssignedList = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
    List <METWorksAjaxUpdatingDateTextField> tDateAssignedList = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
    List <DropDownChoice> daysExpectedDDLList = new ArrayList <DropDownChoice> ();
    List <DropDownChoice> userDDListadd = new ArrayList <DropDownChoice> ();
    List <DropDownChoice> tuserDDListadd = new ArrayList <DropDownChoice> ();
    List <METWorksAjaxUpdatingDateTextField> dateAssignedListadd = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
    List <METWorksAjaxUpdatingDateTextField> orgDateAssignedListadd = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
    List <DropDownChoice> orgdaysExpectedDDList = new ArrayList <DropDownChoice> ();
    List <TextField> taskDescTextFieldList = new ArrayList <TextField> ();
    List <TextField> orgTaskDescTextFieldList = new ArrayList <TextField> ();
    List <TextArea> orgcommentTextAreaList = new ArrayList <TextArea> ();
    List <TextArea> commentTextAreaList = new ArrayList <TextArea> ();
    Boolean pressedPlus = false;
    boolean pressedDelete = false;
    
	int indexUserDDList  = 0;
	int indexUserDDLista = 0;
	public ProcessTrackingDetailsDTO getProcessTrackingDetailsDTO() { return processTrackingDetailsDTO; }
	public void setProcessTrackingDetailsDTO(ProcessTrackingDetailsDTO processTrackingDetailsDTO)  { this.processTrackingDetailsDTO = processTrackingDetailsDTO; }
    List <String>  listOfTasks = new ArrayList <String> ();
	Calendar dDateStarted;
	
	 @Override
	 public void renderHead( IHeaderResponse response)
	     {
	     super.renderHead(response);
         if (isPlusPressed)
             {
        	 isPlusPressed = false;
        	 response.render(OnDomReadyHeaderItem.forScript( "document.getElementById('scroll-to-bottom').scrollTop= document.getElementById('scroll-to-bottom').scrollHeight;"));
         	 }
	 	 }
	 
	public ProgressTrackingDefaultPage(final String id ) 
		{
		this(id, false);
		}
	
	
	public ProgressTrackingDefaultPage(final String id, boolean modifyDefault) 
		{
		
		listOfTasks.addAll(processTrackingService.allTaskDesc());
		for (String ltask : listOfTasks)
			{
			ProcessTracking lpt = processTrackingService.loadByPTbyTaskID(processTrackingService.grabTaskIdFromDesc(ltask));
			daysExpectedMap.put(lpt.getTaskDesc(), lpt.getDaysRequired());
			}		
		add(new ProgressTrackingDefaultForm("progressTrackingDefaultForm", modifyDefault));
		}
	
	public final class ProgressTrackingDefaultForm extends Form 
		{		
		List <String> inventoryList;
		List <String> assayList;
		String theblankinput;
		String volaliquotUnits;
	    String searchType;
	    AjaxButton addAjaxButton = null;
		public ProgressTrackingDefaultForm (String id, boolean modifyDefault)
			{
		
			super(id, new CompoundPropertyModel(processTrackingDetailsDTO));	
		    
			itemIndex = 0;
			aFeedback = new FeedbackPanel("feedback");
			aFeedback.setEscapeModelStrings(false);		
			add(aFeedback);	
			processTrackingDTOAddedTasksMap = new HashMap<Integer, ProcessTrackingDetailsDTO>();
	
			final ModalWindow modal2= new ModalWindow("modal2");
			modal2.setInitialWidth(1500);
	        modal2.setInitialHeight(600);
	        modal2.setWidthUnit("em");
	        modal2.setHeightUnit("em"); 
	        addAjaxButton = buildAddButton("addButton");
	        //////addAjaxButton.add(( new AttributeModifier("onclick", "alert('hey'); document.getElementById('scroll-to-bottom').scrollTop= document.getElementById('scroll-to-bottom').scrollHeight;alert('hey again')")));	
	        add (addAjaxButton);
	        add(expDrowDown = buildExperimentDropDown("experimentDropDown"));
	        add(workflowDD = buildWorkFlowDropDown("wfDropDown"));
	        add (assayDescDD = buildAssayDescDropDown("assayDescDropDown"));
            assayDescDD.add(buildStandardFormComponentUpdateBehavior("change", "updateAssay", null, null));
	        expDrowDown.add(buildStandardFormComponentUpdateBehavior("change", "updateExperiment", null, null));
	        workflowDD.add(buildStandardFormComponentUpdateBehavior("change", "updateWorkFlow", null, null));
	        add(new Label("titleLabel", "Assign Workflow"));
	        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        	{
	            public void onClose(AjaxRequestTarget target)
	            	{
	            	target.add(progressTrackingDefaultPage);
	            //	List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("assigned_task_notification");
	          //      sendOutEmails (listDto2, email_contacts, mailer);
	            	}
	        	}); 
	        add(modal2);
	        add( new AjaxButton ("saveDefault")
	    		{
	        	@Override
	    		public void onSubmit(AjaxRequestTarget target)
	    			{ 
	        		List <ProcessTrackingDetailsDTO> listDto2 = createDTOAddedArray();
	        		processTrackingService.deleteTracking(listDto2.get(0).getExpID(), listDto2.get(0).getAssayID());
	        		String err = errcheck(listDto2);
	        		if  (!StringUtils.isEmptyOrNull(err))
	        			{
	        			target.appendJavaScript(StringUtils.makeAlertMessage(err));
	        			return;
	        			}
	        		processTrackingService.saveDefaultDTOs(listDto2, modifyDefault);
	        		List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("assigned_task_notification");
	        		// issue 210	        		
	    			String msg = "Workflow: " +  listDto2.get(0).getWfID() +  " saved for experiment: " + expID  +  ".";
	    			target.appendJavaScript(StringUtils.makeAlertMessage(msg));
	    			modal2.close(target);
	    			pressedPlus = false;
	    			pressedDelete = false;
	    			listOfTasks.clear();
	    			listOfTasks.addAll(processTrackingService.allTaskDesc());
	    			//////sendOutEmails (listDto2, email_contacts, mailer);
	    			}
	    		});	
	        
	        
	    	add( new AjaxLink<Void>("close")
			{
			public void onClick(AjaxRequestTarget target)
				{ 
				modal2.close(target);
				}
			});	
	        
			///// issue 94
			//// issue 120
	    ////////////////////////////////////////////////////////////////////////////
	    	
	    	   add(listViewProgressTrackingAddedTasks = new ListView("addedTasks", new PropertyModel(this, "addedTasks")) 
	    	        {
	    			public String getAssignedTo() { return assignedTo; }
	            	public void setAssignedTo (String u) { assignedTo = u; }
	            	
	             	private AjaxButton buildDeleteButton(String id, int indexx, ProcessTrackingDetailsDTO lProcessTrackingDetailsDTO)			
	        		{
	        		return new AjaxButton ("deleteButton")
	        			{
	        			
	        			@Override
	        			public void onSubmit(AjaxRequestTarget target)
	        				{
	        				addedTasks.remove(lProcessTrackingDetailsDTO);
	        				target.add(progressTrackingDefaultPage);      			
	        				int ii = 0;
	        				List <TextField> tTaskDescTextFieldList = new ArrayList <TextField> ();
	        				tTaskDescTextFieldList.addAll(taskDescTextFieldList);
	        				
	        			   /******************************************/ 
	        			    dateAssignedListadd.remove(indexx);
    						userDDListadd.remove(indexx);
    						tTaskDescTextFieldList.remove(indexx);
    						commentTextAreaList.remove(indexx);
    						daysExpectedDDLList.remove(indexx);
    						/******************************************/ 
	        				
	        				taskDescTextFieldList = new ArrayList <TextField> ();
	        				taskDescTextFieldList.addAll(tTaskDescTextFieldList);
	        				pressedPlus = false;
	        				pressedDelete = true;
	        				orgcommentTextAreaList = new ArrayList <TextArea> ();
	        				orgcommentTextAreaList.addAll (commentTextAreaList);
	
	        				tuserDDListadd = new ArrayList <DropDownChoice> ();
	        				tuserDDListadd.addAll(userDDListadd);
	        				orgTaskDescTextFieldList = new ArrayList <TextField> ();
	        				orgTaskDescTextFieldList.addAll(taskDescTextFieldList);
	        				orgDateAssignedListadd = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
	        				orgDateAssignedListadd.addAll(dateAssignedListadd);
	        				
	        				indexUserDDList= 0;
	        				indexUserDDLista= 0;
	        				itemIndex = 0;
	        				taskDescTextFieldList = new ArrayList <TextField> ();
	        				dateAssignedListadd = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
	        				userDDListadd = new ArrayList <DropDownChoice> ();
	        				commentTextAreaList  = new ArrayList <TextArea> ();
	        					
	        				}
	        			  
	        			};
	        		}
	            	
	            	//////////
	            	
	            	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, String taskDesc, String assignedToString, ProcessTrackingDetailsDTO lProcessTrackingDetailsDTO, int indexx)
		        		{
		        		return new AjaxFormComponentUpdatingBehavior(event)
		        	        {
		        		    @Override
		        		    protected void onUpdate(AjaxRequestTarget target)
		        		    	{
		        		    	List<String> newAliquotList = new ArrayList<String>();
		        		    	switch (response)
		        		        	{
		        		    	    case "updateProcessTrackingDTOMap" :
		        		    	    	processTrackingDTOMap.put(taskDesc, processTrackingDetailsDTO);
		        		    	    	break;
		        		    	    case "updateExperiment" :
		        		    	    	break;	
		        		    	    case "updateWorkFlow" :
		        		    	    	List<Object[]> nList = processTrackingService.loadAllDefaultTasksAssigned(wfID);
		        		    	    	addedTasks = new ArrayList <ProcessTrackingDetailsDTO> ();
		        		    	    	
		        		    	    	for (Object [] lObj : nList)
			        						{
			        						ProcessTrackingDetailsDTO ptd = new ProcessTrackingDetailsDTO();
			        						ptd.setTaskDesc(lObj[1].toString());
			        						ptd.setDateAssigned(lObj[2].toString());
			        						ptd.setAssignedTo(lObj[6].toString());
			        						ptd.setDetailOrder(Integer.parseInt(lObj[8].toString()));
			        						addedTasks.add(ptd);
			        						}
		        
		        		    	    	target.add(progressTrackingDefaultPage);
		        		    	    	break	;
		        		    	    case "updateUser" : 
		        		    	    	processTrackingDetailsDTO.setAssignedTo(assignedToString);
		        		    	    	processTrackingDTOMap.put(taskDesc, processTrackingDetailsDTO);
		        		    	    	break;	
		        		    	    case "updateUserAddTasks" :
		        		    	    	break;
		        		    	    case "updateDaysExpected" :
		        		    	    	break;
		        			    	}
		        		    	}
		        		    };
		        		}
	
	            	private AjaxButton buildUpArrow (final String id, int index)
	            		{            		
	            		return new AjaxButton ("upArrow")
		        			{	        			
		        			@Override
		        			public void onSubmit(AjaxRequestTarget target)
		        				{ 
						    	indexUserDDLista = 0;
						    	itemIndex = 0;
						    	if (index > 0)
						    		{	
						    		ProcessTrackingDetailsDTO pd = addedTasks.get(index);
						    		TextField tf = taskDescTextFieldList.get(index);
						    		METWorksAjaxUpdatingDateTextField df = dateAssignedListadd.get(index);
						    		DropDownChoice ua = userDDListadd.get(index);
						    		TextArea cmt = commentTextAreaList.get(index);
						    		Integer lDorder = addedTasks.get(index).getDetailOrder();
						    		if (lDorder == null)
						    			lDorder = addedTasks.size();
						    		Integer lDorderPrev = addedTasks.get(index-1).getDetailOrder();
						    	    addedTasks.set(index, addedTasks.get(index-1));
						    	    addedTasks.set(index-1 , pd);
						    	    addedTasks.get(index).setDetailOrder(lDorder);
						    	    addedTasks.get(index-1).setDetailOrder(lDorderPrev);
						    	    taskDescTextFieldList.set(index, taskDescTextFieldList.get(index-1));
						    	    taskDescTextFieldList.set(index-1, tf);
						    	    dateAssignedListadd.set(index,dateAssignedListadd.get(index-1));
						    	    dateAssignedListadd.set(index-1,  df);
						    	    userDDListadd.set(index,  userDDListadd.get(index-1) );
						    	    userDDListadd.set(index-1, ua);
						    	    commentTextAreaList.set(index,  commentTextAreaList.get(index-1) );
						    	    commentTextAreaList.set(index-1, cmt);
						    		resetPlusComponents ();
						    		target.add(progressTrackingDefaultPage);				    		
						    		}				        
		        				}   			  
		        			};
	            		}
	            	
	            	private AjaxButton buildDownArrow (final String id, int index)
	            		{
	            		
	            		return new AjaxButton ("downArrow")
	        			{
	        			
	        			@Override
	        			public void onSubmit(AjaxRequestTarget target)
	        				{
    				    	indexUserDDLista = 0;
    				    	itemIndex = 0;
    				    	
    				    	if (index < addedTasks.size() -1)
    				    		{
    				    		ProcessTrackingDetailsDTO pd = addedTasks.get(index);
    				    		TextField tf = taskDescTextFieldList.get(index);
    				    		METWorksAjaxUpdatingDateTextField df = dateAssignedListadd.get(index);
    				    		DropDownChoice ua = userDDListadd.get(index);
    				    		TextArea cmt = commentTextAreaList.get(index);
    				    		Integer lDorder = addedTasks.get(index).getDetailOrder();
    				    		if (lDorder == null)
    				    			lDorder = addedTasks.size();
    				    		Integer lDorderNext = addedTasks.get(index+1).getDetailOrder();	    		
    				    		if (lDorderNext == null)
    				    			lDorderNext = addedTasks.size();
    				    		addedTasks.set(index, addedTasks.get(index+1));
    				    	    addedTasks.set(index+1 , pd);    	    
    				    	    addedTasks.get(index).setDetailOrder(lDorder);
    				    	    addedTasks.get(index+1).setDetailOrder(lDorderNext);    				    	    
    				    	    taskDescTextFieldList.set(index, taskDescTextFieldList.get(index+1));
    				    	    taskDescTextFieldList.set(index+1, tf);
    				    	    dateAssignedListadd.set(index,dateAssignedListadd.get(index+1));
    				    	    dateAssignedListadd.set(index+1,  df);
    				    	    userDDListadd.set(index,  userDDListadd.get(index+1) );
    				    	    userDDListadd.set(index+1, ua);
    				    	    commentTextAreaList.set(index,  commentTextAreaList.get(index+1) );
    				    	    commentTextAreaList.set(index+1, cmt);
    				    		resetComponents ();
    				    		target.add(progressTrackingDefaultPage);
    				    		
    				    		}

    				    	}

	        			};
	        		}

	            	public DropDownChoice buildUserDropDown(final String id, ProcessTrackingDetailsDTO lProcessTrackingDetailsDTO)
		    		{
		    		setAssignedTo(lProcessTrackingDetailsDTO.getAssignedTo());		
		    		DropDownChoice userDDAssignTasks = new DropDownChoice<String>(id, new PropertyModel(lProcessTrackingDetailsDTO, "assignedTo" ));
		    		userDDAssignTasks.setChoices(userService.allAdminNames(false));		    		  	
		    		return userDDAssignTasks;
		    		}           	

	    			public void populateItem(final ListItem listItem) 
						{
	    				  
	    				listItem.add (buildDownArrow ("downArrow", itemIndex ));
	    				listItem.add (buildUpArrow ("upArrow", itemIndex )); 
	    				lProcessTrackingDetailsDTO = (ProcessTrackingDetailsDTO) listItem.getModelObject();		
	    				ProcessTrackingDetailsDTO lpDTO = new ProcessTrackingDetailsDTO();
	    				
	    				TextField taskDescTxt =  new TextField("taskdescriptiona", new PropertyModel(lpDTO, "taskDesc")
	    						{
	    					
	    						});
	    				taskDescTxt.setDefaultModelObject(addedTasks.get(itemIndex).getTaskDesc())	;	
	    				listItem.add(taskDescTxt);		
	    				taskDescTextFieldList.add(taskDescTxt);
	    				listItem.add(buildDeleteButton("deleteButton", indexUserDDLista, lProcessTrackingDetailsDTO));
	    				METWorksAjaxUpdatingDateTextField dateFldAssigned =  new METWorksAjaxUpdatingDateTextField("dateAssignedda", new PropertyModel<String>(lpDTO, "dateAssigned"), "dateAssignedda")
		    				{
		    				@Override
		    				protected void onUpdate(AjaxRequestTarget target)  
		    			        {
		    			        }
		    			    public boolean isEnabled()
		    				    {
		    				    return (userService.isAccountAdmin(((MedWorksSession) Session.get()).getCurrentUserId()));
		    			    	}
		    						
		    				};		
						
	    				dateFldAssigned.setDefaultStringFormat(ProcessTracking.ProcessTracking_DATE_FORMAT);
	    				dateFldAssigned.add(buildStandardFormComponentUpdateBehavior("change", "updateProcessTrackingDTOMapaa", lProcessTrackingDetailsDTO.getTaskDesc(), null, lProcessTrackingDetailsDTO, itemIndex));
	    				dateFldAssigned.setDefaultModelObject(addedTasks.get(itemIndex).getDateAssigned());
	    				//dateFldAssigned.setRequired(true); 
	    				Calendar dateToConvert = Calendar.getInstance();
	    				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	    				processTrackingDetailsDTO.setDateAssigned ( dateToConvert == null ? "" : sdf.format(dateToConvert.getTime()));
	    				lpDTO.setDateAssigned ( dateToConvert == null ? "" : sdf.format(dateToConvert.getTime()));
	    				
	    				listItem.add(dateFldAssigned);
	    				dateAssignedListadd.add(dateFldAssigned);
	    				
	    				
	    				      setAssignedTo (lProcessTrackingDetailsDTO.getAssignedTo());
	    					  DropDownChoice userNamesDDAssigned = new DropDownChoice<String>("assignedToa", new PropertyModel(lpDTO, "assignedTo" ), new ArrayList <String> () );
	    					  userNamesDDAssigned.setChoices(userService.allAdminNames(false));
	    					  
	    					   if (userNamesDDAssigned != null)
	    						   userNamesDDAssigned.add(buildStandardFormComponentUpdateBehavior("change", "updateUserAddTasks", lProcessTrackingDetailsDTO.getTaskDesc(), lProcessTrackingDetailsDTO.getAssignedTo(), lProcessTrackingDetailsDTO , itemIndex));
	    					  
	    					   listItem.add(userNamesDDAssigned);	
	    					   ///////////////////////
	    					   
	    					   setAssignedTo (lProcessTrackingDetailsDTO.getAssignedTo());
		    					  DropDownChoice daysExpectedDD = new DropDownChoice<String>("daysExpected", new PropertyModel(lpDTO, "daysExpected" ), new ArrayList <String> () );
		    					  daysExpectedDD.setChoices(availableDaysExpected);
		    					  
		    					   if (daysExpectedDD != null)
		    						   daysExpectedDD.add(buildStandardFormComponentUpdateBehavior("change", "updateDaysExpected", lProcessTrackingDetailsDTO.getTaskDesc(), lProcessTrackingDetailsDTO.getAssignedTo(), lpDTO, itemIndex));
		    					   
		    					   listItem.add(daysExpectedDD);
		    					   daysExpectedDDLList.add(daysExpectedDD);
	                               
		    					   if (daysExpectedMap.get(lpDTO.getTaskDesc()) != null) 
		    						   lpDTO.setDaysExpected (daysExpectedMap.get(lpDTO.getTaskDesc()).toString());
		    					  ///// if (StringUtils.isEmptyOrNull(lpDTO.getDaysExpected()))
		    					   else   
		    						   lpDTO.setDaysExpected("1");
	    					   /////////////////////

	    					   userNamesDDAssigned.setDefaultModelObject(addedTasks.get(itemIndex).getAssignedTo());	       
	    				       textAreaNotes = new TextArea("commentsa", new Model(lProcessTrackingDetailsDTO.getComments()));
	    					   listItem.add(textAreaNotes)	;			
	    					   textAreaNotes.add(StringValidator.maximumLength(4000));   
	    					   commentTextAreaList.add(textAreaNotes);
	    					  
	    					   
	    			    userDDListadd.add(userNamesDDAssigned);	   
	    				processTrackingDTOAddedTasksMap.put(itemIndex,lProcessTrackingDetailsDTO ) ;  
	    				assignedToList.add(lProcessTrackingDetailsDTO.getAssignedTo());
	    				taskDescList.add(lProcessTrackingDetailsDTO.getTaskDesc());
	    				itemIndex++;
	    				
	    				
	    				if (pressedDelete || pressedPlus || pressedDown || pressedUp ) 
							{
							if (orgTaskDescTextFieldList.size() > 0 && indexUserDDLista < orgDateAssignedListadd.size())
								{
		    					userDDListadd.get(indexUserDDLista).setDefaultModelObject(tuserDDListadd .get(indexUserDDLista).getDefaultModelObject());		
								dateAssignedListadd.get(indexUserDDLista).setDefaultModelObject(orgDateAssignedListadd.get(indexUserDDLista).getDefaultModelObject());
								taskDescTextFieldList.get(indexUserDDLista).setDefaultModelObject(orgTaskDescTextFieldList .get(indexUserDDLista).getDefaultModelObject());
								commentTextAreaList.get(indexUserDDLista).setDefaultModelObject(orgcommentTextAreaList .get(indexUserDDLista).getDefaultModelObject());
								}
							
							indexUserDDLista++;
							}   	
						}
	    			});
	   
	        add(listViewProgressTracking = new ListView("trackingDetail", new PropertyModel(this, "trackingDetail")) 
				{
	        	public String getAssignedTo() { return assignedTo; }
	        	public void setAssignedTo (String u) { assignedTo = u; }
	        	
	        private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, String taskDesc, String assignedToString, DropDownChoice userdd , METWorksAjaxUpdatingDateTextField dateFldAssigned )
				{
				return new AjaxFormComponentUpdatingBehavior(event)
			        {
				    @Override
				    protected void onUpdate(AjaxRequestTarget target)
				    	{
				    	List<String> newAliquotList = new ArrayList<String>();
				    	//int i=0;
				    	switch (response)
				        	{
				    	    case "updateDateAssigned" :
				    	    	break	;
				    	    
				    	    case "updateExperiment" :
				    	    	break;	
				    	    case "updateWorkFlow" :
				    	    	target.add(progressTrackingDefaultPage);
				    	    	break	;
				    	    case "updateUser" : 
				    	    	break;	
				    	    case "updateUserAddTasks" :
				    	        processTrackingDetailsDTO.setAssignedTo(assignedToString);
					    	}
				    	}
				    };
				} 
	        	
	        	
	        	public DropDownChoice buildUserDropDown(final String id, String userAssignedStr, ProcessTrackingDetailsDTO lpDTO)
		    		{
	        		ProcessTrackingDetailsDTO llDto = new ProcessTrackingDetailsDTO();;
	        		String userLocalString = userAssignedStr;
	        		
	        		llDto.setAssignedTo(lpDTO.getAssignedTo());
		    		setAssignedTo(userAssignedStr);
		    		DropDownChoice userDropDwn = new DropDownChoice<String>(id,  new PropertyModel (lpDTO, "assignedTo"), new ArrayList <String> ());
		            userDropDwn.setChoices (userService.allAdminNames(false));
		    	    return userDropDwn;
		    		}
	        	
				public void populateItem(final ListItem listItem) 
					{
					procTracObject = (Object []) listItem.getModelObject();
					ProcessTrackingDetailsDTO lpDTO = new ProcessTrackingDetailsDTO();
					listItem.add(new Label("taskdescription",new Model(procTracObject[1].toString())));
					processTrackingDetailsDTO.setDateAssigned(procTracObject[2].toString());			
					processTrackingDetailsDTO.setTaskDesc(procTracObject[1].toString());
					lpDTO.setAssignedTo(procTracObject[6].toString());
					METWorksAjaxUpdatingDateTextField dateFldAssigned =  new METWorksAjaxUpdatingDateTextField("dateAssignedd", new PropertyModel<String>(lpDTO, "dateAssigned"), "dateAssignedd")
						{
						@Override
						protected void onUpdate(AjaxRequestTarget target)  
					        {
					        }
					    public boolean isEnabled()
						    {
						    return (userService.isAccountAdmin(((MedWorksSession) Session.get()).getCurrentUserId()));
					    	}
								
						};	
						
					dateFldAssigned.setDefaultStringFormat(ProcessTracking.ProcessTracking_DATE_FORMAT);
					dateFldAssigned.add(buildStandardFormComponentUpdateBehavior("change", "updateDateAssigned", procTracObject[1].toString(), null, null,dateFldAssigned));
					
					listItem.add(dateFldAssigned);
				
					User user = userService.loadById(procTracObject[6].toString());
					
				   setAssignedTo (procTracObject[6].toString());
				   DropDownChoice<String> userNamesDD;
				   listItem.add(userNamesDD = buildUserDropDown("assignedTo", assignedTo, lpDTO ));
				   setAssignedTo (processTrackingDetailsDTO.getAssignedTo());
			       userNamesDD.add(buildStandardFormComponentUpdateBehavior("change", "updateUser", procTracObject[1].toString(), lpDTO.getAssignedTo(), userNamesDD, dateFldAssigned));
			       lpDTO.setDateAssigned(procTracObject[2].toString()); 
			        userDDList.add(userNamesDD);
			        dateAssignedList.add(dateFldAssigned);
					textAreaNotes = new TextArea("comments", new Model(procTracObject[5].toString()));
				    listItem.add(textAreaNotes)	;			
					textAreaNotes.add(StringValidator.maximumLength(4000));
					if (pressedPlus) 
						{
						userDDList.get(indexUserDDList).setDefaultModelObject(tUserDDList.get(indexUserDDList).getDefaultModelObject());	
						dateAssignedList.get(indexUserDDList).setDefaultModelObject(tDateAssignedList.get(indexUserDDList).getDefaultModelObject());
						indexUserDDList++;
						}
					}   
				});			
	        }
	
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehaviorModified(String event, final String response, String taskDesc, String assignedToString, ProcessTrackingDetailsDTO lProcessTrackingDetailsDTO, int indexx)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
	        {
		    @Override
		    protected void onUpdate(AjaxRequestTarget target)
		    	{
		    	List<String> newAliquotList = new ArrayList<String>();
		    	switch (response)
		        	{
		    	    case "updateProcessTrackingDTOMap" :
		    	    	processTrackingDTOMap.put(taskDesc, processTrackingDetailsDTO);
		    	    	break;
		    	    case "updateExperiment" :
		    	    	break;	
		    	    case "updateWorkFlow" :
		    	    	target.add(progressTrackingDefaultPage);
		    	    	break	;
		    	    case "updateUser" : 
		    	    	processTrackingDetailsDTO.setAssignedTo(assignedToString);
		    	    	processTrackingDTOMap.put(taskDesc, processTrackingDetailsDTO);
		    	    	break;	
		    	    case "updateUserAddTasks" :
		    	        processTrackingDTOAddedTasksMap.put(indexx, lProcessTrackingDetailsDTO);
			    	}
		    	}
		    };
		}

		private AjaxButton buildAddButton(String id)	
			{
			return new AjaxButton (id)
				{
				
				@Override
				public void onSubmit(AjaxRequestTarget target)
					{
					
					tUserDDList = new ArrayList <DropDownChoice> ();
					tDateAssignedList = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
					
					
					/*************************************************/			
					orgDateAssignedListadd = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
					tuserDDListadd = new ArrayList <DropDownChoice> ();
					orgTaskDescTextFieldList = new ArrayList <TextField> ();					
					orgcommentTextAreaList = new ArrayList <TextArea> ();
					orgdaysExpectedDDList = new ArrayList <DropDownChoice> ();
				  	addedTasks.add(new ProcessTrackingDetailsDTO ());
				  	pressedPlus = true;
				  	indexUserDDList = 0;
				  	indexUserDDLista = 0;
				  	itemIndex = 0;
				  	tUserDDList.addAll(userDDList);
				  	tDateAssignedList.addAll(dateAssignedList);
				  	orgcommentTextAreaList.addAll(commentTextAreaList);
				  	/**********************************************/
				  	orgDateAssignedListadd.addAll (dateAssignedListadd);
				  	tuserDDListadd.addAll(userDDListadd);
				  	orgTaskDescTextFieldList.addAll(taskDescTextFieldList);	
				  
				  	/////////////////////
				 
				  	target.add(progressTrackingDefaultPage);
				  	/////////////////////
				  	daysExpectedDDLList = new ArrayList <DropDownChoice> ();
				  	dateAssignedListadd = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
				  	userDDListadd = new ArrayList <DropDownChoice> ();
				  	taskDescTextFieldList = new ArrayList <TextField> ();
				  	commentTextAreaList = new ArrayList <TextArea> ();
				  	/***********************************************/
				  	
				  	userDDList = new ArrayList <DropDownChoice> ();
				  	dateAssignedList = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
				  	pressedPlus = true;
    				pressedDelete = false;
				  	/******************************************/  	
    				isPlusPressed = true;
    				System.out.println("here is plus pressed true");
					}
				};
			}
		
		
		public DropDownChoice buildExperimentDropDown(final String id)
			{
			expDD = new DropDownChoice<String>(id, new PropertyModel<String>(this, "expID"), new ArrayList <String> ())
				{
				};
				
			expDD.setChoices(experimentService.expIdsByInceptionDate());
			return expDD;
			}
		
		public DropDownChoice buildWorkFlowDropDown(final String id)
			{		
      		workflowDD =  new DropDownChoice<String>(id, new PropertyModel(this, "wfID" ), new ArrayList <String> ())
					{
					
					};		
		 	workflowDD.setChoices (processTrackingService.loadAllWorkFlows())	;	
					
					
			return workflowDD;
			
			}
		
		public DropDownChoice buildAssayDescDropDown(final String id)
			{		
	  		assayDescDD =  new DropDownChoice<String>(id, new PropertyModel(this, "assayDesc" ), new ArrayList <String> ())
					{
					
					};		
		 	assayDescDD.setChoices (assayService.allAssayNamesForExpId(expID, false))	;	
					
					
			return assayDescDD;
			
			}

		public String getExpID() { return expID; }
		public void setExpID (String e) { expID = e; }
		public String getWfID() { return wfID; }
		public void setWfID (String w) { wfID = w; }
		public String getAssayDesc() { return assayDesc; }
		public void setAssayDesc (String a) { assayDesc = a; }
		
		public String getSearchType()
			{
			return searchType;
			}
	
		public void setSearchType(String searchType)
			{
			this.searchType = searchType;
			}
		
		private List <ProcessTrackingDetailsDTO> createDTOAddedArray()
			{
			int index = 0;
			List <ProcessTrackingDetailsDTO> dtoList = new ArrayList <ProcessTrackingDetailsDTO> () ;
			//List <ProcessTrackingDetailsDTO> theDtoList = (List <ProcessTrackingDetailsDTO>) listViewProgressTrackingAddedTasks.getModelObject();
			List <ProcessTrackingDetailsDTO> theDtoList = new ArrayList <ProcessTrackingDetailsDTO> ();
			index = 0;
			int ii = 0;
			int iDate = 0;
			int totalDaysExpectedToSpan = 0;
			for ( TextField tf : taskDescTextFieldList)
				{
				iDate ++ ;
				ProcessTrackingDTO processTrackingDTO = new ProcessTrackingDTO();
				processTrackingDTO.setTaskDesc(tf.getDefaultModelObjectAsString());
				ProcessTracking pt;
				listOfTasks.addAll(processTrackingService.allTaskDesc());
				if (listOfTasks.contains(tf.getDefaultModelObjectAsString()))
					{
					String taskid = processTrackingService.grabTaskIdFromDesc(tf.getDefaultModelObjectAsString());
					}
				else
					{
					pt = processTrackingService.saveTask(processTrackingDTO);	
					}
				ProcessTrackingDetailsDTO lilProcessTrackingDetailsDTO = new ProcessTrackingDetailsDTO ();
				lilProcessTrackingDetailsDTO.setTaskDesc (tf.getDefaultModelObjectAsString());
				lilProcessTrackingDetailsDTO.setExpID(expID);
				lilProcessTrackingDetailsDTO.setAssayID(assayDescDD.getDefaultModelObjectAsString());
				lilProcessTrackingDetailsDTO.setDaysExpected(daysExpectedDDLList.get(index).getDefaultModelObjectAsString());
				dDateStarted = Calendar.getInstance();
				dDateStarted.add(Calendar.DAY_OF_MONTH, totalDaysExpectedToSpan);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				String datestr = sdf.format(dDateStarted.getTime());
				
				lilProcessTrackingDetailsDTO.setDetailOrder(addedTasks.get(index).getDetailOrder());
				lilProcessTrackingDetailsDTO.setDateStarted(datestr);
				lilProcessTrackingDetailsDTO.setAssignedTo(userDDListadd.get(index).getDefaultModelObjectAsString());
				lilProcessTrackingDetailsDTO.setDateAssigned(dateAssignedListadd.get(index).getDefaultModelObjectAsString());
				workflowDD.setEscapeModelStrings(false);
				lilProcessTrackingDetailsDTO.setWfID(workflowDD.getDefaultModelObjectAsString());
				lilProcessTrackingDetailsDTO.setAssayID(StringParser.parseId(assayDescDD.getDefaultModelObjectAsString()));
				lilProcessTrackingDetailsDTO.setComments(commentTextAreaList.get(index).getDefaultModelObjectAsString());
				lilProcessTrackingDetailsDTO.setStatus("In queue");
				totalDaysExpectedToSpan = totalDaysExpectedToSpan + Integer.parseInt(daysExpectedDDLList.get(index).getDefaultModelObjectAsString());
				theDtoList.add(lilProcessTrackingDetailsDTO);
				
				// issue 210
			//	List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("assigned_task_notification");
				String msg = "";	
				index++;
				ii++;
				
				}
			List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("assigned_task_notification");
			
			return theDtoList;
			}
	
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, String taskDesc, String assignedToString)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
		        {
			    @Override
			    protected void onUpdate(AjaxRequestTarget target)
			    	{
			    	List<String> newAliquotList = new ArrayList<String>();
			    	switch (response)
			        	{
			    	    case "updateProcessTrackingDTOMap" :
			    	    	processTrackingDTOMap.put(taskDesc, processTrackingDetailsDTO);
			    	    	break;
			    	    case "updateExperiment" :
			    	    	assayDescDD.setChoices (assayService.allAssayNamesForExpId(expID, false))	;
			    	    	indexUserDDList = 0;
						  	indexUserDDLista = 0;
						  	itemIndex = 0;
						  	////// issue 210 resetting.....
						  	resetPlusComponents();
			    	    	target.add(progressTrackingDefaultPage);
			    	    	break;	
			    	    case "updateAssay" :
			    	      	////// issue 210 resetting.....
						  	resetPlusComponents();
			    	        break;
			    	    case "updateWorkFlow" :
    		    	    	List<Object[]> nList = processTrackingService.loadAllDefaultTasksAssigned(wfID);
    		    	    	addedTasks = new ArrayList <ProcessTrackingDetailsDTO> ();
    		    	    	indexUserDDList = 0;
    					  	indexUserDDLista = 0;
    					  	itemIndex = 0;
    					  	////// issue 210 resetting.....
						  	resetPlusComponents();
    		    	    	for (Object [] lObj : nList)
        						{
        						ProcessTrackingDetailsDTO ptd = new ProcessTrackingDetailsDTO();
        						ptd.setTaskDesc(lObj[1].toString());
        						ptd.setDateAssigned(lObj[2].toString());
        						ptd.setAssignedTo(lObj[6].toString());
        						ptd.setDetailOrder(Integer.parseInt(lObj[7].toString()));
        						addedTasks.add(ptd);
        						}
    		    	    	target.add(progressTrackingDefaultPage);
			    	    	target.add(progressTrackingDefaultPage);
			    	    	break	;
			    	    case "updateUser" :    	
			    	    	processTrackingDetailsDTO.setAssignedTo(assignedToString);
			    	    	processTrackingDTOMap.put(taskDesc, processTrackingDetailsDTO);
			    	    	break;	
			    	    case "updateUserAddTasks" :
			    	    	
			    	        processTrackingDetailsDTO.setAssignedTo(assignedToString);
				    	}
			    	}
			    };
			} 
		
		
		
		// issue 94	
		
		public void resetComponents ()
			{
	
			int ii = 0;			
			pressedPlus = false;
			pressedDelete = false;
			pressedDown = true;
			pressedUp = false;
			tuserDDListadd = new ArrayList <DropDownChoice> ();
			tuserDDListadd.addAll(userDDListadd);
			orgTaskDescTextFieldList = new ArrayList <TextField> ();
			orgTaskDescTextFieldList.addAll(taskDescTextFieldList);
		
			orgTaskDescTextFieldList = new ArrayList <TextField> ();
			orgTaskDescTextFieldList.addAll(taskDescTextFieldList);
			
			orgcommentTextAreaList  = new ArrayList <TextArea> ();
			orgcommentTextAreaList.addAll(commentTextAreaList);
			
			orgDateAssignedListadd = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
			orgDateAssignedListadd.addAll(dateAssignedListadd);
			orgdaysExpectedDDList.addAll(daysExpectedDDLList);
			
			indexUserDDList= 0;
			indexUserDDLista= 0;
			
			taskDescTextFieldList = new ArrayList <TextField> ();
			dateAssignedListadd = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
			userDDListadd = new ArrayList <DropDownChoice> ();
			commentTextAreaList = new ArrayList <TextArea> ();
			daysExpectedDDLList = new ArrayList <DropDownChoice> ();
		
			//////////////////////////////	
			}
		
		
		public void resetPlusComponents ()
			{
			int ii = 0;			
			pressedPlus = false;
			pressedDelete = false;
			pressedDown = false;
			pressedUp = true;			
			tuserDDListadd = new ArrayList <DropDownChoice> ();
			tuserDDListadd.addAll(userDDListadd);
			orgTaskDescTextFieldList = new ArrayList <TextField> ();
			orgTaskDescTextFieldList.addAll(taskDescTextFieldList);
			orgcommentTextAreaList = new ArrayList <TextArea> ();
			orgcommentTextAreaList.addAll(commentTextAreaList);
			orgdaysExpectedDDList = new ArrayList <DropDownChoice> ();
			orgdaysExpectedDDList.addAll(daysExpectedDDLList);
			orgDateAssignedListadd = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
			orgDateAssignedListadd.addAll(dateAssignedListadd);			
			indexUserDDList= 0;
			indexUserDDLista= 0;
			taskDescTextFieldList = new ArrayList <TextField> ();
			dateAssignedListadd = new ArrayList <METWorksAjaxUpdatingDateTextField> ();
			userDDListadd = new ArrayList <DropDownChoice> ();
			commentTextAreaList = new ArrayList <TextArea> ();
			//////////////////////////////	
			}
		
		private void setModalDimensions(String linkID, ModalWindow modal1)
			{ 
			modal1.setInitialWidth(1500);
			modal1.setInitialHeight(790);
			}
		
		public List<Object[]> getTrackingDetail()
			{
		List<Object[]> nList = processTrackingService.loadAllDefaultTasksAssigned(processTrackingDetailsDTO.getWfID());
			
			///////List<Object[]> nList = new ArrayList <Object []> ();
			return null;
			}
		
		private String buildHTMLScrollString()
			{
			String htmlStr = "";
			htmlStr = htmlStr + " window.location.reload(true);";
			htmlStr = htmlStr + "var objDiv = " + "document.getElementById(" +  "\"" + "scroll-to-bottom" + "\"" + ");" ;
			htmlStr = htmlStr + " objDiv.scrollTop = objDiv.scrollHeight; ";
			return htmlStr;
			} 
		
		public List<ProcessTrackingDetailsDTO> getAddedTasks()
			{
			return addedTasks;
			}
		
		
		public String  errcheck(List <ProcessTrackingDetailsDTO> listDto2)
			{
			int i = 0;
			for (ProcessTrackingDetailsDTO pt : listDto2)
				{
				if (StringUtils.isEmptyOrNull(pt.getTaskDesc()))
					{
					return "Task Description is blank for row number:" + (i+1) + " Please enter a Task Description. ";
					}
				if (StringUtils.isEmptyOrNull(pt.getAssignedTo()))
					{
					return "Assigned To is blank for row number:" + (i+1) + " Please choose Assigned To. ";
					}
				if (StringUtils.isEmptyOrNull(pt.getDateAssigned()))
					{
					return "Date Assigned is blank for row number:" + (i+1) + " Please choose Date Assigned. ";
					}
				i++;
				}
			return null;
			}
		}
				
		public void sendOutEmails (List <ProcessTrackingDetailsDTO> theDtoList, List <String> email_contacts, METWorksMessageMailer mailer)
			{			
			for (ProcessTrackingDetailsDTO lilProcessTrackingDetailsDTO : theDtoList)
				{
				// issue 210
			
				String msg = "";
				if (lilProcessTrackingDetailsDTO.getStatus().equals("In queue"))
					{
					msg = "Test email:  Task:" + lilProcessTrackingDetailsDTO.getTaskDesc() + "\n" + " Experiment:" + lilProcessTrackingDetailsDTO.getExpID() + " \n Workflow:" + lilProcessTrackingDetailsDTO.getWfID() +  "\n has been assigned to :" + lilProcessTrackingDetailsDTO.getAssignedTo();					
					//for (String email_contact : email_contacts) 
					//	{
					User luser = userService.loadUserByFullName(lilProcessTrackingDetailsDTO.getAssignedTo());
						METWorksMailMessage m = new METWorksMailMessage(getMailAddress(), luser.getEmail(), getMailTitle(),  msg);						
						if (luser.getEmail().equals("julieker@umich.edu"))
							mailer.sendMessage(m);
						//}
					}
				}
			}
		
		protected String getMailTitle() { return "Metlims Workflow Task Assigned - Test email from Test"; }
		protected String getMailAddress() { return "metabolomics@med.umich.edu"; }
	}
	
	
	
	
