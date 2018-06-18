package com.bullhorn.orm.timecurrent.dao;

import com.bullhorn.orm.timecurrent.model.Client;
import com.bullhorn.orm.timecurrent.model.TblIntegrationClient;
import com.bullhorn.orm.timecurrent.model.TblIntegrationErrors;
import com.bullhorn.orm.timecurrent.model.TblIntegrationFrontOfficeSystem;

import java.util.HashMap;
import java.util.List;

public interface TimeCurrentDAOExt {

	List<TblIntegrationFrontOfficeSystem> findByStatus(boolean status);
	List<TblIntegrationClient> findByIntegrationKey(String integrationKey);

	void insertError(TblIntegrationErrors error);

	HashMap<String,Client> getAllActiveClients();
}
