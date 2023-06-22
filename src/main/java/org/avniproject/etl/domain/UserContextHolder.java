package org.avniproject.etl.domain;

import org.avniproject.etl.security.UserContext;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    public static void create(UserContext context) {
        userContext.set(context);
    }

    public static void clear() {
        userContext.remove();
    }
}
