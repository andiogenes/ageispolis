package com.andiogenes.config

import com.andiogenes.effects.AudioEffectConstructor
import com.andiogenes.effects.IdempotentEffect

/**
 * Domain-sailed language для конфигурирования программы.
 */
object Dsl {
    /**
     * DSL-строитель обработчика.
     *
     * @param name Имя обработчика
     */
    class ProcessorConfiguration(var name: String = "") {
        /**
         * Количество входных портов
         */
        var inPorts: Int = 0

        /**
         * Количество выходных портов
         */
        var outPorts: Int = 0

        /**
         * Цвет заливки компонента
         */
        var fillColor: Int = 0x66AAAAAA

        /**
         * Параметры обработчика
         */
        class Parameters {
            val list = arrayListOf<Pair<String, Float>>()

            /**
             * Добавляет параметр в список параметров.
             */
            infix fun String.to(defaultValue: Float) {
                list.add(Pair(this, defaultValue))
            }
        }

        var parameters: List<Pair<String, Float>> = listOf()

        /**
         * Настройка параметров обработчика.
         */
        fun parameters(fn: Parameters.() -> Unit) {
            parameters = Parameters().also(fn).list
        }

        var effect: AudioEffectConstructor = { IdempotentEffect(it) }

        /**
         * Установка эффекта обработчика.
         */
        fun effect(fn: AudioEffectConstructor) {
            effect = fn
        }
    }

    /**
     * Строит обработчик на основе конфигурации.
     */
    private fun ProcessorConfiguration.toProcessor(): Processor {
        return Processor(this.name, this.inPorts, this.outPorts, this.fillColor, this.parameters, this.effect)
    }

    /**
     * DSL-строитель списка обработчиков.
     */
    class ProcessorListConfiguration {
        private val internalList = arrayListOf<Processor>()

        /**
         * Список обработчиков.
         */
        val list: List<Processor> get() = internalList

        /**
         * Настройка и добавление обработчика в список.
         *
         * @param name имя обработчика
         */
        fun processor(name: String, fn: ProcessorConfiguration.() -> Unit) {
            val conf = ProcessorConfiguration(name).also(fn)
            internalList.add(conf.toProcessor())
        }
    }

    /**
     * Настройка и создание списка обработчиков.
     */
    fun processors(fn: ProcessorListConfiguration.() -> Unit): List<Processor> {
        val processors = ProcessorListConfiguration()
        fn(processors)
        // Утверждаем, что все обработчики в конфигурации имеют уникальные имена
        // TODO: ввести идентификаторы обработчиков?
        assert(processors.list.run { map { it.name }.toSet().size == size })
        return processors.list
    }
}