/*********************************
 * 
 * Created by Julie Keros Dec 1 2020
 */
package edu.umich.brcf.metabolomics.panels.admin.progresstracking;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Application;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.MarkupCache;
import org.apache.wicket.markup.MarkupFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.mysql.jdbc.StringUtils;

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.metabolomics.panels.admin.progresstracking.ProgressTrackingDefaultPage.ProgressTrackingDefaultForm;
import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixtureAliquotDetail;
import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixturesAdd;
import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.MotrpacOptionsDialog;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.domain.Workflow;
import edu.umich.brcf.shared.layers.dto.ProcessTrackingDetailsDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;


public class GantChart extends WebPage implements IMarkupResourceStreamProvider 
	{
	@SpringBean
	CompoundService compoundService;
	//String dateStartGantt;
	//String dateEndGantt;
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
	String str;
	long startDateGap;
	AjaxCheckBox currentCheckBox;
	AjaxCheckBox inProgressCheckBox;
	AjaxCheckBox onHoldCheckBox;
	AjaxCheckBox allExpChkBox;
	IndicatingAjaxLink ganttDateLink;
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;
	ListView listViewProgressTracking; // issue 61
	GantChart GantChart = this;
	EditProcessTrackingDetail editProcessTrackingDetail;
	List<ProcessTrackingDetails> currentList = new ArrayList <ProcessTrackingDetails> ();
	// itemList
	// issue 118
	String expID ;
	String assayDescID;
	DropDownChoice experimentDD;
	DropDownChoice assayDescDD;
	WebMarkupContainer gantChartWC;
	ListView gantChartListView;
	int dateStartingPointIndex = 0;
	List<ProcessTrackingDetails> nList = new ArrayList <ProcessTrackingDetails> () ;
	IResourceStream iResourceStream;
	MarkupContainer markupContainer;
	Class<?> containerClassl;
	GantChart gantChart = this;
	GantChartForm gantChartForm;
	boolean	isCurrent = false;
	boolean isInProgress = false;
	boolean isOnHold = false;
	boolean allExpAssay = false;
	boolean isAllExp = false;
	int gIndex = 0;
	int ggIndex = 0;
	 ModalWindow modal2 =new ModalWindow("modal2");;
	 long numDaysComplt = 0L;
	 long numDaysOnHold = 0L;
	 long numDaysUntilComplete = 0L;
	 DropDownChoice<String> userNamesDD;
	 String assignedTo ;
     
public boolean getIsCurrent ()
	{
	return isCurrent;
	}

public void setIsCurrent(boolean isCurrent)
	{
	this.isCurrent = isCurrent;	
	}

public boolean getIsInProgress ()
	{
	return isInProgress;
	}

public void setIsInProgress(boolean isInProgress)
	{
	this.isInProgress = isInProgress;	
	}

public boolean getIsOnHold ()
	{
	return isOnHold;
	}

public void setIsOnHold(boolean isOnHold)
	{
	this.isOnHold = isOnHold;	
	}

public boolean getIsAllExp ()
	{
	return isAllExp;
	}

public void setIsAllExp(boolean isAllExp)
	{
	this.isAllExp = isAllExp;	
	}


/////////////////////////////
@Override
public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) 
	{
	markupContainer = container;
	containerClassl = containerClass;
	//MarkupFactory.get().getMarkupCache();
	MarkupCache.get().clear();
/////////////////////////////////////////////
	Calendar mCalendar = Calendar.getInstance();    
	mCalendar.add(Calendar.MONTH, 8);
	List <String> monthStrList =  new ArrayList  <String> ();
	List <String> todayStrList = new ArrayList  <String> ();
	nList = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), allExpAssay, assignedTo, isCurrent, isInProgress, isOnHold);
   /// if (nList.size() == 0 )
   ////      return new StringResourceStream(" ");
	int ii = 0; 
	if (isCurrent)
		{
		dateStartingPointIndex = 0;
		Calendar curdate = Calendar.getInstance();
		currentList = new ArrayList <ProcessTrackingDetails> ();
		for (ProcessTrackingDetails lilp : nList)
			{
			if (nList.get(ii).getDateStarted().compareTo(curdate) >= 0)
				{
				currentList.add(lilp);
				}
			ii++;
			}
		}

	int indexx = 0;
    DateFormat formatter = new SimpleDateFormat("MM/dd");
    Calendar dayIncrementer = Calendar.getInstance(); 
    Calendar dayToAdd;
   
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	String startedString = 	(nList.size() == 0 || nList.get(0).getDateStarted()  == null) ? "" : (gantChartForm == null || StringUtils.isNullOrEmpty(gantChartForm.dateStartGantt) ?  sdf.format(nList.get(0).getDateStarted().getTime())  : gantChartForm.dateStartGantt   );
	
	Calendar forCurrent = Calendar.getInstance();
	////  if (isCurrent)
	////	startedString = sdf.format(forCurrent.getTime());		
    for (int i = dateStartingPointIndex; i < dateStartingPointIndex+14; i++)
	    	{	
    	    if (nList.size() == 0)
    	    	   break;
	    	Calendar calP = Calendar.getInstance();
	    	calP.setTime(new Date(startedString));
	    	if (!(gantChartForm == null) && !StringUtils.isNullOrEmpty(gantChartForm.dateStartGantt))
	    		{
	    		calP.setTime(new Date(gantChartForm.dateStartGantt));
	    		}
	    	//calP.setTime(new Date(dateStartGantt));
	    	
	    	calP.add(Calendar.DAY_OF_MONTH, i);
	    	sdf = new SimpleDateFormat("MM/dd/yyyy");
	    	String calendStr =  (calP == null) ? "" : sdf.format(calP.getTime());	    	
	    	todayStrList.add(calendStr.substring(0,5));
	    	}
 	String fillerDate1;
	String fillerDate2;
	String fillerDate3;
	String fillerDate4;
	String fillerDate5;
	String fillerDate6;
	String fillerDate7;
	
	List <String> flDateString = new ArrayList <String> () ;
	
	
	indexx = 0;
    str = "";
	str = 
    		 "<head>" +
					  "<style> " +
					  "* {" +
					  "    margin: 0;" +
					  "    padding: 0;" +
					  "    box-sizing: border-box;    " +
					  "  }" +
					  "  .container {" +
					  "      max-width: 1200px;" +
					  "      min-width: 650px;" +
					  "      margin: 0 auto;" +
					  "      padding: 50px;      " +
					  " border-radius: 10px; " +
					  " display: grid; " + 
					  " grid-row-gap: 0px;" + 
					  " grid-template-columns: auto auto ;" + 
					  "    border: 1px solid  #000;" +
					  " border-right: 1px solid rgba(0, 0, 0, 0.3);" +
				  
					  "  }" +
					  
                           ".grid {" + 
                           "  grid-row-gap: 0px; " + 
						   " grid-template-rows: auto auto auto auto auto auto auto auto auto auto auto auto auto auto auto;" + 
						"} " + 
					  
						
						"	#grid { " + 
						"	  grid-row-gap: 0px; " + 
						"	}" + 

					  " " +
					  ////////////////stuff for table
					  
                      " table, th, td { " + 
					
  					" border-right: 1px solid rgba(0, 0, 0, 0.3);" +
					"    background-color:#808080;" +
					" border-collapse: collapse; " + 
					"	} " + 

    			"tablerange{ " + 
    			    " border: none;" + 
    			    " border-right: none; " + 
    			  //  "      border: 2px solid #000;" +
  					//" border-right: 1px solid rgba(0, 0, 0, 0.3);" +
					"    background-color:#FFFFFF;" +
					//" border-collapse: collapse; " + 
					"	} " +   
					  
					  ///////////////////////
					  "  .chart {" +
					  "      display: grid;" +
					  "      border: 2px solid #000;" +
					  " border-right: 1px solid rgba(0, 0, 0, 0.3);" +
					  "      position: relative;" +
					  "      overflow: hidden;  " +
					  "  }" +
					  "  .chart-row {" +
					  "   " + 
					  "    display: grid;      " +
					  "         grid-template-columns: 300px 1fr;1fr;1fr;1fr;1fr;1fr;1fr;1fr;1fr;1fr;1fr;1fr;1fr;1fr;1fr;" +
					  "         background-color: #DCDCDC;" +
					  "      border: 2px solid #000;" +
					  "         border-right: 1px solid rgba(0, 0, 0, 0.3);" +
					  " grid-row-gap: 0px; " + 
					  "  }" +
					  "  .chart-row:nth-child(odd) {" +
					  "         background-color: #DCDCDC;" +
					  "      border: 2px solid #000;" +
					  "         border-right: 1px solid rgba(0, 0, 0, 0.3);" +
					  "  }" +
					  "  .chart-period {" +
					  "    color:  #fff;" +
					  "    background-color:  #708090 !important;" +
					  "    border-bottom: 2px solid #000; " +
					  "      border: 2px solid #000;" +
					  "         border-right: 1px solid rgba(0, 0, 0, 0.3);" +
					  "    grid-template-columns: 300px 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr  1fr;" +
