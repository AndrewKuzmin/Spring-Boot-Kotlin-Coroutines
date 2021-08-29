package com.example.demo.rest

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
class CoroutinesControllerTests(@Autowired val client: WebTestClient) {

    private val banner = Banner("title", "Welcome to our application B.")

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun index() {
        client.get().uri("/controller/").exchange().expectStatus().is2xxSuccessful.expectBody()
    }

    @Test
    fun suspending() {
        client.get()
            .uri("/controller/suspend")
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
            .uri("/controller/sequential-flow")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList<Banner>().contains(banner, banner, banner, banner)
    }

    @Test
    fun parallelFlow() {
        client.get()
            .uri("/controller/concurrent-flow")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList<Banner>().contains(banner, banner, banner, banner)
    }

    @Test
    fun flowViaWebClient() {
        client.get()
                .uri("/controller/flow-via-webclient")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful
                .expectBodyList<Banner>().contains(banner, banner, banner, banner)
    }

    @Test
    fun error() {
        client.get().uri("/controller/error").exchange().expectStatus().is5xxServerError
    }

    @Test
    fun flowEndpoint() {
    }

    @Test
    fun deferredEndpoint() {
    }

    @Test
    fun sequential() {
    }

    @Test
    fun parallel() {
    }

    @Test
    fun cancel() {
    }

}