package edu.umich.brcf.shared.util;

public class StringParser
	{

	static String name = "";
	static String id = "";

	public static String parseId(String NameWithId)
		{
		id = "";
		if (NameWithId == null)
			return id;

		if (NameWithId.indexOf("(") > 0)
			id = NameWithId.substring(NameWithId.lastIndexOf("(") + 1,
					NameWithId.lastIndexOf(")"));
		return id;
		}

	public static String parseName(String NameWithId)
		{

		name = "";
		if (NameWithId == null)
			return name;
		if (NameWithId.indexOf("(") > 0)
			name = NameWithId.substring(0, NameWithId.lastIndexOf("("));
		else
			name = NameWithId;
		return name.trim();
		}
	}
