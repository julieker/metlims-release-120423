////////////////////////////////////////////////////
// OrganizationClientsWriter.java
// Written by Jan Wigginton, Aug 22, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.organization;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.util.interfaces.IWriteableTextData;
import edu.umich.brcf.shared.util.io.TabbedDataWriter;



public class OrganizationClientsWriter extends TabbedDataWriter implements IWriteableTextData, Serializable
	{
	public OrganizationClientsWriter() {}
	
	List<String> colTitles = Arrays.asList(new String [] {"Dept", "Lab", "Investigator", "Contact"});
	List<Client> clientList;
	String fileName;
	
	public OrganizationClientsWriter(String fullName, List<Client> lst)
		{
		fileName = fullName;
		clientList = lst;
		}

	@Override
	public String getReportFileName()
		{
		return fileName;
		}


	@Override
	public Character getDelimiter()
		{
		return '\t';	
		}

	@Override
	protected String getReportString()
		{
		StringBuilder sb = new StringBuilder();
		String delimiterAsString = getDelimiter().toString();
		
		for (Client client : clientList)
			sb.append(client.toCharDelimited(delimiterAsString) + System.getProperty("line.separator"));
		
		return sb.toString();
		}

	}
