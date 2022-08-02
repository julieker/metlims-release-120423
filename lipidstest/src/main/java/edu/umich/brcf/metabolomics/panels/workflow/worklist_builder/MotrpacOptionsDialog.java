package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.form.button.Button;
import com.googlecode.wicket.kendo.ui.form.dropdown.DropDownList;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

import edu.umich.brcf.shared.util.METWorksException;

public abstract class MotrpacOptionsDialog extends AbstractFormDialog 
    {
	int  nGastroExercise = 0;

	private int nGastroSedentary = 0;

	private int nLiverExercise = 0;

	private int nLiverSedentary = 0;

	private int nAdiposeExercise = 0;

	private int nAdiposeSedentary = 0;

	private int nPlasmaExercise = 0;

	private int nPlasmaSedentary = 0;

	private int nRatPlasma = 0;

	private int nRatG = 0;

	private int nRatL = 0;

	private int nRatA = 0;
	
	protected int  nGastroExercisePrev= 0, nGastroSedentaryPrev= 0,nLiverExercisePrev= 0,
			 nLiverSedentaryPrev= 0,nAdiposeExercisePrev= 0,nAdiposeSedentaryPrev= 0,
			 nPlasmaExercisePrev= 0,
			 nPlasmaSedentaryPrev= 0,
			 nRatPlasmaPrev= 0,
			 nRatGPrev= 0, 
			 nRatLPrev= 0,
			 nRatAPrev= 0;
	
	protected int nLungExercisePrev= 0;
	protected int nLungSedentaryPrev= 0;
	protected int nKidneyExercisePrev= 0;
	protected int nKidneySedentaryPrev= 0;
	protected int nHeartExercisePrev= 0;
	protected int nHeartSedentaryPrev= 0;
	protected int nBrownAdiposeExercisePrev= 0;
	protected int nBrownAdiposeSedentaryPrev= 0;
    protected int nHippoCampusExercisePrev= 0;
	protected int nHippoCampusSedentaryPrev= 0;
	
	// issue 126
	protected int nMuscleHumanMalePrev=0;
	protected int nMuscleHumanFemalePrev=0;
	protected int nHumanMuscleCntrlPrev=0;
	protected int nRefStdAPrev = 0;
	protected int nRefStdBPrev = 0;
	protected int nRefStdCPrev = 0;
	protected int nRefStdDPrev = 0;
	protected int nRefStdEPrev = 0;
	
	
	protected int nRefStdA = 0;
	protected int nRefStdB = 0;
	protected int nRefStdC = 0;
	protected int nRefStdD = 0;
	protected int nRefStdE = 0;
	
	
	// issue 126
	protected int nMuscleHumanMale=0;
	protected int nMuscleHumanFemale=0;
	protected int nHumanMuscleCntrl=0;
	
	// issue 193
	protected int nPlasmaHumanMale=0;
	protected int nPlasmaHumanFemale=0;
	protected int nPlasmaHumanMalePrev=0;
	protected int nPlasmaHumanFemalePrev=0;
	
	private int nLungExercise = 0;
	private int nLungSedentary = 0;
	private int nKidneyExercise = 0;
	private int nKidneySedentary = 0;
	private int nHeartExercise = 0;
	private int nHeartSedentary = 0;
	private int nBrownAdiposeExercise  = 0;
	private int nBrownAdiposeSedentary = 0;
    private int nHippoCampusExercise = 0;
	private int nHippoCampusSedentary = 0;     
	private static final long serialVersionUID = 1L;
	
	final List<Integer> countOptions = Arrays.asList(new Integer [] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15} );
    ExperimentRandomization gEr = null;
	protected Form<?> form;
	final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback");
    public DialogButton submitButton = new DialogButton("submit", "Done");
    WorklistSimple originalWorklist;
	public MotrpacOptionsDialog(String id, String title, WorklistSimple workList)
		{
		super(id, title,  true);
	      //  gOriginalWorklist = originalWorklist;
			// Form //
		originalWorklist = workList;
		this.form = new Form<Integer>("form");
		this.add(this.form);
		form.setOutputMarkupId(true);
		//this.feedback.setOutputMarkupId(true);
		form.add(buildCountDropdown("nGastroExercise", "nGastroExercise")); 
		form.add(buildCountDropdown("nGastroSedentary", "nGastroSedentary")); 
		form.add(buildCountDropdown("nLiverExercise", "nLiverExercise")); 
		form.add(buildCountDropdown("nLiverSedentary", "nLiverSedentary")); 
		
		form.add(buildCountDropdown("nAdiposeExercise", "nAdiposeExercise"));
		form.add(buildCountDropdown("nAdiposeSedentary", "nAdiposeSedentary"));
		
		
		form.add(buildCountDropdown("nPlasmaExercise", "nPlasmaExercise"));
		form.add(buildCountDropdown("nPlasmaSedentary", "nPlasmaSedentary"));
		// issue 22
		form.add(buildCountDropdown("nLungExercise", "nLungExercise"));
		form.add(buildCountDropdown("nLungSedentary", "nLungSedentary"));
		form.add(buildCountDropdown("nKidneyExercise", "nKidneyExercise"));
		form.add(buildCountDropdown("nKidneySedentary", "nKidneySedentary"));
		form.add(buildCountDropdown("nHeartExercise", "nHeartExercise"));
		form.add(buildCountDropdown("nHeartSedentary", "nHeartSedentary"));
		form.add(buildCountDropdown("nBrownAdiposeExercise", "nBrownAdiposeExercise"));
		form.add(buildCountDropdown("nBrownAdiposeSedentary", "nBrownAdiposeSedentary"));
		form.add(buildCountDropdown("nHippoCampusExercise", "nHippoCampusExercise"));
		form.add(buildCountDropdown("nHippoCampusSedentary", "nHippoCampusSedentary"));
		
		// issue 126
		form.add(buildCountDropdown("nMuscleHumanMale", "nMuscleHumanMale"));
		form.add(buildCountDropdown("nMuscleHumanFemale", "nMuscleHumanFemale"));
		// issue 193
		form.add(buildCountDropdown("nPlasmaHumanMale", "nPlasmaHumanMale"));
		form.add(buildCountDropdown("nPlasmaHumanFemale", "nPlasmaHumanFemale"));
		
	
		form.add(buildCountDropdown("nRefStdA", "nRefStdA"));
		form.add(buildCountDropdown("nRefStdB", "nRefStdB"));
		form.add(buildCountDropdown("nRefStdC", "nRefStdC"));
		form.add(buildCountDropdown("nRefStdD", "nRefStdD"));
		form.add(buildCountDropdown("nRefStdE", "nRefStdE"));
				
		form.add(buildCountDropdown("nHumanMuscle", "nHumanMuscleCntrl"));
		
		form.add(buildCountDropdown("nRatPlasma", "nRatPlasma"));
		// Issue 427
		form.add(buildCountDropdown("nRatG", "nRatG"));
		form.add(buildCountDropdown("nRatL", "nRatL"));
		form.add(buildCountDropdown("nRatA", "nRatA"));
		this.form.add(this.feedback);	
			// Buttons //
		submitButton = 
		    new DialogButton("submit", "submit") 
		        {
				private static final long serialVersionUID = 1L;
				//@Override
				public void onSubmit(AjaxRequestTarget target, DialogButton button)
				    {
				    }			
			    };					
			form.setMultiPart(true);	
		}
	
	protected void onOpen(IPartialPageRequestHandler handler)
		{
		// re-attach the feedback panel to clear previously displayed error message(s)
		handler.add(this.feedback);
		}
	
	private DropDownChoice<Integer> buildCountDropdown(String id, String property)
		{
		DropDownChoice<Integer> drp = new DropDownChoice<Integer>(id, new PropertyModel<Integer>(originalWorklist, property), countOptions);
		drp.add(new AjaxFormComponentUpdatingBehavior("change")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)  {  }
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
	
	public int getNLiverExercise() 
		{
		return nLiverExercise;
		}

	public void setNLiverExercise(int nLiverExercise) 
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
	
	// issue 126
	public int getNMuscleHumanMale()
		{
		return nMuscleHumanMale;
		}
	
	public void setNMuscleHumanMale(int nMuscleHumanMale) 
		{
		this.nMuscleHumanMale = nMuscleHumanMale;
		}
	
	// issue 193
	public int getNPlasmaHumanMale()
		{
		return nPlasmaHumanMale;
		}

	// issue 193
	public void setNPlasmaHumanMale(int nPlasmaHumanMale) 
		{
		this.nPlasmaHumanMale = nPlasmaHumanMale;
		}
	
	// issue 126
	public int getNMuscleHumanFemale()
		{
		return nMuscleHumanFemale;
		}
	
	public void setNMuscleHumanFemale(int nMuscleHumanFemale) 
		{
		this.nMuscleHumanFemale = nMuscleHumanFemale;
		}
	
	// issue 193
	public int getNPlasmaHumanFemale()
		{
		return nPlasmaHumanFemale;
		}

	// issue 193
	public void setNPlasmaHumanFemale(int nPlasmaHumanFemale) 
		{
		this.nPlasmaHumanFemale = nPlasmaHumanFemale;
		}
	
	// issue 235
	/**************************************/
	
	// issue 193
	public int getNRefStdA()
		{
		return nRefStdA;
		}

	// issue 193
	public void setNRefStdA(int nRefStdA) 
		{
		this.nRefStdA= nRefStdA;
		}
	
	// issue 193
	public int getNRefStdB()
		{
		return nRefStdB;
		}

	// issue 193
	public void setNRefStdB(int nRefStdB) 
		{
		this.nRefStdB= nRefStdB;
		}
	
	// issue 193
	public int getNRefStdC()
		{
		return nRefStdC;
		}

	// issue 193
	public void setNRefStdC(int nRefStdC) 
		{
		this.nRefStdB= nRefStdC;
		}
	
	
	// issue 193
	public int getNRefStdD()
		{
		return nRefStdD;
		}

	// issue 193
	public void setNRefStdD(int nRefStdD) 
		{
		this.nRefStdD= nRefStdD;
		}
	
	
	// issue 193
	public int getNRefStdE()
		{
		return nRefStdE;
		}

	// issue 193
	public void setNRefStdE(int nRefStdE) 
		{
		this.nRefStdE= nRefStdE;
		}
	
	/*************************************/
	
	// issue 126
	public int getNHumanMuscleCntrl()
		{
		return nHumanMuscleCntrl;
		}
	
	public void setNHumanMuscleCntrl(int nHumanMuscleCntrl) 
		{
		this.nHumanMuscleCntrl = nHumanMuscleCntrl;
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
	
	// issue 6
    public void clearPrevValues ()
	    {
    	// issue 235
    	nRefStdA = 0;
    	nRefStdB = 0;
    	nRefStdC = 0;
    	nRefStdD = 0;
    	nRefStdE = 0;
    	
    	
    	
    	nGastroExercisePrev= 0; 
    	nGastroSedentaryPrev= 0;
    	nLiverExercisePrev= 0;
    	nLiverSedentaryPrev= 0;
    	nAdiposeExercisePrev= 0;
    	nAdiposeSedentaryPrev= 0;
    	nPlasmaExercisePrev= 0;
    	nPlasmaSedentaryPrev= 0;
    	nRatPlasmaPrev= 0;
    	nRatGPrev= 0; 
    	nRatLPrev= 0;
    	nRatAPrev= 0;
    	nLungExercisePrev= 0;
    	nLungSedentaryPrev= 0;
    	// issue 126
    	nMuscleHumanMalePrev= 0;
    	nMuscleHumanFemalePrev= 0;
    	
     	nPlasmaHumanMalePrev= 0;
    	nPlasmaHumanFemalePrev= 0;
    	
    	
    	nHumanMuscleCntrlPrev= 0;
    	
    	nKidneyExercisePrev= 0;
    	nKidneySedentaryPrev= 0;
        nHeartExercisePrev= 0;
    	nHeartSedentaryPrev= 0;
    	nBrownAdiposeExercisePrev= 0;
    	nBrownAdiposeSedentaryPrev= 0;
    	nHippoCampusExercisePrev= 0;
    	nHippoCampusSedentaryPrev= 0; 
	    }	

    }