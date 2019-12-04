package edu.umich.brcf.metabolomics.panels.admin.accounts;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.METWorksModalContentPanel;
import edu.umich.brcf.shared.panels.utilitypanels.METWorksModalWindow;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;


public class UsersPanel extends Panel 
	{
	@SpringBean
	UserService userService;

	WebMarkupContainer container; 
	IModel <List<UserDTO>> usersModel = new LoadableDetachableModel() 
		{
		protected Object load() { return userService.allUsers(); }
		};

		
	public UsersPanel(String id) 
		{
		super(id);
		setOutputMarkupId(true);
		
		container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);
		
		ModalWindow modal1 = new ModalWindow("modal1");
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
			{
			@Override
			public void onClose(AjaxRequestTarget target)  {    target.add(container);  }
			});
		add(modal1);
		
		ModalWindow modal2 = new ModalWindow("modal2");
		add(modal2);
		
		final PageableListView<UserDTO> listView = createListView("users", container, this, modal1, modal2);
		container.add(listView);
		
		container.add(buildLinkToModal("createUser", modal1, new UserDTO()));
		container.add(new PagingNavigator("navigator", listView));
		}

	
	private IndicatingAjaxLink buildLinkToModal(final String linkID,final ModalWindow modal1, final UserDTO dto)
		{
		// issue 39
		return new IndicatingAjaxLink <Void>(linkID)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				modal1.setInitialWidth(600);
				modal1.setInitialHeight("changePassword".equals(linkID) ? 340 : 500);

				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage()
						{
						if ("createUser".equals(linkID))
							return new UserRegistrationPage(getPage(), new UserDTO(), modal1)
								{
								@Override
								protected void onSave(UserDTO dto, AjaxRequestTarget target)  {  }
								
								@Override 
								public boolean isEnabled()
									{
									String userId = (((MedWorksSession) getSession()).getCurrentUserId());
									return (userService.isAccountAdmin(userId));
									}
								};
								
						else if ("editUser".equals(linkID))		
							return new UserRegistrationPage(getPage(), dto,  modal1)
								{
								@Override
								protected void onSave(UserDTO dto, AjaxRequestTarget target) {  }
								
								@Override 
								public boolean isEnabled()
									{
									String userId = (((MedWorksSession) getSession()).getCurrentUserId());
									return (userService.isAccountAdmin(userId) || dto.getId().equals(userId));
									}
								};
						
						return new ChangePasswordPage("changePassword", dto, modal1) //"changePassword", dto, modal1)
							{
							@Override
							protected void onSave(UserDTO dto, AjaxRequestTarget target) {   }
							
							@Override 
							public boolean isEnabled()
								{
								String userId = (((MedWorksSession) getSession()).getCurrentUserId());
								return (userService.isAccountAdmin(userId) || dto.getId().equals(userId));
								}
							};
						}
					});

				modal1.show(target);
				}
			};
		}

	
	private PageableListView<UserDTO> createListView(String id, final WebMarkupContainer wmc, final UsersPanel panel, final ModalWindow modal1, final ModalWindow modal2)
			{
			return new PageableListView<UserDTO> ("users", usersModel, 15) 
				{
				public void populateItem(final ListItem<UserDTO>  listItem) 
					{
					final UserDTO user = (UserDTO) listItem.getModelObject();

				//	final ModalWindow editModal = createEditModal("editModal", user, panel, wmc);
					final ModalWindow detailModal = createDetailModal("detailModal", user,  panel);

					//listItem.add(createLinkToModalWindow("edit", editModal));
					listItem.add(buildLinkToModal("editUser", modal1, user));
					listItem.add(createLinkToModalWindow("details", detailModal).add(new Label("userName", user.getUserName())));

					listItem.add(detailModal);
				//	listItem.add(editModal);
					listItem.add(buildLinkToModal("changePassword", modal2, user));
					listItem.add(new Label("name", new Model<String>(user.getLastName() + ", " + user.getFirstName())));
					listItem.add(new Label("phone", new Model<String>(user.getPhone())));
					listItem.add(new Label("level", new Model<String>(user.getViewpoint().getName())));
					
					listItem.add(createRemoveLink("remove", user, wmc));
					
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}
	
	
	public List<UserDTO> getUsers() 
		{
		return userService.allUsers();
		}
	
	
	Link createRemoveLink(String id, final UserDTO user, final WebMarkupContainer wmc)
		{
		Link lnk = new Link <Void>("remove", new Model(user)) 
			{
			@Override
			public boolean isEnabled()
				{
				String userId = (((MedWorksSession) getSession()).getCurrentUserId());
				return userService.isAccountAdmin(userId);
				}
		
			@Override
			public void onClick()
				{
				//System.out.println("Here we would delete");
				userService.delete(user);
				}
			};

		String confirmMsg = "Are you sure that you would like to delete user " + user.getUserName() + "?";
		lnk.add(new AttributeModifier("onclick", "return confirm('" + confirmMsg + "');" ));
		return lnk;
		}
	
	
	
	ModalWindow createDetailModal(String id, UserDTO user,  UsersPanel panel)
		{
		ModalWindow modal = createModalWindow(id, user, new UserDetails("modaldata", new Model(user), panel, null), panel, 
				"User : " + user.getFullName(), METWorksModalContentPanel.MODE_CLOSE_BUTTON_ONLY);
		
		modal.setInitialHeight(300);
		modal.setInitialWidth(500);
		return modal;
		}

	
	
	public AjaxLink createLinkToModalWindow(final String linkName, final ModalWindow modal) 
		{
		// issue 39
		return new AjaxLink <Void> (linkName) 
			{
			@Override
			public void onClick(AjaxRequestTarget target) { modal.show(target); }
			@Override
			public boolean isVisible()
				{
				String userId = (((MedWorksSession) getSession()).getCurrentUserId());
				return (!linkName.equals("createUser") || userService.isTrustedAdmin(userId));
				}
			};
		}

	
	public ModalWindow createModalWindow(String modalId, final UserDTO user, final Panel contentPanel,
			final UsersPanel panel, String userName, final int mode) 
		{
		final ModalWindow selectModalWindow = new METWorksModalWindow(modalId, userName, contentPanel, mode) 
			{
			@Override
			protected void onSave(AjaxRequestTarget target, IModel model) 
				{
				UserDTO myBean = (UserDTO) model.getObject();
				try { userService.saveOrUpdateUser(myBean); }
				catch (Exception e) { }
				
				target.add(panel);
			//	close(target);
				}

			protected void onCancel(AjaxRequestTarget target)  { close(target); }
			};

		selectModalWindow.setInitialHeight(400);
		selectModalWindow.setInitialWidth(650);
		selectModalWindow.setOutputMarkupPlaceholderTag(true);
	
		return selectModalWindow;
		}
	}





/*
public class UsersPanel extends Panel 
	{
	@SpringBean
	UserService userService;

	
	IModel <List<UserDTO>> usersModel = new LoadableDetachableModel() 
		{
		protected Object load() { return userService.allUsers(); }
		};

		
	public UsersPanel(String id) 
		{
		super(id);
		setOutputMarkupId(true);
		
		final WebMarkupContainer wmc = new WebMarkupContainer("container");
		wmc.setOutputMarkupId(true);
		add(wmc);
		
		ModalWindow modal1 = new ModalWindow("modal1");
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
			{
			@Override
			public void onClose(AjaxRequestTarget target)  {    target.add(wmc);  }
			});
		add(modal1);
		
		ModalWindow modal2 = new ModalWindow("modal2");
		add(modal2);
		
		final PageableListView<UserDTO> listView = createListView("users", wmc, this, modal1, modal2);
		wmc.add(listView);
		
		wmc.add(buildLinkToModal("createUser", modal1, new UserDTO()));
		wmc.add(new PagingNavigator("navigator", listView));
		}

	
	private IndicatingAjaxLink buildLinkToModal(final String linkID,final ModalWindow modal1, final UserDTO dto)
		{
		return new IndicatingAjaxLink(linkID)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				modal1.setInitialWidth(600);
				modal1.setInitialHeight("changePassword".equals(linkID) ? 340 : 500);

				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage()
						{
						if ("createUser".equals(linkID))
							return new UserRegistrationPage(getPage(), new UserDTO(), modal1)
								{
								@Override
								protected void onSave(UserDTO dto, AjaxRequestTarget target)  {  }
								
								@Override 
								public boolean isEnabled()
									{
									String userId = (((MedWorksSession) getSession()).getCurrentUserId());
									return (userService.isAccountAdmin(userId));
									}
								};
								
						else if ("editUser".equals(linkID))		
							return new UserRegistrationPage(getPage(), dto,  modal1)
								{
								@Override
								protected void onSave(UserDTO dto, AjaxRequestTarget target) {  }
								
								@Override 
								public boolean isEnabled()
									{
									String userId = (((MedWorksSession) getSession()).getCurrentUserId());
									return (userService.isAccountAdmin(userId) || dto.getId().equals(userId));
									}
								};
						
						return new ChangePasswordPage("changePassword", dto, modal1) //"changePassword", dto, modal1)
							{
							@Override
							protected void onSave(UserDTO dto, AjaxRequestTarget target) {   }
							
							@Override 
							public boolean isEnabled()
								{
								String userId = (((MedWorksSession) getSession()).getCurrentUserId());
								return (userService.isAccountAdmin(userId) || dto.getId().equals(userId));
								}
							};
						}
					});

				modal1.show(target);
				}
			};
		}

	
	private PageableListView<UserDTO> createListView(String id, final WebMarkupContainer wmc, final UsersPanel panel, final ModalWindow modal1, final ModalWindow modal2)
			{
			return new PageableListView<UserDTO> ("users", usersModel, 15) 
				{
				public void populateItem(final ListItem<UserDTO>  listItem) 
					{
					final UserDTO user = (UserDTO) listItem.getModelObject();

				//	final ModalWindow editModal = createEditModal("editModal", user, panel, wmc);
					final ModalWindow detailModal = createDetailModal("detailModal", user,  panel);

					//listItem.add(createLinkToModalWindow("edit", editModal));
					listItem.add(buildLinkToModal("editUser", modal1, user));
					listItem.add(createLinkToModalWindow("details", detailModal).add(new Label("userName", user.getUserName())));

					listItem.add(detailModal);
				//	listItem.add(editModal);
					listItem.add(buildLinkToModal("changePassword", modal2, user));
					listItem.add(new Label("name", new Model<String>(user.getLastName() + ", " + user.getFirstName())));
					listItem.add(new Label("phone", new Model<String>(user.getPhone())));
					listItem.add(new Label("level", new Model<String>(user.getViewpoint().getName())));
					
					listItem.add(createRemoveLink("remove", user, wmc));
					
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}
	
	
	public List<UserDTO> getUsers() 
		{
		return userService.allUsers();
		}
	
	
	Link createRemoveLink(String id, final UserDTO user, final WebMarkupContainer wmc)
		{
		Link lnk = new Link("remove", new Model(user)) 
			{
			@Override
			public boolean isEnabled	()
				{
				String userId = (((MedWorksSession) getSession()).getCurrentUserId());
				return userService.isAccountAdmin(userId);
				}
			
			@Override
			public void onClick() 
				{
				userService.delete(user);
				setResponsePage(getPage());
				}
			};

		//String confirmMsg = "Are you sure that you would like to delete user " + user.getUserName();
		//lnk.add(new SimpleAttributeModifier("onclick", "return confirm('" + "a" + "');" ));
		return lnk;
		}
	
	ModalWindow createDetailModal(String id, UserDTO user,  UsersPanel panel)
		{
		ModalWindow modal = createModalWindow(id, user, new UserDetails("modaldata", new Model(user), panel, null), panel, 
				"User : " + user.getFullName(), METWorksModalContentPanel.MODE_CLOSE_BUTTON_ONLY);
		
		modal.setInitialHeight(300);
		modal.setInitialWidth(500);
		return modal;
		}

	
	
	public AjaxLink createLinkToModalWindow(final String linkName, final ModalWindow modal) 
		{
		return new AjaxLink(linkName) 
			{
			@Override
			public void onClick(AjaxRequestTarget target) { modal.show(target); }
			
			@Override
			public boolean isVisible()
				{
				String userId = (((MedWorksSession) getSession()).getCurrentUserId());
				return (!linkName.equals("createUser") || userService.isTrustedAdmin(userId));
				}
			};
		}

	
	public ModalWindow createModalWindow(String modalId, final UserDTO user, final Panel contentPanel,
			final UsersPanel panel, String userName, final int mode) 
		{
		final ModalWindow selectModalWindow = new METWorksModalWindow(modalId, userName, contentPanel, mode) 
			{
			@Override
			protected void onSave(AjaxRequestTarget target, IModel model) 
				{
				UserDTO myBean = (UserDTO) model.getObject();
				try { userService.saveOrUpdateUser(myBean); }
				catch (Exception e) { }
				
				target.add(panel);
			//	close(target);
				}

			protected void onCancel(AjaxRequestTarget target)  { close(target); }
			};

		selectModalWindow.setInitialHeight(400);
		selectModalWindow.setInitialWidth(650);
		selectModalWindow.setOutputMarkupPlaceholderTag(true);
	
		return selectModalWindow;
		}
	}


*/




