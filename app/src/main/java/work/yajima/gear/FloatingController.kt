package work.yajima.gear

import android.graphics.PointF

class FloatingController(val min: Float, val max: Float) {

    var capturing = false
    val origin: PointF = PointF()
    val shift: PointF = PointF()

    fun capture(x: Float, y: Float) {
        origin.set(x, y)
        shift.set(0f, 0f)
        capturing = true
    }

    fun tilt(x: Float, y: Float) {
        val vx = origin.x - x
        val vy = origin.y - y
        val volume = Math.sqrt((vx*vx + vy*vy).toDouble()).toFloat()
        when (volume) {
            minOf(volume, min) -> shift.set(0f, 0f)
            maxOf(volume, max) -> shift.set(vx / volume * max, vy / volume * max)
            else -> shift.set(vx, vy)
        }
    }

    fun release() {
        if (capturing) {
            capturing = false
            shift.set(0f, 0f)
        }
    }

    fun degree(): Float {
        return (Math.atan2(shift.y.toDouble(), shift.x.toDouble()) / Math.PI * 180).toFloat()
    }

    fun volume(): Float {
        return Math.sqrt((shift.x*shift.x + shift.y*shift.y).toDouble()).toFloat()
    }
}