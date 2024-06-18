data class ScoutingLog(
    val match: Int? = null,
    val team: Int? = null,
    val robotStartPosition: Int? = null,

    val autos: String? = null,
    val teleNotes: String? = null,

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
            match ?: "",
            team ?: "",
            robotStartPosition ?: "",
            autos ?: "",
            teleNotes ?: "",
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
                match = values[0].toIntOrNull(),
                team = values[1].toIntOrNull(),
                robotStartPosition = values[2].toIntOrNull(),
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