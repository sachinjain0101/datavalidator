package com.bullhorn.orm.refreshWork.dao;

import com.bullhorn.orm.refreshWork.model.TblIntegrationServiceBusMessages;
import com.bullhorn.orm.timecurrent.dao.TimeCurrentDAOExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ServiceBusMessagesDAOExtImpl implements ServiceBusMessagesDAOExt {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeCurrentDAOExt.class);

    @Autowired
    @Qualifier("refreshWorkJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<TblIntegrationServiceBusMessages> msgs) {
        int[] updateCounts = jdbcTemplate.batchUpdate(
                "INSERT INTO tblIntegration_ServiceBusMessages (FrontOfficeSystemRecordID, Message, RecordID, SequenceNumber, MessageID) values (?, ?, ?, ?, ?)",
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
        return null;
    }

}