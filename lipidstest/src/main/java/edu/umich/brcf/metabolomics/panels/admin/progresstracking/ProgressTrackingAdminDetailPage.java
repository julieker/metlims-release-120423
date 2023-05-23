/*********************************
 * 
 * Created by Julie Keros Dec 1 2020
 */
package edu.umich.brcf.metabolomics.panels.admin.progresstracking;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.StringParser;


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
    String assayDescID;
    ProcessTrackingDetails gPtd;
   
    Map<String, Boolean> collapseMap = new HashMap<String, Boolean> ();
	
	public ProgressTrackingAdminDetailPage(final String id) 
		{
	   // super(id);
		final ModalWindow modal2= new ModalWindow("modal2");
		modal2.setInitialWidth(1580);
        modal2.setInitialHeight(600);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em"); 
      
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
				target.add(progressTrackingAdminDetailPage);
				}
			});
        
        ////////////////////////////////////////////////////
        
    	///////////////////////////////////////////////////
    	
     	add(new AjaxCheckBox("inProgress", new PropertyModel<Boolean>(this, "inProgress"))
			{
			public void onUpdate(AjaxRequestTarget target)
				{
				target.add(progressTrackingAdminDetailPage);
				}
			});
    	
    	///////////////////////////////////////////////////
     
     	add(new AjaxCheckBox("inQueue", new PropertyModel<Boolean>(this, "inQueue"))
		{
		public void onUpdate(AjaxRequestTarget target)
			{
			target.add(progressTrackingAdminDetailPage);
			}
		}); 	
     	
		add(new AjaxCheckBox("completed", new PropertyModel<Boolean>(this, "completed"))
			{
			public void onUpdate(AjaxRequestTarget target)
				{
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
    	
    	
    	add ( listViewWF = new ListView ("wfDetail", new PropertyModel (this , "wfDetail"))
    	    {
    		//////////////////////////   	
    		private AjaxLink buildLinkWF(final String linkID, Workflow wf) 
    		{
    		// issue 39
    		AjaxLink wfLink;
    		wfLink =  new AjaxLink <Void>(linkID)
    			{	    		
    			@Override
    			public void onClick(AjaxRequestTarget target)
    				{				
    				wf.setCollapse(!wf.getCollapse());
    				
    				if (collapseMap.get(wf.getWfDesc()) != null)
    					{
    					collapseMap.put(wf.getWfDesc(), !collapseMap.get(wf.getWfDesc()));
    					}
    				else 
    					{
    					collapseMap.put(wf.getWfDesc(), false);
    					}
    				target.add(progressTrackingAdminDetailPage);
    				
    				}
    			};
    		if (linkID.equals("wfLink"))
    		    wfLink.add(new Label("wfDesc", wfDescString ));
    		return wfLink;
    		}
    	
    		public void populateItem(final ListItem wfItem)
    			{
    			wfDescString = (String) wfItem.getModelObject();
    			int lindex = 0;
    			if (lindex == 0)
    			lindex++;
    			if (collapseMap.get(wfDescString) == null)
    			    collapseMap.put(wfDescString,  false);
    			wfItem.add(new Label("wfDesc", new Model(wfDescString == null ? null :  wfDescString  )).setEscapeModelStrings(false));    			
    			Workflow  wf = processTrackingService.loadByIdWF(processTrackingService.grabWfIDFromDesc(wfDescString));
    			  			
    			wf.setCollapse(collapseMap.get(wfDescString) == null ? false : collapseMap.get(wfDescString));
    		//////change 	if (!wf.getCollapse() && wf.getProcessTrackingDetailsList().size() >0 )
        	//////		expID =  wf.getProcessTrackingDetailsList().get(0).getExperiment().getExpID();
    			wfItem.add(new Label("eLabel", new Model(wf.getCollapse() ? expID : "")));
    			wfItem.add(new Label("assignedDate", new Model(wf.getCollapse() ? processTrackingService.grabMinDateAssigned(wf.getWfID()) : "")));
    			wfItem.add(new Label("onHoldStatus", new Model (wf.getCollapse()? processTrackingService.existsOnHold(wf.getWfID()) : "")));  
    			
    			wfItem.add(buildLinkWF("wfLink", wf));
    			
    			wf.setAssignedTo(assignedTo);
    			
    			wf.setOnHold(onHold);
    			wf.setCompleted(completed);
    			wf.setInProgress(inProgress);
    			wf.setInQueue(inQueue);
		        wf.setExpID(expID);
		        wf.setAssayID(StringParser.parseId(assayDescID));
		        wfItem.add(listViewProgressTracking = new ListView("trackingDetail", new PropertyModel(wf, "trackingListForExpAssay"))
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
		    		    expLink.add(new Label("experimentID", expId ));
		    		return expLink;
		    		}
		        	
					public void populateItem(final ListItem listItem) 
						{				
						//listItem.setEscapeModelStrings(false) ;
						final ProcessTrackingDetails procTracDetails = (ProcessTrackingDetails) listItem.getModelObject();		
					    if (procTracDetails.getStatus().equals("On hold"))
					    	existsOnHold = true;
					    listItem.add(new Label("sampleType", new Model (processTrackingService.grabSampleType(wf.getWfID(), expID))));
						final Project withDocs = projectService.loadById(procTracDetails.getExperiment().getProject().getProjectID());
						listItem.add(buildLinkExp("eLink", modal2, withDocs, procTracDetails.getExperiment().getExpID()));
						listItem.add(new Label("taskdescription", new Model(procTracDetails.getProcessTracking().getTaskDesc())));
						//listItem.add(new Label("dateassigned", new Model(procTracDetails.getDateStarted()))); // issue 118
						listItem.add(new Label("dateAssigned", new Model( procTracDetails.convertToDateString( procTracDetails.getDateAssigned() ))));     
						listItem.add(new Label("dateStarted", new Model( procTracDetails.convertToDateString( procTracDetails.getDateStarted() ))));
						listItem.add(new Label("assignedto", new Model( procTracDetails.getAssignedTo().getFullName())));
						//listItem.add(new Label("datecompleted", new Model(procTracDetails.convertToCreateDateString(procTracDetails.getDateCompleted())));				
						listItem.add(new Label("datecompleted", new Model( procTracDetails.convertToDateString( procTracDetails.getDateCompleted() ))));				
						listItem.add(new Label("comments", new Model(edu.umich.brcf.shared.util.io.StringUtils.isEmptyOrNull(procTracDetails.getComments()) ? "" : (  procTracDetails.getComments().length() > 20 ?  procTracDetails.getComments().substring(0,20) :  procTracDetails.getComments()          )    )));				
						listItem.add(new Label("status", new Model(procTracDetails.getStatus())));		
						listItem.add(buildLinkToEditTracking("editTrackingAdmin",procTracDetails,modal2));	
						listItem.add(buildLinkToDeleteTracking("deleteTrackingAdmin",procTracDetails,modal2));	
						}   
					});			
    	        }
    		});
		    }
			
	// issue 94	
	private AjaxLink buildLinkToEditTracking(final String id, final ProcessTrackingDetails ptd, final ModalWindow modal1 ) 
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
					public Page createPage() {   return setPage(id, modal1, ptd);   }
					});	
				    	modal1.show(target); 
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
		List <String> nWfList = processTrackingService.loadAllWFsAssigned(getExpID());
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
		    	switch (response)
		        	{		    	   
		    	    case "updateUser" : 
		    	    	if (assignedTo.equals("All Users"))
		    	    		assignedTo = "";
		    	    	target.add(userNamesDD);
		    	    	target.add(progressTrackingAdminDetailPage);
		    	    	break;
		    	    case "updateStatus" : 
		    	    	target.add(progressTrackingAdminDetailPage);
		    	    	break;
		    	    	// issue 273
		    	    case "updateExperiment" : 
		    	    	// issue 273
		    	    	 if (expID.contentEquals("All Experiments"))
		    	    		{	
		    	    		expID = null;
		    	    		target.add(experimentDD);
		    	    		}
		    	    	newAliquotList.add("Choose One");
		    	    	newAliquotList.addAll(processTrackingService.allAssayNamesForExpIdInTracking(expID, false));
		    	    	//assayDescDD.setChoices (processTrackingService.allAssayNamesForExpIdInTracking(expID, false));
		    	    	assayDescDD.setChoices (newAliquotList);
		    	    	assayDescID = " " ;
		    	    	target.add(assayDescDD);
		    	    	target.add(progressTrackingAdminDetailPage);
		    	    	break;
		    	    case "updateAssayDesc" :
						assayDescDD.setChoices (processTrackingService.allAssayNamesForExpIdInTracking(expID, false));	
						target.add(progressTrackingAdminDetailPage);
						break;	
		    	    }
		    	}
		    };
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
	
	}