" grid-row-gap: 0px; }" +
"  .chart-lines {" +
"    position: absolute;" +
"    height: 100%;" +
"    width: 100%;" +
"    background-color: transparent;" +
"    grid-template-columns: 300px 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr;" +
"  grid-row-gap: 0px;}" +
"  .theborder {  "   + 
"   border-right: 1px solid rgba(0, 0, 0, 0.3);" +
"  }" +
"  .chart-period > span {    " +
"    text-align: center;" +
"               font-size: 13px;" +
"               align-self: center;" +
"               font-weight: bold;" +
"    padding: 10px 0;    " +
"  }" +
"  .chart-lines > span {" +
"    display: block;" +
"    border-right: 1px solid rgba(0, 0, 0, 0.3);" +
"  }" +
"  grid-template-columns {" + 
"        " + 
"    border: 1px solid  #000;" +
"  border-right: 1px solid rgba(0, 0, 0, 0.3);  grid-row-gap:0px;   } " +
"  grid_column {" + 
"        " + 
"    border: 1px solid  #000;" +
"  border-right: 1px solid rgba(0, 0, 0, 0.3);     } " +
"  span {" + 
"        " + 
"  border-right: 1px solid rgba(0, 0, 0, 0.3);    } " +
"  td {" + 
"        " + 
"  font-weight: bold;"
+ "   } " +
" " +
"  .chart-row-item {" +
"    background-color:#808080;" +
"    border: 1px solid  #000;" +
"    border-top: 0;" +
"    border-left: 0;      " +
"    padding: 9px 0;" +
"    font-size: 10px;" +
"    font-weight: bold;" +
"    text-align: center;" +
"  } " +
"" +
"  .chart-row-item-big {" +
"    background-color:#808080;" +
"    border: 1px solid  #000;" +
"    border-top: 0;" +
"    border-left: 0;      " +
"    border-right: 1px solid rgba(0, 0, 0, 0.3);" +
"    padding: 9px 0;" +
"    font-size: 11px;" +
"    font-weight: bold;" +
"    text-align: left;" +
"    width: 300px;" + 
"  } " +


"  .chart-row-item-big2 {" +
//"    background-color:#808080;" +
" border: none;" + 
"    padding: 9px 0;" +
"    font-size: 11px;" +
"    font-weight: bold;" +
"    text-align: left;" +
"    width: 300px;" + 
"  } " +


