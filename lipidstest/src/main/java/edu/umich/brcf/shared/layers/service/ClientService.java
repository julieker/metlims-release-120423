package edu.umich.brcf.shared.layers.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dto.ClientDTO;
import edu.umich.brcf.shared.layers.dao.ClientDAO;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.OrganizationDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;


@Transactional
public class ClientService
	{
	ClientDAO clientDao;
	UserDAO userDao;
	OrganizationDAO organizationDao;
	ExperimentDAO experimentDao;

	
	public List<Client> allClients()
		{
		return clientDao.allClients();
		}

	public List<Client> allClientsSmall()
		{
		return clientDao.allClientsSmall();
		}
	
	public List<Client> allClientsForOrganization(String orgId)
		{
		return clientDao.allClientsForOrganization(orgId);
		}

	public List<String> allClientNames()
		{
		return clientDao.allClientNames();
		}

	public List<String> allClientLabs(boolean withIds)
		{
		return clientDao.allClientLabs(withIds);
		}

	public List<Client> loadClientTreeWithOpenProjectsAndExperiments()
		{
		return clientDao.clientsWithOpenProjectsAndExperimentsInPriorityOrder();
		}

	
	public Client loadById(String id)
		{
		Assert.notNull(id);
		return clientDao.loadById(id);
		}

	public List<String> allContacts()
		{
		return clientDao.allContacts();
		}

	
	public List<String> allContactsWithIds()
		{
		return clientDao.allContactsWithIds();
		}
	
	
	public boolean verifyContactExists(String str)
		{
		if (StringUtils.isEmptyOrNull(str)) 
			throw new RuntimeException("Contact name can't be null");
		
		String searchStr = str;
		String clientId = StringParser.parseId(str);
		if (!StringUtils.isEmptyOrNull(clientId) && FormatVerifier.verifyFormat(Client.fullIdFormat, clientId))
			searchStr = StringParser.parseName(str);
				
		
		List<String> eidList = null; 
		try  
			{
			eidList = experimentDao.loadExperimentIdsByClientContact(searchStr);
			}
		catch (Exception e)
			{
			throw new RuntimeException("Error while loading contact with name " + searchStr +  "from database");
			}
		
		return (eidList.size() > 0);
		} 
	
	
	public Client save(ClientDTO dto)
		{
		Assert.notNull(dto);
			
	
		
		 if (StringUtils.isEmptyOrNull(dto.getId()) || "to be assigned".equals(dto.getId()))
		 {
			 if (clientDao.checkClientLabExists(dto.getLab()))
			     throw new RuntimeException("Duplicate client lab. Please enter a different client lab");
		 }
		 else if (clientDao.checkClientLabExistsAndIsNotSameItem(dto.getLab(), dto))	    
		          throw new RuntimeException("Duplicate client lab. Please enter a different client lab");

		Organization org = null;
		try { org = organizationDao.loadById(dto.getOrganizationID()); org.getOrgAddress(); }
		catch (Exception e) 
			{
			throw new RuntimeException("Organization field cannot be empty and must refer to an existing organization");
			}
		
		
		User investigator = null;
		try { investigator = userDao.loadById(dto.getInvestigatorID()); investigator.getEmail(); }
		catch (Exception e)
			{
			throw new RuntimeException("Investigator field cannot be empty and must refer to an existing client/user");
			}
		
		
		User contact = null;
		try { contact = userDao.loadById(dto.getContact()); contact.getEmail(); } 
		catch (Exception e)
			{
			throw new RuntimeException("Contact field cannot be empty and must refer to an existing client/user");
			}
		
		
		Client client = null;
		if (StringUtils.isEmptyOrNull(dto.getId()) || dto.getId().equals( "to be assigned"))
			try
				{
				client = Client.instance(dto.getDept(), dto.getLab(), dto.getOrganizationID(), investigator, contact);
				clientDao.createClient(client);
				}
			catch (Exception e) { client = null; e.printStackTrace(); }
		
		else
			try
				{
				client = clientDao.loadById(dto.getId());
				client.update(dto, investigator, contact);
				}
			catch (Exception e) { client = null; e.printStackTrace(); }
		
		
		return client;
		}

	
	public void updateClient(String id, ClientDTO dto)
		{
		Assert.notNull(id);
		Assert.notNull(dto);
		
		Client client = null;
		try
			{
			client = clientDao.loadById(id);
			client.getClientID();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new RuntimeException("Client save error : client must exist in the database");
			}
		
		User investigator = null;
		
		try {
			investigator = userDao.loadById(dto.getInvestigatorID());
			investigator.getEmail();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new RuntimeException("Client save errror : investigator must exist in the database");
			}
			
	
		
		User contact = null;
		
		try 
			{
			contact = userDao.loadById(dto.getContact());
			contact.getEmail();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new RuntimeException("Client save error : client must exist in the database ");
			}
			
		client.update(dto, investigator, contact);
		
		}

	
	public List<String> assignBarcodes(String clientID, List<String> sampleBarcodes)
		{
		return clientDao.assignBarcodes(clientID, sampleBarcodes);
		}


	public void setClientDao(ClientDAO clientDao)
		{
		Assert.notNull(clientDao);
		this.clientDao = clientDao;
		}

	
	public void setUserDao(UserDAO userDao)
		{
		this.userDao = userDao;
		}

	public void setOrganizationDao(OrganizationDAO organizationDao)
		{
		this.organizationDao = organizationDao;
		}
	
	
	public void setExperimentDao(ExperimentDAO experimentDao)
		{
		this.experimentDao = experimentDao;
		}

	
	public boolean isValidClientSearch(String cl)
		{
		Client client;
		if (StringUtils.isEmptyOrNull(cl))
			return false;

		if (FormatVerifier.verifyFormat(Client.fullIdFormat, cl.toUpperCase()))
			try { client = loadById(cl);  client.getContactName(); } 
			catch (Exception e) { client = null; }
		else
			try { client = loadById(StringParser.parseId(cl));  client.getContactName(); } 
			catch (Exception e) { client = null; }
		
		return (client != null);
		}
	}
