package com.andiogenes.events

/**
 * Базовый класс для всех классов, отправляющих события.
 */
open class EventDispatcher {
    /**
     * Слушатели событий объекта.
     */
    private val listeners: MutableMap<String, ArrayList<EventListener>> = mutableMapOf()

    /**
     * Добавить слушателя события типа [type].
     */
    fun addEventListener(type: String, listener: EventListener) {
        listeners.getOrPut(type) { arrayListOf() }.add(listener)
    }

    /**
     * Удалить слушателя события типа [type].
     */
    fun removeEventListener(type: String, listener: EventListener) {
        listeners[type]?.remove(listener)
    }

    /**
     * Послать событие [event] в поток событий.
     */
    fun dispatchEvent(event: Event) {
        listeners[event.type]?.forEach { it(event) }
    }
}