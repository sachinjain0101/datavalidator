package com.bullhorn.orm.timecurrent.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientMapper implements RowMapper<Client> {
    @Override
    public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
        Client ci = new Client();
        ci.setRecordId(rs.getInt("RecordID"));
        ci.setClient(rs.getString("Client"));
        ci.setIntegrationKey(rs.getString("IntegrationKey"));
        ci.setFrontOfficeSystemRecordID(rs.getInt("FrontOfficeSystemRecordID"));
        ci.setMapName(rs.getString("Map"));
        ci.setMapped(rs.getBoolean("IsMapped"));
        return ci;
    }
}
