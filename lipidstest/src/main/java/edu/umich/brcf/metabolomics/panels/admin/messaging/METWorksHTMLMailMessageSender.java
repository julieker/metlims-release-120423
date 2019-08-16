package edu.umich.brcf.metabolomics.panels.admin.messaging;
import java.util.List;

import java.util.Map;

// issue 345
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMailMessage;

public class METWorksHTMLMailMessageSender extends SimpleMailMessage
    {
//	@SpringBean
//	private JavaMailSender mailSender = null;
	
	public METWorksHTMLMailMessageSender(JavaMailSender mailSender,String mailAddress, String email_contact, String mailTitle, String msg, String htmlString)
	    {
		METWorksMailMessage m = new METWorksMailMessage(mailAddress, email_contact, mailTitle,  msg);		
		sendHTMLOldSamples(mailSender, mailAddress, email_contact, mailTitle,msg,  htmlString);
	    }
    public void sendHTMLOldSamples(JavaMailSender mailSender, String mailAddress, String email_contact, String mailTitle, String message, String headerText)
        {
    //JAK issue 199 and issue 214
    	METWorksMailMessage m = new METWorksMailMessage(mailAddress, email_contact, mailTitle,  message);		
        MimeBodyPart mbp1 = new MimeBodyPart();
        try
            {
            mbp1.setContent(headerText, "text/html");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);          
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessage.setContent(headerText, "text/html");
            helper.setTo(m.getTo());
            helper.setSubject(m.getSubject());
            helper.setFrom(m.getFrom());
            mimeMessage.setContent(mp);
            mailSender.send(mimeMessage);
            }
        catch (MessagingException e)
            {
//TODO Auto-generated catch block
            e.printStackTrace();
            }
         }
    }