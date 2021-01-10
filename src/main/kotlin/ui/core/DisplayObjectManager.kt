package ui.core

import ui.display.DisplayObject

/**
 * Менеджер отображаемых объектов. Поддерживает перечень отображаемых объектов
 * для обеспечения эффективной работы с ними.
 */
object DisplayObjectManager {
    private val internalObjects: ArrayList<DisplayObject> = arrayListOf()

    /**
     * Отображаемые объекты.
     */
    val objects: List<DisplayObject>
        get() = internalObjects

    /**
     * Добавляет объект в набор.
     */
    fun addObject(obj: DisplayObject): Boolean = internalObjects.add(obj)

    /**
     * Удаляет объект из набора.
     */
    fun removeObject(obj: DisplayObject): Boolean = internalObjects.remove(obj)
}