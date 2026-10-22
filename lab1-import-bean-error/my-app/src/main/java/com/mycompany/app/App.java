package com.mycompany.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // 尝试加载包含错配 Schema 的 XML 配置文件

        ApplicationContext context = new ClassPathXmlApplicationContext("test.xml");

    }
}
