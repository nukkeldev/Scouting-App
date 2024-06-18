import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import data.RobotPosition
import kotlinx.serialization.json.JsonObject
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


var compKey = "2024week0"

var matchData: JsonObject? = null
var teamData: JsonObject? = null

const val url = "https://www.thebluealliance.com/api/v3"
private val client = OkHttpClient()

fun run(url: String, headers: Headers): String {
    val request = Request.Builder().url(url).headers(headers).build()

    client.newCall(request).execute().use {
        return it.body?.string() ?: ""
    }
}

expect fun setTeam(
    matchNumber: Int?,
    position: RobotPosition?,
    setTeamNumber: (Int) -> Unit
)

@RequiresApi(Build.VERSION_CODES.O)
var lastSynced = mutableStateOf(Instant.now())

@RequiresApi(Build.VERSION_CODES.O)
fun getLastSynced(): String {
    val formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
        .withZone(ZoneId.systemDefault())

    return formatter.format(lastSynced.value)
}

private const val PATTERN_FORMAT = "dd/MM/yyyy @ hh:mm"


const val apiKeyEncoded =
    "eEtWS2RkemlRaTlJWkJhYXMxU0M0cUdlVkVTMXdaams3VDhUckZ1amFSODFmQlFIUXgybTdzTGJoZ0lnQVNPRw=="