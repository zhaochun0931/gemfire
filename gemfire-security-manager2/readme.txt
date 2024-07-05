https://tanzu.vmware.com/developer/data/gemfire/blog/security-manager-basics-authentication-and-authorization/



https://tanzu.vmware.com/developer/data/gemfire/blog/security-manager-basics-authentication-only/




start locator


start server --locators=localhost[10334] --J=-Dgemfire.security-manager=App --classpath=/root/my-app/target/my-app-1.0-SNAPSHOT.jar:/root/my-app/target/classes --user=admin --password=password


