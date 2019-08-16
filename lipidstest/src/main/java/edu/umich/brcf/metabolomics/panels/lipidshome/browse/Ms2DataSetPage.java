package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Ms2DataSetPage.java
//Written by Jan Wigginton 02/10/15,  Rewritten 05/01/15
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.wicket.markup.html.WebPage;

import edu.umich.brcf.shared.util.METWorksException;


public class Ms2DataSetPage extends WebPage
	{
	public Ms2DataSetPage(String id, WebPage backPage, String dataSetId)
		{
		add(new Ms2DataSetPanel("uploadResults", backPage, dataSetId));
		}
	
	
	public Ms2DataSetPage(String id, WebPage backPage,  String expId, String uploadedFileName,  Calendar runDate, String ionMode, 
	String dataNotation, ArrayList<Integer> colIndices) throws METWorksException
		{
		try 
			{
			add(new Ms2DataSetPanel("uploadResults", backPage, expId, uploadedFileName, runDate, ionMode, dataNotation, colIndices));
			}
		catch (METWorksException e)
			{
			throw new METWorksException(e.getMetworksMessage()); 
			}
		}
	}

