package org.avniproject.etl.service;

import org.avniproject.etl.config.AvniKeycloakConfig;
import org.avniproject.etl.config.CognitoConfig;
import org.avniproject.etl.domain.User;
import org.avniproject.etl.repository.UserRepository;
import org.avniproject.etl.security.IAMAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoIAMAuthService implements IAMAuthService {
    private final UserRepository userRepository;
    private final CognitoConfig cognitoConfig;
    private final AvniKeycloakConfig avniKeycloakConfig;

    @Autowired
    public NoIAMAuthService(UserRepository userRepository, CognitoConfig cognitoConfig, AvniKeycloakConfig avniKeycloakConfig) {
        this.userRepository = userRepository;
        this.cognitoConfig = cognitoConfig;
        this.avniKeycloakConfig = avniKeycloakConfig;
    }

    @Override
    public User getUserFromToken(String userName) {
        if (cognitoConfig.isConfigured() || avniKeycloakConfig.isConfigured()) {
            throw new RuntimeException("Server improperly configured as no auth is not allowed is either cognito or keycloak is configured. This is security check to avoid mistakes and opening up Avni with auth. Probably your idpType is set to none.");
        }
        return userRepository.findByUsername(userName);
    }
}
