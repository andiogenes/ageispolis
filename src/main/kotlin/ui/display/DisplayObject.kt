package ui.display

import org.jetbrains.skija.Canvas
import ui.core.DisplayObjectManager
import ui.events.EventDispatcher

/**
 * Отображаемый объект. Узел графа сцены.
 */
abstract class DisplayObject(private var parent: DisplayObject? = null) : EventDispatcher(), Cloneable {
    init { DisplayObjectManager.addObject(this) }

    /**
     * Координата x объекта.
     */
    var x: Int = 0

    /**
     * Координата y объекта.
     */
    var y: Int = 0

    fun setParent(obj: DisplayObject?) {
        this.parent = obj
    }

    /**
     * Процедура отрисовки объекта.
     */
    open fun draw(canvas: Canvas) {}

    /**
     * Обработка события нажатия на кнопку мыши.
     */
    open fun onMouseButton(x: Int, y: Int, button: Int, action: Int, mods: Int) {}

    /**
     * Обработка события изменения положения курсора.
     */
    open fun onMouseMove(x: Int, y: Int) {}

    public override fun clone(): Any {
        return super.clone().also { DisplayObjectManager.addObject(it as DisplayObject) }
    }
}