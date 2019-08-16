// Revisited : October 2016 (JK, JW)
package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.metabolomics.layers.service.CompoundNameService;
import edu.umich.brcf.shared.layers.dto.CompoundNameDTO;

public class EditCompoundName extends WebPage 
	{
	@SpringBean
	CompoundNameService cnService;
	
	boolean newFlag=false;
	
	
	public EditCompoundName(Page backPage, String cid, final WebMarkupContainer container, ModalWindow modal1) 
		{
		newFlag =true;
		CompoundNameDTO cnDto = new CompoundNameDTO();
		cnDto.setCid(cid);
		add(new Label("titleLabel", "Add Compound Name"));
		
		add(new FeedbackPanel("feedback"));
		add(new EditCompoundNameForm("editCompoundNameForm", cnDto, container, modal1));
		}
	
	
	public EditCompoundName(Page backPage, IModel cnModel, final WebMarkupContainer container, ModalWindow modal1) 
		{
		add(new Label("titleLabel", "Edit Compound Name"));
		CompoundName cname = (CompoundName) cnModel.getObject();
		add(new FeedbackPanel("feedback"));
		add(new EditCompoundNameForm("editCompoundNameForm", CompoundNameDTO.instance(cname), container, modal1));
		}
	
	
	public final class EditCompoundNameForm extends Form 
		{
		public EditCompoundNameForm(final String id, final CompoundNameDTO cnDto, final WebMarkupContainer container, final ModalWindow modal1) 
			{
			super(id, new CompoundPropertyModel(cnDto));

			add(new Label("cid", cnDto.getCid()));
		
			Label label = new Label("name", cnDto.getName());
		    label.setVisible(false);
			add(label);
			
			RequiredTextField nameText = new RequiredTextField("newName");
			if ((cnDto.getName()!=null)&&(cnDto.getName().trim().length()>0))
				nameText.setEnabled(false);
			add(nameText);
			nameText.add(StringValidator.maximumLength(500));
			
			DropDownChoice typesDD=new DropDownChoice("type", CompoundName.TYPES);
			typesDD.setRequired(true);
			add(typesDD);
			
			TextField htmlFld;
			add(htmlFld = new TextField("html"));
			htmlFld.add(StringValidator.maximumLength(500));
			
			add(buildSaveButton("saveChanges", container));
					
			add( new AjaxLink("close")
				{
				public void onClick(AjaxRequestTarget target)
					{
					if (container!=null)
						{
						if(!(cnDto.getCid().equals("to be assigned")))
							{
							List<CompoundName> nList = cnService.loadByCid(cnDto.getCid());
							((PageableListView)container.get("names")).setList(nList);
							}
						}
					modal1.close(target);
					}
				@Override // issue 464
				public MarkupContainer setDefaultModel(IModel model) 
				    {
					// TODO Auto-generated method stub
					return this;
				    }
				});
			}

		
		private RequiredTextField newRequiredTextField(String id,  int maxLenght) 
			{
			RequiredTextField textField = new RequiredTextField(id);
			textField.add(StringValidator.maximumLength(maxLenght));
			return textField;
			}
		}
	
	
	private Button buildSaveButton(String linkID, final WebMarkupContainer container)
		{
		Button btn = new Button("saveChanges")
			{
			@Override
			public void onSubmit() 
				{
				CompoundNameDTO cnDto = (CompoundNameDTO) getForm().getModelObject();
				boolean err=false;
				List<CompoundName> cnList = cnService.loadByCid(cnDto.getCid());
				if (!newFlag)
					for (CompoundName cmpName : cnList)
						if (cmpName.getName().equals(cnDto.getName()))
							{
							cnList.remove(cmpName);
							break;
							}
						
				for (CompoundName cmpName : cnList) 
					{
					if ((cmpName.getNameType().equals("pri"))&&(cnDto.getType().equals("pri")))
						{
						EditCompoundName.this.error("Compound cannot have more than one primary name!!!");
						err=true;
						}
					if (cmpName.getName().equalsIgnoreCase(cnDto.getNewName()))
						{
						EditCompoundName.this.error("Compound cannot have duplicate names!!!");
						err=true;
						}
					}

				if (!err)
					{
					CompoundName cname = cnService.save(cnDto);
					EditCompoundName.this.info("Compound name saved!!!");
					if (container!=null)
						{
						List<CompoundName> nList = cnService.loadByCid(cnDto.getCid());
						((PageableListView)container.get("names")).setList(nList);
						}
					}
				setResponsePage(getPage());
				}
			};
			
		return btn;
		}
	}
	
	
	
	