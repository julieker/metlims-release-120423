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

package edu.umich.brcf.metabolomics.panels.admin.accounts;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import edu.umich.brcf.shared.layers.dto.UserDTO;

public final class UserDetails extends Panel
	{
	public UserDetails(String id, IModel userModel, UsersPanel panel,
			final ModalWindow editModal)
		{
		super(id);
		UserDTO user = (UserDTO) userModel.getObject();
		add(new Label("id", user.getId()));
		add(new Label("name", user.getFirstName() + " " + user.getLastName()));
		add(new Label("email", user.getEmail()));
		add(new Label("userName", user.getUserName()));
		add(new Label("viewpoint", user.getViewpoint().getName()));
		add(new Label("lab", user.getLab()));
		add(new Label("phone", user.getPhone()));
		add(new Label("fax", user.getFaxNumber()));
		// add(panel.createLinkToModalWindow("edit", editModal));
		}

	// private Link buildEditLink(UserDTO user) {
	// Link link = new Link("edit", new Model(user)) {
	// public void onClick() {
	// setResponsePage(new EditUser(getPage(), getModel()));
	// }
	// };
	// return link;
	// }
	}
