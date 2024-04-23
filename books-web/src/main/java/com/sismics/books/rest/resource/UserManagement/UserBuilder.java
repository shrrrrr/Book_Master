package com.sismics.books.rest.resource;

import com.sismics.books.core.model.jpa.User;
import java.util.Date;

public class UserBuilder {
    private String username;
    private String password;
    private String email;
    private String roleId;
    private String localeId;
    private String theme;
    private Date createDate;
    private boolean firstConnection;

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withRoleId(String roleId) {
        this.roleId = roleId;
        return this;
    }

    public UserBuilder withLocaleId(String localeId) {
        this.localeId = localeId;
        return this;
    }

    public UserBuilder withTheme(String theme) {
        this.theme = theme;
        return this;
    }

    public UserBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public UserBuilder withFirstConnection(boolean firstConnection) {
        this.firstConnection = firstConnection;
        return this;
    }

    public User build() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRoleId(roleId);
        user.setLocaleId(localeId);
        user.setTheme(theme);
        user.setCreateDate(createDate);
        user.setFirstConnection(firstConnection);
        return user;
    }
}
