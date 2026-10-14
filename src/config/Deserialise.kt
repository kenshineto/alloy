package cat.freya.alloy.config

import cat.freya.alloy.annotation.*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

private fun merge(target: ObjectNode, source: ObjectNode): ObjectNode {
    source.properties().forEach { (key, value) ->
        val existing = target.get(key)
        if (existing is ObjectNode && value is ObjectNode) {
            merge(existing, value)
        } else {
            target.set<JsonNode>(key, value)
        }
    }
    return target
}

fun <T : Any> deserialize(type: KClass<T>, ins: InputStream?, defaults: T? = null): T {
    val reader = ins?.let { InputStreamReader(it) }
    return deserialize(type, reader, defaults)
}

fun <T : Any> deserialize(type: KClass<T>, ins: Reader?, possibleDefaults: T? = null): T {
    val defaults = possibleDefaults ?: type.createInstance()
    if (ins == null) return defaults

    val defaultsNode = mapper.valueToTree<ObjectNode>(defaults)
    val loadedNode = mapper.readTree(ins)
    merge(defaultsNode, loadedNode as ObjectNode)

    return mapper.treeToValue(defaultsNode, type.java)
}
