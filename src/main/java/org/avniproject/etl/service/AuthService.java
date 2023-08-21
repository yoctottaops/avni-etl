package org.avniproject.etl.service;

import com.auth0.jwk.SigningKeyNotFoundException;
import org.avniproject.etl.domain.User;
import org.avniproject.etl.security.IAMAuthService;
import org.avniproject.etl.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {
    public final static SimpleGrantedAuthority ANALYTICS_USER_AUTHORITY = new SimpleGrantedAuthority(User.ANALYTICS_USER);
    public final static SimpleGrantedAuthority USER_AUTHORITY = new SimpleGrantedAuthority(User.USER);
    public final static SimpleGrantedAuthority ADMIN_AUTHORITY = new SimpleGrantedAuthority(User.ADMIN);
    public final static List<SimpleGrantedAuthority> ALL_AUTHORITIES = Arrays.asList(USER_AUTHORITY, ANALYTICS_USER_AUTHORITY, ADMIN_AUTHORITY);

    private final IdpServiceFactory idpServiceFactory;

    @Autowired
    public AuthService(IdpServiceFactory idpServiceFactory) {
        this.idpServiceFactory = idpServiceFactory;
    }

    public User authenticate(String token) {
        IAMAuthService iamAuthService = idpServiceFactory.getAuthService();
        try {
            return iamAuthService.getUserFromToken(token);
        } catch (SigningKeyNotFoundException signingKeyNotFoundException) {
            throw new RuntimeException(signingKeyNotFoundException);
        }
    }

    public UserContext setupUserContext(User user) {
        UserContext userContext = new UserContext();
        userContext.setUser(user);
        List<SimpleGrantedAuthority> authorities = ALL_AUTHORITIES.stream()
            .filter(authority -> userContext.getRoles().contains(authority.getAuthority()))
            .collect(Collectors.toList());
        SecurityContextHolder.getContext().setAuthentication(createTempAuth(authorities));
        return userContext;
    }

    private Authentication createTempAuth(List<SimpleGrantedAuthority> authorities) {
        String token = UUID.randomUUID().toString();
        return new AnonymousAuthenticationToken(token, token, authorities);
    }
}
