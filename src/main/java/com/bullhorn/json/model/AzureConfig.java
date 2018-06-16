package com.bullhorn.json.model;

import com.bullhorn.orm.timecurrent.model.TblIntegrationFrontOfficeSystem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AzureConfig {

    private String topicName;

    private List<TblIntegrationFrontOfficeSystem> lstFOS;

    public List<TblIntegrationFrontOfficeSystem> getLstFOS() {
        return lstFOS;
    }

    public void setLstFOS(List<TblIntegrationFrontOfficeSystem> lstFOS) {
        this.lstFOS = lstFOS;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public String toString() {
        return "AzureConfig{" +
                "topicName='" + topicName + '\'' +
                ", lstFOS=" + lstFOS +
                '}';
    }
}
