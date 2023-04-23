package org.avniproject.etl.service;

import com.auth0.jwk.SigningKeyNotFoundException;
import org.avniproject.etl.domain.User;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.repository.UserRepository;
import org.avniproject.etl.security.IAMAuthService;
import org.avniproject.etl.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final IdpServiceFactory idpServiceFactory;

    @Autowired
    public AuthService(UserRepository userRepository, OrganisationRepository organisationRepository,
                       IdpServiceFactory idpServiceFactory) {
        this.idpServiceFactory = idpServiceFactory;
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
    }

    public UserContext authenticateByToken(String authToken) {
        IAMAuthService iamAuthService = idpServiceFactory.getAuthService();
        UserContext userContext = new UserContext();
        try {
            User user = iamAuthService.getUserFromToken(authToken);
            userContext.setUser(user);
        } catch (SigningKeyNotFoundException signingKeyNotFoundException) {
            throw new RuntimeException(signingKeyNotFoundException);
        }
        userContext.setAuthToken(authToken);
        return userContext;
    }
}
