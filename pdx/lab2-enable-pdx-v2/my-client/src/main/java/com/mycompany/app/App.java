package com.mycompany.app;

/**
 * GemFire PDX 验证客户端 - 支持 Customer 包含 email 字段的新版本
 */

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

public class App {

    private static final String REGION_NAME = "CustomerRegion";

    public static void main(String[] args) {
        System.out.println("Hello World!");

        // 1. 连接 GemFire 集群 (Locator)
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .set("log-level", "info")
                // 自动序列化器匹配 Customer 所在包路径
                .setPdxSerializer(new ReflectionBasedAutoSerializer("com.mycompany.app.*"))
                // 开启 read-serialized=true，避免客户端自动反序列化，便于验证 PdxInstance 底层结构
                .setPdxReadSerialized(true)
                .create();

        try {
            // 创建客户端 PROXY Region 句柄（直连 Docker 服务端的同名 Region）
            Region<String, Object> region = cache
                    .<String, Object>createClientRegionFactory(ClientRegionShortcut.PROXY)
                    .create(REGION_NAME);

            System.out.println("========== 2. 批量准备并写入 5 个 Customer 实体（演示新旧字段混用） ==========");
            Map<String, Customer> customers = new LinkedHashMap<>();

            // 写入带有 email 字段的新数据（使用 4 参数构造器）
            customers.put("CUST-001", new Customer("CUST-001", "Alice", 28, "alice@example.com"));
            customers.put("CUST-002", new Customer("CUST-002", "Bob", 32, "bob@example.com"));
            customers.put("CUST-003", new Customer("CUST-003", "Charlie", 24, "charlie@example.com"));

            // 写入不带 email 的数据（调用 3 参数兼容构造器，email 自动为 null）
            customers.put("CUST-004", new Customer("CUST-004", "Diana", 41));
            customers.put("CUST-005", new Customer("CUST-005", "Ethan", 35));

            // 一次网络请求批量写入
            region.putAll(customers);
            System.out.println("成功批量写入 " + customers.size() + " 条数据！\n");

            System.out.println("========== 3. 循环验证服务端存储的 PDX 实例 ==========");
            for (String key : customers.keySet()) {
                Object result = region.get(key);

                if (result instanceof PdxInstance) {
                    PdxInstance pdxInstance = (PdxInstance) result;

                    // 1. 免反序列化直接按字段读取（包括新增的 email）
                    String customerId = (String) pdxInstance.getField("customerId");
                    String customerName = (String) pdxInstance.getField("name");
                    Integer age = (Integer) pdxInstance.getField("age");
                    String email = (String) pdxInstance.getField("email");

                    System.out.println(String.format("Key [%s] -> PDX 字段: id=%s, name=%s, age=%d, email=%s",
                            key, customerId, customerName, age, email));

                    // 2. 按需手动还原为实体类验证
                    Customer restored = (Customer) pdxInstance.getObject();
                    System.out.println("       \\-> 还原 Java 对象: " + restored);
                } else {
                    System.err.println("Key [" + key + "] 读取异常，非 PdxInstance 类型: " 
                            + (result != null ? result.getClass().getName() : "null"));
                }
            }

        } catch (Exception e) {
            System.err.println("执行过程中发生异常：");
            e.printStackTrace();
        } finally {
            // 关闭客户端连接
            cache.close();
            System.out.println("\n========== 4. 客户端缓存已安全关闭 ==========");
        }
    }
}
