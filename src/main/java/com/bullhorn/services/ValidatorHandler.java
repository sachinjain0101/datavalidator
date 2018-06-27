package com.bullhorn.services;

import com.bullhorn.orm.refreshWork.dao.ServiceBusMessagesDAO;
import com.bullhorn.orm.refreshWork.dao.ValidatedMessagesDAO;
import com.bullhorn.orm.timecurrent.dao.ClientDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class ValidatorHandler {

    @Autowired
    @Qualifier("validatorTaskScheduler")
    ThreadPoolTaskScheduler taskScheduler;

    private final ServiceBusMessagesDAO serviceBusMessagesDAO;
    private final ClientDAO clientDAO;
    private final ValidatedMessagesDAO validatedMessagesDAO;

    private long interval;
    private int poolSize;

    Map<CancellableRunnable, Future<?>> cancellableFutures = new HashMap<>();

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    @Autowired
    public ValidatorHandler(ServiceBusMessagesDAO serviceBusMessagesDAO, ClientDAO clientDAO, ValidatedMessagesDAO validatedMessagesDAO) {
        this.serviceBusMessagesDAO = serviceBusMessagesDAO;
        this.clientDAO = clientDAO;
        this.validatedMessagesDAO = validatedMessagesDAO;
    }

    public void executeAsynchronously() {
        //taskScheduler.scheduleWithFixedDelay(new Validator(serviceBusMessagesDAO,clientDAO, validatedMessagesDAO),interval);
        for (int i = 1; i <= poolSize; i++) {
            Validator validator = new Validator(serviceBusMessagesDAO,clientDAO, validatedMessagesDAO,interval);
            Future<?> future = taskScheduler.submit(validator);
            cancellableFutures.put(validator, future);
        }
    }

    public void shutdown() {

        for (Map.Entry<CancellableRunnable, Future<?>> entry : cancellableFutures.entrySet()) {
            entry.getKey().cancel();
            entry.getValue().cancel(true);
        }

        taskScheduler.shutdown();
    }


}
