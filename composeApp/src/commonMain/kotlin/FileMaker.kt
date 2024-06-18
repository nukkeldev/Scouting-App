import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun getJsonFromMatchHash(scoutingLogs: Map<Int, MutableMap<Int, String>>): JsonObject =
    JsonObject(scoutingLogs.map { (k, v) ->
        k.toString() to JsonObject(v.map { (k, v) ->
            k.toString() to JsonPrimitive(v)
        }.associate { it })
    }.associate { it })
