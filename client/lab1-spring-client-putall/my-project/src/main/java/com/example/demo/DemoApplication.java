package com.example.demo;

import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;

@SpringBootApplication
@ClientCacheApplication(locators = {
    @ClientCacheApplication.Locator(host = "127.0.0.1", port = 10334)
})
@EnableEntityDefinedRegions(
    basePackageClasses = Customer.class,
    clientRegionShortcut = ClientRegionShortcut.PROXY
)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DemoApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE); // 显式关闭 Web 容器
        app.run(args);
    }

    @Bean
    public CommandLineRunner insertData(CustomerRepository repository) {
        return args -> {
            System.out.println("=========================================");
            System.out.println("开始向 GemFire 插入 1000 条数据...");

            for (int i = 1; i <= 1000; i++) {
                Customer customer = new Customer("CUST_" + i, "User_" + i, 20 + (i % 30));
                repository.save(customer);

                if (i % 20 == 0) {
                    System.out.println("已插入 " + i + " 条...");
                }
            }

            System.out.println("插入完成！");
            long count = repository.count();
            System.out.println("当前 Customer Region 的总数据量: " + count);
            System.out.println("=========================================");
        };
    }
}
