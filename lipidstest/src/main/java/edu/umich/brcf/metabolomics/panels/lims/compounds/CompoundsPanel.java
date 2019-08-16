
package edu.umich.brcf.metabolomics.panels.lims.compounds;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


public class CompoundsPanel extends Panel 
	{
	public CompoundsPanel(String id) 
		{
		super(id);
		add(new CompoundDetail("compoundDetail"));
		}
	
	
	 public CompoundsPanel(String id, IModel model) 
		 {
		super(id, model);
		 }
	}
