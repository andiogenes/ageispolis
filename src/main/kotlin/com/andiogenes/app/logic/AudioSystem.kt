package com.andiogenes.app.logic

import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import com.andiogenes.effects.AudioEffect
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Звуковая система приложения.
 */
class AudioSystem {
    /**
     * Диспетчер звука, объект логики TarsosDSP.
     */
    private val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(wsola.inputBufferSize, wsola.overlap)

    private val effects = arrayListOf<AudioEffect>()

    /**
     * Добавляет эффект в цепь обработки звука.
     */
    fun add(effect: AudioEffect) {
        effects.add(effect)
        dispatcher.addAudioProcessor(effect)
    }

    /**
     * Добавляет эффекты в цепь обработки звука.
     */
    fun add(vararg effects: AudioEffect) {
        this.effects.addAll(effects)
        for (e in effects) dispatcher.addAudioProcessor(e)
    }

    /**
     * Удаляет эффект из цепи обработки звука.
     */
    fun remove(effect: AudioEffect) {
        effects.remove(effect)
        dispatcher.removeAudioProcessor(effect)
    }

    /**
     * Очищает цепь обработки звука.
     */
    fun clean() {
        effects.forEach { dispatcher.removeAudioProcessor(it) }
        effects.clear()
    }

    /**
     * Включает обработку звука в отдельном потоке.
     */
    fun run() {
        GlobalScope.launch {
            dispatcher.run()
        }
    }

    companion object {
        /**
         *
         */
        const val sampleRate = 44110

        /**
         * Метод перекрытия, основанный на подобии формы волны
         */
        val wsola = WaveformSimilarityBasedOverlapAdd(
            WaveformSimilarityBasedOverlapAdd.Parameters.slowdownDefaults(
                1.0,
                sampleRate.toDouble()
            )
        )
    }
}