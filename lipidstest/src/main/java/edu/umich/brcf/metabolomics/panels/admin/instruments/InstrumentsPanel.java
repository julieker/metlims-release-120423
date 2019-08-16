package edu.umich.brcf.metabolomics.panels.admin.instruments;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Instrument;
import edu.umich.brcf.metabolomics.layers.domain.InstrumentRegistry;
import edu.umich.brcf.metabolomics.layers.service.InstrumentService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.METWorksModalContentPanel;
import edu.umich.brcf.shared.panels.utilitypanels.METWorksModalWindow;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;


public class InstrumentsPanel extends Panel 
	{
	@SpringBean
	private InstrumentService instrumentService;

	private boolean showButtonColumn;

	
	public InstrumentsPanel(String id)
		{
		super(id);
		init(false);
		}

	public InstrumentsPanel(String id, boolean showButtons)
		{
		super(id);
		init(showButtons);
		}

	private void init(boolean showButtons) 
		{
		final WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);
		
		final METWorksPctSizableModal modal1 = new METWorksPctSizableModal("modal1", 0.4, .3); //ModalCreator.createModalWindow("modal1", 1000, 200);
		add(modal1);
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
			{
			@Override
			public void onClose(AjaxRequestTarget target)  {    target.add(container);  }
			});
		this.setOutputMarkupId(true);
		
		final InstrumentsPanel panelRef = this;
		setOutputMarkupId(true);
		IModel instrumentModel = new LoadableDetachableModel() 
			{
			protected Object load() { return instrumentService.allInstruments(); }
			};
			
		showButtonColumn = showButtons;
		
		container.add(new ListView("instruments", instrumentModel) 
			{
			public void populateItem(final ListItem listItem) 
				{
				final Instrument instrument = (Instrument) listItem.getModelObject();
				
				UserListPanel panel = new UserListPanel("modaldata", instrument);
				final ModalWindow notifyModal = createModalWindow("notificationModal", instrument, panel);
				listItem.add(notifyModal);

				listItem.add(new Label("id", instrument.getInstrumentID()));
				listItem.add(showNotifyModalButton("notifyButton", showButtonColumn, notifyModal, panel));
				
				listItem.add(new Label("instrumentName", instrument.getName()));
				listItem.add(new Label("instrumentDescription", instrument.getDescription()));
				listItem.add(new Label("type", instrument.getType()));
				listItem.add(new Label("room", instrument.getRoom()));
				listItem.add(new Label("manufacturer", instrument.getManufacturer()));
				listItem.add(new Label("model", instrument.getModel()));
				listItem.add(new Label("serial", instrument.getSerialNumber()));
				listItem.add(OddEvenAttributeModifier.create(listItem));
				listItem.add(buildLinkToModal("editInstrument", instrument, modal1, panelRef));
				}
			});
		
		 add(buildLinkToModal("createInstrument",null, modal1, this));
		 
		}

	
	private AjaxLink buildLinkToModal(final String linkID, final Instrument instrument,  final METWorksPctSizableModal modal1, final InstrumentsPanel panel) 
		{
		return new AjaxLink(linkID)
        	{
            @Override
            public void onClick(AjaxRequestTarget target)
            	{
            	double widthPct, heightPct;
            	switch (linkID)
	            	{
	            	case "createInstrument" :  widthPct = 0.5; heightPct = 0.57; break;
	            	case "clientLink" : widthPct = 0.9; heightPct = 0.8; break;
	            	default : widthPct = 0.4; heightPct = 0.6; break;
	            	}
            	
            	setPageDimensions(modal1, widthPct, heightPct);
            	
            	modal1.setPageCreator(new ModalWindow.PageCreator()
            		{
                     public Page createPage()
                     	{
                    	return buildModalPage(linkID, modal1, panel, instrument == null ? null : new Model<Instrument>(instrument));
                     	}
            		});
            	 modal1.show(target);
            	}
            
            @Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
        	};
		}
	
	
	public Page buildModalPage(String linkID, final METWorksPctSizableModal modal1, final InstrumentsPanel panel, final IModel<Instrument> model)
		{
		boolean createInstrument = linkID.equals("createInstrument");
		Instrument instrument = model == null ? null : model.getObject();
		
		return (new EditInstrumentPage(getPage(), model, modal1)
	    	{
	    	@Override
			protected void onSave(Instrument instrument, AjaxRequestTarget target) 
				{
				//target.add(panel);
				//if (modal1 != null) modal1.close(target); 
				}
	    	});
	    }
		
		
	private AjaxLink showModalButton(String id, InstrumentRegistry registryEntry, boolean showButton, final METWorksPctSizableModal modal) 
		{
		AjaxLink link = new AjaxLink(id) 
			{
			public void onClick(AjaxRequestTarget target) { modal.show(target); }
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};

		if (registryEntry == null || !showButton)
			link.setVisible(false);

		return link;
		}


	private ModalWindow createModalWindow(final String modalName, Instrument instrument, final Panel panel) {
		METWorksModalWindow modal = new METWorksModalWindow(modalName, instrument.getName() + " Caretaker List", panel,
				METWorksModalContentPanel.MODE_CLOSE_BUTTON_ONLY) 
			{
			public void onSave(AjaxRequestTarget target, IModel model) 
				{
				close(target);
				}

			public void onCancel(AjaxRequestTarget target) { close(target);  }
			};
			
		modal.setOutputMarkupId(true);
		modal.setInitialHeight(600);
		modal.setInitialWidth(800);
		return modal;
		}
	
	
	private void setPageDimensions(ModalWindow modal1, double widthPct, double heightPct)
		{
		int pageHeight = ((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
		modal1.setInitialHeight(((int) Math.round(pageHeight * heightPct)));
		int pageWidth = ((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();
		modal1.setInitialWidth(((int) Math.round(pageWidth * widthPct)));
		}
	

	private AjaxLink showNotifyModalButton(String id, boolean showButton, final ModalWindow modal, final UserListPanel panel) 
		{
		AjaxLink link = new AjaxLink(id) 
			{
			public void onClick(AjaxRequestTarget target) 
				{
				panel.updateUserList();
				modal.show(target);
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		if (!showButton)
			link.setVisible(false);
		return link;
		}
	}
	
