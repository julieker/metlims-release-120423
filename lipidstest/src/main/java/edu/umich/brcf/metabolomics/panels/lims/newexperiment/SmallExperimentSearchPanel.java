////////////////////////////////////////////////////
// SmallExperimentSearchPanel.java
// Written by Jan Wigginton, Sep 8, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newexperiment;

import java.util.HashMap;
import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.metabolomics.panels.lims.newexperiment.NewExperimentPanel;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.panels.utilitypanels.experimentsearch.ExperimentSearchByAnythingPage;
import edu.umich.brcf.shared.panels.login.MedWorksSession;

public class SmallExperimentSearchPanel extends Panel 
		{
		@SpringBean
		private SampleService sampleService;
		
		@SpringBean 
		private ExperimentService experimentService;
		
		@SpringBean 
		private ProjectService projectService;
		
		private NewExperimentPanel expPanel;
		private Label progressLabel;
		
		private String progressMessage = "", searchLabel = "newExperimentSearch";//                          *************** Experiment search active ***************";
		IndicatingAjaxLink experimentSearchBtn;
		WebMarkupContainer container;
		ExperimentSearchByAnythingPage experimentSearchByAnythingPage;
		SmallExperimentSearchPanel smallExperimentSearchPanel = this;
		
		public SmallExperimentSearchPanel(String id) 
			{
			super(id);
			setOutputMarkupId(true);
			add(new FeedbackPanel("feedback"));
			((MedWorksSession) Session.get()).setExpProjmap(new  HashMap<String, List <Experiment> >());
					
			ExperimentSearchForm form;
			add(form = new ExperimentSearchForm("experimentSearchForm"));
			form.setOutputMarkupId(true);
			}
		
		
		public final class ExperimentSearchForm extends Form 
			{
			public ExperimentSearchForm(final String id)
				{
				super(id);
				setOutputMarkupId(true);
				
				container = new WebMarkupContainer("container");
				container.setOutputMarkupId(true);
				add(container);
				
				final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 650, 400);
				modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
					{
					@Override
					public void onClose(AjaxRequestTarget target)  
						{
						target.add(progressLabel);
						String val = (String) ((MedWorksSession) Session.get()).getSaveValue();
						List<String> pList = ((MedWorksSession) Session.get()).getSaveValues();
						expPanel.setExperiment(val == null ?  null : experimentService.loadByIdWithProject(val));
						expPanel.updateProjectList(val, pList);
						expPanel.renewPage(target);
						expPanel.renewPanels(target,  val);
						experimentSearchBtn.setEnabled(true);
						progressLabel.setEnabled(false);
						target.add(container);
						}
					});				
				container.add(modal1); 				
				final ModalWindow modal2 = ModalCreator.createModalWindow("modal2", 650, 400);
				modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
					{
					@Override
					public void onClose(AjaxRequestTarget target)  
						{
						experimentSearchBtn.setEnabled(true);
						target.add(experimentSearchBtn);
						}
					});
							
				container.add(modal2);				
				container.add(progressLabel = new Label("progressLabel", new PropertyModel<String>(this, "progressMessage"))
					{
					@Override
					protected void onComponentTag(ComponentTag tag)
			    		{
			    		super.onComponentTag(tag);
			    		if (this.isEnabled())
			    			tag.put("style", "color : red");
			    		else tag.put("style", "color : white");
			    		}	
					});
				progressLabel.setOutputMarkupId(true);
				progressLabel.setEnabled(false);				
				container.add(buildLinkToModal("createExperiment",modal2).setOutputMarkupId(true));
				container.add(experimentSearchBtn = buildLinkToModal("newExperimentSearch", modal1));
				experimentSearchBtn.setOutputMarkupId(true);
				container.add(expPanel = new NewExperimentPanel("expPanel",  null, null, null));
				expPanel.setOutputMarkupId(true);
				}
	
		private IndicatingAjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1) 
			{
			// issue 39
			return new IndicatingAjaxLink <Void> (linkID)
				{
				@Override
				public void onClick(final AjaxRequestTarget target)  
					{ 
					if (this.getId().equals(searchLabel))
						{
						progressLabel.setEnabled(true);  
						experimentSearchBtn.setEnabled(false);
						}
					
					target.add(experimentSearchBtn); 
					target.add(progressLabel);
					target.add(container);
					doClick(modal1, target, linkID); 
					}
					
				@Override
				protected void onComponentTag(ComponentTag tag)
		    		{
		    		super.onComponentTag(tag);
		    		if (this.getId().equals(searchLabel))
		    			{	
		    			if (this.isEnabled())
		    				tag.put("value", "Experiment Search...");
			    		else 
			    			{
			    			tag.put("style", "color : red; font-weight : 700");
			    			tag.put("value", "Search Pending...");
			    			}
		    			}
		    		}
				};
			}
		
		
		public void doClick(final ModalWindow modal1, final AjaxRequestTarget target, final String linkID)
			{
			modal1.setPageCreator(new ModalWindow.PageCreator()
				{
				public Page createPage()
					{
					switch (linkID)
						{
						case "createExperiment" :  { return buildEditExperiment(modal1); }
							
						default : 
							//experimentSearchByAnythingPage = new ExperimentSearchByAnythingPage(getPage());
							return experimentSearchByAnythingPage  = new ExperimentSearchByAnythingPage(getPage())
								{
								@Override
								// issue 187
								protected void doBusiness(String expId, List<String> projIds)
									{	
									((MedWorksSession) Session.get()).setSaveValue(expId);
									((MedWorksSession) Session.get()).setSaveValues(projIds);
									((MedWorksSession) Session.get()).setExpProjmap(new  HashMap<String, List <Experiment> >());
									if (experimentSearchByAnythingPage.experimentSearchForm.getSearchTypeOuter().equals("Assay Id") && projIds.size() > 0 )
										{	
										for (String lilProjId: projIds)
											{										
											if (!experimentSearchByAnythingPage.assayPanel.grabAssayLabelForm.getUseDate())
											    ((MedWorksSession) Session.get()).getExpProjmap().put(lilProjId,  experimentService.loadExpExperimentProjectByAssay(experimentSearchByAnythingPage.assayPanel.getAssay(), lilProjId));
											else 
												 ((MedWorksSession) Session.get()).getExpProjmap().put(lilProjId,  experimentService.loadExpExperimentProjectByAssay(experimentSearchByAnythingPage.assayPanel.getAssay(), lilProjId,experimentSearchByAnythingPage.assayPanel.grabAssayLabelForm.getCreateDate(), experimentSearchByAnythingPage.assayPanel.grabAssayLabelForm.getCreateDateTo()));
											}
										String firstProject = projIds.get(0);
										String firstExperiment = ((MedWorksSession) Session.get()).getExpProjmap().get(firstProject).get(0).getExpID();
										((MedWorksSession) Session.get()).setSaveValue(firstExperiment);
										}
									}
								};
						}
					}
				});
			
			int initWidth, initHeight;
			switch (linkID)
				{
				case "createExperiment" :  initWidth = 600; initHeight = 650; break;
				// issue 187
				case "newExperimentSearch" : initWidth = 800; initHeight = 300; break;
				default : initWidth = 625; initHeight = 230; break;
				}
			
			modal1.setInitialHeight(initHeight);
			modal1.setInitialWidth(initWidth);
			modal1.show(target);
			}
			
		
		EditExperiment buildEditExperiment(final ModalWindow modal1)
			{
			return (new EditExperiment(getPage(), modal1)
				{
				@Override
				protected void onSave(final Experiment exp, AjaxRequestTarget target1) 
					{
					modal1.close(target1);
					}
				});
			}
		
		
		public String getProgressMessage()
			{
			return progressMessage;
			}


		public void setProgressMessage(String p)
			{
			progressMessage = p;
			}
		}
	}
			
		
		
		
		
