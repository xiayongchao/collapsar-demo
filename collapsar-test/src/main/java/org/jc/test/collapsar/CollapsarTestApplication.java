package org.jc.test.collapsar;

import org.jc.framework.collapsar.annotation.EnableCollapsarConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@CollapsarComponentScan(projectName = "ask", basePackages = {"org.jc.test.collapsar.common", "org.jc.test.collapsar.penetration"})
@EnableCollapsarConfiguration(projectName = "comment", basePackages = {"org.jc.test.collapsar"})
public class CollapsarTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollapsarTestApplication.class, args);
    }
}
