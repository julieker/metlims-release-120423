// LipidMapsMatchesPage.java
// Written by Jan Wigginton 05/26/15

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
//import org.apache.wicket.markup.html.image.resource.RenderedDynamicImageResource;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.LipidMapsEntry;
import edu.umich.brcf.metabolomics.layers.service.LipidBlastPrecursorService;
import edu.umich.brcf.metabolomics.layers.service.LipidMapsEntryService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.interfaces.IWriteableTextData;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.TextDownloadLink;

//import chemaxon.formats.MolImporter;
//import chemaxon.marvin.MolPrinter;
//import chemaxon.struc.Molecule;
//import edu.emory.mathcs.backport.java.util.Arrays;



public class LipidMapsMatchesPage extends WebPage 
	{
	@SpringBean
	LipidMapsEntryService lipidMapsEntryService;
	
	@SpringBean
	LipidBlastPrecursorService lipidBlastPrecursorService;
		
	
	public LipidMapsMatchesPage()
		{
		super();
		}
	

	public LipidMapsMatchesPage(String subClass, String lipidMapsClassName, ModalWindow modal1) 
		{	
		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		LipidMapsMatchesForm rlf = new LipidMapsMatchesForm("lipidInfoForm", modal1, subClass, lipidMapsClassName);
		add(rlf);
		}
	
	
	public final class LipidMapsMatchesForm extends Form 
		{
		private List<LipidMapsEntry> infoList;
		private PageableListView infoListView;
		String boxTitle = "Lipid Map Matches", boxSubTitle  = "";
		static final int maxItemsPerList = 6;
		
		public LipidMapsMatchesForm(final String id, final ModalWindow modal1, String subClass, String lipidMapsClassName) 
			{
			super(id);
			
			setPageDimensions(modal1);
			
			this.setOutputMarkupId(true);
			
			//add(new NonCachingImage("structure", new PropertyModel(this, "structure")));
			setMultiPart(true);
        
			infoList = lipidMapsEntryService.loadAllForSubClass(subClass);
			
			boxTitle = "Lipid Maps Subclass " + subClass;
			boxSubTitle = "(" + lipidMapsClassName + ") : " + infoList.size() +  " Compounds";
			add(new Label("boxTitle", new PropertyModel<String>(this, "boxTitle")));
			add(new Label("boxSubTitle", new PropertyModel<String>(this, "boxSubTitle")));
		
			int nonEmptyItems = infoList.size();
			int rem = (nonEmptyItems % maxItemsPerList);
			if (rem > 0 || infoList.size() == 0)
				for (int i = rem; i < maxItemsPerList; i++)
					infoList.add(new LipidMapsEntry("-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-"));
					
		
			List <LipidMapsEntry> itemsToPrint = new ArrayList <LipidMapsEntry>(infoList.subList(0, nonEmptyItems));
			IWriteableTextData writer = new LipidMapsMatchesWriter("Lipid_Maps_Matches_" + lipidMapsClassName + "_" + subClass  + ".tsv", itemsToPrint);
			add(new TextDownloadLink("downloadData", writer));
			
			
			add(infoListView = buildInfoListView("infoListView", modal1)); 
			add(new PagingNavigator("navigator", infoListView));
			add(new AjaxCancelLink("cancelButton", modal1));
	        }

		
	private void setPageDimensions(ModalWindow modal1)
		{
		int pageHeight = ((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
		modal1.setInitialHeight((int) Math.round(pageHeight * .6)) ;
		int pageWidth = ((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();
		modal1.setInitialWidth(pageWidth);
		}
	
	
	
     private PageableListView buildInfoListView(String id, final ModalWindow modal)
     	{
     	return new PageableListView(id, new PropertyModel(this, "infoList"), maxItemsPerList)
			{	
			public void populateItem(ListItem listItem) 
				{
				LipidMapsEntry itemInfo =   (LipidMapsEntry) listItem.getModelObject();
				
				listItem.add(new Label("lipidMapsId", new PropertyModel<String>(itemInfo, "lipidMapsId") ));
				listItem.add(new Label("mainClass", new PropertyModel<String>(itemInfo, "mainClass") ));
				listItem.add(new Label("subClass", new PropertyModel<String>(itemInfo, "subClass") ));
				listItem.add(new Label("classLevel4", new PropertyModel<String>(itemInfo, "classLevel4") ));
				listItem.add(new Label("commonName", new PropertyModel<String>(itemInfo, "commonName") ));
				listItem.add(new Label("systematicName", new PropertyModel<String>(itemInfo, "systematicName") ));
				listItem.add(new Label("category", new PropertyModel<String>(itemInfo, "category") ));
				listItem.add(new Label("smiles", new PropertyModel<String>(itemInfo, "smiles") ));
				listItem.add(new Label("inchiKey", new PropertyModel<String>(itemInfo, "inchiKey") ));
				listItem.add(new Label("molecularFormula", new PropertyModel<String>(itemInfo, "molecularFormula") ));
				
				String smiles = itemInfo.getSmiles();
				String commonName = itemInfo.getCommonName();
			
				//AjaxLink link;
				//listItem.add(link = buildLinkToImage("imageLink", smiles, commonName, modal));
				//listItem.add(new Label("smiles", new PropertyModel<String>(itemInfo, "smiles") ));
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			};
     	}
     	
     	
     public AjaxLink buildLinkToImage(String id, final String smiles, final String commonName, final ModalWindow modal)
			{
    	 // issue 39
			return new AjaxLink <Void>(id)
				{
				public boolean isVisible()	
					{
					return true;
					}

				public boolean isEnabled()
					{
					return true;
					}
			
				 @Override
			       public void onClick(final AjaxRequestTarget target)
			        	{
			        	modal.setPageCreator(new ModalWindow.PageCreator()
			        		 {
			                 public Page createPage()
			                 	{
			                	//Page pg = ((Page) (new LipidImagePage(smiles, commonName, modal)));	
			                	
			                	return null;
			                 	}
			                 });
			             
			        	modal.show(target);
			        	}
			    	};
				}
			

    public List<LipidMapsEntry> getInfoList()
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
	//	public StructureDynamicImageResource getStructure()
	//		{
	//		return null;
		//	return (new StructureDynamicImageResource(350, 350, "P(=O)(OC[C@H](COP(=O)(O)OC[C@H](OC(=O)*)COC(=O)*)O)(O)OC[C@H](OC(=O)*)COC(=O)*"));
	//		}

		}
	
//	private final class StructureDynamicImageResource extends RenderedDynamicImageResource
//		{
//		private String smiles;
//		private StructureDynamicImageResource(int width, int height, String smiles)
//			{
//			super(width, height);
//			this.smiles=smiles;
//			}

/*		protected boolean render(Graphics2D graphics)
			{
			try {
				drawImage(graphics, smiles); 
				} 
			catch (IOException e) { e.printStackTrace(); }
			
			return true;
			}
		} */

	
	private void drawImage(Graphics2D graphics, String smiles) throws IOException 
		{
	  /*  Molecule mol = MolImporter.importMol(smiles);
	    //BufferedImage im = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = graphics;//im.createGraphics();
	    g.setColor(Color.white);
	    g.fillRect(0, 0, 350, 350);
	    g.setColor(Color.red);
	    Rectangle r = new Rectangle(20, 20, 300, 300);
	    g.draw(r);
	    MolPrinter p = new MolPrinter(mol);
	    p.setScale(p.maxScale(r)); // fit image in the rectangle
	    p.paint(g, r); */
		}
	}

