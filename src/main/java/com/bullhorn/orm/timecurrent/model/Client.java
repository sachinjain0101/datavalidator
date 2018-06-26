package com.bullhorn.orm.timecurrent.model;

public class Client {
    private Integer recordId;
    private String client;
    private String integrationKey;
    private String mapName;
    private Integer frontOfficeSystemRecordID;
    private Boolean isMapped;

    public Client(String client, String integrationKey, String mapName, Integer frontOfficeSystemRecordID, Boolean isMapped) {
        this.client = client;
        this.integrationKey = integrationKey;
        this.mapName = mapName;
        this.frontOfficeSystemRecordID = frontOfficeSystemRecordID;
        this.isMapped = isMapped;
    }

    public Client() {
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getIntegrationKey() {
        return integrationKey;
    }

    public void setIntegrationKey(String integrationKey) {
        this.integrationKey = integrationKey;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Integer getFrontOfficeSystemRecordID() {
        return frontOfficeSystemRecordID;
    }

    public void setFrontOfficeSystemRecordID(Integer frontOfficeSystemRecordID) {
        this.frontOfficeSystemRecordID = frontOfficeSystemRecordID;
    }

    public Boolean getMapped() {
        return isMapped;
    }

    public void setMapped(Boolean mapped) {
        isMapped = mapped;
    }

    @Override
    public String toString() {
        return "Client{" +
                "client='" + client + '\'' +
                ", integrationKey='" + integrationKey + '\'' +
                ", mapName='" + mapName + '\'' +
                ", frontOfficeSystemRecordID=" + frontOfficeSystemRecordID +
                ", isMapped=" + isMapped +
                '}';
    }
}
