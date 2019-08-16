///////////////////////////////////////
//ProjectByStartDateComparator.java
//Written by Jan Wigginton July 2015
///////////////////////////////////////

package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.shared.layers.domain.Project;


public class ProjectByStartDateComparator implements Comparator<Project> 
	{
	@Override
	public int compare(Project arg0, Project arg1) 
		{
		return arg1.getStartDate().compareTo(arg0.getStartDate());
		}
	}