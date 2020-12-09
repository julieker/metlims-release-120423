///////////////////////////////////////////////////
// LaunchMixtureToolsPanel.java
// created by Julie Keros Nov 2020 
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.admin.sample_submission;


import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.metabolomics.layers.service.Mrc2SubmissionDataService;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.RollbackItemService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;

public class LaunchMixtureToolsPanel extends Panel
	{
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	UserService userService;

	@SpringBean 
	SystemConfigService systemConfigService;
	
	@SpringBean
	METWorksMessageMailer mailer;
	 
	@SpringBean 
	RollbackItemService rollbackItemService;
	 
	@SpringBean
	Mrc2SubmissionDataService mrc2SubmissionDataService;
	 
	@SpringBean
	SampleService sampleService;
	
	// issue 86
	@SpringBean
	AliquotService aliquotService;
	
	public LaunchMixtureToolsPanel(String id) 
		{
		super(id);
		
		LaunchMixtureToolsForm lde = new LaunchMixtureToolsForm("launchMixtureToolsForm");
		lde.setMultiPart(true);
		add(lde);
		}
		
	public final class LaunchMixtureToolsForm extends Form 
		{	
		LaunchMixtureToolsForm(String id)
			{
			super(id);

			final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 250, 250);
			add(modal1);
			final METWorksPctSizableModal modal2 = new METWorksPctSizableModal("modal2", 0.75, 0.99);
			add(modal2);
			add(new NewMixtureEntryPanel("uploadMixtures")); // issue 94
			}		
	
		}
	}
	
