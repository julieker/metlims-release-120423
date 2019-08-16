package edu.umich.brcf.shared.layers.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dao.LocationDAO;
import edu.umich.brcf.shared.layers.domain.Location;
import edu.umich.brcf.shared.util.io.StringUtils;


@Transactional
public class LocationService
	{
	LocationDAO locationDao;

	public Location loadById(String id)
		{
		Assert.notNull(id);
		return locationDao.loadById(id);
		}

	public List<String> getDistinctUnitsCapitalized()
		{
		List<String> lst;
		List<String> newList = new ArrayList<String>();
		lst = locationDao.getDistinctUnits();
		for (int i = 0; i < lst.size(); i++)
			newList.add(StringUtils.capitalize(lst.get(i)));

		return newList;
		}

	public Map<String, String> getNamesForLocationIds(List<String> ids)
		{
		return locationDao.getNamesForLocationIds(ids);
		}

	public List<String> getDistinctUnits()
		{
		return locationDao.getDistinctUnits();
		}

	public List<String> getDistinctUnitsForSamples()
		{
		return locationDao.getDistinctUnitsForSamples();
		}

	public List<String> getLocationsForSamples()
		{
		return locationDao.getLocationsForSamples();
		}

	public List<Location> getLocationsByUnit(String unit)
		{
		return locationDao.getLocationsByUnit(unit);
		}

	public List<Location> getLocationsByUnitForSamples(String unit)
		{
		return locationDao.getLocationsByUnit("-80 freezer");
		}

	public List<String> getLocationNamesByUnit(String unit)
		{
		List<String> names = new ArrayList<String>();
		List<Location> locations = getLocationsByUnit(unit);

		for (int i = 0; i < locations.size(); i++)
			names.add(locations.get(i).getLocationId());

		return names;
		}
	

	public List<String> getSampleLocationNamesByUnit(String unit)
		{
		List<String> names = new ArrayList<String>();
		List<Location> locations = getLocationsByUnitForSamples(unit);

		names.add("LC0000");
		for (int i = 0; i < locations.size(); i++)
			names.add(locations.get(i).getLocationId());

		return names;
		}
	

	public void setLocationDao(LocationDAO locationDao)
		{
		this.locationDao = locationDao;
		}
	}
