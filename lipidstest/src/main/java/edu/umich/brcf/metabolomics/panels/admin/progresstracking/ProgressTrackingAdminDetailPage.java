/*********************************
 * 
 * Created by Julie Keros Dec 1 2020
 */
package edu.umich.brcf.metabolomics.panels.admin.progresstracking;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.mysql.jdbc.StringUtils;

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.metabolomics.panels.lims.project.ProjectDetail2;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.Workflow;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.comparator.ProgressTrackDetailsComparator;


public class ProgressTrackingAdminDetailPage extends WebPage
	{
	@SpringBean
	CompoundService compoundService;
	AjaxLink lnkResetAssignTo;
	
	// issue 61
	@SpringBean 
	AliquotService aliquotService;
	
	@SpringBean
	AssayService assayService;     
	
	@SpringBean
	MixtureService mixtureService;   
	@SpringBean 
	InventoryService inventoryService;
	@SpringBean
	UserService userService;
	@SpringBean
	ProcessTrackingService processTrackingService;
	@SpringBean
	ProjectService projectService;
	@SpringBean
	ExperimentService experimentService;
	
	// issue 290
	AjaxLink deleteAjaxLink;
	AjaxLink editAjaxLink;
	Label deleteLabel;
	Label editLabel;
	DropDownChoice<String> userNamesDD;
	DropDownChoice<String> experimentDD;
	DropDownChoice<String> assayDescDD;
	AbstractDefaultAjaxBehavior confirmBehavior;
	String assignedTo ;
	String status ;
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;
	ListView listViewProgressTracking; // issue 61
	ListView listViewWF;
	
	
	ProgressTrackingAdminDetailPage progressTrackingAdminDetailPage = this;
	EditProcessTrackingDetail editProcessTrackingDetail;
	ProgressTrackingDefaultPage progressTrackingDefaultPage;
	// itemList
	// issue 118
    boolean onHold = true;
    boolean inProgress = true;
    boolean completed = false;
    boolean inQueue = true; // issue 262
    String wfDescString;
    boolean collapse = false;
    String expID;
    Boolean existsOnHold = false;
    String callingLink = "";
    
    String assayDescID;
    ProcessTrackingDetails gPtd;
    Label cLabel ;
    List <ProcessTrackingDetails> glPTD = new ArrayList <ProcessTrackingDetails> ();
    List <ProcessTrackingDetails> glPTDAllExp = new ArrayList <ProcessTrackingDetails> ();
    int indexForDetail = 0;
   
    Map<String, Boolean> collapseMap = new HashMap<String, Boolean> ();
    
	boolean initialFirstTime = true;
    List <Object []> expAssayObjs = new ArrayList <Object[]> ();
    Map<String, String> numSampleMap = new HashMap<String, String> ();
    Map<String, String> investigatorAndContactMap = new HashMap<String, String> ();
    Map<String, String> sampleTypeMap = new HashMap<String, String> ();
    Map <String, String> serviceRequestMap =  new HashMap<String, String>();
    AjaxCheckBox inProgCheckBox;
    AjaxCheckBox completedCheckBox;
    AjaxCheckBox inQueueCheckBox;
    AjaxCheckBox onHoldCheckBox;
    String gJobId = "";
    ProcessTrackingDetails gptd ;
    
	public ProgressTrackingAdminDetailPage(final String id) 
		{
		
	   // super(id); 
		/////  set it back  setExpID("EX01266");
		numSampleMap = buildNumberOfSamplesMap ();
		investigatorAndContactMap = buildInvestigatorAndContactMap();
		serviceRequestMap = buildServiceRequestMap();
		//// sampleTypeMap = buildSampleTypeMap();
		// issue 292
		sampleTypeMap =  processTrackingService.createSampleTypeStringMapFromList();
		final ModalWindow modal2= new ModalWindow("modal2");
		modal2.setInitialWidth(1580);
        modal2.setInitialHeight(600);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em");   
        // issue 290
        /// put back
        glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), true, assignedTo, false, inProgress, onHold, completed, inQueue, false);
        glPTDAllExp.addAll(glPTD);
        
     // issue 262
    	  confirmBehavior = new 
    	        AbstractDefaultAjaxBehavior() 
    		        { 
    		        @Override 
    				protected void respond(AjaxRequestTarget target) 
    		            {           
    				    try 
    				        { 
    				    	// issue 233 
    				        processTrackingService.deleteTrackingDetails(gPtd.getJobid());
    				        // issue 290
    				        //
    				        //
    				        // issue 290
    				        int sz = glPTD.size();
    				        for (int i = 0; i< sz; i++)
    				        	{
    				        	if (gPtd.getJobid().equals(glPTD.get(i).getJobid()))
    				        	    {
    				        		glPTD.remove(i);
    				        		break;
    				        	    }
    				        	   
    				        	}
    				        sz = glPTDAllExp.size();
    				        for (int i = 0; i< sz; i++)
    				        	{
    				        	if (gPtd.getJobid().equals(glPTDAllExp.get(i).getJobid()))
    				        	    {
    				        		glPTDAllExp.remove(i);
    				        		break;
    				        	    }
    				        	
    				        	}
    				        target.add(progressTrackingAdminDetailPage);
    				        } 
    				    catch (Exception e) 
    				        { 				        
    				        } 
    				     } 
    				 }; 
        
        add (confirmBehavior);       
    	add(new AjaxCheckBox("onHold", new PropertyModel<Boolean>(this, "onHold"))
			{
			public void onUpdate(AjaxRequestTarget target)
				{
				glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), StringUtils.isNullOrEmpty(expID) , assignedTo, false, inProgress, onHold, completed, inQueue,false);
				target.add(progressTrackingAdminDetailPage);
				}
			});
        
        ////////////////////////////////////////////////////
        
    	///////////////////////////////////////////////////
    	
    	// issue 290
     	add(inProgCheckBox = new AjaxCheckBox("inProgress", new PropertyModel<Boolean>(this, "inProgress"))
			{
			public void onUpdate(AjaxRequestTarget target)
				{
  
				glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), StringUtils.isNullOrEmpty(expID) , assignedTo, false, inProgress, onHold, completed, inQueue, false);
				target.add(progressTrackingAdminDetailPage);
				}
			});
     	
    	///////////////////////////////////////////////////
     
     	add(inQueueCheckBox = new AjaxCheckBox("inQueue", new PropertyModel<Boolean>(this, "inQueue"))
		{
		public void onUpdate(AjaxRequestTarget target)
			{ 
			glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), StringUtils.isNullOrEmpty(expID) , assignedTo, false, inProgress, onHold, completed, inQueue, false);
			target.add(progressTrackingAdminDetailPage);   
			}
		}); 	
     	
		add(new AjaxCheckBox("completed", new PropertyModel<Boolean>(this, "completed"))
			{
			public void onUpdate(AjaxRequestTarget target)
				{  
				glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), StringUtils.isNullOrEmpty(expID) , assignedTo, false, inProgress, onHold, completed, inQueue, false);
				target.add(progressTrackingAdminDetailPage);  
				}
			});
        add(new Label("titleLabel", "View/Edit Workflows"));        
    	userNamesDD= new DropDownChoice("assignedTo", new PropertyModel<String>(this, "assignedTo"),   new ArrayList <String> ())
		    {
			
			}
			;			
		userNamesDD.setOutputMarkupId(true);
		add(userNamesDD);
		userNamesDD.setChoices(userService.allAdminNames(false));			
		userNamesDD.add(buildStandardFormComponentUpdateBehavior("change", "updateUser"));
		add(experimentDD = buildExperimentDropDown("experimentDropDown"));		
		 add(assayDescDD = buildAssayDescDropDown("assayDescDropDown"));
		experimentDD.add(buildStandardFormComponentUpdateBehavior("change", "updateExperiment"));
		assayDescDD.add(buildStandardFormComponentUpdateBehavior("change", "updateAssayDesc"));
      
        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	if (callingLink.equals("projectDetails"))        
            	    return;
            
            	List <ProcessTrackingDetails> lPTD = new ArrayList <ProcessTrackingDetails> ();
            	gptd = processTrackingService.loadById(gJobId);
            	processTrackingService.initializeProcessKids(gptd);
    			List <ProcessTrackingDetails> lstPtdIncludingBelow = processTrackingService.loadAllTasksBelowEditedExperiment(gptd.getExperiment().getExpID(), gptd.getAssay().getAssayId() , gptd.getDetailOrder());
            	List <String> jobIDList = new ArrayList <String> ();
            	int j = 0;
            	for ( ProcessTrackingDetails sPTD : lstPtdIncludingBelow)
            		{            		
            		jobIDList.add(sPTD.getJobid());
            		processTrackingService.initializeProcessKids(sPTD);
            		}
            	
            	/// issue 290
            	List <String> statusList = new ArrayList <String> ();
            	if (completed)
            		statusList.add("Completed");
             	if (inQueue)
            		statusList.add("In queue");
             	if (inProgress)
            		statusList.add("In progress");
             	if (onHold)
            		statusList.add("On hold");      
             	   
            	for (int i=0; i<glPTD.size(); i++)
            		{
            		
            		if (jobIDList.contains(glPTD.get(i).getJobid()))
        				{
            			
            			if (!statusList.contains(lstPtdIncludingBelow.get(j).getStatus()))
            				{
            				j++;
            				continue;
            				}
            				
            			lPTD.add(lstPtdIncludingBelow.get(j));                  
            			j++;    
        				}
            		else 
            			lPTD.add(glPTD.get(i));    
            		}
            	
            	glPTD =  new ArrayList <ProcessTrackingDetails> ();
            	glPTD.addAll(lPTD);
            	lPTD = new ArrayList <ProcessTrackingDetails> ();
            	j = 0;
            	for (int i = 0; i<glPTDAllExp.size(); i++ )
	        		{
            		if (jobIDList.contains(glPTDAllExp.get(i).getJobid()))
	    				{
            			      
            			if (!statusList.contains(lstPtdIncludingBelow.get(j).getStatus()))
	        				{
	        				j++;
	        				continue;
	        				}
            			           			
	        			lPTD.add(lstPtdIncludingBelow.get(j));   
	        			j++;
	    				}
	    			else 
	    				lPTD.add(glPTDAllExp.get(i));
	        		} 
            	
            	glPTDAllExp =  new ArrayList <ProcessTrackingDetails> ();
            	glPTDAllExp.addAll(lPTD); 
            	            	
            	//glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), StringUtils.isNullOrEmpty(expID) , assignedTo, false, inProgress, onHold, true, true, false);
            	target.add(progressTrackingAdminDetailPage);
            	}
        	}); 
        add(modal2);
        
        
    	add( new AjaxLink<Void>("close")
		{
		public void onClick(AjaxRequestTarget target)
			{ 
			modal2.close(target);
			}
		});	
        
		///// issue 94
		//// issue 120
    	
    	///////////////////////////////////////////////////////////////////////// leave off here 
    	/////////////////////////////////////////////////////////////////////////

		        add(listViewProgressTracking = new ListView("trackingDetail", new PropertyModel(this, "trackingListForExpAssay"))
					{
		        	private AjaxLink buildLinkExp(final String linkID,  final ModalWindow modal1 ,Project prj, String expId) 
			    		{
			    		// issue 39
			    		AjaxLink expLink;
			    		expLink =  new AjaxLink <Void>(linkID)
			    			{			    		
			    			@Override
			    			public void onClick(AjaxRequestTarget target)
			    				{
			    				modal1.setInitialWidth(625);
			    			    modal1.setInitialHeight(600);
			    			    callingLink = "projectDetails";
			    			    modal1.setPageCreator(new ModalWindow.PageCreator()
			    		    		{
			    		            public Page createPage()
			    		             	{
			    		            	return (new ProjectDetail2(new Model(prj)));
			    		             	}
			    		    		});
			    				modal1.show(target); 
			    				}
			    			};
			    		if (linkID.equals("eLink"))
			    		    expLink.add(new Label("experimentID", expId));
			    		return expLink;
			    		}
		        	
		        	// issue 290
		        	private AjaxLink buildLinkBlank(final String linkID) 
		    		{
		    		// issue 39
		    		AjaxLink expLink;
		    		expLink =  new AjaxLink <Void>(linkID)
		    			{			    		
		    			@Override
		    			public void onClick(AjaxRequestTarget target)
		    				{				
		    				
		    				}
		    			};
		    		if (linkID.equals("eLink"))
		    		    expLink.add(new Label("experimentID", ""));
		    		return expLink;
		    		}
		        	
		        	
					public void populateItem(final ListItem listItem)    
						{		
						Label assayIDLabel;
						
					    listItem.setEscapeModelStrings(false) ;        
						final ProcessTrackingDetails procTracDetails = (ProcessTrackingDetails) listItem.getModelObject();		
					    
					     
						if (procTracDetails.getStatus().equals("On hold"))
					    	existsOnHold = true;
						AjaxLink exAssayLink;
						if (procTracDetails.getJobid().length() < 7)        
							{
							listItem.add(new Label("ContactPI",    new Model ("---")) );
							listItem.add(new Label("sampleType", new Model ("--- ")));    
							listItem.add(exAssayLink = buildLinkBlank("eLink"));
							listItem.add(assayIDLabel = new Label("assayID", new Model(" <br><br><br>")));
							assayIDLabel.setEscapeModelStrings(false);
						     
							//procTracDetails.getAssay().getAssayId()   
							
							// issue 285   
							// issue 290						
							listItem.add(new Label("taskdescription", new Model("--- ")));  
							listItem.add(new Label("serviceRequest", new Model("--- "))); 
							listItem.add(new Label("dateStarted", new Model( " ---")));
							listItem.add(new Label("assignedto", new Model( " ---")));
							//listItem.add(new Label("datecompleted", new Model(procTracDetails.convertToCreateDateString(procTracDetails.getDateCompleted())));				
							listItem.add(new Label("datecompleted", new Model( " ---")));				
							listItem.add(cLabel = new Label("comments", new Model("--- "   )));				
							// issue 285			
							listItem.add(new Label("status", new Model("--- ")));		
							listItem.add(editAjaxLink = buildLinkToEditTrackingBlank("editTrackingAdmin"));	
							listItem.add(deleteAjaxLink = buildLinkToDeleteTrackingBlank("deleteTrackingAdmin"));	
							deleteAjaxLink.add(deleteLabel = new Label("deleteLabel", new Model(" ")));
							editAjaxLink.add(editLabel = new Label("editLabel", new Model(" ")));
							editLabel.setEscapeModelStrings(false);   
							return;
							}  //////////////////////////////////////////////////////
						
						
						// issue 290
						
						// issue 290    
						listItem.add(new Label("ContactPI",    new Model (investigatorAndContactMap.get(procTracDetails.getExperiment().getExpID())) ).setEscapeModelStrings(false));
						     

					    // issue 290
					   // listItem.add(new Label("sampleType", new Model (processTrackingService.grabSampleType( expID)  
					   // 		                   + "&nbsp;&nbsp;&nbsp;("  +  numSampleMap.get(expID) + ")")).setEscapeModelStrings(false));
					
						listItem.add(new Label("sampleType", new Model (sampleTypeMap.get(procTracDetails.getExperiment().getExpID())      
						   		                   + "&nbsp;&nbsp;&nbsp;("  +  numSampleMap.get(procTracDetails.getExperiment().getExpID()) + ")")).setEscapeModelStrings(false));
						
						////listItem.add(exAssayLink = buildLinkBlank("eLink"));
						final Project withDocs = projectService.loadById(procTracDetails.getExperiment().getProject().getProjectID());
						
						listItem.add(new Label("serviceRequest", new Model(serviceRequestMap.get(procTracDetails.getExperiment().getExpID())))); 
						
						
					    listItem.add(exAssayLink = buildLinkExp("eLink", modal2, withDocs, procTracDetails.getExperiment().getExpID() ));
						// issue 290
						listItem.add(assayIDLabel = new Label("assayID", new Model(procTracDetails.getAssay().getAssayId())));
						//procTracDetails.getAssay().getAssayId()         
						
						// issue 285   
						// issue 290
						assayIDLabel.add(AttributeModifier.replace("title",     procTracDetails.getAssay().getAssayName()    ));						
						listItem.add(new Label("taskdescription", new Model(procTracDetails.getProcessTracking().getTaskDesc())));
						listItem.add(new Label("dateStarted", new Model( procTracDetails.convertToDateString( procTracDetails.getDateStarted() ))));
						listItem.add(new Label("assignedto", new Model( procTracDetails.getAssignedTo().getFullName())));
						//listItem.add(new Label("datecompleted", new Model(procTracDetails.convertToCreateDateString(procTracDetails.getDateCompleted())));				
						listItem.add(new Label("datecompleted", new Model( procTracDetails.convertToDateString( procTracDetails.getDateCompleted() ))));				
						listItem.add(cLabel = new Label("comments", new Model(edu.umich.brcf.shared.util.io.StringUtils.isEmptyOrNull(procTracDetails.getComments()) ? "" : (  procTracDetails.getComments().length() > 20 ?  procTracDetails.getComments().substring(0,20) :  procTracDetails.getComments()          )    )));				
						// issue 285			
						cLabel.add(AttributeModifier.replace("title",     edu.umich.brcf.shared.util.io.StringUtils.isEmptyOrNull(procTracDetails.getComments()) ? "" :   procTracDetails.getComments()       ));
						
						listItem.add(new Label("status", new Model(procTracDetails.getStatus())));		
						listItem.add(editAjaxLink = buildLinkToEditTracking("editTrackingAdmin",procTracDetails,modal2));	
						listItem.add(deleteAjaxLink = buildLinkToDeleteTracking("deleteTrackingAdmin",procTracDetails,modal2));	
						editAjaxLink.add(editLabel = new Label("editLabel", new Model("edit")));
					    deleteAjaxLink.add(deleteLabel = new Label("deleteLabel", new Model("delete")));
						deleteLabel.setEscapeModelStrings(false);
						indexForDetail++;
						}   
					});	
		      //  glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), true, assignedTo, false, inProgress, onHold);
    	        }
    		   /// end of wf
    	////////////////////////////////////////////////////////////
		  
			   
	// issue 94	
	private AjaxLink buildLinkToEditTracking(final String id, final ProcessTrackingDetails ptd, final ModalWindow modal1 ) 
		{
		// issue 39
		 AjaxLink lnk =  new AjaxLink<Void> (id)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				gptd = ptd;
				gJobId = ptd.getJobid();
				setModalDimensions(id, modal1);
				 modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage() {   return setPage(id, modal1, ptd);   }
					});	
				    	modal1.show(target); 
				    	callingLink = "editTrackingDetails";
				}
			};
		return lnk;
		} 
	
	/// issue 290
	private AjaxLink buildLinkToEditTrackingBlank (final String id ) 
	{
	// issue 39
	 AjaxLink lnk =  new AjaxLink<Void> (id)
		{
		@Override
		public void onClick(AjaxRequestTarget target)
			{
			
			}
		};
	return lnk;
	}

	private AjaxLink buildLinkToDeleteTracking(final String id, final ProcessTrackingDetails ptd, final ModalWindow modal1 ) 
		{
		// issue 39
		 AjaxLink lnk =  new AjaxLink<Void> (id)
			{		
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				gPtd = ptd;
				//////gPtd = processTrackingService.loadById(ptd.getJobid());
				target.appendJavaScript("if (confirm(' Are you sure you want to remove task:"  + gPtd.getProcessTracking().getTaskDesc() +  
                         " ')) { " +  confirmBehavior.getCallbackScript() + " }"  );					
				}
			};
		return lnk;
		} 
	
	// issue 290
	private AjaxLink buildLinkToDeleteTrackingBlank(final String id ) 
	{
	// issue 39
	 AjaxLink lnk =  new AjaxLink<Void> (id)
		{		
		@Override
		public void onClick(AjaxRequestTarget target)
			{
							
			}
		};
	return lnk;
	} 
	
	 private Page setPage(String linkID, final ModalWindow modal1, ProcessTrackingDetails ptd)
		{
		switch(linkID)
			{
			//case "detailMixture" : return new MixtureAliquotDetail (linkID, mix);
			case "editTrackingAdmin"   : editProcessTrackingDetail = new EditProcessTrackingDetail (getPage(), new Model <ProcessTrackingDetails> (ptd), modal1, false);
			                         return editProcessTrackingDetail;
			                        
			case "addTrackingAdmin"  : editProcessTrackingDetail =  new EditProcessTrackingDetail (getPage(), modal1);  
				                      return editProcessTrackingDetail;
				                    
			case "addTrackingAdminDefault" : progressTrackingDefaultPage = new ProgressTrackingDefaultPage	(linkID) ;
			                           return progressTrackingDefaultPage;
			                         
			//default :              return new MixtureAliquotDetail (linkID, mix);
			}
		return editProcessTrackingDetail;
		} 
	
	
	private void setModalDimensions(String linkID, ModalWindow modal1)
		{ 
		modal1.setInitialWidth(1500);
		modal1.setInitialHeight(790);
		}
	
	public List<String> getWfDetail()
		{
		// issue 287
		List <String> nWfList = processTrackingService.loadAllWFsAssigned(getExpID(), StringParser.parseId(assayDescID));
		return nWfList;
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
		    	    case "updateUser" :    
		    	    	if (assignedTo.equals("All Users"))
		    	    		assignedTo = "";
		    	    	// issue 290     
		    	    	completed = false;
		    	    	glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), StringUtils.isNullOrEmpty(expID) , assignedTo, false, inProgress, onHold, false, true, false);
		    	    	target.add(userNamesDD);
		    	    	target.add(progressTrackingAdminDetailPage);
		    	    	break;
		    	    case "updateStatus" : 
		    	    	target.add(progressTrackingAdminDetailPage);
		    	    	break;
		    	    case "updateInProgress" :
		    	    	glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), StringUtils.isNullOrEmpty(expID) , assignedTo, false, inProgress, onHold, true, true, false);
		    	    	target.add(progressTrackingAdminDetailPage);
		    	    	// issue 273
		    	    case "updateExperiment" :            
		    	    	assayDescID = null; // issue 290    
		    	    	completed = false;
				    	glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), StringUtils.isNullOrEmpty(expID) , assignedTo, false, inProgress, onHold, false, true, false);
		    	    	// issue 273
				    
		    	    	 if (expID == null || expID.contentEquals("All Experiments"))   
		    	    		{	
		    	    		 // issue 292
		    	    		completed = false;
		    	    		onHold = true;
		    	    		inProgress = true;
		    	    		inQueue = true;
		    	    		expID = null; 
		    	    		assignedTo = null; // issue 290     
		    	    		glPTD = new ArrayList <ProcessTrackingDetails> ();
		    	    		glPTD.addAll(glPTDAllExp);   
		    	    		
		    	    		target.add(experimentDD);
		    	    		}
		    	    	 // issue 287   
		    	    	newAliquotList.add("Choose One"); 
		    	    	newAliquotList.add("All Assays");
		    	    	// issue 287
		    	    	newAliquotList.addAll(processTrackingService.allAssayNamesForExpIdInTracking(expID, false));
		    	    	//assayDescDD.setChoices (processTrackingService.allAssayNamesForExpIdInTracking(expID, false));
		    	    	assayDescDD.setChoices (newAliquotList);
		    	    	assayDescID = " " ;
		    	    	target.add(assayDescDD);
		    	    	target.add(progressTrackingAdminDetailPage);
		    	    	break;
		    	    case "updateAssayDesc" :
		    	    	completed = false;
		    	    	if (!StringUtils.isNullOrEmpty (expID))
		    	    		newAliquotList.add("All Assays");
		    	    	newAliquotList.addAll(processTrackingService.allAssayNamesForExpIdInTracking(expID, false)  );		    	      	    	
						assayDescDD.setChoices (newAliquotList);	
						 if (assayDescID.equals("All Assays"))
		    	    		{	
		    	    		assayDescID = null;
		    	    		target.add(assayDescDD);
		    	    		}
						 
						glPTD = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), StringUtils.isNullOrEmpty(expID) , assignedTo, false, inProgress, onHold, false, true, false);
						target.add(progressTrackingAdminDetailPage);
						
						break;	    
		    	    }
		    	}
		    };
		}
	
	
	// 290
	
  
