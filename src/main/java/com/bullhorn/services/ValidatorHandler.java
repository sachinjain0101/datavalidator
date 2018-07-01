package com.bullhorn.services;

import com.bullhorn.orm.refreshWork.dao.ServiceBusMessagesDAO;
import com.bullhorn.orm.refreshWork.dao.ValidatedMessagesDAO;
import com.bullhorn.orm.timecurrent.dao.ClientDAO;
import com.bullhorn.orm.timecurrent.model.TblIntegrationFrontOfficeSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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

    private List<TblIntegrationFrontOfficeSystem> lstFOS;
    public void setLstFOS(List<TblIntegrationFrontOfficeSystem> lstFOS) {
        this.lstFOS = lstFOS;
    }

    @Autowired
    public ValidatorHandler(ServiceBusMessagesDAO serviceBusMessagesDAO, ClientDAO clientDAO, ValidatedMessagesDAO validatedMessagesDAO) {
        this.serviceBusMessagesDAO = serviceBusMessagesDAO;
        this.clientDAO = clientDAO;
        this.validatedMessagesDAO = validatedMessagesDAO;
    }

    public void executeAsynchronously() {
        //taskScheduler.scheduleWithFixedDelay(new Validator(serviceBusMessagesDAO,clientDAO, validatedMessagesDAO),interval);
        for (TblIntegrationFrontOfficeSystem FOS:lstFOS) {
            Validator validator = new Validator(serviceBusMessagesDAO,clientDAO, validatedMessagesDAO);
            validator.setFOS(FOS);
            validator.setInterval(interval);
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