"  .chart-row-bars {" +
"    list-style: none;" +
"               display: grid;" +
"               padding: 15px 0;" +
"               margin: 0; grid-row-gap:0px;" +
"               grid-template-columns:  repeat(15, 1fr);" +
"               grid-gap: 0px 0;" +
//"               border-bottom: 1px solid  #000;" +
"    border-right: 1px solid rgba(0, 0, 0, 0.3);" +
"  }" +
"  li {" +
"    font-weight: 450;" +
"               text-align: left;" +
"               font-size: 15px;" +
"               min-height: 15px;" +
"               background-color: #708090;" +
"               padding: 5px 15px;" +
"               color: #fff;" +
"               overflow: hidden;" +
"               position: relative;" +
"               cursor: pointer;" +
"               border-radius: 25px;" +
"                height: 15px; " + 
"    border-right: 1px solid rgba(0, 0, 0, 0.3);margin-top: 0;" +
"  }" +
"  ul {  "   + 
"   border-right: 1px solid rgba(0, 0, 0, 0.3); margin-top: 0;"
+ "  }" ;

 
	////////////////////////////// small for loop /////////////
	//////////////////////////////                /////////////
	
	String theStartDate ;
	String theEndingDate;
	int indexxofDate ;
	int indexxofEndingDate = 0;

	//// handle all rows...
	for (int i = 0; i<= nList.size() -1 ; i++)
		{
		theStartDate = nList.get(i).convertToDateString(nList.get(i).getDateStarted()).substring(0,5);
		Calendar dCalendar = Calendar.getInstance();
		int daysExpected = Integer.parseInt(nList.get(i).getDaysExpected());
        
		dCalendar.setTime(new Date(nList.get(i).convertToDateString(nList.get(i).getDateStarted())));
	
		
		dCalendar.add(Calendar.DAY_OF_MONTH, daysExpected);
		theEndingDate = sdf.format(dCalendar.getTime());
		
		indexxofDate = todayStrList.indexOf(theStartDate);
		indexxofEndingDate = todayStrList.indexOf(theEndingDate.substring(0,5));
		long daysBetween = 0L;
		if (nList.get(i).getStatus().equals("In progress"))
		    {
			if (nList.get(i).getDateStarted().compareTo(Calendar.getInstance()) <= 0 )
		        {
				daysBetween = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(),Calendar.getInstance().toInstant() );
				}
			else 
			    {
				daysBetween =  StringUtils.isNullOrEmpty(nList.get(i).getDaysExpected()) || (Integer.parseInt(nList.get(i).getDaysExpected()) <= 0) ? 1 : Integer.parseInt(nList.get(i).getDaysExpected()) - 1;
			  	}
		  	}
		else 
	  	    {
		    if (!(nList.get(i).getDateOnHold() == null)) 
			    daysBetween = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(), nList.get(i).getDateOnHold().toInstant());
		    else if (!(nList.get(i).getDateCompleted() == null)) 
			    daysBetween = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(), nList.get(i).getDateCompleted().toInstant());		  	
	  	    }	    
		 String showit;
		 if (indexxofDate <0)
		 	 {
			 str = str + 
			  "  ul .chart-li-" + (i) +  "{" +
			  "    grid-column:" +  "0" + "/" + "0" +   "; grid-row:2; " ;
			  showit =  "  ul .chart-li-" + (i) +  "{" +
					  "    grid-column:" +  "0" + "/" + "0" +    "; grid-row:2; " ;
		 	 }
		 else
			 {
			 str = str + 
			 "  ul .chart-li-" + (i) +  "{" +
			// "  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate ) + "/" + (indexxofDate +  (daysBetween + 2 > 15 ? 15: daysBetween + 2) ) +      ";  grid-row:2; "   ;
			"  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate ) + "/" + (  (indexxofDate +  daysBetween + 2) > 15 ? 15 :  (indexxofDate +  daysBetween + 2) )  +      ";  grid-row:2; "   ;
			  showit =  "  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate ) + "/" + (  (indexxofDate +  daysBetween + 2) > 15 ? 15 :  (indexxofDate +  daysBetween + 2) )  +      ";  grid-row:2; "   ;
			 }
		 str = str + "   background-color:#4C9A2A;" ;		 
		 str = str + 
				 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
				 "  }" ;
		 
		 //////////////////////
		 
		 if (nList.get(i).getDateCompleted() != null)
		 	{
			 numDaysComplt = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(), nList.get(i).getDateCompleted().toInstant()); 
			 str = str + 
					  "  ul .chart-li-cmplt-" + (i) +  "{" +
				//	  "    grid-column:" +  ( numDaysComplt > 14 ? 14 :  (1+ indexxofDate + numDaysComplt + 1)) + "/" + ( numDaysComplt > 14 ? 14 :  (indexxofDate +  numDaysComplt + 1)) +      ";  grid-row:2; "   ;
				"    grid-column:" +   (1+ indexxofDate + numDaysComplt + 1) + "/" + (     ( indexxofDate +  numDaysComplt + 1) > 15 ? 15 : ( indexxofDate +  numDaysComplt + 1)     ) +      ";  grid-row:2; "   ;
			 str = str + "   background-color:#0000FF;" ;
			 str = str + 
			(numDaysComplt > 15 ? "    border-right: 1px ; border-style: dotted; " : "    border-right: 1px solid ") + " rgba(0, 0, 0, 0.3); height: 5px;" +
					 "  }" ;	
			 showit =  "  ul .chart-li-cmplt-" + (i) +  "{" +
					  "    grid-column:" +  (1+ indexxofDate + numDaysComplt + 1) + "/" + (indexxofDate +  numDaysComplt + 1) +      ";  grid-row:2; "   ;
		 	}
		 
		 //////////////////////
		 		 	 
		 if (nList.get(i).getDateOnHold() != null)
		 	{
			 numDaysOnHold = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(), nList.get(i).getDateOnHold().toInstant()); 
			if ( nList.get(i).getDateOnHold() != null &&  nList.get(i).getDateCompleted() != null)
				numDaysUntilComplete = ChronoUnit.DAYS.between(nList.get(i).getDateOnHold().toInstant(), nList.get(i).getDateCompleted().toInstant());
			else 
				numDaysUntilComplete = 0;
			 str = str + 
					  "  ul .chart-li-onHold-" + (i) +  "{" +
			 		  "    grid-column:" +  (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       )) + "/" + (    (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1))  ) > 15 ? 15 :  (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1)) )    ) +      ";  grid-row:2; "   ;
			 str = str + "   background-color:#FF0000;" ;
			 str = str + 
					 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
					 "  }" ;		 	
		 	}		 
		 //////////////////////
		 
		 str = str + 
				 "  ul .chart-li-" + (i)  + "-expect {" +
			 "    grid-column:" +  (1 + indexxofDate) + "/" + ( StringUtils.isNullOrEmpty(nList.get(i).getDaysExpected()) ? 1 : (Integer.parseInt(nList.get(i).getDaysExpected()) + 1) + indexxofDate) +   ";  grid-row:1; "  ;
				 str = str + "   background-color:#000000;" ;

				 str = str + 
				 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 1px;" +
				 "  }" ;
				 
				 str = str + 
						 "  ul .chart-li-" + (i) +  "-comment {" +
						 "    grid-column:" +  "15" + "/" + "15" +   ";  grid-row:1; "  ;

						 str = str + "   background-color:transparent;" ;
						 str = str + 
						 " border: none; width: 200px; height: 30px;" + 
						 "  }";				 
		}
	//////////////////////////////////////////////////	
	
