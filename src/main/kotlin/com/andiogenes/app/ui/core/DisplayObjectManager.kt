package com.andiogenes.app.ui.core

import com.andiogenes.app.ui.display.DisplayObject

/**
 * Менеджер отображаемых объектов. Поддерживает перечень отображаемых объектов
 * для обеспечения эффективной работы с ними.
 */
object DisplayObjectManager {
    private val internalObjects: List<ArrayList<DisplayObject>> = List(2) { arrayListOf() }

    /**
     * Отображаемые объекты.
     */
    val objects: List<List<DisplayObject>>
        get() = internalObjects

    /**
     * Добавляет объект в набор.
     */
    fun addObject(obj: DisplayObject, layer: Int = 0): Boolean = internalObjects[layer].add(obj)

    /**
     * Удаляет объект из набора.
     */
    fun removeObject(obj: DisplayObject, layer: Int = 0): Boolean = internalObjects[layer].remove(obj)
}