package com.bullhorn.services;

import com.bullhorn.app.OperaStatus;
import com.bullhorn.orm.refreshWork.dao.ServiceBusMessagesDAO;
import com.bullhorn.orm.refreshWork.dao.ValidatedMessagesDAO;
import com.bullhorn.orm.refreshWork.model.TblIntegrationServiceBusMessages;
import com.bullhorn.orm.refreshWork.model.TblIntegrationValidatedMessages;
import com.bullhorn.orm.timecurrent.dao.ClientDAO;
import com.bullhorn.orm.timecurrent.model.Client;
import com.bullhorn.orm.timecurrent.model.TblIntegrationErrors;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Validator.class);
    private static final String NOT_APPLICABLE = "NA";
    private static final String DATA_VALIDATOR = "DataValidator";

    private ServiceBusMessagesDAO serviceBusMessagesDAO;
    private ClientDAO clientDAO;
    private ValidatedMessagesDAO validatedMessagesDAO;

    private List<TblIntegrationServiceBusMessages> downloadedMessages;
    private HashMap<String, Client> clients;

    @Autowired
    public Validator(ServiceBusMessagesDAO serviceBusMessagesDAO, ClientDAO clientDAO, ValidatedMessagesDAO validatedMessagesDAO) {
        this.serviceBusMessagesDAO = serviceBusMessagesDAO;
        this.clientDAO = clientDAO;
        this.validatedMessagesDAO = validatedMessagesDAO;
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 3000)
    public void run() {
        LOGGER.debug("Running the Data Validator");
        clients = clientDAO.getAllActiveClients();
        downloadedMessages = serviceBusMessagesDAO.findAllDownloaded();
        doMessageValidation();

        List<TblIntegrationValidatedMessages> validMessages = getValidMessages();

        validatedMessagesDAO.batchInsertValidatedMessages(validMessages);
        serviceBusMessagesDAO.updateAllDownloaded(downloadedMessages);

        LOGGER.debug("********* DONE",validMessages.size());
    }

    private List<TblIntegrationValidatedMessages> getValidMessages() {
        List<TblIntegrationServiceBusMessages> messages =  downloadedMessages.stream()
                .filter(msg -> msg.getStatus() == OperaStatus.PROCESSED.toString())
                .collect(Collectors.toList());

        List<TblIntegrationValidatedMessages> validMessages = new ArrayList<>();
        for(TblIntegrationServiceBusMessages msg:messages){

            Client client = clients.get(msg.getIntegrationKey());

            TblIntegrationValidatedMessages vm = new TblIntegrationValidatedMessages();
            vm.setClient(client.getClient());
            vm.setIntegrationKey(msg.getIntegrationKey());
            vm.setMapName(client.getMapName());
            vm.setIsMapped(client.getMapped());
            vm.setMessageId(msg.getMessageID());
            vm.setSequenceNumber(msg.getSequenceNumber());
            vm.setMessage(msg.getMessage());
            vm.setServiceBusMessagesRecordID(msg.getRecordID());
            vm.setFrontOfficeSystemRecordID(msg.getFrontOfficeSystemRecordID());
            vm.setClientRecordID(client.getRecordId());

            validMessages.add(vm);
        }

        return validMessages;

    }

    public boolean isJSONValid(String jsonStr) {
        try {
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(jsonStr).getAsJsonArray();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void doMessageValidation() {
        downloadedMessages = downloadedMessages.stream()
                .peek(msg -> {
                    if (!isJSONValid(msg.getMessage())) {
                        msg.setStatus(OperaStatus.INVALID_JSON_ERROR.toString());
                        msg.setErrorDescription("Invalid JSON");
                    } else if (!clients.containsKey(msg.getIntegrationKey())) {
                        msg.setStatus(OperaStatus.INTEGRATION_KEY_ERROR.toString());
                        msg.setErrorDescription("Invalid IntegrationKey");
                    }
                })
                .peek(msg -> {
                            if (msg.getStatus() == null)
                                msg.setStatus(OperaStatus.PROCESSED.toString());
                        }
                ).collect(Collectors.toList());
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
