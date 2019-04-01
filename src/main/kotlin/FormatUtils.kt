import kotlin.js.Date

class FormatUtils {
    fun formatTimeStamp(timestamp: String?): String = if (timestamp == null || timestamp == "0")
        "N/A"
    else {
        Date(timestamp.toLong() * 1000L).toLocaleString("en-us")
    }

    fun formatMinutes(totalMinutes: Int): String {
        val days = totalMinutes / 24 / 60
        val remainingMinutes = totalMinutes - days * 24 * 60
        val hours = remainingMinutes / 60
        val minutes = (remainingMinutes - hours * 60)
        return "${fmtTimeUnit(days, "day", ", ")}${fmtTimeUnit(hours, "hour", " and ")}${fmtTimeUnit(
            minutes,
            "minute"
        )}"
    }

    private fun fmtTimeUnit(value: Int, unit: String, suffix: String = "") = when (value) {
        0 -> ""
        1 -> "$value $unit$suffix"
        else -> "$value ${unit}s$suffix"
    }

    fun shortenText(string: String) = if (string.length <= 70) {
        string
    } else {
        string.substring(0, 34) + "..." + string.substring(string.length - 33, string.length)
    }
}