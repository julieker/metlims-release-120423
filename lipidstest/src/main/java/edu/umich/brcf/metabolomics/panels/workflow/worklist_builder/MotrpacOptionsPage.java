////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  CustomizeControlGroupPage.java
//  Written by Jan Wigginton
//  March 2019
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class MotrpacOptionsPage extends WebPage
	{
	
	public MotrpacOptionsPage(final ModalWindow modal)
		{
		add(new FeedbackPanel("feedback"));
	add(new MotrpacOptionsForm("motrpacOptionsForm", modal));
	}

public final class MotrpacOptionsForm extends Form 
	{
	// issue 422
	Integer nGastroExercise = 1, nGastroSedentary = 1,nLiverExercise = 1,
			 nLiverSedentary = 1,nAdiposeExercise = 1,nAdiposeSedentary = 1,
			 nPlasmaExercise = 1,
			 nPlasmaSedentary = 1,
			 nRatPlasma = 1,
			 nRatG = 1, 
			 nRatL = 1,
			 nRatA = 1;
	
    Integer nLungExercise = 1;
    Integer nLungSedentary = 1;
    Integer nKidneyExercise = 1;
    Integer nKidneySedentary = 1;
    Integer nHeartExercise = 1;
    Integer nHeartSedentary = 1;
    Integer nBrownAdiposeExercise  = 1;
    Integer nBrownAdiposeSedentary = 1;
    Integer nHippoCampusExercise = 1;
    Integer nHippoCampusSedentary = 1;          
	
	protected List<Integer> countOptions = Arrays.asList(new Integer [] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15} );
	
	public MotrpacOptionsForm(final String id, final ModalWindow modal1)
		{
		super(id);		
		initializeValuesFromSession();
		add(buildCountDropdown("nGastroExercise", "nGastroExercise")); 
		add(buildCountDropdown("nGastroSedentary", "nGastroSedentary")); 
		add(buildCountDropdown("nLiverExcercise", "nLiverExcercise")); 
		add(buildCountDropdown("nLiverSedentary", "nLiverSedentary")); 
		add(buildCountDropdown("nAdiposeExercise", "nAdiposeExercise"));
		add(buildCountDropdown("nAdiposeSedentary", "nAdiposeSedentary"));
		add(buildCountDropdown("nPlasmaExercise", "nPlasmaExercise"));
		add(buildCountDropdown("nPlasmaSedentary", "nPlasmaSedentary"));
		// issue 22
		add(buildCountDropdown("nLungExercise", "nLungExercise"));
		add(buildCountDropdown("nLungSedentary", "nLungSedentary"));
		add(buildCountDropdown("nKidneyExercise", "nKidneyExercise"));
		add(buildCountDropdown("nKidneySedentary", "nKidneySedentary"));
		add(buildCountDropdown("nHeartExercise", "nHeartExercise"));
		add(buildCountDropdown("nHeartSedentary", "nHeartSedentary"));
		add(buildCountDropdown("nBrownAdiposeExercise", "nBrownAdiposeExercise"));
		add(buildCountDropdown("nBrownAdiposeSedentary", "nBrownAdiposeSedentary"));
		add(buildCountDropdown("nHippoCampusExercise", "nHippoCampusExercise"));
		add(buildCountDropdown("nHippoCampusSedentary", "nHippoCampusSedentary"));
			
		add(buildCountDropdown("nRatPlasma", "nRatPlasma"));
		// Issue 427
		add(buildCountDropdown("nRatG", "nRatG"));
		add(buildCountDropdown("nRatL", "nRatL"));
		add(buildCountDropdown("nRatA", "nRatA"));
		
		// issue 422
	    add(new AjaxCancelLink("cancelButton", modal1, "Done")
    		{
    		@Override
    		public void onClick(AjaxRequestTarget target) 
    			{
    			MotrpacOptionsPage.this.onSave(nGastroExercise,nGastroSedentary,nLiverExercise,nLiverSedentary,nAdiposeExercise,nAdiposeSedentary,nPlasmaExercise,nPlasmaSedentary,nRatPlasma, nRatG, nRatL, nRatA,
    					                       nLungExercise, nLungSedentary,nKidneyExercise, nKidneySedentary,nHeartExercise, nHeartSedentary, nBrownAdiposeExercise, nBrownAdiposeSedentary,nHippoCampusExercise, nHippoCampusSedentary);
    			if (modal1 != null)
    				modal1.close(target);
    			}
    		});
		}
	
	private void initializeValuesFromSession() 
		{
		nGastroExercise = ((MedWorksSession) Session.get()).getNGastroExercise();	
		nGastroSedentary = ((MedWorksSession) Session.get()).getNGastroSedentary();	
		nLiverExercise = ((MedWorksSession) Session.get()).getNLiverExercise();	
		nLiverSedentary = ((MedWorksSession) Session.get()).getNLiverSedentary();	
		nAdiposeExercise = ((MedWorksSession) Session.get()).getNAdiposeExercise();	
		nAdiposeSedentary = ((MedWorksSession) Session.get()).getNAdiposeSedentary();	
		nPlasmaExercise = ((MedWorksSession) Session.get()).getNPlasmaExercise();	
		nPlasmaSedentary = ((MedWorksSession) Session.get()).getNPlasmaSedentary();
		nRatPlasma = ((MedWorksSession) Session.get()).getNRatPlasma();
		nRatG = ((MedWorksSession) Session.get()).getNRatG();
		nRatL = ((MedWorksSession) Session.get()).getNRatL();
		nRatA = ((MedWorksSession) Session.get()).getNRatA();
		// issue 22
		nLungExercise = ((MedWorksSession) Session.get()).getNLungExercise();	
		nLungSedentary = ((MedWorksSession) Session.get()).getNLungSedentary();
		nKidneyExercise = ((MedWorksSession) Session.get()).getNKidneyExercise();	
		nKidneySedentary = ((MedWorksSession) Session.get()).getNKidneySedentary();	
		nHeartExercise = ((MedWorksSession) Session.get()).getNHeartExercise();	
		nHeartSedentary = ((MedWorksSession) Session.get()).getNHeartSedentary();
		nBrownAdiposeExercise = ((MedWorksSession) Session.get()).getNBrownAdiposeExercise();	
		nBrownAdiposeSedentary = ((MedWorksSession) Session.get()).getNBrownAdiposeSedentary();	
		nHippoCampusExercise = ((MedWorksSession) Session.get()).getNHippoCampusExercise();	
		nHippoCampusSedentary = ((MedWorksSession) Session.get()).getNHippoCampusSedentary();			
		}

	private DropDownChoice<Integer> buildCountDropdown(String id, String property)
		{
		DropDownChoice<Integer> drp = new DropDownChoice<Integer>(id, new PropertyModel<Integer>(this, property), countOptions);
		
		drp.add(new AjaxFormComponentUpdatingBehavior("change")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)  {   }
			});
		return drp;
		}
	
	public int getNGastroExercise() 
		{
		return nGastroExercise;
		}

	public void setNGastroExercise(int nGastroExercise) 
		{
		this.nGastroExercise = nGastroExercise;
		}
	
	public int getNGastroSedentary() 
		{
		return nGastroSedentary;
		}
	
	public void setNGastroSedentary(int nGastroSedentary) 
		{
		this.nGastroSedentary = nGastroSedentary;
		}
	
	public int getNLiverExcercise() 
		{
		return nLiverExercise;
		}

	public void setNLiverExcercise(int nLiverExercise) 
		{
		this.nLiverExercise = nLiverExercise;
		}
	
	public int getNLiverSedentary() 
		{
		return nLiverSedentary;
		}
	
	 public void setNLiverSedentary(int nLiverSedentary) 
		{
		this.nLiverSedentary = nLiverSedentary;
		}
	
	public int getNPlasmaExercise() 
		{
		return nPlasmaExercise;
		}

	public void setNPlasmaExercise(int nPlasmaExercise) 
		{
		this.nPlasmaExercise = nPlasmaExercise;
		}
	
	public int getNPlasmaSedentary() 
		{
		return nPlasmaSedentary;
		}
	
	public void setNPlasmaSedentary(int nPlasmaSedentary )
		{
		this.nPlasmaSedentary = nPlasmaSedentary;
		}
	
	public int getNRatPlasma() 
		{
		return nRatPlasma;
		}
	
	public void setNRatPlasma(int nRatPlasma) 
		{
		this.nRatPlasma = nRatPlasma;
		}
	
	// issue 427
	public int getNRatG() 
		{
		return nRatG;
		}

	public void setNRatG(int nRatG) 
		{
		this.nRatG = nRatG;
		}
	
	public int getNRatL() 
		{
		return nRatL;
		}

	public void setNRatL(int nRatL) 
		{
		this.nRatL = nRatL;
		}
	
	public int getNRatA() 
		{
		return nRatA;
		}

	public void setNRatA(int nRatA) 
		{
		this.nRatA = nRatA;
		}
	
	public int getNAdiposeExercise() 
		{
		return nAdiposeExercise;
		}

	public void setNAdiposeExercise(int nAdiposeExercise) 
	    {
		this.nAdiposeExercise = nAdiposeExercise;
		}
	
	public int getNAdiposeSedentary() 
		{
		return nAdiposeSedentary;
		}
	
	public void setNAdiposeSedentary(int nAdiposeSedentary) 
		{
		this.nAdiposeSedentary = nAdiposeSedentary;
		}	
	// issue 22
	// issue 22
	public int getNLungExercise() 
		{
		return nLungExercise;
		}

	public void setNLungExercise(int nLungExercise) 
		{
		this.nLungExercise = nLungExercise;
		}
	
	// issue 22
	public int getNLungSedentary() 
		{
		return nLungSedentary;
		}

	public void setNLungSedentary(int nLungSedentary) 
		{
		this.nLungSedentary = nLungSedentary;
		}
	
	// issue 22
	public int getNKidneyExercise() 
		{
		return nKidneyExercise;
		}

	public void setNKidneyExercise(int nKidneyExercise) 
		{
		this.nKidneyExercise = nKidneyExercise;
		}
	
	// issue 22
	public int getNKidneySedentary() 
		{
		return nKidneySedentary;
		}

	public void setNKidneySedentary(int nKidneySedentary) 
		{
		this.nKidneySedentary = nKidneySedentary;
		}

	// issue 22
	public int getNHeartExercise() 
		{
		return nHeartExercise;
		}

	public void setNHeartExercise(int nHeartExercise) 
		{
		this.nHeartExercise = nHeartExercise;
		}
	
	// issue 22
	public int getNHeartSedentary() 
		{
		return nHeartSedentary;
		}

	public void setNHeartSedentary(int nHeartSedentary) 
		{
		this.nHeartSedentary = nHeartSedentary;
		}
	
	// issue 22
	public int getNBrownAdiposeExercise() 
		{
		return nBrownAdiposeExercise;
		}

	public void setNBrownAdiposeExercise(int nBrownAdiposeExercise) 
		{
		this.nBrownAdiposeExercise = nBrownAdiposeExercise;
		}
	
	// issue 22
	public int getNBrownAdiposeSedentary() 
		{
		return nBrownAdiposeSedentary;
		}
	
    public void setNBrownAdiposeSedentary(int nBrownAdiposeSedentary) 
		{
		this.nBrownAdiposeSedentary = nBrownAdiposeSedentary;
		}
       
	// issue 22
	public int getNHippoCampusExercise() 
		{
		return nHippoCampusExercise;
		}

	public void setNHippoCampusExercise(int nHippoCampusExercise) 
		{
		this.nHippoCampusExercise = nHippoCampusExercise;
		}
	
	// issue 22
	public int getNHippoCampusSedentary() 
		{
		return nHippoCampusSedentary;
		}

	public void setNHippoCampusSedentary(int nHippoCampusSedentary) 
		{
		this.nHippoCampusSedentary = nHippoCampusSedentary;
		}
	}

// issue 422 issue 427
 protected abstract void onSave(Integer nGastroExercise, Integer nGastroSedentary, Integer nLiverExcercise, Integer nLiverSedentary, Integer nAdiposeExercise, Integer nAdiposeSedentary,  Integer nPlasmaExercise, Integer nPlasmaSedentary, Integer nratPlasma, Integer nRatG, Integer nRatL, Integer nRatA,
		                        Integer nLungExercise, Integer nLungSedentary,
		                        Integer nKidneyExercise, Integer nKidneySedentary,
		                        Integer nHeartExercise, Integer nHeartSedentary,
		                        Integer nBrownAdiposeExercise, Integer nBrownAdiposeSedentary,
		                        Integer nHippoCampusExercise, Integer nHippoCampusSedentary
		                        );
}
