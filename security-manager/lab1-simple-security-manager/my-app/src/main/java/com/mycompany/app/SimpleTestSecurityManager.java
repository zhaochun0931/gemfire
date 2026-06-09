package com.mycompany.app;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;
import org.apache.geode.security.SecurityManager;
import java.util.Properties;

public class SimpleTestSecurityManager implements SecurityManager {

    private static final String ALLOWED_USER = "testUser";
    private static final String ALLOWED_PASS = "password123";

    @Override
    public void init(Properties securityProps) {
        System.out.println(">>> SimpleTestSecurityManager Initialized. Only '" + ALLOWED_USER + "' is permitted.");
    }

    @Override
    public Object authenticate(Properties credentials) throws AuthenticationFailedException {
        String username = credentials.getProperty("security-username");
        String password = credentials.getProperty("security-password");

        // Explicitly only permit the chosen test user
        if (ALLOWED_USER.equals(username) && ALLOWED_PASS.equals(password)) {
            System.out.println(">>> [AUTH PASSED] User '" + username + "' successfully authenticated.");
            return username; // This becomes the 'principal' for authorization
        }

        System.out.println(">>> [AUTH FAILED] Access denied for user: '" + username + "'");
        throw new AuthenticationFailedException("Access Denied: Invalid credentials or unauthorized user.");
    }

    @Override
    public boolean authorize(Object principal, ResourcePermission permission) {
        // Since authenticate() only lets 'testUser' through, principal will always be 'testUser' here
        if (ALLOWED_USER.equals(principal)) {
            return true; // Permit this specific user to perform all actions (Read/Write/Manage)
        }
        return false;
    }

    @Override
    public void close() {
        // Cleanup if necessary
    }
}