////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  ControlInfoPage.java
//  Written by Jan Wigginton
//  March 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public class ControlInfoPage extends WebPage
	{
	public ControlInfoPage() { super(); }

	public ControlInfoPage(Page backPage, ModalWindow modal1)
		{
		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		ControlInfoForm rlf = new ControlInfoForm("controlInfoForm", modal1);
		add(rlf);
		}

	
	public class ControlInfoForm extends Form
		{
		public ControlInfoForm(final String id, final ModalWindow modal1)
			{
			this(id, "", modal1, "", "");
			}

		
		public ControlInfoForm(final String id, final String associated, final ModalWindow modal1, String associated2, String associated3)
			{
			super(id);
			add(new AjaxCancelLink("cancelButton", modal1));
			}

		@Override
		protected void onSubmit() { }
		}
	}