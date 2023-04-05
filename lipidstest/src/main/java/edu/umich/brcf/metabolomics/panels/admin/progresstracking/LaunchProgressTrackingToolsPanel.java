///////////////////////////////////////////////////
// LaunchProgressTrackingToolsPanel.java
// Written by Jan Wigginton, Jul 2015
// Updated by Julie Keros Oct 2020 
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.admin.progresstracking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.metabolomics.layers.service.Mrc2SubmissionDataService;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMailMessage;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.metabolomics.panels.admin.sample_submission.NewSampleEntryPanel;

import edu.umich.brcf.shared.layers.dto.RollbackItemDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.RollbackItemService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentOrDateSearchPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentPlusCountPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentSelectorPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentSelectorWithPopupConfirm;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.panels.utilitypanels.OptimizedBarcodeSelectorPage;
import edu.umich.brcf.shared.util.ModalSizes;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.structures.Pair;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;

public class LaunchProgressTrackingToolsPanel extends Panel
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
	
	public LaunchProgressTrackingToolsPanel(String id) 
		{
		super(id);
		LaunchProgressTrackingForm lde = new LaunchProgressTrackingForm("launchProgressTrackingForm");
		lde.setMultiPart(true);
		add(lde);
		}
		
	// issue 210
	public final class LaunchProgressTrackingForm extends Form 
		{	
		LaunchProgressTrackingForm(String id)
			{
			super(id);
			add(new NewProgressTrackingAdminPanel("submitProgressAdminPanel"));
			add(new NewProgressTrackingPanel("submitProgressPanel"));
			add(new NewProgressReportingPanel("submitReportPanel"));
			add(new NewMeetingPanel("submitMeetingPanel"));
			add(new NewProgressTasksPanel("submitTaskPanel"));
			add(new NewDefaultTrackingAdminPanel("submitDefaultPanel"));
			}			 
 		}  
	}
	
