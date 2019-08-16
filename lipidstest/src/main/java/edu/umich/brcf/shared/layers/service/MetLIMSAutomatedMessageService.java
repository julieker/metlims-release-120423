package edu.umich.brcf.shared.layers.service;
// issue 358
import java.util.List;
import org.apache.wicket.spring.injection.annot.SpringBean;
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


@Service


@Configuration
@EnableScheduling
@Component


@Transactional
public class MetLIMSAutomatedMessageService
	{
	 @Autowired
	@SpringBean
	METWorksMessageMailer mailer;
	
	 
	@Autowired
	@SpringBean
	SampleService sampleService;
	
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
	@Scheduled(cron="0 39 14 01 * ?")
     // Issue 214
	public void sendExpiredSampleReport()
        {     	
        String msg = " Samples between " +  sampleLowerAgeLimit + " and " + sampleUpperAgeLimit + " days old ";
        String htmlString = "<h3><b> "  + msg + " </b></h3> <br> <h4>" +  buildSampleExpireString(sampleService.getExpiredSamples(sampleLowerAgeLimit,sampleUpperAgeLimit))  + "</h4>";
        String mailTitle = "METLIMS between " +  sampleLowerAgeLimit + " and " + sampleUpperAgeLimit +   " days old ";
        String mailAddress =  "metabolomics@med.umich.edu";
    	List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("expired_samples_notification_member");	       
	 	if (email_contacts != null)
	 			for (String email_contact : email_contacts)
	 			    {
	 				METWorksHTMLMailMessageSender metWorksHTMLMailMessageSender=  new METWorksHTMLMailMessageSender(mailSender, mailAddress, email_contact, mailTitle, msg, htmlString);
	 			    }
         } 
	
	 public  String buildSampleExpireString (List<String[]> sList )
         {
	     String sampleStringHeader = "";	 
	     sampleStringHeader = sampleStringHeader + " <html> <head> <style>";
	     sampleStringHeader = sampleStringHeader + "table { font-family: arial, sans-serif;border-collapse: collapse; width: 100%;}";
	     sampleStringHeader = sampleStringHeader + "td, th { border: 1px solid #dddddd; text-align: left; padding: 8px;}";
	     sampleStringHeader = sampleStringHeader + "table tr:nth-child(even) {  background-color: #f2f2f2; }"; 
	     sampleStringHeader = sampleStringHeader + " </style> </head> <body>";
	     sampleStringHeader = sampleStringHeader + "<h2>SAMPLES BETWEEN " +  sampleLowerAgeLimit + " AND " + sampleUpperAgeLimit + " DAYS OLD  </h2>";
         sampleStringHeader = sampleStringHeader + "<table> <tr style = \"background-color: #f2f2f2\">   <th>Experiment Name </th>  <th>Experiment Id</th> <th> Total Expired Samples </th> <th>Date Created</th> <th>Contact</th> <th>Email</th> </tr><tr>";
	     for (Object [] obj : sList)
		     sampleStringHeader = sampleStringHeader + " <tr><td>" + obj[0].toString() + "</td><td>" + obj[1] + "</td><td> " +  obj[2] + "</td><td> " + obj[3] + "</td><td> " + obj[4] + "</td><td> " +  obj[5] + "</td></tr>" ;
	     sampleStringHeader = sampleStringHeader + "</table></body></html>";
	     return sampleStringHeader;
         }
     }
	
	