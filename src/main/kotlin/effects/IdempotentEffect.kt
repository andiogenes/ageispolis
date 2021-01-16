package effects

import be.tarsos.dsp.AudioEvent

/**
 * Эффект, который ничего не делает.
 */
class IdempotentEffect(params: FloatArray) : AudioEffect(params) {
    override fun process(p0: AudioEvent?): Boolean {
        return true
    }

    override fun processingFinished() {}
}