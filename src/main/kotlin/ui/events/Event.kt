package ui.events

/**
 * Слушатель события.
 */
typealias EventListener = (Event) -> Unit

/**
 * Тип события.
 */
enum class EventType

/**
 * Событие и информация о нём.
 *
 * @param type Тип события.
 */
sealed class Event(val type: EventType)
