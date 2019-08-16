package edu.umich.brcf.shared.layers.dao;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BaseDAO 
	{
	@PersistenceContext
	EntityManager em;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) 
		{
		this.em = entityManager;
		}

	public EntityManager getEntityManager() 
		{
		return em;
		}


	protected void initializeTheKids(Object parent, String[] kids) 
		{
		for (String currentKidName : kids) {
			try 
				{
				Hibernate.initialize(PropertyUtils.getNestedProperty(parent, currentKidName));
				} 
			catch (Exception iae) 
				{
				throw new RuntimeException(iae);
				}
			}
		}
	
	
	protected Object deproxy(Object object) 
		{
		Hibernate.initialize(object);
		if (object == null)
			return null;

		if (HibernateProxy.class.isInstance(object)) 
			{
			HibernateProxy proxy = (HibernateProxy) object;
			return proxy.getHibernateLazyInitializer().getImplementation();
			}

		return object;
		}
	}