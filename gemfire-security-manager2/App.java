package BasicSecurityManager;

import java.util.Properties;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.SecurityManager;

public class App implements SecurityManager {

  @Override

  public Object authenticate(Properties credentials) throws AuthenticationFailedException {

    Boolean isAuthenticated = false;
    String username = credentials.getProperty("security-username");
    String password = credentials.getProperty("security-password");

    if ("myuser".equals(username) && "mypassword".equals(password) ) {
      isAuthenticated = true;
    } else{
      throw new AuthenticationFailedException("Wrong username/password");
    }
    return isAuthenticated;
  }

}
