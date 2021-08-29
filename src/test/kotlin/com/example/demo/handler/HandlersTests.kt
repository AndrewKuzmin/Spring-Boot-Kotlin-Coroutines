package com.example.demo.handler

import com.example.demo.Banner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList


/**
 * Created by Andrew Kuzmin on 8/29/2021.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class HandlersTests(@Autowired val client: WebTestClient) {

    private val banner = Banner("title", "Welcome to our application A.")

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun index() {
        client.get().uri("/").exchange().expectStatus().is2xxSuccessful.expectBody()
    }

    @Test
    fun suspending() {
        client.get()
            .uri("/suspend")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody<Banner>()
            .isEqualTo(banner)
    }

    @Test
    fun sequentialFlow() {
        client.get()
            .uri("/sequential-flow")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList<Banner>().contains(banner, banner, banner, banner)
    }

    @Test
    fun concurrentFlow() {
        client.get()
            .uri("/concurrent-flow")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList<Banner>().contains(banner, banner, banner, banner)
    }

    @Test
    fun error() {
        client.get().uri("/error").exchange().expectStatus().is5xxServerError
    }

}