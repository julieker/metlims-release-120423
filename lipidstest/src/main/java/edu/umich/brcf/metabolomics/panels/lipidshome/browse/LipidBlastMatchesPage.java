// LipidBlastMatchesPage.java
// Written by Jan Wigginton 05/08/15

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;


import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.LipidBlastPrecursor;
import edu.umich.brcf.metabolomics.layers.domain.LipidMapsClass;
import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.metabolomics.layers.service.LipidBlastPrecursorService;
import edu.umich.brcf.metabolomics.layers.service.LipidMapsEntryService;
import edu.umich.brcf.metabolomics.layers.service.Ms2PeakSetService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.interfaces.IWriteableTextData;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.TextDownloadLink;



public class LipidBlastMatchesPage extends WebPage 
	{
	@SpringBean
	Ms2PeakSetService ms2PeakSetService;
	
	@SpringBean
	LipidMapsEntryService lipidMapsEntryService;
	
	@SpringBean
	LipidBlastPrecursorService lipidBlastPrecursorService;
	 
	
	public LipidBlastMatchesPage()
		{
		super();
		}
	

	public LipidBlastMatchesPage(Page backPage, ModalWindow modal1, IModel<Ms2PeakSet> peakSet) 
		{	
		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		
		LipidInfoForm rlf = new LipidInfoForm("lipidInfoForm", modal1, peakSet);
		add(rlf);
		}
	
	
	public final class LipidInfoForm extends Form 
		{
		static final int maxItemsPerList = 15;
		
		private List<LipidBlastPrecursor> infoList;
		private PageableListView infoListView;
		private String boxTitle = "Lipid Details", boxSubTitle = "";
		private int nonEmptyItems = 0;
		ModalWindow modal2;
		
		public LipidInfoForm(final String id, ModalWindow modal1, IModel<Ms2PeakSet> peakModel) 
			{
			super(id);
			
			modal2 = ModalCreator.createScalingModalWindow("modal2", 1.0, 0.7, (MedWorksSession) getSession());
		
			add(modal2);
			setPageDimensions(modal2);

			add(new FeedbackPanel("feedback"));
			this.setOutputMarkupId(true);
			setMultiPart(true);

			String fullName = peakModel.getObject().getLipidName();
			infoList = buildInfoList(fullName);

			setBoxTitle("LipidBlast Matches : " + fullName);
			setBoxSubTitle("(" + nonEmptyItems +  " Compounds)");
			add(new Label("boxTitle", new PropertyModel<String>(this, "boxTitle")));
			add(new Label("boxSubTitle", new PropertyModel<String>(this, "boxSubTitle")));

			add(infoListView = buildInfoListView("infoListView", modal2)); 
			add(new PagingNavigator("navigator", infoListView));
		//	add(buildDownloadLink(fullName).setOutputMarkupId(true));
			
			IWriteableTextData writer = new LipidBlastMatchesWriter(fullName + ".tsv", infoList);
			add(new TextDownloadLink("downloadData", writer));
			add(new AjaxCancelLink("cancelButton", modal1));
            }
   
		
		private void setPageDimensions(ModalWindow modal1)
			{
			int pageHeight = ((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
			modal1.setInitialHeight(((int) Math.round(pageHeight * 0.9)));
			int pageWidth = ((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();
			modal1.setInitialWidth(((int) Math.round(pageWidth * 0.9)));
			}
		
		private List<LipidBlastPrecursor> buildInfoList(String fullName)
			{
			String adduct = LipidStringParser.getLipidBlastAdductFromLongName(fullName);
			String codeName = LipidStringParser.getLipidBlastCodeNameFromLongName(fullName);
			List<LipidBlastPrecursor> infoList = lipidBlastPrecursorService.getLipidInfoRelatedTo(codeName, adduct);

			nonEmptyItems = infoList.size();
			int rem = (nonEmptyItems % maxItemsPerList);
			if (rem > 0 || infoList.size() == 0)
				for (int i = rem; i < maxItemsPerList; i++)
					infoList.add(new LipidBlastPrecursor("-", "-", "-", "-", "-", "-", "-", "-", new LipidMapsClass("-", "-", "-")));
			
			return infoList;
			}
		
        private PageableListView buildInfoListView(String id, final ModalWindow modal)
        	{
        	return new PageableListView(id, new PropertyModel(this, "infoList"), maxItemsPerList)
				{	
				public void populateItem(ListItem listItem) 
					{
					LipidBlastPrecursor itemInfo =   (LipidBlastPrecursor) listItem.getModelObject();
					String classId = itemInfo.getLipidMapsClass().getClassName();
					
					listItem.add(new Label("lipidId", new PropertyModel<String>(itemInfo, "lipidId") ));
					listItem.add(new Label("fullName", new PropertyModel<String>(itemInfo, "fullName") ));
					listItem.add(new Label("molecularFormula", new PropertyModel<String>(itemInfo, "molecularFormula") ));
					listItem.add(new Label("precursorMz", new PropertyModel<String>(itemInfo, "precursorMz") ));
					listItem.add(new Label("msMode", new PropertyModel<String>(itemInfo, "msMode") ));
					listItem.add(new Label("classCode", new PropertyModel<String>(itemInfo, "classCode") ));
					listItem.add(new Label("formulaMass", new PropertyModel<String>(itemInfo, "formulaMass") ));
					
					listItem.add(new Label("lipidMapsClassName", new PropertyModel<String>(itemInfo, "lipidMapsClass.className") ));
					String subClass = itemInfo.getLipidMapsClass().getClassId();
					String lipidMapsClassName = itemInfo.getLipidMapsClass().getClassName();
					AjaxLink link;
					listItem.add(link = buildLinkToLipidMapsInfo("lipidMapsLink", subClass, lipidMapsClassName, modal));
					link.add(new Label("lipidMapsClass", new PropertyModel<String>(itemInfo, "lipidMapsClass.classId") ));
					
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
        	}
        	
        	
       public List<LipidBlastPrecursor> getInfoList()
	       	{
	       	return this.infoList;
	       	}
           
		@Override
		protected void onSubmit()
    		{
			}
		
		public String getBoxTitle()
			{
			return boxTitle;
			}


		public void setBoxTitle(String boxTitle)
			{
			this.boxTitle = boxTitle;
			}
		

		public String getBoxSubTitle()
			{
			return boxSubTitle;
			}


		public void setBoxSubTitle(String boxSubTitle)
			{
			this.boxSubTitle = boxSubTitle;
			}

		
		public AjaxLink buildLinkToLipidMapsInfo(String id, final String subClass, final String lipidMapsClassName, 
				final ModalWindow modal)
			{
			return new AjaxLink(id)
				{
				 @Override
			       public void onClick(final AjaxRequestTarget target)
			        	{
			        	modal.setPageCreator(new ModalWindow.PageCreator()
			        		 {
			                 public Page createPage()
			                 	{
			                	return new LipidMapsMatchesPage(subClass, lipidMapsClassName, modal);	
			                 	}
			                 });
			             
			        	modal.show(target);
			        	}
					@Override // issue 464
					public MarkupContainer setDefaultModel(IModel model) 
					    {
						// TODO Auto-generated method stub
						return this;
					    }
			    	};
				}
			
			
			}
		}
	
/* 

add(new AjaxLink("cancelButton")
{
public void onClick(AjaxRequestTarget target)
	{
	if (modal1 != null)
		modal1.close(target);
	}					
});

  add(new Button("submitButton")
	            {
	        	public boolean isVisible()
	        		{
	        		return true;
	        		}
	        	
	        	public boolean isEnabled()
	        		{
	        		return true; //(fileUploadField.getFileUpload().getClientFileName() != null); // getSelected().equals("Client Report");
	        		}
	            });
*/