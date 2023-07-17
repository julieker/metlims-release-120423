/*********************************
 * 
 * Created by Julie Keros Dec 1 2020
 */
package edu.umich.brcf.metabolomics.panels.admin.progresstracking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.MarkupCache;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import com.mysql.jdbc.StringUtils;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;


public class GantChart extends WebPage implements IMarkupResourceStreamProvider 
	{
	@SpringBean
	CompoundService compoundService;
	File julieFile;
	
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
	boolean continuingInProcess = false;
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
	List <Calendar> todayCalList = new ArrayList <Calendar> ();
	
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
	
	
	boolean allExpAssay = true; 
	boolean isAllExp = true;
	int gIndex = 0;
	int ggIndex = 0;
	 ModalWindow modal2 =new ModalWindow("modal2");;
	 long numDaysComplt = 0L;
	 long numDaysOnHold = 0L;
	 long numDaysUntilComplete = 0L;
	 DropDownChoice<String> userNamesDD;
	 String assignedTo ;
	 Map <String, String> sampleTypeMap =  new HashMap<String, String>();
    boolean initialLoad = false;
    int indexCompleted  = 0;
    String thecompleted = "";
	String theOnHold = "";
	Calendar theStartingCal;
	Calendar theEndingCal;
	Calendar forCurrentCal = Calendar.getInstance();
	/* @Override
	 public void renderHead( IHeaderResponse response)
	     {
	     super.renderHead(response);
        	// response.render(OnDomReadyHeaderItem.forScript( " alert('hi'); document.getElementById('scroll-to-bottom').scrollTop= document.getElementById('scroll-to-bottom').scrollHeight; alert('okay here is the ehight' +  document.getElementById('scroll-to-bottom').scrollHeight);"));
        	 
	     response.render(OnDomReadyHeaderItem.forScript( " alert('hi');     window.scrollBy(0, -10);    alert('okay here is the ehight' +  document.getElementById('scroll-to-bottom').scrollHeight);"));
	           System.out.println("I just did the render....");
	 	 } */
	 
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
	//Map <String, String> sampleTypeMap =  new HashMap<String, String>();
    str = "";
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
	
	
	////  if (isCurrent)
	////	startedString = sdf.format(forCurrent.getTime());	
	Calendar calP = Calendar.getInstance();
	todayStrList = new ArrayList <String> ();
	todayCalList = new ArrayList <Calendar> ();
	
    for (int i = dateStartingPointIndex; i < dateStartingPointIndex+14; i++)
	    	{	
    	   
    	    if (nList.size() == 0) 
    	    	   break;
    
    	    calP = Calendar.getInstance();
    	    calP.setTime(new Date(startedString));
	    	if (!(gantChartForm == null) && !StringUtils.isNullOrEmpty(gantChartForm.dateStartGantt))
	    		{
	    		calP.setTime(new Date(gantChartForm.dateStartGantt));
	    		}
	    	
	    	
	        if (i==0)
	    		theStartingCal= Calendar.getInstance();
	 
	    	//calP.setTime(new Date(dateStartGantt));
	    		
	    	calP.add(Calendar.DAY_OF_MONTH, i);
	    	theEndingCal = calP;
	    	sdf = new SimpleDateFormat("MM/dd/yyyy");
	    	String calendStr =  (calP == null) ? "" : sdf.format(calP.getTime());
	    
	    	if (i== dateStartingPointIndex+13)
	    		{
	    		theEndingCal.setTime(new Date (calendStr));
	    		}
	    	todayStrList.add(calendStr.substring(0,5));
	    	todayCalList.add(calP);
	    	
	    	if (i==0)
    			{
	    		theStartingCal.setTime(new Date (calendStr));
    			}
	    	}
    
    theStartingCal = Calendar.getInstance();
    theStartingCal = todayCalList.get(0);
    theEndingCal = Calendar.getInstance();
    theEndingCal = todayCalList.get(13);

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
					  "      max-width: 1390px;" +
					  "      min-width: 850px;" +
					  "      margin: 0 auto;" +
					  "      padding: 10px;      " +
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
	int indexxofDate = 0 ;
	int indexxofEndingDate = 0;
	int indexxofCurrentDate = 0;
	// issue 283 
	int indexxofHoldDate = 0;
	
	long daysBetweeni = 0L;
	long daysBetweenj = 0L;
	int theExpAssayIndex = 0;
	String prevAssay = "";
	String prevExp = "" ; 
	if (!initialLoad)
		processTrackingService.doAutomaticPropagation();
	for (int i = 0; i<= nList.size() -2 ; i++)
		{		
		daysBetweeni = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(),Calendar.getInstance().toInstant() );
		daysBetweenj = ChronoUnit.DAYS.between(nList.get(i+1).getDateStarted().toInstant(),Calendar.getInstance().toInstant() );
	
		if (!prevExp.equals(nList.get(i).getExperiment().getExpID()) && !prevAssay.equals(nList.get(i).getAssay().getAssayId()))
			theExpAssayIndex= 0;
		if (StringUtils.isNullOrEmpty(prevExp) && StringUtils.isNullOrEmpty(prevAssay))
			theExpAssayIndex++;
		prevAssay = nList.get(i).getAssay().getAssayId();
		prevExp = nList.get(i).getExperiment().getExpID();
		
		}
    initialLoad = true;
    int indexxofProgressDate = 0;   
    Calendar cCalendar = Calendar.getInstance();
    
    String theCurrentDate = 
    		sdf.format(cCalendar.getTime());
    
    
	//// handle all rows...
	for (int i = 0; i<= nList.size() -1 ; i++)
		{
		theStartDate = nList.get(i).convertToDateString(nList.get(i).getDateStarted()).substring(0,5);
		Calendar dCalendar = Calendar.getInstance();
		int daysExpected = Integer.parseInt(nList.get(i).getDaysExpected());
        
		dCalendar.setTime(new Date(nList.get(i).convertToDateString(nList.get(i).getDateStarted())));
	
		
		dCalendar.add(Calendar.DAY_OF_MONTH, daysExpected);
		theEndingDate = sdf.format(dCalendar.getTime());
		
		   
		if  (nList.get(i).getDateCompleted() != null)
			{
			if (i==260)   
				 System.out.println("alright in if  step 1 260 now....here is indexCompleted:" + indexCompleted + " " + 
			      "here is experiment" + nList.get(i).getExperiment().getExpID() + " " + nList.get(i).getAssay().getAssayId() + " " + 
			      nList.get(i).getProcessTracking().getTaskDesc()    );
			thecompleted = nList.get(i).convertToDateString(nList.get(i).getDateCompleted()).substring(0,5);
			if (i==260)   
				 System.out.println("alright in if  step 1 260 now....here is thecompleted:" + thecompleted);
			indexCompleted = todayStrList.indexOf(thecompleted);
			}
		else 
			indexCompleted = -1;
		
		if (i==260)   
			 System.out.println("FIRST step 1 260 now....here is indexCompleted:" + indexCompleted);
		
		
		indexxofDate = todayStrList.indexOf(theStartDate);
		indexxofEndingDate = todayStrList.indexOf(theEndingDate.substring(0,5));
		
		// issue 283 for on hold look at here 
		if (nList.get(i).getDateOnHold() != null)
			{
			theOnHold = nList.get(i).convertToDateString(nList.get(i).getDateOnHold()).substring(0,5);
			indexxofHoldDate= todayStrList.indexOf(theOnHold);
			}
		else 
			indexxofHoldDate = -1;
		
		indexxofCurrentDate = todayStrList.indexOf(theCurrentDate.substring(0,5));
		
		long daysBetween = 0L;
		if (nList.get(i).getStatus().equals("In progress"))
		    {
			if (nList.get(i).getDateStarted().compareTo(Calendar.getInstance()) <= 0 )
				daysBetween = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(),Calendar.getInstance().toInstant() );
			else 
				daysBetween =  StringUtils.isNullOrEmpty(nList.get(i).getDaysExpected()) || (Integer.parseInt(nList.get(i).getDaysExpected()) <= 0) ? 1 : Integer.parseInt(nList.get(i).getDaysExpected()) - 1;
		  	}
		else 
	  	    {
		    if (!(nList.get(i).getDateOnHold() == null)) 
			    daysBetween = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(), nList.get(i).getDateOnHold().toInstant());
		    else if (!(nList.get(i).getDateCompleted() == null)) 
			    daysBetween = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(), nList.get(i).getDateCompleted().toInstant());		  	
	  	    }	    
		 String showit = "";
		
		 indexxofCurrentDate = todayStrList.indexOf(theCurrentDate.substring(0,5));
		 indexxofDate = todayStrList.indexOf(theStartDate);
		 boolean didonHoldForInprocess = false;
		 if (i==260)
			 System.out.println("here is indexxofDate and indexxofcurrentdate:" + indexxofDate + " " + indexxofCurrentDate);
		 if (indexxofDate <0  && indexxofCurrentDate <= 0) 
		 	 {
			 if (i==260)
		    	 System.out.println("made it in indexdate current date...");
			 if (ChronoUnit.DAYS.between(theStartingCal.toInstant(),  nList.get(i).getDateStarted().toInstant()) < 0 
			     &&
			     ChronoUnit.DAYS.between(theEndingCal.toInstant(),  forCurrentCal.toInstant()) >= 0
			     )
				 	{   
				     if (i==260)
				    	 System.out.println("in i 260");
				     String theProgressDate = "";
				     if (nList.get(i).getDateInProgress() != null)
				     	{
			             theProgressDate = nList.get(i).convertToDateString(nList.get(i).getDateInProgress()).substring(0,5);
				         indexxofProgressDate = todayStrList.indexOf(theProgressDate);
				     	}
				     else indexxofProgressDate = -1;
				     if (nList.get(i).getDateOnHold() != null)
						{
						theOnHold = nList.get(i).convertToDateString(nList.get(i).getDateOnHold()).substring(0,5);
						indexxofHoldDate= todayStrList.indexOf(theOnHold);
						}
				     else
				    	 indexxofHoldDate = -1;
				     
				     // july 14 left off here...
				     if (nList.get(i).getDateOnHold() != null
				    		 && nList.get(i).getStatus().equals("In progress")
				    		 && ChronoUnit.DAYS.between(theStartingCal.toInstant(),  nList.get(i).getDateOnHold().toInstant()) <=0
				    		 && nList.get(i).getDateInProgress() != null
				    		 && indexxofProgressDate > 0
				    		 && indexxofHoldDate <= 0 
				    		 )
				     		{ 
				    	    int startingpoint = 
				    	    		1;
				    	    int endingpoint =
				    	    		indexxofProgressDate + 1;
				    	    if (i==260)
						    	 System.out.println("ok in i 260");
				    	    str = str +
									  "  ul .chart-li-onHold-" + (i) +  "{" +
											  "    grid-column:" +  (1 ) + "/" + ( endingpoint   ) +      ";  grid-row:2; " ;
						 str = str + "   background-color:#FF0000;" ;
						 str = str + 
								 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
								 "  }" ;
				    	    
				    	    
						 didonHoldForInprocess = true; 
				     	}
				     else
				     	{ 
				    	int endPointProcessBeforeHold = 15;
					    if (i==260)
						     System.out.println("not ok in i 260");
					    if (ChronoUnit.DAYS.between(theStartingCal.toInstant(),  nList.get(i).getDateStarted().toInstant()) <=0
					    	&& indexxofHoldDate >= 0 
					    	&& nList.get(i).getDateOnHold() != null
					    	&& nList.get(i).getStatus().equals("In progress")
					    	&& ChronoUnit.DAYS.between(theStartingCal.toInstant(),  nList.get(i).getDateOnHold().toInstant()) >=0
					    	)
					     	{
					    	endPointProcessBeforeHold =  indexxofHoldDate;
					     	}
						str = str + 
								  "  ul .chart-li-" + (i) +  "{" +
								  "  grid-row-gap:0px;  grid-column:" +  "1" + "/" + (endPointProcessBeforeHold) +   "; grid-row:2; " ; 
				     	}
				 	}
			 else       
				 {  
			     if (i==260)
			    	 System.out.println("in ii 260");
				  str = str + 
				  "  ul .chart-li-" + (i) +  "{" +
				  "    grid-column:" +  "0" + "/" + "0" +   "; grid-row:2; " ;
				  if (ChronoUnit.DAYS.between(theEndingCal.toInstant(),  nList.get(i).getDateStarted().toInstant()) > 0 )
					    str = str + "display:none;";
				  showit =  "  ul .chart-li-" + (i) +  "{" +
						  "    grid-column:" +  "3" + "/" + "4" +    "; grid-row:2; " ;
				 }
		 	 }
    	 else if (indexxofDate <0 && indexxofCurrentDate >= 0 )       
		 	{    
    		  if (i==260)
			    	 System.out.println("in iii 260"); 
    		  int endpoint = 0;
    		  if (indexCompleted > 0 )
    			  endpoint = indexCompleted + 1; 
    		  else 
    			  endpoint = indexxofCurrentDate+ 2;
    		  if (i==260)
			    	 System.out.println("here is indexCompleted:" + indexCompleted + " " + endpoint); 
			 str = str + 
					 "  ul .chart-li-" + (i) +  "{" +
				"  grid-row-gap:0px;  grid-column:"  + 1 +"/" + (endpoint) +      ";  grid-row:2; "   ;
				showit =  "  ul .chart-li-" + (i) +  "{" + "  grid-row-gap:0px;  grid-column:" +  3 + "/" + 7 +      ";  grid-row:2; "   ;  
			 	}
		 
		 ///// june29
    	 else if (indexxofDate >=0 && indexxofCurrentDate <0  && indexCompleted >= 0 && nList.get(i).getDateCompleted() != null)       
		 	{
    		 if (i==260)
		    	 System.out.println("in iv 260"); 
			 str = str + 
					 "  ul .chart-li-" + (i) +  "{" +				 
				"  grid-row-gap:0px;  grid-column:"  + (indexxofDate+ 1) +"/" + (indexCompleted + 1) +      ";  grid-row:2; "   ;
				showit =  "  ul .chart-li-" + (i) +  "{" + "  grid-row-gap:0px;  grid-column:" +  3 + "/" + 7 +      ";  grid-row:2; "   ;      
			 	}
		 ///// june29           
    	 else if (indexxofDate >=0 && indexxofCurrentDate <0  && indexCompleted < 0 && nList.get(i).getDateCompleted() != null )       
		 	{
    		 if (i==260)
		    	 System.out.println("in v 260"); 
			 str = str + 
					 "  ul .chart-li-" + (i) +  "{" +			 
				"  grid-row-gap:0px;  grid-column:"  + indexxofDate +"/" + 15 +      ";  grid-row:2; "   ;
				showit =  "  ul .chart-li-" + (i) +  "{" + "  grid-row-gap:0px;  grid-column:" +  3 + "/" + 7 +      ";  grid-row:2; "   ;
			 	}
		 else    
			 {
			 indexxofDate = indexxofDate < 0 ? 0 : indexxofDate;
			 indexxofCurrentDate = indexxofCurrentDate < 0 ? 0 : indexxofCurrentDate;
			if (i == 260)
				{
				System.out.println("here in vi 260");
				}
			 str = str + 
			 "  ul .chart-li-" + (i) +  "{" +
			"  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate ) + "/" + (  (indexxofDate +  daysBetween + 2 ) > 15 ? 15 :  (indexxofDate +  daysBetween + 2 ) )  +      ";  grid-row:2; "   ;
			 showit =  "  ul .chart-li-" + (i) +  "{" +
					"  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate ) + "/" + (  (indexxofDate +  daysBetween + 2 ) > 15 ? 15 :  (indexxofDate +  daysBetween + 2 ) )  +      ";  grid-row:2; "   ;  
			 }
		 
		if  (nList.get(i).getDateCompleted() != null)
			{
			thecompleted = nList.get(i).convertToDateString(nList.get(i).getDateCompleted()).substring(0,5);
			indexCompleted = todayStrList.indexOf(thecompleted);
			}
		else 
			indexCompleted = -1;
		if  (nList.get(i).getDateOnHold() != null)
			{
			theOnHold = nList.get(i).convertToDateString(nList.get(i).getDateOnHold()).substring(0,5);
			indexxofHoldDate= todayStrList.indexOf(theOnHold);
			}
		else 
			indexxofHoldDate = -1;
		 
		 
		 if (!didonHoldForInprocess )
			 {
			 str = str + "   background-color:#4C9A2A;" ;		   
			 str = str + 
					 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
					 "  }" ;  
			}
		 showit = showit +  "   background-color:#4C9A2A;" ;
		 showit = showit + 
				 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
				 "  }" ; 
		
		 // issue 283 taking off hold completed.....
		 if (nList.get(i).getDateCompleted() != null
				 && nList.get(i).getDateOnHold() != null &&
				 nList.get(i).getDateInProgress() != null) 
		     {
			 if (indexxofHoldDate  > 0 && indexCompleted > 0 && indexxofDate > 0   )
			 	{
				 if (i==260)
			    	 System.out.println("in vii 260"); 
				 str = str + 
						 "  ul .chart-li-2Prog" + (i)  +  "{" +
						// "  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate ) + "/" + (indexxofDate +  (daysBetween + 2 > 15 ? 15: daysBetween + 2) ) +      ";  grid-row:2; "   ;
						"  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate) + "/" + (indexxofHoldDate + 1  )  +      ";  grid-row:2; "   ;
					 		
					 		str = str + "   background-color:#4C9A2A;" ;		 
					 			str = str + 
					 	"    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
					 	"  }" ;
			 	}
			              
			 if (indexxofHoldDate  > 0 && indexCompleted > 0  )
			 	{
				 String theProgressDate = 
							nList.get(i).convertToDateString(nList.get(i).getDateInProgress()).substring(0,5);
					indexxofProgressDate = todayStrList.indexOf(theProgressDate);
					str = str + 
				 "  ul .chartInProgress-li-" + (i)  +  "{" +
				// "  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate ) + "/" + (indexxofDate +  (daysBetween + 2 > 15 ? 15: daysBetween + 2) ) +      ";  grid-row:2; "   ;
				"  grid-row-gap:0px;  grid-column:" +  (1+ indexxofProgressDate) + "/" + (indexCompleted + 1  )  +      ";  grid-row:2; "   ;

			 		str = str + "   background-color:#4C9A2A;" ;		 
			 			str = str + 
			 	"    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
			 	"  }" ;
			 	}
			 
		 	 }
		 
		 // issue 283
		 ///////////////////////////////////////////////////////////////////
		 
		 if (nList.get(i).getDateCompleted() == null
				 && nList.get(i).getDateOnHold() != null &&
				 nList.get(i).getDateInProgress() != null) 
		     {
			 String theProgressDate = 
						nList.get(i).convertToDateString(nList.get(i).getDateInProgress()).substring(0,5);
				indexxofProgressDate = todayStrList.indexOf(theProgressDate);
			 if (indexxofProgressDate > 0   )
			    {
				 if (i==260)
			    	 System.out.println("in viii 260"); 
				 int theendpoint = indexxofCurrentDate >= 0 ? indexxofCurrentDate : 15;
				 str = str + 
						 "  ul .chart-li-2Prog" + (i)  +  "{" +
						// "  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate ) + "/" + (indexxofDate +  (daysBetween + 2 > 15 ? 15: daysBetween + 2) ) +      ";  grid-row:2; "   ;
						"  grid-row-gap:0px;  grid-column:" +  (1+ indexxofProgressDate) + "/" + (theendpoint  )  +      ";  grid-row:2; "   ;
					 		
					 		str = str + "   background-color:#4C9A2A;" ;		 
					 			str = str + 
					 	"    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
					 	"  }" ;
					 			
// issue 283 test onhold, inprogress, onhold, in progress again 
				/*	str = str + 
					"  ul .chart-li-" + (i)  +  "{" +
					// "  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate ) + "/" + (indexxofDate +  (daysBetween + 2 > 15 ? 15: daysBetween + 2) ) +      ";  grid-row:2; "   ;
					"  grid-row-gap:0px;  grid-column:" +  (1+ indexxofProgressDate) + "/" + (indexxofCurrentDate + 2  )  +      ";  grid-row:2; "   ;	 		
					str = str + "   background-color:#4C9A2A;" ;		 
					str = str + 
					"    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
					"  }" ;	 */			
					 				 			
			 	}

		     }
 
		 ///////////////////////////////////////////////////////////////////

		 // issue 283
		 if (nList.get(i).getDateCompleted() != null)
		 	{
			 /// issue 283 displaying before .... 
			 ///
			 int startingPoint = 0 ;
			 int endingPoint = 0;
			 if (nList.get(i).getDateOnHold()!= null)
				 {
				 if (i==260)
					 System.out.println("step 1 260printing out completed.....");
				
				 if (indexxofDate >= 0 )
					 startingPoint = indexxofDate+ 1;
				 else 
					 startingPoint = 1;
				 if (indexxofHoldDate >=0 )
					 endingPoint = indexxofHoldDate + 1;
				 else 
					 endingPoint = 15;    
				 }
			
			 //////////   leaving off jul12   //////////////////
			 
			 if (nList.get(i).getDateOnHold()== null)
			 {
				 if (i==260)   
					 System.out.println("step 1 260 now....here is indexCompleted:" + indexCompleted);
				
				 if (indexxofDate >= 0 )     
					 startingPoint = indexxofDate+ 1;
				 else 
					 startingPoint = 1;
				 if (indexCompleted >=0 )
					 endingPoint = indexCompleted + 1;
				 else 
					 endingPoint = 15;
				 if (i==260)
					 System.out.println("here is endpoint:" + endingPoint); 
			 }
			 
			
			 
			 //////////////////////////
			 boolean displayProgress = false;
			 if (indexxofDate >= 0 )
				 displayProgress = true;
			 else if (indexxofHoldDate >= 0 )
				 displayProgress = true;
			 else if   (nList.get(i).getDateOnHold() != null &&    ChronoUnit.DAYS.between(theStartingCal.toInstant(), nList.get(i).getDateStarted().toInstant()) <= 0 
					 &&
					 ChronoUnit.DAYS.between(theEndingCal.toInstant(), nList.get(i).getDateOnHold().toInstant()) >= 0 )
			 	 {
				 displayProgress = true;
			 	 }
			 /////// leaving off jl12 
			 else if (nList.get(i).getDateOnHold() == null && indexCompleted > 0 )
			 {
				 displayProgress = true; 
			 }
			 if ( displayProgress					 
			    ) 
			 	{    
				 if (i==260)
					 System.out.println("ix in 260 printing out completed..........");
					 str = str + 	
					 "  ul .chart-li-" + (i) +  "{" +
								//	  "    grid-column:" +  ( numDaysComplt > 14 ? 14 :  (1+ indexxofDate + numDaysComplt + 1)) + "/" + ( numDaysComplt > 14 ? 14 :  (indexxofDate +  numDaysComplt + 1)) +      ";  grid-row:2; "   ;
					 "    grid-column:" +   (startingPoint) + "/" + ( endingPoint  ) +      ";  grid-row:2; "   ;
					 str = str + "   background-color:#4C9A2A;" ;		   
					 str = str + 
							 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
							 "  }" ; 
				 
			 	}
			 
			 
			 numDaysComplt = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(), nList.get(i).getDateCompleted().toInstant()); 
			 if (nList.get(i).getDateCompleted() != null && nList.get(i).getDateOnHold()  != null  
					 && indexCompleted >= 0)
			 	 {
					 str = str + 
							  "  ul .chart-li-cmplt-" + (i) +  "{" +
						//	  "    grid-column:" +  ( numDaysComplt > 14 ? 14 :  (1+ indexxofDate + numDaysComplt + 1)) + "/" + ( numDaysComplt > 14 ? 14 :  (indexxofDate +  numDaysComplt + 1)) +      ";  grid-row:2; "   ;
						"    grid-column:" +   (indexCompleted + 1) + "/" + ( indexCompleted + 1     ) +      ";  grid-row:2; "   ;
					
			 	 }
			 else if (nList.get(i).getDateCompleted() != null && nList.get(i).getDateOnHold()  == null  
					 && indexCompleted >= 0)
			 	{
				 str = str + 
						  "  ul .chart-li-cmplt-" + (i) +  "{" +
					//	  "    grid-column:" +  ( numDaysComplt > 14 ? 14 :  (1+ indexxofDate + numDaysComplt + 1)) + "/" + ( numDaysComplt > 14 ? 14 :  (indexxofDate +  numDaysComplt + 1)) +      ";  grid-row:2; "   ;
					"    grid-column:" +   (indexCompleted + 1) + "/" + ( indexCompleted + 1     ) +      ";  grid-row:2; "   ;
			 	}
			 
			 else     
			 ////////////////////////////////////////////
				 str = str + 
						  "  ul .chart-li-cmplt-" + (i) +  "{" +
					//	  "    grid-column:" +  ( numDaysComplt > 14 ? 14 :  (1+ indexxofDate + numDaysComplt + 1)) + "/" + ( numDaysComplt > 14 ? 14 :  (indexxofDate +  numDaysComplt + 1)) +      ";  grid-row:2; "   ;
					"    grid-column:" +   (1+ indexxofDate + numDaysComplt + 1) + "/" + (     ( indexxofDate +  numDaysComplt + 1) > 15 ? 15 : ( indexxofDate +  numDaysComplt + 1)     ) +      ";  grid-row:2; "   ;
		     str = str + "   background-color:#0000FF;" ;
			 str = str + 
			(numDaysComplt > 15 ? "    border-right: 1px ;  " : "    border-right: 1px solid ") + " rgba(0, 0, 0, 0.3); height: 5px;" +
					 "  }" ;	
			 showit =  "  ul .chart-li-cmplt-" + (i) +  "{" +
					  "    grid-column:" +  (1+ indexxofDate + numDaysComplt + 1) + "/" + (indexxofDate +  numDaysComplt + 1) +      ";  grid-row:2; "   ;
		 	}
		 
		 //////////////////////
		 /// issue 283
		 long extOnHold = 0L;	 	
		 
		 // just changed yesterday
		 if (nList.get(i).getDateOnHold() != null)
		 	{
			 
			 theOnHold = nList.get(i).convertToDateString(nList.get(i).getDateOnHold()).substring(0,5);
			 indexxofHoldDate= todayStrList.indexOf(theOnHold);
			 indexxofCurrentDate = todayStrList.indexOf(theCurrentDate.substring(0,5));
			 theStartDate = nList.get(i).convertToDateString(nList.get(i).getDateStarted()).substring(0,5);
			 indexxofDate = todayStrList.indexOf(theStartDate);
			 
			// issue 283 issue with grey bar for on hold
			if (ChronoUnit.DAYS.between(theStartingCal.toInstant(),nList.get(i).getDateOnHold().toInstant() ) < 0
				&& nList.get(i).getDateOnHold() != null 
				&& nList.get(i).getDateInProgress() != null 
				&& nList.get(i).getDateCompleted() == null
				&& (ChronoUnit.DAYS.between(theEndingCal.toInstant(),nList.get(i).getDateInProgress().toInstant() ) >0					
					) )
				{
				if (i==260)
			    	 System.out.println("in XXX 260"); 
				str = str +
						  "  ul .chart-li-onHold-" + (i) +  "{" +
								  "    grid-column:" +  ( 1)+ "/" + 15 +      ";  grid-row:2; " ;
				}   
			      
			          
			if (nList.get(i).getDateCompleted() == null && indexxofHoldDate < 0 && indexxofDate > 0 )
				{
				    if (indexxofCurrentDate > 0 )
				    	{
				    	if (i==260)
					    	 System.out.println("in X 260"); 
				        str = str + 
								  "  ul .chart-li-" + (i) +  "{" +
								  "    grid-column:" +  (indexxofDate + 1 ) + "/" + (indexxofDate + 1 + indexxofCurrentDate + 1)  +   "; grid-row:2; " ;
					    str = str + "   background-color:#4C9A2A;" ;		   
						 str = str + 
								 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
								 "  }" ;  
				    	}
				    else 
				    	{
				    	if (i==260)
					    	 System.out.println("in XI 260"); 
				    	
				    	if (i==98)
					    	 System.out.println("in i 98"); 
				    	str = str +    
								  "  ul .chart-li-" + (i) +  "{" +
								  "    grid-column:" +  (indexxofDate + 1 ) + "/" + (15)  +   "; grid-row:2; " ;
					    str = str + "   background-color:#4C9A2A;" ;		   
						 str = str + 
								 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
								 "  }" ;  
				    	}
				        
				    str = str +      
						  "  ul .chart-li-onHold-" + (i) +  "{" +
								  "    grid-column:" +  (0)+ "/" + ( 0 ) +      ";  grid-row:2; display:none;" ;
				}
			   
			else if (nList.get(i).getDateCompleted() == null && indexxofHoldDate >= 0 && indexxofDate >= 0   && indexxofCurrentDate <0 )
				{
				if (i==260)
			    	 System.out.println("in XXXi 260"); 
                if (i == 98)
                    {
                	System.out.println("here in 98 ii");
                    }
				str = str +
						  "  ul .chart-li-onHold-" + (i) +  "{" +
								  "    grid-column:" +  (indexxofHoldDate + 1)+ "/" + 15 +      ";  grid-row:2; " ;
				}
			else if (nList.get(i).getDateCompleted() == null && indexxofHoldDate >= 0   && indexxofCurrentDate >=0 )
				{
				int endforlOnHold = indexxofCurrentDate + 2;
				// issue 283 fix on hold not extending all the way
				if (i==260)
			    	 System.out.println("in XXXii 260"); 
				if (i == 98)
                   {
               	   System.out.println("here in 98 iii");
                   }
				if (nList.get(i).getStatus().equals ("In progress")
						&& (indexxofProgressDate > 0 )
						)       
					endforlOnHold = indexxofProgressDate + 1 ;
				str = str +
						  "  ul .chart-li-onHold-" + (i) +  "{" +
						  "    grid-column:" +  (indexxofHoldDate + 1)+ "/" + (endforlOnHold) +      ";  grid-row:2; " ;
				}
			    
			/***********/
			
			else if (nList.get(i).getDateCompleted() == null && indexxofHoldDate <0   && indexxofCurrentDate >=0 )
				{
				if (i == 98)
                	{
            	   System.out.println("here in 98 iv");
                	}
				if (i==260)
			    	 System.out.println("in XXX 260"); 
				str = str +
						  "  ul .chart-li-onHold-" + (i) +  "{" +
						  "    grid-column:" +  (1)+ "/" + (indexxofCurrentDate + 2) +      ";  grid-row:2; " ;
				}
					
			/******/      
			
			else if (nList.get(i).getDateCompleted() == null && indexxofHoldDate >=0   && indexxofCurrentDate < 0 )
				{
				
				if (i==260)
			    	 System.out.println("in XXXiv 260"); 
				if (i == 98)
                	{
            	   System.out.println("here in 98 iv");
                	}
				int endd = 15;
				if (ChronoUnit.DAYS.between(nList.get(i).getDateOnHold().toInstant(),Calendar.getInstance().toInstant() ) <= 0 )
					{
					endd = indexxofHoldDate + 1;  
					}
				str = str +
						  "  ul .chart-li-onHold-" + (i) +  "{" +   
						  "    grid-column:" +  (indexxofHoldDate + 1)+ "/" + (endd) +      ";  grid-row:2; " ;
				}
			
			 /// issue 283
			if (nList.get(i).getDateCompleted() == null)
				extOnHold = ChronoUnit.DAYS.between(nList.get(i).getDateOnHold().toInstant(),Calendar.getInstance().toInstant() ) ;
			if (extOnHold <0)
				extOnHold = 0;
			numDaysOnHold = ChronoUnit.DAYS.between(nList.get(i).getDateStarted().toInstant(), nList.get(i).getDateOnHold().toInstant()); 			
			if ( nList.get(i).getDateOnHold() != null &&  nList.get(i).getDateCompleted() != null && nList.get(i).getDateInProgress() == null)
				numDaysUntilComplete = ChronoUnit.DAYS.between(nList.get(i).getDateOnHold().toInstant(), nList.get(i).getDateCompleted().toInstant());
			// issue 283
			else if (nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() == null
					  && extOnHold > 0 ) 
				numDaysUntilComplete = ChronoUnit.DAYS.between(nList.get(i).getDateOnHold().toInstant(), Calendar.getInstance().toInstant());
				// issue 283
			else if (nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() != null
					   && nList.get(i).getDateInProgress() != null) 
				{
				numDaysUntilComplete = ChronoUnit.DAYS.between(nList.get(i).getDateOnHold().toInstant(), nList.get(i).getDateInProgress().toInstant()) ;
				String theProgressDate = 
						nList.get(i).convertToDateString(nList.get(i).getDateInProgress()).substring(0,5);
				indexxofProgressDate = todayStrList.indexOf(theProgressDate);
				Long numDaysbtwProgressAndComplete = ChronoUnit.DAYS.between(nList.get(i).getDateInProgress().toInstant(), nList.get(i).getDateCompleted().toInstant()) ;
				if (i==260)
			    	 System.out.println("in XII 260");
				str = str + 
						 "  ul .chart-li-" + (i) +  "{" +
						// "  grid-row-gap:0px;  grid-column:" +  (1+ indexxofDate ) + "/" + (indexxofDate +  (daysBetween + 2 > 15 ? 15: daysBetween + 2) ) +      ";  grid-row:2; "   ;
						"  grid-row-gap:0px;  grid-column:" +  (1+ indexxofProgressDate) + "/" + ( 1+ indexxofProgressDate + numDaysbtwProgressAndComplete )  +      ";  grid-row:2; "   ;
			    showit =  "  ul .chart-li-" + (i) +  "{" + 	"  grid-row-gap:0px;  grid-column:" +  (1+ indexxofProgressDate) + "/" + ( 1+ indexxofProgressDate + numDaysbtwProgressAndComplete )  +      ";  grid-row:2; "   ;

			 	str = str + "   background-color:#4C9A2A;" ;		 
			 	str = str + 
					 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
					 "  }" ;
				}		
				// issue 283
			else if (nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() != null) 
				{
				numDaysUntilComplete = ChronoUnit.DAYS.between(nList.get(i).getDateOnHold().toInstant(), nList.get(i).getDateCompleted().toInstant()) ;
				}   
				
			else 
				// issue 283
				numDaysUntilComplete = 0; 	
				
			String lStr = "";
			Long firstPart = (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       ));
			Long secondPart = numDaysUntilComplete; 
			if (nList.get(i).getStatus().equals("In progress")  &&  nList.get(i).getDateOnHold() != null )
				secondPart = secondPart -1 ;
			
			// issue 283 start here small change
			
			if (nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() != null 
					     && indexxofHoldDate >= 0 && indexCompleted >= 0 )
				{
				if (i == 98)
                	{
            	   System.out.println("here in 98 v");
                	} 
				
				if (i==260)
			    	 System.out.println("in XXXv 260"); 
				str = str +
						  "  ul .chart-li-onHold-" + (i) +  "{" +
								  "    grid-column:" +  (1+ indexxofHoldDate ) + "/" + ( 1 + indexCompleted    ) +      ";  grid-row:2; " ;
				}	
			
		
			else if (nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() != null && indexxofHoldDate < 0 && indexCompleted >= 0)
				{
				if (i == 98)
                	{
            	   System.out.println("here in 98 vi");
                	}
				if (i==260)
			    	 System.out.println("in XXXvi 260"); 
				str = str + 
					  "  ul .chart-li-onHold-" + (i) +  "{" +
			 		//  "    grid-column:" +  (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       )) + "/" + (    (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 + extOnHold + numDaysUntilComplete + 5 : (numDaysUntilComplete + numDaysOnHold + 1))  ) > 15 ? 15 :  (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1)) )    ) +      ";  grid-row:2; "   ;
			        "    grid-column:" +  (1 +  "/" + ( 1+  indexCompleted   )) +      ";  grid-row:2; "   ;
				}
			else if (nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() != null && indexxofHoldDate >= 0 && indexCompleted < 0)
				{
				if (i == 98)
	            	{
	        	   System.out.println("here in 98 vii");
	            	}
				if (i==260)
			    	 System.out.println("in XXXvii 260"); 
				str = str + 
					  "  ul .chart-li-onHold-" + (i) +  "{" +
			 		//  "    grid-column:" +  (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       )) + "/" + (    (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 + extOnHold + numDaysUntilComplete + 5 : (numDaysUntilComplete + numDaysOnHold + 1))  ) > 15 ? 15 :  (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1)) )    ) +      ";  grid-row:2; "   ;
			        "    grid-column:" +  (1+ indexxofHoldDate +  "/" + ( 15  )) +      ";  grid-row:2; "   ;
				}
			
			//////////////////////////
			else if (nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() != null && indexxofHoldDate < 0 && indexCompleted < 0)
				{
				if (i == 98)
	            	{
	        	   System.out.println("here in 98 viii");
	            	}
				if (ChronoUnit.DAYS.between(theStartingCal.toInstant(),  nList.get(i).getDateOnHold().toInstant())  < 0
						&& ChronoUnit.DAYS.between(theEndingCal.toInstant(),nList.get(i).getDateCompleted().toInstant())  > 0 )
				    { 
					if (i == 98)
		            	{
		        	   System.out.println("here in 98 ix");
		            	}
					if (i==260)
				    	 System.out.println("in XXXviii 260"); 
					str = str + 
							  "  ul .chart-li-onHold-" + (i) +  "{" +
					 		//  "    grid-column:" +  (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       )) + "/" + (    (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 + extOnHold + numDaysUntilComplete + 5 : (numDaysUntilComplete + numDaysOnHold + 1))  ) > 15 ? 15 :  (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1)) )    ) +      ";  grid-row:2; "   ;
					        "    grid-column:" +  (1 +   "/" + ( 15  )) +      ";  grid-row:2; "   ;
					
					}
				
	///// issue 283 Take care of the on hold grey 
				else if (nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() == null && indexxofHoldDate >= 0 && indexxofDate < 0 && indexxofCurrentDate < 0 )
					{
					if (i == 98)
		            	{
		        	   System.out.println("here in 98 x");
		            	}
					if (i==260)
				    	 System.out.println("in XXXix 260"); 
					str = str +   "  ul .chart-li-onHold-" + (i) +  "{" +
						 		//  "    grid-column:" +  (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       )) + "/" + (    (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 + extOnHold + numDaysUntilComplete + 5 : (numDaysUntilComplete + numDaysOnHold + 1))  ) > 15 ? 15 :  (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1)) )    ) +      ";  grid-row:2; "   ;
						        "    grid-column:" +  (indexxofHoldDate + 1 +   "/" + ( 15 )) +      ";  grid-row:2; "   ;	
					}
				else if (nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() == null && indexxofHoldDate < 0 && indexxofDate < 0
						&& 
						ChronoUnit.DAYS.between(theStartingCal.toInstant(),  nList.get(i).getDateOnHold().toInstant()) < 0 &&
						ChronoUnit.DAYS.between(theEndingCal.toInstant(), Calendar.getInstance().toInstant()) < 0 &&
						indexxofCurrentDate < 0 
						)
					{
					if (i == 98)
		            	{
		        	    System.out.println("here in 98 xi");
		            	}
					if (i==260)
				    	 System.out.println("in XXXx 260"); 
				    str = str +   "  ul .chart-li-onHold-" + (i) +  "{" +
					 		//  "    grid-column:" +  (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       )) + "/" + (    (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 + extOnHold + numDaysUntilComplete + 5 : (numDaysUntilComplete + numDaysOnHold + 1))  ) > 15 ? 15 :  (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1)) )    ) +      ";  grid-row:2; "   ;
					        "    grid-column:" +  ( 1 +   "/" + ( 15 )) +      ";  grid-row:2; "   ;	
					}
				else if (nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() == null && indexxofHoldDate >= 0 && indexxofDate > 0 && indexxofCurrentDate <0
						 
						
						)
					{
					if (i == 98)
		            	{
		        	    System.out.println("here in 98 xii");
		            	}
					if (i==260)
				    	 System.out.println("in XXXxii 260"); 
				str = str +   "  ul .chart-li-onHold-" + (i) +  "{" +
					 		//  "    grid-column:" +  (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       )) + "/" + (    (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 + extOnHold + numDaysUntilComplete + 5 : (numDaysUntilComplete + numDaysOnHold + 1))  ) > 15 ? 15 :  (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1)) )    ) +      ";  grid-row:2; "   ;
					        "    grid-column:" +  ( (indexxofHoldDate)  +   "/" + 15) +      ";  grid-row:2; "   ;	
					}
				  
				
				else if (  
						ChronoUnit.DAYS.between(theStartingCal.toInstant(),  nList.get(i).getDateOnHold().toInstant()) < 0  &&
						ChronoUnit.DAYS.between(theEndingCal.toInstant(),nList.get(i).getDateCompleted().toInstant()) < 0 
						)
					{
					if (i == 98)
	            	{
	        	    System.out.println("here in 98 xiii");
	            	}
					
					if (i==260)
				    	 System.out.println("in XXXxiii 260"); 
					str = str + 
							  "  ul .chart-li-onHold-" + (i) +  "{" +
					 		//  "    grid-column:" +  (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       )) + "/" + (    (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 + extOnHold + numDaysUntilComplete + 5 : (numDaysUntilComplete + numDaysOnHold + 1))  ) > 15 ? 15 :  (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1)) )    ) +      ";  grid-row:2; "   ;
					        "    grid-column:" +  (-1 +   "/" + ( -1  )) +      ";  grid-row:2; "   ;	
					}
				
			    else if (ChronoUnit.DAYS.between(theEndingCal.toInstant(), nList.get(i).getDateCompleted().toInstant())  > 0					 
			    		&&
			    		ChronoUnit.DAYS.between(theStartingCal.toInstant(), nList.get(i).getDateCompleted().toInstant())  > 0
			    		)
					{	
			    	if (i == 98)
	            	{
	        	    System.out.println("here in 98 xiv");
	            	}
					if (i==260)
				    	 System.out.println("in XXXxv 260"); 
					str = str + 
						  "  ul .chart-li-onHold-" + (i) +  "{" +
				 		//  "    grid-column:" +  (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       )) + "/" + (    (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 + extOnHold + numDaysUntilComplete + 5 : (numDaysUntilComplete + numDaysOnHold + 1))  ) > 15 ? 15 :  (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1)) )    ) +      ";  grid-row:2; "   ;
				        "    grid-column:" +  (-1 +  "/" + ( -1  )) +      ";  grid-row:2; "   ;
					}
			
				}
			else if (indexxofHoldDate< 0 && nList.get(i).getDateOnHold() != null && nList.get(i).getDateCompleted() == null 
					&&  ChronoUnit.DAYS.between(theStartingCal.toInstant(), Calendar.getInstance().toInstant()) > 0 && 
			ChronoUnit.DAYS.between(theStartingCal.toInstant(), nList.get(i).getDateOnHold().toInstant()) < 0  &&
			indexxofCurrentDate < 0 && nList.get(i).getDateInProgress() == null)
				{
				if (i == 260)    
				{
					System.out.println("yippieee for 260");
				}
				
				if (i==260)
			    	 System.out.println("in XXXxv 260");     
				str = str + 
				  "  ul .chart-li-onHold-" + (i) +  "{" +
		 		//  "    grid-column:" +  (1+ indexxofDate + numDaysOnHold + (    nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null  ? 1 : 0       )) + "/" + (    (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 + extOnHold + numDaysUntilComplete + 5 : (numDaysUntilComplete + numDaysOnHold + 1))  ) > 15 ? 15 :  (indexxofDate +   (nList.get(i).getDateOnHold() == null || nList.get(i).getDateCompleted() == null ?    numDaysOnHold + 1 : (numDaysUntilComplete + numDaysOnHold + 1)) )    ) +      ";  grid-row:2; "   ;
		        "    grid-column:" +  ((1) +  "/" + ( 15 )) +      ";  grid-row:2; "   ;
				}
			 str = str + "   background-color:#FF0000;" ;
			 str = str + 
					 "    border-right: 1px solid rgba(0, 0, 0, 0.3); height: 5px;" +
					 "  }" ;
		
			
			
		 	}	/// end of on hold	 
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
					 "    grid-column:" +  "15" + "/" + "15" +   "; ; "  ;

						 str = str + "   background-color:transparent;" ;
						 str = str + 
						 " border: none; width: 300px; height: 30px;" + 
						 "  }";				 
		}
	//////////////////////////////////////////////////	
	
