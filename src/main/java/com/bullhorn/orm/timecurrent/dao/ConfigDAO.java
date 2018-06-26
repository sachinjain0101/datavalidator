package com.bullhorn.orm.timecurrent.dao;

import com.bullhorn.orm.timecurrent.model.TblIntegrationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigDAO extends JpaRepository<TblIntegrationConfig,Long> {
}
