package com.mycompany.app;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.Region;
import org.apache.geode.security.NotAuthorizedException;

import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("=== 开始多 Region 动态权限探测测试 ===");

        // 显式声明正确的测试凭据
        System.setProperty("test.user", "testUser");
        System.setProperty("test.pass", "password123");

        // 🔍 定义客户端想要尝试访问的所有 Region 列表（您可以自由在这里增加更多 Region 名字）
        List<String> regionsToTest = Arrays.asList("exampleRegion1", "exampleRegion2", "exampleRegion3");

        ClientCache cache = null;
        try {
            // 1. 创建客户端连接池
            cache = new ClientCacheFactory()
                    .addPoolLocator("localhost", 10334)
                    .set("security-client-auth-init", "com.mycompany.app.TestAuthInit")
                    .create();

            ClientRegionFactory<String, String> regionFactory = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

            System.out.println("当前登录用户: " + System.getProperty("test.user"));
            System.out.println("准备探测的 Region 列表: " + regionsToTest);
            System.out.println("------------------------------------------------");

            // 2. 🔂 使用循环动态遍历每个 Region，自动识别哪些被放行，哪些被拦截
            for (String regionName : regionsToTest) {
                System.out.println("\n>>> [开始测试] 正在尝试访问 Region: [" + regionName + "]...");

                try {
                    // 创建客户端 Proxy Region 映射
                    Region<String, String> region = regionFactory.create(regionName);

                    // 尝试写入数据（这一步会强制触发 Server 端的 authorize() 校验）
                    String testKey = "key_" + regionName;
                    String testValue = "Data_for_" + regionName;
                    region.put(testKey, testValue);

                    // 如果成功走到这里，说明该 Region 没有被限制
                    System.out.println("   【放行成功】✓ 用户对 [" + regionName + "] 拥有读写权限。");
                    System.out.println("   [读取验证] " + testKey + " = " + region.get(testKey));

                } catch (Exception e) {
                    // 检查是否是被 Server 端安全机制拦截
                    if (e.getMessage().contains("not authorized") ||
                            e instanceof NotAuthorizedException ||
                            e.getCause() instanceof NotAuthorizedException) {

                        System.err.println("   【拦截成功】✗ 访问被拒绝！Server 安全管理器已限制该用户操作 [" + regionName + "]。");
                        System.err.println("   [原因提示] " + e.getMessage());
                    } else {
                        // 捕获其他未知错误（例如 Server 端根本没创建这个 Region 容器）
                        System.err.println("   【操作失败】⚠ 发生非安全类错误: " + e.getMessage());
                    }
                }
            }

            System.out.println("\n------------------------------------------------");
            System.out.println("=== 多 Region 动态探测结束 ===");

        } finally {
            if (cache != null && !cache.isClosed()) {
                cache.close();
            }
        }
    }
}