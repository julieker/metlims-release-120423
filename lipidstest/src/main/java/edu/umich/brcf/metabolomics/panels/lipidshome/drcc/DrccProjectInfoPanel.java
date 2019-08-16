// DrccProjectInfoPanel.java
// Written by Jan Wigginton 06/01/15


package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;


import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;



public class DrccProjectInfoPanel extends Panel
	{
	private String expId;
	DrccProjectInfo projectInfo; 
	
	public DrccProjectInfoPanel(String id, String selectedExperiment, WebPage backPage, DrccProjectInfo projectInfo) 
		{
		super(id);
		
		expId = selectedExperiment;
		this.projectInfo = projectInfo;
		
		for (int i = 0; i< projectInfo.getInfoFields().size(); i++)
			{
			DrccInfoField valueSource = projectInfo.infoFields.get(i);
			String fieldTag = valueSource.getFieldTag();
			if (!fieldTag.equals("projectDescription"))
				add(buildTextField(i)); 
			else
				add(buildTextArea(i));
			}
		}
	
	
	private TextArea buildTextArea(int i)
		{
		DrccInfoField valueSource = projectInfo.infoFields.get(i);
		TextArea fld = new TextArea(valueSource.getFieldTag(),  new PropertyModel <String>(valueSource, "fieldValues.0"));
		
		fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForTextField", fld,  i));
		
		return fld;
		}
	
	private TextField buildTextField(int i)
		{
		DrccInfoField valueSource = projectInfo.infoFields.get(i);
		TextField fld = new TextField(valueSource.getFieldTag(),  new PropertyModel <String>(valueSource, "fieldValues.0"));
		
		fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForTextField", fld,  i));
		
		return fld;
		}
	
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final 
			String response, final Component field, final int i)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
	        {
	        @Override
	        protected void onUpdate(AjaxRequestTarget target)
	        	{
	        	target.add(field);
	        	DrccInfoField valueSource = projectInfo.infoFields.get(i);
	    		
	        	valueSource.setFieldValues(0, field.getDefaultModelObjectAsString());
	        	}
	        };
		}
	}
	
	
	
	
























