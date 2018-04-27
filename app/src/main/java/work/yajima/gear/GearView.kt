package work.yajima.gear

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class GearView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    private val ctrl = FloatingController(0f, 100f)
    private val ctrlPaint = Paint()
    private val outGearPath = newGear(220f, -20f, 20)
    private val inGearPath  = newGear(100f, 20f, 10)
    private val eppchTime = System.currentTimeMillis()
    private var time = 0L
    private var outDegree = 0f

    init {
        ctrlPaint.style = Paint.Style.STROKE
        ctrlPaint.strokeWidth = 1f
        ctrlPaint.color = Color.WHITE

        outGearPath.addCircle(0f, 0f, 240f, Path.Direction.CW)
    }

    private fun newGear(radius: Float, height: Float, num: Int): Path {
        val sin : (Double) -> Float = { deg -> Math.sin(deg).toFloat() }
        val cos : (Double) -> Float = { deg -> Math.cos(deg).toFloat() }
        return Path().apply {
            val fl = Math.PI * 2 / num
            val hl = fl / 2
            val hw = fl / 16 // harmonic wear
            val r = arrayOf(radius, radius + height)
            moveTo(0f, r[0])
            (0 until num).forEach {
                val a = arrayOf(
                        fl * it + hl - hw,
                        fl * it + hl + hw,
                        fl * it + fl - hw,
                        fl * it + fl + hw)
                lineTo(sin(a[0]) * r[0], cos(a[0]) * r[0])
                lineTo(sin(a[1]) * r[1], cos(a[1]) * r[1])
                lineTo(sin(a[2]) * r[1], cos(a[2]) * r[1])
                lineTo(sin(a[3]) * r[0], cos(a[3]) * r[0])
            }
            close()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        if (ctrl.capturing) {
            time = (System.currentTimeMillis() - eppchTime) / 10
            val gearing = ctrl.volume()/ctrl.max > 0.85

            canvas.save()
            canvas.translate(ctrl.origin.x, ctrl.origin.y)
            canvas.translate(-ctrl.shift.x, -ctrl.shift.y)
            canvas.rotate(time + (if (gearing) ctrl.degree() else 0f))
            canvas.drawPath(inGearPath, ctrlPaint)
            canvas.restore()

            canvas.save()
            canvas.translate(ctrl.origin.x, ctrl.origin.y)
            canvas.rotate(outDegree.takeUnless { gearing }?: (time/2 + ctrl.degree()))
            canvas.drawPath(outGearPath, ctrlPaint)
            canvas.restore()
        }

        handler.post({ invalidate() })
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> ctrl.capture(event.x, event.y)
            MotionEvent.ACTION_MOVE -> ctrl.tilt(event.x, event.y)
            MotionEvent.ACTION_UP   -> ctrl.release()
        }

        return true
    }
}