long daysBetween;
str = str +  
"" +
"</style>" +
"</head>" +
"" +
"<form wicket:id = \"gantChartForm\" > " + 
" <span style=\"  border: none; \"> " + " Start: </span> " + 
"<span style=\"outline: 1px solid rgba(0, 0, 0, 0.3)\"> <input style=\"   background-color:#FFFFFF;   \" wicket:id=\"dateStartGantt\" type=\"text\" value=\"date\"  size=\"10\"> </span> " + 
"<span style=\"  border: none; \"> " + "&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp; End: </span> " + 
"<span style=\"outline: 1px solid rgba(0, 0, 0, 0.3)\"> <input style=\"  background-color:#FFFFFF;   \" wicket:id=\"dateEndGantt\" type=\"text\" value=\"date\"  size=\"10\">  </span>" + 
" <span style=\"border:none\">  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  </span>  " + 
" <span style=\"outline: 1px solid ;  \"> <input style=\"   background-color: #D3D3D3;   \" wicket:id=\"dateRangeBtn\" type=\"submit\" value=\"Calculate Date Range\"  size=\"20\"> </span> " + 

" <span style=\"border:none\">  &nbsp;&nbsp; Experiment:  </span>  " + 

"  <select wicket:id = \"experimentDropDown\" style = \"margin-left : 15px; width : 100px;\" ></select> &nbsp;&nbsp;&nbsp; " + 
" <span style=\"border:none\">   Assay:  </span>  " + 
"  <select wicket:id = \"assayDescDropDown\" style = \"margin-left : 15px; width : 100px;\" ></select> &nbsp;&nbsp;&nbsp; " + 
" <span style= \"border: none; \"> All Experiments? </span> <input style=\"   background-color: #D3D3D3;   \" wicket:id=\"allExpChkBox\" type=\"checkbox\" value=\"View Current?\"  size=\"20\"> " + 
" <span style=\"border:none\">  &nbsp;&nbsp;  Assigned To:  </span>  " + 
"  <select wicket:id = \"assignedToDropDown\" style = \"margin-left : 15px; width : 100px;\" ></select>  " +
" <span style=\"outline: 1px solid ;  \"> <input style=\"   background-color: #D3D3D3;   \" wicket:id=\"AllUsersBtn\" type=\"submit\" value=\"All Users \"  size=\"10\"> </span> " + 

"<br> <br>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <button wicket:id = \"leftArrow\"  type= \"submit\"  >   <span>  &#x2190;  </span>     </button>   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <button wicket:id = \"rightArrow\"  type= \"submit\"  >   <span>  &#x2192;  </span>  </button> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  <span style= \"border: none; \"> View Current and Future? </span> <input style=\"   background-color: #D3D3D3;   \" wicket:id=\"currentChkBox\" type=\"checkbox\" value=\"View Current?\"  size=\"20\">   &nbsp;&nbsp;&nbsp;&nbsp; <span style= \"border: none; \"> View In Progress? </span> <input style=\"   background-color: #D3D3D3;   \" wicket:id=\"inProgressChkBox\" type=\"checkbox\" value=\"View Current?\"  size=\"20\">   <span style= \"border: none; \"> View On Hold? </span>  <input style=\"   background-color: #D3D3D3;   \" wicket:id=\"onHoldChkBox\" type=\"checkbox\" value=\"View Current?\"  size=\"20\">      " + 


"<br> <br>     <div class=\"container\">" +

"<div wicket:id=\"modal2\"></div>" +
/////////////////////////////////////////////////////
"<table> <tr> <td> " +  "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +  "  </td> <td>&nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp;  &nbsp; &nbsp;  &nbsp; &nbsp;  &nbsp; &nbsp;  &nbsp; &nbsp;      week 1   &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp;   &nbsp; &nbsp; &nbsp; &nbsp;   </td> <td>  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp;  &nbsp; &nbsp;&nbsp; &nbsp;    week 2  &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;&nbsp; </td> </tr> " ;

//flDate3.add(Calendar.DAY_OF_MONTH, 3);
Calendar cal = Calendar.getInstance();
Calendar calEndWeek = Calendar.getInstance();

if (gantChartForm != null &&!StringUtils.isNullOrEmpty(gantChartForm.dateStartGantt))
	{
	cal.setTime(new Date(gantChartForm.dateStartGantt));
	calEndWeek.setTime(new Date(gantChartForm.dateStartGantt));
//// fixj	dateStartingPointIndex = 0;
	}
else if (gantChartForm != null &&StringUtils.isNullOrEmpty(gantChartForm.dateStartGantt))
	{
	sdf = new SimpleDateFormat("MM/dd/yyyy");
	startedString = 	(nList.size() == 0 || nList.get(0).getDateStarted()  == null) ? "" : sdf.format(nList.get(0).getDateStarted().getTime());
	if (nList.size() > 0 )
		{
		cal.setTime(new Date(startedString));
		calEndWeek.setTime(new Date(startedString));
		}
	}
