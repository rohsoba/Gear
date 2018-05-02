package work.yajima.gear

import android.graphics.*

class Gear(
        var position: PointF,
        val size: Float,
        private val numOfTeeth: Long,
        private val thick: Float,
        var angle: Float
) {
    private val harmonicList = mutableListOf<Gear>()
    fun addHarmonic(gear: Gear) {
        harmonicList.takeIf { !it.contains(gear) }?.add(gear)
    }
    fun removeHarmonic(gear: Gear) {
        harmonicList.removeIf { harmonicList.contains(it) }
    }

    private val path = Path().apply {
        val fullLength = Math.PI * 2 / numOfTeeth
        val halfLength = fullLength / 2
        val harmonicWear = halfLength / 8
        val radiuses = arrayOf(size, size + thick)
        moveTo(0f, radiuses[0])
        (0 until numOfTeeth).forEach {
            val angles = arrayOf(
                    fullLength * it + halfLength - harmonicWear,
                    fullLength * it + halfLength + harmonicWear,
                    fullLength * it + fullLength - harmonicWear,
                    fullLength * it + fullLength + harmonicWear)
            lineTo(sin(angles[0]) * radiuses[0], cos(angles[0]) * radiuses[0])
            lineTo(sin(angles[1]) * radiuses[1], cos(angles[1]) * radiuses[1])
            lineTo(sin(angles[2]) * radiuses[1], cos(angles[2]) * radiuses[1])
            lineTo(sin(angles[3]) * radiuses[0], cos(angles[3]) * radiuses[0])
        }
        if (thick < 0) {
            addCircle(0f, 0f, size - thick, Path.Direction.CW)
        }
        close()
    }

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = Color.WHITE
    }

    fun rotate(speed: Float) {
        val pm = -thick / Math.abs(thick)
        angle += speed / size * pm
        harmonicList.forEach {
            it.rotate(-speed)
        }

    }

    fun draw(canvas: Canvas) {
        canvas.save()
        canvas.translate(position.x, position.y)
        canvas.rotate(angle)
        canvas.drawPath(path, paint)
        canvas.restore()
    }

    private fun sin(degree: Double): Float {
        return Math.sin(degree).toFloat()
    }

    private fun cos(degree: Double): Float {
        return Math.cos(degree).toFloat()
    }
}