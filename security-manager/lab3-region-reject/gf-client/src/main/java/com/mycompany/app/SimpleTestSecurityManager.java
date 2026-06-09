package com.mycompany.app;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;
import org.apache.geode.security.SecurityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SimpleTestSecurityManager implements SecurityManager {

    private static final String ALLOWED_USER = "testUser";
    private static final String ALLOWED_PASS = "password123";

    // 【核心修正】将原本的单个 Region 字符串升级为允许访问的 Region 列表（白名单）
    private static final List<String> PERMITTED_REGIONS = Arrays.asList("exampleRegion1", "exampleRegion2");

    @Override
    public void init(Properties securityProps) {
        System.out.println(">>> SimpleTestSecurityManager 启动。当前白名单 Region 列表: " + PERMITTED_REGIONS);
    }

    @Override
    public Object authenticate(Properties credentials) throws AuthenticationFailedException {
        String username = credentials.getProperty("security-username");
        String password = credentials.getProperty("security-password");

        if (ALLOWED_USER.equals(username) && ALLOWED_PASS.equals(password)) {
            return username;
        }
        throw new AuthenticationFailedException("Access Denied: 认证失败。");
    }

    @Override
    public boolean authorize(Object principal, ResourcePermission permission) {
        String username = (String) principal;

        // 1. 放行所有 CLUSTER 级别运维命令
        if (ResourcePermission.Resource.CLUSTER.equals(permission.getResource())) {
            return true;
        }

        // 2. 放行 DATA:MANAGE 级别命令（允许创建/删除 Region）
        if (ResourcePermission.Resource.DATA.equals(permission.getResource())
                && ResourcePermission.Operation.MANAGE.equals(permission.getOperation())) {
            return true;
        }

        // 3. 运行期数据读写硬隔离审查
        if (ResourcePermission.Resource.DATA.equals(permission.getResource())) {
            String targetRegion = permission.getTarget();

            // 【核心修正】使用 .contains(targetRegion) 检查当前操作的 Region 是否在白名单列表中
            if (ALLOWED_USER.equals(username) && PERMITTED_REGIONS.contains(targetRegion)) {
                System.out.println(">>> [数据操作放行] 用户 '" + username + "' 允许读写 Region: " + targetRegion);
                return true;
            } else {
                System.err.println(">>> [安全拦截拒绝!!] 用户 '" + username + "' 企图越权读写未经授权的 Region: " + targetRegion);
                return false;
            }
        }

        return false;
    }

    @Override
    public void close() {}
}