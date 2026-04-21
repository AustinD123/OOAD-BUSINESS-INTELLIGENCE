package com.bi.security;

import com.bi.db.ERPClient;
import com.bi.exceptions.AuthenticationFailedException;
import com.bi.exceptions.UnauthorizedAccessException;
import com.bi.interfaces.ISecurityService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SecurityServiceImpl implements ISecurityService to handle user authentication,
 * resource authorisation, and permission retrieval.
 *
 * It tries to authenticate against the shared RDS `users` table via the ERP SDK.
 * If the SDK is unavailable or the user is not found in RDS, it falls back to a
 * hardcoded in-memory check so the console app can still be demoed offline.
 *
 * Database access: ERPClient (Integration team SDK) — no direct JDBC.
 */
public class SecurityServiceImpl implements ISecurityService {

    /**
     * In-memory role→permissions map.
     * Used as the authoritative permission source (the shared RDS `users` table
     * grants the username but we manage permission rules locally to avoid tight
     * coupling on the Integration team's user schema).
     */
    private static final Map<String, List<String>> ROLE_PERMISSIONS = new HashMap<>();

    static {
        List<String> adminPerms = new ArrayList<>();
        adminPerms.add("VIEW_DASHBOARD");
        adminPerms.add("RUN_QUERY");
        adminPerms.add("GENERATE_REPORT");
        adminPerms.add("MANAGE_USERS");
        adminPerms.add("VIEW_KPI");
        adminPerms.add("EXPORT_DATA");
        ROLE_PERMISSIONS.put("ADMIN", adminPerms);

        List<String> analystPerms = new ArrayList<>();
        analystPerms.add("VIEW_DASHBOARD");
        analystPerms.add("RUN_QUERY");
        analystPerms.add("GENERATE_REPORT");
        analystPerms.add("VIEW_KPI");
        analystPerms.add("EXPORT_DATA");
        ROLE_PERMISSIONS.put("ANALYST", analystPerms);

        List<String> viewerPerms = new ArrayList<>();
        viewerPerms.add("VIEW_DASHBOARD");
        viewerPerms.add("VIEW_KPI");
        ROLE_PERMISSIONS.put("VIEWER", viewerPerms);

        List<String> managerPerms = new ArrayList<>();
        managerPerms.add("VIEW_DASHBOARD");
        managerPerms.add("RUN_QUERY");
        managerPerms.add("GENERATE_REPORT");
        managerPerms.add("VIEW_KPI");
        ROLE_PERMISSIONS.put("MANAGER", managerPerms);
    }

    /**
     * Authenticates a user by checking the `users` table on the shared RDS.
     * Falls back to hardcoded admin/admin123 if the SDK is unavailable.
     *
     * @param username the username to authenticate
     * @param password the plain-text password
     * @return true if authentication succeeds
     * @throws AuthenticationFailedException if credentials are invalid or null
     */
    @Override
    public boolean authenticate(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new AuthenticationFailedException("Username cannot be null or empty.");
        }
        if (password == null || password.isBlank()) {
            throw new AuthenticationFailedException("Password cannot be null or empty.");
        }

        System.out.println("[SecurityService] Authenticating user: " + username);

        String hashedInput = hashPassword(password);

        try {
            // Query the shared users table for a matching username + password_hash
            List<Map<String, Object>> rows = ERPClient.readAll(
                    "users",
                    Map.of("username", username, "password_hash", hashedInput)
            );

            if (!rows.isEmpty()) {
                System.out.println("[SecurityService] Authentication SUCCESS (RDS) for: " + username);
                return true;
            }

            // Username found but password did not match
            throw new AuthenticationFailedException(
                    "Authentication failed for user: " + username);

        } catch (AuthenticationFailedException e) {
            throw e; // re-throw our own exception unchanged
        } catch (Exception sdkEx) {
            System.err.println("[SecurityService] RDS unavailable, using in-memory fallback: "
                    + sdkEx.getMessage());
            // Offline fallback: admin / admin123
            if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
                System.out.println("[SecurityService] Fallback auth SUCCESS for: " + username);
                return true;
            }
            throw new AuthenticationFailedException(
                    "Authentication failed for user: " + username);
        }
    }

    /**
     * Authorises a user (by userId / username) to access a specific resource
     * based on their role's permission set.
     *
     * @param userId   the username or role string
     * @param resource the permission being checked (e.g. "GENERATE_REPORT")
     * @return true if authorised
     * @throws UnauthorizedAccessException if the user lacks permission
     */
    @Override
    public boolean authorize(String userId, String resource) {
        if (userId == null || userId.isBlank()) {
            throw new UnauthorizedAccessException("userId cannot be null or empty.");
        }
        if (resource == null || resource.isBlank()) {
            throw new UnauthorizedAccessException("Resource cannot be null or empty.");
        }

        System.out.println("[SecurityService] Authorising user '"
                + userId + "' for resource: " + resource);

        List<String> permissions = getPermissions(userId);

        if (permissions.contains(resource)) {
            System.out.println("[SecurityService] Authorisation GRANTED: "
                    + userId + " -> " + resource);
            return true;
        }

        throw new UnauthorizedAccessException(
                "User '" + userId + "' is not authorised to access resource: " + resource);
    }

    /**
     * Retrieves the list of permissions for a user.
     *
     * Attempts to read the user's role from the shared RDS `users` table.
     * Falls back to treating the userId itself as a role name if RDS is unavailable.
     *
     * @param userId the username
     * @return list of permission strings
     * @throws UnauthorizedAccessException if userId is null
     */
    @Override
    public List<String> getPermissions(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new UnauthorizedAccessException(
                    "userId cannot be null or empty for permission lookup.");
        }

        try {
            List<Map<String, Object>> rows = ERPClient.readAll(
                    "users", Map.of("username", userId));

            if (!rows.isEmpty()) {
                Object roleObj = rows.get(0).get("role");
                if (roleObj != null) {
                    String role = roleObj.toString().toUpperCase();
                    List<String> perms = ROLE_PERMISSIONS.getOrDefault(role, new ArrayList<>());
                    System.out.println("[SecurityService] Permissions for user '" + userId
                            + "' (role=" + role + "): " + perms);
                    return perms;
                }
            }

        } catch (Exception e) {
            System.err.println("[SecurityService] RDS unavailable for permission lookup, "
                    + "using in-memory map: " + e.getMessage());
        }

        // Fallback: treat userId itself as role name (e.g. "ADMIN", "ANALYST")
        String fallbackRole = userId.toUpperCase();
        List<String> fallbackPerms =
                ROLE_PERMISSIONS.getOrDefault(fallbackRole, new ArrayList<>());
        System.out.println("[SecurityService] Fallback permissions for '"
                + userId + "': " + fallbackPerms);
        return fallbackPerms;
    }

    // ─── private helpers ──────────────────────────────────────────────────────

    /**
     * SHA-256 hex hash of the plain-text password, matching the stored password_hash
     * column in the shared RDS users table.
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md =
                    java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(
                    password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return password; // SHA-256 is always present in Java — unreachable
        }
    }
}
