////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistFieldBuilder.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;




public class WorklistFieldBuilder implements Serializable
	{
/////////////////////////////////////
	@SpringBean
	private SampleService sampleService;
	// issue 394
	
//////////////////////////////////////
	public WorklistFieldBuilder()
		{
		}
	
	// issue 229
	public static TextField buildStringTextField(String id, WorklistItemSimple item, String field)
		{
		TextField wrkField = new TextField(id, new PropertyModel<String>(item, field));
		// issue 229
		if (field.equals("outputFileNameWithDir"))
			{
			item.setOutputFileNameWithDir(item.getGroup().getParent().getIsCustomDirectoryStructure() ?  item.grabDataFileWithCustomDirectory() : item.getOutputFileName());
			wrkField.add(StringValidator.maximumLength(250));
			}
		if (field.equals("sampleName"))
	    	// issue 215
	        wrkField.add(AttributeModifier.append("title", item.getSampleName().indexOf("-") >= 0 ? item.getSampleName().substring(0, item.getSampleName().lastIndexOf("-")) : item.getSampleName()));
		// Issue 268
	    if (field.equals("outputFileName"))
	    	wrkField.add(AttributeModifier.append("title", item.getOutputFileName()));
	    // issue 268
	    if (field.equals("methodFileName"))
	    	wrkField.add(AttributeModifier.append("title", item.getMethodFileName()));
	    if (field.equals("overrideMethod"))
	    	wrkField.add(AttributeModifier.append("title", item.getOverrideMethod()));
	    return wrkField;
		}

	// issue 217
	public static Label buildPlateLabelField(String id, final WorklistItemSimple item, String field)
		{
		return new Label(id, new PropertyModel<String>(item, field))
			{
			@Override
			protected void onComponentTag(ComponentTag tag)
				{
				super.onComponentTag(tag);
				String displayTitle = assembleStyleTag(item, true);
				tag.put("style", displayTitle);
				if (!(item == null || item.getSampleType().trim().startsWith("Unknown")))
					tag.put("title", item.getSampleType());
				}
			};
		}
	
	public static TextField buildIntegerTextField(String id, final WorklistItemSimple item, final String field)
		{
		return new TextField<Integer>(id, new PropertyModel<Integer>(item,field))
			{
			public boolean isEnabled()
				{
				return (field.equals("randomIdx") && item.getRepresentsControl() == true);
				}
			};
		}

	public static TextField buildDoubleTextField(String id, WorklistItemSimple item, String field)
		{
		return new TextField(id, new PropertyModel<Double>(item, field));
		}
	
	public static TextField buildFillDoubleTextField(String id, WorklistItemSimple item, String field)
		{
		return new TextField(id, new PropertyModel<Double>(item, field));
		}

	public static TextField buildStringWorklistField(final String id, final WorklistItemSimple item, final String field)
		{
		TextField txt = WorklistFieldBuilder.buildStringTextField(id, item, field);
		txt.add(buildTextDecoratingBehavior(id, item, field));
		txt.add(buildChangeRegistrationBehavior(id, item, field));
		return txt;
		}
	
	// onComponentTag
	public static Label buildPlateLabelWorklistField(boolean bothQCMPandMP , final String id, final WorklistItemSimple item,  String field, WorklistSimple ws)
		{
		// issue 217
        if (!item.getRepresentsControl())
        	item.setCommentResearcherId(item.calcCommentToolTip(ws, item));
		// Issue 268 
		// issue 346
		// issue 17
		if (bothQCMPandMP)
			if (item.getShortSampleName().equals("CS00000MP") || item.getShortSampleName().equals("CS000QCMP"))
			    {
				item.setShortSampleName("CS00000MP\nCS000QCMP");
				item.setMpQcmpName("Master Pool (CS00000MP)\nMaster Pool.QCMP (CS000QCMP)");
			    }
		if (!item.getNameForUserControlGroup().equals(""))
			field = "shortNameForUserControlGroup";
		// issue 217
		// issue 229
		else 
			field = "commentResearcherId";	
		// issue 215 tool tip         
		Label pLabel = WorklistFieldBuilder.buildPlateLabelField(id, item, field);
		String theCommentString = "";
		if (!item.getRepresentsControl())
			{
			theCommentString = item.getSampleName() +  (StringUtils.isEmptyOrNull(item.getSampleName()) ? ""  : "\n" + item.calcCommentToolTip(ws, item));
			pLabel.add(AttributeModifier.append("title",theCommentString));
			}
		else 
			pLabel.add(AttributeModifier.append("title",item.getSampleName().indexOf("-") >= 0 ? item.getSampleName().substring(0, item.getSampleName().lastIndexOf("-")) : item.getSampleName()));
		if (bothQCMPandMP)
		    if (item.getShortSampleName().equals("CS00000MP\nCS000QCMP"))
			    pLabel.add(AttributeModifier.replace("title",item.getMpQcmpName()));			  
		return pLabel;
		}

	public static TextField buildDoubleWorklistField(final String id, final WorklistItemSimple item, final String field)
		{
		TextField txt = WorklistFieldBuilder.buildDoubleTextField(id, item, field);
		txt.add(buildTextDecoratingBehavior(id, item, field));
		return txt;
		}
	
	// issue 179
	public static TextField buildDoubleWorklistFieldAgilent(final String id, final WorklistItemSimple item, final String field)
		{
		TextField txt;
		if (item.getGroup().getParent().getChangeDefaultInjVolume())
			{
			item.setInjectionVolume (item.getGroup().getParent().getDefaultInjectionVol());
			txt = WorklistFieldBuilder.buildDoubleTextField(id, item, field);
			}
		else
			{
			item.setInjectionVolume("As Method");
			txt = WorklistFieldBuilder.buildStringWorklistField(id, item, field);
			//txt = new TextField(id, new PropertyModel<String>(item, field));
			}
		txt.add(buildTextDecoratingBehavior(id, item, field));
		return txt;
		}
	
	public static TextField buildIntegerWorklistField(final String id, final WorklistItemSimple item, String field)
		{
		TextField txt = WorklistFieldBuilder.buildIntegerTextField(id, item, field);

		txt.add(buildTextDecoratingBehavior(id, item, field));

		return txt;
		}

	
	private static AjaxFormComponentUpdatingBehavior buildChangeRegistrationBehavior(String id, final WorklistItemSimple item, final String field)
		{
		return new AjaxFormComponentUpdatingBehavior("change")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
				{
				target.appendJavaScript("Something changed!!');");
				item.setIsHandEdited(true);
				}
			};
		}

	
	private static AjaxFormComponentUpdatingBehavior buildTextDecoratingBehavior(String id, final WorklistItemSimple item, final String field)
		{
		return new AjaxFormComponentUpdatingBehavior("change")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target) { }

			protected void onComponentTag(final ComponentTag tag)
				{
				super.onComponentTag(tag);
				String style = assembleStyleTag(item, false);
				
				if (field.equals("randomIdx") && item.getRepresentsControl())
					tag.put("title", "Delete control " + item.getSampleName());

				tag.put("style", style);
				}
			};
		}

	
	static String assembleStyleTag(WorklistItemSimple item, boolean forTable)
		{
		String style = forTable ? "" : "width : 100%;";
	
		if (item.getIsDeleted())
			style += decorateAsDeleted(item);

		style += shadeBackground(item);

		return style;
		}

	
	private static String decorateAsDeleted(WorklistItemSimple item)
		{
		String style = "";
		String big_blank = "                                                                 ";
		item.setSampleName(item.getSampleName()
				+ "                                                                                                                                                       ");
		item.setMethodFileName(item.getMethodFileName() + big_blank + big_blank);
		item.setOutputFileName(item.getOutputFileName() + big_blank + big_blank);

		style += "white-space:nowrap;  text-decoration : line-through; text-decoration-color : red; -moz-text-decoration-color: red;";
		return style;
		}
	
