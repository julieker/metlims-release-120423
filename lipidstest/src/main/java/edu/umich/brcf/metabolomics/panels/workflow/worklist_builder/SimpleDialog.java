package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;


import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
// issue 46
public abstract class SimpleDialog extends AbstractDialog<String>
    {
	public MultiLineLabel multiInvalidSamplesLabel ;
	private static final long serialVersionUID = 1L;
   // String message = "The tbd message a very very long long message message message message message message message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message messagemessage message message message";
	public SimpleDialog(String id, String title, Model<String> userDefinedSamplesModel)
	    {		
		super(id, title);
		multiInvalidSamplesLabel = new MultiLineLabel("message", userDefinedSamplesModel);
		add( multiInvalidSamplesLabel);
	    }

	@Override
	public boolean isResizable()
	    {
		return true;
	    }

	@Override
	protected List<DialogButton> getButtons()
	    {
		return DialogButtons.OK_CANCEL.toList(); //this syntax is allowed until the button state (enable and/or visible) is not altered
	    }
    }



 