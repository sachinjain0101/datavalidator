package com.bullhorn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Primary
@Configuration
@PropertySource({"file:orm-multi-db.properties"})
@EnableJpaRepositories(basePackages = "com.bullhorn.orm.timecurrent.dao", entityManagerFactoryRef = "timeCurrentEntityManager", transactionManagerRef = "timeCurrentTransactionManager")
public class TimeCurrentDBConfig {
    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean timeCurrentEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(timeCurrentDataSource());
        em.setPackagesToScan(new String[]{"com.bullhorn.orm.timecurrent.model"});
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql",env.getProperty("hibernate.show_sql"));
        properties.put("hibernate.cache.use_second_level_cache",env.getProperty("hibernate.cache.use_second_level_cache"));
        properties.put("hibernate.cache.use_query_cache",env.getProperty("hibernate.cache.use_query_cache"));
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    public DataSource timeCurrentDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("timeCurrent.jdbc.url"));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager timeCurrentTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(timeCurrentEntityManager().getObject());
        return transactionManager;
    }

    @Primary
    @Bean(name = "timeCurrentJdbcTemplate")
    public JdbcTemplate metricsJdbcTemplate() {
        return new JdbcTemplate(timeCurrentDataSource());
    }

    @Primary
    @Bean(name = "timeCurrentNamedJdbcTemplate")
    public NamedParameterJdbcTemplate metricsNamedJdbcTemplate() {
        return new NamedParameterJdbcTemplate(timeCurrentDataSource());
    }
}