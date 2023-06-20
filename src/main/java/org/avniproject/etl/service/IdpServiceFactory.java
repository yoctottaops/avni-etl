package org.avniproject.etl.service;

import com.auth0.jwk.SigningKeyNotFoundException;
import org.avniproject.etl.config.IdpType;
import org.avniproject.etl.domain.User;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.repository.UserRepository;
import org.avniproject.etl.security.IAMAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IdpServiceFactory {
    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private CognitoAuthServiceImpl cognitoAuthService;

    @Autowired(required = false)
    private KeycloakAuthService keycloakAuthService;

    @Value("${avni.idp.type}")
    private IdpType idpType;

    public IdpServiceFactory() {
    }

    public IdpServiceFactory(OrganisationRepository organisationRepository, CognitoAuthServiceImpl cognitoAuthService,  KeycloakAuthService keycloakAuthService, IdpType idpType) {
        this.organisationRepository = organisationRepository;
        this.cognitoAuthService = cognitoAuthService;
        this.keycloakAuthService = keycloakAuthService;
        this.idpType = idpType;
    }

    public IAMAuthService getAuthService() {
        switch (idpType) {
            case cognito:
                return cognitoAuthService;
            case keycloak:
                return keycloakAuthService;
            case both:
                return new CompositeIAMAuthService(cognitoAuthService, keycloakAuthService);
            case none:
                return new NoIAMAuthService(userRepository);
            default:
                throw new RuntimeException(String.format("IdpType: %s is not supported", idpType));
        }
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

    @Service
    public static class NoIAMAuthService implements IAMAuthService {
        private final UserRepository userRepository;

        @Autowired
        public NoIAMAuthService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public User getUserFromToken(String userName) {
            return userRepository.findByUsername(userName);
        }
    }
}

