//package com.mycompany.app;

import java.util.Properties;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.security.AuthInitialize;
import org.apache.geode.security.AuthenticationFailedException;

public class UserPasswordAuthInit implements AuthInitialize {
    @Override
    public Properties getCredentials(Properties properties, DistributedMember distributedMember, boolean isPeer) throws AuthenticationFailedException {
        properties.setProperty("security-username", "myuser");
        properties.setProperty("security-password", "mypassword");
        return properties;
    }
}
