package com.bullhorn.orm.refreshWork.dao;

import com.bullhorn.orm.refreshWork.model.TblIntegrationServiceBusMessages;
import com.bullhorn.orm.refreshWork.model.TblIntegrationValidatedMessages;
import com.bullhorn.orm.timecurrent.model.Client;

import java.util.HashMap;
import java.util.List;

public interface RefreshWorkDAOExt {

    List<TblIntegrationServiceBusMessages> findAllDownloaded(HashMap<String, Client> clients);

    boolean updateAllDownloaded(List<TblIntegrationServiceBusMessages> tblIntegrationServiceBusMessages);

    void batchInsertValidatedMessages(List<TblIntegrationValidatedMessages> msgs);
}
