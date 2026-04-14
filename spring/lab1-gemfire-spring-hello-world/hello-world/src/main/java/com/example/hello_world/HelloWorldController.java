package com.example.hello_world;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    private final HelloWorldService helloWorldService;

    public HelloWorldController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @GetMapping("/")
    public String hello(@RequestParam(value = "key", defaultValue = "hello") String key) {
        // Start a timer
        long startTime = System.currentTimeMillis();

        // Fetch the value from the Service (which will check GemFire first)
        String value = helloWorldService.getHelloValue(key);

        // Stop the timer
        long elapsedTime = System.currentTimeMillis() - startTime;

        // Output the results directly to the browser
        return String.format(
                "key: %s <br/> value: %s <br/> time to look up: %dms",
                key, value, elapsedTime
        );
    }
}