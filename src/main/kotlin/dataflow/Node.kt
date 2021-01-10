package dataflow

/**
 * Узел потока данных.
 */
class Node<A>(
    /**
     * Мощность входящих портов.
     */
    private val inCapacity: Int,
    /**
     * Мощность выходящих портов.
     */
    private val outCapacity: Int = 1,
    /**
     * Вычислительная работа узла.
     */
    val operation: (List<A>) -> A,
) {
    /**
     * Входящие порты.
     */
    val `in`: Array<Node<A>?> = Array(inCapacity) { null }

    /**
     * Выходящие порты.
     */
    val out: Array<Node<A>?> = Array(outCapacity) { null }

    /**
     * Соединяет входящий порт [inPort] узла с выходящим портом [outPort] узла [source].
     */
    fun connectLeft(inPort: Int, source: Node<A>, outPort: Int) {
        `in`[inPort] = source
        source.out[outPort] = this
    }

    /**
     * Соединяет выходящий порт [outPort] узла с входящим портом [inPort] узла [destination].
     */
    fun connectRight(outPort: Int, destination: Node<A>, inPort: Int) {
        out[outPort] = destination
        destination.`in`[inPort] = this
    }
}