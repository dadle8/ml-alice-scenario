package tk.dadle8

import com.justai.jaicf.channel.http.asHttpBotRequest
import java.util.function.Function

/**
 * Укажите путь к этому классу при загрузке навык в Яндекс Облако
 */
class ManageListsFunction: Function<String, String> {
    override fun apply(input: String): String {
        return channel.process(input.asHttpBotRequest())?.output?.toString() ?: ""
    }
}
