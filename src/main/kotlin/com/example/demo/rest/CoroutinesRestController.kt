package com.example.demo.rest

import com.example.demo.Banner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import org.springframework.http.MediaType
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.reactive.function.client.bodyToFlow

/**
 * Created by Andrew Kuzmin on 6/19/2021.
 */

@RestController
@RequestMapping("/controller")
class CoroutinesRestController(builder: WebClient.Builder) {

    private val client = builder.baseUrl("http://localhost:8080").build()

    private val banner = Banner("title", "Welcome to our application B.")

    @GetMapping("/suspend")
    @ResponseBody
    suspend fun suspendingEndpoint(): Banner {
        delay(10)
        return banner
    }

    @GetMapping("/deferred")
    @ResponseBody
    fun deferredEndpoint(): Deferred<Banner> = GlobalScope.async {
        delay(10)
        banner
    }

    @GetMapping("/")
    suspend fun render(model: Model): String {
        delay(10)
        model["banner"] = banner
        return "index"
    }

    @GetMapping("/sequential-flow")
    @ResponseBody
    suspend fun sequentialFlow() = flow {
        for (i in 1..4) {
            emit(
                client
                    .get()
                    .uri("/controller/suspend")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<Banner>()
            )
        }
    }

    // TODO Improve when https://github.com/Kotlin/kotlinx.coroutines/issues/1147 will be fixed
    @GetMapping("/concurrent-flow")
    @ResponseBody
    suspend fun concurrentFlow() = flow {
        for (i in 1..4) emit("/controller/suspend")
    }.flatMapMerge {
        flow {
            emit(
                client
                    .get()
                    .uri(it)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<Banner>()
            )
        }
    }

    @GetMapping("/flow-via-webclient")
    @ResponseBody
    suspend fun flowViaWebClient() =
        client.get()
            .uri("/controller/concurrent-flow")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlow<Banner>()

    @GetMapping("/error")
    @ResponseBody
    suspend fun error() {
        throw IllegalStateException()
    }

    /* Spring docs */

    @GetMapping("/flow")
    fun flowEndpoint() = flow {
        delay(10)
        emit(banner)
        delay(10)
        emit(banner)
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

    @GetMapping("/cancel")
    suspend fun cancel() {
        throw CancellationException()
    }

}
