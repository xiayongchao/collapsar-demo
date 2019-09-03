package org.jc.test.collapsar;

import org.jc.framework.collapsar.annotation.CollapsarComponentScan;
import org.jc.framework.collapsar.annotation.EnableCollapsar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@CollapsarComponentScan(scope = {@CollapsarComponentScan.Rule(projectName = "comment", basePackages = {"org.jc.test.collapsar"})})
@EnableCollapsar
public class CollapsarTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollapsarTestApplication.class, args);
    }
}
