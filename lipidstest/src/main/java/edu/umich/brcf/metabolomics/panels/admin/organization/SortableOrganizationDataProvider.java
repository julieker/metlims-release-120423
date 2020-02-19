////////////////////////////////////////////////////
// SortableOrganizationDataProvider.java
// Written by Jan Wigginton, Oct 27, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.organization;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import edu.umich.brcf.shared.layers.domain.Organization;


public class SortableOrganizationDataProvider extends SortableDataProvider<Organization, String> 
	{
	IModel <List<Organization>> organizations;
	
	// JAK put there for successful mvn build issue 19
	@Override
	public void detach()
		{
		super.detach();	
		}
	public SortableOrganizationDataProvider() { } 
	
	public SortableOrganizationDataProvider(IModel<List<Organization>> peaks)
		{
		organizations = peaks;
		setSort("orgName", SortOrder.DESCENDING);
		}
	
	
	public Iterator<? extends Organization>  iterator (long first, long count) 
		{
		List<Organization> data = organizations.getObject(); 
		
		Collections.sort(data, new Comparator<Organization>()
			{
			public int compare(Organization org1, Organization org2)
				{
				int dir = getSort().isAscending() ? 1 : -1;
				
				if ("orgName".equals(getSort().getProperty()))
					return dir * (org1.getOrgName().toUpperCase().compareTo(org2.getOrgName().toUpperCase()));
				
				if ("organizationId".equals(getSort().getProperty()))
					return dir * (org1.getOrganizationId().compareTo(org2.getOrganizationId()));
				
				return -1 * dir;
				}
			});
		
		return data.subList((short) first, (short) Math.min(first + count,  data.size())).iterator();
		}
		
	
	public IModel<Organization> model(final Organization object)
		{
		return new LoadableDetachableModel<Organization>()
			{
			@Override
			protected Organization load() { return object; }
			};
		}
	
	
	private List<Organization> getSortedList()
		{
		List <Organization> list = organizations.getObject();
		Collections.sort(list, new Comparator<Organization>()
			{
			public int compare(Organization arg0, Organization arg1) 
				{ 
				return arg0.getOrgName().compareTo(arg1.getOrgName()); 
				}
			});
		
		return list;
		}
	
	
	@Override
	public long size() { return (long) organizations.getObject().size(); }
	}

