package com.bi.mock;

import com.bi.interfaces.ISecurityService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock implementation of ISecurityService.
 * Hardcoded users for demo purposes — replace with DB-backed impl for production.
 */
public class SecurityServiceMock implements ISecurityService {

    // username → password (plain text for demo only)
    private static final Map<String, String> USERS = new HashMap<>();
    // username → role permissions
    private static final Map<String, List<String>> PERMISSIONS = new HashMap<>();

    static {
        USERS.put("admin",   "admin123");
        USERS.put("manager", "manager1");
        USERS.put("analyst", "analyst1");

        PERMISSIONS.put("admin",   Arrays.asList("dashboard", "kpi", "reports", "settings"));
        PERMISSIONS.put("manager", Arrays.asList("dashboard", "kpi", "reports"));
        PERMISSIONS.put("analyst", Arrays.asList("dashboard", "kpi"));
    }

    @Override
    public boolean authenticate(String username, String password) {
        return USERS.containsKey(username) && USERS.get(username).equals(password);
    }

    @Override
    public boolean authorize(String userId, String resource) {
        List<String> perms = PERMISSIONS.getOrDefault(userId, List.of());
        return perms.contains(resource);
    }

    @Override
    public List<String> getPermissions(String userId) {
        return PERMISSIONS.getOrDefault(userId, List.of());
    }
}
