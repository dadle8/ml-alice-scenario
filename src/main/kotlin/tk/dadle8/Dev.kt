package tk.dadle8

import com.justai.jaicf.channel.http.httpBotRouting
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {

    embeddedServer(Netty, 8889) {
        routing {
            httpBotRouting("/" to channel)
        }
    }.start(wait = true)

}
