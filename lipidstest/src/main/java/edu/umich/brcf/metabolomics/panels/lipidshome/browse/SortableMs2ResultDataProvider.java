///////////////////////////////////
//SortableMS2ResultDataProvider.java
//Written by Jan Wigginton May 2015
///////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;


public class SortableMs2ResultDataProvider extends SortableDataProvider<Ms2PeakSet, String>
	{
	List<Ms2PeakSet> peakSets;

	public SortableMs2ResultDataProvider(List<Ms2PeakSet> peaks)
		{
		peakSets = peaks;
		setSort("lipidName", SortOrder.ASCENDING);
		}
		

	public IModel<Ms2PeakSet> model(Ms2PeakSet object)
		{
		return Model.of(object);
		}
	

	private List<Ms2PeakSet> getSortedList()
		{
		List<Ms2PeakSet> list = peakSets;

		Collections.sort(list, new Comparator<Ms2PeakSet>()
			{
			public int compare(Ms2PeakSet arg0, Ms2PeakSet arg1)
				{
				return arg0.getLipidClass().compareTo(arg1.getLipidClass());
				}
		});

		return list;
		}

	public Iterator<? extends Ms2PeakSet>  iterator (long first, long count) 
	{
	List<Ms2PeakSet> data = new ArrayList<Ms2PeakSet>(peakSets);
	
	Collections.sort(data, new Comparator<Ms2PeakSet>()
		{
		public int compare(Ms2PeakSet peak1, Ms2PeakSet peak2)
			{
			if (peak1 == null || peak2 == null)
				System.out.println("Null peak");
			
			int dir = getSort().isAscending() ? 1 : -1;
		
			if ("peakSetId".equals(getSort().getProperty()))
				return dir * (peak1.getPeakSetId().compareTo(peak2.getPeakSetId()));
			
			if ("lipidName".equals(getSort().getProperty()))
				return dir * (peak1.getLipidName().compareTo(peak2.getLipidName()));
			
			if ("startMass".equals(getSort().getProperty()))
				{
				if (peak1.getStartMass() == null || peak2.getStartMass() == null)
					return dir;
				return dir * (peak1.getStartMass().compareTo(peak2.getStartMass()));
				}
			
			if ("endMass".equals(getSort().getProperty()))
				{
				if (peak1.getEndMass() == null || peak2.getEndMass() == null)
					return dir;
				
				return dir * (peak1.getEndMass().compareTo(peak2.getEndMass()));
				}
			
			if ("expectedRt".equals(getSort().getProperty()))
				{
				if (peak1.getExpectedRt() == null || peak2.getExpectedRt() == null)
					return dir;
				return dir * (peak1.getExpectedRt().compareTo(peak2.getExpectedRt()));
				}
			
			if ("lipidClass".equals(getSort().getProperty()))
				return dir * (peak1.getLipidClass().compareTo(peak2.getLipidClass()));
			
			if ("knownStatus".equals(getSort().getProperty()))
				return dir * (peak1.getKnownStatus().compareTo(peak2.getKnownStatus()));
			
			else
				{
				Integer idx = 0;
				try  
					{ 
					idx = StringUtils.extractIntegerPortion(getSort().getProperty(), 0);
					if (idx > peak1.getSamplePeaks().size() || idx > peak2.getSamplePeaks().size()) 
						idx = 0;
					if (idx < 0)
						idx = 0;
					}
				catch (METWorksException e) { idx = 0; }
			
				if (peak1.getSamplePeaks(idx).getPeakArea() == null)
					return -1 * dir;
				
				if (peak2.getSamplePeaks(idx).getPeakArea() == null)
					return dir;
				
				return dir * (peak1.getSamplePeaks(idx).getPeakArea().compareTo(peak2.getSamplePeaks(idx).getPeakArea()));
				}
			}
		});
	//.xls
	return data.subList((short) first, (short) Math.min(first + count,  data.size())).iterator();
	//return dataSet.getPeakSets().iterator();
	}




	@Override
	public long size()
		{
		return peakSets.size();
		}
	}
