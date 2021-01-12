package ui.components

import org.jetbrains.skija.*
import org.lwjgl.glfw.GLFW
import ui.display.DisplayObject
import utils.pointInBox

/**
 * Компонент узла потока данных.
 *
 * @param title Заголовок компонента
 * @param width Ширина компонента
 * @param height Высота компонента
 * @param fillColor Цвет заливки компонента
 */
class FlowNode(title: String, width: Int, height: Int, fillColor: Int) : DisplayObject() {
    // Внутренние объекты Skija для отрисовки компонента.
    private val fillShader = Shader.makeLinearGradient(0f, 0f, 0f, 480f, intArrayOf(fillColor, 0xFF000000.toInt()))
    private val fillPaint = Paint().setShader(fillShader).setMode(PaintMode.FILL).setImageFilter(fillShadow)

    private val titleLine = TextLine.make(title, font)
    private val titleLineWidth = titleLine.width
    private val titleLineHeight = titleLine.height

    // TODO: вычислять из агрегируемого узла
    /**
     * Количество входящих портов узла.
     */
    private val inPortsCount = 3

    /**
     * Количество исходящих портов узла.
     */
    private val outPortsCount = 1

    /**
     * Минимальная высота компонента с заданным количеством входящих портов.
     */
    private val inPortsHeight = portDiameter * inPortsCount + (inPortsCount - 1) * (portDistance - portDiameter)

    /**
     * Минимальная высота компонента с заданным количеством исходящих портов.
     */
    private val outPortsHeight = portDiameter * outPortsCount + (outPortsCount - 1) * (portDistance - portDiameter)

    /**
     * Действительная ширина компонента.
     */
    private val width = maxOf(width, (titleLineWidth * horizontalPaddingScale).toInt())

    /**
     * Действительная высота компонента.
     */
    private val height = maxOf(
        height,
        (headerHeight * 2).toInt(),
        (inPortsHeight * verticalPaddingScale).toInt(),
        (outPortsHeight * verticalPaddingScale).toInt()
    )

    /**
     * Координата X опорной точки, относительно которой перетаскивается компонент.
     */
    private var dragX = 0

    /**
     * Координата Y опорной точки, относительно которой перетаскивается компонент.
     */
    private var dragY = 0

    /**
     * Находится ли компонент в режиме перемещения.
     */
    private var isDrag = false

    override fun onMouseButton(x: Int, y: Int, button: Int, action: Int, mods: Int): Boolean {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            when (action) {
                GLFW.GLFW_PRESS -> if (pointInBox(x, y, this.x, this.y, width, height)) {
                    isDrag = true
                    dragX = x - this.x
                    dragY = y - this.y
                }
                GLFW.GLFW_RELEASE -> isDrag = false
            }
        }

        return false
    }

    override fun onMouseMove(x: Int, y: Int): Boolean {
        if (isDrag) {
            this.x = x - dragX
            this.y = y - dragY
            return true
        }

        return false
    }

    override fun draw(canvas: Canvas) {
        val mainRRect = RRect.makeXYWH(0f, 0f, width.toFloat(), height.toFloat(), 10f)
        val headerRRect = RRect.makeComplexXYWH(
            0f, 0f,
            width.toFloat(), headerHeight,
            floatArrayOf(10f, 10f, 0f, 0f)
        )

        canvas.save()
        canvas.translate(absoluteX.toFloat(), absoluteY.toFloat())
        canvas.drawRRect(headerRRect, headerPaint)
        canvas.drawRRect(mainRRect, fillPaint)
        canvas.drawLine(0f, headerHeight, width.toFloat(), headerHeight, strokePaint)
        canvas.drawRRect(mainRRect, strokePaint)
        drawPorts(canvas)
        canvas.drawTextLine(titleLine, (width - titleLineWidth) / 2, headerHeight - titleLineHeight / 4, textPaint)
        canvas.restore()
    }

    private val inPortsStartY = (headerHeight + this.height - inPortsHeight) / 2
    private val outPortsStartY = (headerHeight + this.height - outPortsHeight) / 2

    /**
     * Отрисовка портов.
     */
    private fun drawPorts(canvas: Canvas) {
        for (i in 0 until inPortsCount) {
            val portY = inPortsStartY + i * portDistance + portRadius
            canvas.drawCircle(0f, portY, portRadius, portPaint)
            canvas.drawCircle(0f, portY, portRadius, portStrokePaint)
        }

        for (i in 0 until outPortsCount) {
            val portY = outPortsStartY + i * portDistance + portRadius
            canvas.drawCircle(width.toFloat(), portY, portRadius, portPaint)
            canvas.drawCircle(width.toFloat(), portY, portRadius, portStrokePaint)
        }
    }

    companion object {
        // Внутренние объекты Skija для отрисовки компонента.
        private val fillShadow = ImageFilter.makeDropShadow(0f, 10f, 5f, 10f, 0xFF000000.toInt())
        private val strokePaint = Paint().setColor(0xFF000000.toInt()).setMode(PaintMode.STROKE).setStrokeWidth(0.8f)
        private val headerPaint = Paint().setBlendMode(BlendMode.CLEAR)

        private val portPaint = Paint().setColor(0xFFDBE11E.toInt()).setMode(PaintMode.FILL)
        private val portStrokePaint = Paint().setColor(0xFF000000.toInt()).setMode(PaintMode.STROKE).setStrokeWidth(1.25f)

        private val typeface = Typeface.makeDefault()
        private val font = Font(typeface, 16f)
        private val textPaint = Paint().setColor(0xFF000000.toInt())

        /**
         * Высота заголовка.
         */
        private const val headerHeight = 24f

        /**
         * Радиус порта.
         */
        private const val portRadius = 7.5f

        /**
         * Диаметр порта.
         */
        private const val portDiameter = portRadius * 2

        /**
         * Расстояние между портами.
         */
        private const val portDistance = 30f

        /**
         * Горизонтальный отступ внутри компонента.
         */
        private const val horizontalPaddingScale = 1.25

        /**
         * Вертикальный отступ внутри компонента.
         */
        private const val verticalPaddingScale = 1.75
    }
}
