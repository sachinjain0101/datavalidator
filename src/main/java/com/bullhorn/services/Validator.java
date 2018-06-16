package com.bullhorn.services;

import com.bullhorn.orm.refreshWork.dao.ServiceBusMessagesDAO;
import com.bullhorn.orm.refreshWork.model.TblIntegrationServiceBusMessages;
import com.bullhorn.orm.timecurrent.model.TblIntegrationErrors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Validator.class);
    private static final String NOT_APPLICABLE = "NA";
    private static final String DATA_VALIDATOR = "DataValidator";

    ServiceBusMessagesDAO serviceBusMessagesDAO;

    @Autowired
    public Validator(ServiceBusMessagesDAO serviceBusMessagesDAO) {
        this.serviceBusMessagesDAO = serviceBusMessagesDAO;
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 3000)
    public void run() {
        LOGGER.info("Running the Data Validator");
        //List<TblIntegrationServiceBusMessages> tblIntegrationServiceBusMessages = serviceBusMessagesDAO.findAllDownloaded();
    }


    private boolean validateJSON(List<TblIntegrationServiceBusMessages> tblIntegrationServiceBusMessages) {
        return false;
    }


    private boolean validateIntegrationKey(List<TblIntegrationServiceBusMessages> tblIntegrationServiceBusMessages) {
        return false;
    }


    private boolean updateStatus(List<TblIntegrationServiceBusMessages> tblIntegrationServiceBusMessages) {
        return false;
    }


    private boolean doCleanup(String jsonStr) {
        return false;
    }

    private TblIntegrationErrors getErrorObject(Exception e, String messageId, String methodName) {
        TblIntegrationErrors error = new TblIntegrationErrors();

        error.setIntegrationKey(NOT_APPLICABLE);
        error.setClient(NOT_APPLICABLE);
        //TODO:Set the FOI ID
        error.setFrontOfficeSystemRecordId(null);
        error.setProcessName(DATA_VALIDATOR);
        error.setMessageId(messageId);
        error.setErrorSource(methodName);
        error.setErrorCode(e.getMessage());
        error.setErrorDescription(ExceptionUtils.getStackTrace(e));
        error.setCreateDateTime(new Date());
        return error;
    }


}
