package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.dto.ClientDTO;
import edu.umich.brcf.shared.layers.dto.ProjectDTO;


//.exp.expId
@Repository
public class ClientDAO extends BaseDAO
	{
	public void createClient(Client client)
		{
		getEntityManager().persist(client);
		}

	public void deleteClient(Client client)
		{
		getEntityManager().remove(client);
		}

	
	public List<Client> allClients()
		{
		List<Client> clientList = getEntityManager().createQuery("from Client").getResultList();
		
		for (Client client : clientList)
			initializeTheKids(client, new String[] { "investigator", "contact" });
			
		return clientList;
		}
	
	
	public List<Client> allClientsSmall()
		{
		List<Client> clientList = getEntityManager().createQuery("from Client").getResultList();
		
		return clientList;
		}

	
	public List<Client> allClientsForOrganization(String orgId)
		{
		List<Client> clientList = getEntityManager().createQuery("from Client s where s.organizationID = :orgId order by s.dept")
		  .setParameter("orgId", orgId)	.getResultList();
		
		for (Client client : clientList) 
			initializeTheKids(client,new String[] { "investigator", "contact" });
		
		return clientList;
		}

	
	public List<String> allClientNames()
		{
		Query query = getEntityManager().createNativeQuery("select c.lab||' ('||c.client_id||')' from client c order by 1");
		List<String> orgList = query.getResultList();
		return orgList;
		}
	
	
	public List<String> allClientLabs(boolean withIds)
		{
		List<String> clients = new ArrayList();
		List<Client> clientList = getEntityManager().createQuery("from Client").getResultList();
		
			
		for (Client client : clientList)
			{
			String clientId = client.getClientID();
			String labName = client.getLab();
	
			String name = labName;
			if (withIds) 
				name += (" (" + clientId + ")");
			clients.add(name);
			}
		
		return clients;
		}
	
	
	public Client loadById(String id)
		{
		Client client = getEntityManager().find(Client.class, id);
		initializeTheKids(client, new String[] { "investigator" });
		initializeTheKids(client, new String[] { "contact", "docList" });
		return client;
		}

	
	public List<String> assignBarcodes(String clientID,
			List<String> sampleBarcodes)
		{
		List<String> duplicates = new ArrayList<String>();
		int i = 0;
		for (String barcode : sampleBarcodes)
			{
			if (isRegisteredBarcode(barcode))
				duplicates.add(barcode);
			else
				{					
				Query query = getEntityManager().createNativeQuery("insert into unrecoveredsamples values ( ?1 ,?2)"  )
						.setParameter(1, barcode).setParameter(2, clientID);		
				i += query.executeUpdate();
				}
			}

		System.out.println("Number of barcodes registered: " + i);
		return duplicates;
		}
	
	
	private boolean isRegisteredBarcode(String barcode)
		{
		Query query = getEntityManager().createNativeQuery("select client_id from unrecoveredsamples where sample_id='" + barcode + "'");
		
		List<String> sid = query.getResultList();
		
		return ((sid.size() > 0)) ? true : false;
		}

	
	public List<Client> clientsWithOpenProjectsAndExperimentsInPriorityOrder()
		{
		List<Client> clientList = getEntityManager().createQuery("from Client").getResultList();
		
		initializeClientListForTree(clientList);
		
		List<Client> newClientList = new ArrayList<Client>();
		for (Client client : clientList)
			if (client.getProjectList() != null && client.getProjectList().size() > 0)
				newClientList.add(client);
		
		return newClientList;
		}

	
	private void initializeClientListForTree(List<Client> clientList)
		{
		for (Client client : clientList)
			{
			initializeTheKids(client, new String[] { "projectList", "contact","investigator" });
			
			for (Project project : client.getProjectList())
				{
				Hibernate.initialize(project.getExperimentList());
				for (Experiment experiment : project.getExperimentList())
					Hibernate.initialize(experiment.getSampleList());
				}
			}
		}
	
	
	public List<String> allContacts()
		{
		return allContacts(false);
		}

	
	public List<String> allContactsWithName(String name)
		{
		List<String> allContacts = allContacts(true);
		List<String> contactsWithName = new ArrayList<String>();
		
		for (String contact :allContacts)
			{
			if (contact.startsWith(name))
				contactsWithName.add(contact);
			}
		
		return contactsWithName;
		}
	
	
	public List<String> allContactsWithIds()
		{
		return allContacts(true);
		}

	
	private List<String> allContacts(boolean withIds)
		{
		List<String> contacts = new ArrayList();
		List<Client> clientList = getEntityManager().createQuery("from Client").getResultList();
		
		for (Client client : clientList)
			initializeTheKids(client, new String[] { "contact", "investigator" });
		
		Map<String, String> map = new HashMap<String, String>();
		
		String name = null;
		for (Client client : clientList)
			{
			if (withIds)
				name = client.getContactNameByLast() + ("(" + client.getClientID() + ")");
			else
				name = client.getContactNameByLast();
		
			if (map.get(name) == null)
				{
				map.put(name, "");
				contacts.add(name);
				}
		//	if (!contacts.contains(name))
		//		contacts.add(name);
			
			if (withIds)
				name = client.getInvestigatorNameByLast() + ("(" + client.getClientID() + ")");
		
			else				
				name = client.getInvestigatorNameByLast() ;
			
			if (map.get(name) == null)
				{
				map.put(name, "");
				contacts.add(name);
				}
			//if (!contacts.contains(name))
			//	contacts.add(name);
			}

		
		Collections.sort(contacts);
		
		return contacts;
		}

	
	
	public boolean checkClientLabExists(String lab)
	{
	Query query = getEntityManager().createNativeQuery("select * from client c where c.lab = ?1").setParameter(1, lab);
	
	return (query.getResultList().size() > 0);
	}
	
	
public boolean checkClientLabExistsAndIsNotSameItem(String lab, ClientDTO dto)
	{
	String currentId = dto.getId();

	Query query = getEntityManager().createNativeQuery("select * from client c where lab = ?1 and client_id <> ?2").setParameter(1, lab).setParameter(2, currentId);
	return (query.getResultList().size() > 0);
	}
	
	
	
	
	// *mchear
	/*
	private List<String> allContacts(boolean withIds)
    {
    List<String> contacts = new ArrayList();
    List<Client> clientList = getEntityManager().createQuery("from Client c order by $
    HashMap<String, String> ids = new HashMap<String, String>();

    for (Client client : clientList)
            initializeTheKids(client, new String[] { "contact", "investigator" });

    for (Client client : clientList)
            {
            String clientId = client.getClientID();
            String piName = client.getInvestigator().getUserName();
            String contactName = client.getContact().getUserName();

            if (!ids.containsKey(contactName))
                    {
                    String name = client.getContactName();
                    if (withIds)
                            name += ("(" + clientId + ")");
                    contacts.add(name);
                    ids.put(contactName, "");
                    }

            if (!ids.containsKey(piName))
                    {
                    String name = client.getInvestigatorName();
                    if (withIds)
                            name += ("(" + clientId + ")");

                    contacts.add(name);
                    ids.put(piName, "");
                    }
            }

*/
	}
