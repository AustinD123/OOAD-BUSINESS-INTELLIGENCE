package com.bi.controller;

import com.bi.interfaces.ISecurityService;

/**
 * Controller for the Login screen.
 * Sits between LoginPanel (View) and ISecurityService (Model).
 * No Swing imports — purely logic.
 */
public class LoginController {

    private final ISecurityService securityService;
    private String loggedInUser = "";

    public LoginController(ISecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Validates credentials via ISecurityService.
     *
     * @param username entered username
     * @param password entered password
     * @return true if authentication succeeds
     */
    public boolean login(String username, String password) {
        if (username == null || username.isBlank()) return false;
        if (password == null || password.isEmpty())  return false;

        boolean ok = securityService.authenticate(username.trim(), password);
        if (ok) loggedInUser = username.trim();
        return ok;
    }

    /** Returns the username of the currently logged-in user. */
    public String getLoggedInUser() {
        return loggedInUser;
    }

    /** Returns the permissions list for the logged-in user. */
    public java.util.List<String> getPermissions() {
        return securityService.getPermissions(loggedInUser);
    }
}
