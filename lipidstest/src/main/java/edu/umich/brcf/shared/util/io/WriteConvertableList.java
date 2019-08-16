////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WriteConvertableList.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;



public class WriteConvertableList implements IWriteConvertable, Serializable
	{
	private static final long serialVersionUID = -6639800415771127184L;
	List<Object> rawValues;

	WriteConvertableList()
		{
		rawValues = new ArrayList<Object>();
		}

	WriteConvertableList(List<? extends Object> raw)
		{
		rawValues = (List<Object>) raw;
		}

	@Override
	public String toCharDelimited(String delimiter)
		{
		StringBuilder line = new StringBuilder();

		for (int i = 0; i < rawValues.size(); i++)
			line.append(rawValues.get(i).toString() + delimiter);

		return line.toString();
		}

	@Override
	public String toExcelRow()
		{
		return toCharDelimited("\t");
		}
	}

/*
 * import java.util.List;
 * 
 * import edu.umich.brcf.lipidstest.interfaces.IWriteConvertable;
 * 
 * public class WriteConvertableList implements IWriteConvertable { List
 * <Object> rawValues;
 * 
 * 
 * WriteConvertableList(List <Object> raw) { rawValues = raw; }
 * 
 * 
 * @Override public String toCSVString() { StringBuilder line = new
 * StringBuilder();
 * 
 * for (int i = 0; i < rawValues.size(); i++)
 * line.append(rawValues.get(i).toString() + ", ");
 * 
 * return line.toString(); }
 * 
 * 
 * @Override public String toTSVString() { StringBuilder line = new
 * StringBuilder();
 * 
 * for (int i = 0; i < rawValues.size(); i++)
 * line.append(rawValues.get(i).toString() + "\t");
 * 
 * return line.toString(); }
 * 
 * 
 * @Override public String toExcelRow() { return toTSVString(); /* StringBuilder
 * line = new StringBuilder();
 * 
 * for (int i = 0; i < rawValues.size(); i++)
 * line.append(rawValues.get(i).toString() + ", ");
 * 
 * return line.toString();
 */