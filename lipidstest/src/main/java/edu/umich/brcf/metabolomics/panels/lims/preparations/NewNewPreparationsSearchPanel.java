package edu.umich.brcf.metabolomics.panels.lims.preparations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.utility.PrepSearchByClient;
import edu.umich.brcf.metabolomics.panels.utility.PrepSearchByCreator;
import edu.umich.brcf.metabolomics.panels.utility.PrepSearchByDate;
import edu.umich.brcf.metabolomics.panels.utility.PrepSearchByExperiment;
import edu.umich.brcf.metabolomics.panels.utility.PrepSearchByPrep;
import edu.umich.brcf.metabolomics.panels.utility.SearchByAssay;
import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;



public class NewNewPreparationsSearchPanel extends Panel 
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	TabbedPanel tabbedPanel; 

	NewNewPreparationsSearchPanel pp;
	NewPreparationsListPanel sidePanel;
	NewPrepDetailHeader pdh;

	String preparation= "SP00606";

	public void setSamplePrepService(SamplePrepService samplePrepService) 
		{
		this.samplePrepService = samplePrepService;
		}
	
	public NewNewPreparationsSearchPanel(String id) 
		{
		super(id);
		pp=this;
		setOutputMarkupId(true);
        add(new FeedbackPanel("feedback"));
		add(new NewPreparationForm("preparationForm"));
		}
	
	
	public final class NewPreparationForm extends Form 
		{
		public NewPreparationForm(final String id) 
			{
			super(id);
			
			
			METWorksPctSizableModal modal2= new METWorksPctSizableModal("modal2", .35, .2);
	        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        	{
	            public void onClose(AjaxRequestTarget target)
	            	{
	            	String date = DateUtils.dateAsString(new Date(), "MM/dd/yyyy");
	            	updatePanels(preparation);
		        	target.add(pp);
		        	target.add(pdh);
        			target.add(tabbedPanel);
        			target.add(sidePanel);
		        	tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
	            	}
	        	});
	        add(modal2);

	        add(sidePanel = buildSidePanel("sidePanel"));
	        sidePanel.setOutputMarkupId(true);
	        
	        add(buildLinkToModal("creatorSearchLink", modal2));
	        add(buildLinkToModal("dateSearchLink", modal2));
	        add(buildLinkToModal("experimentSearchLink", modal2));
	        add(buildLinkToModal("prepSearchLink", modal2));
	        add(buildLinkToModal("createPrepLink", modal2));
	        add(buildLinkToModal("assaySearchLink", modal2));
	        add(buildLinkToModal("sampleSearchLink", modal2));
	        
			final List tabs=new ArrayList();
			
			tabs.add(new AbstractTab(new Model("Prep Details")) 
				{
		 		public Panel getPanel(String panelId)
		 		   	{
		 			return (new NewPreparationDetailPanel(panelId, getPreparation()));
		 		   	}
		 		});
			 
			tabs.add(new AbstractTab(new Model("Edit Prep Methods")) 
				{
	 		   	public Panel getPanel(String panelId)
	 		   		{
	 		   		return (new NewNewPrepMethodsPanel(panelId, getPreparation()));
	 		   		}
				});
		   
		    add(tabbedPanel=new TabbedPanel("tabs", tabs));
	        tabbedPanel.setSelectedTab(0);
			tabbedPanel.setOutputMarkupId(true);

			add(pdh=new NewPrepDetailHeader("pdh", new CompoundPropertyModel(null)));
			pdh.setOutputMarkupId(true);
			
		    updatePanels(null); 
			}	

		private NewPreparationsListPanel buildSidePanel(String id)
			{
			return (new NewPreparationsListPanel(id)
		   		{
				@Override
				public boolean isVisible()
					{
					return (preps.size() > 1);
					}
				
		   		@Override
		   		protected void onIdClick(String prep, AjaxRequestTarget target) 
		   			{
		   			setPreparation(prep);
	            	updatePanels(preparation);
		        	target.add(pp);
		        	target.add(pdh);
        			target.add(tabbedPanel);
        			target.add(sidePanel);
		        	tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
		   			}
		   		});
			}
		}
	
	public void setPreparation(String preparation)
		{
		this.preparation=preparation;
		}
	
	public String getPreparation()
		{
		return preparation;
		}
	
	public void updatePanels(String preparation)
		{
		System.out.println("Getting prep" + getPreparation());
		tabbedPanel.setVisible((preparation!=null));
		if(preparation!=null)pdh.setDefaultModel(new CompoundPropertyModel(samplePrepService.loadPreparationByID(getPreparation())));
		pdh.setVisible((preparation!=null));
		}
	
	private Iterator getSamplePrepChoices(String input)
		{
		List<String> choices = new ArrayList();
		for (Preparation prep : samplePrepService.allSamplePreparations()) 
			{
			final String prepTitle = prep.getTitle();
			if (prepTitle.toUpperCase().contains(input.toUpperCase()))
				choices.add(prepTitle + " (" + prep.getPrepID()+")");
			}
		
		return choices.iterator();
		}
	
	
	private String parseValue(String temp)
		{
		if (temp.indexOf("(")>0)
			temp = temp.substring(temp.lastIndexOf("(") + 1, temp.lastIndexOf(")"));
		
		return temp;
		}
	
	private boolean verifyFormat(String format, String input) {
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
	}
	
	
	
	PrepSearchByPrep buildSearchByPrep(final ModalWindow modal1)
	{
	return (new PrepSearchByPrep(getPage(), this)
		{
		@Override
		protected void onSave(String prep, AjaxRequestTarget target1) 
			{
			modal1.close(target1);
			setPreparation(StringParser.parseId(prep));
			sidePanel.setPrepsByString(Arrays.asList(new String [] { prep }));
			}
		});
	}


