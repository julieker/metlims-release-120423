package edu.umich.brcf.metabolomics.panels.admin.messaging;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
//import org.testng.log4testng.Logger;
import org.springframework.mail.javamail.MimeMessageHelper;
// authenticate

public class METWorksMessageMailer
	{
	// private Log ger logger = Logger.getLogger(METWorksMessageMailer.class);
	private JavaMailSender mailSender = null;

	public void sendMessage(METWorksMailMessage message)
		{
		try
			{
			mailSender.send(message);
			} catch (MailException me)
			{
			// logger.error(me.getMessage() + " -- " + me.getCause());
			}
		}

	public void sendAttachmentMessage(METWorksMailMessage message,
			String fileAttachment)
		{
		try
			{
			MimeMessage mimeMsg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true);
			helper.setText(message.getText());
			helper.setFrom(message.getFrom());
			helper.setTo(message.getTo());
			helper.setSubject(message.getSubject());

			FileSystemResource attachment = new FileSystemResource(
					fileAttachment);

			helper.addAttachment(fileAttachment, attachment);
			mailSender.send(message);
			} catch (MailException | MessagingException me)
			{
			// logger.error(me.getMessage() + " -- " + me.getCause());
			}
		}

	public void sendHtmlMessage(METWorksMailMessage message, String headerText)
		{
		try
			{
			MimeMessage mimeMsg = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true,
					"UTF-8");
			helper.setText(convertToHtml(message.getText(), headerText));
			helper.setFrom(message.getFrom());
			helper.setTo(message.getTo());
			helper.setSubject(message.getSubject());

			mailSender.send(mimeMsg);
			} catch (MailException | MessagingException me)
			{
			// logger.error(me.getMessage() + " -- " + me.getCause());
			}
		}

	public String getReportUploadMessage(String user, String assay, String expId)
		{
		return "Client Report uploaded by " + user + " for " + expId + ", assay (" + assay  + ") ";
		}
	
	public String getStandardProtocolUploadMessage(String user, String assay, String sampleType)
		{
		return "New standard protocol uploaded by " + user + " for " + assay + " and sample type " + sampleType;
		}
	
	private String convertToHtml(String text, String headerText)
		{
		String html = "<html><body>" + "<h2>" + headerText + "</h2>" + "<i>"
				+ text + "</i><br>" + "<i>" + text + "</i><br><br>"
				+ "</body></html>";

		return html;

		}

	@Required
	public void setMailSender(JavaMailSender mailSender)
		{
		this.mailSender = mailSender;
		}
	}
