package com.example.demo.handler

import com.example.demo.Banner
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.*

/**
 * Created by Andrew Kuzmin on 6/21/2021.
 */
@Suppress("DuplicatedCode")
@Service
class Handlers(builder: WebClient.Builder) {

    private val client = builder.baseUrl("http://localhost:8080").build()

    private val banner = Banner("title", "Welcome to our application A.")

    suspend fun index(request: ServerRequest) =
        ServerResponse
            .ok()
            .renderAndAwait("index", mapOf("banner" to banner))

    suspend fun suspending(request: ServerRequest) =
        ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(banner)

    suspend fun sequentialFlow(request: ServerRequest) = flow {
        for (i in 1..4) {
            emit(client
                .get()
                .uri("/suspend")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody<Banner>())}
    }.let { ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyAndAwait(it) }

    // TODO Improve when https://github.com/Kotlin/kotlinx.coroutines/issues/1147 will be fixed
    suspend fun concurrentFlow(request: ServerRequest): ServerResponse = flow {
        for (i in 1..4) emit("/suspend")
    }.flatMapMerge {
        flow {
            emit(client
                .get()
                .uri(it)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody<Banner>())
        }
    }.let { ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyAndAwait(it) }

    suspend fun error(request: ServerRequest): ServerResponse {
        throw IllegalStateException()
    }

}
