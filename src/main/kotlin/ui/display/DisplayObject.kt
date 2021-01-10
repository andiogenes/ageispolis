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
     * Координата x объекта (относительно родителя).
     */
    var x: Int = 0

    /**
     * Координата y объекта (относительно родителя).
     */
    var y: Int = 0

    /**
     * Абсолютная координата x объекта.
     */
    val absoluteX: Int
        get() = (parent?.x ?: 0) + x

    /**
     * Абсолютная координата y объекта.
     */
    val absoluteY: Int
        get() = (parent?.y ?: 0) + y

    /**
     * Присоединяет родителя к объекту.
     */
    fun assignParent(obj: DisplayObject?) {
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