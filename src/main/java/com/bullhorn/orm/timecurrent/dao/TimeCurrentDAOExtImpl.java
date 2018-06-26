package com.bullhorn.orm.timecurrent.dao;

import com.bullhorn.orm.timecurrent.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;

public class TimeCurrentDAOExtImpl implements TimeCurrentDAOExt {

	private static final Logger LOGGER = LoggerFactory.getLogger(TimeCurrentDAOExt.class);

	private final JdbcTemplate jdbcTemplate;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private final EntityManager em;

	public TimeCurrentDAOExtImpl(@Qualifier("timeCurrentJdbcTemplate") JdbcTemplate jdbcTemplate
								,@Qualifier("timeCurrentNamedJdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate
								,@Qualifier("timeCurrentEntityManager") EntityManager em) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.em = em;
	}

    @Override
	public List<TblIntegrationFrontOfficeSystem> findByStatus(boolean status) {
		LOGGER.debug("Getting data for status - {}",status);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TblIntegrationFrontOfficeSystem> cq = cb.createQuery(TblIntegrationFrontOfficeSystem.class);
		Root<TblIntegrationFrontOfficeSystem> root = cq.from(TblIntegrationFrontOfficeSystem.class);
		cq.where(cb.equal(root.get("recordStatus"), status));
		//cq.orderBy(cb.asc(root.get("recordId")));
		TypedQuery<TblIntegrationFrontOfficeSystem> query = em.createQuery(cq);
		return query.getResultList();
	}

    @Override
    public List<TblIntegrationClient> findByIntegrationKey(String integrationKey) {
        LOGGER.debug("Getting data for integrationKey - {}",integrationKey);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TblIntegrationClient> cq = cb.createQuery(TblIntegrationClient.class);
        Root<TblIntegrationClient> root = cq.from(TblIntegrationClient.class);
        cq.where(cb.equal(root.get("integrationKey"), integrationKey));
        //cq.orderBy(cb.asc(root.get("recordId")));
        TypedQuery<TblIntegrationClient> query = em.createQuery(cq);
        return query.getResultList();
    }

    @Override
	public void insertError(TblIntegrationErrors error){
		em.persist(error);
	}

	@Override
	public HashMap<String,Client> getAllActiveClients(){
		LOGGER.debug("Getting all clients with StatusRecordID = 1");
	    Integer statusRecordID = 1;
        String sql = "SELECT * FROM TimeCurrent.dbo.tblIntegration_Client WHERE StatusRecordID = ?";
        List<Client> rows = jdbcTemplate.query(sql,new Object[]{statusRecordID},new ClientMapper());
        HashMap<String,Client> clients = new HashMap<>();
        rows.forEach((c) -> clients.put(c.getIntegrationKey(),c));
        return clients;
	}

}
