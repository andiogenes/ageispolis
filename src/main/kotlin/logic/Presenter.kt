package logic

import dataflow.Dataflow
import dataflow.Node
import logic.bindings.NodeBinding
import ui.components.Port
import ui.components.Root

class Presenter(private val view: Root) {
    private val model = Dataflow<Int>()

    init {
        view.addEventListener(Root.nodeCreatedEventType) {
            if (it is Root.NodeCreatedEvent) {
                val node = Node<Int>(it.node.inPortsCount, it.node.outPortsCount, it.node.parameters.size) { 0 }
                model.add(node)

                val binding = NodeBinding(node, it.node)

                binding.addEventListener(NodeBinding.nodeRemovedType) { e ->
                    if (e is NodeBinding.NodeRemovedEvent) {
                        model.remove(e.model)
                    }
                }

                binding.addEventListener(NodeBinding.inEventType) { e ->
                    if (e is NodeBinding.InEvent) processInEvent(e)
                }

                binding.addEventListener(NodeBinding.outEventType) { e ->
                    if (e is NodeBinding.OutEvent) processOutEvent(e)
                }
            }
        }
        view.addEventListener(Root.nodeRemovedEventType) { println(it) }
    }

    /**
     * Предыдущее поступившее событие входного порта связки модели-представления.
     */
    private var previousInEvent: NodeBinding.InEvent? = null

    /**
     * Предыдущее поступившее событие выходного порта связки модели-представления.
     */
    private var previousOutEvent: NodeBinding.OutEvent? = null

    /**
     * Обработка текущего события входного узла.
     *
     * Связывает или разделяет узлы модели по их модели представления.
     */
    private fun processInEvent(e: NodeBinding.InEvent) {
        if (previousOutEvent != null) {
            if (e.reason != previousOutEvent!!.reason) return

            when (e.reason) {
                Port.PortEvent.Reason.LINK_CREATED -> {
                    e.model.connectLeft(e.portIndex, previousOutEvent!!.model, previousOutEvent!!.portIndex)
                }
                Port.PortEvent.Reason.LINK_REMOVED -> {
                    e.model.`in`[e.portIndex] = null
                    previousOutEvent!!.model.out[previousOutEvent!!.portIndex] = null
                }
                else -> return
            }
            previousInEvent = null
            previousOutEvent = null
        } else if (e.reason == Port.PortEvent.Reason.LINK_CREATED || e.reason == Port.PortEvent.Reason.LINK_REMOVED) {
            previousInEvent = e
        }
    }

    /**
     * Обработка текущего события выходного узла.
     *
     * Связывает или разделяет узлы модели по их модели представления.
     */
    private fun processOutEvent(e: NodeBinding.OutEvent) {
        if (previousInEvent != null) {
            if (e.reason != previousInEvent!!.reason) return

            when (e.reason) {
                Port.PortEvent.Reason.LINK_CREATED -> {
                    e.model.connectRight(e.portIndex, previousInEvent!!.model, previousInEvent!!.portIndex)
                }
                Port.PortEvent.Reason.LINK_REMOVED -> {
                    e.model.`out`[e.portIndex] = null
                    previousInEvent!!.model.`in`[previousInEvent!!.portIndex] = null
                }
                else -> return
            }
            previousInEvent = null
            previousOutEvent = null
        } else if (e.reason == Port.PortEvent.Reason.LINK_CREATED || e.reason == Port.PortEvent.Reason.LINK_REMOVED) {
            previousOutEvent = e
        }
    }

    /**
     * Минимум:
     * 1. Добавление узлов с UI в модель V
     * 2. Удаление узлов из UI и модели V
     * 3. Пересчет цепи в модели
     * 4. Как то замапить узлы UI с узлами модели V
     */
}