///////////////////////////////////////
//SortableProjectDataProvider.java
//Written by Jan Wigginton August 2015
///////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lims.project;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import edu.umich.brcf.shared.layers.domain.Project;



public class SortableProjectDataProvider extends SortableDataProvider<Project, String> 
	{
	IModel <List<Project>> projects;

	
	public SortableProjectDataProvider(IModel<List<Project>> peaks)
		{
		projects = peaks;
		setSort("projectName", SortOrder.DESCENDING);
		}
	
	public Iterator<? extends Project>  iterator (long first, long count) 
		{
		List<Project> data = projects.getObject(); // new ArrayList<Project>(projects);
		
		Collections.sort(data, new Comparator<Project>()
			{
			public int compare(Project peak1, Project peak2)
				{
				
				int dir = getSort().isAscending() ? 1 : -1;
			
				if ("projectName".equals(getSort().getProperty()))
					return dir * (peak1.getProjectName().toUpperCase().compareTo(peak2.getProjectName().toUpperCase()));
				
				if ("projectID".equals(getSort().getProperty()))
					return dir * (peak1.getProjectID().compareTo(peak2.getProjectID()));
				//if ("knownStatus".equals(getSort().getProperty()))
				//	return dir * (peak1.getKnownStatus().compareTo(peak2.getKnownStatus()));
				
				//	if (peak1.getSamplePeaks(idx).getPeakArea() == null)
				return -1 * dir;
				}
			});
		
		return data.subList((short) first, (short) Math.min(first + count,  data.size())).iterator();
		}

	
    /*
	@Override
	public IModel<Project> model(Ms2PeakSet object) 
		{
		final String peakId = ((Ms2PeakSet) object).getPeakSetId();
		// REVIEW : This will be inefficient -- replace with Model
		final Ms2PeakSet object2 = (Ms2PeakSet) object;
		
		return new LoadableDetachableModel<Ms2PeakSet>() 
			{
			@Override
			protected Ms2PeakSet load() 
				{
				// REVIEW Make this work
				return object2;
				}
			};
		} */
	
	public IModel<Project> model(final Project object)
		{
		return new LoadableDetachableModel<Project>()
			{
			@Override
			protected Project load() { return object; }
			};
		}
	
	
	
	private List<Project> getSortedList()
		{
		List <Project> list = projects.getObject();
		
		Collections.sort(list, new Comparator<Project>()
	        {
	        public int compare(Project arg0, Project arg1)
	        	{
	        	// REVIEW : DOES THIS WORK?
	        	return arg0.getProjectID().compareTo(arg1.getProjectID());
	            ////return arg0.getLipidClass().compareTo(arg1.getLipidClass());
	        	//return -1;
	        	}
	        });
		
		return list;
		}


	@Override
	public long size()
		{
		return (long) projects.getObject().size();
		}
	}
