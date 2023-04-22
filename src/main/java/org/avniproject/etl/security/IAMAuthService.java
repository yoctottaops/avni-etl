package org.avniproject.etl.security;

import com.auth0.jwk.SigningKeyNotFoundException;
import org.avniproject.etl.domain.User;

public interface IAMAuthService {
    User getUserFromToken(String token) throws SigningKeyNotFoundException;
}