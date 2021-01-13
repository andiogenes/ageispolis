package ui.components

import org.jetbrains.skija.*
import org.lwjgl.glfw.GLFW
import ui.display.DisplayObject
import ui.events.Event

/**
 * Слой отображения дуг между узлами потока данных.
 */
class PathLayer : DisplayObject() {
    /**
     * Начало строимой дуги.
     */
    private var start: Port? = null

    /**
     * Узел, в котором начинается строимая дуга.
     */
    private var startNode: FlowNode? = null

    /**
     * Исходящий порт, один из концов строимой дуги.
     */
    private var from: Port? = null

    /**
     * Входящий порт, один из концов строимой дуги.
     */
    private var to: Port? = null

    /**
     * Пары портов, между которыми есть дуги.
     */
    private val connectedPorts: ArrayList<Pair<Port, Port>> = arrayListOf()

    /**
     * Создает дугу из [src] в [dest].
     */
    private fun addPath(src: Port, dest: Port): Boolean {
        return connectedPorts.add(src to dest)
    }

    /**
     * Удаляет дугу с [endpoint] на одном из концов.
     */
    private fun removePath(endpoint: Port): Boolean {
        return connectedPorts.removeIf { it.first == endpoint || it.second == endpoint }
    }

    /**
     * Обрабатывает событие входящего порта узла.
     */
    fun processInEvent(e: Event) {
        if (e is FlowNode.InEvent) {
            when (e.portEvent.reason) {
                Port.PortEvent.Reason.MOUSE_PRESSED -> {
                    start = e.portEvent.port
                    startNode = e.node
                    to = start
                }
                Port.PortEvent.Reason.MOUSE_RELEASED -> {
                    if (to == null && from != null && e.node != startNode) {
                        to = e.portEvent.port
                        removePath(from!!)
                        addPath(from!!, to!!)
                    }
                    reset()
                }
                Port.PortEvent.Reason.RIGHT_CLICK -> {
                    removePath(e.portEvent.port)
                    reset()
                }
            }
        }
    }

    /**
     * Обрабатывает событие исходящего порта узла.
     */
    fun processOutEvent(e: Event) {
        if (e is FlowNode.OutEvent) {
            when (e.portEvent.reason) {
                Port.PortEvent.Reason.MOUSE_PRESSED -> {
                    start = e.portEvent.port
                    startNode = e.node
                    from = start
                }
                Port.PortEvent.Reason.MOUSE_RELEASED -> {
                    if (from == null && to != null && e.node != startNode) {
                        from = e.portEvent.port
                        removePath(to!!)
                        addPath(from!!, to!!)
                    }
                    reset()
                }
                Port.PortEvent.Reason.RIGHT_CLICK -> {
                    removePath(e.portEvent.port)
                    reset()
                }
            }
        }
    }

    /**
     * Координата x курсора
     */
    var mouseX = 0

    /**
     * Координата y курсора
     */
    var mouseY = 0

    override fun onMouseMove(x: Int, y: Int): Boolean {
        mouseX = x
        mouseY = y

        return false
    }

    /**
     * Сбрасывает состояние слоя.
     */
    private fun reset() {
        start = null
        startNode = null
        to = null
        from = null
    }

    override fun onMouseButton(x: Int, y: Int, button: Int, action: Int, mods: Int): Boolean {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_RELEASE) {
            reset()
            return true
        }

        return false
    }

    override fun draw(canvas: Canvas) {
        // Готовые дуги
        for ((from, to) in connectedPorts) {
            canvas.drawLine(
                from.absoluteX.toFloat(), from.absoluteY.toFloat(),
                to.absoluteX.toFloat(), to.absoluteY.toFloat(),
                strokePaint
            )
        }

        // Строимая дуга
        if (start != null) {
            canvas.drawLine(
                start!!.absoluteX.toFloat(), start!!.absoluteY.toFloat(),
                mouseX.toFloat(), mouseY.toFloat(),
                strokePaint
            )
        }
    }

    companion object {
        /**
         * Ширина дуги.
         */
        private const val strokeWidth = 5f

        // Внутренние объекты Skija для отрисовки компонента.
        private val strokePaint = Paint().setColor(0xFFFF0000.toInt())
            .setMode(PaintMode.STROKE)
            .setStrokeWidth(strokeWidth)
            .setStrokeCap(PaintStrokeCap.ROUND)
    }
}