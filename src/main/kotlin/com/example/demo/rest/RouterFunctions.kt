package com.example.demo.rest

import com.example.demo.handler.Handlers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

/**
 * Created by Andrew Kuzmin on 6/21/2021.
 */
@Configuration
class RouterFunctions {

    @Bean
    fun routes(handlers: Handlers) = coRouter {
        GET("/", handlers::index)
        GET("/suspend", handlers::suspending)
        GET("/sequential-flow", handlers::sequentialFlow)
        GET("/concurrent-flow", handlers::concurrentFlow)
        GET("/error", handlers::error)
    }

}