package work.yajima.gear

import android.graphics.PointF

class FloatingController(val min: Float, val max: Float) {

    var capturing = false
    val origin: PointF = PointF()
    val beyond: PointF = PointF()
    val shift: PointF = PointF()

    fun capture(x: Float, y: Float) {
        origin.set(x, y)
        beyond.set(x, y)
        shift.set(0f, 0f)
        capturing = true
    }

    fun tilt(x: Float, y: Float) {
        shift.set(x - origin.x, y - origin.y)
        val volume = shift.length()
        when (volume) {
            minOf(volume, min) -> shift.set(0f, 0f)
            maxOf(volume, max) -> shift *= max / volume
        }
        beyond.set(origin.x + shift.x, origin.y + shift.y)
    }

    fun release() {
        shift.set(0f, 0f)
        if (capturing) {
            capturing = false
        }
    }

    fun degree(): Float {
        return (Math.atan2(shift.y.toDouble(), shift.x.toDouble()) / Math.PI * 180).toFloat()
    }

    private operator fun PointF.timesAssign(f: Float) {
        set(x * f, y * f)
    }
}
