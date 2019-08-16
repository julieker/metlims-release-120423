// LipidImagePage.java
// Written by Jan Wigginton 05/08/15

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.awt.Graphics2D;
	
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.image.resource.RenderedDynamicImageResource;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.panels.login.MedWorksSession;

//import chemaxon.formats.MolImporter;
//import chemaxon.marvin.MolPrinter;
//import chemaxon.struc.Molecule;
//import edu.umich.metworks.web.METWorksSession;


class LipidImagePage extends WebPage
	{
	String smiles; 
	double pageHeight, pageWidth;
	int intHeight, intWidth;
	/*
	public LipidImagePage(String smiles, String commonName, ModalWindow modal1) 
		{	
		this.smiles = smiles;
		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		//modal1.setTitle(new PropertyModel(this, "boxTitle"));
	
		pageHeight = ((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
		intHeight = (int) Math.round(pageHeight * 0.8);
		modal1.setInitialHeight(intHeight);
		
		pageWidth = ((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();
		intWidth = (int) Math.round(pageWidth * 0.8);
		modal1.setInitialWidth((int) Math.round(intWidth));
		
		add(new FeedbackPanel("feedback"));
		this.setOutputMarkupId(true);
		add(new Label("pageTitle", "Structure for " + commonName));
		
		add(new NonCachingImage("structure", new PropertyModel<StructureDynamicImageResource>(this, "structure")));
	}
	
	public StructureDynamicImageResource getStructure()
		{
		return (new StructureDynamicImageResource(intWidth, intHeight, smiles));
		}
	
	private final class StructureDynamicImageResource extends RenderedDynamicImageResource
	{
	private String smiles;
	private StructureDynamicImageResource(int width, int height, String smiles)
		{
		super(width, height);
		this.smiles=smiles;
		}

	protected boolean render(Graphics2D graphics)
		{
		try {
		//	drawImage(graphics, smiles); 
			} 
		catch (Exception e) { e.printStackTrace(); }
		
		return true;
		} */
	}



	
/*private void drawImage(Graphics2D graphics, String smiles) throws IOException {
    Molecule mol = MolImporter.importMol(smiles);
    //BufferedImage im = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = graphics;//im.createGraphics();
    g.setColor(Color.white);
    g.fillRect(0, 0, intWidth, intHeight);
    g.setColor(Color.red);
    Rectangle r = new Rectangle(30, 30, intWidth - 30, intHeight - 30);
   // g.draw(r);
    MolPrinter p = new MolPrinter(mol);
    p.setScale(p.maxScale(r)); // fit image in the rectangle
    p.paint(g, r);
	} */
//}

