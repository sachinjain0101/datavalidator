package com.bullhorn.orm.refreshWork.dao;

import com.bullhorn.orm.refreshWork.model.TblIntegrationValidatedMessages;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidatedMessagesDAO extends CrudRepository<TblIntegrationValidatedMessages,Long>, RefreshWorkDAOExt {
}
