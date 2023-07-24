package com.increff.pos.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("com.increff.pos")

@EnableWebMvc
@PropertySources({ //
        @PropertySource(value = "file:./pos.properties", ignoreResourceNotFound = true) //
})
@EnableScheduling
@EnableAsync
public class SpringConfig {


}
