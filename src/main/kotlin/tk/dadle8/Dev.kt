package tk.dadle8

import com.justai.jaicf.channel.http.httpBotRouting
import io.ktor.application.Application
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    routing {
        httpBotRouting("/" to channel)
    }
}
