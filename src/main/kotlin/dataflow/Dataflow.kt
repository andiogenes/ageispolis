package dataflow

/**
 * Поток данных.
 */
class Dataflow<T> {
    /**
     * Узлы потока данных.
     */
    private val nodes: ArrayList<Node<T>> = arrayListOf()

    /**
     * Добавляет узел в поток данных.
     */
    fun add(node: Node<T>) = nodes.add(node)

    /**
     * Удаляет узел из потока данных.
     */
    fun remove(node: Node<T>): Boolean {
        // Удаляем связи с узлом
        node.`in`.forEach { it?.out?.forEachIndexed { index, v -> if (v == node) it.out[index] = null  } }
        node.`out`.forEach { it?.`in`?.forEachIndexed { index, v -> if (v == node) it.`in`[index] = null  } }
        return nodes.remove(node)
    }

    /**
     * Ищет источник потока - узел без входящих портов.
     */
    fun findSource(): Node<T> = nodes.first { it.`in`.isEmpty() }

    /**
     * Ищет сток потока - узел без выходящих портов.
     */
    fun findSink(): Node<T> = nodes.first { it.out.isEmpty() }

    /**
     * Строит цепь обработки данных - функцию, которая принимает данные из источника потока,
     * последовательно обрабатывает данные во всех узлах и возвращает обработанные данные.
     */
    fun makePipeline(): (List<T>) -> T {
        val pipeline = arrayListOf<(List<T>) -> T>()

        var curNode: Node<T>? = findSource()
        while (curNode != null) {
            pipeline.add(curNode.operation)
            if (curNode.out.isEmpty()) break
            curNode = curNode.out.first()
        }

        return pipeline.reduce { acc, function -> { v: List<T> -> function(listOf(acc(v))) } }
    }
}