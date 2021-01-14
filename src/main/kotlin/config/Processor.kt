package config

/**
 * Конфигурация обработчика.
 */
data class Processor(
    /**
     * Имя обработчика
     */
    val name: String,

    /**
     * Входящие порты
     */
    val inPorts: Int,

    /**
     * Исходящие порты
     */
    val outPorts: Int,

    /**
     * Параметры обработчика
     */
    val parameters: List<Pair<String, Float>>
)