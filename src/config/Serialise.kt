package cat.freya.alloy.config

import cat.freya.alloy.annotation.*
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.text.buildString

private fun isValueInline(value: Any?): Boolean {
    if (value == null) return true
    if (value is List<*>) return value.all { isValueInline(it) }
    if (value is Map<*, *>) return value.isEmpty()
    if (value is Boolean) return true
    if (value::class.isData) return false

    return true
}

private fun serializeSection(section: Section): String {
    val width = 100
    val prefixWidth = 3
    val headerWidth = section.text.length
    val slugWidth = width - prefixWidth - headerWidth

    return buildString {
        appendLine() // spacing

        // top line
        append("#")
        append(" ".repeat(prefixWidth))
        append("┌")
        append("─".repeat(headerWidth + 2))
        appendLine("┐")

        // bottom line
        append("#")
        append("─".repeat(prefixWidth))
        append("┘ ${section.text} └")
        appendLine("─".repeat(slugWidth))

        appendLine() // spacing
    }
}

private fun serializeComment(comment: Comment): String {
    return buildString {
        for (line in comment.text.lines()) {
            appendLine("# $line")
        }
    }
}

private fun serializeDeprecated(deprecated: Deprecated): String {
    return "# Warning: This field has been deprecated: ${deprecated.message}\n"
}

private fun <T : Any> serializeClass(instance: T): String {
    val type = instance::class
    require(type.isData) { "$type is not a data class" }

    val propValues =
        type.primaryConstructor!!
            .parameters
            .mapNotNull { param -> type.memberProperties.find { it.name == param.name } }
            .associateWith { prop -> prop.getter.call(instance) }

    return buildString {
        for ((prop, value) in propValues) {
            if (value == null && prop.annotations.contains(Omittable())) continue

            val lines = serialize(value).trim().lines().filter { it.isNotEmpty() }

            // append comments
            for (annotation in prop.annotations) {
                when (annotation) {
                    is Section -> append(serializeSection(annotation))
                    is Comment -> append(serializeComment(annotation))
                    is Deprecated -> append(serializeDeprecated(annotation))
                }
            }

            // no content, then skip
            if (lines.isEmpty()) continue

            // no indentation if only a single item
            if (lines.size == 1 && isValueInline(value)) {
                appendLine("${prop.name}: ${lines[0]}")
                continue
            }

            appendLine("${prop.name}:")
            for (line in lines) {
                appendLine("  $line")
            }
        }
    }
}

private fun <T> serializeList(list: List<T>): String {
    if (list.isEmpty()) return "[]"

    if (list.size == 1 && isValueInline(list)) {
        val text = serialize(list[0])
        return "[$text]"
    }

    return buildString {
        for (value in list) {
            val lines = serialize(value).trim().lines().filter { it.isNotEmpty() }
            for ((i, line) in lines.withIndex()) {
                append(if (i == 0) "- " else "  ")
                appendLine(line)
            }
        }
    }
}

private fun <K, V> serializeMap(map: Map<K, V>): String {
    if (map.isEmpty()) return "{}"

    return buildString {
        for ((key, value) in map) {
            if (key !is String) error("Map values must be strings")
            val keyString = key.toString()
            val lines = serialize(value).trim().lines().filter { it.isNotEmpty() }

            if (lines.isEmpty()) continue

            if (lines.size == 1 && isValueInline(value)) {
                appendLine("$keyString: ${lines[0]}")
                continue
            }

            appendLine("$keyString:")
            for (line in lines) {
                append("  ")
                appendLine(line)
            }
        }
    }
}

private fun <T : Any> serializePrimitive(value: T): String {
    val normalized: Any =
        when (value) {
            is LocaleString1 -> value.inner
            is LocaleString2 -> value.inner
            is LocaleString3 -> value.inner
            is UInt -> value.toInt()
            is ULong -> value.toLong()
            else -> value
        }

    return mapper.writeValueAsString(normalized).removePrefix("---").trim()
}

fun <T : Any> serialize(value: T?): String {
    if (value == null) return "null"

    val type = value::class
    return when {
        type.isData -> serializeClass(value)
        type.java.isEnum -> value.toString()
        type.isSubclassOf(List::class) -> serializeList(value as List<*>)
        type.isSubclassOf(Map::class) -> serializeMap(value as Map<*, *>)
        else -> serializePrimitive(value)
    }
}
