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
import java.util.*;

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

	private List<String> getCfgValues(String sql){
		LOGGER.debug("configSql = {}",sql);
		List<String> clusterLst = new ArrayList<>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : rows) {
			String cfgValue = map.get("CfgValue").toString();
			if (cfgValue.contains(","))
				clusterLst.addAll(Arrays.asList(cfgValue.split(",")));
			else
				clusterLst.add(cfgValue);
		}
		return clusterLst;
	}

	private final String includeClusterSql = "SELECT CfgValue FROM TimeCurrent.dbo.tblIntegration_Config WHERE CfgKey = '%s'";
	private final String excludeClusterSql = "SELECT CfgValue FROM TimeCurrent.dbo.tblIntegration_Config WHERE CfgKey LIKE 'AZURE_CONSUMER_CLUSTER%'";


	@Override
	public List<TblIntegrationFrontOfficeSystem> findByStatus(boolean status, String cluster) {
		LOGGER.debug("Getting data for status - {} , cluster - {}",status, cluster);
		List<String> lstFosNames = new ArrayList<>();
		String fosSql = "SELECT * FROM TimeCurrent.dbo.tblIntegration_FrontOfficeSystem fos WHERE fos.RecordStatus = 1 AND fos.Name %s (%s)";

		if (!cluster.isEmpty())
			lstFosNames = getCfgValues(String.format(includeClusterSql, cluster));
		else
			lstFosNames = getCfgValues(excludeClusterSql);

		String subSql = "";
		String selectTemplate = "SELECT '%s' ";
		for (int i = 0; i < lstFosNames.size(); i++) {
			subSql += String.format(selectTemplate,lstFosNames.get(i));
			if (i + 1 != lstFosNames.size())
				subSql += " UNION ALL ";
		}
		if (!cluster.isEmpty())
			fosSql = String.format(fosSql,"IN",subSql);
		else
			fosSql = String.format(fosSql,"NOT IN",subSql);
		LOGGER.debug("fosSql = {}",fosSql);
		List<TblIntegrationFrontOfficeSystem> lstFos = new ArrayList<>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(fosSql);
		for(Map<String, Object> row : rows){
			String val = "";
			TblIntegrationFrontOfficeSystem fos = new TblIntegrationFrontOfficeSystem();
			fos.setRecordId((Integer) row.get("recordId"));
			val=(row.get("name")==null)?"":row.get("name").toString();
			fos.setName(val);
			val=(row.get("description")==null)?"":row.get("description").toString();
			fos.setDescription(val);
			val=(row.get("versionNumber")==null)?"":row.get("versionNumber").toString();
			fos.setVersionNumber(val);
			val=(row.get("azureEndPoint")==null)?"":row.get("azureEndPoint").toString();
			fos.setAzureEndPoint(val);
			fos.setRecordStatus((Boolean) row.get("RecordStatus"));
			if(fos.getAzureEndPoint()!="")
				lstFos.add(fos);
		}
		return lstFos;
	}

	@Override
	public HashMap<String,Client> getAllActiveClients(int frontOfficeSystemRecordID){
		LOGGER.debug("Getting all clients with StatusRecordID = 1 and IntegrationKey = {}",frontOfficeSystemRecordID);
		String sql = "SELECT * FROM TimeCurrent.dbo.tblIntegration_Client WHERE StatusRecordID = 1 and FrontOfficeSystemRecordID = ?";
		List<Client> rows = jdbcTemplate.query(sql,new Object[]{frontOfficeSystemRecordID},new ClientMapper());
		HashMap<String,Client> clients = new HashMap<>();
		rows.forEach((c) -> clients.put(c.getIntegrationKey(),c));
		return clients;
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

}
