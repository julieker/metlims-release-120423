package edu.umich.brcf.metabolomics.panels.admin.instruments;

import java.util.Arrays;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Instrument;
import edu.umich.brcf.metabolomics.layers.service.InstrumentService;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.service.UserService;

public class UserListPanel extends Panel
	{
	@SpringBean
	UserService userService;

	@SpringBean
	InstrumentService instrumentService;

	ListView userList;
	private User selectedUser;
	private DropDownChoice userChoice;
	Instrument instrument;

	public UserListPanel(String id, final Instrument instrument)
		{
		super(id);
		setOutputMarkupId(true);
		this.instrument = instrument;
		IModel usersModel = new LoadableDetachableModel()
			{
			protected Object load()
				{
				return Arrays.asList(instrumentService.getUserNotifySetForInstrument(instrument).toArray());
				}
			};

		final WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);
		userList = new ListView("users", usersModel)
			{
			public void populateItem(ListItem item)
				{
				final User user = (User) item.getModelObject();
				item.add(new Label("name", user.getFullName()));
				item.add(new AjaxLink("delete")
					{
					public void onClick(AjaxRequestTarget target)
						{
						instrumentService.removeInstrumentToUserAssociation(instrument, user);
						updateUserList();
						target.add(container);
						}
					@Override // issue 464
					public MarkupContainer setDefaultModel(IModel model) 
					    {
						// TODO Auto-generated method stub
						return this;
					    }
					});
				}
			};
		container.add(userList);
		container.add(new AjaxLink("add")
			{
			public void onClick(AjaxRequestTarget target)
				{
				userChoice.setVisible(true);
				target.add(userChoice);
				target.add(container);
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
		});
		container.add(userChoice = new DropDownChoice("userChoiceBox",
				new PropertyModel(this, "selectedUser"), userService.getUsersForInstrumentAssociations(),
				new ChoiceRenderer("fullName", "id")));
		userChoice.setOutputMarkupId(true);
		userChoice.setVisible(false);
		userChoice.add(new AjaxFormComponentUpdatingBehavior("change")
			{
			protected void onUpdate(AjaxRequestTarget target)
				{
				userChoice.setVisible(false);
				instrumentService.addUserAssociationToInstrument(
						instrument, selectedUser);
				updateUserList();
				target.add(userChoice);
				target.add(container);
				}
			});
		}

	public void updateUserList()
		{
		userList.setList(Arrays.asList(instrumentService.getUserNotifySetForInstrument(instrument).toArray()));
		}

	public User getSelectedUser()
		{
		return selectedUser;
		}

	public void setSelectedUser(User selectedUser)
		{
		this.selectedUser = selectedUser;
		}
	}
