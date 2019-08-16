///////////////////////////////////////
//LocationDAO.java
//Written by Jan Wigginton, July 2015
////////////////////////////////////////

package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.Location;

@Repository
public class LocationDAO extends BaseDAO
	{
	public void createLocation(Location loc)
		{
		getEntityManager().persist(loc);
		}

	
	public void deleteLocation(Location location)
		{
		getEntityManager().remove(location);
		}

	
	public List<Location> allLocations()
		{
		return getEntityManager().createQuery("from Location").getResultList();
		}

	// epigenomics.
	public Location getLocationById(String id)
		{
		return getEntityManager().find(Location.class, id);
		}

	
	public Location loadById(String id)
		{
		return getEntityManager().find(Location.class, id);
		}

	
	public List<String> getDistinctUnits()
		{
		Query query = getEntityManager().createNativeQuery("select distinct(unit) from locations");
		return query.getResultList();
		}

	
	public List<String> getDistinctUnitsForSamples()
		{
		List<String> locList = new ArrayList<String>();
		locList.add("-80 freezer"); // = query.getResultList();
		return locList;
		}

	
	public List<Location> getLocationsByUnit(String unit)
		{
		List<Location> lst = getEntityManager().createQuery("from Location l where l.unit = :unit order by 1")
				.setParameter("unit", unit).getResultList();
		
		return lst;
		}

	
	public List<Location> getLocationsByUnitForSamples(String unit)
		{
		List<Location> lst = getEntityManager().createQuery("from Location l where l.unit = :unit order by 1")
				.setParameter("unit", unit).getResultList();
		
		return lst;
		}

	
	public List<String> getLocationsForSamples()
		{
		List<String> units = getDistinctUnitsForSamples();

		String unitsForSamples = "('";
		for (String unit : units)
			unitsForSamples += unit + "'";
			
		unitsForSamples += ")";

		Query query = getEntityManager().createNativeQuery("select cast(l.locationid as VARCHAR2(6)) from Locations l  where l.unit in " + unitsForSamples);
		return query.getResultList();
		}

	
	public Map<String, String> getNamesForLocationIds(List<String> ids)
		{
		Map<String, String> descriptions = new HashMap<String, String>();
		for (String id : ids)
			try
				{
				Location l = getLocationById(id);
				String description = (l == null ? "" : l.getDescription());
				descriptions.put(id, description);
				} 
			catch (Exception e) {  }
	
		return descriptions;
		}
	}
