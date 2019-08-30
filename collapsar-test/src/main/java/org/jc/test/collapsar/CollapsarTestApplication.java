package org.jc.test.collapsar;

import org.jc.framework.collapsar.annotation.CollapsarComponentScan;
import org.jc.framework.collapsar.annotation.EnableCollapsarConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@CollapsarComponentScan(projectName = "ask", basePackages = {"org.jc.test.collapsar.common"})
@EnableCollapsarConfiguration(projectName = "comment", basePackages = {"org.jc.test.collapsar.caches", "org.jc.test.collapsar.penetration"})
public class CollapsarTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollapsarTestApplication.class, args);
    }
}
