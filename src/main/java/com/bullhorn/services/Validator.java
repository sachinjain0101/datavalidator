package com.bullhorn.services;

import com.bullhorn.app.OperaStatus;
import com.bullhorn.orm.refreshWork.dao.ServiceBusMessagesDAO;
import com.bullhorn.orm.refreshWork.dao.ValidatedMessagesDAO;
import com.bullhorn.orm.refreshWork.model.TblIntegrationServiceBusMessages;
import com.bullhorn.orm.refreshWork.model.TblIntegrationValidatedMessages;
import com.bullhorn.orm.timecurrent.dao.ClientDAO;
import com.bullhorn.orm.timecurrent.model.Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Validator implements CancellableRunnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(Validator.class);
    private static final String NOT_APPLICABLE = "NA";
    private static final String DATA_VALIDATOR = "DataValidator";

    private final ServiceBusMessagesDAO serviceBusMessagesDAO;
    private final ClientDAO clientDAO;
    private final ValidatedMessagesDAO validatedMessagesDAO;

    private List<TblIntegrationServiceBusMessages> downloadedMessages;
    private HashMap<String, Client> clients;
    public final long interval;

    private AtomicBoolean processing = new AtomicBoolean();

    public Validator(ServiceBusMessagesDAO serviceBusMessagesDAO, ClientDAO clientDAO, ValidatedMessagesDAO validatedMessagesDAO, long interval) {
        this.serviceBusMessagesDAO = serviceBusMessagesDAO;
        this.clientDAO = clientDAO;
        this.validatedMessagesDAO = validatedMessagesDAO;
        this.interval = interval;
    }

    @Override
    public void run() {
        processing.set(true);
        LOGGER.debug("Running the Data Validator");
        while (!Thread.interrupted() && processing.get()) {
            clients = clientDAO.getAllActiveClients();
            downloadedMessages = serviceBusMessagesDAO.findAllDownloaded();
            doMessageValidation();
            List<TblIntegrationValidatedMessages> validMessages = getValidMessages();
            validatedMessagesDAO.batchInsertValidatedMessages(validMessages);
            serviceBusMessagesDAO.updateAllDownloaded(downloadedMessages);
            LOGGER.debug("********* DONE", validMessages.size());
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                LOGGER.debug("Data Validator interrupted : {}", e.getMessage());
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
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

    private boolean isJSONValid(String jsonStr) {
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

    @Override
    public void cancel() {
        processing.set(false);
        LOGGER.debug("Stopping the Data Swapper : {}", processing.get());
    }
}
