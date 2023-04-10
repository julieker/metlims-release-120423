package edu.umich.brcf.shared.layers.service;
import java.util.ArrayList;
// issue 358
import java.util.List;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksHTMLMailMessageSender;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.util.StringParser;


@Service


@Configuration
@EnableScheduling
@Component


@Transactional
public class MetLIMSTrackingAutomatedMessageService
	{
	 @Autowired
	@SpringBean
	METWorksMessageMailer mailer;
	
	 
	@Autowired
	@SpringBean
	SampleService sampleService;
	
	@Autowired
	@SpringBean
	ProcessTrackingService processTrackingService;
	
	@Autowired
	@SpringBean
	private JavaMailSender mailSender = null;
	
	@Autowired
	@SpringBean
	SystemConfigService systemConfigService;
	
	int sampleLowerAgeLimit = 90;
	int sampleUpperAgeLimit = 180;

	//@Scheduled(cron = "0 5 13 * * * ")
    //  @Scheduled(cron = "0 */2 * * * * ")
	//@Scheduled(cron="0 7 12 1 1/1 *")
	////////@Scheduled(cron="0 39 14 01 * ?")
     // Issue 214
	//@Scheduled(cron="0 39 14 01 * ?")
	//@Scheduled(cron="0 10 10 23 * ?")
	
	//@Scheduled(cron="0 10 10 23 * ?")
	
	//@Scheduled(cron="0 51 11 * * WED")
	@Scheduled(cron="0 15 08 * * FRI")
	
	public void sendAssignedTasksReport()
        {     	
        String msg = " Metlims Tracking System:  Your tasks ";
        String htmlString = "";
        
        String mailTitle = "METLIMS TRACKING your Friday tasks \n";
        String mailAddress =  "metabolomics@med.umich.edu";
        List<Object[]> nList = new ArrayList<Object[]> ();
    	List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("task_notification");	       
    	if (email_contacts != null)
 			for (String email_contact : email_contacts)
 			    {
 				nList = processTrackingService.loadTasksAssignedForUser(email_contact);
 				htmlString = "<h3><b> "  + msg + " </b></h3> <br> <h4>" +  buildTaskString(nList, "List of your assigned tasks")  + "</h4>";
 				METWorksHTMLMailMessageSender metWorksHTMLMailMessageSender=  new METWorksHTMLMailMessageSender(mailSender, mailAddress, "julieker@umich.edu", mailTitle, msg, htmlString);
 			    }
    	nList = processTrackingService.loadTasksAssignedForExp(nList.get(0)[5].toString());												
		//msg = "Friday List of Tasks assigned for Experiment"  + nList.get(0)[5].toString() + " Workflow:" + nList.get(0)[6].toString();
		sendAssignedTasksExpReport(nList, msg);   
         } 
	
	
	public void sendAssignedTasksExpReport(List<Object[]> nlist , String msgTitle)
    	{     
		sendAssignedTasksExpReport(nlist, msgTitle, true);
    	}
	public void sendAssignedTasksExpReport(List<Object[]> nlist , String msgTitle, boolean isScheduled)
	    {  
		List<String> email_contacts = new ArrayList <String> ();
		String mailAddress =  "metabolomics@med.umich.edu";
		email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("task_notification");		
		String msg = (isScheduled) ? "Metlims Tracking System: List of all Experiments and Assigned Tasks <br><br>" : " Metlims Tracking System:  Tasks for Experiment: " + (nlist.size() == 0 ? "No tasks for this Experiment:" : nlist.get(0)[5]);
		String htmlString = "";	
	    List <Object[]> newEachAssayList = new ArrayList <Object []> ();
	    List <Object[]> listExpAssay = new ArrayList <Object []> ();
	    List <Object[]> listExpAssayExp = new ArrayList <Object []> ();
	    listExpAssay = processTrackingService.listExpAssay();
	    listExpAssayExp = nlist.size() == 0 ? new ArrayList <Object []> () : processTrackingService.listExpAssayExp(nlist.get(0)[5].toString());
	    String mailTitle = " ";	    
	    
	    if (!isScheduled)
	    	{
	    	///////////
		    	if (nlist.size() == 0)
		    	{
			        for (String email_contact : email_contacts) 
			    		{
			    		htmlString =   "<h3><b> "  + msg + " " +   msgTitle + " </b></h3> <br> ";
			    		METWorksHTMLMailMessageSender metWorksHTMLMailMessageSender=  new METWorksHTMLMailMessageSender(mailSender, mailAddress, "julieker@umich.edu", "METLIMS Tracking - METLIMS Sample Registration Message", msg, htmlString);
			    		}
		    	return;
		    	
		    	}
	    	///////////
		    if (email_contacts != null)
					{
			 		for (String email_contact : email_contacts)
			 		    {
			 			
			 			htmlString = "";
			 			try
				 			{
			 				for (Object[] llAssayExp: listExpAssayExp)
		 						{
			 					newEachAssayList = new ArrayList <Object []> ();
			 					for (Object[]llist : nlist )
			 						{
			 						if (llist[6].toString().equals(llAssayExp[1].toString()))
			 							{
			 							newEachAssayList.add(llist);
			 							}
			 						}
			 					htmlString = htmlString +  "<h3><b> "  + msg + " " +   msgTitle + " </b></h3> <br> <h4>" +  buildTaskString(newEachAssayList, "List of assigned tasks for experiment:" + nlist.get(0)[5].toString()  + " and assay: " + llAssayExp[1])  + " </h4>";
		 						}
				 			METWorksHTMLMailMessageSender metWorksHTMLMailMessageSender=  new METWorksHTMLMailMessageSender(mailSender, mailAddress, "julieker@umich.edu", "METLIMS Tracking - METLIMS Sample Registration Message", msg, htmlString);
				 			}
			 			catch (Exception e)
				 			{
				 			System.out.println("in except....");
				 			e.printStackTrace();
				 			}
			 		    }
			 		return;
					}
		    	
	    	} 
		if (email_contacts != null)
			{
 			for (String email_contact : email_contacts)
 			    {
 				for (Object[] lilexpAssay: listExpAssay)
 					{
 					nlist = new ArrayList<Object[]> ();
 					nlist = processTrackingService.loadTasksAssignedForExpAndAssay(lilexpAssay[0].toString(),StringParser.parseId(lilexpAssay[1].toString()) );	
 					//mailTitle = "METLIMS Tasks for Experiment " + nlist.get(0)[5];
 					mailTitle = "";
 					msgTitle = "Friday List of Tasks assigned for Experiment:" + nlist.get(0)[5].toString() + " Assay: " + nlist.get(0)[6].toString() + " Workflow:" + nlist.get(0)[8].toString();
 					htmlString = htmlString +  "<h3><b> "  + msg + " " +   msgTitle + " </b></h3> <br> <h4>" +  buildTaskString(nlist, "List of assigned tasks for experiment:" + nlist.get(0)[5].toString())  + "</h4>";
 					}
 				METWorksHTMLMailMessageSender metWorksHTMLMailMessageSender=  new METWorksHTMLMailMessageSender(mailSender, mailAddress, "julieker@umich.edu", "METLIMS Tracking - Weekly task list" , msg, htmlString);
 				htmlString = "";
 			    }
		}
	     } 
	
	 public  String buildTaskString (List<Object[]> sList, String titleStr )
         {
	     String sampleStringHeader = "";	     
	     sampleStringHeader = sampleStringHeader + " <html> <head> <style>";
	     sampleStringHeader = sampleStringHeader + "table { font-family: arial, sans-serif;border-collapse: collapse; width: 100%;}";
	     sampleStringHeader = sampleStringHeader + "td, th { border: 1px solid #dddddd; text-align: left; padding: 8px;}";
	     sampleStringHeader = sampleStringHeader + "table tr:nth-child(even) {  background-color: #f2f2f2; }"; 
	     sampleStringHeader = sampleStringHeader + " </style> </head> <body>";
	     sampleStringHeader = sampleStringHeader + "<h2>" +  titleStr  +  "</h2>";
         sampleStringHeader = sampleStringHeader + "<table> <tr style = \"background-color: #f2f2f2\">   <th>User Name </th>  <th>Task Description</th> <th> Date Started </th> <th> Date On Hold </th> <th>Status</th> <th> Exp ID </th> <th> Assay ID </th> <th>WorkFlow Desc</th> </tr><tr>";
         for (Object [] obj : sList)
		     sampleStringHeader = sampleStringHeader + " <tr><td>" + obj[0].toString() + "</td><td>" + obj[1] + "</td><td> " +  (obj[2] == null ? " " : obj[2].toString()) + "</td><td> " + (obj[3] == null   ? " " : obj[3].toString()) + "</td><td> " + obj[4] + "</td><td> " +  obj[5] +  "</td><td> " +   obj[6] +  "</td><td> " + obj[8] + "</td></tr>" ;
	     sampleStringHeader = sampleStringHeader + "</table></body></html>";
	     return sampleStringHeader;
         }
     }
	
	
