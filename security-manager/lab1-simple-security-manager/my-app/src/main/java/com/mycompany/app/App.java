package com.mycompany.app;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.Region;

public class App {
    public static void main(String[] args) {
        System.out.println("=== 开始测试 2：合法账号连线与真实 Region 读写测试 ===");

        // 设置正确的账号与密码
        System.setProperty("test.user", "testUser");
        System.setProperty("test.pass", "password123");

        ClientCache goodCache = null;
        try {
            // 1. 初始化 ClientCache 并加载认证初始化类
            goodCache = new ClientCacheFactory()
                    .addPoolLocator("172.16.204.139", 10334)
                    .set("security-client-auth-init", "com.mycompany.app.TestAuthInit")
                    .create();

            // 2. 绑定您服务器上真实存在的 exampleRegion
            ClientRegionFactory<String, String> regionFactory = goodCache.createClientRegionFactory(ClientRegionShortcut.PROXY);
            Region<String, String> exampleRegion = regionFactory.create("exampleRegion");

            System.out.println("-> 开始写入测试数据到 exampleRegion...");

            // 3. 执行 put 操作写入几笔测试记录 (这会自动迫使 Client 发送账号密码给 Server 验证)
            exampleRegion.put("emp_001", "Alice");
            exampleRegion.put("emp_002", "Bob");
            exampleRegion.put("emp_003", "Charlie");
            System.out.println("-> 成功写入 3 笔数据。");

            System.out.println("-> 开始读取刚刚写入的数据进行验证...");

            // 4. 执行 get 操作读取出来验证
            String val1 = exampleRegion.get("emp_001");
            String val2 = exampleRegion.get("emp_002");
            String val3 = exampleRegion.get("emp_003");

            System.out.println("   [读取结果] emp_001 = " + val1);
            System.out.println("   [读取结果] emp_002 = " + val2);
            System.out.println("   [读取结果] emp_003 = " + val3);

            // 没有抛出任何 Exception，代表认证成功且拥有该 Region 的读写权限！
            System.out.println("\n【TEST PASSED】成功！'testUser' 顺利通过安全验证，并成功读写 exampleRegion！");

        } catch (Exception e) {
            // 如果抛出异常，可能是密码错、或者该账号在 SecurityManager 里没有被赋予 exampleRegion 的权限
            System.err.println("\n【TEST FAILED】错误：无法操作 exampleRegion！");
            System.err.println("错误详细信息: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (goodCache != null && !goodCache.isClosed()) {
                goodCache.close();
            }
        }
    }
}