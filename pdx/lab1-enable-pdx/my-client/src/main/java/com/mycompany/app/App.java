package com.mycompany.app;

/**
 * Hello world!
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

        // 1. Connect to the GemFire Cluster (Locators)
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .set("log-level", "info")
                // 匹配 Customer 所在的包路径
                .setPdxSerializer(new ReflectionBasedAutoSerializer("com.mycompany.app.*"))
                // 开启 read-serialized=true，直接以 PdxInstance 读取
                .setPdxReadSerialized(true)
                .create();

        try {
            // 创建客户端 PROXY Region 句柄
            Region<String, Object> region = cache
                    .<String, Object>createClientRegionFactory(ClientRegionShortcut.PROXY)
                    .create(REGION_NAME);

            System.out.println("========== 2. 批量准备并写入 5 个 Customer 实体 ==========");
            // 使用 LinkedHashMap 保持写入顺序
            Map<String, Customer> customers = new LinkedHashMap<>();
            customers.put("CUST-001", new Customer("CUST-001", "Alice", 28));
            customers.put("CUST-002", new Customer("CUST-002", "Bob", 32));
            customers.put("CUST-003", new Customer("CUST-003", "Charlie", 24));
            customers.put("CUST-004", new Customer("CUST-004", "Diana", 41));
            customers.put("CUST-005", new Customer("CUST-005", "Ethan", 35));

            // 一次网络请求批量写入 5 条记录
            region.putAll(customers);
            System.out.println("成功批量写入 " + customers.size() + " 条数据！");

            System.out.println("========== 3. 循环验证服务端存储的 5 个 PDX 实例 ==========");
            for (String key : customers.keySet()) {
                Object result = region.get(key);

                if (result instanceof PdxInstance) {
                    PdxInstance pdxInstance = (PdxInstance) result;

                    // 免反序列化直接按字段读取
                    String customerId = (String) pdxInstance.getField("customerId");
                    String customerName = (String) pdxInstance.getField("name");
                    Integer age = (Integer) pdxInstance.getField("age");

                    System.out.println(String.format("Key [%s] -> PDX 字段: id=%s, name=%s, age=%d",
                            key, customerId, customerName, age));

                    // 按需手动还原为实体类验证
                    Customer restored = (Customer) pdxInstance.getObject();
                    System.out.println("       \\-> 还原 Java 对象: " + restored);
                } else {
                    System.err.println("Key [" + key + "] 读取异常，非 PdxInstance 类型: " + (result != null ? result.getClass().getName() : "null"));
                }
            }

        } catch (Exception e) {
            System.err.println("执行过程中发生异常：");
            e.printStackTrace();
        } finally {
            // 关闭客户端连接
            cache.close();
            System.out.println("========== 4. 客户端缓存已安全关闭 ==========");
        }
    }
}