long daysBetween;
str = str +  
"" +
"</style>" +
"</head>" +
"" +
"<form  wicket:id = \"gantChartForm\" > " + 
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
" <span style=\"border:none\">  &nbsp;&nbsp;  Assigned To:  </span>  " + 
"  <select wicket:id = \"assignedToDropDown\" style = \"margin-left : 15px; width : 100px;\" ></select>  " +
" <span style=\"outline: 1px solid ;  \"> <input style=\"   background-color: #D3D3D3;   \" wicket:id=\"AllUsersBtn\" type=\"submit\" value=\"All Users \"  size=\"10\"> </span> " + 

"<br> <br>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <button wicket:id = \"leftArrow\"  type= \"submit\"  >   <span>  &#x2190;  </span>     </button>   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <button wicket:id = \"rightArrow\"  type= \"submit\"  >   <span>  &#x2192;  </span>  </button> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  <span style= \"border: none; \"> View Current and Future? </span> <input style=\"   background-color: #D3D3D3;   \" wicket:id=\"currentChkBox\" type=\"checkbox\" value=\"View Current?\"  size=\"20\">   &nbsp;&nbsp;&nbsp;&nbsp; <span style= \"border: none; \"> View In Progress? </span> <input style=\"   background-color: #D3D3D3;   \" wicket:id=\"inProgressChkBox\" type=\"checkbox\" value=\"View Current?\"  size=\"20\">   <span style= \"border: none; \"> View On Hold? </span>  <input style=\"   background-color: #D3D3D3;   \" wicket:id=\"onHoldChkBox\" type=\"checkbox\" value=\"View Current?\"  size=\"20\">      " + 


