package org.avniproject.etl.domain;

import java.util.HashSet;
import java.util.Set;

public class Account {

    private Long id;

    private String name;

    private Set<AccountAdmin> accountAdmin = new HashSet<>();

    public Set<AccountAdmin> getAccountAdmin() {
        return accountAdmin;
    }

    public void setAccountAdmin(Set<AccountAdmin> accountAdmin) {
        this.accountAdmin.clear();
        if (accountAdmin != null) {
            this.accountAdmin.addAll(accountAdmin);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
