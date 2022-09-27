package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*

fun main() {
    val port = System.getenv("daichang.port")?.toIntOrNull() ?: 8080
    val host = System.getenv("daichang.host") ?: "127.0.0.1"
    embeddedServer(Netty, port = port, host = host, watchPaths = listOf("classes", "/resources/static/", "resources")) {
        configureTemplating()
        configureRouting()
    }.start(wait = true)
}
