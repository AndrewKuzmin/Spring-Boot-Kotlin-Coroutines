package com.example.demo.config

import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

/**
 * Created by GEDC_AAKuzmin on 25.05.2020.
 */
@Configuration
class ApplicationConfiguration {

    companion object : Log() {}

    @Bean
    fun commandLineRunnerShowBeans(ctx: ApplicationContext): CommandLineRunner {
        return CommandLineRunner { _: Array<String?>? ->
            val beanNames = ctx.beanDefinitionNames
            logger.info(String.format("Let's inspect the %s beans provided by Spring Boot:", beanNames.size))
            Arrays.sort(beanNames)
            for (beanName in beanNames) {
                logger.info(beanName)
            }
        }
    }

}