"<br> <br>     <div id = \"scroll-to-bottom\" class=\"container\">" +

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



"      <div  class=\"chart\">" ;

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
"          <span style=\"border:none; height: 43px; width:401px;   \"> " + "comments" + "</span>" ; 
str = str + "      </div>" ;
str = str + "      <div class=\"chart-row chart-lines\">" +
"        <span></span><span></span><span></span>" +
"<span></span><span></span><span></span>" +
"                               <span></span><span></span><span></span>" +
"                               <span></span><span></span><span></span>  <span></span> " +  
//"  <span > </span> <span > </span> <span style=\" width:200px; \"> </span>  " + 
 "  <span > </span> <span > </span> <span style=\" width:401px;height:43px;   \" > </span>  " + 
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
		if  (ptd.getDateCompleted() != null)
			{
			thecompleted = ptd.convertToDateString(ptd.getDateCompleted()).substring(0,5);
			indexCompleted = todayStrList.indexOf(thecompleted);
			}
		else 
			indexCompleted = -1;
		if  (ptd.getDateOnHold() != null)
			{
			theOnHold = ptd.convertToDateString(ptd.getDateOnHold()).substring(0,5);
			indexxofHoldDate= todayStrList.indexOf(theOnHold);
			}
		else 
			indexxofHoldDate = -1;
		
		if (ptd.getDateInProgress()!= null)
			{
			 String theProgressDate = 
					nList.get(indexLink).convertToDateString(nList.get(indexLink).getDateInProgress()).substring(0,5);
			indexxofProgressDate = todayStrList.indexOf(theProgressDate);	
			}
		else 
			indexxofProgressDate = -1;
		
		
    	theStartDate = nList.get(indexLink).convertToDateString(nList.get(indexLink).getDateStarted()).substring(0,5);
    	indexxofDate = todayStrList.indexOf(theStartDate); 
    	if (nList.get(indexLink).getDateCompleted() != null)
    		{
    		theCDate = nList.get(indexLink).convertToDateString(nList.get(indexLink).getDateCompleted()).substring(0,5);
    		indexxofCDate = todayStrList.indexOf(theCDate);
    		}
    	Calendar dCalendar = Calendar.getInstance();
		int daysExpected = Integer.parseInt(nList.get(indexLink).getDaysExpected());
        
		//// whether or not to display
		
		boolean doDisplay = true;  
		/* if (
				ptd.getDateOnHold() == null ||
				(  // (ptd.getDateOnHold() != null && ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateOnHold().toInstant()) < 0 ) ||
				( ptd.getDateOnHold() != null && ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateOnHold().toInstant()) > 0 ) ||
				( ptd.getDateOnHold() != null && ChronoUnit.DAYS.between(theStartingCal.toInstant(),  Calendar.getInstance().toInstant())< 0 ) )
		   )
			{
	         doDisplay = false;
			} */
		      
		if (ptd.getDateOnHold() == null)   
	         doDisplay = false;
		if (ptd.getDateOnHold() != null && ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateStarted().toInstant()) > 0 )
			doDisplay = false;
		if (ptd.getDateOnHold() != null && ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateOnHold().toInstant()) > 0 )
			doDisplay = false;
		if (ptd.getDateOnHold() != null && ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateOnHold().toInstant()) > 0 )
			doDisplay = false;
		if (ptd.getDateOnHold() != null && ptd.getDateCompleted() != null && ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateCompleted().toInstant()) < 0 )
			doDisplay = false;
		if (ptd.getDateOnHold() != null && ChronoUnit.DAYS.between(theStartingCal.toInstant(),  Calendar.getInstance().toInstant()) <= 0 
			&&  ChronoUnit.DAYS.between(ptd.getDateOnHold().toInstant(),  Calendar.getInstance().toInstant()) > 0 )// issue 283 mark less than or equal to 0
			doDisplay = false;
		if (str.indexOf("chart-li-onHold-" + (indexLink)) < 0)
			doDisplay = false;
		if (ptd.getStatus().equals ("In progress") 
			&& 	
			ptd.getDateOnHold() != null 
			&&		
			ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateInProgress().toInstant()) <= 0
			&&
			indexxofHoldDate  <0				
		   )
		   doDisplay = false;
		
		dCalendar.setTime(new Date(nList.get(indexLink).convertToDateString(nList.get(indexLink).getDateStarted())));
		dCalendar.add(Calendar.DAY_OF_MONTH, daysExpected);
		theEndingDate = sdf.format(dCalendar.getTime());
    	String sampleTypeString = "";
    	if (!StringUtils.isNullOrEmpty(sampleTypeString))	   
    		sampleTypeString = sampleTypeString.substring(0,sampleTypeString.length()-1);
    	indexxofEndingDate = todayStrList.indexOf(theEndingDate.substring(0,5));
    	if (index == 0 )
	        {
		   prevWf = ptd.getWorkflow().getWfDesc();
		   prevExpId = ptd.getExperiment().getExpID();
		   prevAssayId = ptd.getAssay().getAssayName() + " (" +  ptd.getAssay().getAssayId() + ")";
		   // issue 262
		   str = str + 
   				" <span>" + ptd.getWorkflow().getWfDesc().replace("<", "&lt").replace(">", "&gt")  + " <br>" + 
				     ptd.getExperiment().getExpID() + " " + ptd.getAssay().getAssayName() + " (" +  ptd.getAssay().getAssayId() + ")"  + "<br>" + sampleTypeMap.get(ptd.getExperiment().getExpID()) +   "<br>" + "Contact and PI:" + ptd.getExperiment().getProject().getContactPerson().getFullNameByLast() + " " + ptd.getExperiment().getProject().getClient().getInvestigator().getFullNameByLast() + "</span>";
		   
	       }
	    	// issue 273
		    else if (!prevWf.equals(ptd.getWorkflow().getWfDesc()) || !prevExpId.equals(ptd.getExperiment().getExpID()) ||  !prevAssayId.equals(ptd.getAssay().getAssayName() + " (" +  ptd.getAssay().getAssayId() + ")") )
		   	   {
		    	str = str + "      <div  class=\"chart-row chart-period  \">" ;
		    	str = str + "          <div class=\"chart-row-item-big\"></div> " ;
		    	// issue 269
		    	
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
		    			"          <span style=\"border:none; height: 43px; width:401px;   \"> " + "comments" + "</span>" ; 
		    	
		    	
		    	        str = str + "      </div>" ;
		    	
		    	
		    	
		    	
		    	
		    	
		    	
			   str = str + " <span>" + ptd.getWorkflow().getWfDesc().replace("<", "&lt").replace(">", "&gt")  + " <br>" + 
				     ptd.getExperiment().getExpID() + " " + ptd.getAssay().getAssayName() + " (" +  ptd.getAssay().getAssayId() + ")" + "<br>" + sampleTypeMap.get(ptd.getExperiment().getExpID()) +  "<br>" + "Contact and PI:" + ptd.getExperiment().getProject().getContactPerson().getFullNameByLast() + " " + ptd.getExperiment().getProject().getClient().getInvestigator().getFullNameByLast() +  "</span>";
		       index = 0;
		       prevWf = ptd.getWorkflow().getWfDesc();
		       prevExpId = ptd.getExperiment().getExpID();
		       prevAssayId = ptd.getAssay().getAssayName() + " (" +  ptd.getAssay().getAssayId() + ")";
		   	   }
	    	 
	    	// issue 262
	    	str = str +    		
	    				"<div>" + 
	        			"  <a style=\"text-decoration:none;\" wicket:id= \"gchart" + Integer.toString(indexLink) + "\"   href=\"#\" > <div class=\"chart-row\">" +
	        			"        <div style=\"text-decoration:none;font-size: 15px;font-weight: bold;color:black; \"  class=\"chart-row-item-big\">" + (index + 1) + ". " + ptd.getProcessTracking().getTaskDesc().replace("<","&lt").replace(">", "&gt") + "\n" + "<br>"  + "Assigned To:" +  ptd.getAssignedTo().getFullName() +  "</div>" ;		
	    				
	    		String truncComment = StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ||  nList.get(indexLink).getComments().length() <= 50 ? nList.get(indexLink).getComments() : nList.get(indexLink).getComments().substring(0, 50);
	    		if (StringUtils.isNullOrEmpty(truncComment))
	    			truncComment = "";
	    		/// resume here
	    		// issue 283
	    		if (nList.get(indexLink).getDateCompleted() != null)
	    			{
	    			thecompleted = nList.get(indexLink).convertToDateString(nList.get(indexLink).getDateCompleted()).substring(0,5);
	    			indexCompleted = todayStrList.indexOf(thecompleted);
	    			}
	    		else
	    			indexCompleted = -1; 
	    		
	    		// issue 283 date in progress 
	    		
	    		if ( nList.get(indexLink).getDateCompleted() == null && 
	    				nList.get(indexLink).getDateOnHold() != null && 
	    				indexxofHoldDate >= 0   && indexxofDate >= 0 )
	    			{	
	    		      if (indexLink == 260)  
                 	   System.out.println("260 ahha here ");
	    		      if (indexLink == 0)  
	                   	   System.out.println("0 number 1");
	    			str = str + "        <ul class=\"chart-row-bars\">" +	    					    
	    					 ( ( indexxofDate < 0 ) ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" ) +	 					
		   		 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
		   		 		(doDisplay   ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
		   		 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 
		   		 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
		   			  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
		   		 		"                               </ul>" ;
	    			    
	    			}
	    		
	    		// issue 283 in progress
	    		if (nList.get(indexLink).getDateInProgress() != null && nList.get(indexLink).getDateCompleted() != null && 
	    				indexCompleted >= 0 && indexxofHoldDate >= 0 && indexxofProgressDate > 0  )
	    			{	
	    		      if (indexLink == 260)  
                   	   System.out.println("260 number 1");
	    		      if (indexLink == 0)  
	                   	   System.out.println("0 number 1");
	    			str = str + "        <ul class=\"chart-row-bars\">" +
	    			
                 
	    					    
	    					 ( ( indexxofDate < 0 ) ? "" : " <li   class=\" chart-li-2Prog" + (indexLink) +   "\">" +   "</li>" ) +	 					
		   		 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
		   		 		(doDisplay   ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
		   		 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 
		   		 	  ((  nList.get(indexLink).getDateInProgress() != null) ? " <li   class=\" chartInProgress-li-" + (indexLink) +   "\">" +   "</li>" : " ") +
		   		 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
		   			  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
		   		 		"                               </ul>" ;
	    			
	    			}
	    		
	    		// issue 283 in progress on hold in progress to complete 
	    		if (nList.get(indexLink).getDateInProgress() != null && nList.get(indexLink).getDateCompleted() != null && 
	    				indexCompleted > 0 && indexxofHoldDate < 0 && indexxofProgressDate > 0  )
	    			{
	    		      if (indexLink == 260)  
	                   	   System.out.println("260 number 2");
	    		      
	    		      if (indexLink == 0)  
	                   	   System.out.println("0 number 2");
	    			str = str + "        <ul class=\"chart-row-bars\">" +
	    			
                                              
	    					 
	    					 ( ( indexxofDate < 0 ) ? "" : " <li   class=\" chart-li-2Prog" + (indexLink) +   "\">" +   "</li>" ) +	 					
		   		 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
		   		 		(doDisplay   ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
		   		 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 
		   		 	  ((  nList.get(indexLink).getDateInProgress() != null) ? " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" : " ") +
		   		 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
		   			  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
		   		 		"                               </ul>" ;
	    			
	    			         
	    			}
	    		
	    		///////////////
	    		///////////////  issue 283 putting switching in progress on hold
	    		///////////////
	    		
	    		if (nList.get(indexLink).getDateInProgress() != null && nList.get(indexLink).getDateCompleted() == null && indexxofProgressDate > 0 
	    				  )
	    			{
	    			boolean displayprog = false;
	    			if ( str.indexOf("ul .chart-li-" + (indexLink) +  "{") < 0 )
	    			    displayprog = false;
	    			else
	    				displayprog = true;          
	    		      if (indexLink == 260)     
	                   	   System.out.println("260 number 3");
	    		      if (indexLink == 0)  
	                   	   System.out.println("0 number 3");
	    		      if (str.indexOf("<li   class=\" chart-li-" + (indexLink) +   "\">") < 0)
		    		      {
		                 str = str + "        <ul class=\"chart-row-bars\">" + 
		                   	(displayprog ? (" <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>") : " ")   +
			   		 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
			   		 		(doDisplay ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
			   		 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	
			   		 	 ( ( indexxofProgressDate < 0 ) ? "" : " <li   class=\" chart-li-2Prog" + (indexLink) +   "\">" +   "</li>" ) +	
			   		 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
			   			  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
			   		 		"                               </ul>" ;
		    		      }
	    		      }
	    		           
	    		//// index 283 fix onhold stuff not displaying 
	    		
	    	//	if (nList.get(indexLink).getDateInProgress() != null && nList.get(indexLink).getDateCompleted() == null && indexxofProgressDate < 0 
	    		if (nList.get(indexLink).getDateInProgress() != null  /*&& indexxofProgressDate < 0 		  ) */
	    			&& str.indexOf(" <li   class=\" chart-li-onHold-" + (indexLink) +   "\">") < 0 )
	    		     {
	    			  boolean doDisplayInProgressAfterCurrentDate = true;
	    			  if (   (ChronoUnit.DAYS.between(theStartingCal.toInstant(),  Calendar.getInstance().toInstant() )  <= 0 )   )
	    			  	  {
	    				  doDisplayInProgressAfterCurrentDate  = false;
	    			  	  }
	    		      if (indexLink == 260 	     
	    		    	 )  
	    		      	{  
	                   	System.out.println("260 number 4 here come starting and current date..." + (doDisplayInProgressAfterCurrentDate));
	                   	SimpleDateFormat sddf = new SimpleDateFormat("MM/dd/yyyy");
	                   	System.out.println ((theStartingCal == null) ? "" : sdf.format(theStartingCal.getTime()));
	                    Calendar lcal = Calendar.getInstance();
	                    
	                   	System.out.println((lcal == null) ? "" : sdf.format(lcal.getTime()));
	                   	System.out.println(ChronoUnit.DAYS.between(theStartingCal.toInstant(),  Calendar.getInstance().toInstant() ) );
	                   	System.out.println(ChronoUnit.DAYS.between(theEndingCal.toInstant(),  Calendar.getInstance().toInstant() ) );
	    		      	}
	    		      if (indexLink == 0)  
	                   	   System.out.println("0 number 4");
	                  str = str + "        <ul class=\"chart-row-bars\">" + 
	                		 ( doDisplayInProgressAfterCurrentDate ?  " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" : " ") + 
		   		 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
		   		 		(doDisplay ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
		   		 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	
		   		 
		   		 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
		   			  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
		   		 		"                               </ul>" ;
	    			}
	    		
	    		   
	    		
	    		   
	    		indexxofCurrentDate = todayStrList.indexOf(theCurrentDate.substring(0,5));
	    		
	    		if (indexxofDate <0 && indexxofCurrentDate < 0  && 
	    			ChronoUnit.DAYS.between(theStartingCal.toInstant(),  nList.get(indexLink).getDateStarted().toInstant()) < 0 
	   			     &&
	   			     ChronoUnit.DAYS.between(theEndingCal.toInstant(),  forCurrentCal.toInstant()) > 0	
	    			&& 	nList.get(indexLink).getStatus().equals("In progress")
	    		    )	    				
	    			{
	    			 continuingInProcess = true;		
	    			if (nList.get(indexLink).getDateInProgress()== null)
		    			{
	    			      if (indexLink == 260)  
	                      	   System.out.println("260 number 5");
	    			      if (indexLink == 0)  
	                      	   System.out.println("0 number 5");
		    			str = str + "        <ul class=\"chart-row-bars\">" +
		   		 			 ( (ChronoUnit.DAYS.between(theStartingCal.toInstant(), Calendar.getInstance().toInstant())) < 0   ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" ) +		 					
		   		 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
		   		 		(doDisplay  ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
		   		 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 		
		   		 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
		   			  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
		   		 		"                               </ul>" ;
		    			}
	    			
	    			}  
	    		
	    		// issue 283 see how it works plain on hold
	    		else if (indexxofDate >0 
	    				    && indexxofCurrentDate < 0   
		    			 	&& nList.get(indexLink).getStatus().equals("On hold")
		    		    )	  
	    			{
	    		      if (indexLink == 260)  
	                   	   System.out.println("260 number 6");
	    		      if (indexLink == 0)  
	                   	   System.out.println("0 number 6");
	    			if (nList.get(indexLink).getDateInProgress()== null)
		    			{
		    			str = str + "        <ul class=\"chart-row-bars\">" +
			   		 			  " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>"  +		 					
			   		 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
			   		 		(doDisplay ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
			   		 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 		
			   		 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
			   			  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
			   		 		"                               </ul>" ;
		    			}
	    			}
	    		    
	    		
	    		if ((indexxofDate >= 0 || indexxofCurrentDate >= 0 ) &&
	    				! (ptd.getDateOnHold() != null && ptd.getDateCompleted() != null
	    					&&	indexxofHoldDate < 0 &&indexCompleted < 0	    						
	    				   )	
	    		   ) //&& indexxofEndingDate>= 0    
	    				
	    			{  
	    			
	    			
	    			
	    			daysBetweeni = ChronoUnit.DAYS.between(nList.get(indexLink).getDateStarted().toInstant(),Calendar.getInstance().toInstant() );
	    			
	    			if (indexxofDate >=0 && indexxofCurrentDate <0  && indexCompleted >= 0 && ptd.getDateCompleted() != null && ptd.getDateOnHold() == null)
	    			  {
	    		    				 
	    				 if (ptd.getDateCompleted()!= null &&  ChronoUnit.DAYS.between(nList.get(indexLink).getDateStarted().toInstant(), ptd.getDateCompleted().toInstant())  == 0)
	    				 	{
	    					  
	    						if (nList.get(indexLink).getDateInProgress()== null)
		    		    			{
	    						      if (indexLink == 260)  
	    			                   	   System.out.println("260 number 7");
	    						      if (indexLink == 0)  
	    			                   	   System.out.println("0 number 7");
		    					    str = str + "        <ul class=\"chart-row-bars\">" +
			    					""	+ 				
			    					(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
			    					(doDisplay ? " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
			    					(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 		
			    					// 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
			    					" <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
			    					"                               </ul>" ;
		    		    			}
	    		    			}
	    				 
	    				else 
		    				{	
	    					 if (nList.get(indexLink).getDateInProgress()== null)
	    		    			 {
	    						 if (indexLink == 260)  
  			                   	   System.out.println("260 number 8");
	    						 if (indexLink == 0)  
	  			                   	   System.out.println("0 number 8");
		    					 str = str + "        <ul class=\"chart-row-bars\">" +
				    					 (daysBetweeni < 0  ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" )  +		 					
				   		 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
				   		 		(doDisplay ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
				   		 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 		
				   		 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
				   		 	  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
				   		 		"                               </ul>" ;
	    		    			}
		    				}
	    			  }
	    		
	    			   //// issue 283 put in more cases more cases
	    			else if (!(
	    				nList.get(indexLink).getDateCompleted() == null &&
	    				nList.get(indexLink).getDateOnHold() != null && 
	    				indexxofCurrentDate < 0 && 
	    				indexxofHoldDate < 0 &&	
	    				indexxofDate >= 0 
	    				) 
	    				&& 
	    				! (nList.get(indexLink).getDateCompleted() == null &&
	    				nList.get(indexLink).getDateOnHold() != null && 
	    				indexxofCurrentDate < 0 && 
	    				indexxofHoldDate >= 0 &&	
	    				indexxofDate >= 0 )
	    			     )   
		    			{
	    				boolean displayInProg = true;
	    				if (nList.get(indexLink).getDateCompleted() != null &&  indexCompleted < 0 &&
	    						ChronoUnit.DAYS.between(theStartingCal.toInstant(),  nList.get(indexLink).getDateCompleted().toInstant() )< 0 
	    						) 
								{
                                displayInProg = false;
								}
	    				if (nList.get(indexLink).getStatus().equals("In queue")
	    						) 
								{
                                displayInProg = false;
								}
	    				 // fix vanilla on hold issue 283
	    				 if (nList.get(indexLink).getDateInProgress()== null)
			    			 {
	    					 if (indexLink == 260)  
			                   	   System.out.println("260 number 9");
	    					 if (indexLink == 0)  
			                   	   System.out.println("0 number 9");
			    			str = str + "        <ul class=\"chart-row-bars\">" +
			    				/////////	 (daysBetweeni < 0 || ( !nList.get(indexLink).getStatus().equals("In progress") && nList.get(indexLink).getDateInProgress() == null) ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" )  +	
			    				(!displayInProg  ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" )  +
			   		 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
			   		 		(doDisplay ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
			   		 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 		
			   		 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
			   		 	  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
			   		 		"                               </ul>" ;
			    			 }
		    			}
	       			
	    			///// put back
	    		////	str = str + "        <ul class=\"chart-row-bars\">" +
		 		////	 (daysBetweeni < 0 || ( !nList.get(indexLink).getStatus().equals("In progress") && nList.get(indexLink).getDateInProgress() == null) ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" ) +		 					
		 		////(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
		 		////(nList.get(indexLink).getDateOnHold() != null ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
		 		////(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 		
		 	  ////  // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
		 	 //// " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
		 	////	"                               </ul>" ;
	    			}
	    		
	    		else if ((indexxofHoldDate >= 0 || indexCompleted >= 0) &&
	    				!
	    				((ptd.getDateOnHold() != null && ptd.getDateCompleted() != null
    		    		&& ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateOnHold().toInstant()) > 0 
    		    		&& ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateOnHold().toInstant()) > 0 
    		    		&& ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateCompleted().toInstant()) > 0 
    		    		&& ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateCompleted().toInstant()) > 0 )
    		    	||
    		    (ptd.getDateOnHold() != null && ptd.getDateCompleted() != null
	    		&& ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateOnHold().toInstant()) < 0 
	    		&& ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateOnHold().toInstant()) < 0 
	    		&& ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateCompleted().toInstant()) < 0 
	    		&& ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateCompleted().toInstant()) < 0 ))
	    				)
	    			{
	    			 if (nList.get(indexLink).getDateInProgress()== null)
		    			 {
	    				 if (indexLink == 260)  
		                   	   System.out.println("260 number 10:" + doDisplay);
	    				 if (indexLink == 0)  
		                   	   System.out.println("0 number 10");       
	    				 
	    				 boolean displayProgress = false;
    					 if (indexxofDate >= 0 )
    						 displayProgress = true;
    					 else if (indexxofHoldDate >= 0 )
    						 displayProgress = true;
    					 else if   (nList.get(indexLink).getDateOnHold() != null &&    ChronoUnit.DAYS.between(theStartingCal.toInstant(), nList.get(indexLink).getDateStarted().toInstant()) <= 0 
    							 &&
    							 ChronoUnit.DAYS.between(theEndingCal.toInstant(), nList.get(indexLink).getDateOnHold().toInstant()) >= 0 )
    					 	 {
    						 displayProgress = true;   
    					 	 }    
    					 else if (nList.get(indexLink).getDateOnHold() == null && nList.get(indexLink).getDateCompleted() != null && 
    							 indexCompleted > 0 )
    						 displayProgress = true;
	    				 
		    			str = str + "        <ul class=\"chart-row-bars\">" +
		    					// 
		    					(!displayProgress ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" ) +		 					
		    				 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
		    				 		(doDisplay ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
		    				 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 		
		    				 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
		    				 	  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
		    				 		"                               </ul>" ;
		    			 }
	    			}
	    		
	    		/// continue here .....
	    		else if (indexxofHoldDate < 0 || indexCompleted < 0  )  
					{	 
	    			 
	    			///////
	    			
	    		    Long sCron = 0L;
	    		    long eCron = 0L;
	    		    
	    		  //  if (ptd.getDateOnHold() != null && ptd.getDateCompleted() != null)
	    		   // 	{
	    		    	
	    		  //  	}
	    		//	Long sCron = ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateOnHold().toInstant()) ;
	    		//	Long eCron = ChronoUnit.DAYS.between(theEndingCal.toInstant(),ptd.getDateCompleted().toInstant());
	    		
	    			////////
	    		    
	    		    
	    		    
	    		    if ((ptd.getDateOnHold() != null && ptd.getDateCompleted() != null
	    		    		&& ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateOnHold().toInstant()) > 0 
	    		    		&& ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateOnHold().toInstant()) > 0 
	    		    		&& ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateCompleted().toInstant()) > 0 
	    		    		&& ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateCompleted().toInstant()) > 0 )
	    		    	||
	    		    (ptd.getDateOnHold() != null && ptd.getDateCompleted() != null
		    		&& ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateOnHold().toInstant()) < 0 
		    		&& ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateOnHold().toInstant()) < 0 
		    		&& ChronoUnit.DAYS.between(theStartingCal.toInstant(),  ptd.getDateCompleted().toInstant()) < 0 
		    		&& ChronoUnit.DAYS.between(theEndingCal.toInstant(),  ptd.getDateCompleted().toInstant()) < 0 )
	    		    	)
	    		    	
	    		    	{  
	    				 if (nList.get(indexLink).getDateInProgress()== null)
			    			 {
	    					 if (indexLink == 260)  
			                   	   System.out.println("260 number 11");
	    					 if (indexLink == 0)  
			                   	   System.out.println("0 number 11");
	    					 
	    					 boolean displayProgress = false;
	    					 if (indexxofDate >= 0 )
	    						 displayProgress = true;
	    					 else if (indexxofHoldDate >= 0 )
	    						 displayProgress = true;
	    					 else if   (nList.get(indexLink).getDateOnHold() != null &&    ChronoUnit.DAYS.between(theStartingCal.toInstant(), nList.get(indexLink).getDateStarted().toInstant()) <= 0 
	    							 &&
	    							 ChronoUnit.DAYS.between(theEndingCal.toInstant(), nList.get(indexLink).getDateOnHold().toInstant()) >= 0 )
	    					 	 {
	    						 displayProgress = true;
	    					 	 } 
		    		    	str = str + "        <ul class=\"chart-row-bars\">" +
									/// (daysBetweeni < 0 || ( !nList.get(indexLink).getStatus().equals("In progress") && nList.get(indexLink).getDateInProgress() == null) ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" ) +		 					
									(!displayProgress ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" ) +		
									(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) +  	
								 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 		
								 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
								 	  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
								 		"                               </ul>" ;
			    			 }
	    		    	
	    		    	}
	    		    else      
	    		    	
		    		    { 
	    		    	if (!continuingInProcess)      
		    		    	{    
	    		    		 boolean displayProcessAfterComplete = false;
	    		    		 if (nList.get(indexLink).getDateCompleted() != null && ChronoUnit.DAYS.between(theStartingCal.toInstant(),  nList.get(indexLink).getDateCompleted().toInstant()) < 0 )
	    		    		     displayProcessAfterComplete = false;
	    		    		 else 
	    		    			 displayProcessAfterComplete = true;
	    		    		 
	    		    			 if (nList.get(indexLink).getDateInProgress()== null)
				    			 {
	    		    			 if (indexLink == 260)  
				                   	   System.out.println("260 number 12");
	    		    			 if (indexLink == 0)  
				                   	   System.out.println("0 number 12");
		    		    		str = str + "        <ul class=\"chart-row-bars\">" +
										 (  (!displayProcessAfterComplete || daysBetweeni < 0) || ( !nList.get(indexLink).getStatus().equals("In progress") && nList.get(indexLink).getDateInProgress() == null  &&   ChronoUnit.DAYS.between(theStartingCal.toInstant(),  Calendar.getInstance().toInstant()) < 0                 ) ? "" : " <li   class=\" chart-li-" + (indexLink) +   "\">" +   "</li>" ) +		 					
									 		(nList.get(indexLink).getDateCompleted() != null && indexCompleted>=0 ? 	 " <li   class=\" chart-li-cmplt-" + (indexLink) +   "\">" +   "</li>" : "" ) + 
									 		(doDisplay ? 	 " <li   class=\" chart-li-onHold-" + (indexLink) +   "\">" +   "</li>" : "" ) + 	
									 		(indexxofDate >= 0 ? "  <li   class=\" chart-li-" + (indexLink)  + "-expect\">" +   " </li>" : "")+	 		
									 	    // 	" <li style= \" width:401px; height:35px; overflow-y: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + nList.get(index).getComments() +   "\">  "  + truncComment.replace("\n", "<br>").replace("\r",  "<br>") + "<br>" +  " </li>  " +
									 	  " <li style= \" width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
									 		"                               </ul>" ;
				    			 }  
		    		    	}       
	    		    	else    
	    		    		continuingInProcess = false;   
		    		      }
					}
	    		else 
	    		{

	    			str = str + "        <ul class=\"chart-row-bars\"  style= \"background-color: transparent; \" >" +
	    		 		 " <li  style= \" display:none; \">" +   "</li>"  +	
	    		 		 " <li style= \" display:none; \">" +   "</li>"  +	
	    		 		 " <li style= \" display:none; \">" +   "</li>"  +	
	    		 	  " <li style= \" display:block; width:401px; height:43px; overflow-y: auto; overflow-x: auto; color:black;text-align:top;   \" class=\" chart-li-" + (indexLink) + "-comment\"  title=\"" + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : nList.get(indexLink).getComments()) +   "\">  "  + (StringUtils.isNullOrEmpty(nList.get(indexLink).getComments()) ? " " : truncComment) +  "<br>" +  " </li>  " +
	    		 		"                               </ul>" ;
	    		}
			str = str + "      </div> </a></div>";
	    		
	    		
	    		     
	    	    index++;
	    	    indexLink++;
    	
    	} // end of for loop end of for loop
    
    
    
        str = str + 
        		"    </div>" +
        		"" +
        		"</div> </form>";
        
  try 
  	{
    BufferedWriter f_writer
    = new BufferedWriter(new FileWriter(
     "/Users/admin/gantchart.txt"));
    f_writer.write(str);
    f_writer.close();
  	}
  catch(Exception e)
  
  {
	  
  }
        
    return new StringResourceStream(str);
	}

public GantChart (String id)
	{
	assignedTo = "";
	sampleTypeMap =  processTrackingService.createSampleTypeStringFromList();
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
	    nList = processTrackingService.loadAllTasksAssigned(expID, StringParser.parseId(assayDescID), allExpAssay, assignedTo, isCurrent, isInProgress, isOnHold);
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
					    // issue 273
					case "updateExperiment" :
						if (expID.equals ("All Experiments") )
							{
							allExpAssay = true;
							expID = "All Experiments";
							assayDescID = null;
							MarkupCache.get().clear();
				    	    getMarkupResourceStream(markupContainer, containerClassl) ;
				    	    target.add(gantChart); 
				    	    break;
							}
						allExpAssay = false;
						isAllExp = false;
						assayDescDD.setChoices (assayService.allAssayNamesForExpId(expID, false));	
						MarkupCache.get().clear();
				    	    getMarkupResourceStream(markupContainer, containerClassl) ;
				    	    target.add(gantChart); 
						break;
					case "updateAssayDesc" :
						allExpAssay = false;
						isAllExp = false;
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
	    
		List <String> expList = new ArrayList <String> ();
		// issue 273
		expList.add("All Experiments");
		expList.addAll(processTrackingService.loadAllAssignedExperiments());
	 	experimentDD.setChoices (expList)	;		
		if (allExpAssay)
			expID = "All Experiments";
	 	return experimentDD;
		
		}
	 
	 ///////////////////////////////
	 
	 private AjaxLink buildGanttChartLink(String id, int i)
		{
		AjaxLink link;		
		// Issue 2607
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
	 
	 // issue 283
	 public void doMoveDownInProcess()
	 	{
		 Calendar theCurrentPDate = Calendar.getInstance();
		 int indexx = 0;
		 int i = 0;
		// System.out.println("just before for loop");
		 String prevExp = ""; 
		 String prevAssay = "";
		 SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		 List <ProcessTrackingDetails> nListExpAssay = new ArrayList <ProcessTrackingDetails> ();
		 for (ProcessTrackingDetails ptd : nList)
		 	{
			 if (ptd.getExperiment().getExpID().equals("EX00599")
					 && ptd.getAssay().getAssayId().equals("A003"))
				 nListExpAssay.add(ptd);
				 
		 	}
		 
		 for (ProcessTrackingDetails ptd : nListExpAssay)
		 	{
			 if (indexx == 0)
			 	{
				 indexx ++;
				 continue;
			 	}   
	        theCurrentPDate.add(Calendar.DAY_OF_MONTH, 1);
	        
			ptd.setDateStarted(theCurrentPDate);
			}
		 
	 	}
	 
		public String getAssignedTo() { return assignedTo; }
		public void setAssignedTo (String e) { assignedTo = e; }
}
	