// issue 371 and 372 and 394
	private static String shadeBackground(WorklistItemSimple item)
		{     
		String style = "background :";
		String name = item.getSampleType().trim();
		if (item.getSampleType().startsWith("Blank"))
			style += "#CEF7E8";  
		else if (item.getSampleType().startsWith("Pooled Plasma"))
			style += "#F6CDCB"; 
		else if (item.getSampleType().startsWith("Standard Mix"))
			style += "#CBE3F6";  
		else if (item.getSampleType().startsWith("Test Pool"))
			style += "#D9F6CB";  
		else if (item.getSampleType().startsWith("Neat Blank"))
			style += "#CEF7E8"; 
		else if (item.getSampleType().startsWith("Process Blank"))
			style += "#F6CDCB";  
		else if (item.getSampleType().startsWith("RC Plasma"))
			style += "#CBE3F6";  
		else if (item.getSampleType().startsWith("Red Cross"))
			style += "#CBE3F6";  
		else if (item.getSampleType().startsWith("Matrix Blank"))
			style += "#CBE3F6";  	
		else if (item.getSampleType().startsWith("Solvent Blank"))
			style += "#D9F6CB";  
		else if (item.getSampleType().startsWith("Standard.0"))
			style += "#EFF6CB";  
		else if (item.getSampleType().startsWith("Standard 0"))
			style += "#EFF6CB";  
		else if (item.getSampleType().startsWith("Standard.10"))
			style += "#F6CBF2";  
		else if (item.getSampleType().startsWith("Standard 10"))
			style += "#F6CBF2";  
		else if (item.getSampleType().startsWith("Standard.11"))
			style += "#E9E9FF";
		else if (item.getSampleType().startsWith("Standard 11"))
			style += "#E9E9FF";
		else if (item.getSampleType().startsWith("Standard.1"))
			style += "#E9E9FF";  
		else if (item.getSampleType().startsWith("Standard 1"))
			style += "#E9E9FF";  
		else if (item.getSampleType().startsWith("Standard.2"))
			style += "#CBD9F6";  
		else if (item.getSampleType().startsWith("Standard 2"))
			style += "#CBD9F6";  
		else if (item.getSampleType().startsWith("Standard.3"))
			style += "#E8CBF6";  
		else if (item.getSampleType().startsWith("Standard 3"))
			style += "#E8CBF6";  
		else if (item.getSampleType().startsWith("Standard.4"))
			style += "#F6CBF2";  
		else if (item.getSampleType().startsWith("Standard 4"))
			style += "#F6CBF2";  
		else if (item.getSampleType().startsWith("Standard.5"))
			style += "#E9E9FF";
		else if (item.getSampleType().startsWith("Standard 5"))
			style += "#E9E9FF";
		else if (item.getSampleType().startsWith("Standard.6"))
			style += "#EFF6CB";  
		else if (item.getSampleType().startsWith("Standard 6"))
			style += "#EFF6CB";  
		else if (item.getSampleType().startsWith("Standard.7"))
			style += "#CBF6D2";  
		else if (item.getSampleType().startsWith("Standard 7"))
			style += "#CBF6D2";  
		else if (item.getSampleType().startsWith("Standard.8"))
			style += "#CBD9F6";  
		else if (item.getSampleType().startsWith("Standard 8"))
			style += "#CBD9F6";  
		else if (item.getSampleType().startsWith("Standard.9"))
			style += "#E8CBF6";  
		else if (item.getSampleType().startsWith("Standard 9"))
			style += "#E8CBF6";  
		else if (item.getSampleType().startsWith("Blank"))
			style += "#CEF7E8";  
		else if (item.getSampleType().startsWith("Master Pool"))
			style += "#F6CBCF";  
		else if (item.getSampleType().startsWith("Pool.0"))
			style += "#F6CBCF";  
		else if (item.getSampleType().startsWith("Pool.1b"))
			style += "#B3CDE0";  
		else if (item.getSampleType().startsWith("Batch Pool.M1"))
		        style += "#CBF6D2";	 
		else if (item.getSampleType().startsWith("Pool.1"))
		        style += "#CBF6D2";	 		
		else if (item.getSampleType().startsWith("Batch Pool.M2"))
			style += "#CBD9F6";  
		else if (item.getSampleType().startsWith("Pool.2"))
			style += "#CBD9F6";  
		else if (item.getSampleType().startsWith("Batch Pool.M3"))
			style += "#E8CBF6";  
		else if (item.getSampleType().startsWith("Pool.3"))
			style += "#E8CBF6";  
		else if (item.getSampleType().startsWith("Batch Pool.M4"))
			style += "#F6CBF2";  
		else if (item.getSampleType().startsWith("Pool.4"))
			style += "#F6CBF2";  
		else if (item.getSampleType().startsWith("Batch Pool.M5"))
			style += "#E9E9FF";
		else if (item.getSampleType().startsWith("Pool.5"))
			style += "#E9E9FF";
		else if (item.getSampleType().startsWith("Other Pool.0"))
			style += "#EFF6CB"; 
		else if (item.getSampleType().startsWith("Pool.6"))
			style += "#EFF6CB"; 
		else if (item.getSampleType().startsWith("Other Pool.1"))
			style += "#CBF6D2";
		else if (item.getSampleType().startsWith("Pool.7"))
			style += "#CBF6D2";
		else if (item.getSampleType().startsWith("Other Pool.2"))
			style += "#CBD9F6"; 
		else if (item.getSampleType().startsWith("Pool.8"))
			style += "#CBD9F6"; 
		else if (item.getSampleType().startsWith("Other Pool.3"))
			style += "#E8CBF6"; 
		else if (item.getSampleType().startsWith("Pool.9"))
			style += "#E8CBF6"; 
		else if (item.getSampleType().startsWith("Reference 1 - urine"))
			style += "#e9fb8c"; 
		else if (item.getSampleType().startsWith("Reference 2 - urine"))
			style += "#f7fed6"; 
		else if (item.getSampleType().startsWith("Reference 1 - plasma"))
			style += "#d8f4f8"; 
		else if (item.getSampleType().startsWith("Reference 2 - plasma"))
			style += "#ACE8F1"; 
		// MotrPac Issue 422
		// Issue 422
		else if (item.getSampleType().startsWith("MoTrPAC -   Gastrocnemius, Exercise"))
			style += "#FF92BB";
		// issue 422
		else if (item.getSampleType().startsWith("MoTrPAC -   Gastrocnemius, Sedentary"))
			style += "#EEE0E5";
		// Issue 422
		else if (item.getSampleType().startsWith("MoTrPAC -   Liver, Exercise"))
			style += "#BDA0CB";
		// issue 422
		else if (item.getSampleType().startsWith("MoTrPAC -   Liver, Sedentary"))
			style += "#CDB5CD";
		// Issue 422
		else if (item.getSampleType().startsWith("MoTrPAC -   Adipose, Exercise"))
			style += "#ECC8EC";
		// issue 422
	    else if (item.getSampleType().startsWith("MoTrPAC -   Adipose, Sedentary"))
            style += "#EEA9B8";
		// Issue 422
		else if (item.getSampleType().startsWith("MoTrPAC -   Plasma, Exercise"))
			style += "#DCA2CD";
		// issue 422
		else if (item.getSampleType().startsWith("MoTrPAC -   Plasma, Sedentary"))
			style += "#D3BECF";
		// issue 422
		else if (item.getSampleType().startsWith("UM rat   plasma control"))
			style += "#FFADB9";	
		// issue 427
		else if (item.getSampleType().startsWith("UM rat   liver control"))
			style += "#EE799F";	
		else if (item.getSampleType().startsWith("UM rat   adipose control"))
			style += "#F6C9CC";	
		else if (item.getSampleType().startsWith("UM rat   gastrocnemius control"))
			style += "#D0A9AA";	
		// issue 22
		else if (item.getSampleType().startsWith("MoTrPAC -   Lung, Exercise"))
			style += "#FFDF00";
		else if (item.getSampleType().startsWith("MoTrPAC -   Lung, Sedentary"))
			style += "#D4AF37";
		else if (item.getSampleType().startsWith("MoTrPAC -   Kidney, Exercise"))
			style += "#CFB53B";
		else if (item.getSampleType().startsWith("MoTrPAC -   Kidney, Sedentary"))
			style += "#C5B358";
		else if (item.getSampleType().startsWith("MoTrPAC -   Heart, Exercise"))
			style += "#E6BE8A";
		else if (item.getSampleType().startsWith("MoTrPAC -   Heart, Sedentary"))
			style += "#DAA520";
		else if (item.getSampleType().startsWith("MoTrPAC -   Brown Adipose, Exercise"))
			style += "#CD853F";
		else if (item.getSampleType().startsWith("MoTrPAC -   Brown Adipose, Sedentary"))
			style += "#D2691E";
		else if (item.getSampleType().startsWith("MoTrPAC -   Hippocampus, Exercise"))
			style += "#EEE8AA";
		else if (item.getSampleType().startsWith("MoTrPAC -   Hippocampus, Sedentary"))
			style += "#FAFAD2";
		// issue 126
		else if (item.getSampleType().startsWith("MoTrPAC -   Muscle-Human : Male"))
			style += "#66FF99";
		else if (item.getSampleType().startsWith("MoTrPAC -   Muscle-Human : Female"))
			style += "#CC99FF";
		else if (item.getSampleType().startsWith("UM Human muscle control"))
			style += "#CCFFFF";
		// issue 193
		else if (item.getSampleType().startsWith("MoTrPAC -   Plasma-Human: Male"))
			style += "#CCFAFf";
		else if (item.getSampleType().startsWith("MoTrPAC -   Plasma-Human: Female"))
			style += "#CCF0FF";
		//else if (item.getSampleType().startsWith("Injection - plasma   (R00CHRPL1-Pre)") )
		else if (item.getSampleType().startsWith("Injection"))
			style += "#c8a2c8";
			// color will be good choice when pool1b is no longer use 
		else if (item.getSampleType().startsWith("Adi RefStdA"))
			style += "#EADDCA";
		else if (item.getSampleType().startsWith("Adi RefStdB"))
			style += "#FFBF00";
		else if (item.getSampleType().startsWith("Adi RefStdC"))
			style += "#FBCEB1";
		else if (item.getSampleType().startsWith("Adi RefStdD"))
			style += "#F5F5DC";
		else if (item.getSampleType().startsWith("Adi RefStdE"))
			style += "#E1C16E";
			
		else if (item.getRepresentsUserDefinedControl())
			style += "#e0b3dd";	
		
		else
			style += "#eaeef2";
		return style;		
		}

	//issue 394 get rid of dead routines buildDynamicHOverMessageBehavior, trimtags
	
	}

 