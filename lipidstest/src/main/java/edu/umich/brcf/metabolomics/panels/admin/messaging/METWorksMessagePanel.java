package edu.umich.brcf.metabolomics.panels.admin.messaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.widgets.AjaxUpdatingRequiredTextField;


public class METWorksMessagePanel extends Panel
	{
	@SpringBean
	METWorksMessageMailer mailer;

	@SpringBean
	UserService userService;

	private String message;
	boolean allChecked = false;

	
	public METWorksMessagePanel(String id)
		{
		super(id);
		
		setDefaultModel(new CompoundPropertyModel(this));
		final WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		
		final AjaxCheckBox allCheckedBox;
		final ListView view = new ListView("admins", wrapUsers(userService.allAdmins()))
			{
			public void populateItem(ListItem item)
				{
				UserWrapper wrapped = (UserWrapper) item.getModelObject();
				item.add(new AjaxCheckBox("check", new PropertyModel(wrapped, "selected"))
					{
					public void onUpdate(AjaxRequestTarget target) {  } 
					});
				
				item.add(new Label("userName", wrapped.getUser().getUserName()));
				item.add(new Label("name", wrapped.getUser().getFullName()));
				item.add(new Label("email", wrapped.getUser().getEmail()));
				item.add(OddEvenAttributeModifier.create(item));
				}
			};
		view.setReuseItems(true);

		container.add(allCheckedBox = new AjaxCheckBox("allChecked")
			{
			public void onUpdate(AjaxRequestTarget target)
				{
				setAllSelected(view, allChecked);
				target.add(container);
				}
			});

		add(new AjaxUpdatingRequiredTextField("message", "onblur"));
		// issue 39
		add(new AjaxLink <Void>("sendMessage")
			{
				public void onClick(AjaxRequestTarget target)
					{
					this.setEnabled(false);
					for (UserWrapper user : getSelectedItems(view))
						{
						mailer.sendMessage(new METWorksMailMessage("metabolomics@med.umich.edu", user.getUser().getEmail(), "METLIMS Mail Message", message));
						}
					target.add(this);
					}
			});
		container.add(view);
		add(container);
		}

	public void setMessage(String message)
		{
		this.message = message;
		}

	public String getMessage()
		{
		return message;
		}

	public boolean getAllChecked()
		{
		return allChecked;
		}

	public boolean isAllChecked()
		{
		return allChecked;
		}

	private void setAllSelected(ListView listView, boolean value)
		{
		List<UserWrapper> list = (List<UserWrapper>) listView.getList();
		for (UserWrapper user : list)
			user.setSelected(value);
		}

	private List<UserWrapper> getSelectedItems(ListView listView)
		{
		List<UserWrapper> list = (List<UserWrapper>) listView.getList();
		List<UserWrapper> selectedItems = new ArrayList<UserWrapper>();

		for (UserWrapper entry : list)
			if (entry.getSelected())
				selectedItems.add(entry);

		return selectedItems;
		}

	private List<UserWrapper> wrapUsers(List<UserDTO> list)
		{
		List<UserWrapper> wrappedUsers = new ArrayList<UserWrapper>();
		for (UserDTO user : list)
			wrappedUsers.add(new UserWrapper(user));
		return wrappedUsers;
		}

	private class UserWrapper implements Serializable
		{
		private UserDTO person;
		private Boolean selected = Boolean.FALSE;

		public UserWrapper(UserDTO user)
			{
			this.person = user;
			}

		public Boolean getSelected()
			{
			return selected;
			}

		public void setSelected(Boolean selected)
			{
			this.selected = selected;
			}

		public UserDTO getUser()
			{
			return person;
			}

		public void setUser(UserDTO user)
			{
			person = user;
			}

		public String toString()
			{
			return person.getFullName() + ": " + selected;
			}
		}
	}
