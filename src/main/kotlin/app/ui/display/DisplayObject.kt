package app.ui.display

import org.jetbrains.skija.Canvas
import app.ui.core.DisplayObjectManager
import events.Event
import events.EventDispatcher

/**
 * Отображаемый объект. Узел графа сцены.
 */
abstract class DisplayObject(private var parent: DisplayObject? = null, layer: Int = 0) : EventDispatcher(), Cloneable {
    init { DisplayObjectManager.addObject(this, layer) }

    /**
     * Событие удаления объекта.
     *
     * @param disposedObject Удаленный объект
     */
    data class DisposeEvent(val disposedObject: DisplayObject) : Event(disposeEventType)

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
    open fun onMouseButton(x: Int, y: Int, button: Int, action: Int, mods: Int): Boolean { return false }

    /**
     * Обработка события изменения положения курсора.
     */
    open fun onMouseMove(x: Int, y: Int): Boolean { return false }

    /**
     * Удлаяет объект из системы отрисовки и обработки событий.
     */
    open fun dispose(): Boolean {
        dispatchEvent(DisposeEvent(this))
        return DisplayObjectManager.removeObject(this)
    }

    public override fun clone(): Any {
        return super.clone().also { DisplayObjectManager.addObject(it as DisplayObject) }
    }

    companion object {
        /**
         * Ключ, по которому можно подписаться на события типа [DisposeEvent].
         */
        const val disposeEventType = "onDispose"
    }
}