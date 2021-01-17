package tk.dadle8

import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.channel.yandexalice.model.AliceEvent
import com.justai.jaicf.model.scenario.Scenario

object MainScenario : Scenario() {
    init {
        state("main") {
            activators {
                event(AliceEvent.START)
            }

            action {
                reactions.say("Привет! Скажи мне что-нибудь, а я запишу")
                reactions.alice?.buttons("Запиши")
            }
        }

        state("write") {
            activators {
                regex("запиши .+")
            }

            action {
                val tokens = request.alice?.request?.nlu?.tokens
                val items = tokens?.subList(1, tokens.size)
                if (items != null) {
                    sendGet(items)
                    reactions.say("Записал:")
                    items.forEach {
                        reactions.say(" $it")
                    }
                }
            }
        }

        fallback {
            reactions.say("Скажите \"Запиши\" и продиктуйте список")
        }
    }
}

fun sendGet(tokens: List<String>) {
    tokens.forEach {
        khttp.post(
            url = "https://dadle8.tk:8443/manageListItem?name=test&completed=true",
            json = mapOf("description" to "From Alice", "id" to "_" + getRandomString(9), "title" to it)
        )
    }
}

fun getRandomString(length: Int): String {
    val allowedChars = ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}
