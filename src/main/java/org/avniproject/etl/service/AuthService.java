package org.avniproject.etl.service;

import com.auth0.jwk.SigningKeyNotFoundException;
import org.avniproject.etl.domain.User;
import org.avniproject.etl.security.IAMAuthService;
import org.avniproject.etl.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final IdpServiceFactory idpServiceFactory;

    @Autowired
    public AuthService(IdpServiceFactory idpServiceFactory) {
        this.idpServiceFactory = idpServiceFactory;
    }

    public User authenticateByTokenOrUserName(String tokenOrUserName) {
        IAMAuthService iamAuthService = idpServiceFactory.getAuthService();
        try {
            return iamAuthService.getUserFromToken(tokenOrUserName);
        } catch (SigningKeyNotFoundException signingKeyNotFoundException) {
            throw new RuntimeException(signingKeyNotFoundException);
        }
    }

    public UserContext setupUserContext(User user) {
        UserContext userContext = new UserContext();
        userContext.setUser(user);
        return userContext;
    }
}
