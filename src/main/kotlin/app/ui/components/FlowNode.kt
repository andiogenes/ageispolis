package app.ui.components

import org.jetbrains.skija.*
import org.lwjgl.glfw.GLFW
import app.ui.components.Port.PortEvent
import app.ui.display.DisplayObjectContainer
import events.Event
import app.ui.utils.pointInBox

/**
 * Компонент узла потока данных.
 *
 * @param title Заголовок компонента
 * @param width Ширина компонента
 * @param height Высота компонента
 * @param fillColor Цвет заливки компонента
 */
class FlowNode(
    val title: String,
    width: Int,
    height: Int,
    fillColor: Int,

    /**
     * Количество входящих портов узла.
     */
    val inPortsCount: Int = 0,

    /**
     * Количество выходящих портов узла.
     */
    val outPortsCount: Int = 0,

    /**
     * Параметры обработчика.
     */
    val parameters: List<Pair<String, Float>> = listOf(),
) : DisplayObjectContainer() {
    /**
     * Событие, связанное с входящим портом узла.
     *
     * @param portIndex Индекс порта
     * @param node Узел, вызвавший событие
     * @param portEvent Событие порта
     */
    data class InEvent(val portIndex: Int, val node: FlowNode, val portEvent: PortEvent) : Event(inEventType)

    /**
     * Событие, связанное с выходящим портом узла.
     *
     * @param portIndex Индекс порта
     * @param node Узел, вызвавший событие
     * @param portEvent Событие порта
     */
    data class OutEvent(val portIndex: Int, val node: FlowNode, val portEvent: PortEvent) : Event(outEventType)

    /**
     * Событие изменения значения одним из ползунков.
     *
     * @param valueIndex Индекс ползунка
     * @param oldValue Старое значение
     * @param newValue Новое значение
     */
    data class ValueChangedEvent(val valueIndex: Int, val oldValue: Float, val newValue: Float) : Event(valueChangedEventType)

    // Внутренние объекты Skija для отрисовки компонента.
    private val fillShader = Shader.makeLinearGradient(0f, 0f, 0f, 480f, intArrayOf(fillColor, 0xFF000000.toInt()))
    private val fillPaint = Paint().setShader(fillShader).setMode(PaintMode.FILL).setImageFilter(fillShadow)

    private val titleLine = TextLine.make(title, font)
    private val titleLineWidth = titleLine.width
    private val titleLineHeight = titleLine.height

    /**
     * Минимальная высота компонента с заданным количеством входящих портов.
     */
    private val inPortsHeight = Port.diameter * inPortsCount + (inPortsCount - 1) * (portDistance - Port.diameter)

    /**
     * Минимальная высота компонента с заданным количеством исходящих портов.
     */
    private val outPortsHeight = Port.diameter * outPortsCount + (outPortsCount - 1) * (portDistance - Port.diameter)

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
        // TODO: убрать волшебство
        (headerHeight + 30 * parameters.size + 6).toInt(),
        (inPortsHeight * verticalPaddingScale).toInt(),
        (outPortsHeight * verticalPaddingScale).toInt()
    )

    private val portsInternal: ArrayList<Port> = arrayListOf()

    /**
     * Входные и выходные порты узла.
     */
    val ports: List<Port> get() = portsInternal

    init {
        val inPortsStartY = (headerHeight + this.height - inPortsHeight) / 2
        val outPortsStartY = (headerHeight + this.height - outPortsHeight) / 2

        // Добавление входящих портов
        for (i in 0 until inPortsCount) {
            Port().apply {
                y = (inPortsStartY + i * portDistance + Port.radius).toInt()
                addEventListener(Port.portEventType) {
                    if (it is PortEvent) {
                        this@FlowNode.dispatchEvent(InEvent(i, this@FlowNode, it))
                    }
                }
            }.also {
                portsInternal.add(it)
                addChild(it)
            }
        }

        // Добавление исходящих портов
        for (i in 0 until outPortsCount) {
            Port().apply {
                x = width
                y = (outPortsStartY + i * portDistance + Port.radius).toInt()
                addEventListener(Port.portEventType) {
                    if (it is PortEvent) {
                        this@FlowNode.dispatchEvent(OutEvent(i, this@FlowNode, it))
                    }
                }
            }.also {
                portsInternal.add(it)
                addChild(it)
            }
        }

        // Добавление ползунков параметров
        val sliderWidth = (width / horizontalPaddingScale * 1.1).toInt()
        val sliderX = (this@FlowNode.width - sliderWidth) / 2
        val sliderOffset = 30

        for ((i, v) in parameters.withIndex()) {
            addChild(Slider(title = v.first, sliderWidth, sliderHeight, value = v.second).apply {
                x = sliderX
                y = sliderOffset * (i + 1)
                addEventListener(Slider.valueChangedEventType) {
                    if (it is Slider.ValueChangedEvent) {
                        this@FlowNode.dispatchEvent(ValueChangedEvent(i, it.oldValue, it.newValue))
                    }
                }
            })
        }
    }

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
        if (!pointInBox(x, y, this.x, this.y, width, height)) return false

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            when (action) {
                GLFW.GLFW_PRESS -> {
                    isDrag = true
                    dragX = x - this.x
                    dragY = y - this.y
                }
                GLFW.GLFW_RELEASE -> isDrag = false
            }
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            if (action == GLFW.GLFW_PRESS) {
                dispose()
                return true
            }
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return true
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
        canvas.drawTextLine(titleLine, (width - titleLineWidth) / 2, headerHeight - titleLineHeight / 4, textPaint)
        canvas.restore()
    }

    companion object {
        // Внутренние объекты Skija для отрисовки компонента.
        private val fillShadow = ImageFilter.makeDropShadow(0f, 10f, 5f, 10f, 0xFF000000.toInt())
        private val strokePaint = Paint().setColor(0xFF000000.toInt()).setMode(PaintMode.STROKE).setStrokeWidth(0.8f)
        private val headerPaint = Paint().setBlendMode(BlendMode.CLEAR)

        private val typeface = Typeface.makeDefault()
        private val font = Font(typeface, 16f)
        private val textPaint = Paint().setColor(0xFF000000.toInt())

        /**
         * Высота заголовка.
         */
        private const val headerHeight = 24f

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

        /**
         * Высота ползунка параметра.
         */
        private const val sliderHeight = 24

        /**
         * Ключ для подписки на события типа [InEvent].
         */
        const val inEventType = "onInPort"

        /**
         * Ключ для подписки на события типа [OutEvent].
         */
        const val outEventType = "onOutPort"

        /**
         * Ключ для подписки на события типа [ValueChangedEvent].
         */
        const val valueChangedEventType = "onValueChanged"
    }
}
