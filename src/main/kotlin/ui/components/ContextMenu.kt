package ui.components

import org.jetbrains.skija.*
import org.lwjgl.glfw.GLFW
import ui.display.DisplayObject
import utils.pointInBox

/**
 * Выпадающее меню.
 *
 * @param menuItems Элементы меню.
 */
class ContextMenu(private val menuItems: List<MenuItem>) : DisplayObject() {
    /**
     * Элемент меню.
     *
     * @param text Отображаемый текст
     * @param action Действие, совершаемое при нажатии на элемент
     */
    data class MenuItem(val text: String, val action: (ActionInfo) -> Unit = { _ -> }) {
        /**
         * Данные, необходимые для выполнения действия.
         *
         * @param x Координата x компонента
         * @param y Координата y компонента
         */
        data class ActionInfo(val x: Int, val y: Int)
    }

    /**
     * Строки элементов меню (внутреннее представление Skija)
     */
    private val menuLines = menuItems.map { TextLine.make(it.text, font) }

    /**
     * Ширина компонента.
     */
    private val width = menuLines.map { it.width }.maxOf { it } * horizontalPaddingScale

    /**
     * Высота компонента.
     */
    private val height = menuLines.size * itemHeight

    /**
     * Определяет, скрыт объект или нет.
     *
     * Если объект скрыт, все события с ним опускаются.
     */
    var hidden: Boolean = false

    /**
     * Захваченная координата x мыши.
     */
    private var mouseX = 0

    /**
     * Захваченная координата y мыши.
     */
    private var mouseY = 0

    /**
     * Перекрывает ли курсор элемент.
     */
    private var isHovered = false

    /**
     * Индекс перекрытого курсором элемента.
     */
    private var hoveredItemId = 0

    override fun onMouseButton(x: Int, y: Int, button: Int, action: Int, mods: Int): Boolean {
        if (hidden || !isHovered) return false

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
            menuItems[hoveredItemId].action(MenuItem.ActionInfo(this.x, this.y))
            hidden = true
            isHovered = false
        }

        return true
    }

    override fun onMouseMove(x: Int, y: Int): Boolean {
        if (hidden) return false

        mouseX = x
        mouseY = y

        if (pointInBox(mouseX, mouseY, absoluteX, absoluteY, width.toInt(), height.toInt())) {
            isHovered = true
            hoveredItemId = ((mouseY - absoluteY) / itemHeight).toInt()
        } else {
            isHovered = false
        }

        return true
    }

    override fun draw(canvas: Canvas) {
        if (hidden) return

        val mainRect = Rect.makeXYWH(0f, 0f, width, height)

        canvas.save()
        canvas.translate(absoluteX.toFloat(), absoluteY.toFloat())
        canvas.drawRect(mainRect, fillPaint)

        // Выделение перекрытого курсором элемента.
        if (isHovered) {
            canvas.drawRect(Rect.makeXYWH(0f, hoveredItemId * itemHeight, width, itemHeight), hoverPaint)
        }

        for (i in menuLines.indices.drop(1)) {
            val lineY = itemHeight * i
            canvas.drawLine(0f, lineY, width, lineY, strokePaint)
        }
        for ((i, line) in menuLines.withIndex()) {
            canvas.drawTextLine(line, (width - line.width) / 2, itemHeight * (i + 1) - itemVerticalPadding, textPaint)
        }
        canvas.drawRect(mainRect, strokePaint)
        canvas.restore()
    }

    companion object {
        // Внутренние объекты Skija для отрисовки компонента.
        private val fillShadow = ImageFilter.makeDropShadow(0f, 10f, 5f, 10f, 0xA6000000.toInt())
        private val fillPaint = Paint().setColor(0xFF696969.toInt()).setMode(PaintMode.FILL).setImageFilter(fillShadow)
        private val strokePaint = Paint().setColor(0xFF000000.toInt()).setMode(PaintMode.STROKE).setStrokeWidth(0.8f)
        private val hoverPaint = Paint().setColor(0xFF969696.toInt()).setMode(PaintMode.FILL)

        /**
         * Размер шрифта.
         */
        private const val fontSize = 16f
        private val typeface = Typeface.makeDefault()
        private val font = Font(typeface, fontSize)
        private val textPaint = Paint().setColor(0xFF000000.toInt())

        /**
         * Высота элемента меню.
         */
        private const val itemHeight = 24f

        /**
         * Вертикальный отступ внутри элемента.
         */
        private const val itemVerticalPadding = (itemHeight - fontSize) / 1.5f

        /**
         * Горизонтальный отступ внутри меню.
         */
        private const val horizontalPaddingScale = 1.25f
    }
}