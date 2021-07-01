package com.example.demo.rest

import com.example.demo.Banner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange

/**
 * Created by Andrew Kuzmin on 6/19/2021.
 */

@RestController
@RequestMapping("/controller")
class CoroutinesRestController(builder: WebClient.Builder) {

    private val client = builder.baseUrl("http://localhost:8080").build()

    private val banner = Banner("title", "Welcome to our application B.")

    @GetMapping("/suspend")
    suspend fun suspendingEndpoint(): Banner {
        delay(10)
        return banner
    }

    @GetMapping("/flow")
    fun flowEndpoint() = flow {
        delay(10)
        emit(banner)
        delay(10)
        emit(banner)
    }

    @GetMapping("/deferred")
    fun deferredEndpoint() = GlobalScope.async {
        delay(10)
        banner
    }

    @GetMapping("/sequential")
    suspend fun sequential(): List<Banner> {
        val banner1 = client
            .get()
            .uri("/suspend")
            .accept(MediaType.APPLICATION_JSON)
            .awaitExchange()
            .awaitBody<Banner>()
        val banner2 = client
            .get()
            .uri("/suspend")
            .accept(MediaType.APPLICATION_JSON)
            .awaitExchange()
            .awaitBody<Banner>()
        return listOf(banner1, banner2)
    }

    @GetMapping("/parallel")
    suspend fun parallel(): List<Banner> = coroutineScope {
        val deferredBanner1: Deferred<Banner> = async {
            client
                .get()
                .uri("/suspend")
                .accept(MediaType.APPLICATION_JSON)
                .awaitExchange()
                .awaitBody<Banner>()
        }
        val deferredBanner2: Deferred<Banner> = async {
            client
                .get()
                .uri("/suspend")
                .accept(MediaType.APPLICATION_JSON)
                .awaitExchange()
                .awaitBody<Banner>()
        }
        listOf(deferredBanner1.await(), deferredBanner2.await())
    }

    @GetMapping("/error")
    suspend fun error() {
        throw IllegalStateException()
    }

    @GetMapping("/cancel")
    suspend fun cancel() {
        throw CancellationException()
    }

}
