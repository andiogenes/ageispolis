package effects

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.filters.LowPassSP
import app.logic.AudioSystem
import kotlin.math.abs
import kotlin.math.exp

/**
 * Эффект "Искажение".
 *
 * Обозначения элементов массива параметров:
 * Элемент 0 - соотношение "чистого" и искаженного сигнала.
 * Элемент 1 - степень искажения звука.
 * Элемент 2 - тембр эффекта.
 */
class DistortionEffect(params: FloatArray) : AudioEffect(params) {
    private var tone = params[2]
    private val lowPassFilter = LowPassSP(1000 + tone * 20000f, AudioSystem.sampleRate.toFloat())

    override fun process(p0: AudioEvent?): Boolean {
        if (tone != params[2]) {
            tone = params[2]
            lowPassFilter.setFrequency(1000 + tone * 20000f)
        }
        lowPassFilter.process(p0)

        val audioFloatBuffer = p0!!.floatBuffer

        for (i in p0.overlap until audioFloatBuffer.size) {
            val x = audioFloatBuffer[i]
            val q = x / abs(x)
            val y = q * (1 - exp(params[1] * 20 * q * x))
            audioFloatBuffer[i] = params[0] * y + (1 - params[0]) * x
            if (audioFloatBuffer[i] > 1) audioFloatBuffer[i] = 1f
            if (audioFloatBuffer[i] < -1) audioFloatBuffer[i] = -1f
        }

        return true
    }

    override fun processingFinished() {
        lowPassFilter.processingFinished()
    }
}