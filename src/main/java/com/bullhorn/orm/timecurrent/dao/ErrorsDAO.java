package com.bullhorn.orm.timecurrent.dao;

import com.bullhorn.orm.timecurrent.model.TblIntegrationErrors;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorsDAO extends CrudRepository<TblIntegrationErrors, Long> {
}
