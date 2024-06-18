import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Headers
import java.io.File
import java.lang.Integer.parseInt
import java.time.Instant
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun syncTeamsAndMatches(refresh: Boolean, filesDir: File) {
    if (!refresh) {
        if ((teamData != null && matchData != null) || loadMatchAndTeamFiles(filesDir)) lastSynced.value =
            Instant.now()
        return
    }

    val apiKey = String(Base64.getDecoder().decode(apiKeyEncoded))
    CoroutineScope(Dispatchers.Default).launch {
        val teams = run(
            "$url/event/$compKey/teams/simple",
            Headers.headersOf(
                "X-TBA-Auth-Key",
                apiKey
            )
        )
        teamData = Json.decodeFromString("{\"teams\":$teams}") as? JsonObject

        val matches = run(
            "$url/event/$compKey/matches",
            Headers.headersOf(
                "X-TBA-Auth-Key",
                apiKey
            )
        )
        matchData =
            Json.decodeFromString("{\"matches\":$matches}") as? JsonObject

        if (teamData != null && matchData != null)
            lastSynced.value = Instant.now()
    }
}

actual fun setTeam(
    match: Int?,
    robotStartPosition: Int?,
    setTeamNumber: (Int) -> Unit
) {
    val jsonObject = matchData ?: return

    jsonObject["matches"]?.jsonArray?.forEach {
        it as JsonObject
        if (it["comp_level"]?.jsonPrimitive?.content == "qm") {
            if ((it["match_number"]?.jsonPrimitive?.intOrNull) != match)
                return@forEach
        } else {
            return@forEach
        }

        val redAlliance =
            it["alliances"]?.jsonObject?.get("red")?.jsonObject?.get("team_keys")?.jsonArray
                ?: return@forEach
        val blueAlliance =
            it["alliances"]?.jsonObject?.get("blue")?.jsonObject?.get("team_keys")?.jsonArray
                ?: return@forEach
        val teamKey = when (robotStartPosition) {
            0 -> redAlliance[0].jsonPrimitive.content
            1 -> redAlliance[1].jsonPrimitive.content
            2 -> redAlliance[2].jsonPrimitive.content
            3 -> blueAlliance[0].jsonPrimitive.content
            4 -> blueAlliance[1].jsonPrimitive.content
            5 -> blueAlliance[2].jsonPrimitive.content
            else -> ""
        }
        setTeamNumber(parseInt(teamKey.slice(3..<teamKey.length)))
    }
}