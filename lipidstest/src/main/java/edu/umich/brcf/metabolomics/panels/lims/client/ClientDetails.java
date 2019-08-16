/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Upgraded/cleaned up August 2016
// Revisited : October 2016 (JW)

package edu.umich.brcf.metabolomics.panels.lims.client;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
//import org.apache.wicket.markup.html.list.PageableListView;
//import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.ClientDocument;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.MyFileLink;



public final class ClientDetails extends WebPage 
	{
	@SpringBean
	private ProjectService projectService;

	
	public ClientDetails(IModel clientModel) 
		{
		this(clientModel, null);	
		}
	
	
	public ClientDetails(IModel clientModel, ModalWindow modal1) 
		{
		final Client client = (Client) clientModel.getObject();
		
		final ModalWindow modal = modal1;
		
		add(new Label("id", client.getClientID()));
		add(new Label("dept", client.getDept()));
		add(new Label("lab", client.getLab()));
		add(new Label("organizationID", client.getOrganizationID()));
		add(new Label("investigatorID", client.getInvestigator().getFirstName() + " " + client.getInvestigator().getLastName()));
		add(new Label("contact", client.getContact().getFirstName() + " " + client.getContact().getLastName()));
		
		List<ClientDocument> docList = client.getDocList();
		modal.setInitialHeight(modal.getInitialHeight() + docList.size() * 20);
		
		add(new ListView("docs", docList) 
			{
			@Override
			protected void populateItem(ListItem item) 
				{
				final ClientDocument doc = (ClientDocument) item.getModelObject();
				MyFileLink link = new MyFileLink("fileLink", new Model(doc));
				link.add(new Label("fileName", doc.getFileName()));
				item.add(link);
				}
			});
		
		add(new AjaxCancelLink("cancelButton", modal));
		}

	
		private Link buildEditLink(Client client, final ModalWindow modal1) 
			{
			return new MyPopupLink("editButton", new Model(client)) 
				{
				public void onClick() 
					{
					setResponsePage(new EditClient(getPage(), getModel(), modal1 )
						{
						@Override
						protected void onSave(Client client, AjaxRequestTarget target1) {  }
						});
					}
				};	
			}
    	
		
		private class MyPopupLink extends Link 	
			{
			private MyPopupLink(String id, IModel model) 
				{
				super(id, model);
				setPopupSettings(new PopupSettings(PopupSettings.RESIZABLE
						| PopupSettings.STATUS_BAR | PopupSettings.SCROLLBARS).setHeight(400).setWidth(600));
				}
	
			@Override
			public void onClick()  {  }

			@Override
			// issue 464
			public MarkupContainer setDefaultModel(IModel arg0) {
				// TODO Auto-generated method stub
				return (MarkupContainer) getDefaultModel();
			}
			}
		
		
		public List<Project> getProjects(Client client) 
			{
			return projectService.loadProjectByClient(client);
			}
		}
