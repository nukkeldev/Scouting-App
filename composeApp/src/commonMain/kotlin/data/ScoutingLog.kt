package data

import androidx.compose.compiler.plugins.kotlin.write
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.File

typealias TeamNumber = Int

enum class RobotPosition {
    R1, R2, R3,
    B1, B2, B3
}

data class ScoutingLog(
    val match: Int,
    val position: RobotPosition,
    val team: Int? = null,

    val autos: String = "",
    val teleNotes: String = "",

    val autoSpeakerNum: Int = 0,
    val autoAmpNum: Int = 0,
    val collected: Int = 0,
    val autoSMissed: Int = 0,
    val autoAMissed: Int = 0,
    val teleSpeakerNum: Int = 0,
    val teleAmpNum: Int = 0,
    val telePassed: Int = 0,
    val teleTrapNum: Int = 0,
    val teleSMissed: Int = 0,
    val teleAMissed: Int = 0,
    val teleSReceived: Int = 0,
    val teleAReceived: Int = 0,
    val lostComms: Boolean = false,
    val autoStop: Boolean = false,
) {
    override fun toString(): String {
        return listOf(
            match,
            team ?: "",
            position,
            autos,
            teleNotes,
            autoSpeakerNum,
            autoAmpNum,
            collected,
            autoSMissed,
            autoAMissed,
            teleSpeakerNum,
            teleAmpNum,
            telePassed,
            teleTrapNum,
            teleSMissed,
            teleAMissed,
            teleSReceived,
            teleAReceived,
            lostComms,
            autoStop,
        ).joinToString("\n") { s -> s.toString().replace("\n", "") }
    }

    companion object {
        fun deserialize(data: String): ScoutingLog? {
            val values = data.lines()
            if (values.size != 20) return null

            return ScoutingLog(
                match = values[0].toIntOrNull() ?: return null,
                team = values[1].toIntOrNull(),
                position = RobotPosition.valueOf(values[2]) as? RobotPosition
                    ?: return null,
                autos = values[3],
                teleNotes = values[4],
                autoSpeakerNum = values[5].toIntOrNull() ?: return null,
                autoAmpNum = values[6].toIntOrNull() ?: return null,
                collected = values[7].toIntOrNull() ?: return null,
                autoSMissed = values[8].toIntOrNull() ?: return null,
                autoAMissed = values[9].toIntOrNull() ?: return null,
                teleSpeakerNum = values[10].toIntOrNull() ?: return null,
                teleAmpNum = values[11].toIntOrNull() ?: return null,
                telePassed = values[12].toIntOrNull() ?: return null,
                teleTrapNum = values[13].toIntOrNull() ?: return null,
                teleSMissed = values[14].toIntOrNull() ?: return null,
                teleAMissed = values[15].toIntOrNull() ?: return null,
                teleSReceived = values[16].toIntOrNull() ?: return null,
                teleAReceived = values[17].toIntOrNull() ?: return null,
                lostComms = values[18].toBooleanStrictOrNull() ?: return null,
                autoStop = values[19].toBooleanStrictOrNull() ?: return null,
            )
        }
    }
}

data class ScoutingLogs(
    val file: File,
    val currentLog: MutableState<ScoutingLog?> = mutableStateOf(null),
    // [match][position.ordinal]
    val logs: SnapshotStateMap<Int, Array<ScoutingLog?>> = mutableStateMapOf()
) {
    init {
        if (file.createNewFile()) {
            file.write {
                write(toJson().toString())
            }
        }
    }

    private fun ensureMatchExists(match: Int) {
        logs.putIfAbsent(match, arrayOfNulls(6))
    }

    fun setCurrentLog(match: Int, position: RobotPosition) {
        // Save the current log if present
        currentLog.value?.let {
            ensureMatchExists(it.match)
            logs[it.match]?.set(it.position.ordinal, currentLog.value)
        }

        ensureMatchExists(match)
        currentLog.value = logs[match]?.get(position.ordinal) ?: ScoutingLog(match, position)
    }

    fun saveCurrentLog() {
        val log = currentLog.value ?: return

        ensureMatchExists(log.match)
        logs[log.match]?.set(log.position.ordinal, log)
    }

    fun toJson(): JsonObject {
        saveCurrentLog()
        return JsonObject(logs.map { (match, positions) ->
            match.toString() to JsonArray(positions.map { JsonPrimitive(it.toString()) })
        }.associate { it })
    }

    fun exportToFile(filesDir: File) {
        file.overwriteString(
            toJson().toString()
        )
    }

    fun deleteFile(filesDir: File) {
        file.delete()
    }

    companion object {
        const val FILE_NAME = "match_scouting_data.json"
    }
}

fun File.overwriteString(data: String?) {
    if (delete()) createNewFile()

    write {
        data?.let { data -> write(data) }
    }
}