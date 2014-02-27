package com.newresources.funkyquest.api;

/**
 * Created by BigBlackBug on 1/31/14.
 */
public class LoginCredentials {
    public String password;
    public String email;

    public LoginCredentials() {
    }

    public LoginCredentials(String email, String password) {
        this.password = password;
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
