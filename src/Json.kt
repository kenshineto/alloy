package cat.freya.alloy

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URI

inline fun <reified T : Any> fetchJson(url: String, userAgent: String): Result<T> {
    return runCatching {
            val connection = URI(url).toURL().openConnection()
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", userAgent)

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val mapper = jacksonObjectMapper()
            mapper.readValue(response, T::class.java)
        }
}
