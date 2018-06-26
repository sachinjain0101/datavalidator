package com.bullhorn.services;

import com.bullhorn.orm.refreshWork.dao.ServiceBusMessagesDAO;
import com.bullhorn.orm.refreshWork.dao.ValidatedMessagesDAO;
import com.bullhorn.orm.refreshWork.model.TblIntegrationServiceBusMessages;
import com.bullhorn.orm.timecurrent.dao.ClientDAO;
import com.bullhorn.orm.timecurrent.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ValidatorAsyncService {

    @Autowired
    @Qualifier("validatorTaskScheduler")
    ThreadPoolTaskScheduler taskScheduler;

    private final ServiceBusMessagesDAO serviceBusMessagesDAO;
    private final ClientDAO clientDAO;
    private final ValidatedMessagesDAO validatedMessagesDAO;

    private long interval;

    public void setInterval(long interval) {
        this.interval = interval;
    }

    @Autowired
    public ValidatorAsyncService(ServiceBusMessagesDAO serviceBusMessagesDAO, ClientDAO clientDAO, ValidatedMessagesDAO validatedMessagesDAO) {
        this.serviceBusMessagesDAO = serviceBusMessagesDAO;
        this.clientDAO = clientDAO;
        this.validatedMessagesDAO = validatedMessagesDAO;
    }

    public void executeAsynchronously() {
        taskScheduler.scheduleWithFixedDelay(new Validator(serviceBusMessagesDAO,clientDAO, validatedMessagesDAO),interval);
    }

}
