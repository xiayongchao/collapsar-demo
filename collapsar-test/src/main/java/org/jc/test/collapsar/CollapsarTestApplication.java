package org.jc.test.collapsar;

import org.jc.framework.collapsar.annotation.EnableCollapsarConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCollapsarConfiguration(projectName = "comment",basePackages = "org.jc.test.collapsar.caches")
public class CollapsarTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollapsarTestApplication.class, args);
    }
}
