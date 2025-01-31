import java.util.Properties;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.security.AuthInitialize;
import org.apache.geode.security.AuthenticationFailedException;

public class UserPasswordAuthInit implements AuthInitialize {
@Override
    public Properties getCredentials(Properties properties, DistributedMember distributedMember, boolean isPeer) throws AuthenticationFailedException {
    properties.setProperty("security-username", "default");
    properties.setProperty("security-password", "reallyBadPassword");
    return properties;
}
}
