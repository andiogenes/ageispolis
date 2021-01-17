package com.andiogenes.effects

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.effects.DelayEffect
import com.andiogenes.app.logic.AudioSystem

/**
 * Эффект "задержки" - чёткие затухающие повторы (эхо) исходного сигнала.
 *
 * Обозначения элементов массива параметров:
 * Элемент 0 - время задержки.
 * Элемент 1 - затухание эха.
 */
class DelayEffect(params: FloatArray) : AudioEffect(params) {
    private var echoLength = params[0]
    private var decay = params[1]

    private val delayProcessor = DelayEffect(echoLength.toDouble() * 3, decay.toDouble(), AudioSystem.sampleRate.toDouble())

    override fun process(p0: AudioEvent?): Boolean {
        if (echoLength != params[0]) {
            echoLength = params[0]
            delayProcessor.setEchoLength(echoLength.toDouble() * 3)
        }
        if (decay != params[1]) {
            decay = params[1]
            delayProcessor.setDecay(decay.toDouble())
        }
        delayProcessor.process(p0)
        return true
    }

    override fun processingFinished() {
        delayProcessor.processingFinished()
    }
}