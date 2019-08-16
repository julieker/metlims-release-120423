////////////////////////////////////////////////////
// DatabaseAdminTools.java
// Written by Jan Wigginton, Oct 28, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.database_utility;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.shared.layers.service.UserService;


public class DatabaseAdminTools extends Panel
	{
	@SpringBean
	UserService userService;
	
	@SpringBean
	METWorksMessageMailer mailer;
	
	
	public DatabaseAdminTools(String id) 
		{
		super(id);
		
		FeedbackPanel feedback = new FeedbackPanel("feedback");
		
		DatabaseAdminToolsForm lde = new DatabaseAdminToolsForm("databaseAdminToolsForm");
		lde.setMultiPart(true);
		add(lde);
		}
	
	
	public final class DatabaseAdminToolsForm extends Form 
		{	
		DatabaseAdminToolsForm(String id)
			{
			super(id);
			add(buildPasswordConverterButton("convertPasswordsButton", this));
			}	
		
		
		private IndicatingAjaxButton buildPasswordConverterButton(String id, Form form)
			{
			return new IndicatingAjaxButton("id", form)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					try
						{
				//		List<String> updatedUsers = userService.populateNewUserPasswords();
						
					//	String msg = ListUtils.bulletPrint(updatedUsers, "The following account passwords have been re-encrypted" );
					//	System.out.println(msg);
					//	target.appendJavaScript(StringUtils.makeAlertMessage(msg));
						
						DatabaseAdminTools.this.error("Database updated successfully");
						target.add(DatabaseAdminTools.this.get("feedback"));
					
						}
					catch (Exception e)
						{
						DatabaseAdminTools.this.error("Error while updating database passwords");
						target.add(DatabaseAdminTools.this.get("feedback"));
						}
				
			        }
				};
			}
		}
	}
	
	