package tk.dadle8

import com.justai.jaicf.channel.http.httpBotRouting
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {

    embeddedServer(Netty, 8889) {
        routing {
            httpBotRouting("/" to channel)
            get("/greeting") {

                khttp.post(
                    url = "https://dadle8.tk:8443/manageListItem?name=test&completed=true",
                    json = mapOf("description" to "From Alice", "id" to "_" + getRandomString(9), "title" to "test")
                )

                call.respondText("Hello")
            }
        }
    }.start(wait = true)

}
