////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  PlatePreviewPage.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;


public class PlatePreviewPage extends AbstractFormDialog
	{
	@SpringBean
	private SampleService sampleService;
	WebMarkupContainer arrowContainer;
	WebMarkupContainer arrowContainerright;	
	WebMarkupContainer holdarea , holdarearight, holderforcell;	
	Label plateHead10, plateHead11, plateHead12, arrow, arrowright;
	int nRowsNeeded, nRowsToCreate, nPlatesNeeded;
	int nItemsPerRow, nItemsPerCol;
	boolean gBothQCMPandMP;
	WorklistBuilderPanel workListBuilderPanel;
	Map<String, String> idsVsReasearcherNameMap = new HashMap<String, String> ();
	String thePrevSampleName;
	WorklistItemSimple prevItem;
	Label pLabelPrev;
	public Form<?> form;
	PlatePreviewForm rlf; 
	public DialogButton submitButton = new DialogButton("submit", "Done");
	public DialogButton submitButton2 = new DialogButton("submit2", "ResetDefault");
	WorklistItemSimpleMatrix itemMatrix;
	WorklistBuilderPanel wpMain;
	List<WorklistItemSimple> spacedg = new ArrayList <WorklistItemSimple> ();
	List<WorklistItemSimple> itemsg = new ArrayList <WorklistItemSimple> ();
	PageableListView plateListView;
	AjaxPagingNavigator ajaxPagingNavigator;
	Label lpos10, lpos11, lpos12, lhead10, lhead11, lhead12;
	boolean hasBeenOpened = false;
	Map<String, String> colorMap = new HashMap<String, String> ();
	PlateListHandler handler;
	boolean firstTimeOpening = true; 
	WorklistItemSimple itemHoldforCell;
	Label pLabelHoldForCell;
	boolean cameFromHolder = false;
	boolean cameFromPreview = false;

	
	public PlatePreviewPage(boolean bothQCMPandMP,  List<WorklistItemSimple> items, boolean useCarousel, WorklistBuilderPanel wp, String id, String title,WorklistSimple ws)
		{
		super(id, title,  true);
		workListBuilderPanel = wp;
		wpMain = wp;
		wpMain.setOutputMarkupId(true);
		//modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		List<WorklistItemSimple> listCopy = new ArrayList<WorklistItemSimple>();
		for (int i = 0; i < items.size(); i++)
			listCopy.add(items.get(i).makeCopy());
		if (listCopy.size() > 0 &&listCopy.get(0).getGroup().getParent().getIs96Well())
			{
			nItemsPerRow = useCarousel ? 10 : 12;
			nItemsPerCol = useCarousel ? 10 : 8;
			}
		else
			{
			nItemsPerRow = useCarousel ? 10 : 9;
			nItemsPerCol = useCarousel ? 10 : 6;
			}
        
		rlf = new PlatePreviewForm("platePreviewForm",
				listCopy, useCarousel, ws);
		rlf.setMultiPart(true);
		add(rlf);
		form = rlf;
		gBothQCMPandMP=bothQCMPandMP;
		}

	public class PlatePreviewForm extends Form
		{
		boolean useCarousel = false;
		List<WorklistItemSimple> sortedSpacedItems;
		
		public PlatePreviewForm(final String id, List<WorklistItemSimple> items,  boolean useCarousel, WorklistSimple ws)
			{
			super(id);
			add(new FeedbackPanel("feedback"));
			this.setOutputMarkupId(true);
			setMultiPart(true);		
			
			
			arrowContainer = new WebMarkupContainer("arrowarea")
				{
			
				};
				add (arrowContainer);
								
			arrowContainerright = new WebMarkupContainer("arrowarearight")
				{
		
				};
				add (arrowContainerright);	
				
			holdarea = new WebMarkupContainer("holdarea")
				{
			
				};
				add (holdarea);
			
			holderforcell = new WebMarkupContainer("holderforcell")
				{
			
				};
				add (holderforcell);
				holderforcell.setOutputMarkupId(true);
				pLabelHoldForCell = new Label("holderforcelllabel", "Drag Control here to move");
				pLabelHoldForCell.setOutputMarkupId(true);
				holderforcell.add(pLabelHoldForCell);		
					
				holderforcell.add(new AjaxEventBehavior("drop")
					{
				    protected void onEvent(AjaxRequestTarget target)
				    	{	    	
				    	try
			            	{
				    	    if (prevItem.getSampleName().startsWith("S000"))
				                {
				            	target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("Please choose a control rather than sample:" + prevItem.getSampleName().replace("\n", "")));
				            	return;
				            	} 
				    	    if (StringUtils.isEmptyOrNull(prevItem.getSampleName()))
				                {
				            	target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("Please choose a control rather than a blank square:" + prevItem.getSampleName().replace("\n", "")));
				            	return;
				            	} 
				    	  	itemHoldforCell = prevItem;
				    	  	itemHoldforCell.setShortSampleName(prevItem.getShortSampleName());		
				         	pLabelHoldForCell.setDefaultModelObject(itemHoldforCell.getShortSampleName().replace("CS00000MPCS000QCMP", "CS00000MP        CS000QCMP"));
			            	thePrevSampleName = thePrevSampleName.replace("\n",  "");
			            	prevItem.setMpQcmpName(prevItem.getMpQcmpName().replace("\n",  ""));
			            	prevItem.setSampleName(prevItem.getSampleName().replace("\n",  ""));
			            	prevItem.setShortSampleName(prevItem.getShortSampleName().replace("\n",  ""));
			             	String controlTitle = colorMap.get(thePrevSampleName);
			             	
			             	if (StringUtils.isNullOrEmpty(controlTitle))
			             		{
			             		if (thePrevSampleName.contains("CS00000MP"))
			             			{
			             		    controlTitle = colorMap.get("CS00000MP");
			             		    if (StringUtils.isNullOrEmpty(controlTitle))
			             		        controlTitle = WorklistFieldBuilder.assembleStyleTag(prevItem, true);
			             			}
			             		else
			             			controlTitle = WorklistFieldBuilder.assembleStyleTag(prevItem, true); 
			             		}			             	
			             	// goback if (StringUtils.isNullOrEmpty(controlTitle))
			             	// goBack	controlTitle = WorklistFieldBuilder.assembleStyleTag(prevItem, true); 
			        		holderforcell.add(new AttributeModifier("style", controlTitle));
			             	target.add(holderforcell);
			             	target.add(pLabelHoldForCell);
			            	}
		            	catch(Exception e)
			            	{
			            	e.printStackTrace();
			            	}
				    	}			    
					});
				
				holderforcell.add(new AjaxEventBehavior("dragstart")
					{
				    protected void onEvent(AjaxRequestTarget target)
				    	{
				    
				    	//undo if (StringUtils.isEmptyOrNull(holderforcell.getDefaultModelObjectAsString()))
				   
				    	cameFromHolder = true;
				    	cameFromPreview = false;
				    	prevItem = itemHoldforCell;
				    	}
					}
					);
				
				holdarearight = new WebMarkupContainer("holdarearight")
					{
				
					};
					add (holdarearight);
			arrowContainer.add (arrow = new Label("arrow", " "));
			arrowContainerright.add (arrowright = new Label("arrowright", " "));	
			this.useCarousel = useCarousel;
			PlateListHandler handler = new PlateListHandler(nItemsPerCol, nItemsPerRow, useCarousel);
			// issue 212
			if (items.size() > 0 && items.get(0).getGroup().getParent().getIs96Well())
				sortedSpacedItems = handler.condenseSortAndSpace(items);
			else 
				sortedSpacedItems = handler.condenseSortAndSpaceOriginal(items);
			spacedg = sortedSpacedItems;
			buildPlateListView(nItemsPerRow, nItemsPerCol, spacedg , ws);
		    add (plateListView);
			add(ajaxPagingNavigator = new AjaxPagingNavigator("navigator", plateListView));// Issue 283
			// issue 464	
			
			}
		public void buildPlateListView( int nItemsPerRow, int nItemsPerCol, List<WorklistItemSimple> items, WorklistSimple ws)
			{	
			gBothQCMPandMP = ws.getBothQCMPandMP();
			List <WorklistItemSimple> listCopy = new ArrayList <WorklistItemSimple> ();
			setMultiPart(true);
			handler = new PlateListHandler(nItemsPerCol, nItemsPerRow, useCarousel);
			// issue 212
			if (items.size() > 0 && items.get(0).getGroup().getParent().getIs96Well())
				sortedSpacedItems = handler.condenseSortAndSpace(items);
			else 
				sortedSpacedItems = handler.condenseSortAndSpaceOriginal(items);
			spacedg = sortedSpacedItems;
		 	try
	   			{
		 		
			    add(lhead10 = new Label("plateHead10", "10"));
			    add(lhead11 = new Label("plateHead11", "11"));
			    add(lhead12 = new Label("plateHead12", "12"));
	   			}
		 	catch  (Exception e)
		 		{
		 		replace(lhead10 = new Label("plateHead10", "10"));
			    replace(lhead11 = new Label("plateHead11", "11"));
			    replace(lhead12 = new Label("plateHead12", "12"));
		 		}			
			for (int i = 0; i < items.size(); i++)
				{
				listCopy.add(items.get(i).makeCopy());
				}
			if (listCopy.size() > 0)
				{
				items.clear();
				for (int i = 0; i < listCopy.size(); i++)
					items.add(listCopy.get(i));
				}						
	    	int nRowsNeeded = (int) Math.ceil((spacedg.size() > 0 ? spacedg.size() : items.size()) / (nItemsPerRow * 1.0));
			int nPlatesNeeded = (int) Math.ceil(nRowsNeeded / (nItemsPerCol * 1.0));
			int nRowsToCreate = nPlatesNeeded * nItemsPerCol;					
			// issue 205		    
			idsVsReasearcherNameMap =
				     sampleService.sampleIdToResearcherNameMapForExpId(ws.getSampleGroup(0).getExperimentId());								
			ws.populateSampleName(ws,idsVsReasearcherNameMap ); 
			spacedg = handler.updateWorkListItemsMovedSpacedg(ws, spacedg);
			itemMatrix = new WorklistItemSimpleMatrix(nRowsToCreate, nItemsPerRow, spacedg);
			//ws.setBothQCMPandMP (StringParser.parseId(ws.getPoolTypeA()).equals("CS00000MP") && StringParser.parseId(ws.getPoolTypeB()).equals("CS000QCMP") && poolSpacingA > 0 && poolSpacingB > 0); 
			plateListView = new PageableListView("plateRows", new PropertyModel<WorklistItemSimpleRow>(itemMatrix, "rows"), nItemsPerCol)
				{
				final int nPerCol = nItemsPerCol;
				public void populateItem(ListItem listItem)
					{		
					WorklistItemSimpleRow item = (WorklistItemSimpleRow) listItem.getModelObject();
					char c = ((char) ('A' + (item.getRowIndex() % nPerCol)));
					Character cs = c;
					String rowLabel = cs.toString();
					listItem.add(new Label("rowTitle", rowLabel));
					String commentString = "";
					String vSampleName;
					for (int i = 0; i < item.nItemsPerRow; i++)
						{							
						String property = "name." + i;
						String label = "position" + (i + 1);
						
						if (!item.getItem(i).getRepresentsControl())
							item.getItem(i).setResearcherName(idsVsReasearcherNameMap.get(item.getItem(i).getSampleName()));
						else 
							{							
							vSampleName = item.getItem(i).getSampleName();
							item.getItem(i).setShortSampleName(vSampleName.length() > 9 ? vSampleName.substring(0, 9) : vSampleName);
							}
						if (! item.getItem(i).getSampleName().contains("SB"))
					    	{					   
					    	// issue 212
					    	listItem.add(buildPlateLabelWorklistFieldNonStatic(gBothQCMPandMP, label, item.getItem(i), "sampleName",i, item.getRowIndex(), ws));
					    	}
						else // issue 215
							{
							if (workListBuilderPanel.change96WellBox.getDefaultModelObjectAsString().equals("true"))								
								listItem.add(buildPlateLabelWorklistFieldNonStatic(gBothQCMPandMP, label, new WorklistItemSimple(), "sampleName",i, item.getRowIndex(), ws));
							else 
								{
								listItem.add(buildPlateLabelWorklistFieldNonStatic(gBothQCMPandMP, label, item.getItem(i), "sampleName",i, item.getRowIndex(), ws));
								}	
							}
						}											    
					    if (workListBuilderPanel.change96WellBox.getDefaultModelObjectAsString().equals("false"))
					       try
						   		{
						    	listItem.add(lpos10 = buildPlateLabelWorklistFieldNonStatic(gBothQCMPandMP, "position10", new WorklistItemSimple(), "sampleName", 0, item.getRowIndex(), ws));
						    	listItem.add(lpos11 =buildPlateLabelWorklistFieldNonStatic(gBothQCMPandMP, "position11", new WorklistItemSimple(), "sampleName", 0, item.getRowIndex(), ws));
						    	listItem.add(lpos12 = buildPlateLabelWorklistFieldNonStatic(gBothQCMPandMP, "position12", new WorklistItemSimple(), "sampleName", 0, item.getRowIndex(),ws));
						   		}				    
						   catch (Exception e)
						    	{
						        listItem.replace(lpos10 = buildPlateLabelWorklistFieldNonStatic(gBothQCMPandMP, "position10", new WorklistItemSimple(), "sampleName", 0, item.getRowIndex(), ws));
						        listItem.replace(lpos11 =buildPlateLabelWorklistFieldNonStatic(gBothQCMPandMP, "position11", new WorklistItemSimple(), "sampleName", 0, item.getRowIndex(), ws));
						    	listItem.replace(lpos12 = buildPlateLabelWorklistFieldNonStatic(gBothQCMPandMP, "position12", new WorklistItemSimple(), "sampleName", 0, item.getRowIndex(),ws));
						    	}				    
						if (workListBuilderPanel.change96WellBox.getDefaultModelObjectAsString().equals("false"))
						    {
							lpos10.add(AttributeAppender.replace("style", "display:none"));
							lpos11.add(AttributeAppender.replace("style", "display:none"));
							lpos12.add(AttributeAppender.replace("style", "display:none"));
							lhead10.add(AttributeAppender.replace("style", "display:none"));
							lhead11.add(AttributeAppender.replace("style", "display:none"));
							lhead12.add(AttributeAppender.replace("style", "display:none"));						
						    }
				    }				
				};
			}