PrepSearchByExperiment buildSearchByExperiment(final ModalWindow modal1)
	{
	return (new PrepSearchByExperiment(getPage(), this)
		{
		@Override
		protected void onSave(String prep, AjaxRequestTarget target1) 
			{
			modal1.close(target1);
			//sidePanel.set
			//setProjectList(samplePrepService.loadProjectExperimentByPrep(prep));
			//updatePanels(getProjectList());
			//setExperiment(null);
			}
		});
	}

SearchByAssay buildSearchByAssay(final ModalWindow modal1)
	{
	return new SearchByAssay(getPage())
		{
		@Override
		protected void onSave(String assay, AjaxRequestTarget target1) 
			{
			modal1.close(target1);
			}
		};
	}

/*	ExperimentSearchBySample buildSearchBySample(final ModalWindow modal1)
		{
		return new ExperimentSearchBySample(getPage())
			{
			@Override
			protected void onSave(String prep, AjaxRequestTarget target1) 
				{
				modal1.close(target1);
				}
			};
		} */

	PrepSearchByClient buildSearchByClient(final ModalWindow modal1)
		{
		return (new PrepSearchByClient(getPage())
			{
			@Override
			protected void onSave(String prep, AjaxRequestTarget target1) 
				{
				modal1.close(target1);
				//setProjectList(samplePrepService.loadProjectExperimentByPrep(prep));
				//updatePanels(getProjectList());
				//setExperiment(null);
				}
			});
		}

	PrepSearchByCreator buildSearchByCreator(final ModalWindow modal1)
		{
		return (new PrepSearchByCreator(getPage())
			{
			@Override
			protected void onSave(String creator, AjaxRequestTarget target) 
				{
				String creatorName = StringParser.parseName(creator);
				String creatorId = StringParser.parseId(creator);
				boolean grabAll = "U00000".equals(creatorId);
				
				List<Preparation> prepsByCreator;
				
				if (grabAll)
					prepsByCreator = samplePrepService.allSamplePreparations();
				else
				    prepsByCreator = samplePrepService.loadByCreatorId(StringParser.parseId(creator));
				
				
				if (prepsByCreator.size() > 0)
					processResultList(prepsByCreator, target);
				else
					target.appendJavaScript("alert('No preps found for creator " + creatorName + "');");
				
				modal1.close(target);
				}
			});
		}

	protected void processResultList(List <Preparation> list, AjaxRequestTarget target)	
		{
		if (list.size() > 0)
			setPreparation(list.get(0).getPrepID());
		sidePanel.setPrepsByPrepList(list);
		target.add(sidePanel);
		}

	PrepSearchByDate buildSearchByDate(final ModalWindow modal1)
		{
		return (new PrepSearchByDate(getPage())
			{
			@Override
			protected void onSave(String prep, AjaxRequestTarget target1) 
				{
				modal1.close(target1);
				}
			});
		}

	NewDataEntryTypePage buildCreatePrep(final METWorksPctSizableModal modal1)
		{
		return (new NewDataEntryTypePage(getPage(), (ModalWindow) modal1)
			{
			@Override
			protected void onSave(String choice1, String choice2, String prepTitle) 
				{
				if (choice1.equals("Manual"))
					setResponsePage(new NewEditPrepPlate(getPage(), prepTitle, pp, choice2));
				else
					setResponsePage(new NewPrepSheetUpload(getPage(), prepTitle, pp, choice2));
				}
			});
		}	

	private AjaxLink buildLinkToModal(final String linkID, final METWorksPctSizableModal modal1) 
		{
		return new AjaxLink(linkID)
	    	{
	        @Override
	        public void onClick(final AjaxRequestTarget target)
	        	{
	        	doClick(modal1, target, linkID);
	        	}
	    	@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		}
	
	
	public void doClick(final METWorksPctSizableModal modal2, AjaxRequestTarget target, final String linkID)
		{
		boolean isCreate = (linkID != null && "createPrepLink".equals(linkID));
		modal2.setPageDimensions(isCreate ? 0.9 : 0.35, isCreate ? 0.7 : 0.2);
	
		modal2.setPageCreator(new ModalWindow.PageCreator()
	        {
	        public Page createPage()
	        	{
	        	switch(linkID)
		        	{
		        	case "prepSearchLink":
		        		return buildSearchByPrep(modal2);
		        	case "experimentSearchLink" :
		        		return buildSearchByExperiment(modal2);
		        	case "clientSearchLink" :
		        		return buildSearchByClient(modal2);
		        	case "dateSearchLink" :
		        		return buildSearchByDate(modal2);
		        	case "creatorSearchLink" : 
		           		return buildSearchByCreator(modal2);
		        	case "assaySearchLink" : 
		           		return buildSearchByAssay(modal2);
		     //   	case "sampleSearchLink" : 
		     //      		return buildSearchBySample(modal2);
		        	default :
		           		return buildCreatePrep(modal2);
		        	}
	        	}
	        });
		modal2.show(target);
		}
	}



