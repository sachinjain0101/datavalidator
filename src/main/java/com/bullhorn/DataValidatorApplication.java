package com.bullhorn;

import com.bullhorn.app.Constants;
import com.bullhorn.orm.refreshWork.dao.ServiceBusMessagesDAO;
import com.bullhorn.orm.refreshWork.dao.ValidatedMessagesDAO;
import com.bullhorn.orm.timecurrent.dao.ClientDAO;
import com.bullhorn.orm.timecurrent.dao.ConfigDAO;
import com.bullhorn.orm.timecurrent.dao.FrontOfficeSystemDAO;
import com.bullhorn.orm.timecurrent.model.TblIntegrationConfig;
import com.bullhorn.orm.timecurrent.model.TblIntegrationFrontOfficeSystem;
import com.bullhorn.services.ValidatorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { JacksonAutoConfiguration.class })
@EnableScheduling
public class DataValidatorApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataValidatorApplication.class);

	private Environment env;

	@Autowired
	public void setEnv(Environment env) {
		this.env = env;
	}

	private final FrontOfficeSystemDAO frontOfficeSystemDAO;
	private final ServiceBusMessagesDAO serviceBusMessagesDAO;
	private final ClientDAO clientDAO;
	private final ValidatedMessagesDAO validatedMessagesDAO;
	private final ConfigDAO configDAO;

	@Autowired
	public DataValidatorApplication(ServiceBusMessagesDAO serviceBusMessagesDAO, ClientDAO clientDAO
			, ValidatedMessagesDAO validatedMessagesDAO, ConfigDAO configDAO, FrontOfficeSystemDAO frontOfficeSystemDAO) {
		this.serviceBusMessagesDAO = serviceBusMessagesDAO;
		this.clientDAO = clientDAO;
		this.validatedMessagesDAO = validatedMessagesDAO;
		this.configDAO = configDAO;
		this.frontOfficeSystemDAO = frontOfficeSystemDAO;
	}

	public static void main(String[] args) {
		SpringApplication.run(DataValidatorApplication.class, args);
	}

	private List<TblIntegrationFrontOfficeSystem> lstFOS = null;

	@Bean(name = "integrationConfig")
	public List<TblIntegrationConfig> getConfig(){
		String cluster = (env.getProperty("azureConsumer.clusterName")!=null)?env.getProperty("azureConsumer.clusterName"):"";
		LOGGER.debug("Cluster info : {} & isEmpty : {}",cluster,cluster.isEmpty());
		// VIMP: lstFOS drives the AzureConsumer ThreadPool size
		lstFOS = frontOfficeSystemDAO.findByStatus(true,cluster);
		return configDAO.findAll();
	}

	@Bean("validatorTaskScheduler")
	@DependsOn("integrationConfig")
	public ThreadPoolTaskScheduler validatorTaskScheduler() {
		LOGGER.debug("Starting Validator Task Scheduler");
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(lstFOS.size());
		threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
		TblIntegrationConfig val2 = getConfig().stream()
				.filter((k) -> k.getCfgKey().equals(Constants.DATA_VALIDATOR_THREADPOOL_SCHEDULER_TERMINATION_TIME_INSECONDS))
				.collect(Collectors.toList()).get(0);
		int terminationTime = Integer.parseInt(val2.getCfgValue());
		threadPoolTaskScheduler.setAwaitTerminationSeconds(terminationTime);
		return threadPoolTaskScheduler;
	}

	@Bean("validatorHandler")
	@DependsOn("validatorTaskScheduler")
	public ValidatorHandler validatorHandler(){
		LOGGER.debug("DataSwapperAsyncService Constructed");
		TblIntegrationConfig val1 = getConfig().stream().filter((k) -> k.getCfgKey().equals(Constants.DATA_VALIDATOR_EXECUTE_INTERVAL)).collect(Collectors.toList()).get(0);
		long interval = Long.parseLong(val1.getCfgValue());
		ValidatorHandler validatorHandler = new ValidatorHandler(serviceBusMessagesDAO, clientDAO, validatedMessagesDAO);
		validatorHandler.setInterval(interval);
		validatorHandler.setPoolSize(lstFOS.size());
		validatorHandler.setLstFOS(lstFOS);
		return validatorHandler;
	}

	@EventListener
	public void init(ContextRefreshedEvent event) {
		LOGGER.debug("Starting Data Validator");
		validatorHandler().executeAsynchronously();
		addDataSwapperShutdownHook();
	}

	@PreDestroy
	public void destroy() {
		LOGGER.debug("Shutting down Data Validator");
	}

	public void addDataSwapperShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				LOGGER.info("Shutdown received");
				validatorHandler().shutdown();
			}
		});

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				LOGGER.error("Uncaught Exception on " + t.getName() + " : " + e, e);
				validatorHandler().shutdown();
			}
		});
		LOGGER.info("Data Validator ShutdownHook Added");
	}


}
