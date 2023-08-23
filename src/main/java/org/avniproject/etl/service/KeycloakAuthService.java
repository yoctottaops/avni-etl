package org.avniproject.etl.service;

import com.auth0.jwt.interfaces.Verification;
import org.avniproject.etl.config.AvniKeycloakConfig;
import org.avniproject.etl.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression("'${avni.idp.type}'=='keycloak' or '${avni.idp.type}'=='both'")
public class KeycloakAuthService extends BaseIAMService {
    private final Logger logger = LoggerFactory.getLogger(KeycloakAuthService.class);
    private final AvniKeycloakConfig avniKeycloakConfig;

    @Autowired
    public KeycloakAuthService(UserRepository userRepository, AvniKeycloakConfig avniKeycloakConfig) {
        super(userRepository);
        this.avniKeycloakConfig = avniKeycloakConfig;
    }

    @Override
    public void logConfiguration() {
        logger.debug("Keycloak configuration");
        logger.debug(String.format("Keycloak server: %s", avniKeycloakConfig.getAuthServerUrl()));
        logger.debug(String.format("Realm name: %s", avniKeycloakConfig.getRealm()));
        logger.debug(String.format("Audience name: %s", avniKeycloakConfig.getResource()));
    }

    protected String getJwkProviderUrl() {
        return String.format(avniKeycloakConfig.getOpenidConnectCertsUrlFormat(), getIssuer());
    }

    protected String getIssuer() {
        return String.format(avniKeycloakConfig.getRealmsUrlFormat(), avniKeycloakConfig.getAuthServerUrl(), avniKeycloakConfig.getRealm());
    }
    @Override
    protected String getUserUuidField() {
        return avniKeycloakConfig.getCustomUserUUID();
    }

    @Override
    protected String getUsernameField() {
        return avniKeycloakConfig.getPreferredUserName();
    }

    @Override
    protected void addClaim(Verification verification) {
        verification.withClaim(avniKeycloakConfig.getUserEmailVerified(), true);
    }

    @Override
    protected String getAudience() {
        return avniKeycloakConfig.getVerifyTokenAudience();
    }
}
