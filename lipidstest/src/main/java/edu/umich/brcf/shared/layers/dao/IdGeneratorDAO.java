		package edu.umich.brcf.shared.layers.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.Type;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.IdGenerator;




@Repository
public class IdGeneratorDAO extends BaseDAO implements Configurable, IdentifierGenerator {
	protected int width = 6; // define this to be the width of the ID string

	private String idClass;
	private String configWidthParm;

	// Issue 205
	public Object getNextValue(String idClass) {
		return getNextValue(idClass, true,1);
	}
	// Issue 205
	public Object getNextValue(String idClass, boolean increment, Integer incrementNumber) {
		return getEntityManager().find(IdGenerator.class, idClass).getNextIdValue(increment, incrementNumber);
	}

	public void configure(Type type, Properties params, Dialect d) throws MappingException {
		String val = params.getProperty("idClass");
		String width = params.getProperty("width");
		idClass = val;
		configWidthParm = width;
		// IdentifierGeneratorFactory.create();
	}

	/**
	 * fetch the IdGenerator from the DB using pure Hibernate as we're below JPA
	 * here. then increment the "sequence" value for this class.
	 */
	public Serializable generate(SessionImplementor sessionImpl, Object obj) throws HibernateException {
		Session session = (Session) sessionImpl;
		//System.out.println("********************===========>" + idClass + "<===");
		IdGenerator idgen = getClassInstance(session);
		//System.out.println("********************====y=======>" + idClass + "<=== Seq:" + idgen.getSequence());
		return idgen.getNextIdValue();
	}

	private IdGenerator getClassInstance(Session session) {
		Query query = session.createQuery("from IdGenerator i where i.idClass = :idClass");
		query.setParameter("idClass", idClass);
		List<IdGenerator> list = query.list();
		//System.out.println("List length in get classs instance was " + list.size());
		//System.out.println("********************=======x====>" + idClass + "<===" + list.get(0).getSequence());
		return (IdGenerator) DataAccessUtils.requiredSingleResult(list);
	}
}
