//package com.mycompany.app;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;
import org.apache.geode.security.SecurityManager;




/**
 * Hello world!
 *
 */
public class App implements SecurityManager
{

    @Override
    public Object authenticate(Properties credentials) throws AuthenticationFailedException {
        return "USER";
    }


    @Override
    public boolean authorize(Object principal, ResourcePermission permission) {
        return true;
    }




}