///////////////SCRAP CODE ////////////
//			String creator = ((METWorksSession) Session.get()).getCurrentUserFirstName()+" "+((METWorksSession) Session.get()).getCurrentUserLastName();
//SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//String prepDate = sdf.format(Calendar.getInstance().getTime());
//add(new Label("creator", creator));
//add(new Label("prepDate", prepDate));



/*
private AjaxLink buildLinkToViewPlate(String linkID, final TextField title, final ModalWindow modal1, final NewPreparationSearchPanel pp) 
	{
	AjaxLink link=new AjaxLink(linkID)
    	{
        @Override
        public void onClick(AjaxRequestTarget target)
        	{
        	final String titleTxt=title.getValue().trim();
        	
        	if ((titleTxt!=null)&&(titleTxt.length()>0))
        		{
        		String temp=parseValue(titleTxt);
        		if(verifyFormat("(SP)\\d{5}",temp))
        			{
        			setPreparation(temp);
        			updatePanels(temp);
        			target.add(pdh);
        			target.add(tabbedPanel);
        			tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
        			}
        		
        		}
        	else
        		{
        		setPreparation(null);
        		updatePanels(null);
        		getParent().error("Please enter a Prep Title! ");
        		}
        	target.add(pp);
        	}
    	};
    return link;
	}

*/
/*
private AjaxLink buildLinkToAddModal(String linkID, final TextField title, final ModalWindow modal1, final NewNewPreparationsSearchPanel pp) 
	{
	AjaxLink link =	new AjaxLink(linkID)
    	{
        @Override
        public void onClick(AjaxRequestTarget target)
        	{
           	final String titleTxt=title.getValue().trim();
            
			modal1.setPageCreator(new ModalWindow.PageCreator()
				{
				public Page createPage()
					{
					return (new DataEntryTypePage(getPage())
                		{
						@Override
						protected void onSave(String choice1, String choice2) {
						if (choice1.equals("Manual"))
							setResponsePage(new NewEditPrepPlate(getPage(), titleTxt, pp, choice2));
						else
							setResponsePage(new NewPrepSheetUpload(getPage(), titleTxt, pp, choice2));
						}
                		});
					}
				});
        			
			tabbedPanel.setSelectedTab(0);
        	modal1.show(target);
        	}
    	};
    return link;
	}
	
	
		/*
			final ModalWindow modal1= new ModalWindow("modal1");
			modal1.setInitialWidth(900);
		    modal1.setInitialHeight(550);
		    modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		    	{
		        public void onClose(AjaxRequestTarget target)
		        	{
		    		System.out.println("Getting prep in callback" + getPreparation());

		        	updatePanels(getPreparation());
		        	target.add(pp);
		        	target.add(pdh);
        			target.add(tabbedPanel);
        			String firstPrep = getPreparation();
        			List <String> lst = Arrays.asList(new String [] { firstPrep });
        			sidePanel.setPreps(lst);
        			target.add(sidePanel);
		        	tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
		        	}
		    	});
		    add(modal1);
		    */
		    
		   // AjaxLink viewExistingPrep, addNewPrep;
		   // add(viewExistingPrep = buildLinkToViewPlate("viewExistingPrep",title, modal1, pp));
		    //add(addNewPrep = buildLinkToAddModal("addNewPrep",title, modal1, pp));
		    
		    
		   // title.add(new AjaxFormSubmitBehavior(this, "change") 
		   // 	{
			//	protected void onSubmit(AjaxRequestTarget target) 
			//		{
			//		target.add(modal1);
			//		}
			//		@Override
			//		protected void onError(AjaxRequestTarget target) {
			//		}
		    //	});

/*tabs.add(new AbstractTab(new Model("Prep List")) 
{
	public Panel getPanel(String panelId)
		{
	   	return (new NewPreparationsListPanel(panelId)
	   		{
	   		@Override
	   		protected void onIdClick(String prep, AjaxRequestTarget target) 
	   			{
	   			tabbedPanel.setSelectedTab(1);
	   			target.add(tabbedPanel);
	   			setPreparation(prep);
	   			updatePanels(prep);
			target.add(pdh);
//				target.add(psp.tabbedPanel);
//				psp.tabbedPanel.setSelectedTab(0);
//				target.add(psp);
			}
	   		});
		}
});
*/



