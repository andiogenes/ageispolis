package com.andiogenes.app.ui.components

import com.andiogenes.config.Configuration
import com.andiogenes.app.logic.Presenter
import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Paint
import org.lwjgl.glfw.GLFW
import com.andiogenes.app.ui.display.DisplayObjectContainer
import com.andiogenes.events.Event

/**
 * Корневой компонент приложения.
 */
class Root : DisplayObjectContainer() {
    /**
     * Событие добавления узла в рабочую область.
     */
    data class NodeCreatedEvent(val node: FlowNode) : Event(nodeCreatedEventType)

    /**
     * Событие удаления узла из рабочей области.
     */
    data class NodeRemovedEvent(val node: FlowNode) : Event(nodeRemovedEventType)

    /**
     * Экземпляр презентовщика этого представления.
     */
    private val presenter = Presenter(this)

    /**
     * Слой отображения дуг.
     */
    private val pathLayer = PathLayer().also { addChild(it) }

    /**
     * Контекстное меню рабочей области.
     */
    private val contextMenu = ContextMenu(Configuration.processors.map {
        ContextMenu.MenuItem(it.name) { info ->
            val node = FlowNode(it.name, 140, 0, it.fillColor, it.inPorts, it.outPorts, it.parameters).apply {
                x = info.x
                y = info.y
                addEventListener(FlowNode.inEventType) { e -> pathLayer.processInEvent(e) }
                addEventListener(FlowNode.outEventType) { e -> pathLayer.processOutEvent(e) }
                addEventListener(disposeEventType) { e ->
                    if (e is DisposeEvent) {
                        if (e.disposedObject is FlowNode) {
                            e.disposedObject.ports.forEach { p -> pathLayer.removePath(p) }
                            this@Root.dispatchEvent(NodeRemovedEvent(e.disposedObject))
                        }
                        removeChild(e.disposedObject)
                    }
                }
            }
            addChild(node)
            dispatchEvent(NodeCreatedEvent(node))
        }
    }).also {
        it.hidden = true
        addChild(it)
    }

    override fun onMouseButton(x: Int, y: Int, button: Int, action: Int, mods: Int): Boolean {
        return when (button) {
            GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                contextMenu.hidden = true
                true
            }
            GLFW.GLFW_MOUSE_BUTTON_RIGHT -> if (action == GLFW.GLFW_PRESS) {
                contextMenu.hidden = false
                contextMenu.x = x
                contextMenu.y = y
                true
            } else {
                false
            }
            else -> false
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.clear(backgroundColor)
        // TODO: строить сетку по размеру окна
        for (x in 1..320) {
            canvas.drawLine(x.toFloat() * gridWidth, 0f, x.toFloat() * gridWidth, 1024f, gridPaint)
        }
        for (y in 1..120) {
            canvas.drawLine(0f, y.toFloat() * gridWidth, 1924f, y.toFloat() * gridWidth, gridPaint)
        }
        canvas.restore()
    }

    companion object {
        // Внутренние объекты Skija для отрисовки компонента.
        private val gridPaint = Paint().setColor(0xFF000000.toInt()).setStrokeWidth(1f).setAntiAlias(false)

        /**
         * Цвет рабочей области.
         */
        private const val backgroundColor = 0xFF393939.toInt()

        /**
         * Ширина сетки.
         */
        private const val gridWidth = 20

        /**
         * Ключ, по которому можно подписаться на [NodeCreatedEvent].
         */
        const val nodeCreatedEventType = "onNodeCreated"

        /**
         * Ключ, по которому можно подписаться на [NodeRemovedEvent].
         */
        const val nodeRemovedEventType = "onNodeRemoved"
    }
}