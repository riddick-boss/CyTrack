package abandonedstudio.app.cytrack.utils

object ConvertersUI {

    fun formatAvgSpeed(speed: Float): String{
        return "${String.format("%.1f", speed)} km/h"
    }

    fun formatTimeFromMinutes(time: Int): String{
        return String.format("${time / 60}:${String.format("%02d", time % 60)}")
    }

    fun formatDistance(distance: Float): String{
        return "${String.format("%.3f", distance)} km"
    }
}