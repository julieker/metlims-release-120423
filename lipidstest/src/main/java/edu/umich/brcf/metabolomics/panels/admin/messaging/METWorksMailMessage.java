package edu.umich.brcf.metabolomics.panels.admin.messaging;

import org.springframework.mail.SimpleMailMessage;

// JavaMailSender
public class METWorksMailMessage extends SimpleMailMessage
	{
	public METWorksMailMessage()
		{
		super();
		}

	public METWorksMailMessage(String from, String to, String subject, String text)
		{
		super();
		this.setFrom(from);
		this.setTo(to);
		this.setSubject(subject);
		this.setText(text);
		}
	}

