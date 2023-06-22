package org.avniproject.etl.security;


import org.avniproject.etl.config.IdpType;
import org.avniproject.etl.domain.User;
import org.avniproject.etl.domain.UserContextHolder;
import org.avniproject.etl.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {
    public static final String USER_NAME_HEADER = "USER-NAME";
    public static final String AUTH_TOKEN_HEADER = "AUTH-TOKEN";
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);


    private final AuthService authService;
    private final String defaultUserName;
    private final IdpType idpType;


    public AuthenticationFilter(AuthService authService, IdpType idpType, String defaultUserName) {
        this.authService = authService;
        this.idpType = idpType;
        this.defaultUserName = defaultUserName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String method = request.getMethod();
            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            String tokenForAuth;
            if (idpType.equals(IdpType.none)) {
                String userName = request.getHeader(USER_NAME_HEADER);
                tokenForAuth = StringUtils.hasLength(userName) ? userName : defaultUserName;
            } else {
                tokenForAuth = request.getHeader(AUTH_TOKEN_HEADER);
            }
            User user = authService.authenticate(tokenForAuth);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                logger.info(String.format("%s %s?%s Unauthorized access token: %s", method, requestURI, queryString, tokenForAuth));
                return;
            }
            UserContextHolder.create(authService.setupUserContext(user));
            long start = System.currentTimeMillis();
            chain.doFilter(request, response);
            long end = System.currentTimeMillis();
            logger.info(String.format("%s %s?%s User: %s Time: %s ms", method, requestURI, queryString, user.getUsername(), (end - start)));
        } catch (Exception exception) {
            this.logException(request, exception);
            throw exception;
        }
        finally {
            UserContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private void logException(HttpServletRequest request, Exception exception) {
        logger.error("Exception on Request URI", request.getRequestURI());
        logger.error("Exception Message:", exception);
    }
}
