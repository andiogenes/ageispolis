package ui.events

/**
 * Базовый класс для всех классов, отправляющих события.
 */
open class EventDispatcher {
    /**
     * Слушатели событий объекта.
     */
    private val listeners: MutableMap<EventType, ArrayList<EventListener>> = mutableMapOf()

    /**
     * Добавить слушателя события типа [type].
     */
    fun addEventListener(type: EventType, listener: EventListener) {
        listeners.getOrPut(type) { arrayListOf() }.add(listener)
    }

    /**
     * Удалить слушателя события типа [type].
     */
    fun removeEventListener(type: EventType, listener: EventListener) {
        listeners[type]?.remove(listener)
    }

    /**
     * Послать событие [event] в поток событий.
     */
    fun dispatchEvent(event: Event) {
        listeners[event.type]?.forEach { it(event) }
    }
}