public List <ProcessTrackingDetails> getTrackingListForExpAssay ()
	{        	  
	List <ProcessTrackingDetails> ptListCriteria  = new ArrayList <ProcessTrackingDetails> ();
	List <ProcessTrackingDetails> ptListExpAssayWk  = new ArrayList <ProcessTrackingDetails> ();
	return glPTD;
	}
	
	
	// issue 273
	 public DropDownChoice buildExperimentDropDown(final String id)
		{
		List <String> expList = new ArrayList <String> ();
		expList.add("All Experiments");
		expList.addAll(processTrackingService.loadAllAssignedExperiments());
		experimentDD =  new DropDownChoice<String>(id, new PropertyModel(this, "expID" ), new ArrayList <String> ())
				{				
				};							
	    experimentDD.setChoices (expList)	;
		return experimentDD;		
		}
	
	    public String getExpID ()
			{
	    	return this.expID;
			}
	    
	    public void setExpID (String expID)
			{
	    	this.expID = expID;
			}
	    
		 public DropDownChoice buildAssayDescDropDown(final String id)
			{	
			 List <String> assayList = new ArrayList <String> ();
				assayList.add("All Assays");
				assayList.addAll(processTrackingService.allAssayNamesForExpIdInTracking(expID, false));
			assayDescDD =  new DropDownChoice<String>(id, new PropertyModel(this, "assayDescID" ), new ArrayList <String> ())
					{				
					};		
					assayDescDD.setChoices (processTrackingService.allAssayNamesForExpIdInTracking(expID, false))	;						
			return assayDescDD;			
			}	 
		public String getAssignedTo() { return assignedTo; }
		public void setAssignedTo (String e) { assignedTo = e; }
		public String getStatus() { return status; }
		public void setStatus (String e) { status = e; }		
		public boolean getOnHold() { return onHold; }
		public void setOnHold (boolean e) { onHold = e; }
		public boolean getInProgress() { return inProgress; }
		public void setInProgress (boolean e) { inProgress = e; }
		public boolean getInQueue() { return inQueue; }
		public void setInQueue (boolean e) { inQueue = e; }
		public boolean getCompleted() { return completed; }
		public void setCompleted (boolean e) { completed = e; }
		public String getAssayDescID ()
			{
			return this.assayDescID;
			}
		public void setAssayDescID (String assayDescID)
			{
			this.assayDescID = assayDescID;    
			}

		 public Map <String, String> buildNumberOfSamplesMap ()
			 {
			 Map <String, String> numberSamplesMap =  new HashMap<String, String>();
			 List <Object []> expAssayObjs = processTrackingService.listExpAssay();
			 Object [] currentTaskObj;			
			 for (Object [] lobj : expAssayObjs)   
			 	 {	
			     numberSamplesMap.put((String) lobj[0],  processTrackingService.grabNumberOfSamplesForEmail ((String) lobj[0]));
			 	 }   
			 return numberSamplesMap;
			 }
		 
		 public Map <String, String> buildSampleTypeMap ()
		 	{
			 sampleTypeMap = processTrackingService.grabAllSampleTypes();
			 return sampleTypeMap;
		 	}	
		 public Map <String, String> buildInvestigatorAndContactMap ()
			 {
			 Map <String, String> investigatorContactMap =  new HashMap<String, String>();
			 for (ProcessTrackingDetails ptd : processTrackingService.listProcTrackDetails())   
			 	 {	
				 investigatorContactMap.put(ptd.getExperiment().getExpID(),ptd.getExperiment().getProject().getClient().getLab() + ";"  +  "<br>" + ptd.getExperiment().getProject().getContactPerson().getFullNameByLast() );
			 	 }   
			 return investigatorContactMap;   
			 }
		 
		 public Map <String, String> buildServiceRequestMap ()
			 {
			 Map <String, String> serviceRequestMap =  new HashMap<String, String>();
			 for (ProcessTrackingDetails ptd : processTrackingService.listProcTrackDetails())   
			 	 {	
				 serviceRequestMap.put(ptd.getExperiment().getExpID(),ptd.getExperiment().getServiceRequest());
			 	 }   
			 return serviceRequestMap;   
			 }
		 
		 
	
	}
