///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ListUtils.java
//Written by Jan Wigginton September 2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.utilpackages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;

public class ListUtils<T>
	{
	public static <T> boolean hasRepeats(List<T> list)
		{
		Map<T, String> map = new HashMap<T, String>();
		for (int i = 0; i < list.size(); i++)
			map.put(list.get(i), null);

		return map.size() < list.size();
		}
	

	public static boolean isNonEmpty(List list)
		{
		return list != null && list.size() > 0;
		}
	
	
	public static <T> List<T> uniqueEntries(List<T> list)
		{
		Map<T, String> map = new HashMap<T, String>();
		for (int i = 0; i < list.size(); i++)
			map.put(list.get(i), null);

		List<T> lst = new ArrayList<T>();
		for (T key : map.keySet())
			lst.add(key);

		return lst;
		}

	
	public static String jsMessagePrint(List<String> list_to_print, String header)
		{
		StringBuilder sb = new StringBuilder();
		
		sb.append(header + "  [");
		for (int i = 0; i < list_to_print.size(); i++)
			{
			sb.append(list_to_print.get(i));
			if (i < list_to_print.size() - 1) 
				 sb.append(", ");
			}
		
		sb.append("]");
		return sb.toString();
		}

	
	public static <T> List<T> uniqueEntriesInOrder(List<T> list)
		{
		Map<Integer, T> map = new HashMap<Integer, T>();
		for (int i = 0; i < list.size(); i++)
			{
			if (map.containsValue(list.get(i)))
				continue;
			map.put(i, list.get(i));
			}
		List<Integer> keys = new ArrayList<Integer>();
		for (Integer key : map.keySet())
			keys.add(key);
		Collections.sort(keys);

		List<T> lst = new ArrayList<T>();
		for (Integer key : keys)
			lst.add(map.get(key));

		return lst;
		}

	
	public static List<String> makeEntriesUnique(List<String> lst)
		{
		List<String> uniqueList = new ArrayList<String>();
		for (int i = 0; i < lst.size(); i++)
			uniqueList.add(lst.get(i) + "_" + i);

		return uniqueList;
		}

	
	public static List<String> makeEntriesUniqueIfNeeded(
			List<String> list_to_check)
		{
		List<String> lst = ListUtils.uniqueEntries(list_to_check);

		if (lst.size() < list_to_check.size())
			return ListUtils.makeEntriesUnique(list_to_check);

		return list_to_check;
		}
	

	public static String prettyPrint(List<String> list_to_print)
		{
		StringBuilder sb = new StringBuilder();

		for (String entry : list_to_print)
			sb.append(entry + System.getProperty("line.separator"));

		return sb.toString();
		}

// issue 441
	public static <T> List<T> makeListFromObjectCollection(Collection<T> collection_to_list)
		{
		List<T> lst = new ArrayList<T>();		
		for (T entry : collection_to_list)
			lst.add(entry);
		return lst;
		}
	
	public static String bulletPrint(List<String> list_to_print, String header)
		{
		StringBuilder sb = new StringBuilder();

		sb.append(header + System.getProperty("line.separator"));
		for (String entry : list_to_print)
			sb.append("\t*\t" + entry + System.getProperty("line.separator"));

		return sb.toString();
		}
	}