// issue 205	

		public   Label  buildPlateLabelWorklistFieldNonStatic (boolean bothQCMPandMP , final String id, final WorklistItemSimple item,  String field, int colPos, int rowPos, WorklistSimple ws)
			{
			// Issue 268 
			// issue 346
			// issue 17	
			// issue 217
	        if (!item.getRepresentsControl())
	        	//item.setCommentResearcherId(item.calcCommentToolTip(ws, item));
	        	item.setCommentResearcherId(StringUtils.isEmptyOrNull(item.getComments()) ? item.calcCommentToolTip(ws, item) : item.getComments()   );
	       
	        String firstHalf, secondHalf;
	        int halfWayPoint, numberlimit;
	    //    if ( !StringUtils.isNullOrEmpty(item.getCommentResearcherId()) && item.getCommentResearcherId().length() > 10 && (Math.round(item.getCommentResearcherId().length()/2)) >= 10)
	        if ( !StringUtils.isNullOrEmpty(item.getCommentResearcherId()) && item.getCommentResearcherId().length() > 10 )	
	            {
	        	numberlimit = item.getCommentResearcherId().length() >= 20 ? 20 : item.getCommentResearcherId().length();
	        	item.setCommentResearcherId(item.getCommentResearcherId().substring(0,numberlimit));
	        	halfWayPoint = Math.round(item.getCommentResearcherId().length()/2);
	        	firstHalf = item.getCommentResearcherId().substring(0, halfWayPoint) + "\n";
	        	secondHalf = item.getCommentResearcherId().substring(halfWayPoint+1);
	        	item.setCommentResearcherId(firstHalf + secondHalf);
	        	}
	        
	        // issue 227
	        // Issue 268 
			// issue 346
			// issue 17
	       
			if (bothQCMPandMP)
				{
				if (item.getShortSampleName().equals("CS00000MP") || item.getShortSampleName().equals("CS000QCMP"))
				    {
					item.setShortSampleName("CS00000MP\nCS000QCMP");
					item.setMpQcmpName("Master Pool (CS00000MP)\nMaster Pool.QCMP (CS000QCMP)");
				    }
				}
			if (!item.getNameForUserControlGroup().equals(""))
				field = "shortNameForUserControlGroup";
			// issue 217
			// issue 205
			// issue 229 
			
			else 
				{
				if (!item.getRepresentsControl())
					field = "commentResearcherId";
				else
					field = "shortSampleName";
				}

			Label pLabel = WorklistFieldBuilder.buildPlateLabelField(id, item, field);
			/////////////////////////////////////////////////
			
			String theCommentString = "";
			if (!item.getRepresentsControl())
				{
				theCommentString = item.getSampleName() +  (StringUtils.isEmptyOrNull(item.getSampleName()) ? ""  : "\n" +  (  StringUtils.isEmptyOrNull(item.getComments()) ? item.calcCommentToolTip(ws, item) : item.getComments() )   );
				pLabel.add(AttributeModifier.append("title",theCommentString));
				}
			else 
				pLabel.add(AttributeModifier.append("title",item.getSampleName().indexOf("-") >= 0 ? item.getSampleName().substring(0, item.getSampleName().lastIndexOf("-")) : item.getSampleName()  ));
			if (bothQCMPandMP)
			    if (item.getShortSampleName().equals("CS00000MP\nCS000QCMP"))
				    pLabel.add(AttributeModifier.replace("title",item.getMpQcmpName()));
			
			
			// issue 229
			pLabel.add(new AjaxEventBehavior("dragstart")			
			    {
			    protected void onEvent(AjaxRequestTarget target)
				    {
				    thePrevSampleName = pLabel.getDefaultModelObjectAsString();		  				       
				    cameFromHolder = false;
				    cameFromPreview = true;				    	
				    thePrevSampleName = pLabel.getDefaultModelObjectAsString();
		            prevItem = item;
		            String controlTitlee = WorklistFieldBuilder.assembleStyleTag(prevItem, true);
		            if (!controlTitlee.equals("background :#eaeef2"))
		                colorMap.put(prevItem.getSampleName().indexOf("-") >= 0 ? prevItem.getSampleName().substring(0, prevItem.getSampleName().lastIndexOf("-")) : prevItem.getSampleName(), controlTitlee);
		            pLabelPrev = pLabel; 
				    }
			    });
			    
			// issue 205
			// issue 205
			// issue 231			
			
			pLabel.add(new AjaxEventBehavior("drop")
					{
				    protected void onEvent(AjaxRequestTarget target)
				    	{			    	
				    	try
			            	{
				    		if (cameFromHolder)
				    			{
				    		 	if (StringUtils.isEmptyOrNull(pLabelHoldForCell.getDefaultModelObjectAsString()) || pLabelHoldForCell.getDefaultModelObjectAsString().contains("Drag Control")  )
							    	{
						    		target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("There is no control in the holding position.   Please drag a control to the holding position if you wish to move a control to a different page."));	
							    	return;
							    	}
				    			 thePrevSampleName = pLabelHoldForCell.getDefaultModelObjectAsString();
					             prevItem = itemHoldforCell;
					             String controlTitlee = WorklistFieldBuilder.assembleStyleTag(prevItem, true);
					             if (!controlTitlee.equals("background :#eaeef2"))
					                 colorMap.put(prevItem.getSampleName().indexOf("-") >= 0 ? prevItem.getSampleName().substring(0, prevItem.getSampleName().lastIndexOf("-")) : prevItem.getSampleName(), controlTitlee);
					             pLabelPrev = pLabelHoldForCell;
				    			}
			    	       if (prevItem.getSampleName().startsWith("S000"))
				                {
				            	target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("Please choose a control rather than sample:" + prevItem.getSampleName().replace("\n", "")));
				            	return;
				            	} 
				    		if (StringUtils.isEmptyOrNull(prevItem.getSampleName()))
				                {
				            	target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("Please do not move a blank square:" + pLabel.getDefaultModelObjectAsString().replace("\n", "")));
				            	return;
				            	}
				    		
				    	  	if (!edu.umich.brcf.shared.util.io.StringUtils.isEmptyOrNull(pLabel.getDefaultModelObjectAsString()) )
				        	 	{
				        		target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("Please drop on a blank space " ));
				            	return;
				        	 	} 
			            	thePrevSampleName = thePrevSampleName.replace("\n",  "");
			            	prevItem.setMpQcmpName(prevItem.getMpQcmpName().replace("\n",  ""));
			            	prevItem.setSampleName(prevItem.getSampleName().replace("\n",  ""));
			            	prevItem.setShortSampleName(prevItem.getShortSampleName().replace("\n",  ""));
			            	pLabel.setDefaultModelObject(thePrevSampleName.replace("CS00000MPCS000QCMP", "CS00000MP        CS000QCMP"));
			                item.setShortSampleName(thePrevSampleName);
			                item.setSampleName(thePrevSampleName);
			                item.setSampleType(prevItem.getSampleType());  //putback
			                item.setMpQcmpName(prevItem.getMpQcmpName());
			            
			                String controlTitle = colorMap.get(thePrevSampleName);
			             	
			             	if (StringUtils.isNullOrEmpty(controlTitle))
			             		{
			             		if (thePrevSampleName.contains("CS00000MP"))
			             			{
			             		    controlTitle = colorMap.get("CS00000MP");
			             		    if (StringUtils.isNullOrEmpty(controlTitle))
			             		        controlTitle = WorklistFieldBuilder.assembleStyleTag(prevItem, true);
			             			}
			             		else
			             		    {
			             			if (StringUtils.isNullOrEmpty(controlTitle))
			             				controlTitle = colorMap.get(thePrevSampleName + "-Pre");
			             			if (StringUtils.isNullOrEmpty(controlTitle))
			             				controlTitle = WorklistFieldBuilder.assembleStyleTag(prevItem, true);
			             		    }
			             		}
			             	if (prevItem.getRepresentsControl())
			             		{
			             		pLabel.add(AttributeModifier.replace("title",prevItem.getSampleType()));
			             		}
			             	pLabel.add(new AttributeModifier("style", controlTitle));
			             	pLabel.setDefaultModelObject(thePrevSampleName);
			             	target.add(pLabel);	
			           	if (bothQCMPandMP)
			            		if (item.getShortSampleName().equals("CS00000MP\nCS000QCMP") || item.getShortSampleName().equals("CS00000MPCS000QCMP") || item.getShortSampleName().equals("CS00000MP        CS000QCMP"))	    
			            			pLabel.add(AttributeModifier.replace("title",prevItem.getMpQcmpName()));
			        		
			             	item.setSamplePosition(calcPlatePositionBasedOnIndex(colPos, rowPos, wpMain.worklist.getIs96Well()));
			             	String preEnding = wpMain.worklist.getIs96Well() && item.getSampleType().contains("Pre")? "-Pre" : "";
			             	wpMain.worklist.controlsMovedMap.put(item.getSampleName().replace(" ",  "") + preEnding , item.getSamplePosition());
			            	for (Map.Entry<String, String> entry : wpMain.worklist.controlsMovedMap.entrySet()) 
			    				{			          
			            		for (WorklistItemSimple lilwi : wpMain.worklist.getItems())
				                	{
				             	    if (!lilwi.getRepresentsControl())
				             	    	continue;
				             	    if ((lilwi.getSampleName() + preEnding).contains(entry.getKey().toString()))
				             	    	lilwi.setSamplePosition(entry.getValue().toString());
				             	    if ((lilwi.getSampleName().contains("CS00000MP") 
				             			   || lilwi.getSampleName().contains("CS000QCMP")) 
				             			   && (entry.getKey().toString()
				             					  .equals("CS00000MPCS000QCMP")  ))	   
				             	    	lilwi.setSamplePosition(entry.getValue().toString());	
				                	}
			    				}	
			                WorklistItemSimple lItem = new WorklistItemSimple();
			                lItem.setSampleName(" ");
			                lItem.setSampleType(" ");
			                controlTitle = WorklistFieldBuilder.assembleStyleTag(lItem, true);
			                if (cameFromPreview) 
			                	{			                	
			                	pLabelPrev.add(new AttributeModifier("style", controlTitle));
			                	pLabelPrev.setDefaultModelObject(" ");	
			                	target.add(pLabel); //wow
			                	target.add(pLabelPrev);
			                	//cameFromPreview = false;
			                	}
			                
			                else if (cameFromHolder)
			                	{			                	
			                	holderforcell.add(new AttributeModifier("style", controlTitle));
			                	pLabelHoldForCell.setDefaultModelObject("Drag Control here to move");	
			                	itemHoldforCell.setShortSampleName("");
			                	target.add(holderforcell);
			                	target.add(pLabelHoldForCell);
			                	cameFromHolder = false;
			                	}
			                controlTitle = WorklistFieldBuilder.assembleStyleTag(prevItem, true);             
			             	List <WorklistItemSimple> lilitems = new ArrayList <WorklistItemSimple> ();
			             	lilitems.add(item);		             	
			                wpMain.worklist.setItemsMovedNewPositions(lilitems);
			                ws.setItemsMovedNewPositions(lilitems);
			                wpMain.lilmovedlist.addAll(ws.getItemsMovedNewPositions());
			             
			             	/************************/
			               // if (!cameFromPreview)
			                //	{
			                /***********************/
			                	Long currentPage =  plateListView.getCurrentPage();
			                	target.add(wpMain.form.platePreviewPageDialog);  // undo 
			                	wpMain.form.platePreviewPageDialog.open(target);
			                	plateListView.setCurrentPage(currentPage.intValue());
			               // 	}	
			                /***********************/
			                if (cameFromPreview)
			                	cameFromPreview = false;
			                target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("The control:" + item.getSampleName().replace("CS00000MPCS000QCMP", "CS00000MP CS000QCMP").replace("CS00000MP        CS000QCMP" ,"CS00000MP CS000QCMP") + " has been moved to position:" + item.getSamplePosition()));		                			                		            	
			            	}
		            	catch(Exception e)
			            	{
			            	e.printStackTrace();
			            	}
				    	}
					});
			return pLabel;
			}		
				
		// issue 205
		
		@Override
		protected void onSubmit() {  }
		
		// issue 205
		protected String calcPlatePositionBasedOnIndex( int colPos, int rowIndex, boolean is96Wells)
			{	
			String letterOfRow = "";
			int intForPlate = !is96Wells ? (int) Math.ceil((rowIndex + 1)/6) + ((rowIndex + 1)%6 >0 ? 1 : 0) : 
				                            (int) Math.ceil((rowIndex + 1)/8) + ((rowIndex + 1)%8 >0 ? 1 : 0);
			String plate = "P" + String.valueOf(intForPlate);
			String letterRow = 
					!is96Wells ? String.valueOf((rowIndex + 1)%6)
							   : String.valueOf((rowIndex + 1)%8);
			
			if (!is96Wells)
				{
				switch (letterRow)
					{
					case "1" : letterOfRow = "A"; break;
					case "2" : letterOfRow = "B"; break;
					case "3" : letterOfRow = "C" ; break;
					case "4" : letterOfRow = "D" ; break;
					case "5" : letterOfRow = "E"; break;
					case "0" : letterOfRow = "F"; break;
					default :  letterOfRow = "F"; break;
					}		
				}
			else
				{
				switch (letterRow)
					{
					case "1" : letterOfRow = "A"; break;
					case "2" : letterOfRow = "B"; break;
					case "3" : letterOfRow = "C" ; break;
					case "4" : letterOfRow = "D" ; break;
					case "5" : letterOfRow = "E"; break;
					case "6" : letterOfRow = "F"; break;
					case "7" : letterOfRow = "G"; break;
					case "0" : letterOfRow = "H"; break;
					default :  letterOfRow = "H"; break;
					}		
				}
			return plate + "-" + letterOfRow + String.valueOf(colPos + 1);	
			}
		
		public void clearHolding ()
			{
		    WorklistItemSimple lItem = new WorklistItemSimple();
            lItem.setSampleName(" ");
            lItem.setSampleType(" ");
            String controlTitle = WorklistFieldBuilder.assembleStyleTag(lItem, true);
            holderforcell.add(new AttributeModifier("style", controlTitle));
            pLabelHoldForCell.setDefaultModelObject("Drag Control\n here to move");	
            if (itemHoldforCell != null)
            	itemHoldforCell.setShortSampleName("");
			}
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
		
	}
