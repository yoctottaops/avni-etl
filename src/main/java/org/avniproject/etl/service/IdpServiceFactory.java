package org.avniproject.etl.service;

import com.auth0.jwk.SigningKeyNotFoundException;
import org.avniproject.etl.config.IdpType;
import org.avniproject.etl.domain.User;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.security.IAMAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdpServiceFactory {
    @Autowired
    private OrganisationRepository organisationRepository;

//    @Autowired(required = false)
//    private CognitoIdpService cognitoIdpService;
//
//    @Autowired(required = false)
//    private KeycloakIdpService keycloakIdpService;

    @Autowired(required = false)
    private CognitoAuthServiceImpl cognitoAuthService;

    @Autowired(required = false)
    private KeycloakAuthService keycloakAuthService;

    private IdpType idpType = IdpType.both;

    @Autowired
//    private OrganisationConfigService organisationConfigService;

    public IdpServiceFactory() {
    }

    public IdpServiceFactory(OrganisationRepository organisationRepository, CognitoAuthServiceImpl cognitoAuthService,  KeycloakAuthService keycloakAuthService, IdpType idpType) {
        this.organisationRepository = organisationRepository;
//        this.cognitoIdpService = cognitoIdpService;
//        this.keycloakIdpService = keycloakIdpService;
        this.cognitoAuthService = cognitoAuthService;
        this.keycloakAuthService = keycloakAuthService;
        this.idpType = idpType;
//        this.organisationConfigService = organisationConfigService;
    }

//    public IdpService getIdpService(Organisation organisation) {
//        OrganisationConfig.Settings settings = getSettings(organisation);
//
//        if (settings.useKeycloakAsIdp())
//            return keycloakIdpService;
//
//        if (idpType.equals(IdpType.none))
//            return new NoopIdpService();
//
//        return cognitoIdpService;
//    }
//
//    public IdpService getIdpService(User user) {
//        Organisation organisation = organisationRepository.findOne(user.getOrganisationId());
//        return getIdpService(organisation);
//    }

    public IAMAuthService getAuthService() {
        switch (idpType) {
            case cognito:
                return cognitoAuthService;
            case keycloak:
                return keycloakAuthService;
            case both:
                return new CompositeIAMAuthService(cognitoAuthService, keycloakAuthService);
            case none:
                return new NoIAMAuthService();
            default:
                throw new RuntimeException(String.format("IdpType: %s is not supported", idpType));
        }
    }

//    private OrganisationConfig.Settings getSettings(Organisation organisation) {
//        OrganisationConfig organisationConfig = organisationConfigService.getOrganisationConfig(organisation);
//        return organisationConfig.getSettingsObject();
//    }

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

    public static class NoIAMAuthService implements IAMAuthService {
        @Override
        public User getUserFromToken(String token) throws SigningKeyNotFoundException {
            return null;
        }
    }
}

