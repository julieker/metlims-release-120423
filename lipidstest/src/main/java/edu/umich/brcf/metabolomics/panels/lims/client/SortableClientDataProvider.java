////////////////////////////////////////////////////
// SortableClientDataProvider.java
// Written by Jan Wigginton, Oct 27, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.client;


import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
//import org.apache.wicket.model.Model;

import edu.umich.brcf.shared.layers.domain.Client;


public class SortableClientDataProvider extends SortableDataProvider<Client, String> 
	{
	IModel <List<Client>> clients;
	
	
	public SortableClientDataProvider(IModel<List<Client>> peaks)
		{
		clients = peaks;
		setSort("clientName", SortOrder.DESCENDING);
		}
	
		
	public Iterator<? extends Client>  iterator (long first, long count) 
		{
		List<Client> data = clients.getObject(); 

		Collections.sort(data, new Comparator<Client>()
			{
			public int compare(Client peak1, Client peak2)
				{
				int dir = getSort().isAscending() ? 1 : -1;
				
				if ("clientName".equals(getSort().getProperty()))
					return dir * (peak1.getLab().toUpperCase().compareTo(peak2.getLab().toUpperCase()));
				
				if ("clientID".equals(getSort().getProperty()))
					return dir * (peak1.getClientID().compareTo(peak2.getClientID()));
			
				return -1 * dir;
				}
			});
	
		return data.subList((short) first, (short) Math.min(first + count,  data.size())).iterator();
		}
	
	
	public IModel<Client> model(final Client object)
		{
		return new LoadableDetachableModel<Client>()
			{
			@Override
			protected Client load() { return object; }
			};
		}
	
	
	private List<Client> getSortedList()
		{
		List <Client> list = clients.getObject();
		
		Collections.sort(list, new Comparator<Client>()
			{
			public int compare(Client arg0, Client arg1)
				{
				return arg0.getClientID().compareTo(arg1.getClientID());
				}
			});
		
		return list;
		}
	
	
	@Override
	public long size() { return (long) clients.getObject().size(); }
	}
