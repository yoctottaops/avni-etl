package org.avniproject.etl.service;

import com.auth0.jwk.SigningKeyNotFoundException;
import org.avniproject.etl.config.AvniKeycloakConfig;
import org.avniproject.etl.config.CognitoConfig;
import org.avniproject.etl.config.IdpType;
import org.avniproject.etl.domain.User;
import org.avniproject.etl.repository.UserRepository;
import org.avniproject.etl.security.IAMAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IdpServiceFactory {
    private final UserRepository userRepository;

    private CognitoAuthServiceImpl cognitoAuthService;

    private KeycloakAuthService keycloakAuthService;

    @Value("${avni.idp.type}")
    private IdpType idpType;

    private final CognitoConfig cognitoConfig;

    private final AvniKeycloakConfig avniKeycloakConfig;

    @Autowired
    public IdpServiceFactory(UserRepository userRepository, CognitoConfig cognitoConfig, AvniKeycloakConfig avniKeycloakConfig) {
        this.userRepository = userRepository;
        this.cognitoConfig = cognitoConfig;
        this.avniKeycloakConfig = avniKeycloakConfig;
    }

    @Autowired(required = false)
    public void setCognitoAuthService(CognitoAuthServiceImpl cognitoAuthService) {
        this.cognitoAuthService = cognitoAuthService;
    }

    @Autowired(required = false)
    public void setKeycloakAuthService(KeycloakAuthService keycloakAuthService) {
        this.keycloakAuthService = keycloakAuthService;
    }

    public IAMAuthService getAuthService() {
        return switch (idpType) {
            case cognito -> cognitoAuthService;
            case keycloak -> keycloakAuthService;
            case both -> new CompositeIAMAuthService(cognitoAuthService, keycloakAuthService);
            case none -> new NoIAMAuthService(userRepository, cognitoConfig, avniKeycloakConfig);
            default -> throw new RuntimeException(String.format("IdpType: %s is not supported", idpType));
        };
    }

    public static class CompositeIAMAuthService implements IAMAuthService {
        private final CognitoAuthServiceImpl cognitoAuthService;
        private final KeycloakAuthService keycloakAuthService;

        public CompositeIAMAuthService(CognitoAuthServiceImpl cognitoAuthService, KeycloakAuthService keycloakAuthService) {
            this.cognitoAuthService = cognitoAuthService;
            this.keycloakAuthService = keycloakAuthService;
        }

        @Override
        public User getUserFromToken(String token) throws SigningKeyNotFoundException {
            try {
                return cognitoAuthService.getUserFromToken(token);
            } catch (SigningKeyNotFoundException e) {
                return keycloakAuthService.getUserFromToken(token);
            }
        }
    }

}

