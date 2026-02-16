package com.xod.bdsb.server.dao;

import com.xod.bdsb.server.security.BdsbUserDetails;

public interface UserDao {
    BdsbUserDetails loadUserByUsername(String username);
}
