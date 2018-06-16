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
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@PropertySource({"classpath:orm-multi-db.properties"})
@EnableJpaRepositories(basePackages = "com.bullhorn.orm.refreshWork.dao", entityManagerFactoryRef = "refreshWorkEntityManager", transactionManagerRef = "refreshWorkTransactionManager")
public class RefreshWorkDBConfig {
    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean refreshWorkEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(refreshWorkDataSource());
        em.setPackagesToScan(new String[]{"com.bullhorn.orm.refreshWork.model"});
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    public DataSource refreshWorkDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("refreshWork.jdbc.url"));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager refreshWorkTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(refreshWorkEntityManager().getObject());
        return transactionManager;
    }

    @Primary
    @Bean(name = "refreshWorkJdbcTemplate")
    public JdbcTemplate refreshWorkJdbcTemplate() {
        return new JdbcTemplate(refreshWorkDataSource());
    }

    @Primary
    @Bean(name = "refreshWorkNamedJdbcTemplate")
    public NamedParameterJdbcTemplate refreshWorkNamedJdbcTemplate() {
        return new NamedParameterJdbcTemplate(refreshWorkDataSource());
    }

    @Primary
    @Bean(name = "refreshWorkTransactionTemplate")
    public TransactionTemplate refreshWorkTransactionTemplate(){
        return new TransactionTemplate(refreshWorkTransactionManager());
    }
}
