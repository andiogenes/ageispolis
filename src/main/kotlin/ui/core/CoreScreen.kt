package ui.core

import org.jetbrains.skija.Canvas
import skija.Screen
import ui.display.DisplayObject

/**
 * Главный набор обработчиков, инкапсулирующий работу с графом сцены.
 */
class CoreScreen(private val root: DisplayObject) : Screen {
    private var x: Int = 0
    private var y: Int = 0

    override fun onDraw(canvas: Canvas) {
        DisplayObjectManager.objects.forEach { it.draw(canvas) }
    }

    override fun onMouseButton(button: Int, action: Int, mods: Int) {
        DisplayObjectManager.objects.asReversed().forEach { it.onMouseButton(x, y, button, action, mods) }
    }

    override fun onCursor(x: Int, y: Int) {
        this.x = x
        this.y = y

        DisplayObjectManager.objects.asReversed().forEach { it.onMouseMove(x, y) }
    }

    override fun onWindowSize(width: Int, height: Int) {}

    override fun onKey(key: Int, scancode: Int, action: Int, mods: Int) {}
}