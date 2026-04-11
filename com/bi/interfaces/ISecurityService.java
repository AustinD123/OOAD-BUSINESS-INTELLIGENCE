package com.bi.interfaces;

import java.util.List;

public interface ISecurityService {
    boolean authenticate(String username, String password);

    boolean authorize(String userId, String resource);

    List<String> getPermissions(String userId);
}
