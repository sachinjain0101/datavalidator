package com.bullhorn;

import com.bullhorn.json.model.AzureConfig;
import com.bullhorn.orm.refreshWork.dao.ServiceBusMessagesDAO;
import com.bullhorn.orm.timecurrent.dao.ErrorsDAO;
import com.bullhorn.orm.timecurrent.dao.FrontOfficeSystemDAO;
import com.bullhorn.services.Validator;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PreDestroy;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { JacksonAutoConfiguration.class })
@EnableScheduling
public class DataValidatorApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataValidatorApplication.class);

	@Autowired
	Environment env;

	final ErrorsDAO errorsDAO;
	final ServiceBusMessagesDAO serviceBusMessagesDAO;

	@Autowired
	public DataValidatorApplication(ErrorsDAO errorsDAO, ServiceBusMessagesDAO serviceBusMessagesDAO) {
		this.errorsDAO = errorsDAO;
		this.serviceBusMessagesDAO = serviceBusMessagesDAO;
	}

	public static void main(String[] args) {
		SpringApplication.run(DataValidatorApplication.class, args);
	}

	@EventListener
	public void init(ContextRefreshedEvent event) {
		LOGGER.info("Starting Data Validator");
		Validator dataSwapper = new Validator(serviceBusMessagesDAO);
		dataSwapper.run();
	}

	@PreDestroy
	public void destroy() {
		LOGGER.info("Shutting down Data Validator");

	}

}
