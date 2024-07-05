https://tanzu.vmware.com/developer/data/gemfire/blog/security-manager-basics-authentication-and-authorization/



https://tanzu.vmware.com/developer/data/gemfire/blog/security-manager-basics-authentication-only/




start locator --name=locator1 \
--J=-Dgemfire.security-manager=App \
--classpath=/root/my-app/target/my-app-1.0-SNAPSHOT.jar:/root/my-app/target/classes


start server --name=server1 \
--J=-Dgemfire.security-manager=App \
--classpath=/root/my-app/target/my-app-1.0-SNAPSHOT.jar:/root/my-app/target/classes