cal.add(Calendar.DAY_OF_MONTH, dateStartingPointIndex);
calEndWeek.add(Calendar.DAY_OF_MONTH, 6 + dateStartingPointIndex);

sdf = new SimpleDateFormat("MM/dd/yyyy");

String calStr =  (cal == null) ? "" : sdf.format(cal.getTime());
String calEndWeekStr =  (calEndWeek == null) ? "" : sdf.format(calEndWeek.getTime());


Calendar calend = Calendar.getInstance();
Calendar calEndWeekend = Calendar.getInstance();

if (  nList.size() > 0 && gantChartForm != null && !StringUtils.isNullOrEmpty(gantChartForm.dateStartGantt))
	{
	calend.setTime(new Date(gantChartForm.dateStartGantt));
	calEndWeekend.setTime(new Date(gantChartForm.dateStartGantt));
	}
else if (nList.size() > 0 &&  gantChartForm != null &&StringUtils.isNullOrEmpty(gantChartForm.dateStartGantt))
	{
	sdf = new SimpleDateFormat("MM/dd/yyyy");
	//String startedString = 	(nList.get(0).getDateStarted()  == null) ? "" : sdf.format(nList.get(0).getDateStarted().getTime());
	startedString = 	(nList.size() == 0 || nList.get(0).getDateStarted()  == null) ? "" : sdf.format(nList.get(0).getDateStarted().getTime());
	calend.setTime(new Date(startedString));
	calEndWeekend.setTime(new Date(startedString));
	}

calend.add(Calendar.DAY_OF_MONTH, 7 + dateStartingPointIndex);
calEndWeekend.add(Calendar.DAY_OF_MONTH, 13 + dateStartingPointIndex);




String calendStr =  (calend == null) ? "" : sdf.format(calend.getTime());
String calEndWeekEndStr =  (calEndWeekend == null) ? "" : sdf.format(calEndWeekend.getTime());




str = str + 
" <tr><td>  </td> <td>   &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " +  calStr + "-" + calEndWeekStr +        " &nbsp; </td> <td>  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; "           + calendStr + "-" + calEndWeekEndStr + "    </td> </tr> </table> <br>" +



"      <div class=\"chart\">" ;

str = str + "      <div  class=\"chart-row chart-period  \">" ;
///////////////////////////

str = str + "          <div class=\"chart-row-item-big\"></div> " ;
if (nList.size() > 0)

str = str + "          <span style=\"border:none\">" + todayStrList.get(0) + "</span>" + 
"          <span style=\"border:none\"> " + todayStrList.get(1) + "</span>" + 
"          <span style=\"border:none\">" + todayStrList.get(2)  + "</span>" +
"          <span style=\"border:none\"> " + todayStrList.get(3) + "</span>" + 
"          <span style=\"border:none\"> " + todayStrList.get(4) + "</span>" + 
"          <span style=\"border:none\"> " + todayStrList.get(5) + "</span>" + 
"          <span style=\"border:none\"> " + todayStrList.get(6) + "</span>" + 
"          <span style=\"border:none\"> " + todayStrList.get(7) + "</span>" + 
"          <span style=\"border:none\"> " + todayStrList.get(8) + "</span>" + 
"          <span style=\"border:none\"> " + todayStrList.get(9) + "</span>" + 
"          <span style=\"border:none\">  " + todayStrList.get(10) + "</span>" + 
"          <span style=\"border:none\"  > " + todayStrList.get(11) + "</span>" + 
"          <span style=\"border:none\">  " + todayStrList.get(12) + "</span>" + 
"          <span style=\"border:none\">  " + todayStrList.get(13) + "</span>" + 
//"          <span style=\"border:none; width:200px; \"> " + "comments" + "</span>" + 
"          <span style=\"border:none; width:200px;  \"> " + "comments" + "</span>" ; 



/////////




str = str + "      </div>" ;





str = str + "      <div class=\"chart-row chart-lines\">" +
"        <span></span><span></span><span></span>" +
"<span></span><span></span><span></span>" +
"                               <span></span><span></span><span></span>" +
"                               <span></span><span></span><span></span>  <span></span> " +  
//"  <span > </span> <span > </span> <span style=\" width:200px; \"> </span>  " + 
 "  <span > </span> <span > </span> <span style=\" width:200px; \" > </span>  " + 
