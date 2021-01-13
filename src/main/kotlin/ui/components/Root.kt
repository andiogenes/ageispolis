package ui.components

import logic.Presenter
import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Paint
import org.lwjgl.glfw.GLFW
import ui.display.DisplayObject
import ui.display.DisplayObjectContainer

/**
 * Корневой компонент приложения.
 */
class Root : DisplayObjectContainer() {
    private val pathLayer = PathLayer()

    /**
     * Слой отображения дуг.
     */
    private val pathLayer = PathLayer().also { addChild(it) }

    /**
     * Контекстное меню рабочей области.
     */
    private val contextMenu = ContextMenu(listOf("Overdrive", "Delay", "Chorus").map {
        ContextMenu.MenuItem(it) { info ->
            addChild(FlowNode(it, 140, 164, 0x66AAAAAA).apply {
                x = info.x
                y = info.y
                addEventListener(FlowNode.inEventType) { e -> pathLayer.processInEvent(e) }
                addEventListener(FlowNode.outEventType) { e -> pathLayer.processOutEvent(e) }
                addEventListener(disposeEventType) { e ->
                    if (e is DisposeEvent) {
                        if (e.disposedObject is FlowNode) {
                            e.disposedObject.ports.forEach { p -> pathLayer.removePath(p) }
                        }
                        removeChild(e.disposedObject)
                    }
                }
            })
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
        private val gridPaint = Paint().setColor(0xFF000000.toInt()).setStrokeWidth(1f).setAntiAlias(false)
        private const val backgroundColor = 0xFF393939.toInt()
        private const val gridWidth = 20
    }
}