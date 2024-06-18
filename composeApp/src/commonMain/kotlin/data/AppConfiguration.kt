package data

import Competition

data class AppConfiguration(
    var scout: Pair<String, String>? = null,
    val competition: Competition? = null
) {
    fun scoutName(): String = scout?.let { (f, l) -> "$f $l" } ?: "Unknown Scouter"
}
