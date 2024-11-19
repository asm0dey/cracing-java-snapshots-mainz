package io.github.asm0dey.last_edit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
@EnableScheduling
class MyApp

fun main(args: Array<String>) {
    runApplication<MyApp>(*args)
}

const val LAST_EDIT_URL = "https://stream.wikimedia.org/v2/stream/recentchange"

@RestController
class SseReader {
    private val client = WebClient.create(LAST_EDIT_URL)
    private var lastData: String? = null
    private val dataFlux = client.get()
        .retrieve()
        .bodyToFlux(String::class.java)
        .doOnNext { lastData = it }

    init {
        dataFlux.subscribe()
    }

    @GetMapping("/last-data")
    fun getLastData(): String? {
        return lastData
    }
}

