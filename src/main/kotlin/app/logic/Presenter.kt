package app.logic

import config.Configuration
import dataflow.Dataflow
import dataflow.Node
import effects.AudioEffectConstructor
import app.logic.bindings.NodeBinding
import app.ui.components.Port
import app.ui.components.Root

/**
 * Представитель приложения.
 */
class Presenter(view: Root) {
    private val model = Dataflow<AudioEffectConstructor>()
    private val audioSystem = AudioSystem().apply { run() }

    init {
        view.addEventListener(Root.nodeCreatedEventType) {
            if (it is Root.NodeCreatedEvent) {
                val parameters = it.node.parameters.map { it.second }.toFloatArray()

                val node = Node<AudioEffectConstructor>(
                    it.node.inPortsCount,
                    it.node.outPortsCount,
                    parameters
                ) { _ ->
                    processors[it.node.title]!!.also { e -> audioSystem.add(e(parameters)) }
                }
                model.add(node)

                val binding = NodeBinding(node, it.node)

                binding.addEventListener(NodeBinding.nodeRemovedType) { e ->
                    if (e is NodeBinding.NodeRemovedEvent) {
                        model.remove(e.model)
                        updateCircuit()
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
     * Обновляет активные обработчики.
     */
    private fun updateCircuit() {
        audioSystem.clean()
        model.makePipeline()(listOf())
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
                    updateCircuit()
                }
                Port.PortEvent.Reason.LINK_REMOVED -> {
                    e.model.`in`[e.portIndex] = null
                    previousOutEvent!!.model.out[previousOutEvent!!.portIndex] = null
                    updateCircuit()
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
                    updateCircuit()
                }
                Port.PortEvent.Reason.LINK_REMOVED -> {
                    e.model.`out`[e.portIndex] = null
                    previousInEvent!!.model.`in`[previousInEvent!!.portIndex] = null
                    updateCircuit()
                }
                else -> return
            }
            previousInEvent = null
            previousOutEvent = null
        } else if (e.reason == Port.PortEvent.Reason.LINK_CREATED || e.reason == Port.PortEvent.Reason.LINK_REMOVED) {
            previousOutEvent = e
        }
    }

    companion object {
        private val processors: Map<String, AudioEffectConstructor> = Configuration.processors.map { it.name to it.effect }.toMap()
    }
}