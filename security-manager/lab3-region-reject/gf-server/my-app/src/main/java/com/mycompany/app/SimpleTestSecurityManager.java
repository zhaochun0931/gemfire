package com.mycompany.app;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;
import org.apache.geode.security.SecurityManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTestSecurityManager implements SecurityManager {

    private static final String ALLOWED_USER = "testUser";
    private static final String ALLOWED_PASS = "password123";

    // 核心高并发容器：存放 用户名 -> 允许访问的Region集合
    private final Map<String, Set<String>> userRegionMap = new ConcurrentHashMap<>();

    // 权限配置文件路径（可通过 JVM 参数 -Dgemfire.auth.file=/path/to/file 自定义）
    private String configFilePath;
    private long lastModifiedTime = 0;
    private Timer reloadTimer;

    @Override
    public void init(Properties securityProps) {
        // 1. 获取配置文件路径，若未配置则默认为当前目录下的 gemfire-auth.properties
        configFilePath = System.getProperty("gemfire.auth.file", "gemfire-auth.properties");

        // 2. 立即执行首次加载
        loadPermissions();

        // 3. 启动定时器：每 5 秒钟检查一次文件是否有变更，实现无需重启动态刷新
        reloadTimer = new Timer("AuthFileReloadTimer", true);
        reloadTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                File file = new File(configFilePath);
                if (file.exists() && file.lastModified() > lastModifiedTime) {
                    System.out.println(">>> [动态安全] 检测到权限配置文件发生变更，开始重新加载...");
                    loadPermissions();
                }
            }
        }, 5000, 5000);
    }

    /**
     * 核心加载逻辑：读取属性文件并解析到内存中
     */
    private synchronized void loadPermissions() {
        File file = new File(configFilePath);
        if (!file.exists()) {
            System.err.println(">>> [安全警告] 找不到权限配置文件: " + configFilePath + "，多租户隔离白名单将为空！");
            return;
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);

            Map<String, Set<String>> newMap = new HashMap<>();
            for (String key : props.stringPropertyNames()) {
                if (key.startsWith("users.")) {
                    String username = key.substring(6).trim(); // 提取出用户名
                    String regionsStr = props.getProperty(key);

                    Set<String> regionSet = new HashSet<>();
                    if (regionsStr != null && !regionsStr.trim().isEmpty()) {
                        String[] regions = regionsStr.split(",");
                        for (String r : regions) {
                            regionSet.add(r.trim());
                        }
                    }
                    newMap.put(username, regionSet);
                }
            }

            // 整体替换，确保高并发读取时的原子性与线程安全
            userRegionMap.clear();
            userRegionMap.putAll(newMap);
            this.lastModifiedTime = file.lastModified();
            System.out.println(">>> [动态安全] 权限文件加载成功！当前策略: " + userRegionMap);

        } catch (IOException e) {
            System.err.println(">>> [安全错误] 读取权限配置文件失败: " + e.getMessage());
        }
    }

    @Override
    public Object authenticate(Properties credentials) throws AuthenticationFailedException {
        String username = credentials.getProperty("security-username");
        String password = credentials.getProperty("security-password");

        // 生产环境此处的账号密码也可以写进 properties，这里演示仍保持 testUser 登录
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

        // 2. 放行 DATA:MANAGE 级别命令（允许通过 gfsh 创建/删除 Region）
        if (ResourcePermission.Resource.DATA.equals(permission.getResource())
                && ResourcePermission.Operation.MANAGE.equals(permission.getOperation())) {
            return true;
        }

        // 3. 运行期数据读写硬隔离审查（DATA:READ 或 DATA:WRITE）
        if (ResourcePermission.Resource.DATA.equals(permission.getResource())) {
            String targetRegion = permission.getTarget();

            // 🔍 动态配置做法：从高并发内存缓存 Map 中直接获取该用户绑定的合法 Region 列表
            Set<String> allowedRegions = userRegionMap.get(username);

            // 只要当前企图操作的 Region 在白名单集合内，就放行
            if (allowedRegions != null && allowedRegions.contains(targetRegion)) {
                System.out.println(">>> [动态授权通过] 用户 '" + username + "' 允许操作 Region: " + targetRegion);
                return true;
            } else {
                System.err.println(">>> [安全拦截] 用户 '" + username + "' 企图越权操作未经授权的 Region: " + targetRegion);
                return false; // 拦截并拒绝访问
            }
        }

        return false;
    }

    @Override
    public void close() {
        if (reloadTimer != null) {
            reloadTimer.cancel();
        }
        userRegionMap.clear();
    }
}