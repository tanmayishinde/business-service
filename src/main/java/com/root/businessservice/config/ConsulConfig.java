package com.root.businessservice.config;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class ConsulConfig {

    private final String configProperties;

    public ConsulConfig(@Value("${config-properties}") String configProperties){
        this.configProperties = configProperties;
    }

    public int getConfigValueByKey(String key, int defaultValue){
        String configValue = getStringValueFromJson(key);
        return StringUtils.isNotEmpty(configValue) ? Integer.parseInt(configValue) : defaultValue;
    }
    public String getConfigValueByKey(String key, String defaultValue){
        String configValue = getStringValueFromJson(key);
        return StringUtils.isNotEmpty(configValue) ? configValue : defaultValue;
    }

    public String getConfigValueByKey(String key){
        return getStringValueFromJson(key);
    }

    private String getStringValueFromJson(String key){
        JSONObject jsonObject = new JSONObject(configProperties);
        return jsonObject.optString(key);
    }

}
