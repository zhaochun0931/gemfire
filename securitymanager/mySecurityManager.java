import java.util.Properties;
import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.SecurityManager;

public class mySecurityManager implements SecurityManager {
@Override

public Object authenticate(Properties credentials) throws AuthenticationFailedException {
    Boolean isAuthenticated = false;
    String username = credentials.getProperty("security-username");
    String password = credentials.getProperty("security-password");

if ("default".equals(username) && "reallyBadPassword".equals(password) ) {
    isAuthenticated = true;
} else{
    throw new AuthenticationFailedException("Wrong username/password");
}
    return isAuthenticated;
}
}
