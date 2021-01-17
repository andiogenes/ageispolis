package effects

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.effects.FlangerEffect
import app.logic.AudioSystem

/**
 * Эффект "Фланжер". Напоминает "летящее" звучание.
 *
 * Обозначения элементов массива параметров:
 * Элемент 0 - время задержки.
 * Элемент 1 - баланс "чистого" и измененного сигнала.
 * Элемент 2 - частота колебаний.
 */
class FlangerEffect(params: FloatArray) : AudioEffect(params) {
    private var maxFlangerLength = params[0]
    private var wet = params[1]
    private var lfoFrequency = params[2]

    private val flangerProcessor = FlangerEffect(maxFlangerLength.toDouble() / 10, wet.toDouble(), AudioSystem.sampleRate.toDouble(), lfoFrequency.toDouble() * 2)

    override fun process(p0: AudioEvent?): Boolean {
        if (maxFlangerLength != params[0]) {
            maxFlangerLength = params[0]
            flangerProcessor.setFlangerLength(maxFlangerLength.toDouble() / 10)
        }
        if (wet != params[1]) {
            wet = params[1]
            flangerProcessor.setWet(wet.toDouble())
        }
        if (lfoFrequency != params[2]) {
            lfoFrequency = params[2]
            flangerProcessor.setLFOFrequency(lfoFrequency.toDouble() * 2)
        }
        flangerProcessor.process(p0)
        return true
    }

    override fun processingFinished() {
        flangerProcessor.processingFinished()
    }
}