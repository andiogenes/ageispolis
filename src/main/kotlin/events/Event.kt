package events

/**
 * Слушатель события.
 */
typealias EventListener = (Event) -> Unit

/**
 * Событие и информация о нём.
 *
 * @param type Тип события.
 */
open class Event(val type: String)
