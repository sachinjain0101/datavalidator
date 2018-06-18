package com.bullhorn.orm.refreshWork.dao;

import com.bullhorn.orm.refreshWork.model.TblIntegrationServiceBusMessages;
import com.bullhorn.orm.timecurrent.dao.TimeCurrentDAOExt;
import com.bullhorn.orm.timecurrent.model.TblIntegrationFrontOfficeSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ServiceBusMessagesDAOExtImpl implements ServiceBusMessagesDAOExt {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeCurrentDAOExt.class);

    @Autowired
    @Qualifier("refreshWorkJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("refreshWorkEntityManager")
    EntityManager em;

    @Override
    public void batchInsert(List<TblIntegrationServiceBusMessages> msgs) {
        String sql = "INSERT INTO tblIntegration_ServiceBusMessages " +
                            "(FrontOfficeSystemRecordID, Message, RecordID, SequenceNumber, MessageID) " +
                            "values (?, ?, ?, ?, ?)";
        int[] updateCounts = jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, msgs.get(i).getFrontOfficeSystemRecordID());
                        ps.setString(2, msgs.get(i).getMessage());
                        ps.setLong(3, msgs.get(i).getSequenceNumber());
                        ps.setLong(4, msgs.get(i).getRecordID());
                        ps.setString(5, msgs.get(i).getMessageID());
                    }

                    public int getBatchSize() {
                        return msgs.size();
                    }
                });
    }

    @Override
    public List<TblIntegrationServiceBusMessages> findAllDownloaded() {
        LOGGER.info("Getting downloaded messages");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TblIntegrationServiceBusMessages> cq = cb.createQuery(TblIntegrationServiceBusMessages.class);
        Root<TblIntegrationServiceBusMessages> root = cq.from(TblIntegrationServiceBusMessages.class);
        cq.where(cb.isNull(root.get("processed")));
        cq.orderBy(cb.asc(root.get("recordID")));

        TypedQuery<TblIntegrationServiceBusMessages> query = em.createQuery(cq);

        return query.getResultList();

    }

    @Override
    public boolean updateAllDownloaded(List<TblIntegrationServiceBusMessages> msgs) {

        LOGGER.info("Updating downloaded messages");
        String sql = "UPDATE tblIntegration_ServiceBusMessages " +
                     "SET Processed = ? , ErrorDescription = ? " +
                     "WHERE RecordID = ?";
        int[] updateCounts = jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, msgs.get(i).getProcessed());
                        ps.setString(2, msgs.get(i).getErrorDescription());
                        ps.setLong(3, msgs.get(i).getRecordID());
                    }

                    public int getBatchSize() {
                        return msgs.size();
                    }
                });

        return false;
    }



}