//public List <IWriteConvertable> getInfoLines()
	//	{
	//	return infoLines;
	//	}
	
	//public String getOutputFileName()
	//	{
	//	return "DRCCProjectInfo_" + expId + ".tsv";
	//	}
	
	/*public String getBlank()
		{
		return blank;
		}
	
	public void setBlank(String b)
		{
		blank = "";
		}
	
	public String getFundingSource()
		{
		return fundingSource;
		}
	
	public void setFundingSource(String fs)
		{
		this.fundingSource = fs;
		}
	
	protected ResourceLink buildDownloadLink(String linkId, List<IWriteConvertable> infoLines)
		{	
		WebFileDownloadResource resource = new WebFileDownloadResource(infoLines);
		resource.setColTitlesListModel(null);
		resource.setOutfileName(new PropertyModel<String> (this, "outputFileName"));
		//resource.setValuesToWriteModel(new PropertyModel<List <? extends IWriteConvertable>>(this, infoLines));
		resource.setCacheable(false);		
		final WebFileDownloadResource res = resource;
		resource.setMimeType("text/tsv");
		
		return new ResourceLink(linkId, resource);
		}
	}

	
	//protected ResourceLink buildDataDownloadLink(String linkId)
	//{	
	//List <IWriteConvertable> printItems = this.projectLines != null ? projectLines : new ArrayList<IWriteConvertable>();
							//	? worklist.getItemsOnPage(listview.getCurrentPage()) 
							//	: new ArrayList<WorklistItemSimple>();   
	
	//WebFileDownloadResource resource = new WebFileDownloadResource(this.getProjectLines());
	//resource.setColTitlesListModel(null);
	//resource.setOutfileName(new PropertyModel<String> (this, "outputFileName"));
	//resource.setCacheable(false);		
	//resource.setMimeType("tsv");
//	resource.setReturnZipped(true);
	//final WebFileDownloadResource res = resource;
	
//	ResourceLink rl = new ResourceLink(linkId, resource)
//		{
//		@Override 
//		public boolean isVisible()
//			{
//			return true;
		//	return !worklist.getItems().isEmpty();
//			}	
		
	  //  @Override
	  //  public 
	//	};
		
	//return rl;
	//}
	
//	List<String> printInformation()
//		{
//		List<String> arrList = new ArrayList<String>();
//		return arrList;
//		}

	//List <? extends IWriteConvertable> getInfoLines()
	//	{
	//	return infoLines;
	//	}
	

/*
 * 	/*
		Experiment exp = experimentService.loadExperimentWithInfoForDrcc(expId);	
		String projectId = exp == null ? "PR0253" : exp.getProject().getProjectID();
		Project  project = projectService.loadById(projectId);
	
		projectDescription = project.getDescription();
		
		String clientId = project.getClient().getClientID();
		Client client = clientService.loadById(clientId);
		
		final String investigatorUsername = client.getInvestigator().getUserName();
		LoadableDetachableModel<User> investigator = new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() { return (userService.getUserByUserName(investigatorUsername));}
			};
		
		String orgId = client.getOrganizationID();
		Organization organization = organizationService.loadById(orgId);

		List <String> grantSources = experimentService.getGrantInfo(expId);
		fundingSource = grantSources.toString();

		PropertyModel <String> model = new PropertyModel<String>(project, "projectName");
		infoLines.add(new InfoLine("Project Title", model));
		add(new TextField("projectName", model));
		InfoLine line;
		
		infoLines.add(new InfoLine("Project Type", new PropertyModel<String>(this, "blank")));
		
		model = new PropertyModel<String>(project, "description");
		infoLines.add(line = new InfoLine("Project Summary", model));
		add(new TextArea("projectDescription", model));
		
		model = new PropertyModel<String>(organization, "orgName");
		infoLines.add(new InfoLine("Institute", model));
		add(new TextField("projectOrganization", model));
		
		model = new PropertyModel<String>(client, "dept");
		infoLines.add(new InfoLine("Department", model));
		add(new TextField("projectDepartment", model));
		
		model = new PropertyModel<String>(investigator, "lab");
		infoLines.add(new InfoLine("Laboratory", model));
		add(new TextField("projectLab", model));	

		PropertyModel <String> modelFund = new PropertyModel<String>(this, "fundingSource");
		infoLines.add(new InfoLine("Funding Source", modelFund));
		add(new TextField("fundingSource", modelFund));
		
		model = new PropertyModel<String>(investigator, "lastName");
		infoLines.add(new InfoLine("Principal Investigator: Last Name", model));
		add(new TextField("piLastName", model));
		
		model = new PropertyModel<String>(investigator, "firstName");
		infoLines.add(new InfoLine("Principal Investigator : First Name", model));
		add(new TextField("piFirstName", model));
		
		model =  new PropertyModel<String>(organization, "orgAddress");
		infoLines.add(new InfoLine("Address", model));
		add(new TextField("piAddress",model));
		
		model = new PropertyModel<String>(investigator, "email");
		infoLines.add(new InfoLine("E-mail", model));
		add(new TextField("piEmail", model));

		//projectInfo.setFundingSource("cats");

		//model = new PropertyModel<String>(projectInfo, "fundingSource");
		infoLines.add(new InfoLine("Phone", new PropertyModel<String>(projectInfo, "fundingSource")));
		add(new TextField("piPhone", new PropertyModel<String>(projectInfo, "fundingSource")));
		
		

		
			//	String fullName = "DrccProjInfo_" + this.expId;
	//	METWorksDataDownload resource = new METWorksDataDownload("downloadLink", new PropertyModel(projectInfo, "infoFields"), fullName +".tsv", null);
	//	add(resource.getResourceLink());
		
	//	add(new AjaxBackButton("backButton", backPage));

*/	
 
	