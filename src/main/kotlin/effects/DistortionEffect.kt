package effects

import be.tarsos.dsp.AudioEvent
import kotlin.math.abs
import kotlin.math.exp

class DistortionEffect(params: FloatArray) : AudioEffect(params) {
    override fun process(p0: AudioEvent?): Boolean {
        val audioFloatBuffer = p0!!.floatBuffer

        for (i in p0.overlap until audioFloatBuffer.size) {
            val x = audioFloatBuffer[i]
            val q = x / abs(x)
            val y = q * (1 - exp(params[1] * 20 * q * x))
            audioFloatBuffer[i] = params[0] * y + (1 - params[0]) * x
        }

        return true
    }

    override fun processingFinished() {}
}