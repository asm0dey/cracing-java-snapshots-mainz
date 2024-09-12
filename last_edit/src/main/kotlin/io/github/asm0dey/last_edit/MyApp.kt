    package io.github.asm0dey.last_edit

import org.crac.Context
import org.crac.Core
import org.crac.Resource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.Disposable

@SpringBootApplication
@EnableScheduling
class MyApp

fun main(args: Array<String>) {
    runApplication<MyApp>(*args)
}

@RestController
class LastMessageController{
    var data = ""
    private final val stream = run {
        val client = WebClient.create("https://stream.wikimedia.org/v2/stream")
        client.get().uri("/recentchange").retrieve().bodyToFlux<String>()
    }
    private lateinit var disp: Disposable

    init {
        disp = stream.subscribe { data = it }
    }

    @GetMapping("/")
    fun data() = data
}
