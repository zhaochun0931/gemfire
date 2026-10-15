package com.mycompany.app;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.server.CacheServer;

public class App {

    public static void main(String[] args) {
        // 从 JVM 系统属性获取参数
        String memberName = System.getProperty("gemfire.name", "app1");
        String locatorAddress = System.getProperty("gemfire.locators", "127.0.0.1[10334]");
        boolean startServer = Boolean.getBoolean("startServer"); // 传 -DstartServer=true 则为 Ready，默认 false 为 Initializing

        System.out.println("=================================================");
        System.out.println(">>> 正在以 Peer 节点身份加入 GemFire 集群...");
        System.out.println(">>> 节点名称: " + memberName);
        System.out.println(">>> Locator: " + locatorAddress);
        System.out.println("=================================================");

        Cache cache = null;
        try {
            // 1. 声明并加入分布式系统
            cache = new CacheFactory()
                    .set("name", memberName)
                    .set("locators", locatorAddress)
                    .set("log-level", "config")
                    .create();

            System.out.println(">>> [成功] 节点 " + memberName + " 已加入集群！");

            // 2. 根据参数控制 Status 显示（Ready vs Initializing）
            if (startServer) {
                CacheServer server = cache.addCacheServer();
                server.setPort(0); // 监听随机空闲端口
                server.start();
                System.out.println(">>> CacheServer 已启动，在 list members 中将显示为: Ready");
            } else {
                System.out.println(">>> 未启动 CacheServer，在 list members 中将显示为: Initializing");
            }

            // 3. 注册 ShutdownHook，捕获 Ctrl+C 并优雅关闭 Cache
            final Cache currentCache = cache;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (currentCache != null && !currentCache.isClosed()) {
                    System.out.println("\n>>> 正在断开与 GemFire 集群的连接...");
                    currentCache.close();
                    System.out.println(">>> 节点已从集群正常注销。");
                }
            }));

            // 4. 阻塞主线程以保持节点存活
            System.out.println(">>> 节点保持运行中（按 Ctrl+C 退出进程）...");
            Thread.currentThread().join();

        } catch (InterruptedException e) {
            System.out.println(">>> 主线程被中断，正在退出...");
        } catch (Exception e) {
            System.err.println(">>> 节点启动异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cache != null && !cache.isClosed()) {
                cache.close();
            }
        }
    }
}
