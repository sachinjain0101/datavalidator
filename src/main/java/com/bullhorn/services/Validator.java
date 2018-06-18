package com.bullhorn.services;

import com.bullhorn.exceptions.InboundJSONException;
import com.bullhorn.orm.refreshWork.dao.ServiceBusMessagesDAO;
import com.bullhorn.orm.refreshWork.model.TblIntegrationServiceBusMessages;
import com.bullhorn.orm.timecurrent.dao.ClientDAO;
import com.bullhorn.orm.timecurrent.model.TblIntegrationClient;
import com.bullhorn.orm.timecurrent.model.TblIntegrationErrors;
import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Validator.class);
    private static final String NOT_APPLICABLE = "NA";
    private static final String DATA_VALIDATOR = "DataValidator";

    private ServiceBusMessagesDAO serviceBusMessagesDAO;
    private ClientDAO clientDAO;

    private List<TblIntegrationServiceBusMessages> downloadedMessages;

    @Autowired
    public Validator(ServiceBusMessagesDAO serviceBusMessagesDAO, ClientDAO clientDAO) {
        this.serviceBusMessagesDAO = serviceBusMessagesDAO;
        this.clientDAO = clientDAO;
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 3000)
    public void run() {
        LOGGER.info("Running the Data Validator");
        downloadedMessages = serviceBusMessagesDAO.findAllDownloaded();
        List<TblIntegrationServiceBusMessages> validIntegrationKeyMessgages = getMessagesWithValidIntegrationKey();
        downloadedMessages.forEach((m)->{
                 LOGGER.info("{}-{}-{}",m.getRecordID(),m.getProcessed(),m.getErrorDescription());
                }
        );

        validIntegrationKeyMessgages.forEach((m)->{
                    LOGGER.info("{}-{}-{}",m.getRecordID(),m.getProcessed(),m.getErrorDescription());
                }
        );

//        List<TblIntegrationServiceBusMessages> validMessages = getValidJSONMessages();
//
//
//
//        serviceBusMessagesDAO.updateAllDownloaded(downloadedMessages);

        //LOGGER.info("Valid Message Count - {}",validMessages.size());
    }

    public List<TblIntegrationServiceBusMessages> getValidJSONMessages() {
        List<TblIntegrationServiceBusMessages> validMessages = new ArrayList<>();
            JsonParser parser = new JsonParser();
            for(TblIntegrationServiceBusMessages msg : downloadedMessages){
                try {
                    JsonArray array = parser.parse(msg.getMessage()).getAsJsonArray();
                    msg.setProcessed(1);
                    validMessages.add(msg);
                }catch (Exception e){
                    msg.setProcessed(0);
                    msg.setErrorDescription("Invalid JSON");
                    continue;
                }
            }
        return validMessages;
    }

    private List<TblIntegrationServiceBusMessages> getMessagesWithValidIntegrationKey() {
        List<TblIntegrationServiceBusMessages> validIntegrationKeyMessages = new ArrayList<>();

        HashMap<String,TblIntegrationClient> clientsMap = getAllClientsMap();

        validIntegrationKeyMessages = downloadedMessages.stream()
                .peek(msg -> {
                    if(!clientsMap.containsKey(msg.getIntegrationKey())){
                        msg.setProcessed(0);
                        msg.setErrorDescription("Invalid IntegrationKey");
                    }
                })
                .filter(msg -> getAllClientsMap().containsKey(msg.getIntegrationKey()))
                .collect(Collectors.toList());

        return validIntegrationKeyMessages;
    }

    private HashMap<String,TblIntegrationClient> getAllClientsMap(){
        List<TblIntegrationClient> clients = clientDAO.findAll();
        HashMap<String,TblIntegrationClient> clientMap = new HashMap<>();
        clients.forEach((c)->{
            clientMap.put(c.getIntegrationKey(),c);
        });
        return clientMap;
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
