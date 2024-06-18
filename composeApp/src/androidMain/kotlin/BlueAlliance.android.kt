import android.os.Build
import androidx.annotation.RequiresApi
import data.RobotPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
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
fun syncTeamsAndMatches(refresh: Boolean) {
    if (!refresh) {
        if ((teamData != null && matchData != null) || loadMatchAndTeamFiles()) lastSynced.value =
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
    matchNumber: Int?,
    position: RobotPosition?,
    setTeamNumber: (Int) -> Unit
) {
    val jsonObject = matchData ?: return

    jsonObject["matches"]?.jsonArray?.forEach { match ->
        match as JsonObject
        if (match["comp_level"]?.jsonPrimitive?.content == "qm") {
            if ((match["match_number"]?.jsonPrimitive?.intOrNull) != matchNumber)
                return@forEach
        } else {
            return@forEach
        }

        val redAlliance =
            match["alliances"]?.jsonObject?.get("red")?.jsonObject?.get("team_keys")?.jsonArray
                ?: return@forEach
        val blueAlliance =
            match["alliances"]?.jsonObject?.get("blue")?.jsonObject?.get("team_keys")?.jsonArray
                ?: return@forEach
        val teamKey = position?.let { (if (it.ordinal < 3) redAlliance[it.ordinal] else blueAlliance[it.ordinal - 3]) as JsonPrimitive }?.content ?: ""
        setTeamNumber(parseInt(teamKey.slice(3..<teamKey.length)))
    }
}