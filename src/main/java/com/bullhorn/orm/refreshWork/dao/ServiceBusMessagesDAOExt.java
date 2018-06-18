package com.bullhorn.orm.refreshWork.dao;

import com.bullhorn.orm.refreshWork.model.TblIntegrationServiceBusMessages;

import java.util.List;

public interface ServiceBusMessagesDAOExt {
    void batchInsert(List<TblIntegrationServiceBusMessages> msgs);

    List<TblIntegrationServiceBusMessages> findAllDownloaded();

    boolean updateAllDownloaded(List<TblIntegrationServiceBusMessages> tblIntegrationServiceBusMessages);
}
