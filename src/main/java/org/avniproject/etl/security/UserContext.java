package org.avniproject.etl.security;

import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class UserContext {
    private final Collection<String> roles = new HashSet<>();
    private Organisation organisation;
    private User user;
    private String authToken;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.addRolesToContext();
    }

    private void addRolesToContext() {
        String[] roles = this.user.getRoles();
        Arrays.stream(roles).forEach(this::addRole);
    }

    public UserContext addRole(String role) {
        this.roles.add(role);
        return this;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
