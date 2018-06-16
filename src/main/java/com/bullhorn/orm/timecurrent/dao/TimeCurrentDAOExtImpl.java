package com.bullhorn.orm.timecurrent.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.bullhorn.orm.timecurrent.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

public class TimeCurrentDAOExtImpl implements TimeCurrentDAOExt {

	private static final Logger LOGGER = LoggerFactory.getLogger(TimeCurrentDAOExt.class);

	@Autowired
	@Qualifier("timeCurrentEntityManager")
	private EntityManager em;

	@Override
	@Transactional("timeCurrentTransactionManager")
	public List<TblIntegrationFrontOfficeSystem> findByStatus(boolean status) {
		LOGGER.info("Getting data for status - {}",status);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TblIntegrationFrontOfficeSystem> cq = cb.createQuery(TblIntegrationFrontOfficeSystem.class);
		Root<TblIntegrationFrontOfficeSystem> root = cq.from(TblIntegrationFrontOfficeSystem.class);
		cq.where(cb.equal(root.get("recordStatus"), status));
		//cq.orderBy(cb.asc(root.get("recordId")));

		TypedQuery<TblIntegrationFrontOfficeSystem> query = em.createQuery(cq);

		return query.getResultList();
	}

    @Override
	@Transactional("timeCurrentTransactionManager")
    public List<TblIntegrationClient> findByIntegrationKey(String integrationKey) {
        LOGGER.info("Getting data for integrationKey - {}",integrationKey);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TblIntegrationClient> cq = cb.createQuery(TblIntegrationClient.class);
        Root<TblIntegrationClient> root = cq.from(TblIntegrationClient.class);
        cq.where(cb.equal(root.get("integrationKey"), integrationKey));
        //cq.orderBy(cb.asc(root.get("recordId")));

        TypedQuery<TblIntegrationClient> query = em.createQuery(cq);

        return query.getResultList();
    }

    @Override
	@Transactional("timeCurrentTransactionManager")
	public void insertError(TblIntegrationErrors error){
		em.persist(error);
	}
}
