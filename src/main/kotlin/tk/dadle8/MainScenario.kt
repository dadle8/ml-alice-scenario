package tk.dadle8

import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.channel.yandexalice.model.AliceEvent
import com.justai.jaicf.model.scenario.Scenario
import org.json.JSONObject

object MainScenario : Scenario() {
    init {
        state("main") {
            activators {
                event(AliceEvent.START)
            }

            action {
                reactions.say("Привет! Скажи мне что-нибудь, а я запишу.")
            }
        }

        state("write") {
            activators {
                regex("(запиши|записать|добавить) .+")
            }

            action {
                val tokens = request.alice?.request?.nlu?.tokens
                val items = tokens?.subList(1, tokens.size)?.filter { it != "и" }
                if (items != null) {
                    sendGet(items)
                    reactions.say("Записал:")
                    items.forEach {
                        reactions.say(" $it")
                    }
                }
            }
        }

        state("get_list") {
            activators {
                regex("(показать список|покажи)")
            }

            action {
                reactions.say("Записано:")
                val items = getItems()
                items.forEach { reactions.say(" $it") }
            }
        }

        fallback {
            reactions.say("Скажи \"Запиши\" и продиктуй список.")
        }
    }
}

fun sendGet(tokens: List<String>) {
    tokens.forEach {
        khttp.post(
            url = "https://dadle8.tk:8443/manageListItem?name=Покупочки&completed=true",
            json = mapOf("description" to "From Alice", "id" to "_" + getRandomString(9), "title" to it)
        )
    }
}

fun getItems(): List<String> {
    val response = khttp.get(url = "https://dadle8.tk:8443/manageList?name=Покупочки")
    val listJson = response.jsonObject
    val todoItemsJson = listJson.getJSONArray("todoItems")

    val todoItems = mutableListOf<String>()
    for (rawTodoItem in todoItemsJson) {
        val todoItemJson = rawTodoItem as JSONObject
        todoItems.add(todoItemJson.getString("title"))
    }

    return todoItems
}

fun getRandomString(length: Int): String {
    val allowedChars = ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}
