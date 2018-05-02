package work.yajima.gear

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class GearView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    private val ctrl = FloatingController(0f, 100f)

    private val gears = mutableListOf<Gear>().apply {
        add(Gear(ctrl.beyond, 100f, 10, 20f, 0f))
        add(Gear(ctrl.origin, 220f, 20, -20f, 0f))
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        if (ctrl.capturing) {
            val root = gears[0]
            gears.forEach {
                it.draw(canvas)
            }
            if (ctrl.shift.length() > ctrl.max * 0.9) {
                root.addHarmonic(gears[1])
            } else {
                root.removeHarmonic(gears[1])
            }
            root.rotate(100f)
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
