package org.avniproject.etl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CognitoConfig {
    @Value("${cognito.poolid}")
    private String poolId;

    @Value("${cognito.clientid}")
    private String clientId;

    public String getPoolId() {
        return poolId;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isConfigured() {
        return !("dummy".equals(poolId) || "dummy".equals(clientId));
    }
}
