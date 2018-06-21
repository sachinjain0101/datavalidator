package com.bullhorn;

import com.bullhorn.orm.refreshWork.dao.ServiceBusMessagesDAO;
import com.bullhorn.orm.refreshWork.dao.ValidatedMessagesDAO;
import com.bullhorn.orm.timecurrent.dao.ErrorsDAO;
import com.bullhorn.orm.timecurrent.dao.ClientDAO;
import com.bullhorn.services.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

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
	final ClientDAO clientDAO;
	final ValidatedMessagesDAO validatedMessagesDAO;

	@Autowired
	public DataValidatorApplication(ErrorsDAO errorsDAO, ServiceBusMessagesDAO serviceBusMessagesDAO, ClientDAO clientDAO, ValidatedMessagesDAO validatedMessagesDAO) {
		this.errorsDAO = errorsDAO;
		this.serviceBusMessagesDAO = serviceBusMessagesDAO;
		this.clientDAO = clientDAO;
		this.validatedMessagesDAO = validatedMessagesDAO;
	}

	public static void main(String[] args) {
		SpringApplication.run(DataValidatorApplication.class, args);
	}

	@EventListener
	public void init(ContextRefreshedEvent event) {
		LOGGER.debug("Starting Data Validator");
		Validator validator = new Validator(serviceBusMessagesDAO,clientDAO, validatedMessagesDAO);
		validator.run();
	}

	@PreDestroy
	public void destroy() {
		LOGGER.debug("Shutting down Data Validator");

	}

}
