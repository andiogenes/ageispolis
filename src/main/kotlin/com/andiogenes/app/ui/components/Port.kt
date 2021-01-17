package com.andiogenes.app.ui.components

import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Paint
import org.jetbrains.skija.PaintMode
import org.lwjgl.glfw.GLFW
import com.andiogenes.app.ui.display.DisplayObject
import com.andiogenes.events.Event
import com.andiogenes.app.ui.utils.pointInCircle

/**
 * Компонент порта узла потока данных.
 */
class Port : DisplayObject() {
    /**
     * Событие, связанное с портом.
     *
     * @param port Порт, вызвавший событие
     * @param reason Причина вызова события
     */
    data class PortEvent(val port: Port, val reason: Reason) : Event(portEventType) {
        enum class Reason {
            MOUSE_PRESSED,
            MOUSE_RELEASED,
            RIGHT_CLICK,
            LINK_CREATED,
            LINK_REMOVED
        }
    }

    /**
     * Информирует о том, что с этим портом установлена связь.
     */
    fun informAboutLinkCreation() = dispatchEvent(PortEvent(this, PortEvent.Reason.LINK_CREATED))

    /**
     * Информирует о том, что с этим портом разорвана связь.
     */
    fun informAboutLinkDeletion() = dispatchEvent(PortEvent(this, PortEvent.Reason.LINK_REMOVED))

    override fun onMouseButton(x: Int, y: Int, button: Int, action: Int, mods: Int): Boolean {
        if (pointInCircle(x, y, absoluteX, absoluteY, radius)) {
            when (button) {
                GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                    if (action == GLFW.GLFW_PRESS) {
                        dispatchEvent(PortEvent(this, PortEvent.Reason.MOUSE_PRESSED))
                    } else if (action == GLFW.GLFW_RELEASE) {
                        dispatchEvent(PortEvent(this, PortEvent.Reason.MOUSE_RELEASED))
                    }
                }
                GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                    if (action == GLFW.GLFW_PRESS) {
                        dispatchEvent(PortEvent(this, PortEvent.Reason.RIGHT_CLICK))
                    }
                }
            }
            return true
        }
        return false
    }

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

        /**
         * Ключ для подписки на события типа [PortEvent].
         */
        const val portEventType = "onPort"
    }
}