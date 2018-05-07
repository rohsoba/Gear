package work.yajima.gear

import android.graphics.*

class Gear(
        var position: PointF,
        val size: Float,
        private val numOfTeeth: Int,
        val thick: Float,
        var angle: Float
) {
    private val circularPitch = Math.PI * 2 / numOfTeeth
    private val circularThickness = circularPitch / 2
    private val harmonicWear = circularThickness / 8
    private val inOutRadius = arrayOf(size, size + thick)
    private val angles: Array<Array<Double>> = Array(numOfTeeth, init = {
        arrayOf(circularPitch * it + circularThickness - harmonicWear,
                circularPitch * it + circularThickness + harmonicWear,
                circularPitch * it + circularPitch - harmonicWear,
                circularPitch * it + circularPitch + harmonicWear)
    })

    private val harmonicList = mutableListOf<Gear>()
    fun addHarmonic(gear: Gear) {
        harmonicList.takeIf { !it.contains(gear) }?.add(gear)
    }
    fun removeHarmonic(gear: Gear) {
        harmonicList.removeIf { harmonicList.contains(it) }
    }

    private val path = Path().apply {

        moveTo(0f, inOutRadius[0])
        (0 until numOfTeeth).forEach {
            val current = angles[it]
            lineTo(sin(current[0]) * inOutRadius[0], cos(current[0]) * inOutRadius[0])
            lineTo(sin(current[1]) * inOutRadius[1], cos(current[1]) * inOutRadius[1])
            lineTo(sin(current[2]) * inOutRadius[1], cos(current[2]) * inOutRadius[1])
            lineTo(sin(current[3]) * inOutRadius[0], cos(current[3]) * inOutRadius[0])
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

    fun isNear(gear: Gear): Boolean {
        val x = position.x - gear.position.x
        val y = position.y - gear.position.y
        return Math.sqrt((x*x + y*y).toDouble()) < thick
    }
}