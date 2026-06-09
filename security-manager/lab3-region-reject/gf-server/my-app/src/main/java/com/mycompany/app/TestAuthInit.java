package com.mycompany.app;

import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.security.AuthInitialize;
import org.apache.geode.security.AuthenticationFailedException;
import java.util.Properties;

public class TestAuthInit implements AuthInitialize {

    @Override
    public Properties getCredentials(Properties securityProps, DistributedMember server, boolean isPeer)
            throws AuthenticationFailedException {

        Properties credentials = new Properties();
        
        // 【修正】從傳入的 securityProps 中讀取 gfsecurity.properties 的內容
        String user = securityProps.getProperty("security-username");
        String pass = securityProps.getProperty("security-password");
        
        // 後備機制：如果設定檔沒寫，才去抓 System.getProperty
        if (user == null) user = System.getProperty("test.user");
        if (pass == null) pass = System.getProperty("test.pass");
        
        // 最終預設值
        if (user == null) user = "anonymous";
        if (pass == null) pass = "wrong";

        credentials.setProperty("security-username", user.trim());
        credentials.setProperty("security-password", pass.trim());
        return credentials;
    }
}
