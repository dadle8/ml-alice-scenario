package tk.dadle8

import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.channel.yandexalice.model.AliceEvent
import com.justai.jaicf.model.scenario.Scenario
import org.json.JSONObject

val URL_API_ML: String = System.getenv("URL_API_ML")
val SCENARIO_USER_ML: String = System.getenv("SCENARIO_USER_ML")
val SCENARIO_PASSWORD_ML: String = System.getenv("SCENARIO_PASSWORD_ML")

const val WRITE_STATE_REGEX: String = "(запиши|записать|добавить) .+"
const val GET_LIST_STATE_REGEX: String = "(показать список|покажи)"
var TOKEN: String = ""

object MainScenario : Scenario() {
    init {
        if (TOKEN.isEmpty() || checkAuth() == 401) {
            auth()
        }

        state("main") {
            activators {
                event(AliceEvent.START)
            }

            action {
                reactions.say(Messages.WELCOME)
            }
        }

        state("write") {
            activators {
                regex(WRITE_STATE_REGEX)
            }

            action {
                val tokens = request.alice?.request?.nlu?.tokens
                val items = tokens?.subList(1, tokens.size)?.filter { it != "и" }
                if (items != null) {
                    sendGet(items)
                    reactions.say(Messages.WROTE)
                    items.forEach {
                        reactions.say(" $it")
                    }
                }
            }
        }

        state("get_list") {
            activators {
                regex(GET_LIST_STATE_REGEX)
            }

            action {
                reactions.say(Messages.WRITTEN)
                val items = getItems()
                items.forEach { reactions.say(" $it") }
            }
        }

        fallback {
            reactions.say(Messages.FALLBACK)
        }
    }
}

fun sendGet(tokens: List<String>) {
    tokens.forEach {
        khttp.post(
            url = "$URL_API_ML/api/ml/manageListItem",
            json = mapOf("description" to "From Alice", "id" to "_" + getRandomString(9), "title" to it),
            params = mapOf("name" to "Покупочки", "completed" to "true")
        )
    }
}

fun getItems(): List<String> {
    val response = khttp.get(
        url = "$URL_API_ML/api/ml/manageLists",
        params = mapOf("name" to "Покупочки"),
        headers = mapOf("Authorization" to "Bearer $TOKEN")
    )
    val listJson = response.jsonArray
    val jsonObject = listJson.getJSONObject(0)
    val todoItemsJson = jsonObject.getJSONArray("todoItems")

    val todoItems = mutableListOf<String>()
    for (rawTodoItem in todoItemsJson) {
        val todoItemJson = rawTodoItem as JSONObject
        todoItems.add(todoItemJson.getString("title"))
    }

    return todoItems
}

fun auth() {
    val response = khttp.post(
        url = "$URL_API_ML/authenticate",
        json = mapOf("username" to SCENARIO_USER_ML, "password" to SCENARIO_PASSWORD_ML)
    )
    val jsonResponse = response.jsonObject
    TOKEN = jsonResponse.getString("token")
}

fun checkAuth(): Int {
    val response = khttp.get(
        url = "$URL_API_ML/api/ml/manageLists",
        params = mapOf("name" to "Покупочки"),
        headers = mapOf("Authorization" to "Bearer $TOKEN")
    )

    return response.statusCode;
}

fun getRandomString(length: Int): String {
    val allowedChars = ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}
