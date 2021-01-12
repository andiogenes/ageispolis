package ui.components

import org.jetbrains.skija.*
import ui.display.DisplayObject

/**
 * Компонент порта узла потока данных.
 */
class Port : DisplayObject() {
    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.translate(absoluteX.toFloat(), absoluteY.toFloat())
        canvas.drawCircle(0f, 0f, radius, paint)
        canvas.drawCircle(0f, 0f, radius, strokePaint)
        canvas.restore()
    }

    companion object {
        // Внутренние объекты Skija для отрисовки компонента.
        private val paint = Paint().setColor(0xFFDBE11E.toInt()).setMode(PaintMode.FILL)
        private val strokePaint = Paint().setColor(0xFF000000.toInt()).setMode(PaintMode.STROKE).setStrokeWidth(1.25f)

        /**
         * Радиус порта.
         */
        const val radius = 7.5f

        /**
         * Диаметр порта.
         */
        const val diameter = radius * 2
    }
}