"      </div>" ;
    
    
   ///// nList = processTrackingService.loadAllTasksAssigned(expID);
    int index = 0;
    String prevWf = "";
    String prevExpId = "";
    String prevAssayId = "";
    int indexxofCDate = 0;
    String theCDate = "";
    int indexLink = 0;
    for (ProcessTrackingDetails ptd: nList)
    	{
    	
    	theStartDate = nList.get(indexLink).convertToDateString(nList.get(indexLink).getDateStarted()).substring(0,5);
    	indexxofDate = todayStrList.indexOf(theStartDate); 
    	if (nList.get(indexLink).getDateCompleted() != null)
    		{
    		theCDate = nList.get(indexLink).convertToDateString(nList.get(indexLink).getDateCompleted()).substring(0,5);
    		indexxofCDate = todayStrList.indexOf(theCDate);
    		}
    	Calendar dCalendar = Calendar.getInstance();
		int daysExpected = Integer.parseInt(nList.get(indexLink).getDaysExpected());
        
		
		
		dCalendar.setTime(new Date(nList.get(indexLink).convertToDateString(nList.get(indexLink).getDateStarted())));
		dCalendar.add(Calendar.DAY_OF_MONTH, daysExpected);
		theEndingDate = sdf.format(dCalendar.getTime());
    	
    	
    	
    	indexxofEndingDate = todayStrList.indexOf(theEndingDate.substring(0,5));
    	if (index == 0 )
	        {
		   prevWf = ptd.getWorkflow().getWfDesc();
		   prevExpId = ptd.getExperiment().getExpID();
		   prevAssayId = ptd.getAssay().getAssayName() + " (" +  ptd.getAssay().getAssayId() + ")";
		   
		   str = str + 
   				" <span>" + ptd.getWorkflow().getWfDesc().replace("<", "&lt").replace(">", "&gt")  + " <br>" + 
				     ptd.getExperiment().getExpID() + " " + ptd.getAssay().getAssayName() + " (" +  ptd.getAssay().getAssayId() + ")" + "</span>";
		   
	       }
	    else if (!prevWf.equals(ptd.getWorkflow().getWfDesc()) || !prevExpId.equals(ptd.getExperiment().getExpID()) ||  !prevAssayId.equals(ptd.getAssay().getAssayName() + " (" +  ptd.getAssay().getAssayId() + ")") )
	   	   {
		   str = str + " <span>" + ptd.getWorkflow().getWfDesc().replace("<", "&lt").replace(">", "&gt")  + " <br>" + 
			     ptd.getExperiment().getExpID() + " " + ptd.getAssay().getAssayName() + " (" +  ptd.getAssay().getAssayId() + ")" + "</span>";
	       index = 0;
	       prevWf = ptd.getWorkflow().getWfDesc();
	       prevExpId = ptd.getExperiment().getExpID();
	       prevAssayId = ptd.getAssay().getAssayName() + " (" +  ptd.getAssay().getAssayId() + ")";
	   	   }
    	   
    	str = str +    		
    				"<div>" + 
        			"  <a style=\"text-decoration:none;\" wicket:id= \"gchart" + Integer.toString(indexLink) + "\"   href=\"#\" > <div class=\"chart-row\">" +
        			"        <div style=\"text-decoration:none;font-size: 15px;font-weight: bold;color:black; \"  class=\"chart-row-item-big\">" + (index + 1) + ". " + ptd.getProcessTracking().getTaskDesc() + "\n" + "<br>"  + "Assigned To:" +  ptd.getAssignedTo().getFullName() +   "</div>" ;		
    				
    		String truncComment = StringUtils.isNullOrEmpty(nList.get(index).getComments()) ||  nList.get(index).getComments().length() < 50 ? nList.get(index).getComments() : nList.get(index).getComments().substring(0, 50);
    		if (StringUtils.isNullOrEmpty(truncComment))
    			truncComment = "";
    		/// resume here
    		if (indexxofDate >= 0  && indexxofEndingDate>= 0 )		           
		 			str = str + "        <ul class=\"chart-row-bars\">" +
		 			( nList.get(indexLink).getStatus().equals("In queue") ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" ) +		 					
		 		(nList.get(indexLink).getDateCompleted() != null ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
		 		(nList.get(indexLink).getDateOnHold() != null ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
		 		" <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" +  
		 	     	" <li style= \" width:200px; color:black;text-align:center;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">" +  truncComment +  "</li> " +
	
		 		"                               </ul>" ;
    		str = str + "      </div> </a></div>";    	
    	    index++;
    	    indexLink++;
    	
    	} // end of for loop

        str = str + 
        		"    </div>" +
        		"" +
        		"</div> </form>";
    return new StringResourceStream(str);
	}

public GantChart (String id)
	{
	assignedTo = "";
	add(gantChartForm = new GantChartForm("gantChartForm"));
    
	}


public class GantChartForm extends Form 
	{
	String dateStartGantt;
    String dateEndGantt;
   // boolean	isCurrent;
    
    
//////////////////////////////
    
    List<ProcessTrackingDetails> nList;
    
    public List<ProcessTrackingDetails> getNList ()
    	{
        return nList;
    	}
  
    public void setNList (List<ProcessTrackingDetails> nList)
    	{
    	this.nList = nList;
    	}
    
	public String getDateStartGantt ()
		{
		return this.dateStartGantt;
		}
	public void setDateStartGantt (String dateStartGantt)
		{
		this.dateStartGantt = dateStartGantt;
		}

public String getDateEndGantt ()
	{
	return this.dateEndGantt;
	}
public void setDateEndGantt (String dateEndGantt)
	{
	this.dateEndGantt = dateEndGantt;
	}



	public GantChartForm(String id) 
		{
		///////////////////
		super(id, new CompoundPropertyModel(gantChart));	
		getMarkupResourceStream(markupContainer, containerClassl) ;
	    nList = new ArrayList <ProcessTrackingDetails> () ;
	    
	    for (int i=0 ; i<= nList.size();i++ )
		 	{
	    	try
	    	   {
			   add(buildGanttChartLink(("gchart" + Integer.toString(i)),i));
	    	   }
	    	catch (Exception e)
	    	   {
	    		remove(buildGanttChartLink(("gchart" + Integer.toString(i)),i));
	    		add(buildGanttChartLink(("gchart" + Integer.toString(i)),i));
	    	   }
	    	}
		currentCheckBox = buildScreeningChkBox("currentChkBox", "isCurrent");
		inProgressCheckBox = buildScreeningChkBox("inProgressChkBox", "isInProgress");
		onHoldCheckBox = buildScreeningChkBox("onHoldChkBox", "isOnHold");
		allExpChkBox = buildScreeningChkBox("allExpChkBox", "isAllExp");
		add (currentCheckBox); 
		add (inProgressCheckBox); 
		add (onHoldCheckBox); 
		add (allExpChkBox);
		
		 nList = new ArrayList <ProcessTrackingDetails> ();
		 gIndex = 0;
		
		 add(experimentDD = buildExperimentDropDown("experimentDropDown"));
		 experimentDD.setOutputMarkupId(true);
		 add(assayDescDD = buildAssayDescDropDown("assayDescDropDown"));
		
		 add(userNamesDD = buildUserAssignedDropDown("assignedToDropDown"));
		 
		 assayDescDD.add(buildStandardFormComponentUpdateBehavior("change", "updateAssayDesc"));
		 experimentDD.add(buildStandardFormComponentUpdateBehavior("change", "updateExperiment"));
		 userNamesDD.add(buildStandardFormComponentUpdateBehavior("change", "updateUser"));
		 experimentDD.setNullValid(true);
		IndicatingAjaxLink <Void> bldArrowLnk = buildRightArrow ("rightArrow", 1 );
		add (bldArrowLnk);
		
		IndicatingAjaxLink <Void> bldLeftArrowLnk = buildLeftArrow ("leftArrow", 1 );
		add (bldLeftArrowLnk);
		
		 METWorksAjaxUpdatingDateTextField dateFld =  new METWorksAjaxUpdatingDateTextField("dateStartGantt", new PropertyModel<String>(this, "dateStartGantt"), "dateStartGantt")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)  
		        { 
		        }
			};		
		dateFld.setDefaultStringFormat(ProcessTracking.ProcessTracking_DATE_FORMAT);
		add(dateFld);
		dateFld.setRequired(true);
		
		METWorksAjaxUpdatingDateTextField dateFldEnd =  new METWorksAjaxUpdatingDateTextField("dateEndGantt", new PropertyModel<String>(this, "dateEndGantt"), "dateEndGantt")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)  
		        { 
		        }
			};		
		dateFldEnd.setDefaultStringFormat(ProcessTracking.ProcessTracking_DATE_FORMAT);
		add(dateFldEnd);
		dateFldEnd.setRequired(true);
		dateFld.add(buildStandardFormComponentUpdateBehavior("change", "updateStartDate"));
		dateFldEnd.add(buildStandardFormComponentUpdateBehavior("change", "updateEndDate"));
		
		
		//////////// Date calculate button    /////////////////////////
		
		add(ganttDateLink =  new IndicatingAjaxLink <Void>("dateRangeBtn") 
		    {			
			
			@Override
			public boolean isEnabled()
				{
				return true;
				}
			@Override
			public void onClick(AjaxRequestTarget target) 			     
			    {	
				MarkupCache.get().clear();
				getMarkupResourceStream(markupContainer, containerClassl) ;
				target.add(gantChart);
				/// fixj
				dateStartingPointIndex = 0;
			    }
		    });
		
		
		
		
		add(ganttDateLink =  new IndicatingAjaxLink <Void>("AllUsersBtn") 
	    {			
		@Override
		public boolean isEnabled()
			{
			return true;
			}
		@Override
		public void onClick(AjaxRequestTarget target) 			     
		    {
			
			assignedTo = "";
			nList = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), allExpAssay, assignedTo); 
			MarkupCache.get().clear();
			getMarkupResourceStream(markupContainer, containerClassl) ;
			target.add(gantChartForm);
			target.add(gantChart);
			AjaxLink gCLink;
			ggIndex = 0;
			// nList = new ArrayList <ProcessTrackingDetails> () ;
			    for (int i=0 ; i< nList.size();i++ )
				 	{
			    	ggIndex = i;
			    	try
				    	{
						gantChartForm.add(gCLink = buildGanttChartLink(("gchart" + Integer.toString(i)),i));
				    	}
			    	catch (Exception e)
			    		{
			    		gantChartForm.replace(buildGanttChartLink(("gchart" + Integer.toString(i)  ),i));
			    		}
			    	}
			target.add(gantChart);
			target.add(gantChartForm);
		    }
	    });
		
		
		modal2= new ModalWindow("modal2");
		modal2.setInitialWidth(1500);
        modal2.setInitialHeight(600);
        modal2.setWidthUnit("em");
        modal2.setHeightUnit("em"); 
        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	MarkupCache.get().clear();
	    	    getMarkupResourceStream(markupContainer, containerClassl) ;
	    	    target.add(gantChart);
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
        
			
        } // end of constructor
	}
	/////
	// issue 94	


	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(  String event,  String response)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
				{	
				switch (response)
					{
					case "updateStartDate" :
						break;	
						// issue 13
					case "updateEndDate" :
					    break;
					case "updateExperiment" :
						allExpAssay = false;
						assayDescDD.setChoices (assayService.allAssayNamesForExpId(expID, false));	
						MarkupCache.get().clear();
				    	    getMarkupResourceStream(markupContainer, containerClassl) ;
				    	    target.add(gantChart); 
						break;
					case "updateAssayDesc" :
						allExpAssay = false;
						assayDescDD.setChoices (assayService.allAssayNamesForExpId(expID, false));	
						MarkupCache.get().clear();
				    	    getMarkupResourceStream(markupContainer, containerClassl) ;
				    	    target.add(gantChartForm);
				    	    target.add(gantChart);
				    	    for (int i=0 ; i< nList.size();i++ )
							 	{
						    	ggIndex = i;
						    	try
							    	{
									gantChartForm.add( buildGanttChartLink(("gchart" + Integer.toString(i)),i));								
							    	}
						    	catch (Exception e)
						    		{
						    		gantChartForm.replace(buildGanttChartLink(("gchart" + Integer.toString(i)  ),i));
						    		}
							 	 } 
					    	break;				    	
						 case "updateUser" :
							 MarkupCache.get().clear();
					    	    getMarkupResourceStream(markupContainer, containerClassl) ;
					    	    target.add(gantChartForm);
					    	    target.add(gantChart);
					    	    for (int i=0 ; i< nList.size();i++ )
								 	{
							    	ggIndex = i;
							    	try
								    	{
										gantChartForm.add( buildGanttChartLink(("gchart" + Integer.toString(i)),i));
									
								    	}
							    	catch (Exception e)
							    		{
							    		gantChartForm.replace(buildGanttChartLink(("gchart" + Integer.toString(i)  ),i));
							    		}
								 	 } 
							 break;
					}
				}
			};
		}
		
	private IndicatingAjaxLink <Void> buildRightArrow (String id, int index)
		{
		
			return new IndicatingAjaxLink <Void> ("rightArrow")
			{
			
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				dateStartingPointIndex = dateStartingPointIndex+ 7;
				MarkupCache.get().clear();
				getMarkupResourceStream(markupContainer, containerClassl) ;
				target.add(gantChart);
				} 
			};
		}
	
	private IndicatingAjaxLink <Void> buildLeftArrow ( String id, int index)
	{
	
		return new IndicatingAjaxLink <Void> ("leftArrow")
			{
			
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				dateStartingPointIndex = dateStartingPointIndex- 7;
				MarkupCache.get().clear();
				getMarkupResourceStream(markupContainer, containerClassl) ;
				target.add(gantChart);
				} 
			};
	}
	
	protected AjaxCheckBox buildScreeningChkBox( String id, String property)
	    {
		AjaxCheckBox cCheckBox = new AjaxCheckBox(id, new PropertyModel(this, property))
		    {
	
		    @Override
		    public void onUpdate(AjaxRequestTarget target)
			    {
		    	if (property.equals("isOnHold") && isOnHold)
		    		{
		    		isCurrent = false;
		    		isInProgress = false;
		    		}
		    	else if (property.equals("isInProgress") && isInProgress)
		    		{
		    		isCurrent = false;
		    		isOnHold = false;
		    		}
		    	else if (property.equals("isCurrent") && isCurrent)
		    		{
		    		isOnHold = false;
		    		isInProgress = false;
		    		}
		    	if (property.equals("isAllExp"))
		    		{
		    		if (isAllExp)
			    		{
		    			///////////////////////////////
		    			
		    			
		    			allExpAssay = true;
						expID = "";
						assayDescID = null;
						List <String> lilExp = new ArrayList <String> ();
						List <String> lilAssay = new ArrayList <String> ();
						experimentDD.setChoices(lilExp);
						assayDescDD.setChoices(lilAssay);
					    MarkupCache.get().clear();
						getMarkupResourceStream(markupContainer, containerClassl) ;
		    	  
						nList = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), allExpAssay, assignedTo); 
						AjaxLink gCLink;
						ggIndex = 0;
						// nList = new ArrayList <ProcessTrackingDetails> () ;
						    for (int i=0 ; i< nList.size();i++ )
							 	{
						    	ggIndex = i;
						    	try
							    	{
									gantChartForm.add(gCLink = buildGanttChartLink(("gchart" + Integer.toString(i)),i));
							    	}
						    	catch (Exception e)
						    		{
						    		gantChartForm.replace(buildGanttChartLink(("gchart" + Integer.toString(i)  ),i));
						    		}
						    	}
						target.add(gantChart);
						target.add(gantChartForm);
					    }
		    			
			    		////////////////////////////////
			    		
		    		else
		    			{
		    			experimentDD.setChoices (processTrackingService.loadAllAssignedExperiments())	;	
		    			allExpAssay = false;
		    			}
		    		}
		    	
		        MarkupCache.get().clear();
		        getMarkupResourceStream(markupContainer, containerClassl) ;
		        target.add(gantChart); 
			    }
		    };
		
	    return cCheckBox;
	    }
	
	 public String getExpID ()
		{
		return this.expID;
		}
	 public void setExpID (String expID)
		{
		this.expID = expID;
		}
	 
	 public String getAssayDescID ()
		{
		return this.assayDescID;
		}
	 public void setAssayDescID (String assayDescID)
		{
		this.assayDescID = assayDescID;
		}
	 
	 
	 public DropDownChoice buildExperimentDropDown( String id)
		{
		experimentDD =  new DropDownChoice<String>(id, new PropertyModel(this, "expID" ), new ArrayList <String> ())
				{				
				};				
	 	experimentDD.setChoices (processTrackingService.loadAllAssignedExperiments())	;		
		return experimentDD;
		
		}
	 
	 ///////////////////////////////
	 
	 private AjaxLink buildGanttChartLink(String id, int i)
		{
		AjaxLink link;		
		// Issue 237
		// issue 39
	    link =  new AjaxLink <Void>(id)
			{
			@Override
			public void onClick(AjaxRequestTarget target) 
				{
				try
					{
					 setModalDimensions(id, modal2);					 
					 ProcessTrackingDetails ptd = processTrackingService.loadById(nList.get(i).getJobid());					 
					 modal2.setPageCreator(new ModalWindow.PageCreator()
						{
						public Page createPage() {   return setPage(id, modal2, ptd);   }
						});	
					    	modal2.show(target); 
					
					
					}
				catch (Exception e) { e.printStackTrace(); }
				}
			};
	    
		return link;
		}
	
	 
	private void setModalDimensions(String linkID, ModalWindow modal1)
		{ 
		modal2.setInitialWidth(1500);
		modal2.setInitialHeight(790);
		}
	 
	 private Page setPage(String linkID,  ModalWindow modal1, ProcessTrackingDetails ptd)
		{
		editProcessTrackingDetail = new EditProcessTrackingDetail (getPage(), new Model <ProcessTrackingDetails> (ptd), modal1, false);
        return editProcessTrackingDetail;
		}  
	 
	 
	 public DropDownChoice buildAssayDescDropDown( String id)
		{
			
		assayDescDD =  new DropDownChoice<String>(id, new PropertyModel(this, "assayDescID" ), new ArrayList <String> ())
				{				
				};		
		assayDescDD.setChoices (assayService.allAssayNamesForExpId(expID, false))	;	
		assayDescDD.setNullValid(true);		
				
		return assayDescDD;
		
		}
	 
	 public DropDownChoice buildUserAssignedDropDown( String id)
		{			
		userNamesDD= new DropDownChoice(id, new PropertyModel<String>(this, "assignedTo"),   new ArrayList <String> ())
		    {
			
			}
			;			
		userNamesDD.setOutputMarkupId(true);
		add(userNamesDD);
		userNamesDD.setChoices(userService.allAdminNames(false));	
				
		return userNamesDD;		
		}
	 
		public String getAssignedTo() { return assignedTo; }
		public void setAssignedTo (String e) { assignedTo = e; }
}
	
