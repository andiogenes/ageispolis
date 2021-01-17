package app.ui.components

import org.jetbrains.skija.*
import org.lwjgl.glfw.GLFW
import app.ui.display.DisplayObject
import events.Event
import app.ui.utils.pointInBox
import kotlin.math.abs
import kotlin.properties.Delegates.observable

/**
 * Компонент "Ползунок".
 *
 * Значение ползунка - нормированная величина в пределах от [0, 1],
 * задаваемая числом с плавающей точкой одинарной точности.
 *
 * Компонент позволяет подписаться на событие типа [ValueChangedEvent] по ключу [valueChangedEventType].
 *
 * @param title Отображаемый заголовок компонента
 * @param width Ширина компонента
 * @param height Высота компонента
 * @param value Стандартное значение компонента
 */
class Slider(
    title: String,
    private val width: Int,
    private val height: Int,
    value: Float = 0.5f
) : DisplayObject() {
    /**
     * Событие изменения значения ползунка.
     * @param oldValue Предыдущее значение ползунка
     * @param newValue Текущее значение ползунка
     */
    data class ValueChangedEvent(val oldValue: Float, val newValue: Float) : Event(valueChangedEventType)

    private var value: Float by observable(value) { _, oldValue, newValue ->
        dispatchEvent(ValueChangedEvent(oldValue, newValue))
    }

    /**
     * Действительный отображаемый текст
     */
    private val caption = "$title:"

    /**
     * Меняется ли значение ползунка в текущий момент времени.
     */
    private var isChanging = false

    override fun onMouseButton(x: Int, y: Int, button: Int, action: Int, mods: Int): Boolean {
        val left = absoluteX
        if (pointInBox(x, y, left, absoluteY, width + 1, height)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) return true

            isChanging = action == GLFW.GLFW_PRESS
            // Препятствует изменению значения при отжатии левой кнопки мыши
            if (!isChanging) return true

            value = (x - left) / width.toFloat()
            return true
        }

        return false
    }

    override fun onMouseMove(x: Int, y: Int): Boolean {
        val left = absoluteX
        if (isChanging) {
            if (pointInBox(x, y, left, absoluteY, width + 1, height)) {
                value = (x - left) / width.toFloat()
            } else {
                isChanging = false
            }
            return true
        }

        return false
    }

    override fun draw(canvas: Canvas) {
        val rRect = RRect.makeXYWH(0f, 0f, width.toFloat(), height.toFloat(), 15f)
        val textY = (fontSize + height) / 2

        canvas.save()
        canvas.translate(absoluteX.toFloat(), absoluteY.toFloat())
        canvas.drawRRect(rRect, fillPaint)
        canvas.drawRRect(rRect, strokePaint)
        val dashOffsetX = width * value
        if (dashOffsetX > contentMargin && dashOffsetX < width - contentMargin) {
            canvas.drawLine(dashOffsetX, 0f, dashOffsetX, height.toFloat(), strokePaint)
        } else {
            val distance = minOf(dashOffsetX, abs(width - dashOffsetX))
            val actualHeight = height * distance / contentMargin
            canvas.drawLine(dashOffsetX, (height - actualHeight) / 2, dashOffsetX, (height + actualHeight) / 2, strokePaint)
        }
        canvas.drawString(caption, contentMargin, textY, font, textPaint)
        canvas.drawString("%.2g".format(value), width - valueWidth - contentMargin, textY, font, textPaint)
        canvas.restore()
    }

    companion object {
        // Внутренние объекты Skija для отрисовки компонента.
        private val fillPaint = Paint().setColor(0x66969696).setMode(PaintMode.FILL)
        private val strokePaint = Paint().setColor(0xFF000000.toInt()).setMode(PaintMode.STROKE).setStrokeWidth(0.8f)

        /**
         * Размер шрифта
         */
        private const val fontSize = 14f
        private val typeface = Typeface.makeDefault()
        private val font = Font(typeface, fontSize)
        private val textPaint = Paint().setColor(0xFF000000.toInt())

        /**
         * Ширина текстового значения величины.
         */
        private val valueWidth = TextLine.make("1.00", font).width

        /**
         * Отступ содержимого ползунка по краям.
         */
        private const val contentMargin = 7.5f

        /**
         * Ключ для подписки на события типа [ValueChangedEvent].
         */
        const val valueChangedEventType = "onSliderValueChanged"
    }
}